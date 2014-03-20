/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
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

	final Dialect dialect;
	final ConnectionProvider connectionProvider;

	String error = null;
	Color particularColor = null;
	Color cumulativeColor = null;

	Node(final Dialect dialect, final ConnectionProvider connectionProvider)
	{
		if(dialect==null)
			throw new NullPointerException("dialect");
		if(connectionProvider==null)
			throw new NullPointerException("connectionProvider");

		this.dialect = dialect;
		this.connectionProvider = connectionProvider;
	}

	final String quoteName(final String name)
	{
		return dialect.quoteName(name);
	}

	static final String GET_TABLES = "getTables";
	static final String GET_COLUMNS = "getColumns";

	static interface ResultSetHandler
	{
		public void run(ResultSet resultSet) throws SQLException;
	}

	@SuppressFBWarnings({"ES_COMPARING_PARAMETER_STRING_WITH_EQ", "SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE"}) // Comparison of String parameter using == or !=
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
				resultSet = connection.getMetaData().getTables(null, dialect.schema, null, new String[]{"TABLE"});
			}
			else if(GET_COLUMNS==statement)
			{
				resultSet = connection.getMetaData().getColumns(null, dialect.schema, null, null);
			}
			else
			{
				sqlStatement = connection.createStatement();
				resultSet = sqlStatement.executeQuery(statement);
			}
			resultSetHandler.run(resultSet);
		}
		catch(final SQLException e)
		{
			throw new SQLRuntimeException(e, statement);
		}
		finally
		{
			if(sqlStatement!=null)
			{
				try
				{
					sqlStatement.close();
				}
				catch(final SQLException e)
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
				catch(final SQLException e)
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
				catch(final SQLException e)
				{
					// exception is already thrown
				}
			}
		}
	}

	@SuppressFBWarnings("SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE")
	final void executeSQL(final String statement, final StatementListener listener)
	{
		// NOTE:
		// Should be done without holding resources such as connection, statement etc.
		if(listener!=null && !listener.beforeExecute(statement))
			return;

		final int rows;
		Connection connection = null;
		try
		{
			connection = connectionProvider.getConnection();
			//System.out.println(statement);
			try(java.sql.Statement sqlStatement = connection.createStatement())
			{
				rows = sqlStatement.executeUpdate(statement);
				//System.out.println("  ("+rows+")");
			}
		}
		catch(final SQLException e)
		{
			//System.out.println("  -> "+e.getMessage());
			throw new SQLRuntimeException(e, statement);
		}
		finally
		{
			if(connection!=null)
			{
				try
				{
					connectionProvider.putConnection(connection);
				}
				catch(final SQLException e)
				{
					// exception is already thrown
				}
			}
		}
		// NOTE:
		// Should be done without holding resources such as connection, statement etc.
		if(listener!=null)
			listener.afterExecute(statement, rows);
	}

	final String getCatalog()
	{
		try
		{
			final Connection connection = connectionProvider.getConnection();
			try
			{
				return connection.getCatalog();
			}
			finally
			{
				connectionProvider.putConnection(connection);
			}
		}
		catch(final SQLException e)
		{
			throw new SQLRuntimeException(e, "getCatalog");
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

