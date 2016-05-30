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
import static com.exedio.cope.DatePrecisionItem.TYPE;
import static com.exedio.cope.DatePrecisionItem.minutes;
import static com.exedio.cope.DatePrecisionItem.seconds;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.exedio.cope.DateField.RoundingMode;
import java.util.Date;
import org.junit.Test;

public class DatePrecisionTest extends TestWithEnvironment
{
	static final Model MODEL = new Model(TYPE);

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
					"1970-01-01 00:00:55.066 GMT (66) is too precise for DatePrecisionItem.seconds, " +
					"must be Seconds, round either to past: " +
					"1970-01-01 00:00:55.000 or future: " +
					"1970-01-01 00:00:56.000",
					e.getMessage());
			assertEquals(
					"precision violation on DatePrecisionItem-0, " +
					"1970-01-01 00:00:55.066 GMT (66) is too precise, " +
					"must be Seconds, round either to past: " +
					"1970-01-01 00:00:55.000 or future: " +
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
					"1970-01-01 00:45:55.066 GMT (66) is too precise for DatePrecisionItem.minutes, " +
					"must be Minutes, round either to past: " +
					"1970-01-01 00:45:00.000 or future: " +
					"1970-01-01 00:46:00.000",
					e.getMessage());
			assertEquals(
					"precision violation on DatePrecisionItem-0, " +
					"1970-01-01 00:45:55.066 GMT (66) is too precise, " +
					"must be Minutes, round either to past: " +
					"1970-01-01 00:45:00.000 or future: " +
					"1970-01-01 00:46:00.000",
					e.getMessageWithoutFeature());
			assertEquals(item, e.getItem());
			assertEquals(minutes, e.getFeature());
			assertEquals(wrong, e.getValue());
		}
		assertEquals(ok, item.getMinutes());
	}

	@Test public void testSecondsRound()
	{
		final DatePrecisionItem item = new DatePrecisionItem();

		final Date value  = date(0, 0, 0, 55, 66);
		final Date past   = date(0, 0, 0, 55,  0);
		final Date future = date(0, 0, 0, 56,  0);

		item.setSecondsRounded(value, RoundingMode.PAST);
		assertEquals(past, item.getSeconds());

		item.setSecondsRounded(null, RoundingMode.PAST);
		assertEquals(null, item.getSeconds());

		item.setSecondsRounded(value, RoundingMode.FUTURE);
		assertEquals(future, item.getSeconds());

		item.setSecondsRounded(null, RoundingMode.FUTURE);
		assertEquals(null, item.getSeconds());
	}

	@Test public void testMinutesRound()
	{
		final DatePrecisionItem item = new DatePrecisionItem();

		final Date value  = date(0, 0, 44, 55, 66);
		final Date past   = date(0, 0, 44,  0,  0);
		final Date future = date(0, 0, 45,  0,  0);

		item.setMinutesRounded(value, RoundingMode.PAST);
		assertEquals(past, item.getMinutes());

		item.setMinutesRounded(null, RoundingMode.PAST);
		assertEquals(null, item.getMinutes());

		item.setMinutesRounded(value, RoundingMode.FUTURE);
		assertEquals(future, item.getMinutes());

		item.setMinutesRounded(null, RoundingMode.FUTURE);
		assertEquals(null, item.getMinutes());
	}

	@Test public void testNullMode()
	{
		final DatePrecisionItem item = new DatePrecisionItem();
		final Date value  = date(0, 0, 44, 55, 66);

		try
		{
			item.setMinutesRounded(value, null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("roundingMode", e.getMessage());
		}
		try
		{
			item.setMinutesRounded(null, null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("roundingMode", e.getMessage());
		}
	}
}
