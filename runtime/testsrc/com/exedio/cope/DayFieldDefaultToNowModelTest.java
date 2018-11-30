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

import static com.exedio.cope.DayFieldDefaultToNowItem.TYPE;
import static com.exedio.cope.DayFieldDefaultToNowItem.dayNone;
import static com.exedio.cope.DayFieldDefaultToNowItem.dayNow;
import static com.exedio.cope.DayFieldDefaultToNowItem.dayNowOpt;
import static com.exedio.cope.tojunit.Assert.list;
import static com.exedio.cope.util.TimeZoneStrict.getTimeZone;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.util.Day;
import org.junit.jupiter.api.Test;

public class DayFieldDefaultToNowModelTest
{
	public static final Model MODEL = new Model(TYPE);

	@Test void testIt()
	{
		assertEquals(list(
				TYPE.getThis(),
				dayNow, dayNowOpt, dayNone
				), TYPE.getDeclaredFeatures());

		assertEquals(true,  dayNow.hasDefault());
		assertEquals(true,  dayNowOpt.hasDefault());
		assertEquals(false, dayNone.hasDefault());

		assertEquals(null, dayNow.getDefaultConstant());
		assertEquals(null, dayNowOpt.getDefaultConstant());
		assertEquals(null, dayNone.getDefaultConstant());

		assertEquals(true,  dayNow.isDefaultNow());
		assertEquals(true,  dayNowOpt.isDefaultNow());
		assertEquals(false, dayNone.isDefaultNow());

		assertEquals(getTimeZone("Europe/Berlin"),  dayNow.getDefaultNowZimeZone());
		assertEquals(getTimeZone("Europe/Berlin"),  dayNowOpt.getDefaultNowZimeZone());
		assertEquals(null, dayNone.getDefaultNowZimeZone());
		{
			final DayField feature = dayNow.defaultTo(new Day(2010, 1, 13));
			assertEquals(true, feature.hasDefault());
			assertEquals(new Day(2010, 1, 13), feature.getDefaultConstant());
			assertEquals(false, feature.isDefaultNow());
		}
	}
}
