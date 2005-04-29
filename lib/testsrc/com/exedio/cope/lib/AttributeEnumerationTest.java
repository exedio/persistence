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

import com.exedio.cope.testmodel.AttributeItem;


public class AttributeEnumerationTest extends AttributeTest
{
	public void testSomeEnumeration()
	{
		assertEquals(null, item.getSomeEnumeration());
		item.setSomeEnumeration(AttributeItem.SomeEnumeration.enumValue1);
		assertEquals(
			AttributeItem.SomeEnumeration.enumValue1,
			item.getSomeEnumeration());
		item.setSomeEnumeration(
			AttributeItem.SomeEnumeration.enumValue2);
		assertEquals(
			AttributeItem.SomeEnumeration.enumValue2,
			item.getSomeEnumeration());
		
		assertContains(item,
				item.TYPE.search(Cope.equal(item.someEnumeration, AttributeItem.SomeEnumeration.enumValue2)));
		assertContains(item2,
				item.TYPE.search(Cope.equal(item.someEnumeration, null)));

		assertContains(AttributeItem.SomeEnumeration.enumValue2, null, search(item.someEnumeration));
		assertContains(AttributeItem.SomeEnumeration.enumValue2, search(item.someEnumeration, Cope.equal(item.someEnumeration, AttributeItem.SomeEnumeration.enumValue2)));

		item.passivateItem();
		assertEquals(
			AttributeItem.SomeEnumeration.enumValue2,
			item.getSomeEnumeration());
		item.setSomeEnumeration(null);
		assertEquals(null, item.getSomeEnumeration());
	}

	public void testNotNullSomeEnumeration()
			throws NotNullViolationException
	{
		assertEquals(AttributeItem.SomeEnumeration.enumValue1, item.getSomeNotNullEnumeration());
		item.setSomeNotNullEnumeration(AttributeItem.SomeEnumeration.enumValue2);
		assertEquals(
			AttributeItem.SomeEnumeration.enumValue2,
			item.getSomeNotNullEnumeration());
		item.setSomeNotNullEnumeration(
			AttributeItem.SomeEnumeration.enumValue3);
		assertEquals(
			AttributeItem.SomeEnumeration.enumValue3,
			item.getSomeNotNullEnumeration());
		item.passivateItem();
		assertEquals(
			AttributeItem.SomeEnumeration.enumValue3,
			item.getSomeNotNullEnumeration());
		try
		{
			item.setSomeNotNullEnumeration(null);
		}
		catch(NotNullViolationException e)
		{
			assertEquals(item, e.getItem());
			assertEquals(item.someNotNullEnumeration, e.getNotNullAttribute());
		}
		assertEquals(
			AttributeItem.SomeEnumeration.enumValue3,
			item.getSomeNotNullEnumeration());

		try
		{
			new AttributeItem("someString", 5, 6l, 2.2, true, someItem, null);
			fail("should have thrown NotNullViolationException");
		}
		catch(NotNullViolationException e)
		{
			assertEquals(null, e.getItem());
			assertEquals(item.someNotNullEnumeration, e.getNotNullAttribute());
		}
	}
}
