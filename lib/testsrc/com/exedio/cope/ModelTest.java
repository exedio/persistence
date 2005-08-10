/*
 * Copyright (C) 2004-2005  exedio GmbH (www.exedio.com)
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
import com.exedio.cope.testmodel.FirstSub;
import com.exedio.cope.testmodel.ItemWithSingleUnique;
import com.exedio.cope.testmodel.ItemWithSingleUniqueNotNull;
import com.exedio.cope.testmodel.ItemWithSingleUniqueReadOnly;
import com.exedio.cope.testmodel.Main;
import com.exedio.cope.testmodel.SecondSub;
import com.exedio.cope.testmodel.Super;
import com.exedio.cope.util.ReactivationConstructorDummy;

/**
 * Tests the model itself, without creating/using any persistent data.
 * @author Ralf Wiebicke
 */
public class ModelTest extends AbstractLibTest
{
	public ModelTest()
	{
		super(Main.model);
	}
	
	public void testType()
	{
		final AttributeItem item = null;

		assertEquals(AttributeItem.class, item.TYPE.getJavaClass());
		assertEquals(item.TYPE, Type.findByJavaClass(AttributeItem.class));
		try
		{
			Type.findByJavaClass(ModelTest.class);
			fail("should have thrown RuntimeException");
		}
		catch(RuntimeException e)
		{
			assertEquals("there is no type for class com.exedio.cope.ModelTest", e.getMessage());
		}
		assertEquals(item.TYPE, model.findTypeByID(item.TYPE.getID()));
		
		final Attribute[] attributes = new Attribute[]{
			item.someString,
			item.someNotNullString,
			item.someInteger,
			item.someNotNullInteger,
			item.someLong,
			item.someNotNullLong,
			item.someDouble,
			item.someNotNullDouble,
			item.someDate,
			item.someLongDate,
			item.someBoolean,
			item.someNotNullBoolean,
			item.someItem,
			item.someNotNullItem,
			item.someEnum,
			item.someNotNullEnumeration,
			item.someData,
		};
		assertEquals(Arrays.asList(attributes), item.TYPE.getAttributes());
		assertEquals(Arrays.asList(attributes), item.TYPE.getDeclaredAttributes());
		assertUnmodifiable(item.TYPE.getAttributes());
		assertUnmodifiable(item.TYPE.getDeclaredAttributes());

		final Feature[] features = new Feature[]{
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
			item.someLongDate,
			item.someBoolean,
			item.someNotNullBoolean,
			item.someItem,
			item.someNotNullItem,
			item.someEnum,
			item.someNotNullEnumeration,
			item.someData,
		};
		assertEquals(Arrays.asList(features), item.TYPE.getFeatures());
		assertEquals(Arrays.asList(features), item.TYPE.getDeclaredFeatures());
		assertUnmodifiable(item.TYPE.getFeatures());
		assertUnmodifiable(item.TYPE.getDeclaredFeatures());
		
		assertEquals(item.someString, item.TYPE.getFeature("someString"));
		assertEquals(item.someStringUpperCase, item.TYPE.getFeature("someStringUpperCase"));
		
		try
		{
			new Type(NoCreationConstructor.class);
			fail();
		}
		catch(NestingRuntimeException e)
		{
			assertEquals(
					NoCreationConstructor.class.getName() +
					" does not have a creation constructor:" + NoCreationConstructor.class.getName() +
					".<init>([L" + AttributeValue.class.getName() + ";)", e.getMessage());
			assertEquals(NoSuchMethodException.class, e.getNestedCause().getClass());
		}

		try
		{
			new Type(NoReactivationConstructor.class);
			fail();
		}
		catch(NestingRuntimeException e)
		{
			assertEquals(e.getMessage(),
					NoReactivationConstructor.class.getName() +
					" does not have a reactivation constructor:" + NoReactivationConstructor.class.getName() +
					".<init>(" + ReactivationConstructorDummy.class.getName() + ", int)", e.getMessage());
			assertEquals(NoSuchMethodException.class, e.getNestedCause().getClass());
		}
	}
	
	static class NoCreationConstructor extends Item
	{
		NoCreationConstructor()
		{
			super(null);
		}
	}

	static class NoReactivationConstructor extends Item
	{
		NoReactivationConstructor(final AttributeValue[] initialAttributes)
		{
			super(null);
		}
	}

	public void testSomeEnumeration()
	{
		final AttributeItem item = null;

		assertEquals(item.TYPE, item.someEnum.getType());
		assertEquals(
			list(
				AttributeItem.SomeEnumeration.enumValue1,
				AttributeItem.SomeEnumeration.enumValue2,
				AttributeItem.SomeEnumeration.enumValue3),
			item.someEnum.getValues());

		assertEquals(
			AttributeItem.SomeEnumeration.enumValue1,
			item.someEnum.getValue(
				AttributeItem.SomeEnumeration.enumValue1NUM));
		assertEquals(
			AttributeItem.SomeEnumeration.enumValue2,
			item.someEnum.getValue(
				AttributeItem.SomeEnumeration.enumValue2NUM));
		assertEquals(
			AttributeItem.SomeEnumeration.enumValue3,
			item.someEnum.getValue(
				AttributeItem.SomeEnumeration.enumValue3NUM));

		assertEquals(
			AttributeItem.SomeEnumeration.enumValue1,
			item.someEnum.getValue("enumValue1"));
		assertEquals(
			AttributeItem.SomeEnumeration.enumValue2,
			item.someEnum.getValue("enumValue2"));
		assertEquals(
			AttributeItem.SomeEnumeration.enumValue3,
			item.someEnum.getValue("enumValue3"));

		assertEquals(AttributeItem.SomeEnumeration.class,
			AttributeItem.SomeEnumeration.enumValue1.getEnumerationClass());
		assertEquals(AttributeItem.SomeEnumeration.class,
			AttributeItem.SomeEnumeration.enumValue2.getEnumerationClass());
		assertEquals(AttributeItem.SomeEnumeration.class,
			AttributeItem.SomeEnumeration.enumValue3.getEnumerationClass());

		assertEquals("enumValue1",
			AttributeItem.SomeEnumeration.enumValue1.getCode());
		assertEquals("enumValue2",
			AttributeItem.SomeEnumeration.enumValue2.getCode());
		assertEquals("enumValue3",
			AttributeItem.SomeEnumeration.enumValue3.getCode());

		assertEquals(100,
			AttributeItem.SomeEnumeration.enumValue1.getNumber());
		assertEquals(200,
			AttributeItem.SomeEnumeration.enumValue2.getNumber());
		assertEquals(300,
			AttributeItem.SomeEnumeration.enumValue3.getNumber());

		assertEquals(new Integer(100),
			AttributeItem.SomeEnumeration.enumValue1.getNumberObject());
		assertEquals(new Integer(200),
			AttributeItem.SomeEnumeration.enumValue2.getNumberObject());
		assertEquals(new Integer(300),
			AttributeItem.SomeEnumeration.enumValue3.getNumberObject());

		assertTrue(!
			AttributeItem.SomeEnumeration.enumValue1.equals(
			AttributeItem.SomeEnumeration.enumValue2));
		assertTrue(!
			AttributeItem.SomeEnumeration.enumValue2.equals(
			AttributeItem.SomeEnumeration.enumValue3));
		assertTrue(!
			AttributeItem.SomeEnumeration.enumValue3.equals(
			AttributeItem.SomeEnumeration.enumValue1));

		AttributeItem.SomeEnumeration someEnumeration = AttributeItem.SomeEnumeration.enumValue1;
		switch (someEnumeration.getNumber())
		{
			case AttributeItem.SomeEnumeration.enumValue1NUM :
				someEnumeration = AttributeItem.SomeEnumeration.enumValue2;
				break;
			case AttributeItem.SomeEnumeration.enumValue2NUM :
				someEnumeration = AttributeItem.SomeEnumeration.enumValue3;
				break;
			case AttributeItem.SomeEnumeration.enumValue3NUM :
				someEnumeration = AttributeItem.SomeEnumeration.enumValue1;
				break;
			default :
				throw new RuntimeException("Ooooops");
		}
		assertEquals(someEnumeration, AttributeItem.SomeEnumeration.enumValue2);
		
		try
		{
			new EnumAttribute(Item.DEFAULT, getClass());
			fail("should have thrown RuntimeException");
		}
		catch(RuntimeException e)
		{
		}
	}
	
	public void testUniqueConstraints()
	{
		assertEquals(
			list(ItemWithSingleUnique.uniqueString),
			ItemWithSingleUnique.uniqueString.getSingleUniqueConstraint().getUniqueAttributes());
		assertEquals(
			list(ItemWithSingleUniqueReadOnly.uniqueReadOnlyString),
			ItemWithSingleUniqueReadOnly.uniqueReadOnlyString.getSingleUniqueConstraint().getUniqueAttributes());
		assertEquals(
			list(ItemWithSingleUniqueNotNull.uniqueNotNullString),
			ItemWithSingleUniqueNotNull.uniqueNotNullString.getSingleUniqueConstraint().getUniqueAttributes());
	}

	public void testHierarchy()
	{
		// Super
		assertEquals(null, Super.TYPE.getSupertype());
		assertEquals(list(FirstSub.TYPE, SecondSub.TYPE), Super.TYPE.getSubTypes());

		assertEquals(list(Super.superInt, Super.superString), Super.TYPE.getDeclaredAttributes());
		assertEquals(list(Super.superInt, Super.superString), Super.TYPE.getAttributes());
		assertEquals(list(Super.superInt, Super.superString), Super.TYPE.getDeclaredFeatures());
		assertEquals(list(Super.superInt, Super.superString), Super.TYPE.getFeatures());

		assertUnmodifiable(Super.TYPE.getSubTypes());
		assertUnmodifiable(Super.TYPE.getDeclaredAttributes());
		assertUnmodifiable(Super.TYPE.getAttributes());
		assertUnmodifiable(Super.TYPE.getDeclaredFeatures());
		assertUnmodifiable(Super.TYPE.getFeatures());

		assertEquals(Super.TYPE, Super.superInt.getType());
		
		// FirstSub
		assertEquals(Super.TYPE, FirstSub.TYPE.getSupertype());
		assertEquals(list(), FirstSub.TYPE.getSubTypes());

		assertEquals(list(FirstSub.firstSubString), FirstSub.TYPE.getDeclaredAttributes());
		assertEquals(list(Super.superInt, Super.superString, FirstSub.firstSubString), FirstSub.TYPE.getAttributes());
		assertEquals(list(FirstSub.firstSubString), FirstSub.TYPE.getDeclaredFeatures());
		assertEquals(list(Super.superInt, Super.superString, FirstSub.firstSubString), FirstSub.TYPE.getFeatures());

		assertUnmodifiable(FirstSub.TYPE.getSubTypes());
		assertUnmodifiable(FirstSub.TYPE.getDeclaredAttributes());
		assertUnmodifiable(FirstSub.TYPE.getAttributes());
		assertUnmodifiable(FirstSub.TYPE.getDeclaredFeatures());
		assertUnmodifiable(FirstSub.TYPE.getFeatures());

		assertEquals(FirstSub.TYPE, FirstSub.firstSubString.getType());
	}

}
