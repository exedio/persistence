package com.exedio.cope.lib;

import com.exedio.cope.testmodel.AttributeItem;
import com.exedio.cope.testmodel.EmptyItem;


public class AttributeItemTest extends AttributeTest
{
	public void testSomeItem()
	{
		assertEquals(item.TYPE, item.someItem.getType());
		assertEquals(EmptyItem.TYPE, item.someItem.getTargetType());
		assertEquals(null, item.getSomeItem());
		item.setSomeItem(someItem);
		assertEquals(someItem, item.getSomeItem());

		assertContains(item,
				item.TYPE.search(Cope.equal(item.someItem, someItem)));
		assertContains(item2,
				item.TYPE.search(Cope.equal(item.someItem, null)));

		assertContains(someItem, null, search(item.someItem));
		assertContains(someItem, search(item.someItem, Cope.equal(item.someItem, someItem)));

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
			fail("should have thrown IntegrityViolationException");
		}
		catch(IntegrityViolationException e)
		{
			assertTrue(!EXTRA_WURST.equals(model.getDatabase().getClass().getName()));
			assertEquals(item.someNotNullItem, e.getAttribute());
			assertEquals(null/*TODO someItem*/, e.getItem());
		}
		catch(NestingRuntimeException e)
		{
			assertTrue(EXTRA_WURST.equals(model.getDatabase().getClass().getName()));
			assertEquals("Cannot delete or update a parent row: a foreign key constraint fails", e.getNestedCause().getMessage());
		}

		try
		{
			new AttributeItem("someString", 5, 6l, 2.2, true, null, AttributeItem.SomeEnumeration.enumValue1);
			fail("should have thrown NotNullViolationException");
		}
		catch(NotNullViolationException e)
		{
			assertEquals(null, e.getItem());
			assertEquals(item.someNotNullItem, e.getNotNullAttribute());
		}
	}


}
