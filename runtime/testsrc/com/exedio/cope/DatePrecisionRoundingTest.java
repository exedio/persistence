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

import static com.exedio.cope.DateField.Precision.ZONE;
import static com.exedio.cope.DatePrecisionConditionTest.date;
import static com.exedio.cope.util.TimeZoneStrict.getTimeZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import com.exedio.cope.DateField.Precision;
import com.exedio.cope.DateField.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import org.junit.Test;

public class DatePrecisionRoundingTest
{
	@Test public void testMillis()
	{
		assertRound(Precision.MILLI,
				date(0, 15, 44, 55, 66),
				date(0, 15, 44, 55, 66),
				date(0, 15, 44, 55, 66));
	}

	@Test public void testSeconds()
	{
		assertRound(Precision.SECOND,
				date(9, 15, 44, 55, 66),
				date(9, 15, 44, 55,  0),
				date(9, 15, 44, 56,  0));
		assertRound(Precision.SECOND,
				date(9, 15, 44, 55,  0),
				date(9, 15, 44, 55,  0),
				date(9, 15, 44, 55,  0));
		assertRound(Precision.SECOND,
				date(9, 15, 44, 55,999),
				date(9, 15, 44, 55,  0),
				date(9, 15, 44, 56,  0));
		assertRound(Precision.SECOND,
				date(0,  0,  0, 55, 66),
				date(0,  0,  0, 55,  0),
				date(0,  0,  0, 56,  0));
		assertRound(Precision.SECOND,
				date(0,  0,  0, 55,  0),
				date(0,  0,  0, 55,  0),
				date(0,  0,  0, 55,  0));
		assertRound(Precision.SECOND,
				date(0,  0,  0, 55,999),
				date(0,  0,  0, 55,  0),
				date(0,  0,  0, 56,  0));
	}

	@Test public void testMinutes()
	{
		assertRound(Precision.MINUTE,
				date(9, 15, 44, 55, 66),
				date(9, 15, 44,  0,  0),
				date(9, 15, 45,  0,  0));
		assertRound(Precision.MINUTE,
				date(9, 15, 44, 55,  0),
				date(9, 15, 44,  0,  0),
				date(9, 15, 45,  0,  0));
		assertRound(Precision.MINUTE,
				date(9, 15, 44,  0, 66),
				date(9, 15, 44,  0,  0),
				date(9, 15, 45,  0,  0));
		assertRound(Precision.MINUTE,
				date(9, 15, 44,  0,  0),
				date(9, 15, 44,  0,  0),
				date(9, 15, 44,  0,  0));
		assertRound(Precision.MINUTE,
				date(9, 15, 44, 59,999),
				date(9, 15, 44,  0,  0),
				date(9, 15, 45,  0,  0));
		assertRound(Precision.MINUTE,
				date(0,  0, 44, 55, 66),
				date(0,  0, 44,  0,  0),
				date(0,  0, 45,  0,  0));
		assertRound(Precision.MINUTE,
				date(0,  0, 44, 55,  0),
				date(0,  0, 44,  0,  0),
				date(0,  0, 45,  0,  0));
		assertRound(Precision.MINUTE,
				date(0,  0, 44,  0, 66),
				date(0,  0, 44,  0,  0),
				date(0,  0, 45,  0,  0));
		assertRound(Precision.MINUTE,
				date(0,  0, 44,  0,  0),
				date(0,  0, 44,  0,  0),
				date(0,  0, 44,  0,  0));
		assertRound(Precision.MINUTE,
				date(0,  0, 44, 59,999),
				date(0,  0, 44,  0,  0),
				date(0,  0, 45,  0,  0));
	}

	@Test public void testHours()
	{
		assertRound(Precision.HOUR,
				date(9, 15, 44, 55, 66),
				date(9, 15,  0,  0,  0),
				date(9, 16,  0,  0,  0));
		assertRound(Precision.HOUR,
				date(9, 15, 44,  0,  0),
				date(9, 15,  0,  0,  0),
				date(9, 16,  0,  0,  0));
		assertRound(Precision.HOUR,
				date(9, 15,  0,  0, 66),
				date(9, 15,  0,  0,  0),
				date(9, 16,  0,  0,  0));
		assertRound(Precision.HOUR,
				date(9, 15,  0,  0,  0),
				date(9, 15,  0,  0,  0),
				date(9, 15,  0,  0,  0));
		assertRound(Precision.HOUR,
				date(9, 15, 59, 59,999),
				date(9, 15,  0,  0,  0),
				date(9, 16,  0,  0,  0));
		assertRound(Precision.HOUR,
				date(0, 15, 44, 55, 66),
				date(0, 15,  0,  0,  0),
				date(0, 16,  0,  0,  0));
		assertRound(Precision.HOUR,
				date(0, 15, 44,  0,  0),
				date(0, 15,  0,  0,  0),
				date(0, 16,  0,  0,  0));
		assertRound(Precision.HOUR,
				date(0, 15,  0,  0, 66),
				date(0, 15,  0,  0,  0),
				date(0, 16,  0,  0,  0));
		assertRound(Precision.HOUR,
				date(0, 15,  0,  0,  0),
				date(0, 15,  0,  0,  0),
				date(0, 15,  0,  0,  0));
		assertRound(Precision.HOUR,
				date(0, 15, 59, 59,999),
				date(0, 15,  0,  0,  0),
				date(0, 16,  0,  0,  0));
	}

	@Test public void testNull()
	{
		assertRound(Precision.MILLI , null, null, null);
		assertRound(Precision.SECOND, null, null, null);
		assertRound(Precision.MINUTE, null, null, null);
		assertRound(Precision.HOUR  , null, null, null);
	}

	@Test public void testEpochBefore() throws ParseException
	{
		assertRound(Precision.MILLI,
				dateS("1966-05-23 15:44:55.066"),
				dateS("1966-05-23 15:44:55.066"),
				dateS("1966-05-23 15:44:55.066"));
		assertRound(Precision.SECOND,
				dateS("1966-05-23 15:44:55.066"),
				dateS("1966-05-23 15:44:55.000"),
				dateS("1966-05-23 15:44:56.000"));
		assertRound(Precision.MINUTE,
				dateS("1966-05-23 15:44:55.066"),
				dateS("1966-05-23 15:44:00.000"),
				dateS("1966-05-23 15:45:00.000"));
		assertRound(Precision.HOUR,
				dateS("1966-05-23 15:44:55.066"),
				dateS("1966-05-23 15:00:00.000"),
				dateS("1966-05-23 16:00:00.000"));
	}

	@Test public void testEpochAfter() throws ParseException
	{
		assertRound(Precision.MILLI,
				dateS("1986-05-23 15:44:55.066"),
				dateS("1986-05-23 15:44:55.066"),
				dateS("1986-05-23 15:44:55.066"));
		assertRound(Precision.SECOND,
				dateS("1986-05-23 15:44:55.066"),
				dateS("1986-05-23 15:44:55.000"),
				dateS("1986-05-23 15:44:56.000"));
		assertRound(Precision.MINUTE,
				dateS("1986-05-23 15:44:55.066"),
				dateS("1986-05-23 15:44:00.000"),
				dateS("1986-05-23 15:45:00.000"));
		assertRound(Precision.HOUR,
				dateS("1986-05-23 15:44:55.066"),
				dateS("1986-05-23 15:00:00.000"),
				dateS("1986-05-23 16:00:00.000"));
	}

	private static Date dateS(final String date) throws ParseException
	{
		final SimpleDateFormat result = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		result.setTimeZone(ZONE);
		result.setLenient(false);
		return result.parse(date);
	}

	@Test public void testFracHourZone() throws ParseException
	{
		assertRound(Precision.MILLI,
				dateI("1986-05-23 15:44:55.066"),
				dateI("1986-05-23 15:44:55.066"),
				dateI("1986-05-23 15:44:55.066"));
		assertRound(Precision.SECOND,
				dateI("1986-05-23 15:44:55.066"),
				dateI("1986-05-23 15:44:55.000"),
				dateI("1986-05-23 15:44:56.000"));
		assertRound(Precision.MINUTE,
				dateI("1986-05-23 15:44:55.066"),
				dateI("1986-05-23 15:44:00.000"),
				dateI("1986-05-23 15:45:00.000"));
		assertRound(Precision.HOUR,
				dateI("1986-05-23 15:44:55.066"),
				dateI("1986-05-23 15:30:00.000"),
				dateI("1986-05-23 16:30:00.000"));
	}

	private static Date dateI(final String date) throws ParseException
	{
		final SimpleDateFormat result = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		result.setTimeZone(getTimeZone("IST")); // Indian Standard Time = UTC+5h30min
		result.setLenient(false);
		return result.parse(date);
	}

	private static void assertRound(
			final Precision precision,
			final Date origin,
			final Date past,
			final Date future)
	{
		assertEquals("past"  , past  , precision.round(origin, RoundingMode.PAST  ));
		assertEquals("future", future, precision.round(origin, RoundingMode.FUTURE));

		if(Objects.equals(past, future))
		{
			assertEquals(past  , origin);
			assertEquals(future, origin);
		}
	}

	@Test public void testMap()
	{
		final DateField f = new DateField().precisionMinute();
		final Date value  = date(9, 15, 44, 55, 66);
		final Date past   = date(9, 15, 44,  0,  0);
		final Date future = date(9, 15, 45,  0,  0);

		assertMap(f, value , f.map(value));
		assertMap(f, past  , f.mapRounded(value, RoundingMode.PAST  ));
		assertMap(f, future, f.mapRounded(value, RoundingMode.FUTURE));

		assertMap(f, null, f.map(null));
		assertMap(f, null, f.mapRounded(null, RoundingMode.PAST  ));
		assertMap(f, null, f.mapRounded(null, RoundingMode.FUTURE));
	}

	private static void assertMap(
			final DateField field,
			final Date value,
			final SetValue<Date> mapping)
	{
		assertSame  ("field", field, mapping.settable);
		assertEquals("value", value, mapping.value);
	}

	@Test public void testMapNullMode()
	{
		final DateField f = new DateField().precisionMinute();
		final Date value  = date(9, 15, 44, 55, 66);

		try
		{
			f.mapRounded(value, null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("roundingMode", e.getMessage());
		}
		try
		{
			f.mapRounded(null, null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("roundingMode", e.getMessage());
		}
	}

	@Test public void testIt() throws ParseException
	{
		final SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");
		df.setTimeZone(getTimeZone("Europe/Berlin"));
		assertEquals(1409234400000l, df.parse("28.08.2014 16:00:00.000").getTime());
		// leap second on June 30th, 2015
		assertEquals(1440770400000l, df.parse("28.08.2015 16:00:00.000").getTime());
		assertEquals(DateField.Precision.HOUR, DateField.Precision.HOUR);
	}
}
