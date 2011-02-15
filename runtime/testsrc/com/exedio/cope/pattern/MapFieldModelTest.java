/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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

package com.exedio.cope.pattern;

import java.io.Serializable;

import com.exedio.cope.EnumField;
import com.exedio.cope.Item;
import com.exedio.cope.ItemField;
import com.exedio.cope.Model;
import com.exedio.cope.StringField;
import com.exedio.cope.ItemField.DeletePolicy;
import com.exedio.cope.junit.CopeAssert;
import com.exedio.cope.misc.Computed;

public class MapFieldModelTest extends CopeAssert
{
	static final Model MODEL = new Model(MapFieldItem.TYPE);
	private static final Model model = MODEL; // TODO remove

	static
	{
		MODEL.enableSerialization(MapFieldModelTest.class, "MODEL");
	}

	private static final MapFieldItem item = null; // TODO remove

	public void testIt()
	{
		assertEquals(item.TYPE, item.name.getType());
		assertEquals("name", item.name.getName());
		assertEquals(MapFieldItem.class, item.TYPE.getJavaClass());
		assertEquals(true, item.TYPE.isBound());
		assertEquals(null, item.TYPE.getPattern());

		assertEquals(item.TYPE, item.nameParent().getValueType());
		assertEquals("parent", item.nameParent().getName());
		assertEquals(DeletePolicy.CASCADE, item.nameParent().getDeletePolicy());
		assertSame(item.name.getRelationType(), item.nameParent().getType());
		assertEquals(null, item.nameParent().getPattern());
		assertSame(item.nameParent(), item.name.getParent());

		assertEquals(MapFieldItem.Language.class, ((EnumField<MapFieldItem.Language>)item.name.getKey()).getValueClass());
		assertEquals("key", item.name.getKey().getName());
		assertSame(item.name.getRelationType(), item.name.getKey().getType());
		assertEquals(null, item.name.getKey().getPattern());

		assertEqualsUnmodifiable(list(item.nameParent(), item.name.getKey()), item.name.getUniqueConstraint().getFields());
		assertEquals("uniqueConstraint", item.name.getUniqueConstraint().getName());
		assertSame(item.name.getRelationType(), item.name.getUniqueConstraint().getType());
		assertEquals(list(item.nameParent(), item.name.getKey()), item.name.getUniqueConstraint().getFields());

		assertEquals(String.class, item.name.getValue().getValueClass());
		assertEquals("value", item.name.getValue().getName());
		assertSame(item.name.getRelationType(), item.name.getValue().getType());
		assertEquals(null, item.name.getValue().getPattern());

		assertEquals("MapFieldItem-name", item.name.getRelationType().getID());
		assertEquals(PatternItem.class, item.name.getRelationType().getJavaClass());
		assertEquals(false, item.name.getRelationType().isBound());
		assertSame(item.name, item.name.getRelationType().getPattern());
		assertEquals(null, item.name.getRelationType().getSupertype());
		assertEquals(list(), item.name.getRelationType().getSubtypes());
		assertEqualsUnmodifiable(
				list(
						item.name.getRelationType().getThis(),
						item.nameParent(), item.name.getKey(), item.name.getUniqueConstraint(),
						item.name.getValue()),
				item.name.getRelationType().getFeatures());
		assertEquals(model, item.name.getRelationType().getModel());

		assertEqualsUnmodifiable(list(item.TYPE.getThis(), item.name, item.nameLength, item.string, item.integer), item.TYPE.getFeatures());
		assertEqualsUnmodifiable(list(item.TYPE, item.name.getRelationType(), item.nameLength.getRelationType(), item.string.getRelationType(), item.integer.getRelationType()), model.getTypes());
		assertEqualsUnmodifiable(list(item.TYPE, item.name.getRelationType(), item.nameLength.getRelationType(), item.string.getRelationType(), item.integer.getRelationType()), model.getTypesSortedByHierarchy());

		assertEquals("MapFieldItem-name", item.name.getRelationType().getID());
		assertEquals("MapFieldItem-name.parent", item.name.getParent(MapFieldItem.class).getID());
		assertEquals("MapFieldItem-name.key", item.name.getKey().getID());
		assertEquals("MapFieldItem-name.value", item.name.getValue().getID());
		assertSame(item.name.getRelationType(), model.getType("MapFieldItem-name"));
		assertSame(item.name.getParent(MapFieldItem.class), model.getFeature("MapFieldItem-name.parent"));
		assertSame(item.name.getKey(), model.getFeature("MapFieldItem-name.key"));
		assertSame(item.name.getValue(), model.getFeature("MapFieldItem-name.value"));

		assertTrue(item.name      .getRelationType().isAnnotationPresent(Computed.class));
		assertTrue(item.nameLength.getRelationType().isAnnotationPresent(Computed.class));
		assertTrue(item.string    .getRelationType().isAnnotationPresent(Computed.class));
		assertTrue(item.integer   .getRelationType().isAnnotationPresent(Computed.class));

		assertSerializedSame(item.name      , 383);
		assertSerializedSame(item.nameLength, 389);
		assertSerializedSame(item.string    , 385);
		assertSerializedSame(item.integer   , 386);

		try
		{
			MapField.newMap(null, null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("key", e.getMessage());
		}
		try
		{
			MapField.newMap(new StringField().unique(), null);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("key must not be unique", e.getMessage());
		}
		try
		{
			MapField.newMap(new StringField(), null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("value", e.getMessage());
		}
		try
		{
			MapField.newMap(new StringField(), new StringField().unique());
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("value must not be unique", e.getMessage());
		}
		MapField.newMap(new StringField(), new StringField());

		try
		{
			item.name.getParent(Item.class);
			fail();
		}
		catch(final ClassCastException e)
		{
			assertEquals("expected a " + ItemField.class.getName() + "<" + Item.class.getName() + ">, but was a " + ItemField.class.getName() + "<" + MapFieldItem.class.getName() + ">", e.getMessage());
		}
	}

	private static final void assertSerializedSame(final Serializable value, final int expectedSize)
	{
		assertSame(value, reserialize(value, expectedSize));
	}
}
