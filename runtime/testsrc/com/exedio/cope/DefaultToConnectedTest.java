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
import static com.exedio.cope.DefaultToItem.DefaultToEnum.ONE;
import static com.exedio.cope.DefaultToItem.DefaultToEnum.THREE;
import static com.exedio.cope.DefaultToItem.DefaultToEnum.TWO;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import java.util.Date;

import com.exedio.cope.util.Day;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public class DefaultToConnectedTest extends AbstractRuntimeTest
{
	public DefaultToConnectedTest()
	{
		super(DefaultToTest.MODEL);
	}

	@SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_INFERRED")
	public void testIt()
	{
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
			assertEquals(ONE, item.getEnumOne());
			assertEquals(TWO, item.getEnumTwo());
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
			assertEquals(ONE, item.getEnumOne());
			assertEquals(TWO, item.getEnumTwo());
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
					enumOne.map(THREE),
					enumTwo.map(ONE),
					enumNone.map(TWO)
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
			assertEquals(THREE, item.getEnumOne());
			assertEquals(ONE, item.getEnumTwo());
			assertEquals(TWO, item.getEnumNone());
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
					enumOne.map(TWO),
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
			assertEquals(TWO, item.getEnumOne());
			assertEquals(null, item.getEnumTwo());
			assertEquals(null, item.getEnumNone());
		}
		assertInfo(model.getSequenceInfo(), TYPE.getThis(), integerNext);
		assertInfo(TYPE, 4, 0, 3, TYPE.getPrimaryKeyInfo());
		assertInfo(integerNext, 2, 10001, 10002, integerNext.getDefaultToNextInfo());
		assertNull(integerNone.getDefaultToNextInfo());

		// test breaking the sequence
		{
			final boolean c = model.getConnectProperties().primaryKeyGenerator.persistent;

			assertInfo(integerNext, 2, 10001, 10002, integerNext.getDefaultToNextInfo());

			final DefaultToItem item1 = deleteOnTearDown(new DefaultToItem(booleanNone.map(false)));
			assertEquals(integer(10003), item1.getIntegerNext());
			model.commit();
			assertInfo(integerNext, 3, 10001, 10003, integerNext.getDefaultToNextInfo());
			assertEquals(0, integerNext.checkDefaultToNext());
			model.startTransaction(DefaultToConnectedTest.class.getName());

			assertEquals(integer(10003), item1.getIntegerNext());
			model.commit();
			assertInfo(integerNext, 3, 10001, 10003, integerNext.getDefaultToNextInfo());
			assertEquals(0, integerNext.checkDefaultToNext());
			model.startTransaction(DefaultToConnectedTest.class.getName());

			final DefaultToItem item2 = deleteOnTearDown(new DefaultToItem(booleanNone.map(false), integerNext.map(10028)));
			assertEquals(integer(10028), item2.getIntegerNext());
			model.commit();
			assertInfo(integerNext, 3, 10001, 10003, integerNext.getDefaultToNextInfo());
			assertEquals(25, integerNext.checkDefaultToNext());
			model.startTransaction(DefaultToConnectedTest.class.getName());

			assertEquals(integer(10028), item2.getIntegerNext());
			model.commit();
			assertInfo(integerNext, 3, 10001, 10003, integerNext.getDefaultToNextInfo());
			assertEquals((c&&oracle)?7:25, integerNext.checkDefaultToNext());
			model.startTransaction(DefaultToConnectedTest.class.getName());
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
