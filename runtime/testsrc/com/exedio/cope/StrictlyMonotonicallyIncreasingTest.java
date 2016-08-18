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

import static com.exedio.cope.IntegerColumn.strictlyMonotonicallyIncreasing;
import static java.lang.Integer.MAX_VALUE;
import static java.lang.Integer.MIN_VALUE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import org.junit.Test;

public class StrictlyMonotonicallyIncreasingTest
{
	@Test public void testIt()
	{
		assertGood(1);
		assertGood(1, 2);
		assertGood(1, 2, 3, 4, 5);
		assertFail("1>=1", 1, 1);
		assertFail("5>=5", 1, 2, 3, 4, 5, 5);
		assertFail("1>=0", 1, 0);
		assertFail("5>=4", 1, 2, 3, 4, 5, 4);
	}

	@Test public void testMin()
	{
		assertGood(
				MAX_VALUE-2, MAX_VALUE-1, MAX_VALUE);
		assertFail("2147483647>=2147483647",
				MAX_VALUE-2, MAX_VALUE-1, MAX_VALUE, MAX_VALUE);
	}

	@Test public void testMax()
	{
		assertFail("-2147483648>=-2147483648",
				MIN_VALUE, MIN_VALUE+1, MIN_VALUE+2); // TODO should be ok
		assertFail("-2147483648>=-2147483648", // TODO wrong message
				MIN_VALUE, MIN_VALUE+1, MIN_VALUE+2, MIN_VALUE+2);
	}

	@Test public void testEmpty()
	{
		assertGood();
	}

	private static void assertGood(final int... allowedValues)
	{
		assertSame(allowedValues, strictlyMonotonicallyIncreasing(allowedValues));
	}

	private static void assertFail(final String message, final int... allowedValues)
	{
		try
		{
			strictlyMonotonicallyIncreasing(allowedValues);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals(message, e.getMessage());
		}
	}
}
