/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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

import com.exedio.cope.Executor.ResultSetHandler;
import com.exedio.dsmf.Column;
import com.exedio.dsmf.SQLRuntimeException;
import com.exedio.dsmf.Schema;
import com.exedio.dsmf.Table;
import com.exedio.dsmf.UniqueConstraint;

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
	
	
	
	static final String TABLE_NAME = com.exedio.cope.Table.REVISION_TABLE_NAME;
	static final String UNIQUE_CONSTRAINT_NAME = com.exedio.cope.Table.REVISION_UNIQUE_CONSTRAINT_NAME;
	static final String COLUMN_NUMBER_NAME = "v";
	static final String COLUMN_INFO_NAME = "i";
	
	void makeSchema(final Schema result, final Dialect dialect)
	{
		final Table table = new com.exedio.dsmf.Table(result, TABLE_NAME);
		new Column(table, COLUMN_NUMBER_NAME, dialect.getIntegerType(RevisionInfoMutex.NUMBER, Integer.MAX_VALUE));
		new Column(table, COLUMN_INFO_NAME, dialect.getBlobType(100*1000));
		new UniqueConstraint(table, UNIQUE_CONSTRAINT_NAME, '(' + dialect.dsmfDialect.quoteName(COLUMN_NUMBER_NAME) + ')');
	}
	
	private int getActualRevisionNumber(final Connection connection, final Executor executor)
	{
		final com.exedio.dsmf.Dialect dsmfDialect = executor.dialect.dsmfDialect;
		
		final Statement bf = executor.newStatement();
		final String revision = dsmfDialect.quoteName(COLUMN_NUMBER_NAME);
		bf.append("select max(").
			append(revision).
			append(") from ").
			append(dsmfDialect.quoteName(TABLE_NAME)).
			append(" where ").
			append(revision).
			append(">=0");
			
		return executor.query(connection, bf, null, false, integerResultSetHandler);
	}
	
	Map<Integer, byte[]> getRevisionLogs(final ConnectionPool connectionPool, final Executor executor)
	{
		Connection con = null;
		try
		{
			con = connectionPool.get(true);
			return getRevisionLogs(con, executor);
		}
		finally
		{
			if(con!=null)
			{
				connectionPool.put(con);
				con = null;
			}
		}
	}
	
	private Map<Integer, byte[]> getRevisionLogs(final Connection connection, final Executor executor)
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
			append(dsmfDialect.quoteName(TABLE_NAME)).
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
	
	void inserCreate(final ConnectionPool connectionPool, final Executor executor, final Map<String, String> environment)
	{
		final int revisionNumber = getNumber();
		Connection con = null;
		try
		{
			con = connectionPool.get(true);
			new RevisionInfoCreate(revisionNumber, new Date(), environment).insert(con, executor);
		}
		finally
		{
			if(con!=null)
			{
				connectionPool.put(con);
				con = null;
			}
		}
	}
	
	void revise(final ConnectionPool connectionPool, final Executor executor, final Map<String, String> environment)
	{
		final int targetNumber = getNumber();
		
		assert targetNumber>=0 : targetNumber;

		Connection con = null;
		try
		{
			con = connectionPool.get(true);
			
			final int departureNumber = getActualRevisionNumber(con, executor);
			final List<Revision> revisionsToRun = getListToRun(departureNumber);
			
			if(!revisionsToRun.isEmpty())
			{
				final Date date = new Date();
				try
				{
					new RevisionInfoMutex(date, environment, targetNumber, departureNumber).insert(con, executor);
				}
				catch(SQLRuntimeException e)
				{
					throw new IllegalStateException(
							"Revision mutex set: " +
							"Either a revision is currently underway, " +
							"or a revision has failed unexpectedly.", e);
				}
				for(final Revision revision : revisionsToRun)
				{
					final int number = revision.number;
					final String[] body = revision.body;
					final RevisionInfoRevise.Body[] bodyInfo = new RevisionInfoRevise.Body[body.length];
					for(int bodyIndex = 0; bodyIndex<body.length; bodyIndex++)
					{
						final String sql = body[bodyIndex];
						if(Model.isLoggingEnabled())
							System.out.println("COPE revising " + number + ':' + sql);
						final Statement bf = executor.newStatement();
						bf.append(sql);
						final long start = System.currentTimeMillis();
						final int rows = executor.update(con, bf, false);
						final long elapsed = System.currentTimeMillis() - start;
						if(elapsed>1000)
							System.out.println(
									"Warning: slow cope revision " + number +
									" body " + bodyIndex + " takes " + elapsed + "ms: " + sql);
						bodyInfo[bodyIndex] = new RevisionInfoRevise.Body(sql, rows, elapsed);
					}
					final RevisionInfoRevise info = new RevisionInfoRevise(number, date, environment, revision.comment, bodyInfo);
					info.insert(con, executor);
				}
				{
					final com.exedio.dsmf.Dialect dsmfDialect = executor.dialect.dsmfDialect;
					final Statement bf = executor.newStatement();
					bf.append("delete from ").
						append(dsmfDialect.quoteName(TABLE_NAME)).
						append(" where ").
						append(dsmfDialect.quoteName(COLUMN_NUMBER_NAME)).
						append('=').
						appendParameter(RevisionInfoMutex.NUMBER);
					executor.update(con, bf, true);
				}
			}
		}
		finally
		{
			if(con!=null)
			{
				connectionPool.put(con);
				con = null;
			}
		}
	}
}
