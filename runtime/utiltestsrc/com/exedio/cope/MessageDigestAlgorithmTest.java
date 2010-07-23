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

package com.exedio.cope;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import com.exedio.cope.junit.CopeAssert;
import com.exedio.cope.pattern.MessageDigestAlgorithm;
import com.exedio.cope.util.Hex;

public class MessageDigestAlgorithmTest extends CopeAssert
{
	public void testIt()
	{
		try
		{
			new MessageDigestAlgorithm("NIXUS", -1, 0);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertTrue(e.getMessage(), e.getMessage().startsWith("no such MessageDigest NIXUS, choose one of: "));
			assertEquals(NoSuchAlgorithmException.class, e.getCause().getClass());
		}
		try
		{
			new MessageDigestAlgorithm("SHA-512", -1, 0);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("saltLength must be at least zero, but was -1", e.getMessage());
		}
		try
		{
			new MessageDigestAlgorithm("SHA-512", 0, 0);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("iterations must be at least one, but was 0", e.getMessage());
		}
	}

	public void testSalted()
	{
		final MessageDigestAlgorithm a =
			new MessageDigestAlgorithm("SHA-512", 8, 5);
		assertEquals("SHA512s8i5", a.name());
		assertEquals(72, a.length());
		assertEquals(8, a.getSaltLength());
		assertEquals(5, a.getIterations());

		// TODO test hash and check
	}

	public void testSaltedMinimal()
	{
		final MessageDigestAlgorithm a =
			new MessageDigestAlgorithm("SHA-512", 1, 2);
		assertEquals("SHA512s1i2", a.name());
		assertEquals(65, a.length());
		assertEquals(1, a.getSaltLength());
		assertEquals(2, a.getIterations());

		// TODO test hash and check
	}

	public void testUnsalted()
	{
		final MessageDigestAlgorithm a =
			new MessageDigestAlgorithm("SHA-512", 0, 5);
		assertEquals("SHA512i5", a.name());
		assertEquals(64, a.length());
		assertEquals(0, a.getSaltLength());
		assertEquals(5, a.getIterations());
	}

	public void testNoniterated()
	{
		final MessageDigestAlgorithm a =
			new MessageDigestAlgorithm("SHA-512", 8, 1);
		assertEquals("SHA512s8", a.name());
		assertEquals(72, a.length());
		assertEquals(8, a.getSaltLength());
		assertEquals(1, a.getIterations());
	}

	public void testUnsaltedNoniterated()
	{
		final MessageDigestAlgorithm a =
			new MessageDigestAlgorithm("SHA-512", 0, 1);
		assertEquals("SHA512", a.name());
		assertEquals(64, a.length());
		assertEquals(0, a.getSaltLength());
		assertEquals(1, a.getIterations());

		try
		{
			a.hash(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals(null, e.getMessage());
		}
		assertDigest(a,
			"",
			"cf83e1357eefb8bdf1542850d66d8007d620e4050b5715dc83f4a921d36ce9ce47d0d13c5d85f2b0ff8318d2877eec2f63b931bd47417a81a538327af927da3e");
		assertDigest(a,
			"knollo",
			"1835861e09d3f89ee0f3f0e875366cad0d1877615ad322be2fff1135eb8e6f1ee1f55ce00edd17ae1c2ad89a96e676dfb106a0a8e78a7ea71e3ac373a5426af6");
		assertDigest(a,
			"knolloknolloknolloknolloknolloknolloknolloknolloknolloknolloknollo" +
			"knolloknolloknolloknolloknolloknolloknolloknolloknolloknolloknollo" +
			"knolloknolloknolloknolloknollo",
			"cb7e2023fc2372d06a876501b2f8f6c5347ac23fd8e92c02b5efd8ce60e03240a60ca82d760849103455dfd26cd1c28695e5f51b41001f71496e8126d168eb84");
		assertDigest(a,
			"Franz jagt im komplett verwahrlosten Taxi quer durch Bayern",
			"af9ed2de700433b803240a552b41b5a472a6ef3fe1431a722b2063c75e9f07451f67a28e37d09cde769424c96aea6f8971389db9e1993d6c565c3c71b855723c");
		assertDigest(a,
			"Frank jagt im komplett verwahrlosten Taxi quer durch Bayern",
			"90b30ef9902ae4c4c691d2d78c2f8fa0aa785afbc5545286b310f68e91dd2299c84a2484f0419fc5eaa7de598940799e1091c4948926ae1c9488dddae180bb80");
	}

	private static void assertDigest(
			final MessageDigestAlgorithm algorithm,
			final String plainText,
			final String expectedHash)
	{
		try
		{
			assertEquals(Hex.encodeLower(algorithm.hash(plainText.getBytes("utf8"))), expectedHash);
		}
		catch(final UnsupportedEncodingException e)
		{
			throw new RuntimeException(e);
		}
	}
}
