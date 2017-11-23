/*
 * Copyright (C) 2004-2015  exedio GmbH (www.exedio.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package org.junit;

import org.junit.jupiter.api.Assertions;

/**
 * This is a replacement of the respective class in JUnit 4.
 * Allows switching to JUnit 5 without extensive changes in the project.
 */
public final class Assert
{
	public static void assertTrue(final boolean condition)
	{
		Assertions.assertTrue(condition);
	}

	public static void assertTrue(final boolean condition, final String message)
	{
		Assertions.assertTrue(condition, message);
	}

	public static void assertFalse(final boolean condition)
	{
		Assertions.assertFalse(condition);
	}

	public static void assertFalse(final boolean condition, final String message)
	{
		Assertions.assertFalse(condition, message);
	}


	public static void assertNull(final Object actual)
	{
		Assertions.assertNull(actual);
	}

	public static void assertNull(final Object actual, final String message)
	{
		Assertions.assertNull(actual, message);
	}

	public static void assertNotNull(final Object actual)
	{
		Assertions.assertNotNull(actual);
	}

	public static void assertNotNull(final Object actual, final String message)
	{
		Assertions.assertNotNull(actual, message);
	}


	public static void assertEquals(final short expected, final short actual)
	{
		Assertions.assertEquals(expected, actual);
	}

	public static void assertEquals(final short expected, final short actual, final String message)
	{
		Assertions.assertEquals(expected, actual, message);
	}

	public static void assertEquals(final byte expected, final byte actual)
	{
		Assertions.assertEquals(expected, actual);
	}

	public static void assertEquals(final byte expected, final byte actual, final String message)
	{
		Assertions.assertEquals(expected, actual, message);
	}

	public static void assertEquals(final int expected, final int actual)
	{
		Assertions.assertEquals(expected, actual);
	}

	public static void assertEquals(final int expected, final int actual, final String message)
	{
		Assertions.assertEquals(expected, actual, message);
	}

	public static void assertEquals(final long expected, final long actual)
	{
		Assertions.assertEquals(expected, actual);
	}

	public static void assertEquals(final long expected, final long actual, final String message)
	{
		Assertions.assertEquals(expected, actual, message);
	}

	public static void assertEquals(final char expected, final char actual)
	{
		Assertions.assertEquals(expected, actual);
	}

	public static void assertEquals(final char expected, final char actual, final String message)
	{
		Assertions.assertEquals(expected, actual, message);
	}

	public static void assertEquals(final float expected, final float actual)
	{
		Assertions.assertEquals(expected, actual);
	}

	public static void assertEquals(final float expected, final float actual, final String message)
	{
		Assertions.assertEquals(expected, actual, message);
	}

	public static void assertEquals(final double expected, final double actual)
	{
		Assertions.assertEquals(expected, actual);
	}

	public static void assertEquals(final double expected, final double actual, final String message)
	{
		Assertions.assertEquals(expected, actual, message);
	}

	public static void assertEquals(final Object expected, final Object actual)
	{
		Assertions.assertEquals(expected, actual);
	}

	public static void assertEquals(final Object expected, final Object actual, final String message)
	{
		Assertions.assertEquals(expected, actual, message);
	}


	public static void assertEquals(final double expected, final double actual, final double delta)
	{
		Assertions.assertEquals(expected, actual, delta);
	}

	public static void assertEquals(final double expected, final double actual, final double delta, final String message)
	{
		Assertions.assertEquals(expected, actual, delta, message);
	}


	public static void assertArrayEquals(final Object[] expected, final Object[] actual)
	{
		Assertions.assertArrayEquals(expected, actual);
	}

	public static void assertArrayEquals(final Object[] expected, final Object[] actual, final String message)
	{
		Assertions.assertArrayEquals(expected, actual, message);
	}


	public static void assertSame(final Object expected, final Object actual)
	{
		Assertions.assertSame(expected, actual);
	}

	public static void assertSame(final Object expected, final Object actual, final String message)
	{
		Assertions.assertSame(expected, actual, message);
	}

	public static void assertNotSame(final Object unexpected, final Object actual)
	{
		Assertions.assertNotSame(unexpected, actual);
	}


	public static void fail()
	{
		Assertions.fail((String)null);
	}

	public static void fail(final String message)
	{
		Assertions.fail(message);
	}


	private Assert()
	{
		// prevent instantiation
	}
}
