package com.exedio.cope.lib;

import java.util.Collection;

public class SearchTest extends DatabaseLibTest
{
	public void testUnmodifiableSearchResult()
			throws IntegrityViolationException
	{
		final EmptyItem someItem = new EmptyItem();
		final ItemWithManyAttributes item;
		final ItemWithManyAttributes item2;
		try
		{
			item = new ItemWithManyAttributes("someString", 5, 6l, 2.2, true, someItem, ItemWithManyAttributes.SomeEnumeration.enumValue1);
			item2 = new ItemWithManyAttributes("someString2", 5, 6l, 2.2, false, someItem, ItemWithManyAttributes.SomeEnumeration.enumValue2);
		}
		catch(NotNullViolationException e)
		{
			throw new SystemException(e);
		}
		item.setSomeNotNullInteger(0);
		final Collection searchResult = Search.search(item.TYPE, Search.equal(item.someNotNullInteger, 0));
		assertEquals(set(item), toSet(searchResult));
		assertUnmodifiable(searchResult);
		
		assertEquals(set(item, item2), toSet(Search.search(item.TYPE, null)));
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
			Search.search(EmptyItem.TYPE, Search.equal(ItemWithManyAttributes.someInteger, 0));
			fail("should have thrown RuntimeException");
		}
		catch(RuntimeException e)
		{
			assertEquals(
				"attribute someInteger{} belongs to type com.exedio.cope.lib.ItemWithManyAttributes, which is not a from-type of the query: [com.exedio.cope.lib.EmptyItem]",
				e.getMessage());
		}
	}
	
}
