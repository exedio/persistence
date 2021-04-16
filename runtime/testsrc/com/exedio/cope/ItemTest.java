/*
 * Copyright (C) 2004-2015  exedio GmbH (www.exedio.com)
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

import static com.exedio.cope.AbstractRuntimeTest.assertDelete;
import static com.exedio.cope.CompareAssert.assertCompare;
import static com.exedio.cope.SequenceInfoAssert.assertInfo;
import static com.exedio.cope.testmodel.EmptyItem.TYPE;
import static com.exedio.cope.tojunit.Assert.assertContains;
import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static com.exedio.cope.tojunit.EqualsAssert.assertNotEqualsAndHash;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.testmodel.AttributeItem;
import com.exedio.cope.testmodel.CollisionItem1;
import com.exedio.cope.testmodel.CollisionItem2;
import com.exedio.cope.testmodel.EmptyItem;
import com.exedio.cope.testmodel.EmptyItem2;
import com.exedio.cope.testmodel.FinalItem;
import com.exedio.cope.testmodel.PointerItem;
import com.exedio.cope.testmodel.PointerTargetItem;
import com.exedio.dsmf.Constraint;
import java.util.EnumSet;
import org.junit.jupiter.api.Test;

public class ItemTest extends TestmodelTest
{

	/**
	 * Test getType, ID, equals, hashCode etc.
	 */
	@Test void testItemMethods()
			throws IntegrityViolationException, NoSuchIDException
	{
		assertEquals(TYPE, TypesBound.forClass(EmptyItem.class));
		assertEquals(EmptyItem2.TYPE, TypesBound.forClass(EmptyItem2.class));
		final Type<?>[] modelTypes = new Type<?>[]{
				TYPE,
				EmptyItem2.TYPE,
				AttributeItem.TYPE,
				PointerTargetItem.TYPE,
				PointerItem.TYPE,
				FinalItem.TYPE,
				CollisionItem1.TYPE,
				CollisionItem2.TYPE};
		assertEqualsUnmodifiable(asList(modelTypes), model.getTypes());
		assertEqualsUnmodifiable(asList(modelTypes), model.getTypesSortedByHierarchy());

		assertInfo(TYPE, TYPE.getPrimaryKeyInfo());
		final EmptyItem item1 = new EmptyItem();
		assertID("EmptyItem-0", item1);
		final EmptyItem item2 = new EmptyItem();
		assertID("EmptyItem-1", item2);
		final EmptyItem2 item3 = new EmptyItem2();
		assertID("EmptyItem2-0", item3);
		assertInfo(TYPE, 2, 0, 1, TYPE.getPrimaryKeyInfo());

		assertEquals(TYPE, item1.getCopeType());
		assertEquals(TYPE, item2.getCopeType());
		assertEquals(EmptyItem2.TYPE, item3.getCopeType());

		assertSame(item1, TYPE.cast(item1));
		try
		{
			TYPE.cast(item3);
			fail();
		}
		catch(final ClassCastException e)
		{
			assertEquals("Cannot cast " + EmptyItem2.TYPE.getJavaClass().getName() + " to " + EmptyItem.class.getName(), e.getMessage());
		}

		assertSame(TYPE, TYPE.as(EmptyItem.class));
		assertSame(TYPE, TYPE.asExtends(EmptyItem.class));
		assertSame(TYPE, TYPE.asSuper(EmptyItem.class));
		try
		{
			TYPE.as(EmptyItem2.class);
			fail();
		}
		catch(final ClassCastException e)
		{
			assertEquals("expected " + EmptyItem2.class.getName() + ", but was " + TYPE.getJavaClass().getName(), e.getMessage());
		}
		try
		{
			TYPE.asExtends(EmptyItem2.class);
			fail();
		}
		catch(final ClassCastException e)
		{
			assertEquals("expected ? extends " + EmptyItem2.class.getName() + ", but was " + TYPE.getJavaClass().getName(), e.getMessage());
		}

		assertEquals("EmptyItem-0", item1.getCopeID());
		assertEquals("EmptyItem-1", item2.getCopeID());
		assertEquals("EmptyItem2-0", item3.getCopeID());

		assertEquals(item1, model.getItem("EmptyItem-0"));
		assertEquals(item2, model.getItem("EmptyItem-1"));
		assertEquals(item3, model.getItem("EmptyItem2-0"));

		assertNotEqualsAndHash(item1, item2, item3);
		assertCompare(asList(item1, item2, item3));

		assertSame(item1, item1.get(TYPE.getThis()));
		assertSame(item1, TYPE.getThis().get(item1));
		assertContains(item1, TYPE.search(TYPE.getThis().equal(item1)));
		assertContains(item2, TYPE.search(TYPE.getThis().notEqual(item1)));
		assertContains(item1, item2, TYPE.search(TYPE.getThis().in(asList(item1, item2))));

		final EmptyItem item4 = new EmptyItem();
		assertEquals("EmptyItem-2", item4.getCopeID());
		final EmptyItem item5 = new EmptyItem();
		assertEquals("EmptyItem-3", item5.getCopeID());
		assertNotEqualsAndHash(item1, item2, item3, item4, item5);
		final EmptyItem item6 = new EmptyItem();
		assertEquals("EmptyItem-4", item6.getCopeID());
		assertNotEqualsAndHash(item1, item2, item3, item4, item5, item6);

		assertDelete(item1);
		assertDelete(item2);
		assertDelete(item3);
		assertDelete(item4);
		assertDelete(item5);
		assertDelete(item6);
	}

	@Test void testCheckDatabase()
	{
		commit();

		model.dropSchemaConstraints(EnumSet.allOf(Constraint.Type.class));
		model.createSchemaConstraints(EnumSet.allOf(Constraint.Type.class));
		model.dropSchemaConstraints(EnumSet.of(Constraint.Type.PrimaryKey, Constraint.Type.ForeignKey));
		model.createSchemaConstraints(EnumSet.of(Constraint.Type.PrimaryKey, Constraint.Type.ForeignKey));
		model.dropSchemaConstraints(EnumSet.of(Constraint.Type.ForeignKey));
		model.createSchemaConstraints(EnumSet.of(Constraint.Type.ForeignKey));
		if(!mysql) // causes SQLException: Cannot drop index 'CollisItem2_collAttri_Unq': needed in a foreign key constraint
		{
			model.dropSchemaConstraints(EnumSet.of(Constraint.Type.Unique));
			model.createSchemaConstraints(EnumSet.of(Constraint.Type.Unique));
		}
		model.dropSchemaConstraints(EnumSet.of(Constraint.Type.Check));
		model.createSchemaConstraints(EnumSet.of(Constraint.Type.Check));
		assertNotNull(model.getItemCacheStatistics().getDetails());
		assertNotNull(model.getQueryCacheInfo());
		assertNotNull(model.getQueryCacheHistogram());
		assertNotNull(model.getConnectionPoolInfo());
		assertNotNull(model.getConnectionPoolInfo().getCounter());
		model.flushConnectionPool();

		startTransaction();
	}

	@Test void testItemCreation()
	{
		final EmptyItem item1 = TYPE.newItem();
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
		assertEquals(20.2, item2.getSomeNotNullDouble());
		assertEquals(false, item2.getSomeNotNullBoolean());
		assertEquals(item1, item2.getSomeNotNullItem());
		assertEquals(AttributeItem.SomeEnum.enumValue3, item2.getSomeNotNullEnum());
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
