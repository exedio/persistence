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
import com.exedio.cope.util.ReactivationConstructorDummy;

public class TypeComplexTest extends CopeAssert
{
	public void testType()
	{
		try
		{
			Type.forClass(AnItem.class);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("there is no type for class com.exedio.cope.TypeComplexTest$AnItem", e.getMessage());
		}
		try
		{
			Type.forClassUnchecked(AnItem.class);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("there is no type for class com.exedio.cope.TypeComplexTest$AnItem", e.getMessage());
		}
		
		
		final Type<AnItem> type = Item.newType(AnItem.class);
		
		assertSame(type, Type.forClass(AnItem.class));
		assertSame(type, Type.forClassUnchecked(AnItem.class));
		try
		{
			Type.forClass(Item.class);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("there is no type for class com.exedio.cope.Item", e.getMessage());
		}
		try
		{
			Type.forClassUnchecked(Item.class);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("there is no type for class com.exedio.cope.Item", e.getMessage());
		}
		assertEquals("AnItem", type.getID());
		assertEquals(AnItem.class, type.getJavaClass());
		assertEquals(true, type.isJavaClassExclusive());

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
		
		
		final Type<AnotherItem> typO = Item.newType(AnotherItem.class);
		assertSame(typO, Type.forClass(AnotherItem.class));
		assertSame(typO, Type.forClassUnchecked(AnotherItem.class));
		assertEquals("AnotherItem", typO.getID());
		assertEquals(AnotherItem.class, typO.getJavaClass());
		assertEquals(true, typO.isJavaClassExclusive());
		
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
		
		// error if not initialized
		final String modelMessage =
			"model not set for type AnItem, " +
			"probably you forgot to put this type into the model.";
		try
		{
			type.getModel();
			fail();
		}
		catch(RuntimeException e)
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
			assertEquals(null, e.getMessage());
		}
		try
		{
			assertEquals(null, type.getDeclaredReferences());
			fail();
		}
		catch(IllegalStateException e)
		{
			assertEquals(null, e.getMessage());
		}
		assertEqualsUnmodifiable(list(typO), type.getSubTypes());
		try
		{
			type.getSubTypesTransitively();
			fail();
		}
		catch(RuntimeException e)
		{
			assertEquals(null, e.getMessage());
		}
		try
		{
			type.getTypesOfInstances();
			fail();
		}
		catch(RuntimeException e)
		{
			assertEquals(null, e.getMessage());
		}
		// AnotherItem
		final String modelMessageO =
			"model not set for type AnotherItem, " +
			"probably you forgot to put this type into the model.";
		try
		{
			typO.getModel();
			fail();
		}
		catch(RuntimeException e)
		{
			assertEquals(modelMessageO, e.getMessage());
		}
		try
		{
			typO.getReferences();
			fail();
		}
		catch(IllegalStateException e)
		{
			assertEquals(null, e.getMessage());
		}
		try
		{
			assertEquals(null, typO.getDeclaredReferences());
			fail();
		}
		catch(IllegalStateException e)
		{
			assertEquals(null, e.getMessage());
		}
		assertEqualsUnmodifiable(list(), typO.getSubTypes());
		try
		{
			typO.getSubTypesTransitively();
			fail();
		}
		catch(RuntimeException e)
		{
			assertEquals(null, e.getMessage());
		}
		try
		{
			typO.getTypesOfInstances();
			fail();
		}
		catch(RuntimeException e)
		{
			assertEquals(null, e.getMessage());
		}
		
		
		final Model model = new Model(type, typO);
		assertSame(type, model.getType(type.getID()));
		assertSame(typO, model.getType(typO.getID()));
		assertSame(model, type.getModel());
		assertSame(model, typO.getModel());
		assertEqualsUnmodifiable(list(AnotherItem.itemField), type.getReferences());
		assertEqualsUnmodifiable(list(AnotherItem.itemField), typO.getReferences());
		assertEqualsUnmodifiable(list(AnotherItem.itemField), type.getDeclaredReferences());
		assertEqualsUnmodifiable(list(), typO.getDeclaredReferences());
		assertEqualsUnmodifiable(list(typO), type.getSubTypes());
		assertEqualsUnmodifiable(list(), typO.getSubTypes());
		assertEqualsUnmodifiable(list(type, typO), type.getSubTypesTransitively());
		assertEqualsUnmodifiable(list(typO), typO.getSubTypesTransitively());
		assertEqualsUnmodifiable(list(type, typO), type.getTypesOfInstances());
		assertEqualsUnmodifiable(list(typO), typO.getTypesOfInstances());
	}
	
	static class AnItem extends Item
	{
		private static final long serialVersionUID = 1l;

		protected AnItem(final SetValue[] setValues)
		{
			super(setValues);
		}
		
		protected AnItem(final ReactivationConstructorDummy reactivationDummy, final int pk)
		{
			super(reactivationDummy, pk);
		}
		
		static final IntegerField intField = new IntegerField();
		static final BooleanField boolField = new BooleanField();
	}
	
	static class AnotherItem extends AnItem
	{
		private static final long serialVersionUID = 1l;

		private AnotherItem(final SetValue[] setValues)
		{
			super(setValues);
		}
		
		private AnotherItem(final ReactivationConstructorDummy reactivationDummy, final int pk)
		{
			super(reactivationDummy, pk);
		}
		
		static final DoubleField doubleField = new DoubleField();
		static final ItemField itemField = newItemField(AnItem.class);
	}
}
