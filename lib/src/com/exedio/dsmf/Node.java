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
package com.exedio.dsmf;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.exedio.cope.NestingRuntimeException;

public abstract class Node
{
	static final int COLOR_NOT_YET_CALC = 0;
	public static final int COLOR_OK = 1;
	public static final int COLOR_WARNING = 2;
	public static final int COLOR_ERROR = 3;
	
	final Driver driver;
	final ConnectionProvider connectionProvider;

	String error = null;
	int particularColor = Schema.COLOR_NOT_YET_CALC;
	int cumulativeColor = Schema.COLOR_NOT_YET_CALC;
	
	Node(final Driver driver, final ConnectionProvider connectionProvider)
	{
		if(driver==null)
			throw new NullPointerException("driver");
		if(connectionProvider==null)
			throw new NullPointerException("connectionProvider");
		
		this.driver = driver;
		this.connectionProvider = connectionProvider;
	}

	final String protectName(final String name)
	{
		return driver.protectName(name);
	}
	
	static final String GET_TABLES = "getTables";
	static final String GET_COLUMNS = "getColumns";

	static interface ResultSetHandler
	{
		public void run(ResultSet resultSet) throws SQLException;
	}

	final void querySQL(final String statement, final ResultSetHandler resultSetHandler)
	{
		Connection connection = null;
		ResultSet resultSet = null;
		java.sql.Statement sqlStatement = null;
		try
		{
			connection = connectionProvider.getConnection();
			//System.err.println(statement);

			if(GET_TABLES.equals(statement))
			{
				resultSet = connection.getMetaData().getTables(null, driver.schema, null, new String[]{"TABLE"});
			}
			else if(GET_COLUMNS.equals(statement))
			{
				resultSet = connection.getMetaData().getColumns(null, driver.schema, null, null);
			}
			else
			{
				sqlStatement = connection.createStatement();
				resultSet = sqlStatement.executeQuery(statement);
			}
			resultSetHandler.run(resultSet);
		}
		catch(SQLException e)
		{
			throw new NestingRuntimeException(e, statement.toString());
		}
		finally
		{
			if(sqlStatement!=null)
			{
				try
				{
					sqlStatement.close();
				}
				catch(SQLException e)
				{
					// exception is already thrown
				}
			}
			if(resultSet!=null)
			{
				try
				{
					resultSet.close();
				}
				catch(SQLException e)
				{
					// exception is already thrown
				}
			}
			if(connection!=null)
			{
				try
				{
					connectionProvider.putConnection(connection);
				}
				catch(SQLException e)
				{
					// exception is already thrown
				}
			}
		}
	}
	
	final void executeSQL(final String statement)
	{
		Connection connection = null;
		java.sql.Statement sqlStatement = null;
		try
		{
			connection = connectionProvider.getConnection();
			//System.err.println(statement);
			sqlStatement = connection.createStatement();
			final int rows = sqlStatement.executeUpdate(statement);

			//System.out.println("("+rows+"): "+statement);
			if(rows!=0)
				throw new RuntimeException("expected no rows, but got "+rows+" on statement "+statement);
		}
		catch(SQLException e)
		{
			throw new NestingRuntimeException(e, statement.toString());
		}
		finally
		{
			if(sqlStatement!=null)
			{
				try
				{
					sqlStatement.close();
				}
				catch(SQLException e)
				{
					// exception is already thrown
				}
			}
			if(connection!=null)
			{
				try
				{
					connectionProvider.putConnection(connection);
				}
				catch(SQLException e)
				{
					// exception is already thrown
				}
			}
		}
	}
	
	abstract void finish();

	public final String getError()
	{
		if(particularColor==Schema.COLOR_NOT_YET_CALC)
			throw new RuntimeException();

		return error;
	}

	public final int getParticularColor()
	{
		if(particularColor==Schema.COLOR_NOT_YET_CALC)
			throw new RuntimeException();

		return particularColor;
	}

	public final int getCumulativeColor()
	{
		if(cumulativeColor==Schema.COLOR_NOT_YET_CALC)
			throw new RuntimeException();

		return cumulativeColor;
	}
}

