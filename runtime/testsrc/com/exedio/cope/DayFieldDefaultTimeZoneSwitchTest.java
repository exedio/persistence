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
import org.junit.jupiter.api.BeforeEach;
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

	private boolean prep;
	private boolean mysqlx;

	@BeforeEach void setUp()
	{
		prep = !model.getConnectProperties().isSupportDisabledForPreparedStatements();
		mysqlx = mysql && !model.getEnvironmentInfo().getDriverName().startsWith("MariaDB");
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
				0,
				(mysqlx||hsqldb||postgresql) ? -1 : 0, // TODO is a bug
				(mysqlx) ? -1 : 0, // TODO is a bug
				"GMT", "Canada/Mountain");
	}

	@Test void testBerlinGMT(final TimeZoneDefaultRule tzd)
	{
		test(tzd,
				(mysqlx&&prep) ? -1 : 0, // TODO is a bug
				(mysqlx&&prep||hsqldb||postgresql) ? -1 : 0, // TODO is a bug
				(mysqlx&&prep) ? -1 : 0, // TODO is a bug
				"Europe/Berlin", "GMT");
	}

	@Test void testKiritimatiGMT(final TimeZoneDefaultRule tzd)
	{
		test(tzd,
				(mysqlx&&prep) ? -1 : 0, // TODO is a bug
				(mysqlx&&prep||hsqldb||postgresql) ? -1 : 0, // TODO is a bug
				(mysqlx&&prep) ? -1 : 0, // TODO is a bug
				"Pacific/Kiritimati", "GMT");
	}

	@Test void testCanadaGMT(final TimeZoneDefaultRule tzd)
	{
		test(tzd,
				mysqlx ? -1 : 0, // TODO is a bug
				0,  0,
				"Canada/Mountain", "GMT");
	}

	@Test void testKiritimatiCanada(final TimeZoneDefaultRule tzd)
	{
		test(tzd,
				(mysqlx&&prep) ? -1 : 0, // TODO is a bug
				(mysqlx&&prep) ? -2 : (mysqlx||hsqldb||postgresql) ? -1 : 0, // TODO is a bug
				(mysqlx&&prep) ? -2 : (mysqlx) ? -1 : 0, // TODO is a bug
				"Pacific/Kiritimati", "Canada/Mountain");
	}

	@Test void testCanadaKiritimati(final TimeZoneDefaultRule tzd)
	{
		test(tzd,
				(mysqlx) ? -1 : 0, // TODO is a bug
				0,  0,
				"Canada/Mountain", "Pacific/Kiritimati");
	}

	@Test void testBerlinLondon(final TimeZoneDefaultRule tzd)
	{
		test(tzd,
				(mysqlx&&prep) ? -1 : 0, // TODO is a bug
				(mysqlx&&prep||hsqldb||postgresql) ? -1 : 0, // TODO is a bug
				(mysqlx&&prep) ? -1 : 0, // TODO is a bug
				"Europe/Berlin", "Europe/London");
	}

	@Test void testLondonBerlin(final TimeZoneDefaultRule tzd)
	{
		test(tzd, "Europe/London", "Europe/Berlin"); // TODO see exception in method test
	}


	private void test(
			final TimeZoneDefaultRule tzd,
			final String from,
			final String to)
	{
		test(tzd, 0, 0, 0, from, to);
	}

	private void test(
			final TimeZoneDefaultRule tzd,
			int offsetWrong,
			int offsetWrongClear,
			int offsetWrongFlush,
			final String from,
			final String to)
	{
		final Day winter = new Day(2005, 12, 20);
		final Day summer = new Day(2005,  8, 10);
		for(final Day value : new Day[]{winter, summer})
		{
			// TODO is a bug
			if(mysqlx && prep &&
				"Europe/London".equals(from) &&
				summer.equals(value))
				offsetWrong = offsetWrongClear = offsetWrongFlush = -1;

			tzd.set(getTimeZone(from));
			clearAndFlush();
			final DayItem item = new DayItem(value);

			clear();
			assertIt(from, value.plusDays(offsetWrong), item);

			tzd.set(getTimeZone(to));
			clear();
			assertIt(
					from + "->" + to,
					value.plusDays(
							model.getConnectProperties().getConnectionPoolIdleLimit() > 0
									? offsetWrongClear
									: offsetWrongFlush),
					item);

			clearAndFlush();
			assertIt(
					from + "->" + to + " flushed",
					value.plusDays(offsetWrongFlush),
					item);

			item.deleteCopeItem();
		}
	}

	private void clear()
	{
		final String oldName = model.currentTransaction().getName();
		model.commit();

		model.clearCache();

		model.startTransaction(oldName+"-restart");
	}

	private void clearAndFlush()
	{
		final String oldName = model.currentTransaction().getName();
		model.commit();

		model.clearCache();
		model.flushConnectionPool();

		model.startTransaction(oldName+"-restart");
	}

	private static void assertIt(final String heading, final Day value, final DayItem item)
	{
		assertAll(
				heading,
				() -> assertEquals(value, item.getDay(), "item"),
				() -> assertEquals(value, new Query<>(day, (Condition)null).searchSingleton(), "query"));
	}
}
