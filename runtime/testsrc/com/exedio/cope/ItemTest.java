/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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
import java.util.EnumSet;

import com.exedio.cope.testmodel.AttributeItem;
import com.exedio.cope.testmodel.CollisionItem1;
import com.exedio.cope.testmodel.CollisionItem2;
import com.exedio.cope.testmodel.EmptyItem;
import com.exedio.cope.testmodel.EmptyItem2;
import com.exedio.cope.testmodel.FinalItem;
import com.exedio.cope.testmodel.PointerItem;
import com.exedio.cope.testmodel.PointerTargetItem;
import com.exedio.dsmf.Constraint;

public class ItemTest extends TestmodelTest
{

	/**
	 * Test getType, ID, equals, hashCode etc.
	 */
	public void testItemMethods()
			throws IntegrityViolationException, NoSuchIDException
	{
		assertEquals(EmptyItem.TYPE, TypesBound.forClass(EmptyItem.class));
		assertEquals(EmptyItem2.TYPE, TypesBound.forClass(EmptyItem2.class));
		final Type<?>[] modelTypes = new Type[]{
				EmptyItem.TYPE,
				EmptyItem2.TYPE,
				AttributeItem.TYPE,
				PointerTargetItem.TYPE,
				PointerItem.TYPE,
				FinalItem.TYPE,
				CollisionItem1.TYPE,
				CollisionItem2.TYPE};
		assertEqualsUnmodifiable(Arrays.asList(modelTypes), model.getTypes());
		assertEqualsUnmodifiable(Arrays.asList(modelTypes), model.getTypesSortedByHierarchy());

		assertInfo(EmptyItem.TYPE, EmptyItem.TYPE.getPrimaryKeyInfo());
		final EmptyItem item1 = new EmptyItem();
		assertID("EmptyItem-0", item1);
		final EmptyItem item2 = new EmptyItem();
		assertID("EmptyItem-1", item2);
		final EmptyItem2 item3 = new EmptyItem2();
		assertID("EmptyItem2-0", item3);
		assertInfo(EmptyItem.TYPE, 2, 0, 1, EmptyItem.TYPE.getPrimaryKeyInfo());

		assertEquals(EmptyItem.TYPE, item1.getCopeType());
		assertEquals(EmptyItem.TYPE, item2.getCopeType());
		assertEquals(EmptyItem2.TYPE, item3.getCopeType());

		assertSame(item1, item1.TYPE.cast(item1));
		try
		{
			item1.TYPE.cast(item3);
			fail();
		}
		catch(final ClassCastException e)
		{
			assertEquals("expected a " + EmptyItem.class.getName() + ", but was a " + item3.TYPE.getJavaClass().getName(), e.getMessage());
		}

		assertSame(item1.TYPE, item1.TYPE.as(EmptyItem.class));
		try
		{
			item1.TYPE.as(EmptyItem2.class);
			fail();
		}
		catch(final ClassCastException e)
		{
			assertEquals("expected " + EmptyItem2.class.getName() + ", but was " + item1.TYPE.getJavaClass().getName(), e.getMessage());
		}

		assertEquals("EmptyItem-0", item1.getCopeID());
		assertEquals("EmptyItem-1", item2.getCopeID());
		assertEquals("EmptyItem2-0", item3.getCopeID());

		assertEquals(item1, model.getItem("EmptyItem-0"));
		assertEquals(item2, model.getItem("EmptyItem-1"));
		assertEquals(item3, model.getItem("EmptyItem2-0"));

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

		assertEquals( 0, item1.compareTo(item1));
		assertEquals( 1, item2.compareTo(item1));
		assertEquals( 1, item3.compareTo(item1));
		assertEquals(-1, item1.compareTo(item2));
		assertEquals( 0, item2.compareTo(item2));
		assertEquals( 1, item3.compareTo(item2));
		assertEquals(-1, item1.compareTo(item3));
		assertEquals(-1, item2.compareTo(item3));
		assertEquals( 0, item3.compareTo(item3));

		assertSame(item1, item1.get(item1.TYPE.getThis()));
		assertSame(item1, item1.TYPE.getThis().get(item1));
		assertContains(item1, item1.TYPE.search(item1.TYPE.getThis().equal(item1)));
		assertContains(item2, item1.TYPE.search(item1.TYPE.getThis().notEqual(item1)));
		assertContains(item1, item2, item1.TYPE.search(item1.TYPE.getThis().in(listg(item1, item2))));

		final EmptyItem item4 = new EmptyItem();
		assertEquals("EmptyItem-2", item4.getCopeID());
		final EmptyItem item5 = new EmptyItem();
		assertEquals("EmptyItem-3", item5.getCopeID());
		assertNotEquals(item4, item5);
		final EmptyItem item6 = new EmptyItem();
		assertEquals("EmptyItem-4", item6.getCopeID());
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
		model.checkSchema();

		model.commit();

		model.dropSchemaConstraints(EnumSet.allOf(Constraint.Type.class));
		model.createSchemaConstraints(EnumSet.allOf(Constraint.Type.class));
		model.dropSchemaConstraints(EnumSet.of(Constraint.Type.PrimaryKey, Constraint.Type.ForeignKey));
		model.createSchemaConstraints(EnumSet.of(Constraint.Type.PrimaryKey, Constraint.Type.ForeignKey));
		model.dropSchemaConstraints(EnumSet.of(Constraint.Type.ForeignKey));
		model.createSchemaConstraints(EnumSet.of(Constraint.Type.ForeignKey));
		if(!mysql) // causes: Error on rename of './yourdatabase/#sql-35fb_13a3b' to './yourdatabase/CollisionItem2' (errno: 150)
		{
			model.dropSchemaConstraints(EnumSet.of(Constraint.Type.Unique));
			model.createSchemaConstraints(EnumSet.of(Constraint.Type.Unique));
		}
		model.dropSchemaConstraints(EnumSet.of(Constraint.Type.Check));
		model.createSchemaConstraints(EnumSet.of(Constraint.Type.Check));
		assertNotNull(model.getItemCacheInfo());
		assertNotNull(model.getQueryCacheInfo());
		assertNotNull(model.getQueryCacheHistogram());
		assertNotNull(model.getConnectionPoolInfo());
		assertNotNull(model.getConnectionPoolInfo().getCounter());

		model.startTransaction();
	}

	public void testItemCreation()
	{
		final EmptyItem item1 = EmptyItem.TYPE.newItem();
		final AttributeItem item2 = AttributeItem.TYPE.newItem(
			AttributeItem.someNotNullString.map("someGenericString"),
			AttributeItem.someNotNullInteger.map(50),
			AttributeItem.someNotNullLong.map(60l),
			AttributeItem.someNotNullDouble.map(20.2),
			AttributeItem.someNotNullBoolean.map(false),
			AttributeItem.someNotNullItem.map(item1),
			AttributeItem.someNotNullEnum.map(AttributeItem.SomeEnum.enumValue3));

		assertEquals("someGenericString", item2.getSomeNotNullString());
		assertEquals(50, item2.getSomeNotNullInteger());
		assertEquals(60l, item2.getSomeNotNullLong());
		assertEquals(20.2, item2.getSomeNotNullDouble(), 0.0);
		assertEquals(false, item2.getSomeNotNullBoolean());
		assertEquals(item1, item2.getSomeNotNullItem());
		assertEquals(AttributeItem.SomeEnum.enumValue3, item2.getSomeNotNullEnum());

		try
		{
			item2.set(
					AttributeItem.someNotNullString.map("someGenericString2"),
					AttributeItem.someNotNullString.map("someGenericString2"));
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("SetValues contain duplicate settable AttributeItem.someNotNullString", e.getMessage());
		}
		assertEquals("someGenericString", item2.getSomeNotNullString());

		try
		{
			AttributeItem.TYPE.newItem(
					AttributeItem.someNotNullString.map("someGenericString"),
					AttributeItem.someNotNullString.map("someGenericString"));
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("SetValues contain duplicate settable AttributeItem.someNotNullString", e.getMessage());
		}
		assertContains(item2, AttributeItem.TYPE.search());

		assertDelete(item2);
		assertDelete(item1);
	}

	private static void assertID(final String id, final Item item)
	{
		assertEquals(id, item.getCopeID());
		final StringBuilder bf = new StringBuilder();
		item.appendCopeID(bf);
		assertEquals(id, bf.toString());
	}
}
