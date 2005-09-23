/*
 * Copyright (C) 2004-2005  exedio GmbH (www.exedio.com)
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


package com.exedio.cope.util;

import com.exedio.cope.junit.CopeAssert;


public class DayTest extends CopeAssert
{
	public void testIt()
	{
		try
		{
			new Day(999, 31, 12);
		}
		catch(RuntimeException e)
		{
			assertEquals("year must be in range 1000..9999, but was: 999", e.getMessage());
		}
		try
		{
			new Day(10000, 31, 12);
		}
		catch(RuntimeException e)
		{
			assertEquals("year must be in range 1000..9999, but was: 10000", e.getMessage());
		}
		try
		{
			new Day(2005, 0, 12);
		}
		catch(RuntimeException e)
		{
			assertEquals("month must be in range 1..12, but was: 0", e.getMessage());
		}
		try
		{
			new Day(2005, 32, 12);
		}
		catch(RuntimeException e)
		{
			assertEquals("month must be in range 1..12, but was: 32", e.getMessage());
		}
		try
		{
			new Day(2005, 9, 0);
		}
		catch(RuntimeException e)
		{
			assertEquals("day must be in range 1..31, but was: 0", e.getMessage());
		}
		try
		{
			new Day(2005, 9, 32);
		}
		catch(RuntimeException e)
		{
			assertEquals("day must be in range 1..31, but was: 32", e.getMessage());
		}

		final Day d = new Day(2005, 9, 23);
		assertEquals(2005, d.getYear());
		assertEquals(9, d.getMonth());
		assertEquals(23, d.getDay());
		assertEquals("2005/9/23", d.toString());
		
		assertEquals(d, new Day(2005, 9, 23));
		assertNotEquals(d, new Day(2004, 9, 23));
		assertNotEquals(d, new Day(2005, 8, 23));
		assertNotEquals(d, new Day(2005, 9, 22));
		assertTrue(!d.equals("hallo"));
		assertTrue(!d.equals(new Integer(22)));
	}
	
	static final void assertEquals(final Day expected, final Day actual)
	{
		assertEquals((Object)expected, (Object)actual);
		assertEquals(expected.hashCode(), actual.hashCode());
	}
	
	static final void assertNotEquals(final Day expected, final Day actual)
	{
		assertTrue(!expected.equals(actual));
		assertTrue(expected.hashCode()!=actual.hashCode());
	}
	
}
