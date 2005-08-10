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

import java.util.Arrays;

import com.exedio.cope.testmodel.AttributeItem;
import com.exedio.cope.testmodel.EmptyItem;
import com.exedio.cope.testmodel.EmptyItem2;

public class ItemTest extends TestmodelTest
{
	
	/**
	 * Test getType, ID, equals, hashCode etc.
	 */
	public void testItemMethods()
			throws IntegrityViolationException, NoSuchIDException
	{
		assertEquals(EmptyItem.TYPE, Type.findByJavaClass(EmptyItem.class));
		assertEquals(EmptyItem2.TYPE, Type.findByJavaClass(EmptyItem2.class));
		assertEquals(Arrays.asList(modelTypes), model.getTypes());

		final EmptyItem item1 = new EmptyItem();
		final EmptyItem item2 = new EmptyItem();
		final EmptyItem2 item3 = new EmptyItem2();

		assertEquals(EmptyItem.TYPE, item1.getCopeType());
		assertEquals(EmptyItem.TYPE, item2.getCopeType());
		assertEquals(EmptyItem2.TYPE, item3.getCopeType());

		assertID(0, item1);
		assertID(1, item2);
		assertID(0, item3);

		assertEquals(item1, model.findByID(item1.getCopeID()));
		assertEquals(item2, model.findByID(item2.getCopeID()));
		assertEquals(item3, model.findByID(item3.getCopeID()));

		assertEquals(item1, item1);
		assertEquals(item2, item2);
		assertEquals(item3, item3);

		assertFalse(item1.equals(null));
		assertFalse(item2.equals(null));
		assertFalse(item3.equals(null));

		assertNotEquals(item1, item2);
		assertNotEquals(item1, item3);
		assertNotEquals(item2, item3);

		assertFalse(item1.equals("hello"));
		assertFalse(item1.equals(new Integer(1)));
		assertFalse(item1.equals(Boolean.TRUE));

		final EmptyItem item4 = new EmptyItem();
		assertID(2, item4);
		item4.TYPE.getPrimaryKeyIterator().flushPK();
		final EmptyItem item5 = new EmptyItem();
		assertID(3, item5);
		assertNotEquals(item4, item5);
		item4.TYPE.getPrimaryKeyIterator().flushPK();
		final EmptyItem item6 = new EmptyItem();
		assertID(4, item6);
		assertNotEquals(item4, item5);
		assertNotEquals(item4, item6);
		assertNotEquals(item5, item6);
		
		assertDelete(item1);
		assertDelete(item2);
		assertDelete(item3);
		assertDelete(item4);
		assertDelete(item5);
		assertDelete(item6);
	}
	
	public void testCheckDatabase()
	{
		model.checkDatabase();
	}
	
	public void testItemCreation()
			throws IntegrityViolationException
	{
		final EmptyItem item1 = (EmptyItem)EmptyItem.TYPE.newItem(null);
		final AttributeItem item2 = (AttributeItem)AttributeItem.TYPE.newItem(new AttributeValue[]{
			Cope.attributeValue(AttributeItem.someNotNullString, "someGenericString"),
			Cope.attributeValue(AttributeItem.someNotNullInteger, 50),
			Cope.attributeValue(AttributeItem.someNotNullLong, 60l),
			Cope.attributeValue(AttributeItem.someNotNullDouble, 20.2),
			Cope.attributeValue(AttributeItem.someNotNullBoolean, false),
			Cope.attributeValue(AttributeItem.someNotNullItem, item1), 
			Cope.attributeValue(AttributeItem.someNotNullEnum, AttributeItem.SomeEnum.enumValue3)});
		
		assertEquals("someGenericString", item2.getSomeNotNullString());
		assertEquals(50, item2.getSomeNotNullInteger());
		assertEquals(60l, item2.getSomeNotNullLong());
		assertEquals(20.2, item2.getSomeNotNullDouble(), 0.0);
		assertEquals(false, item2.getSomeNotNullBoolean());
		assertEquals(item1, item2.getSomeNotNullItem());
		assertEquals(AttributeItem.SomeEnum.enumValue3, item2.getSomeNotNullEnum());

		assertDelete(item2);
		assertDelete(item1);
	}

}
