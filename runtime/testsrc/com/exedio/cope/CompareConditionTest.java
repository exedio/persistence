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

import com.exedio.cope.testmodel.AttributeItem;
import com.exedio.cope.testmodel.EmptyItem;

public class CompareConditionTest extends TestmodelTest
{
	EmptyItem someItem;
	AttributeItem item1;
	AttributeItem item2;
	AttributeItem item3;
	AttributeItem item4;
	AttributeItem item5;
	Date date;
	
	private void setDate(final AttributeItem item, final Date date)
	{
		item.setSomeDate(date);
	}
	
	private Date offset(final Date date, final long offset)
	{
		return new Date(date.getTime()+offset);
	}

	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		deleteOnTearDown(someItem = new EmptyItem());
		deleteOnTearDown(item1 = new AttributeItem("string1", 1, 11l, 2.1, true, someItem, AttributeItem.SomeEnum.enumValue1));
		deleteOnTearDown(item2 = new AttributeItem("string2", 2, 12l, 2.2, true, someItem, AttributeItem.SomeEnum.enumValue1));
		deleteOnTearDown(item3 = new AttributeItem("string3", 3, 13l, 2.3, true, someItem, AttributeItem.SomeEnum.enumValue2));
		deleteOnTearDown(item4 = new AttributeItem("string4", 4, 14l, 2.4, true, someItem, AttributeItem.SomeEnum.enumValue3));
		deleteOnTearDown(item5 = new AttributeItem("string5", 5, 15l, 2.5, true, someItem, AttributeItem.SomeEnum.enumValue3));
		date = new Date(1087365298214l);
		setDate(item1, offset(date, -2));
		setDate(item2, offset(date, -1));
		setDate(item3, date);
		setDate(item4, offset(date, 1));
		setDate(item5, offset(date, 2));
	}
	
	public void testLiteralConditions()
	{
		// test equals/hashCode
		assertEquals(item1.someString.less("a"), item1.someString.less("a"));
		assertNotEquals(item1.someString.less("a"), item1.someString.less("b"));
		assertNotEquals(item1.someString.less("a"), item1.someNotNullString.less("a"));
		assertNotEquals(item1.someString.less("a"), item1.someString.lessOrEqual("a"));
		
		
		// less
		assertContains(item1, item2,
			item1.TYPE.search(item1.someNotNullString.less("string3")));
		assertContains(item1, item2,
			item1.TYPE.search(item1.someNotNullInteger.less(3)));
		assertContains(item1, item2,
			item1.TYPE.search(item1.someNotNullLong.less(13l)));
		assertContains(item1, item2,
			item1.TYPE.search(item1.someNotNullDouble.less(2.3)));
		assertContains(item1, item2,
			item1.TYPE.search(item1.someDate.less(date)));
		assertContains(item1, item2,
			item1.TYPE.search(item1.someNotNullEnum.less(AttributeItem.SomeEnum.enumValue2)));

		// less or equal
		assertContains(item1, item2, item3,
			item1.TYPE.search(item1.someNotNullString.lessOrEqual("string3")));
		assertContains(item1, item2, item3,
			item1.TYPE.search(item1.someNotNullInteger.lessOrEqual(3)));
		assertContains(item1, item2, item3,
			item1.TYPE.search(item1.someNotNullLong.lessOrEqual(13l)));
		assertContains(item1, item2, item3,
			item1.TYPE.search(item1.someNotNullDouble.lessOrEqual(2.3)));
		assertContains(item1, item2, item3,
			item1.TYPE.search(item1.someDate.lessOrEqual(date)));
		assertContains(item1, item2, item3,
			item1.TYPE.search(item1.someNotNullEnum.lessOrEqual(AttributeItem.SomeEnum.enumValue2)));

		// greater
		assertContains(item4, item5,
			item1.TYPE.search(item1.someNotNullString.greater("string3")));
		assertContains(item4, item5,
			item1.TYPE.search(item1.someNotNullInteger.greater(3)));
		assertContains(item4, item5,
			item1.TYPE.search(item1.someNotNullLong.greater(13l)));
		assertContains(item4, item5,
			item1.TYPE.search(item1.someNotNullDouble.greater(2.3)));
		assertContains(item4, item5,
			item1.TYPE.search(item1.someDate.greater(date)));
		assertContains(item4, item5,
			item1.TYPE.search(item1.someNotNullEnum.greater(AttributeItem.SomeEnum.enumValue2)));

		// greater or equal
		assertContains(item3, item4, item5,
			item1.TYPE.search(item1.someNotNullString.greaterOrEqual("string3")));
		assertContains(item3, item4, item5,
			item1.TYPE.search(item1.someNotNullInteger.greaterOrEqual(3)));
		assertContains(item3, item4, item5,
			item1.TYPE.search(item1.someNotNullLong.greaterOrEqual(13l)));
		assertContains(item3, item4, item5,
			item1.TYPE.search(item1.someNotNullDouble.greaterOrEqual(2.3)));
		assertContains(item3, item4, item5,
			item1.TYPE.search(item1.someDate.greaterOrEqual(date)));
		assertContains(item3, item4, item5,
			item1.TYPE.search(item1.someNotNullEnum.greaterOrEqual(AttributeItem.SomeEnum.enumValue2)));
		
		// in
		assertContains(item1, item3,
			item1.TYPE.search(item1.someNotNullString.in(listg("string1", "string3", "stringNone"))));
		assertContains(item1, item3,
			item1.TYPE.search(item1.someNotNullInteger.in(listg(1, 3, 25))));
		assertContains(item1, item3,
			item1.TYPE.search(item1.someNotNullLong.in(listg(11l, 13l, 255l))));
		assertContains(item1, item3,
			item1.TYPE.search(item1.someNotNullDouble.in(listg(2.1, 2.3, 25.2))));
		assertContains(item1, item3,
			item1.TYPE.search(item1.someDate.in(listg(offset(date, -2), date, offset(date, +25)))));
		assertContains(item1, item2, item3,
			item1.TYPE.search(item1.someNotNullEnum.in(listg(AttributeItem.SomeEnum.enumValue1, AttributeItem.SomeEnum.enumValue2))));
		
		// min
		assertEquals("string1", new Query<String>(item1.someNotNullString.min()).searchSingleton());
		assertEquals(new Integer(1), new Query<Integer>(item1.someNotNullInteger.min()).searchSingleton());
		assertEquals(new Long(11l), new Query<Long>(item1.someNotNullLong.min()).searchSingleton());
		assertEquals(new Double(2.1), new Query<Double>(item1.someNotNullDouble.min()).searchSingleton());
		assertEquals(offset(date, -2), new Query<Date>(item1.someDate.min()).searchSingleton());
		assertEquals(AttributeItem.SomeEnum.enumValue1, new Query<AttributeItem.SomeEnum>(item1.someNotNullEnum.min()).searchSingleton());

		// max
		assertEquals("string5", new Query<String>(item1.someNotNullString.max()).searchSingleton());
		assertEquals(new Integer(5), new Query<Integer>(item1.someNotNullInteger.max()).searchSingleton());
		assertEquals(new Long(15l), new Query<Long>(item1.someNotNullLong.max()).searchSingleton());
		assertEquals(new Double(2.5), new Query<Double>(item1.someNotNullDouble.max()).searchSingleton());
		assertEquals(offset(date, +2), new Query<Date>(item1.someDate.max()).searchSingleton());
		assertEquals(AttributeItem.SomeEnum.enumValue3, new Query<AttributeItem.SomeEnum>(item1.someNotNullEnum.max()).searchSingleton());

		// test extremum aggregate
		assertEquals(true,  item1.someNotNullString.min().isMinimum());
		assertEquals(false, item1.someNotNullString.min().isMaximum());
		assertEquals(false, item1.someNotNullString.max().isMinimum());
		assertEquals(true,  item1.someNotNullString.max().isMaximum());

		// sum
		{
			final Query<Integer> q = new Query<Integer>(item1.someNotNullInteger.sum());
			assertEquals(new Integer(1+2+3+4+5), q.searchSingleton());
			q.setCondition(item1.someNotNullInteger.less(4));
			assertEquals(new Integer(1+2+3), q.searchSingleton());
		}
		{
			final Query<Long> q = new Query<Long>(item1.someNotNullLong.sum());
			assertEquals(new Long(11+12+13+14+15), q.searchSingleton());
			q.setCondition(item1.someNotNullLong.less(14l));
			assertEquals(new Long(11+12+13), q.searchSingleton());
		}
		{
			final Query<Double> q = new Query<Double>(item1.someNotNullDouble.sum());
			assertEquals(new Double(2.1+2.2+2.3+2.4+2.5).doubleValue(), q.searchSingleton().doubleValue(), 0.000000000000005);
			q.setCondition(item1.someNotNullDouble.less(2.4));
			assertEquals(new Double(2.1+2.2+2.3).doubleValue(), q.searchSingleton().doubleValue(), 0.000000000000005);
		}

		model.checkUnsupportedConstraints();
	}

}
