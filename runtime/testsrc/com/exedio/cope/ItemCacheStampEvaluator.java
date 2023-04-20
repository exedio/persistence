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

import static com.exedio.cope.misc.TimeUtil.toMillies;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Test expenses for System.nanoTime against atomic sequences
 * <p>
 * Typical result:
 * System.currentTimeMillis()     267 ms
 * System.nanoTime()              195 ms
 * AtomicLong.   incrementAndGet() 95 ms
 * AtomicInteger.incrementAndGet() 95 ms
 * AtomicLong.   getAndIncrement() 95 ms
 * AtomicInteger.getAndIncrement() 94 ms
 */
@Disabled
@SuppressWarnings("NewClassNamingConvention")
public class ItemCacheStampEvaluator
{
	private static final int ITERATIONS = 10000000;

	@Test void testIt()
	{
		{
			final long start = System.nanoTime();
			for(int i=0; i<ITERATIONS; i++)
			{
				System.currentTimeMillis();
			}
			System.out.println("System.currentTimeMillis()     " + toMillies(System.nanoTime(), start) + " ms ");
		}
		{
			final long start = System.nanoTime();
			for(int i=0; i<ITERATIONS; i++)
			{
				System.nanoTime();
			}
			System.out.println("System.nanoTime()              " + toMillies(System.nanoTime(), start) + " ms ");
		}
		{
			final AtomicLong x = new AtomicLong();
			final long start = System.nanoTime();
			for(int i=0; i<ITERATIONS; i++)
			{
				x.incrementAndGet();
			}
			System.out.println("AtomicLong.   incrementAndGet() " + toMillies(System.nanoTime(), start) + " ms ");
		}
		{
			final AtomicInteger x = new AtomicInteger();
			final long start = System.nanoTime();
			for(int i=0; i<ITERATIONS; i++)
			{
				x.incrementAndGet();
			}
			System.out.println("AtomicInteger.incrementAndGet() " + toMillies(System.nanoTime(), start) + " ms ");
		}
		{
			final AtomicLong x = new AtomicLong();
			final long start = System.nanoTime();
			for(int i=0; i<ITERATIONS; i++)
			{
				x.getAndIncrement();
			}
			System.out.println("AtomicLong.   getAndIncrement() " + toMillies(System.nanoTime(), start) + " ms ");
		}
		{
			final AtomicInteger x = new AtomicInteger();
			final long start = System.nanoTime();
			for(int i=0; i<ITERATIONS; i++)
			{
				x.getAndIncrement();
			}
			System.out.println("AtomicInteger.getAndIncrement() " + toMillies(System.nanoTime(), start) + " ms ");
		}
	}
}
