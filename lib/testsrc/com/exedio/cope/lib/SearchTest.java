package com.exedio.cope.lib;

import java.util.Collection;

public class SearchTest extends DatabaseLibTest
{
	public void testUnmodifiableSearchResult()
			throws IntegrityViolationException
	{
		final EmptyItem someItem = new EmptyItem();
		final AttributeItem item;
		final AttributeItem item2;
		try
		{
			item = new AttributeItem("someString", 5, 6l, 2.2, true, someItem, AttributeItem.SomeEnumeration.enumValue1);
			item2 = new AttributeItem("someString2", 5, 6l, 2.2, false, someItem, AttributeItem.SomeEnumeration.enumValue2);
		}
		catch(NotNullViolationException e)
		{
			throw new SystemException(e);
		}
		item.setSomeNotNullInteger(0);
		final Collection searchResult = item.TYPE.search(Search.equal(item.someNotNullInteger, 0));
		assertEquals(set(item), toSet(searchResult));
		assertUnmodifiable(searchResult);
		
		assertEquals(set(item, item2), toSet(item.TYPE.search(null)));
		assertEquals(set(item, item2), toSet(
			Search.search(
				item.TYPE,
				Search.or(
					Search.equal(item.someNotNullString, "someString"),
					Search.equal(item.someNotNullString, "someString2")))));
		assertEquals(set(), toSet(
			Search.search(
				item.TYPE,
				Search.and(
					Search.equal(item.someNotNullString, "someString"),
					Search.equal(item.someNotNullString, "someString2")))));
		
		assertDelete(item);
		assertDelete(item2);
		assertDelete(someItem);
	}

	public void testIllegalSearch()
	{
		try
		{
			EmptyItem.TYPE.search(Search.equal(AttributeItem.someInteger, 0));
			fail("should have thrown RuntimeException");
		}
		catch(RuntimeException e)
		{
			assertEquals(
				"function someInteger{} belongs to type com.exedio.cope.lib.AttributeItem, which is not a from-type of the query: [com.exedio.cope.lib.EmptyItem]",
				e.getMessage());
		}
	}
	
}
