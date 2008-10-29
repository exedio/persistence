/*
 * Copyright (C) 2004-2008  exedio GmbH (www.exedio.com)
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

package com.exedio.cope.console;

import java.util.Arrays;

import junit.framework.TestCase;

import com.exedio.cope.util.QueryCacheHistogram;

public class QueryCacheTest extends TestCase
{
	public void testIt()
	{
		try
		{
			new QueryCacheCop.Content(null, true);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals(null, e.getMessage());
		}
		{
			final QueryCacheHistogram[] histogram = new QueryCacheHistogram[]{};
			final QueryCacheCop.Content content = new QueryCacheCop.Content(histogram, false);
			assertSame(histogram, content.histogram);
			assertNull(content.histogramCondensed);
			assertEquals(-1, content.avgKeyLength);
			assertEquals(-1, content.minKeyLength);
			assertEquals(-1, content.maxKeyLength);
			assertEquals(-1, content.avgResultSize);
			assertEquals(-1, content.minResultSize);
			assertEquals(-1, content.maxResultSize);
			assertEquals(new int[]{}, content.resultSizes);
			assertEquals(-1, content.avgHits);
			assertEquals(-1, content.minHits);
			assertEquals(-1, content.maxHits);
		}
		{
			final QueryCacheHistogram[] histogram = new QueryCacheHistogram[]{
					new QueryCacheHistogram("query1",   3, 101),
					new QueryCacheHistogram("query2xx", 7, 103),
			};
			final QueryCacheCop.Content content = new QueryCacheCop.Content(histogram, false);
			assertSame(histogram, content.histogram);
			assertNull(content.histogramCondensed);
			assertEquals(7, content.avgKeyLength);
			assertEquals(6, content.minKeyLength);
			assertEquals(8, content.maxKeyLength);
			assertEquals(5, content.avgResultSize);
			assertEquals(3, content.minResultSize);
			assertEquals(7, content.maxResultSize);
			assertEquals(new int[]{0, 0, 0, 1, 0}, content.resultSizes);
			assertEquals(102, content.avgHits);
			assertEquals(101, content.minHits);
			assertEquals(103, content.maxHits);
		}
		{
			final QueryCacheHistogram[] histogram = new QueryCacheHistogram[]{
					new QueryCacheHistogram("query1 'hallo' and 'bello' order by",   11, 31),
					new QueryCacheHistogram("query1 'knollo' and 'knallo' order by", 13, 33),
					new QueryCacheHistogram("query2 nixus",                          14, 34),
					new QueryCacheHistogram("query3 'backus'",                       15, 35),
					new QueryCacheHistogram("'frontus' query4",                      16, 36),
					new QueryCacheHistogram("'' query5",                             17, 37),
					new QueryCacheHistogram("query6 ''",                             18, 38),
					new QueryCacheHistogram("query7 '' order by",                    19, 39),
			};
			final QueryCacheCop.Content content = new QueryCacheCop.Content(histogram, true);
			assertSame(histogram, content.histogram);
			final QueryCacheCop.Condense[] cn = content.histogramCondensed;
			assertInfo(cn[0], 2, 0, 24, 64, "query1 ? and ? order by");
			assertInfo(cn[1], 1, 2, 14, 34, "query2 nixus");
			assertInfo(cn[2], 1, 3, 15, 35, "query3 ?");
			assertInfo(cn[3], 1, 4, 16, 36, "? query4");
			assertInfo(cn[4], 1, 5, 17, 37, "? query5");
			assertInfo(cn[5], 1, 6, 18, 38, "query6 ?");
			assertInfo(cn[6], 1, 7, 19, 39, "query7 ? order by");
			assertEquals(7, cn.length);
		}
	}
	
	private static final void assertInfo(
			final QueryCacheCop.Condense actual,
			final int count,
			final int recentUsage,
			final int resultSize,
			final long hits,
			final String query)
	{
		assertEquals(count, actual.getCount());
		assertEquals(recentUsage, actual.getRecentUsage());
		assertEquals(resultSize, actual.getResultSize());
		assertEquals(hits, actual.getHits());
		assertEquals(query, actual.query);
	}
	
	private static final void assertEquals(final int[] expected, final int[] actual)
	{
		if(!Arrays.equals(expected, actual))
			fail("expected " + Arrays.toString(expected) + ", but was " + Arrays.toString(actual));
	}
}
