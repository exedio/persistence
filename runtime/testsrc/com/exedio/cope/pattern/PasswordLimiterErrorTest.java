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
import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.time.Duration;
import org.junit.jupiter.api.Test;

@SuppressFBWarnings("NP_NULL_PARAM_DEREF_NONVIRTUAL")
public class PasswordLimiterErrorTest
{
	@Test void testPasswordNull()
	{
		assertFails(
				() -> new PasswordLimiter(null, 0, 0),
				NullPointerException.class,
				"password");
	}

	@Test void testPeriodZero()
	{
		assertFails(
				() -> new PasswordLimiter(password, 0, 0),
				IllegalArgumentException.class,
				"period must be greater zero, but was 0");
	}

	@Test void testLimitZero()
	{
		assertFails(
				() -> new PasswordLimiter(password, 1, 0),
				IllegalArgumentException.class,
				"limit must be greater zero, but was 0");
	}

	@Test void testMinimum()
	{
		final PasswordLimiter l = new PasswordLimiter(password, 1, 1);
		assertEquals(password, l.getPassword());
		assertEquals(1, l.getPeriod());
		assertEquals(1, l.getLimit());
	}
	@Test void testMinimumInterface()
	{
		final PasswordLimiter l = new PasswordLimiter((HashInterface)password, 1, 1);
		assertEquals(password, l.getPassword());
		assertEquals(1, l.getPeriod());
		assertEquals(1, l.getLimit());
	}
}
