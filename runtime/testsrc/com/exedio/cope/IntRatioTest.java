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

import static com.exedio.cope.IntRatio.ratio;
import static java.lang.Integer.MAX_VALUE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

public class IntRatioTest
{
	@Test void testNormal()
	{
		assertEquals(20, ratio(30, 2, 3));
		assertEquals(13, ratio(20, 2, 3)); // correct rounding
	}

	@Test void testBorders()
	{
		assertEquals(0, ratio( 0, 20, 3));
		assertEquals(0, ratio(20,  0, 3));

		assertEquals(0, ratio(20,  0, 1));
		assertEquals(0, ratio( 0, 20, 1));

		assertEquals(20, ratio(MAX_VALUE, 20, MAX_VALUE));
		assertEquals( 0, ratio(MAX_VALUE,  0, MAX_VALUE));
		assertEquals(20, ratio(20, MAX_VALUE, MAX_VALUE));
		assertEquals( 0, ratio( 0, MAX_VALUE, MAX_VALUE));

		assertEquals(MAX_VALUE  , ratio(MAX_VALUE  , MAX_VALUE  , MAX_VALUE));
		assertEquals(MAX_VALUE-1, ratio(MAX_VALUE-1, MAX_VALUE  , MAX_VALUE));
		assertEquals(MAX_VALUE-1, ratio(MAX_VALUE  , MAX_VALUE-1, MAX_VALUE));
	}

	@Test void testFail()
	{
		try
		{
			ratio(-1, 20, 3);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("number must not be negative, but was -1", e.getMessage());
		}
		try
		{
			ratio(20, -1, 3);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("dividend must not be negative, but was -1", e.getMessage());
		}
		try
		{
			ratio(20, 20, 0);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("divisor must be greater zero, but was 0", e.getMessage());
		}
		try
		{
			ratio(MAX_VALUE  , MAX_VALUE  , MAX_VALUE-1);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("result overflow 2147483648", e.getMessage());
		}
	}
}
