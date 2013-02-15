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

package com.exedio.cope;

import static java.lang.Double.NaN;
import static java.lang.Long.MAX_VALUE;
import static java.lang.Long.MIN_VALUE;

import java.util.Random;

import junit.framework.AssertionFailedError;

import com.exedio.cope.junit.CopeAssert;

public class LongFieldRandomTest extends CopeAssert
{
	static class RandomX extends Random
	{
		private static final long serialVersionUID = 1l;

		@Override public synchronized void setSeed(final long seed)
		{
			// is called by constructor
		}

		@Override public void nextBytes(final byte[] bytes){throw failure();}
		@Override public int nextInt(){throw failure();}
		@Override public int nextInt(final int n){throw failure();}
		@Override public long nextLong(){throw failure();}
		@Override public boolean nextBoolean(){throw failure();}
		@Override public float nextFloat(){throw failure();}
		@Override public synchronized double nextGaussian(){throw failure();}

		private static AssertionFailedError failure()
		{
			return new AssertionFailedError();
		}

		private double nextDouble = NaN;

		@Override
		public double nextDouble()
		{
			assertFalse(Double.isNaN(nextDouble));
			final double result = nextDouble;
			nextDouble = Double.NaN;
			return result;
		}

		void setNextDouble(final double nextDouble)
		{
			assertFalse(Double.isNaN(nextDouble));
			assertTrue(Double.isNaN(this.nextDouble));
			this.nextDouble = nextDouble;
		}
	}

	private final RandomX r = new RandomX();

	public void testPositive()
	{
		final LongField s = new LongField().range(100, 109).defaultToRandom(r);
		assertIt(s, 100, 0.0);
		assertIt(s, 100, 0.099999999999);
		assertIt(s, 101, 0.1);
		assertIt(s, 101, 0.199999999999);
		assertIt(s, 109, 0.9);
		assertIt(s, 109, 0.999999999999);
	}

	public void testNegative()
	{
		final LongField s = new LongField().range(-110, -101).defaultToRandom(r);
		assertIt(s, -110, 0.0);
		assertIt(s, -110, 0.09999999999999);
		assertIt(s, -109, 0.1);
		assertIt(s, -109, 0.19999999999999);
		assertIt(s, -101, 0.9);
		assertIt(s, -101, 0.99999999999999);
	}

	public void testSpan()
	{
		final LongField s = new LongField().range(-5, 4).defaultToRandom(r);
		assertIt(s, -5, 0.0);
		assertIt(s, -5, 0.0999999999999999);
		assertIt(s, -4, 0.1);
		assertIt(s, -4, 0.1999999999999999);
		assertIt(s,  3, 0.8);
		assertIt(s,  3, 0.8999999999999999);
		assertIt(s,  4, 0.9);
		assertIt(s,  4, 0.9999999999999999);
	}

	public void testFull()
	{
		// TODO does not use complete number space
		final LongField s = new LongField().defaultToRandom(r);
		assertIt(s, MIN_VALUE, 0.0);
		assertIt(s, MIN_VALUE, 0.00000000000000001);
		assertIt(s, MIN_VALUE, 0.000000000000000055);
		assertIt(s, MAX_VALUE-4095, 0.9999999999999998);
		assertIt(s, MAX_VALUE-4095, 0.99999999999999983);
		assertIt(s, MAX_VALUE-4095, 0.999999999999999833);
		assertIt(s, MAX_VALUE-2047, 0.999999999999999834);
		assertIt(s, MAX_VALUE-2047, 0.9999999999999999);
	}

	public void testFullAlmostLow()
	{
		// TODO does not use complete number space
		final LongField s = new LongField().min(MIN_VALUE+1).defaultToRandom(r);
		assertIt(s, MIN_VALUE, 0.0);
		assertIt(s, MIN_VALUE, 0.00000000000000001);
		assertIt(s, MIN_VALUE, 0.000000000000000055);
		assertIt(s, MAX_VALUE-4095, 0.9999999999999998);
		assertIt(s, MAX_VALUE-4095, 0.99999999999999983);
		assertIt(s, MAX_VALUE-4095, 0.999999999999999833);
		assertIt(s, MAX_VALUE-2047, 0.999999999999999834);
		assertIt(s, MAX_VALUE-2047, 0.9999999999999999);
	}

	private void assertIt(final LongField l, final long expected, final double actual)
	{
		final DefaultSource<Long> s = l.defaultSource;
		r.setNextDouble(actual);
		final Long actualGenerated = s.generate(MIN_VALUE);
		assertEquals(Long.valueOf(expected), actualGenerated);
		l.check(actualGenerated);
	}
}
