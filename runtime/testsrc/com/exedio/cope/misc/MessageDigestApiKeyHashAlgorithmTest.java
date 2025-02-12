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

package com.exedio.cope.misc;

import static com.exedio.cope.misc.MessageDigestApiKeyHashAlgorithm.create;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.exedio.cope.util.IllegalAlgorithmException;
import com.exedio.cope.util.MessageDigestFactory;
import org.junit.jupiter.api.Test;

public class MessageDigestApiKeyHashAlgorithmTest
{
	@Test void testDigestWrong()
	{
		assertThrows(
				IllegalAlgorithmException.class,
				() -> create(5, "NIXUS"));
		assertThrows(
				IllegalAlgorithmException.class,
				() -> create(5, US_ASCII, "NIXUS"));
	}

	@Test void testMinimumPlainTextLengthWrong()
	{
		assertFails(
				() -> create(49, "SHA-512"),
				IllegalArgumentException.class,
				"minimumPlainTextLength must be at least 50, but was 49");
		assertFails(
				() -> create(49, US_ASCII, "SHA-512"),
				IllegalArgumentException.class,
				"minimumPlainTextLength must be at least 50, but was 49");
		final MessageDigestFactory sha512 = new MessageDigestFactory("SHA-512");
		assertFails(
				() -> create(49, sha512),
				IllegalArgumentException.class,
				"minimumPlainTextLength must be at least 50, but was 49");
		assertFails(
				() -> create(49, US_ASCII, sha512),
				IllegalArgumentException.class,
				"minimumPlainTextLength must be at least 50, but was 49");
	}

	@Test void testFactory()
	{
		final MessageDigestFactory md5 = new MessageDigestFactory("MD5");
		final MessageDigestApiKeyHashAlgorithm a1 = create(66, md5);
		assertEquals("MD5-UTF8", a1.getID());
		assertEquals("MD5-UTF8(66)", a1.getDescription());
		final MessageDigestApiKeyHashAlgorithm a2 = create(77, US_ASCII, md5);
		assertEquals("MD5-USASCII", a2.getID());
		assertEquals("MD5-USASCII(77)", a2.getDescription());
	}

	@Test void testDefault()
	{
		final MessageDigestApiKeyHashAlgorithm a = create(50, "SHA-512");
		assertEquals("SHA512-UTF8", a.getID());
		assertEquals("SHA512-UTF8(50)", a.getDescription());

		assertFails(
				() -> a.hash(null),
				NullPointerException.class,
				"plainText");
		assertFails(
				() -> a.check(null, "myHash"),
				NullPointerException.class,
				"plainText");
		assertFails(
				() -> a.check("myPlain", null),
				NullPointerException.class,
				"hash");
		assertDigest(a,
				"knolloknolloknolloknolloknolloknolloknolloknolloknolloknolloknollo" +
				"knolloknolloknolloknolloknolloknolloknolloknolloknolloknolloknollo" +
				"knolloknolloknolloknolloknollo",
				"cb7e2023fc2372d06a876501b2f8f6c5347ac23fd8e92c02b5efd8ce60e03240a60ca82d760849103455dfd26cd1c28695e5f51b41001f71496e8126d168eb84");
		assertDigest(a,
				"012345678901234567890123456789012345678901234567890",
				"537a077fe5241c622d5704f56f58b251026710651095ab5f64de2afcbdd1a226267c927b3d7a6af1c3639901793cd5a20e97167d64db7898feaea81d1224b469");
		assertFails(
				() -> a.hash("0123456789012345678901234567890123456789012345678"),
				IllegalArgumentException.class,
				"plainText must have at least 50 characters, but had 49");
		assertFails(
				() -> a.check("0123456789012345678901234567890123456789012345678", ""),
				IllegalArgumentException.class,
				"plainText must have at least 50 characters, but had 49");

		// reference example from https://de.wikipedia.org/wiki/SHA-512
		assertDigest(a,
				"Franz jagt im komplett verwahrlosten Taxi quer durch Bayern",
				"af9ed2de700433b803240a552b41b5a472a6ef3fe1431a722b2063c75e9f07451f67a28e37d09cde769424c96aea6f8971389db9e1993d6c565c3c71b855723c");
		assertDigest(a,
				"Frank jagt im komplett verwahrlosten Taxi quer durch Bayern",
				"90b30ef9902ae4c4c691d2d78c2f8fa0aa785afbc5545286b310f68e91dd2299c84a2484f0419fc5eaa7de598940799e1091c4948926ae1c9488dddae180bb80");
	}

	@Test void testMinimumPlainTextLength()
	{
		final MessageDigestApiKeyHashAlgorithm a = create(51, US_ASCII, "SHA-512");
		assertEquals("SHA512-USASCII", a.getID());
		assertEquals("SHA512-USASCII(51)", a.getDescription());
		assertDigest(a,
				"knolloknolloknolloknolloknolloknolloknolloknolloknolloknolloknollo" +
				"knolloknolloknolloknolloknolloknolloknolloknolloknolloknolloknollo" +
				"knolloknolloknolloknolloknollo",
				"cb7e2023fc2372d06a876501b2f8f6c5347ac23fd8e92c02b5efd8ce60e03240a60ca82d760849103455dfd26cd1c28695e5f51b41001f71496e8126d168eb84");
		assertDigest(a,
				"012345678901234567890123456789012345678901234567890",
				"537a077fe5241c622d5704f56f58b251026710651095ab5f64de2afcbdd1a226267c927b3d7a6af1c3639901793cd5a20e97167d64db7898feaea81d1224b469");
		assertFails(
				() -> a.hash("01234567890123456789012345678901234567890123456789"),
				IllegalArgumentException.class,
				"plainText must have at least 51 characters, but had 50");
		assertFails(
				() -> a.check("01234567890123456789012345678901234567890123456789", ""),
				IllegalArgumentException.class,
				"plainText must have at least 51 characters, but had 50");
	}

	private static void assertDigest(
			final MessageDigestApiKeyHashAlgorithm algorithm,
			final String plainText,
			final String expectedHash)
	{
		assertEquals(expectedHash, algorithm.hash(plainText));

		assertTrue(algorithm.check(plainText, expectedHash));
		assertFalse(algorithm.check(plainText+"x", expectedHash));
	}
}
