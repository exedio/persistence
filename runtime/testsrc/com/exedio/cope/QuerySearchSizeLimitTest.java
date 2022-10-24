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
import static com.exedio.cope.tojunit.Assert.assertFails;
import static com.exedio.cope.tojunit.Assert.list;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.Query.SearchSizeLimitExceededException;
import com.exedio.cope.util.Day;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class QuerySearchSizeLimitTest extends TestWithEnvironment
{
	public QuerySearchSizeLimitTest()
	{
		super(DayFieldTest.MODEL);
	}

	static final Day d1 = new Day(2006, 2, 19);
	static final Day d2 = new Day(2006, 2, 20);
	static final Day d3 = new Day(2006, 2, 21);

	DayItem i1, i2, i3, i4, i5;

	@BeforeEach final void setUp()
	{
		i1 = new DayItem(d1);
		i2 = new DayItem(d2);
		i3 = new DayItem(d3);
		i4 = new DayItem(d1);
		i5 = new DayItem(d2);
	}

	@Test void testIt()
	{
		// allows testing influence of query cache
		restartTransaction();
		final ConnectProperties props = model.getConnectProperties();
		final boolean cache = props.getQueryCacheLimit()>0;

		final Query<?> q = TYPE.newQuery();
		assertEquals(props.getQuerySearchSizeLimit(), q.getSearchSizeLimit());
		assertEquals(list(i1, i2, i3, i4, i5), q.search());


		// limit 5
		q.setSearchSizeLimit(5);
		assertEquals(5, q.getSearchSizeLimit());
		assertEquals(list(i1, i2, i3, i4, i5), q.search());

		model.clearCache();
		assertEquals(list(i1, i2, i3, i4, i5), q.search());


		// limit 4
		q.setSearchSizeLimit(4);
		assertEquals(4, q.getSearchSizeLimit());
		if(cache)
		{
			assertEquals(list(i1, i2, i3, i4, i5), q.search());
		}
		else
		{
			assertFails(
					q::search,
					SearchSizeLimitExceededException.class,
					"Query#getSearchSizeLimit() of 4 exceeded: " +
					"select this from DayItem");
		}

		model.clearCache();
		assertFails(
				q::search,
				SearchSizeLimitExceededException.class,
				"Query#getSearchSizeLimit() of 4 exceeded: " +
				"select this from DayItem");
	}
}
