
package com.exedio.cope.lib;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

class ConnectionPool
{
	
	private Connection[] pool = new Connection[10];
	private int size = 0;
	private Object lock = new Object();

	final Connection getConnection() throws SQLException
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
				return createConnection();
			}
		}
	}

	final Connection createConnection() throws SQLException
	{
		final Properties properties = Properties.getInstance();
		final String driver = properties.getDatabaseDriver();
		final String url = properties.getDatabaseUrl();
		final String user = properties.getDatabaseUser();
		final String password = properties.getDatabasePassword();

		try
		{
			Class.forName(driver);
		}
		catch (ClassNotFoundException e)
		{
			throw new SystemException(e);
		}

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
