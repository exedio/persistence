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

import com.exedio.cope.junit.CopeAssert;

public class CharacterSetTest extends CopeAssert
{
	public void testIt()
	{
		try
		{
			new CharSet('Z', 'A');
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("inconsistent character set, character 'A' on position 1 is less character 'Z' on position 0", e.getMessage());
		}
		try
		{
			new CharSet('B', 'A');
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("inconsistent character set, character 'A' on position 1 is less character 'B' on position 0", e.getMessage());
		}
		try
		{
			new CharSet('A', 'C', 'B', 'A');
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("inconsistent character set, character 'B' on position 2 is less character 'C' on position 1", e.getMessage());
		}
		try
		{
			new CharSet('A', 'C', 'N', 'M');
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("inconsistent character set, character 'M' on position 3 is less character 'N' on position 2", e.getMessage());
		}
		{
			CharSet cs = new CharSet('C', 'C');
			assertRegexp("^[C]*$", cs);
			assertEquals(cs.toString(), "[C-C]", cs.toString());
			assertFalse(cs.contains('A'));
			assertTrue(cs.contains('C'));
			assertFalse(cs.contains('D'));
		}
		{
			CharSet cs = new CharSet('C', 'C', 'M', 'O', 'm', 'o');
			assertRegexp("^[C,M-O,m-o]*$", cs);
			assertEquals(cs.toString(), "[C-C,M-O,m-o]", cs.toString());
			assertFalse(cs.contains('A'));
			assertTrue(cs.contains('C'));
			assertFalse(cs.contains('D'));
			assertFalse(cs.contains('L'));
			assertTrue(cs.contains('M'));
			assertTrue(cs.contains('O'));
			assertFalse(cs.contains('Q'));
			assertFalse(cs.contains('l'));
			assertTrue(cs.contains('m'));
			assertTrue(cs.contains('o'));
			assertFalse(cs.contains('q'));
		}
		assertEquals(
				new CharSet('A', 'A'),
				new CharSet('A', 'A'));
		assertEquals(
				new CharSet('A', 'X', 'a', 'x'),
				new CharSet('A', 'X', 'a', 'x'));
		assertNotEquals(
				new CharSet('A', 'A'),
				new CharSet('A', 'A', 'a', 'x'));
		assertNotEquals(
				new CharSet('A', 'X', 'a', 'x'),
				new CharSet('A', 'X', 'a', 'y'));
		
		assertRegexp("^[-,a-z]*$", new CharSet('-', '-', 'a', 'z'));
		assertRegexp("^[-,(-)]*$", new CharSet('(', ')', '-', '-'));
		assertRegexp("^[-,(-),0-9]*$", new CharSet('(', ')', '-', '-', '0', '9'));
	}
	
	private static void assertEquals(final CharSet cs1, final CharSet cs2)
	{
		assertEquals((Object)cs1, (Object)cs2);
		assertEquals((Object)cs2, (Object)cs1);
		assertEquals(cs1.hashCode(), cs2.hashCode());
	}
	
	private static void assertNotEquals(final CharSet cs1, final CharSet cs2)
	{
		assertTrue(!cs1.equals(cs2));
		assertTrue(!cs2.equals(cs1));
		assertTrue(cs1.hashCode()!=cs2.hashCode());
	}
	
	private static void assertRegexp(final String regularExpression, final CharSet cs)
	{
		final String actual = cs.getRegularExpression();
		assertEquals(actual, regularExpression, actual);
	}
}
