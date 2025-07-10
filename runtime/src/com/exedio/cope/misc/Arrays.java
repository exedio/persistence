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

import java.lang.reflect.Array;

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

	public static boolean[] append(final boolean[] a, final boolean b)
	{
		final int aLength = a.length;
		final boolean[] result = java.util.Arrays.copyOf(a, aLength+1);
		result[aLength] = b;
		return result;
	}

	public static <T> T[] append(final T[] a, final T b)
	{
		final int aLength = a.length;
		final T[] result = java.util.Arrays.copyOf(a, aLength+1);
		result[aLength] = b;
		return result;
	}

	public static <T> T[] append(final T[] a, final T[] b)
	{
		final int aLength = a.length;
		final int bLength = b.length;
		final T[] result = java.util.Arrays.copyOf(a, aLength+bLength);
		System.arraycopy(b, 0, result, aLength, bLength);
		return result;
	}

	public static <T> T[] prepend(final T a, final T[] b)
	{
		final int bLength = b.length;
		@SuppressWarnings("unchecked")
		final T[] result = (T[])Array.newInstance(b.getClass().getComponentType(), bLength+1);
		result[0] = a;
		System.arraycopy(b, 0, result, 1, bLength);
		return result;
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

		final StringBuilder sb = new StringBuilder();
		append0(sb, a, limit);
		return sb.toString();
	}

	/**
	 * Is equivalent to {@code sb.{@link StringBuilder#append(String) append}({@link #toString(byte[], int) toString}(a, limit));}
	 */
	public static void append(final StringBuilder sb, final byte[] a, final int limit)
	{
		if(limit<=0)
			throw new IllegalArgumentException("limit must be greater that zero, but was " + limit);
		if(a==null)
		{
			sb.append("null");
			return;
		}
		if(a.length==0)
		{
			sb.append("[]");
			return;
		}
		append0(sb, a, limit);
	}

	private static void append0(final StringBuilder sb, final byte[] a, final int limit)
	{
		sb.append('[').
			append(a[0]);

		final boolean exceedLimit = a.length>limit;
		final int actualLimit = exceedLimit ? limit : a.length;

		for(int i = 1; i<actualLimit; i++)
			sb.append(", ").
				append(a[i]);

		if(exceedLimit)
			sb.append(" ... (").
				append(a.length).
				append(")]");
		else
			sb.append(']');
	}

	private Arrays()
	{
		// prevent instantiation
	}
}
