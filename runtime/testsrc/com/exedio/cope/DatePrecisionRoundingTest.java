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

import static com.exedio.cope.DatePrecisionTest.date;
import static com.exedio.cope.util.TimeZoneStrict.getTimeZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import com.exedio.cope.DateField.Precision;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.junit.Test;

public class DatePrecisionRoundingTest
{
	@Test public void testMillis()
	{
		assertRound(Precision.Millis,
				date(0, 15, 44, 55, 66),
				date(0, 15, 44, 55, 66),
				date(0, 15, 44, 55, 66));
	}

	@Test public void testSeconds()
	{
		assertRound(Precision.Seconds,
				date(9, 15, 44, 55, 66),
				date(9, 15, 44, 55,  0),
				date(9, 15, 44, 56,  0));
		assertRound(Precision.Seconds,
				date(9, 15, 44, 55,  0),
				date(9, 15, 44, 55,  0),
				date(9, 15, 44, 55,  0));
		assertRound(Precision.Seconds,
				date(9, 15, 44, 55,999),
				date(9, 15, 44, 55,  0),
				date(9, 15, 44, 56,  0));
		assertRound(Precision.Seconds,
				date(0,  0,  0, 55, 66),
				date(0,  0,  0, 55,  0),
				date(0,  0,  0, 56,  0));
		assertRound(Precision.Seconds,
				date(0,  0,  0, 55,  0),
				date(0,  0,  0, 55,  0),
				date(0,  0,  0, 55,  0));
		assertRound(Precision.Seconds,
				date(0,  0,  0, 55,999),
				date(0,  0,  0, 55,  0),
				date(0,  0,  0, 56,  0));
	}

	@Test public void testMinutes()
	{
		assertRound(Precision.Minutes,
				date(9, 15, 44, 55, 66),
				date(9, 15, 44,  0,  0),
				date(9, 15, 45,  0,  0));
		assertRound(Precision.Minutes,
				date(9, 15, 44, 55,  0),
				date(9, 15, 44,  0,  0),
				date(9, 15, 45,  0,  0));
		assertRound(Precision.Minutes,
				date(9, 15, 44,  0, 66),
				date(9, 15, 44,  0,  0),
				date(9, 15, 45,  0,  0));
		assertRound(Precision.Minutes,
				date(9, 15, 44,  0,  0),
				date(9, 15, 44,  0,  0),
				date(9, 15, 44,  0,  0));
		assertRound(Precision.Minutes,
				date(9, 15, 44, 59,999),
				date(9, 15, 44,  0,  0),
				date(9, 15, 45,  0,  0));
		assertRound(Precision.Minutes,
				date(0,  0, 44, 55, 66),
				date(0,  0, 44,  0,  0),
				date(0,  0, 45,  0,  0));
		assertRound(Precision.Minutes,
				date(0,  0, 44, 55,  0),
				date(0,  0, 44,  0,  0),
				date(0,  0, 45,  0,  0));
		assertRound(Precision.Minutes,
				date(0,  0, 44,  0, 66),
				date(0,  0, 44,  0,  0),
				date(0,  0, 45,  0,  0));
		assertRound(Precision.Minutes,
				date(0,  0, 44,  0,  0),
				date(0,  0, 44,  0,  0),
				date(0,  0, 44,  0,  0));
		assertRound(Precision.Minutes,
				date(0,  0, 44, 59,999),
				date(0,  0, 44,  0,  0),
				date(0,  0, 45,  0,  0));
	}

	@Test public void testHours()
	{
		assertRound(Precision.Hours,
				date(9, 15, 44, 55, 66),
				date(9, 15,  0,  0,  0),
				date(9, 16,  0,  0,  0));
		assertRound(Precision.Hours,
				date(9, 15, 44,  0,  0),
				date(9, 15,  0,  0,  0),
				date(9, 16,  0,  0,  0));
		assertRound(Precision.Hours,
				date(9, 15,  0,  0, 66),
				date(9, 15,  0,  0,  0),
				date(9, 16,  0,  0,  0));
		assertRound(Precision.Hours,
				date(9, 15,  0,  0,  0),
				date(9, 15,  0,  0,  0),
				date(9, 15,  0,  0,  0));
		assertRound(Precision.Hours,
				date(9, 15, 59, 59,999),
				date(9, 15,  0,  0,  0),
				date(9, 16,  0,  0,  0));
		assertRound(Precision.Hours,
				date(0, 15, 44, 55, 66),
				date(0, 15,  0,  0,  0),
				date(0, 16,  0,  0,  0));
		assertRound(Precision.Hours,
				date(0, 15, 44,  0,  0),
				date(0, 15,  0,  0,  0),
				date(0, 16,  0,  0,  0));
		assertRound(Precision.Hours,
				date(0, 15,  0,  0, 66),
				date(0, 15,  0,  0,  0),
				date(0, 16,  0,  0,  0));
		assertRound(Precision.Hours,
				date(0, 15,  0,  0,  0),
				date(0, 15,  0,  0,  0),
				date(0, 15,  0,  0,  0));
		assertRound(Precision.Hours,
				date(0, 15, 59, 59,999),
				date(0, 15,  0,  0,  0),
				date(0, 16,  0,  0,  0));
	}

	@Test public void testNull()
	{
		assertRound(Precision.Millis , null, null, null);
		assertRound(Precision.Seconds, null, null, null);
		assertRound(Precision.Minutes, null, null, null);
		assertRound(Precision.Hours  , null, null, null);
	}

	private static void assertRound(
			final Precision precision,
			final Date origin,
			final Date down,
			final Date up)
	{
		assertEquals("down", down, precision.round(origin, false));
		assertEquals("up"  , up  , precision.round(origin, true));
	}

	@Test public void testMap()
	{
		final DateField f = new DateField().minutes();
		final Date value = date(9, 15, 44, 55, 66);
		final Date down  = date(9, 15, 44,  0,  0);
		final Date up    = date(9, 15, 45,  0,  0);

		assertMap(f, value, f.map(value));
		assertMap(f, down , f.mapAndRoundDown(value));
		assertMap(f, up   , f.mapAndRoundUp  (value));

		assertMap(f, null, f.map(null));
		assertMap(f, null, f.mapAndRoundDown(null));
		assertMap(f, null, f.mapAndRoundUp  (null));
	}

	private static void assertMap(
			final DateField field,
			final Date value,
			final SetValue<Date> mapping)
	{
		assertSame  ("field", field, mapping.settable);
		assertEquals("value", value, mapping.value);
	}

	@Test public void testIt() throws ParseException
	{
		final SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");
		df.setTimeZone(getTimeZone("Europe/Berlin"));
		assertEquals(1409234400000l, df.parse("28.08.2014 16:00:00.000").getTime());
		// leap second on June 30th, 2015
		assertEquals(1440770400000l, df.parse("28.08.2015 16:00:00.000").getTime());
		assertEquals(DateField.Precision.Hours, DateField.Precision.Hours);
	}
}
