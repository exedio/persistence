/*
 * Copyright (C) 2004-2008  exedio GmbH (www.exedio.com)
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

import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.exedio.cope.junit.CopeAssert;


public class DayTest extends CopeAssert
{
	public void testIt() throws ParseException
	{
		try
		{
			new Day(999, 31, 12);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("year must be in range 1000..9999, but was: 999", e.getMessage());
		}
		try
		{
			new Day(10000, 31, 12);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("year must be in range 1000..9999, but was: 10000", e.getMessage());
		}
		try
		{
			new Day(2005, 0, 12);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("month must be in range 1..12, but was: 0", e.getMessage());
		}
		try
		{
			new Day(2005, 32, 12);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("month must be in range 1..12, but was: 32", e.getMessage());
		}
		try
		{
			new Day(2005, 9, 0);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("day must be in range 1..31, but was: 0", e.getMessage());
		}
		try
		{
			new Day(2005, 9, 32);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("day must be in range 1..31, but was: 32", e.getMessage());
		}
		assertEquals(2005, new Day(2005, 2, 31).getYear()); // TODO this is not ok
		assertEquals(2,    new Day(2005, 2, 31).getMonth());
		assertEquals(31,   new Day(2005, 2, 31).getDay());

		final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

		final Day d = new Day(2005, 9, 23);
		assertEquals(2005, d.getYear());
		assertEquals(9, d.getMonth());
		assertEquals(23, d.getDay());
		assertEquals(df.parse("2005-09-23 00:00:00.000").getTime(), d.getTimeInMillis());
		assertEquals("2005/9/23", d.toString());
		
		assertEquals(d, new Day(2005, 9, 23));
		assertNotEquals(d, new Day(2004, 9, 23));
		assertNotEquals(d, new Day(2005, 8, 23));
		assertNotEquals(d, new Day(2005, 9, 22));
		assertTrue(!d.equals("hallo"));
		assertTrue(!d.equals(Integer.valueOf(22)));
		
		assertEquals(new Day(2005, 2, 22), new Day(df.parse("2005-02-22 00:00:00.000")));
		assertEquals(new Day(2005, 2, 22), new Day(df.parse("2005-02-22 23:59:59.999")));

		assertEquals(new Day(2005, 2, 23), new Day(2005,  2, 22).add(1));
		assertEquals(new Day(2005, 3,  1), new Day(2005,  2, 28).add(1));
		assertEquals(new Day(2006, 1,  1), new Day(2005, 12, 31).add(1));
	}
	
	static final void assertEquals(final Day expected, final Day actual)
	{
		assertEquals((Object)expected, (Object)actual);
		assertEquals((Object)actual, (Object)expected);
		assertEquals(expected.hashCode(), actual.hashCode());
	}
	
	static final void assertNotEquals(final Day expected, final Day actual)
	{
		assertTrue(!expected.equals(actual));
		assertTrue(!actual.equals(expected));
		assertTrue(expected.hashCode()!=actual.hashCode());
	}
	
}
