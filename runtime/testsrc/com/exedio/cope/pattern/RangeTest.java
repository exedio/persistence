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

package com.exedio.cope.pattern;

import static com.exedio.cope.pattern.Range.valueOf;
import static com.exedio.cope.tojunit.Assert.assertEqualsStrict;
import static com.exedio.cope.tojunit.Assert.assertNotEqualsStrict;
import static com.exedio.cope.tojunit.Assert.reserialize;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

public class RangeTest
{
	@Test void testAB()
	{
		final Range<Integer> r = valueOf(1, 3);
		assertEquals(1, r.getFrom().intValue());
		assertEquals(3, r.getTo().intValue());
		assertEquals("[1-3]", r.toString());

		assertEquals(false, r.contains(0));
		assertEquals(true,  r.contains(1));
		assertEquals(true,  r.contains(3));
		assertEquals(false, r.contains(4));

		try
		{
			r.contains(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("value", e.getMessage());
		}
	}

	@Test void testNA()
	{
		final Range<Integer> r = valueOf(null, 3);
		assertEquals(null, r.getFrom());
		assertEquals(3, r.getTo().intValue());
		assertEquals("[-3]", r.toString());

		assertEquals(true,  r.contains(0));
		assertEquals(true,  r.contains(1));
		assertEquals(true,  r.contains(3));
		assertEquals(false, r.contains(4));

		try
		{
			r.contains(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("value", e.getMessage());
		}
	}

	@Test void testAN()
	{
		final Range<Integer> r = valueOf(1, null);
		assertEquals(1, r.getFrom().intValue());
		assertEquals(null, r.getTo());
		assertEquals("[1-]", r.toString());

		assertEquals(false, r.contains(0));
		assertEquals(true,  r.contains(1));
		assertEquals(true,  r.contains(3));
		assertEquals(true,  r.contains(4));

		try
		{
			r.contains(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("value", e.getMessage());
		}
	}

	@Test void testNN()
	{
		final Range<Integer> r = valueOf((Integer)null, null);
		assertEquals(null, r.getFrom());
		assertEquals(null, r.getTo());
		assertEquals("[-]", r.toString());

		assertEquals(true, r.contains(0));
		assertEquals(true, r.contains(1));
		assertEquals(true, r.contains(3));
		assertEquals(true, r.contains(4));

		try
		{
			r.contains(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("value", e.getMessage());
		}
	}

	@Test void testEquals()
	{
		assertEqualsStrict(valueOf(1, 3), valueOf(1, 3));
		assertEqualsStrict(valueOf(null, 3), valueOf(null, 3));
		assertEqualsStrict(valueOf(1, null), valueOf(1, null));
		assertEqualsStrict(valueOf((Integer)null, null), valueOf((Integer)null, null));
		assertNotEqualsStrict(valueOf(1, 3), valueOf(2, 3));
		assertNotEqualsStrict(valueOf(1, 3), valueOf(1, 4));
		try
		{
			valueOf(3, 2);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("from 3 greater than to 2", e.getMessage());
		}

		assertEqualsStrict(valueOf(5, 5), valueOf(5, 5));
		assertNotEqualsStrict(valueOf(5, 5), valueOf(6, 6));

		assertNotSame(valueOf(1, 3), valueOf(1, 3));
		assertNotSame(valueOf(null, 3), valueOf(null, 3));
		assertNotSame(valueOf(1, null), valueOf(1, null));
		assertSame(valueOf((Integer)null, null), valueOf((Integer)null, null));
	}

	@Test void testSerializable()
	{
		final Range<Integer> value = valueOf(44, 55);
		assertEquals(Integer.valueOf(44), value.getFrom());
		assertEquals(Integer.valueOf(55), value.getTo());

		final Range<Integer> serializedValue = reserialize(value, 179);
		assertEquals(value, serializedValue);
		assertNotSame(value, serializedValue);
		assertEquals(Integer.valueOf(44), serializedValue.getFrom());
		assertEquals(Integer.valueOf(55), serializedValue.getTo());
	}
}
