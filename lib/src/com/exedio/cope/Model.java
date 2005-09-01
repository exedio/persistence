/*
 * Copyright (C) 2004-2005  exedio GmbH (www.exedio.com)
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
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import com.exedio.cope.util.PoolCounter;
import com.exedio.dsmf.Schema;


public final class Model
{
	private final Type[] types;
	private final List typeList;
	private final HashMap typesByID = new HashMap();
	final int numberOfTypes;
	
	public Model(final Type[] types)
	{
		this.types = types;
		this.typeList = Collections.unmodifiableList(Arrays.asList(types));

		for(int i = 0; i<types.length; i++)
		{
			final Type type = types[i];
			type.initialize(this, i);
		}
		this.numberOfTypes = types.length;
	}
	
	private Properties properties;
	private Database database;

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
	public final void setPropertiesInitially(final Properties properties)
	{
		if(properties==null)
			throw new NullPointerException();

		if(this.properties!=null)
		{
			this.properties.ensureEquality(properties);
		}
		else
		{
			if(this.database!=null)
				throw new RuntimeException();
	
			this.properties = properties;
			this.database = properties.createDatabase();
			
			final HashSet typeSet = new HashSet(Arrays.asList(types));
			final HashSet materialized = new HashSet();
	
			for(int i = 0; i<types.length; i++)
			{
				final ArrayList stack = new ArrayList();

				//System.out.println("------------------------------ "+types[i].getID());

				for(Type type = types[i]; type!=null; type=type.getSupertype())
				{
					//System.out.println("-------------------------------> "+type.getID());
					if(!typeSet.contains(type))
						throw new RuntimeException("type "+type.getID()+ " is supertype of " + types[i].getID() + " but not part of the model");
					stack.add(type);
				}
				
				for(ListIterator j = stack.listIterator(stack.size()); j.hasPrevious(); )
				{
					final Type type = (Type)j.previous();
					//System.out.println("-------------------------------) "+type.getID());

					if(!materialized.contains(type))
					{
						//System.out.println("-------------------------------] "+type.getID());
						type.materialize(database);
						if(typesByID.put(type.getID(), type)!=null)
							throw new RuntimeException(type.getID());
						materialized.add(type);
					}
				}
			}
			if(!materialized.equals(typeSet))
				throw new RuntimeException(materialized.toString()+"<->"+typeSet.toString());
		}
	}

	public final List getTypes()
	{
		return typeList;
	}
	
	public final Type findTypeByID(final String id)
	{
		if(this.properties==null)
			throw new RuntimeException();

		return (Type)typesByID.get(id);
	}
	
	public final Properties getProperties()
	{
		if(properties==null)
			throw new RuntimeException();

		return properties;
	}
	
	public final boolean supportsCheckConstraints()
	{
		return database.supportsCheckConstraints();
	}

	final Database getDatabase()
	{
		if(database==null)
			throw new RuntimeException();

		return database;
	}
	
	public void createDatabase()
	{
		for(int i = 0; i<types.length; i++)
			createDataDirectories(types[i]);

		database.createDatabase();
	}

	private void createDataDirectories(final Type type)
	{
		File typeDirectory = null;

		for(Iterator i = type.getAttributes().iterator(); i.hasNext(); )
		{
			final Attribute attribute = (Attribute)i.next();
			if(attribute instanceof DataAttribute)
			{
				if(typeDirectory==null)
				{
					final File directory = properties.getDatadirPath();
					typeDirectory = new File(directory, type.getID());
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
			if(attribute instanceof DataAttribute)
			{
				if(typeDirectory==null)
				{
					final File directory = properties.getDatadirPath();
					typeDirectory = new File(directory, type.getID());
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
			if(attribute instanceof DataAttribute)
			{
				if(typeDirectory==null)
				{
					final File directory = properties.getDatadirPath();
					typeDirectory = new File(directory, type.getID());
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
		final List types = typeList;
		for(ListIterator i = types.listIterator(types.size()); i.hasPrevious(); )
			((Type)i.previous()).onDropTable();

		database.dropDatabase();

		for(int i = 0; i<this.types.length; i++)
			dropDataDirectories(this.types[i]);
	}

	public void tearDownDatabase()
	{
		database.tearDownDatabase();

		for(int i = 0; i<this.types.length; i++)
			tearDownDataDirectories(this.types[i]);
	}
	
	public void close()
	{
		database.connectionPool.flush();
	}

	public Schema getVerifiedSchema()
	{
		// TODO: check data directories
		return database.makeVerifiedSchema();
	}

	/**
	 * Returns the item with the given ID.
	 * Always returns {@link Item#activeCopeItem() active} objects.
	 * @see Item#getCopeID()
	 * @throws NoSuchIDException if there is no item with the given id.
	 */
	public final Item findByID(final String id)
			throws NoSuchIDException
	{
		final int pos = id.lastIndexOf('.');
		if(pos<=0)
			throw new NoSuchIDException(id, true, "no dot in id");

		final String typeID = id.substring(0, pos);
		final Type type = findTypeByID(typeID);
		if(type==null)
			throw new NoSuchIDException(id, true, "no such type "+typeID);
		
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

		final int pk = type.getPrimaryKeyIterator().id2pk(idNumber);
		
		final Item result = type.getItemObject(pk);
		if ( ! result.existsCopeItem() )
		{
			throw new NoSuchIDException(id, false, "item <"+idNumber+"> does not exist");
		}
		return result;
	}
	
	public PoolCounter getConnectionPoolCounter()
	{
		return database.connectionPool.counter;
	}
	
	// ----------------------- transaction
	
	private final ThreadLocal transactionThreads = new ThreadLocal();
	
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
		if( hasCurrentTransaction() )
			throw new RuntimeException("there is already a transaction bound to current thread");
		final Transaction result = new Transaction(this, name);
		result.boundThread = Thread.currentThread();
		transactionThreads.set(result);
		return result;
	}
	
	public Transaction leaveTransaction()
	{
		Transaction tx = getCurrentTransaction();
		tx.boundThread = null;
		transactionThreads.set( null );
		return tx;
	}
	
	public void joinTransaction( Transaction tx )
	{
		if ( hasCurrentTransaction() )
			throw new RuntimeException("there is already a transaction bound to current thread");
		tx.boundThread = Thread.currentThread();
		transactionThreads.set(tx);		
	}
	
	public boolean hasCurrentTransaction()
	{
		boolean hasCurrentTransaction = transactionThreads.get()!=null;
		if ( hasCurrentTransaction )
		{
			getCurrentTransaction();
		}
		return hasCurrentTransaction;
	}

	/**
	 * Returns the transaction for this model,
	 * that is bound to the currently running thread.
	 * @see Thread#currentThread()
	 */
	public final Transaction getCurrentTransaction()
	{
		final Transaction result = (Transaction)transactionThreads.get();
		
		if(result==null)
			throw new RuntimeException("there is no cope transaction bound to this thread, see Model#startTransaction");
		
		if(result.boundThread!=Thread.currentThread())
			throw new RuntimeException();
		
		return result;
	}
	
	private final Transaction getOrNot()
	{
		final Transaction result = (Transaction)transactionThreads.get();
		
		if(result!=null && result.boundThread!=Thread.currentThread())
			throw new RuntimeException();

		return result;
	}
	
	private final void set(final Transaction transaction)
	{
		transactionThreads.set(transaction);
		
		if(transaction!=null)
			transaction.boundThread = Thread.currentThread();
	}
	
	final Transaction hop(final Transaction transaction)
	{
		final Transaction result = getCurrentTransaction();
		if(result==null)
			throw new RuntimeException();
		set(transaction);
		return result;
	}
	
	public void rollback()
	{
		getCurrentTransaction().rollback();
		set(null);
	}
	
	public void rollbackIfNotCommitted()
	{
		final Transaction t = getOrNot();
		if(t!=null)
		{
			t.rollback();
			set(null);
		}
	}
	
	public void commit()
	{
		getCurrentTransaction().commit();
		set(null);
	}

	/**
	 *	Returns true if the database supports READ_COMMITTED or any more strict transaction isolation level.
	 */
	boolean supportsReadCommited()
	{
		try
		{
			return getCurrentTransaction().getConnection().getMetaData().supportsTransactionIsolationLevel( 
				Connection.TRANSACTION_READ_COMMITTED 
			);
		}
		catch (SQLException e)
		{
			throw new NestingRuntimeException( e );
		}
	}
	
}
