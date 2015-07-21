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

import static com.exedio.cope.DayItem.TYPE;
import static com.exedio.cope.DayItem.day;
import static com.exedio.cope.DayItem.optionalDay;
import static com.exedio.cope.RuntimeAssert.assertSerializedSame;
import static com.exedio.cope.util.TimeZoneStrict.getTimeZone;

import com.exedio.cope.junit.AbsoluteMockClockStrategy;
import com.exedio.cope.util.Clock;
import com.exedio.cope.util.Day;
import java.util.List;

public class DayFieldTest extends AbstractRuntimeTest
{
	public static final Model MODEL = new Model(DayItem.TYPE);

	static
	{
		MODEL.enableSerialization(DayFieldTest.class, "MODEL");
	}

	DayItem item, item2;
	private AbsoluteMockClockStrategy clock;
	static final Day DEFAULT = new Day(2005, 8, 14);
	static final Day DEFAULT2 = new Day(2005, 8, 15);

	public DayFieldTest()
	{
		super(MODEL);
	}

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		item = deleteOnTearDown(new DayItem(DEFAULT));
		item2 = deleteOnTearDown(new DayItem(DEFAULT2));
		clock = new AbsoluteMockClockStrategy();
		Clock.override(clock);
	}

	@Override
	protected void tearDown() throws Exception
	{
		Clock.clearOverride();
		super.tearDown();
	}

	public void testIt()
	{
		final Day thisDay = new Day(2005, 9, 23);
		final Day beforeDay = new Day(2005, 9, 22);
		final Day nextDay = new Day(2005, 9, 24);

		assertEquals(TYPE, day.getType());
		assertEquals(Day.class, day.getValueClass());
		assertSerializedSame(day, 364);

		// test persistence
		assertEquals(DEFAULT, item.getDay());
		assertContains(TYPE.search(day.equal((Day)null)));
		assertContains(TYPE.search(day.isNull()));
		assertContains(item, item2, TYPE.search(day.notEqual((Day)null)));
		assertContains(item, item2, TYPE.search(day.isNotNull()));
		assertEquals(null, item.getOptionalDay());
		assertContains(item, item2, TYPE.search(optionalDay.equal((Day)null)));
		assertContains(item, item2, TYPE.search(optionalDay.isNull()));
		assertContains(TYPE.search(optionalDay.notEqual((Day)null)));
		assertContains(TYPE.search(optionalDay.isNotNull()));

		item.setDay(thisDay);
		assertEquals(thisDay, item.getDay());

		assertContains(thisDay, DEFAULT2, search(day));
		assertContains(thisDay, search(day, day.equal(thisDay)));
		assertContains(null, null, search(optionalDay));

		restartTransaction();
		assertEquals(thisDay, item.getDay());
		assertEquals(list(item), TYPE.search(day.equal(thisDay)));
		assertEquals(list(item), TYPE.search(day.greaterOrEqual(thisDay).and(day.lessOrEqual(thisDay))));
		assertEquals(list(item2), TYPE.search(day.notEqual(thisDay)));
		assertEquals(list(), TYPE.search(day.equal((Day)null)));
		assertEquals(list(), TYPE.search(day.isNull()));
		assertContains(item, item2, TYPE.search(day.notEqual((Day)null)));
		assertContains(item, item2, TYPE.search(day.isNotNull()));
		assertEquals(list(), TYPE.search(day.equal(beforeDay)));
		assertEquals(list(), TYPE.search(day.equal(nextDay)));
		assertEquals(list(), TYPE.search(day.greaterOrEqual(beforeDay).and(day.lessOrEqual(beforeDay))));
		assertEquals(list(), TYPE.search(day.greaterOrEqual(nextDay).and(day.lessOrEqual(nextDay))));
		assertEquals(list(item), TYPE.search(day.greaterOrEqual(thisDay).and(day.lessOrEqual(nextDay))));
		assertEquals(list(item), TYPE.search(day.greaterOrEqual(beforeDay).and(day.lessOrEqual(thisDay))));
		assertEquals(null, item.getOptionalDay());
		assertContains(item, item2, TYPE.search(optionalDay.equal((Day)null)));
		assertContains(item, item2, TYPE.search(optionalDay.isNull()));
		assertEquals(list(), TYPE.search(optionalDay.notEqual((Day)null)));
		assertEquals(list(), TYPE.search(optionalDay.isNotNull()));

		item.setDay(nextDay);
		restartTransaction();
		assertEquals(nextDay, item.getDay());

		final Day firstDay = new Day(1000, 1, 1);
		item.setDay(firstDay);
		restartTransaction();
		assertEquals(firstDay, item.getDay());

		final Day lastDay = new Day(9999, 12, 31);
		item.setDay(lastDay);
		restartTransaction();
		assertEquals(lastDay, item.getDay());

		final Day optionalDay = new Day(5555, 12, 31);
		item.setOptionalDay(optionalDay);
		assertEquals(optionalDay, item.getOptionalDay());
		restartTransaction();
		assertEquals(optionalDay, item.getOptionalDay());

		clock.add(988888888888l);
		item.touchOptionalDay(getTimeZone("Europe/Berlin"));
		clock.assertEmpty();
		clock.add(988888888888l);
		assertEquals(new Day(getTimeZone("Europe/Berlin")), item.getOptionalDay());

		item.setOptionalDay(null);
		assertEquals(null, item.getOptionalDay());
		restartTransaction();
		assertEquals(null, item.getOptionalDay());
	}

	public void testDayPartViews()
	{
		final DayPartView dayDpv = day.day();
		final DayPartView monthDpv = day.month();
		final DayPartView yearDpv = day.year();
		final DayPartView weekDpv = day.week();

		assertEquals("dayOfMonth(DayItem.day)", dayDpv.toString());
		assertEquals("month(DayItem.day)", monthDpv.toString());
		assertEquals("year(DayItem.day)", yearDpv.toString());
		assertEquals("weekOfYear(DayItem.day)", weekDpv.toString());

		final Day day1 = new Day(2006, 9, 23);
		final Day day2 = new Day(2006, 9, 22);
		final Day day3 = new Day(2006, 10, 23);

		item.setDay(day1);
		restartTransaction();
		assertEquals(23, dayDpv.get(item).intValue());
		assertEquals(9, monthDpv.get(item).intValue());
		assertEquals(2006, yearDpv.get(item).intValue());
		assertEquals(38, weekDpv.get(item).intValue());
		assertContains(TYPE.search(dayDpv.equal(1)));
		assertContains(item, TYPE.search(dayDpv.equal(23)));
		assertContains(TYPE.search(monthDpv.equal(10)));
		assertContains(item, TYPE.search(monthDpv.equal(9)));
		assertContains(TYPE.search(yearDpv.equal(2007)));
		assertContains(item, TYPE.search(yearDpv.equal(2006)));
		assertContains(TYPE.search(weekDpv.equal(1)));
		assertContains(item, TYPE.search(weekDpv.equal(38)));

		item.setDay(day2);
		restartTransaction();
		assertEquals(22, dayDpv.get(item).intValue());
		assertEquals(9, monthDpv.get(item).intValue());
		assertEquals(2006, yearDpv.get(item).intValue());
		assertEquals(38, weekDpv.get(item).intValue());
		assertContains(TYPE.search(dayDpv.equal(1)));
		assertContains(item, TYPE.search(dayDpv.equal(22)));
		assertContains(TYPE.search(monthDpv.equal(10)));
		assertContains(item, TYPE.search(monthDpv.equal(9)));
		assertContains(TYPE.search(yearDpv.equal(2007)));
		assertContains(item, TYPE.search(yearDpv.equal(2006)));
		assertContains(TYPE.search(weekDpv.equal(1)));
		assertContains(item, TYPE.search(weekDpv.equal(38)));

		item.setDay(day3);
		restartTransaction();
		assertEquals(23, dayDpv.get(item).intValue());
		assertEquals(10, monthDpv.get(item).intValue());
		assertEquals(2006, yearDpv.get(item).intValue());
		assertEquals(43, weekDpv.get(item).intValue());
		assertContains(TYPE.search(dayDpv.equal(1)));
		assertContains(item, TYPE.search(dayDpv.equal(23)));
		assertContains(TYPE.search(monthDpv.equal(9)));
		assertContains(item, TYPE.search(monthDpv.equal(10)));
		assertContains(TYPE.search(yearDpv.equal(2007)));
		assertContains(item, TYPE.search(yearDpv.equal(2006)));
		assertContains(TYPE.search(weekDpv.equal(1)));
		assertContains(item, TYPE.search(weekDpv.equal(43)));

		assertContains(23, new Query<>(dayDpv, TYPE, TYPE.thisFunction.equal(item)).search());
		assertContains(10, new Query<>(monthDpv, TYPE, TYPE.thisFunction.equal(item)).search());
		assertContains(2006, new Query<>(yearDpv, TYPE, TYPE.thisFunction.equal(item)).search());
		assertContains(43, new Query<>(weekDpv, TYPE, TYPE.thisFunction.equal(item)).search());

		final DayPartView optionalDayDpv = optionalDay.day();
		final DayPartView optionalMonthDpv = optionalDay.month();
		final DayPartView optionalYearDpv = optionalDay.year();
		final DayPartView optionalWeekDpv = optionalDay.week();

		assertContains(item, item2, TYPE.search(optionalDayDpv.isNull()));
		assertContains(TYPE.search(optionalDayDpv.isNotNull()));
		assertContains(item, item2, TYPE.search(optionalMonthDpv.isNull()));
		assertContains(TYPE.search(optionalMonthDpv.isNotNull()));
		assertContains(item, item2, TYPE.search(optionalYearDpv.isNull()));
		assertContains(TYPE.search(optionalYearDpv.isNotNull()));
		assertContains(item, item2, TYPE.search(optionalWeekDpv.isNull()));
		assertContains(TYPE.search(optionalWeekDpv.isNotNull()));

		final Day optionalDay = new Day(5555, 12, 31);
		item.setOptionalDay(optionalDay);
		restartTransaction();
		assertContains(item2, TYPE.search(optionalDayDpv.isNull()));
		assertContains(item, TYPE.search(optionalDayDpv.isNotNull()));
		assertContains(item2, TYPE.search(optionalMonthDpv.isNull()));
		assertContains(item, TYPE.search(optionalMonthDpv.isNotNull()));
		assertContains(item2, TYPE.search(optionalYearDpv.isNull()));
		assertContains(item, TYPE.search(optionalYearDpv.isNotNull()));
		assertContains(item2, TYPE.search(optionalWeekDpv.isNull()));
		assertContains(item, TYPE.search(optionalWeekDpv.isNotNull()));
	}

	@SuppressWarnings({"unchecked", "rawtypes"}) // OK: test bad API usage
	public void testUnchecked()
	{
		try
		{
			item.set((FunctionField)day, Integer.valueOf(10));
			fail();
		}
		catch(final ClassCastException e)
		{
			assertEquals("expected a " + Day.class.getName() + ", but was a " + Integer.class.getName() + " for " + day + '.', e.getMessage());
		}
	}

	protected static List<? extends Day> search(final DayField selectField)
	{
		return search(selectField, null);
	}

	protected static List<? extends Day> search(final DayField selectField, final Condition condition)
	{
		return new Query<>(selectField, condition).search();
	}

	public void testSchema()
	{
		assertSchema();
	}
}
