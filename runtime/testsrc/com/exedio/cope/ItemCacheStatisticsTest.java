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

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.micrometer.core.instrument.Counter;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

public class ItemCacheStatisticsTest
{
	@Test void summarizedFields()
	{
		final ItemCacheInfo i1 = new ItemCacheInfo(null, 21, c(31), c(41), c(51), c(71), c(111), c(121), 141, c(151), c(161));
		final ItemCacheInfo i2 = new ItemCacheInfo(null, 23, c(33), c(43), c(53), c(73), c(113), c(123), 143, c(153), c(163));
		final ItemCacheInfo i0 = new ItemCacheInfo(null,  0, c( 0), c( 0), c( 0), c( 0), c(  0), c(  0),   0, c(  0), c(  0));
		assertEquals(111+121, i1.getInvalidationsOrdered());
		assertEquals(113+123, i2.getInvalidationsOrdered());
		assertEquals(0,       i0.getInvalidationsOrdered());

		final ItemCacheStatistics ms = new ItemCacheStatistics(0, 0, new ItemCacheInfo[]{i1, i2, i0});
		assertEquals( 64, ms.getSummarizedHits());
		assertEquals( 84, ms.getSummarizedMisses());
		assertEquals(104, ms.getSummarizedConcurrentLoads());
		assertEquals(144, ms.getSummarizedReplacements());
		assertEquals(224+244, ms.getSummarizedInvalidationsOrdered());
		assertEquals(244, ms.getSummarizedInvalidationsDone());
		assertEquals(284, ms.getSummarizedStampsSize());
		assertEquals(304, ms.getSummarizedStampsHits());
		assertEquals(324, ms.getSummarizedStampsPurged());
	}

	@Test void summarizedFieldsEmpty()
	{
		final ItemCacheStatistics ms = new ItemCacheStatistics(0, 0, new ItemCacheInfo[0]);
		assertEquals(0, ms.getSummarizedHits());
		assertEquals(0, ms.getSummarizedMisses());
		assertEquals(0, ms.getSummarizedConcurrentLoads());
		assertEquals(0, ms.getSummarizedReplacements());
		assertEquals(0, ms.getSummarizedInvalidationsOrdered());
		assertEquals(0, ms.getSummarizedInvalidationsDone());
		assertEquals(0, ms.getSummarizedStampsSize());
		assertEquals(0, ms.getSummarizedStampsHits());
		assertEquals(0, ms.getSummarizedStampsPurged());
	}

	static Counter c(final long count)
	{
		return new Counter()
		{
			@Override
			public void increment(final double amount)
			{
				throw new AssertionFailedError("" + amount);
			}

			@Override
			public double count()
			{
				return count;
			}

			@Override
			public Id getId()
			{
				throw new AssertionFailedError();
			}
		};
	}
}
