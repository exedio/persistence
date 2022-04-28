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
import static com.exedio.cope.DayFieldDefaultToNowItem.mandatory;
import static com.exedio.cope.DayFieldDefaultToNowItem.none;
import static com.exedio.cope.DayFieldDefaultToNowItem.optional;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static com.exedio.cope.tojunit.Assert.list;
import static com.exedio.cope.util.TimeZoneStrict.getTimeZone;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.util.Day;
import java.time.ZoneId;
import java.util.TimeZone;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;

public class DayFieldDefaultToNowModelTest
{
	public static final Model MODEL = new Model(TYPE);

	@Test void testModel()
	{
		assertEquals(list(
				TYPE.getThis(),
				mandatory, optional, none
				), TYPE.getDeclaredFeatures());

		assertEquals(true,  mandatory.hasDefault());
		assertEquals(true,  optional.hasDefault());
		assertEquals(false, none.hasDefault());

		assertEquals(null, mandatory.getDefaultConstant());
		assertEquals(null, optional.getDefaultConstant());
		assertEquals(null, none.getDefaultConstant());

		assertEquals(true,  mandatory.isDefaultNow());
		assertEquals(true,  optional.isDefaultNow());
		assertEquals(false, none.isDefaultNow());

		assertEquals(ZoneId.of("Europe/Berlin"), mandatory.getDefaultNowZone());
		assertEquals(ZoneId.of("Europe/Berlin"), optional.getDefaultNowZone());
		assertEquals(null, none.getDefaultNowZone());
	}
	@SuppressWarnings("deprecation") // OK: testing deprecated API
	@Test void testModelTimeZone()
	{
		assertEquals(getTimeZone("Europe/Berlin"), mandatory.getDefaultNowTimeZone());
		assertEquals(getTimeZone("Europe/Berlin"), optional.getDefaultNowTimeZone());
		assertEquals(null, none.getDefaultNowTimeZone());
	}
	@Test void testNowToConstant()
	{
		final DayField feature = mandatory.defaultTo(new Day(2010, 1, 13));
		assertEquals(true, feature.hasDefault());
		assertEquals(new Day(2010, 1, 13), feature.getDefaultConstant());
		assertEquals(false, feature.isDefaultNow());
	}
	@Test void testConstantToNow()
	{
		final DayField origin = new DayField().defaultTo(new Day(2011, 1, 13));
		final DayField feature = origin.defaultToNow(ZoneId.of("Canada/Eastern"));
		assertEquals(true, feature.hasDefault());
		assertEquals(null, feature.getDefaultConstant());
		assertEquals(true, feature.isDefaultNow());
		assertEquals(ZoneId.of("Canada/Eastern"), feature.getDefaultNowZone());
	}
	@SuppressWarnings("deprecation") // OK: testing deprecated API
	@Test void testConstantToNowTimeZone()
	{
		final DayField origin = new DayField().defaultTo(new Day(2011, 1, 13));
		final DayField feature = origin.defaultToNow(getTimeZone("Canada/Eastern"));
		assertEquals(true, feature.hasDefault());
		assertEquals(null, feature.getDefaultConstant());
		assertEquals(true, feature.isDefaultNow());
		assertEquals(ZoneId.of  ("Canada/Eastern"), feature.getDefaultNowZone());
		assertEquals(getTimeZone("Canada/Eastern"), feature.getDefaultNowTimeZone());
	}
	@Test void testZoneNull()
	{
		assertFails(
				() -> none.defaultToNow((ZoneId)null),
				NullPointerException.class, "zone");
	}
	@Test void testZoneSupplierNull()
	{
		assertFails(
				() -> none.defaultToNow((Supplier<ZoneId>)null),
				NullPointerException.class, "zone");
	}
	@SuppressWarnings("deprecation") // OK: testing deprecated API
	@Test void testZoneNullTimeZone()
	{
		assertFails(
				() -> none.defaultToNow((TimeZone)null),
				NullPointerException.class, "zone");
	}
}
