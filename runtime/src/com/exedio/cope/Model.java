/*
 * Copyright (C) 2004-2008  exedio GmbH (www.exedio.com)
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
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import com.exedio.cope.util.CacheInfo;
import com.exedio.cope.util.CacheQueryInfo;
import com.exedio.cope.util.ConnectionPoolInfo;
import com.exedio.cope.util.ModificationListener;
import com.exedio.dsmf.Constraint;
import com.exedio.dsmf.Schema;

public final class Model
{
	private final boolean revisionEnabled;
	private int revisionNumber;
	private Revision[] revisions;
	private final Object revisionLock = new Object();
	
	private final Type<?>[] types;
	private final Type<?>[] concreteTypes;
	private final Type<?>[] typesSorted;
	private final int concreteTypeCount;
	private final List<Type<?>> typeList;
	private final List<Type<?>> typeListSorted;
	private final List<Type<?>> concreteTypeList;
	private final HashMap<String, Type> typesByID = new HashMap<String, Type>();
	private final HashMap<String, Feature> featuresByID = new HashMap<String, Feature>();
	private final Date initializeDate;
	private final LinkedList<WeakReference<ModificationListener>> modificationListeners = new LinkedList<WeakReference<ModificationListener>>();
	private int modificationListenersCleared = 0;

	// set by connect
	private final Object connectLock = new Object();
	private ConnectProperties propertiesIfConnected;
	private Database databaseIfConnected;
	private ItemCache itemCacheIfConnected;
	private QueryCache queryCacheIfConnected;
	private Date connectDate = null;
	private boolean logTransactions = false;

	private long nextTransactionId = 0;
	private long lastTransactionStartDate = Long.MIN_VALUE;
	private final Object nextTransactionIdLock = new Object();
	
	private final HashSet<Transaction> openTransactions = new HashSet<Transaction>();
	private final ThreadLocal<Transaction> boundTransactions = new ThreadLocal<Transaction>();
	
	public Model(final Type... types)
	{
		this(-1, null, types);
	}
	
	private static final int checkRevisionNumber(final int revisionNumber)
	{
		if(revisionNumber<0)
			throw new IllegalArgumentException("revision number must not be negative, but was " + revisionNumber);
		return revisionNumber;
	}
	
	public Model(final int revisionNumber, final Type... types)
	{
		this(checkRevisionNumber(revisionNumber), new Revision[0], types);
	}
	
	private static final Revision[] checkRevisions(final Revision[] revisions)
	{
		if(revisions==null)
			throw new NullPointerException("revisions must not be null");
		if(revisions.length==0)
			throw new IllegalArgumentException("revisions must not be empty");
		
		// make a copy to avoid modifications afterwards
		final Revision[] result = new Revision[revisions.length];
		System.arraycopy(revisions, 0, result, 0, revisions.length);
		
		int base = -1;
		for(int i = 0; i<result.length; i++)
		{
			final Revision revision = result[i];
			if(revision==null)
				throw new NullPointerException("revision must not be null, but was at index " + i);
			
			if(i==0)
				base = revision.number;
			else
			{
				final int expectedNumber = base-i;
				if(revision.number!=expectedNumber)
					throw new IllegalArgumentException("inconsistent revision number at index " + i + ", expected " + expectedNumber + ", but was " + revision.number);
			}
		}
		
		return result;
	}
	
	private static final int revisionNumber(final Revision[] revisions)
	{
		if(revisions==null)
			return -1;
		else if(revisions.length==0)
			return 0;
		else
			return revisions[0].number;
	}
	
	public Model(final Revision[] revisions, final Type... types)
	{
		this(revisionNumber(revisions), checkRevisions(revisions), types);
	}
	
	private Model(final int revisionNumber, final Revision[] revisions, final Type... types)
	{
		assert (revisionNumber>=0) == (revisions!=null);
		
		this.revisionEnabled = (revisions!=null);
		this.revisionNumber = revisionNumber;
		this.revisions = revisions;
		
		if(types==null)
			throw new NullPointerException("types must not be null");
		if(types.length==0)
			throw new IllegalArgumentException("types must not be empty");
		
		final Type<?>[] explicitTypes = types;
		final Type<?>[] explicitTypesSorted = sort(explicitTypes);
		assert types.length==explicitTypesSorted.length;

		final ArrayList<Type<?>> typesL = new ArrayList<Type<?>>();
		for(final Type<?> type : explicitTypes)
			addTypeIncludingGenerated(type, typesL, 10);
		
		final ArrayList<Type<?>> concreteTypes = new ArrayList<Type<?>>();
		for(final Type<?> type : typesL)
		{
			final Type collisionType = typesByID.put(type.id, type);
			if(collisionType!=null)
				throw new IllegalArgumentException("duplicate type id \"" + type.id + "\" for classes " + collisionType.getJavaClass().getName() + " and " + type.getJavaClass().getName());
			if(!type.isAbstract)
				concreteTypes.add(type);
			
			for(final Feature feature : type.getDeclaredFeatures())
				if(featuresByID.put(feature.getID(), feature)!=null)
					throw new IllegalArgumentException("duplicate feature id \"" + feature.getID() + '"');
		}
		
		final ArrayList<Type<?>> typesSorted = new ArrayList<Type<?>>();
		for(final Type<?> type : explicitTypesSorted)
			addTypeIncludingGenerated(type, typesSorted, 10);

		int concreteTypeCount = 0;
		int abstractTypeCount = -1;
		for(final Type<?> type : typesSorted)
			type.initialize(this, type.isAbstract ? abstractTypeCount-- : concreteTypeCount++);
		
		for(final Type<?> type : typesSorted)
			type.postInitialize();
		
		this.types = typesL.toArray(new Type[typesL.size()]);
		this.typeList = Collections.unmodifiableList(typesL);
		this.concreteTypeCount = concreteTypeCount;
		this.concreteTypes = concreteTypes.toArray(new Type[concreteTypeCount]);
		this.concreteTypeList = Collections.unmodifiableList(Arrays.asList(this.concreteTypes));
		this.typesSorted = typesSorted.toArray(new Type[typesSorted.size()]);
		this.typeListSorted = Collections.unmodifiableList(Arrays.asList(this.typesSorted));
		this.initializeDate = new Date();
		
		assert this.concreteTypeCount==this.concreteTypes.length;
		assert this.concreteTypeCount==this.concreteTypeList.size();
	}
	
	private static final Type<?>[] sort(final Type<?>[] types)
	{
		final HashSet<Type> typeSet = new HashSet<Type>(Arrays.asList(types));
		final HashSet<Type> done = new HashSet<Type>();
		//System.out.println(">--------------------"+Arrays.asList(types));

		final ArrayList<Type> result = new ArrayList<Type>();
		for(int i = 0; i<types.length; i++)
		{
			final ArrayList<Type> stack = new ArrayList<Type>();

			//System.out.println("------------------------------ "+types[i].getID());

			for(Type type = types[i]; type!=null; type=type.supertype)
			{
				//System.out.println("-------------------------------> "+type.getID());
				if(!typeSet.contains(type))
					throw new RuntimeException("type "+type.id+ " is supertype of " + types[i].id + " but not part of the model");
				stack.add(type);
			}
			
			for(ListIterator<Type> j = stack.listIterator(stack.size()); j.hasPrevious(); )
			{
				final Type type = j.previous();
				//System.out.println("-------------------------------) "+type.getID());

				if(!done.contains(type))
				{
					//System.out.println("-------------------------------] "+type.getID());
					result.add(type);
					done.add(type);
				}
			}
		}
		if(!done.equals(typeSet))
			throw new RuntimeException(done.toString()+"<->"+typeSet.toString());
		
		//System.out.println("<--------------------"+result);
		return result.toArray(new Type[]{});
	}
	
	private static final void addTypeIncludingGenerated(final Type<?> type, final ArrayList<Type<?>> result, int hopCount)
	{
		hopCount--;
		if(hopCount<0)
			throw new RuntimeException();
		
		result.add(type);
		for(final Feature f : type.getDeclaredFeatures())
			if(f instanceof Pattern)
				for(final Type<?> generatedType : ((Pattern)f).generatedTypes)
					addTypeIncludingGenerated(generatedType, result, hopCount);
	}
	
	public Map<Feature, Feature> getHiddenFeatures()
	{
		final HashMap<Feature, Feature> result = new HashMap<Feature, Feature>();
		for(final Type<?> t : types)
		{
			final Type st = t.getSupertype();
			if(st==null)
				continue;
			
			for(final Feature f : t.getDeclaredFeatures())
			{
				if(f instanceof Type.This)
					continue;
				
				final Feature hidden = st.getFeature(f.getName());
				if(hidden!=null)
				{
					final Feature previous = result.put(f, hidden);
					assert previous==null;
				}
			}
		}
		return result;
	}
	
	/**
	 * Connects this model to the database described in the properties.
	 *
	 * @throws IllegalStateException if this model has already been connected.
	 */
	public void connect(final ConnectProperties properties)
	{
		if(properties==null)
			throw new NullPointerException();

		synchronized(connectLock)
		{
			if(this.propertiesIfConnected==null)
			{
				if(this.databaseIfConnected!=null)
					throw new RuntimeException();
				if(this.itemCacheIfConnected!=null)
					throw new RuntimeException();
				if(this.queryCacheIfConnected!=null)
					throw new RuntimeException();
				if(this.connectDate!=null)
					throw new RuntimeException();
		
				// do this at first, to avoid half-connected model if probe connection fails
				final Database db = properties.createDatabase(revisionEnabled);
				this.propertiesIfConnected = properties;
				this.databaseIfConnected = db;
				
				for(final Type type : typesSorted)
					type.connect(db);
				
				final int[] itemCacheLimits = new int[concreteTypeCount];
				final int itemCacheLimit = properties.getItemCacheLimit() / concreteTypeCount;
				Arrays.fill(itemCacheLimits, itemCacheLimit);
				this.itemCacheIfConnected = new ItemCache(concreteTypes, itemCacheLimits);
				this.queryCacheIfConnected = new QueryCache(properties.getQueryCacheLimit());
				this.logTransactions = properties.getTransactionLog();
				this.connectDate = new Date();
			}
			else
				throw new IllegalStateException("model already been connected"); // TODO reorder code
		}
	}
	
	public void disconnect()
	{
		synchronized(connectLock)
		{
			if(this.propertiesIfConnected!=null)
			{
				if(this.databaseIfConnected==null)
					throw new RuntimeException();
				if(this.itemCacheIfConnected==null)
					throw new RuntimeException();
				if(this.queryCacheIfConnected==null)
					throw new RuntimeException();
				if(this.connectDate==null)
					throw new RuntimeException();
		
				this.propertiesIfConnected = null;
				final Database db = this.databaseIfConnected;
				this.databaseIfConnected = null;
				
				for(final Type type : typesSorted)
					type.disconnect();
				
				this.itemCacheIfConnected = null;
				this.queryCacheIfConnected = null;
				this.connectDate = null;
				
				db.close();
			}
			else
				throw new IllegalStateException("model not yet connected, use connect(Properties)"); // TODO reorder code
		}
	}

	public boolean isRevisionEnabled()
	{
		return revisionEnabled;
	}
	
	private final void assertRevisionEnabled()
	{
		if(!revisionEnabled)
			throw new IllegalArgumentException("revisions are not enabled");
	}

	public int getRevisionNumber()
	{
		assertRevisionEnabled();
		return revisionNumber;
	}

	public List<Revision> getRevisions()
	{
		assertRevisionEnabled();
		return Collections.unmodifiableList(Arrays.asList(revisions));
	}
	
	void setRevisions(final Revision[] revisions) // for test only, not for productive use !!!
	{
		assertRevisionEnabled();
		this.revisions = checkRevisions(revisions);
		this.revisionNumber = revisionNumber(revisions);
	}

	public void revise()
	{
		assertRevisionEnabled();
		
		synchronized(revisionLock)
		{
			getDatabase().revise(revisionNumber, revisions);
		}
	}

	public void reviseIfSupported()
	{
		if(!revisionEnabled)
			return;
		
		revise();
	}

	public Map<Integer, byte[]> getRevisionLogs()
	{
		assertRevisionEnabled();
		return getDatabase().getRevisionLogs();
	}
	
	public ConnectProperties getProperties()
	{
		if(propertiesIfConnected==null)
			throw new IllegalStateException("model not yet connected, use connect(Properties)");

		return propertiesIfConnected;
	}
	
	Database getDatabase()
	{
		if(databaseIfConnected==null)
			throw new IllegalStateException("model not yet connected, use connect(Properties)");

		return databaseIfConnected;
	}
	
	ItemCache getItemCache()
	{
		if(itemCacheIfConnected==null)
			throw new IllegalStateException("model not yet connected, use connect(Properties)");

		return itemCacheIfConnected;
	}
	
	QueryCache getQueryCache()
	{
		if(queryCacheIfConnected==null)
			throw new IllegalStateException("model not yet connected, use connect(Properties)");

		return queryCacheIfConnected;
	}
	
	public Date getConnectDate()
	{
		return connectDate;
	}
	
	public List<Type<?>> getTypes()
	{
		return typeList;
	}
	
	public List<Type<?>> getTypesSortedByHierarchy()
	{
		return typeListSorted;
	}
	
	public List<Type<?>> getConcreteTypes()
	{
		return concreteTypeList;
	}

	/**
	 * @see Type#getID()
	 */
	public Type getType(final String id)
	{
		return typesByID.get(id);
	}
	
	/**
	 * @see Feature#getID()
	 */
	public Feature getFeature(final String id)
	{
		return featuresByID.get(id);
	}
	
	Type getConcreteType(final int transientNumber)
	{
		return concreteTypes[transientNumber];
	}
	
	public Date getInitializeDate()
	{
		return initializeDate;
	}
	
	public boolean supportsCheckConstraints()
	{
		return getDatabase().driver.supportsCheckConstraints();
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
		return !getProperties().getDatabaseDontSupportEmptyStrings() && getDatabase().dialect.supportsEmptyStrings();
	}

	public boolean isDatabaseLogEnabled()
	{
		return getDatabase().log!=null;
	}
	
	/**
	 * Threshold time in milliseconds.
	 */
	public int getDatabaseLogThreshold()
	{
		final DatabaseLogConfig log = getDatabase().log;
		return log!=null ? log.threshold : 0;
	}
	
	public String getDatabaseLogSQL()
	{
		final DatabaseLogConfig log = getDatabase().log;
		return log!=null ? log.sql : null;
	}
	
	public void setDatabaseLog(final boolean enable, final int threshold, final String sql, final PrintStream out)
	{
		getDatabase().log = enable ? new DatabaseLogConfig(threshold, sql, out) : null;
	}
	
	/**
	 * @return the listener previously registered for this model
	 */
	DatabaseListener setDatabaseListener(final DatabaseListener listener)
	{
		return getDatabase().setListener(listener);
	}
	
	public void createDatabase()
	{
		getDatabase().createDatabase(revisionNumber);
		clearCache();
	}

	public void createDatabaseConstraints(final EnumSet<Constraint.Type> types)
	{
		getDatabase().createDatabaseConstraints(types);
	}

	/**
	 * Checks the database,
	 * whether the database tables representing the types do exist.
	 * Issues a single database statement,
	 * that touches all tables and columns,
	 * that would have been created by
	 * {@link #createDatabase()}.
	 * @throws RuntimeException
	 * 	if something is wrong with the database.
	 * 	TODO: use a more specific exception.
	 */
	public void checkDatabase()
	{
		getDatabase().checkDatabase(getCurrentTransaction().getConnection());
	}

	public void checkEmptyDatabase()
	{
		getDatabase().checkEmptyDatabase(getCurrentTransaction().getConnection());
	}

	public void dropDatabase()
	{
		IntegerField.flushDefaultNextCache(this);
		
		// TODO: rework this method
		final List<Type<?>> types = typeList;
		for(ListIterator<Type<?>> i = types.listIterator(types.size()); i.hasPrevious(); )
			i.previous().onDropTable();

		getDatabase().dropDatabase();
		clearCache();
	}

	public void dropDatabaseConstraints(final EnumSet<Constraint.Type> types)
	{
		getDatabase().dropDatabaseConstraints(types);
	}

	public void tearDownDatabase()
	{
		getDatabase().tearDownDatabase();
		clearCache();
	}
	
	public void tearDownDatabaseConstraints(final EnumSet<Constraint.Type> types)
	{
		getDatabase().tearDownDatabaseConstraints(types);
	}

	public Schema getVerifiedSchema()
	{
		// TODO: check data directories
		return getDatabase().makeVerifiedSchema();
	}

	public Schema getSchema()
	{
		return getDatabase().makeSchema();
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
		final int pos = id.lastIndexOf(Item.ID_SEPARATOR);
		if(pos<=0)
			throw new NoSuchIDException(id, true, "no separator '" + Item.ID_SEPARATOR + "' in id");

		final String typeID = id.substring(0, pos);
		final Type type = getType(typeID);
		if(type==null)
			throw new NoSuchIDException(id, true, "type <" + typeID + "> does not exist");
		if(type.isAbstract)
			throw new NoSuchIDException(id, true, "type is abstract");
		
		final String idString = id.substring(pos+1);

		final long idNumber;
		try
		{
			idNumber = Long.parseLong(idString);
		}
		catch(NumberFormatException e)
		{
			throw new NoSuchIDException(id, e, idString);
		}

		if(idNumber<0)
			throw new NoSuchIDException(id, true, "must be positive");
		if(idNumber>=2147483648l)
			throw new NoSuchIDException(id, true, "does not fit in 31 bit");
		final int pk = (int)idNumber;
		
		final Item result = type.getItemObject(pk);
		if ( ! result.existsCopeItem() )
		{
			throw new NoSuchIDException(id, false, "item <"+idNumber+"> does not exist");
		}
		return result;
	}
	
	public List<ModificationListener> getModificationListeners()
	{
		synchronized(modificationListeners)
		{
			final int size = modificationListeners.size();
			if(size==0)
				return Collections.<ModificationListener>emptyList();
			
			// make a copy to avoid ConcurrentModificationViolations
			final ArrayList<ModificationListener> result = new ArrayList<ModificationListener>(size);
			int cleared = 0;
			for(final Iterator<WeakReference<ModificationListener>> i = modificationListeners.iterator(); i.hasNext(); )
			{
				final ModificationListener listener = i.next().get();
				if(listener==null)
				{
					i.remove();
					cleared++;
				}
				else
					result.add(listener);
			}
			
			if(cleared>0)
				this.modificationListenersCleared += cleared;
			
			return Collections.unmodifiableList(result);
		}
	}

	public int getModificationListenersCleared()
	{
		synchronized(modificationListeners)
		{
			return modificationListenersCleared;
		}
	}
	
	public void addModificationListener(final ModificationListener listener)
	{
		if(listener==null)
			throw new NullPointerException("listener must not be null");
		
		final WeakReference<ModificationListener> ref = new WeakReference<ModificationListener>(listener);
		synchronized(modificationListeners)
		{
			modificationListeners.add(ref);
		}
	}
	
	public void removeModificationListener(final ModificationListener listener)
	{
		if(listener==null)
			throw new NullPointerException("listener must not be null");

		synchronized(modificationListeners)
		{
			int cleared = 0;
			for(final Iterator<WeakReference<ModificationListener>> i = modificationListeners.iterator(); i.hasNext(); )
			{
				final ModificationListener l = i.next().get();
				if(l==null)
				{
					i.remove();
					cleared++;
				}
				else if(l==listener)
					i.remove();
			}
			if(cleared>0)
				this.modificationListenersCleared += cleared;
		}
	}
	
	public CacheInfo[] getItemCacheInfo()
	{
		return getItemCache().getInfo();
	}
	
	public long[] getQueryCacheInfo()
	{
		return getQueryCache().getQueryInfo();
	}
	
	public CacheQueryInfo[] getQueryCacheHistogram()
	{
		return getQueryCache().getHistogram();
	}
	
	public ConnectionPoolInfo getConnectionPoolInfo()
	{
		return getDatabase().connectionPool.getInfo();
	}
	
	public java.util.Properties getDatabaseInfo()
	{
		final DialectParameters db = getDatabase().dialectParameters;
		final java.util.Properties result = new java.util.Properties();
		result.setProperty("database.name", db.databaseProductName);
		result.setProperty("database.version", db.databaseProductVersion + ' ' + '(' + db.databaseMajorVersion + '.' + db.databaseMinorVersion + ')');
		result.setProperty("driver.name", db.driverName);
		result.setProperty("driver.version", db.driverVersion + ' ' + '(' + db.driverMajorVersion + '.' + db.driverMinorVersion + ')');
		return result;
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
		getDatabase(); // ensure connected
		
		if(logTransactions)
			System.out.println("transaction start " + name);

		final Transaction previousTransaction = getCurrentTransactionIfAvailable();
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
		synchronized(nextTransactionIdLock)
		{
			id = nextTransactionId++;
			lastTransactionStartDate = startDate;
		}
		
		final Transaction result = new Transaction(this, concreteTypeCount, id, name, startDate);
		setTransaction( result );
		synchronized(openTransactions)
		{
			openTransactions.add(result);
		}
		return result;
	}
	
	public long getNextTransactionId()
	{
		synchronized(nextTransactionIdLock)
		{
			return nextTransactionId;
		}
	}
	
	public Date getLastTransactionStartDate()
	{
		final long lastTransactionStartDate;
		synchronized(nextTransactionIdLock)
		{
			lastTransactionStartDate = this.lastTransactionStartDate;
		}
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
		return getCurrentTransactionIfAvailable()!=null;
	}

	/**
	 * Returns the transaction for this model,
	 * that is bound to the currently running thread.
	 * @throws IllegalStateException if there is no cope transaction bound to current thread
	 * @see Thread#currentThread()
	 */
	public Transaction getCurrentTransaction()
	{
		final Transaction result = getCurrentTransactionIfAvailable();
		if(result==null)
		{
			throw new IllegalStateException("there is no cope transaction bound to this thread, see Model#startTransaction");
		}
		assert result.assertBoundToCurrentThread();
		return result;
	}
	
	private Transaction getCurrentTransactionIfAvailable()
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
		final Transaction t = getCurrentTransactionIfAvailable();
		if( t!=null )
		{
			rollback();
		}
	}
	
	public void commit()
	{
		commitOrRollback(false);
	}

	private void commitOrRollback(final boolean rollback)
	{
		final Transaction tx = getCurrentTransaction();
		
		if(logTransactions)
			System.out.println("transaction " + (rollback?"rollback":"commit") + ' ' + tx);
		
		synchronized(openTransactions)
		{
			openTransactions.remove(tx);
		}
		setTransaction(null);
		tx.commitOrRollback(rollback);
	}

	/**
	 *	Returns true if the database supports READ_COMMITTED or any more strict transaction isolation level.
	 */
	boolean supportsReadCommitted()
	{
		return getDatabase().supportsReadCommitted;
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
	
	public void clearCache()
	{
		getItemCache().clear();
		getQueryCache().clear();
	}
	
	/**
	 * @see ItemFunction#checkTypeColumn()
	 */
	public void checkTypeColumns()
	{
		for(final Type<?> t : getTypes())
		{
			checkTypeColumn(t.thisFunction);
			for(final Field a : t.getDeclaredFields())
				if(a instanceof ItemField)
					checkTypeColumn((ItemField)a);
		}
	}
	
	private static final void checkTypeColumn(final ItemFunction f)
	{
		if(f.needsCheckTypeColumn())
		{
			final int count = f.checkTypeColumn();
			if(count!=0)
				throw new RuntimeException("wrong type column for " + f + " on " + count + " tuples.");
		}
	}
	
	public void checkUnsupportedConstraints()
	{
		getDatabase().makeSchema().checkUnsupportedConstraints();
	}
	
	private static final boolean skipIntern = Boolean.valueOf(System.getProperty("com.exedio.cope.skipIntern"));
	
	static
	{
		if(skipIntern)
			System.out.println("COPE: skipping String#intern()");
	}
	
	static final String intern(final String s)
	{
		if(skipIntern)
			return s;
		
		final String result = s.intern();
		//System.out.println("Model.intern >" + s + "< " + (result!=s ? "NEW" : "OLD"));
		return result;
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
	public CacheInfo[] getCacheInfo()
	{
		return getItemCacheInfo();
	}
	
	/**
	 * @deprecated renamed to {@link #getQueryCacheHistogram()}.
	 */
	@Deprecated
	public CacheQueryInfo[] getCacheQueryHistogram()
	{
		return getQueryCacheHistogram();
	}
	
	/**
	 * @deprecated renamed to {@link #getQueryCacheInfo()}.
	 */
	@Deprecated
	public long[] getCacheQueryInfo()
	{
		return getQueryCacheInfo();
	}
	
	/**
	 * @deprecated Use {@link #getRevisionNumber()} instead
	 */
	@Deprecated
	public int getMigrationVersion()
	{
		return getRevisionNumber();
	}

	/**
	 * @deprecated Use {@link #getRevisionNumber()} instead
	 */
	@Deprecated
	public int getMigrationRevision()
	{
		return getRevisionNumber();
	}
	
	/**
	 * @deprecated Use {@link #isRevisionEnabled()} instead
	 */
	@Deprecated
	public boolean isMigrationSupported()
	{
		return isRevisionEnabled();
	}
	
	/**
	 * @deprecated Use {@link #getRevisions()} instead
	 */
	@Deprecated
	public List<Revision> getMigrations()
	{
		return getRevisions();
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
}
