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

import static com.exedio.cope.CompareConditionItem.TYPE;
import static com.exedio.cope.CompareConditionItem.intx;
import static com.exedio.cope.CompareConditionItem.longx;
import static com.exedio.cope.CompareConditionTest.MODEL;
import static java.lang.Integer.valueOf;
import static java.lang.Long.valueOf;
import static org.junit.Assert.assertEquals;

import com.exedio.cope.CompareConditionItem.YEnum;
import com.exedio.cope.util.Day;
import java.util.Date;
import org.junit.Before;
import org.junit.Test;

public class AverageRoundingTest extends TestWithEnvironment
{
	public AverageRoundingTest()
	{
		super(MODEL);
	}

	CompareConditionItem item1, item2, item3, item4, item5, itemX;
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

	@Before public final void setUp()
	{
		item1 = new CompareConditionItem("string1", 1, 11l, 2.1, date(-2), day(-2), YEnum.V1);
		item2 = new CompareConditionItem("string2", 2, 12l, 2.2, date(-1), day(-1), YEnum.V2);
		item3 = new CompareConditionItem("string3", 3, 13l, 2.3, date( 0), day( 0), YEnum.V3);
		item4 = new CompareConditionItem("string4", 4, 14l, 2.4, date(+1), day(+1), YEnum.V4);
		item5 = new CompareConditionItem("string5", 5, 15l, 2.5, date(+2), day(+2), YEnum.V5);
		itemX = new CompareConditionItem(null, null, null, null, null, null, null);
	}

	@Test public void testAverageRounding()
	{
		{
			final Query<Integer> q = new Query<>(intx.average());
			q.setCondition(intx.less(3));
			assertEquals("select avg(" + intx.getName() + ") from " + TYPE + " where " + intx.getName() + "<'3'", q.toString());
			assertEquals(valueOf(1) /* 1.5 */, q.searchSingleton());
		}
		{
			final Query<Long> q = new Query<>(longx.average());
			q.setCondition(longx.less(15l));
			assertEquals("select avg(" + longx.getName() + ") from " + TYPE + " where " + longx.getName() + "<'15'", q.toString());
			assertEquals(valueOf(12l /* 12.5 */), q.searchSingleton());
		}

		new CompareConditionItem(null, 5, 15l, null, null, null, null);

		{
			final Query<Integer> q = new Query<>(intx.average());
			assertEquals("select avg(" + intx.getName() + ") from " + TYPE, q.toString());
			assertEquals(valueOf(3) /* 3.33333333 */, q.searchSingleton());
		}
		{
			final Query<Long> q = new Query<>(longx.average());
			assertEquals("select avg(" + longx.getName() + ") from " + TYPE, q.toString());
			assertEquals(valueOf(13l) /* 13.3333333 */, q.searchSingleton());
		}

		new CompareConditionItem(null, 7, 17l, null, null, null, null);

		{
			final Query<Integer> q = new Query<>(intx.average());
			assertEquals("select avg(" + intx.getName() + ") from " + TYPE, q.toString());
			assertEquals(valueOf(3) /* 3.85714286 */, q.searchSingleton());
		}
		{
			final Query<Long> q = new Query<>(longx.average());
			assertEquals("select avg(" + longx.getName() + ") from " + TYPE, q.toString());
			assertEquals(valueOf(13l) /* 13.8571429 */, q.searchSingleton());
		}
	}
}
