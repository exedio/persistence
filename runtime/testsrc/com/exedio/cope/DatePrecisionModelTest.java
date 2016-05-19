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

import static com.exedio.cope.DatePrecisionItem.minutes;
import static com.exedio.cope.DatePrecisionItem.seconds;
import static com.exedio.cope.DatePrecisionTest.date;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Date;
import org.junit.Test;

public class DatePrecisionModelTest
{
	@Test public void testGetPrecision()
	{
		assertEquals(DateField.Precision.Seconds, seconds.getPrecision());
		assertEquals(DateField.Precision.Minutes, minutes.getPrecision());
	}

	@Test public void testSeconds()
	{
		assertFails(date(0, 0, 0, 55, 66), "1970-01-01 00:00:55.066 (66)", seconds);
	}

	@Test public void testMinutes()
	{
		assertFails(date(0, 0, 45, 55, 66), "1970-01-01 00:45:55.066 (66)", minutes);
	}

	@Test public void testMinutesWithSeconds()
	{
		assertFails(date(0, 0, 45, 55, 0), "1970-01-01 00:45:55.000 (55)", minutes);
	}

	@Test public void testMinutesWithMillis()
	{
		assertFails(date(0, 0, 45, 0, 66), "1970-01-01 00:45:00.066 (66)", minutes);
	}

	private static void assertFails(
			final Date wrong,
			final String wrongString,
			final DateField field)
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
					wrongString + " is too precise for " + field + ", " +
					"must be " + field.getPrecision(),
					e.getMessage());
			assertEquals(
					"precision violation, " +
					wrongString + " is too precise, " +
					"must be " + field.getPrecision(),
					e.getMessageWithoutFeature());
			assertEquals(null, e.getItem());
			assertEquals(field, e.getFeature());
			assertEquals(wrong, e.getValue());
		}
	}
}
