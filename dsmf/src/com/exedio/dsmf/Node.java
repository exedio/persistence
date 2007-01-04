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

package com.exedio.dsmf;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class Node
{
	public static enum Color
	{
		OK("ok"),
		WARNING("warning"),
		ERROR("error");
		
		private final String style;
		
		Color(final String style)
		{
			this.style = style;
		}
		
		Color max(final Color other)
		{
			return Color.class.getEnumConstants()[Math.max(ordinal(), other.ordinal())];
		}
		
		Color min(final Color other)
		{
			return Color.class.getEnumConstants()[Math.min(ordinal(), other.ordinal())];
		}
		
		@Override
		public String toString()
		{
			return style;
		}
	}
	
	final Driver driver;
	final ConnectionProvider connectionProvider;

	String error = null;
	Color particularColor = null;
	Color cumulativeColor = null;
	
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

			if(GET_TABLES==statement)
			{
				resultSet = connection.getMetaData().getTables(null, driver.schema, null, new String[]{"TABLE"});
			}
			else if(GET_COLUMNS==statement)
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
			throw new SQLRuntimeException(e, statement.toString());
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
	
	final void executeSQL(final String statement, final StatementListener listener)
	{
		Connection connection = null;
		java.sql.Statement sqlStatement = null;
		final boolean logAllStatements = false;
		try
		{
			connection = connectionProvider.getConnection();
			if ( logAllStatements ) System.out.println(statement);
			sqlStatement = connection.createStatement();
			if(listener!=null)
				listener.beforeExecute(statement);
			final int rows = sqlStatement.executeUpdate(statement);
			if(listener!=null)
				listener.afterExecute(statement, rows);
			if ( logAllStatements ) System.out.println("  ("+rows+")");
		}
		catch(SQLException e)
		{
			if ( logAllStatements ) System.out.println("  -> "+e.getMessage());
			throw new SQLRuntimeException(e, statement.toString());
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
		assert particularColor!=null;
		assert cumulativeColor!=null;

		return error;
	}

	public final Color getParticularColor()
	{
		assert particularColor!=null;
		assert cumulativeColor!=null;

		return particularColor;
	}

	public final Color getCumulativeColor()
	{
		assert particularColor!=null;
		assert cumulativeColor!=null;

		return cumulativeColor;
	}
}

