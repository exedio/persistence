/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.exedio.cope;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import com.exedio.cope.info.ClusterListenerInfo;
import com.exedio.cope.info.ClusterSenderInfo;
import com.exedio.cope.info.ItemCacheInfo;
import com.exedio.cope.info.QueryCacheHistogram;
import com.exedio.cope.info.QueryCacheInfo;
import com.exedio.cope.info.SequenceInfo;
import com.exedio.cope.util.ModificationListener;
import com.exedio.cope.util.Pool;
import com.exedio.dsmf.Constraint;
import com.exedio.dsmf.Schema;

public final class Model
{
	private Revisions revisions; // TODO make final
	private final Object reviseLock = new Object();
	
	final Types types;
	private final long initializeDate;
	private final ModificationListeners modificationListeners = new ModificationListeners();
	
	private final Object connectLock = new Object();
	private Connect connect;

	private final AtomicLong nextTransactionId = new AtomicLong();
	private volatile long lastTransactionStartDate = Long.MIN_VALUE;
	
	private final HashSet<Transaction> openTransactions = new HashSet<Transaction>();
	private final ThreadLocal<Transaction> boundTransactions = new ThreadLocal<Transaction>();
	
	private volatile long transactionsCommitWithoutConnection = 0;
	private volatile long transactionsCommitWithConnection = 0;
	private volatile long transactionsRollbackWithoutConnection = 0;
	private volatile long transactionsRollbackWithConnection = 0;
	
	public Model(final Type... types)
	{
		this((Revisions)null, types);
	}
	
	public Model(final Revisions revisions, final Type... types)
	{
		this.revisions = revisions;
		this.types = new Types(this, types);
		this.initializeDate = System.currentTimeMillis();
	}
	
	public boolean containsTypeSet(final Type... typeSet)
	{
		return types.containsTypeSet(typeSet);
	}
	
	public Map<Feature, Feature> getHiddenFeatures()
	{
		return types.getHiddenFeatures();
	}
	
	/**
	 * Connects this model to the database described in the properties.
	 *
	 * @throws IllegalStateException if this model has already been connected.
	 */
	public void connect(final ConnectProperties properties)
	{
		if(properties==null)
			throw new NullPointerException("properties");

		synchronized(connectLock)
		{
			if(this.connect!=null)
				throw new IllegalStateException("model already been connected");
			
			this.connect = new Connect(types, revisions, properties);
			types.connect(connect.database);
		}
	}
	
	public void disconnect()
	{
		synchronized(connectLock)
		{
			if(this.connect==null)
				throw new IllegalStateException("model not yet connected, use Model#connect");
			
			final Connect connect = this.connect;
			this.connect = null;
			types.disconnect();
			connect.close();
		}
	}
	
	Connect connect()
	{
		final Connect connect = this.connect;
		if(connect==null)
			throw new IllegalStateException("model not yet connected, use Model#connect");
		return connect;
	}
	
	public void flushSequences()
	{
		connect().database.flushSequences();
	}
	
	private final void assertRevisionEnabled()
	{
		if(revisions==null)
			throw new IllegalArgumentException("revisions are not enabled");
	}

	public Revisions getRevisions()
	{
		return revisions;
	}
	
	void setRevisions(final Revisions revisions) // for test only, not for productive use !!!
	{
		assertRevisionEnabled();
		connect().database.setRevisions(revisions); // do this first to fail early if not yet connected
		this.revisions = revisions;
	}

	public void revise()
	{
		assertRevisionEnabled();
		
		synchronized(reviseLock)
		{
			connect().database.revise();
		}
	}

	public void reviseIfSupported()
	{
		if(revisions==null)
			return;
		
		revise();
	}

	public Map<Integer, byte[]> getRevisionLogs()
	{
		assertRevisionEnabled();
		return connect().database.getRevisionLogs();
	}
	
	public ConnectProperties getProperties()
	{
		return connect().properties;
	}
	
	public Date getConnectDate()
	{
		final Connect connect = this.connect;
		if(connect==null)
			return null;
		return new Date(connect.date);
	}
	
	public List<Type<?>> getTypes()
	{
		return types.typeList;
	}
	
	public List<Type<?>> getTypesSortedByHierarchy()
	{
		return types.typeListSorted;
	}
	
	public List<Type<?>> getConcreteTypes()
	{
		return types.concreteTypeList;
	}

	/**
	 * @see Type#getID()
	 */
	public Type getType(final String id)
	{
		return types.getType(id);
	}
	
	/**
	 * @see Feature#getID()
	 */
	public Feature getFeature(final String id)
	{
		return types.getFeature(id);
	}
	
	public Date getInitializeDate()
	{
		return new Date(initializeDate);
	}
	
	public boolean supportsCheckConstraints()
	{
		return connect().database.dsmfDialect.supportsCheckConstraints();
	}
	
	public boolean supportsSequences()
	{
		return connect().database.supportsSequences;
	}
	
	/**
	 * Returns, whether the database can store empty strings.
	 * <p>
	 * If true, an empty string can be stored into a {@link StringField}
	 * like any other string via {@link FunctionField#set(Item,Object)}.
	 * A subsequent retrieval of that string via {@link FunctionField#get(Item)}
	 * returns an empty string.
	 * If false, an empty string stored into a {@link StringField} is
	 * converted to null, thus a subsequent retrieval of that string returns
	 * null.
	 * <p>
	 * Up to now, only Oracle does not support empty strings.
	 */
	public boolean supportsEmptyStrings()
	{
		return !getProperties().getDatabaseDontSupportEmptyStrings() && connect().dialect.supportsEmptyStrings();
	}

	public boolean isDatabaseLogEnabled()
	{
		return connect().database.log!=null;
	}
	
	/**
	 * Threshold time in milliseconds.
	 */
	public int getDatabaseLogThreshold()
	{
		final DatabaseLogConfig log = connect().database.log;
		return log!=null ? log.threshold : 0;
	}
	
	public String getDatabaseLogSQL()
	{
		final DatabaseLogConfig log = connect().database.log;
		return log!=null ? log.sql : null;
	}
	
	public void setDatabaseLog(final boolean enable, final int threshold, final String sql, final PrintStream out)
	{
		connect().database.log = enable ? new DatabaseLogConfig(threshold, sql, out) : null;
	}
	
	/**
	 * @return the listener previously registered for this model
	 */
	DatabaseListener setDatabaseListener(final DatabaseListener listener)
	{
		return connect().database.setListener(listener);
	}
	
	public void createSchema()
	{
		connect().database.createSchema();
		clearCache();
	}

	public void createSchemaConstraints(final EnumSet<Constraint.Type> types)
	{
		connect().database.createSchemaConstraints(types);
	}

	/**
	 * Checks the schema,
	 * whether the tables representing the types do exist.
	 * Issues a single database statement,
	 * that touches all tables and columns,
	 * that would have been created by
	 * {@link #createSchema()}.
	 * @throws RuntimeException
	 * 	if something is wrong with the database.
	 * 	TODO: use a more specific exception.
	 */
	public void checkSchema()
	{
		connect().database.checkSchema(getCurrentTransaction().getConnection());
	}

	public void checkEmptySchema()
	{
		connect().database.checkEmptySchema(getCurrentTransaction().getConnection());
	}

	public void dropSchema()
	{
		connect().database.dropSchema();
		clearCache();
	}

	public void dropSchemaConstraints(final EnumSet<Constraint.Type> types)
	{
		connect().database.dropSchemaConstraints(types);
	}

	public void tearDownSchema()
	{
		connect().database.tearDownSchema();
		clearCache();
	}
	
	public void tearDownSchemaConstraints(final EnumSet<Constraint.Type> types)
	{
		connect().database.tearDownSchemaConstraints(types);
	}

	public Schema getVerifiedSchema()
	{
		return connect().database.makeVerifiedSchema();
	}

	public Schema getSchema()
	{
		return connect().database.makeSchema();
	}

	/**
	 * Returns the item with the given ID.
	 * Always returns {@link Item#activeCopeItem() active} objects.
	 * @see Item#getCopeID()
	 * @throws NoSuchIDException if there is no item with the given id.
	 */
	public Item getItem(final String id)
			throws NoSuchIDException
	{
		return types.getItem(id);
	}
	
	public List<ModificationListener> getModificationListeners()
	{
		return modificationListeners.get();
	}

	public int getModificationListenersCleared()
	{
		return modificationListeners.getCleared();
	}
	
	public void addModificationListener(final ModificationListener listener)
	{
		modificationListeners.add(listener);
	}
	
	public void removeModificationListener(final ModificationListener listener)
	{
		modificationListeners.remove(listener);
	}
	
	public List<SequenceInfo> getSequenceInfo()
	{
		return connect().database.getSequenceInfo();
	}
	
	public ItemCacheInfo[] getItemCacheInfo()
	{
		return connect().itemCache.getInfo();
	}
	
	public QueryCacheInfo getQueryCacheInfo()
	{
		return connect().queryCache.getInfo();
	}
	
	public QueryCacheHistogram[] getQueryCacheHistogram()
	{
		return connect().queryCache.getHistogram();
	}
	
	public Pool.Info getConnectionPoolInfo()
	{
		return connect().connectionPool.getInfo();
	}
	
	public java.util.Properties getDatabaseInfo()
	{
		return connect().database.dialectParameters.getInfo();
	}

	public ClusterSenderInfo getClusterSenderInfo()
	{
		final ClusterSender clusterSender = connect().clusterSender;
		if(clusterSender==null)
			return null;
		return clusterSender.getInfo();
	}

	public ClusterListenerInfo getClusterListenerInfo()
	{
		final ClusterListener clusterListener = connect().clusterListener;
		if(clusterListener==null)
			return null;
		return clusterListener.getInfo();
	}
	
	// ----------------------- transaction
	
	/**
	 * @throws IllegalStateException
	 *    if there is already a transaction bound
	 *    to the current thread for this model
	 * @see #startTransaction(String)
	 */
	public Transaction startTransaction()
	{
		return startTransaction(null);
	}
	
	/**
	 * @param name
	 * 	a name for the transaction, useful for debugging.
	 * 	This name is used in {@link Transaction#toString()}.
	 * @throws IllegalStateException
	 *    if there is already a transaction bound
	 *    to the current thread for this model
	 * @see #startTransaction()
	 */
	public Transaction startTransaction(final String name)
	{
		if(connect().logTransactions)
			System.out.println("transaction start " + name);

		final Transaction previousTransaction = getCurrentTransactionIfBound();
		if(previousTransaction!=null)
		{
			final String previousName = previousTransaction.name;
			throw new IllegalStateException(
					"tried to start a new transaction " +
					(name!=null ? ("with name >" + name + '<') : "without a name") +
					", but there is already a transaction " + previousTransaction + ' ' +
					(previousName!=null ? ("with name >" + previousName + '<') : "without a name") +
					" started on " + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS").format(previousTransaction.getStartDate()) +
					" bound to current thread");
		}
		
		final long id;
		final long startDate = System.currentTimeMillis();
		id = nextTransactionId.getAndIncrement();
		lastTransactionStartDate = startDate;
		
		final Transaction result = new Transaction(this, types.concreteTypeCount, id, name, startDate);
		setTransaction( result );
		synchronized(openTransactions)
		{
			openTransactions.add(result);
		}
		return result;
	}
	
	public long getNextTransactionId()
	{
		return nextTransactionId.get();
	}
	
	public Date getLastTransactionStartDate()
	{
		final long lastTransactionStartDate = this.lastTransactionStartDate;
		return lastTransactionStartDate!=Long.MIN_VALUE ? new Date(lastTransactionStartDate) : null;
	}
	
	public Transaction leaveTransaction()
	{
		Transaction tx = getCurrentTransaction();
		tx.unbindThread();
		setTransaction( null );
		return tx;
	}
	
	public void joinTransaction( Transaction tx )
	{
		if ( hasCurrentTransaction() )
			throw new RuntimeException("there is already a transaction bound to current thread");
		setTransaction(tx);
	}
	
	public boolean hasCurrentTransaction()
	{
		return getCurrentTransactionIfBound()!=null;
	}

	/**
	 * Returns the transaction for this model,
	 * that is bound to the currently running thread.
	 * @throws IllegalStateException if there is no cope transaction bound to current thread
	 * @see Thread#currentThread()
	 */
	public Transaction getCurrentTransaction()
	{
		final Transaction result = getCurrentTransactionIfBound();
		if(result==null)
			throw new IllegalStateException("there is no cope transaction bound to this thread, see Model#startTransaction");
		assert result.assertBoundToCurrentThread();
		return result;
	}
	
	private Transaction getCurrentTransactionIfBound()
	{
		final Transaction result = boundTransactions.get();
		assert result==null || result.assertBoundToCurrentThread();
		return result;
	}
	
	private void setTransaction(final Transaction transaction)
	{
		if(transaction!=null)
		{
			transaction.bindToCurrentThread();
			boundTransactions.set(transaction);
		}
		else
			boundTransactions.remove();
	}
	
	public void rollback()
	{
		commitOrRollback(true);
	}
	
	public void rollbackIfNotCommitted()
	{
		final Transaction t = getCurrentTransactionIfBound();
		if( t!=null )
			rollback();
	}
	
	public void commit()
	{
		commitOrRollback(false);
	}

	private void commitOrRollback(final boolean rollback)
	{
		final Transaction tx = getCurrentTransaction();
		
		if(connect().logTransactions)
			System.out.println("transaction " + (rollback?"rollback":"commit") + ' ' + tx);
		
		synchronized(openTransactions)
		{
			openTransactions.remove(tx);
		}
		setTransaction(null);
		final boolean hadConnection = tx.commitOrRollback(rollback);
		
		if(hadConnection)
			if(rollback)
				transactionsRollbackWithConnection++;
			else
				transactionsCommitWithConnection++;
		else
			if(rollback)
				transactionsRollbackWithoutConnection++;
			else
				transactionsCommitWithoutConnection++;
	}

	/**
	 *	Returns true if the database supports READ_COMMITTED or any more strict transaction isolation level.
	 */
	boolean supportsReadCommitted()
	{
		return connect().database.supportsReadCommitted;
	}
	
	/**
	 * Returns the collection of open {@link Transaction}s
	 * on this model.
	 * <p>
	 * Returns an unmodifiable snapshot of the actual data,
	 * so iterating over the collection on a live server cannot cause
	 * {@link java.util.ConcurrentModificationException}s.
	 */
	public Collection<Transaction> getOpenTransactions()
	{
		final Transaction[] result;
		synchronized(openTransactions)
		{
			result = openTransactions.toArray(new Transaction[openTransactions.size()]);
		}
		return Collections.unmodifiableCollection(Arrays.asList(result));
	}
	
	public TransactionCounters getTransactionCounters()
	{
		return new TransactionCounters(
				transactionsCommitWithoutConnection,
				transactionsCommitWithConnection,
				transactionsRollbackWithoutConnection,
				transactionsRollbackWithConnection);
	}
	
	public void clearCache()
	{
		connect().itemCache.clear();
		connect().queryCache.clear();
	}
	
	/**
	 * @see ItemFunction#checkTypeColumn()
	 */
	public void checkTypeColumns()
	{
		types.checkTypeColumns();
	}
	
	public void checkUnsupportedConstraints()
	{
		connect().database.makeSchema().checkUnsupportedConstraints();
	}
	
	public boolean isClusterNetworkEnabled()
	{
		return connect().clusterSender!=null;
	}
	
	public void pingClusterNetwork()
	{
		pingClusterNetwork(1);
	}
	
	public void pingClusterNetwork(final int count)
	{
		final ClusterSender clusterSender = connect().clusterSender;
		if(clusterSender==null)
			throw new IllegalStateException("cluster network not enabled");
		clusterSender.ping(count);
	}
	
	public static final boolean isLoggingEnabled()
	{
		return Boolean.valueOf(System.getProperty("com.exedio.cope.logging"));
	}
	
	// ------------------- deprecated stuff -------------------
	
	/**
	 * @deprecated renamed to {@link #getItemCacheInfo()}.
	 */
	@Deprecated
	public ItemCacheInfo[] getCacheInfo()
	{
		return getItemCacheInfo();
	}
	
	/**
	 * @deprecated renamed to {@link #getQueryCacheHistogram()}.
	 */
	@Deprecated
	public QueryCacheHistogram[] getCacheQueryHistogram()
	{
		return getQueryCacheHistogram();
	}
	
	/**
	 * @deprecated renamed to {@link #getQueryCacheInfo()}.
	 */
	@Deprecated
	public QueryCacheInfo getCacheQueryInfo()
	{
		return getQueryCacheInfo();
	}
	
	/**
	 * @deprecated Use {@link #revise()} instead
	 */
	@Deprecated
	public void migrate()
	{
		revise();
	}

	/**
	 * @deprecated Use {@link #reviseIfSupported()} instead
	 */
	@Deprecated
	public void migrateIfSupported()
	{
		reviseIfSupported();
	}
	
	/**
	 * @deprecated Use {@link #getRevisionLogs()} instead
	 */
	@Deprecated
	public Map<Integer, byte[]> getMigrationLogs()
	{
		return getRevisionLogs();
	}
	
	/**
	 * @deprecated Use {@link #getModificationListenersCleared()} instead
	 */
	@Deprecated
	public int getModificationListenersRemoved()
	{
		return getModificationListenersCleared();
	}
	
	/**
	 * @deprecated renamed to {@link #connect(ConnectProperties)}.
	 */
	@Deprecated
	public void setPropertiesInitially(final ConnectProperties properties)
	{
		connect(properties);
	}
	
	/**
	 * @deprecated Use {@link #getItem(String)} instead
	 */
	@Deprecated
	public Item findByID(final String id) throws NoSuchIDException
	{
		return getItem(id);
	}
	
	/**
	 * @deprecated Use {@link #getType(String)} instead
	 */
	@Deprecated
	public Type findTypeByID(final String id)
	{
		return getType(id);
	}
	
	/**
	 * @deprecated Use {@link #getFeature(String)} instead
	 */
	@Deprecated
	public Feature findFeatureByID(final String id)
	{
		return getFeature(id);
	}
	
	/**
	 * @deprecated Use {@link #createSchema()} instead
	 */
	@Deprecated
	public void createDatabase()
	{
		createSchema();
	}

	/**
	 * @deprecated Use {@link #createSchemaConstraints(EnumSet)} instead
	 */
	@Deprecated
	public void createDatabaseConstraints(final EnumSet<Constraint.Type> types)
	{
		createSchemaConstraints(types);
	}

	/**
	 * @deprecated Use {@link #checkSchema()} instead
	 */
	@Deprecated
	public void checkDatabase()
	{
		checkSchema();
	}

	/**
	 * @deprecated Use {@link #checkEmptySchema()} instead
	 */
	@Deprecated
	public void checkEmptyDatabase()
	{
		checkEmptySchema();
	}

	/**
	 * @deprecated Use {@link #dropSchema()} instead
	 */
	@Deprecated
	public void dropDatabase()
	{
		dropSchema();
	}

	/**
	 * @deprecated Use {@link #dropSchemaConstraints(EnumSet)} instead
	 */
	@Deprecated
	public void dropDatabaseConstraints(final EnumSet<Constraint.Type> types)
	{
		dropSchemaConstraints(types);
	}

	/**
	 * @deprecated Use {@link #tearDownSchema()} instead
	 */
	@Deprecated
	public void tearDownDatabase()
	{
		tearDownSchema();
	}

	/**
	 * @deprecated Use {@link #tearDownSchemaConstraints(EnumSet)} instead
	 */
	@Deprecated
	public void tearDownDatabaseConstraints(final EnumSet<Constraint.Type> types)
	{
		tearDownSchemaConstraints(types);
	}
	
	/**
	 * @deprecated Use {@link #Model(Revisions, Type...)} and {@link Revisions#Revisions(int)}.
	 */
	@Deprecated
	public Model(final int revisionNumber, final Type... types)
	{
		this(new Revisions(revisionNumber), types);
	}
	
	/**
	 * @deprecated Use {@link #Model(Revisions, Type...)} and {@link Revisions#Revisions(Revision[])}.
	 */
	@Deprecated
	public Model(final Revision[] revisions, final Type... types)
	{
		this(new Revisions(revisions), types);
	}
}
