/*
 * Copyright (C) 2004-2006  exedio GmbH (www.exedio.com)
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

import java.util.Date;

import com.exedio.cope.CompareConditionItem.YEnum;
import com.exedio.cope.util.Day;

public class CompareConditionTest extends AbstractLibTest
{
	public CompareConditionTest()
	{
		super(Main.compareConditionModel);
	}
	
	CompareConditionItem item1, item2, item3, item4, item5, itemX;
	static final Date date = new Date(1087365298214l);
	static final Day day = new Day(2007, 4, 28);
	boolean seq;
	
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
		seq = !model.getProperties().getPkSourceButterfly();
		deleteOnTearDown(item1 = new CompareConditionItem("string1", 1, 11l, 2.1, date(-2), day(-2), YEnum.V1));
		deleteOnTearDown(item2 = new CompareConditionItem("string2", 2, 12l, 2.2, date(-1), day(-1), YEnum.V2));
		deleteOnTearDown(item3 = new CompareConditionItem("string3", 3, 13l, 2.3, date( 0), day( 0), YEnum.V3));
		deleteOnTearDown(item4 = new CompareConditionItem("string4", 4, 14l, 2.4, date(+1), day(+1), YEnum.V4));
		deleteOnTearDown(item5 = new CompareConditionItem("string5", 5, 15l, 2.5, date(+2), day(+2), YEnum.V5));
		deleteOnTearDown(itemX = new CompareConditionItem(null, null, null, null, null, null, null));
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
		assertContains(itemX, item1.TYPE.search(item1.string.isNull()));
		assertContains(itemX, item1.TYPE.search(item1.intx.isNull()));
		assertContains(itemX, item1.TYPE.search(item1.longx.isNull()));
		assertContains(itemX, item1.TYPE.search(item1.doublex.isNull()));
		assertContains(itemX, item1.TYPE.search(item1.date.isNull()));
		assertContains(itemX, item1.TYPE.search(item1.day.isNull()));
		assertContains(itemX, item1.TYPE.search(item1.enumx.isNull()));
		assertContains(itemX, item1.TYPE.search(item1.item.isNull()));
		assertContains(item1.TYPE.search(item1.TYPE.getThis().isNull()));

		// isNotNull
		assertContains(item1, item2, item3, item4, item5, item1.TYPE.search(item1.string.isNotNull()));
		assertContains(item1, item2, item3, item4, item5, item1.TYPE.search(item1.intx.isNotNull()));
		assertContains(item1, item2, item3, item4, item5, item1.TYPE.search(item1.longx.isNotNull()));
		assertContains(item1, item2, item3, item4, item5, item1.TYPE.search(item1.doublex.isNotNull()));
		assertContains(item1, item2, item3, item4, item5, item1.TYPE.search(item1.date.isNotNull()));
		assertContains(item1, item2, item3, item4, item5, item1.TYPE.search(item1.day.isNotNull()));
		assertContains(item1, item2, item3, item4, item5, item1.TYPE.search(item1.enumx.isNotNull()));
		assertContains(item1, item2, item3, item4, item5, item1.TYPE.search(item1.item.isNotNull()));
		assertContains(item1, item2, item3, item4, item5, itemX, item1.TYPE.search(item1.TYPE.getThis().isNotNull()));

		// equal
		assertContains(item3, item1.TYPE.search(item1.string.equal("string3")));
		assertContains(item3, item1.TYPE.search(item1.intx.equal(3)));
		assertContains(item3, item1.TYPE.search(item1.longx.equal(13l)));
		assertContains(item3, item1.TYPE.search(item1.doublex.equal(2.3)));
		assertContains(item3, item1.TYPE.search(item1.date.equal(date)));
		assertContains(item3, item1.TYPE.search(item1.day.equal(day)));
		assertContains(item3, item1.TYPE.search(item1.enumx.equal(YEnum.V3)));
		assertContains(item3, item1.TYPE.search(item1.item.equal(item3)));
		assertContains(item3, item1.TYPE.search(item1.TYPE.getThis().equal(item3)));

		// notEqual
		assertContains(item1, item2, item4, item5, item1.TYPE.search(item1.string.notEqual("string3")));
		assertContains(item1, item2, item4, item5, item1.TYPE.search(item1.intx.notEqual(3)));
		assertContains(item1, item2, item4, item5, item1.TYPE.search(item1.longx.notEqual(13l)));
		assertContains(item1, item2, item4, item5, item1.TYPE.search(item1.doublex.notEqual(2.3)));
		assertContains(item1, item2, item4, item5, item1.TYPE.search(item1.date.notEqual(date)));
		assertContains(item1, item2, item4, item5, item1.TYPE.search(item1.day.notEqual(day)));
		assertContains(item1, item2, item4, item5, item1.TYPE.search(item1.enumx.notEqual(YEnum.V3)));
		assertContains(item1, item2, item4, item5, item1.TYPE.search(item1.item.notEqual(item3)));
		assertContains(item1, item2, item4, item5, itemX, item1.TYPE.search(item1.TYPE.getThis().notEqual(item3)));

		// less
		assertContains(item1, item2, item1.TYPE.search(item1.string.less("string3")));
		assertContains(item1, item2, item1.TYPE.search(item1.intx.less(3)));
		assertContains(item1, item2, item1.TYPE.search(item1.longx.less(13l)));
		assertContains(item1, item2, item1.TYPE.search(item1.doublex.less(2.3)));
		assertContains(item1, item2, item1.TYPE.search(item1.date.less(date)));
		assertContains(item1, item2, item1.TYPE.search(item1.day.less(day)));
		assertContains(item1, item2, item1.TYPE.search(item1.enumx.less(YEnum.V3)));
		if(seq)
		{
			assertContains(item1, item2, item1.TYPE.search(item1.item.less(item3)));
			assertContains(item1, item2, item1.TYPE.search(item1.TYPE.getThis().less(item3)));
		}

		// lessOrEqual
		assertContains(item1, item2, item3, item1.TYPE.search(item1.string.lessOrEqual("string3")));
		assertContains(item1, item2, item3, item1.TYPE.search(item1.intx.lessOrEqual(3)));
		assertContains(item1, item2, item3, item1.TYPE.search(item1.longx.lessOrEqual(13l)));
		assertContains(item1, item2, item3, item1.TYPE.search(item1.doublex.lessOrEqual(2.3)));
		assertContains(item1, item2, item3, item1.TYPE.search(item1.date.lessOrEqual(date)));
		assertContains(item1, item2, item3, item1.TYPE.search(item1.day.lessOrEqual(day)));
		assertContains(item1, item2, item3, item1.TYPE.search(item1.enumx.lessOrEqual(YEnum.V3)));
		if(seq)
		{
			assertContains(item1, item2, item3, item1.TYPE.search(item1.item.lessOrEqual(item3)));
			assertContains(item1, item2, item3, item1.TYPE.search(item1.TYPE.getThis().lessOrEqual(item3)));
		}

		// greater
		assertContains(item4, item5, item1.TYPE.search(item1.string.greater("string3")));
		assertContains(item4, item5, item1.TYPE.search(item1.intx.greater(3)));
		assertContains(item4, item5, item1.TYPE.search(item1.longx.greater(13l)));
		assertContains(item4, item5, item1.TYPE.search(item1.doublex.greater(2.3)));
		assertContains(item4, item5, item1.TYPE.search(item1.date.greater(date)));
		assertContains(item4, item5, item1.TYPE.search(item1.day.greater(day)));
		assertContains(item4, item5, item1.TYPE.search(item1.enumx.greater(YEnum.V3)));
		if(seq)
		{
			assertContains(item4, item5, item1.TYPE.search(item1.item.greater(item3)));
			assertContains(item4, item5, itemX, item1.TYPE.search(item1.TYPE.getThis().greater(item3)));
		}

		// greaterOrEqual
		assertContains(item3, item4, item5, item1.TYPE.search(item1.string.greaterOrEqual("string3")));
		assertContains(item3, item4, item5, item1.TYPE.search(item1.intx.greaterOrEqual(3)));
		assertContains(item3, item4, item5, item1.TYPE.search(item1.longx.greaterOrEqual(13l)));
		assertContains(item3, item4, item5, item1.TYPE.search(item1.doublex.greaterOrEqual(2.3)));
		assertContains(item3, item4, item5, item1.TYPE.search(item1.date.greaterOrEqual(date)));
		assertContains(item3, item4, item5, item1.TYPE.search(item1.day.greaterOrEqual(day)));
		assertContains(item3, item4, item5, item1.TYPE.search(item1.enumx.greaterOrEqual(YEnum.V3)));
		if(seq)
		{
			assertContains(item3, item4, item5, item1.TYPE.search(item1.item.greaterOrEqual(item3)));
			assertContains(item3, item4, item5, itemX, item1.TYPE.search(item1.TYPE.getThis().greaterOrEqual(item3)));
		}
		
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
		assertEquals("string1", new Query<String>(item1.string.min()).searchSingleton());
		assertEquals(new Integer(1), new Query<Integer>(item1.intx.min()).searchSingleton());
		assertEquals(new Long(11l), new Query<Long>(item1.longx.min()).searchSingleton());
		assertEquals(new Double(2.1), new Query<Double>(item1.doublex.min()).searchSingleton());
		assertEquals(date(-2), new Query<Date>(item1.date.min()).searchSingleton());
		assertEquals(day(-2), new Query<Day>(item1.day.min()).searchSingleton());
		assertEquals(YEnum.V1, new Query<YEnum>(item1.enumx.min()).searchSingleton());
		if(seq)
		{
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
		}

		// max
		assertEquals("string5", new Query<String>(item1.string.max()).searchSingleton());
		assertEquals(new Integer(5), new Query<Integer>(item1.intx.max()).searchSingleton());
		assertEquals(new Long(15l), new Query<Long>(item1.longx.max()).searchSingleton());
		assertEquals(new Double(2.5), new Query<Double>(item1.doublex.max()).searchSingleton());
		assertEquals(date(+2), new Query<Date>(item1.date.max()).searchSingleton());
		assertEquals(day(+2), new Query<Day>(item1.day.max()).searchSingleton());
		assertEquals(YEnum.V5, new Query<YEnum>(item1.enumx.max()).searchSingleton());
		if(seq)
		{
			assertEquals(item5, new Query<CompareConditionItem>(item1.item.max()).searchSingleton());
			assertEquals(itemX, new Query<CompareConditionItem>(item1.TYPE.getThis().max()).searchSingleton());
		}

		// test extremum aggregate
		assertEquals(true,  item1.string.min().isMinimum());
		assertEquals(false, item1.string.min().isMaximum());
		assertEquals(false, item1.string.max().isMinimum());
		assertEquals(true,  item1.string.max().isMaximum());

		// sum
		{
			final Query<Integer> q = new Query<Integer>(item1.intx.sum());
			assertEquals(new Integer(1+2+3+4+5), q.searchSingleton());
			q.setCondition(item1.intx.less(4));
			assertEquals(new Integer(1+2+3), q.searchSingleton());
		}
		{
			final Query<Long> q = new Query<Long>(item1.longx.sum());
			assertEquals(new Long(11+12+13+14+15), q.searchSingleton());
			q.setCondition(item1.longx.less(14l));
			assertEquals(new Long(11+12+13), q.searchSingleton());
		}
		{
			final Query<Double> q = new Query<Double>(item1.doublex.sum());
			assertEquals(new Double(2.1+2.2+2.3+2.4+2.5).doubleValue(), q.searchSingleton().doubleValue(), 0.000000000000005);
			q.setCondition(item1.doublex.less(2.4));
			assertEquals(new Double(2.1+2.2+2.3).doubleValue(), q.searchSingleton().doubleValue(), 0.000000000000005);
		}

		model.checkUnsupportedConstraints();
	}

}
