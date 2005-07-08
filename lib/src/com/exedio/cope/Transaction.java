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
import java.util.HashMap;

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
	
	final HashMap rows = new HashMap();
	private Connection connection = null;
	private ConnectionPool connectionPool = null;
	private boolean closed = false;
	
	final Row getRow(final Item item, final boolean present)
	{
		if(closed)
			throw new RuntimeException();

		Row result = (Row)rows.get(item);
		if(result!=null)
			return result;
		else
		{
			result = new Row(this, item, present);
			if(present)
				database.load(result);
			rows.put(item, result);
			return result;
		}
	}

	final Row getRowIfActive(final Item item)
	{
		if(closed)
			throw new RuntimeException();

		return (Row)rows.get(item);
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
		close("commit");
	}

	private void rollbackInternal()
	{
		close("rollback");
	}
	
	private void close(final String command)
	{
		if(closed)
			throw new RuntimeException();

		try
		{
			if(connection!=null)
			{
				final Statement bf = database.createStatement();
				bf.append(command);
				try
				{
					database.executeSQLUpdate(bf, 0);
				}
				catch(ConstraintViolationException e)
				{
					throw new NestingRuntimeException(e);
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
			rows.clear();
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
