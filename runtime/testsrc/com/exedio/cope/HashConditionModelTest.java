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

import static com.exedio.cope.HashConditionTest.Algorithm;
import static com.exedio.cope.HashConditionTest.MyItem.hash;
import static com.exedio.cope.RuntimeTester.assertFieldsCovered;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.util.Arrays.asList;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.tojunit.EqualsAssert;
import com.exedio.cope.util.Hex;
import com.exedio.cope.util.MessageDigestUtil;
import java.security.MessageDigest;
import org.junit.jupiter.api.Test;

public class HashConditionModelTest
{
	@Test void testFieldsCovered()
	{
		final DataField data = new DataField();
		assertFieldsCovered(asList(hash, data), hash.hashMatchesIfSupported("ALGO", data));
	}

	@Test void identity()
	{
		final DataField data = new DataField();
		final Condition c = hash.hashMatchesIfSupported("ALGO", data);
		EqualsAssert.assertEqualsAndHash(c,
				hash.hashMatchesIfSupported("ALGO", data));
		EqualsAssert.assertNotEqualsAndHash(c,
				hash.hashMatchesIfSupported("ALGOx", data),
				hash.hashMatchesIfSupported("ALGO", new DataField()),
				hash.hashDoesNotMatchIfSupported("ALGO", data)
		);
	}
	@Test void identityNot()
	{
		final DataField data = new DataField();
		final Condition c = hash.hashDoesNotMatchIfSupported("ALGO", data);
		EqualsAssert.assertEqualsAndHash(c,
				hash.hashDoesNotMatchIfSupported("ALGO", data));
		EqualsAssert.assertNotEqualsAndHash(c,
				hash.hashDoesNotMatchIfSupported("ALGOx", data),
				hash.hashDoesNotMatchIfSupported("ALGO", new DataField()),
				hash.hashMatchesIfSupported("ALGO", data)
		);
	}
	@Test void algorithmNull()
	{
		try
		{
			hash.hashMatchesIfSupported(null, null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("algorithm", e.getMessage());
		}
	}
	@Test void algorithmNullNot()
	{
		try
		{
			hash.hashDoesNotMatchIfSupported(null, null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("algorithm", e.getMessage());
		}
	}
	@Test void algorithmEmpty()
	{
		try
		{
			hash.hashMatchesIfSupported("", null);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("algorithm must not be empty", e.getMessage());
		}
	}
	@Test void algorithmEmptyNot()
	{
		try
		{
			hash.hashDoesNotMatchIfSupported("", null);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("algorithm must not be empty", e.getMessage());
		}
	}
	@Test void dataNull()
	{
		try
		{
			hash.hashMatchesIfSupported("ALGO", null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("data", e.getMessage());
		}
	}
	@Test void dataNullNot()
	{
		try
		{
			hash.hashDoesNotMatchIfSupported("ALGO", null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("data", e.getMessage());
		}
	}
	@Test void algorithmConsistentWithMessageDigest()
	{
		for(final Algorithm a : Algorithm.values())
		{
			final MessageDigest digest = MessageDigestUtil.getInstance(a.code);
			digest.update(new byte[]{});
			assertEquals(a.empty, Hex.encodeLower(digest.digest()), a.code);

			digest.reset();
			digest.update("Franz jagt im komplett verwahrlosten Taxi quer durch Bayern".getBytes(US_ASCII));
			assertEquals(a.franz, Hex.encodeLower(digest.digest()), a.code);

			assertEquals(a.code, digest.getAlgorithm());
			assertEquals(a.empty.length(), digest.getDigestLength()*2, a.code);
			assertEquals(a.franz.length(), digest.getDigestLength()*2, a.code);
		}
	}
}
