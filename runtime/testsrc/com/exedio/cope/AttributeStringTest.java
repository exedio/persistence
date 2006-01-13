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

import com.exedio.cope.testmodel.AttributeItem;


public class AttributeStringTest extends AttributeTest
{
	public void testSomeString() throws ConstraintViolationException
	{
		// test model
		assertEquals(item.TYPE, item.someString.getType());
		assertEquals(item.TYPE, item.someStringUpperCase.getType());
		assertEquals("someString", item.someString.getName());
		assertEqualsUnmodifiable(list(), item.someString.getPatterns());
		assertEquals("someStringUpperCase", item.someStringUpperCase.getName());

		// test conditions
		assertEquals(item.someString.equal("hallo"), item.someString.equal("hallo"));
		assertNotEquals(item.someString.equal("hallo"), item.someString.equal("bello"));
		assertNotEquals(item.someString.equal("hallo"), item.someString.equal((String)null));
		assertNotEquals(item.someString.equal("hallo"), item.someString.like("hallo"));
		assertEquals(item.someString.equal(item.someNotNullString), item.someString.equal(item.someNotNullString));
		assertNotEquals(item.someString.equal(item.someNotNullString), item.someString.equal(item.someString));
	}
	
	public void testSomeNotNullString()
		throws MandatoryViolationException
	{
		assertEquals(item.TYPE, item.someNotNullString.getType());
		assertEquals("someString", item.getSomeNotNullString());

		item.setSomeNotNullString("someOtherString");
		assertEquals("someOtherString", item.getSomeNotNullString());

		try
		{
			item.setSomeNotNullString(null);
			fail();
		}
		catch (MandatoryViolationException e)
		{
			assertEquals(item, e.getItem());
			assertEquals(item.someNotNullString, e.getMandatoryAttribute());
			assertEquals("mandatory violation on " + item + " for AttributeItem#someNotNullString", e.getMessage());
		}

		try
		{
			new AttributeItem(null, 5, 6l, 2.2, true, someItem, AttributeItem.SomeEnum.enumValue1);
			fail();
		}
		catch(MandatoryViolationException e)
		{
			assertEquals(null, e.getItem());
			assertEquals(item.someNotNullString, e.getMandatoryAttribute());
			assertEquals("mandatory violation on a newly created item for AttributeItem#someNotNullString", e.getMessage());
		}
	}

}
