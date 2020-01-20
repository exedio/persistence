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
import static com.exedio.cope.util.Check.requireGreaterZero;
import static com.exedio.cope.util.Check.requireNonEmpty;
import static com.exedio.cope.util.Check.requireNonEmptyAndCopy;
import static java.lang.System.nanoTime;

import com.exedio.dsmf.SQLRuntimeException;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Revision
{
	@SuppressWarnings("LoggerInitializedWithForeignClass")
	private static final Logger logger = LoggerFactory.getLogger(Revisions.class);

	final int number;
	final String comment;
	final String[] body;

	/**
	 * @param body See {@link #getBody()} for further information.
	 */
	public Revision(final int number, final String comment, final String... body)
	{
		this.number = requireGreaterZero(number, "number");
		this.comment = requireNonEmpty(comment, "comment");
		this.body = requireNonEmptyAndCopy(body, "body");
	}

	public int getNumber()
	{
		return number;
	}

	public String getComment()
	{
		return comment;
	}

	/**
	 * The statements listed here
	 * are guaranteed to be executed subsequently
	 * in the order specified by the list
	 * by one single {@link java.sql.Connection connection}.
	 * So you may use connection states within a revision.
	 * <p>
	 * For each revision a new {@link java.sql.Connection connection} is created.
	 * That connection is not used for any other purpose afterwards
	 * so you don't have to cleanup connection state at the end of each revision.
	 * This is for minimizing effects between revisions.
	 */
	public List<String> getBody()
	{
		return Collections.unmodifiableList(Arrays.asList(body));
	}

	@Override
	public String toString()
	{
		return String.valueOf('R') + number + ':' + comment;
	}

	@SuppressFBWarnings("SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE")
	RevisionInfoRevise execute(
			final String savepoint,
			final Date date,
			final Map<String, String> environment,
			final ConnectionFactory connectionFactory)
	{
		final RevisionInfoRevise.Body[] bodyInfo = new RevisionInfoRevise.Body[body.length];
		// IMPORTANT
		// Do not use connection pool,
		// because connection state may have
		// been changed by the revision
		try(Connection connection = connectionFactory.create())
		{
			for(int bodyIndex = 0; bodyIndex<body.length; bodyIndex++)
			{
				final String sql = body[bodyIndex];
				if(logger.isInfoEnabled())
					logger.info(MessageFormat.format("revise {0}/{1}:{2}", number, bodyIndex, sql));
				final long start = nanoTime();
				final int rows = executeUpdate(connection, sql);
				final long elapsed = toMillies(nanoTime(), start);
				bodyInfo[bodyIndex] = new RevisionInfoRevise.Body(sql, rows, elapsed);
			}
		}
		catch(final SQLException e)
		{
			throw new SQLRuntimeException(e, "close");
		}
		return new RevisionInfoRevise(number, savepoint, date, environment, comment, bodyInfo);
	}

	@SuppressFBWarnings("SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE")
	private static int executeUpdate(
			final Connection connection,
			final String sql)
	{
		try(java.sql.Statement statement = connection.createStatement())
		{
			return statement.executeUpdate(sql);
		}
		catch(final SQLException e)
		{
			throw new SQLRuntimeException(e, sql);
		}
	}
}
