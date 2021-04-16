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
import static com.exedio.cope.DatePrecisionItem.hours;
import static com.exedio.cope.DatePrecisionItem.millis;
import static com.exedio.cope.DatePrecisionItem.minutes;
import static com.exedio.cope.DatePrecisionItem.seconds;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.DateField.Precision;
import java.util.Date;
import org.junit.jupiter.api.Test;

public class DatePrecisionModelTest
{
	@Test void testConstrains()
	{
		assertEquals(false, Precision.MILLI .constrains());
		assertEquals(true , Precision.SECOND.constrains());
		assertEquals(true , Precision.MINUTE.constrains());
		assertEquals(true , Precision.HOUR  .constrains());
	}

	@Test void testGetPrecision()
	{
		assertEquals(Precision.MILLI , millis .getPrecision());
		assertEquals(Precision.SECOND, seconds.getPrecision());
		assertEquals(Precision.MINUTE, minutes.getPrecision());
		assertEquals(Precision.HOUR  , hours  .getPrecision());
	}

	@Test void testMillis()
	{
		assertOk(millis, date(0, 15, 44, 55,  66));
	}

	@Test void testSeconds()
	{
		assertOk   (seconds, date(0, 0, 0, 55,  0));
		assertFails(seconds,
				date(0, 0, 0, 55, 66), "1970-01-01 00:00:55.066 GMT (66)",
				date(0, 0, 0, 55,  0), "1970-01-01 00:00:55.000",
				date(0, 0, 0, 56,  0), "1970-01-01 00:00:56.000");
	}

	@Test void testMinutes()
	{
		assertOk   (minutes, date(0, 0, 45,  0,  0));
		assertFails(minutes,
				date(0, 0, 45, 55, 66), "1970-01-01 00:45:55.066 GMT (66)",
				date(0, 0, 45,  0,  0), "1970-01-01 00:45:00.000",
				date(0, 0, 46,  0,  0), "1970-01-01 00:46:00.000");
	}

	@Test void testMinutesWithSeconds()
	{
		assertFails(minutes,
				date(0, 0, 45, 55, 0), "1970-01-01 00:45:55.000 GMT (55)",
				date(0, 0, 45,  0, 0), "1970-01-01 00:45:00.000",
				date(0, 0, 46,  0, 0), "1970-01-01 00:46:00.000");
	}

	@Test void testMinutesWithMillis()
	{
		assertFails(minutes,
				date(0, 0, 45, 0, 66), "1970-01-01 00:45:00.066 GMT (66)",
				date(0, 0, 45, 0,  0), "1970-01-01 00:45:00.000",
				date(0, 0, 46, 0,  0), "1970-01-01 00:46:00.000");
	}

	@Test void testHours()
	{
		assertOk   (hours, date(0, 15,  0,  0,  0));
		assertFails(hours,
				date(0, 15, 44, 55, 66), "1970-01-01 15:44:55.066 GMT (66)",
				date(0, 15,  0,  0,  0), "1970-01-01 15:00:00.000",
				date(0, 16,  0,  0,  0), "1970-01-01 16:00:00.000");
	}

	@Test void testHoursWithMinutes()
	{
		assertFails(hours,
				date(0, 15, 44, 0, 0), "1970-01-01 15:44:00.000 GMT (44)",
				date(0, 15,  0, 0, 0), "1970-01-01 15:00:00.000",
				date(0, 16,  0, 0, 0), "1970-01-01 16:00:00.000");
	}

	@Test void testHoursWithSeconds()
	{
		assertFails(hours,
				date(0, 15, 0, 55, 0), "1970-01-01 15:00:55.000 GMT (55)",
				date(0, 15, 0,  0, 0), "1970-01-01 15:00:00.000",
				date(0, 16, 0,  0, 0), "1970-01-01 16:00:00.000");
	}

	@Test void testHoursWithMillis()
	{
		assertFails(hours,
				date(0, 15, 0, 0, 66), "1970-01-01 15:00:00.066 GMT (66)",
				date(0, 15, 0, 0,  0), "1970-01-01 15:00:00.000",
				date(0, 16, 0, 0,  0), "1970-01-01 16:00:00.000");
	}

	private static void assertFails(
			final DateField field,
			final Date wrong,
			final String wrongString,
			final Date past,
			final String pastString,
			final Date future,
			final String futureString)
	{
		try
		{
			field.checkNotNull(wrong, null);
			fail();
		}
		catch(final DatePrecisionViolationException e)
		{
			assertEquals(
					"precision violation, " +
					wrongString + " is too precise for " + field.getPrecision() + " of " + field + ", " +
					"round either to " + pastString + " in the past or " + futureString + " in the future.",
					e.getMessage());
			assertEquals(
					"precision violation, " +
					wrongString + " is too precise for " + field.getPrecision() + ", " +
					"round either to " + pastString + " in the past or " + futureString + " in the future.",
					e.getMessageWithoutFeature());
			assertEquals(null, e.getItem());
			assertEquals(field, e.getFeature());
			assertEquals(field.getPrecision(), e.getPrecision());
			assertEquals(wrong, e.getValue());
			assertEquals(past  , e.getValueAllowedInPast  ());
			assertEquals(future, e.getValueAllowedInFuture());
		}
	}

	private static void assertOk(
			final DateField field,
			final Date ok)
	{
		field.checkNotNull(ok, null);
	}
}
