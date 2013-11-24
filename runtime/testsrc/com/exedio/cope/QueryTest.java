/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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

import static com.exedio.cope.Condition.FALSE;
import static com.exedio.cope.Condition.TRUE;
import static com.exedio.cope.EqualsAssert.assertEqualsAndHash;
import static com.exedio.cope.Query.newQuery;

import com.exedio.cope.util.Day;
import java.util.List;

public class QueryTest extends AbstractRuntimeTest
{
	public QueryTest()
	{
		super(DayFieldTest.MODEL);
	}

	static final Day d1 = new Day(2006, 02, 19);
	static final Day d2 = new Day(2006, 02, 20);
	static final Day d3 = new Day(2006, 02, 21);

	public void testIt()
	{
		final Query<?> q = DayItem.TYPE.newQuery(null);
		assertEquals(DayItem.TYPE, q.getType());
		assertEquals(null, q.getCondition());
		assertEqualsUnmodifiable(list(), q.getJoins());

		q.narrow(DayItem.day.less(d1));
		assertEquals(DayItem.TYPE, q.getType());
		assertEqualsAndHash(DayItem.day.less(d1), q.getCondition());
		assertEqualsUnmodifiable(list(), q.getJoins());

		q.narrow(DayItem.day.greater(d1));
		assertEquals(DayItem.TYPE, q.getType());
		assertEqualsAndHash(DayItem.day.less(d1).and(DayItem.day.greater(d1)), q.getCondition());
		assertEqualsUnmodifiable(list(), q.getJoins());

		final Condition c1 = DayItem.day.equal(d1);
		final Condition c2 = DayItem.day.equal(d2);

		assertEqualsAndHash(c1, DayItem.day.equal(d1));
		assertFalse(c1.equals(c2));
		assertEqualsAndHash(c1.and(c2), DayItem.day.equal(d1).and(DayItem.day.equal(d2)));
		assertFalse(c1.and(c2).equals(c2.and(c1)));

		{
			final String search = SchemaInfo.search(q);
			assertTrue(search, search.startsWith("select "));
			final String total = SchemaInfo.total(q);
			assertTrue(total, total.startsWith("select count(*) from "));
		}
	}

	public void testSetSelect()
	{
		final Query<DayItem> q = DayItem.TYPE.newQuery(null);

		try
		{
			q.setSelects(new Selectable<?>[]{DayItem.day});
			fail();
		}
		catch(final RuntimeException e)
		{
			assertEquals("must have at least 2 selects, but was [" + DayItem.day + "]", e.getMessage());
		}
		try
		{
			q.setSelects(new Selectable<?>[]{DayItem.TYPE.getThis(), DayItem.day});
			fail();
		}
		catch(final RuntimeException e)
		{
			assertEquals("use setSelect instead", e.getMessage());
		}
	}

	public void testSetSelects()
	{
		try
		{
			newQuery(new Selectable<?>[]{DayItem.day}, DayItem.TYPE, null);
			fail();
		}
		catch(final RuntimeException e)
		{
			assertEquals("must have at least 2 selects, but was [" + DayItem.day + "]", e.getMessage());
		}

		final Query<List<Object>> q = newQuery(new Selectable<?>[]{DayItem.day, DayItem.optionalDay}, DayItem.TYPE, null);
		q.setSelects(new Selectable<?>[]{DayItem.TYPE.getThis(), DayItem.day});

		try
		{
			q.setSelects(new Selectable<?>[]{DayItem.day});
			fail();
		}
		catch(final RuntimeException e)
		{
			assertEquals("must have at least 2 selects, but was [" + DayItem.day + "]", e.getMessage());
		}
	}

	@SuppressWarnings({"unchecked", "rawtypes"}) // OK: test bad api usage
	public void testSetSelectsUnchecked()
	{
		final Query q = newQuery(new Selectable[]{DayItem.day, DayItem.optionalDay}, DayItem.TYPE, null);
		try
		{
			q.setSelect(DayItem.TYPE.getThis());
			fail();
		}
		catch(final RuntimeException e)
		{
			assertEquals("use setSelects instead", e.getMessage());
		}
	}

	public void testLiterals()
	{
		final Condition c1 = DayItem.day.equal(d1);
		final Condition c2 = DayItem.day.equal(d2);
		{
			final Query<?> q = DayItem.TYPE.newQuery(TRUE);
			assertSame(null, q.getCondition());

			model.currentTransaction().setQueryInfoEnabled(true);
			assertContains(q.search());
			assertTrue(model.currentTransaction().getQueryInfos().get(0).getText().startsWith("select "));

			q.narrow(c1);
			assertSame(c1, q.getCondition());

			q.narrow(c2);
			assertEqualsAndHash(c1.and(c2), q.getCondition());

			q.narrow(FALSE);
			assertSame(FALSE, q.getCondition());

			q.narrow(c1);
			assertSame(FALSE, q.getCondition());
		}
		{
			final Query<?> q = DayItem.TYPE.newQuery(FALSE);
			assertSame(FALSE, q.getCondition());

			model.currentTransaction().setQueryInfoEnabled(true);
			assertContains(q.search());
			assertEquals("skipped search because condition==false", model.currentTransaction().getQueryInfos().get(0).getText());
			model.currentTransaction().setQueryInfoEnabled(false);

			model.currentTransaction().setQueryInfoEnabled(true);
			assertEquals(0, q.total());
			assertEquals("skipped search because condition==false", model.currentTransaction().getQueryInfos().get(0).getText());
			model.currentTransaction().setQueryInfoEnabled(false);

			q.setCondition(TRUE);
			assertSame(null, q.getCondition());

			q.setCondition(c1);
			assertSame(c1, q.getCondition());

			q.setCondition(FALSE);
			assertSame(FALSE, q.getCondition());

			q.setCondition(null);
			assertSame(null, q.getCondition());

			q.narrow(TRUE);
			assertSame(null, q.getCondition());
		}
		assertSerializedSame(TRUE, 103);
		assertSerializedSame(FALSE, 103);
	}

	public void testResult()
	{
		assertEquals("select this from DayItem where FALSE", DayItem.TYPE.emptyQuery().toString());
		assertEquals(list(), DayItem.TYPE.emptyQuery().search());

		assertEquals(list(), Query.Result.empty().getData());
		assertEquals(0, Query.Result.empty().getTotal());
		assertEquals(0, Query.Result.empty().getOffset());
		assertEquals(-1, Query.Result.empty().getLimit());
		assertSame(Query.Result.empty(), Query.Result.empty());
		assertEquals(Query.Result.empty(), Query.Result.empty());
		assertEquals(Query.Result.empty().hashCode(), Query.Result.empty().hashCode());

		deleteOnTearDown(new DayItem(d1));
		deleteOnTearDown(new DayItem(d2));
		deleteOnTearDown(new DayItem(d3));
		deleteOnTearDown(new DayItem(d1));
		deleteOnTearDown(new DayItem(d2));
		deleteOnTearDown(new DayItem(d3));
		assertEquals(list(d2, d3, d1, d2), r(1, 4).getData());
		assertEquals(6, r(1, 4).getTotal());
		assertEquals(1, r(1, 4).getOffset());
		assertEquals(4, r(1, 4).getLimit());
		assertEqualsResult(r(0, 3), r(0, 3));
		assertEqualsResult(r(1, 3), r(1, 3));
		assertNotEqualsResult(r(0, 3), r(1, 3));
		assertNotEqualsResult(r(0, 3), r(1, 4));
		assertNotEqualsResult(r(0, 3), r(2, 3));
		assertNotEqualsResult(r(0, 3), r(3, 3));

		{
			final Query.Result<Day> r = new Query.Result<Day>(listg(d1), 0, 0);
			assertEquals(list(d1), r.getData());
			assertEquals(0, r.getTotal());
			assertEquals(0, r.getOffset());
			assertEquals(-1, r.getLimit());
		}
		{
			final Query.Result<Day> r = new Query.Result<Day>(listg(d1), 0, 0, 0);
			assertEquals(list(d1), r.getData());
			assertEquals(0, r.getTotal());
			assertEquals(0, r.getOffset());
			assertEquals(0, r.getLimit());
		}
		{
			final Query.Result<Day> r = new Query.Result<Day>(listg(d1), 11, 22);
			assertEquals(list(d1), r.getData());
			assertEquals(11, r.getTotal());
			assertEquals(22, r.getOffset());
			assertEquals(-1, r.getLimit());
		}
		{
			final Query.Result<Day> r = new Query.Result<Day>(listg(d1), 11, 22, 33);
			assertEquals(list(d1), r.getData());
			assertEquals(11, r.getTotal());
			assertEquals(22, r.getOffset());
			assertEquals(33, r.getLimit());
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
			new Query.Result<Day>(listg(d1), -1, -1);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("total must not be negative, but was -1", e.getMessage());
		}
		try
		{
			new Query.Result<Day>(listg(d1), -1, -1, -1);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("total must not be negative, but was -1", e.getMessage());
		}
		try
		{
			new Query.Result<Day>(listg(d1), 0, -1);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("offset must not be negative, but was -1", e.getMessage());
		}
		try
		{
			new Query.Result<Day>(listg(d1), 0, -1, -1);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("offset must not be negative, but was -1", e.getMessage());
		}
		try
		{
			new Query.Result<Day>(listg(d1), 0, 0, -1);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("limit must not be negative, but was -1", e.getMessage());
		}
	}

	private static Query.Result<Day> r(final int offset, final int limit)
	{
		final Query<Day> q = new Query<Day>(DayItem.day);
		q.setOrderBy(DayItem.TYPE.getThis(), true);
		q.setLimit(offset, limit);
		return q.searchAndTotal();
	}

	private static void assertEqualsResult(final Query.Result<Day> expected, final Query.Result<Day> actual)
	{
		assertEquals(expected, actual);
		assertEquals(actual, expected);
		assertEquals(actual.hashCode(), expected.hashCode());
	}

	private static void assertNotEqualsResult(final Query.Result<Day> expected, final Query.Result<Day> actual)
	{
		assertFalse(expected.equals(actual));
		assertFalse(actual.equals(expected));
		assertFalse(actual.hashCode()==expected.hashCode());
	}

	public void testGroupBy()
	{
		final DayItem item1 = deleteOnTearDown( new DayItem(d1) );
		final DayItem item2a = deleteOnTearDown( new DayItem(d2) );
		final DayItem item2b = deleteOnTearDown( new DayItem(d2) );
		final DayItem item3 = deleteOnTearDown( new DayItem(d3) );

		assertContains(
			item1, item2a, item2b, item3,
			DayItem.TYPE.search()
		);
		final Query<?> query = Query.newQuery( new Selectable<?>[]{DayItem.day, DayItem.day}, DayItem.TYPE, Condition.TRUE );
		assertEquals("select day,day from DayItem", query.toString());

		query.setGroupBy( DayItem.day );
		assertEquals("select day,day from DayItem group by day", query.toString());
		assertContains(
			list(d1, d1), list(d2, d2), list(d3, d3),
			query.search()
		);
	}
}
