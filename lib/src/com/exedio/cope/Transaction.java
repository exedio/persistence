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

import java.sql.Connection;
import java.sql.SQLException;

import bak.pcj.map.IntKeyOpenHashMap;

import com.exedio.dsmf.SQLRuntimeException;


public class Transaction
{
	final Model model;
	final Database database;
	final String name;
	
	Transaction(final Model model, final String name)
	{
		this.model = model;
		this.database = model.getDatabase();
		this.name = name;
		threadLocal.set(this);
		boundThread = Thread.currentThread();
		rowMaps = new IntKeyOpenHashMap[model.numberOfTypes];
	}
	
	public static void rollback()
	{
		get().rollbackInternal();
		set(null);
	}
	
	public static void rollbackIfNotCommitted()
	{
		final Transaction t = getOrNot();
		if(t!=null)
		{
			t.rollbackInternal();
			set(null);
		}
	}
	
	public static void commit()
	{
		get().commitInternal();
		set(null);
	}
	
	// ----------------------- thread local
	
	private Thread boundThread = null;
	private static final ThreadLocal threadLocal = new ThreadLocal();
	
	static final Transaction get()
	{
		final Transaction result = (Transaction)threadLocal.get();
		
		if(result==null)
			throw new RuntimeException("there is no cope transaction bound to this thread, see Model#startTransaction");
		
		if(result.boundThread!=Thread.currentThread())
			throw new RuntimeException();
		
		return result;
	}
	
	private static final Transaction getOrNot()
	{
		final Transaction result = (Transaction)threadLocal.get();
		
		if(result!=null && result.boundThread!=Thread.currentThread())
			throw new RuntimeException();

		return result;
	}
	
	private static final void set(final Transaction transaction)
	{
		threadLocal.set(transaction);
		
		if(transaction!=null)
			transaction.boundThread = Thread.currentThread();
	}
	
	static final Transaction hop(final Transaction transaction)
	{
		final Transaction result = get();
		if(result==null)
			throw new RuntimeException();
		set(transaction);
		return result;
	}
	
	// ----------------------- the transaction itself
	
	final IntKeyOpenHashMap[] rowMaps;
	private Connection connection = null;
	private ConnectionPool connectionPool = null;
	private boolean closed = false;
	
	final Row getRow(final Item item, final boolean present)
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
			final Row result = new Row(this, item, present);
			if(present)
				database.load(result);
			rowMap.put(pk, result);
			return result;
		}
		else
		{
			Row result = (Row)rowMap.get(pk);
			if(result!=null)
				return result;
			else
			{
				result = new Row(this, item, present);
				if(present)
					database.load(result);
				rowMap.put(pk, result);
				return result;
			}
		}
	}

	final Row getRowIfActive(final Item item)
	{
		if(closed)
			throw new RuntimeException();

		final Type type = item.type;
		final int pk = item.pk;

		final IntKeyOpenHashMap rowMap = rowMaps[type.transientNumber];
		if(rowMap==null)
			return null;
		return (Row)rowMap.get(pk);
	}

	Connection getConnection() throws SQLException
	{
		if(closed)
			throw new RuntimeException();

		if(connection!=null)
			return connection;
		
		if(connectionPool!=null)
			throw new RuntimeException();

		connectionPool = database.connectionPool;
		connection = connectionPool.getConnection();
		connection.setAutoCommit(false);
		
		return connection;
	}
	
	private void commitInternal()
	{
		close(false);
	}

	private void rollbackInternal()
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
	
	public String toString()
	{
		if(name==null)
			return "CT." + System.identityHashCode(this);
		else
			return "CT." + name;
	}

}
