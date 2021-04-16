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

import static com.exedio.cope.CastUtils.toIntCapped;
import static java.lang.Integer.MAX_VALUE;
import static java.lang.Integer.MIN_VALUE;
import static java.lang.Math.toIntExact;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

public class CastUtilsTest
{
	@Test void testToIntExact()
	{
		assertEquals(0, toIntExact(0L));
		assertEquals(10, toIntExact(10L));
		assertEquals(-10, toIntExact(-10L));
		assertEquals(MAX_VALUE, toIntExact(MAX_VALUE));
		assertEquals(MIN_VALUE, toIntExact(MIN_VALUE));

		try
		{
			assertEquals(MAX_VALUE, toIntExact(MAX_VALUE+1L));
			fail();
		}
		catch(final ArithmeticException e)
		{
			assertEquals("integer overflow", e.getMessage());
		}
		try
		{
			assertEquals(MIN_VALUE, toIntExact(MIN_VALUE-1L));
			fail();
		}
		catch(final ArithmeticException e)
		{
			assertEquals("integer overflow", e.getMessage());
		}

		try
		{
			assertEquals(MAX_VALUE, toIntExact(Long.MAX_VALUE));
			fail();
		}
		catch(final ArithmeticException e)
		{
			assertEquals("integer overflow", e.getMessage());
		}

		try
		{
			assertEquals(MIN_VALUE, toIntExact(Long.MIN_VALUE));
			fail();
		}
		catch(final ArithmeticException e)
		{
			assertEquals("integer overflow", e.getMessage());
		}
	}

	@Test void testToIntCapped()
	{
		assertEquals(0, toIntCapped(0L));
		assertEquals(10, toIntCapped(10L));
		assertEquals(-10, toIntCapped(-10L));
		assertEquals(MAX_VALUE, toIntCapped(MAX_VALUE));
		assertEquals(MIN_VALUE, toIntCapped(MIN_VALUE));

		assertEquals(MAX_VALUE, toIntCapped(MAX_VALUE+1L));
		assertEquals(MIN_VALUE, toIntCapped(MIN_VALUE-1L));

		assertEquals(MAX_VALUE, toIntCapped(Long.MAX_VALUE));
		assertEquals(MIN_VALUE, toIntCapped(Long.MIN_VALUE));
	}
}
