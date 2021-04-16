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

package com.exedio.cope.misc;

import static com.exedio.cope.misc.QueryAggregatorItem.TYPE;
import static com.exedio.cope.misc.QueryIterators.iterateStableQuery;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.Query;
import com.exedio.cope.TestWithEnvironment;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class StableQueryIteratorTest extends TestWithEnvironment
{
	public StableQueryIteratorTest()
	{
		super(QueryAggregatorTest.MODEL);
	}

	QueryAggregatorItem item0, item1, item2, item3, item4, item5, item6, item7, item8, item9;
	Query<QueryAggregatorItem> q;

	@BeforeEach final void setUp()
	{
		item0 = new QueryAggregatorItem(0);
		item1 = new QueryAggregatorItem(1);
		item2 = new QueryAggregatorItem(2);
		item3 = new QueryAggregatorItem(3);
		item4 = new QueryAggregatorItem(4);
		item5 = new QueryAggregatorItem(5);
		item6 = new QueryAggregatorItem(6);
		item7 = new QueryAggregatorItem(7);
		item8 = new QueryAggregatorItem(8);
		item9 = new QueryAggregatorItem(9);

		q = TYPE.newQuery();
		q.setOrderBy(TYPE.getThis(), true);
	}

	@Test void testIt()
	{
		try
		{
			iterateStableQuery(null,  0);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("query", e.getMessage());
		}
		try
		{
			iterateStableQuery(q,  0);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("slice must be greater zero, but was 0", e.getMessage());
		}

		assertIt(asList(item0, item1, item2, item3, item4, item5, item6, item7, item8, item9), q);

		q.setPageUnlimited(4);
		assertIt(asList(item4, item5, item6, item7, item8, item9), q);

		q.setPage(0, 6);
		assertIt(asList(item0, item1, item2, item3, item4, item5), q);

		q.setPage(0, 5);
		assertIt(asList(item0, item1, item2, item3, item4), q);

		q.setPage(2, 6);
		assertIt(asList(item2, item3, item4, item5, item6, item7), q);

		q.setPage(2, 5);
		assertIt(asList(item2, item3, item4, item5, item6), q);
	}

	private static void assertIt(final List<QueryAggregatorItem> expected, final Query<QueryAggregatorItem> q)
	{
		assertEquals(expected, l(iterateStableQuery(q,  1)));
		assertEquals(expected, l(iterateStableQuery(q,  2)));
		assertEquals(expected, l(iterateStableQuery(q,  3)));
		assertEquals(expected, l(iterateStableQuery(q,  4)));
		assertEquals(expected, l(iterateStableQuery(q,  5)));
		assertEquals(expected, l(iterateStableQuery(q,  6)));
		assertEquals(expected, l(iterateStableQuery(q,  7)));
		assertEquals(expected, l(iterateStableQuery(q,  8)));
		assertEquals(expected, l(iterateStableQuery(q,  9)));
		assertEquals(expected, l(iterateStableQuery(q, 10)));
		assertEquals(expected, l(iterateStableQuery(q, 11)));
		assertEquals(expected, l(iterateStableQuery(q, 12)));
		assertEquals(expected, l(iterateStableQuery(q, 13)));
		assertEquals(expected, l(iterateStableQuery(q, 14)));
		assertEquals(expected, l(iterateStableQuery(q, 15)));
	}

	private static ArrayList<QueryAggregatorItem> l(final Iterator<QueryAggregatorItem> iterator)
	{
		final ArrayList<QueryAggregatorItem> result = new ArrayList<>();
		while(iterator.hasNext())
			result.add(iterator.next());
		try
		{
			iterator.next();
			fail();
		}
		catch(final NoSuchElementException e)
		{
			assertEquals(null, e.getMessage());
		}
		return result;
	}
}
