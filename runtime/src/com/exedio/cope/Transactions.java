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

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tags;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

final class Transactions
{
	private static final Transaction[] EMPTY_TRANSACTION_ARRAY = new Transaction[0];

	private final HashSet<Transaction> open = new HashSet<>();

	@SuppressWarnings("ThreadLocalNotStaticFinal") // OK: class is instantiated on static context only
	private final ThreadLocal<Transaction> threadLocal = new ThreadLocal<>();

	void onModelNameSet(final Tags tags)
	{
		Gauge.
				builder(
						Transaction.class.getName() + ".open",
						open,
						// BEWARE:
						// Must not use Collections#synchronizedCollection because
						// it uses the synchronized wrapper as mutex, but the code
						// in this class uses "open" itself as mutex.
						(s) -> {
							//noinspection SynchronizationOnLocalVariableOrMethodParameter OK: parameter is actually field "open"
							synchronized(s) { return s.size(); } }).
				tags(tags).
				description("The number of open (currently running) transactions.").
				register(Metrics.globalRegistry);
	}

	void add(final Transaction tx)
	{
		setThreadLocal(tx);
		synchronized(open)
		{
			open.add(tx);
		}
	}

	Transaction leave()
	{
		final Transaction tx = current();
		tx.unbindThread();
		setThreadLocal(null);
		return tx;
	}

	void join(final Transaction tx)
	{
		if(hasCurrent())
			throw new RuntimeException("there is already a transaction bound to current thread");
		setThreadLocal(tx);
	}

	boolean hasCurrent()
	{
		return currentIfBound()!=null;
	}

	Transaction current()
	{
		final Transaction result = currentIfBound();
		if(result==null)
			throw new IllegalStateException("there is no cope transaction bound to this thread, see Model#startTransaction");
		return result;
	}

	Transaction currentIfBound()
	{
		final Transaction result = threadLocal.get();
		assert result==null || result.assertBoundToCurrentThread();
		return result;
	}

	private void setThreadLocal(final Transaction tx)
	{
		if(tx!=null)
		{
			tx.bindToCurrentThread();
			threadLocal.set(tx);
		}
		else
			threadLocal.remove();
	}

	void remove(final Transaction tx)
	{
		synchronized(open)
		{
			open.remove(tx);
		}
		setThreadLocal(null);
	}

	long getOldestCacheStamp(final CacheStamp cacheStamp)
	{
		// DO NOT USE Long.MAX_VALUE below, otherwise we might miss concurrent transactions
		long oldestStamp = cacheStamp.current();

		synchronized(open)
		{
			for(final Transaction tx : open)
			{
				final long currentStamp = tx.getCacheStampOrMax();
				if(oldestStamp>currentStamp)
					oldestStamp = currentStamp;
			}
		}
		return oldestStamp;
	}

	Collection<Transaction> getOpen()
	{
		final Transaction[] result;
		synchronized(open)
		{
			result = open.toArray(EMPTY_TRANSACTION_ARRAY);
		}
		return Collections.unmodifiableCollection(Arrays.asList(result));
	}

	/**
	 * otherwise mysql 5.5. may hang on dropping constraints
	 */
	void assertNoCurrentTransaction()
	{
		final Transaction tx = currentIfBound();
		if(tx!=null)
			throw new IllegalStateException("must not be called within a transaction: " + tx.getName());
	}
}
