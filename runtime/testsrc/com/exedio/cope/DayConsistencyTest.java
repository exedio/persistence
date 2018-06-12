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

import static java.time.LocalTime.MIDNIGHT;
import static java.time.ZoneOffset.UTC;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.util.TimeZoneStrict;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import org.junit.jupiter.api.Test;

public class DayConsistencyTest
{
	@Test void test1900Parse() throws ParseException
	{
		final Instant instant = LocalDateTime.of(LocalDate.of(1900, 1, 23), MIDNIGHT).toInstant(UTC);

		final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH);
		format.setTimeZone(TimeZoneStrict.getTimeZone("UTC"));

		assertEquals(
				instant.toEpochMilli(),
				format.parse("1900-01-23 00:00:00.000").getTime());
	}

	@Test void test1900Calendar()
	{
		final Instant instant = LocalDateTime.of(LocalDate.of(1900, 1, 23), MIDNIGHT).toInstant(UTC);

		final GregorianCalendar cal = new GregorianCalendar(TimeZoneStrict.getTimeZone("UTC"), Locale.ENGLISH);
		cal.set(Calendar.YEAR, 1900);
		cal.set(Calendar.MONTH, 0);
		cal.set(Calendar.DAY_OF_MONTH, 23);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		assertEquals(
				instant.toEpochMilli(),
				cal.getTimeInMillis());
	}

	@Test void test1600Parse() throws ParseException
	{
		final Instant instant = LocalDateTime.of(LocalDate.of(1600, 1, 23), MIDNIGHT).toInstant(UTC);

		final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH);
		format.setTimeZone(TimeZoneStrict.getTimeZone("UTC"));

		assertEquals(
				instant.toEpochMilli(),
				format.parse("1600-01-23 00:00:00.000").getTime());
	}

	@Test void test1600Calendar()
	{
		final Instant instant = LocalDateTime.of(LocalDate.of(1600, 1, 23), MIDNIGHT).toInstant(UTC);

		final GregorianCalendar cal = new GregorianCalendar(TimeZoneStrict.getTimeZone("UTC"), Locale.ENGLISH);
		cal.set(Calendar.YEAR, 1600);
		cal.set(Calendar.MONTH, 0);
		cal.set(Calendar.DAY_OF_MONTH, 23);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		assertEquals(
				instant.toEpochMilli(),
				cal.getTimeInMillis());
	}

	@Test void test1588Parse() throws ParseException
	{
		final Instant instant = LocalDateTime.of(LocalDate.of(1588, 1, 23), MIDNIGHT).toInstant(UTC);

		final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH);
		format.setTimeZone(TimeZoneStrict.getTimeZone("UTC"));

		assertEquals(
				instant.toEpochMilli(),
				format.parse("1588-01-23 00:00:00.000").getTime());
	}

	@Test void test1588Calendar()
	{
		final Instant instant = LocalDateTime.of(LocalDate.of(1588, 1, 23), MIDNIGHT).toInstant(UTC);

		final GregorianCalendar cal = new GregorianCalendar(TimeZoneStrict.getTimeZone("UTC"), Locale.ENGLISH);
		cal.set(Calendar.YEAR, 1588);
		cal.set(Calendar.MONTH, 0);
		cal.set(Calendar.DAY_OF_MONTH, 23);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		assertEquals(
				instant.toEpochMilli(),
				cal.getTimeInMillis());
	}

	@Test void test1500Parse() throws ParseException
	{
		final Instant instant = LocalDateTime.of(LocalDate.of(1500, 1, 23), MIDNIGHT).toInstant(UTC);

		final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH);
		format.setTimeZone(TimeZoneStrict.getTimeZone("UTC"));

		assertEquals(
				instant.toEpochMilli(),
				format.parse("1500-01-14 00:00:00.000").getTime()); // TODO don't know why day of month is not 23
	}

	@Test void test1500Calendar()
	{
		final Instant instant = LocalDateTime.of(LocalDate.of(1500, 1, 23), MIDNIGHT).toInstant(UTC);

		final GregorianCalendar cal = new GregorianCalendar(TimeZoneStrict.getTimeZone("UTC"), Locale.ENGLISH);
		cal.set(Calendar.YEAR, 1500);
		cal.set(Calendar.MONTH, 0);
		cal.set(Calendar.DAY_OF_MONTH, 14); // TODO don't know why day of month is not 23
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		assertEquals(
				instant.toEpochMilli(),
				cal.getTimeInMillis());
	}
}
