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

package com.exedio.cope.misc;

import static com.exedio.cope.misc.Check.requireGreaterZero;
import static java.util.Objects.requireNonNull;

import com.exedio.cope.Condition;
import com.exedio.cope.Item;
import com.exedio.cope.Query;
import com.exedio.cope.This;
import com.exedio.cope.TransactionTry;
import com.exedio.cope.Type;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public final class QueryIterators
{
	public static <E extends Item> Iterator<E> iterateType(
			final Type<E> type,
			final Condition condition,
			final int slice)
	{
		return iterate(type, condition, false, slice);
	}

	/**
	 * Works as {@link #iterateType(Type, Condition, int)}
	 * but creates its own transaction whenever needed.
	 */
	public static <E extends Item> Iterator<E> iterateTypeTransactionally(
			final Type<E> type,
			final Condition condition,
			final int slice)
	{
		return iterate(type, condition, true, slice);
	}

	private static <E extends Item> Iterator<E> iterate(
			final Type<E> type,
			final Condition condition,
			final boolean transactionally,
			final int slice)
	{
		return new Iter<>(
				requireNonNull(type, "type"),
				condition,
				transactionally,
				requireGreaterZero(slice, "slice"));
	}

	private static final class Iter<E extends Item> implements Iterator<E>
	{
		private final This<E> typeThis;
		private final Condition condition;
		private final boolean transactionally;
		private final Query<E> query;

		private Iterator<E> iterator;
		private boolean limitExhausted;

		Iter(
				final Type<E> type,
				final Condition condition,
				final boolean transactionally,
				final int slice)
		{
			this.typeThis = type.getThis();
			this.condition = condition;
			this.transactionally = transactionally;

			this.query  = type.newQuery(condition);
			query.setOrderBy(typeThis, true);
			query.setLimit(0, slice);
			this.iterator = search();
		}

		public boolean hasNext()
		{
			return iterator.hasNext();
		}

		public E next()
		{
			final E result = iterator.next();
			if(result==null)
				throw new NullPointerException("does not support null in result");

			if(!iterator.hasNext())
			{
				if(limitExhausted)
				{
					final Condition c = typeThis.greater(result);
					query.setCondition(condition!=null ? condition.and(c) : c);
					this.iterator = search();
				}
				else
				{
					this.iterator = Collections.<E>emptyIterator();
				}
			}

			return result;
		}

		private Iterator<E> search()
		{
			final List<E> result;

			if(transactionally)
			{
				try(TransactionTry tx = typeThis.getType().getModel().startTransactionTry(query.toString()))
				{
					result = query.search();
					tx.commit();
				}
			}
			else
			{
				result = query.search();
			}

			limitExhausted = (result.size()==query.getLimit());

			return result.iterator();
		}

		public void remove()
		{
			throw new UnsupportedOperationException();
		}
	}


	private QueryIterators()
	{
		// prevent instantiation
	}
}
