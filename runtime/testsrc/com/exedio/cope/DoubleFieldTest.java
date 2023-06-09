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
import static java.lang.Double.NEGATIVE_INFINITY;
import static java.lang.Double.NaN;
import static java.lang.Double.POSITIVE_INFINITY;

import org.junit.jupiter.api.Test;

public class DoubleFieldTest
{
	@Test void testIllegalRangeInfinity()
	{
		assertIllegalRange(POSITIVE_INFINITY, 44.22, "minimum must not be infinite, but was Infinity");
		assertIllegalRange(44.22, POSITIVE_INFINITY, "maximum must not be infinite, but was Infinity");
		assertIllegalRange(NEGATIVE_INFINITY, 44.22, "minimum must not be infinite, but was -Infinity");
		assertIllegalRange(44.22, NEGATIVE_INFINITY, "maximum must not be infinite, but was -Infinity");
	}

	@Test void testIllegalRangeNaN()
	{
		assertIllegalRange(NaN, 44.22, "minimum must not be NaN, but was NaN");
		assertIllegalRange(44.22, NaN, "maximum must not be NaN, but was NaN");
	}

	@Test void testIllegalRange()
	{
		assertIllegalRange( 0.0,  0.0,  "Redundant field with minimum==maximum (0.0) is probably a mistake. You may call method rangeEvenIfRedundant if you are sure this is ok.");
		assertIllegalRange(22.2, 22.2, "Redundant field with minimum==maximum (22.2) is probably a mistake. You may call method rangeEvenIfRedundant if you are sure this is ok.");
		assertIllegalRange(22.2, 21.1, "maximum must be at least minimum, but was 21.1 and 22.2");
		assertIllegalRange(MAX, MIN, "maximum must be at least minimum, but was " + MIN + " and " + MAX);
		assertIllegalRange(MIN, MIN, "Redundant field with minimum==maximum (" + MIN + ") is probably a mistake. You may call method rangeEvenIfRedundant if you are sure this is ok.");
		assertIllegalRange(MAX, MAX, "Redundant field with minimum==maximum (" + MAX + ") is probably a mistake. You may call method rangeEvenIfRedundant if you are sure this is ok.");
	}

	private static void assertIllegalRange(final double minimum, final double maximum, final String message)
	{
		final DoubleField f = new DoubleField().optional();
		assertFails(
				() -> f.range(minimum, maximum),
				IllegalArgumentException.class,
				message);
	}

	private static final double MIN = -Double.MAX_VALUE;
	private static final double MAX = Double.MAX_VALUE;
}
