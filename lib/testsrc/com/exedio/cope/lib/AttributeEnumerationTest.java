package com.exedio.cope.lib;

import com.exedio.cope.testmodel.AttributeItem;


public class AttributeEnumerationTest extends AttributeTest
{
	public void testSomeEnumeration()
	{
		assertEquals(null, item.getSomeEnumeration());
		item.setSomeEnumeration(AttributeItem.SomeEnumeration.enumValue1);
		assertEquals(
			AttributeItem.SomeEnumeration.enumValue1,
			item.getSomeEnumeration());
		item.setSomeEnumeration(
			AttributeItem.SomeEnumeration.enumValue2);
		assertEquals(
			AttributeItem.SomeEnumeration.enumValue2,
			item.getSomeEnumeration());
		item.passivate();
		assertEquals(
			AttributeItem.SomeEnumeration.enumValue2,
			item.getSomeEnumeration());
		item.setSomeEnumeration(null);
		assertEquals(null, item.getSomeEnumeration());
	}

	public void testNotNullSomeEnumeration()
			throws NotNullViolationException
	{
		assertEquals(AttributeItem.SomeEnumeration.enumValue1, item.getSomeNotNullEnumeration());
		item.setSomeNotNullEnumeration(AttributeItem.SomeEnumeration.enumValue2);
		assertEquals(
			AttributeItem.SomeEnumeration.enumValue2,
			item.getSomeNotNullEnumeration());
		item.setSomeNotNullEnumeration(
			AttributeItem.SomeEnumeration.enumValue3);
		assertEquals(
			AttributeItem.SomeEnumeration.enumValue3,
			item.getSomeNotNullEnumeration());
		item.passivate();
		assertEquals(
			AttributeItem.SomeEnumeration.enumValue3,
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
			AttributeItem.SomeEnumeration.enumValue3,
			item.getSomeNotNullEnumeration());

		try
		{
			new AttributeItem("someString", 5, 6l, 2.2, true, someItem, null);
			fail("should have thrown NotNullViolationException");
		}
		catch(NotNullViolationException e)
		{
			assertEquals(null, e.getItem());
			assertEquals(item.someNotNullEnumeration, e.getNotNullAttribute());
		}
	}
}
