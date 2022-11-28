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

import static com.exedio.cope.misc.DatabaseLogListener.Builder.LOGS_LIMIT_DEFAULT;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static java.lang.System.lineSeparator;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.exedio.cope.misc.DatabaseLogListener.Builder;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DatabaseLogListenerTest
{
	@Test void test()
	{
		final DatabaseLogListener l =
				new Builder(print).build();
		assertNotNull(l.getDate());
		assertEquals(LOGS_LIMIT_DEFAULT, l.getLogsLimit());
		assertEquals(LOGS_LIMIT_DEFAULT, l.getLogsLeft());
		assertEquals(0, l.getThreshold());
		assertEquals(null, l.getSQL());
		assertEquals(false, l.isPrintStackTraceEnabled());
		assertEquals("", out.toString(UTF_8));

		l.onStatement("sql", asList("param1", "param2"), 1, 2, 3, 4);
		assertIt("1|2|3|4|sql|[param1, param2]");

		l.onStatement("", asList(), 0, 0, 0, 0);
		assertIt("0|0|0|0||[]");

		l.onStatement(null, asList(), -1, -1, -1, -1);
		assertIt("-1|-1|-1|-1|null|[]");

		l.onStatement(null, null, -1, -1, -1, -1);
		assertIt("-1|-1|-1|-1|null");
	}

	@Test void testLogsLimit()
	{
		final DatabaseLogListener l = new Builder(print).
				logsLimit(3).
				durationThreshold(5).
				sqlFilter("sqlFilter").
				build();
		assertNotNull(l.getDate());
		assertEquals(3, l.getLogsLimit());
		assertEquals(3, l.getLogsLeft());
		assertEquals(5, l.getThreshold());
		assertEquals("sqlFilter", l.getSQL());
		assertEquals(false, l.isPrintStackTraceEnabled());
		assertEquals("", out.toString(UTF_8));

		l.onStatement("sqlFilter", null, 0, 0, 0, 0);
		assertEmpty();
		assertEquals(3, l.getLogsLeft());

		l.onStatement("sql", null, 0, 0, 0, 0);
		assertEmpty();
		assertEquals(3, l.getLogsLeft());

		l.onStatement("sqlFilter", null, 5, 0, 0, 0);
		assertIt("5|0|0|0|sqlFilter");
		assertEquals(2, l.getLogsLeft());

		l.onStatement("sqlFilter", null, 5, 0, 0, 0);
		assertIt("5|0|0|0|sqlFilter");
		assertEquals(1, l.getLogsLeft());

		l.onStatement("sqlFilter", null, 5, 0, 0, 0);
		assertIt("5|0|0|0|sqlFilter");
		assertEquals(0, l.getLogsLeft());

		l.onStatement("sqlFilter", null, 5, 0, 0, 0);
		assertEmpty();
		assertEquals(0, l.getLogsLeft());

		l.onStatement("sqlFilter", null, 5, 0, 0, 0);
		assertEmpty();
		assertEquals(0, l.getLogsLeft());

		assertEquals(3, l.getLogsLimit());
	}

	@Test void testThreshold()
	{
		final DatabaseLogListener l =
				new Builder(print).durationThreshold(40).build();
		assertNotNull(l.getDate());
		assertEquals(LOGS_LIMIT_DEFAULT, l.getLogsLimit());
		assertEquals(LOGS_LIMIT_DEFAULT, l.getLogsLeft());
		assertEquals(40, l.getThreshold());
		assertEquals(null, l.getSQL());
		assertEquals(false, l.isPrintStackTraceEnabled());
		assertEquals("", out.toString(UTF_8));

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

	@Test void testSQL()
	{
		final DatabaseLogListener l =
				new Builder(print).sqlFilter("match").build();
		assertNotNull(l.getDate());
		assertEquals(LOGS_LIMIT_DEFAULT, l.getLogsLimit());
		assertEquals(LOGS_LIMIT_DEFAULT, l.getLogsLeft());
		assertEquals(0, l.getThreshold());
		assertEquals("match", l.getSQL());
		assertEquals(false, l.isPrintStackTraceEnabled());
		assertEquals("", out.toString(UTF_8));

		l.onStatement("match", null, 0, 0, 0, 0);
		assertIt("0|0|0|0|match");

		l.onStatement("matc", null, 0, 0, 0, 0);
		assertEmpty();

		l.onStatement("atch", null, 0, 0, 0, 0);
		assertEmpty();

		l.onStatement("match", null, 0, 0, 0, 0);
		assertIt("0|0|0|0|match");
	}

	@Test void testPrintStackTrace()
	{
		final DatabaseLogListener l =
				new Builder(print).printStackTrace().build();
		assertNotNull(l.getDate());
		assertEquals(LOGS_LIMIT_DEFAULT, l.getLogsLimit());
		assertEquals(LOGS_LIMIT_DEFAULT, l.getLogsLeft());
		assertEquals(0, l.getThreshold());
		assertEquals(null, l.getSQL());
		assertEquals(true, l.isPrintStackTraceEnabled());
		assertEquals("", out.toString(UTF_8));

		l.onStatement("sql", null, 0, 0, 0, 0);
		assertItStart(
				"0|0|0|0|sql" + lineSeparator() +
				"java.lang.Exception: DatabaseLogListener" + lineSeparator() +
				"\tat " + DatabaseLogListener.class.getName() + ".onStatement(DatabaseLogListener.java:186)" + lineSeparator() +
				"\tat " + DatabaseLogListenerTest.class.getName() + ".testPrintStackTrace(");
	}


	private ByteArrayOutputStream out;
	private PrintStream print;

	@BeforeEach void setUp()
	{
		out = new ByteArrayOutputStream();
		print = new PrintStream(out, true, UTF_8);
	}

	private void assertIt(final String expected)
	{
		final String actual = out.toString(UTF_8);
		out.reset();
		assertTrue(actual.endsWith(lineSeparator()));
		final int pos = actual.indexOf('|') + 1;
		assertEquals(expected, actual.substring(pos, actual.length()-lineSeparator().length()));
	}

	private void assertItStart(final String expected)
	{
		final String actual = out.toString(UTF_8);
		out.reset();
		assertTrue(actual.endsWith(lineSeparator()));
		final int pos = actual.indexOf('|') + 1;
		assertTrue(actual.substring(pos, actual.length()-1).startsWith(expected), actual);
	}

	private void assertEmpty()
	{
		print.flush();
		assertEquals("", out.toString(UTF_8));
	}


	@SuppressWarnings("deprecation") // OK testing deprecated api
	@Test void testDeprecated()
	{
		final DatabaseLogListener l =
				new DatabaseLogListener(567, "sqlFilter", print);
		assertNotNull(l.getDate());
		assertEquals(LOGS_LIMIT_DEFAULT, l.getLogsLimit());
		assertEquals(LOGS_LIMIT_DEFAULT, l.getLogsLeft());
		assertEquals(567, l.getThreshold());
		assertEquals("sqlFilter", l.getSQL());
		assertEquals(false, l.isPrintStackTraceEnabled());
		assertEquals("", out.toString(UTF_8));

		l.onStatement("sqlFilter", asList(), 1, 2, 3, 567);
		assertIt("1|2|3|567|sqlFilter|[]");
	}

	@SuppressWarnings("deprecation") // OK testing deprecated api
	@Test void testThresholdNegative()
	{
		assertFails(
				() -> new DatabaseLogListener(-1, null, null),
				IllegalArgumentException.class,
				"threshold must not be negative, but was -1");
	}

	@SuppressWarnings("deprecation") // OK testing deprecated api
	@Test void testOutNull()
	{
		assertFails(
				() -> new DatabaseLogListener(0, null, null),
				NullPointerException.class,
				"out");
	}

	@Test void testBuilderDefault()
	{
		final DatabaseLogListener l = new Builder(print).build();
		assertNotNull(l.getDate());
		assertEquals(LOGS_LIMIT_DEFAULT, l.getLogsLimit());
		assertEquals(LOGS_LIMIT_DEFAULT, l.getLogsLeft());
		assertEquals(0, l.getThreshold());
		assertEquals(null, l.getSQL());
		assertEquals(false, l.isPrintStackTraceEnabled());
		assertEquals("", out.toString(UTF_8));

		l.onStatement("sql", asList(), 1, 2, 3, 4);
		assertIt("1|2|3|4|sql|[]");
	}

	@Test void testBuilderNonDefault()
	{
		final DatabaseLogListener l = new Builder(print).
				logsLimit(8765432).
				durationThreshold(567).
				sqlFilter("specialSql").
				build();
		assertNotNull(l.getDate());
		assertEquals(8765432, l.getLogsLimit());
		assertEquals(8765432, l.getLogsLeft());
		assertEquals(567, l.getThreshold());
		assertEquals("specialSql", l.getSQL());
		assertEquals(false, l.isPrintStackTraceEnabled());
		assertEquals("", out.toString(UTF_8));

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

	@Test void testBuilderLogsLimitZero()
	{
		//noinspection WriteOnlyObject OK: tested for throwing exception
		final Builder b = new Builder(print);
		assertFails(
				() -> b.logsLimit(0),
				IllegalArgumentException.class,
				"logsLimit must be greater zero, but was 0");
	}

	@Test void testBuilderDurationThresholdZero()
	{
		//noinspection WriteOnlyObject OK: tested for throwing exception
		final Builder b = new Builder(print);
		assertFails(
				() -> b.durationThreshold(0),
				IllegalArgumentException.class,
				"durationThreshold must be greater zero, but was 0");
	}

	@Test void testBuilderSqlFilterNull()
	{
		//noinspection WriteOnlyObject OK: tested for throwing exception
		final Builder b = new Builder(print);
		assertFails(
				() -> b.sqlFilter(null),
				NullPointerException.class,
				"sqlFilter");
	}

	@Test void testBuilderSqlFilterEmpty()
	{
		//noinspection WriteOnlyObject OK: tested for throwing exception
		final Builder b = new Builder(print);
		assertFails(
				() -> b.sqlFilter(""),
				IllegalArgumentException.class,
				"sqlFilter must not be empty");
	}
}
