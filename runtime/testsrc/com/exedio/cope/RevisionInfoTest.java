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
import java.util.Properties;
import java.util.TreeMap;

import com.exedio.cope.RevisionInfoRevise.Body;
import com.exedio.cope.junit.CopeAssert;

public class RevisionInfoTest extends CopeAssert
{
	private static final Date DATE = new Date(2874526134l);
	private static final String DATE_STRING = "1970/02/03 06:28:46.134";
	
	private HashMap<String, String> env = null;
	
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		env = new HashMap<String, String>();
		env.put("env1Key", "env1Value");
		env.put("env2Key", "env2Value");
		env.put("env3Key", "env3Value");
	}
	
	public void testRevise()
	{
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
		
		assertEquals(map(
				"revision", "5",
				"dateUTC", DATE_STRING,
				"env1Key", "env1Value",
				"env2Key", "env2Value",
				"env3Key", "env3Value",
				"comment", "comment5",
				"body0.sql", "sql5.0",
				"body0.rows", "55",
				"body0.elapsed", "23",
				"body1.sql", "sql5.1",
				"body1.rows", "56",
				"body1.elapsed", "24"),
				reparse(i));
	}
	
	public void testCreate()
	{
		final Date before = new Date();
		final RevisionInfoCreate i =
			new RevisionInfoCreate(5, env);
		final Date after = new Date();
		assertEquals(5, i.getNumber());
		assertWithin(before, after, i.getDate());
		assertEqualsUnmodifiable(env, i.getEnvironment());
	}
	
	public void testMutex()
	{
		final RevisionInfoMutex i =
			new RevisionInfoMutex(DATE, env, 72, 78);
		assertEquals(-1, i.getNumber());
		assertEquals(DATE, i.getDate());
		assertEqualsUnmodifiable(env, i.getEnvironment());
		assertEquals(72, i.getExpectedNumber());
		assertEquals(78, i.getActualNumber());
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
	
	private static final TreeMap<String, String> reparse(final RevisionInfo info)
	{
		final byte[] bytes = info.toBytes();
		String bytesString;
		try
		{
			bytesString = new String(bytes, "latin1");
		}
		catch(UnsupportedEncodingException e)
		{
			throw new RuntimeException(e);
		}
		assertTrue(bytesString, bytesString.startsWith("#migrationlogv01\n"));
		final Properties p = RevisionInfo.parse(bytes);
		final TreeMap<String, String> result = new TreeMap<String, String>();
		for(final Object key : p.keySet())
			result.put((String)key, p.getProperty((String)key));
		return result;
	}
	
	public static final TreeMap<String, String> map(final String... s)
	{
		final TreeMap<String, String> result = new TreeMap<String, String>();
		int i = 0;
		while(i<s.length)
			result.put(s[i++], s[i++]);
		return result;
	}
}
