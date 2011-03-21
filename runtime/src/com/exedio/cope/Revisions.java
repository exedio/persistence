/*
 * Copyright (C) 2004-2011  exedio GmbH (www.exedio.com)
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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.exedio.cope.Executor.ResultSetHandler;
import com.exedio.dsmf.Column;
import com.exedio.dsmf.SQLRuntimeException;
import com.exedio.dsmf.Schema;
import com.exedio.dsmf.Table;
import com.exedio.dsmf.UniqueConstraint;

/**
 * NOTE:
 *
 * The statements listed in {@link Revision#getBody()}
 * are guaranteed to be executed subsequently
 * in the order specified by the list
 * by one single {@link java.sql.Connection connection}.
 * So you may use connection states within a revision.
 *
 * Additionally,
 * {@link Revision revisions} listed in {@link #getList()}
 * are guaranteed to be executed subsequently
 * reversely to the order specified the list,
 * each revision by a newly created {@link java.sql.Connection connection}.
 * The connection is not used for any other purpose afterwards.
 * So you cannot use connection states between revisions,
 * but you also don't have to cleanup connection state at the end of each revision.
 * This is for minimizing effects between revisions.
 */
public final class Revisions
{
	static final Logger logger = Logger.getLogger(Revisions.class.getName());

	private final int number;
	private final Revision[] revisions;

	public Revisions(final int number)
	{
		if(number<0)
			throw new IllegalArgumentException("revision number must not be negative, but was " + number);

		this.number = number;
		this.revisions = new Revision[0];
	}

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

	public List<Revision> getList()
	{
		return Collections.unmodifiableList(Arrays.asList(revisions));
	}

	List<Revision> getListToRun(final int departureNumber)
	{
		if(departureNumber==number)
			return Collections.emptyList();
		if(departureNumber>number)
			throw new IllegalArgumentException("cannot revise backwards, expected " + number + ", but was " + departureNumber);

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

	void makeSchema(
			final Schema result,
			final ConnectProperties properties,
			final Dialect dialect)
	{
		final Table table = new com.exedio.dsmf.Table(result, properties.revisionTableName.stringValue());
		new Column(table, COLUMN_NUMBER_NAME, dialect.getIntegerType(RevisionInfoMutex.NUMBER, Integer.MAX_VALUE));
		new Column(table, COLUMN_INFO_NAME, dialect.getBlobType(100*1000));
		new UniqueConstraint(table, properties.revisionUniqueName.stringValue(), '(' + dialect.dsmfDialect.quoteName(COLUMN_NUMBER_NAME) + ')');
	}

	private int getActualNumber(
			final ConnectProperties properties,
			final ConnectionPool connectionPool,
			final Executor executor)
	{
		final com.exedio.dsmf.Dialect dsmfDialect = executor.dialect.dsmfDialect;

		final Statement bf = executor.newStatement();
		final String revision = dsmfDialect.quoteName(COLUMN_NUMBER_NAME);
		bf.append("select max(").
			append(revision).
			append(") from ").
			append(dsmfDialect.quoteName(properties.revisionTableName.stringValue())).
			append(" where ").
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

	Map<Integer, byte[]> getLogs(
			final ConnectProperties properties,
			final ConnectionPool connectionPool,
			final Executor executor)
	{
		final Connection connection = connectionPool.get(true);
		try
		{
			return getLogs(properties, connection, executor);
		}
		finally
		{
			connectionPool.put(connection);
		}
	}

	private Map<Integer, byte[]> getLogs(
			final ConnectProperties properties,
			final Connection connection,
			final Executor executor)
	{
		final Dialect dialect = executor.dialect;
		final com.exedio.dsmf.Dialect dsmfDialect = dialect.dsmfDialect;

		final Statement bf = executor.newStatement();
		final String revision = dsmfDialect.quoteName(COLUMN_NUMBER_NAME);
		bf.append("select ").
			append(revision).
			append(',').
			append(dsmfDialect.quoteName(COLUMN_INFO_NAME)).
			append(" from ").
			append(dsmfDialect.quoteName(properties.revisionTableName.stringValue())).
			append(" where ").
			append(revision).
			append(">=0");

		final HashMap<Integer, byte[]> result = new HashMap<Integer, byte[]>();

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
		return Collections.unmodifiableMap(result);
	}

	void insertCreate(
			final ConnectProperties properties,
			final ConnectionPool connectionPool,
			final Executor executor,
			final Map<String, String> environment)
	{
		new RevisionInfoCreate(getNumber(), new Date(), environment).insert(properties, connectionPool, executor);
	}

	void revise(
			final ConnectProperties properties,
			final ConnectionPool connectionPool,
			final Executor executor,
			final Map<String, String> environment)
	{
		final int actualNumber = getActualNumber(properties, connectionPool, executor);
		final List<Revision> revisionsToRun = getListToRun(actualNumber);

		if(!revisionsToRun.isEmpty())
			revise(properties, connectionPool, executor, environment, revisionsToRun, actualNumber);
	}

	private void revise(
			final ConnectProperties properties,
			final ConnectionPool connectionPool,
			final Executor executor,
			final Map<String, String> environment,
			final List<Revision> revisionsToRun,
			final int actualNumber)
	{
		final Date date = new Date();
		try
		{
			new RevisionInfoMutex(date, environment, getNumber(), actualNumber).insert(properties, connectionPool, executor);
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
			final RevisionInfoRevise.Body[] bodyInfo = revision.execute(connectionPool, executor);
			new RevisionInfoRevise(revision.number, date, environment, revision.comment, bodyInfo).
				insert(properties, connectionPool, executor);
		}
		{
			final com.exedio.dsmf.Dialect dsmfDialect = executor.dialect.dsmfDialect;
			final Statement bf = executor.newStatement();
			bf.append("delete from ").
				append(dsmfDialect.quoteName(properties.revisionTableName.stringValue())).
				append(" where ").
				append(dsmfDialect.quoteName(COLUMN_NUMBER_NAME)).
				append('=').
				appendParameter(RevisionInfoMutex.NUMBER);

			final Connection connection = connectionPool.get(true);
			try
			{
				executor.updateStrict(connection, null, bf);
			}
			finally
			{
				connectionPool.put(connection);
			}
		}
	}
}
