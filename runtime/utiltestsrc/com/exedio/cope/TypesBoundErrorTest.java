/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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

import static com.exedio.cope.TypesBound.newType;

import com.exedio.cope.junit.CopeAssert;

public class TypesBoundErrorTest extends CopeAssert
{
	public void testErrors()
	{
		try
		{
			newType(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("javaClass", e.getMessage());
		}
		try
		{
			newType(Item.class);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("Cannot make a type for " + Item.class + " itself, but only for subclasses.", e.getMessage());
		}
		try
		{
			newType(castItemClass(NoItem.class));
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals(NoItem.class.toString() + " is not a subclass of Item", e.getMessage());
		}
		try
		{
			newType(NoActivationConstructor.class);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals(e.getMessage(),
					NoActivationConstructor.class.getName() +
					" does not have an activation constructor NoActivationConstructor(" + ActivationParameters.class.getName() + ")", e.getMessage());
			assertEquals(NoSuchMethodException.class, e.getCause().getClass());
		}
		final Type<WrongActivationConstructor> wrongActivationConstructor = newType(WrongActivationConstructor.class);
		try
		{
			new Model(wrongActivationConstructor);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("WrongActivationConstructor/" + WrongActivationConstructor.class.getName(), e.getMessage());
		}
		try
		{
			newType(WrongActivationConstructor.class);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("class is already bound to a type: " + WrongActivationConstructor.class.getName(), e.getMessage());
		}
		try
		{
			newType(NullFeature.class);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals(NullFeature.class.getName() + "#nullFeature", e.getMessage());
		}

		try
		{
			NonResolvingItemField.itemField.getValueType();
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("value type of " + NonResolvingItemField.itemField.toString() + " (" + NullFeature.class.getName() + ") does not belong to any model", e.getMessage());
		}
		final Type<NonResolvingItemField> nonResolvingItemField = newType(NonResolvingItemField.class);
		try
		{
			NonResolvingItemField.itemField.getValueType();
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("value type of " + NonResolvingItemField.itemField.toString() + " (" + NullFeature.class.getName() + ") does not belong to any model", e.getMessage());
		}
		try
		{
			new Model(nonResolvingItemField);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("there is no type for class " + NullFeature.class.getName(), e.getMessage());
		}

		try
		{
			newType(BeforeNewNotStatic.class);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals(
					"method beforeNewCopeItem(SetValue[]) " +
					"in class " + BeforeNewNotStatic.class.getName() +
					" must be static",
					e.getMessage());
		}
		try
		{
			newType(BeforeNewWrongReturn.class);
			fail();
		}
		catch(final IllegalArgumentException e)
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

		static final ItemField<NullFeature> itemField = ItemField.create(NullFeature.class);

		NonResolvingItemField(final ActivationParameters ap)
		{
			super(ap);
		}
	}

	static class BeforeNewNotStatic extends Item
	{
		private static final long serialVersionUID = 1l;

		@SuppressWarnings({"unused", "static-method"})
		private final SetValue<?>[] beforeNewCopeItem(final SetValue<?>[] setValues)
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
		private static final String beforeNewCopeItem(final SetValue<?>[] setValues)
		{
			return "";
		}

		BeforeNewWrongReturn(final ActivationParameters ap)
		{
			super(ap);
		}
	}

	@SuppressWarnings({"unchecked", "rawtypes"}) // OK: test bad API usage
	private static final Class<Item> castItemClass(final Class c)
	{
		return c;
	}
}
