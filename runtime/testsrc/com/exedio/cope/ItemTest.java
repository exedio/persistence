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

import java.util.Arrays;

import com.exedio.cope.testmodel.AttributeItem;
import com.exedio.cope.testmodel.EmptyItem;
import com.exedio.cope.testmodel.EmptyItem2;
import com.exedio.dsmf.Constraint;

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
		assertEqualsUnmodifiable(Arrays.asList(modelTypes), model.getTypes());
		assertEqualsUnmodifiable(Arrays.asList(modelTypes), model.getTypesSortedByHierarchy());
		
		assertNotNull(EmptyItem.TYPE.getPrimaryKeyInfo());
		final EmptyItem item1 = new EmptyItem();
		final EmptyItem item2 = new EmptyItem();
		final EmptyItem2 item3 = new EmptyItem2();
		assertNotNull(EmptyItem.TYPE.getPrimaryKeyInfo());

		assertEquals(EmptyItem.TYPE, item1.getCopeType());
		assertEquals(EmptyItem.TYPE, item2.getCopeType());
		assertEquals(EmptyItem2.TYPE, item3.getCopeType());
		
		assertSame(item1, item1.TYPE.cast(item1));
		try
		{
			item1.TYPE.cast(item3);
			fail();
		}
		catch(ClassCastException e)
		{
			assertEquals("expected a " + EmptyItem.class.getName() + ", but was a " + item3.TYPE.getJavaClass().getName(), e.getMessage());
		}

		assertSame(item1.TYPE, item1.TYPE.castType(EmptyItem.class));
		try
		{
			item1.TYPE.castType(EmptyItem2.class);
			fail();
		}
		catch(ClassCastException e)
		{
			assertEquals("expected " + EmptyItem2.class.getName() + ", but was " + item1.TYPE.getJavaClass().getName(), e.getMessage());
		}

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
		assertFalse(item1.equals(Integer.valueOf(1)));
		assertFalse(item1.equals(Boolean.TRUE));
		
		assertSame(item1, item1.get(item1.TYPE.getThis()));
		assertSame(item1, item1.TYPE.getThis().get(item1));
		assertContains(item1, item1.TYPE.search(item1.TYPE.getThis().equal(item1)));
		assertContains(item2, item1.TYPE.search(item1.TYPE.getThis().notEqual(item1)));
		assertContains(item1, item2, item1.TYPE.search(item1.TYPE.getThis().in(listg(item1, item2))));

		final EmptyItem item4 = new EmptyItem();
		assertID(2, item4);
		item4.TYPE.getPkSource().flushPK();
		final EmptyItem item5 = new EmptyItem();
		assertID(3, item5);
		assertNotEquals(item4, item5);
		item4.TYPE.getPkSource().flushPK();
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
		if(!postgresql)
		{
			model.dropDatabaseConstraints(Constraint.MASK_ALL);
			model.createDatabaseConstraints(Constraint.MASK_ALL);
			model.dropDatabaseConstraints(Constraint.MASK_PK|Constraint.MASK_FK);
			model.createDatabaseConstraints(Constraint.MASK_PK|Constraint.MASK_FK);
			model.dropDatabaseConstraints(Constraint.MASK_FK);
			model.createDatabaseConstraints(Constraint.MASK_FK);
			if(!mysql) // causes: Error on rename of './yourdatabase/#sql-35fb_13a3b' to './yourdatabase/CollisionItem2' (errno: 150)
			{
				model.dropDatabaseConstraints(Constraint.MASK_UNIQUE);
				model.createDatabaseConstraints(Constraint.MASK_UNIQUE);
			}
			model.dropDatabaseConstraints(Constraint.MASK_CHECK);
			model.createDatabaseConstraints(Constraint.MASK_CHECK);
		}
		assertNotNull(model.getCacheInfo());
		assertNotNull(model.getCacheQueryInfo());
		assertNotNull(model.getCacheQueryHistogram());
		assertNotNull(model.getConnectionPoolInfo());
		assertNotNull(model.getConnectionPoolInfo().getCounter());
	}
	
	public void testItemCreation()
	{
		final EmptyItem item1 = EmptyItem.TYPE.newItem(null);
		final AttributeItem item2 = AttributeItem.TYPE.newItem(new SetValue[]{
			AttributeItem.someNotNullString.map("someGenericString"),
			AttributeItem.someNotNullInteger.map(50),
			AttributeItem.someNotNullLong.map(60l),
			AttributeItem.someNotNullDouble.map(20.2),
			AttributeItem.someNotNullBoolean.map(false),
			AttributeItem.someNotNullItem.map(item1),
			AttributeItem.someNotNullEnum.map(AttributeItem.SomeEnum.enumValue3)});
		
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
	
	public void testItemPostCreate()
	{
		final EmptyItem item = EmptyItem.TYPE.newItem(null);
		assertEquals(5, item.getPostCreateValue());
		
		assertDelete(item);
	}

}
