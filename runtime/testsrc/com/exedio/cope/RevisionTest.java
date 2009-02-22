/*
 * Copyright (C) 2004-2008  exedio GmbH (www.exedio.com)
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

import java.io.UnsupportedEncodingException;

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
			assertEquals("comment must not be null", e.getMessage());
		}
		try
		{
			new Revision(1, "some comment", (String[])null);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals("body must not be null", e.getMessage());
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
			assertEquals("body must not be null, but was at index 1", e.getMessage());
		}
		
		final Revision m = new Revision(123, "test-comment", "sql1", "sql2");
		assertEquals(123, m.getNumber());
		assertEquals("test-comment", m.getComment());
		assertEqualsUnmodifiable(list("sql1", "sql2"), m.getBody());
		assertEquals("R123:test-comment", m.toString());
	}
		
	public void testModel()
	{
		try
		{
			new Model(-1, (Type[])null);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("revision number must not be negative, but was -1", e.getMessage());
		}
		try
		{
			new Model(null, (Type[])null);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals("revisions must not be null", e.getMessage());
		}
		try
		{
			new Model(new Revision[]{}, (Type[])null);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("revisions must not be empty", e.getMessage());
		}
		try
		{
			new Model(new Revision[]{new Revision(1, "revision1", "nonsensesql1"), null}, (Type[])null);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals("revision must not be null, but was at index 1", e.getMessage());
		}
		try
		{
			new Model(new Revision[]{
					new Revision(8, "revision8", "nonsensesql8"),
					new Revision(6, "revision6", "nonsensesql6"),
					}, (Type[])null);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("inconsistent revision number at index 1, expected 7, but was 6", e.getMessage());
		}
	}
	
	public void testParse() throws UnsupportedEncodingException
	{
		assertEquals(map("key1", "value1", "key2", "value2"), RevisionInfo.parse("#migrationlogv01\nkey1=value1\nkey2=value2".getBytes("latin1")));
		assertEquals(null, RevisionInfo.parse("migrationlogv01".getBytes("latin1")));
		assertEquals(null, RevisionInfo.parse("#migrationlogv0".getBytes("latin1")));
		assertEquals(null, RevisionInfo.parse("x#migrationlogv01".getBytes("latin1")));
		assertEquals(null, RevisionInfo.parse("".getBytes("latin1")));
		try
		{
			RevisionInfo.parse(null);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals(null, e.getMessage());
		}
	}
}
