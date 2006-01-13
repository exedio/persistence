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

package com.exedio.cope;

import com.exedio.cope.testmodel.StringItem;

public class StringTest extends TestmodelTest
{
	StringItem item, item2;
	
	public void setUp() throws Exception
	{
		super.setUp();
		deleteOnTearDown(item = new StringItem("StringTest"));
		deleteOnTearDown(item2 = new StringItem("StringTest2"));
	}
	
	public void testStrings() throws ConstraintViolationException
	{
		assertEquals(0, item.any.getMinimumLength());
		assertEquals(StringAttribute.DEFAULT_LENGTH, item.any.getMaximumLength());
		assertEquals(false, item.any.hasLengthConstraintCheckedException());

		assertEquals(4, item.min4.getMinimumLength());
		assertEquals(StringAttribute.DEFAULT_LENGTH, item.min4.getMaximumLength());
		assertEquals(true, item.min4.hasLengthConstraintCheckedException());

		assertEquals(0, item.max4.getMinimumLength());
		assertEquals(4, item.max4.getMaximumLength());
		assertEquals(true, item.max4.hasLengthConstraintCheckedException());

		assertEquals(0, item.max5Unchecked.getMinimumLength());
		assertEquals(5, item.max5Unchecked.getMaximumLength());
		assertEquals(false, item.max5Unchecked.hasLengthConstraintCheckedException());

		assertEquals(4, item.min4Max8.getMinimumLength());
		assertEquals(8, item.min4Max8.getMaximumLength());
		assertEquals(true, item.min4Max8.hasLengthConstraintCheckedException());
		
		assertEquals(6, item.exact6.getMinimumLength());
		assertEquals(6, item.exact6.getMaximumLength());
		assertEquals(true, item.exact6.hasLengthConstraintCheckedException());
		
		// any
		item.setAny("1234");
		assertEquals("1234", item.getAny());
		item.setAny("123");
		assertEquals("123", item.getAny());
		
		// standard tests
		item.setAny(null);
		assertString(item, item2, item.any);
		assertString(item, item2, item.long1K);
		assertString(item, item2, item.long1M);
		
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
			assertEquals("length violation on StringItem.0, '123' is too short for StringItem#min4", e.getMessage());
		}
		assertEquals(null, item.getMin4());
		restartTransaction();
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
			assertEquals("length violation on StringItem.0, '12345' is too long for StringItem#max4", e.getMessage());
		}
		assertEquals("1234", item.getMax4());
		restartTransaction();
		assertEquals("1234", item.getMax4());

		// max5Unchecked
		item.setMax5Unchecked("12345");
		assertEquals("12345", item.getMax5Unchecked());
		try
		{
			item.setMax5Unchecked("123456");
			fail("should have thrown LengthViolationException");
		}
		catch(LengthViolationRuntimeException e)
		{
			assertEquals(item, e.getItem());
			assertEquals(item.max5Unchecked, e.getStringAttribute());
			assertEquals("123456", e.getValue());
			assertEquals(false, e.isTooShort());
			assertEquals("length violation on StringItem.0, '123456' is too long for StringItem#max5Unchecked", e.getMessage());
		}
		assertEquals("12345", item.getMax5Unchecked());
		restartTransaction();
		assertEquals("12345", item.getMax5Unchecked());

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
			assertEquals("length violation on StringItem.0, '123' is too short for StringItem#min4Max8", e.getMessage());
		}
		assertEquals(null, item.getMin4Max8());
		restartTransaction();
		assertEquals(null, item.getMin4Max8());

		item.setMin4Max8("1234");
		assertEquals("1234", item.getMin4Max8());

		item.setMin4Max8("12345678");
		assertEquals("12345678", item.getMin4Max8());

		restartTransaction();
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
			assertEquals("length violation on StringItem.0, '123456789' is too long for StringItem#min4Max8", e.getMessage());
		}
		assertEquals("12345678", item.getMin4Max8());
		restartTransaction();
		assertEquals("12345678", item.getMin4Max8());

		// exact6
		try
		{
			item.setExact6("12345");
			fail("should have thrown LengthViolationException");
		}
		catch(LengthViolationException e)
		{
			assertEquals(item, e.getItem());
			assertEquals(item.exact6, e.getStringAttribute());
			assertEquals("12345", e.getValue());
			assertEquals(true, e.isTooShort());
			assertEquals("length violation on StringItem.0, '12345' is too short for StringItem#exact6", e.getMessage());
		}
		assertEquals(null, item.getExact6());
		restartTransaction();
		assertEquals(null, item.getExact6());

		item.setExact6("123456");
		assertEquals("123456", item.getExact6());

		restartTransaction();
		assertEquals("123456", item.getExact6());

		try
		{
			item.setExact6("1234567");
			fail("should have thrown LengthViolationException");
		}
		catch(LengthViolationException e)
		{
			assertEquals(item, e.getItem());
			assertEquals(item.exact6, e.getStringAttribute());
			assertEquals("1234567", e.getValue());
			assertEquals(false, e.isTooShort());
			assertEquals("length violation on StringItem.0, '1234567' is too long for StringItem#exact6", e.getMessage());
		}
		assertEquals("123456", item.getExact6());
		restartTransaction();
		assertEquals("123456", item.getExact6());
	}

}
