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

import static com.exedio.cope.SequenceInfoAssert.assertInfoAny;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class SequenceCounterTest
{
	private static final StringField feature = new StringField();

	@Test void testNormal()
	{
		final SequenceCounter c =
				new SequenceCounter(feature, 15, 10, 20);
		assertIt(c, 15, 10, 20);
		c.next(15);
		assertIt(c, 15, 10, 20, 1, 15, 15);
		c.next(16);
		assertIt(c, 15, 10, 20, 2, 15, 16);
		c.next(17);
		assertIt(c, 15, 10, 20, 3, 15, 17);
	}

	@Test void testLimit()
	{
		final SequenceCounter c =
				new SequenceCounter(feature, 10, 10, 12);
		assertIt(c, 10, 10, 12);
		c.next(10);
		assertIt(c, 10, 10, 12, 1, 10, 10);
		c.next(11);
		assertIt(c, 10, 10, 12, 2, 10, 11);
		c.next(12);
		assertIt(c, 10, 10, 12, 3, 10, 12);
		try
		{
			c.next(13);
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("sequence overflow to 13 in " + feature + " limited to 10,12", e.getMessage());
		}
		assertIt(c, 10, 10, 12, 3, 10, 12);
	}

	@Test void testHole()
	{
		final SequenceCounter c =
				new SequenceCounter(feature, 15, 10, 20);
		assertIt(c, 15, 10, 20);
		c.next(17);
		assertIt(c, 15, 10, 20, 1, 17, 17);
		c.next(19);
		assertIt(c, 15, 10, 20, 2, 17, 19);
	}

	@Test void testMin()
	{
		final SequenceCounter c =
				new SequenceCounter(feature, 15, 10, 20);
		assertIt(c, 15, 10, 20);
		try
		{
			c.next(9);
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("sequence overflow to 9 in " + feature + " limited to 10,20", e.getMessage());
		}
		assertIt(c, 15, 10, 20);
	}

	@Test void testMax()
	{
		final SequenceCounter c =
				new SequenceCounter(feature, 15, 10, 20);
		assertIt(c, 15, 10, 20);
		try
		{
			c.next(21);
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("sequence overflow to 21 in " + feature + " limited to 10,20", e.getMessage());
		}
		assertIt(c, 15, 10, 20);
	}

	private static void assertIt(
			final SequenceCounter counter,
			final int start, final int minimum, final int maximum,
			final int count, final int first, final int last)
	{
		assertInfoAny(feature, start, minimum, maximum, count, first, last, counter.getInfo());
	}

	private static void assertIt(
			final SequenceCounter counter,
			final int start, final int minimum, final int maximum)
	{
		assertInfoAny(feature, start, minimum, maximum, counter.getInfo());
	}
}