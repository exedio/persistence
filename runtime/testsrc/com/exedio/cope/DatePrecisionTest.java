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

import static com.exedio.cope.DateField.Precision.Hours;
import static com.exedio.cope.DateField.Precision.Millis;
import static com.exedio.cope.DateField.Precision.Minutes;
import static com.exedio.cope.DateField.Precision.Seconds;
import static com.exedio.cope.DatePrecisionItem.TYPE;
import static com.exedio.cope.DatePrecisionItem.minutes;
import static com.exedio.cope.DatePrecisionItem.seconds;
import static com.exedio.cope.SchemaInfo.getColumnValue;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.exedio.cope.DateField.Precision;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Date;
import org.junit.Test;

public class DatePrecisionTest extends TestWithEnvironment
{
	private static final Model MODEL = new Model(TYPE);

	public DatePrecisionTest()
	{
		super(MODEL);
	}

	@Test public void testSeconds()
	{
		final DatePrecisionItem item = new DatePrecisionItem();
		final Date ok    = date(0, 0, 0, 55, 0 );
		final Date wrong = date(0, 0, 0, 55, 66);

		item.setSeconds(ok);
		assertEquals(ok, item.getSeconds());

		try
		{
			item.setSeconds(wrong);
			fail();
		}
		catch(final DatePrecisionViolationException e)
		{
			assertEquals(
					"precision violation on DatePrecisionItem-0, " +
					"1970-01-01 00:00:55.066 (66) is too precise for DatePrecisionItem.seconds, " +
					"must be Seconds, round either to " +
					"1970-01-01 00:00:55.000 or " +
					"1970-01-01 00:00:56.000",
					e.getMessage());
			assertEquals(
					"precision violation on DatePrecisionItem-0, " +
					"1970-01-01 00:00:55.066 (66) is too precise, " +
					"must be Seconds, round either to " +
					"1970-01-01 00:00:55.000 or " +
					"1970-01-01 00:00:56.000",
					e.getMessageWithoutFeature());
			assertEquals(item, e.getItem());
			assertEquals(seconds, e.getFeature());
			assertEquals(wrong, e.getValue());
		}
		assertEquals(ok, item.getSeconds());
	}

	@Test public void testMinutes()
	{
		final DatePrecisionItem item = new DatePrecisionItem();
		final Date ok    = date(0, 0, 45,  0,  0);
		final Date wrong = date(0, 0, 45, 55, 66);

		item.setMinutes(ok);
		assertEquals(ok, item.getMinutes());

		try
		{
			item.setMinutes(wrong);
			fail();
		}
		catch(final DatePrecisionViolationException e)
		{
			assertEquals(
					"precision violation on DatePrecisionItem-0, " +
					"1970-01-01 00:45:55.066 (66) is too precise for DatePrecisionItem.minutes, " +
					"must be Minutes, round either to " +
					"1970-01-01 00:45:00.000 or " +
					"1970-01-01 00:46:00.000",
					e.getMessage());
			assertEquals(
					"precision violation on DatePrecisionItem-0, " +
					"1970-01-01 00:45:55.066 (66) is too precise, " +
					"must be Minutes, round either to " +
					"1970-01-01 00:45:00.000 or " +
					"1970-01-01 00:46:00.000",
					e.getMessageWithoutFeature());
			assertEquals(item, e.getItem());
			assertEquals(minutes, e.getFeature());
			assertEquals(wrong, e.getValue());
		}
		assertEquals(ok, item.getMinutes());
	}

	@SuppressFBWarnings("ICAST_INT_2_LONG_AS_INSTANT")
	static Date date(
			final int days,
			final int hours,
			final int minutes,
			final int seconds,
			final int milliseconds)
	{
		return new Date(
				(((days *
				  24 + less(  24, hours)) *
				  60 + less(  60, minutes)) *
				  60 + less(  60, seconds)) *
				1000 + less(1000, milliseconds));
	}

	private static int less(final int limit, final int value)
	{
		assertTrue("" + value + " " + limit, value<limit);
		return value;
	}

	@Test public void testEnumSchema()
	{
		assertEquals(asList(Millis, Seconds, Minutes, Hours), asList(Precision.values()));
		assertEquals(10, getColumnValue(Millis ));
		assertEquals(20, getColumnValue(Seconds  ));
		assertEquals(30, getColumnValue(Minutes ));
		assertEquals(40, getColumnValue(Hours));
	}
}
