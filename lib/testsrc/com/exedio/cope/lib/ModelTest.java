
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
		final ItemWithManyAttributes item = null;

		assertEquals(ItemWithManyAttributes.class, item.TYPE.getJavaClass());
		assertEquals(item.TYPE, Type.findByJavaClass(ItemWithManyAttributes.class));
		assertEquals(item.TYPE, Type.findByID(item.TYPE.getID()));
		
		final Attribute[] attributes = new Attribute[]{
			item.someString,
			item.someNotNullString,
			item.someInteger,
			item.someNotNullInteger,
			item.someLong,
			item.someNotNullLong,
			item.someDouble,
			item.someNotNullDouble,
			item.someBoolean,
			item.someNotNullBoolean,
			item.someItem,
			item.someNotNullItem,
			item.someEnumeration,
			item.someNotNullEnumeration,
			item.someMedia,
			item.someQualifiedString,
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
			item.someBoolean,
			item.someNotNullBoolean,
			item.someItem,
			item.someNotNullItem,
			item.someEnumeration,
			item.someNotNullEnumeration,
			item.someMedia,
			item.someQualifiedString,
		};
		assertEquals(Arrays.asList(features), item.TYPE.getFeatures());
		assertEquals(Arrays.asList(features), item.TYPE.getDeclaredFeatures());
		assertUnmodifiable(item.TYPE.getFeatures());
		assertUnmodifiable(item.TYPE.getDeclaredFeatures());
	}

	public void testSomeEnumeration()
	{
		final ItemWithManyAttributes item = null;

		assertEquals(item.TYPE, item.someEnumeration.getType());
		assertEquals(
			list(
				ItemWithManyAttributes.SomeEnumeration.enumValue1,
				ItemWithManyAttributes.SomeEnumeration.enumValue2,
				ItemWithManyAttributes.SomeEnumeration.enumValue3),
			item.someEnumeration.getValues());

		assertEquals(
			ItemWithManyAttributes.SomeEnumeration.enumValue1,
			item.someEnumeration.getValue(
				ItemWithManyAttributes.SomeEnumeration.enumValue1NUM));
		assertEquals(
			ItemWithManyAttributes.SomeEnumeration.enumValue2,
			item.someEnumeration.getValue(
				ItemWithManyAttributes.SomeEnumeration.enumValue2NUM));
		assertEquals(
			ItemWithManyAttributes.SomeEnumeration.enumValue3,
			item.someEnumeration.getValue(
				ItemWithManyAttributes.SomeEnumeration.enumValue3NUM));

		assertEquals(
			ItemWithManyAttributes.SomeEnumeration.enumValue1,
			item.someEnumeration.getValue("enumValue1"));
		assertEquals(
			ItemWithManyAttributes.SomeEnumeration.enumValue2,
			item.someEnumeration.getValue("enumValue2"));
		assertEquals(
			ItemWithManyAttributes.SomeEnumeration.enumValue3,
			item.someEnumeration.getValue("enumValue3"));

		assertEquals(ItemWithManyAttributes.SomeEnumeration.class,
			ItemWithManyAttributes.SomeEnumeration.enumValue1.getEnumerationClass());
		assertEquals(ItemWithManyAttributes.SomeEnumeration.class,
			ItemWithManyAttributes.SomeEnumeration.enumValue2.getEnumerationClass());
		assertEquals(ItemWithManyAttributes.SomeEnumeration.class,
			ItemWithManyAttributes.SomeEnumeration.enumValue3.getEnumerationClass());

		assertEquals("enumValue1",
			ItemWithManyAttributes.SomeEnumeration.enumValue1.getCode());
		assertEquals("enumValue2",
			ItemWithManyAttributes.SomeEnumeration.enumValue2.getCode());
		assertEquals("enumValue3",
			ItemWithManyAttributes.SomeEnumeration.enumValue3.getCode());

		assertEquals(100,
			ItemWithManyAttributes.SomeEnumeration.enumValue1.getNumber());
		assertEquals(200,
			ItemWithManyAttributes.SomeEnumeration.enumValue2.getNumber());
		assertEquals(300,
			ItemWithManyAttributes.SomeEnumeration.enumValue3.getNumber());

		assertEquals(new Integer(100),
			ItemWithManyAttributes.SomeEnumeration.enumValue1.getNumberObject());
		assertEquals(new Integer(200),
			ItemWithManyAttributes.SomeEnumeration.enumValue2.getNumberObject());
		assertEquals(new Integer(300),
			ItemWithManyAttributes.SomeEnumeration.enumValue3.getNumberObject());

		assertTrue(!
			ItemWithManyAttributes.SomeEnumeration.enumValue1.equals(
			ItemWithManyAttributes.SomeEnumeration.enumValue2));
		assertTrue(!
			ItemWithManyAttributes.SomeEnumeration.enumValue2.equals(
			ItemWithManyAttributes.SomeEnumeration.enumValue3));
		assertTrue(!
			ItemWithManyAttributes.SomeEnumeration.enumValue3.equals(
			ItemWithManyAttributes.SomeEnumeration.enumValue1));

		ItemWithManyAttributes.SomeEnumeration someEnumeration = ItemWithManyAttributes.SomeEnumeration.enumValue1;
		switch (someEnumeration.getNumber())
		{
			case ItemWithManyAttributes.SomeEnumeration.enumValue1NUM :
				someEnumeration = ItemWithManyAttributes.SomeEnumeration.enumValue2;
				break;
			case ItemWithManyAttributes.SomeEnumeration.enumValue2NUM :
				someEnumeration = ItemWithManyAttributes.SomeEnumeration.enumValue3;
				break;
			case ItemWithManyAttributes.SomeEnumeration.enumValue3NUM :
				someEnumeration = ItemWithManyAttributes.SomeEnumeration.enumValue1;
				break;
			default :
				throw new RuntimeException("Ooooops");
		}
		assertEquals(someEnumeration, ItemWithManyAttributes.SomeEnumeration.enumValue2);
		
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
		assertEquals(null, Super.TYPE.getSupertype());
		assertEquals(list(Super.superInt), Super.TYPE.getDeclaredAttributes());
		assertEquals(list(Super.superInt), Super.TYPE.getAttributes());
		assertUnmodifiable(Super.TYPE.getDeclaredAttributes());
		assertUnmodifiable(Super.TYPE.getAttributes());
		assertEquals(Super.TYPE, Super.superInt.getType());
		
		assertEquals(Super.TYPE, FirstSub.TYPE.getSupertype());
		assertEquals(list(FirstSub.firstSubString), FirstSub.TYPE.getDeclaredAttributes());
		assertEquals(list(Super.superInt, FirstSub.firstSubString), FirstSub.TYPE.getAttributes());
		assertUnmodifiable(FirstSub.TYPE.getDeclaredAttributes());
		assertUnmodifiable(FirstSub.TYPE.getAttributes());
		assertEquals(FirstSub.TYPE, FirstSub.firstSubString.getType());
	}

}
