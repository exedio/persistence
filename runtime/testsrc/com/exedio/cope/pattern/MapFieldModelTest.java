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

package com.exedio.cope.pattern;

import static com.exedio.cope.pattern.MapFieldItem.TYPE;
import static com.exedio.cope.pattern.MapFieldItem.integer;
import static com.exedio.cope.pattern.MapFieldItem.name;
import static com.exedio.cope.pattern.MapFieldItem.nameLength;
import static com.exedio.cope.pattern.MapFieldItem.nameParent;
import static com.exedio.cope.pattern.MapFieldItem.string;
import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static com.exedio.cope.tojunit.Assert.list;
import static com.exedio.cope.tojunit.Assert.reserialize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.exedio.cope.Item;
import com.exedio.cope.ItemField.DeletePolicy;
import com.exedio.cope.Model;
import com.exedio.cope.StringField;
import com.exedio.cope.misc.Computed;
import com.exedio.cope.pattern.MapFieldItem.Language;
import java.io.Serializable;
import org.junit.jupiter.api.Test;

public class MapFieldModelTest
{
	static final Model MODEL = new Model(TYPE);

	static
	{
		MODEL.enableSerialization(MapFieldModelTest.class, "MODEL");
	}

	@Test void testIt()
	{
		assertEquals(TYPE, name.getType());
		assertEquals("name", name.getName());
		assertEquals(MapFieldItem.class, TYPE.getJavaClass());
		assertEquals(true, TYPE.isBound());
		assertEquals(null, TYPE.getPattern());

		assertEquals(TYPE, nameParent().getValueType());
		assertEquals("parent", nameParent().getName());
		assertEquals(DeletePolicy.CASCADE, nameParent().getDeletePolicy());
		assertSame(name.getRelationType(), nameParent().getType());
		assertEquals(null, nameParent().getPattern());
		assertSame(nameParent(), name.getParent());

		assertEquals(Language.class, name.getKey().getValueClass());
		assertEquals("key", name.getKey().getName());
		assertSame(name.getRelationType(), name.getKey().getType());
		assertEquals(null, name.getKey().getPattern());

		assertEqualsUnmodifiable(list(nameParent(), name.getKey()), name.getUniqueConstraint().getFields());
		assertEquals("uniqueConstraint", name.getUniqueConstraint().getName());
		assertSame(name.getRelationType(), name.getUniqueConstraint().getType());
		assertEquals(list(nameParent(), name.getKey()), name.getUniqueConstraint().getFields());

		assertEquals(String.class, name.getValue().getValueClass());
		assertEquals("value", name.getValue().getName());
		assertSame(name.getRelationType(), name.getValue().getType());
		assertEquals(null, name.getValue().getPattern());

		assertEquals("MapFieldItem-name", name.getRelationType().getID());
		assertEquals(PatternItem.class, name.getRelationType().getJavaClass());
		assertEquals(false, name.getRelationType().isBound());
		assertSame(name, name.getRelationType().getPattern());
		assertEquals(null, name.getRelationType().getSupertype());
		assertEquals(list(), name.getRelationType().getSubtypes());
		assertEqualsUnmodifiable(
				list(
						name.getRelationType().getThis(),
						nameParent(), name.getKey(), name.getUniqueConstraint(),
						name.getValue()),
				name.getRelationType().getFeatures());
		assertEquals(MODEL, name.getRelationType().getModel());

		assertEqualsUnmodifiable(list(TYPE.getThis(), name, nameLength, string, integer), TYPE.getFeatures());
		assertEqualsUnmodifiable(list(TYPE, name.getRelationType(), nameLength.getRelationType(), string.getRelationType(), integer.getRelationType()), MODEL.getTypes());
		assertEqualsUnmodifiable(list(TYPE, name.getRelationType(), nameLength.getRelationType(), string.getRelationType(), integer.getRelationType()), MODEL.getTypesSortedByHierarchy());

		assertEquals("MapFieldItem-name", name.getRelationType().getID());
		assertEquals("MapFieldItem-name.parent", name.getParent(MapFieldItem.class).getID());
		assertEquals("MapFieldItem-name.key", name.getKey().getID());
		assertEquals("MapFieldItem-name.value", name.getValue().getID());
		assertSame(name.getRelationType(), MODEL.getType("MapFieldItem-name"));
		assertSame(name.getParent(MapFieldItem.class), MODEL.getFeature("MapFieldItem-name.parent"));
		assertSame(name.getKey(), MODEL.getFeature("MapFieldItem-name.key"));
		assertSame(name.getValue(), MODEL.getFeature("MapFieldItem-name.value"));

		assertTrue(name      .getRelationType().isAnnotationPresent(Computed.class));
		assertTrue(nameLength.getRelationType().isAnnotationPresent(Computed.class));
		assertTrue(string    .getRelationType().isAnnotationPresent(Computed.class));
		assertTrue(integer   .getRelationType().isAnnotationPresent(Computed.class));

		assertEquals(Language.class, name.getKeyClass());
		assertEquals(String.class, name.getValueClass());
		assertEquals(Language.class, nameLength.getKeyClass());
		assertEquals(Integer.class, nameLength.getValueClass());
		assertEquals(String.class, string.getKeyClass());
		assertEquals(String.class, string.getValueClass());
		assertEquals(String.class, integer.getKeyClass());
		assertEquals(Integer.class, integer.getValueClass());

		assertSerializedSame(name      , 383);
		assertSerializedSame(nameLength, 389);
		assertSerializedSame(string    , 385);
		assertSerializedSame(integer   , 386);
	}

	@Test void testKeyNull()
	{
		assertFails(
				() -> MapField.create(null, null),
				NullPointerException.class,
				"key");
	}
	@Test void testKeyOptional()
	{
		final StringField key = new StringField().optional();
		assertFails(
				() -> MapField.create(key, null),
				IllegalArgumentException.class,
				"key must be mandatory");
	}
	@Test void testKeyDefault()
	{
		final StringField key = new StringField().defaultTo("someDefault");
		assertFails(
				() -> MapField.create(key, null),
				IllegalArgumentException.class,
				"key must not have any default");
	}
	@Test void testKeyUnique()
	{
		final StringField key = new StringField().unique();
		assertFails(
				() -> MapField.create(key, null),
				IllegalArgumentException.class,
				"key must not be unique");
	}
	@Test void testValueNull()
	{
		final StringField key = new StringField();
		assertFails(
				() -> MapField.create(key, null),
				NullPointerException.class,
				"value");
	}
	@Test void testValueOptional()
	{
		final StringField key = new StringField();
		final StringField value = new StringField().optional();
		assertFails(
				() -> MapField.create(key, value),
				IllegalArgumentException.class,
				"value must be mandatory");
	}
	@Test void testValueDefault()
	{
		final StringField key = new StringField();
		final StringField value = new StringField().defaultTo("someDefault");
		assertFails(
				() -> MapField.create(key, value),
				IllegalArgumentException.class,
				"value must not have any default");
	}
	@Test void testValueUnique()
	{
		final StringField key = new StringField();
		final StringField value = new StringField().unique();
		assertFails(
				() -> MapField.create(key, value),
				IllegalArgumentException.class,
				"value must not be unique");
	}
	@Test void testOk()
	{
		MapField.create(new StringField(), new StringField());
	}
	@Test void testGetParentWrongParentClass()
	{
		assertFails(
				() -> name.getParent(Item.class),
				ClassCastException.class,
				"parentClass requires " + MapFieldItem.class.getName() + ", " +
				"but was " + Item.class.getName());
	}

	private static void assertSerializedSame(final Serializable value, final int expectedSize)
	{
		assertSame(value, reserialize(value, expectedSize));
	}
}
