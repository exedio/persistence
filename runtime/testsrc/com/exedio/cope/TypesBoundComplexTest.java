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

import static com.exedio.cope.TypesBound.forClass;
import static com.exedio.cope.TypesBound.forClassUnchecked;
import static com.exedio.cope.TypesBound.newType;
import static com.exedio.cope.instrument.Visibility.NONE;
import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static com.exedio.cope.tojunit.Assert.list;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.instrument.WrapperIgnore;
import com.exedio.cope.instrument.WrapperType;
import org.junit.jupiter.api.Test;

public class TypesBoundComplexTest
{
	@Test void testType()
	{
		try
		{
			forClass(AnItem.class);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("there is no type for class " + AnItem.class.getName(), e.getMessage());
		}
		try
		{
			forClassUnchecked(AnItem.class);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("there is no type for class " + AnItem.class.getName(), e.getMessage());
		}


		final Type<AnItem> type = newType(AnItem.class, AnItem::new);

		assertSame(type, forClass(AnItem.class));
		assertSame(type, forClassUnchecked(AnItem.class));
		try
		{
			forClass(Item.class);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("there is no type for class com.exedio.cope.Item", e.getMessage());
		}
		try
		{
			forClassUnchecked(Item.class);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("there is no type for class com.exedio.cope.Item", e.getMessage());
		}
		assertEquals("AnItem", type.getID());
		assertEquals(AnItem.class, type.getJavaClass());
		assertEquals(true, type.isBound());

		assertSame(type, type.getThis().getType());
		assertEquals("AnItem.this", type.getThis().getID());
		assertEquals("AnItem.this", type.getThis().toString());
		assertEquals("this", type.getThis().getName());

		assertEqualsUnmodifiable(list(AnItem.intField, AnItem.boolField), type.getFields());
		assertEqualsUnmodifiable(list(AnItem.intField, AnItem.boolField), type.getDeclaredFields());
		assertEqualsUnmodifiable(list(), type.getUniqueConstraints());
		assertEqualsUnmodifiable(list(), type.getDeclaredUniqueConstraints());
		assertEqualsUnmodifiable(list(type.getThis(), AnItem.intField, AnItem.boolField), type.getFeatures());
		assertEqualsUnmodifiable(list(type.getThis(), AnItem.intField, AnItem.boolField), type.getDeclaredFeatures());

		assertSame(AnItem.intField, type.getFeature("intField"));
		assertSame(AnItem.boolField, type.getFeature("boolField"));
		assertSame(AnItem.intField, type.getDeclaredFeature("intField"));
		assertSame(AnItem.boolField, type.getDeclaredFeature("boolField"));

		try
		{
			type.isAssignableFrom(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals(null, e.getMessage());
		}
		assertTrue(type.isAssignableFrom(type));


		final Type<AnotherItem> typO = newType(AnotherItem.class, AnotherItem::new);
		assertSame(typO, forClass(AnotherItem.class));
		assertSame(typO, forClassUnchecked(AnotherItem.class));
		assertEquals("AnotherItem", typO.getID());
		assertEquals(AnotherItem.class, typO.getJavaClass());
		assertEquals(true, typO.isBound());

		assertSame(typO, typO.getThis().getType());
		assertEquals("AnotherItem.this", typO.getThis().getID());
		assertEquals("AnotherItem.this", typO.getThis().toString());
		assertEquals("this", typO.getThis().getName());

		assertEqualsUnmodifiable(list(AnItem.intField, AnItem.boolField, AnotherItem.doubleField, AnotherItem.itemField), typO.getFields());
		assertEqualsUnmodifiable(list(AnotherItem.doubleField, AnotherItem.itemField), typO.getDeclaredFields());
		assertEqualsUnmodifiable(list(), typO.getUniqueConstraints());
		assertEqualsUnmodifiable(list(), typO.getDeclaredUniqueConstraints());
		assertEqualsUnmodifiable(list(typO.getThis(), AnItem.intField, AnItem.boolField, AnotherItem.doubleField, AnotherItem.itemField), typO.getFeatures());
		assertEqualsUnmodifiable(list(typO.getThis(), AnotherItem.doubleField, AnotherItem.itemField), typO.getDeclaredFeatures());

		assertSame(AnItem.intField, typO.getFeature("intField"));
		assertSame(AnItem.boolField, typO.getFeature("boolField"));
		assertSame(AnotherItem.doubleField, typO.getFeature("doubleField"));
		assertSame(null, typO.getDeclaredFeature("intField"));
		assertSame(null, typO.getDeclaredFeature("boolField"));
		assertSame(AnotherItem.doubleField, typO.getDeclaredFeature("doubleField"));
		assertSame(AnotherItem.itemField, typO.getDeclaredFeature("itemField"));

		try
		{
			typO.isAssignableFrom(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals(null, e.getMessage());
		}
		assertTrue(typO.isAssignableFrom(typO));
		assertTrue(type.isAssignableFrom(typO));
		assertFalse(typO.isAssignableFrom(type));


		// error if not initialized
		final String modelMessage =
			"type AnItem (" + AnItem.class.getName() +
			") does not belong to any model";
		try
		{
			type.getModel();
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals(modelMessage, e.getMessage());
		}
		try
		{
			type.getReferences();
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals(modelMessage, e.getMessage());
		}
		try
		{
			type.getDeclaredReferences();
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals(modelMessage, e.getMessage());
		}
		try
		{
			type.getSubtypes();
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals(modelMessage, e.getMessage());
		}
		try
		{
			type.getSubtypesTransitively();
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals(modelMessage, e.getMessage());
		}
		try
		{
			type.getTypesOfInstances();
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals(modelMessage, e.getMessage());
		}
		// AnotherItem
		try
		{
			AnotherItem.itemField.getValueType();
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals(
					"item field AnotherItem.itemField (" + AnItem.class.getName() + ") does not belong to any model",
					e.getMessage());
		}
		final String modelMessageO =
			"type AnotherItem (" + AnotherItem.class.getName() +
			") does not belong to any model";
		try
		{
			typO.getModel();
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals(modelMessageO, e.getMessage());
		}
		try
		{
			typO.getReferences();
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals(modelMessageO, e.getMessage());
		}
		try
		{
			typO.getDeclaredReferences();
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals(modelMessageO, e.getMessage());
		}
		try
		{
			typO.getSubtypes();
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals(modelMessageO, e.getMessage());
		}
		try
		{
			typO.getSubtypesTransitively();
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals(modelMessageO, e.getMessage());
		}
		try
		{
			typO.getTypesOfInstances();
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals(modelMessageO, e.getMessage());
		}


		final Model model = new Model(type, typO);
		assertSame(type, AnotherItem.itemField.getValueType());
		assertSame(type, model.getType(type.getID()));
		assertSame(typO, model.getType(typO.getID()));
		assertSame(model, type.getModel());
		assertSame(model, typO.getModel());
		assertEqualsUnmodifiable(list(AnotherItem.itemField), type.getReferences());
		assertEqualsUnmodifiable(list(AnotherItem.itemField), typO.getReferences());
		assertEqualsUnmodifiable(list(AnotherItem.itemField), type.getDeclaredReferences());
		assertEqualsUnmodifiable(list(), typO.getDeclaredReferences());
		assertEqualsUnmodifiable(list(typO), type.getSubtypes());
		assertEqualsUnmodifiable(list(), typO.getSubtypes());
		assertEqualsUnmodifiable(list(type, typO), type.getSubtypesTransitively());
		assertEqualsUnmodifiable(list(typO), typO.getSubtypesTransitively());
		assertEqualsUnmodifiable(list(type, typO), type.getTypesOfInstances());
		assertEqualsUnmodifiable(list(typO), typO.getTypesOfInstances());
	}

	@WrapperType(type=NONE, constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static class AnItem extends Item
	{
		@WrapperIgnore static final IntegerField intField = new IntegerField();
		@WrapperIgnore static final BooleanField boolField = new BooleanField();

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		protected AnItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(type=NONE, constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static final class AnotherItem extends AnItem
	{
		@WrapperIgnore static final DoubleField doubleField = new DoubleField();
		@WrapperIgnore static final ItemField<AnItem> itemField = ItemField.create(AnItem.class);

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private AnotherItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}
