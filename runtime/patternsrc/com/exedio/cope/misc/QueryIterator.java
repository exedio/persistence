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

import com.exedio.cope.Query;
import java.util.Iterator;

/**
 * @param <E> was used when class was not yet deprecated
 * @deprecated Use {@link StableQueryIterator} instead.
 */
@Deprecated
public final class QueryIterator<E>
{
	/**
	 * @deprecated Use {@link StableQueryIterator#iterate(Query, int)} instead.
	 */
	@Deprecated
	public static <E> Iterator<E> wrap(
			final Query<E> query,
			final int slice)
	{
		return StableQueryIterator.iterate(query, slice);
	}


	private QueryIterator()
	{
		// prevent instantiation
	}
}