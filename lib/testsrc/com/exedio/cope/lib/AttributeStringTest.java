package com.exedio.cope.lib;

import com.exedio.cope.testmodel.AttributeItem;


public class AttributeStringTest extends AttributeTest
{
	public void testSomeString()
	{
		assertEquals(item.TYPE, item.someString.getType());
		assertEquals(item.TYPE, item.someStringUpperCase.getType());
		assertEquals(null, item.getSomeString());
		assertEquals(null, item.getSomeStringUpperCase());
		assertEquals(null, item.getSomeStringLength());
		item.setSomeString("someString");
		assertEquals("someString", item.getSomeString());
		assertEquals("SOMESTRING", item.getSomeStringUpperCase());
		assertEquals(new Integer("someString".length()), item.getSomeStringLength());
		assertContains(item,
			item.TYPE.search(Cope.equal(item.someString, "someString")));
		assertContains(item2,
			item.TYPE.search(Cope.notEqual(item.someString, "someString")));
		assertContains(
			item.TYPE.search(Cope.equal(item.someString, "SOMESTRING")));
		assertContains(item,
			item.TYPE.search(Cope.equal(item.someStringUpperCase, "SOMESTRING")));
		assertContains(item2,
			item.TYPE.search(Cope.notEqual(item.someStringUpperCase, "SOMESTRING")));
		assertContains(
			item.TYPE.search(Cope.equal(item.someStringUpperCase, "someString")));
		assertContains(item,
				item.TYPE.search(Cope.equal(item.someStringLength, "someString".length())));
		assertContains(item2,
			item.TYPE.search(Cope.notEqual(item.someStringLength, "someString".length())));
		assertContains(
			item.TYPE.search(Cope.equal(item.someStringLength, "someString".length()+1)));

		assertContains("someString", null, search(item.someString));
		assertContains("someString", search(item.someString, Cope.equal(item.someString, "someString")));
		// TODO allow functions for select
		//assertContains("SOMESTRING", search(item.someStringUpperCase, Cope.equal(item.someString, "someString")));

		item.passivate();
		assertEquals("someString", item.getSomeString());
		assertEquals("SOMESTRING", item.getSomeStringUpperCase());
		assertEquals(new Integer("someString".length()), item.getSomeStringLength());
		item.setSomeString(null);
		assertEquals(null, item.getSomeString());
		assertEquals(null, item.getSomeStringUpperCase());
		assertEquals(null, item.getSomeStringLength());
	}

	public void testSomeNotNullString()
		throws NotNullViolationException
	{
		assertEquals(item.TYPE, item.someNotNullString.getType());
		assertEquals("someString", item.getSomeNotNullString());

		item.setSomeNotNullString("someOtherString");
		assertEquals("someOtherString", item.getSomeNotNullString());

		try
		{
			item.setSomeNotNullString(null);
			fail("should have thrown NotNullViolationException");
		}
		catch (NotNullViolationException e)
		{
			assertEquals(item, e.getItem());
			assertEquals(item.someNotNullString, e.getNotNullAttribute());
		}

		try
		{
			new AttributeItem(null, 5, 6l, 2.2, true, someItem, AttributeItem.SomeEnumeration.enumValue1);
			fail("should have thrown NotNullViolationException");
		}
		catch(NotNullViolationException e)
		{
			assertEquals(null, e.getItem());
			assertEquals(item.someNotNullString, e.getNotNullAttribute());
		}
	}

}
