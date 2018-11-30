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

import static com.exedio.cope.DayFieldDefaultToNowItem.mandatory;
import static com.exedio.cope.DayFieldDefaultToNowItem.none;
import static com.exedio.cope.DayFieldDefaultToNowItem.optional;
import static com.exedio.cope.util.TimeZoneStrict.getTimeZone;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.junit.AbsoluteMockClockStrategy;
import com.exedio.cope.tojunit.ClockRule;
import com.exedio.cope.tojunit.MainRule;
import com.exedio.cope.util.Day;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@MainRule.Tag
public class DayFieldDefaultToNowTest extends TestWithEnvironment
{
	public DayFieldDefaultToNowTest()
	{
		super(DayFieldDefaultToNowModelTest.MODEL);
	}

	private final AbsoluteMockClockStrategy clock = new AbsoluteMockClockStrategy();
	private final ClockRule clockRule = new ClockRule();


	@BeforeEach final void setUp()
	{
		clockRule.override(clock);
	}

	@Test void testNow()
	{
		clock.add(value1.getTimeInMillisFrom(getTimeZone("Europe/Berlin")));
		final DayFieldDefaultToNowItem item = new DayFieldDefaultToNowItem(
		);
		clock.assertEmpty();

		assertEquals(value1, item.getMandatory());
		assertEquals(value1, item.getOptional());
		assertEquals(null, item.getNone());
	}
	@Test void testNowOther()
	{
		clock.add(value2.getTimeInMillisFrom(getTimeZone("Europe/Berlin")));
		final DayFieldDefaultToNowItem item = new DayFieldDefaultToNowItem(
		);
		clock.assertEmpty();

		assertEquals(value2, item.getMandatory());
		assertEquals(value2, item.getOptional());
		assertEquals(null, item.getNone());
	}
	@Test void testSet()
	{
		clock.assertEmpty();
		final DayFieldDefaultToNowItem item = new DayFieldDefaultToNowItem(
				mandatory.map(value1),
				optional.map(value2),
				none.map(value3)
		);
		clock.assertEmpty();

		assertEquals(value1, item.getMandatory());
		assertEquals(value2, item.getOptional());
		assertEquals(value3, item.getNone());
	}
	@Test void testSetNull()
	{
		clock.add(value3.getTimeInMillisFrom(getTimeZone("Europe/Berlin")));
		final DayFieldDefaultToNowItem item = new DayFieldDefaultToNowItem(
				optional.map(null),
				none.map(null)
		);
		clock.assertEmpty();

		assertEquals(value3, item.getMandatory());
		assertEquals(null, item.getOptional());
		assertEquals(null, item.getNone());
	}

	private static final Day value1 = new Day(2014, 1,  3);
	private static final Day value2 = new Day(2015, 5, 14);
	private static final Day value3 = new Day(2018, 8, 25);
}
