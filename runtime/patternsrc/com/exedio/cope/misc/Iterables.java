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

import java.util.Iterator;

public final class Iterables
{
	public static <E> Iterable<E> once(final Iterator<E> iterator)
	{
		return new Once<>(requireNonNull(iterator, "iterator"));
	}

	private static final class Once<E> implements Iterable<E>
	{
		private Iterator<E> iterator;

		Once(final Iterator<E> iterator)
		{
			this.iterator = iterator;
		}

		@Override
		public Iterator<E> iterator()
		{
			if(iterator==null)
				throw new IllegalStateException("exhausted");

			final Iterator<E> result = iterator;
			iterator = null;
			return result;
		}
	}

	private Iterables()
	{
		// prevent instantiation
	}
}
