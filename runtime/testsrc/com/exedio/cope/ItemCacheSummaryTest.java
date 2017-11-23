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
import static org.junit.Assert.fail;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.jupiter.api.Test;

@Deprecated // OK: testing deprecated API
public class ItemCacheSummaryTest
{
	@Test public void testIt()
	{
		final ItemCacheInfo i1 = new ItemCacheInfo(null, 21, 31, 41, 51, 71, 111, 121, 141, 151, 161);
		final ItemCacheInfo i2 = new ItemCacheInfo(null, 23, 33, 43, 53, 73, 113, 123, 143, 153, 163);
		final ItemCacheInfo i0 = new ItemCacheInfo(null,  0,  0,  0,  0,  0,   0,   0,   0,   0,   0);
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
		assertEquals(224, ms.getInvalidationsOrdered());
		assertEquals(244, ms.getInvalidationsDone());
		assertEquals(284, ms.getStampsSize());
		assertEquals(304, ms.getStampsHits());
		assertEquals(324, ms.getStampsPurged());
	}

	@SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_NO_SIDE_EFFECT")
	@Test public void testNull()
	{
		try
		{
			new com.exedio.cope.misc.ItemCacheSummary(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals(null, e.getMessage());
		}
	}

	@SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_NO_SIDE_EFFECT")
	@Test public void testNullElement()
	{
		final ItemCacheInfo i1 = new ItemCacheInfo(null, 21, 31, 41, 51, 71, 111, 121, 131, 141, 151);
		try
		{
			new com.exedio.cope.misc.ItemCacheSummary(new ItemCacheInfo[]{i1, null});
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals(null, e.getMessage());
		}
	}

	@Test public void testEmpty()
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
