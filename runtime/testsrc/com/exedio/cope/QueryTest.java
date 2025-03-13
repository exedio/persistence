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
import static com.exedio.cope.DayItem.mandatory;
import static com.exedio.cope.RuntimeAssert.assertSerializedSame;
import static com.exedio.cope.tojunit.Assert.assertContains;
import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static com.exedio.cope.tojunit.Assert.list;
import static com.exedio.cope.tojunit.EqualsAssert.assertEqualsAndHash;
import static com.exedio.cope.tojunit.EqualsAssert.assertNotEqualsAndHash;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.util.Day;
import org.junit.jupiter.api.Test;

public class QueryTest extends TestWithEnvironment
{
	public QueryTest()
	{
		super(DayFieldTest.MODEL);
	}

	static final Day d1 = new Day(2006, 2, 19);
	static final Day d2 = new Day(2006, 2, 20);
	static final Day d3 = new Day(2006, 2, 21);

	@Test void testIt()
	{
		final Query<?> q = TYPE.newQuery(null);
		assertEquals(TYPE.getThis(), q.getSelectSingle());
		assertEquals(TYPE, q.getType());
		assertEquals(null, q.getCondition());
		assertEqualsUnmodifiable(list(), q.getJoins());

		q.narrow(mandatory.less(d1));
		assertEquals(TYPE, q.getType());
		assertEqualsAndHash(mandatory.less(d1), q.getCondition());
		assertEqualsUnmodifiable(list(), q.getJoins());

		q.narrow(mandatory.greater(d1));
		assertEquals(TYPE, q.getType());
		assertEqualsAndHash(mandatory.less(d1).and(mandatory.greater(d1)), q.getCondition());
		assertEqualsUnmodifiable(list(), q.getJoins());

		final Condition c1 = mandatory.is(d1);
		final Condition c2 = mandatory.is(d2);

		assertEqualsAndHash(c1, mandatory.is(d1));
		assertEqualsAndHash(c1.and(c2), mandatory.is(d1).and(mandatory.is(d2)));
		assertNotEqualsAndHash(c1, c2, c1.and(c2), c2.and(c1));
	}

	@Test void testLiterals()
	{
		{
			final Query<?> q = TYPE.newQuery(TRUE);
			assertSame(null, q.getCondition());

			model.currentTransaction().setQueryInfoEnabled(true);
			assertContains(q.search());
			assertTrue(model.currentTransaction().getQueryInfos().get(0).getText().startsWith("select "));
		}
		{
			final Query<?> q = TYPE.newQuery(FALSE);
			assertSame(FALSE, q.getCondition());

			model.currentTransaction().setQueryInfoEnabled(true);
			assertContains(q.search());
			assertEquals("skipped search because condition==false", model.currentTransaction().getQueryInfos().get(0).getText());
			model.currentTransaction().setQueryInfoEnabled(false);

			model.currentTransaction().setQueryInfoEnabled(true);
			assertEquals(0, q.total());
			assertEquals("skipped search because condition==false", model.currentTransaction().getQueryInfos().get(0).getText());
			model.currentTransaction().setQueryInfoEnabled(false);

			model.currentTransaction().setQueryInfoEnabled(true);
			assertEquals(false, q.exists());
			assertEquals("skipped search because condition==false", model.currentTransaction().getQueryInfos().get(0).getText());
			model.currentTransaction().setQueryInfoEnabled(false);
		}
		assertSerializedSame(TRUE, 103);
		assertSerializedSame(FALSE, 103);
	}

	@Test void testResult()
	{
		assertEquals("select this from DayItem where FALSE", TYPE.emptyQuery().toString());
		assertEquals(list(), TYPE.emptyQuery().search());

		assertEquals(list(), Query.Result.empty().getData());
		assertEquals(0, Query.Result.empty().getTotal());
		assertEquals(0, Query.Result.empty().getPageOffset());
		assertEquals(-1, Query.Result.empty().getPageLimitOrMinusOne());
		//noinspection EqualsWithItself
		assertSame(Query.Result.empty(), Query.Result.empty());
		//noinspection EqualsWithItself
		assertEquals(Query.Result.empty(), Query.Result.empty());
		assertEquals(Query.Result.empty().hashCode(), Query.Result.empty().hashCode());

		new DayItem(d1);
		new DayItem(d2);
		new DayItem(d3);
		new DayItem(d1);
		new DayItem(d2);
		new DayItem(d3);
		assertEquals(list(d2, d3, d1, d2), r(1, 4).getData());
		assertEquals(6, r(1, 4).getTotal());
		assertEquals(1, r(1, 4).getPageOffset());
		assertEquals(4, r(1, 4).getPageLimitOrMinusOne());
		assertEqualsAndHash(r(0, 3), r(0, 3));
		assertEqualsAndHash(r(1, 3), r(1, 3));
		assertNotEqualsAndHash(
				r(0, 3),
				r(1, 3),
				r(1, 4),
				r(2, 3),
				r(3, 3));

		{
			final Query.Result<Day> r = new Query.Result<>(asList(d1), 0, 0);
			assertEquals(list(d1), r.getData());
			assertEquals(0, r.getTotal());
			assertEquals(0, r.getPageOffset());
			assertEquals(-1, r.getPageLimitOrMinusOne());
		}
		{
			final Query.Result<Day> r = new Query.Result<>(asList(d1), 0, 0, 0);
			assertEquals(list(d1), r.getData());
			assertEquals(0, r.getTotal());
			assertEquals(0, r.getPageOffset());
			assertEquals(0, r.getPageLimitOrMinusOne());
		}
		{
			final Query.Result<Day> r = new Query.Result<>(asList(d1), 11, 22);
			assertEquals(list(d1), r.getData());
			assertEquals(11, r.getTotal());
			assertEquals(22, r.getPageOffset());
			assertEquals(-1, r.getPageLimitOrMinusOne());
		}
		{
			final Query.Result<Day> r = new Query.Result<>(asList(d1), 11, 22, 33);
			assertEquals(list(d1), r.getData());
			assertEquals(11, r.getTotal());
			assertEquals(22, r.getPageOffset());
			assertEquals(33, r.getPageLimitOrMinusOne());
		}
		try
		{
			new Query.Result<Day>(null, -1, -1);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("data", e.getMessage());
		}
		try
		{
			new Query.Result<Day>(null, -1, -1, -1);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("data", e.getMessage());
		}
		try
		{
			new Query.Result<>(asList(d1), -1, -1);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("total must not be negative, but was -1", e.getMessage());
		}
		try
		{
			new Query.Result<>(asList(d1), -1, -1, -1);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("total must not be negative, but was -1", e.getMessage());
		}
		try
		{
			new Query.Result<>(asList(d1), 0, -1);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("offset must not be negative, but was -1", e.getMessage());
		}
		try
		{
			new Query.Result<>(asList(d1), 0, -1, -1);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("offset must not be negative, but was -1", e.getMessage());
		}
		try
		{
			new Query.Result<>(asList(d1), 0, 0, -1);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("limit must not be negative, but was -1", e.getMessage());
		}
	}

	private static Query.Result<Day> r(final int offset, final int limit)
	{
		final Query<Day> q = new Query<>(mandatory);
		q.setOrderBy(TYPE.getThis(), true);
		q.setPage(offset, limit);
		return q.searchAndTotal();
	}

	@Test void testGroupBy()
	{
		final DayItem item1 =  new DayItem(d1) ;
		final DayItem item2a =  new DayItem(d2) ;
		final DayItem item2b =  new DayItem(d2) ;
		final DayItem item3 =  new DayItem(d3) ;

		assertContains(
			item1, item2a, item2b, item3,
			TYPE.search()
		);
		final Query<?> query = Query.newQuery( new Selectable<?>[]{mandatory, mandatory}, TYPE, TRUE );
		assertEquals("select mandatory,mandatory from DayItem", query.toString());

		query.setGroupBy( mandatory );
		assertEquals("select mandatory,mandatory from DayItem group by mandatory", query.toString());
		assertContains(
			list(d1, d1), list(d2, d2), list(d3, d3),
			query.search()
		);
	}


	private static final Condition TRUE = Condition.ofTrue();
	private static final Condition FALSE = Condition.ofFalse();
}
