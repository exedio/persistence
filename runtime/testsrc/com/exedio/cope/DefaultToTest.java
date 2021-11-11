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

import static com.exedio.cope.DefaultToItem.DefaultToEnum.ONE;
import static com.exedio.cope.DefaultToItem.DefaultToEnum.THREE;
import static com.exedio.cope.DefaultToItem.DefaultToEnum.TWO;
import static com.exedio.cope.DefaultToItem.booleanNone;
import static com.exedio.cope.DefaultToItem.booleanTrue;
import static com.exedio.cope.DefaultToItem.dateEight;
import static com.exedio.cope.DefaultToItem.dateEighty;
import static com.exedio.cope.DefaultToItem.dateNone;
import static com.exedio.cope.DefaultToItem.dayEight;
import static com.exedio.cope.DefaultToItem.dayNone;
import static com.exedio.cope.DefaultToItem.enumNone;
import static com.exedio.cope.DefaultToItem.enumOne;
import static com.exedio.cope.DefaultToItem.enumTwo;
import static com.exedio.cope.DefaultToItem.integerFifty;
import static com.exedio.cope.DefaultToItem.integerFive;
import static com.exedio.cope.DefaultToItem.longRandom;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.exedio.cope.junit.AbsoluteMockClockStrategy;
import com.exedio.cope.tojunit.ClockRule;
import com.exedio.cope.tojunit.MainRule;
import com.exedio.cope.util.Day;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@MainRule.Tag
public class DefaultToTest extends TestWithEnvironment
{
	public DefaultToTest()
	{
		super(DefaultToModelTest.MODEL);
	}

	private final AbsoluteMockClockStrategy clock = new AbsoluteMockClockStrategy();
	private final ClockRule clockRule = new ClockRule();


	@BeforeEach final void setUp()
	{
		clockRule.override(clock);
	}

	@Test void testIt()
	{
		{
			clock.assertEmpty();
			final DefaultToItem item = new DefaultToItem(
					SetValue.map(booleanNone, false)
			);
			clock.assertEmpty();

			assertEquals(TRUE, item.getBooleanTrue());
			assertEquals(false, item.getBooleanNone());
			assertEquals(5, item.getIntegerFive());
			assertEquals(integer(50), item.getIntegerFifty());
			assertEquals(null, item.getIntegerNone());
			assertEquals(date(8), item.getDateEight());
			assertEquals(date(80), item.getDateEighty());
			assertEquals(null, item.getDateNone());
			assertEquals(day(1608, 8, 8), item.getDayEight());
			assertEquals(null, item.getDayNone());
			assertNotNull(item.getLongRandom());
			assertEquals(ONE, item.getEnumOne());
			assertEquals(TWO, item.getEnumTwo());
			assertEquals(null, item.getEnumNone());
		}
		{
			clock.assertEmpty();
			final DefaultToItem item = new DefaultToItem(
					SetValue.map(booleanNone, false)
			);
			clock.assertEmpty();

			assertEquals(TRUE, item.getBooleanTrue());
			assertEquals(false, item.getBooleanNone());
			assertEquals(5, item.getIntegerFive());
			assertEquals(integer(50), item.getIntegerFifty());
			assertEquals(null, item.getIntegerNone());
			assertEquals(date(8), item.getDateEight());
			assertEquals(date(80), item.getDateEighty());
			assertEquals(null, item.getDateNone());
			assertEquals(null, item.getDayNone());
			assertNotNull(item.getLongRandom());
			assertEquals(ONE, item.getEnumOne());
			assertEquals(TWO, item.getEnumTwo());
			assertEquals(null, item.getEnumNone());
		}
		{
			clock.assertEmpty();
			final DefaultToItem item = new DefaultToItem(
					SetValue.map(booleanTrue, false),
					SetValue.map(booleanNone, true),
					SetValue.map(integerFive, 6),
					SetValue.map(integerFifty, 51),
					SetValue.map(dateEight, date(9)),
					SetValue.map(dateEighty, date(81)),
					SetValue.map(dateNone, date(503)),
					SetValue.map(dayEight, day(1609, 9, 9)),
					SetValue.map(dayNone, day(2010, 1, 15)),
					SetValue.map(longRandom, Long.valueOf(37)),
					SetValue.map(enumOne, THREE),
					SetValue.map(enumTwo, ONE),
					SetValue.map(enumNone, TWO)
			);
			clock.assertEmpty();

			assertEquals(FALSE, item.getBooleanTrue());
			assertEquals(true, item.getBooleanNone());
			assertEquals(6, item.getIntegerFive());
			assertEquals(integer(51), item.getIntegerFifty());
			assertEquals(null, item.getIntegerNone());
			assertEquals(date(9), item.getDateEight());
			assertEquals(date(81), item.getDateEighty());
			assertEquals(date(503), item.getDateNone());
			assertEquals(day(1609, 9, 9), item.getDayEight());
			assertEquals(day(2010, 1, 15), item.getDayNone());
			assertEquals(Long.valueOf(37), item.getLongRandom());
			assertEquals(THREE, item.getEnumOne());
			assertEquals(ONE, item.getEnumTwo());
			assertEquals(TWO, item.getEnumNone());
		}
		{
			clock.assertEmpty();
			final DefaultToItem item = new DefaultToItem(
					SetValue.map(booleanTrue, null),
					SetValue.map(booleanNone, true),
					SetValue.map(integerFifty, null),
					SetValue.map(dateEighty, null),
					SetValue.map(dateNone, null),
					SetValue.map(dayNone, null),
					SetValue.map(longRandom, null),
					SetValue.map(enumOne, TWO),
					SetValue.map(enumTwo, null),
					SetValue.map(enumNone, null)
			);
			clock.assertEmpty();

			assertEquals(null, item.getBooleanTrue());
			assertEquals(true, item.getBooleanNone());
			assertEquals(5, item.getIntegerFive());
			assertEquals(null, item.getIntegerFifty());
			assertEquals(null, item.getIntegerNone());
			assertEquals(date(8), item.getDateEight());
			assertEquals(null, item.getDateEighty());
			assertEquals(null, item.getDateNone());
			assertEquals(day(1608, 8, 8), item.getDayEight());
			assertEquals(null, item.getDayNone());
			assertEquals(null, item.getLongRandom());
			assertEquals(TWO, item.getEnumOne());
			assertEquals(null, item.getEnumTwo());
			assertEquals(null, item.getEnumNone());
		}
	}

	private static Integer integer(final int i)
	{
		return Integer.valueOf(i);
	}

	private static Date date(final long l)
	{
		return new Date(l);
	}

	private static Day day(final int year, final int month, final int day)
	{
		return new Day(year, month, day);
	}
}
