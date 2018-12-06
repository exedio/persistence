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

public final class Check
{
	public static int requireGreaterZero(final int value, final String name)
	{
		return com.exedio.cope.util.Check.requireGreaterZero(value, name);
	}

	public static long requireGreaterZero(final long value, final String name)
	{
		return com.exedio.cope.util.Check.requireGreaterZero(value, name);
	}

	public static int requireNonNegative(final int value, final String name)
	{
		return com.exedio.cope.util.Check.requireNonNegative(value, name);
	}

	public static long requireNonNegative(final long value, final String name)
	{
		return com.exedio.cope.util.Check.requireNonNegative(value, name);
	}

	public static <E extends Comparable<E>> E requireAtLeast(final E value, final String name, final E minimum)
	{
		return com.exedio.cope.util.Check.requireAtLeast(value, name, minimum);
	}

	public static String requireNonEmpty(final String value, final String name)
	{
		return com.exedio.cope.util.Check.requireNonEmpty(value, name);
	}

	/**
	 * Besides checking the value, this method returns a copy of the given value
	 * to avoid later modifications of the value by the caller.
	 */
	public static <T> T[] requireNonEmptyAndCopy(final T[] value, final String name)
	{
		return com.exedio.cope.util.Check.requireNonEmptyAndCopy(value, name);
	}

	/**
	 * Besides checking the value, this method returns a copy of the given value
	 * to avoid later modifications of the value by the caller.
	 */
	public static String[] requireNonEmptyAndCopy(final String[] value, final String name)
	{
		return com.exedio.cope.util.Check.requireNonEmptyAndCopy(value, name);
	}

	private Check()
	{
		// prevent instantiation
	}
}
