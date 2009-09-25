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

import com.exedio.cope.testmodel.AttributeItem;
import com.exedio.cope.util.ReactivationConstructorDummy;

public class TypeTest extends TestmodelTest
{
	public void testType()
	{
		final AttributeItem item = null;
		
		assertEquals(AttributeItem.class, item.TYPE.getJavaClass());
		assertEquals(true, item.TYPE.isJavaClassExclusive());
		assertEquals(item.TYPE, Type.forClass(AttributeItem.class));
		try
		{
			Type.forClass(Item.class);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("there is no type for class com.exedio.cope.Item", e.getMessage());
		}
		assertEquals(item.TYPE, model.getType(item.TYPE.getID()));

		assertSame(item.TYPE, item.TYPE.getThis().getType());
		assertEquals("AttributeItem.this", item.TYPE.getThis().getID());
		assertEquals("AttributeItem.this", item.TYPE.getThis().toString());
		assertEquals("this", item.TYPE.getThis().getName());
		
		final Field[] attributes = {
			item.someString,
			item.someNotNullString,
			item.someInteger,
			item.someNotNullInteger,
			item.someLong,
			item.someNotNullLong,
			item.someDouble,
			item.someNotNullDouble,
			item.someDate,
			item.day,
			item.someBoolean,
			item.someNotNullBoolean,
			item.someItem,
			item.someNotNullItem,
			item.someEnum,
			item.someNotNullEnum,
			item.someData.getBody(),
			item.someData.getContentType(),
			item.someData.getLastModified(),
		};
		assertEqualsUnmodifiable(Arrays.asList(attributes), item.TYPE.getFields());
		assertEqualsUnmodifiable(Arrays.asList(attributes), item.TYPE.getDeclaredFields());
		assertEqualsUnmodifiable(list(), item.TYPE.getUniqueConstraints());
		assertEqualsUnmodifiable(list(), item.TYPE.getDeclaredUniqueConstraints());

		final Feature[] features = {
			item.TYPE.getThis(),
			item.someString,
			item.someStringUpperCase,
			item.someStringLength,
			item.someNotNullString,
			item.someInteger,
			item.someNotNullInteger,
			item.someLong,
			item.someNotNullLong,
			item.someDouble,
			item.someNotNullDouble,
			item.someDate,
			item.day,
			item.someBoolean,
			item.someNotNullBoolean,
			item.someItem,
			item.someNotNullItem,
			item.someEnum,
			item.someNotNullEnum,
			item.someData,
			item.someData.getBody(),
			item.someData.getContentType(),
			item.someData.getLastModified(),
		};
		assertEqualsUnmodifiable(Arrays.asList(features), item.TYPE.getFeatures());
		assertEqualsUnmodifiable(Arrays.asList(features), item.TYPE.getDeclaredFeatures());
		
		assertEquals(item.someString, item.TYPE.getDeclaredFeature("someString"));
		assertEquals(item.someStringUpperCase, item.TYPE.getDeclaredFeature("someStringUpperCase"));
		
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

		NoReactivationConstructor(final SetValue[] initialAttributes)
		{
			super(initialAttributes);
		}
	}
	
	@SuppressWarnings("unchecked") // OK: test bad API usage
	private static final Class<Item> castItemClass(Class c)
	{
		return c;
	}
}
