package com.exedio.cope.lib;

import java.util.Collection;

public class SearchTest extends DatabaseLibTest
{
	public void testUnmodifiableSearchResult()
			throws IntegrityViolationException
	{
		final ItemWithoutAttributes someItem = new ItemWithoutAttributes();
		final ItemWithManyAttributes item;
		final ItemWithManyAttributes item2;
		try
		{
			item = new ItemWithManyAttributes("someString", 5, true, someItem, ItemWithManyAttributes.SomeEnumeration.enumValue1);
			item2 = new ItemWithManyAttributes("someString", 5, false, someItem, ItemWithManyAttributes.SomeEnumeration.enumValue2);
		}
		catch(NotNullViolationException e)
		{
			throw new SystemException(e);
		}
		item.setSomeNotNullInteger(0);
		final Collection searchResult = Search.search(item.TYPE, Search.equal(item.someNotNullInteger, 0));
		assertEquals(set(item), toSet(searchResult));
		assertUnmodifiable(searchResult);
		
		assertDelete(item);
		assertDelete(item2);
		assertDelete(someItem);
	}

	public void testIllegalSearch()
	{
		try
		{
			Search.search(ItemWithoutAttributes.TYPE, Search.equal(ItemWithManyAttributes.someInteger, 0));
			fail("should have thrown RuntimeException");
		}
		catch(RuntimeException e)
		{
			assertEquals(
				"attribute someInteger{} belongs to type com.exedio.cope.lib.ItemWithManyAttributes, which is not a from-type of the query: [com.exedio.cope.lib.ItemWithoutAttributes]",
				e.getMessage());
		}
	}
	
}
