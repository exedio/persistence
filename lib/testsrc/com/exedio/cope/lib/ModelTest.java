
package com.exedio.cope.lib;

import java.util.Arrays;

import com.exedio.cope.lib.hierarchy.FirstSub;
import com.exedio.cope.lib.hierarchy.Super;

/**
 * Tests the model itself, without creating/using any persistent data.
 */
public class ModelTest extends AbstractLibTest
{
	public void testType()
	{
		final AttributeItem item = null;

		assertEquals(AttributeItem.class, item.TYPE.getJavaClass());
		assertEquals(item.TYPE, Type.findByJavaClass(AttributeItem.class));
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
			item.someEnumeration,
			item.someNotNullEnumeration,
			item.someMedia,
		};
		assertEquals(Arrays.asList(attributes), item.TYPE.getAttributes());
		assertEquals(Arrays.asList(attributes), item.TYPE.getDeclaredAttributes());
		assertUnmodifiable(item.TYPE.getAttributes());
		assertUnmodifiable(item.TYPE.getDeclaredAttributes());

		final Feature[] features = new Feature[]{
			item.someString,
			item.someStringUpperCase,
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
			item.someEnumeration,
			item.someNotNullEnumeration,
			item.someMedia,
		};
		assertEquals(Arrays.asList(features), item.TYPE.getFeatures());
		assertEquals(Arrays.asList(features), item.TYPE.getDeclaredFeatures());
		assertUnmodifiable(item.TYPE.getFeatures());
		assertUnmodifiable(item.TYPE.getDeclaredFeatures());
		
		assertEquals(item.someString, item.TYPE.getFeature("someString"));
		assertEquals(item.someStringUpperCase, item.TYPE.getFeature("someStringUpperCase"));
	}

	public void testSomeEnumeration()
	{
		final AttributeItem item = null;

		assertEquals(item.TYPE, item.someEnumeration.getType());
		assertEquals(
			list(
				AttributeItem.SomeEnumeration.enumValue1,
				AttributeItem.SomeEnumeration.enumValue2,
				AttributeItem.SomeEnumeration.enumValue3),
			item.someEnumeration.getValues());

		assertEquals(
			AttributeItem.SomeEnumeration.enumValue1,
			item.someEnumeration.getValue(
				AttributeItem.SomeEnumeration.enumValue1NUM));
		assertEquals(
			AttributeItem.SomeEnumeration.enumValue2,
			item.someEnumeration.getValue(
				AttributeItem.SomeEnumeration.enumValue2NUM));
		assertEquals(
			AttributeItem.SomeEnumeration.enumValue3,
			item.someEnumeration.getValue(
				AttributeItem.SomeEnumeration.enumValue3NUM));

		assertEquals(
			AttributeItem.SomeEnumeration.enumValue1,
			item.someEnumeration.getValue("enumValue1"));
		assertEquals(
			AttributeItem.SomeEnumeration.enumValue2,
			item.someEnumeration.getValue("enumValue2"));
		assertEquals(
			AttributeItem.SomeEnumeration.enumValue3,
			item.someEnumeration.getValue("enumValue3"));

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
			new EnumerationAttribute(Item.DEFAULT, getClass());
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
			ItemWithSingleUnique.uniqueString.getSingleUniqueConstaint().getUniqueAttributes());
		assertEquals(
			list(ItemWithSingleUniqueReadOnly.uniqueReadOnlyString),
			ItemWithSingleUniqueReadOnly.uniqueReadOnlyString.getSingleUniqueConstaint().getUniqueAttributes());
		assertEquals(
			list(ItemWithSingleUniqueNotNull.uniqueNotNullString),
			ItemWithSingleUniqueNotNull.uniqueNotNullString.getSingleUniqueConstaint().getUniqueAttributes());
	}

	public void testHierarchy()
	{
		// Super
		assertEquals(null, Super.TYPE.getSupertype());

		assertEquals(list(Super.superInt), Super.TYPE.getDeclaredAttributes());
		assertEquals(list(Super.superInt), Super.TYPE.getAttributes());
		assertEquals(list(Super.superInt), Super.TYPE.getDeclaredFeatures());
		assertEquals(list(Super.superInt), Super.TYPE.getFeatures());

		assertUnmodifiable(Super.TYPE.getDeclaredAttributes());
		assertUnmodifiable(Super.TYPE.getAttributes());
		assertUnmodifiable(Super.TYPE.getDeclaredFeatures());
		assertUnmodifiable(Super.TYPE.getFeatures());

		assertEquals(Super.TYPE, Super.superInt.getType());
		
		// FirstSub
		assertEquals(Super.TYPE, FirstSub.TYPE.getSupertype());

		assertEquals(list(FirstSub.firstSubString), FirstSub.TYPE.getDeclaredAttributes());
		assertEquals(list(Super.superInt, FirstSub.firstSubString), FirstSub.TYPE.getAttributes());
		assertEquals(list(FirstSub.firstSubString), FirstSub.TYPE.getDeclaredFeatures());
		assertEquals(list(Super.superInt, FirstSub.firstSubString), FirstSub.TYPE.getFeatures());

		assertUnmodifiable(FirstSub.TYPE.getDeclaredAttributes());
		assertUnmodifiable(FirstSub.TYPE.getAttributes());
		assertUnmodifiable(FirstSub.TYPE.getDeclaredFeatures());
		assertUnmodifiable(FirstSub.TYPE.getFeatures());

		assertEquals(FirstSub.TYPE, FirstSub.firstSubString.getType());
	}

}
