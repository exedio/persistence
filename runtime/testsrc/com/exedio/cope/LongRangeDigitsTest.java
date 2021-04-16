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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

public class LongRangeDigitsTest
{
	@Test void testIt()
	{
		final LongField f = new LongField();

		assertIt(1l                 ,                  9l, f.rangeDigits( 1));
		assertIt(10l                ,                 99l, f.rangeDigits( 2));
		assertIt(1000l              ,               9999l, f.rangeDigits( 4));
		assertIt(10000000000000000l ,  99999999999999999l, f.rangeDigits(17));
		assertIt(100000000000000000l, 999999999999999999l, f.rangeDigits(18));

		assertIt(0,                  9l, f.rangeDigits(0,  1));
		assertIt(0,                 99l, f.rangeDigits(0,  2));
		assertIt(0,               9999l, f.rangeDigits(0,  4));
		assertIt(0,  99999999999999999l, f.rangeDigits(0, 17));
		assertIt(0, 999999999999999999l, f.rangeDigits(0, 18));

		assertIt(1l                 ,                  9l, f.rangeDigits( 1,  1));
		assertIt(10l                ,                 99l, f.rangeDigits( 2,  2));
		assertIt(1000l              ,               9999l, f.rangeDigits( 4,  4));
		assertIt(10000000000000000l ,  99999999999999999l, f.rangeDigits(17, 17));
		assertIt(100000000000000000l, 999999999999999999l, f.rangeDigits(18, 18));

		assertIt(0l                ,                  9l, f.rangeDigits( 0,  1));
		assertIt(1l                ,                 99l, f.rangeDigits( 1,  2));
		assertIt(100l              ,               9999l, f.rangeDigits( 3,  4));
		assertIt(1000000000000000l ,  99999999999999999l, f.rangeDigits(16, 17));
		assertIt(10000000000000000l, 999999999999999999l, f.rangeDigits(17, 18));


		// digits
		try
		{
			f.rangeDigits(0);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("digits must be between 1 and 18, but was 0", e.getMessage());
		}
		try
		{
			f.rangeDigits(19);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("digits must be between 1 and 18, but was 19", e.getMessage());
		}

		// minimumDigits
		try
		{
			f.rangeDigits(-1, 1);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("minimumDigits must be between 0 and 18, but was -1", e.getMessage());
		}
		try
		{
			f.rangeDigits(19, 1);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("minimumDigits must be between 0 and 18, but was 19", e.getMessage());
		}

		// maximumDigits
		try
		{
			f.rangeDigits(0, 0);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("maximumDigits must be between 1 and 18, but was 0", e.getMessage());
		}
		try
		{
			f.rangeDigits(0, 19);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("maximumDigits must be between 1 and 18, but was 19", e.getMessage());
		}

		// wrong order
		try
		{
			f.rangeDigits(4, 3);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("maximumDigits must be greater or equal than minimumDigits, but was 3 and 4.", e.getMessage());
		}
	}

	private static void assertIt(
			final long expectedMinimum,
			final long expectedMaximum,
			final LongField actual)
	{
		assertEquals(expectedMinimum, actual.getMinimum());
		assertEquals(expectedMaximum, actual.getMaximum());
	}
}
