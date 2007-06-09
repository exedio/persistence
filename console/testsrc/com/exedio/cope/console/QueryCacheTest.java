/*
 * Copyright (C) 2004-2007  exedio GmbH (www.exedio.com)
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

import com.exedio.cope.util.CacheQueryInfo;

public class QueryCacheTest extends TestCase
{
	public void testIt()
	{
		try
		{
			new QueryCacheCop.Content(null);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals(null, e.getMessage());
		}
		{
			final CacheQueryInfo[] histogram = new CacheQueryInfo[]{};
			final QueryCacheCop.Content content = new QueryCacheCop.Content(histogram);
			assertSame(histogram, content.histogram);
			assertEquals(-1, content.avgKeyLength);
			assertEquals(-1, content.minKeyLength);
			assertEquals(-1, content.maxKeyLength);
			assertEquals(-1, content.avgResultSize);
			assertEquals(-1, content.minResultSize);
			assertEquals(-1, content.maxResultSize);
			assertEquals(new int[]{}, content.resultSizes);
		}
		{
			final CacheQueryInfo[] histogram = new CacheQueryInfo[]{
					new CacheQueryInfo("query1",   3, 101),
					new CacheQueryInfo("query2xx", 7, 103),
			};
			final QueryCacheCop.Content content = new QueryCacheCop.Content(histogram);
			assertSame(histogram, content.histogram);
			assertEquals(7, content.avgKeyLength);
			assertEquals(6, content.minKeyLength);
			assertEquals(8, content.maxKeyLength);
			assertEquals(5, content.avgResultSize);
			assertEquals(3, content.minResultSize);
			assertEquals(7, content.maxResultSize);
			assertEquals(new int[]{0, 0, 0, 1, 0}, content.resultSizes);
		}
	}
	
	private static final void assertEquals(final int[] expected, final int[] actual)
	{
		if(!Arrays.equals(expected, actual))
			fail("expected " + Arrays.toString(expected) + ", but was " + Arrays.toString(actual));
	}
}
