package com.exedio.cope.lib;


public class AttributeQualifiedTest extends AttributesTest
{
	public void testSomeQualifiedAttribute()
			throws IntegrityViolationException
	{
		assertEquals(item.TYPE, item.someQualifiedString.getType());
		final EmptyItem someItem2 = new EmptyItem();
		assertEquals(null, item.getSomeQualifiedString(someItem));
		assertEquals(null, item.getSomeQualifiedString(someItem2));
		item.setSomeQualifiedString(someItem, "someQualifiedValue");
		assertEquals("someQualifiedValue", item.getSomeQualifiedString(someItem));
		assertEquals("someQualifiedValue" /*null TODO*/, item.getSomeQualifiedString(someItem2));
		item.passivate();
		assertEquals("someQualifiedValue", item.getSomeQualifiedString(someItem));
		assertEquals("someQualifiedValue" /*null TODO*/, item.getSomeQualifiedString(someItem2));
		item.setSomeQualifiedString(someItem, null);
		assertEquals(null, item.getSomeQualifiedString(someItem));
		assertEquals(null, item.getSomeQualifiedString(someItem2));

		assertDelete(someItem2);
	}
}
