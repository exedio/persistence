/*
 * Copyright (C) 2004-2015  exedio GmbH (www.exedio.com)
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

import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static com.exedio.cope.tojunit.Assert.list;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

public class RevisionTest
{
	@Test void testNumberNegative()
	{
		try
		{
			new Revision(-1, null, (String[])null);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("number must be greater zero, but was -1", e.getMessage());
		}
	}
	@Test void testNumberZero()
	{
		try
		{
			new Revision(0, null, (String[])null);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("number must be greater zero, but was 0", e.getMessage());
		}
	}
	@Test void testCommentNull()
	{
		try
		{
			new Revision(1, null, (String[])null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("comment", e.getMessage());
		}
	}
	@Test void testCommentEmpty()
	{
		try
		{
			new Revision(1, "", (String[])null);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("comment must not be empty", e.getMessage());
		}
	}
	@Test void testBodyNull()
	{
		try
		{
			new Revision(1, "some comment", (String[])null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("body", e.getMessage());
		}
	}
	@Test void testBodyEmpty()
	{
		try
		{
			new Revision(1, "some comment", new String[0]);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("body must not be empty", e.getMessage());
		}
	}
	@Test void testBodyElementNull()
	{
		try
		{
			new Revision(1, "some comment", "hallo", null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("body[1]", e.getMessage());
		}
	}
	@Test void testBodyElementEmpty()
	{
		try
		{
			new Revision(1, "some comment", "hallo", "");
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("body[1] must not be empty", e.getMessage());
		}
	}
	@Test void testSuccess()
	{
		final Revision m = new Revision(123, "test-comment", "sql1", "sql2");
		assertEquals(123, m.getNumber());
		assertEquals("test-comment", m.getComment());
		assertEqualsUnmodifiable(list("sql1", "sql2"), m.getBody());
		assertEquals("R123:test-comment", m.toString());
	}
	@Test void testCopy()
	{
		final String[] body = {"a", "b", "c"};
		final Revision r = new Revision(5, "comment", body);
		assertEquals(list("a", "b", "c"), r.getBody());

		body[0] = "x";
		assertEquals(list("a", "b", "c"), r.getBody());
	}
}
