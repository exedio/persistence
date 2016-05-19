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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Date;
import org.junit.Test;

public class DatePrecisionTest extends TestWithEnvironment
{
	private static final Model MODEL = new Model(TYPE);

	public DatePrecisionTest()
	{
		super(MODEL);
	}

	@Test public void testIt()
	{
		final DatePrecisionItem item = new DatePrecisionItem();

		item.setSeconds(new Date(55000));
		assertEquals(new Date(55000), item.getSeconds());

		try
		{
			item.setSeconds(new Date(55100));
			fail();
		}
		catch(final DatePrecisionViolationException e)
		{
			assertEquals(
					"precision violation on DatePrecisionItem-0, " +
					"55100 (100) is too precise  for DatePrecisionItem.seconds, " +
					"must be Seconds", e.getMessage());
		}
		assertEquals(new Date(55000), item.getSeconds());
	}
}
