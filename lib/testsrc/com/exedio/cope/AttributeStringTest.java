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
		assertEquals(item.TYPE, item.someString.getType());
		assertEquals(item.TYPE, item.someStringUpperCase.getType());
		assertEquals("someString", item.someString.getName());
		assertEquals("someStringUpperCase", item.someStringUpperCase.getName());
		
		{
			final StringAttribute copy = (StringAttribute)Item.stringAttribute(Item.DEFAULT).copyAsTemplate();
			assertEquals(false, copy.isReadOnly());
			assertEquals(false, copy.isNotNull());
			assertEquals(false, copy.isLengthConstrained());
			assertEquals(0, copy.getMinimumLength());
			assertEquals(Integer.MAX_VALUE, copy.getMaximumLength());
		}
		{
			final StringAttribute copy = (StringAttribute)Item.stringAttribute(Item.READ_ONLY, 10).copyAsTemplate();
			assertEquals(true, copy.isReadOnly());
			assertEquals(false, copy.isNotNull());
			assertEquals(true, copy.isLengthConstrained());
			assertEquals(10, copy.getMinimumLength());
			assertEquals(Integer.MAX_VALUE, copy.getMaximumLength());
		}
		{
			final StringAttribute copy = (StringAttribute)Item.stringAttribute(Item.NOT_NULL, 10, 20).copyAsTemplate();
			assertEquals(false, copy.isReadOnly());
			assertEquals(true, copy.isNotNull());
			assertEquals(true, copy.isLengthConstrained());
			assertEquals(10, copy.getMinimumLength());
			assertEquals(20, copy.getMaximumLength());
		}

		assertEquals(null, item.getSomeString());
		assertEquals(null, item.getSomeStringUpperCase());
		assertEquals(null, item.getSomeStringLength());
		item.setSomeString("someString");
		assertEquals("someString", item.getSomeString());
		assertEquals("SOMESTRING", item.getSomeStringUpperCase());
		assertEquals(new Integer("someString".length()), item.getSomeStringLength());
		assertContains(item, item.TYPE.search(item.someString.equal("someString")));
		assertContains(item2, item.TYPE.search(item.someString.notEqual("someString")));
		assertContains(item.TYPE.search(item.someString.equal("SOMESTRING")));
		assertContains(item, item.TYPE.search(item.someNotNullString.like("someString")));
		assertContains(item, item2, item.TYPE.search(item.someNotNullString.like("someString%")));
		assertContains(item2, item.TYPE.search(item.someNotNullString.like("someString2%")));

		assertContains(item, item.TYPE.search(item.someStringUpperCase.equal("SOMESTRING")));
		assertContains(item, item.TYPE.search(item.someString.uppercase().equal("SOMESTRING")));
		assertContains(item2, item.TYPE.search(item.someStringUpperCase.notEqual("SOMESTRING")));
		assertContains(item2, item.TYPE.search(item.someString.uppercase().notEqual("SOMESTRING")));
		assertContains(item.TYPE.search(item.someStringUpperCase.equal("someString")));
		assertContains(item.TYPE.search(item.someString.uppercase().equal("someString")));
		
		assertContains(item, item.TYPE.search(item.someStringLength.equal("someString".length())));
		assertContains(item2, item.TYPE.search(item.someStringLength.notEqual("someString".length())));
		assertContains(item.TYPE.search(item.someStringLength.equal("someString".length()+1)));

		assertContains("someString", null, search(item.someString));
		assertContains("someString", search(item.someString, item.someString.equal("someString")));
		// TODO allow functions for select
		//assertContains("SOMESTRING", search(item.someStringUpperCase, item.someString.equal("someString")));

		item.passivateCopeItem();
		assertEquals("someString", item.getSomeString());
		assertEquals("SOMESTRING", item.getSomeStringUpperCase());
		assertEquals(new Integer("someString".length()), item.getSomeStringLength());
		item.setSomeString(null);
		assertEquals(null, item.getSomeString());
		assertEquals(null, item.getSomeStringUpperCase());
		assertEquals(null, item.getSomeStringLength());

		try
		{
			item.set(item.someString, new Integer(10));
			fail();
		}
		catch(ClassCastException e)
		{
			assertEquals("expected string, got " + Integer.class.getName() + " for someString", e.getMessage());
		}
	}

	public void testSomeNotNullString()
		throws NotNullViolationException
	{
		assertEquals(item.TYPE, item.someNotNullString.getType());
		assertEquals("someString", item.getSomeNotNullString());

		item.setSomeNotNullString("someOtherString");
		assertEquals("someOtherString", item.getSomeNotNullString());

		try
		{
			item.setSomeNotNullString(null);
			fail("should have thrown NotNullViolationException");
		}
		catch (NotNullViolationException e)
		{
			assertEquals(item, e.getItem());
			assertEquals(item.someNotNullString, e.getNotNullAttribute());
		}

		try
		{
			new AttributeItem(null, 5, 6l, 2.2, true, someItem, AttributeItem.SomeEnum.enumValue1);
			fail("should have thrown NotNullViolationException");
		}
		catch(NotNullViolationException e)
		{
			assertEquals(null, e.getItem());
			assertEquals(item.someNotNullString, e.getNotNullAttribute());
		}
	}

}
