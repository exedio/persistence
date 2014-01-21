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

import com.exedio.cope.junit.CopeAssert;
import com.exedio.cope.misc.ItemCacheSummary;
import java.util.Date;

public class ItemCacheSummaryTest extends CopeAssert
{
	private static final Date D1 = new Date(123456789);
	private static final Date D2 = new Date(123456989);

	public void testIt()
	{
		final ItemCacheInfo i1 = new ItemCacheInfo(null, 11, 21, 31, 41, 51, 61, 71, D1, 81, 91, 101, 111, 121, 141, 151, 161);
		final ItemCacheInfo i2 = new ItemCacheInfo(null, 13, 23, 33, 43, 53, 63, 73, D2, 83, 93, 103, 113, 123, 143, 153, 163);
		final ItemCacheInfo i0 = new ItemCacheInfo(null,  0,  0,  0,  0,  0,  0,  0, null, 0, 0,   0,   0,   0,   0,   0,   0);
		assertEquals(D1, i1.getLastReplacementRun());
		assertEquals(D2, i2.getLastReplacementRun());
		assertEquals(null, i0.getLastReplacementRun());

		assertEquals(3, i1.getAgeAverageMillis());
		assertEquals(3, i2.getAgeAverageMillis());

		final ItemCacheSummary ms = new ItemCacheSummary(new ItemCacheInfo[]{i1, i2, i0});
		assertEquals( 24, ms.getLimit());
		assertEquals( 44, ms.getLevel());
		assertEquals( 64, ms.getHits());
		assertEquals( 84, ms.getMisses());
		assertEquals(104, ms.getConcurrentLoads());
		assertEquals(124, ms.getReplacementRuns());
		assertEquals(144, ms.getReplacements());
		assertEquals( D2, ms.getLastReplacementRun());
		assertEquals( 91, ms.getAgeMinimumMillis());
		assertEquals(  3, ms.getAgeAverageMillis());
		assertEquals(103, ms.getAgeMaximumMillis());
		assertEquals(224, ms.getInvalidationsOrdered());
		assertEquals(244, ms.getInvalidationsDone());
		assertEquals(284, ms.getStampsSize());
		assertEquals(304, ms.getStampsHits());
		assertEquals(324, ms.getStampsPurged());
	}

	public void testNull()
	{
		try
		{
			new ItemCacheSummary(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals(null, e.getMessage());
		}
	}

	public void testNullElement()
	{
		final ItemCacheInfo i1 = new ItemCacheInfo(null, 11, 21, 31, 41, 51, 61, 71, D1, 81, 91, 101, 111, 121, 131, 141, 151);
		try
		{
			new ItemCacheSummary(new ItemCacheInfo[]{i1, null});
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals(null, e.getMessage());
		}
	}

	public void testEmpty()
	{
		final ItemCacheSummary ms = new ItemCacheSummary(new ItemCacheInfo[]{});
		assertEquals(0, ms.getLimit());
		assertEquals(0, ms.getLevel());
		assertEquals(0, ms.getHits());
		assertEquals(0, ms.getMisses());
		assertEquals(0, ms.getConcurrentLoads());
		assertEquals(0, ms.getReplacementRuns());
		assertEquals(0, ms.getReplacements());
		assertEquals(null, ms.getLastReplacementRun());
		assertEquals(0, ms.getAgeMinimumMillis());
		assertEquals(0, ms.getAgeAverageMillis());
		assertEquals(0, ms.getAgeMaximumMillis());
		assertEquals(0, ms.getInvalidationsOrdered());
		assertEquals(0, ms.getInvalidationsDone());
		assertEquals(0, ms.getStampsSize());
		assertEquals(0, ms.getStampsHits());
		assertEquals(0, ms.getStampsPurged());
	}
}
