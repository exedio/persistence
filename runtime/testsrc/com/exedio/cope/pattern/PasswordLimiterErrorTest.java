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

import static com.exedio.cope.pattern.PasswordLimiterItem.password;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static java.time.Duration.ofMillis;
import static java.time.Duration.ofNanos;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;
import org.junit.jupiter.api.Test;

public class PasswordLimiterErrorTest
{
	@SuppressWarnings("deprecation") // OK: testing deprecated API
	@Test void testPasswordNull()
	{
		assertFails(
				() -> new PasswordLimiter(null, 0, 0),
				NullPointerException.class,
				"password");
	}
	@Test void testPasswordNullDuration()
	{
		assertFails(
				() -> new PasswordLimiter(null, Duration.ZERO, 0),
				NullPointerException.class,
				"password");
	}

	@SuppressWarnings("deprecation") // OK: testing deprecated API
	@Test void testPeriodZero()
	{
		assertFails(
				() -> new PasswordLimiter(password, 0, 0),
				IllegalArgumentException.class,
				"period must be at least PT0.001S, but was PT0S");
	}
	@Test void testPeriodZeroDuration()
	{
		assertFails(
				() -> new PasswordLimiter(password, ofMillis(1).minus(ofNanos(1)), 0),
				IllegalArgumentException.class,
				"period must be at least PT0.001S, but was PT0.000999999S");
	}
	@Test void testPeriodNull()
	{
		assertFails(
				() -> new PasswordLimiter(password, null, 0),
				NullPointerException.class,
				"period");
	}

	@SuppressWarnings("deprecation") // OK: testing deprecated API
	@Test void testLimitZero()
	{
		assertFails(
				() -> new PasswordLimiter(password, 1, 0),
				IllegalArgumentException.class,
				"limit must be greater zero, but was 0");
	}
	@Test void testLimitZeroDuration()
	{
		assertFails(
				() -> new PasswordLimiter(password, ofMillis(1), 0),
				IllegalArgumentException.class,
				"limit must be greater zero, but was 0");
	}

	@SuppressWarnings("deprecation") // OK: testing deprecated API
	@Test void testMinimum()
	{
		final PasswordLimiter l = new PasswordLimiter(password, 1, 1);
		assertEquals(password, l.getPassword());
		assertEquals(1, l.getPeriod());
		assertEquals(ofMillis(1), l.getPeriodDuration());
		assertEquals(1, l.getLimit());
	}
	@Test void testMinimumDuration()
	{
		final PasswordLimiter l = new PasswordLimiter(password, ofMillis(1), 1);
		assertEquals(password, l.getPassword());
		assertEquals(1, l.getPeriod());
		assertEquals(ofMillis(1), l.getPeriodDuration());
		assertEquals(1, l.getLimit());
	}
}
