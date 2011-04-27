/*
 * Copyright (C) 2004-2011  exedio GmbH (www.exedio.com)
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
import static com.exedio.cope.CompareFunctionConditionItem.leftDate;
import static com.exedio.cope.CompareFunctionConditionItem.leftDay;
import static com.exedio.cope.CompareFunctionConditionItem.leftDouble;
import static com.exedio.cope.CompareFunctionConditionItem.leftEnum;
import static com.exedio.cope.CompareFunctionConditionItem.leftInt;
import static com.exedio.cope.CompareFunctionConditionItem.leftItem;
import static com.exedio.cope.CompareFunctionConditionItem.leftLong;
import static com.exedio.cope.CompareFunctionConditionItem.leftString;

import java.util.Date;
import java.util.List;

import com.exedio.cope.CompareFunctionConditionItem.XEnum;
import com.exedio.cope.util.Day;

public class CoalesceFunctionTest extends AbstractRuntimeTest
{
	public CoalesceFunctionTest()
	{
		super(CompareFunctionConditionTest.MODEL);
	}

	CompareFunctionConditionItem item1, item2, itemX;
	static final Date aDate = new Date(1087365298214l);
	static final Day aDay = new Day(2007, 4, 28);

	private Date date(final long offset)
	{
		return new Date(aDate.getTime()+offset);
	}

	private Day day(final int offset)
	{
		return aDay.add(offset);
	}

	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		item1 = deleteOnTearDown(new CompareFunctionConditionItem("string1", 1, 11l, 2.1, date(-2), day(-2), XEnum.V1));
		item2 = deleteOnTearDown(new CompareFunctionConditionItem("string2", 2, 12l, 2.2, date(-1), day(-1), XEnum.V2));
		itemX = deleteOnTearDown(new CompareFunctionConditionItem(null, null, null, null, null, null, null));
		item1.setLeftItem(item1);
		item2.setLeftItem(item2);
	}

	public void testIt()
	{
		assertIt(listg("string1", "string2", "stringX"), leftString, "stringX");
		assertIt(listg(1, 2, 55), leftInt, 55);
		assertIt(listg(11l, 12l, 55l), leftLong, 55l);
		assertIt(listg(2.1, 2.2, 55.5), leftDouble, 55.5);
		assertIt(listg(date(-2), date(-1), date(+1)), leftDate, date(+1));
		assertIt(listg(day(-2), day(-1), day(+1)), leftDay, day(+1));
		assertIt(listg(XEnum.V1, XEnum.V2, XEnum.V5), leftEnum, XEnum.V5);
		assertIt(listg(item1, item2, itemX), leftItem, itemX);
	}

	public <E> void assertIt(final List<E> expected, final FunctionField<E> function, final E literal)
	{
		final Query<E> q = new Query<E>(coalesce(function, literal));
		assertEquals("select coalesce(" + function.getName() + "," + literal + ") from " + TYPE, q.toString());
		q.setOrderBy(TYPE.getThis(), true);
		assertEquals(expected, q.search());
	}
}
