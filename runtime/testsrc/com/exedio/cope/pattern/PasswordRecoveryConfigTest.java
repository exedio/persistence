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

import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.pattern.PasswordRecovery.Config;
import org.junit.jupiter.api.Test;

public class PasswordRecoveryConfigTest
{
	@Test void testConfigFailure()
	{
		try
		{
			new Config(0);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("expiryMillis must be greater zero, but was 0", e.getMessage());
		}
		try
		{
			new Config(0, -1);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("expiryMillis must be greater zero, but was 0", e.getMessage());
		}
		try
		{
			new Config(1, -1);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("reuseMillis must not be negative, but was -1", e.getMessage());
		}
		try
		{
			new Config(1, 2);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("reuseMillis must not be be greater expiryMillis, but was 2 and 1", e.getMessage());
		}
	}

	@Test void testConfigDefaultReuse()
	{
		final Config c = new Config(1);
		assertEquals(1, c.getExpiryMillis());
		assertEquals(1, c.getReuseMillis());
	}

	@Test void testConfigDefaultReuseBig()
	{
		final Config c = new Config(10001);
		assertEquals(10001, c.getExpiryMillis());
		assertEquals(10000, c.getReuseMillis());
	}

	@Test void testConfigMinimal()
	{
		final Config c2 = new Config(20, 10);
		assertEquals(20, c2.getExpiryMillis());
		assertEquals(10, c2.getReuseMillis());
	}

	@Test void testConfigNoReuse()
	{
		final Config c2 = new Config(20, 0);
		assertEquals(20, c2.getExpiryMillis());
		assertEquals(0, c2.getReuseMillis());
	}

	@Test void testPasswordRecovery()
	{
		try
		{
			new PasswordRecovery(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("password", e.getMessage());
		}
		try
		{
			new PasswordRecovery(null, null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("password", e.getMessage());
		}
		final Hash hash = new Hash(MessageDigestHash.algorithm(20));
		try
		{
			new PasswordRecovery(hash, null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("random", e.getMessage());
		}
	}
}
