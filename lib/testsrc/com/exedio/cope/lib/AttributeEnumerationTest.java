package com.exedio.cope.lib;


public class AttributeEnumerationTest extends AttributesTest
{
	public void testSomeEnumeration()
	{
		assertEquals(null, item.getSomeEnumeration());
		item.setSomeEnumeration(ItemWithManyAttributes.SomeEnumeration.enumValue1);
		assertEquals(
			ItemWithManyAttributes.SomeEnumeration.enumValue1,
			item.getSomeEnumeration());
		item.setSomeEnumeration(
			ItemWithManyAttributes.SomeEnumeration.enumValue2);
		assertEquals(
			ItemWithManyAttributes.SomeEnumeration.enumValue2,
			item.getSomeEnumeration());
		item.passivate();
		assertEquals(
			ItemWithManyAttributes.SomeEnumeration.enumValue2,
			item.getSomeEnumeration());
		item.setSomeEnumeration(null);
		assertEquals(null, item.getSomeEnumeration());
	}

	public void testNotNullSomeEnumeration()
			throws NotNullViolationException
	{
		assertEquals(ItemWithManyAttributes.SomeEnumeration.enumValue1, item.getSomeNotNullEnumeration());
		item.setSomeNotNullEnumeration(ItemWithManyAttributes.SomeEnumeration.enumValue2);
		assertEquals(
			ItemWithManyAttributes.SomeEnumeration.enumValue2,
			item.getSomeNotNullEnumeration());
		item.setSomeNotNullEnumeration(
			ItemWithManyAttributes.SomeEnumeration.enumValue3);
		assertEquals(
			ItemWithManyAttributes.SomeEnumeration.enumValue3,
			item.getSomeNotNullEnumeration());
		item.passivate();
		assertEquals(
			ItemWithManyAttributes.SomeEnumeration.enumValue3,
			item.getSomeNotNullEnumeration());
		try
		{
			item.setSomeNotNullEnumeration(null);
		}
		catch(NotNullViolationException e)
		{
			assertEquals(item, e.getItem());
			assertEquals(item.someNotNullEnumeration, e.getNotNullAttribute());
		}
		assertEquals(
			ItemWithManyAttributes.SomeEnumeration.enumValue3,
			item.getSomeNotNullEnumeration());

		try
		{
			new ItemWithManyAttributes("someString", 5, 6l, 2.2, true, someItem, null);
			fail("should have thrown NotNullViolationException");
		}
		catch(NotNullViolationException e)
		{
			assertEquals(null, e.getItem());
			assertEquals(item.someNotNullEnumeration, e.getNotNullAttribute());
		}
	}
}
