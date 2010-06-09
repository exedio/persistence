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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.exedio.cope.CompareConditionItem.YEnum;
import com.exedio.cope.util.Day;

public class CompareConditionTest extends AbstractRuntimeTest
{
	static final Model MODEL = new Model(CompareConditionItem.TYPE);

	public CompareConditionTest()
	{
		super(MODEL);
	}
	
	CompareConditionItem item1, item2, item3, item4, item5, itemX;
	static final Date date = new Date(1087365298214l);
	static final Day day = new Day(2007, 4, 28);
	
	private Date date(final long offset)
	{
		return new Date(date.getTime()+offset);
	}

	private Day day(final int offset)
	{
		return day.add(offset);
	}

	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		item1 = deleteOnTearDown(new CompareConditionItem("string1", 1, 11l, 2.1, date(-2), day(-2), YEnum.V1));
		item2 = deleteOnTearDown(new CompareConditionItem("string2", 2, 12l, 2.2, date(-1), day(-1), YEnum.V2));
		item3 = deleteOnTearDown(new CompareConditionItem("string3", 3, 13l, 2.3, date( 0), day( 0), YEnum.V3));
		item4 = deleteOnTearDown(new CompareConditionItem("string4", 4, 14l, 2.4, date(+1), day(+1), YEnum.V4));
		item5 = deleteOnTearDown(new CompareConditionItem("string5", 5, 15l, 2.5, date(+2), day(+2), YEnum.V5));
		itemX = deleteOnTearDown(new CompareConditionItem(null, null, null, null, null, null, null));
		item1.setItem(item1);
		item2.setItem(item2);
		item3.setItem(item3);
		item4.setItem(item4);
		item5.setItem(item5);
	}
	
	public void testCompareConditions()
	{
		// test equals/hashCode
		assertEquals(item1.string.less("a"), item1.string.less("a"));
		assertNotEquals(item1.string.less("a"), item1.string.less("b"));
		assertNotEquals(item1.string.less("a"), item1.otherString.less("a"));
		assertNotEquals(item1.string.less("a"), item1.string.lessOrEqual("a"));

		// test toString
		assertEquals("CompareConditionItem.string='string3'",  item1.string.equal("string3").toString());
		assertEquals("CompareConditionItem.string<>'string3'", item1.string.notEqual("string3").toString());
		assertEquals("CompareConditionItem.string<'string3'",  item1.string.less("string3").toString());
		assertEquals("CompareConditionItem.string<='string3'", item1.string.lessOrEqual("string3").toString());
		assertEquals("CompareConditionItem.string>'string3'",  item1.string.greater("string3").toString());
		assertEquals("CompareConditionItem.string>='string3'", item1.string.greaterOrEqual("string3").toString());
		
		
		// isNull
		assertCondition(itemX, item1.string.isNull());
		assertCondition(itemX, item1.intx.isNull());
		assertCondition(itemX, item1.longx.isNull());
		assertCondition(itemX, item1.doublex.isNull());
		assertCondition(itemX, item1.date.isNull());
		assertCondition(itemX, item1.day.isNull());
		assertCondition(itemX, item1.enumx.isNull());
		assertCondition(itemX, item1.item.isNull());
		assertCondition(item1.TYPE.getThis().isNull());

		// isNotNull
		assertCondition(item1, item2, item3, item4, item5, item1.string.isNotNull());
		assertCondition(item1, item2, item3, item4, item5, item1.intx.isNotNull());
		assertCondition(item1, item2, item3, item4, item5, item1.longx.isNotNull());
		assertCondition(item1, item2, item3, item4, item5, item1.doublex.isNotNull());
		assertCondition(item1, item2, item3, item4, item5, item1.date.isNotNull());
		assertCondition(item1, item2, item3, item4, item5, item1.day.isNotNull());
		assertCondition(item1, item2, item3, item4, item5, item1.enumx.isNotNull());
		assertCondition(item1, item2, item3, item4, item5, item1.item.isNotNull());
		assertCondition(item1, item2, item3, item4, item5, itemX, item1.TYPE.getThis().isNotNull());

		// equal
		assertCondition(item3, item1.string.equal("string3"));
		assertCondition(item3, item1.intx.equal(3));
		assertCondition(item3, item1.longx.equal(13l));
		assertCondition(item3, item1.doublex.equal(2.3));
		assertCondition(item3, item1.date.equal(date));
		assertCondition(item3, item1.day.equal(day));
		assertCondition(item3, item1.enumx.equal(YEnum.V3));
		assertCondition(item3, item1.item.equal(item3));
		assertCondition(item3, item1.TYPE.getThis().equal(item3));

		// notEqual
		assertCondition(item1, item2, item4, item5, item1.string.notEqual("string3"));
		assertCondition(item1, item2, item4, item5, item1.intx.notEqual(3));
		assertCondition(item1, item2, item4, item5, item1.longx.notEqual(13l));
		assertCondition(item1, item2, item4, item5, item1.doublex.notEqual(2.3));
		assertCondition(item1, item2, item4, item5, item1.date.notEqual(date));
		assertCondition(item1, item2, item4, item5, item1.day.notEqual(day));
		assertCondition(item1, item2, item4, item5, item1.enumx.notEqual(YEnum.V3));
		assertCondition(item1, item2, item4, item5, item1.item.notEqual(item3));
		assertCondition(item1, item2, item4, item5, itemX, item1.TYPE.getThis().notEqual(item3));

		// less
		assertCondition(item1, item2, item1.string.less("string3"));
		assertCondition(item1, item2, item1.intx.less(3));
		assertCondition(item1, item2, item1.longx.less(13l));
		assertCondition(item1, item2, item1.doublex.less(2.3));
		assertCondition(item1, item2, item1.date.less(date));
		assertCondition(item1, item2, item1.day.less(day));
		assertCondition(item1, item2, item1.enumx.less(YEnum.V3));
		assertCondition(item1, item2, item1.item.less(item3));
		assertCondition(item1, item2, item1.TYPE.getThis().less(item3));

		// lessOrEqual
		assertCondition(item1, item2, item3, item1.string.lessOrEqual("string3"));
		assertCondition(item1, item2, item3, item1.intx.lessOrEqual(3));
		assertCondition(item1, item2, item3, item1.longx.lessOrEqual(13l));
		assertCondition(item1, item2, item3, item1.doublex.lessOrEqual(2.3));
		assertCondition(item1, item2, item3, item1.date.lessOrEqual(date));
		assertCondition(item1, item2, item3, item1.day.lessOrEqual(day));
		assertCondition(item1, item2, item3, item1.enumx.lessOrEqual(YEnum.V3));
		assertCondition(item1, item2, item3, item1.item.lessOrEqual(item3));
		assertCondition(item1, item2, item3, item1.TYPE.getThis().lessOrEqual(item3));

		// greater
		assertCondition(item4, item5, item1.string.greater("string3"));
		assertCondition(item4, item5, item1.intx.greater(3));
		assertCondition(item4, item5, item1.longx.greater(13l));
		assertCondition(item4, item5, item1.doublex.greater(2.3));
		assertCondition(item4, item5, item1.date.greater(date));
		assertCondition(item4, item5, item1.day.greater(day));
		assertCondition(item4, item5, item1.enumx.greater(YEnum.V3));
		assertCondition(item4, item5, item1.item.greater(item3));
		assertCondition(item4, item5, itemX, item1.TYPE.getThis().greater(item3));

		// greaterOrEqual
		assertCondition(item3, item4, item5, item1.string.greaterOrEqual("string3"));
		assertCondition(item3, item4, item5, item1.intx.greaterOrEqual(3));
		assertCondition(item3, item4, item5, item1.longx.greaterOrEqual(13l));
		assertCondition(item3, item4, item5, item1.doublex.greaterOrEqual(2.3));
		assertCondition(item3, item4, item5, item1.date.greaterOrEqual(date));
		assertCondition(item3, item4, item5, item1.day.greaterOrEqual(day));
		assertCondition(item3, item4, item5, item1.enumx.greaterOrEqual(YEnum.V3));
		assertCondition(item3, item4, item5, item1.item.greaterOrEqual(item3));
		assertCondition(item3, item4, item5, itemX, item1.TYPE.getThis().greaterOrEqual(item3));
		
		// between
		assertContains(item2, item3, item4, item1.TYPE.search(item1.string.between("string2", "string4")));
		assertContains(item2, item3, item4, item1.TYPE.search(item1.intx.between(2, 4)));
		assertContains(item2, item3, item4, item1.TYPE.search(item1.longx.between(12l, 14l)));
		assertContains(item2, item3, item4, item1.TYPE.search(item1.doublex.between(2.2, 2.4)));
		assertContains(item2, item3, item4, item1.TYPE.search(item1.date.between(date(-1), date(+1))));
		assertContains(item2, item3, item4, item1.TYPE.search(item1.day.between(day(-1), day(+1))));
		assertContains(item2, item3, item4, item1.TYPE.search(item1.enumx.between(YEnum.V2, YEnum.V4)));
		assertContains(item2, item3, item4, item1.TYPE.search(item1.item.between(item2, item4)));
		assertContains(item2, item3, item4, item1.TYPE.search(item1.TYPE.getThis().between(item2, item4)));
		
		// in
		assertContains(item1, item3, item1.TYPE.search(item1.string.in(listg("string1", "string3", "stringNone"))));
		assertContains(item1, item3, item1.TYPE.search(item1.intx.in(listg(1, 3, 25))));
		assertContains(item1, item3, item1.TYPE.search(item1.longx.in(listg(11l, 13l, 255l))));
		assertContains(item1, item3, item1.TYPE.search(item1.doublex.in(listg(2.1, 2.3, 25.2))));
		assertContains(item1, item3, item1.TYPE.search(item1.date.in(listg(date(-2), date, date(+25)))));
		assertContains(item1, item3, item1.TYPE.search(item1.day.in(listg(day(-2), day, day(+25)))));
		assertContains(item1, item3, item1.TYPE.search(item1.enumx.in(listg(YEnum.V1, YEnum.V3, YEnum.VX))));
		assertContains(item1, item3, item1.TYPE.search(item1.item.in(listg(item1, item3))));
		assertContains(item1, item3, item1.TYPE.search(item1.TYPE.getThis().in(listg(item1, item3))));
		
		// min
		assertEquals("select min(" + item1.string.getName() + ") from " + item1.TYPE, new Query<String>(item1.string.min()).toString());
		assertEquals("string1", new Query<String>(item1.string.min()).searchSingleton());
		assertEquals(new Integer(1), new Query<Integer>(item1.intx.min()).searchSingleton());
		assertEquals(new Long(11l), new Query<Long>(item1.longx.min()).searchSingleton());
		assertEquals(new Double(2.1), new Query<Double>(item1.doublex.min()).searchSingleton());
		assertEquals(date(-2), new Query<Date>(item1.date.min()).searchSingleton());
		assertEquals(day(-2), new Query<Day>(item1.day.min()).searchSingleton());
		assertEquals(YEnum.V1, new Query<YEnum>(item1.enumx.min()).searchSingleton());
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
		// http://bugs.mysql.com/bug.php?id=11039
		//
		// This bug occurs for columns with an index only (that is created by
		// the foreign key constraint here) and only when using the min()
		// aggregate.
		assertEquals(item1, new Query<CompareConditionItem>(item1.item.min()).searchSingleton());
		assertEquals(item1, new Query<CompareConditionItem>(item1.TYPE.getThis().min()).searchSingleton());

		// max
		assertEquals("select max(" + item1.string.getName() + ") from " + item1.TYPE, new Query<String>(item1.string.max()).toString());
		assertEquals("string5", new Query<String>(item1.string.max()).searchSingleton());
		assertEquals(new Integer(5), new Query<Integer>(item1.intx.max()).searchSingleton());
		assertEquals(new Long(15l), new Query<Long>(item1.longx.max()).searchSingleton());
		assertEquals(new Double(2.5), new Query<Double>(item1.doublex.max()).searchSingleton());
		assertEquals(date(+2), new Query<Date>(item1.date.max()).searchSingleton());
		assertEquals(day(+2), new Query<Day>(item1.day.max()).searchSingleton());
		assertEquals(YEnum.V5, new Query<YEnum>(item1.enumx.max()).searchSingleton());
		assertEquals(item5, new Query<CompareConditionItem>(item1.item.max()).searchSingleton());
		assertEquals(itemX, new Query<CompareConditionItem>(item1.TYPE.getThis().max()).searchSingleton());

		// test extremum aggregate
		assertEquals(true,  item1.string.min().isMinimum());
		assertEquals(false, item1.string.min().isMaximum());
		assertEquals(false, item1.string.max().isMinimum());
		assertEquals(true,  item1.string.max().isMaximum());

		// sum
		{
			final Query<Integer> q = new Query<Integer>(item1.intx.sum());
			assertEquals("select sum(" + item1.intx.getName() + ") from " + item1.TYPE, q.toString());
			assertEquals(new Integer(1+2+3+4+5), q.searchSingleton());
			q.setCondition(item1.intx.less(4));
			assertEquals("select sum(" + item1.intx.getName() + ") from " + item1.TYPE + " where " + item1.intx.getName() + "<'4'", q.toString());
			assertEquals(new Integer(1+2+3), q.searchSingleton());
		}
		{
			final Query<Long> q = new Query<Long>(item1.longx.sum());
			assertEquals("select sum(" + item1.longx.getName() + ") from " + item1.TYPE, q.toString());
			assertEquals(new Long(11+12+13+14+15), q.searchSingleton());
			q.setCondition(item1.longx.less(14l));
			assertEquals("select sum(" + item1.longx.getName() + ") from " + item1.TYPE + " where " + item1.longx.getName() + "<'14'", q.toString());
			assertEquals(new Long(11+12+13), q.searchSingleton());
		}
		{
			final Query<Double> q = new Query<Double>(item1.doublex.sum());
			assertEquals("select sum(" + item1.doublex.getName() + ") from " + item1.TYPE, q.toString());
			assertEquals(new Double(2.1+2.2+2.3+2.4+2.5).doubleValue(), q.searchSingleton().doubleValue(), 0.000000000000005);
			q.setCondition(item1.doublex.less(2.4));
			assertEquals("select sum(" + item1.doublex.getName() + ") from " + item1.TYPE + " where " + item1.doublex.getName() + "<'2.4'", q.toString());
			assertEquals(new Double(2.1+2.2+2.3).doubleValue(), q.searchSingleton().doubleValue(), 0.000000000000005);
		}
		// average
		{
			final Query<Integer> q = new Query<Integer>(item1.intx.average());
			assertEquals("select avg(" + item1.intx.getName() + ") from " + item1.TYPE, q.toString());
			assertEquals(new Integer((1+2+3+4+5)/5), q.searchSingleton());
			q.setCondition(item1.intx.less(4));
			assertEquals("select avg(" + item1.intx.getName() + ") from " + item1.TYPE + " where " + item1.intx.getName() + "<'4'", q.toString());
			assertEquals(new Integer((1+2+3)/3), q.searchSingleton());
		}
		{
			final Query<Long> q = new Query<Long>(item1.longx.average());
			assertEquals("select avg(" + item1.longx.getName() + ") from " + item1.TYPE, q.toString());
			assertEquals(new Long((11+12+13+14+15)/5l), q.searchSingleton());
			q.setCondition(item1.longx.less(14l));
			assertEquals("select avg(" + item1.longx.getName() + ") from " + item1.TYPE + " where " + item1.longx.getName() + "<'14'", q.toString());
			assertEquals(new Long((11+12+13)/3l), q.searchSingleton());
		}
		{
			final Query<Double> q = new Query<Double>(item1.doublex.average());
			assertEquals("select avg(" + item1.doublex.getName() + ") from " + item1.TYPE, q.toString());
			assertEquals(new Double((2.1+2.2+2.3+2.4+2.5)/5.0).doubleValue(), q.searchSingleton().doubleValue(), 0.000000000000005);
			q.setCondition(item1.doublex.less(2.4));
			assertEquals("select avg(" + item1.doublex.getName() + ") from " + item1.TYPE + " where " + item1.doublex.getName() + "<'2.4'", q.toString());
			assertEquals(new Double((2.1+2.2+2.3)/3.0).doubleValue(), q.searchSingleton().doubleValue(), 0.000000000000005);
		}

		model.checkUnsupportedConstraints();
	}
	
	private void assertCondition(final Condition actual)
	{
		assertCondition(Arrays.asList(new CompareConditionItem[]{}), actual);
	}
	
	private <T extends Item> void assertCondition(final T o1, final Condition actual)
	{
		final ArrayList<T> l = new ArrayList<T>();
		l.add(o1);
		assertCondition(l, actual);
	}
	
	private <T extends Item> void assertCondition(final T o1, final T o2, final Condition actual)
	{
		final ArrayList<T> l = new ArrayList<T>();
		l.add(o1);
		l.add(o2);
		assertCondition(l, actual);
	}
	
	private <T extends Item> void assertCondition(final T o1, final T o2, final T o3, final Condition actual)
	{
		final ArrayList<T> l = new ArrayList<T>();
		l.add(o1);
		l.add(o2);
		l.add(o3);
		assertCondition(l, actual);
	}
	
	private <T extends Item> void assertCondition(final T o1, final T o2, final T o3, final T o4, final Condition actual)
	{
		final ArrayList<T> l = new ArrayList<T>();
		l.add(o1);
		l.add(o2);
		l.add(o3);
		l.add(o4);
		assertCondition(l, actual);
	}
	
	private <T extends Item> void assertCondition(final T o1, final T o2, final T o3, final T o4, final T o5, final Condition actual)
	{
		final ArrayList<T> l = new ArrayList<T>();
		l.add(o1);
		l.add(o2);
		l.add(o3);
		l.add(o4);
		l.add(o5);
		assertCondition(l, actual);
	}
	
	private <T extends Item> void assertCondition(final T o1, final T o2, final T o3, final T o4, final T o5, final T o6, final Condition actual)
	{
		final ArrayList<T> l = new ArrayList<T>();
		l.add(o1);
		l.add(o2);
		l.add(o3);
		l.add(o4);
		l.add(o5);
		l.add(o6);
		assertCondition(l, actual);
	}
	
	private <T extends Item> void assertCondition(final List<T> expected, final Condition actual)
	{
		assertContainsList(expected, CompareConditionItem.TYPE.search(actual));
		for(final CompareConditionItem item : CompareConditionItem.TYPE.search())
			assertCondition(expected, actual, (T)item);
	}
	
	private static <T extends Item> void assertCondition(final List<T> expected, final Condition actual, final T item)
	{
		assertEquals(expected.contains(item), actual.evaluate(item));
	}
}
