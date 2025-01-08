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

import com.exedio.dsmf.Schema;
import com.exedio.dsmf.Sequence;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;
import java.sql.Connection;

final class SequenceImplSequence implements SequenceImpl
{
	private final Timer timer;
	private final Sequence.Type type;
	private final long start;
	private final Executor executor;
	private final ConnectionPool connectionPool;
	private final String name;
	private final String quotedName;

	SequenceImplSequence(
			final ModelMetrics metrics,
			final IntegerColumn column,
			final Sequence.Type type,
			final long start,
			final ConnectionPool connectionPool,
			final Database database,
			final String nameSuffix)
	{
		this.timer = timer(metrics);
		this.type = type;
		this.start = start;
		this.executor = database.executor;
		this.connectionPool = connectionPool;
		this.name = column.makeGlobalID(TrimClass.Data, nameSuffix);
		this.quotedName = database.dsmfDialect.quoteName(this.name);
	}

	SequenceImplSequence(
			final ModelMetrics metrics,
			final String name,
			final Sequence.Type type,
			final long start,
			final ConnectionPool connectionPool,
			final Executor executor,
			final com.exedio.dsmf.Dialect dsmfDialect)
	{
		this.timer = timer(metrics);
		this.type = type;
		this.start = start;
		this.executor = executor;
		this.connectionPool = connectionPool;
		this.name = name;
		this.quotedName = dsmfDialect.quoteName(this.name);
	}

	private static Timer timer(final ModelMetrics metrics)
	{
		return metrics.timer("fetch", "How long fetching a sequence takes in the database", Tags.empty());
	}

	@Override
	public void makeSchema(final Schema schema)
	{
		schema.newSequence(name, type, start);
	}

	@Override
	public long next()
	{
		final Connection connection = connectionPool.get(true);
		try
		{
			final Timer.Sample start = Timer.start();
			final long result = executor.dialect.nextSequence(executor, connection, quotedName);
			start.stop(timer);
			return result;
		}
		finally
		{
			connectionPool.put(connection);
		}
	}

	@Override
	public long getNext()
	{
		final Connection connection = connectionPool.get(true);
		try
		{
			return executor.dialect.getNextSequence(executor, connection, name);
		}
		finally
		{
			connectionPool.put(connection);
		}
	}

	@Override
	public void delete(final StringBuilder bf, final Dialect dialect)
	{
		dialect.deleteSequence(bf, quotedName, start);
	}

	@Override
	public void flush()
	{
		// empty
	}

	@Override
	public String getSchemaName()
	{
		return name;
	}
}
