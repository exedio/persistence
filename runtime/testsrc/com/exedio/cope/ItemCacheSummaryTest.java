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

import static com.exedio.cope.ItemCacheStatisticsTest.c;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

@Deprecated // OK: testing deprecated API
public class ItemCacheSummaryTest
{
	@Test void testIt()
	{
		final ItemCacheInfo i1 = new ItemCacheInfo(null, 21, c(31), c(41), c(51), c(71), c(111), c(121), 141, c(151), c(161));
		final ItemCacheInfo i2 = new ItemCacheInfo(null, 23, c(33), c(43), c(53), c(73), c(113), c(123), 143, c(153), c(163));
		final ItemCacheInfo i0 = new ItemCacheInfo(null,  0, c( 0), c( 0), c( 0), c( 0), c(  0), c(  0),   0, c(  0), c(  0));
		assertEquals(111+121, i1.getInvalidationsOrdered());
		assertEquals(113+123, i2.getInvalidationsOrdered());
		assertEquals(0,       i0.getInvalidationsOrdered());
		assertEquals(null, i1.getLastReplacementRun());
		assertEquals(null, i2.getLastReplacementRun());
		assertEquals(null, i0.getLastReplacementRun());

		assertEquals(0, i1.getAgeAverageMillis());
		assertEquals(0, i2.getAgeAverageMillis());
		final com.exedio.cope.misc.ItemCacheSummary ms = new com.exedio.cope.misc.ItemCacheSummary(new ItemCacheInfo[]{i1, i2, i0});
		assertEquals(  0, ms.getLimit());
		assertEquals( 44, ms.getLevel());
		assertEquals( 64, ms.getHits());
		assertEquals( 84, ms.getMisses());
		assertEquals(104, ms.getConcurrentLoads());
		assertEquals(  0, ms.getReplacementRuns());
		assertEquals(144, ms.getReplacementsL());
		assertEquals(null, ms.getLastReplacementRun());
		assertEquals( -1, ms.getAgeMinimumMillis());
		assertEquals( -1, ms.getAgeAverageMillis());
		assertEquals( -1, ms.getAgeMaximumMillis());
		assertEquals(224+244, ms.getInvalidationsOrdered());
		assertEquals(244, ms.getInvalidationsDone());
		assertEquals(284, ms.getStampsSize());
		assertEquals(304, ms.getStampsHits());
		assertEquals(324, ms.getStampsPurged());
	}

	@Test void testNull()
	{
		try
		{
			new com.exedio.cope.misc.ItemCacheSummary(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("Cannot read the array length because \"<local20>\" is null", e.getMessage());
		}
	}

	@Test void testNullElement()
	{
		final ItemCacheInfo i1 = new ItemCacheInfo(null, 21, c(31), c(41), c(51), c(71), c(111), c(121), 131, c(141), c(151));
		try
		{
			new com.exedio.cope.misc.ItemCacheSummary(new ItemCacheInfo[]{i1, null});
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("Cannot invoke \"com.exedio.cope.ItemCacheInfo.getLevel()\" because \"info\" is null", e.getMessage());
		}
	}

	@Test void testEmpty()
	{
		final com.exedio.cope.misc.ItemCacheSummary ms = new com.exedio.cope.misc.ItemCacheSummary(new ItemCacheInfo[]{});
		assertEquals(0, ms.getLimit());
		assertEquals(0, ms.getLevel());
		assertEquals(0, ms.getHits());
		assertEquals(0, ms.getMisses());
		assertEquals(0, ms.getConcurrentLoads());
		assertEquals(0, ms.getReplacementRuns());
		assertEquals(0, ms.getReplacementsL());
		assertEquals(null, ms.getLastReplacementRun());
		assertEquals(-1, ms.getAgeMinimumMillis());
		assertEquals(-1, ms.getAgeAverageMillis());
		assertEquals(-1, ms.getAgeMaximumMillis());
		assertEquals(0, ms.getInvalidationsOrdered());
		assertEquals(0, ms.getInvalidationsDone());
		assertEquals(0, ms.getStampsSize());
		assertEquals(0, ms.getStampsHits());
		assertEquals(0, ms.getStampsPurged());
	}
}
