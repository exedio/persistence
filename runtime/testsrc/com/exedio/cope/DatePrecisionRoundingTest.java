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

import static com.exedio.cope.DatePrecisionConditionTest.date;
import static com.exedio.cope.util.TimeZoneStrict.getTimeZone;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.DateField.Precision;
import com.exedio.cope.DateField.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import org.junit.jupiter.api.Test;

public class DatePrecisionRoundingTest
{
	@Test void testMillis()
	{
		assertRound(Precision.MILLI,
				date(0, 15, 44, 55, 66),
				date(0, 15, 44, 55, 66),
				date(0, 15, 44, 55, 66));
	}

	@Test void testSeconds()
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

	@Test void testMinutes()
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

	@Test void testHours()
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

	@Test void testNull()
	{
		assertRound(Precision.MILLI , null, null, null);
		assertRound(Precision.SECOND, null, null, null);
		assertRound(Precision.MINUTE, null, null, null);
		assertRound(Precision.HOUR  , null, null, null);
	}

	@Test void testEpochBefore() throws ParseException
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

	@Test void testEpochAfter() throws ParseException
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
		final SimpleDateFormat result = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH);
		result.setTimeZone(getTimeZone("GMT"));
		result.setLenient(false);
		return result.parse(date);
	}

	@Test void testFracHourZone() throws ParseException
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
		final SimpleDateFormat result = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH);
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
		assertEquals(past,   precision.round(origin, RoundingMode.PAST  , null, null), "past");
		assertEquals(future, precision.round(origin, RoundingMode.FUTURE, null, null), "future");

		if(Objects.equals(past, future))
		{
			assertEquals(past  , origin);
			assertEquals(future, origin);
			assertEquals(origin, precision.round(origin, RoundingMode.UNNECESSARY, null, null), "UNNECESSARY");
		}
		else
		{
			try
			{
				precision.round(origin, RoundingMode.UNNECESSARY, null, null);
				fail("UNNECESSARY should have failed");
			}
			catch(final DatePrecisionViolationException e)
			{
				assertEquals(null, e.getItem());
				assertEquals(null, e.getFeature());
				assertEquals(precision, e.getPrecision());
				assertEquals(origin, e.getValue());
				assertEquals(past  , e.getValueAllowedInPast());
				assertEquals(future, e.getValueAllowedInFuture());
				assertNotNull(e.getMessage());
			}
		}
	}

	@Test void testMap()
	{
		final DateField f = new DateField().precisionMinute();
		final Date value  = date(9, 15, 44, 55, 66);
		final Date past   = date(9, 15, 44,  0,  0);
		final Date future = date(9, 15, 45,  0,  0);

		assertMap(f, value , SetValue.map(f, value));
		assertMap(f, past  , f.mapRounded(value, RoundingMode.PAST  ));
		assertMap(f, future, f.mapRounded(value, RoundingMode.FUTURE));
		try
		{
			f.mapRounded(value, RoundingMode.UNNECESSARY);
			fail();
		}
		catch(final DatePrecisionViolationException e)
		{
			assertEquals(null, e.getItem());
			assertEquals(f, e.getFeature());
			assertEquals(Precision.MINUTE, e.getPrecision());
			assertEquals(value, e.getValue());
			assertEquals(past  , e.getValueAllowedInPast  ());
			assertEquals(future, e.getValueAllowedInFuture());
		}

		assertMap(f, null, SetValue.map(f, null));
		assertMap(f, null, f.mapRounded(null, RoundingMode.PAST  ));
		assertMap(f, null, f.mapRounded(null, RoundingMode.FUTURE));
		assertMap(f, null, f.mapRounded(null, RoundingMode.UNNECESSARY));

		assertMap(f, past, SetValue.map(f, past));
		assertMap(f, past, f.mapRounded(past, RoundingMode.PAST  ));
		assertMap(f, past, f.mapRounded(past, RoundingMode.FUTURE));
		assertMap(f, past, f.mapRounded(past, RoundingMode.UNNECESSARY));
	}

	private static void assertMap(
			final DateField field,
			final Date value,
			final SetValue<Date> mapping)
	{
		assertSame  (field, mapping.settable, "field");
		assertEquals(value, mapping.value,    "value");
	}

	@Test void testMapNullMode()
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

	@Test void testIt() throws ParseException
	{
		final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH);
		df.setTimeZone(getTimeZone("Europe/Berlin"));
		assertEquals(1409234400000l, df.parse("2014-08-28 16:00:00.000").getTime());
		// leap second on June 30th, 2015
		assertEquals(1440770400000l, df.parse("2015-08-28 16:00:00.000").getTime());
	}
}
