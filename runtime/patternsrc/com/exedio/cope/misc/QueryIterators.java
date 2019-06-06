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

import static com.exedio.cope.util.Check.requireGreaterZero;
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
import java.util.NoSuchElementException;

public final class QueryIterators
{
	public static <E extends Item> Iterator<E> iterateType(
			final Type<E> type,
			final Condition condition,
			final int slice)
	{
		return iterateType(type, condition, false, slice);
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
		return iterateType(type, condition, true, slice);
	}

	private static <E extends Item> Iterator<E> iterateType(
			final Type<E> type,
			final Condition condition,
			final boolean transactionally,
			final int slice)
	{
		return new ByType<>(
				requireNonNull(type, "type"),
				condition,
				transactionally,
				requireGreaterZero(slice, "slice"));
	}

	private static final class ByType<E extends Item> implements Iterator<E>
	{
		private final This<E> typeThis;
		private final Condition condition;
		private final boolean transactionally;
		private final Query<E> query;

		private Iterator<E> iterator;
		private boolean limitExhausted;

		ByType(
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
			query.setPage(0, slice);
			this.iterator = search();
		}

		@Override
		public boolean hasNext()
		{
			return iterator.hasNext();
		}

		@Override
		public E next()
		{
			final E result = iterator.next();
			if(result==null)
				throw new NullPointerException(
						"does not support null in result" + query); // TODO test

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
					this.iterator = Collections.emptyIterator();
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

			limitExhausted = (result.size()==query.getPageLimitOrMinusOne());

			return result.iterator();
		}
	}


	/**
	 * @param query
	 * <b>BEWARE:</b>
	 * The mechanism of this iterator works only,
	 * if the query result is stable.
	 * This means, the result of the query does not change while iterating.
	 * Otherwise, the iterator may miss some results,
	 * or return duplicates.
	 * Consider using
	 * {@link QueryIterators#iterateType(com.exedio.cope.Type, com.exedio.cope.Condition, int)}
	 * to deal with unstable queries.
	 */
	public static <E> Iterator<E> iterateStableQuery(
			final Query<E> query,
			final int slice)
	{
		requireNonNull(query, "query");
		requireGreaterZero(slice, "slice");

		//System.out.println("-- " + query.getPageOffset() + " " + query.getPageLimitOrMinusOne() + " " + slice);

		final Query<E> queryCopy = new Query<>(query);
		final int queryLimit = queryCopy.getPageLimitOrMinusOne();
		return (queryLimit==-1)
			? new ByStableQueryUnlimited<>(queryCopy, slice)
			: new ByStableQueryLimited  <>(queryCopy, slice, queryLimit);
	}

	private static final class ByStableQueryUnlimited<E> implements Iterator<E>
	{
		private final Query<E> query;
		private final int slice;

		private int offset;
		private Iterator<E> iterator;

		ByStableQueryUnlimited(
				final Query<E> query,
				final int slice)
		{
			this.query = query;
			this.slice = slice;

			this.offset = query.getPageOffset();
			this.iterator = iterator();
		}

		private Iterator<E> iterator()
		{
			//System.out.println("--   " + offset + " " + slice);
			query.setPage(offset, slice);
			return query.search().iterator();
		}

		@Override
		public boolean hasNext()
		{
			return iterator.hasNext();
		}

		@Override
		public E next()
		{
			final E result = iterator.next();

			if(!iterator.hasNext())
			{
				offset += slice;
				this.iterator = iterator();
			}

			return result;
		}
	}

	private static final class ByStableQueryLimited<E> implements Iterator<E>
	{
		private final Query<E> query;
		private final int slice;

		private int offset;
		private final int queryEnd;
		private Iterator<E> iterator;

		ByStableQueryLimited(
				final Query<E> query,
				final int slice,
				final int queryLimit)
		{
			this.query = query;
			this.slice = slice;

			this.offset = query.getPageOffset();
			this.queryEnd = offset + queryLimit;
			this.iterator = iterator();
		}

		private Iterator<E> iterator()
		{
			if(queryEnd<=offset)
				return null;

			final int newLimit =
				((offset+slice)<=queryEnd)
				? slice
				: (queryEnd - offset);

			//System.out.println("--   " + offset + " " + newLimit);
			assert 0<newLimit : newLimit;
			assert newLimit<=slice : newLimit;
			query.setPage(offset, newLimit);
			return query.search().iterator();
		}

		@Override
		public boolean hasNext()
		{
			return iterator!=null && iterator.hasNext();
		}

		@Override
		public E next()
		{
			if(iterator==null)
				throw new NoSuchElementException();

			final E result = iterator.next();

			if(!iterator.hasNext())
			{
				offset += slice;
				this.iterator = iterator();
			}

			return result;
		}
	}


	private QueryIterators()
	{
		// prevent instantiation
	}
}
