/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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

import static java.lang.System.arraycopy;

import java.lang.reflect.Array;

public final class Arrays
{
	public static boolean[] copyOf(final boolean[] original)
	{
		final int l = original.length;
		final boolean[] copy = new boolean[l];
		arraycopy(original, 0, copy, 0, l);
		return copy;
	}

	public static <T> T[] copyOf(final T[] original)
	{
		final int l = original.length;
		@SuppressWarnings("unchecked")
		final T[] copy = (T[])Array.newInstance(original.getClass().getComponentType(), l);
		arraycopy(original, 0, copy, 0, l);
		return copy;
	}

	/**
	 * @see java.util.Arrays#toString(byte[])
	 */
	public static String toString(final byte[] a, final int limit)
	{
		if(limit<=0)
			throw new IllegalArgumentException("limit must be greater that zero, but was " + limit);
		if(a==null||a.length<=limit)
			return java.util.Arrays.toString(a);

		final StringBuilder bf = new StringBuilder();
		bf.append('[');
		for(int i = 0; i<limit; i++)
		{
			bf.append(a[i]);
			bf.append(", ");
		}
		bf.append("... (").
			append(a.length).
			append(")]");
		return bf.toString();
	}

	private Arrays()
	{
		// prevent instantiation
	}
}
