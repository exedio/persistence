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

import static com.exedio.cope.DayItem.TYPE;
import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static com.exedio.cope.tojunit.Assert.list;
import static org.junit.Assert.assertEquals;

import com.exedio.cope.util.Day;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

public class QuerySearchSizeCacheLimitTest extends TestWithEnvironment
{
	public QuerySearchSizeCacheLimitTest()
	{
		super(DayFieldTest.MODEL);
	}

	static final Day d1 = new Day(2006, 02, 19);
	static final Day d2 = new Day(2006, 02, 20);
	static final Day d3 = new Day(2006, 02, 21);

	private boolean cacheEnabled;
	private Query<?> q;
	private DayItem i1, i2, i3, i4, i5;

	@Before public final void setUp()
	{
		cacheEnabled = model.getConnectProperties().getQueryCacheLimit()>0;
		q = TYPE.newQuery();
		i1 = new DayItem(d1);
		i2 = new DayItem(d2);
		i3 = new DayItem(d3);
		i4 = new DayItem(d1);
		i5 = new DayItem(d2);

		// allows testing influence of query cache
		restartTransaction();
	}

	@Test public void testIt()
	{
		q.setSearchSizeCacheLimit(5);
		assertEquals(5, q.getSearchSizeCacheLimit());
		assertEquals(list(), modelHistogram());
		assertEqualsUnmodifiable(list(i1, i2, i3, i4, i5), q.search());
		assertEquals(cacheEnabled ? list(queryHistogram(0)) : list(), modelHistogram());

		q.setSearchSizeCacheLimit(4);
		assertEquals(4, q.getSearchSizeCacheLimit());
		assertEquals(cacheEnabled ? list(queryHistogram(0)) : list(), modelHistogram());
		assertEqualsUnmodifiable(list(i1, i2, i3, i4, i5), q.search());
		assertEquals(cacheEnabled ? list(queryHistogram(1)) : list(), modelHistogram());
	}

	@Test public void testExceed()
	{
		q.setSearchSizeCacheLimit(4);
		assertEquals(4, q.getSearchSizeCacheLimit());
		assertEquals(list(), modelHistogram());
		assertEqualsUnmodifiable(list(i1, i2, i3, i4, i5), q.search());
		assertEquals(list(), modelHistogram());

		q.setSearchSizeCacheLimit(5);
		assertEquals(5, q.getSearchSizeCacheLimit());
		assertEquals(list(), modelHistogram());
		assertEqualsUnmodifiable(list(i1, i2, i3, i4, i5), q.search());
		assertEquals(cacheEnabled ? list(queryHistogram(0)) : list(), modelHistogram());
	}

	private static QueryCacheHistogram queryHistogram(final int hits)
	{
		return new QueryCacheHistogram("select this from DayItem", 5, hits);
	}

	private List<QueryCacheHistogram> modelHistogram()
	{
		return Arrays.asList(model.getQueryCacheHistogram());
	}
}
