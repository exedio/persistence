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
import static java.time.Duration.ofMillis;
import static java.time.Duration.ofNanos;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.pattern.PasswordRecovery.Config;
import java.time.Duration;
import org.junit.jupiter.api.Test;

public class PasswordRecoveryConfigTest
{
	@SuppressWarnings("deprecation") // OK: testing deprecated API
	@Test void testConfigExpiryZero()
	{
		assertFails(() ->
			new Config(0),
			IllegalArgumentException.class,
			"expiry must be at least PT0.001S, but was PT0S");
	}

	@SuppressWarnings("deprecation") // OK: testing deprecated API
	@Test void testConfigExpiryZeroWithReuse()
	{
		assertFails(() ->
			new Config(0, -1),
			IllegalArgumentException.class,
			"expiry must be at least PT0.001S, but was PT0S");
	}
	@Test void testConfigExpiryZeroWithReuseDuration()
	{
		assertFails(() ->
			new Config(ofMillis(1).minus(ofNanos(1)), ofMillis(-1)),
			IllegalArgumentException.class,
			"expiry must be at least PT0.001S, but was PT0.000999999S");
	}

	@SuppressWarnings("deprecation") // OK: testing deprecated API
	@Test void testConfigReuseNegative()
	{
		assertFails(() ->
			new Config(1, -1),
			IllegalArgumentException.class,
			"reuse must be at least PT0S, but was PT-0.001S");
	}
	@Test void testConfigReuseNegativeDuration()
	{
		assertFails(() ->
			new Config(ofMillis(1), ofNanos(-1)),
			IllegalArgumentException.class,
			"reuse must be at least PT0S, but was PT-0.000000001S");
	}

	@SuppressWarnings("deprecation") // OK: testing deprecated API
	@Test void testConfigReuseGreaterExpiry()
	{
		assertFails(() ->
			new Config(1, 2),
			IllegalArgumentException.class,
			"reuse must not be be greater expiry, but was PT0.002S and PT0.001S");
	}
	@Test void testConfigReuseGreaterExpiryDuration()
	{
		assertFails(() ->
			new Config(ofMillis(1), ofMillis(1).plus(ofNanos(1))),
			IllegalArgumentException.class,
			"reuse must not be be greater expiry, but was PT0.001000001S and PT0.001S");
	}

	@SuppressWarnings("deprecation") // OK: testing deprecated API
	@Test void testConfigDefaultReuse()
	{
		final Config c = new Config(1);
		assertEquals(1, c.getExpiryMillis());
		assertEquals(1, c.getReuseMillis());
		assertEquals(ofMillis(1), c.getExpiry());
		assertEquals(ofMillis(1), c.getReuse());
	}

	@SuppressWarnings("deprecation") // OK: testing deprecated API
	@Test void testConfigDefaultReuseBig()
	{
		final Config c = new Config(10001);
		assertEquals(10001, c.getExpiryMillis());
		assertEquals(10000, c.getReuseMillis());
		assertEquals(ofMillis(10001), c.getExpiry());
		assertEquals(ofMillis(10000), c.getReuse());
	}

	@SuppressWarnings("deprecation") // OK: testing deprecated API
	@Test void testConfigMinimal()
	{
		final Config c = new Config(20, 10);
		assertEquals(20, c.getExpiryMillis());
		assertEquals(10, c.getReuseMillis());
		assertEquals(ofMillis(20), c.getExpiry());
		assertEquals(ofMillis(10), c.getReuse());
	}
	@Test void testConfigMinimalDuration()
	{
		final Config c = new Config(ofMillis(20), ofMillis(10));
		assertEquals(ofMillis(20), c.getExpiry());
		assertEquals(ofMillis(10), c.getReuse());
	}

	@SuppressWarnings("deprecation") // OK: testing deprecated API
	@Test void testConfigNoReuse()
	{
		final Config c = new Config(20, 0);
		assertEquals(20, c.getExpiryMillis());
		assertEquals(0, c.getReuseMillis());
		assertEquals(ofMillis(20), c.getExpiry());
		assertEquals(ofMillis( 0), c.getReuse());
	}
	@Test void testConfigNoReuseDuration()
	{
		final Config c = new Config(ofMillis(20), Duration.ZERO);
		assertEquals(ofMillis(20), c.getExpiry());
		assertEquals(ofMillis( 0), c.getReuse());
	}

	@SuppressWarnings("deprecation") // OK: testing deprecated API
	@Test void testConfigSameReuse()
	{
		final Config c = new Config(20, 20);
		assertEquals(20, c.getExpiryMillis());
		assertEquals(20, c.getReuseMillis());
		assertEquals(ofMillis(20), c.getExpiry());
		assertEquals(ofMillis(20), c.getReuse());
	}
	@Test void testConfigSameReuseDuration()
	{
		final Config c = new Config(ofMillis(20), ofMillis(20));
		assertEquals(ofMillis(20), c.getExpiry());
		assertEquals(ofMillis(20), c.getReuse());
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
		final Hash hash = new Hash(new ConstructorHashAlgorithm("---"));
		assertFails(() ->
			new PasswordRecovery(hash, null),
			NullPointerException.class,
			"random");
	}
}
