package com.exedio.cope.lib;


public class AttributeStringTest extends AttributeTest
{
	public void testSomeString()
	{
		assertEquals(item.TYPE, item.someString.getType());
		assertEquals(item.TYPE, item.someStringUpperCase.getType());
		assertEquals(null, item.getSomeString());
		assertEquals(null, item.getSomeStringUpperCase());
		item.setSomeString("someString");
		assertEquals("someString", item.getSomeString());
		assertEquals("SOMESTRING", item.getSomeStringUpperCase());
		assertEquals(
			set(item),
			toSet(
				Search.search(
					item.TYPE,
					Search.equal(item.someString, "someString"))));
		assertEquals(
			set(item2),
			toSet(
				Search.search(
					item.TYPE,
					Search.notEqual(item.someString, "someString"))));
		assertEquals(
			set(),
			toSet(
				Search.search(
					item.TYPE,
					Search.equal(item.someString, "SOMESTRING"))));
		assertEquals(
			set(item),
			toSet(
				Search.search(
					item.TYPE,
					Search.equal(item.someStringUpperCase, "SOMESTRING"))));
		assertEquals(
			set(item2),
			toSet(
				Search.search(
					item.TYPE,
					Search.notEqual(item.someStringUpperCase, "SOMESTRING"))));
		assertEquals(
			set(),
			toSet(
				Search.search(
					item.TYPE,
					Search.equal(item.someStringUpperCase, "someString"))));
		item.passivate();
		assertEquals("someString", item.getSomeString());
		assertEquals("SOMESTRING", item.getSomeStringUpperCase());
		item.setSomeString(null);
		assertEquals(null, item.getSomeString());
		assertEquals(null, item.getSomeStringUpperCase());
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
