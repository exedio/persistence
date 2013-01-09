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

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import com.exedio.cope.Condition;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.Query;
import com.exedio.cope.This;
import com.exedio.cope.Type;

public final class TypeIterator
{
	public static <E extends Item> Iterator<E> iterate(
			final Type<E> type,
			final Condition condition,
			final int slice)
	{
		return iterate(type, condition, false, slice);
	}

	/**
	 * Works as {@link #iterate(Type, Condition, int)}
	 * but creates its own transaction whenever needed.
	 */
	public static <E extends Item> Iterator<E> iterateTransactionally(
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
		if(type==null)
			throw new NullPointerException("type");
		if(slice<1)
			throw new IllegalArgumentException("slice must be greater 0, but was " + slice);

		return new Iter<E>(type, condition, transactionally, slice);
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
					this.iterator = Iter.<E>empty();
				}
			}

			return result;
		}

		private Iterator<E> search()
		{
			final List<E> result;

			if(transactionally)
			{
				final Model model = typeThis.getType().getModel();
				try
				{
					model.startTransaction(query.toString());
					result = query.search();
					model.commit();
				}
				finally
				{
					model.rollbackIfNotCommitted();
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

		@SuppressWarnings({"unchecked", "rawtypes"}) // OK: universal object
		private static final <E> Iterator<E> empty()
		{
			return (Iterator)EMPTY;
		}

		private static final Iterator<Object> EMPTY = new Iterator<Object>(){

			public boolean hasNext()
			{
				return false;
			}

			public Object next()
			{
				throw new NoSuchElementException();
			}

			public void remove()
			{
				throw new RuntimeException();
			}
		};
	}


	private TypeIterator()
	{
		// prevent instantiation
	}
}
