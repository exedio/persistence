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

import java.security.NoSuchAlgorithmException;

import com.exedio.cope.junit.CopeAssert;

public class MessageDigestAlgorithmTest extends CopeAssert
{
	public void testIt()
	{
		try
		{
			new MessageDigestAlgorithm("NIXUS", 1);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertTrue(e.getMessage(), e.getMessage().startsWith("no such MessageDigest NIXUS, choose one of: "));
			assertEquals(NoSuchAlgorithmException.class, e.getCause().getClass());
		}
		try
		{
			new MessageDigestAlgorithm("SHA-512", -1, 0);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("saltLength must be at least zero, but was -1", e.getMessage());
		}
		try
		{
			new MessageDigestAlgorithm("SHA-512", 0, 0);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("iterations must be at least one, but was 0", e.getMessage());
		}
	}
	
	public void testSalted()
	{
		final MessageDigestAlgorithm a =
			new MessageDigestAlgorithm("SHA-512", 8, 100);
		assertEquals("SHA512", a.name());
		assertEquals(72, a.length());
		assertEquals(100, a.getIterations());
	}
	
	public void testUnsalted()
	{
		final MessageDigestAlgorithm a =
			new MessageDigestAlgorithm("SHA-512", 0, 100);
		assertEquals("SHA512", a.name());
		assertEquals(64, a.length());
		assertEquals(100, a.getIterations());
	}
	
	public void testNoniterated()
	{
		final MessageDigestAlgorithm a =
			new MessageDigestAlgorithm("SHA-512", 8, 1);
		assertEquals("SHA512", a.name());
		assertEquals(72, a.length());
		assertEquals(1, a.getIterations());
	}
	
	public void testUnsaltedNoniterated()
	{
		final MessageDigestAlgorithm a =
			new MessageDigestAlgorithm("SHA-512", 0, 1);
		assertEquals("SHA512", a.name());
		assertEquals(64, a.length());
		assertEquals(1, a.getIterations());
	}
}
