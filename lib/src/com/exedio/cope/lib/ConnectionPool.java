package com.exedio.cope.lib;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionPool
{
	private static ConnectionPool theInstance = null;

	static final ConnectionPool getInstance()
	{
		if (theInstance == null)
			theInstance = new ConnectionPool();

		return theInstance;
	}

	final Connection getConnection() throws SQLException
	{
		final Properties properties = Properties.getInstance();
		final String driver = properties.getDriver();
		final String url = properties.getUrl();
		final String user = properties.getUser();
		final String password = properties.getPassword();

		try
		{
			Class.forName(driver);
		}
		catch (ClassNotFoundException e)
		{
			throw new SystemException(e);
		}

		return DriverManager.getConnection(url, user, password);
	}

	final void putConnection(final Connection connection) throws SQLException
	{
		connection.close();
	}

}
