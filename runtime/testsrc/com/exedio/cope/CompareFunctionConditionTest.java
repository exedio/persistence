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

import static com.exedio.cope.CompareFunctionConditionItem.TYPE;
import static com.exedio.cope.CompareFunctionConditionItem.leftDate;
import static com.exedio.cope.CompareFunctionConditionItem.leftDay;
import static com.exedio.cope.CompareFunctionConditionItem.leftDouble;
import static com.exedio.cope.CompareFunctionConditionItem.leftEnum;
import static com.exedio.cope.CompareFunctionConditionItem.leftInt;
import static com.exedio.cope.CompareFunctionConditionItem.leftItem;
import static com.exedio.cope.CompareFunctionConditionItem.leftLong;
import static com.exedio.cope.CompareFunctionConditionItem.leftString;
import static com.exedio.cope.CompareFunctionConditionItem.rightDate;
import static com.exedio.cope.CompareFunctionConditionItem.rightDay;
import static com.exedio.cope.CompareFunctionConditionItem.rightDouble;
import static com.exedio.cope.CompareFunctionConditionItem.rightEnum;
import static com.exedio.cope.CompareFunctionConditionItem.rightInt;
import static com.exedio.cope.CompareFunctionConditionItem.rightItem;
import static com.exedio.cope.CompareFunctionConditionItem.rightLong;
import static com.exedio.cope.CompareFunctionConditionItem.rightString;
import static com.exedio.cope.EqualsAssert.assertEqualsAndHash;
import static com.exedio.cope.EqualsAssert.assertNotEqualsAndHash;
import static com.exedio.cope.RuntimeAssert.assertCondition;

import com.exedio.cope.CompareFunctionConditionItem.XEnum;
import com.exedio.cope.util.Day;
import java.util.Date;

public class CompareFunctionConditionTest extends AbstractRuntimeTest
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
		assertEqualsAndHash(leftString.less(rightString), leftString.less(rightString));
		assertNotEqualsAndHash(
				leftString.less(rightString),
				leftString.less(leftString),
				rightString.less(rightString),
				leftString.lessOrEqual(rightString),
				leftString.equal(rightString));

		// test toString
		assertEquals("CompareFunctionConditionItem.leftString=CompareFunctionConditionItem.rightString",  leftString.equal(rightString).toString());
		assertEquals("CompareFunctionConditionItem.leftString<CompareFunctionConditionItem.rightString",  leftString.less(rightString).toString());
		assertEquals("CompareFunctionConditionItem.leftString<=CompareFunctionConditionItem.rightString", leftString.lessOrEqual(rightString).toString());
		assertEquals("CompareFunctionConditionItem.leftString>CompareFunctionConditionItem.rightString",  leftString.greater(rightString).toString());
		assertEquals("CompareFunctionConditionItem.leftString>=CompareFunctionConditionItem.rightString", leftString.greaterOrEqual(rightString).toString());


		// equal
		assertCondition(item3, TYPE, leftString.equal(rightString));
		assertCondition(item3, TYPE, leftInt.equal(rightInt));
		assertCondition(item3, TYPE, leftLong.equal(rightLong));
		assertCondition(item3, TYPE, leftDouble.equal(rightDouble));
		assertCondition(item3, TYPE, leftDate.equal(rightDate));
		assertCondition(item3, TYPE, leftDay.equal(rightDay));
		assertCondition(item3, TYPE, leftEnum.equal(rightEnum));
		assertCondition(item3, TYPE, leftItem.equal(rightItem));
		assertCondition(item3, TYPE, TYPE.getThis().equal(rightItem));

		// notEqual
		assertCondition(item1, item2, item4, item5, TYPE, leftString.notEqual(rightString));
		assertCondition(item1, item2, item4, item5, TYPE, leftInt.notEqual(rightInt));
		assertCondition(item1, item2, item4, item5, TYPE, leftLong.notEqual(rightLong));
		assertCondition(item1, item2, item4, item5, TYPE, leftDouble.notEqual(rightDouble));
		assertCondition(item1, item2, item4, item5, TYPE, leftDate.notEqual(rightDate));
		assertCondition(item1, item2, item4, item5, TYPE, leftDay.notEqual(rightDay));
		assertCondition(item1, item2, item4, item5, TYPE, leftEnum.notEqual(rightEnum));
		assertCondition(item1, item2, item4, item5, TYPE, leftItem.notEqual(rightItem));
		assertCondition(item1, item2, item4, item5, TYPE, TYPE.getThis().notEqual(rightItem));

		// less
		assertCondition(item1, item2, TYPE, leftString.less(rightString));
		assertCondition(item1, item2, TYPE, leftInt.less(rightInt));
		assertCondition(item1, item2, TYPE, leftLong.less(rightLong));
		assertCondition(item1, item2, TYPE, leftDouble.less(rightDouble));
		assertCondition(item1, item2, TYPE, leftDate.less(rightDate));
		assertCondition(item1, item2, TYPE, leftDay.less(rightDay));
		assertCondition(item1, item2, TYPE, leftEnum.less(rightEnum));
		assertCondition(item1, item2, TYPE, leftItem.less(rightItem));
		assertCondition(item1, item2, TYPE, TYPE.getThis().less(rightItem));

		// lessOrEqual
		assertCondition(item1, item2, item3, TYPE, leftString.lessOrEqual(rightString));
		assertCondition(item1, item2, item3, TYPE, leftInt.lessOrEqual(rightInt));
		assertCondition(item1, item2, item3, TYPE, leftLong.lessOrEqual(rightLong));
		assertCondition(item1, item2, item3, TYPE, leftDouble.lessOrEqual(rightDouble));
		assertCondition(item1, item2, item3, TYPE, leftDate.lessOrEqual(rightDate));
		assertCondition(item1, item2, item3, TYPE, leftDay.lessOrEqual(rightDay));
		assertCondition(item1, item2, item3, TYPE, leftEnum.lessOrEqual(rightEnum));
		assertCondition(item1, item2, item3, TYPE, leftItem.lessOrEqual(rightItem));
		assertCondition(item1, item2, item3, TYPE, TYPE.getThis().lessOrEqual(rightItem));

		// greater
		assertCondition(item4, item5, TYPE, leftString.greater(rightString));
		assertCondition(item4, item5, TYPE, leftInt.greater(rightInt));
		assertCondition(item4, item5, TYPE, leftLong.greater(rightLong));
		assertCondition(item4, item5, TYPE, leftDouble.greater(rightDouble));
		assertCondition(item4, item5, TYPE, leftDate.greater(rightDate));
		assertCondition(item4, item5, TYPE, leftDay.greater(rightDay));
		assertCondition(item4, item5, TYPE, leftEnum.greater(rightEnum));
		assertCondition(item4, item5, TYPE, leftItem.greater(rightItem));
		assertCondition(item4, item5, TYPE, TYPE.getThis().greater(rightItem));

		// greaterOrEqual
		assertCondition(item3, item4, item5, TYPE, leftString.greaterOrEqual(rightString));
		assertCondition(item3, item4, item5, TYPE, leftInt.greaterOrEqual(rightInt));
		assertCondition(item3, item4, item5, TYPE, leftLong.greaterOrEqual(rightLong));
		assertCondition(item3, item4, item5, TYPE, leftDouble.greaterOrEqual(rightDouble));
		assertCondition(item3, item4, item5, TYPE, leftDate.greaterOrEqual(rightDate));
		assertCondition(item3, item4, item5, TYPE, leftDay.greaterOrEqual(rightDay));
		assertCondition(item3, item4, item5, TYPE, leftEnum.greaterOrEqual(rightEnum));
		assertCondition(item3, item4, item5, TYPE, leftItem.greaterOrEqual(rightItem));
		assertCondition(item3, item4, item5, TYPE, TYPE.getThis().greaterOrEqual(rightItem));
	}
}
