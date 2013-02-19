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
		@Override public double nextDouble(){throw failure();}
		@Override public boolean nextBoolean(){throw failure();}
		@Override public float nextFloat(){throw failure();}
		@Override public synchronized double nextGaussian(){throw failure();}

		private static AssertionFailedError failure()
		{
			return new AssertionFailedError();
		}

		private boolean nextLongAvailable = false;
		private long nextLong = 0;

		@Override
		public long nextLong()
		{
			assertTrue(nextLongAvailable);
			nextLongAvailable = false;
			return nextLong;
		}

		void setNextLong(final long nextLong)
		{
			assertFalse(nextLongAvailable);
			this.nextLongAvailable = true;
			this.nextLong = nextLong;
		}
	}

	private final RandomX r = new RandomX();

	public void testUnsupportedMin()
	{
		final LongField s = new LongField().min(1);
		try
		{
			s.defaultToRandom(r);
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("defaultToRandom supports minimum of -9223372036854775808 or 0 only, but was 1", e.getMessage());
		}
	}

	public void testUnsupportedMax()
	{
		final LongField s = new LongField().max(1);
		try
		{
			s.defaultToRandom(r);
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("defaultToRandom supports maximum of 9223372036854775807 only, but was 1", e.getMessage());
		}
	}

	public void testUnsupportedMinMax()
	{
		final LongField s = new LongField().defaultToRandom(r);
		try
		{
			s.min(1);
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("defaultToRandom supports minimum of -9223372036854775808 or 0 only, but was 1", e.getMessage());
		}
		try
		{
			s.max(1);
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("defaultToRandom supports maximum of 9223372036854775807 only, but was 1", e.getMessage());
		}
	}

	public void testFull()
	{
		final LongField s = new LongField().defaultToRandom(r);
		assertIt(s, MIN_VALUE  , MIN_VALUE  );
		assertIt(s, MIN_VALUE+1, MIN_VALUE+1);
		assertIt(s, MIN_VALUE+2, MIN_VALUE+2);
		assertIt(s,          -2,          -2);
		assertIt(s,          -1,          -1);
		assertIt(s,           0,           0);
		assertIt(s,           1,           1);
		assertIt(s,           2,           2);
		assertIt(s, MAX_VALUE-2, MAX_VALUE-2);
		assertIt(s, MAX_VALUE-1, MAX_VALUE-1);
		assertIt(s, MAX_VALUE  , MAX_VALUE  );
	}

	public void testPositive()
	{
		final LongField s = new LongField().defaultToRandom(r).min(0);
		assertIt(s, MAX_VALUE  , MIN_VALUE  );
		assertIt(s, MAX_VALUE  , MIN_VALUE+1);
		assertIt(s, MAX_VALUE-1, MIN_VALUE+2);
		assertIt(s,           2,          -2);
		assertIt(s,           1,          -1);
		assertIt(s,           0,           0);
		assertIt(s,           1,           1);
		assertIt(s,           2,           2);
		assertIt(s, MAX_VALUE-2, MAX_VALUE-2);
		assertIt(s, MAX_VALUE-1, MAX_VALUE-1);
		assertIt(s, MAX_VALUE  , MAX_VALUE  );
	}

	private void assertIt(final LongField l, final long expected, final long actual)
	{
		final DefaultSource<Long> s = l.defaultSource;
		r.setNextLong(actual);
		final Long actualGenerated = s.generate(MIN_VALUE);
		assertEquals(Long.valueOf(expected), actualGenerated);
		l.check(actualGenerated);
	}
}
