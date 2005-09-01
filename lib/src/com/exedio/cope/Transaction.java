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

import bak.pcj.set.IntOpenHashSet;
import bak.pcj.set.IntSet;
import java.sql.Connection;
import java.sql.SQLException;

import bak.pcj.map.IntKeyOpenHashMap;

import com.exedio.dsmf.SQLRuntimeException;


public final class Transaction
{
	final Model model;
	final Database database;
	final String name;
	private Thread boundThread = null;
	
	Transaction(final Model model, final String name)
	{
		this.model = model;
		this.database = model.getDatabase();
		this.name = name;
		rowMaps = new IntKeyOpenHashMap[model.numberOfTypes];
		invalidations = new IntSet[model.numberOfTypes];
	}
	
	void bindToCurrentThread()
	{
		if ( boundThread!=null && !boundThread.equals(Thread.currentThread()) )
		{
			throw new RuntimeException("transaction already bound to other thread");
		}
		boundThread = Thread.currentThread();
	}
	
	void assertBoundToCurrentThread()
	{
		if ( ! Thread.currentThread().equals(boundThread) )
		{
			throw new RuntimeException();
		}
	}
	
	void unbindThread()
	{
		if ( boundThread==null )
		{
			throw new RuntimeException( "transaction not bound to any thread" );
		}
		boundThread = null;
	}
	
	/**
	 *	index in array is {@link Type#transientNumber transient type number};
	 * value in array is a map, where the keys are {@link Item#pk item pks} 
	 * and the values are {@link Entity}s
	 *	TODO rename to entityMaps
	 */
	final IntKeyOpenHashMap[] rowMaps;
	private Connection connection = null;
	private ConnectionPool connectionPool = null;
	private boolean closed = false;
	final IntSet[] invalidations;
	
	public boolean isClosed()
	{
		return closed;
	}
	
	final Entity getEntity(final Item item, final boolean present)
	{
		if(closed)
			throw new RuntimeException();
		
		final Type type = item.type;
		final int pk = item.pk;

		IntKeyOpenHashMap rowMap = rowMaps[type.transientNumber];
		if(rowMap==null)
		{
			rowMap = new IntKeyOpenHashMap();
			rowMaps[type.transientNumber] = rowMap;
		}

		Entity result = (Entity)rowMap.get(pk);
		if(result==null)
		{
			final State state;
			if ( present )
			{
				state = new PersistentState( this.getConnection(), item );
			}
			else
			{
				state = new CreatedState( this, item );
			}
			result = new Entity(this, state);
			rowMap.put(pk, result);
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
	
	final void removeEntity(final Item item)
	{
		IntKeyOpenHashMap rowMap = rowMaps[item.type.transientNumber];
		if(rowMap!=null)
		{
			rowMap.remove( item.pk );
		}		
	}
	
	void addInvalidation(final Type type, final int pk)
	{
		IntSet invalidationsForType = invalidations[ type.transientNumber ];
		if ( invalidationsForType==null )
		{
			invalidationsForType = new IntOpenHashSet();
			invalidations[ type.transientNumber ] = invalidationsForType;
		}
		invalidationsForType.add( pk );
	}

	final Entity getEntityIfActive(final Type type, final int pk)
	{
		if(closed)
			throw new RuntimeException();

		final IntKeyOpenHashMap rowMap = rowMaps[type.transientNumber];
		if(rowMap==null)
			return null;
		return (Entity)rowMap.get(pk);
	}
	
	Connection getConnection()
	{
		if(closed)
			throw new RuntimeException();

		if(connection!=null)
			return connection;
		
		if(connectionPool!=null)
			throw new RuntimeException();

		connectionPool = database.connectionPool;
		try
		{
			connection = connectionPool.getConnection();
			connection.setAutoCommit(false);
		}
		catch(SQLException e)
		{
			throw new SQLRuntimeException(e, "create connection");
		}
		
		return connection;
	}
	
	void commit()
	{
		close(false);
	}

	void rollback()
	{
		close(true);
	}
	
	private void close(final boolean rollback)
	{
		if(closed)
			throw new RuntimeException();

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
			for(int i = 0; i<rowMaps.length; i++)
				if(rowMaps[i]!=null)
					rowMaps[i].clear();
			closed = true;
		}
	}
	
	public String getName()
	{
		return name;
	}
	
	public String toString()
	{
		if(name==null)
			return "CT." + System.identityHashCode(this);
		else
			return "CT." + name;
	}

}
