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

import static com.exedio.cope.DayFieldTest.MODEL;
import static com.exedio.cope.DayItem.day;
import static com.exedio.cope.util.TimeZoneStrict.getTimeZone;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.tojunit.MainRule;
import com.exedio.cope.tojunit.TimeZoneDefaultRule;
import com.exedio.cope.util.Day;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@MainRule.Tag
@ExtendWith(TimeZoneDefaultRule.class)
public class DayFieldDefaultTimeZoneSwitchTest extends TestWithEnvironment
{
	public DayFieldDefaultTimeZoneSwitchTest()
	{
		super(MODEL);
	}


	@Test void testGMTBerlin(final TimeZoneDefaultRule tzd)
	{
		test(tzd, "GMT", "Europe/Berlin");
	}

	@Test void testGMTMoscow(final TimeZoneDefaultRule tzd)
	{
		test(tzd, "GMT", "Europe/Moscow");
	}

	@Test void testGMTKiritimati(final TimeZoneDefaultRule tzd)
	{
		test(tzd, "GMT", "Pacific/Kiritimati");
	}

	@Test void testGMTCanada(final TimeZoneDefaultRule tzd)
	{
		test(tzd,
				"GMT", "Canada/Mountain");
	}

	@Test void testBerlinGMT(final TimeZoneDefaultRule tzd)
	{
		test(tzd,
				"Europe/Berlin", "GMT");
	}

	@Test void testKiritimatiGMT(final TimeZoneDefaultRule tzd)
	{
		test(tzd,
				"Pacific/Kiritimati", "GMT");
	}

	@Test void testCanadaGMT(final TimeZoneDefaultRule tzd)
	{
		test(tzd,
				"Canada/Mountain", "GMT");
	}

	@Test void testKiritimatiCanada(final TimeZoneDefaultRule tzd)
	{
		test(tzd,
				"Pacific/Kiritimati", "Canada/Mountain");
	}

	@Test void testCanadaKiritimati(final TimeZoneDefaultRule tzd)
	{
		test(tzd,
				"Canada/Mountain", "Pacific/Kiritimati");
	}

	@Test void testBerlinLondon(final TimeZoneDefaultRule tzd)
	{
		test(tzd,
				"Europe/Berlin", "Europe/London");
	}

	@Test void testLondonBerlin(final TimeZoneDefaultRule tzd)
	{
		test(tzd, "Europe/London", "Europe/Berlin");
	}


	private void test(
			final TimeZoneDefaultRule tzd,
			final String from,
			final String to)
	{
		final Day winter = new Day(2005, 12, 20);
		final Day summer = new Day(2005,  8, 10);
		for(final Day value : new Day[]{winter, summer})
		{
			tzd.set(getTimeZone(from));
			clearAndFlush();
			final DayItem item = new DayItem(value);

			clear();
			assertIt(from, value, item);

			tzd.set(getTimeZone(to));
			clear();
			assertIt(from + "->" + to, value, item);

			clearAndFlush();
			assertIt(from + "->" + to + " flushed", value, item);

			item.deleteCopeItem();
		}
	}

	private void clear()
	{
		restartTransaction(model::clearCache);
	}

	private void clearAndFlush()
	{
		restartTransaction(() ->
		{
			model.clearCache();
			model.flushConnectionPool();
		});
	}

	private static void assertIt(final String heading, final Day value, final DayItem item)
	{
		assertAll(
				heading,
				() -> assertEquals(value, item.getDay(), "item"),
				() -> assertEquals(value, new Query<>(day, (Condition)null).searchSingleton(), "query"));
	}
}
