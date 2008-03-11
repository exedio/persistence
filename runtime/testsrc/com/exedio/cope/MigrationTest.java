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

public class MigrationTest extends CopeAssert
{
	public void testMigration()
	{
		try
		{
			new Migration(-1, null, (String[])null);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("revision must be greater zero", e.getMessage());
		}
		try
		{
			new Migration(0, null, (String[])null);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("revision must be greater zero", e.getMessage());
		}
		try
		{
			new Migration(1, null, (String[])null);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals("comment must not be null", e.getMessage());
		}
		try
		{
			new Migration(1, "some comment", (String[])null);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals("body must not be null", e.getMessage());
		}
		try
		{
			new Migration(1, "some comment", new String[0]);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("body must not be empty", e.getMessage());
		}
		try
		{
			new Migration(1, "some comment", "hallo", null);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals("body must not be null, but was at index 1", e.getMessage());
		}
		
		final Migration m = new Migration(123, "test-comment", "sql1", "sql2");
		assertEquals(123, m.getRevision());
		assertEquals("test-comment", m.getComment());
		assertEqualsUnmodifiable(list("sql1", "sql2"), m.getBody());
		assertEquals("M123:test-comment", m.toString());
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
			assertEquals("migration revision must not be negative, but was -1", e.getMessage());
		}
		try
		{
			new Model(null, (Type[])null);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals("migrations must not be null", e.getMessage());
		}
		try
		{
			new Model(new Migration[]{new Migration(1, "migration1", "nonsensesql1"), null}, (Type[])null);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals("migration must not be null, but was at index 1", e.getMessage());
		}
		try
		{
			new Model(new Migration[]{
					new Migration(8, "migration8", "nonsensesql8"),
					new Migration(6, "migration6", "nonsensesql6"),
					}, (Type[])null);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("inconsistent migration revision at index 1, expected 7, but was 6", e.getMessage());
		}
	}
	
	public static void testParse() throws UnsupportedEncodingException
	{
		assertEquals(map("key1", "value1", "key2", "value2"), Migration.parse("#migrationlogv01\nkey1=value1\nkey2=value2".getBytes("latin1")));
		assertEquals(null, Migration.parse("migrationlogv01".getBytes("latin1")));
		assertEquals(null, Migration.parse("#migrationlogv0".getBytes("latin1")));
		assertEquals(null, Migration.parse("x#migrationlogv01".getBytes("latin1")));
		assertEquals(null, Migration.parse("".getBytes("latin1")));
		try
		{
			Migration.parse(null);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals(null, e.getMessage());
		}
	}
}
