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
				"env.env1Key", "env1Value",
				"env.env2Key", "env2Value",
				"env.env3Key", "env3Value",
				"comment", "comment5",
				"body0.sql", "sql5.0",
				"body0.rows", "55",
				"body0.elapsed", "23",
				"body1.sql", "sql5.1",
				"body1.rows", "56",
				"body1.elapsed", "24"),
				reparse(i));
		
		try
		{
			new RevisionInfoRevise(0, null, null, null, (Body[])null);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals("date must not be null", e.getMessage());
		}
		try
		{
			new RevisionInfoRevise(0, DATE, null, null, (Body[])null);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals("environment must not be null", e.getMessage());
		}
		try
		{
			new RevisionInfoRevise(0, DATE, env, null, (Body[])null);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("number must be greater zero, but was 0", e.getMessage());
		}
		try
		{
			new RevisionInfoRevise(1, DATE, env, null, (Body[])null);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals("comment must not be null", e.getMessage());
		}
		try
		{
			new RevisionInfoRevise(1, DATE, env, "comment", (Body[])null);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals("body must not be null", e.getMessage());
		}
		try
		{
			new RevisionInfoRevise(1, DATE, env, "comment", new Body[]{});
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("body must not be empty", e.getMessage());
		}
		try
		{
			new RevisionInfoRevise(1, DATE, env, "comment", new Body[]{null});
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals("body must not be null, but was at index 0", e.getMessage());
		}
		new RevisionInfoRevise(1, DATE, env, "comment", new Body[]{new Body("sql", 5, 5)});
		
		try
		{
			new Body(null, -1, -1);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals("sql must not be null", e.getMessage());
		}
		try
		{
			new Body("", -1, -1);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("sql must not be empty", e.getMessage());
		}
		try
		{
			new Body("x", -1, -1);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("rows must be greater or equal zero, but was -1", e.getMessage());
		}
		try
		{
			new Body("x", 0, -1);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("elapsed must be greater or equal zero, but was -1", e.getMessage());
		}
		new Body("x", 0, 0);
	}
	
	public void testCreate()
	{
		final RevisionInfoCreate i =
			new RevisionInfoCreate(5, DATE, env);
		assertEquals(5, i.getNumber());
		assertEquals(DATE, i.getDate());
		assertEqualsUnmodifiable(env, i.getEnvironment());
		
		assertEquals(map(
				"revision", "5",
				"dateUTC", DATE_STRING,
				"env.env1Key", "env1Value",
				"env.env2Key", "env2Value",
				"env.env3Key", "env3Value",
				"create", "true"),
				reparse(i));
		
		try
		{
			new RevisionInfoCreate(-1, null, null);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals("date must not be null", e.getMessage());
		}
		try
		{
			new RevisionInfoCreate(-1, DATE, null);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals("environment must not be null", e.getMessage());
		}
		try
		{
			new RevisionInfoCreate(-1, DATE, env);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("number must be greater or equal zero, but was -1", e.getMessage());
		}
		new RevisionInfoCreate(0, DATE, env);
	}
	
	public void testMutex()
	{
		final RevisionInfoMutex i =
			new RevisionInfoMutex(DATE, env, 78, 72);
		assertEquals(-1, i.getNumber());
		assertEquals(DATE, i.getDate());
		assertEqualsUnmodifiable(env, i.getEnvironment());
		assertEquals(78, i.getExpectedNumber());
		assertEquals(72, i.getActualNumber());
		
		assertEquals(map(
				"dateUTC", DATE_STRING,
				"env.env1Key", "env1Value",
				"env.env2Key", "env2Value",
				"env.env3Key", "env3Value",
				"mutex", "true",
				"mutex.expected", "78",
				"mutex.actual", "72"),
				reparse(i));
		
		try
		{
			new RevisionInfoMutex(null, null, -1, -1);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals("date must not be null", e.getMessage());
		}
		try
		{
			new RevisionInfoMutex(DATE, null, -1, -1);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals("environment must not be null", e.getMessage());
		}
		try
		{
			new RevisionInfoMutex(DATE, env, -1, -1);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("expectedNumber must be greater or equal zero, but was -1", e.getMessage());
		}
		try
		{
			new RevisionInfoMutex(DATE, env, 0, 0);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("expectedNumber must be greater than 0, but was 0", e.getMessage());
		}
		new RevisionInfoMutex(DATE, env, 1, 0);
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
