package com.exedio.cope.lib;

import java.util.List;


public class OrderByTest extends DatabaseLibTest
{

	protected EmptyItem someItem, someItem2;
	protected AttributeItem item1, item2, item3, item4, item5, item;

	public void setUp() throws Exception
	{
		super.setUp();
		someItem = new EmptyItem();
		someItem2 = new EmptyItem();
		item1 = new AttributeItem("someString9", 1, 4l, 2.1, true, someItem, AttributeItem.SomeEnumeration.enumValue1);
		item2 = new AttributeItem("someString8", 3, 5l, 2.4, true, someItem, AttributeItem.SomeEnumeration.enumValue2);
		item3 = new AttributeItem("someString7", 5, 7l, 2.2, false, someItem, AttributeItem.SomeEnumeration.enumValue3);
		item4 = new AttributeItem("someString6", 4, 6l, 2.5, false, someItem2, AttributeItem.SomeEnumeration.enumValue2);
		item5 = new AttributeItem("someString5", 2, 3l, 2.3, false, someItem2, AttributeItem.SomeEnumeration.enumValue3);
		item = item1;
	}
	
	public void testOrderBy()
	{
		assertOrder(list(item5, item4, item3, item2, item1), item.someNotNullString);
		assertOrder(list(item1, item5, item2, item4, item3), item.someNotNullInteger);
		assertOrder(list(item1, item5, item2, item4, item3), item.someNotNullInteger);
		assertOrder(list(item1, item3, item5, item2, item4), item.someNotNullDouble);
	}
	
	private void assertOrder(final List expectedOrder, final ObjectAttribute searchAttribute)
	{
		final Query query = new Query(item1.TYPE, null);
		query.setOrderBy(searchAttribute);
		assertEquals(expectedOrder, Search.search(query));
	}
	
	public void tearDown() throws Exception
	{
		item1.delete();
		item1 = null;
		item2.delete();
		item2 = null;
		item3.delete();
		item3 = null;
		item4.delete();
		item4 = null;
		item5.delete();
		item5 = null;
		someItem.delete();
		someItem = null;
		someItem2.delete();
		someItem2 = null;
		super.tearDown();
	}
}