
package com.exedio.cope.lib;

import com.exedio.cope.lib.hierarchy.FirstSub;
import com.exedio.cope.lib.hierarchy.Super;

/**
 * Tests the model itself, without creating/using any persistent data.
 */
public class ModelTest extends AbstractLibTest
{
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
		switch (someEnumeration.number)
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
			new EnumerationAttribute(getClass());
			fail("should have thrown RuntimeException");
		}
		catch(RuntimeException e)
		{
		}
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
