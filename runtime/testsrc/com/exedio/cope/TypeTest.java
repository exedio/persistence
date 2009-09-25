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

import java.util.Arrays;

import com.exedio.cope.junit.CopeAssert;
import com.exedio.cope.util.ReactivationConstructorDummy;

public class TypeTest extends CopeAssert
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
			assertEquals("there is no type for class com.exedio.cope.TypeTest$AnItem", e.getMessage());
		}
		try
		{
			Type.forClassUnchecked(AnItem.class);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("there is no type for class com.exedio.cope.TypeTest$AnItem", e.getMessage());
		}
		
		
		final Type<AnItem> type = Item.newType(AnItem.class);
		
		assertEquals(AnItem.class, type.getJavaClass());
		assertEquals(true, type.isJavaClassExclusive());
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

		assertSame(type, type.getThis().getType());
		assertEquals("AnItem.this", type.getThis().getID());
		assertEquals("AnItem.this", type.getThis().toString());
		assertEquals("this", type.getThis().getName());
		
		final Field[] attributes = {
			AnItem.intField,
			AnItem.boolField,
		};
		assertEqualsUnmodifiable(Arrays.asList(attributes), type.getFields());
		assertEqualsUnmodifiable(Arrays.asList(attributes), type.getDeclaredFields());
		assertEqualsUnmodifiable(list(), type.getUniqueConstraints());
		assertEqualsUnmodifiable(list(), type.getDeclaredUniqueConstraints());

		final Feature[] features = {
			type.getThis(),
			AnItem.intField,
			AnItem.boolField,
		};
		assertEqualsUnmodifiable(Arrays.asList(features), type.getFeatures());
		assertEqualsUnmodifiable(Arrays.asList(features), type.getDeclaredFeatures());
		
		assertSame(AnItem.intField, type.getDeclaredFeature("intField"));
		assertSame(AnItem.boolField, type.getDeclaredFeature("boolField"));
		
		
		final Model model = new Model(type);
		assertSame(type, model.getType(type.getID()));
	}
	
	static class AnItem extends Item
	{
		private static final long serialVersionUID = 1l;

		private AnItem(final SetValue[] setValues)
		{
			super(setValues);
		}
		
		private AnItem(final ReactivationConstructorDummy reactivationDummy, final int pk)
		{
			super(reactivationDummy, pk);
		}
		
		static final IntegerField intField = new IntegerField();
		static final BooleanField boolField = new BooleanField();
	}
	
	
	public void testErrors()
	{
		try
		{
			Types.newType(null);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals("javaClass", e.getMessage());
		}
		try
		{
			Types.newType(Item.class);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("Cannot make a type for " + Item.class + " itself, but only for subclasses.", e.getMessage());
		}
		try
		{
			Types.newType(castItemClass(NoItem.class));
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals(NoItem.class.toString() + " is not a subclass of Item", e.getMessage());
		}
		try
		{
			Types.newType(NoCreationConstructor.class);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals(
					NoCreationConstructor.class.getName() +
					" does not have a creation constructor NoCreationConstructor(" + SetValue.class.getName() + "[])", e.getMessage());
			assertEquals(NoSuchMethodException.class, e.getCause().getClass());
		}
		try
		{
			Types.newType(NoReactivationConstructor.class);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals(e.getMessage(),
					NoReactivationConstructor.class.getName() +
					" does not have a reactivation constructor NoReactivationConstructor(" + ReactivationConstructorDummy.class.getName() + ",int)", e.getMessage());
			assertEquals(NoSuchMethodException.class, e.getCause().getClass());
		}
	}
	
	static class NoItem
	{
		NoItem()
		{
			// just a dummy constructor
		}
	}
	
	static class NoCreationConstructor extends Item
	{
		private static final long serialVersionUID = 1l;

		NoCreationConstructor()
		{
			super(new SetValue[]{});
		}
	}

	static class NoReactivationConstructor extends Item
	{
		private static final long serialVersionUID = 1l;

		NoReactivationConstructor(final SetValue[] setValues)
		{
			super(setValues);
		}
	}
	
	@SuppressWarnings("unchecked") // OK: test bad API usage
	private static final Class<Item> castItemClass(Class c)
	{
		return c;
	}
}
