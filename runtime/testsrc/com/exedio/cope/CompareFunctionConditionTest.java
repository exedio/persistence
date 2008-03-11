/*
 * Copyright (C) 2004-2008  exedio GmbH (www.exedio.com)
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

import com.exedio.cope.CompareFunctionConditionItem.XEnum;
import com.exedio.cope.util.Day;

public class CompareFunctionConditionTest extends AbstractLibTest
{
	static final Model MODEL = new Model(CompareFunctionConditionItem.TYPE);

	public CompareFunctionConditionTest()
	{
		super(MODEL);
	}
	
	CompareFunctionConditionItem item1, item2, item3, item4, item5, itemX;
	final Date date = CompareFunctionConditionItem.date;
	final Day day = CompareFunctionConditionItem.day;
	
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
		item1 = deleteOnTearDown(new CompareFunctionConditionItem("string1", 1, 11l, 2.1, date(-2), day(-2), XEnum.V1));
		item2 = deleteOnTearDown(new CompareFunctionConditionItem("string2", 2, 12l, 2.2, date(-1), day(-1), XEnum.V2));
		item3 = deleteOnTearDown(new CompareFunctionConditionItem("string3", 3, 13l, 2.3, date( 0), day( 0), XEnum.V3));
		item4 = deleteOnTearDown(new CompareFunctionConditionItem("string4", 4, 14l, 2.4, date(+1), day(+1), XEnum.V4));
		item5 = deleteOnTearDown(new CompareFunctionConditionItem("string5", 5, 15l, 2.5, date(+2), day(+2), XEnum.V5));
		itemX = deleteOnTearDown(new CompareFunctionConditionItem(null, null, null, null, null, null, null));
		item1.setRightItem(item3);
		item2.setRightItem(item3);
		item3.setRightItem(item3);
		item4.setRightItem(item3);
		item5.setRightItem(item3);
	}
	
	public void testCompareConditions()
	{
		// test equals/hashCode
		assertEquals(item1.leftString.less(item1.rightString), item1.leftString.less(item1.rightString));
		assertNotEquals(item1.leftString.less(item1.rightString), item1.leftString.less(item1.leftString));
		assertNotEquals(item1.leftString.less(item1.rightString), item1.rightString.less(item1.rightString));
		assertNotEquals(item1.leftString.less(item1.rightString), item1.leftString.lessOrEqual(item1.rightString));
		assertNotEquals(item1.leftString.less(item1.rightString), item1.leftString.equal(item1.rightString));

		// test toString
		assertEquals("CompareFunctionConditionItem.leftString=CompareFunctionConditionItem.rightString",  item1.leftString.equal(item1.rightString).toString());
		assertEquals("CompareFunctionConditionItem.leftString<CompareFunctionConditionItem.rightString",  item1.leftString.less(item1.rightString).toString());
		assertEquals("CompareFunctionConditionItem.leftString<=CompareFunctionConditionItem.rightString", item1.leftString.lessOrEqual(item1.rightString).toString());
		assertEquals("CompareFunctionConditionItem.leftString>CompareFunctionConditionItem.rightString",  item1.leftString.greater(item1.rightString).toString());
		assertEquals("CompareFunctionConditionItem.leftString>=CompareFunctionConditionItem.rightString", item1.leftString.greaterOrEqual(item1.rightString).toString());
		
		
		// equal
		assertContains(item3, item1.TYPE.search(item1.leftString.equal(item1.rightString)));
		assertContains(item3, item1.TYPE.search(item1.leftInt.equal(item1.rightInt)));
		assertContains(item3, item1.TYPE.search(item1.leftLong.equal(item1.rightLong)));
		assertContains(item3, item1.TYPE.search(item1.leftDouble.equal(item1.rightDouble)));
		assertContains(item3, item1.TYPE.search(item1.leftDate.equal(item1.rightDate)));
		assertContains(item3, item1.TYPE.search(item1.leftDay.equal(item1.rightDay)));
		assertContains(item3, item1.TYPE.search(item1.leftEnum.equal(item1.rightEnum)));
		assertContains(item3, item1.TYPE.search(item1.leftItem.equal(item1.rightItem)));
		assertContains(item3, item1.TYPE.search(item1.TYPE.getThis().equal(item1.rightItem)));

		// notEqual
		assertContains(item1, item2, item4, item5, item1.TYPE.search(item1.leftString.notEqual(item1.rightString)));
		assertContains(item1, item2, item4, item5, item1.TYPE.search(item1.leftInt.notEqual(item1.rightInt)));
		assertContains(item1, item2, item4, item5, item1.TYPE.search(item1.leftLong.notEqual(item1.rightLong)));
		assertContains(item1, item2, item4, item5, item1.TYPE.search(item1.leftDouble.notEqual(item1.rightDouble)));
		assertContains(item1, item2, item4, item5, item1.TYPE.search(item1.leftDate.notEqual(item1.rightDate)));
		assertContains(item1, item2, item4, item5, item1.TYPE.search(item1.leftDay.notEqual(item1.rightDay)));
		assertContains(item1, item2, item4, item5, item1.TYPE.search(item1.leftEnum.notEqual(item1.rightEnum)));
		assertContains(item1, item2, item4, item5, item1.TYPE.search(item1.leftItem.notEqual(item1.rightItem)));
		assertContains(item1, item2, item4, item5, item1.TYPE.search(item1.TYPE.getThis().notEqual(item1.rightItem)));

		// less
		assertContains(item1, item2, item1.TYPE.search(item1.leftString.less(item1.rightString)));
		assertContains(item1, item2, item1.TYPE.search(item1.leftInt.less(item1.rightInt)));
		assertContains(item1, item2, item1.TYPE.search(item1.leftLong.less(item1.rightLong)));
		assertContains(item1, item2, item1.TYPE.search(item1.leftDouble.less(item1.rightDouble)));
		assertContains(item1, item2, item1.TYPE.search(item1.leftDate.less(item1.rightDate)));
		assertContains(item1, item2, item1.TYPE.search(item1.leftDay.less(item1.rightDay)));
		assertContains(item1, item2, item1.TYPE.search(item1.leftEnum.less(item1.rightEnum)));
		assertContains(item1, item2, item1.TYPE.search(item1.leftItem.less(item1.rightItem)));
		assertContains(item1, item2, item1.TYPE.search(item1.TYPE.getThis().less(item1.rightItem)));

		// lessOrEqual
		assertContains(item1, item2, item3, item1.TYPE.search(item1.leftString.lessOrEqual(item1.rightString)));
		assertContains(item1, item2, item3, item1.TYPE.search(item1.leftInt.lessOrEqual(item1.rightInt)));
		assertContains(item1, item2, item3, item1.TYPE.search(item1.leftLong.lessOrEqual(item1.rightLong)));
		assertContains(item1, item2, item3, item1.TYPE.search(item1.leftDouble.lessOrEqual(item1.rightDouble)));
		assertContains(item1, item2, item3, item1.TYPE.search(item1.leftDate.lessOrEqual(item1.rightDate)));
		assertContains(item1, item2, item3, item1.TYPE.search(item1.leftDay.lessOrEqual(item1.rightDay)));
		assertContains(item1, item2, item3, item1.TYPE.search(item1.leftEnum.lessOrEqual(item1.rightEnum)));
		assertContains(item1, item2, item3, item1.TYPE.search(item1.leftItem.lessOrEqual(item1.rightItem)));
		assertContains(item1, item2, item3, item1.TYPE.search(item1.TYPE.getThis().lessOrEqual(item1.rightItem)));

		// greater
		assertContains(item4, item5, item1.TYPE.search(item1.leftString.greater(item1.rightString)));
		assertContains(item4, item5, item1.TYPE.search(item1.leftInt.greater(item1.rightInt)));
		assertContains(item4, item5, item1.TYPE.search(item1.leftLong.greater(item1.rightLong)));
		assertContains(item4, item5, item1.TYPE.search(item1.leftDouble.greater(item1.rightDouble)));
		assertContains(item4, item5, item1.TYPE.search(item1.leftDate.greater(item1.rightDate)));
		assertContains(item4, item5, item1.TYPE.search(item1.leftDay.greater(item1.rightDay)));
		assertContains(item4, item5, item1.TYPE.search(item1.leftEnum.greater(item1.rightEnum)));
		assertContains(item4, item5, item1.TYPE.search(item1.leftItem.greater(item1.rightItem)));
		assertContains(item4, item5, item1.TYPE.search(item1.TYPE.getThis().greater(item1.rightItem)));

		// greaterOrEqual
		assertContains(item3, item4, item5, item1.TYPE.search(item1.leftString.greaterOrEqual(item1.rightString)));
		assertContains(item3, item4, item5, item1.TYPE.search(item1.leftInt.greaterOrEqual(item1.rightInt)));
		assertContains(item3, item4, item5, item1.TYPE.search(item1.leftLong.greaterOrEqual(item1.rightLong)));
		assertContains(item3, item4, item5, item1.TYPE.search(item1.leftDouble.greaterOrEqual(item1.rightDouble)));
		assertContains(item3, item4, item5, item1.TYPE.search(item1.leftDate.greaterOrEqual(item1.rightDate)));
		assertContains(item3, item4, item5, item1.TYPE.search(item1.leftDay.greaterOrEqual(item1.rightDay)));
		assertContains(item3, item4, item5, item1.TYPE.search(item1.leftEnum.greaterOrEqual(item1.rightEnum)));
		assertContains(item3, item4, item5, item1.TYPE.search(item1.leftItem.greaterOrEqual(item1.rightItem)));
		assertContains(item3, item4, item5, item1.TYPE.search(item1.TYPE.getThis().greaterOrEqual(item1.rightItem)));
	}
}
