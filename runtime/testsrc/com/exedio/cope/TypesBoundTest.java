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
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.exedio.cope.instrument.CopeWarnings;
import com.exedio.cope.instrument.WrapperIgnore;
import com.exedio.cope.instrument.WrapperType;
import org.junit.jupiter.api.Test;

public class TypesBoundTest
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


		final Type<AnItem> type = newType(AnItem.class);

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

		assertEquals(null, type.getSupertype());
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

		assertSame(null, type.getFeature("xxx"));
		assertSame(null, type.getDeclaredFeature("xxx"));
		assertSame(null, type.getFeature(""));
		assertSame(null, type.getDeclaredFeature(""));
		assertSame(null, type.getFeature(null));
		assertSame(null, type.getDeclaredFeature(null));

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


		// error if not mounted
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


		final Model model = new Model(type);
		assertSame(type, model.getType(type.getID()));
		assertSame(null, model.getType("xxx"));
		assertSame(null, model.getType(""));
		assertSame(null, model.getType(null));
		assertSame(model, type.getModel());
		assertEqualsUnmodifiable(list(), type.getReferences());
		assertEqualsUnmodifiable(list(), type.getDeclaredReferences());
		assertEqualsUnmodifiable(list(), type.getSubtypes());
		assertEqualsUnmodifiable(list(type), type.getSubtypesTransitively());
		assertEqualsUnmodifiable(list(type), type.getTypesOfInstances());

		try
		{
			new Model(type);
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("type AnItem already mounted", e.getMessage());
		}
	}

	@WrapperType(indent=2, comments=false, type=NONE, constructor=NONE, genericConstructor=NONE)
	private static final class AnItem extends Item
	{
		@WrapperIgnore
		static final IntegerField intField = new IntegerField();
		@WrapperIgnore
		static final BooleanField boolField = new BooleanField();

		// test, that these fields do not become features of the type
		@SuppressWarnings({CopeWarnings.FEATURE_NOT_STATIC_FINAL, "unused"}) // OK: test bad API usage
		final BooleanField notStatic = new BooleanField();
		@SuppressWarnings({CopeWarnings.FEATURE_NOT_STATIC_FINAL, "unused"}) // OK: test bad API usage
		static BooleanField notFinal = new BooleanField();
		@SuppressWarnings("unused") // OK: test bad API usage
		static final Object noFeature = new BooleanField();

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private AnItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}
