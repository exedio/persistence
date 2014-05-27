/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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

import static com.exedio.cope.DefaultToItem.TYPE;
import static com.exedio.cope.DefaultToItem.booleanNone;
import static com.exedio.cope.DefaultToItem.booleanTrue;
import static com.exedio.cope.DefaultToItem.dateEight;
import static com.exedio.cope.DefaultToItem.dateEighty;
import static com.exedio.cope.DefaultToItem.dateNone;
import static com.exedio.cope.DefaultToItem.dateNow;
import static com.exedio.cope.DefaultToItem.dateNowOpt;
import static com.exedio.cope.DefaultToItem.dayEight;
import static com.exedio.cope.DefaultToItem.dayNone;
import static com.exedio.cope.DefaultToItem.dayNow;
import static com.exedio.cope.DefaultToItem.dayNowOpt;
import static com.exedio.cope.DefaultToItem.enumNone;
import static com.exedio.cope.DefaultToItem.enumOne;
import static com.exedio.cope.DefaultToItem.enumTwo;
import static com.exedio.cope.DefaultToItem.integerFifty;
import static com.exedio.cope.DefaultToItem.integerFive;
import static com.exedio.cope.DefaultToItem.integerNext;
import static com.exedio.cope.DefaultToItem.integerNone;
import static com.exedio.cope.DefaultToItem.longRandom;
import static com.exedio.cope.DefaultToItem.DefaultToEnum.ONE;
import static com.exedio.cope.DefaultToItem.DefaultToEnum.TWO;
import static com.exedio.cope.SchemaInfo.getDefaultToNextSequenceName;
import static java.lang.Boolean.TRUE;
import static java.util.TimeZone.getTimeZone;

import com.exedio.cope.junit.CopeAssert;
import com.exedio.cope.util.Day;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Date;
import java.util.TimeZone;

public class DefaultToModelTest extends CopeAssert
{
	public static final Model MODEL = new Model(TYPE);

	@SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_INFERRED")
	public void testIt()
	{
		assertEquals(list(
				TYPE.getThis(),
				booleanTrue, booleanNone,
				integerFive, integerFifty, integerNext, integerNext.getDefaultNext(), integerNone,
				dateEight, dateEighty, dateNow, dateNowOpt, dateNone,
				dayEight, dayNow, dayNowOpt, dayNone,
				longRandom,
				enumOne, enumTwo, enumNone
				), TYPE.getDeclaredFeatures());

		assertEquals(true,  booleanTrue.hasDefault());
		assertEquals(false, booleanNone.hasDefault());
		assertEquals(true,  integerFive.hasDefault());
		assertEquals(true,  integerFifty.hasDefault());
		assertEquals(true,  integerNext.hasDefault());
		assertEquals(false, integerNone.hasDefault());
		assertEquals(true,  dateEight.hasDefault());
		assertEquals(true,  dateEighty.hasDefault());
		assertEquals(true,  dateNow.hasDefault());
		assertEquals(true,  dateNowOpt.hasDefault());
		assertEquals(false, dateNone.hasDefault());
		assertEquals(true,  dayEight.hasDefault());
		assertEquals(true,  dayNow.hasDefault());
		assertEquals(true,  dayNowOpt.hasDefault());
		assertEquals(false, dayNone.hasDefault());
		assertEquals(true,  longRandom.hasDefault());

		assertEquals(TRUE, booleanTrue.getDefaultConstant());
		assertEquals(null, booleanNone.getDefaultConstant());
		assertEquals(integer(5), integerFive.getDefaultConstant());
		assertEquals(integer(50), integerFifty.getDefaultConstant());
		assertEquals(null, integerNext.getDefaultConstant());
		assertEquals(null, integerNone.getDefaultConstant());

		assertEquals(false, integerFive.isDefaultNext());
		assertEquals(false, integerFifty.isDefaultNext());
		assertEquals(true,  integerNext.isDefaultNext());
		assertEquals(false, integerNone.isDefaultNext());

		assertEquals(null, integerFive.getDefaultNextStart());
		assertEquals(null, integerFifty.getDefaultNextStart());
		assertEquals(integer(10001), integerNext.getDefaultNextStart());
		assertEquals(null, integerNone.getDefaultNextStart());

		assertEquals(null, integerFive.getDefaultNext());
		assertEquals(null, integerFifty.getDefaultNext());
		{
			final Sequence s = integerNext.getDefaultNext();
			assertNotNull(s);
			assertEquals("integerNextzack",s.getName());
			assertEquals("DefaultToItem.integerNextzack", s.getID());
			assertEquals(TYPE, s.getType());
			assertEquals(null, s.getPattern());
			assertEquals(10001, s.getStart());
			assertEquals(Integer.MAX_VALUE, s.getEnd());
		}
		assertEquals(null, integerNone.getDefaultNext());

		assertEquals(date(8), dateEight.getDefaultConstant());
		assertEquals(date(80), dateEighty.getDefaultConstant());
		assertEquals(null, dateNow.getDefaultConstant());
		assertEquals(null, dateNowOpt.getDefaultConstant());
		assertEquals(null, dateNone.getDefaultConstant());

		assertEquals(false, dateEight.isDefaultNow());
		assertEquals(false, dateEighty.isDefaultNow());
		assertEquals(true,  dateNow.isDefaultNow());
		assertEquals(true,  dateNowOpt.isDefaultNow());
		assertEquals(false, dateNone.isDefaultNow());

		assertEquals(day(1008, 8, 8), dayEight.getDefaultConstant());
		assertEquals(null, dayNow.getDefaultConstant());
		assertEquals(null, dayNowOpt.getDefaultConstant());
		assertEquals(null, dayNone.getDefaultConstant());

		assertEquals(false, dayEight.isDefaultNow());
		assertEquals(true,  dayNow.isDefaultNow());
		assertEquals(true,  dayNowOpt.isDefaultNow());
		assertEquals(false, dayNone.isDefaultNow());

		assertEquals(null, dayEight.getDefaultNowZimeZone());
		assertEquals(tz("Europe/Berlin"),  dayNow.getDefaultNowZimeZone());
		assertEquals(tz("Europe/Berlin"),  dayNowOpt.getDefaultNowZimeZone());
		assertEquals(null, dayNone.getDefaultNowZimeZone());

		assertEquals(null, longRandom.getDefaultConstant());

		assertEquals(ONE, enumOne.getDefaultConstant());
		assertEquals(TWO, enumTwo.getDefaultConstant());
		assertEquals(null, enumNone.getDefaultConstant());

		{
			final IntegerField feature = integerFifty.defaultToNext(88);
			assertEquals(true, feature.hasDefault());
			assertEquals(null, feature.getDefaultConstant());
			assertEquals(true, feature.isDefaultNext());
			assertEquals(integer(88), feature.getDefaultNextStart());
		}
		{
			final IntegerField feature = integerNext.defaultTo(99);
			assertEquals(true, feature.hasDefault());
			assertEquals(integer(99), feature.getDefaultConstant());
			assertEquals(false, feature.isDefaultNext());
			assertEquals(null, feature.getDefaultNextStart());
		}
		try
		{
			integerNext.min(10002);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals(
					"The start value for defaultToNext of the field does not comply to one of it's own constraints, " +
					"caused a IntegerRangeViolationException: " +
					"range violation, " +
					"10001 is too small, " +
					"must be at least 10002. Start value was '10001'.", e.getMessage());
		}
		try
		{
			getDefaultToNextSequenceName(integerFive);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("is not defaultToNext: " + integerFive, e.getMessage());
		}
		{
			final DateField feature = dateEight.defaultToNow();
			assertEquals(true, feature.hasDefault());
			assertEquals(null, feature.getDefaultConstant());
			assertEquals(true, feature.isDefaultNow());
		}
		{
			final DateField feature = dateNow.defaultTo(new Date(444));
			assertEquals(true, feature.hasDefault());
			assertEquals(new Date(444), feature.getDefaultConstant());
			assertEquals(false, feature.isDefaultNow());
		}
		{
			final DayField feature = dayEight.defaultToNow(tz("Canada/Eastern"));
			assertEquals(true, feature.hasDefault());
			assertEquals(null, feature.getDefaultConstant());
			assertEquals(true, feature.isDefaultNow());
			assertEquals(tz("Canada/Eastern"), feature.getDefaultNowZimeZone());
		}
		{
			final DayField feature = dayNow.defaultTo(new Day(2010, 1, 13));
			assertEquals(true, feature.hasDefault());
			assertEquals(new Day(2010, 1, 13), feature.getDefaultConstant());
			assertEquals(false, feature.isDefaultNow());
		}
		try
		{
			new StringField().lengthMax(3).defaultTo("1234");
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			//e.printStackTrace();
			assertEquals(
					"The default constant of the field does not comply to one of it's own constraints, " +
					"caused a StringLengthViolationException: " +
					"length violation, '1234' is too long, " +
					"must be at most 3 characters, but was 4. " +
					"Default constant was '1234'.",
					e.getMessage());
		}
		try
		{
			dayEight.defaultToNow(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("zone", e.getMessage());
		}
		try
		{
			longRandom.defaultToRandom(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("source", e.getMessage());
		}
	}

	private static final Integer integer(final int i)
	{
		return Integer.valueOf(i);
	}

	private static final Date date(final long l)
	{
		return new Date(l);
	}

	private static final Day day(final int year, final int month, final int day)
	{
		return new Day(year, month, day);
	}

	private static final TimeZone tz(final String ID)
	{
		final TimeZone result = getTimeZone(ID);
		assertEquals(ID, result.getID());
		return result;
	}
}
