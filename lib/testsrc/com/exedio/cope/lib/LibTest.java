
package com.exedio.cope.lib;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

public class LibTest extends AbstractLibTest
{
	
	public LibTest()
	{}
	
	public void testLib()
	{
		// BEWARE:
		// if something does not compile,
		// it may be an error in the 
		// instrumentor as well.
		
		dotestItemWithManyAttributes();
		dotestContinuousPrimaryKeyGeneration();
	}


	private void dotestItemWithManyAttributes()
	{
		final ItemWithoutAttributes someItem = new ItemWithoutAttributes();
		final ItemWithManyAttributes item;
		try
		{
			item = new ItemWithManyAttributes("someString", 5, true, someItem);
		}
		catch(NotNullViolationException e)
		{
			throw new SystemException(e);
		}
		dotestUnmodifiableSearchResult(item);
		dotestIllegalSearch();
	}

	private void dotestUnmodifiableSearchResult(final ItemWithManyAttributes item)
	{
		item.setSomeNotNullInteger(0);
		assertUnmodifiable(Search.search(item.TYPE, Search.equal(item.someNotNullInteger, 0)));
	}
	
	private void dotestIllegalSearch()
	{
		try
		{
			Search.search(ItemWithoutAttributes.TYPE, Search.equal(ItemWithManyAttributes.someInteger, 0));
			fail("should have thrown RuntimeException");
		}
		catch(RuntimeException e)
		{
			assertEquals(
				"attribute someInteger{} belongs to type com.exedio.cope.lib.ItemWithManyAttributes, which is the type of the query: com.exedio.cope.lib.ItemWithoutAttributes",
				e.getMessage());
		}
	}
	
	private void assertUnmodifiable(final Collection c)
	{
		try
		{
			c.add(new Object());
			fail("should have thrown UnsupportedOperationException");
		}
		catch(UnsupportedOperationException e) {}
		try
		{
			c.addAll(Collections.EMPTY_LIST);
			fail("should have thrown UnsupportedOperationException");
		}
		catch(UnsupportedOperationException e) {}
		try
		{
			c.clear();
			fail("should have thrown UnsupportedOperationException");
		}
		catch(UnsupportedOperationException e) {}
		try
		{
			c.remove(new Object());
			fail("should have thrown UnsupportedOperationException");
		}
		catch(UnsupportedOperationException e) {}
		try
		{
			c.removeAll(Collections.EMPTY_LIST);
			fail("should have thrown UnsupportedOperationException");
		}
		catch(UnsupportedOperationException e) {}
		try
		{
			c.retainAll(Collections.EMPTY_LIST);
			fail("should have thrown UnsupportedOperationException");
		}
		catch(UnsupportedOperationException e) {}

		final Iterator iterator = c.iterator();
		try
		{
			iterator.remove();
			fail("should have thrown UnsupportedOperationException");
		}
		catch(UnsupportedOperationException e) {}
	}


	private void dotestContinuousPrimaryKeyGeneration()
	{
		final ItemWithoutAttributes item1 = new ItemWithoutAttributes();
		item1.TYPE.flushPK();
		final ItemWithoutAttributes item2 = new ItemWithoutAttributes();
		assertNotEquals(item1, item2);
	}

}
