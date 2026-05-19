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

import static com.exedio.cope.tojunit.Assert.assertFails;

import com.exedio.cope.IntegerField;
import com.exedio.cope.Model;
import com.exedio.cope.StringField;
import org.junit.jupiter.api.Test;

public class ListFieldModelTest
{
	public static final Model MODEL = new Model(ListFieldItem.TYPE);

	static
	{
		MODEL.enableSerialization(ListFieldModelTest.class, "MODEL");
	}

	@Test void testElementNull()
	{
		assertFails(
				() -> ListField.create(null),
				NullPointerException.class,
				"element");
	}
	@Test void testElementFinal()
	{
		final StringField element = new StringField().toFinal();
		assertFails(
				() -> ListField.create(element),
				IllegalArgumentException.class,
				"element must not be final");
	}
	@Test void testElementDefault()
	{
		final StringField element = new StringField().defaultTo("someDefault");
		assertFails(
				() -> ListField.create(element),
				IllegalArgumentException.class,
				"element must not have any default");
	}
	@Test void testElementUnique()
	{
		final StringField element = new StringField().unique();
		assertFails(
				() -> ListField.create(element),
				IllegalArgumentException.class,
				"element must not be unique");
	}
	@Test void customEntryFeatureNameCollision()
	{
		final ListField<?> listField = ListField.create(new IntegerField()).
				customEntryFeature("collide", new IntegerField());
		assertFails(
				() -> listField.customEntryFeature("collide", new StringField()),
				IllegalArgumentException.class,
				"there is already a custom entry feature named 'collide'"
		);
	}
	@Test void customEntryFeatureNameReserved()
	{
		final ListField<?> listField = ListField.create(new IntegerField());
		assertFails(
				() -> listField.customEntryFeature("parent", new StringField()),
				IllegalArgumentException.class,
				"invalid name 'parent'"
		);
		assertFails(
				() -> listField.customEntryFeature("order", new StringField()),
				IllegalArgumentException.class,
				"invalid name 'order'"
		);
		assertFails(
				() -> listField.customEntryFeature("uniqueConstraint", new StringField()),
				IllegalArgumentException.class,
				"invalid name 'uniqueConstraint'"
		);
		assertFails(
				() -> listField.customEntryFeature("entries", new StringField()),
				IllegalArgumentException.class,
				"invalid name 'entries'"
		);
		assertFails(
				() -> listField.customEntryFeature("element", new StringField()),
				IllegalArgumentException.class,
				"invalid name 'element'"
		);
	}
	@Test void customEntryFeatureNull()
	{
		final ListField<?> listField = ListField.create(new IntegerField());
		assertFails(
				() -> listField.customEntryFeature("custom", null),
				NullPointerException.class,
				"feature"
		);
	}
	@Test void customEntryFeatureNameNull()
	{
		final ListField<?> listField = ListField.create(new IntegerField());
		assertFails(
				() -> listField.customEntryFeature(null, new IntegerField()),
				NullPointerException.class,
				"name"
		);
	}
}
