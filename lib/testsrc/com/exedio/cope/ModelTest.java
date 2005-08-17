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
import com.exedio.cope.testmodel.ItemWithSingleUnique;
import com.exedio.cope.testmodel.ItemWithSingleUniqueNotNull;
import com.exedio.cope.testmodel.ItemWithSingleUniqueReadOnly;
import com.exedio.cope.testmodel.Main;
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
			item.someNotNullEnum,
			item.someData.getData(),
			item.someData.getMimeMajor(),
			item.someData.getMimeMinor(),
		};
		assertEqualsUnmodifiable(Arrays.asList(attributes), item.TYPE.getAttributes());
		assertEqualsUnmodifiable(Arrays.asList(attributes), item.TYPE.getDeclaredAttributes());

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
			item.someNotNullEnum,
			item.someData,
			item.someData.getData(),
			item.someData.getMimeMajor(),
			item.someData.getMimeMinor(),
			item.emptyItem,
		};
		assertEqualsUnmodifiable(Arrays.asList(features), item.TYPE.getFeatures());
		assertEqualsUnmodifiable(Arrays.asList(features), item.TYPE.getDeclaredFeatures());
		
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
				AttributeItem.SomeEnum.enumValue1,
				AttributeItem.SomeEnum.enumValue2,
				AttributeItem.SomeEnum.enumValue3),
			item.someEnum.getValues());

		assertEquals(
			AttributeItem.SomeEnum.enumValue1,
			item.someEnum.getValue(
				AttributeItem.SomeEnum.enumValue1NUM));
		assertEquals(
			AttributeItem.SomeEnum.enumValue2,
			item.someEnum.getValue(
				AttributeItem.SomeEnum.enumValue2NUM));
		assertEquals(
			AttributeItem.SomeEnum.enumValue3,
			item.someEnum.getValue(
				AttributeItem.SomeEnum.enumValue3NUM));

		assertEquals(
			AttributeItem.SomeEnum.enumValue1,
			item.someEnum.getValue("enumValue1"));
		assertEquals(
			AttributeItem.SomeEnum.enumValue2,
			item.someEnum.getValue("enumValue2"));
		assertEquals(
			AttributeItem.SomeEnum.enumValue3,
			item.someEnum.getValue("enumValue3"));

		assertEquals(AttributeItem.SomeEnum.class,
			AttributeItem.SomeEnum.enumValue1.getEnumerationClass());
		assertEquals(AttributeItem.SomeEnum.class,
			AttributeItem.SomeEnum.enumValue2.getEnumerationClass());
		assertEquals(AttributeItem.SomeEnum.class,
			AttributeItem.SomeEnum.enumValue3.getEnumerationClass());

		assertEquals("enumValue1",
			AttributeItem.SomeEnum.enumValue1.getCode());
		assertEquals("enumValue2",
			AttributeItem.SomeEnum.enumValue2.getCode());
		assertEquals("enumValue3",
			AttributeItem.SomeEnum.enumValue3.getCode());

		assertEquals(100,
			AttributeItem.SomeEnum.enumValue1.getNumber());
		assertEquals(200,
			AttributeItem.SomeEnum.enumValue2.getNumber());
		assertEquals(300,
			AttributeItem.SomeEnum.enumValue3.getNumber());

		assertEquals(new Integer(100),
			AttributeItem.SomeEnum.enumValue1.getNumberObject());
		assertEquals(new Integer(200),
			AttributeItem.SomeEnum.enumValue2.getNumberObject());
		assertEquals(new Integer(300),
			AttributeItem.SomeEnum.enumValue3.getNumberObject());

		assertTrue(!
			AttributeItem.SomeEnum.enumValue1.equals(
			AttributeItem.SomeEnum.enumValue2));
		assertTrue(!
			AttributeItem.SomeEnum.enumValue2.equals(
			AttributeItem.SomeEnum.enumValue3));
		assertTrue(!
			AttributeItem.SomeEnum.enumValue3.equals(
			AttributeItem.SomeEnum.enumValue1));

		AttributeItem.SomeEnum someEnumeration = AttributeItem.SomeEnum.enumValue1;
		switch (someEnumeration.getNumber())
		{
			case AttributeItem.SomeEnum.enumValue1NUM :
				someEnumeration = AttributeItem.SomeEnum.enumValue2;
				break;
			case AttributeItem.SomeEnum.enumValue2NUM :
				someEnumeration = AttributeItem.SomeEnum.enumValue3;
				break;
			case AttributeItem.SomeEnum.enumValue3NUM :
				someEnumeration = AttributeItem.SomeEnum.enumValue1;
				break;
			default :
				throw new RuntimeException("Ooooops");
		}
		assertEquals(someEnumeration, AttributeItem.SomeEnum.enumValue2);
		
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
		assertEqualsUnmodifiable(
			list(ItemWithSingleUnique.uniqueString),
			ItemWithSingleUnique.uniqueString.getSingleUniqueConstraint().getUniqueAttributes());
		assertEqualsUnmodifiable(
			list(ItemWithSingleUniqueReadOnly.uniqueReadOnlyString),
			ItemWithSingleUniqueReadOnly.uniqueReadOnlyString.getSingleUniqueConstraint().getUniqueAttributes());
		assertEqualsUnmodifiable(
			list(ItemWithSingleUniqueNotNull.uniqueNotNullString),
			ItemWithSingleUniqueNotNull.uniqueNotNullString.getSingleUniqueConstraint().getUniqueAttributes());
	}

}
