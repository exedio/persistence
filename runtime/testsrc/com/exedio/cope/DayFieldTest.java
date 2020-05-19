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
import static com.exedio.cope.DayItem.mandatory;
import static com.exedio.cope.DayItem.optional;
import static com.exedio.cope.RuntimeAssert.assertSerializedSame;
import static com.exedio.cope.tojunit.Assert.assertContains;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static com.exedio.cope.tojunit.Assert.list;
import static com.exedio.cope.util.TimeZoneStrict.getTimeZone;
import static org.junit.jupiter.api.Assertions.assertEquals;

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

	@BeforeEach final void setUp()
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

		assertEquals(TYPE, mandatory.getType());
		assertEquals(Day.class, mandatory.getValueClass());
		assertSerializedSame(mandatory, 370);

		// test persistence
		assertEquals(DEFAULT, item.getMandatory());
		assertContains(TYPE.search(mandatory.equal((Day)null)));
		assertContains(TYPE.search(mandatory.isNull()));
		assertContains(item, item2, TYPE.search(mandatory.notEqual((Day)null)));
		assertContains(item, item2, TYPE.search(mandatory.isNotNull()));
		assertEquals(null, item.getOptional());
		assertContains(item, item2, TYPE.search(optional.equal((Day)null)));
		assertContains(item, item2, TYPE.search(optional.isNull()));
		assertContains(TYPE.search(optional.notEqual((Day)null)));
		assertContains(TYPE.search(optional.isNotNull()));

		item.setMandatory(thisDay);
		assertEquals(thisDay, item.getMandatory());

		assertContains(thisDay, DEFAULT2, search(mandatory));
		assertContains(thisDay, search(mandatory, mandatory.equal(thisDay)));
		assertContains(null, null, search(optional));

		restartTransaction();
		assertEquals(thisDay, item.getMandatory());
		assertEquals(list(item), TYPE.search(mandatory.equal(thisDay)));
		assertEquals(list(item), TYPE.search(mandatory.greaterOrEqual(thisDay).and(mandatory.lessOrEqual(thisDay))));
		assertEquals(list(item2), TYPE.search(mandatory.notEqual(thisDay)));
		assertEquals(list(), TYPE.search(mandatory.equal((Day)null)));
		assertEquals(list(), TYPE.search(mandatory.isNull()));
		assertContains(item, item2, TYPE.search(mandatory.notEqual((Day)null)));
		assertContains(item, item2, TYPE.search(mandatory.isNotNull()));
		assertEquals(list(), TYPE.search(mandatory.equal(beforeDay)));
		assertEquals(list(), TYPE.search(mandatory.equal(nextDay)));
		assertEquals(list(), TYPE.search(mandatory.greaterOrEqual(beforeDay).and(mandatory.lessOrEqual(beforeDay))));
		assertEquals(list(), TYPE.search(mandatory.greaterOrEqual(nextDay).and(mandatory.lessOrEqual(nextDay))));
		assertEquals(list(item), TYPE.search(mandatory.greaterOrEqual(thisDay).and(mandatory.lessOrEqual(nextDay))));
		assertEquals(list(item), TYPE.search(mandatory.greaterOrEqual(beforeDay).and(mandatory.lessOrEqual(thisDay))));
		assertEquals(null, item.getOptional());
		assertContains(item, item2, TYPE.search(optional.equal((Day)null)));
		assertContains(item, item2, TYPE.search(optional.isNull()));
		assertEquals(list(), TYPE.search(optional.notEqual((Day)null)));
		assertEquals(list(), TYPE.search(optional.isNotNull()));

		item.setMandatory(nextDay);
		restartTransaction();
		assertEquals(nextDay, item.getMandatory());

		final Day firstDay = new Day(1600, 1, 1);
		item.setMandatory(firstDay);
		restartTransaction();
		assertEquals(firstDay, item.getMandatory());

		final Day lastDay = new Day(9999, 12, 31);
		item.setMandatory(lastDay);
		restartTransaction();
		assertEquals(lastDay, item.getMandatory());

		final Day optionalDay = new Day(5555, 12, 31);
		item.setOptional(optionalDay);
		assertEquals(optionalDay, item.getOptional());
		restartTransaction();
		assertEquals(optionalDay, item.getOptional());

		clock.add(988888888888l);
		item.touchOptional(getTimeZone("Europe/Berlin"));
		clock.assertEmpty();
		assertEquals(new Day(988888888888l, getTimeZone("Europe/Berlin")), item.getOptional());

		item.setOptional(null);
		assertEquals(null, item.getOptional());
		restartTransaction();
		assertEquals(null, item.getOptional());
	}

	@Test void testDayPartViews()
	{
		final DayPartView dayDpv = mandatory.dayOfMonth();
		final DayPartView monthDpv = mandatory.month();
		final DayPartView yearDpv = mandatory.year();
		final DayPartView weekDpv = mandatory.weekOfYear();

		assertEquals("dayOfMonth(DayItem.mandatory)", dayDpv.toString());
		assertEquals("month(DayItem.mandatory)", monthDpv.toString());
		assertEquals("year(DayItem.mandatory)", yearDpv.toString());
		assertEquals("weekOfYear(DayItem.mandatory)", weekDpv.toString());

		final Day day1 = new Day(2006, 9, 23);
		final Day day2 = new Day(2006, 9, 22);
		final Day day3 = new Day(2006, 10, 23);

		item.setMandatory(day1);
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

		item.setMandatory(day2);
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

		item.setMandatory(day3);
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

		final DayPartView optionalDayDpv = optional.dayOfMonth();
		final DayPartView optionalMonthDpv = optional.month();
		final DayPartView optionalYearDpv = optional.year();
		final DayPartView optionalWeekDpv = optional.weekOfYear();

		assertContains(item, item2, TYPE.search(optionalDayDpv.isNull()));
		assertContains(TYPE.search(optionalDayDpv.isNotNull()));
		assertContains(item, item2, TYPE.search(optionalMonthDpv.isNull()));
		assertContains(TYPE.search(optionalMonthDpv.isNotNull()));
		assertContains(item, item2, TYPE.search(optionalYearDpv.isNull()));
		assertContains(TYPE.search(optionalYearDpv.isNotNull()));
		assertContains(item, item2, TYPE.search(optionalWeekDpv.isNull()));
		assertContains(TYPE.search(optionalWeekDpv.isNotNull()));

		final Day optionalDay = new Day(5555, 12, 31);
		item.setOptional(optionalDay);
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
		// The first week of the year always contains 4 January.
		// https://en.wikipedia.org/wiki/ISO_week_date

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
		assertWeek(new Day(2002,  1,  4),  1); // Friday

		assertWeek(new Day(2002, 12, 27), 52); // Friday
		assertWeek(new Day(2002, 12, 28), 52); // Saturday
		assertWeek(new Day(2002, 12, 29), 52); // Sunday
		assertWeek(new Day(2002, 12, 30),  1); // Monday
		assertWeek(new Day(2002, 12, 31),  1); // Tuesday
		assertWeek(new Day(2003,  1,  1),  1); // Wednesday
		assertWeek(new Day(2003,  1,  2),  1); // Thursday
		assertWeek(new Day(2003,  1,  3),  1); // Friday
		assertWeek(new Day(2003,  1,  4),  1); // Saturday

		assertWeek(new Day(2003, 12, 27), 52); // Saturday
		assertWeek(new Day(2003, 12, 28), 52); // Sunday
		assertWeek(new Day(2003, 12, 29),  1); // Monday
		assertWeek(new Day(2003, 12, 30),  1); // Tuesday
		assertWeek(new Day(2003, 12, 31),  1); // Wednesday
		assertWeek(new Day(2004,  1,  1),  1); // Thursday
		assertWeek(new Day(2004,  1,  2),  1); // Friday
		assertWeek(new Day(2004,  1,  3),  1); // Saturday
		assertWeek(new Day(2004,  1,  4),  1); // Sunday

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
		assertWeek(new Day(2006,  1,  4),  1); // Wednesday
	}

	private void assertWeek(final Day value, final int week)
	{
		final DayPartView view = mandatory.weekOfYear();
		item.setMandatory(value);
		assertEquals(week, view.get(item).intValue());
		assertContains(week, new Query<>(view, TYPE, TYPE.thisFunction.equal(item)).search());
	}

	@SuppressWarnings({"unchecked","rawtypes"}) // OK: test bad API usage
	@Test void testUnchecked()
	{
		assertFails(
				() -> item.set((FunctionField)mandatory, Integer.valueOf(10)),
				ClassCastException.class,
				"expected a " + Day.class.getName() + ", " +
				"but was a " + Integer.class.getName() + " " +
				"for " + mandatory + ".");
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
