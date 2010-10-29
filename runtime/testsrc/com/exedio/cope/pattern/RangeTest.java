/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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

import static com.exedio.cope.pattern.Range.newRange;

import com.exedio.cope.junit.CopeAssert;

public class RangeTest extends CopeAssert
{
	public void testIt()
	{
		assertEquals(newRange(1, 3), newRange(1, 3));
		assertNotEquals(newRange(1, 3), newRange(2, 3));
		assertNotEquals(newRange(1, 3), newRange(1, 4));
		assertNotEquals(newRange(1, 3), newRange(3, 1));

		assertEquals(newRange(5, 5), newRange(5, 5));
		assertNotEquals(newRange(5, 5), newRange(6, 6));
	}

	private static void assertEquals(final Range c1, final Range c2)
	{
		assertEquals((Object)c1, (Object)c2);
		assertEquals((Object)c2, (Object)c1);
		assertEquals(c1.hashCode(), c2.hashCode());
	}

	private static void assertNotEquals(final Range c1, final Range c2)
	{
		assertTrue(!c1.equals(c2));
		assertTrue(!c2.equals(c1));
		assertTrue(c1.hashCode()!=c2.hashCode());
	}
}
