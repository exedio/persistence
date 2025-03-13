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

import static com.exedio.cope.CompareConditionItem.TYPE;
import static com.exedio.cope.CompareConditionItem.date;
import static com.exedio.cope.CompareConditionItem.day;
import static com.exedio.cope.CompareConditionItem.doublex;
import static com.exedio.cope.CompareConditionItem.enumx;
import static com.exedio.cope.CompareConditionItem.intx;
import static com.exedio.cope.CompareConditionItem.item;
import static com.exedio.cope.CompareConditionItem.longx;
import static com.exedio.cope.CompareConditionItem.otherString;
import static com.exedio.cope.CompareConditionItem.string;
import static com.exedio.cope.RuntimeAssert.assertCondition;
import static com.exedio.cope.RuntimeTester.assertFieldsCovered;
import static com.exedio.cope.tojunit.Assert.assertContains;
import static com.exedio.cope.tojunit.Assert.assertContainsList;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static com.exedio.cope.tojunit.Assert.list;
import static com.exedio.cope.tojunit.EqualsAssert.assertEqualsAndHash;
import static com.exedio.cope.tojunit.EqualsAssert.assertNotEqualsAndHash;
import static java.lang.Double.valueOf;
import static java.lang.Integer.valueOf;
import static java.lang.Long.valueOf;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.exedio.cope.CompareConditionItem.YEnum;
import com.exedio.cope.util.Day;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CompareConditionTest extends TestWithEnvironment
{
	static final Model MODEL = new Model(TYPE);
	private static final This<CompareConditionItem> THIS = TYPE.getThis();

	public CompareConditionTest()
	{
		super(MODEL);
	}

	CompareConditionItem item1, item2, item3, item4, item5, itemX;
	static final Date aDate = new Date(1087365298214l);
	static final Day aDay = new Day(2007, 4, 28);

	private static Date date(final long offset)
	{
		return new Date(aDate.getTime()+offset);
	}

	private static Day day(final int offset)
	{
		return aDay.plusDays(offset);
	}

	@BeforeEach final void setUp()
	{
		item1 = new CompareConditionItem("string1", 1, 11l, 2.1, date(-2), day(-2), YEnum.V1);
		item2 = new CompareConditionItem("string2", 2, 12l, 2.2, date(-1), day(-1), YEnum.V2);
		item3 = new CompareConditionItem("string3", 3, 13l, 2.3, date( 0), day( 0), YEnum.V3);
		item4 = new CompareConditionItem("string4", 4, 14l, 2.4, date(+1), day(+1), YEnum.V4);
		item5 = new CompareConditionItem("string5", 5, 15l, 2.5, date(+2), day(+2), YEnum.V5);
		itemX = new CompareConditionItem(null, null, null, null, null, null, null);
		item1.setItem(item1);
		item2.setItem(item2);
		item3.setItem(item3);
		item4.setItem(item4);
		item5.setItem(item5);
	}

	@Test void testFieldsCovered()
	{
		assertFieldsCovered(asList(string), string.isNull());
		assertFieldsCovered(asList(string), string.isNotNull());
		assertFieldsCovered(asList(string), string.is("a"));
		assertFieldsCovered(asList(string), string.isNot("a"));
		assertFieldsCovered(asList(string), string.less("a"));
		assertFieldsCovered(asList(string), string.lessOrEqual("a"));
		assertFieldsCovered(asList(string), string.greater("a"));
		assertFieldsCovered(asList(string), string.greaterOrEqual("a"));
	}

	@Test void testEqualsHashCode()
	{
		assertEqualsAndHash(string.less("a"), string.less("a"));
		assertNotEqualsAndHash(
				string.less("a"),
				string.less("b"),
				otherString.less("a"),
				string.lessOrEqual("a"));
	}

	@Test void testToString()
	{
		assertEquals("CompareConditionItem.string='string3'",  string.is("string3").toString());
		assertEquals("CompareConditionItem.string<>'string3'", string.isNot("string3").toString());
		assertEquals("CompareConditionItem.string<'string3'",  string.less("string3").toString());
		assertEquals("CompareConditionItem.string<='string3'", string.lessOrEqual("string3").toString());
		assertEquals("CompareConditionItem.string>'string3'",  string.greater("string3").toString());
		assertEquals("CompareConditionItem.string>='string3'", string.greaterOrEqual("string3").toString());
	}

	@Test void testIsNull()
	{
		assertCondition(itemX, TYPE, string.isNull());
		assertCondition(itemX, TYPE, intx.isNull());
		assertCondition(itemX, TYPE, longx.isNull());
		assertCondition(itemX, TYPE, doublex.isNull());
		assertCondition(itemX, TYPE, date.isNull());
		assertCondition(itemX, TYPE, day.isNull());
		assertCondition(itemX, TYPE, enumx.isNull());
		assertCondition(itemX, TYPE, item.isNull());
		assertCondition(TYPE, THIS.isNull());
	}

	@Test void testIsNotNull()
	{
		assertCondition(item1, item2, item3, item4, item5, TYPE, string.isNotNull());
		assertCondition(item1, item2, item3, item4, item5, TYPE, intx.isNotNull());
		assertCondition(item1, item2, item3, item4, item5, TYPE, longx.isNotNull());
		assertCondition(item1, item2, item3, item4, item5, TYPE, doublex.isNotNull());
		assertCondition(item1, item2, item3, item4, item5, TYPE, date.isNotNull());
		assertCondition(item1, item2, item3, item4, item5, TYPE, day.isNotNull());
		assertCondition(item1, item2, item3, item4, item5, TYPE, enumx.isNotNull());
		assertCondition(item1, item2, item3, item4, item5, TYPE, item.isNotNull());
		assertCondition(item1, item2, item3, item4, item5, itemX, TYPE, THIS.isNotNull());
	}

	@Test void testEqual()
	{
		assertCondition(item3, TYPE, string.is("string3"));
		assertCondition(item3, TYPE, intx.is(3));
		assertCondition(item3, TYPE, longx.is(13l));
		assertCondition(item3, TYPE, doublex.is(2.3));
		assertCondition(item3, TYPE, date.is(aDate));
		assertCondition(item3, TYPE, day.is(aDay));
		assertCondition(item3, TYPE, enumx.is(YEnum.V3));
		assertCondition(item3, TYPE, item.is(item3));
		assertCondition(item3, TYPE, THIS.is(item3));
	}

	@Test void testNotEqual()
	{
		assertCondition(item1, item2, item4, item5, TYPE, string.isNot("string3"));
		assertCondition(item1, item2, item4, item5, TYPE, intx.isNot(3));
		assertCondition(item1, item2, item4, item5, TYPE, longx.isNot(13l));
		assertCondition(item1, item2, item4, item5, TYPE, doublex.isNot(2.3));
		assertCondition(item1, item2, item4, item5, TYPE, date.isNot(aDate));
		assertCondition(item1, item2, item4, item5, TYPE, day.isNot(aDay));
		assertCondition(item1, item2, item4, item5, TYPE, enumx.isNot(YEnum.V3));
		assertCondition(item1, item2, item4, item5, TYPE, item.isNot(item3));
		assertCondition(item1, item2, item4, item5, itemX, TYPE, THIS.isNot(item3));
	}

	@Test void testLess()
	{
		assertCondition(item1, item2, TYPE, string.less("string3"));
		assertCondition(item1, item2, TYPE, intx.less(3));
		assertCondition(item1, item2, TYPE, longx.less(13l));
		assertCondition(item1, item2, TYPE, doublex.less(2.3));
		assertCondition(item1, item2, TYPE, date.less(aDate));
		assertCondition(item1, item2, TYPE, day.less(aDay));
		assertCondition(item1, item2, TYPE, enumx.less(YEnum.V3));
		assertCondition(item1, item2, TYPE, item.less(item3));
		assertCondition(item1, item2, TYPE, THIS.less(item3));
	}

	@Test void testLessOrEqual()
	{
		assertCondition(item1, item2, item3, TYPE, string.lessOrEqual("string3"));
		assertCondition(item1, item2, item3, TYPE, intx.lessOrEqual(3));
		assertCondition(item1, item2, item3, TYPE, longx.lessOrEqual(13l));
		assertCondition(item1, item2, item3, TYPE, doublex.lessOrEqual(2.3));
		assertCondition(item1, item2, item3, TYPE, date.lessOrEqual(aDate));
		assertCondition(item1, item2, item3, TYPE, day.lessOrEqual(aDay));
		assertCondition(item1, item2, item3, TYPE, enumx.lessOrEqual(YEnum.V3));
		assertCondition(item1, item2, item3, TYPE, item.lessOrEqual(item3));
		assertCondition(item1, item2, item3, TYPE, THIS.lessOrEqual(item3));
	}

	@Test void testGreater()
	{
		assertCondition(item4, item5, TYPE, string.greater("string3"));
		assertCondition(item4, item5, TYPE, intx.greater(3));
		assertCondition(item4, item5, TYPE, longx.greater(13l));
		assertCondition(item4, item5, TYPE, doublex.greater(2.3));
		assertCondition(item4, item5, TYPE, date.greater(aDate));
		assertCondition(item4, item5, TYPE, day.greater(aDay));
		assertCondition(item4, item5, TYPE, enumx.greater(YEnum.V3));
		assertCondition(item4, item5, TYPE, item.greater(item3));
		assertCondition(item4, item5, itemX, TYPE, THIS.greater(item3));
	}

	@Test void testGreaterOrEqual()
	{
		assertCondition(item3, item4, item5, TYPE, string.greaterOrEqual("string3"));
		assertCondition(item3, item4, item5, TYPE, intx.greaterOrEqual(3));
		assertCondition(item3, item4, item5, TYPE, longx.greaterOrEqual(13l));
		assertCondition(item3, item4, item5, TYPE, doublex.greaterOrEqual(2.3));
		assertCondition(item3, item4, item5, TYPE, date.greaterOrEqual(aDate));
		assertCondition(item3, item4, item5, TYPE, day.greaterOrEqual(aDay));
		assertCondition(item3, item4, item5, TYPE, enumx.greaterOrEqual(YEnum.V3));
		assertCondition(item3, item4, item5, TYPE, item.greaterOrEqual(item3));
		assertCondition(item3, item4, item5, itemX, TYPE, THIS.greaterOrEqual(item3));
	}

	@Test void testNot()
	{
		assertEquals(intx+" is not null", intx.isNull   ().not().toString());
		assertEquals(intx+" is null",     intx.isNotNull().not().toString());
		assertEquals(intx+"<>'3'", intx.is            (3) .not().toString());
		assertEquals(intx+ "='3'", intx.isNot         (3) .not().toString());
		assertEquals(intx+">='3'", intx.less          (3) .not().toString());
		assertEquals(intx+ ">'3'", intx.lessOrEqual   (3) .not().toString());
		assertEquals(intx+"<='3'", intx.greater       (3) .not().toString());
		assertEquals(intx+ "<'3'", intx.greaterOrEqual(3) .not().toString());
		assertCondition(item1, item2, item3, item4, item5, TYPE, intx.isNull().not());
		assertCondition(itemX,                             TYPE, intx.isNotNull().not());
		assertCondition(item1, item2,        item4, item5, TYPE, intx.is(3).not());
		assertCondition(              item3,               TYPE, intx.isNot(3).not());
		assertCondition(              item3, item4, item5, TYPE, intx.less(3).not());
		assertCondition(                     item4, item5, TYPE, intx.lessOrEqual(3).not());
		assertCondition(item1, item2, item3,               TYPE, intx.greater(3).not());
		assertCondition(item1, item2,                      TYPE, intx.greaterOrEqual(3).not());
	}

	@Test void testBetween()
	{
		assertCondition(item2, item3, item4, TYPE, string.between("string2", "string4"));
		assertCondition(item2, item3, item4, TYPE, intx.between(2, 4));
		assertCondition(item2, item3, item4, TYPE, longx.between(12l, 14l));
		assertCondition(item2, item3, item4, TYPE, doublex.between(2.2, 2.4));
		assertCondition(item2, item3, item4, TYPE, date.between(date(-1), date(+1)));
		assertCondition(item2, item3, item4, TYPE, day.between(day(-1), day(+1)));
		assertCondition(item2, item3, item4, TYPE, enumx.between(YEnum.V2, YEnum.V4));
		assertCondition(item2, item3, item4, TYPE, item.between(item2, item4));
		assertCondition(item2, item3, item4, TYPE, THIS.between(item2, item4));
	}

	@Test void testIn()
	{
		assertCondition(item1, item3, TYPE, string.in(asList("string1", "string3", "stringNone")));
		assertCondition(item1, item3, itemX, TYPE, string.in(asList("string1", "string3", "stringNone", null)));
		assertCondition(item1, item3, TYPE, intx.in(asList(1, 3, 25)));
		assertCondition(item1, item3, itemX, TYPE, intx.in(asList(1, 3, 25, null)));
		assertCondition(item1, item3, TYPE, longx.in(asList(11l, 13l, 255l)));
		assertCondition(item1, item3, itemX, TYPE, longx.in(asList(11l, 13l, 255l, null)));
		assertCondition(item1, item3, TYPE, doublex.in(asList(2.1, 2.3, 25.2)));
		assertCondition(item1, item3, itemX, TYPE, doublex.in(asList(2.1, 2.3, 25.2, null)));
		assertCondition(item1, item3, TYPE, date.in(asList(date(-2), aDate, date(+25))));
		assertCondition(item1, item3, itemX, TYPE, date.in(asList(date(-2), aDate, date(+25), null)));
		assertCondition(item1, item3, TYPE, day.in(asList(day(-2), aDay, day(+25))));
		assertCondition(item1, item3, itemX, TYPE, day.in(asList(day(-2), aDay, day(+25), null)));
		assertCondition(item1, item3, TYPE, enumx.in(asList(YEnum.V1, YEnum.V3, YEnum.VX)));
		assertCondition(item1, item3, itemX, TYPE, enumx.in(asList(YEnum.V1, YEnum.V3, YEnum.VX, null)));
		assertCondition(item1, item3, TYPE, item.in(asList(item1, item3)));
		assertCondition(item1, item3, itemX, TYPE, item.in(asList(item1, item3, null)));
		assertCondition(item1, item3, TYPE, THIS.in(asList(item1, item3)));
		assertCondition(item1, item3, TYPE, THIS.in(asList(item1, item3, null)));
	}

	@Test void testInVarargs()
	{
		assertCondition(item1, item3, TYPE, string.in("string1", "string3", "stringNone"));
		assertCondition(item1, item3, itemX, TYPE, string.in("string1", "string3", "stringNone", null));
		assertCondition(item1, item3, TYPE, intx.in(1, 3, 25));
		assertCondition(item1, item3, itemX, TYPE, intx.in(1, 3, 25, null));
		assertCondition(item1, item3, TYPE, longx.in(11l, 13l, 255l));
		assertCondition(item1, item3, itemX, TYPE, longx.in(11l, 13l, 255l, null));
		assertCondition(item1, item3, TYPE, doublex.in(2.1, 2.3, 25.2));
		assertCondition(item1, item3, itemX, TYPE, doublex.in(2.1, 2.3, 25.2, null));
		assertCondition(item1, item3, TYPE, date.in(date(-2), aDate, date(+25)));
		assertCondition(item1, item3, itemX, TYPE, date.in(date(-2), aDate, date(+25), null));
		assertCondition(item1, item3, TYPE, day.in(day(-2), aDay, day(+25)));
		assertCondition(item1, item3, itemX, TYPE, day.in(day(-2), aDay, day(+25), null));
		assertCondition(item1, item3, TYPE, enumx.in(YEnum.V1, YEnum.V3, YEnum.VX));
		assertCondition(item1, item3, itemX, TYPE, enumx.in(YEnum.V1, YEnum.V3, YEnum.VX, null));
		assertCondition(item1, item3, TYPE, item.in(item1, item3));
		assertCondition(item1, item3, itemX, TYPE, item.in(item1, item3, null));
		assertCondition(item1, item3, TYPE, THIS.in(item1, item3));
		assertCondition(item1, item3, TYPE, THIS.in(item1, item3, null));
	}

	@Test void testMin()
	{
		assertEquals("select min(" + string.getName() + ") from " + TYPE, new Query<>(string.min()).toString());
		assertEquals("string1", new Query<>(string.min()).searchSingleton());
		assertEquals(valueOf(1), new Query<>(intx.min()).searchSingleton());
		assertEquals(valueOf(11l), new Query<>(longx.min()).searchSingleton());
		assertEquals(valueOf(2.1), new Query<>(doublex.min()).searchSingleton());
		assertEquals(date(-2), new Query<>(date.min()).searchSingleton());
		assertEquals(day(-2), new Query<>(day.min()).searchSingleton());
		assertEquals(YEnum.V1, new Query<>(enumx.min()).searchSingleton());
		// The following line causes MySQL 4 to write a warning to the syslog,
		// that looks like this:
		//
		// -------------------------------------------------------------------
		// InnoDB: Warning: using a partial-field key prefix in search.
		// InnoDB: index `CompareConditItem_item_Fk` of table `xyz/CompareConditionItem`. Last data field length 5 bytes,
		// InnoDB: key ptr now exceeds key end by 4 bytes.
		// InnoDB: Key value in the MySQL format:
		//  len 1; hex 01; asc  ;
		// -------------------------------------------------------------------
		//
		// This is very probably caused by a bug in MySQL 4, which has been
		// fixed in MySQL 5:
		//
		// https://bugs.mysql.com/bug.php?id=11039
		//
		// This bug occurs for columns with an index only (that is created by
		// the foreign key constraint here) and only when using the min()
		// aggregate.
		assertEquals(item1, new Query<>(item.min()).searchSingleton());
		assertEquals(item1, new Query<>(THIS.min()).searchSingleton());
	}

	@Test void testMax()
	{
		assertEquals("select max(" + string.getName() + ") from " + TYPE, new Query<>(string.max()).toString());
		assertEquals("string5", new Query<>(string.max()).searchSingleton());
		assertEquals(valueOf(5),   new Query<>(intx.max()   ).searchSingleton());
		assertEquals(valueOf(15l), new Query<>(longx.max()  ).searchSingleton());
		assertEquals(valueOf(2.5), new Query<>(doublex.max()).searchSingleton());
		assertEquals(date(+2), new Query<>(date.max()).searchSingleton());
		assertEquals(day(+2), new Query<>(day.max()).searchSingleton());
		assertEquals(YEnum.V5, new Query<>(enumx.max()).searchSingleton());
		assertEquals(item5, new Query<>(item.max()).searchSingleton());
		assertEquals(itemX, new Query<>(THIS.max()).searchSingleton());

		// test extremum aggregate
		assertEquals(true,  string.min().isMinimum());
		assertEquals(false, string.min().isMaximum());
		assertEquals(false, string.max().isMinimum());
		assertEquals(true,  string.max().isMaximum());
		assertEquals(String.class, string.min().getValueClass());
		assertEquals(String.class, string.max().getValueClass());
	}

	@Test void testSum()
	{
		{
			final Query<Integer> q = new Query<>(intx.sum());
			assertEquals("select sum(" + intx.getName() + ") from " + TYPE, q.toString());
			assertEquals(valueOf(1+2+3+4+5), q.searchSingleton());
			q.setCondition(intx.less(4));
			assertEquals("select sum(" + intx.getName() + ") from " + TYPE + " where " + intx.getName() + "<'4'", q.toString());
			assertEquals(valueOf(1+2+3), q.searchSingleton());
		}
		{
			final Query<Long> q = new Query<>(longx.sum());
			assertEquals("select sum(" + longx.getName() + ") from " + TYPE, q.toString());
			assertEquals(valueOf(11l+12l+13l+14l+15l), q.searchSingleton());
			q.setCondition(longx.less(14l));
			assertEquals("select sum(" + longx.getName() + ") from " + TYPE + " where " + longx.getName() + "<'14'", q.toString());
			assertEquals(valueOf(11l+12l+13l), q.searchSingleton());
		}
		{
			final Query<Double> q = new Query<>(doublex.sum());
			assertEquals("select sum(" + doublex.getName() + ") from " + TYPE, q.toString());
			assertEquals(2.1+2.2+2.3+2.4+2.5, q.searchSingleton(), 0.000000000000005);
			q.setCondition(doublex.less(2.4));
			assertEquals("select sum(" + doublex.getName() + ") from " + TYPE + " where " + doublex.getName() + "<'2.4'", q.toString());
			assertEquals(2.1+2.2+2.3, q.searchSingleton(), 0.000000000000005);
		}
	}

	@Test void testAverage()
	{
		{
			final Query<Double> q = new Query<>(intx.average());
			assertEquals("select avg(" + intx.getName() + ") from " + TYPE, q.toString());
			assertEquals(valueOf((1d+2+3+4+5)/5), q.searchSingleton());
			q.setCondition(intx.less(4));
			assertEquals("select avg(" + intx.getName() + ") from " + TYPE + " where " + intx.getName() + "<'4'", q.toString());
			assertEquals(valueOf((1d+2+3)/3), q.searchSingleton());
		}
		{
			final Query<Double> q = new Query<>(longx.average());
			assertEquals("select avg(" + longx.getName() + ") from " + TYPE, q.toString());
			assertEquals(valueOf((11d+12+13+14+15)/5), q.searchSingleton());
			q.setCondition(longx.less(14l));
			assertEquals("select avg(" + longx.getName() + ") from " + TYPE + " where " + longx.getName() + "<'14'", q.toString());
			assertEquals(valueOf((11d+12+13)/3), q.searchSingleton());
		}
		{
			final Query<Double> q = new Query<>(doublex.average());
			assertEquals("select avg(" + doublex.getName() + ") from " + TYPE, q.toString());
			assertEquals((2.1+2.2+2.3+2.4+2.5)/5.0, q.searchSingleton(), 0.000000000000005);
			q.setCondition(doublex.less(2.4));
			assertEquals("select avg(" + doublex.getName() + ") from " + TYPE + " where " + doublex.getName() + "<'2.4'", q.toString());
			assertEquals((2.1+2.2+2.3)/3.0, q.searchSingleton(), 0.000000000000005);
		}
	}

	@Test void testCheckUnsupportedConstraints()
	{
		commit();
		model.checkUnsupportedConstraints();
		startTransaction();
	}

	@Test void testGroup()
	{
		new CompareConditionItem("s", 10, 456L, 7.89, new Date(), day(0), YEnum.V1);
		new CompareConditionItem("s", 20, 456L, 7.89, new Date(), day(2), YEnum.V1);
		final Query<List<Object>> q = Query.newQuery(new Selectable<?>[]{day, intx/*.sum()*/}, TYPE, Condition.ofTrue());

		assertContainsList(
			list(
				list(day(-2), 1),
				list(day(-1), 2),
				list(day(0), 3),
				list(day(0), 10),
				list(day(1), 4),
				list(day(2), 5),
				list(day(2), 20),
				list(null, null)
			),
			q.search()
		);

		q.setGroupBy( day );
		q.setSelects( day, intx.sum() );
		assertEquals( "select day,sum(intx) from CompareConditionItem group by day", q.toString() );
		assertContains(
			list(day(-2), 1),
			list(day(-1), 2),
			list(day(0), 13),
			list(day(1), 4),
			list(day(2), 25),
			list(null, null),
			q.search()
		);
		assertEquals(6, q.total());
		assertTrue(q.exists());
	}

	@Test void testGetAggregateCompare()
	{
		final CompareConditionItem item = new CompareConditionItem(null, null, null, null, null, null, null);
		final Condition c = day.max().greater(new Day(2008,3,14));
		assertFails(
				() -> c.get(item),
				IllegalArgumentException.class,
				"condition contains unsupported function: max(CompareConditionItem.day)");
	}

	@Test void testGetAggregateIsNull()
	{
		final CompareConditionItem item = new CompareConditionItem(null, null, null, null, null, null, null);
		final Condition c = day.max().isNotNull();
		assertFails(
				() -> c.get(item),
				IllegalArgumentException.class,
				"condition contains unsupported function: max(CompareConditionItem.day)");
	}
}
