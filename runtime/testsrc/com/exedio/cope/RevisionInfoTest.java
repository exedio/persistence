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
import static com.exedio.cope.tojunit.Assert.assertUnmodifiable;
import static java.lang.System.lineSeparator;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.exedio.cope.RevisionInfoRevise.Body;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.TreeMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RevisionInfoTest
{
	private static final Date DATE = new Date(2874526134l);
	private static final String DATE_STRING = "1970/02/03 06:28:46.134";

	private HashMap<String, String> env = null;

	@BeforeEach final void setUp()
	{
		env = new HashMap<>();
		env.put("env1Key", "env1Value");
		env.put("env2Key", "env2Value");
		env.put("env3Key", "env3Value");
	}

	@SuppressFBWarnings("NP_NULL_PARAM_DEREF_NONVIRTUAL")
	@Test void testRevise()
	{
		final RevisionInfoRevise i =
			new RevisionInfoRevise(5, "saveRevise", DATE, env, "comment5",
					new Body("sql5.0", 55, 23),
					new Body("sql5.1", 56, 24));
		assertEquals(5, i.getNumber());
		assertEquals("saveRevise", i.getSavepoint());
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
			assertEquals("sql5.0(55/23)", b.toString());
		}
		{
			final Body b = it.next();
			assertEquals("sql5.1", b.getSQL());
			assertEquals(56, b.getRows());
			assertEquals(24, b.getElapsed());
			assertEquals("sql5.1(56/24)", b.toString());
		}
		assertFalse(it.hasNext());

		assertEquals(map(
				"revision", "5",
				"savepoint", "saveRevise",
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

		{
			final RevisionInfoRevise i2 = reread(i);
			assertEquals(5, i2.getNumber());
			assertEquals(DATE, i2.getDate());
			assertEqualsUnmodifiable(env, i2.getEnvironment());
			assertEquals("comment5", i2.getComment());
			assertUnmodifiable(i2.getBody());
			final Iterator<Body> it2 = i2.getBody().iterator();
			{
				final Body b = it2.next();
				assertEquals("sql5.0", b.getSQL());
				assertEquals(55, b.getRows());
				assertEquals(23, b.getElapsed());
			}
			{
				final Body b = it2.next();
				assertEquals("sql5.1", b.getSQL());
				assertEquals(56, b.getRows());
				assertEquals(24, b.getElapsed());
			}
			assertFalse(it2.hasNext());
		}

		try
		{
			new RevisionInfoRevise(1, null, null, null, null, (Body[])null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("date", e.getMessage());
		}
		try
		{
			new RevisionInfoRevise(1, null, DATE, null, null, (Body[])null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("environment", e.getMessage());
		}
		try
		{
			new RevisionInfoRevise(0, null, DATE, env, null, (Body[])null);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("number must be greater zero, but was 0", e.getMessage());
		}
		try
		{
			new RevisionInfoRevise(1, null, DATE, env, null, (Body[])null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("comment", e.getMessage());
		}
		try
		{
			new RevisionInfoRevise(1, null, DATE, env, "comment", (Body[])null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("body", e.getMessage());
		}
		try
		{
			new RevisionInfoRevise(1, null, DATE, env, "comment");
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("body must not be empty", e.getMessage());
		}
		try
		{
			new RevisionInfoRevise(1, null, DATE, env, "comment", new Body[]{null});
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("body[0]", e.getMessage());
		}
		new RevisionInfoRevise(1, null, DATE, env, "comment", new Body("sql", 5, 5));

		try
		{
			new Body(null, -1, -1);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("sql", e.getMessage());
		}
		try
		{
			new Body("", -1, -1);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("sql must not be empty", e.getMessage());
		}
		try
		{
			new Body("x", -1, -1);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("rows must not be negative, but was -1", e.getMessage());
		}
		try
		{
			new Body("x", 0, -1);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("elapsed must not be negative, but was -1", e.getMessage());
		}
		new Body("x", 0, 0);
	}

	@Test void testCreate()
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

		{
			final RevisionInfoCreate i2 = reread(i);
			assertEquals(5, i2.getNumber());
			assertEquals(DATE, i2.getDate());
			assertEqualsUnmodifiable(env, i2.getEnvironment());
		}

		try
		{
			new RevisionInfoCreate(-1, null, null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("date", e.getMessage());
		}
		try
		{
			new RevisionInfoCreate(-1, DATE, null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("environment", e.getMessage());
		}
		try
		{
			new RevisionInfoCreate(-1, DATE, env);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("number must be greater or equal zero, but was -1", e.getMessage());
		}
		new RevisionInfoCreate(0, DATE, env);
	}

	@Test void testMutex()
	{
		final RevisionInfoMutex i =
			new RevisionInfoMutex("saveMutex", DATE, env, 78, 72);
		assertEquals(-1, i.getNumber());
		assertEquals("saveMutex", i.getSavepoint());
		assertEquals(DATE, i.getDate());
		assertEqualsUnmodifiable(env, i.getEnvironment());
		assertEquals(78, i.getExpectedNumber());
		assertEquals(72, i.getActualNumber());

		assertEquals(map(
				"savepoint", "saveMutex",
				"dateUTC", DATE_STRING,
				"env.env1Key", "env1Value",
				"env.env2Key", "env2Value",
				"env.env3Key", "env3Value",
				"mutex", "true",
				"mutex.expected", "78",
				"mutex.actual", "72"),
				reparse(i));

		{
			final RevisionInfoMutex i2 = reread(i);
			assertEquals(-1, i2.getNumber());
			assertEquals(DATE, i2.getDate());
			assertEqualsUnmodifiable(env, i2.getEnvironment());
			assertEquals(78, i2.getExpectedNumber());
			assertEquals(72, i2.getActualNumber());
		}

		try
		{
			new RevisionInfoMutex(null, null, null, -1, -1);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("date", e.getMessage());
		}
		try
		{
			new RevisionInfoMutex(null, DATE, null, -1, -1);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("environment", e.getMessage());
		}
		try
		{
			new RevisionInfoMutex(null, DATE, env, -1, -1);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("expectedNumber must be greater or equal zero, but was -1", e.getMessage());
		}
		try
		{
			new RevisionInfoMutex(null, DATE, env, 0, 0);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("expectedNumber must be greater than 0, but was 0", e.getMessage());
		}
		new RevisionInfoMutex(null, DATE, env, 1, 0);
	}

	@Test void testParse() throws UnsupportedEncodingException
	{
		assertEquals(map("key1", "value1", "key2", "value2"), RevisionInfo.parse(("#migrationlogv01" + lineSeparator() + "key1=value1" + lineSeparator() + "key2=value2").getBytes("latin1")));
		assertEquals(null, RevisionInfo.parse("migrationlogv01".getBytes("latin1")));
		assertEquals(null, RevisionInfo.parse("#migrationlogv0".getBytes("latin1")));
		assertEquals(null, RevisionInfo.parse("x#migrationlogv01".getBytes("latin1")));
		assertEquals(null, RevisionInfo.parse("".getBytes("latin1")));
		try
		{
			RevisionInfo.parse(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals(null, e.getMessage());
		}
	}

	private static TreeMap<String, String> reparse(final RevisionInfo info)
	{
		final byte[] bytes = info.toBytes();
		final String bytesString;
		try
		{
			bytesString = new String(bytes, "latin1");
		}
		catch(final UnsupportedEncodingException e)
		{
			throw new RuntimeException(e);
		}
		assertTrue(bytesString.startsWith("#migrationlogv01" + lineSeparator()), bytesString);
		final Properties p = RevisionInfo.parse(bytes);
		final TreeMap<String, String> result = new TreeMap<>();
		for(final Object key : p.keySet())
			result.put((String)key, p.getProperty((String)key));
		return result;
	}

	@SuppressWarnings("unchecked")
	private static <X extends RevisionInfo> X reread(final X info)
	{
		final byte[] bytes = info.toBytes();
		final String bytesString;
		try
		{
			bytesString = new String(bytes, "latin1");
		}
		catch(final UnsupportedEncodingException e)
		{
			throw new RuntimeException(e);
		}
		assertTrue(bytesString.startsWith("#migrationlogv01" + lineSeparator()), bytesString);
		return (X)RevisionInfo.read(bytes);
	}

	public static final TreeMap<String, String> map(final String... s)
	{
		final TreeMap<String, String> result = new TreeMap<>();
		int i = 0;
		while(i<s.length)
			result.put(s[i++], s[i++]);
		return result;
	}
}
