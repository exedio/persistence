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

package com.exedio.cope.instrument.tojunit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.exedio.cope.ConstraintViolationException;
import com.exedio.cope.Feature;
import com.exedio.cope.Item;
import java.util.function.Function;
import org.junit.jupiter.api.function.Executable;

@SuppressWarnings("unused") // OK: for later use
public final class Assert
{
	public static <T extends Throwable> T assertFails(
			final Executable executable,
			final Class<T> expectedType,
			final String expectedMessage)
	{
		return assertFails(executable, expectedType, expectedMessage, Function.identity());
	}

	public static <T extends Throwable> T assertFails(
			final Executable executable,
			final Class<T> expectedType,
			final String expectedMessage,
			final Function<String, String> actualMessageFilter)
	{
		final T result = assertThrows(expectedType, executable);
		assertSame(expectedType, result.getClass());
		assertEquals(expectedMessage, actualMessageFilter.apply(result.getMessage()));
		return result;
	}

	@SuppressWarnings("UnusedReturnValue") // OK: for later use
	public static <T extends ConstraintViolationException> T assertFails(
			final Executable executable,
			final Class<T> expectedType,
			final String expectedMessage,
			final Feature expectedFeature)
	{
		return assertFails(executable, expectedType, expectedMessage, expectedFeature, null);
	}

	public static <T extends ConstraintViolationException> T assertFails(
			final Executable executable,
			final Class<T> expectedType,
			final String expectedMessage,
			final Feature expectedFeature,
			final Item expectedItem)
	{
		final T result = assertFails(executable, expectedType, expectedMessage);
		assertSame(expectedFeature, result.getFeature());
		assertSame(expectedItem, result.getItem());
		return result;
	}


	private Assert()
	{
		// prevent instantiation
	}
}
