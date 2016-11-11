package com.exedio.cope;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ItemCacheStatisticsTest
{
	@Test public void summarizedFields()
	{
		final ItemCacheInfo i1 = new ItemCacheInfo(null, 21, 31, 41, 51, 71, 111, 121, 141, 151, 161);
		final ItemCacheInfo i2 = new ItemCacheInfo(null, 23, 33, 43, 53, 73, 113, 123, 143, 153, 163);
		final ItemCacheInfo i0 = new ItemCacheInfo(null,  0,  0,  0,  0,  0,   0,   0,   0,   0,   0);

		final ItemCacheStatistics ms = new ItemCacheStatistics(0, 0, new ItemCacheInfo[]{i1, i2, i0});
		assertEquals( 64, ms.getSummarizedHits());
		assertEquals( 84, ms.getSummarizedMisses());
		assertEquals(104, ms.getSummarizedConcurrentLoads());
		assertEquals(144, ms.getSummarizedReplacements());
		assertEquals(224, ms.getSummarizedInvalidationsOrdered());
		assertEquals(244, ms.getSummarizedInvalidationsDone());
		assertEquals(284, ms.getSummarizedStampsSize());
		assertEquals(304, ms.getSummarizedStampsHits());
		assertEquals(324, ms.getSummarizedStampsPurged());
	}

	@Test public void summarizedFieldsEmpty()
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
}
