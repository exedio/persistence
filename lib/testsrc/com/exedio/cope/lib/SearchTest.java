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
			throw new NestingRuntimeException(e);
		}
		item.setSomeNotNullInteger(0);
		final Collection searchResult = item.TYPE.search(Search.equal(item.someNotNullInteger, 0));
		assertContains(item, searchResult);
		assertUnmodifiable(searchResult);
		
		assertContains(item, item2, item.TYPE.search(null));
		assertContains(item, item2, 
			item.TYPE.search(
				Search.or(
					Search.equal(item.someNotNullString, "someString"),
					Search.equal(item.someNotNullString, "someString2"))));
		assertContains(
			item.TYPE.search(
				Search.and(
					Search.equal(item.someNotNullString, "someString"),
					Search.equal(item.someNotNullString, "someString2"))));
		
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
