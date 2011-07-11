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

import static com.exedio.cope.Revisions.logger;
import static com.exedio.cope.misc.TimeUtil.toMillies;
import static java.lang.System.nanoTime;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Level;

import com.exedio.dsmf.SQLRuntimeException;

public final class Revision
{
	final int number;
	final String comment;
	final String[] body;

	public Revision(final int number, final String comment, final String... body)
	{
		if(number<=0)
			throw new IllegalArgumentException("number must be greater zero");
		if(comment==null)
			throw new NullPointerException("comment");
		if(body==null)
			throw new NullPointerException("body");
		if(body.length==0)
			throw new IllegalArgumentException("body must not be empty");

		// make a copy to avoid modifications afterwards
		final String[] bodyCopy = new String[body.length];
		for(int i = 0; i<body.length; i++)
		{
			final String b = body[i];
			if(b==null)
				throw new NullPointerException("body" + '[' + i + ']');
			bodyCopy[i] = b;
		}

		this.number = number;
		this.comment = comment;
		this.body = bodyCopy;
	}

	public int getNumber()
	{
		return number;
	}

	public String getComment()
	{
		return comment;
	}

	public List<String> getBody()
	{
		return Collections.unmodifiableList(Arrays.asList(body));
	}

	@Override
	public String toString()
	{
		return String.valueOf('R') + number + ':' + comment;
	}

	RevisionInfoRevise execute(
			final Date date,
			final Map<String, String> environment,
			final ConnectionFactory connectionFactory)
	{
		final RevisionInfoRevise.Body[] bodyInfo = new RevisionInfoRevise.Body[body.length];
		// IMPORTANT
		// Do not use connection pool,
		// because connection state may have
		// been changed by the revision
		final Connection connection = connectionFactory.create();
		try
		{
			for(int bodyIndex = 0; bodyIndex<body.length; bodyIndex++)
			{
				final String sql = body[bodyIndex];
				if(logger.isInfoEnabled())
					logger.info(MessageFormat.format("revise {0}/{1}:{2}", number, bodyIndex, sql));
				final long start = nanoTime();
				final int rows = executeUpdate(connection, sql);
				final long elapsed = toMillies(nanoTime(), start);
				if(logger.isEnabledFor(Level.WARN) && elapsed>1000)
					logger.warn(MessageFormat.format("revise {0}/{1}:{2} is slow, takes {3}ms", number, bodyIndex, sql, elapsed));
				bodyInfo[bodyIndex] = new RevisionInfoRevise.Body(sql, rows, elapsed);
			}
		}
		finally
		{
			try
			{
				connection.close();
			}
			catch(final SQLException e)
			{
				logger.error( "close", e);
			}
		}
		return new RevisionInfoRevise(number, date, environment, comment, bodyInfo);
	}

	private static final int executeUpdate(
			final Connection connection,
			final String sql)
	{
		java.sql.Statement statement = null;
		try
		{
			statement = connection.createStatement();
			return statement.executeUpdate(sql);
		}
		catch(final SQLException e)
		{
			throw new SQLRuntimeException(e, sql);
		}
		finally
		{
			if(statement!=null)
			{
				try
				{
					statement.close();
				}
				catch(final SQLException e)
				{
					logger.error( "close", e);
				}
			}
		}
	}

	// ------------------- deprecated stuff -------------------

	/**
	 * @deprecated Use {@link RevisionInfo#parse(byte[])} instead
	 */
	@Deprecated
	public static final Properties parse(final byte[] info)
	{
		return RevisionInfo.parse(info);
	}

	/**
	 * @deprecated Use {@link #getNumber()} instead
	 */
	@Deprecated
	public int getVersion()
	{
		return getNumber();
	}

	/**
	 * @deprecated Use {@link #getNumber()} instead
	 */
	@Deprecated
	public int getRevision()
	{
		return getNumber();
	}
}
