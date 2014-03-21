/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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

import com.exedio.cope.misc.DatabaseListener;
import com.exedio.cope.misc.DirectRevisionsFactory;
import com.exedio.cope.misc.HiddenFeatures;
import com.exedio.cope.util.JobContext;
import com.exedio.cope.util.ModificationListener;
import com.exedio.cope.util.Pool;
import com.exedio.cope.util.Properties;
import com.exedio.dsmf.Constraint;
import com.exedio.dsmf.Schema;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.InvalidObjectException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Model implements Serializable
{
	private static final Logger logger = LoggerFactory.getLogger(Model.class);

	@SuppressFBWarnings("SE_BAD_FIELD") // OK: writeReplace
	private final Revisions.Factory revisions;
	private final Object reviseLock = new Object();

	@SuppressFBWarnings("SE_BAD_FIELD") // OK: writeReplace
	final Types types;
	private final long initializeDate;
	@SuppressFBWarnings("SE_BAD_FIELD") // OK: writeReplace
	final ChangeListeners changeListeners;
	@SuppressFBWarnings("SE_BAD_FIELD") // OK: writeReplace
	final ModificationListeners modificationListeners;

	private final Object connectLock = new Object();
	@SuppressFBWarnings("SE_BAD_FIELD") // OK: writeReplace
	private Connect connectIfConnected;

	private final AtomicLong nextTransactionId = new AtomicLong();
	private volatile long lastTransactionStartDate = Long.MIN_VALUE;

	@SuppressFBWarnings("SE_BAD_FIELD") // OK: writeReplace
	final Transactions transactions = new Transactions();
	@SuppressFBWarnings("SE_BAD_FIELD") // OK: writeReplace
	private final TransactionCounter transactionCounter = new TransactionCounter();

	public Model(final Type<?>... types)
	{
		this((Revisions.Factory)null, types);
	}

	public Model(final Revisions.Factory revisions, final Type<?>... types)
	{
		this(revisions, (TypeSet[])null, types);
	}

	public Model(final Revisions.Factory revisions, final TypeSet[] typeSets, final Type<?>... types)
	{
		this.revisions = revisions;
		this.types = new Types(this, typeSets, types);
		this.initializeDate = System.currentTimeMillis();
		this.changeListeners = new ChangeListeners();
		this.modificationListeners = new ModificationListeners(this.types);

		this.types.afterModelCreated();
	}

	public boolean contains(final TypeSet typeSet)
	{
		return containsTypeSet(typeSet.getTypesArray());
	}

	public boolean containsTypeSet(final Type<?>... typeSet)
	{
		return types.containsTypeSet(typeSet);
	}

	/**
	 * @deprecated Use {@link HiddenFeatures#get(Model)} instead.
	 */
	@Deprecated
	public Map<Feature, Feature> getHiddenFeatures()
	{
		return HiddenFeatures.get(this);
	}

	/**
	 * Connects this model to the database described in the properties.
	 *
	 * @throws IllegalStateException if this model has already been connected.
	 */
	public void connect(final ConnectProperties properties)
	{
		final Timer.Interval timer = connectTimer.start();

		if(properties==null)
			throw new NullPointerException("properties");

		synchronized(connectLock)
		{
			if(this.connectIfConnected!=null)
				throw new IllegalStateException("model already been connected");

			this.connectIfConnected = new Connect(toString(), types, revisions, properties, transactions, changeListeners);
			types.connect(connectIfConnected.database);
		}

		timer.finish("connect");
	}

	public void disconnect()
	{
		final Timer.Interval timer = connectTimer.start();

		synchronized(connectLock)
		{
			final Connect connect = connect();
			this.connectIfConnected = null;
			types.disconnect();
			connect.close();
		}

		timer.finish("disconnect");
	}

	private static final Timer connectTimer = new Timer(logger, "connect");

	Connect connect()
	{
		final Connect result = this.connectIfConnected;
		if(result==null)
			throw new NotConnectedException(this);
		return result;
	}

	public static final class NotConnectedException extends IllegalStateException
	{
		private static final long serialVersionUID = 1l;

		private final Model model;

		NotConnectedException(final Model model)
		{
			this.model = model;
		}

		public Model getModel()
		{
			return model;
		}

		@Override
		public String getMessage()
		{
			return "model not connected, use Model#connect for " + model;
		}
	}

	private final void assertRevisionEnabled()
	{
		if(revisions==null)
			throw new IllegalArgumentException("revisions are not enabled");
		transactions.assertNoCurrentTransaction();
	}

	public Revisions getRevisions()
	{
		return connect().getRevisions();
	}

	public void revise()
	{
		revise(true);
	}

	private void revise(final boolean explicitRequest)
	{
		assertRevisionEnabled();

		synchronized(reviseLock)
		{
			connect().revise(explicitRequest);
		}
	}

	/**
	 * calls {@link #reviseIfSupportedAndAutoEnabled}
	 * @deprecated use {@link #reviseIfSupportedAndAutoEnabled} instead
	 */
	@Deprecated
	public void reviseIfSupported()
	{
		reviseIfSupportedAndAutoEnabled();
	}

	/**
	 * If this method returns successfully, the model's revisions (if any) have been executed.
	 *
	 * Automatic execution of revisions is controlled by connect property revise.auto.enabled.
	 * This method will throw an exception if auto-revisions are not enabled and revisions are pending.
	 */
	public void reviseIfSupportedAndAutoEnabled()
	{
		if(revisions==null)
			return;

		revise(false);
	}

	/**
	 * @see #getRevisionLogsAndMutex()
	 */
	public Map<Integer, byte[]> getRevisionLogs()
	{
		assertRevisionEnabled();
		return connect().getRevisionLogs(false);
	}

	/**
	 * @see #getRevisionLogs()
	 */
	public Map<Integer, byte[]> getRevisionLogsAndMutex()
	{
		assertRevisionEnabled();
		return connect().getRevisionLogs(true);
	}

	public boolean isConnected()
	{
		return this.connectIfConnected!=null;
	}

	public ConnectProperties getConnectProperties()
	{
		return connect().properties;
	}

	public Date getConnectDate()
	{
		final Connect connect = this.connectIfConnected;
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
	public Type<?> getType(final String id)
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

	public boolean nullsAreSortedLow()
	{
		return connect().dialect.nullsAreSortedLow();
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
		return connect().supportsEmptyStrings();
	}

	/**
	 * @see Random
	 */
	public boolean supportsRandom()
	{
		return connect().dialect.supportsRandom();
	}

	public DatabaseListener getDatabaseListener()
	{
		return connect().executor.listener;
	}

	public void setDatabaseListener(final DatabaseListener listener)
	{
		connect().executor.listener = listener;
	}

	/**
	 * @return the listener previously registered for this model
	 */
	TestDatabaseListener setTestDatabaseListener(final TestDatabaseListener listener)
	{
		return connect().executor.setTestListener(listener);
	}

	public void createSchema()
	{
		transactions.assertNoCurrentTransaction();

		final Timer.Interval timer = schemaTimer.start();

		connect().createSchema();

		timer.finish("createSchema");
	}

	public void createSchemaConstraints(final EnumSet<Constraint.Type> types)
	{
		transactions.assertNoCurrentTransaction();

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
		final Transaction tx = currentTransaction();
		tx.connect.database.checkSchema(tx.getConnection());
	}

	public void checkEmptySchema()
	{
		final Timer.Interval timer = schemaTimer.start();

		final Transaction tx = currentTransaction();
		tx.connect.database.checkEmptySchema(tx.getConnection());

		timer.finish("checkEmptySchema");
	}

	/**
	 * @throws IllegalStateException is a transaction is bound to the current thread
	 * @see #deleteSchemaForTest()
	 */
	public void deleteSchema()
	{
		deleteSchema(false);
	}

	/**
	 * Use for tests only.
	 * Does some optimizations for faster execution, that are valid under certain conditions only:
	 * <ul>
	 * <li>Changes to the database are done via this cope model only.
	 *     This also means that there is no cluster.</li>
	 * <li>No transactions running concurrently to <tt>deleteSchemaForTest</tt></li>
	 * </ul>
	 * TODO replace by some {@link ConnectProperties connect property}.
	 * @throws IllegalStateException is a transaction is bound to the current thread
	 * @see #deleteSchema()
	 */
	public void deleteSchemaForTest()
	{
		deleteSchema(true);
	}

	private void deleteSchema(final boolean forTest)
	{
		transactions.assertNoCurrentTransaction();

		final Timer.Interval timer = schemaTimer.start();

		connect().deleteSchema(forTest);

		timer.finish(forTest ? "deleteSchemaForTest" : "deleteSchema");
	}

	public void dropSchema()
	{
		transactions.assertNoCurrentTransaction();

		final Timer.Interval timer = schemaTimer.start();

		connect().dropSchema();

		timer.finish("dropSchema");
	}

	public void dropSchemaConstraints(final EnumSet<Constraint.Type> types)
	{
		transactions.assertNoCurrentTransaction();

		connect().database.dropSchemaConstraints(types);
	}

	public void tearDownSchema()
	{
		transactions.assertNoCurrentTransaction();

		connect().tearDownSchema();
	}

	public void tearDownSchemaConstraints(final EnumSet<Constraint.Type> types)
	{
		transactions.assertNoCurrentTransaction();

		connect().database.tearDownSchemaConstraints(types);
	}

	private static final Timer schemaTimer = new Timer(logger, "schema");

	public Schema getVerifiedSchema()
	{
		transactions.assertNoCurrentTransaction();

		return connect().database.makeVerifiedSchema();
	}

	public Schema getSchema()
	{
		return connect().database.makeSchema();
	}

	/**
	 * Gives this cope model the chance to purge / cleanup whatever it needs to.
	 * Should be called once a day.
	 */
	public void purgeSchema(final JobContext ctx)
	{
		connect().purgeSchema(ctx);
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

	public List<ThreadController> getThreadControllers()
	{
		return connect().getThreadControllers();
	}

	public List<ChangeListener> getChangeListeners()
	{
		return changeListeners.get();
	}

	public List<ModificationListener> getModificationListeners()
	{
		return modificationListeners.get();
	}

	public ChangeListenerInfo getChangeListenersInfo()
	{
		return changeListeners.getInfo();
	}

	public ChangeListenerDispatcherInfo getChangeListenerDispatcherInfo()
	{
		return connect().changeListenerDispatcher.getInfo();
	}

	public int getModificationListenersCleared()
	{
		return modificationListeners.getCleared();
	}

	public void addChangeListener(final ChangeListener listener)
	{
		changeListeners.add(listener);
	}

	public void addModificationListener(final ModificationListener listener)
	{
		modificationListeners.add(listener);
	}

	public void removeChangeListener(final ChangeListener listener)
	{
		changeListeners.remove(listener);
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
		return connect().itemCache.getInfo(types.concreteTypeList);
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

	public EnvironmentInfo getEnvironmentInfo()
	{
		return connect().database.dialectParameters.environmentInfo;
	}

	public Properties getClusterProperties()
	{
		final Cluster cluster = connect().cluster;
		return cluster!=null ? cluster.properties : null;
	}

	public ClusterSenderInfo getClusterSenderInfo()
	{
		final Cluster cluster = connect().cluster;
		return cluster!=null ? cluster.getSenderInfo() : null;
	}

	public ClusterListenerInfo getClusterListenerInfo()
	{
		final Cluster cluster = connect().cluster;
		return cluster!=null ? cluster.getListenerInfo() : null;
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
	 * @see #startTransactionClosable(String)
	 */
	public Transaction startTransaction(final String name)
	{
		final Transaction previousTransaction = transactions.currentIfBound();
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

		final Connect connect = connect();
		final long id = nextTransactionId.getAndIncrement();
		final long startDate = System.currentTimeMillis();
		lastTransactionStartDate = startDate;

		final Transaction result =
			new Transaction(connect, types.concreteTypeCount, id, name, startDate);
		transactions.add(result);

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
		return transactions.leave();
	}

	public void joinTransaction( final Transaction tx )
	{
		transactions.join(tx);
	}

	public boolean hasCurrentTransaction()
	{
		return transactions.currentIfBound()!=null;
	}

	/**
	 * Returns the transaction for this model,
	 * that is bound to the currently running thread.
	 * @throws IllegalStateException if there is no cope transaction bound to current thread
	 * @see Thread#currentThread()
	 */
	public Transaction currentTransaction()
	{
		final Transaction result = transactions.currentIfBound();
		if(result==null)
			throw new IllegalStateException("there is no cope transaction bound to this thread, see Model#startTransaction");
		assert result.assertBoundToCurrentThread();
		return result;
	}

	public void rollback()
	{
		commitOrRollback(true);
	}

	public void rollbackIfNotCommitted()
	{
		final Transaction t = transactions.currentIfBound();
		if( t!=null )
			rollback();
	}

	public void commit()
	{
		commitOrRollback(false);
	}

	private void commitOrRollback(final boolean rollback)
	{
		final Transaction tx = transactions.remove();
		tx.commitOrRollback(rollback, this, transactionCounter);

		if(tx.connect.properties.itemCacheStamps)
		{
			final long oldestStamp = transactions.getOldestCacheStamp();
			connect().itemCache.purgeStamps(oldestStamp);
		}
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
		return transactions.getOpen();
	}


	/**
	 * @see #startTransaction(String)
	 */
	public Tx startTransactionClosable(final String name)
	{
		startTransaction(name);
		return tx;
	}

	private final Tx tx = new Tx(this);

	public static final class Tx implements AutoCloseable
	{
		private final Model model;

		Tx(final Model model)
		{
			this.model = model;
		}

		public void commit()
		{
			model.commit();
		}

		public Item getItem(final String id) throws NoSuchIDException
		{
			return model.getItem(id);
		}

		public boolean hasCurrentTransaction()
		{
			return model.hasCurrentTransaction();
		}

		@Override
		public void close()
		{
			model.rollbackIfNotCommitted();
		}
	}


	public TransactionCounters getTransactionCounters()
	{
		return transactionCounter.get();
	}

	public void clearCache()
	{
		connect().clearCache();
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
		transactions.assertNoCurrentTransaction();

		connect().database.makeSchema().checkUnsupportedConstraints();
	}

	public boolean isClusterNetworkEnabled()
	{
		return connect().cluster!=null;
	}

	public void pingClusterNetwork()
	{
		pingClusterNetwork(1);
	}

	public void pingClusterNetwork(final int count)
	{
		final Cluster cluster = connect().cluster;
		if(cluster==null)
			throw new IllegalStateException("cluster network not enabled");
		cluster.sendPing(count);
	}

	// serialization -------------

	private static final long serialVersionUID = 1l;

	private Serialized serialized = null;

	public void enableSerialization(final Class<?> type, final String name)
	{
		if(type==null)
			throw new NullPointerException("type");
		if(name==null)
			throw new NullPointerException("name");
		if(serialized!=null)
			throw new IllegalStateException("enableSerialization already been called for " + serialized.toString());
		final Serialized serialized = new Serialized(type, name);
		final Object other = serialized.resolveModel();
		if(this!=other)
			throw new IllegalArgumentException("enableSerialization does not resolve to itself " + serialized.toString());

		this.serialized = serialized;
	}

	/**
	 * <a href="http://java.sun.com/j2se/1.5.0/docs/guide/serialization/spec/output.html#5324">See Spec</a>
	 */
	private Object writeReplace() throws ObjectStreamException
	{
		if(serialized==null)
			throw new NotSerializableException(getClass().getName() + " (can be fixed by calling method enableSerialization(Class,String))");

		return serialized;
	}

	/**
	 * Block malicious data streams.
	 * @see #writeReplace()
	 */
	@SuppressWarnings("static-method")
	private void readObject(@SuppressWarnings("unused") final ObjectInputStream ois) throws InvalidObjectException
	{
		throw new InvalidObjectException("required " + Serialized.class);
	}

	/**
	 * Block malicious data streams.
	 * @see #writeReplace()
	 */
	@SuppressWarnings("static-method")
	private Object readResolve() throws InvalidObjectException
	{
		throw new InvalidObjectException("required " + Serialized.class);
	}

	@Override
	public String toString()
	{
		final Serialized serialized = this.serialized;
		return
			serialized!=null
			? serialized.toString()
			: super.toString();
	}

	private static final class Serialized implements Serializable
	{
		private static final long serialVersionUID = 1l;

		private final Class<?> type;
		private final String name;

		Serialized(final Class<?> type, final String name)
		{
			this.type = type;
			this.name = name;
		}

		/**
		 * <a href="http://java.sun.com/j2se/1.5.0/docs/guide/serialization/spec/input.html#5903">See Spec</a>
		 */
		private Object readResolve()
		{
			return resolveModel();
		}

		@Override
		public String toString()
		{
			return type.getName() + '#' + name;
		}

		@SuppressFBWarnings("DP_DO_INSIDE_DO_PRIVILEGED")
		Object resolveModel()
		{
			final java.lang.reflect.Field field;
			try
			{
				field = type.getDeclaredField(name);
			}
			catch(final NoSuchFieldException e)
			{
				throw new IllegalArgumentException(toString() + " does not exist.", e);
			}
			if((field.getModifiers()&STATIC_FINAL)!=STATIC_FINAL)
				throw new IllegalArgumentException(toString() + " is not static final.");
			field.setAccessible(true);
			final Object result;
			try
			{
				result = field.get(null);
			}
			catch(final IllegalAccessException e)
			{
				throw new IllegalArgumentException("accessing " + field.toString(), e);
			}

			if(result==null)
				throw new IllegalArgumentException(toString() + " is null.");
			if(!(result instanceof Model))
				throw new IllegalArgumentException(toString() + " is not a model, but " + result.getClass().getName() + '.');

			return result;
		}

		private static final int STATIC_FINAL = Modifier.STATIC | Modifier.FINAL;
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
	 * @deprecated Use {@link #reviseIfSupportedAndAutoEnabled()} instead
	 */
	@Deprecated
	public void migrateIfSupported()
	{
		reviseIfSupportedAndAutoEnabled();
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
	public Type<?> findTypeByID(final String id)
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
	public Model(final int revisionNumber, final Type<?>... types)
	{
		this(new Revisions(revisionNumber), types);
	}

	/**
	 * @deprecated Use {@link #Model(Revisions, Type...)} and {@link Revisions#Revisions(Revision[])}.
	 */
	@Deprecated
	public Model(final Revision[] revisions, final Type<?>... types)
	{
		this(new Revisions(revisions), types);
	}

	/**
	 * @deprecated Not supported anymore. This method does nothing.
	 */
	@Deprecated
	public void flushSequences()
	{
		// does nothing
	}

	/**
	 * @deprecated Use {@link #getConnectProperties()} instead
	 */
	@Deprecated
	public ConnectProperties getProperties()
	{
		return getConnectProperties();
	}

	/**
	 * @deprecated Use {@link #getEnvironmentInfo()} and {@link EnvironmentInfo#asProperties()} instead.
	 */
	@Deprecated
	public java.util.Properties getDatabaseInfo()
	{
		return getEnvironmentInfo().asProperties();
	}

	/**
	 * @deprecated Use {@link #currentTransaction()} instead
	 */
	@Deprecated
	public Transaction getCurrentTransaction()
	{
		return currentTransaction();
	}

	/**
	 * @deprecated Use {@link SchemaInfo#supportsCheckConstraints(Model)} instead
	 */
	@Deprecated
	public boolean supportsCheckConstraints()
	{
		return SchemaInfo.supportsCheckConstraints(this);
	}

	/**
	 * @deprecated Use {@link SchemaInfo#supportsSequences(Model)} instead
	 */
	@Deprecated
	public boolean supportsSequences()
	{
		return SchemaInfo.supportsSequences(this);
	}

	/**
	 * @deprecated Use {@link ConnectProperties#isLoggingEnabled()} instead, always returns false.
	 */
	@Deprecated
	public static final boolean isLoggingEnabled()
	{
		return false;
	}

	/**
	 * @deprecated use {@link #getChangeListenersInfo()}.{@link ChangeListenerInfo#getCleared()} instead.
	 */
	@Deprecated
	public int getChangeListenersCleared()
	{
		return changeListeners.getInfo().getCleared();
	}

	/**
	 * @deprecated Use {@link #Model(Revisions.Factory, Type...)} or {@link DirectRevisionsFactory} instead.
	 */
	@Deprecated
	public Model(final Revisions revisions, final Type<?>... types)
	{
		this(DirectRevisionsFactory.make(revisions), types);
	}

	/**
	 * @deprecated Use {@link #Model(Revisions.Factory, TypeSet[], Type...)} or {@link DirectRevisionsFactory} instead.
	 */
	@Deprecated
	public Model(final Revisions revisions, final TypeSet[] typeSets, final Type<?>... types)
	{
		this(DirectRevisionsFactory.make(revisions), typeSets, types);
	}

	/**
	 * @deprecated Use {@link #Model(Revisions.Factory, Type...)} instead.
	 */
	@Deprecated
	public Model(final RevisionsFuture revisions, final Type<?>... types)
	{
		this(wrap(revisions), types);
	}

	/**
	 * @deprecated Use {@link #Model(Revisions.Factory, TypeSet[], Type...)} instead.
	 */
	@Deprecated
	public Model(final RevisionsFuture revisions, final TypeSet[] typeSets, final Type<?>... types)
	{
		this(wrap(revisions), typeSets, types);
	}

	@Deprecated
	private static final Revisions.Factory wrap(final RevisionsFuture revisions)
	{
		if(revisions==null)
			return null;

		return new Revisions.Factory()
		{
			public Revisions create(final Revisions.Factory.Context ctx)
			{
				return revisions.get(ctx.getEnvironment());
			}
		};
	}
}
