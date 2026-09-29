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

import static java.util.Objects.requireNonNull;

import com.exedio.cope.ConnectProperties.TrimClass;
import com.exedio.dsmf.Schema;
import com.exedio.dsmf.Sequence;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;
import java.sql.Connection;
import java.util.Arrays;

final class SequenceImplSequence implements SequenceImpl
{
	private final Timer timer;
	private final Sequence.Type type;
	private final long start;
	private final Executor executor;
	private final ConnectionPool connectionPool;
	private final String name;
	private final String nameQuoted;
	private final Prefetch prefetch;

	SequenceImplSequence(
			final ModelMetrics metrics,
			final IntegerColumn column,
			final Sequence.Type type,
			final long start,
			final ConnectionPool connectionPool,
			final Database database,
			final String nameSuffix,
			final int maximumFetchCount)
	{
		this(metrics, column.makeGlobalName(TrimClass.legacy, nameSuffix), type, start, connectionPool,
				database.executor, database.dsmfDialect,
				maximumFetchCount==1 ?
						null :
						new Prefetch(maximumFetchCount, counter(metrics)));
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
		this(metrics, name, type, start, connectionPool, executor, dsmfDialect, null);
	}

	private SequenceImplSequence(
			final ModelMetrics metrics,
			final String name,
			final Sequence.Type type,
			final long start,
			final ConnectionPool connectionPool,
			final Executor executor,
			final com.exedio.dsmf.Dialect dsmfDialect,
			final Prefetch prefetch)
	{
		this.timer = timer(metrics);
		this.type = type;
		this.start = start;
		this.executor = executor;
		this.connectionPool = connectionPool;
		this.name = name;
		this.nameQuoted = dsmfDialect.quoteName(this.name);
		this.prefetch = prefetch;
	}

	private static Timer timer(final ModelMetrics metrics)
	{
		return metrics.timer("fetch", "How long fetching a batch of sequences takes in the database", Tags.empty());
	}

	private static Counter counter(final ModelMetrics metrics)
	{
		return metrics.counter("count", "How many sequence values have been fetched from the database", Tags.empty());
	}

	@Override
	public void makeSchema(final Schema schema)
	{
		schema.newSequence(name, type, start);
	}

	@Override
	public long next()
	{
		if (prefetch==null)
		{
			final Connection connection = connectionPool.get(true);
			try
			{
				return nextInternal(connection, 1)[0];
			}
			finally
			{
				connectionPool.put(connection);
			}
		}
		else
		{
			synchronized(prefetch)
			{
				final int callRate = prefetch.recordCallAndGetRate();
				if(!prefetch.hasPrefetchedValue())
				{
					final Connection connection = connectionPool.get(true);
					try
					{
						final int count = prefetch.limitFetchCount(callRate);
						final long[] result = nextInternal(connection, count);
						prefetch.incrementCounter(count);
						prefetch.replacePrefetchedValues(result);
					}
					finally
					{
						connectionPool.put(connection);
					}
				}
				return prefetch.consumePrefetchedValue();
			}
		}
	}

	private long[] nextInternal(final Connection connection, final int count)
	{
		final Timer.Sample start = Timer.start();
		final long[] result = executor.dialect.nextSequence(executor, connection, nameQuoted, count);
		start.stop(timer);
		if(result.length != count)
			throw new RuntimeException("requested " + count + ", got " + Arrays.toString(result));
		return result;
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
	public void delete(final StringBuilder sb, final Dialect dialect)
	{
		dialect.deleteSequence(sb, nameQuoted, start);
	}

	@Override
	public void flush()
	{
		if (prefetch!=null)
		{
			synchronized(prefetch)
			{
				prefetch.flush();
			}
		}
	}

	@Override
	public String schemaName()
	{
		return name;
	}

	@Override
	public String schemaNameQuoted()
	{
		return nameQuoted;
	}

	static final class Prefetch
	{
		private static final long[] EMPTY_LONG_ARRAY = new long[0];

		private final int maximumFetchCount;
		private final Counter counter;
		private CallRateTracker nextCallRate = new CallRateTracker();

		private long[] prefetchedValues;
		private int prefetchedUsed;

		Prefetch(final int maximumFetchCount,
					final Counter counter)
		{
			if(maximumFetchCount < 2)
				throw new RuntimeException(String.valueOf(maximumFetchCount));
			requireNonNull(counter);
			this.maximumFetchCount = maximumFetchCount;
			this.counter = counter;
			prefetchedValues = EMPTY_LONG_ARRAY;
			prefetchedUsed = 0;
		}

		boolean hasPrefetchedValue()
		{
			return prefetchedUsed<prefetchedValues.length;
		}

		long consumePrefetchedValue()
		{
			return prefetchedValues[prefetchedUsed++];
		}

		void replacePrefetchedValues(final long[] prefetchedValues)
		{
			prefetchedUsed = 0;
			//noinspection AssignmentOrReturnOfFieldWithMutableType only called from outer class and unit test
			this.prefetchedValues = prefetchedValues;
		}

		void flush()
		{
			prefetchedValues = EMPTY_LONG_ARRAY;
			prefetchedUsed = 0;
			nextCallRate = new CallRateTracker();
		}

		int limitFetchCount(final int callRate)
		{
			return Math.min(maximumFetchCount, callRate);
		}

		void incrementCounter(final int count)
		{
			counter.increment(count);
		}

		int recordCallAndGetRate()
		{
			return nextCallRate.recordCallAndGetRate();
		}
	}
}
