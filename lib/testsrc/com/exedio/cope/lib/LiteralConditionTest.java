
package com.exedio.cope.lib;

public class LiteralConditionTest extends DatabaseLibTest
{
	EmptyItem someItem;
	AttributeItem item1;
	AttributeItem item2;
	AttributeItem item3;
	AttributeItem item4;
	AttributeItem item5;

	public void setUp() throws Exception
	{
		super.setUp();
		someItem = new EmptyItem();
		item1 = new AttributeItem("string1", 1, 11l, 2.1, true, someItem, AttributeItem.SomeEnumeration.enumValue1);
		item2 = new AttributeItem("string2", 2, 12l, 2.2, true, someItem, AttributeItem.SomeEnumeration.enumValue1);
		item3 = new AttributeItem("string3", 3, 13l, 2.3, true, someItem, AttributeItem.SomeEnumeration.enumValue2);
		item4 = new AttributeItem("string4", 4, 14l, 2.4, true, someItem, AttributeItem.SomeEnumeration.enumValue3);
		item5 = new AttributeItem("string5", 5, 15l, 2.5, true, someItem, AttributeItem.SomeEnumeration.enumValue3);
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
		// less
		assertEquals(set(item1, item2),
			toSet(Search.search(item1.TYPE, Search.less(item1.someNotNullString, "string3"))));
		assertEquals(set(item1, item2),
			toSet(Search.search(item1.TYPE, Search.less(item1.someNotNullInteger, 3))));
		assertEquals(set(item1, item2),
			toSet(Search.search(item1.TYPE, Search.less(item1.someNotNullLong, 13l))));
		assertEquals(set(item1, item2),
			toSet(Search.search(item1.TYPE, Search.less(item1.someNotNullDouble, 2.3))));
		assertEquals(set(item1, item2),
			toSet(Search.search(item1.TYPE, Search.less(item1.someNotNullEnumeration, AttributeItem.SomeEnumeration.enumValue2))));

		// less or equal
		assertEquals(set(item1, item2, item3),
			toSet(Search.search(item1.TYPE, Search.lessOrEqual(item1.someNotNullString, "string3"))));
		assertEquals(set(item1, item2, item3),
			toSet(Search.search(item1.TYPE, Search.lessOrEqual(item1.someNotNullInteger, 3))));
		assertEquals(set(item1, item2, item3),
			toSet(Search.search(item1.TYPE, Search.lessOrEqual(item1.someNotNullLong, 13l))));
		assertEquals(set(item1, item2, item3),
			toSet(Search.search(item1.TYPE, Search.lessOrEqual(item1.someNotNullDouble, 2.3))));
		assertEquals(set(item1, item2, item3),
			toSet(Search.search(item1.TYPE, Search.lessOrEqual(item1.someNotNullEnumeration, AttributeItem.SomeEnumeration.enumValue2))));

		// greater
		assertEquals(set(item4, item5),
			toSet(Search.search(item1.TYPE, Search.greater(item1.someNotNullString, "string3"))));
		assertEquals(set(item4, item5),
			toSet(Search.search(item1.TYPE, Search.greater(item1.someNotNullInteger, 3))));
		assertEquals(set(item4, item5),
			toSet(Search.search(item1.TYPE, Search.greater(item1.someNotNullLong, 13l))));
		assertEquals(set(item4, item5),
			toSet(Search.search(item1.TYPE, Search.greater(item1.someNotNullDouble, 2.3))));
		assertEquals(set(item4, item5),
			toSet(Search.search(item1.TYPE, Search.greater(item1.someNotNullEnumeration, AttributeItem.SomeEnumeration.enumValue2))));

		// greater or equal
		assertEquals(set(item3, item4, item5),
			toSet(Search.search(item1.TYPE, Search.greaterOrEqual(item1.someNotNullString, "string3"))));
		assertEquals(set(item3, item4, item5),
			toSet(Search.search(item1.TYPE, Search.greaterOrEqual(item1.someNotNullInteger, 3))));
		assertEquals(set(item3, item4, item5),
			toSet(Search.search(item1.TYPE, Search.greaterOrEqual(item1.someNotNullLong, 13l))));
		assertEquals(set(item3, item4, item5),
			toSet(Search.search(item1.TYPE, Search.greaterOrEqual(item1.someNotNullDouble, 2.3))));
		assertEquals(set(item3, item4, item5),
			toSet(Search.search(item1.TYPE, Search.greaterOrEqual(item1.someNotNullEnumeration, AttributeItem.SomeEnumeration.enumValue2))));

	}

}
