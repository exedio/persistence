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
import com.exedio.cope.testmodel.AttributeItem.SomeEnum;


public class AttributeEnumTest extends AttributeTest
{
	public void testSomeEnum() throws ConstraintViolationException
	{
		assertEquals(null, item.getSomeEnum());
		item.setSomeEnum(AttributeItem.SomeEnum.enumValue1);
		assertEquals(
			AttributeItem.SomeEnum.enumValue1,
			item.getSomeEnum());
		item.setSomeEnum(
			AttributeItem.SomeEnum.enumValue2);
		assertEquals(
			AttributeItem.SomeEnum.enumValue2,
			item.getSomeEnum());
		
		assertContains(item,
				item.TYPE.search(item.someEnum.equal(AttributeItem.SomeEnum.enumValue2)));
		assertContains(item2,
				item.TYPE.search(item.someEnum.equal(null)));
		assertContains(item, item2,
				item.TYPE.search(item.someEnum.notEqual(AttributeItem.SomeEnum.enumValue1)));
		assertContains(item2,
				item.TYPE.search(item.someEnum.notEqual(AttributeItem.SomeEnum.enumValue2)));
		assertContains(item,
				item.TYPE.search(item.someEnum.notEqual(null)));

		assertContains(AttributeItem.SomeEnum.enumValue2, null, search(item.someEnum));
		assertContains(AttributeItem.SomeEnum.enumValue2, search(item.someEnum, item.someEnum.equal(AttributeItem.SomeEnum.enumValue2)));

		item.passivateCopeItem();
		assertEquals(
			AttributeItem.SomeEnum.enumValue2,
			item.getSomeEnum());
		item.setSomeEnum(null);
		assertEquals(null, item.getSomeEnum());
		
		try
		{
			item.set(item.someEnum, new Integer(10));
			fail();
		}
		catch(ClassCastException e)
		{
			assertEquals("expected " + SomeEnum.class.getName() + ", got " + Integer.class.getName() + " for someEnum", e.getMessage());
		}
		
		try
		{
			item.set(item.someEnum, AttributeItem.SomeEnum2.enumValue2);
			fail();
		}
		catch(ClassCastException e)
		{
			assertEquals("expected " + SomeEnum.class.getName() + ", got " + AttributeItem.SomeEnum2.class.getName() + " for someEnum", e.getMessage());
		}
	}

	public void testNotNullSomeEnum()
			throws NotNullViolationException
	{
		assertEquals(AttributeItem.SomeEnum.enumValue1, item.getSomeNotNullEnum());
		item.setSomeNotNullEnum(AttributeItem.SomeEnum.enumValue2);
		assertEquals(
			AttributeItem.SomeEnum.enumValue2,
			item.getSomeNotNullEnum());
		item.setSomeNotNullEnum(
			AttributeItem.SomeEnum.enumValue3);
		assertEquals(
			AttributeItem.SomeEnum.enumValue3,
			item.getSomeNotNullEnum());
		item.passivateCopeItem();
		assertEquals(
			AttributeItem.SomeEnum.enumValue3,
			item.getSomeNotNullEnum());
		try
		{
			item.setSomeNotNullEnum(null);
		}
		catch(NotNullViolationException e)
		{
			assertEquals(item, e.getItem());
			assertEquals(item.someNotNullEnum, e.getNotNullAttribute());
		}
		assertEquals(
			AttributeItem.SomeEnum.enumValue3,
			item.getSomeNotNullEnum());

		try
		{
			new AttributeItem("someString", 5, 6l, 2.2, true, someItem, null);
			fail("should have thrown NotNullViolationException");
		}
		catch(NotNullViolationException e)
		{
			assertEquals(null, e.getItem());
			assertEquals(item.someNotNullEnum, e.getNotNullAttribute());
		}
	}
}
