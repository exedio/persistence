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

import static com.exedio.cope.DateFieldDefaultToNowItem.mandatory;
import static com.exedio.cope.DateFieldDefaultToNowItem.none;
import static com.exedio.cope.DateFieldDefaultToNowItem.optional;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.junit.AbsoluteMockClockStrategy;
import com.exedio.cope.tojunit.ClockRule;
import com.exedio.cope.tojunit.MainRule;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@MainRule.Tag
public class DateFieldDefaultToNowTest extends TestWithEnvironment
{
	public DateFieldDefaultToNowTest()
	{
		super(DateFieldDefaultToNowModelTest.MODEL);
	}

	private final AbsoluteMockClockStrategy clock = new AbsoluteMockClockStrategy();
	private final ClockRule clockRule = new ClockRule();


	@BeforeEach final void setUp()
	{
		clockRule.override(clock);
	}

	@Test void testNow()
	{
		clock.add(value1);
		final DateFieldDefaultToNowItem item = new DateFieldDefaultToNowItem(
		);
		clock.assertEmpty();

		assertEquals(value1, item.getMandatory());
		assertEquals(value1, item.getOptional());
		assertEquals(null, item.getNone());
	}
	@Test void testNowOther()
	{
		clock.add(value2);
		final DateFieldDefaultToNowItem item = new DateFieldDefaultToNowItem(
		);
		clock.assertEmpty();

		assertEquals(value2, item.getMandatory());
		assertEquals(value2, item.getOptional());
		assertEquals(null, item.getNone());
	}
	@Test void testSet()
	{
		clock.assertEmpty();
		final DateFieldDefaultToNowItem item = new DateFieldDefaultToNowItem(
				SetValue.map(mandatory, value1),
				SetValue.map(optional, value2),
				SetValue.map(none, value3)
		);
		clock.assertEmpty();

		assertEquals(value1, item.getMandatory());
		assertEquals(value2, item.getOptional());
		assertEquals(value3, item.getNone());
	}
	@Test void testSetNull()
	{
		clock.add(value3);
		final DateFieldDefaultToNowItem item = new DateFieldDefaultToNowItem(
				SetValue.map(optional, null),
				SetValue.map(none, null)
		);
		clock.assertEmpty();

		assertEquals(value3, item.getMandatory());
		assertEquals(null, item.getOptional());
		assertEquals(null, item.getNone());
	}

	private static final Date value1 = new Date(1111);
	private static final Date value2 = new Date(2222);
	private static final Date value3 = new Date(3333);
}
