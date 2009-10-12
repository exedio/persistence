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

public class TypeErrorTest extends CopeAssert
{
	public void testErrors()
	{
		try
		{
			TypesExclusive.newType(null);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals("javaClass", e.getMessage());
		}
		try
		{
			TypesExclusive.newType(Item.class);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("Cannot make a type for " + Item.class + " itself, but only for subclasses.", e.getMessage());
		}
		try
		{
			TypesExclusive.newType(castItemClass(NoItem.class));
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals(NoItem.class.toString() + " is not a subclass of Item", e.getMessage());
		}
		try
		{
			TypesExclusive.newType(NoActivationConstructor.class);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals(e.getMessage(),
					NoActivationConstructor.class.getName() +
					" does not have an activation constructor NoActivationConstructor(" + ActivationParameters.class.getName() + ")", e.getMessage());
			assertEquals(NoSuchMethodException.class, e.getCause().getClass());
		}
		final Type wrongActivationConstructor = TypesExclusive.newType(WrongActivationConstructor.class);
		try
		{
			new Model(wrongActivationConstructor);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("WrongActivationConstructor", e.getMessage());
		}
		try
		{
			TypesExclusive.newType(NullFeature.class);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals(NullFeature.class.getName() + "#nullFeature", e.getMessage());
		}
		
		try
		{
			NonResolvingItemField.itemField.getValueType();
			fail();
		}
		catch(IllegalStateException e)
		{
			assertEquals("valueType of " + NonResolvingItemField.itemField.toString() + " not yet resolved: " + NullFeature.class.getName(), e.getMessage());
		}
		final Type nonResolvingItemField = TypesExclusive.newType(NonResolvingItemField.class);
		try
		{
			NonResolvingItemField.itemField.getValueType();
			fail();
		}
		catch(IllegalStateException e)
		{
			assertEquals("valueType of NonResolvingItemField.itemField not yet resolved: " + NullFeature.class.getName(), e.getMessage());
		}
		try
		{
			new Model(nonResolvingItemField);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("there is no type for class " + NullFeature.class.getName(), e.getMessage());
		}
		
		try
		{
			TypesExclusive.newType(BeforeNewNotStatic.class);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals(
					"method beforeNewCopeItem(SetValue[]) " +
					"in class " + BeforeNewNotStatic.class.getName() +
					" must be static",
					e.getMessage());
		}
		try
		{
			TypesExclusive.newType(BeforeNewWrongReturn.class);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals(
					"method beforeNewCopeItem(SetValue[]) " +
					"in class " + BeforeNewWrongReturn.class.getName() +
					" must return SetValue[], " +
					"but returns java.lang.String", e.getMessage());
		}
	}
	
	static class NoItem
	{
		NoItem()
		{
			// just a dummy constructor
		}
	}
	
	static class NoActivationConstructor extends Item
	{
		private static final long serialVersionUID = 1l;
	}
	
	static class WrongActivationConstructor extends Item
	{
		private static final long serialVersionUID = 1l;
		
		WrongActivationConstructor(final ActivationParameters ap)
		{
			super(new ActivationParameters(ap.type, ap.pk-1));
		}
	}
	
	static class NullFeature extends Item
	{
		private static final long serialVersionUID = 1l;
		
		static final Feature nullFeature = null;
	}
	
	static class NonResolvingItemField extends Item
	{
		private static final long serialVersionUID = 1l;
		
		static final ItemField itemField = Item.newItemField(NullFeature.class);
		
		NonResolvingItemField(final ActivationParameters ap)
		{
			super(ap);
		}
	}
	
	static class BeforeNewNotStatic extends Item
	{
		private static final long serialVersionUID = 1l;
		
		@SuppressWarnings("unused")
		private final SetValue[] beforeNewCopeItem(final SetValue[] setValues)
		{
			return setValues;
		}
		
		public BeforeNewNotStatic(final ActivationParameters ap)
		{
			super(ap);
		}
	}
	
	static class BeforeNewWrongReturn extends Item
	{
		private static final long serialVersionUID = 1l;
		
		@SuppressWarnings("unused")
		private static final String beforeNewCopeItem(final SetValue[] setValues)
		{
			return "";
		}
		
		BeforeNewWrongReturn(final ActivationParameters ap)
		{
			super(ap);
		}
	}
	
	@SuppressWarnings("unchecked") // OK: test bad API usage
	private static final Class<Item> castItemClass(Class c)
	{
		return c;
	}
}
