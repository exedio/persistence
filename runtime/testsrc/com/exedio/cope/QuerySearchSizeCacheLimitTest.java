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
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.util.Day;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class QuerySearchSizeCacheLimitTest extends TestWithEnvironment
{
	public QuerySearchSizeCacheLimitTest()
	{
		super(DayFieldTest.MODEL);
	}

	static final Day d1 = new Day(2006, 2, 19);
	static final Day d2 = new Day(2006, 2, 20);
	static final Day d3 = new Day(2006, 2, 21);

	private boolean cacheEnabled;
	private Query<?> q;
	private DayItem i1, i2, i3, i4, i5;

	@BeforeEach final void setUp()
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

	@Test void testIt()
	{
		q.setSearchSizeCacheLimit(5);
		assertEquals(5, q.getSearchSizeCacheLimit());
		assertEquals(asList(), modelHist());
		assertEqualsUnmodifiable(asList(i1, i2, i3, i4, i5), q.search());
		assertEquals(asListCE(searchHist(0)), modelHist());
		assertEquals(5, q.total());
		assertEquals(asListCE(totalHist(0), searchHist(0)), modelHist());
		assertEquals(true, q.exists());
		assertEquals(asListCE(existsHist(0), totalHist(0), searchHist(0)), modelHist());

		q.setSearchSizeCacheLimit(4);
		assertEquals(4, q.getSearchSizeCacheLimit());
		assertEquals(asListCE(existsHist(0), totalHist(0), searchHist(0)), modelHist());
		assertEqualsUnmodifiable(asList(i1, i2, i3, i4, i5), q.search());
		assertEquals(asListCE(searchHist(1), existsHist(0), totalHist(0)), modelHist());
		assertEquals(5, q.total());
		assertEquals(asListCE(totalHist(1), searchHist(1), existsHist(0)), modelHist());
		assertEquals(true, q.exists());
		assertEquals(asListCE(existsHist(1), totalHist(1), searchHist(1)), modelHist());
	}

	@Test void testExceed()
	{
		q.setSearchSizeCacheLimit(4);
		assertEquals(4, q.getSearchSizeCacheLimit());
		assertEquals(asList(), modelHist());
		assertEqualsUnmodifiable(asList(i1, i2, i3, i4, i5), q.search());
		assertEquals(asList(), modelHist());
		assertEquals(5, q.total());
		assertEquals(asListCE(totalHist(0)), modelHist());
		assertEquals(true, q.exists());
		assertEquals(asListCE(existsHist(0), totalHist(0)), modelHist());

		q.setSearchSizeCacheLimit(5);
		assertEquals(5, q.getSearchSizeCacheLimit());
		assertEquals(asListCE(existsHist(0), totalHist(0)), modelHist());
		assertEqualsUnmodifiable(asList(i1, i2, i3, i4, i5), q.search());
		assertEquals(asListCE(searchHist(0), existsHist(0), totalHist(0)), modelHist());
		assertEquals(5, q.total());
		assertEquals(asListCE(totalHist(1), searchHist(0), existsHist(0)), modelHist());
		assertEquals(true, q.exists());
		assertEquals(asListCE(existsHist(1), totalHist(1), searchHist(0)), modelHist());
	}

	@Test void testTransactionDisabledGet()
	{
		final Transaction tx = model.currentTransaction();
		assertEquals(false, tx.isQueryCacheDisabled());

		assertEquals(asList(), modelHist());
		assertEqualsUnmodifiable(asList(i1, i2, i3, i4, i5), q.search());
		assertEquals(asListCE(searchHist(0)), modelHist());
		assertEquals(5, q.total());
		assertEquals(asListCE(totalHist(0), searchHist(0)), modelHist());
		assertEquals(true, q.exists());
		assertEquals(asListCE(existsHist(0), totalHist(0), searchHist(0)), modelHist());

		tx.setQueryCacheDisabled(true);
		assertEquals(true, tx.isQueryCacheDisabled());
		assertEquals(asListCE(existsHist(0), totalHist(0), searchHist(0)), modelHist());
		assertEqualsUnmodifiable(asList(i1, i2, i3, i4, i5), q.search());
		assertEquals(asListCE(searchHist(1), existsHist(0), totalHist(0)), modelHist());
		assertEquals(5, q.total());
		assertEquals(asListCE(totalHist(1), searchHist(1), existsHist(0)), modelHist());
		assertEquals(true, q.exists());
		assertEquals(asListCE(existsHist(1), totalHist(1), searchHist(1)), modelHist());
	}

	@Test void testTransactionDisabledPut()
	{
		final Transaction tx = model.currentTransaction();
		assertEquals(false, tx.isQueryCacheDisabled());

		tx.setQueryCacheDisabled(true);
		assertEquals(true, tx.isQueryCacheDisabled());
		assertEquals(asList(), modelHist());
		assertEqualsUnmodifiable(asList(i1, i2, i3, i4, i5), q.search());
		assertEquals(asList(), modelHist());
		assertEquals(5, q.total());
		assertEquals(asList(), modelHist());
		assertEquals(true, q.exists());
		assertEquals(asList(), modelHist());

		tx.setQueryCacheDisabled(false);
		assertEquals(false, tx.isQueryCacheDisabled());
		assertEquals(asList(), modelHist());
		assertEqualsUnmodifiable(asList(i1, i2, i3, i4, i5), q.search());
		assertEquals(asListCE(searchHist(0)), modelHist());
		assertEquals(5, q.total());
		assertEquals(asListCE(totalHist(0), searchHist(0)), modelHist());
		assertEquals(true, q.exists());
		assertEquals(asListCE(existsHist(0), totalHist(0), searchHist(0)), modelHist());
	}

	private static QueryCacheHistogram searchHist(final int hits)
	{
		return new QueryCacheHistogram("select this from DayItem", 5, hits);
	}

	private static QueryCacheHistogram totalHist(final int hits)
	{
		return new QueryCacheHistogram("select count(*) from DayItem", 1, hits);
	}

	private static QueryCacheHistogram existsHist(final int hits)
	{
		return new QueryCacheHistogram("select exists( select count(*) from DayItem )", 1, hits);
	}

	private List<QueryCacheHistogram> modelHist()
	{
		return asList(model.getQueryCacheHistogram());
	}

	private List<QueryCacheHistogram> asListCE(final QueryCacheHistogram... histogram)
	{
		return cacheEnabled ? asList(histogram) : asList();
	}
}
