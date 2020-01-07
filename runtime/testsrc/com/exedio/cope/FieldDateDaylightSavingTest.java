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

import static com.exedio.cope.util.TimeZoneStrict.getTimeZone;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.junit.jupiter.api.Test;

public class FieldDateDaylightSavingTest extends FieldTest
{
	@Test void testSpring() throws ParseException
	{
		final Date cutoff = date("2014-3-30 02:00:00.000 (+0100)");
		assertDate(cutoff);

		{
			final Date minus1000 = date("2014-3-30 01:59:59.000 (+0100)");
			assertDiff(-1000, cutoff, minus1000);
			assertDate(minus1000);
		}
		{
			final Date minus2 = date("2014-3-30 01:59:59.998 (+0100)");
			assertDiff(-2, cutoff, minus2);
			assertDate(minus2);
		}
		{
			final Date minus1 = date("2014-3-30 01:59:59.999 (+0100)");
			assertDiff(-1, cutoff, minus1);
			assertDate(minus1);
		}
		{
			final Date plus1 = date("2014-3-30 03:00:00.001 (+0200)");
			assertDiff(1, cutoff, plus1);
			assertDate(plus1);
		}
		{
			final Date plus2 = date("2014-3-30 03:00:00.002 (+0200)");
			assertDiff(2, cutoff, plus2);
			assertDate(plus2);
		}
		{
			final Date plus1000 = date("2014-3-30 03:00:01.000 (+0200)");
			assertDiff(1000, cutoff, plus1000);
			assertDate(plus1000);
		}
	}

	@Test void testAutumnBefore() throws ParseException
	{
		final Date cutoff = date("2014-10-26 02:00:00.000 (+0200)");
		assertDate(cutoff);

		{
			final Date minus1000 = date("2014-10-26 01:59:59.000 (+0200)");
			assertDiff(-1000, cutoff, minus1000);
			assertDate(minus1000);
		}
		{
			final Date minus2 = date("2014-10-26 01:59:59.998 (+0200)");
			assertDiff(-2, cutoff, minus2);
			assertDate(minus2);
		}
		{
			final Date minus1 = date("2014-10-26 01:59:59.999 (+0200)");
			assertDiff(-1, cutoff, minus1);
			assertDate(minus1);
		}
		{
			final Date plus1 = date("2014-10-26 02:00:00.001 (+0200)");
			assertDiff(1, cutoff, plus1);
			assertDate(plus1);
		}
		{
			final Date plus2 = date("2014-10-26 02:00:00.002 (+0200)");
			assertDiff(2, cutoff, plus2);
			assertDate(plus2);
		}
		{
			final Date plus1000 = date("2014-10-26 02:00:01.000 (+0200)");
			assertDiff(1000, cutoff, plus1000);
			assertDate(plus1000);
		}
	}

	@Test void testAutumn() throws ParseException
	{
		final Date cutoff = date("2014-10-26 02:00:00.000 (+0100)");
		assertDate(cutoff);

		{
			final Date minus1000 = date("2014-10-26 02:59:59.000 (+0200)");
			assertDiff(-1000, cutoff, minus1000);
			assertDate(minus1000);
		}
		{
			final Date minus2 = date("2014-10-26 02:59:59.998 (+0200)");
			assertDiff(-2, cutoff, minus2);
			assertDate(minus2);
		}
		{
			final Date minus1 = date("2014-10-26 02:59:59.999 (+0200)");
			assertDiff(-1, cutoff, minus1);
			assertDate(minus1);
		}
		{
			final Date plus1 = date("2014-10-26 02:00:00.001 (+0100)");
			assertDiff(1, cutoff, plus1);
			assertDate(plus1);
		}
		{
			final Date plus2 = date("2014-10-26 02:00:00.002 (+0100)");
			assertDiff(2, cutoff, plus2);
			assertDate(plus2);
		}
		{
			final Date plus1000 = date("2014-10-26 02:00:01.000 (+0100)");
			assertDiff(1000, cutoff, plus1000);
			assertDate(plus1000);
		}
	}

	@Test void testAutumnAfter() throws ParseException
	{
		final Date cutoff = date("2014-10-26 03:00:00.000 (+0100)");
		assertDate(cutoff);

		{
			final Date minus1000 = date("2014-10-26 02:59:59.000 (+0100)");
			assertDiff(-1000, cutoff, minus1000);
			assertDate(minus1000);
		}
		{
			final Date minus2 = date("2014-10-26 02:59:59.998 (+0100)");
			assertDiff(-2, cutoff, minus2);
			assertDate(minus2);
		}
		{
			final Date minus1 = date("2014-10-26 02:59:59.999 (+0100)");
			assertDiff(-1, cutoff, minus1);
			assertDate(minus1);
		}
		{
			final Date plus1 = date("2014-10-26 03:00:00.001 (+0100)");
			assertDiff(1, cutoff, plus1);
			assertDate(plus1);
		}
		{
			final Date plus2 = date("2014-10-26 03:00:00.002 (+0100)");
			assertDiff(2, cutoff, plus2);
			assertDate(plus2);
		}
		{
			final Date plus1000 = date("2014-10-26 03:00:01.000 (+0100)");
			assertDiff(1000, cutoff, plus1000);
			assertDate(plus1000);
		}
	}


	private static Date date(final String s) throws ParseException
	{
		return df().parse(s);
	}

	private static SimpleDateFormat df()
	{
		final SimpleDateFormat result = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS (z)", Locale.ENGLISH);
		result.setTimeZone(getTimeZone("Europe/Berlin"));
		result.setLenient(false);
		return result;
	}

	private void assertDate(final Date date)
	{
		assertEquals(Date.class, date.getClass());

		item.setSomeDate(date);
		restartTransaction();
		final Date actual = item.getSomeDate();
		assertEquals(Date.class, actual.getClass());
		assertEquals(
				date, actual,
				"" + (actual.getTime()-date.getTime()) +
				" " + df().format(date) +
				" " + df().format(actual));
	}

	private static void assertDiff(final long diff, final Date d1, final Date d2)
	{
		assertEquals(diff, d2.getTime()-d1.getTime());
	}
}
