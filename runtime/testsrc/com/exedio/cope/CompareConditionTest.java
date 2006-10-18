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

public class CompareConditionTest extends AbstractLibTest
{
	public CompareConditionTest()
	{
		super(Main.compareConditionModel);
	}
	
	CompareConditionItem item1;
	CompareConditionItem item2;
	CompareConditionItem item3;
	CompareConditionItem item4;
	CompareConditionItem item5;
	Date date;
	
	private void setDate(final CompareConditionItem item, final Date date)
	{
		item.setDate(date);
	}
	
	private Date offset(final Date date, final long offset)
	{
		return new Date(date.getTime()+offset);
	}

	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		// zack
		deleteOnTearDown(item1 = new CompareConditionItem("string1", 1, 11l, 2.1, YEnum.enumValue1));
		deleteOnTearDown(item2 = new CompareConditionItem("string2", 2, 12l, 2.2, YEnum.enumValue1));
		deleteOnTearDown(item3 = new CompareConditionItem("string3", 3, 13l, 2.3, YEnum.enumValue2));
		deleteOnTearDown(item4 = new CompareConditionItem("string4", 4, 14l, 2.4, YEnum.enumValue3));
		deleteOnTearDown(item5 = new CompareConditionItem("string5", 5, 15l, 2.5, YEnum.enumValue3));
		date = new Date(1087365298214l);
		setDate(item1, offset(date, -2));
		setDate(item2, offset(date, -1));
		setDate(item3, date);
		setDate(item4, offset(date, 1));
		setDate(item5, offset(date, 2));
	}
	
	public void testCompareConditions()
	{
		// test equals/hashCode
		assertEquals(item1.someString.less("a"), item1.someString.less("a"));
		assertNotEquals(item1.someString.less("a"), item1.someString.less("b"));
		assertNotEquals(item1.someString.less("a"), item1.string.less("a"));
		assertNotEquals(item1.someString.less("a"), item1.someString.lessOrEqual("a"));
		
		
		// less
		assertContains(item1, item2, item1.TYPE.search(item1.string.less("string3")));
		assertContains(item1, item2, item1.TYPE.search(item1.intx.less(3)));
		assertContains(item1, item2, item1.TYPE.search(item1.longx.less(13l)));
		assertContains(item1, item2, item1.TYPE.search(item1.doublex.less(2.3)));
		assertContains(item1, item2, item1.TYPE.search(item1.date.less(date)));
		assertContains(item1, item2, item1.TYPE.search(item1.enumx.less(YEnum.enumValue2)));

		// less or equal
		assertContains(item1, item2, item3, item1.TYPE.search(item1.string.lessOrEqual("string3")));
		assertContains(item1, item2, item3, item1.TYPE.search(item1.intx.lessOrEqual(3)));
		assertContains(item1, item2, item3, item1.TYPE.search(item1.longx.lessOrEqual(13l)));
		assertContains(item1, item2, item3, item1.TYPE.search(item1.doublex.lessOrEqual(2.3)));
		assertContains(item1, item2, item3, item1.TYPE.search(item1.date.lessOrEqual(date)));
		assertContains(item1, item2, item3, item1.TYPE.search(item1.enumx.lessOrEqual(YEnum.enumValue2)));

		// greater
		assertContains(item4, item5, item1.TYPE.search(item1.string.greater("string3")));
		assertContains(item4, item5, item1.TYPE.search(item1.intx.greater(3)));
		assertContains(item4, item5, item1.TYPE.search(item1.longx.greater(13l)));
		assertContains(item4, item5, item1.TYPE.search(item1.doublex.greater(2.3)));
		assertContains(item4, item5, item1.TYPE.search(item1.date.greater(date)));
		assertContains(item4, item5, item1.TYPE.search(item1.enumx.greater(YEnum.enumValue2)));

		// greater or equal
		assertContains(item3, item4, item5, item1.TYPE.search(item1.string.greaterOrEqual("string3")));
		assertContains(item3, item4, item5, item1.TYPE.search(item1.intx.greaterOrEqual(3)));
		assertContains(item3, item4, item5, item1.TYPE.search(item1.longx.greaterOrEqual(13l)));
		assertContains(item3, item4, item5, item1.TYPE.search(item1.doublex.greaterOrEqual(2.3)));
		assertContains(item3, item4, item5, item1.TYPE.search(item1.date.greaterOrEqual(date)));
		assertContains(item3, item4, item5, item1.TYPE.search(item1.enumx.greaterOrEqual(YEnum.enumValue2)));
		
		// in
		assertContains(item1, item3, item1.TYPE.search(item1.string.in(listg("string1", "string3", "stringNone"))));
		assertContains(item1, item3, item1.TYPE.search(item1.intx.in(listg(1, 3, 25))));
		assertContains(item1, item3, item1.TYPE.search(item1.longx.in(listg(11l, 13l, 255l))));
		assertContains(item1, item3, item1.TYPE.search(item1.doublex.in(listg(2.1, 2.3, 25.2))));
		assertContains(item1, item3, item1.TYPE.search(item1.date.in(listg(offset(date, -2), date, offset(date, +25)))));
		assertContains(item1, item2, item3, item1.TYPE.search(item1.enumx.in(listg(YEnum.enumValue1, YEnum.enumValue2))));
		
		// min
		assertEquals("string1", new Query<String>(item1.string.min()).searchSingleton());
		assertEquals(new Integer(1), new Query<Integer>(item1.intx.min()).searchSingleton());
		assertEquals(new Long(11l), new Query<Long>(item1.longx.min()).searchSingleton());
		assertEquals(new Double(2.1), new Query<Double>(item1.doublex.min()).searchSingleton());
		assertEquals(offset(date, -2), new Query<Date>(item1.date.min()).searchSingleton());
		assertEquals(YEnum.enumValue1, new Query<YEnum>(item1.enumx.min()).searchSingleton());

		// max
		assertEquals("string5", new Query<String>(item1.string.max()).searchSingleton());
		assertEquals(new Integer(5), new Query<Integer>(item1.intx.max()).searchSingleton());
		assertEquals(new Long(15l), new Query<Long>(item1.longx.max()).searchSingleton());
		assertEquals(new Double(2.5), new Query<Double>(item1.doublex.max()).searchSingleton());
		assertEquals(offset(date, +2), new Query<Date>(item1.date.max()).searchSingleton());
		assertEquals(YEnum.enumValue3, new Query<YEnum>(item1.enumx.max()).searchSingleton());

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
