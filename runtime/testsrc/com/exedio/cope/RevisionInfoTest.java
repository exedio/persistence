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
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import com.exedio.cope.RevisionInfoRevise.Body;
import com.exedio.cope.junit.CopeAssert;

public class RevisionInfoTest extends CopeAssert
{
	private static final Date DATE = new Date(2874526134l);
	
	public void testRevise()
	{
		final HashMap<String, String> env = new HashMap<String, String>();
		final RevisionInfoRevise i =
			new RevisionInfoRevise(5, DATE, env, "comment5",
					new Body("sql5.0", 55, 23),
					new Body("sql5.1", 56, 24));
		assertEquals(5, i.getNumber());
		assertEquals(DATE, i.getDate());
		assertEqualsUnmodifiable(env, i.getEnvironment());
		assertEquals("comment5", i.getComment());
		assertUnmodifiable(i.getBody());
		final Iterator<Body> it = i.getBody().iterator();
		{
			final Body b = it.next();
			assertEquals("sql5.0", b.getSQL());
			assertEquals(55, b.getRows());
			assertEquals(23, b.getElapsed());
		}
		{
			final Body b = it.next();
			assertEquals("sql5.1", b.getSQL());
			assertEquals(56, b.getRows());
			assertEquals(24, b.getElapsed());
		}
		assertFalse(it.hasNext());
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
