
package com.exedio.cope.lib;

public class FindByIDTest extends DatabaseLibTest
{
	public void testFindbyID()
	{
		try
		{
			Search.findByID("noDotInThisString");
			fail("should have thrown NoSuchIDException");
		}
		catch(NoSuchIDException e)
		{
			assertEquals("no such id <noDotInThisString>, no dot in id", e.getMessage());
		}

		try
		{
			Search.findByID("noSuchType.x");
			fail("should have thrown NoSuchIDException");
		}
		catch(NoSuchIDException e)
		{
			assertEquals("no such id <noSuchType.x>, no such type noSuchType", e.getMessage());
		}

		try
		{
			Search.findByID("com.exedio.cope.lib.ItemWithoutAttributes.x");
			fail("should have thrown NoSuchIDException");
		}
		catch(NoSuchIDException e)
		{
			assertEquals("no such id <com.exedio.cope.lib.ItemWithoutAttributes.x>, wrong number format <x>", e.getMessage());
		}

		assertIDNotWithin(Long.MIN_VALUE);
		assertIDNotWithin(Long.MAX_VALUE);

		assertIDNotWithin(4294967295l); // 2^32 - 1 // TODO non-sense, should be assertIDNotFound
		assertIDNotWithin(4294967296l); // 2^32

		assertIDNotWithin(-4294967295l); // -(2^32 - 1) // TODO non-sense, should be assertIDNotFound
		assertIDNotWithin(-4294967296l); // -(2^32) equivalent to Type.NOT_A_PK
		assertIDNotWithin(-4294967297l); // -(2^32 + 1)
	}
	
	private void assertIDNotWithin(final long id)
	{
		try
		{
			Search.findByID("com.exedio.cope.lib.ItemWithManyAttributes."+id);
			fail("should have thrown NoSuchIDException");
		}
		catch(NoSuchIDException e)
		{
			assertEquals("number is not within +/- (2^32)-1: <"+id+">", e.getMessage());
		}
	}

	private void assertIDNotFound(final long id)
	{
		try
		{
			Search.findByID("com.exedio.cope.lib.ItemWithManyAttributes."+id);
			fail("should have thrown NoSuchIDException");
		}
		catch(NoSuchIDException e)
		{
			assertEquals("id not found: <"+id+">", e.getMessage());
		}
	}

}
