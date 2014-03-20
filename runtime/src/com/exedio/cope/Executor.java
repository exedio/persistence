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

package com.exedio.cope;

import static com.exedio.cope.misc.TimeUtil.toMillies;
import static java.lang.System.nanoTime;
import static java.sql.Statement.RETURN_GENERATED_KEYS;

import com.exedio.cope.misc.DatabaseListener;
import com.exedio.dsmf.SQLRuntimeException;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;

@SuppressFBWarnings({"SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE", "SQL_PREPARED_STATEMENT_GENERATED_FROM_NONCONSTANT_STRING"})
final class Executor
{
	static final String NO_SUCH_ROW = "no such row";

	final Dialect dialect;
	final Marshallers marshallers;
	final boolean prepare;
	final boolean supportsUniqueViolation;
	final Dialect.LimitSupport limitSupport;
	final boolean fulltextIndex;
	private final HashMap<String, UniqueConstraint> uniqueConstraints = new HashMap<>();
	volatile DatabaseListener listener = null;

	Executor(
			final Dialect dialect,
			final ConnectProperties properties,
			final Marshallers marshallers)
	{
		this.dialect = dialect;
		this.marshallers = marshallers;
		this.prepare = !properties.isSupportDisabledForPreparedStatements();
		this.supportsUniqueViolation =
			!properties.isSupportDisabledForUniqueViolation() &&
			dialect.supportsUniqueViolation();
		this.limitSupport = dialect.getLimitSupport();
		this.fulltextIndex = properties.getFulltextIndex();

		if(limitSupport==null)
			throw new NullPointerException(dialect.toString());
	}

	void addUniqueConstraint(final String id, final UniqueConstraint uniqueConstraint)
	{
		if(uniqueConstraints.put(id, uniqueConstraint)!=null)
			throw new RuntimeException(id);
	}

	protected Statement newStatement()
	{
		return newStatement(true);
	}

	protected Statement newStatement(final boolean qualifyTable)
	{
		return new Statement(this, qualifyTable);
	}

	protected Statement newStatement(final Query<? extends Object> query, final boolean sqlOnly)
	{
		return new Statement(this, query, sqlOnly);
	}

	static interface ResultSetHandler<R>
	{
		public R handle(ResultSet resultSet) throws SQLException;
	}

	static final ResultSetHandler<Integer> integerResultSetHandler = new ResultSetHandler<Integer>()
	{
		public Integer handle(final ResultSet resultSet) throws SQLException
		{
			if(!resultSet.next())
				throw new RuntimeException();

			return resultSet.getInt(1);
		}
	};

	protected <R> R query(
		final Connection connection,
		final Statement statement,
		final QueryInfo queryInfo,
		final boolean explain,
		final ResultSetHandler<R> resultSetHandler)
	{
		final DatabaseListener listener = this.listener;
		final boolean takeTimes = !explain && (listener!=null || (queryInfo!=null));
		final String sqlText = statement.getText();
		final long nanoStart = takeTimes ? nanoTime() : 0;
		final long nanoPrepared;
		final long nanoExecuted;

		final R result;
		final long nanoResultRead;
		if(!prepare)
		{
			try(java.sql.Statement sqlStatement = connection.createStatement())
			{
				nanoPrepared = takeTimes ? nanoTime() : 0;
				try(ResultSet resultSet = sqlStatement.executeQuery(sqlText))
				{
					nanoExecuted = takeTimes ? nanoTime() : 0;
					result = resultSetHandler.handle(resultSet);
					nanoResultRead = takeTimes ? nanoTime() : 0;
				}
			}
			catch(final SQLException e)
			{
				throw new SQLRuntimeException(e, statement.toString());
			}
		}
		else
		{
			try(PreparedStatement prepared = connection.prepareStatement(sqlText))
			{
				int parameterIndex = 1;
				for(final Object p : statement.parameters)
					prepared.setObject(parameterIndex++, p);

				nanoPrepared = takeTimes ? nanoTime() : 0;
				try(ResultSet resultSet = prepared.executeQuery())
				{
					nanoExecuted = takeTimes ? nanoTime() : 0;
					result = resultSetHandler.handle(resultSet);
					nanoResultRead = takeTimes ? nanoTime() : 0;
				}
			}
			catch(final SQLException e)
			{
				throw new SQLRuntimeException(e, statement.toString());
			}
		}

		if(explain)
			return result;

		final long nanoEnd = takeTimes ? nanoTime() : 0;

		if(listener!=null)
			listener.onStatement(
					statement.text.toString(),
					statement.getParameters(),
					toMillies(nanoPrepared, nanoStart),
					toMillies(nanoExecuted, nanoPrepared),
					toMillies(nanoResultRead, nanoExecuted),
					toMillies(nanoEnd, nanoResultRead));

		if(queryInfo!=null)
			makeQueryInfo(queryInfo, statement, connection, nanoStart, nanoPrepared, nanoExecuted, nanoResultRead, nanoEnd);

		return result;
	}

	static <R> R query(
			final Connection connection,
			final String sql,
			final ResultSetHandler<R> resultSetHandler)
	{
		try(
			java.sql.Statement sqlStatement = connection.createStatement();
			ResultSet resultSet = sqlStatement.executeQuery(sql))
		{
			return resultSetHandler.handle(resultSet);
		}
		catch(final SQLException e)
		{
			throw new SQLRuntimeException(e, sql);
		}
	}

	void updateStrict(
			final Connection connection,
			final Item exceptionItem,
			final Statement statement)
		throws UniqueViolationException
	{
		final int rows = update(connection, exceptionItem, statement);
		if(rows!=1)
			throw new TemporaryTransactionException(statement.toString(), rows);
	}

	int update(
			final Connection connection,
			final Item exceptionItem,
			final Statement statement)
		throws UniqueViolationException
	{
		final DatabaseListener listener = this.listener;
		final long nanoStart = listener!=null ? nanoTime() : 0;
		final int rows;

		final long nanoPrepared;
		if(!prepare)
		{
			try(java.sql.Statement sqlStatement = connection.createStatement())
			{
				nanoPrepared = listener!=null ? nanoTime() : 0;
				rows = sqlStatement.executeUpdate(statement.getText());
			}
			catch(final SQLException e)
			{
				throwViolation(e, exceptionItem);
				throw new SQLRuntimeException(e, statement.toString());
			}
		}
		else
		{
			try(PreparedStatement prepared = connection.prepareStatement(statement.getText()))
			{
				int parameterIndex = 1;
				for(final Object p : statement.parameters)
					prepared.setObject(parameterIndex++, p);
				nanoPrepared = listener!=null ? nanoTime() : 0;
				rows = prepared.executeUpdate();
			}
			catch(final SQLException e)
			{
				throwViolation(e, exceptionItem);
				throw new SQLRuntimeException(e, statement.toString());
			}
		}

		final long nanoEnd = listener!=null ? nanoTime() : 0;

		if(listener!=null)
			listener.onStatement(
					statement.text.toString(),
					statement.getParameters(),
					toMillies(nanoPrepared, nanoStart),
					toMillies(nanoEnd, nanoPrepared),
					0,
					0);

		//System.out.println("("+rows+"): "+statement.getText());
		return rows;
	}

	static int update(
			final Connection connection,
			final String sql)
	{
		try(java.sql.Statement sqlStatement = connection.createStatement())
		{
			return sqlStatement.executeUpdate(sql);
		}
		catch(final SQLException e)
		{
			throw new SQLRuntimeException(e, sql);
		}
	}

	<R> R insertAndGetGeneratedKeys(
			final Connection connection,
			final Statement statement,
			final ResultSetHandler<R> generatedKeysHandler)
		throws UniqueViolationException
	{
		java.sql.Statement sqlStatement = null;
		try
		{
			final String sqlText = statement.getText();
			final DatabaseListener listener = this.listener;
			final long nanoStart = listener!=null ? nanoTime() : 0;

			final long nanoPrepared;
			if(!prepare)
			{
				sqlStatement = connection.createStatement();
				nanoPrepared = listener!=null ? nanoTime() : 0;
				sqlStatement.executeUpdate(sqlText, RETURN_GENERATED_KEYS);
			}
			else
			{
				final PreparedStatement prepared = connection.prepareStatement(sqlText, RETURN_GENERATED_KEYS);
				sqlStatement = prepared;
				int parameterIndex = 1;
				for(final Object p : statement.parameters)
					prepared.setObject(parameterIndex++, p);
				nanoPrepared = listener!=null ? nanoTime() : 0;
				prepared.executeUpdate();
			}
			final long nanoExecuted = listener!=null ? nanoTime() : 0;

			final R result;
			try(ResultSet generatedKeysResultSet = sqlStatement.getGeneratedKeys())
			{
				result = generatedKeysHandler.handle(generatedKeysResultSet);
			}

			final long nanoEnd = listener!=null ? nanoTime() : 0;

			if(listener!=null)
				listener.onStatement(
						sqlText,
						statement.getParameters(),
						toMillies(nanoPrepared, nanoStart),
						toMillies(nanoExecuted, nanoPrepared),
						toMillies(nanoEnd, nanoExecuted),
						0);

			return result;
		}
		catch(final SQLException e)
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
				catch(final SQLException e)
				{
					// exception is already thrown
				}
			}
		}
	}

	private void throwViolation(final SQLException sqlException, final Item item)
	{
		if(supportsUniqueViolation)
		{
			final String id = dialect.extractUniqueViolation(sqlException);
			if(id!=null)
			{
				final UniqueConstraint feature = uniqueConstraints.get(id);
				if(feature!=null)
					throw new UniqueViolationException(feature, item, sqlException);
			}
		}
	}

	private void makeQueryInfo(
			final QueryInfo queryInfo, final Statement statement, final Connection connection,
			final long start, final long prepared, final long executed, final long resultRead, final long end)
	{
		queryInfo.addChild(statement.getQueryInfo());

		{
			final QueryInfo timing = new QueryInfo("time elapsed " + numberFormat.format(end-start) + "ns");
			timing.addChild(new QueryInfo("prepare " + numberFormat.format(prepared-start)));
			timing.addChild(new QueryInfo("execute " + numberFormat.format(executed-prepared)));
			timing.addChild(new QueryInfo("result " + numberFormat.format(resultRead-executed)));
			timing.addChild(new QueryInfo("close " + numberFormat.format(end-resultRead)));
			queryInfo.addChild(timing);
		}

		final QueryInfo plan = dialect.explainExecutionPlan(statement, connection, this);
		if(plan!=null)
			queryInfo.addChild(plan);
	}

	private static DecimalFormat numberFormat;

	static
	{
		final DecimalFormatSymbols nfs = new DecimalFormatSymbols();
		nfs.setDecimalSeparator(',');
		nfs.setGroupingSeparator('\'');
		numberFormat = new DecimalFormat("", nfs);
	}

	static int convertSQLResult(final Object sqlInteger)
	{
		// IMPLEMENTATION NOTE
		// Whether the returned object is an Integer, a Long or a BigDecimal,
		// depends on the database used and for oracle on whether
		// OracleStatement.defineColumnType is used or not, so we support all
		// here.
		return ((Number)sqlInteger).intValue();
	}

	// TestDatabaseListener ------------------

	private static final TestDatabaseListener noopTestListener = new TestDatabaseListener()
	{
		public void load(final Connection connection, final Item item)
		{/* DOES NOTHING */}

		public void search(final Connection connection, final Query<?> query, final boolean totalOnly)
		{/* DOES NOTHING */}
	};

	private volatile TestDatabaseListener testListener = noopTestListener;
	private final Object testListenerLock = new Object();

	TestDatabaseListener testListener()
	{
		return this.testListener;
	}

	TestDatabaseListener setTestListener(TestDatabaseListener testListener)
	{
		if(testListener==null)
			testListener = noopTestListener;
		TestDatabaseListener result;

		synchronized(testListenerLock)
		{
			result = this.testListener;
			this.testListener = testListener;
		}

		if(result==noopTestListener)
			result = null;
		return result;
	}
}
