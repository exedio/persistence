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
		assertIllegalRange(0,  0,  "Redundant field with minimum==maximum (0) is probably a mistake. You may call method rangeEvenIfRedundant if you are sure this is ok.");
		assertIllegalRange(22, 22, "Redundant field with minimum==maximum (22) is probably a mistake. You may call method rangeEvenIfRedundant if you are sure this is ok.");
		assertIllegalRange(22, 21, "maximum must be at least minimum, but was 21 and 22");
		assertIllegalRange(MAX, MIN, "maximum must be at least minimum, but was " + MIN + " and " + MAX);
		assertIllegalRange(MIN, MIN, "Redundant field with minimum==maximum (" + MIN + ") is probably a mistake. You may call method rangeEvenIfRedundant if you are sure this is ok.");
		assertIllegalRange(MAX, MAX, "Redundant field with minimum==maximum (" + MAX + ") is probably a mistake. You may call method rangeEvenIfRedundant if you are sure this is ok.");
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
