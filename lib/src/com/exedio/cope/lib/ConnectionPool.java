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

package com.exedio.cope.lib;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

class ConnectionPool
{
	
	private final Connection[] pool = new Connection[10];
	private int size = 0;
	private final Object lock = new Object();
	private final Properties properties;

	ConnectionPool(final Properties properties)
	{
		this.properties = properties;
	}

	final Connection getConnection(final Database database) throws SQLException
	{
		synchronized(lock)
		{
			if(size>0)
			{
				//System.out.println("connection pool: fetch "+(size-1));
				return pool[--size];
			}
			else
			{
				return createConnection(database);
			}
		}
	}

	final Connection createConnection(final Database database) throws SQLException
	{
		final String url = properties.getDatabaseUrl();
		final String user = properties.getDatabaseUser();
		final String password = properties.getDatabasePassword();

		//System.out.println("connection pool: CREATE");
		return DriverManager.getConnection(url, user, password);
	}

	final void putConnection(final Connection connection) throws SQLException
	{
		synchronized(lock)
		{
			if(size<pool.length)
			{
				//System.out.println("connection pool: store "+size);
				pool[size++] = connection;
				return;
			}
		}
		
		//System.out.println("connection pool: CLOSE ");

		// Important to do this outside the synchronized block!
		connection.close();
	}

}
