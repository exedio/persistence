
package com.exedio.cope.lib;

public class FindByIDTest extends DatabaseLibTest
{
	private void assertFail(final String id, final String message)
	{
		try
		{
			Search.findByID(id);
			fail("should have thrown NoSuchIDException");
		}
		catch(NoSuchIDException e)
		{
			assertEquals(message, e.getMessage());
		}
	}

	public void testFindbyID()
	{
		assertFail("noDotInThisString", "no such id <noDotInThisString>, no dot in id");
		assertFail("noSuchType.x", "no such id <noSuchType.x>, no such type noSuchType");
		assertFail("com.exedio.cope.lib.ItemWithoutAttributes.x", "no such id <com.exedio.cope.lib.ItemWithoutAttributes.x>, wrong number format <x>");
		assertFail("com.exedio.cope.lib.ItemWithoutAttributes.92386591832651832659213865193456293456", "no such id <com.exedio.cope.lib.ItemWithoutAttributes.92386591832651832659213865193456293456>, wrong number format <92386591832651832659213865193456293456>");
		assertFail("com.exedio.cope.lib.ItemWithManyAttributes.-1", "no such id number <-1>, must be positive");
		assertFail("com.exedio.cope.lib.ItemWithManyAttributes.50", "no such id <com.exedio.cope.lib.ItemWithManyAttributes.50>, item <50> does not exist");
	}
	
}
