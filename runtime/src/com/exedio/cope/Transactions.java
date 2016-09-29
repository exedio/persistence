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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

final class Transactions
{
	private final HashSet<Transaction> open = new HashSet<>();
	private final ThreadLocal<Transaction> threadLocal = new ThreadLocal<>();

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

	Transaction remove(final Transaction tx)
	{
		synchronized(open)
		{
			open.remove(tx);
		}
		setThreadLocal(null);

		return tx;
	}

	long getOldestCacheStamp()
	{
		long oldestStamp = Long.MAX_VALUE;
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
			result = open.toArray(new Transaction[open.size()]);
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
