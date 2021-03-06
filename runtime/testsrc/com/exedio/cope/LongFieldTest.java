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

import static java.lang.Long.MAX_VALUE;
import static java.lang.Long.MIN_VALUE;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class LongFieldTest
{
	@Test void testIllegalRange()
	{
		assertIllegalRange(0,  0,  "maximum must be greater than minimum, but was 0 and 0");
		assertIllegalRange(22, 22, "maximum must be greater than minimum, but was 22 and 22");
		assertIllegalRange(22, 21, "maximum must be greater than minimum, but was 21 and 22");
		assertIllegalRange(MAX_VALUE, MIN_VALUE, "maximum must be greater than minimum, but was " + MIN_VALUE + " and " + MAX_VALUE);
		assertIllegalRange(MIN_VALUE, MIN_VALUE, "maximum must be greater than minimum, but was " + MIN_VALUE + " and " + MIN_VALUE);
		assertIllegalRange(MAX_VALUE, MAX_VALUE, "maximum must be greater than minimum, but was " + MAX_VALUE + " and " + MAX_VALUE);
	}

	private static void assertIllegalRange(final long minimum, final long maximum, final String message)
	{
		final LongField f = new LongField().optional();
		try
		{
			f.range(minimum, maximum);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals(message, e.getMessage());
		}
	}
}
