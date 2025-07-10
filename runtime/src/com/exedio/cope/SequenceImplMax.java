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
import java.sql.Connection;

final class SequenceImplMax implements SequenceImpl
{
	private final IntegerColumn column;
	private final long start;
	private final ConnectionPool connectionPool;

	private boolean computed = false;
	private long next = Long.MIN_VALUE;
	private final Object lock = new Object();

	SequenceImplMax(final IntegerColumn column, final long start, final ConnectionPool connectionPool)
	{
		this.column = column;
		this.start = start;
		this.connectionPool = connectionPool;
	}

	@Override
	public void makeSchema(final Schema schema)
	{
		// empty
	}

	@Override
	public long next()
	{
		synchronized(lock)
		{
			final long result;
			if(computed)
			{
				result = next;
			}
			else
			{
				final Long current = current();
				result = current!=null ? (current + 1) : start;
				computed = true;
			}

			next = result + 1;
			return result;
		}
	}

	@Override
	public long getNext()
	{
		synchronized(lock)
		{
			final long result;
			if(computed)
			{
				result = next;
			}
			else
			{
				final Long current = current();
				result = current!=null ? (current + 1) : start;
			}

			return result;
		}
	}

	private Long current()
	{
		final Connection connection = connectionPool.get(true);
		try
		{
			return column.max(connection, column.table.database.executor);
		}
		finally
		{
			connectionPool.put(connection);
		}
	}

	@Override
	public void delete(final StringBuilder sb, final Dialect dialect)
	{
		flush();
	}

	@Override
	public void flush()
	{
		synchronized(lock)
		{
			computed = false;
		}
	}

	@Override
	public String getSchemaName()
	{
		return null;
	}
}
