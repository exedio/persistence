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

package com.exedio.cope;

import static com.exedio.cope.misc.TimeUtil.toMillies;
import static java.lang.System.nanoTime;
import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.util.concurrent.TimeUnit.NANOSECONDS;

import com.exedio.cope.misc.DatabaseListener;
import com.exedio.cope.util.JobContext;
import com.exedio.dsmf.SQLRuntimeException;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.EnumMap;
import java.util.HashMap;

final class Executor
{
	static final String NO_SUCH_ROW = "no such row";

	final Dialect dialect;
	final Marshallers marshallers;
	final boolean prepare;
	private final boolean supportsUniqueViolation;
	final boolean fulltextIndex;
	private final HashMap<String, UniqueConstraint> uniqueConstraints = new HashMap<>();
	private final EnumMap<Kind, Timer> timers;

	enum Kind
	{
		/**
		 * A select statement filtering for a primary key constraint.
		 * As an exception statements loading blobs are measured as {@link #loadBlob}.
		 */
		load,
		/**
		 * Any other select statement apart from {@link #load}, possibly retrieving many rows.
		 */
		query,
		/**
		 * Any statement loading blobs.
		 */
		loadBlob,
		/**
		 * An insert, update or delete statement.
		 */
		store,
		/**
		 * Auxiliary operations not part of normal operation,
		 * such as consistency checking and housekeeping.
		 * And fetching sequence numbers, as there are separate per-sequence timers for that.
		 * @see ItemFunction#checkTypeColumnL()
		 * @see DataField#checkVaultTrail()
		 * @see Type#checkCompletenessL(Type)
		 * @see Model#purgeSchema(JobContext)
		 * @see Dialect#nextSequence(Executor, Connection, String)
		 * @see Dialect#getNextSequence(Executor, Connection, String)
		 */
		other
	}

	volatile DatabaseListener listener = null;

	Executor(
			final ModelMetrics metrics,
			final Dialect dialect,
			final boolean supportsUniqueViolation,
			final ConnectProperties properties,
			final Marshallers marshallers)
	{
		this.dialect = dialect;
		this.marshallers = marshallers;
		this.prepare = !properties.isSupportDisabledForPreparedStatements();
		this.supportsUniqueViolation = supportsUniqueViolation;
		this.fulltextIndex = properties.getFulltextIndex();
		this.timers = new EnumMap<>(Kind.class);
		for(final var e : Kind.values())
			this.timers.put(e, metrics.timer("statement", "How long sql statements do take in the database", Tags.of("kind", e.name())));
	}

	void addUniqueConstraint(final String name, final UniqueConstraint uniqueConstraint)
	{
		if(uniqueConstraints.putIfAbsent(name, uniqueConstraint)!=null)
			throw new RuntimeException(name);
	}

	Statement newStatement()
	{
		return newStatement(true, Statement.Mode.NORMAL);
	}

	Statement newStatement(final boolean qualifyTable, final Statement.Mode mode)
	{
		return new Statement(this, qualifyTable, mode);
	}

	Statement newStatement(final Query<?> query, final boolean sqlOnly)
	{
		return new Statement(this, query, sqlOnly);
	}

	@FunctionalInterface
	interface ResultSetHandler<R>
	{
		R handle(ResultSet resultSet) throws SQLException;
	}

	static final ResultSetHandler<Integer> integerResultSetHandler = resultSet ->
		{
			if(!resultSet.next())
				throw new RuntimeException();

			return resultSet.getInt(1);
		};

	static final ResultSetHandler<Long> longResultSetHandler = resultSet ->
		{
			if(!resultSet.next())
				throw new RuntimeException();

			return resultSet.getLong(1);
		};

	<R> R query(
		final Connection connection,
		final Statement statement,
		final Kind kind,
		final ResultSetHandler<R> resultSetHandler)
	{
		final DatabaseListener listener = this.listener;
		final String sqlText = statement.getText();
		final long nanoStart = nanoTime();
		final long nanoPrepared;
		final long nanoExecuted;

		final R result;
		final long nanoResultRead;
		if(!prepare)
		{
			try(java.sql.Statement sqlStatement = connection.createStatement())
			{
				nanoPrepared = listener!=null ? nanoTime() : 0;
				try(ResultSet resultSet = sqlStatement.executeQuery(sqlText))
				{
					nanoExecuted = listener!=null ? nanoTime() : 0;
					result = resultSetHandler.handle(resultSet);
					nanoResultRead = listener!=null ? nanoTime() : 0;
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
				statement.setParameters(prepared);

				nanoPrepared = listener!=null ? nanoTime() : 0;
				try(ResultSet resultSet = prepared.executeQuery())
				{
					nanoExecuted = listener!=null ? nanoTime() : 0;
					result = resultSetHandler.handle(resultSet);
					nanoResultRead = listener!=null ? nanoTime() : 0;
				}
			}
			catch(final SQLException e)
			{
				throw new SQLRuntimeException(e, statement.toString());
			}
		}

		final long nanoEnd = nanoTime();

		timers.get(kind).record(nanoEnd - nanoStart, NANOSECONDS);
		if(listener!=null)
			listener.onStatement(
					statement.text.toString(),
					statement.getParameters(),
					toMillies(nanoPrepared, nanoStart),
					toMillies(nanoExecuted, nanoPrepared),
					toMillies(nanoResultRead, nanoExecuted),
					toMillies(nanoEnd, nanoResultRead));

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
			final Statement statement,
			final Kind kind)
	{
		final int rows = update(connection, exceptionItem, statement, kind);
		if(rows!=1)
			throw new TemporaryTransactionException(statement.toString(), rows);
	}

	int update(
			final Connection connection,
			final Item exceptionItem,
			final Statement statement,
			final Kind kind)
	{
		final DatabaseListener listener = this.listener;
		final long nanoStart = nanoTime();
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
				statement.setParameters(prepared);
				nanoPrepared = listener!=null ? nanoTime() : 0;
				rows = prepared.executeUpdate();
			}
			catch(final SQLException e)
			{
				throwViolation(e, exceptionItem);
				throw new SQLRuntimeException(e, statement.toString());
			}
		}

		final long nanoEnd = nanoTime();

		timers.get(kind).record(nanoEnd - nanoStart, NANOSECONDS);
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

	<R> R insertAndGetGeneratedKeysWithoutTimer(
			final Connection connection,
			final Statement statement,
			final ResultSetHandler<R> generatedKeysHandler)
	{
		final String sqlText = statement.getText();
		final DatabaseListener listener = this.listener;
		final long nanoStart = listener!=null ? nanoTime() : 0;

		final long nanoPrepared;
		final long nanoExecuted;
		final R result;
		if(!prepare)
		{
			try(java.sql.Statement sqlStatement = connection.createStatement())
			{
				nanoPrepared = listener!=null ? nanoTime() : 0;

				sqlStatement.executeUpdate(sqlText, RETURN_GENERATED_KEYS);
				nanoExecuted = listener!=null ? nanoTime() : 0;

				try(ResultSet generatedKeysResultSet = sqlStatement.getGeneratedKeys())
				{
					result = generatedKeysHandler.handle(generatedKeysResultSet);
				}
			}
			catch(final SQLException e)
			{
				throw new SQLRuntimeException(e, statement.toString());
			}
		}
		else
		{
			try(PreparedStatement prepared = connection.prepareStatement(sqlText, RETURN_GENERATED_KEYS))
			{
				statement.setParameters(prepared);
				nanoPrepared = listener!=null ? nanoTime() : 0;

				prepared.executeUpdate();
				nanoExecuted = listener!=null ? nanoTime() : 0;

				try(ResultSet generatedKeysResultSet = prepared.getGeneratedKeys())
				{
					result = generatedKeysHandler.handle(generatedKeysResultSet);
				}
			}
			catch(final SQLException e)
			{
				throw new SQLRuntimeException(e, statement.toString());
			}
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

	private void throwViolation(final SQLException sqlException, final Item item)
	{
		if(supportsUniqueViolation)
		{
			final String name = dialect.extractUniqueViolation(sqlException);
			if(name!=null)
			{
				final UniqueConstraint feature = uniqueConstraints.get(name);
				if(feature!=null)
					throw new UniqueViolationException(feature, item, sqlException);
			}
		}
	}

	// TestDatabaseListener ------------------

	private static final TestDatabaseListener noopTestListener = new TestDatabaseListener()
	{
		@Override
		public void load(final Connection connection, final Item item)
		{/* DOES NOTHING */}

		@Override
		public void search(final Connection connection, final Query<?> query, final Query.Mode mode)
		{/* DOES NOTHING */}
	};

	private volatile TestDatabaseListener testListener = noopTestListener;
	private final Object testListenerLock = new Object();

	TestDatabaseListener testListener()
	{
		return testListener;
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
