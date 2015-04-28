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

import static java.util.TimeZone.getTimeZone;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class FieldDateDaylightSavingTest extends FieldTest
{
	private boolean natve;

	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		natve = SchemaInfo.supportsNativeDate(model);
	}

	public void testAutumnStart() throws ParseException
	{
		final Date cutoff = date("2014/10/26 02:00:00.000 (+0200)");
		assertDate(cutoff, natve ? date(cutoff, 3600000) : cutoff); // TODO bug

		{
			final Date minus1000 = date("2014/10/26 01:59:59.000 (+0200)");
			assertDiff(-1000, cutoff, minus1000);
			assertDate(minus1000);
		}
		{
			final Date minus2 = date("2014/10/26 01:59:59.998 (+0200)");
			assertDiff(-2, cutoff, minus2);
			assertDate(minus2);
		}
		{
			final Date minus1 = date("2014/10/26 01:59:59.999 (+0200)");
			assertDiff(-1, cutoff, minus1);
			assertDate(minus1);
		}
		{
			final Date plus1 = date("2014/10/26 02:00:00.001 (+0200)");
			assertDiff(1, cutoff, plus1);
			assertDate(plus1, natve ? date(plus1, 3600000) : plus1); // TODO bug
		}
		{
			final Date plus2 = date("2014/10/26 02:00:00.002 (+0200)");
			assertDiff(2, cutoff, plus2);
			assertDate(plus2, natve ? date(plus2, 3600000) : plus2); // TODO bug
		}
		{
			final Date plus1000 = date("2014/10/26 02:00:01.000 (+0200)");
			assertDiff(1000, cutoff, plus1000);
			assertDate(plus1000, natve ? date(plus1000, 3600000) : plus1000); // TODO bug
		}
	}

	public void testAutumnEnd() throws ParseException
	{
		final Date cutoff = date("2014/10/26 02:00:00.000 (+0100)");
		assertDate(cutoff, cutoff);

		{
			final Date minus1000 = date("2014/10/26 02:59:59.000 (+0200)");
			assertDiff(-1000, cutoff, minus1000);
			assertDate(minus1000, natve ? date(minus1000, 3600000) : minus1000); // TODO bug
		}
		{
			final Date minus2 = date("2014/10/26 02:59:59.998 (+0200)");
			assertDiff(-2, cutoff, minus2);
			assertDate(minus2, natve ? date(minus2, 3600000) : minus2); // TODO bug
		}
		{
			final Date minus1 = date("2014/10/26 02:59:59.999 (+0200)");
			assertDiff(-1, cutoff, minus1);
			assertDate(minus1, natve ? date(minus1, 3600000) : minus1); // TODO bug
		}
		{
			final Date plus1 = date("2014/10/26 02:00:00.001 (+0100)");
			assertDiff(1, cutoff, plus1);
			assertDate(plus1, plus1);
		}
		{
			final Date plus2 = date("2014/10/26 02:00:00.002 (+0100)");
			assertDiff(2, cutoff, plus2);
			assertDate(plus2, plus2);
		}
		{
			final Date plus1000 = date("2014/10/26 02:00:01.000 (+0100)");
			assertDiff(1000, cutoff, plus1000);
			assertDate(plus1000, plus1000);
		}
	}


	private static final Date date(final Date date, final int offset)
	{
		return new Date(date.getTime()+offset);
	}

	private static final Date date(final String s) throws ParseException
	{
		return df().parse(s);
	}

	private static final SimpleDateFormat df()
	{
		final SimpleDateFormat result = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS (z)");
		result.setTimeZone(tz("Europe/Berlin"));
		result.setLenient(false);
		return result;
	}

	private void assertDate(final Date date)
	{
		assertDate(date, date);
	}

	private void assertDate(final Date expectedSet, final Date expectedGet)
	{
		assertEquals(Date.class, expectedSet.getClass());
		assertEquals(Date.class, expectedGet.getClass());

		item.setSomeDate(expectedSet);
		restartTransaction();
		final Date actual = item.getSomeDate();
		assertEquals(Date.class, actual.getClass());
		assertEquals(
				"" + (actual.getTime()-expectedGet.getTime()) +
				" " + df().format(expectedGet) +
				" " + df().format(actual),
				expectedGet, actual);
	}

	private static void assertDiff(final long diff, final Date d1, final Date d2)
	{
		assertEquals(diff, d2.getTime()-d1.getTime());
	}

	private static final TimeZone tz(final String ID)
	{
		// TODO use com.exedio.cope.util.TimeZoneStrict when available
		final TimeZone result = getTimeZone(ID);
		assertEquals(ID, result.getID());
		return result;
	}
}
