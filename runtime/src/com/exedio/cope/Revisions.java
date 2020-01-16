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

import static com.exedio.cope.Executor.integerResultSetHandler;
import static com.exedio.cope.util.Check.requireNonEmptyAndCopy;
import static com.exedio.cope.util.Check.requireNonNegative;

import com.exedio.dsmf.Column;
import com.exedio.dsmf.SQLRuntimeException;
import com.exedio.dsmf.Schema;
import com.exedio.dsmf.Table;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Revisions
{
	private static final Logger logger = LoggerFactory.getLogger(Revisions.class);

	private final int number;
	private final Revision[] revisions;

	public Revisions(final int number)
	{
		this.number = requireNonNegative(number, "number");
		this.revisions = EMPTY_REVISIONS;
	}

	private static final Revision[] EMPTY_REVISIONS = {};

	/**
	 * @param revisions See {@link #getList()} for further information.
	 */
	public Revisions(final Revision... revisions)
	{
		this.revisions = requireNonEmptyAndCopy(revisions, "revisions");
		this.number = revisions[0].number;

		for(int i = 1; i<revisions.length; i++)
		{
			final int expectedNumber = number-i;
			final int actualNumber = revisions[i].number;
			if(expectedNumber!=actualNumber)
				throw new IllegalArgumentException(
						"inconsistent revision number at index " + i + ", " +
						"expected " + expectedNumber + ", but was " + actualNumber);
		}
	}

	public int getNumber()
	{
		return number;
	}

	/**
	 * {@link Revision Revisions} listed here
	 * are guaranteed to be executed subsequently
	 * reversely to the order specified the list.
	 */
	public List<Revision> getList()
	{
		return Collections.unmodifiableList(Arrays.asList(revisions));
	}

	List<Revision> getListToRun(final int departureNumber)
	{
		if(departureNumber==number)
			return Collections.emptyList();
		if(departureNumber>number)
			throw new IllegalArgumentException(
					"cannot revise backwards, expected " + number +
					", but was " + departureNumber);

		final int startIndex = number - departureNumber - 1;
		if(startIndex>=revisions.length)
			throw new IllegalArgumentException(
					"attempt to revise from " + departureNumber + " to " + number +
					", but declared revisions allow from " + (number - revisions.length) + " only");

		final Revision[] result = new Revision[number - departureNumber];
		int resultIndex = 0;
		for(int i = startIndex; i>=0; i--)
			result[resultIndex++] = revisions[i];

		return Collections.unmodifiableList(Arrays.asList(result));
	}



	static final String COLUMN_NUMBER_NAME = "v";
	static final String COLUMN_INFO_NAME = "i";

	static void makeSchema(
			final Schema result,
			final ConnectProperties properties,
			final Dialect dialect)
	{
		final Table table = result.newTable(properties.revisionTableName);
		final Column pk = table.newColumn(COLUMN_NUMBER_NAME, dialect.getIntegerType(RevisionInfoMutex.NUMBER, Integer.MAX_VALUE));
		table.newColumn(COLUMN_INFO_NAME, dialect.getBlobType(100*1000));
		pk.newPrimaryKey(properties.revisionPrimaryKeyName);
	}

	private static int getActualNumber(
			final ConnectProperties properties,
			final ConnectionPool connectionPool,
			final Executor executor)
	{
		final com.exedio.dsmf.Dialect dsmfDialect = executor.dialect.dsmfDialect;

		final Statement bf = executor.newStatement();
		final String revision = dsmfDialect.quoteName(COLUMN_NUMBER_NAME);
		bf.append("SELECT MAX(").
			append(revision).
			append(") FROM ").
			append(dsmfDialect.quoteName(properties.revisionTableName)).
			append(" WHERE ").
			append(revision).
			append(">=0");

		final Connection connection = connectionPool.get(true);
		try
		{
			return executor.query(connection, bf, null, false, integerResultSetHandler);
		}
		finally
		{
			connectionPool.put(connection);
		}
	}

	@SuppressWarnings("StaticMethodOnlyUsedInOneClass")
	static Map<Integer, byte[]> getLogs(
			final boolean withMutex,
			final ConnectProperties properties,
			final ConnectionPool connectionPool,
			final Executor executor)
	{
		final Dialect dialect = executor.dialect;
		final com.exedio.dsmf.Dialect dsmfDialect = dialect.dsmfDialect;

		final Statement bf = executor.newStatement();
		final String revision = dsmfDialect.quoteName(COLUMN_NUMBER_NAME);
		bf.append("SELECT ").
			append(revision).
			append(',').
			append(dsmfDialect.quoteName(COLUMN_INFO_NAME)).
			append(" FROM ").
			append(dsmfDialect.quoteName(properties.revisionTableName));

		if(!withMutex)
			bf.append(" WHERE ").
				append(revision).
				append(">=0");

		final HashMap<Integer, byte[]> result = new HashMap<>();

		final Connection connection = connectionPool.get(true);
		try
		{
			executor.query(connection, bf, null, false, resultSet ->
				{
					while(resultSet.next())
					{
						final int currentRevision = resultSet.getInt(1);
						final byte[] info = resultSet.getBytes(2);
						final byte[] previous = result.putIfAbsent(currentRevision, info);
						if(previous!=null)
							throw new RuntimeException("duplicate revision " + currentRevision);
					}

					return null;
				}
			);
		}
		finally
		{
			connectionPool.put(connection);
		}
		return Collections.unmodifiableMap(result);
	}

	void insertCreate(
			final ConnectProperties properties,
			final ConnectionPool connectionPool,
			final Executor executor,
			final Map<String, String> environment)
	{
		new RevisionInfoCreate(getNumber(), new Date(), environment).
			insert(properties, connectionPool, executor);
	}

	void revise(
			final ConnectProperties properties,
			final ConnectionFactory connectionFactory,
			final ConnectionPool connectionPool,
			final Executor executor,
			final CopeProbe probe,
			final Dialect dialect,
			final boolean explicitRequest)
	{
		final int actualNumber = getActualNumber(properties, connectionPool, executor);
		final List<Revision> revisionsToRun = getListToRun(actualNumber);

		if(!revisionsToRun.isEmpty())
		{
			if ( !explicitRequest && !properties.autoReviseEnabled )
			{
				throw new IllegalStateException(
					"Model#reviseIfSupportedAndAutoEnabled called with auto-revising disabled and " +
					revisionsToRun.size()+" revisions pending " +
					"(last revision in DB: "+actualNumber+"; last revision in model: "+number+")"
				);
			}

			final String savepoint =
				properties.reviseSavepoint
				? getSavepoint(connectionPool, dialect)
				: "disabled by " + ConnectProperties.reviseSavepointKey;

			final Date date = new Date();
			final Map<String, String> environment = probe.getRevisionEnvironment();
			final RevisionInfoMutex mutex = new RevisionInfoMutex(savepoint, date, environment, getNumber(), actualNumber);
			try
			{
				mutex.insert(properties, connectionPool, executor);
			}
			catch(final SQLRuntimeException e)
			{
				throw new IllegalStateException(
						"Revision mutex set: " +
						"Either a revision is currently underway, " +
						"or a revision has failed unexpectedly.", e);
			}
			for(final Revision revision : revisionsToRun)
			{
				revision.execute(savepoint, date, environment, connectionFactory).
					insert(properties, connectionPool, executor);
			}
			RevisionInfoMutex.delete(properties, connectionPool, executor);
		}
	}

	private static String getSavepoint(final ConnectionPool connectionPool, final Dialect dialect)
	{
		final String result;
		try
		{
			result = dialect.getSchemaSavepoint(connectionPool);
		}
		catch(final SQLException e)
		{
			logger.error("savepoint", e);
			return "fails: " + e.getMessage();
		}
		logger.info("savepoint {}", result);
		return result;
	}

	@Override
	public String toString()
	{
		final int length = revisions.length;
		return
			"Revisions(" +
			(
				length>0
				? (String.valueOf(number) + '-' + revisions[length-1].number)
				: String.valueOf(number)
			) +
			')';
	}

	/**
	 * If you supply an instance of {@link Factory} to a {@link Model}
	 * via {@link ModelBuilder#add(Revisions.Factory)}
	 * the model takes care, that {@link #create(Context)}
	 * is called only while the model is connected and only once for each connect.
	 */
	@FunctionalInterface
	@SuppressWarnings("UnnecessarilyQualifiedInnerClassAccess") // otherwise javadoc issues warnings
	public interface Factory
	{
		Revisions create(Context ctx);

		final class Context
		{
			private final EnvironmentInfo environment;

			Context(final EnvironmentInfo environment)
			{
				this.environment = environment;
			}

			public EnvironmentInfo getEnvironment()
			{
				return environment;
			}
		}
	}
}
