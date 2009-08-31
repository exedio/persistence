/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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

import com.exedio.cope.junit.CopeAssert;

public class RevisionTest extends CopeAssert
{
	public void testRevision()
	{
		try
		{
			new Revision(-1, null, (String[])null);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("number must be greater zero", e.getMessage());
		}
		try
		{
			new Revision(0, null, (String[])null);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("number must be greater zero", e.getMessage());
		}
		try
		{
			new Revision(1, null, (String[])null);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals("comment", e.getMessage());
		}
		try
		{
			new Revision(1, "some comment", (String[])null);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals("body", e.getMessage());
		}
		try
		{
			new Revision(1, "some comment", new String[0]);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("body must not be empty", e.getMessage());
		}
		try
		{
			new Revision(1, "some comment", "hallo", null);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals("body[1]", e.getMessage());
		}
		
		final Revision m = new Revision(123, "test-comment", "sql1", "sql2");
		assertEquals(123, m.getNumber());
		assertEquals("test-comment", m.getComment());
		assertEqualsUnmodifiable(list("sql1", "sql2"), m.getBody());
		assertEquals("R123:test-comment", m.toString());
	}
	
	public void testCopy()
	{
		final String[] body = new String[]{"a", "b", "c"};
		final Revision r = new Revision(5, "comment", body);
		assertEquals(list("a", "b", "c"), r.getBody());
		
		body[0] = "x";
		assertEquals(list("a", "b", "c"), r.getBody());
	}
}
