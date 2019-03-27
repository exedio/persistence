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

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.security.SecureRandom;
import org.junit.jupiter.api.Test;

public class NestedHashAlgorithmTest
{
	@Test void testNormal()
	{
		final HashAlgorithm a = NestedHashAlgorithm.create(
				MessageDigestHashAlgorithm.create(UTF_8, "MD5",     0, null,               100),
				MessageDigestHashAlgorithm.create(UTF_8, "SHA-512", 8, new SecureRandom(), 200));

		assertEquals("MD5i100-SHA512s8i200", a.getID());
		assertEquals("MD5i100-SHA512s8i200", a.getDescription());

		final String hash = a.hash("1234");
		assertEquals(true,  a.check("1234", hash));
		assertEquals(false, a.check("12345", hash));
	}

	@Test void testMigration()
	{
		final HashAlgorithm legacy = MessageDigestHashAlgorithm.create(UTF_8, "MD5", 0, null, 100);

		final String legacyHash = legacy.hash("1234");
		assertEquals(true,  legacy.check("1234", legacyHash));
		assertEquals(false, legacy.check("12345", legacyHash));

		final HashAlgorithm neu = MessageDigestHashAlgorithm.create(UTF_8, "SHA-512", 8, new SecureRandom(), 200);
		final String newHash = neu.hash(legacyHash);
		final HashAlgorithm a = NestedHashAlgorithm.create(legacy, neu);
		assertEquals(true,  a.check("1234", newHash));
		assertEquals(false, a.check("12345", newHash));
	}

	@Test void testFail()
	{
		try
		{
			NestedHashAlgorithm.create(
					MessageDigestHashAlgorithm.create(UTF_8, "SHA-512", 8, new SecureRandom(), 200),
					MessageDigestHashAlgorithm.create(UTF_8, "MD5",     0, null,               100));
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("legacy algorithm must be deterministic (i.e. unsalted), but was SHA512s8i200", e.getMessage());
		}
		// TODO test nulls etc
	}
}
