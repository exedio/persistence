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

import com.exedio.cope.Condition;
import com.exedio.cope.Item;
import com.exedio.cope.Type;
import java.util.Iterator;

/**
 * @deprecated Use {@link QueryIterators} instead
 */
@Deprecated
public final class TypeIterator
{
	/**
	 * @deprecated Use {@link QueryIterators#iterateType(Type, Condition, int)} instead
	 */
	@Deprecated
	public static <E extends Item> Iterator<E> iterate(
			final Type<E> type,
			final Condition condition,
			final int slice)
	{
		return QueryIterators.iterateType(type, condition, slice);
	}

	/**
	 * @deprecated Use {@link QueryIterators#iterateTypeTransactionally(Type, Condition, int)} instead
	 */
	@Deprecated
	public static <E extends Item> Iterator<E> iterateTransactionally(
			final Type<E> type,
			final Condition condition,
			final int slice)
	{
		return QueryIterators.iterateTypeTransactionally(type, condition, slice);
	}


	private TypeIterator()
	{
		// prevent instantiation
	}
}
