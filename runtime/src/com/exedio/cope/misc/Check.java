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

import java.lang.reflect.Array;

public final class Check
{
	public static int requireGreaterZero(final int value, final String name)
	{
		if(value<=0)
			throw new IllegalArgumentException(name + " must be greater zero, but was " + value);
		return value;
	}

	public static int requireNonNegative(final int value, final String name)
	{
		if(value<0)
			throw new IllegalArgumentException(name + " must not be negative, but was " + value);
		return value;
	}

	public static String requireNonEmpty(final String value, final String name)
	{
      if(value==null)
         throw new NullPointerException(name);
		if(value.isEmpty())
			throw new IllegalArgumentException(name + " must not be empty");
		return value;
	}

	/**
	 * Besides checking the value, this method returns a copy of the given value
	 * to avoid later modifications of the value by the caller.
	 */
	public static <T> T[] requireNonEmptyAndCopy(final T[] value, final String name)
	{
		if(value==null)
			throw new NullPointerException(name);
		if(value.length==0)
			throw new IllegalArgumentException(name + " must not be empty");

		@SuppressWarnings("unchecked")
		final T[] result = (T[])Array.newInstance(value.getClass().getComponentType(), value.length);
		for(int i = 0; i<value.length; i++)
		{
			final T s = value[i];
			if(s==null)
				throw new NullPointerException(name + '[' + i + ']');
			result[i] = s;
		}

		return result;
	}

	/**
	 * Besides checking the value, this method returns a copy of the given value
	 * to avoid later modifications of the value by the caller.
	 */
	public static String[] requireNonEmptyAndCopy(final String[] value, final String name)
	{
		if(value==null)
			throw new NullPointerException(name);
		if(value.length==0)
			throw new IllegalArgumentException(name + " must not be empty");

		final String[] result = new String[value.length];
		for(int i = 0; i<value.length; i++)
		{
			final String s = value[i];
			if(s==null)
				throw new NullPointerException(name + '[' + i + ']');
			if(s.isEmpty())
				throw new IllegalArgumentException(name + '[' + i + "] must not be empty");
			result[i] = s;
		}

		return result;
	}

	private Check()
	{
		// prevent instantiation
	}
}
