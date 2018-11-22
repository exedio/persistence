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

import static com.exedio.cope.tojunit.Assert.assertFails;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.pattern.PasswordRecovery.Config;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.jupiter.api.Test;

@SuppressFBWarnings("NP_NULL_PARAM_DEREF_NONVIRTUAL")
public class PasswordRecoveryConfigTest
{
	@Test void testConfigExpiryZero()
	{
		assertFails(() ->
			new Config(0),
			IllegalArgumentException.class,
			"expiryMillis must be greater zero, but was 0");
	}

	@Test void testConfigExpiryZeroWithReuse()
	{
		assertFails(() ->
			new Config(0, -1),
			IllegalArgumentException.class,
			"expiryMillis must be greater zero, but was 0");
	}

	@Test void testConfigReuseNegative()
	{
		assertFails(() ->
			new Config(1, -1),
			IllegalArgumentException.class,
			"reuseMillis must not be negative, but was -1");
	}

	@Test void testConfigReuseGreaterExpiry()
	{
		assertFails(() ->
			new Config(1, 2),
			IllegalArgumentException.class,
			"reuseMillis must not be be greater expiryMillis, but was 2 and 1");
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
		final Config c = new Config(20, 10);
		assertEquals(20, c.getExpiryMillis());
		assertEquals(10, c.getReuseMillis());
	}

	@Test void testConfigNoReuse()
	{
		final Config c = new Config(20, 0);
		assertEquals(20, c.getExpiryMillis());
		assertEquals(0, c.getReuseMillis());
	}

	@Test void testConfigSameReuse()
	{
		final Config c = new Config(20, 20);
		assertEquals(20, c.getExpiryMillis());
		assertEquals(20, c.getReuseMillis());
	}

	@Test void testPasswordRecoveryHashNull()
	{
		assertFails(() ->
			new PasswordRecovery(null),
			NullPointerException.class,
			"password");
	}

	@Test void testPasswordRecoveryHashNullWithRandom()
	{
		assertFails(() ->
			new PasswordRecovery(null, null),
			NullPointerException.class,
			"password");
	}

	@Test void testPasswordRecoveryRandomNull()
	{
		final Hash hash = new Hash(MessageDigestHash.algorithm(20));
		assertFails(() ->
			new PasswordRecovery(hash, null),
			NullPointerException.class,
			"random");
	}
}
