package com.exedio.cope.lib;


public class AttributeItemTest extends AttributesTest
{
	public void testSomeItem()
	{
		assertEquals(item.TYPE, item.someItem.getType());
		assertEquals(EmptyItem.TYPE, item.someItem.getTargetType());
		assertEquals(null, item.getSomeItem());
		item.setSomeItem(someItem);
		assertEquals(someItem, item.getSomeItem());
		item.passivate();
		assertEquals(someItem, item.getSomeItem());
		item.setSomeItem(null);
		assertEquals(null, item.getSomeItem());
	}

	public void testSomeNotNullItem()
		throws NotNullViolationException
	{
		assertEquals(item.TYPE, item.someNotNullItem.getType());
		assertEquals(
			EmptyItem.TYPE,
			item.someNotNullItem.getTargetType());
		assertEquals(someItem, item.getSomeNotNullItem());

		item.setSomeNotNullItem(someItem2);
		assertEquals(someItem2, item.getSomeNotNullItem());

		item.passivate();
		assertEquals(someItem2, item.getSomeNotNullItem());
		try
		{
			item.setSomeNotNullItem(null);
			fail("should have thrown NotNullViolationException");
		}
		catch (NotNullViolationException e)
		{
			assertEquals(item, e.getItem());
			assertEquals(item.someNotNullItem, e.getNotNullAttribute());
		}
		assertEquals(someItem2, item.getSomeNotNullItem());
		try
		{
			someItem2.delete();
		}
		catch(IntegrityViolationException e)
		{
			assertEquals(item.someNotNullItem, e.getAttribute());
			assertEquals(null/*TODO someItem*/, e.getItem());
		}

		try
		{
			new ItemWithManyAttributes("someString", 5, 6l, 2.2, true, null, ItemWithManyAttributes.SomeEnumeration.enumValue1);
			fail("should have thrown NotNullViolationException");
		}
		catch(NotNullViolationException e)
		{
			assertEquals(null, e.getItem());
			assertEquals(item.someNotNullItem, e.getNotNullAttribute());
		}
	}


}
