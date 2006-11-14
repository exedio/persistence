/*
 * Copyright (C) 2004-2006  exedio GmbH (www.exedio.com)
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

public abstract class PkSourceAbstractTest extends TestCase
{
	final PkSource i;
	
	PkSourceAbstractTest(final PkSource i)
	{
		this.i = i;
	}
	
	void assertIdPk(final long id, final int pk)
			throws NoSuchIDException
	{
		assertEquals(pk, i.id2pk(id, "idString"+id));
		assertEquals(id, i.pk2id(pk));
	}
	
	void assertIDFails(final long id, final String detail)
	{
		try
		{
			i.id2pk(id, "idString"+id);
			fail();
		}
		catch(NoSuchIDException e)
		{
			assertEquals("no such id <idString"+id+">, "+detail, e.getMessage());
		}
	}
	
	public void testPk2Id()
	{
		try
		{
			i.pk2id(Type.NOT_A_PK);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("not a pk", e.getMessage());
		}
	}
}
