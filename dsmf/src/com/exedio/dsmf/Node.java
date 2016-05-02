/*
 * Copyright (C) 2004-2015  exedio GmbH (www.exedio.com)
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

import static java.util.Objects.requireNonNull;

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

		@Override
		public String toString()
		{
			return style;
		}
	}

	final Dialect dialect;
	final ConnectionProvider connectionProvider;

	private Result resultIfSet;

	Node(final Dialect dialect, final ConnectionProvider connectionProvider)
	{
		this.dialect = requireNonNull(dialect, "dialect");
		this.connectionProvider = requireNonNull(connectionProvider, "connectionProvider");
	}

	final String quoteName(final String name)
	{
		return dialect.quoteName(name);
	}

	static final String GET_TABLES = "getTables";
	static final String GET_COLUMNS = "getColumns";

	//@FunctionalInterface in JDK 1.8
	static interface ResultSetHandler
	{
		public void run(ResultSet resultSet) throws SQLException;
	}

	@SuppressFBWarnings({"ES_COMPARING_PARAMETER_STRING_WITH_EQ", "SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE"}) // Comparison of String parameter using == or !=
	final void querySQL(final String statement, final ResultSetHandler resultSetHandler)
	{
		Connection connection = null;
		try
		{
			connection = connectionProvider.getConnection();
			//System.err.println(statement);

			if(GET_TABLES==statement)
			{
				try(ResultSet resultSet = connection.getMetaData().
						getTables(null, dialect.schema, null, new String[]{"TABLE"}))
				{
					resultSetHandler.run(resultSet);
				}
			}
			else if(GET_COLUMNS==statement)
			{
				try(ResultSet resultSet = connection.getMetaData().
						getColumns(null, dialect.schema, null, null))
				{
					resultSetHandler.run(resultSet);
				}
			}
			else
			{
				try(
					java.sql.Statement sqlStatement = connection.createStatement();
					ResultSet resultSet = sqlStatement.executeQuery(statement))
				{
					resultSetHandler.run(resultSet);
				}
			}
		}
		catch(final SQLException e)
		{
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

	final void finish()
	{
		this.resultIfSet = requireNonNull(computeResult(), "computeResult");
	}

	abstract Result computeResult();

	private Result result()
	{
		final Result result = this.resultIfSet;
		if(result==null)
			throw new IllegalStateException("result");
		return result;
	}

	public final String getError()
	{
		return result().error;
	}

	public final Color getParticularColor()
	{
		return result().particularColor;
	}

	public final Color getCumulativeColor()
	{
		return result().cumulativeColor;
	}

	static final class Result
	{
		final String error;
		final Color particularColor;
		final Color cumulativeColor;

		Result(
				final String error,
				final Color particularColor,
				final Color cumulativeColor)
		{
			this.error = error;
			this.particularColor = particularColor;
			this.cumulativeColor = cumulativeColor;
		}

		Result(
				final String error,
				final Color color)
		{
			this.error = error;
			this.particularColor = color;
			this.cumulativeColor = color;
		}
	}
}

