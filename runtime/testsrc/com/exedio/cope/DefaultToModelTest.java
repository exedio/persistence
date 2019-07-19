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
import static com.exedio.cope.DefaultToItem.DefaultToEnum.TWO;
import static com.exedio.cope.DefaultToItem.TYPE;
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
import static com.exedio.cope.DefaultToItem.integerNone;
import static com.exedio.cope.DefaultToItem.longRandom;
import static com.exedio.cope.tojunit.Assert.list;
import static java.lang.Boolean.TRUE;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.util.Day;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Date;
import org.junit.jupiter.api.Test;

public class DefaultToModelTest
{
	public static final Model MODEL = new Model(TYPE);

	@SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_INFERRED")
	@Test void testIt()
	{
		assertEquals(list(
				TYPE.getThis(),
				booleanTrue, booleanNone,
				integerFive, integerFifty, integerNone,
				dateEight, dateEighty, dateNone,
				dayEight, dayNone,
				longRandom,
				enumOne, enumTwo, enumNone
				), TYPE.getDeclaredFeatures());

		assertEquals(true,  booleanTrue.hasDefault());
		assertEquals(false, booleanNone.hasDefault());
		assertEquals(true,  integerFive.hasDefault());
		assertEquals(true,  integerFifty.hasDefault());
		assertEquals(false, integerNone.hasDefault());
		assertEquals(true,  dateEight.hasDefault());
		assertEquals(true,  dateEighty.hasDefault());
		assertEquals(false, dateNone.hasDefault());
		assertEquals(true,  dayEight.hasDefault());
		assertEquals(false, dayNone.hasDefault());
		assertEquals(true,  longRandom.hasDefault());

		assertEquals(TRUE, booleanTrue.getDefaultConstant());
		assertEquals(null, booleanNone.getDefaultConstant());
		assertEquals(integer(5), integerFive.getDefaultConstant());
		assertEquals(integer(50), integerFifty.getDefaultConstant());
		assertEquals(null, integerNone.getDefaultConstant());

		assertEquals(false, integerFive.isDefaultNext());
		assertEquals(false, integerFifty.isDefaultNext());
		assertEquals(false, integerNone.isDefaultNext());

		assertEquals(date(8), dateEight.getDefaultConstant());
		assertEquals(date(80), dateEighty.getDefaultConstant());
		assertEquals(null, dateNone.getDefaultConstant());

		assertEquals(false, dateEight.isDefaultNow());
		assertEquals(false, dateEighty.isDefaultNow());
		assertEquals(false, dateNone.isDefaultNow());

		assertEquals(day(1608, 8, 8), dayEight.getDefaultConstant());
		assertEquals(null, dayNone.getDefaultConstant());

		assertEquals(false, dayEight.isDefaultNow());
		assertEquals(false, dayNone.isDefaultNow());

		assertEquals(null, dayEight.getDefaultNowTimeZone());
		assertEquals(null, dayNone.getDefaultNowTimeZone());

		assertEquals(null, longRandom.getDefaultConstant());

		assertEquals(ONE, enumOne.getDefaultConstant());
		assertEquals(TWO, enumTwo.getDefaultConstant());
		assertEquals(null, enumNone.getDefaultConstant());

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
			longRandom.defaultToRandom(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("source", e.getMessage());
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
