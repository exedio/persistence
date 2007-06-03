/*
 * Copyright (C) 2004-2007  exedio GmbH (www.exedio.com)
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

import gnu.trove.TIntHashSet;
import gnu.trove.TIntIterator;
import gnu.trove.TIntObjectHashMap;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.exedio.cope.util.ModificationListener;
import com.exedio.dsmf.SQLRuntimeException;


public final class Transaction
{
	final Model model;
	final Database database;
	final long id;
	final String name;
	final long startDate;
	
	/**
	 * index in array is {@link Type#idTransiently};
	 * value in array is a map, where the keys are {@link Item#pk item pks}
	 * and the values are {@link Entity}s
	 */
	private final TIntObjectHashMap<Entity>[] entityMaps;
	private final TIntHashSet[] invalidations;
	private Thread boundThread = null;
	ArrayList<QueryInfo> queryInfos = null;
	
	Transaction(final Model model, final int concreteTypeCount, final long id, final String name, final long startDate)
	{
		this.model = model;
		this.database = model.getDatabase();
		this.id = id;
		this.name = name;
		this.startDate = startDate;
		this.entityMaps = cast(new TIntObjectHashMap[concreteTypeCount]);
		this.invalidations = new TIntHashSet[concreteTypeCount];
	}
	
	@SuppressWarnings("unchecked") // OK: no generic array creation
	private static final <X> TIntObjectHashMap<X>[] cast(final TIntObjectHashMap[] o)
	{
		return o;
	}
	
	/**
	 * calling this method directly breaks Model.boundTransactions
	 */
	void bindToCurrentThread()
	{
		if ( closed )
		{
			throw new IllegalStateException("cannot bind to closed transaction");
		}
		if ( boundThread!=null && !boundThread.equals(Thread.currentThread()) )
		{
			throw new IllegalStateException("transaction already bound to other thread: " + boundThread.getId());
		}
		boundThread = Thread.currentThread();
	}
	
	boolean assertBoundToCurrentThread()
	{
		return Thread.currentThread().equals(boundThread);
	}
	
	/**
	 * calling this method directly breaks Model.boundTransactions
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
	private Pool<Connection> connectionPool = null;
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

		TIntObjectHashMap<Entity> entityMap = entityMaps[type.idTransiently];
		if(entityMap==null)
		{
			entityMap = new TIntObjectHashMap<Entity>();
			entityMaps[type.idTransiently] = entityMap;
		}

		Entity result = entityMap.get(pk);
		if(result==null)
		{
			final State state;
			if ( present )
			{
				if ( isInvalidated(item) )
				{
					state = new WrittenState(this.getConnection(), item);
				}
				else
				{
					state = model.getItemCache().getState(this, item);
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
		final TIntObjectHashMap<Entity> entityMap = entityMaps[item.type.idTransiently];
		if(entityMap!=null)
		{
			entityMap.remove( item.pk );
		}
	}
	
	ArrayList<Object> search(final Query<?> query, final boolean doCountOnly)
	{
		if(!model.getQueryCache().isEnabled() || isInvalidated(query))
		{
			return query.searchUncached(this, doCountOnly);
		}
		else
		{
			return model.getQueryCache().search(this, query, doCountOnly);
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
			final TIntHashSet invalidationsForType = invalidations[instanceType.idTransiently];
			if(invalidationsForType!=null && !invalidationsForType.isEmpty())
				return true;
		}
		
		return false;
	}
	
	private boolean isInvalidated( final Item item )
	{
		final TIntHashSet invalidationsForType = invalidations[item.type.idTransiently];
		return invalidationsForType!=null && invalidationsForType.contains(item.pk);
	}
	
	void addInvalidation(final Item item)
	{
		final int typeTransiently = item.type.idTransiently;
		TIntHashSet invalidationsForType = invalidations[typeTransiently];
		if ( invalidationsForType==null )
		{
			invalidationsForType = new TIntHashSet();
			invalidations[typeTransiently] = invalidationsForType;
		}
		invalidationsForType.add(item.pk);
	}

	Entity getEntityIfActive(final Type type, final int pk)
	{
		assert !closed : name;

		final TIntObjectHashMap<Entity> entityMap = entityMaps[type.idTransiently];
		if(entityMap==null)
			return null;
		return entityMap.get(pk);
	}
	
	Connection getConnection()
	{
		assert !closed : name;

		if(connection!=null)
			return connection;
		
		if(connectionPool!=null)
			throw new RuntimeException();

		connectionPool = database.connectionPool;
		final Connection connection = connectionPool.get();
		try
		{
			connection.setAutoCommit(false);
		}
		catch(SQLException e)
		{
			throw new SQLRuntimeException(e, "setAutoCommit");
		}
		this.connection = connection;
		
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
				connectionPool.put(connection);
				connection = null;
				connectionPool = null;
			}
			
			closed = true;
			unbindThread();
		}

		// notify global cache
		if(!rollback || !model.supportsReadCommitted() /* please send any complaints to derschuldige@hsqldb.org */)
		{
			model.getItemCache().invalidate(invalidations);
			model.getQueryCache().invalidate(invalidations);
		}

		// notify ModificationListeners
		if(!rollback)
		{
			final List<ModificationListener> commitListeners = model.getModificationListeners();
			if(!commitListeners.isEmpty())
			{
				ArrayList<Item> modifiedItems = null;
				
				for(int typeTransiently = 0; typeTransiently<invalidations.length; typeTransiently++)
				{
					final TIntHashSet invalidationSet = invalidations[typeTransiently];
					if(invalidationSet!=null)
					{
						if(modifiedItems==null)
							modifiedItems = new ArrayList<Item>();
						
						for(TIntIterator i = invalidationSet.iterator(); i.hasNext(); )
							modifiedItems.add(model.getConcreteType(typeTransiently).createItemObject(i.next()));
					}
				}
				
				if(modifiedItems!=null && !modifiedItems.isEmpty())
				{
					final List<Item> modifiedItemsUnmodifiable = Collections.unmodifiableList(modifiedItems);
					for(final ModificationListener listener : commitListeners)
						listener.onModifyingCommit(modifiedItemsUnmodifiable, name);
				}
			}
		}

		// cleanup
		// do this at the end, because there is no hurry with cleanup
		assert entityMaps.length==invalidations.length;
		for(int typeTransiently = 0; typeTransiently<invalidations.length; typeTransiently++)
		{
			final TIntObjectHashMap<Entity> entityMap = entityMaps[typeTransiently];
			if(entityMap!=null)
			{
				entityMap.clear();
				entityMaps[typeTransiently] = null;
			}
			
			final TIntHashSet invalidationSet = invalidations[typeTransiently];
			if(invalidationSet!=null)
			{
				invalidationSet.clear();
				invalidations[typeTransiently] = null;
			}
		}
	}

	public long getID()
	{
		return id;
	}
	
	public String getName()
	{
		return name;
	}
	
	public Date getStartDate()
	{
		return new Date(startDate);
	}
	
	public Thread getBoundThread()
	{
		return boundThread;
	}
	
	public void setQueryInfoEnabled(final boolean enabled)
	{
		this.queryInfos = enabled ? new ArrayList<QueryInfo>() : null;
	}
	
	public List<QueryInfo> getQueryInfos()
	{
		final ArrayList<QueryInfo> queryInfos = this.queryInfos;
		return queryInfos!=null ? Collections.unmodifiableList(queryInfos) : null;
	}
	
	@Override
	public String toString()
	{
		return "CT." + id + (closed?"(closed)":"");
	}
}
