/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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

import static java.util.Objects.requireNonNull;

import com.exedio.cope.Query;
import java.util.Iterator;
import java.util.NoSuchElementException;

public final class StableQueryIterator<E>
{
	/**
	 * @param query
	 * <b>BEWARE:<b>
	 * The mechanism of this iterator works only,
	 * if the query result is stable.
	 * This means, the result of the query does not change while iterating.
	 * Otherwise, the iterator may miss some results,
	 * or return duplicates.
	 * Consider using
	 * {@link TypeIterator#iterate(com.exedio.cope.Type, com.exedio.cope.Condition, int)}
	 * to deal with unstable queries.
	 */
	public static <E> Iterator<E> iterate(
			final Query<E> query,
			final int slice)
	{
		requireNonNull(query, "query");
		if(slice<1)
			throw new IllegalArgumentException("slice must be greater 0, but was " + slice);

		//System.out.println("-- " + query.getOffset() + " " + query.getLimit() + " " + slice);

		final Query<E> queryCopy = new Query<>(query);
		final int queryLimit = queryCopy.getLimit();
		return (queryLimit==-1)
			? new Unlimited<>(queryCopy, slice)
			: new Limited  <>(queryCopy, slice, queryLimit);
	}

	private static final class Unlimited<E> implements Iterator<E>
	{
		private final Query<E> query;
		private final int slice;

		private int offset;
		private Iterator<E> iterator;

		Unlimited(
				final Query<E> query,
				final int slice)
		{
			this.query = query;
			this.slice = slice;

			this.offset = query.getOffset();
			this.iterator = iterator();
		}

		private Iterator<E> iterator()
		{
			//System.out.println("--   " + offset + " " + slice);
			query.setLimit(offset, slice);
			return query.search().iterator();
		}

		public boolean hasNext()
		{
			return iterator.hasNext();
		}

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

		public final void remove()
		{
			throw new UnsupportedOperationException();
		}
	}

	private static final class Limited<E> implements Iterator<E>
	{
		private final Query<E> query;
		private final int slice;

		private int offset;
		private final int queryEnd;
		private Iterator<E> iterator;

		Limited(
				final Query<E> query,
				final int slice,
				final int queryLimit)
		{
			this.query = query;
			this.slice = slice;

			this.offset = query.getOffset();
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
			query.setLimit(offset, newLimit);
			return query.search().iterator();
		}

		public boolean hasNext()
		{
			return iterator!=null && iterator.hasNext();
		}

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

		public final void remove()
		{
			throw new UnsupportedOperationException();
		}
	}


	private StableQueryIterator()
	{
		// prevent instantiation
	}
}
