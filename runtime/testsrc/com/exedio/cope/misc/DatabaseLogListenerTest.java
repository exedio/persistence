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

package com.exedio.cope.misc;

import static com.exedio.cope.tojunit.Assert.assertFails;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.exedio.cope.misc.DatabaseLogListener.Builder;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SuppressFBWarnings({"NP_NULL_PARAM_DEREF_NONVIRTUAL", "UWF_FIELD_NOT_INITIALIZED_IN_CONSTRUCTOR"})
public class DatabaseLogListenerTest
{
	@Test void test() throws UnsupportedEncodingException
	{
		final DatabaseLogListener l =
				new DatabaseLogListener(0, null, print);
		assertNotNull(l.getDate());
		assertEquals(0, l.getThreshold());
		assertEquals(null, l.getSQL());
		assertEquals("", out.toString("UTF-8"));

		l.onStatement("sql", asList("param1", "param2"), 1, 2, 3, 4);
		assertIt("1|2|3|4|sql|[param1, param2]");

		l.onStatement("", asList(), 0, 0, 0, 0);
		assertIt("0|0|0|0||[]");

		l.onStatement(null, asList(), -1, -1, -1, -1);
		assertIt("-1|-1|-1|-1|null|[]");

		l.onStatement(null, null, -1, -1, -1, -1);
		assertIt("-1|-1|-1|-1|null");
	}

	@Test void testThreshold() throws UnsupportedEncodingException
	{
		final DatabaseLogListener l =
				new DatabaseLogListener(40, null, print);
		assertNotNull(l.getDate());
		assertEquals(40, l.getThreshold());
		assertEquals(null, l.getSQL());
		assertEquals("", out.toString("UTF-8"));

		l.onStatement("sql", null, 10, 10, 10, 10);
		assertIt("10|10|10|10|sql");

		l.onStatement("sql", null,  9, 10, 10, 10);
		assertEmpty();

		l.onStatement("sql", null, 10,  9, 10, 10);
		assertEmpty();

		l.onStatement("sql", null, 10, 10,  9, 10);
		assertEmpty();

		l.onStatement("sql", null, 10, 10, 10,  9);
		assertEmpty();

		l.onStatement("sql", null, 10, 10, 10, 10);
		assertIt("10|10|10|10|sql");
	}

	@Test void testSQL() throws UnsupportedEncodingException
	{
		final DatabaseLogListener l =
				new DatabaseLogListener(0, "match", print);
		assertNotNull(l.getDate());
		assertEquals(0, l.getThreshold());
		assertEquals("match", l.getSQL());
		assertEquals("", out.toString("UTF-8"));

		l.onStatement("match", null, 0, 0, 0, 0);
		assertIt("0|0|0|0|match");

		l.onStatement("matc", null, 0, 0, 0, 0);
		assertEmpty();

		l.onStatement("atch", null, 0, 0, 0, 0);
		assertEmpty();

		l.onStatement("match", null, 0, 0, 0, 0);
		assertIt("0|0|0|0|match");
	}


	private ByteArrayOutputStream out;
	private PrintStream print;

	@BeforeEach void setUp() throws UnsupportedEncodingException
	{
		out = new ByteArrayOutputStream();
		print = new PrintStream(out, true, "UTF-8");
	}

	private void assertIt(final String expected) throws UnsupportedEncodingException
	{
		final String actual = out.toString("UTF-8");
		out.reset();
		//noinspection HardcodedLineSeparator
		assertTrue(actual.endsWith("\n"));
		final int pos = actual.indexOf('|') + 1;
		assertEquals(expected, actual.substring(pos, actual.length()-1));
	}

	private void assertEmpty() throws UnsupportedEncodingException
	{
		print.flush();
		assertEquals("", out.toString("UTF-8"));
	}


	@Test void testThresholdNegative()
	{
		assertFails(
				() -> new DatabaseLogListener(-1, null, null),
				IllegalArgumentException.class,
				"threshold must not be negative, but was -1");
	}

	@Test void testOutNull()
	{
		assertFails(
				() -> new DatabaseLogListener(0, null, null),
				NullPointerException.class,
				"out");
	}

	@Test void testBuilderDefault() throws UnsupportedEncodingException
	{
		final DatabaseLogListener l = new Builder(print).build();
		assertNotNull(l.getDate());
		assertEquals(0, l.getThreshold());
		assertEquals(null, l.getSQL());
		assertEquals("", out.toString("UTF-8"));

		l.onStatement("sql", asList(), 1, 2, 3, 4);
		assertIt("1|2|3|4|sql|[]");
	}

	@Test void testBuilderNonDefault() throws UnsupportedEncodingException
	{
		final DatabaseLogListener l = new Builder(print).
				durationThreshold(567).
				sqlFilter("specialSql").
				build();
		assertNotNull(l.getDate());
		assertEquals(567, l.getThreshold());
		assertEquals("specialSql", l.getSQL());
		assertEquals("", out.toString("UTF-8"));

		l.onStatement("specialSql", asList(), 1, 2, 3, 567);
		assertIt("1|2|3|567|specialSql|[]");
	}

	@Test void testBuilderOutNull()
	{
		assertFails(
				() -> new Builder(null),
				NullPointerException.class,
				"out");
	}

	@Test void testBuilderDurationThresholdZero()
	{
		final Builder b = new Builder(print);
		assertFails(
				() -> b.durationThreshold(0),
				IllegalArgumentException.class,
				"durationThreshold must be greater zero, but was 0");
	}

	@Test void testBuilderSqlFilterNull()
	{
		final Builder b = new Builder(print);
		assertFails(
				() -> b.sqlFilter(null),
				NullPointerException.class,
				"sqlFilter");
	}

	@Test void testBuilderSqlFilterEmpty()
	{
		final Builder b = new Builder(print);
		assertFails(
				() -> b.sqlFilter(""),
				IllegalArgumentException.class,
				"sqlFilter must not be empty");
	}
}
