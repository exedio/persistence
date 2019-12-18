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

package com.exedio.cope;

import static com.exedio.cope.tojunit.Assert.assertFails;

import org.junit.jupiter.api.function.Executable;

public final class JavaVersion
{
	public static void assertThrowsClassCastException(
			final Executable executable,
			final Class<?> from,
			final Class<?> to)
	{
		assertFails(
				executable,
				ClassCastException.class,
				isAtLeastJava9
				? from           + " cannot be cast to " + to + " (" +
				  from.getName() + " and "               + to.getName() + " are in unnamed module of loader 'app')"
				: from.getName() + " cannot be cast to " + to.getName());
	}

	public static void assertThrowsNegativeArraySizeException(
			final Executable executable,
			final int index)
	{
		assertFails(
				executable,
				NegativeArraySizeException.class,
				isAtLeastJava9 ? "" + index : null);
	}

	public static void assertThrowsArrayIndexOutOfBoundsException(
			final Executable executable,
			final int index)
	{
		assertFails(
				executable,
				ArrayIndexOutOfBoundsException.class,
				isAtLeastJava9
				? "Index " + index + " out of bounds for length " + index
				: ""       + index);
	}


	public static final boolean isAtLeastJava9 = isAtLeastJava9();

	private static boolean isAtLeastJava9()
	{
		try
		{
			Class.forName(Runtime.class.getName() + "$Version");
			System.out.println(JavaVersion.class.getName() + " >=9");
			return true;
		}
		catch(final ClassNotFoundException e)
		{
			System.out.println(JavaVersion.class.getName() + " ==1.8");
			return false;
		}
	}


	private JavaVersion()
	{
		// prevent instantiation
	}
}
