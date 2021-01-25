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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;
import org.junit.jupiter.api.Test;

public class LongFieldRandomTest
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

		private static AssertionError failure()
		{
			return new AssertionError();
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

	@Test void testUnsupportedMin()
	{
		final LongField s = new LongField().min(1);
		try
		{
			s.defaultToRandom(r);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("defaultToRandom supports minimum of -9223372036854775808 or 0 only, but was 1", e.getMessage());
		}
	}

	@Test void testUnsupportedMax()
	{
		final LongField s = new LongField().max(1);
		try
		{
			s.defaultToRandom(r);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("defaultToRandom supports maximum of 9223372036854775807 only, but was 1", e.getMessage());
		}
	}

	@Test void testUnsupportedMinMax()
	{
		final LongField s = new LongField().defaultToRandom(r);
		try
		{
			s.min(1);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("defaultToRandom supports minimum of -9223372036854775808 or 0 only, but was 1", e.getMessage());
		}
		try
		{
			s.max(1);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("defaultToRandom supports maximum of 9223372036854775807 only, but was 1", e.getMessage());
		}
	}

	@Test void testFull()
	{
		final LongField s = new LongField().defaultToRandom(r);
		assertIt(s, MIN_VALUE  , MIN_VALUE  );
		assertIt(s, MIN_VALUE+1, MIN_VALUE+1);
		assertIt(s, MIN_VALUE+2, MIN_VALUE+2);
		assertIt(s, MIN_VALUE+3, MIN_VALUE+3);
		assertIt(s,          -3,          -3);
		assertIt(s,          -2,          -2);
		assertIt(s,          -1,          -1);
		assertIt(s,           0,           0);
		assertIt(s,           1,           1);
		assertIt(s,           2,           2);
		assertIt(s,           3,           3);
		assertIt(s, MAX_VALUE-3, MAX_VALUE-3);
		assertIt(s, MAX_VALUE-2, MAX_VALUE-2);
		assertIt(s, MAX_VALUE-1, MAX_VALUE-1);
		assertIt(s, MAX_VALUE  , MAX_VALUE  );
	}

	@Test void testPositive()
	{
		final LongField s = new LongField().min(0).defaultToRandom(r);
		assertIt(s,           0, MIN_VALUE  );
		assertIt(s,           1, MIN_VALUE+1);
		assertIt(s,           2, MIN_VALUE+2);
		assertIt(s,           3, MIN_VALUE+3);
		assertIt(s, MAX_VALUE-2,          -3);
		assertIt(s, MAX_VALUE-1,          -2);
		assertIt(s, MAX_VALUE  ,          -1);
		assertIt(s,           0,           0);
		assertIt(s,           1,           1);
		assertIt(s,           2,           2);
		assertIt(s,           3,           3);
		assertIt(s, MAX_VALUE-3, MAX_VALUE-3);
		assertIt(s, MAX_VALUE-2, MAX_VALUE-2);
		assertIt(s, MAX_VALUE-1, MAX_VALUE-1);
		assertIt(s, MAX_VALUE  , MAX_VALUE  );
	}

	private void assertIt(final LongField l, final long expected, final long actual)
	{
		final DefaultSupplier<Long> s = l.defaultS;
		r.setNextLong(actual);
		final Long actualGenerated = s.generate(null);
		assertEquals(Long.valueOf(expected), actualGenerated);
		l.check(actualGenerated);
	}
}
