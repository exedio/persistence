
package com.exedio.cope.lib;

public class LiteralConditionTest extends DatabaseLibTest
{
	ItemWithoutAttributes someItem;
	ItemWithManyAttributes item1;
	ItemWithManyAttributes item2;
	ItemWithManyAttributes item3;
	ItemWithManyAttributes item4;
	ItemWithManyAttributes item5;

	public void setUp() throws Exception
	{
		super.setUp();
		someItem = new ItemWithoutAttributes();
		item1 = new ItemWithManyAttributes("string1", 1, true, someItem, ItemWithManyAttributes.SomeEnumeration.enumValue1);
		item2 = new ItemWithManyAttributes("string2", 2, true, someItem, ItemWithManyAttributes.SomeEnumeration.enumValue1);
		item3 = new ItemWithManyAttributes("string3", 3, true, someItem, ItemWithManyAttributes.SomeEnumeration.enumValue2);
		item4 = new ItemWithManyAttributes("string4", 4, true, someItem, ItemWithManyAttributes.SomeEnumeration.enumValue3);
		item5 = new ItemWithManyAttributes("string5", 5, true, someItem, ItemWithManyAttributes.SomeEnumeration.enumValue3);
	}
	
	public void tearDown() throws Exception
	{
		item1.delete();
		item2.delete();
		item3.delete();
		item4.delete();
		item5.delete();
		someItem.delete();
		super.tearDown();
	}

	public void testLiteralConditions()
	{
		assertEquals(set(item1, item2),
			toSet(Search.search(item1.TYPE, Search.less(item1.someNotNullString, "string3"))));
		assertEquals(set(item1, item2),
			toSet(Search.search(item1.TYPE, Search.less(item1.someNotNullInteger, 3))));
		assertEquals(set(item1, item2),
			toSet(Search.search(item1.TYPE, Search.less(item1.someNotNullEnumeration, ItemWithManyAttributes.SomeEnumeration.enumValue2))));
	}

}
