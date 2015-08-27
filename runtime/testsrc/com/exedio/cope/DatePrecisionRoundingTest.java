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
import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.junit.Test;

public class DatePrecisionRoundingTest
{
	@Test public void testIt() throws ParseException
	{
		final SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");
		df.setTimeZone(getTimeZone("Europe/Berlin"));
		assertEquals(1409234400000l, df.parse("28.08.2014 16:00:00.000").getTime());
		// leap second on June 30th, 2015
		assertEquals(1440770400000l, df.parse("28.08.2015 16:00:00.000").getTime());
		assertEquals(DateField.Precision.Hours, DateField.Precision.Hours);
	}
}
