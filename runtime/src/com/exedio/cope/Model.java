/*
 * Copyright (C) 2004-2006  exedio GmbH (www.exedio.com)
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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import com.exedio.cope.util.CacheInfo;
import com.exedio.cope.util.CacheQueryInfo;
import com.exedio.cope.util.ConnectionPoolInfo;
import com.exedio.cope.util.ModificationListener;
import com.exedio.dsmf.Schema;


public final class Model
{
	private final Type<?>[] types;
	private final Type<?>[] concreteTypes;
	private final Type<?>[] typesSorted;
	final int concreteTypeCount;
	private final List<Type<?>> typeList;
	private final List<Type<?>> concreteTypeList;
	private final HashMap<String, Type> typesByID = new HashMap<String, Type>();
	final List<ModificationListener> modificationListeners = Collections.synchronizedList(new ArrayList<ModificationListener>());

	// set by setPropertiesInitially
	private Properties properties;
	private Object propertiesLock = new Object();
	private Database database;
	private Cache cache;
	private boolean logTransactions = false;

	private final ThreadLocal<Transaction> transactionThreads = new ThreadLocal<Transaction>();
	private final Set<Transaction> openTransactions = Collections.synchronizedSet(new HashSet<Transaction>());
	
	public Model(final Type[] types)
	{
		this.types = types;
		this.typeList = Collections.unmodifiableList(Arrays.asList(this.types));
		this.typesSorted = sort(this.types);
		assert types.length==typesSorted.length;

		int concreteTypeCount = 0;
		int abstractTypeCount = -1;
		final ArrayList<Type<?>> concreteTypes = new ArrayList<Type<?>>();

		for(final Type<?> type : this.types)
		{
			if(typesByID.put(type.id, type)!=null)
				throw new RuntimeException(type.id);
			if(!type.isAbstract)
				concreteTypes.add(type);
		}
		
		for(final Type<?> type : this.typesSorted)
			type.initialize(this, type.isAbstract ? abstractTypeCount-- : concreteTypeCount++);
		
		for(final Type<?> type : this.typesSorted)
			type.postInitialize();
		
		this.concreteTypeCount = concreteTypeCount;
		this.concreteTypes = concreteTypes.toArray(new Type[concreteTypeCount]);
		this.concreteTypeList = Collections.unmodifiableList(Arrays.asList(this.concreteTypes));
		
		assert this.concreteTypeCount==this.concreteTypes.length;
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
	
	/**
	 * Initially sets the properties for this model.
	 * Can be called multiple times, but only the first time
	 * takes effect.
	 * Any subsequent calls must give properties equal to properties given
	 * on the first call, otherwise a RuntimeException is thrown.
	 * <p>
	 * Usually you may want to use this method, if you want to initialize model
	 * from different servlets with equal properties in an undefined order.
	 *
	 * @throws RuntimeException if a subsequent call provides properties different
	 * 									to the first call.
	 */
	public void setPropertiesInitially(final Properties properties)
	{
		if(properties==null)
			throw new NullPointerException();

		synchronized(propertiesLock)
		{
			if(this.properties==null)
			{
				if(this.database!=null)
					throw new RuntimeException();
		
				this.properties = properties;
				this.database = properties.createDatabase();
				
				for(final Type type : typesSorted)
					type.materialize(database);
				
				final int[] cacheMapSizeLimits = new int[concreteTypeCount];
				final int cacheMapSizeLimit = properties.getCacheLimit() / concreteTypeCount;
				Arrays.fill(cacheMapSizeLimits, cacheMapSizeLimit);
				final Properties p = getProperties();
				this.cache = new Cache(cacheMapSizeLimits, p.getCacheQueryLimit(), p.getCacheQueryHistogram());
				this.logTransactions = properties.getTransactionLog();

				return;
			}
		}
		
		// can be done outside the synchronized block
		this.properties.ensureEquality(properties);
	}

	public List<Type<?>> getTypes()
	{
		return typeList;
	}
	
	public List<Type<?>> getConcreteTypes()
	{
		return concreteTypeList;
	}

	/**
	 * @see Type#getID()
	 */
	public Type findTypeByID(final String id)
	{
		if(this.properties==null)
			throw newNotInitializedException();

		return typesByID.get(id);
	}
	
	/**
	 * @see Feature#getID()
	 */
	public Feature findFeatureByID(final String id)
	{
		if(this.properties==null)
			throw newNotInitializedException();

		final int pos = id.indexOf(Feature.ID_SEPARATOR);
		if(pos<0)
			return null;
		final Type t = typesByID.get(id.substring(0, pos));
		if(t==null)
			return null;
		return t.getDeclaredFeature(id.substring(pos+1));
	}
	
	Type getConcreteType(final int transientNumber)
	{
		return concreteTypes[transientNumber];
	}
	
	private RuntimeException newNotInitializedException()
	{
		throw new RuntimeException("model not yet initialized, use setPropertiesInitially");
	}
	
	public Properties getProperties()
	{
		if(properties==null)
			throw newNotInitializedException();

		return properties;
	}
	
	public boolean supportsCheckConstraints()
	{
		return database.driver.supportsCheckConstraints();
	}
	
	/**
	 * Returns, whether the database can store empty strings.
	 * <p>
	 * If true, an empty string can be stored into a {@link StringAttribute}
	 * like any other string via {@link FunctionAttribute#set(Item,Object)}.
	 * A subsequent retrieval of that string via {@link FunctionAttribute#get(Item)}
	 * returns an empty string.
	 * If false, an empty string stored into a {@link StringAttribute} is
	 * converted to null, thus a subsequent retrieval of that string returns
	 * null.
	 * <p>
	 * Up to now, only Oracle does not support empty strings.
	 */
	public boolean supportsEmptyStrings()
	{
		return !getProperties().getDatabaseDontSupportEmptyStrings() && database.supportsEmptyStrings();
	}

	public boolean supportsRightOuterJoins()
	{
		return database.supportsRightOuterJoins();
	}

	Database getDatabase()
	{
		if(database==null)
			throw newNotInitializedException();

		return database;
	}
	
	/**
	 * @return the listener previously registered for this model
	 */
	DatabaseListener setDatabaseListener(final DatabaseListener listener)
	{
		return database.setListener(listener);
	}
	
	public void createDatabase()
	{
		for(int i = 0; i<types.length; i++)
			createDataDirectories(types[i]);

		database.createDatabase();
		clearCache();
	}

	public void createDatabaseConstraints(final int mask)
	{
		database.createDatabaseConstraints(mask);
	}

	private void createDataDirectories(final Type type)
	{
		File typeDirectory = null;

		for(Iterator i = type.getAttributes().iterator(); i.hasNext(); )
		{
			final Attribute attribute = (Attribute)i.next();
			if((attribute instanceof DataAttribute) && !((DataAttribute)attribute).impl.blob)
			{
				if(typeDirectory==null)
				{
					final File directory = properties.getDatadirPath();
					typeDirectory = new File(directory, type.id);
					typeDirectory.mkdir();
				}
				final File attributeDirectory = new File(typeDirectory, attribute.getName());
				attributeDirectory.mkdir();
			}
		}
	}

	private void dropDataDirectories(final Type type)
	{
		File typeDirectory = null;

		for(Iterator i = type.getAttributes().iterator(); i.hasNext(); )
		{
			final Attribute attribute = (Attribute)i.next();
			if(attribute instanceof DataAttribute && !((DataAttribute)attribute).impl.blob)
			{
				if(typeDirectory==null)
				{
					final File directory = properties.getDatadirPath();
					typeDirectory = new File(directory, type.id);
				}
				final File attributeDirectory = new File(typeDirectory, attribute.getName());
				final File[] files = attributeDirectory.listFiles();
				for(int j = 0; j<files.length; j++)
				{
					final File file = files[j];
					if(!file.delete())
						throw new RuntimeException("delete failed: "+file.getAbsolutePath());
				}
				if(!attributeDirectory.delete())
					throw new RuntimeException("delete failed: "+attributeDirectory.getAbsolutePath());
			}
		}

		if(typeDirectory!=null)
		{
			if(!typeDirectory.delete())
				throw new RuntimeException("delete failed: "+typeDirectory.getAbsolutePath());
		}
	}

	private void tearDownDataDirectories(final Type type)
	{
		File typeDirectory = null;

		for(Iterator i = type.getAttributes().iterator(); i.hasNext(); )
		{
			final Attribute attribute = (Attribute)i.next();
			if(attribute instanceof DataAttribute && !((DataAttribute)attribute).impl.blob)
			{
				if(typeDirectory==null)
				{
					final File directory = properties.getDatadirPath();
					typeDirectory = new File(directory, type.id);
				}
				final File attributeDirectory = new File(typeDirectory, attribute.getName());
				if(attributeDirectory.exists())
				{
					final File[] files = attributeDirectory.listFiles();
					for(int j = 0; j<files.length; j++)
						files[j].delete();
	
					attributeDirectory.delete();
				}
			}
		}

		if(typeDirectory!=null)
			typeDirectory.delete();
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
		// TODO: check for data attribute directories
		database.checkDatabase(getCurrentTransaction().getConnection());
	}

	public void checkEmptyDatabase()
	{
		database.checkEmptyDatabase(getCurrentTransaction().getConnection());
	}

	public void dropDatabase()
	{
		// TODO: rework this method
		final List<Type<?>> types = typeList;
		for(ListIterator<Type<?>> i = types.listIterator(types.size()); i.hasPrevious(); )
			i.previous().onDropTable();

		database.dropDatabase();

		for(int i = 0; i<this.types.length; i++)
			dropDataDirectories(this.types[i]);
		
		clearCache();
	}

	public void dropDatabaseConstraints(final int mask)
	{
		database.dropDatabaseConstraints(mask);
	}

	public void tearDownDatabase()
	{
		database.tearDownDatabase();

		for(int i = 0; i<this.types.length; i++)
			tearDownDataDirectories(this.types[i]);

		clearCache();
	}
	
	public void tearDownDatabaseConstraints(final int mask)
	{
		database.tearDownDatabaseConstraints(mask);
	}

	public void close()
	{
		database.close();
	}

	public Schema getVerifiedSchema()
	{
		// TODO: check data directories
		return database.makeVerifiedSchema();
	}

	public Schema getSchema()
	{
		return database.makeSchema();
	}

	/**
	 * Returns the item with the given ID.
	 * Always returns {@link Item#activeCopeItem() active} objects.
	 * @see Item#getCopeID()
	 * @throws NoSuchIDException if there is no item with the given id.
	 */
	public Item findByID(final String id)
			throws NoSuchIDException
	{
		final int pos = id.lastIndexOf('.');
		if(pos<=0)
			throw new NoSuchIDException(id, true, "no dot in id");

		final String typeID = id.substring(0, pos);
		final Type type = findTypeByID(typeID);
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

		final int pk = type.getPkSource().id2pk(idNumber, id);
		
		final Item result = type.getItemObject(pk);
		if ( ! result.existsCopeItem() )
		{
			throw new NoSuchIDException(id, false, "item <"+idNumber+"> does not exist");
		}
		return result;
	}
	
	public void addModificationListener(final ModificationListener listener)
	{
		// TODO do not hard link to allow GC remove listeners
		modificationListeners.add(listener);
	}
	
	public void removeModificationListener(final ModificationListener listener)
	{
		modificationListeners.remove(listener);
	}
	
	public CacheInfo[] getCacheInfo()
	{
		if(cache==null)
			throw newNotInitializedException();
		
		return cache.getInfo(concreteTypes);
	}
	
	public int[] getCacheQueryInfo()
	{
		if(cache==null)
			throw newNotInitializedException();
		
		return cache.getQueryInfo();
	}
	
	public CacheQueryInfo[] getCacheQueryHistogram()
	{
		if(cache==null)
			throw newNotInitializedException();
		
		return cache.getQueryHistogram();
	}
	
	public ConnectionPoolInfo getConnectionPoolInfo()
	{
		if(database==null)
			throw newNotInitializedException();
		
		return database.getConnectionPool().getInfo();
	}
	
	public java.util.Properties getDatabaseInfo()
	{
		if(database==null)
			throw newNotInitializedException();
		final Database db = database;
		
		final java.util.Properties result = new java.util.Properties();
		result.setProperty("database.name", db.databaseProductName);
		result.setProperty("database.version", db.databaseProductVersion + ' ' + '(' + db.databaseMajorVersion + '.' + db.databaseMinorVersion + ')');
		result.setProperty("driver.name", db.driverName);
		result.setProperty("driver.version", db.driverVersion + ' ' + '(' + db.driverMajorVersion + '.' + db.driverMinorVersion + ')');
		return result;
	}

	// ----------------------- transaction
	
	public Transaction startTransaction()
	{
		return startTransaction(null);
	}
	
	/**
	 * @param name
	 * 	a name for the transaction, useful for debugging.
	 * 	This name is used in {@link Transaction#toString()}.
	 * @throws RuntimeException
	 *    if there is already a transaction bound
	 *    to the current thread for this model
	 */
	public Transaction startTransaction(final String name)
	{
		if(database==null)
			throw newNotInitializedException();
		
		if(logTransactions)
			System.out.println("transaction start " + name);

		if( hasCurrentTransaction() )
			throw new RuntimeException("there is already a transaction bound to current thread");
		final Transaction result = new Transaction(this, name);
		setTransaction( result );
		openTransactions.add( result );
		return result;
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
	 * @see Thread#currentThread()
	 */
	public Transaction getCurrentTransaction()
	{
		final Transaction result = getCurrentTransactionIfAvailable();
		if(result==null)
		{
			throw new RuntimeException("there is no cope transaction bound to this thread, see Model#startTransaction");
		}
		assert result.assertBoundToCurrentThread();
		return result;
	}
	
	private Transaction getCurrentTransactionIfAvailable()
	{
		final Transaction result = transactionThreads.get();
		assert result==null || result.assertBoundToCurrentThread();
		return result;
	}
	
	private void setTransaction(final Transaction transaction)
	{
		if(transaction!=null)
		{
			transaction.bindToCurrentThread();
		}
		transactionThreads.set(transaction);
	}
	
	public void rollback()
	{
		Transaction tx = getCurrentTransaction();
		
		if(logTransactions)
			System.out.println("transaction rollback " + tx.name);
		
		openTransactions.remove( tx );
		setTransaction(null);
		tx.commitOrRollback(true);
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
		Transaction tx = getCurrentTransaction();
		
		if(logTransactions)
			System.out.println("transaction commit " + tx.name);
		
		openTransactions.remove( tx );
		setTransaction(null);
		tx.commitOrRollback(false);
	}

	/**
	 *	Returns true if the database supports READ_COMMITTED or any more strict transaction isolation level.
	 */
	boolean supportsReadCommitted()
	{
		return database.supportsReadCommitted;
	}
	
	/**
	 * Returns the collection of open {@link Transaction}s
	 * on this model.
	 * <p>
	 * Returns a unmodifiable synchronized view on the actual data,
	 * so iterating over the collection on a live server may cause
	 * {@link java.util.ConcurrentModificationException}s.
	 * For such cases you may want to create a copy of the collection first.
	 */
	public Collection<Transaction> getOpenTransactions()
	{
		return Collections.unmodifiableCollection( openTransactions );
	}
	
	Cache getCache()
	{
		if(cache==null)
			throw newNotInitializedException();
		
		return cache;
	}
	
	public void clearCache()
	{
		cache.clear();
	}
	
	/**
	 * @see ItemFunction#checkTypeColumn()
	 */
	public void checkTypeColumns()
	{
		for(final Type<?> t : getTypes())
		{
			checkTypeColumn(t.thisFunction);
			for(final Attribute a : t.getDeclaredAttributes())
				if(a instanceof ItemAttribute)
					checkTypeColumn((ItemAttribute)a);
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
		database.makeSchema().checkUnsupportedConstraints();
	}
}
