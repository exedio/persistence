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

package com.exedio.cope;

import com.exedio.cope.junit.CopeAssert;

public class TypeTest extends CopeAssert
{
	public void testType()
	{
		try
		{
			TypesBound.forClass(AnItem.class);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("there is no type for class com.exedio.cope.TypeTest$AnItem", e.getMessage());
		}
		try
		{
			TypesBound.forClassUnchecked(AnItem.class);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("there is no type for class com.exedio.cope.TypeTest$AnItem", e.getMessage());
		}
		
		
		final Type<AnItem> type = Item.newType(AnItem.class);
		
		assertSame(type, TypesBound.forClass(AnItem.class));
		assertSame(type, TypesBound.forClassUnchecked(AnItem.class));
		try
		{
			TypesBound.forClass(Item.class);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("there is no type for class com.exedio.cope.Item", e.getMessage());
		}
		try
		{
			TypesBound.forClassUnchecked(Item.class);
			fail();
		}
		catch(IllegalArgumentException e)
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
		catch(NullPointerException e)
		{
			assertEquals(null, e.getMessage());
		}
		assertTrue(type.isAssignableFrom(type));
		
		
		// error if not mounted
		final String modelMessage =
			"model not set for type AnItem, " +
			"probably you forgot to put this type into the model.";
		try
		{
			type.getModel();
			fail();
		}
		catch(IllegalStateException e)
		{
			assertEquals(modelMessage, e.getMessage());
		}
		try
		{
			type.getReferences();
			fail();
		}
		catch(IllegalStateException e)
		{
			assertEquals(modelMessage, e.getMessage());
		}
		try
		{
			type.getDeclaredReferences();
			fail();
		}
		catch(IllegalStateException e)
		{
			assertEquals(modelMessage, e.getMessage());
		}
		try
		{
			type.getSubtypes();
			fail();
		}
		catch(IllegalStateException e)
		{
			assertEquals(modelMessage, e.getMessage());
		}
		try
		{
			type.getSubtypesTransitively();
			fail();
		}
		catch(IllegalStateException e)
		{
			assertEquals(modelMessage, e.getMessage());
		}
		try
		{
			type.getTypesOfInstances();
			fail();
		}
		catch(IllegalStateException e)
		{
			assertEquals(modelMessage, e.getMessage());
		}
		
		
		final Model model = new Model(type);
		assertSame(type, model.getType(type.getID()));
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
		catch(IllegalStateException e)
		{
			assertEquals("type already mounted", e.getMessage());
		}
	}
	
	static class AnItem extends Item
	{
		private static final long serialVersionUID = 1l;
		
		private AnItem(final ActivationParameters ap)
		{
			super(ap);
		}
		
		static final IntegerField intField = new IntegerField();
		static final BooleanField boolField = new BooleanField();
		
		// test, that these fields do not become features of the type
		final BooleanField notStatic = new BooleanField();
		static BooleanField notFinal = new BooleanField();
		static final Object noFeature = new BooleanField();
	}
}
