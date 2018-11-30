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
import java.util.Date;
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

	@Test void testIt()
	{
		{
			final Date now = clock.add(1111);
			final DayFieldDefaultToNowItem item = new DayFieldDefaultToNowItem(
			);
			clock.assertEmpty();

			assertEquals(new Day(now, getTimeZone("Europe/Berlin")), item.getMandatory());
			assertEquals(new Day(now, getTimeZone("Europe/Berlin")), item.getOptional());
			assertEquals(null, item.getNone());
		}
		{
			final Date now = clock.add(2222);
			final DayFieldDefaultToNowItem item = new DayFieldDefaultToNowItem(
			);
			clock.assertEmpty();

			assertEquals(new Day(now, getTimeZone("Europe/Berlin")), item.getMandatory());
			assertEquals(new Day(now, getTimeZone("Europe/Berlin")), item.getOptional());
			assertEquals(null, item.getNone());
		}
		{
			clock.assertEmpty();
			final DayFieldDefaultToNowItem item = new DayFieldDefaultToNowItem(
					mandatory.map(day(2010, 1, 13)),
					optional.map(day(2010, 1, 14)),
					none.map(day(2010, 1, 15))
			);
			clock.assertEmpty();

			assertEquals(day(2010, 1, 13), item.getMandatory());
			assertEquals(day(2010, 1, 14), item.getOptional());
			assertEquals(day(2010, 1, 15), item.getNone());
		}
		{
			final Date now = clock.add(4444);
			final DayFieldDefaultToNowItem item = new DayFieldDefaultToNowItem(
					optional.map(null),
					none.map(null)
			);
			clock.assertEmpty();

			assertEquals(new Day(now, getTimeZone("Europe/Berlin")), item.getMandatory());
			assertEquals(null, item.getOptional());
			assertEquals(null, item.getNone());
		}
	}

	private static Day day(final int year, final int month, final int day)
	{
		return new Day(year, month, day);
	}
}
