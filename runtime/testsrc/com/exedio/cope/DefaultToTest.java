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
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import java.util.Date;

import com.exedio.cope.DefaultToItem.DefaultToEnum;
import com.exedio.cope.util.Day;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public class DefaultToTest extends AbstractRuntimeTest
{
	public static final Model MODEL = new Model(TYPE);

	public DefaultToTest()
	{
		super(MODEL);
	}

	@SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_INFERRED")
	public void testIt()
	{
		// test model
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

		assertEquals(null, dayNow.getDefaultConstant());
		assertEquals(null, dayNowOpt.getDefaultConstant());
		assertEquals(null, dayNone.getDefaultConstant());

		assertEquals(true,  dayNow.isDefaultNow());
		assertEquals(true,  dayNowOpt.isDefaultNow());
		assertEquals(false, dayNone.isDefaultNow());

		assertEquals(DefaultToEnum.ONE, enumOne.getDefaultConstant());
		assertEquals(DefaultToEnum.TWO, enumTwo.getDefaultConstant());
		assertEquals(null, enumNone.getDefaultConstant());

		assertInfo(model.getSequenceInfo(), TYPE.getThis(), integerNext);
		assertInfo(TYPE, TYPE.getPrimaryKeyInfo());
		assertInfo(integerNext, integerNext.getDefaultToNextInfo());
		assertNull(integerNone.getDefaultToNextInfo());
		{
			final Date before = new Date();
			final DefaultToItem item = deleteOnTearDown(new DefaultToItem(
					booleanNone.map(false)
			));
			final Date after = new Date();
			assertEquals(TRUE, item.getBooleanTrue());
			assertEquals(false, item.getBooleanNone());
			assertEquals(5, item.getIntegerFive());
			assertEquals(integer(50), item.getIntegerFifty());
			assertEquals(integer(10001), item.getIntegerNext());
			assertEquals(null, item.getIntegerNone());
			assertEquals(date(8), item.getDateEight());
			assertEquals(date(80), item.getDateEighty());
			assertWithin(before, after, item.getDateNow());
			assertWithin(before, after, item.getDateNowOpt());
			assertEquals(item.getDateNow(), item.getDateNowOpt());
			assertEquals(null, item.getDateNone());
			assertEquals(new Day(item.getDateNow()), item.getDayNow());
			assertEquals(new Day(item.getDateNow()), item.getDayNowOpt());
			assertEquals(null, item.getDayNone());
			assertEquals(DefaultToEnum.ONE, item.getEnumOne());
			assertEquals(DefaultToEnum.TWO, item.getEnumTwo());
			assertEquals(null, item.getEnumNone());
		}
		assertInfo(model.getSequenceInfo(), TYPE.getThis(), integerNext);
		assertInfo(TYPE, 1, 0, 0, TYPE.getPrimaryKeyInfo());
		assertInfo(integerNext, 1, 10001, 10001, integerNext.getDefaultToNextInfo());
		assertNull(integerNone.getDefaultToNextInfo());
		{
			final Date before = new Date();
			final DefaultToItem item = deleteOnTearDown(new DefaultToItem(
					booleanNone.map(false)
			));
			final Date after = new Date();
			assertEquals(TRUE, item.getBooleanTrue());
			assertEquals(false, item.getBooleanNone());
			assertEquals(5, item.getIntegerFive());
			assertEquals(integer(50), item.getIntegerFifty());
			assertEquals(integer(10002), item.getIntegerNext());
			assertEquals(null, item.getIntegerNone());
			assertEquals(date(8), item.getDateEight());
			assertEquals(date(80), item.getDateEighty());
			assertWithin(before, after, item.getDateNow());
			assertWithin(before, after, item.getDateNowOpt());
			assertEquals(item.getDateNow(), item.getDateNowOpt());
			assertEquals(null, item.getDateNone());
			assertEquals(new Day(item.getDateNow()), item.getDayNow());
			assertEquals(new Day(item.getDateNow()), item.getDayNowOpt());
			assertEquals(null, item.getDayNone());
			assertEquals(DefaultToEnum.ONE, item.getEnumOne());
			assertEquals(DefaultToEnum.TWO, item.getEnumTwo());
			assertEquals(null, item.getEnumNone());
		}
		assertInfo(model.getSequenceInfo(), TYPE.getThis(), integerNext);
		assertInfo(TYPE, 2, 0, 1, TYPE.getPrimaryKeyInfo());
		assertInfo(integerNext, 2, 10001, 10002, integerNext.getDefaultToNextInfo());
		assertNull(integerNone.getDefaultToNextInfo());
		{
			final DefaultToItem item = deleteOnTearDown(new DefaultToItem(
					booleanTrue.map(false),
					booleanNone.map(true),
					integerFive.map(6),
					integerFifty.map(51),
					integerNext.map(7001),
					dateEight.map(date(9)),
					dateEighty.map(date(81)),
					dateNow.map(date(501)),
					dateNowOpt.map(date(502)),
					dateNone.map(date(503)),
					dayNow.map(day(2010, 1, 13)),
					dayNowOpt.map(day(2010, 1, 14)),
					dayNone.map(day(2010, 1, 15)),
					enumOne.map(DefaultToEnum.THREE),
					enumTwo.map(DefaultToEnum.ONE),
					enumNone.map(DefaultToEnum.TWO)
			));
			assertEquals(FALSE, item.getBooleanTrue());
			assertEquals(true, item.getBooleanNone());
			assertEquals(6, item.getIntegerFive());
			assertEquals(integer(51), item.getIntegerFifty());
			assertEquals(integer(7001), item.getIntegerNext());
			assertEquals(null, item.getIntegerNone());
			assertEquals(date(9), item.getDateEight());
			assertEquals(date(81), item.getDateEighty());
			assertEquals(date(501), item.getDateNow());
			assertEquals(date(502), item.getDateNowOpt());
			assertEquals(date(503), item.getDateNone());
			assertEquals(day(2010, 1, 13), item.getDayNow());
			assertEquals(day(2010, 1, 14), item.getDayNowOpt());
			assertEquals(day(2010, 1, 15), item.getDayNone());
			assertEquals(DefaultToEnum.THREE, item.getEnumOne());
			assertEquals(DefaultToEnum.ONE, item.getEnumTwo());
			assertEquals(DefaultToEnum.TWO, item.getEnumNone());
		}
		assertInfo(model.getSequenceInfo(), TYPE.getThis(), integerNext);
		assertInfo(TYPE, 3, 0, 2, TYPE.getPrimaryKeyInfo());
		assertInfo(integerNext, 2, 10001, 10002, integerNext.getDefaultToNextInfo());
		assertNull(integerNone.getDefaultToNextInfo());
		{
			final Date before = new Date();
			final DefaultToItem item = new DefaultToItem(
					booleanTrue.map(null),
					booleanNone.map(true),
					integerFifty.map(null),
					integerNext.map(null),
					dateEighty.map(null),
					dateNowOpt.map(null),
					dateNone.map(null),
					dayNowOpt.map(null),
					dayNone.map(null),
					enumOne.map(DefaultToEnum.TWO),
					enumTwo.map(null),
					enumNone.map(null)
			);
			final Date after = new Date();
			deleteOnTearDown(item);
			assertEquals(null, item.getBooleanTrue());
			assertEquals(true, item.getBooleanNone());
			assertEquals(5, item.getIntegerFive());
			assertEquals(null, item.getIntegerFifty());
			assertEquals(null, item.getIntegerNext());
			assertEquals(null, item.getIntegerNone());
			assertEquals(date(8), item.getDateEight());
			assertEquals(null, item.getDateEighty());
			assertWithin(before, after, item.getDateNow());
			assertEquals(null, item.getDateNowOpt());
			assertEquals(null, item.getDateNone());
			assertEquals(new Day(item.getDateNow()), item.getDayNow());
			assertEquals(null, item.getDayNowOpt());
			assertEquals(null, item.getDayNone());
			assertEquals(DefaultToEnum.TWO, item.getEnumOne());
			assertEquals(null, item.getEnumTwo());
			assertEquals(null, item.getEnumNone());
		}
		assertInfo(model.getSequenceInfo(), TYPE.getThis(), integerNext);
		assertInfo(TYPE, 4, 0, 3, TYPE.getPrimaryKeyInfo());
		assertInfo(integerNext, 2, 10001, 10002, integerNext.getDefaultToNextInfo());
		assertNull(integerNone.getDefaultToNextInfo());

		try
		{
			integerFifty.defaultToNext(88);
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("cannot use defaultConstant and defaultNext together", e.getMessage());
		}
		try
		{
			integerNext.defaultTo(99);
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("cannot use defaultConstant and defaultNext together", e.getMessage());
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
			dateEight.defaultToNow();
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("cannot use defaultConstant and defaultNow together", e.getMessage());
		}
		try
		{
			dateNow.defaultTo(new Date(444));
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("cannot use defaultConstant and defaultNow together", e.getMessage());
		}
		try
		{
			dayNow.defaultTo(new Day(2010, 1, 13));
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("cannot use defaultConstant and defaultNow together", e.getMessage());
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

		// test breaking the sequence
		{
			final boolean c = model.getConnectProperties().primaryKeyGenerator.persistent;

			assertInfo(integerNext, 2, 10001, 10002, integerNext.getDefaultToNextInfo());

			final DefaultToItem item1 = deleteOnTearDown(new DefaultToItem(booleanNone.map(false)));
			assertEquals(integer(10003), item1.getIntegerNext());
			assertInfo(integerNext, 3, 10001, 10003, integerNext.getDefaultToNextInfo());

			restartTransaction();
			assertEquals(integer(10003), item1.getIntegerNext());
			assertInfo(integerNext, 3, 10001, 10003, integerNext.getDefaultToNextInfo());

			final DefaultToItem item2 = deleteOnTearDown(new DefaultToItem(booleanNone.map(false), integerNext.map(10028)));
			assertEquals(integer(10028), item2.getIntegerNext());
			assertInfo(integerNext, 3, 10001, 10003, integerNext.getDefaultToNextInfo(), hsqldb?25:0);

			restartTransaction();
			assertEquals(integer(10028), item2.getIntegerNext());
			assertInfo(integerNext, 3, 10001, 10003, integerNext.getDefaultToNextInfo(), (c&&oracle)?7:25);
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
}
