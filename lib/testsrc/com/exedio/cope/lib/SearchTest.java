package com.exedio.cope.lib;

import java.util.Collection;

import com.exedio.cope.testmodel.AttributeItem;
import com.exedio.cope.testmodel.EmptyItem;

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
		final Collection searchResult = item.TYPE.search(Cope.equal(item.someNotNullInteger, 0));
		assertContains(item, searchResult);
		assertUnmodifiable(searchResult);
		
		assertContains(item, item2, item.TYPE.search(null));
		assertContains(item, item2, 
			item.TYPE.search(
				Cope.or(
					Cope.equal(item.someNotNullString, "someString"),
					Cope.equal(item.someNotNullString, "someString2"))));
		assertContains(
			item.TYPE.search(
				Cope.and(
					Cope.equal(item.someNotNullString, "someString"),
					Cope.equal(item.someNotNullString, "someString2"))));
		
		assertDelete(item);
		assertDelete(item2);
		assertDelete(someItem);
	}

	public void testIllegalSearch()
	{
		try
		{
			EmptyItem.TYPE.search(Cope.equal(AttributeItem.someInteger, 0));
			fail("should have thrown RuntimeException");
		}
		catch(RuntimeException e)
		{
			assertEquals(
				"function someInteger{} belongs to type "+AttributeItem.class.getName()+", which is not a from-type of the query: ["+EmptyItem.class.getName()+"]",
				e.getMessage());
		}
	}
	
}
