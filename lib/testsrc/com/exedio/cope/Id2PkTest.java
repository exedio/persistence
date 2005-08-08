/*
 * Copyright (C) 2004-2005  exedio GmbH (www.exedio.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.exedio.cope;

import junit.framework.TestCase;

public class Id2PkTest extends TestCase
{
	final PrimaryKeyIterator i = new PrimaryKeyIterator(null);
	
	private void assertIdPk(final long id, final int pk)
			throws NoSuchIDException
	{
		assertEquals(pk, i.id2pk(id));
		assertEquals(id, i.pk2id(pk));
	}
	
	private void assertIDFails(final long id, final String detail)
	{
		try
		{
			i.id2pk(id);
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
		assertIDFails(4294967295l, "is a NOT_A_PK"); // 2^32 - 1
		assertIDFails(4294967296l, "does not fit in 32 bit"); // 2^32
		assertIDFails(4294967297l, "does not fit in 32 bit"); // 2^32 + 1
		assertIDFails(Long.MAX_VALUE, "does not fit in 32 bit");
		
		try
		{
			i.pk2id(Type.NOT_A_PK);
			fail("should have thrown RuntimeException");
		}
		catch(RuntimeException e)
		{
			assertEquals("not a pk", e.getMessage());
		}
	}
	
}
