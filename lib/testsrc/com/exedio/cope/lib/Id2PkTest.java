
package com.exedio.cope.lib;

public class Id2PkTest extends AbstractLibTest // TODO inherit from a super class, that doesn't initializes the database
{
	public void testId2Pk()
	{
		assertIdPk(0, 0);
		assertIdPk(1, -1);
		assertIdPk(2, 1);
		assertIdPk(3, -2);
		assertIdPk(4, 2);
	}
	
	private void assertIdPk(final long id, final int pk)
	{
		assertEquals(pk, Search.id2pk(id));
		assertEquals(id, Search.pk2id(pk));
	}

}
