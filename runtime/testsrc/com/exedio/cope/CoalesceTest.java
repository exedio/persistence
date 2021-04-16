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
import static com.exedio.cope.CompareConditionItem.TYPE;
import static com.exedio.cope.CompareConditionItem.date;
import static com.exedio.cope.CompareConditionItem.day;
import static com.exedio.cope.CompareConditionItem.doublex;
import static com.exedio.cope.CompareConditionItem.enumx;
import static com.exedio.cope.CompareConditionItem.intx;
import static com.exedio.cope.CompareConditionItem.item;
import static com.exedio.cope.CompareConditionItem.longx;
import static com.exedio.cope.CompareConditionItem.string;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.CompareConditionItem.YEnum;
import com.exedio.cope.util.Day;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CoalesceTest extends TestWithEnvironment
{
	public CoalesceTest()
	{
		super(CompareConditionTest.MODEL);
	}

	CompareConditionItem item1, item2, itemX;
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
		itemX = new CompareConditionItem(null, null, null, null, null, null, null);
		item1.setItem(item1);
		item2.setItem(item2);
	}

	@Test void testIt()
	{
		try
		{
			coalesce(string, (String)null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("literal", e.getMessage());
		}
		try
		{
			coalesce(string, string, null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("literal", e.getMessage());
		}

		assertIt(asList("string1", "string2", "stringX"), string, "stringX");
		assertIt(asList(1, 2, 55), intx, 55);
		assertIt(asList(11l, 12l, 55l), longx, 55l);
		assertIt(asList(2.1, 2.2, 55.5), doublex, 55.5);
		assertIt(asList(date(-2), date(-1), date(+1)), date, date(+1));
		// TODO problem with MariaDB Connector/J, see https://jira.mariadb.org/browse/CONJ-280
		assertIt(asList(day(-2), day(-1), day(+1)), day, day(+1));
		assertIt(asList(YEnum.V1, YEnum.V2, YEnum.V5), enumx, YEnum.V5);
		assertIt(asList(item1, item2, itemX), item, itemX);
	}

	public <E> void assertIt(final List<E> expected, final FunctionField<E> function, final E literal)
	{
		final Query<E> q = new Query<>(coalesce(function, literal));
		assertEquals("select coalesce(" + function.getName() + "," + literal + ") from " + TYPE, q.toString());
		q.setOrderBy(TYPE.getThis(), true);
		assertEquals(expected, q.search());
	}
}
