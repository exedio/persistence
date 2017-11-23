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
import static com.exedio.cope.tojunit.Assert.assertContains;
import static com.exedio.cope.tojunit.Assert.list;
import static com.exedio.cope.util.TimeZoneStrict.getTimeZone;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.Assert.fail;

import com.exedio.cope.junit.AbsoluteMockClockStrategy;
import com.exedio.cope.tojunit.ClockRule;
import com.exedio.cope.tojunit.MainRule;
import com.exedio.cope.util.Day;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@MainRule.Tag
public class DayFieldTest extends TestWithEnvironment
{
	public static final Model MODEL = new Model(TYPE);

	static
	{
		MODEL.enableSerialization(DayFieldTest.class, "MODEL");
	}

	private final AbsoluteMockClockStrategy clock = new AbsoluteMockClockStrategy();
	private final ClockRule clockRule = new ClockRule();

	DayItem item, item2;
	static final Day DEFAULT = new Day(2005, 8, 14);
	static final Day DEFAULT2 = new Day(2005, 8, 15);

	public DayFieldTest()
	{
		super(MODEL);
	}

	@BeforeEach public final void setUp()
	{
		item = new DayItem(DEFAULT);
		item2 = new DayItem(DEFAULT2);
		clockRule.override(clock);
	}

	@Test void testIt()
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
		assertEquals(new Day(988888888888l, getTimeZone("Europe/Berlin")), item.getOptionalDay());

		item.setOptionalDay(null);
		assertEquals(null, item.getOptionalDay());
		restartTransaction();
		assertEquals(null, item.getOptionalDay());
	}

	@Test void testDayPartViews()
	{
		final DayPartView dayDpv = day.dayOfMonth();
		final DayPartView monthDpv = day.month();
		final DayPartView yearDpv = day.year();
		final DayPartView weekDpv = day.weekOfYear();

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

		final DayPartView optionalDayDpv = optionalDay.dayOfMonth();
		final DayPartView optionalMonthDpv = optionalDay.month();
		final DayPartView optionalYearDpv = optionalDay.year();
		final DayPartView optionalWeekDpv = optionalDay.weekOfYear();

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

	@Test void testDayPartViewsWeekAroundNewYear()
	{
		assertWeek(new Day(2000, 12, 30), 52); // Saturday
		assertWeek(new Day(2000, 12, 31), 52); // Sunday
		assertWeek(new Day(2001,  1,  1),  1); // Monday
		assertWeek(new Day(2001,  1,  2),  1); // Tuesday
		assertWeek(new Day(2001,  1,  3),  1); // Wednesday
		assertWeek(new Day(2001,  1,  4),  1); // Thursday

		assertWeek(new Day(2001, 12, 29), 52); // Saturday
		assertWeek(new Day(2001, 12, 30), 52); // Sunday
		assertWeek(new Day(2001, 12, 31),  1); // Monday
		assertWeek(new Day(2002,  1,  1),  1); // Tuesday
		assertWeek(new Day(2002,  1,  2),  1); // Wednesday
		assertWeek(new Day(2002,  1,  3),  1); // Thursday

		assertWeek(new Day(2002, 12, 27), 52); // Friday
		assertWeek(new Day(2002, 12, 28), 52); // Saturday
		assertWeek(new Day(2002, 12, 29), 52); // Sunday
		assertWeek(new Day(2002, 12, 30),  1); // Monday
		assertWeek(new Day(2002, 12, 31),  1); // Tuesday
		assertWeek(new Day(2003,  1,  1),  1); // Wednesday
		assertWeek(new Day(2003,  1,  2),  1); // Thursday

		assertWeek(new Day(2003, 12, 27), 52); // Saturday
		assertWeek(new Day(2003, 12, 28), 52); // Sunday
		assertWeek(new Day(2003, 12, 29),  1); // Monday
		assertWeek(new Day(2003, 12, 30),  1); // Tuesday
		assertWeek(new Day(2003, 12, 31),  1); // Wednesday
		assertWeek(new Day(2004,  1,  1),  1); // Thursday
		assertWeek(new Day(2004,  1,  2),  1); // Friday

		assertWeek(new Day(1998, 12, 30), 53); // Wednesday
		assertWeek(new Day(1998, 12, 31), 53); // Thursday
		assertWeek(new Day(1999,  1,  1), 53); // Friday
		assertWeek(new Day(1999,  1,  2), 53); // Saturday
		assertWeek(new Day(1999,  1,  3), 53); // Sunday
		assertWeek(new Day(1999,  1,  4),  1); // Monday
		assertWeek(new Day(1999,  1,  5),  1); // Tuesday

		assertWeek(new Day(2004, 12, 31), 53); // Friday
		assertWeek(new Day(2005,  1,  1), 53); // Saturday
		assertWeek(new Day(2005,  1,  2), 53); // Sunday
		assertWeek(new Day(2005,  1,  3),  1); // Monday
		assertWeek(new Day(2005,  1,  4),  1); // Tuesday

		assertWeek(new Day(2005, 12, 31), 52); // Saturday
		assertWeek(new Day(2006,  1,  1), 52); // Sunday
		assertWeek(new Day(2006,  1,  2),  1); // Monday
		assertWeek(new Day(2006,  1,  3),  1); // Tuesday
	}

	private void assertWeek(final Day value, final int week)
	{
		final DayPartView view = day.weekOfYear();
		item.setDay(value);
		assertEquals(week, view.get(item).intValue());
		assertContains(week, new Query<>(view, TYPE, TYPE.thisFunction.equal(item)).search());
	}

	@SuppressWarnings({"unchecked", "rawtypes"}) // OK: test bad API usage
	@Test void testUnchecked()
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

	protected static List<Day> search(final DayField selectField)
	{
		return search(selectField, null);
	}

	protected static List<Day> search(final DayField selectField, final Condition condition)
	{
		return new Query<>(selectField, condition).search();
	}

	@Test void testSchema()
	{
		assertSchema();
	}
}
