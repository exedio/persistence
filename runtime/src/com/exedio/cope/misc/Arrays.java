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

public final class Arrays
{
	public static boolean[] copyOf(final boolean[] original)
	{
		return java.util.Arrays.copyOf(original, original.length);
	}

	public static byte[] copyOf(final byte[] original)
	{
		return java.util.Arrays.copyOf(original, original.length);
	}

	public static int[] copyOf(final int[] original)
	{
		return java.util.Arrays.copyOf(original, original.length);
	}

	public static <T> T[] copyOf(final T[] original)
	{
		return java.util.Arrays.copyOf(original, original.length);
	}

	/**
	 * @see java.util.Arrays#toString(byte[])
	 */
	public static String toString(final byte[] a, final int limit)
	{
		if(limit<=0)
			throw new IllegalArgumentException("limit must be greater that zero, but was " + limit);
		if(a==null)
			return "null";
		if(a.length==0)
			return "[]";

		final StringBuilder bf = new StringBuilder();
		append0(bf, a, limit);
		return bf.toString();
	}

	/**
	 * Is equivalent to {@code bf.{@link StringBuilder#append(String) append}({@link #toString(byte[], int) toString}(a, limit));}
	 */
	public static void append(final StringBuilder bf, final byte[] a, final int limit)
	{
		if(limit<=0)
			throw new IllegalArgumentException("limit must be greater that zero, but was " + limit);
		if(a==null)
		{
			bf.append("null");
			return;
		}
		if(a.length==0)
		{
			bf.append("[]");
			return;
		}
		append0(bf, a, limit);
	}

	private static void append0(final StringBuilder bf, final byte[] a, final int limit)
	{
		bf.append('[').
			append(a[0]);

		final boolean exceedLimit = a.length>limit;
		final int actualLimit = exceedLimit ? limit : a.length;

		for(int i = 1; i<actualLimit; i++)
			bf.append(", ").
				append(a[i]);

		if(exceedLimit)
			bf.append(" ... (").
				append(a.length).
				append(")]");
		else
			bf.append(']');
	}

	private Arrays()
	{
		// prevent instantiation
	}
}
