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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

final class Transactions
{
	private final HashSet<Transaction> list = new HashSet<Transaction>();
	private final ThreadLocal<Transaction> boundTransactions = new ThreadLocal<Transaction>();

	void add(final Transaction result)
	{
		setTransaction( result );
		synchronized(list)
		{
			list.add(result);
		}
	}

	Transaction leave()
	{
		final Transaction tx = current();
		tx.unbindThread();
		setTransaction( null );
		return tx;
	}

	void join(final Transaction tx)
	{
		if(hasCurrent())
			throw new RuntimeException("there is already a transaction bound to current thread");
		setTransaction(tx);
	}

	boolean hasCurrent()
	{
		return currentIfBound()!=null;
	}

	/**
	 * Returns the transaction for this model,
	 * that is bound to the currently running thread.
	 * @throws IllegalStateException if there is no cope transaction bound to current thread
	 * @see Thread#currentThread()
	 */
	Transaction current()
	{
		final Transaction result = currentIfBound();
		if(result==null)
			throw new IllegalStateException("there is no cope transaction bound to this thread, see Model#startTransaction");
		assert result.assertBoundToCurrentThread();
		return result;
	}

	Transaction currentIfBound()
	{
		final Transaction result = boundTransactions.get();
		assert result==null || result.assertBoundToCurrentThread();
		return result;
	}

	private void setTransaction(final Transaction transaction)
	{
		if(transaction!=null)
		{
			transaction.bindToCurrentThread();
			boundTransactions.set(transaction);
		}
		else
			boundTransactions.remove();
	}

	Transaction remove()
	{
		final Transaction tx = current();

		synchronized(list)
		{
			list.remove(tx);
		}
		setTransaction(null);

		return tx;
	}

	long getOldestConnectionNanos()
	{
			long oldestNanos = Long.MAX_VALUE;
			synchronized(list)
			{
				for(final Transaction tx : list)
				{
					final long currentNanos = tx.getConnectionNanosOrMax();
					if(oldestNanos>currentNanos)
						oldestNanos = currentNanos;
				}
			}
			return oldestNanos;
	}

	Collection<Transaction> getList()
	{
		final Transaction[] result;
		synchronized(list)
		{
			result = list.toArray(new Transaction[list.size()]);
		}
		return Collections.unmodifiableCollection(Arrays.asList(result));
	}
}
