
package com.exedio.cope.lib;

public class Id2PkTest extends AbstractLibTest
{
	private void assertIdPk(final long id, final int pk)
			throws NoSuchIDException
	{
		assertEquals(pk, Search.id2pk(id));
		assertEquals(id, Search.pk2id(pk));
	}
	
	private void assertIDFails(final long id, final String detail)
	{
		try
		{
			Search.id2pk(id);
			fail("should have thrown NoSuchIDException");
		}
		catch(NoSuchIDException e)
		{
			assertEquals("no such id number <"+id+">, "+detail, e.getMessage());
		}
	}

	public void testId2Pk()
			throws NoSuchIDException
	{
		assertIdPk(0, 0);
		assertIdPk(1, -1);
		assertIdPk(2, 1);
		assertIdPk(3, -2);
		assertIdPk(4, 2);

		assertIDFails(-1, "must be positive");
		assertIDFails(Long.MIN_VALUE, "must be positive");

		assertIdPk(4294967291l, -2147483646); // 2^32 - 5
		assertIdPk(4294967292l, 2147483646); // 2^32 - 4
		assertIdPk(4294967293l, -2147483647); // 2^32 - 3
		assertIdPk(4294967294l, 2147483647); // 2^32 - 2
		assertIDFails(4294967295l, "is a NOT_A_TYPE"); // 2^32 - 1
		assertIDFails(4294967296l, "does not fit in 32 bit"); // 2^32
		assertIDFails(4294967297l, "does not fit in 32 bit"); // 2^32 + 1
		assertIDFails(Long.MAX_VALUE, "does not fit in 32 bit");
		
		try
		{
			Search.pk2id(Type.NOT_A_PK);
			fail("should have thrown RuntimeException");
		}
		catch(RuntimeException e)
		{
			assertEquals("not a pk", e.getMessage());
		}
	}
	
}
