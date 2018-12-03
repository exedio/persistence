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
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.jupiter.api.Test;

@SuppressFBWarnings({"RV_RETURN_VALUE_IGNORED_INFERRED","NP_NULL_PARAM_DEREF_ALL_TARGETS_DANGEROUS"})
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

		assertEquals(getTimeZone("Europe/Berlin"), mandatory.getDefaultNowZimeZone());
		assertEquals(getTimeZone("Europe/Berlin"), optional.getDefaultNowZimeZone());
		assertEquals(null, none.getDefaultNowZimeZone());
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
		final DayField feature = origin.defaultToNow(getTimeZone("Canada/Eastern"));
		assertEquals(true, feature.hasDefault());
		assertEquals(null, feature.getDefaultConstant());
		assertEquals(true, feature.isDefaultNow());
		assertEquals(getTimeZone("Canada/Eastern"), feature.getDefaultNowZimeZone());
	}
	@Test void testZoneNull()
	{
		assertFails(
				() -> none.defaultToNow(null),
				NullPointerException.class, "zone");
	}
}
