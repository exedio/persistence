/*
 * Copyright (C) 2004-2006  exedio GmbH (www.exedio.com)
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


public class DefaultToTest extends AbstractLibTest
{
	public DefaultToTest()
	{
		super(Main.defaultToModel);
	}
	
	public void testIt()
	{
		// test model
		assertEquals(TRUE, DefaultToItem.booleanTrue.getDefaultValue());
		assertEquals(null, DefaultToItem.booleanNone.getDefaultValue());
		assertEquals(integer(5), DefaultToItem.integerFive.getDefaultValue());
		assertEquals(integer(50), DefaultToItem.integerFifty.getDefaultValue());
		assertEquals(null, DefaultToItem.integerNone.getDefaultValue());
		assertEquals(date(8), DefaultToItem.dateEight.getDefaultValue());
		assertEquals(date(80), DefaultToItem.dateEighty.getDefaultValue());
		assertEquals(null, DefaultToItem.dateNone.getDefaultValue());
		
		{
			final DefaultToItem item = new DefaultToItem(new SetValue[]{
					DefaultToItem.booleanNone.map(false),
			});
			deleteOnTearDown(item);
			assertEquals(TRUE, item.getBooleanTrue());
			assertEquals(false, item.getBooleanNone());
			assertEquals(5, item.getIntegerFive());
			assertEquals(integer(50), item.getIntegerFifty());
			assertEquals(null, item.getIntegerNone());
			assertEquals(date(8), item.getDateEight());
			assertEquals(date(80), item.getDateEighty());
			assertEquals(null, item.getDateNone());
		}
		{
			final DefaultToItem item = new DefaultToItem(new SetValue[]{
					DefaultToItem.booleanTrue.map(false),
					DefaultToItem.booleanNone.map(true),
					DefaultToItem.integerFive.map(6),
					DefaultToItem.integerFifty.map(51),
					DefaultToItem.dateEight.map(date(9)),
					DefaultToItem.dateEighty.map(date(81)),
			});
			deleteOnTearDown(item);
			assertEquals(FALSE, item.getBooleanTrue());
			assertEquals(true, item.getBooleanNone());
			assertEquals(6, item.getIntegerFive());
			assertEquals(integer(51), item.getIntegerFifty());
			assertEquals(null, item.getIntegerNone());
			assertEquals(date(9), item.getDateEight());
			assertEquals(date(81), item.getDateEighty());
			assertEquals(null, item.getDateNone());
		}
		{
			final DefaultToItem item = new DefaultToItem(new SetValue[]{
					DefaultToItem.booleanTrue.map(null),
					DefaultToItem.booleanNone.map(true),
					DefaultToItem.integerFifty.map(null),
					DefaultToItem.dateEighty.map(null),
			});
			deleteOnTearDown(item);
			assertEquals(null, item.getBooleanTrue());
			assertEquals(true, item.getBooleanNone());
			assertEquals(5, item.getIntegerFive());
			assertEquals(null, item.getIntegerFifty());
			assertEquals(null, item.getIntegerNone());
			assertEquals(date(8), item.getDateEight());
			assertEquals(null, item.getDateEighty());
			assertEquals(null, item.getDateNone());
		}

		try
		{
			new StringAttribute(Item.OPTIONAL).lengthMax(3).defaultTo("1234");
			fail();
		}
		catch(RuntimeException e)
		{
			//e.printStackTrace();
			assertEquals(
					"The default value of the attribute does not comply to one of it's own constraints, " +
					"caused a LengthViolationException. Default value was 1234",
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
