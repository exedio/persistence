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

import static com.exedio.cope.Executor.integerResultSetHandler;

import com.exedio.cope.Executor.ResultSetHandler;
import com.exedio.dsmf.Column;
import com.exedio.dsmf.PrimaryKeyConstraint;
import com.exedio.dsmf.SQLRuntimeException;
import com.exedio.dsmf.Schema;
import com.exedio.dsmf.Table;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Revisions
{
	private final int number;
	private final Revision[] revisions;

	public Revisions(final int number)
	{
		if(number<0)
			throw new IllegalArgumentException("revision number must not be negative, but was " + number);

		this.number = number;
		this.revisions = new Revision[0];
	}

	/**
	 * @param revisions See {@link #getList()} for further information.
	 */
	public Revisions(final Revision... revisions)
	{
		if(revisions==null)
			throw new NullPointerException("revisions");
		if(revisions.length==0)
			throw new IllegalArgumentException("revisions must not be empty");

		// make a copy to avoid modifications afterwards
		final Revision[] revisionsCopy = new Revision[revisions.length];

		int base = -1;
		for(int i = 0; i<revisions.length; i++)
		{
			final Revision revision = revisions[i];
			if(revision==null)
				throw new NullPointerException("revisions" + '[' + i + ']');

			if(i==0)
				base = revision.number;
			else
			{
				final int expectedNumber = base-i;
				if(revision.number!=expectedNumber)
					throw new IllegalArgumentException("inconsistent revision number at index " + i + ", expected " + expectedNumber + ", but was " + revision.number);
			}

			revisionsCopy[i] = revision;
		}

		this.number = revisions[0].number;
		this.revisions = revisionsCopy;
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
		final Table table = new com.exedio.dsmf.Table(result, properties.revisionTableName);
		new Column(table, COLUMN_NUMBER_NAME, dialect.getIntegerType(RevisionInfoMutex.NUMBER, Integer.MAX_VALUE));
		new Column(table, COLUMN_INFO_NAME, dialect.getBlobType(100*1000));
		new PrimaryKeyConstraint(table, properties.revisionPrimaryKeyName, COLUMN_NUMBER_NAME);
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
			executor.query(connection, bf, null, false, new ResultSetHandler<Void>()
			{
				public Void handle(final ResultSet resultSet) throws SQLException
				{
					while(resultSet.next())
					{
						final int revision = resultSet.getInt(1);
						final byte[] info = dialect.getBytes(resultSet, 2);
						final byte[] previous = result.put(revision, info);
						if(previous!=null)
							throw new RuntimeException("duplicate revision " + revision);
					}

					return null;
				}
			});
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
			final DialectParameters dialectParameters,
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
			final Date date = new Date();
			final Map<String, String> environment = dialectParameters.getRevisionEnvironment();
			final RevisionInfoMutex mutex = new RevisionInfoMutex(date, environment, getNumber(), actualNumber);
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
				revision.execute(date, environment, connectionFactory).
					insert(properties, connectionPool, executor);
			}
			RevisionInfoMutex.delete(properties, connectionPool, executor);
		}
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
	 * via {@link Model#Model(Revisions.Factory, Type...)} etc.
	 * the model takes care, that {@link #create(Context)}
	 * is called only while the model is connected and only once for each connect.
	 */
	public static interface Factory
	{
		Revisions create(Context ctx);

		public static final class Context
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
