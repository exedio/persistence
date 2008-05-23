/*
 * Copyright (C) 2004-2008  exedio GmbH (www.exedio.com)
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

import java.util.Date;

import com.exedio.cope.DefaultToItem.DefaultToEnum;

public class DefaultToTest extends AbstractRuntimeTest
{
	static final Model MODEL = new Model(DefaultToItem.TYPE);

	public DefaultToTest()
	{
		super(MODEL);
	}
	
	public void testIt()
	{
		// test model
		assertEquals(TRUE, DefaultToItem.booleanTrue.getDefaultConstant());
		assertEquals(null, DefaultToItem.booleanNone.getDefaultConstant());
		assertEquals(integer(5), DefaultToItem.integerFive.getDefaultConstant());
		assertEquals(integer(50), DefaultToItem.integerFifty.getDefaultConstant());
		assertEquals(null, DefaultToItem.integerNext.getDefaultConstant());
		assertEquals(null, DefaultToItem.integerNone.getDefaultConstant());

		assertEquals(false, DefaultToItem.integerFive.isDefaultNext());
		assertEquals(false, DefaultToItem.integerFifty.isDefaultNext());
		assertEquals(true,  DefaultToItem.integerNext.isDefaultNext());
		assertEquals(false, DefaultToItem.integerNone.isDefaultNext());
		
		assertEquals(null, DefaultToItem.integerFive.getDefaultNextStart());
		assertEquals(null, DefaultToItem.integerFifty.getDefaultNextStart());
		assertEquals(integer(10001), DefaultToItem.integerNext.getDefaultNextStart());
		assertEquals(null, DefaultToItem.integerNone.getDefaultNextStart());
		
		assertEquals(date(8), DefaultToItem.dateEight.getDefaultConstant());
		assertEquals(date(80), DefaultToItem.dateEighty.getDefaultConstant());
		assertEquals(null, DefaultToItem.dateNow.getDefaultConstant());
		assertEquals(null, DefaultToItem.dateNowOpt.getDefaultConstant());
		assertEquals(null, DefaultToItem.dateNone.getDefaultConstant());
		
		assertEquals(false, DefaultToItem.dateEight.isDefaultNow());
		assertEquals(false, DefaultToItem.dateEighty.isDefaultNow());
		assertEquals(true,  DefaultToItem.dateNow.isDefaultNow());
		assertEquals(true,  DefaultToItem.dateNowOpt.isDefaultNow());
		assertEquals(false, DefaultToItem.dateNone.isDefaultNow());
		
		assertEquals(DefaultToEnum.ONE, DefaultToItem.enumOne.getDefaultConstant());
		assertEquals(DefaultToEnum.TWO, DefaultToItem.enumTwo.getDefaultConstant());
		assertEquals(null, DefaultToItem.enumNone.getDefaultConstant());
		
		{
			final Date before = new Date();
			final DefaultToItem item = deleteOnTearDown(new DefaultToItem(
					DefaultToItem.booleanNone.map(false)
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
			assertEquals(DefaultToEnum.ONE, item.getEnumOne());
			assertEquals(DefaultToEnum.TWO, item.getEnumTwo());
			assertEquals(null, item.getEnumNone());
		}
		{
			final Date before = new Date();
			final DefaultToItem item = deleteOnTearDown(new DefaultToItem(
					DefaultToItem.booleanNone.map(false)
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
			assertEquals(DefaultToEnum.ONE, item.getEnumOne());
			assertEquals(DefaultToEnum.TWO, item.getEnumTwo());
			assertEquals(null, item.getEnumNone());
		}
		{
			final DefaultToItem item = deleteOnTearDown(new DefaultToItem(
					DefaultToItem.booleanTrue.map(false),
					DefaultToItem.booleanNone.map(true),
					DefaultToItem.integerFive.map(6),
					DefaultToItem.integerFifty.map(51),
					DefaultToItem.integerNext.map(20001),
					DefaultToItem.dateEight.map(date(9)),
					DefaultToItem.dateEighty.map(date(81)),
					DefaultToItem.dateNow.map(date(501)),
					DefaultToItem.dateNowOpt.map(date(502)),
					DefaultToItem.dateNone.map(date(503)),
					DefaultToItem.enumOne.map(DefaultToEnum.THREE),
					DefaultToItem.enumTwo.map(DefaultToEnum.ONE),
					DefaultToItem.enumNone.map(DefaultToEnum.TWO)
			));
			assertEquals(FALSE, item.getBooleanTrue());
			assertEquals(true, item.getBooleanNone());
			assertEquals(6, item.getIntegerFive());
			assertEquals(integer(51), item.getIntegerFifty());
			assertEquals(integer(20001), item.getIntegerNext());
			assertEquals(null, item.getIntegerNone());
			assertEquals(date(9), item.getDateEight());
			assertEquals(date(81), item.getDateEighty());
			assertEquals(date(501), item.getDateNow());
			assertEquals(date(502), item.getDateNowOpt());
			assertEquals(date(503), item.getDateNone());
			assertEquals(DefaultToEnum.THREE, item.getEnumOne());
			assertEquals(DefaultToEnum.ONE, item.getEnumTwo());
			assertEquals(DefaultToEnum.TWO, item.getEnumNone());
		}
		{
			final Date before = new Date();
			final DefaultToItem item = new DefaultToItem(
					DefaultToItem.booleanTrue.map(null),
					DefaultToItem.booleanNone.map(true),
					DefaultToItem.integerFifty.map(null),
					DefaultToItem.integerNext.map(null),
					DefaultToItem.dateEighty.map(null),
					DefaultToItem.dateNowOpt.map(null),
					DefaultToItem.dateNone.map(null),
					DefaultToItem.enumOne.map(DefaultToEnum.TWO),
					DefaultToItem.enumTwo.map(null),
					DefaultToItem.enumNone.map(null)
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
			assertEquals(DefaultToEnum.TWO, item.getEnumOne());
			assertEquals(null, item.getEnumTwo());
			assertEquals(null, item.getEnumNone());
		}

		try
		{
			DefaultToItem.integerFifty.defaultToNext(88);
			fail();
		}
		catch(IllegalStateException e)
		{
			assertEquals("cannot use defaultConstant and defaultNext together", e.getMessage());
		}
		try
		{
			DefaultToItem.integerNext.defaultTo(99);
			fail();
		}
		catch(IllegalStateException e)
		{
			assertEquals("cannot use defaultConstant and defaultNext together", e.getMessage());
		}
		try
		{
			DefaultToItem.integerNext.min(10002);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals(
					"The start value for defaultToNext of the field does not comply to one of it's own constraints, " +
					"caused a IntegerRangeViolationException: " +
					"range violation on a newly created item, " +
					"10001 is too small, " +
					"must be at least 10002. Start value was '10001'.", e.getMessage());
		}
		try
		{
			DefaultToItem.dateEight.defaultToNow();
			fail();
		}
		catch(IllegalStateException e)
		{
			assertEquals("cannot use defaultConstant and defaultNow together", e.getMessage());
		}
		try
		{
			DefaultToItem.dateNow.defaultTo(new Date(444));
			fail();
		}
		catch(IllegalStateException e)
		{
			assertEquals("cannot use defaultConstant and defaultNow together", e.getMessage());
		}
		try
		{
			new StringField().lengthMax(3).defaultTo("1234");
			fail();
		}
		catch(IllegalArgumentException e)
		{
			//e.printStackTrace();
			assertEquals(
					"The default constant of the field does not comply to one of it's own constraints, " +
					"caused a StringLengthViolationException: " +
					"length violation on a newly created item, '1234' is too long, " +
					"must be at most 3 characters, but was 4. " +
					"Default constant was '1234'.",
					e.getMessage());
		}
	}

	private static final Boolean TRUE = Boolean.TRUE;
	private static final Boolean FALSE = Boolean.FALSE;
	
	private static final Integer integer(final int i)
	{
		return new Integer(i);
	}
	
	private static final Date date(final long l)
	{
		return new Date(l);
	}
	
}
