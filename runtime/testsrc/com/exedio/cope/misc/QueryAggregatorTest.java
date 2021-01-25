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
import static com.exedio.cope.misc.QueryAggregatorItem.intx;
import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static com.exedio.cope.tojunit.Assert.list;
import static java.util.Arrays.asList;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.Model;
import com.exedio.cope.Query;
import com.exedio.cope.TestWithEnvironment;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class QueryAggregatorTest extends TestWithEnvironment
{
	public static final Model MODEL = new Model(TYPE);

	public QueryAggregatorTest()
	{
		super(MODEL);
	}

	QueryAggregatorItem item0, item1, item2, item3, item4, item5, item6, item7, item8, item9;
	Query<QueryAggregatorItem> q1, q2, q3;
	QueryAggregator<QueryAggregatorItem> ag;

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

		q1 = TYPE.newQuery(intx.between(0, 3));
		q2 = TYPE.newQuery(intx.between(4, 5));
		q3 = TYPE.newQuery(intx.between(6, 9));
		q1.setOrderBy(TYPE.getThis(), true);
		q2.setOrderBy(TYPE.getThis(), true);
		q3.setOrderBy(TYPE.getThis(), true);

		ag = QueryAggregator.get(q1, q2, q3);
	}

	@Test void testIt()
	{
		assertEqualsUnmodifiable(list(q1, q2, q3), new QueryAggregator<>(asList(q1, q2, q3)).getQueries());
		assertEqualsUnmodifiable(list(q1, q2, q3), ag.getQueries());
		{
			final Query<QueryAggregatorItem> q1Bad = TYPE.newQuery(intx.between(0, 1));
			final Query<QueryAggregatorItem> q2Bad = TYPE.newQuery(intx.between(2, 3));
			final QueryAggregator<QueryAggregatorItem> agBad = QueryAggregator.get(q1Bad, q2Bad);
			agBad.setPage(1, 2);
			assertEquals(list(item1, item2), agBad.searchAndTotal().getData());

			q1Bad.setPageUnlimited(1);
			try
			{
				agBad.searchAndTotal();
			}
			catch(final IllegalArgumentException e)
			{
				assertEquals("queries must not be limited, but was: " + q1Bad, e.getMessage());
			}
			q1Bad.setPage(0, 1);
			try
			{
				agBad.searchAndTotal();
			}
			catch(final IllegalArgumentException e)
			{
				assertEquals("queries must not be limited, but was: " + q1Bad, e.getMessage());
			}
		}
		try
		{
			ag.setPage(-1, -1);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("offset must not be negative, but was -1", e.getMessage());
		}
		try
		{
			ag.setPage(0, -1);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("limit must not be negative, but was -1", e.getMessage());
		}
		try
		{
			ag.setPageUnlimited(-1);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("offset must not be negative, but was -1", e.getMessage());
		}

		assertEquals(list(item0, item1, item2, item3), q1.search());
		assertEquals(list(item4, item5              ), q2.search());
		assertEquals(list(item6, item7, item8, item9), q3.search());

		assertIt(0, -1, list(item0, item1, item2, item3, item4, item5, item6, item7, item8, item9));
		assertIt(0, 10, list(item0, item1, item2, item3, item4, item5, item6, item7, item8, item9));
		assertIt(0, 11, list(item0, item1, item2, item3, item4, item5, item6, item7, item8, item9));

		assertIt(0, 9, list(item0, item1, item2, item3, item4, item5, item6, item7, item8));
		assertIt(0, 8, list(item0, item1, item2, item3, item4, item5, item6, item7));
		assertIt(0, 7, list(item0, item1, item2, item3, item4, item5, item6));
		assertIt(0, 6, list(item0, item1, item2, item3, item4, item5));
		assertIt(0, 5, list(item0, item1, item2, item3, item4));
		assertIt(1, 4, list(item1, item2, item3, item4));
		assertIt(2, 3, list(item2, item3, item4));
		assertIt(3, 2, list(item3, item4));
		assertIt(3, 1, list(item3));
		assertIt(4, 1, list(item4));
		assertIt(4, 0, list());
		assertIt(3, 0, list());
		assertIt(4, 2, list(item4, item5));

		assertIt(10, -1, list());
		assertIt(10,  1, list());
		assertIt(10,  0, list());
	}

	private void assertIt(final int offset, final int limit, final List<?> expected)
	{
		if(limit>=0)
			ag.setPage(offset, limit);
		else
			ag.setPageUnlimited(offset);

		assertEquals(offset, ag.getPageOffset());
		assertEquals(limit,  ag.getPageLimitOrMinusOne());

		final Query.Result<?> result = ag.searchAndTotal();
		assertEquals(offset, result.getPageOffset());
		assertEquals(limit, result.getPageLimitOrMinusOne());
		assertEqualsUnmodifiable(expected, result.getData());
		assertEquals(10, result.getTotal());
	}
}
