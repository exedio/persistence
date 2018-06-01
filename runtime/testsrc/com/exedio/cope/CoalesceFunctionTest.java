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
import static com.exedio.cope.CompareFunctionConditionItem.dateA;
import static com.exedio.cope.CompareFunctionConditionItem.dateB;
import static com.exedio.cope.CompareFunctionConditionItem.day;
import static com.exedio.cope.CompareFunctionConditionItem.dayA;
import static com.exedio.cope.CompareFunctionConditionItem.dayB;
import static com.exedio.cope.CompareFunctionConditionItem.doubleA;
import static com.exedio.cope.CompareFunctionConditionItem.doubleB;
import static com.exedio.cope.CompareFunctionConditionItem.enumA;
import static com.exedio.cope.CompareFunctionConditionItem.enumB;
import static com.exedio.cope.CompareFunctionConditionItem.intA;
import static com.exedio.cope.CompareFunctionConditionItem.intB;
import static com.exedio.cope.CompareFunctionConditionItem.itemA;
import static com.exedio.cope.CompareFunctionConditionItem.itemB;
import static com.exedio.cope.CompareFunctionConditionItem.longA;
import static com.exedio.cope.CompareFunctionConditionItem.longB;
import static com.exedio.cope.CompareFunctionConditionItem.stringA;
import static com.exedio.cope.CompareFunctionConditionItem.stringB;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.CompareFunctionConditionItem.XEnum;
import com.exedio.cope.util.Day;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CoalesceFunctionTest extends TestWithEnvironment
{
	public CoalesceFunctionTest()
	{
		super(CompareFunctionConditionTest.MODEL);
	}

	@SuppressFBWarnings("URF_UNREAD_FIELD")
	CompareFunctionConditionItem item1, item2, itemf1, itemf2, itemX, itemY;
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
		item1  = new CompareFunctionConditionItem("string1", 1, 11l, 2.1, date(-2), day(-2), XEnum.V1);
		item2  = new CompareFunctionConditionItem("string2", 2, 12l, 2.2, date(-1), day(-1), XEnum.V2);
		itemf1 = new CompareFunctionConditionItem("string1l", "string1r", 1, 101, 11l, 111l, 2.1, 102.1, date(-2), date(-102), day(-2), day(-102), XEnum.V1, XEnum.V4);
		itemf2 = new CompareFunctionConditionItem("string2l", "string2r", 2, 102, 12l, 112l, 2.2, 102.2, date(-1), date(-101), day(-1), day(-101), XEnum.V2, XEnum.V5);
		itemX  = new CompareFunctionConditionItem(null, null, null, null, null, null, null);
		itemY  = new CompareFunctionConditionItem(null, null, null, null, null, null, null, null, null, null, null, null, null, null);
		item1.setItemA(item1);
		item2.setItemA(item2);
	}

	@Test void testIt()
	{
		assertIt(asList("string1", "string2", "string1l", "string2l", "string3", "stringX"), stringA, stringB, "stringX");
		assertIt(asList("string3", "string3", "string1r", "string2r", "string3", "stringX"), stringB, stringA, "stringX");
		assertIt(asList("string1", "string2", "string1l", "string2l", "string3", null), stringA, stringB);
		assertIt(asList("string3", "string3", "string1r", "string2r", "string3", null), stringB, stringA);

		assertIt(asList(1, 2, 1, 2, 3, 55), intA, intB, 55);
		assertIt(asList(3, 3, 101, 102, 3, 55), intB, intA, 55);
		assertIt(asList(1, 2, 1, 2, 3, null), intA, intB);
		assertIt(asList(3, 3, 101, 102, 3, null), intB, intA);

		assertIt(asList(11l, 12l, 11l, 12l, 13l, 55l), longA, longB, 55l);
		assertIt(asList(13l, 13l, 111l, 112l, 13l, 55l), longB, longA, 55l);
		assertIt(asList(11l, 12l, 11l, 12l, 13l, null), longA, longB);
		assertIt(asList(13l, 13l, 111l, 112l, 13l, null), longB, longA);

		assertIt(asList(2.1, 2.2, 2.1, 2.2, 2.3, 55.5), doubleA, doubleB, 55.5);
		assertIt(asList(2.3, 2.3, 102.1, 102.2, 2.3, 55.5), doubleB, doubleA, 55.5);
		assertIt(asList(2.1, 2.2, 2.1, 2.2, 2.3, null), doubleA, doubleB);
		assertIt(asList(2.3, 2.3, 102.1, 102.2, 2.3, null), doubleB, doubleA);

		assertIt(asList(date(-2), date(-1), date(-2), date(-1), date, date(+1)), dateA, dateB, date(+1));
		assertIt(asList(date, date, date(-102), date(-101), date, date(+1)), dateB, dateA, date(+1));
		assertIt(asList(date(-2), date(-1), date(-2), date(-1), date, null), dateA, dateB);
		assertIt(asList(date, date, date(-102), date(-101), date, null), dateB, dateA);

		// TODO problem with MariaDB Connector/J, see https://jira.mariadb.org/browse/CONJ-280
		assertIt(asList(day(-2), day(-1), day(-2), day(-1), day, day(+1)), dayA, dayB, day(+1));
		assertIt(asList(day, day, day(-102), day(-101), day, day(+1)), dayB, dayA, day(+1));
		assertIt(asList(day(-2), day(-1), day(-2), day(-1), day, null), dayA, dayB);
		assertIt(asList(day, day, day(-102), day(-101), day, null), dayB, dayA);

		assertIt(asList(XEnum.V1, XEnum.V2, XEnum.V1, XEnum.V2, XEnum.V3, XEnum.V5), enumA, enumB, XEnum.V5);
		assertIt(asList(XEnum.V3, XEnum.V3, XEnum.V4, XEnum.V5, XEnum.V3, XEnum.V5), enumB, enumA, XEnum.V5);
		assertIt(asList(XEnum.V1, XEnum.V2, XEnum.V1, XEnum.V2, XEnum.V3, null), enumA, enumB);
		assertIt(asList(XEnum.V3, XEnum.V3, XEnum.V4, XEnum.V5, XEnum.V3, null), enumB, enumA);

		assertIt(asList(item1, item2, itemX, itemX, itemX, itemX), itemA, itemB, itemX);
		assertIt(asList(item1, item2, itemX, itemX, itemX, itemX), itemB, itemA, itemX);
		assertIt(asList(item1, item2, null, null, itemX, null), itemA, itemB);
		assertIt(asList(item1, item2, null, null, itemX, null), itemB, itemA);
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
