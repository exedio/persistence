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

import static com.exedio.cope.DatePrecisionTest.date;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.Date;
import org.junit.Test;

public class DatePrecisionDefaultToTest
{
	@Test public void test()
	{
		final DateField f = new DateField().minutes();
		final Date wrong = date(11, 22, 44, 55, 66);
		try
		{
			f.defaultTo(wrong);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals(
					"The default constant of the field does not comply to one of it's own constraints, " +
					"caused a DatePrecisionViolationException: " +
					"precision violation, 1970-01-12 22:44:55.066 (66) is too precise, " +
					"must be Minutes, round either to " +
					"1970-01-12 22:44:00.000 or " +
					"1970-01-12 22:45:00.000 " +
					"Default constant was '" + wrong.toString() + "'.",
					e.getMessage());
			assertNull(e.getCause());
		}
	}
}
