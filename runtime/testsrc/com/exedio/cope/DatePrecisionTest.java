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

import static com.exedio.cope.DatePrecisionItem.TYPE;
import static com.exedio.cope.DatePrecisionItem.seconds;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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

		item.setSeconds(date(0, 0, 0, 55, 0));
		assertEquals(date(0, 0, 0, 55, 0), item.getSeconds());

		try
		{
			item.setSeconds(date(0, 0, 0, 55, 100));
			fail();
		}
		catch(final DatePrecisionViolationException e)
		{
			assertEquals(
					"precision violation on DatePrecisionItem-0, " +
					"1970-01-01 00:00:55.100 (100) is too precise for DatePrecisionItem.seconds, " +
					"must be Seconds", e.getMessage());
			assertEquals(
					"precision violation on DatePrecisionItem-0, " +
					"1970-01-01 00:00:55.100 (100) is too precise, " +
					"must be Seconds", e.getMessageWithoutFeature());
			assertEquals(item, e.getItem());
			assertEquals(seconds, e.getFeature());
			assertEquals(date(0, 0, 0, 55, 100), e.getValue());
		}
		assertEquals(date(0, 0, 0, 55, 0), item.getSeconds());
	}

	@SuppressFBWarnings("ICAST_INT_2_LONG_AS_INSTANT")
	private static Date date(
			final int days,
			final int hours,
			final int minutes,
			final int seconds,
			final int milliseconds)
	{
		return new Date(
				days *
				  24 + less(  24, hours) *
				  60 + less(  60, minutes) *
				  60 + less(  60, seconds) *
				1000 + less(1000, milliseconds));
	}

	private static int less(final int limit, final int value)
	{
		assertTrue("" + value + " " + limit, value<limit);
		return value;
	}
}
