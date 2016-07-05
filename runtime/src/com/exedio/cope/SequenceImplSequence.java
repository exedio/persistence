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
import java.sql.Connection;

final class SequenceImplSequence implements SequenceImpl
{
	private final Sequence.Type type;
	private final long start;
	private final Executor executor;
	private final ConnectionPool connectionPool;
	private final String name;
	private final String quotedName;

	SequenceImplSequence(
			final IntegerColumn column,
			final Sequence.Type type,
			final long start,
			final ConnectionPool connectionPool,
			final Database database,
			final String nameSuffix)
	{
		this.type = type;
		this.start = start;
		this.executor = database.executor;
		this.connectionPool = connectionPool;
		this.name = database.properties.filterTableName(column.makeGlobalID(TrimClass.Other, "Seq"+nameSuffix));
		this.quotedName = database.dsmfDialect.quoteName(this.name);
	}

	SequenceImplSequence(
			final String name,
			final Sequence.Type type,
			final long start,
			final ConnectProperties properties,
			final ConnectionPool connectionPool,
			final Executor executor,
			final com.exedio.dsmf.Dialect dsmfDialect)
	{
		this.type = type;
		this.start = start;
		this.executor = executor;
		this.connectionPool = connectionPool;
		this.name = properties.filterTableName(name);
		this.quotedName = dsmfDialect.quoteName(this.name);
	}

	@Override
	public void makeSchema(final Schema schema)
	{
		new Sequence(schema, name, type, start);
	}

	@Override
	public long next()
	{
		final Connection connection = connectionPool.get(true);
		try
		{
			return executor.dialect.nextSequence(executor, connection, quotedName).longValue();
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
			return executor.dialect.getNextSequence(executor, connection, name).longValue();
		}
		finally
		{
			connectionPool.put(connection);
		}
	}

	@Override
	public void delete(final StringBuilder bf, final Dialect dialect)
	{
		dialect.deleteSequence(bf, quotedName, type, start);
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
