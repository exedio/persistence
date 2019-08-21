/*
 * Copyright (C) 2004-2015  exedio GmbH (www.exedio.com)
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

import static com.exedio.cope.util.Check.requireGreaterZero;
import static java.util.Objects.requireNonNull;

import com.exedio.cope.misc.ChangeHooks;
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
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedSet;
import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Model implements Serializable
{
	private static final Logger logger = LoggerFactory.getLogger(Model.class);

	private final String name;

	@SuppressFBWarnings("SE_BAD_FIELD") // OK: writeReplace
	private final Revisions.Factory revisions;
	private final Object reviseLock = new Object();

	@SuppressFBWarnings("SE_BAD_FIELD") // OK: writeReplace
	final Types types;
	private final Instant initializeDate = Instant.now();
	@SuppressFBWarnings("SE_BAD_FIELD") // OK: writeReplace
	final ChangeHook changeHook;
	@SuppressFBWarnings("SE_BAD_FIELD") // OK: writeReplace
	final ChangeListeners changeListeners = new ChangeListeners();

	private final Object connectLock = new Object();
	@SuppressWarnings("FieldAccessedSynchronizedAndUnsynchronized")
	@SuppressFBWarnings("SE_BAD_FIELD") // OK: writeReplace
	private Connect connectIfConnected;

	private final AtomicLong nextTransactionId = new AtomicLong();
	@SuppressWarnings("VolatileLongOrDoubleField")
	private volatile long lastTransactionStartDate = Long.MIN_VALUE;

	@SuppressFBWarnings("SE_BAD_FIELD") // OK: writeReplace
	final Transactions transactions = new Transactions();
	@SuppressWarnings({"ThisEscapedInObjectConstruction", "resource"})
	@SuppressFBWarnings("SE_BAD_FIELD") // OK: writeReplace
	private final TransactionTry tx = new TransactionTry(this);
	@SuppressFBWarnings("SE_BAD_FIELD") // OK: writeReplace
	private final TransactionCounter transactionCounter = new TransactionCounter();

	public static ModelBuilder builder()
	{
		return new ModelBuilder();
	}

	@SuppressWarnings({"RedundantCast","deprecation"}) // needed for idea
	public Model(final Type<?>... types)
	{
		this((Revisions.Factory)null, (TypeSet[])null, types);
	}

	Model(
			final String name,
			final Revisions.Factory revisions,
			final TypeSet[] typeSets,
			final Type<?>[] types,
			final ChangeHook.Factory changeHook)
	{
		this.name = name;
		this.revisions = revisions;
		//noinspection ThisEscapedInObjectConstruction
		this.types = new Types(this, typeSets, types);
		//noinspection ThisEscapedInObjectConstruction
		this.changeHook = ChangeHooks.create(changeHook, this);

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
	 * Connects this model to the database described in the properties.
	 *
	 * @throws IllegalStateException if this model has already been connected.
	 */
	public void connect(final ConnectProperties properties)
	{
		final Timer.Interval timer = connectTimer.start();

		requireNonNull(properties, "properties");

		synchronized(connectLock)
		{
			if(connectIfConnected!=null)
				throw new IllegalStateException("model already been connected");

			connectIfConnected = new Connect(toString(), types, revisions, properties, transactions, changeListeners);
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
		final Connect result = connectIfConnected;
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

	private void assertRevisionEnabled()
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
		return connectIfConnected!=null;
	}

	public ConnectProperties getConnectProperties()
	{
		return connect().properties;
	}

	/**
	 * BEWARE: In contrast to {@link #getConnectDate()} this method fails
	 * if model is not {@link #isConnected() connected}.
	 * @see #getConnectDate()
	 */
	public Instant getConnectInstant()
	{
		return connect().date;
	}

	/**
	 * Returns null if model is not {@link #isConnected() connected}.
	 * @see #getConnectInstant()
	 */
	public Date getConnectDate()
	{
		final Connect connect = connectIfConnected;
		if(connect==null)
			return null;
		return Date.from(connect.date);
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
		return types.concreteTypes;
	}

	/**
	 * Finds a type by its {@link Type#getID() id}.
	 * Returns null, if there is no such type.
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

	public Instant getInitializeInstant()
	{
		return initializeDate;
	}

	public Date getInitializeDate()
	{
		return Date.from(initializeDate);
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
		return connect().supportsEmptyStrings;
	}

	/**
	 * https://dev.mysql.com/doc/refman/5.6/en/charset-unicode-utf8mb4.html
	 */
	public boolean supportsUTF8mb4()
	{
		return connect().supportsUTF8mb4;
	}

	/**
	 * @see StringField#hashMatchesIfSupported(String, DataField)
	 */
	@SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType") // supportedDataHashAlgorithms is unmodifiable
	public SortedSet<String> getSupportedDataHashAlgorithms()
	{
		return connect().supportedDataHashAlgorithms;
	}

	/**
	 * @see Random
	 */
	public boolean supportsRandom()
	{
		return connect().supportsRandom;
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
	 * <li>No transactions running concurrently to {@code deleteSchemaForTest}</li>
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
		requireNonNull(ctx, "ctx");
		transactions.assertNoCurrentTransaction();

		connect().purgeSchema(ctx);
	}

	/**
	 * Returns a string that may help you resetting the schema to the
	 * current contents.
	 * This may or may not be supported by the database.
	 * The meaning of the result heavily depends on the database.
	 * Never returns null.
	 * @throws SQLException if not supported by the database
	 */
	public String getSchemaSavepoint() throws SQLException
	{
		transactions.assertNoCurrentTransaction();

		return connect().getSchemaSavepoint();
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

	/**
	 * Returns {@link Object#toString()} of the {@link ChangeHook} registered
	 * for this model.
	 */
	public String getChangeHookString()
	{
		return changeHook.toString();
	}

	/**
	 * @see #addChangeListener(ChangeListener)
	 */
	public List<ChangeListener> getChangeListeners()
	{
		return changeListeners.get();
	}

	public ChangeListenerInfo getChangeListenersInfo()
	{
		return changeListeners.getInfo();
	}

	public ChangeListenerDispatcherInfo getChangeListenerDispatcherInfo()
	{
		return connect().changeListenerDispatcher.getInfo();
	}

	/**
	 * Adds a change listener to the model.
	 * The listener is called for each {@link Model#commit()},
	 * even on other nodes of the cluster.
	 * When the listener is called, there is no transaction present,
	 * you may create one if needed.
	 * Multiple listeners are called in order of addition.
	 * <p>
	 * Note, this is something completely different than
	 * {@link #addPostCommitHookIfAbsent(Runnable) commit hooks}.
	 *
	 * @see #getChangeListeners()
	 */
	public void addChangeListener(final ChangeListener listener)
	{
		changeListeners.add(listener);
	}

	public void removeChangeListener(final ChangeListener listener)
	{
		changeListeners.remove(listener);
	}

	public void removeAllChangeListeners()
	{
		changeListeners.removeAll();
	}

	public List<SequenceInfo> getSequenceInfo()
	{
		return connect().database.getSequenceInfo();
	}

	public ItemCacheStatistics getItemCacheStatistics()
	{
		return connect().itemCache.getStatistics(types.concreteTypes);
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

	public void flushConnectionPool()
	{
		connect().connectionPool.flush();
	}

	public EnvironmentInfo getEnvironmentInfo()
	{
		return connect().database.probe.environmentInfo;
	}

	// ----------------------- cluster

	public boolean isClusterEnabled()
	{
		return connect().cluster!=null;
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

	public void pingClusterNetwork()
	{
		pingClusterNetwork(1);
	}

	public void pingClusterNetwork(final int count)
	{
		requireGreaterZero(count, "count");
		final Cluster cluster = connect().cluster;
		if(cluster==null)
			throw new IllegalStateException("cluster network not enabled");
		cluster.sendPing(count);
	}

	// ----------------------- transaction

	/**
	 * @param name
	 * 	a name for the transaction, useful for debugging.
	 * 	This name is used in {@link Transaction#toString()}.
	 * @throws IllegalStateException
	 *    if there is already a transaction bound
	 *    to the current thread for this model
	 * @see #startTransactionTry(String)
	 */
	public Transaction startTransaction(final String name)
	{
		final Transaction previousTransaction = transactions.currentIfBound();
		if(previousTransaction!=null)
		{
			throw new IllegalStateException(
					"tried to start a new transaction " +
					(name!=null ? name : Transaction.ANONYMOUS) +
					", but there is already a transaction " + previousTransaction +
					" started on " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS z (Z)", Locale.ENGLISH).format(previousTransaction.getStartDate()) +
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
		return transactions.hasCurrent();
	}

	/**
	 * Returns the transaction for this model,
	 * that is bound to the currently running thread.
	 * @throws IllegalStateException if there is no cope transaction bound to current thread
	 * @see Thread#currentThread()
	 */
	public Transaction currentTransaction()
	{
		return transactions.current();
	}

	public void rollback()
	{
		commitOrRollback(false);
	}

	public void rollbackIfNotCommitted()
	{
		final Transaction t = transactions.currentIfBound();
		if( t!=null )
			rollback();
	}

	public void commit()
	{
		commitOrRollback(true);
	}

	private void commitOrRollback(final boolean commit)
	{
		final Transaction tx = transactions.current();

		// NOTE:
		// Calling Pre-Commit Hooks must be the very first thing to do. Within the hook
		// the transaction must be still available and usable. If one of the hooks
		// fails, the commit fails as well, and probably the transaction is rolled back
		// subsequently.
		tx.preCommitHooks.handle(commit);

		transactions.remove(tx);

		tx.commitOrRollback(commit, this, transactionCounter);

		final Connect connect = tx.connect;
		if(connect.properties.cacheStamps)
		{
			final long oldestStamp = transactions.getOldestCacheStamp(connect.cacheStamp);

			@SuppressWarnings("deprecation") // needed for idea
			final Runnable within = withinPurgeStamps;
			if(within!=null)
				within.run();

			connect.itemCache.purgeStamps(oldestStamp);
			connect.queryCache.purgeStamps(oldestStamp);
		}

		// NOTE:
		// Calling Post-Commit Hooks must be the very last thing to do. If one of the
		// hooks fails, the transaction should still be successfully and completely
		// committed.
		tx.postCommitHooks.handle(commit);
	}

	/**
	 * @deprecated for unit tests only
	 */
	@Deprecated
	@SuppressFBWarnings("SE_BAD_FIELD") // OK: writeReplace
	volatile Runnable withinPurgeStamps = null;

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
	 * Adds a hook to the current transaction.
	 * The hook is called within {@link Model#commit()}.
	 * When the hook is called, the transaction is not yet committed
	 * and still available for use.
	 * If you don't want this, use a
	 * {@link #addPostCommitHookIfAbsent(Runnable) post-commit} hook instead.
	 * <p>
	 * Multiple hooks are called in order of addition.
	 * <p>
	 * If a hook {@link Object#equals(Object) equal} to {@code hook} has been added before,
	 * this method does nothing and returns the hook previously added.
	 * Otherwise {@code hook} is returned.
	 * Note: this is different from {@link Map#putIfAbsent(Object, Object) Map.putIfAbsent}.
	 * <p>
	 * Note, this is something completely different than
	 * {@link #addChangeListener(ChangeListener) Change Listeners}.
	 *
	 * @return the hook that is present after methods returns.
	 *
	 * @see Transaction#getPreCommitHookCount()
	 * @see #addPostCommitHookIfAbsent(Runnable)
	 */
	@Nonnull
	public <R extends Runnable> R addPreCommitHookIfAbsent(final R hook)
	{
		return transactions.current().preCommitHooks.add(hook);
	}

	/**
	 * Adds a hook to the current transaction.
	 * The hook is called within {@link Model#commit()}.
	 * When the hook is called, the transaction is already committed
	 * and not available for use anymore.
	 * If you don't want this, use a
	 * {@link #addPreCommitHookIfAbsent(Runnable) pre-commit} hook instead.
	 * <p>
	 * Multiple hooks are called in order of addition.
	 * <p>
	 * If a hook {@link Object#equals(Object) equal} to {@code hook} has been added before,
	 * this method does nothing and returns the hook previously added.
	 * Otherwise {@code hook} is returned.
	 * Note: this is different from {@link Map#putIfAbsent(Object, Object) Map.putIfAbsent}.
	 * <p>
	 * Note, this is something completely different than
	 * {@link #addChangeListener(ChangeListener) Change Listeners}.
	 *
	 * @return the hook that is present after methods returns.
	 *
	 * @see Transaction#getPostCommitHookCount()
	 * @see #addPreCommitHookIfAbsent(Runnable)
	 */
	@Nonnull
	public <R extends Runnable> R addPostCommitHookIfAbsent(final R hook)
	{
		return transactions.current().postCommitHooks.add(hook);
	}

	/**
	 * @see #startTransaction(String)
	 */
	public TransactionTry startTransactionTry(final String name)
	{
		startTransaction(name);
		return tx;
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
	 * @see ItemFunction#checkTypeColumnL()
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

	// serialization -------------

	private static final long serialVersionUID = 1l;

	private Serialized serialized = null;

	public boolean isSerializationEnabled()
	{
		return serialized!=null;
	}

	public void enableSerialization(final Class<?> type, final String name)
	{
		requireNonNull(type, "type");
		requireNonNull(name, "name");
		if(serialized!=null)
			throw new IllegalStateException("enableSerialization already been called for " + serialized);
		final Serialized serialized = new Serialized(type, name);
		final Object other = serialized.resolveModel();
		if(this!=other)
			throw new IllegalArgumentException("enableSerialization does not resolve to itself " + serialized);

		this.serialized = serialized;
	}

	/**
	 * <a href="https://java.sun.com/j2se/1.5.0/docs/guide/serialization/spec/output.html#5324">See Spec</a>
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
		if(name!=null)
			return name;

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
		 * <a href="https://java.sun.com/j2se/1.5.0/docs/guide/serialization/spec/input.html#5903">See Spec</a>
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
				throw new IllegalArgumentException(this + " does not exist.", e);
			}
			if((field.getModifiers()&STATIC_FINAL)!=STATIC_FINAL)
				throw new IllegalArgumentException(this + " is not static final.");
			field.setAccessible(true);
			final Object result;
			try
			{
				result = field.get(null);
			}
			catch(final IllegalAccessException e)
			{
				throw new IllegalArgumentException("accessing " + field, e);
			}

			if(result==null)
				throw new IllegalArgumentException(this + " is null.");
			if(!(result instanceof Model))
				throw new IllegalArgumentException(this + " is not a model, but " + result.getClass().getName() + '.');

			return result;
		}

		private static final int STATIC_FINAL = Modifier.STATIC | Modifier.FINAL;
	}

	// ------------------- deprecated stuff -------------------

	/** use {@link #getItemCacheStatistics()} */
	@Deprecated
	public ItemCacheInfo[] getItemCacheInfo()
	{
		return getItemCacheStatistics().getDetails();
	}

	/**
	 * @deprecated Use {@link #addPostCommitHookIfAbsent(Runnable)} instead
	 */
	@Deprecated
	public void addPostCommitHook(final Runnable hook)
	{
		addPostCommitHookIfAbsent(hook);
	}

	/**
	 * @deprecated Use Model.{@link #builder() builder}().{@link ModelBuilder#add(Revisions.Factory) add}(revisions).{@link ModelBuilder#add(Type[]) add}(types).{@link ModelBuilder#build() build}() instead.
	 */
	@Deprecated
	@SuppressWarnings("RedundantCast")
	public Model(final Revisions.Factory revisions, final Type<?>... types)
	{
		this(revisions, (TypeSet[])null, types);
	}

	/**
	 * @deprecated Use Model.{@link #builder() builder}().{@link ModelBuilder#add(Revisions.Factory) add}(revisions).{@link ModelBuilder#add(TypeSet[]) add}(typeSets).{@link ModelBuilder#add(Type[]) add}(types).{@link ModelBuilder#build() build}() instead.
	 */
	@Deprecated
	public Model(final Revisions.Factory revisions, final TypeSet[] typeSets, final Type<?>... types)
	{
		this(null, revisions, typeSets, types, DefaultChangeHook.factory());
	}

	/**
	 * @deprecated Use {@link #addPostCommitHookIfAbsent(Runnable)} instead
	 */
	@Deprecated
	public void addCommitHook(final Runnable hook)
	{
		addPostCommitHookIfAbsent(hook);
	}

	/**
	 * @deprecated Use {@link #isClusterEnabled()} instead
	 */
	@Deprecated
	public boolean isClusterNetworkEnabled()
	{
		return isClusterEnabled();
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
	 * calls {@link #reviseIfSupportedAndAutoEnabled}
	 * @deprecated use {@link #reviseIfSupportedAndAutoEnabled} instead
	 */
	@Deprecated
	public void reviseIfSupported()
	{
		reviseIfSupportedAndAutoEnabled();
	}

	/**
	 * @deprecated
	 * Is not supported any longer.
	 * Always returns true.
	 */
	@Deprecated
	@SuppressWarnings("static-method")
	public boolean nullsAreSortedLow()
	{
		return true;
	}

	/**
	 * @deprecated Use {@link #startTransaction(String)} instead and provide a useful name.
	 */
	@Deprecated
	public Transaction startTransaction()
	{
		return startTransaction(null);
	}

	/**
	 * @deprecated
	 * ModificationListener is no longer supported.
	 * Always returns an empty list.
	 */
	@Deprecated
	@SuppressWarnings("static-method")
	public List<ModificationListener> getModificationListeners()
	{
		return ModificationListeners.get();
	}

	/**
	 * @deprecated
	 * ModificationListener is no longer supported.
	 * Always returns zero.
	 */
	@Deprecated
	@SuppressWarnings("static-method")
	public int getModificationListenersCleared()
	{
		return ModificationListeners.getCleared();
	}

	/**
	 * @deprecated
	 * ModificationListener is no longer supported.
	 * Always throws a {@link NoSuchMethodError}.
	 */
	@Deprecated
	@SuppressWarnings("static-method")
	public void addModificationListener(final ModificationListener listener)
	{
		ModificationListeners.add(listener);
	}

	/**
	 * @deprecated
	 * ModificationListener is no longer supported.
	 */
	@Deprecated
	@SuppressWarnings("static-method")
	public void removeModificationListener(final ModificationListener listener)
	{
		ModificationListeners.remove(listener);
	}

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
	 * Checks the schema,
	 * whether the tables representing the types do exist.
	 * @deprecated
	 * This functionality is no longer supported.
	 * This method does nothing.
	 */
	@Deprecated
	public void checkSchema()
	{
		// empty
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
	public static boolean isLoggingEnabled()
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
	private static Revisions.Factory wrap(final RevisionsFuture revisions)
	{
		if(revisions==null)
			return null;

		return ctx -> revisions.get(ctx.getEnvironment());
	}
}
