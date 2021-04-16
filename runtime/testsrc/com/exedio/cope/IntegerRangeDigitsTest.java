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

public class IntegerRangeDigitsTest
{
	@Test void testIt()
	{
		final IntegerField f = new IntegerField();

		assertIt(1        ,         9, f.rangeDigits(1));
		assertIt(10       ,        99, f.rangeDigits(2));
		assertIt(1000     ,      9999, f.rangeDigits(4));
		assertIt(10000000 ,  99999999, f.rangeDigits(8));
		assertIt(100000000, 999999999, f.rangeDigits(9));

		assertIt(0,         9, f.rangeDigits(0, 1));
		assertIt(0,        99, f.rangeDigits(0, 2));
		assertIt(0,      9999, f.rangeDigits(0, 4));
		assertIt(0,  99999999, f.rangeDigits(0, 8));
		assertIt(0, 999999999, f.rangeDigits(0, 9));

		assertIt(1        ,         9, f.rangeDigits(1, 1));
		assertIt(10       ,        99, f.rangeDigits(2, 2));
		assertIt(1000     ,      9999, f.rangeDigits(4, 4));
		assertIt(10000000 ,  99999999, f.rangeDigits(8, 8));
		assertIt(100000000, 999999999, f.rangeDigits(9, 9));

		assertIt(0       ,         9, f.rangeDigits(0, 1));
		assertIt(1       ,        99, f.rangeDigits(1, 2));
		assertIt(100     ,      9999, f.rangeDigits(3, 4));
		assertIt(1000000 ,  99999999, f.rangeDigits(7, 8));
		assertIt(10000000, 999999999, f.rangeDigits(8, 9));


		// digits
		try
		{
			f.rangeDigits(0);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("digits must be between 1 and 9, but was 0", e.getMessage());
		}
		try
		{
			f.rangeDigits(10);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("digits must be between 1 and 9, but was 10", e.getMessage());
		}

		// minimumDigits
		try
		{
			f.rangeDigits(-1, 1);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("minimumDigits must be between 0 and 9, but was -1", e.getMessage());
		}
		try
		{
			f.rangeDigits(10, 1);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("minimumDigits must be between 0 and 9, but was 10", e.getMessage());
		}

		// maximumDigits
		try
		{
			f.rangeDigits(0, 0);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("maximumDigits must be between 1 and 9, but was 0", e.getMessage());
		}
		try
		{
			f.rangeDigits(0, 10);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("maximumDigits must be between 1 and 9, but was 10", e.getMessage());
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
			final int expectedMinimum,
			final int expectedMaximum,
			final IntegerField actual)
	{
		assertEquals(expectedMinimum, actual.getMinimum());
		assertEquals(expectedMaximum, actual.getMaximum());
	}
}
