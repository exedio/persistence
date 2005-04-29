/*
 * Copyright (C) 2004-2005  exedio GmbH (www.exedio.com)
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

package com.exedio.cope.lib;

import com.exedio.cope.testmodel.StringItem;

public class StringTest extends DatabaseLibTest
{
	StringItem item;

	public void setUp() throws Exception
	{
		super.setUp();
		deleteOnTearDown(item = new StringItem());
	}
	
	public void testStrings() throws LengthViolationException
	{
		assertEquals(0, item.any.getMinimumLength());
		assertEquals(Integer.MAX_VALUE, item.any.getMaximumLength());
		assertEquals(false, item.any.isLengthConstrained());

		assertEquals(4, item.min4.getMinimumLength());
		assertEquals(Integer.MAX_VALUE, item.min4.getMaximumLength());
		assertEquals(true, item.min4.isLengthConstrained());

		assertEquals(0, item.max4.getMinimumLength());
		assertEquals(4, item.max4.getMaximumLength());
		assertEquals(true, item.max4.isLengthConstrained());

		assertEquals(4, item.min4Max8.getMinimumLength());
		assertEquals(8, item.min4Max8.getMaximumLength());
		assertEquals(true, item.min4Max8.isLengthConstrained());
		
		// any
		item.setAny("1234");
		assertEquals("1234", item.getAny());
		item.setAny("123");
		assertEquals("123", item.getAny());
		
		// test SQL injection
		// if SQL injection is not prevented properly,
		// the following line will throw a SQLException
		// due to column "hijackedColumn" not found
		item.setAny("value',hijackedColumn='otherValue");
		assertEquals("value',hijackedColumn='otherValue", item.getAny());
		item.passivateCopeItem();
		// TODO: sql injection protection just swallows apostrophes,
		// should be escaped or wrapped into prepared statements
		assertEquals("value,hijackedColumn=otherValue", item.getAny());

		// test full unicode support
		final String unicodeString =
			"Auml \u00c4; "
			+ "Ouml \u00d6; "
			+ "Uuml \u00dc; "
			+ "auml \u00e4; "
			+ "ouml \u00f6; "
			+ "uuml \u00fc; "
			+ "szlig \u00df; "
			+ "paragraph \u00a7; "
			+ "kringel \u00b0";
		//System.out.println(unicodeString);
		item.setAny(unicodeString);
		assertEquals(unicodeString, item.getAny());
		item.passivateCopeItem();
		assertEquals(unicodeString, item.getAny());
		
		// min4
		try
		{
			item.setMin4("123");
			fail("should have thrown LengthViolationException");
		}
		catch(LengthViolationException e)
		{
			assertEquals(item, e.getItem());
			assertEquals(item.min4, e.getStringAttribute());
			assertEquals("123", e.getValue());
			assertEquals(true, e.isTooShort());
		}
		assertEquals(null, item.getMin4());

		item.setMin4("1234");
		assertEquals("1234", item.getMin4());

		// max4		
		item.setMax4("1234");
		assertEquals("1234", item.getMax4());
		try
		{
			item.setMax4("12345");
			fail("should have thrown LengthViolationException");
		}
		catch(LengthViolationException e)
		{
			assertEquals(item, e.getItem());
			assertEquals(item.max4, e.getStringAttribute());
			assertEquals("12345", e.getValue());
			assertEquals(false, e.isTooShort());
		}
		assertEquals("1234", item.getMax4());

		// min4max8		
		try
		{
			item.setMin4Max8("123");
			fail("should have thrown LengthViolationException");
		}
		catch(LengthViolationException e)
		{
			assertEquals(item, e.getItem());
			assertEquals(item.min4Max8, e.getStringAttribute());
			assertEquals("123", e.getValue());
			assertEquals(true, e.isTooShort());
		}
		assertEquals(null, item.getMin4Max8());

		item.setMin4Max8("1234");
		assertEquals("1234", item.getMin4Max8());

		item.setMin4Max8("12345678");
		assertEquals("12345678", item.getMin4Max8());

		item.passivateCopeItem();
		assertEquals("12345678", item.getMin4Max8());

		try
		{
			item.setMin4Max8("123456789");
			fail("should have thrown LengthViolationException");
		}
		catch(LengthViolationException e)
		{
			assertEquals(item, e.getItem());
			assertEquals(item.min4Max8, e.getStringAttribute());
			assertEquals("123456789", e.getValue());
			assertEquals(false, e.isTooShort());
		}
		assertEquals("12345678", item.getMin4Max8());

		item.passivateCopeItem();
		assertEquals("12345678", item.getMin4Max8());
	}

}
