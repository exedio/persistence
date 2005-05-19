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
import com.exedio.cope.testmodel.EmptyItem;


public class AttributeItemTest extends AttributeTest
{
	public void testSomeItem() throws ConstraintViolationException
	{
		assertEquals(item.TYPE, item.someItem.getType());
		assertEquals(EmptyItem.TYPE, item.someItem.getTargetType());
		assertEquals(null, item.getSomeItem());
		item.setSomeItem(someItem);
		assertEquals(someItem, item.getSomeItem());

		assertContains(item,
				item.TYPE.search(Cope.equal(item.someItem, someItem)));
		assertContains(item2,
				item.TYPE.search(Cope.equal(item.someItem, null)));

		assertContains(someItem, null, search(item.someItem));
		assertContains(someItem, search(item.someItem, Cope.equal(item.someItem, someItem)));

		item.passivateCopeItem();
		assertEquals(someItem, item.getSomeItem());
		item.setSomeItem(null);
		assertEquals(null, item.getSomeItem());
		
		try
		{
			item.setAttribute(item.someItem, new Integer(10));
			fail();
		}
		catch(RuntimeException e)
		{
			assertEquals("expected Item, got java.lang.Integer for someItem", e.getMessage());
		}
	}

	public void testSomeNotNullItem()
		throws NotNullViolationException
	{
		assertEquals(item.TYPE, item.someNotNullItem.getType());
		assertEquals(
			EmptyItem.TYPE,
			item.someNotNullItem.getTargetType());
		assertEquals(someItem, item.getSomeNotNullItem());

		item.setSomeNotNullItem(someItem2);
		assertEquals(someItem2, item.getSomeNotNullItem());

		item.passivateCopeItem();
		assertEquals(someItem2, item.getSomeNotNullItem());
		try
		{
			item.setSomeNotNullItem(null);
			fail("should have thrown NotNullViolationException");
		}
		catch (NotNullViolationException e)
		{
			assertEquals(item, e.getItem());
			assertEquals(item.someNotNullItem, e.getNotNullAttribute());
		}
		assertEquals(someItem2, item.getSomeNotNullItem());
		try
		{
			someItem2.deleteCopeItem();
			fail("should have thrown IntegrityViolationException");
		}
		catch(IntegrityViolationException e)
		{
			assertTrue(!mysql);
			assertEquals(item.someNotNullItem, e.getAttribute());
			assertEquals(null/*TODO someItem*/, e.getItem());
		}
		catch(NestingRuntimeException e)
		{
			assertTrue(mysql);
			assertEquals("Cannot delete or update a parent row: a foreign key constraint fails", e.getNestedCause().getMessage());
		}

		try
		{
			new AttributeItem("someString", 5, 6l, 2.2, true, null, AttributeItem.SomeEnumeration.enumValue1);
			fail("should have thrown NotNullViolationException");
		}
		catch(NotNullViolationException e)
		{
			assertEquals(null, e.getItem());
			assertEquals(item.someNotNullItem, e.getNotNullAttribute());
		}
	}


}
