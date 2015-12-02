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

import static com.exedio.cope.CoalesceView.coalesce;
import static com.exedio.cope.CompareFunctionConditionItem.TYPE;
import static com.exedio.cope.CompareFunctionConditionItem.date;
import static com.exedio.cope.CompareFunctionConditionItem.day;
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
import static java.util.Arrays.asList;

import com.exedio.cope.CompareFunctionConditionItem.XEnum;
import com.exedio.cope.util.Day;
import java.util.Date;
import java.util.List;

public class CoalesceFunctionTest extends AbstractRuntimeModelTest
{
	public CoalesceFunctionTest()
	{
		super(CompareFunctionConditionTest.MODEL);
	}

	CompareFunctionConditionItem item1, item2, itemf1, itemf2, itemX, itemY;
	static final Date aDate = new Date(1087365298214l);
	static final Day aDay = new Day(2007, 4, 28);

	private static Date date(final long offset)
	{
		return new Date(aDate.getTime()+offset);
	}

	private static Day day(final int offset)
	{
		return aDay.add(offset);
	}

	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		item1  = new CompareFunctionConditionItem("string1", 1, 11l, 2.1, date(-2), day(-2), XEnum.V1);
		item2  = new CompareFunctionConditionItem("string2", 2, 12l, 2.2, date(-1), day(-1), XEnum.V2);
		itemf1 = new CompareFunctionConditionItem("string1l", "string1r", 1, 101, 11l, 111l, 2.1, 102.1, date(-2), date(-102), day(-2), day(-102), XEnum.V1, XEnum.V4);
		itemf2 = new CompareFunctionConditionItem("string2l", "string2r", 2, 102, 12l, 112l, 2.2, 102.2, date(-1), date(-101), day(-1), day(-101), XEnum.V2, XEnum.V5);
		itemX  = new CompareFunctionConditionItem(null, null, null, null, null, null, null);
		itemY  = new CompareFunctionConditionItem(null, null, null, null, null, null, null, null, null, null, null, null, null, null);
		item1.setLeftItem(item1);
		item2.setLeftItem(item2);
	}

	public void testIt()
	{
		assertIt(asList("string1", "string2", "string1l", "string2l", "string3", "stringX"), leftString, rightString, "stringX");
		assertIt(asList("string3", "string3", "string1r", "string2r", "string3", "stringX"), rightString, leftString, "stringX");
		assertIt(asList("string1", "string2", "string1l", "string2l", "string3", null), leftString, rightString);
		assertIt(asList("string3", "string3", "string1r", "string2r", "string3", null), rightString, leftString);

		assertIt(asList(1, 2, 1, 2, 3, 55), leftInt, rightInt, 55);
		assertIt(asList(3, 3, 101, 102, 3, 55), rightInt, leftInt, 55);
		assertIt(asList(1, 2, 1, 2, 3, null), leftInt, rightInt);
		assertIt(asList(3, 3, 101, 102, 3, null), rightInt, leftInt);

		assertIt(asList(11l, 12l, 11l, 12l, 13l, 55l), leftLong, rightLong, 55l);
		assertIt(asList(13l, 13l, 111l, 112l, 13l, 55l), rightLong, leftLong, 55l);
		assertIt(asList(11l, 12l, 11l, 12l, 13l, null), leftLong, rightLong);
		assertIt(asList(13l, 13l, 111l, 112l, 13l, null), rightLong, leftLong);

		assertIt(asList(2.1, 2.2, 2.1, 2.2, 2.3, 55.5), leftDouble, rightDouble, 55.5);
		assertIt(asList(2.3, 2.3, 102.1, 102.2, 2.3, 55.5), rightDouble, leftDouble, 55.5);
		assertIt(asList(2.1, 2.2, 2.1, 2.2, 2.3, null), leftDouble, rightDouble);
		assertIt(asList(2.3, 2.3, 102.1, 102.2, 2.3, null), rightDouble, leftDouble);

		assertIt(asList(date(-2), date(-1), date(-2), date(-1), date, date(+1)), leftDate, rightDate, date(+1));
		assertIt(asList(date, date, date(-102), date(-101), date, date(+1)), rightDate, leftDate, date(+1));
		assertIt(asList(date(-2), date(-1), date(-2), date(-1), date, null), leftDate, rightDate);
		assertIt(asList(date, date, date(-102), date(-101), date, null), rightDate, leftDate);

		assertIt(asList(day(-2), day(-1), day(-2), day(-1), day, day(+1)), leftDay, rightDay, day(+1));
		assertIt(asList(day, day, day(-102), day(-101), day, day(+1)), rightDay, leftDay, day(+1));
		assertIt(asList(day(-2), day(-1), day(-2), day(-1), day, null), leftDay, rightDay);
		assertIt(asList(day, day, day(-102), day(-101), day, null), rightDay, leftDay);

		assertIt(asList(XEnum.V1, XEnum.V2, XEnum.V1, XEnum.V2, XEnum.V3, XEnum.V5), leftEnum, rightEnum, XEnum.V5);
		assertIt(asList(XEnum.V3, XEnum.V3, XEnum.V4, XEnum.V5, XEnum.V3, XEnum.V5), rightEnum, leftEnum, XEnum.V5);
		assertIt(asList(XEnum.V1, XEnum.V2, XEnum.V1, XEnum.V2, XEnum.V3, null), leftEnum, rightEnum);
		assertIt(asList(XEnum.V3, XEnum.V3, XEnum.V4, XEnum.V5, XEnum.V3, null), rightEnum, leftEnum);

		assertIt(asList(item1, item2, itemX, itemX, itemX, itemX), leftItem, rightItem, itemX);
		assertIt(asList(item1, item2, itemX, itemX, itemX, itemX), rightItem, leftItem, itemX);
		assertIt(asList(item1, item2, null, null, itemX, null), leftItem, rightItem);
		assertIt(asList(item1, item2, null, null, itemX, null), rightItem, leftItem);
	}

	public <E> void assertIt(final List<E> expected, final FunctionField<E> function1, final FunctionField<E> function2, final E literal)
	{
		final Query<E> q = new Query<>(coalesce(function1, function2, literal));
		assertEquals("select coalesce(" + function1.getName() + "," + function2.getName() + "," + literal + ") from " + TYPE, q.toString());
		q.setOrderBy(TYPE.getThis(), true);
		assertEquals(expected, q.search());
	}

	public <E> void assertIt(final List<E> expected, final FunctionField<E> function1, final FunctionField<E> function2)
	{
		final Query<E> q = new Query<>(coalesce(function1, function2));
		assertEquals("select coalesce(" + function1.getName() + "," + function2.getName() + ") from " + TYPE, q.toString());
		q.setOrderBy(TYPE.getThis(), true);
		assertEquals(expected, q.search());
	}
}
