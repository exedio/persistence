
package com.exedio.cope.lib;


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
		dotestIllegalSearch();
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
	
	private void dotestContinuousPrimaryKeyGeneration()
	{
		final ItemWithoutAttributes item1 = new ItemWithoutAttributes();
		item1.TYPE.flushPK();
		final ItemWithoutAttributes item2 = new ItemWithoutAttributes();
		assertNotEquals(item1, item2);
	}

}
