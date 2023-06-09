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

import static com.exedio.cope.tojunit.Assert.assertFails;

import org.junit.jupiter.api.Test;

public class LongFieldTest
{
	@Test void testIllegalRange()
	{
		assertIllegalRange(0,  0,  "maximum must be greater than minimum, but was 0 and 0");
		assertIllegalRange(22, 22, "maximum must be greater than minimum, but was 22 and 22");
		assertIllegalRange(22, 21, "maximum must be greater than minimum, but was 21 and 22");
		assertIllegalRange(MAX, MIN, "maximum must be greater than minimum, but was " + MIN + " and " + MAX);
		assertIllegalRange(MIN, MIN, "maximum must be greater than minimum, but was " + MIN + " and " + MIN);
		assertIllegalRange(MAX, MAX, "maximum must be greater than minimum, but was " + MAX + " and " + MAX);
	}

	private static void assertIllegalRange(final long minimum, final long maximum, final String message)
	{
		final LongField f = new LongField().optional();
		assertFails(
				() -> f.range(minimum, maximum),
				IllegalArgumentException.class,
				message);
	}

	private static final long MIN = Long.MIN_VALUE;
	private static final long MAX = Long.MAX_VALUE;
}
