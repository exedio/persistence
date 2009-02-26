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

package com.exedio.cope;

import com.exedio.cope.util.Day;

public class QueryTest extends AbstractRuntimeTest
{
	private static final Condition TRUE  = Condition.TRUE;
	private static final Condition FALSE = Condition.FALSE;

	public QueryTest()
	{
		super(DayFieldTest.MODEL);
	}
	
	static final Day d1 = new Day(2006, 02, 19);
	static final Day d2 = new Day(2006, 02, 20);
	static final Day d3 = new Day(2006, 02, 21);
	
	public void testIt()
	{
		final Query q = DayItem.TYPE.newQuery(null);
		assertEquals(DayItem.TYPE, q.getType());
		assertEquals(null, q.getCondition());
		assertEqualsUnmodifiable(list(), q.getJoins());
		
		q.narrow(DayItem.day.less(d1));
		assertEquals(DayItem.TYPE, q.getType());
		assertEquals(DayItem.day.less(d1), q.getCondition());
		assertEqualsUnmodifiable(list(), q.getJoins());
		
		q.narrow(DayItem.day.greater(d1));
		assertEquals(DayItem.TYPE, q.getType());
		assertEquals(DayItem.day.less(d1).and(DayItem.day.greater(d1)), q.getCondition());
		assertEqualsUnmodifiable(list(), q.getJoins());
		
		final Condition c1 = DayItem.day.equal(d1);
		final Condition c2 = DayItem.day.equal(d2);
		
		assertEquals(c1, DayItem.day.equal(d1));
		assertFalse(c1.equals(c2));
		assertEquals(c1.and(c2), DayItem.day.equal(d1).and(DayItem.day.equal(d2)));
		assertFalse(c1.and(c2).equals(c2.and(c1)));
	}
	
	public void testLiterals()
	{
		final Condition c1 = DayItem.day.equal(d1);
		final Condition c2 = DayItem.day.equal(d2);
		{
			final Query q = DayItem.TYPE.newQuery(TRUE);
			assertSame(null, q.getCondition());
	
			model.getCurrentTransaction().setQueryInfoEnabled(true);
			assertContains(q.search());
			assertTrue(model.getCurrentTransaction().getQueryInfos().get(0).getText().startsWith("select "));
			
			q.narrow(c1);
			assertSame(c1, q.getCondition());
			
			q.narrow(c2);
			assertEquals(c1.and(c2), q.getCondition());
			
			q.narrow(FALSE);
			assertSame(FALSE, q.getCondition());
			
			q.narrow(c1);
			assertSame(FALSE, q.getCondition());
		}
		{
			final Query q = DayItem.TYPE.newQuery(FALSE);
			assertSame(FALSE, q.getCondition());
	
			model.getCurrentTransaction().setQueryInfoEnabled(true);
			assertContains(q.search());
			assertEquals("skipped search because condition==false", model.getCurrentTransaction().getQueryInfos().get(0).getText());
			
			q.setCondition(TRUE);
			assertSame(null, q.getCondition());
			
			q.setCondition(c1);
			assertSame(c1, q.getCondition());
			
			q.setCondition(FALSE);
			assertSame(FALSE, q.getCondition());
		}
	}
	
	public void testResult()
	{
		assertEquals(list(), Query.emptyResult().getData());
		assertEquals(0, Query.emptyResult().getTotal());
		assertEquals(0, Query.emptyResult().getOffset());
		assertEquals(-1, Query.emptyResult().getLimit());
		assertNotSame(Query.emptyResult(), Query.emptyResult());
		assertEquals(Query.emptyResult(), Query.emptyResult());
		assertEquals(Query.emptyResult().hashCode(), Query.emptyResult().hashCode());
		
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
}
