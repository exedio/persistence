/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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

import static com.exedio.cope.pattern.Price.valueOf;
import static com.exedio.cope.pattern.Price.splitProportionately;

import java.util.Arrays;

import com.exedio.cope.junit.CopeAssert;
import com.exedio.cope.pattern.Price;

public class PriceProportionatelyTest extends CopeAssert
{
	public void testEquals()
	{
		final Price[] weights = values(0.11, 0.11, 0.11);
		assertIt(values(0.03, 0.03, 0.03), valueOf(0.09), weights);
		assertIt(values(0.03, 0.03, 0.02), valueOf(0.08), weights);
		assertIt(values(0.03, 0.02, 0.02), valueOf(0.07), weights);
		assertIt(values(0.02, 0.02, 0.02), valueOf(0.06), weights);
		assertIt(values(0.02, 0.02, 0.01), valueOf(0.05), weights);
		assertIt(values(0.02, 0.01, 0.01), valueOf(0.04), weights);
		assertIt(values(0.01, 0.01, 0.01), valueOf(0.03), weights);
		assertIt(values(0.01, 0.01, 0.00), valueOf(0.02), weights);
		assertIt(values(0.01, 0.00, 0.00), valueOf(0.01), weights);
		assertIt(values(0.00, 0.00, 0.00), valueOf(0.00), weights);
	}

	public void testUnEquals()
	{
		final Price[] weights = values(0.11, 0.22, 0.11);
		assertIt(values(0.03, 0.06, 0.03), valueOf(0.12), weights);
		assertIt(values(0.03, 0.06, 0.02), valueOf(0.11), weights);
		assertIt(values(0.03, 0.05, 0.02), valueOf(0.10), weights);
		assertIt(values(0.03, 0.04, 0.02), valueOf(0.09), weights);
		assertIt(values(0.02, 0.04, 0.02), valueOf(0.08), weights);
		assertIt(values(0.02, 0.04, 0.01), valueOf(0.07), weights);
		assertIt(values(0.02, 0.03, 0.01), valueOf(0.06), weights);
		assertIt(values(0.02, 0.02, 0.01), valueOf(0.05), weights);
		assertIt(values(0.01, 0.02, 0.01), valueOf(0.04), weights);
		assertIt(values(0.01, 0.02, 0.00), valueOf(0.03), weights);
		assertIt(values(0.01, 0.01, 0.00), valueOf(0.02), weights);
		assertIt(values(0.01, 0.00, 0.00), valueOf(0.01), weights);
		assertIt(values(0.00, 0.00, 0.00), valueOf(0.00), weights);
	}

	public void testElse()
	{
		assertIt(values(0.01, 0.02, 0.00), valueOf(0.03), values(0.11, 0.22, 0.11));
		assertIt(values(0.02, 0.07), valueOf(0.09), values(0.11, 0.44));

		assertIt(values(0.09), valueOf(0.09), values(0.11));
		assertIt(values(0.99), valueOf(0.99), values(1.11));
	}

	public void testZeroWeight()
	{
		assertIt(values(3.33, 3.33, 3.33), valueOf(9.99), values(0.01, 0.01, 0.01));
		assertIt(values(3.33, 0.00, 3.33), valueOf(6.66), values(0.01, 0.00, 0.01));
		assertIt(values(3.34, 0.00, 3.33), valueOf(6.67), values(0.01, 0.00, 0.01));
		assertIt(values(3.34, 0.00, 3.34), valueOf(6.68), values(0.01, 0.00, 0.01));

		assertIt(values(9.99, 0.00, 0.00), valueOf(9.99), values(0.01, 0.00, 0.00));
		assertIt(values(0.00, 9.99, 0.00), valueOf(9.99), values(0.00, 0.01, 0.00));
		assertIt(values(0.00, 0.00, 9.99), valueOf(9.99), values(0.00, 0.00, 0.01));
		assertIt(values(3.33, 3.33, 3.33), valueOf(9.99), values(0.00, 0.00, 0.00));

		assertIt(values(3.34, 3.33, 3.33), valueOf(10.00), values(0.00, 0.00, 0.00));
		assertIt(values(3.34, 3.34, 3.33), valueOf(10.01), values(0.00, 0.00, 0.00));
		assertIt(values(3.34, 3.34, 3.34), valueOf(10.02), values(0.00, 0.00, 0.00));

		assertIt(values(0.01, 0.02, 0.02), valueOf(0.05), values(0.00, 0.03, 0.03)); // TODO should be 0.00, 0.03, 0.02
	}

	private static void assertIt(final Price[] expected, final Price actualTotal, final Price[] actualWeights)
	{
		{
			Price expectedSum = Price.ZERO;
			for(final Price p : expected)
			{
				assertTrue(Price.ZERO.lessThanOrEqual(p));
				expectedSum = expectedSum.add(p);
			}
			assertEquals(expectedSum, actualTotal);
		}

		assertEqualsArray(expected, splitProportionately(actualTotal, actualWeights));

		final Price[] expectedNegative = new Price[expected.length];
		for(int i = 0; i<expectedNegative.length; i++)
			expectedNegative[i] = expected[i].negative();
		assertEqualsArray(expectedNegative, splitProportionately(actualTotal.negative(), actualWeights));
	}



	public void testSpecial()
	{
		try
		{
			splitProportionately(valueOf(9.99), values());
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("weights must not be empty", e.getMessage());
		}
		try
		{
			splitProportionately(null, values(0.01));
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals(null, e.getMessage());
		}
		try
		{
			splitProportionately(valueOf(9), null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals(null, e.getMessage());
		}
		try
		{
			splitProportionately(valueOf(9), values(0.01, 0.01, -0.01));
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("-0.01", e.getMessage());
		}
	}

	private static final Price[] values(final double... values)
	{
		final Price[] result = new Price[values.length];
		for(int i = 0; i<values.length; i++)
			result[i] = Price.valueOf(values[i]);
		return result;
	}

	private static void assertEqualsArray(final Price[] expected, final Price[] actual)
	{
		if(!Arrays.equals(expected, actual))
			fail("expected " + Arrays.toString(expected) + " but was " + Arrays.toString(actual));
	}
}
