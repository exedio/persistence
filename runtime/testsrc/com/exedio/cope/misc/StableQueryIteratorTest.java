/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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
import static com.exedio.cope.misc.StableQueryIterator.iterate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import com.exedio.cope.AbstractRuntimeTest;
import com.exedio.cope.Query;

public class StableQueryIteratorTest extends AbstractRuntimeTest
{
	public StableQueryIteratorTest()
	{
		super(QueryAggregatorTest.MODEL);
	}

	QueryAggregatorItem item0, item1, item2, item3, item4, item5, item6, item7, item8, item9;
	Query<QueryAggregatorItem> q;

	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		item0 = deleteOnTearDown(new QueryAggregatorItem(0));
		item1 = deleteOnTearDown(new QueryAggregatorItem(1));
		item2 = deleteOnTearDown(new QueryAggregatorItem(2));
		item3 = deleteOnTearDown(new QueryAggregatorItem(3));
		item4 = deleteOnTearDown(new QueryAggregatorItem(4));
		item5 = deleteOnTearDown(new QueryAggregatorItem(5));
		item6 = deleteOnTearDown(new QueryAggregatorItem(6));
		item7 = deleteOnTearDown(new QueryAggregatorItem(7));
		item8 = deleteOnTearDown(new QueryAggregatorItem(8));
		item9 = deleteOnTearDown(new QueryAggregatorItem(9));

		q = TYPE.newQuery();
		q.setOrderBy(TYPE.getThis(), true);
	}

	public void testIt()
	{
		try
		{
			iterate(null,  0);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("query", e.getMessage());
		}
		try
		{
			iterate(q,  0);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("slice must be greater 0, but was 0", e.getMessage());
		}

		assertIt(listg(item0, item1, item2, item3, item4, item5, item6, item7, item8, item9), q);

		q.setLimit(4);
		assertIt(listg(item4, item5, item6, item7, item8, item9), q);

		q.setLimit(0, 6);
		assertIt(listg(item0, item1, item2, item3, item4, item5), q);

		q.setLimit(0, 5);
		assertIt(listg(item0, item1, item2, item3, item4), q);

		q.setLimit(2, 6);
		assertIt(listg(item2, item3, item4, item5, item6, item7), q);

		q.setLimit(2, 5);
		assertIt(listg(item2, item3, item4, item5, item6), q);
	}

	private void assertIt(final List<QueryAggregatorItem> expected, final Query<QueryAggregatorItem> q)
	{
		assertEquals(expected, l(iterate(q,  1)));
		assertEquals(expected, l(iterate(q,  2)));
		assertEquals(expected, l(iterate(q,  3)));
		assertEquals(expected, l(iterate(q,  4)));
		assertEquals(expected, l(iterate(q,  5)));
		assertEquals(expected, l(iterate(q,  6)));
		assertEquals(expected, l(iterate(q,  7)));
		assertEquals(expected, l(iterate(q,  8)));
		assertEquals(expected, l(iterate(q,  9)));
		assertEquals(expected, l(iterate(q, 10)));
		assertEquals(expected, l(iterate(q, 11)));
		assertEquals(expected, l(iterate(q, 12)));
		assertEquals(expected, l(iterate(q, 13)));
		assertEquals(expected, l(iterate(q, 14)));
		assertEquals(expected, l(iterate(q, 15)));
	}

	private static ArrayList<QueryAggregatorItem> l(final Iterator<QueryAggregatorItem> iterator)
	{
		final ArrayList<QueryAggregatorItem> result = new ArrayList<QueryAggregatorItem>();
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
