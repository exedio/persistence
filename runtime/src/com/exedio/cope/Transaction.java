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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import bak.pcj.IntIterator;
import bak.pcj.map.IntKeyOpenHashMap;
import bak.pcj.set.IntOpenHashSet;

import com.exedio.cope.util.ModificationListener;
import com.exedio.dsmf.SQLRuntimeException;


public final class Transaction
{
	final Model model;
	final Database database;
	final String name;
	/**
	 *	index in array is {@link Type#transientNumber transient type number};
	 * value in array is a map, where the keys are {@link Item#pk item pks}
	 * and the values are {@link Entity}s
	 */
	final IntKeyOpenHashMap[] entityMaps;
	final IntOpenHashSet[] invalidations;
	private Thread boundThread = null;
	
	Transaction(final Model model, final String name)
	{
		this.model = model;
		this.database = model.getDatabase();
		this.name = name;
		this.entityMaps = new IntKeyOpenHashMap[model.concreteTypeCount];
		this.invalidations = new IntOpenHashSet[model.concreteTypeCount];
	}
	
	/**
	 * calling this method directly breaks model.transactionThreads
	 */
	void bindToCurrentThread()
	{
		if ( closed )
		{
			throw new IllegalStateException("cannot bind to closed transaction");
		}
		if ( boundThread!=null && !boundThread.equals(Thread.currentThread()) )
		{
			throw new IllegalStateException("transaction already bound to other thread");
		}
		boundThread = Thread.currentThread();
	}
	
	boolean assertBoundToCurrentThread()
	{
		return Thread.currentThread().equals(boundThread);
	}
	
	/**
	 * calling this method directly breaks model.transactionThreads
	 */
	void unbindThread()
	{
		if ( boundThread==null )
		{
			throw new RuntimeException( "transaction not bound to any thread" );
		}
		boundThread = null;
	}
	
	private Connection connection = null;
	private ConnectionPool connectionPool = null;
	private boolean closed = false;
	
	public boolean isClosed()
	{
		return closed;
	}
	
	Entity getEntity(final Item item, final boolean present)
	{
		assert !closed : name;
		
		final Type type = item.type;
		final int pk = item.pk;

		IntKeyOpenHashMap entityMap = entityMaps[type.transientNumber];
		if(entityMap==null)
		{
			entityMap = new IntKeyOpenHashMap();
			entityMaps[type.transientNumber] = entityMap;
		}

		Entity result = (Entity)entityMap.get(pk);
		if(result==null)
		{
			final State state;
			if ( present )
			{
				if ( isInvalidated(item) )
				{
					state = new PersistentState( this.getConnection(), item );
				}
				else
				{
					state = model.getCache().getPersistentState( this, item );
				}
			}
			else
			{
				state = new CreatedState( this, item );
			}
			result = new Entity(this, state);
			entityMap.put(pk, result);
			return result;
		}
		else
		{
			if ( !present )
			{
				throw new RuntimeException("tried to create entity that is already in cache: "+item.getCopeID());
			}
			return result;
		}
	}
	
	void removeEntity(final Item item)
	{
		final IntKeyOpenHashMap entityMap = entityMaps[item.type.transientNumber];
		if(entityMap!=null)
		{
			entityMap.remove( item.pk );
		}
	}
	
	<R> List<R> search(final Query<R> query)
	{
		if ( !model.getCache().supportsQueryCaching() || isInvalidated(query) )
		{
			return query.searchUncached();
		}
		else
		{
			return model.getCache().search( query );
		}
	}
	
	private boolean isInvalidated(final Query<?> query)
	{
		if ( isInvalidated(query.type) )
		{
			return true;
		}
		if ( query.joins==null )
		{
			return false;
		}
		for(final Join nextJoin : query.joins)
		{
			if ( isInvalidated(nextJoin.type) )
			{
				return true;
			}
		}
		return false;
	}
	
	private boolean isInvalidated(final Type<?> type)
	{
		if(type==null)
			throw new NullPointerException();
		
		for(final Type<?> instanceType : type.getTypesOfInstances())
		{
			final IntOpenHashSet invalidationsForType = invalidations[instanceType.transientNumber];
			if(invalidationsForType!=null && !invalidationsForType.isEmpty())
				return true;
		}
		
		return false;
	}
	
	private boolean isInvalidated( final Item item )
	{
		final IntOpenHashSet invalidationsForType = invalidations[ item.type.transientNumber ];
		return invalidationsForType!=null && invalidationsForType.contains(item.pk);
	}
	
	void addInvalidation(final Item item)
	{
		final int typeTransientNumber = item.type.transientNumber;
		IntOpenHashSet invalidationsForType = invalidations[typeTransientNumber];
		if ( invalidationsForType==null )
		{
			invalidationsForType = new IntOpenHashSet();
			invalidations[typeTransientNumber] = invalidationsForType;
		}
		invalidationsForType.add(item.pk);
	}

	Entity getEntityIfActive(final Type type, final int pk)
	{
		assert !closed : name;

		final IntKeyOpenHashMap entityMap = entityMaps[type.transientNumber];
		if(entityMap==null)
			return null;
		return (Entity)entityMap.get(pk);
	}
	
	Connection getConnection()
	{
		assert !closed : name;

		if(connection!=null)
			return connection;
		
		if(connectionPool!=null)
			throw new RuntimeException();

		connectionPool = database.getConnectionPool();
		try
		{
			connection = connectionPool.getConnection(false);
		}
		catch(SQLException e)
		{
			throw new SQLRuntimeException(e, "create connection");
		}
		
		return connection;
	}
	
	/**
	 * calling this method directly breaks model.openTransactions
	 */
	void commitOrRollback(final boolean rollback)
	{
		assert !closed : name;

		// notify database
		try
		{
			if(connection!=null)
			{
				try
				{
					if(rollback)
						connection.rollback();
					else
						connection.commit();
				}
				catch(SQLException e)
				{
					throw new SQLRuntimeException(e, rollback ? "rollback" : "commit");
				}
			}
		}
		finally
		{
			if(connection!=null)
			{
				try
				{
					connectionPool.putConnection(connection);
				}
				catch(SQLException e)
				{
					throw new SQLRuntimeException(e, "putConnection");
				}
				connection = null;
				connectionPool = null;
			}
			
			for(final IntKeyOpenHashMap entityMap : entityMaps)
				if(entityMap!=null)
					entityMap.clear();
			
			closed = true;
		}

		// notify global cache
		if(!rollback || !model.supportsReadCommitted() /* please send any complaints to derschuldige@hsqldb.org */)
		{
			model.getCache().invalidate(invalidations);
		}

		// notify ModificationListeners
		if(!rollback)
		{
			final List<ModificationListener> commitListeners = model.modificationListeners;
			if(!commitListeners.isEmpty())
			{
				ArrayList<Item> modifiedItems = null;
				
				for(int transientTypeNumber = 0; transientTypeNumber<invalidations.length; transientTypeNumber++)
				{
					final IntOpenHashSet invalidationSet = invalidations[transientTypeNumber];
					if(invalidationSet!=null)
					{
						if(modifiedItems==null)
							modifiedItems = new ArrayList<Item>();
						
						for(IntIterator i = invalidationSet.iterator(); i.hasNext(); )
							modifiedItems.add(model.getConcreteType(transientTypeNumber).createItemObject(i.next()));
					}
				}
				
				if(modifiedItems!=null && !modifiedItems.isEmpty())
				{
					final List<Item> modifiedItemsUnmodifiable = Collections.unmodifiableList(modifiedItems);
					// make a copy of commitListeners to avoid ConcurrentModificationViolations
					for(final ModificationListener listener : new ArrayList<ModificationListener>(commitListeners))
						listener.onModifyingCommit(modifiedItemsUnmodifiable);
				}
			}
		}

		// cleanup
		for(int transientTypeNumber = 0; transientTypeNumber<invalidations.length; transientTypeNumber++)
		{
			final IntOpenHashSet invalidationSet = invalidations[transientTypeNumber];
			if(invalidationSet!=null)
			{
				invalidationSet.clear();
				invalidations[transientTypeNumber] = null;
			}
		}
	}

	public String getName()
	{
		return name;
	}
	
	private String getID()
	{
		if ( name==null )
		{
			return String.valueOf( System.identityHashCode(this) );
		}
		else
		{
			return name;
		}
	}
	
	@Override
	public String toString()
	{
		return "CT." + getID() + (closed?"(closed)":"");
	}

}
