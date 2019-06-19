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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

public class MysqlDialectMariaDriverExceptionPatternTest
{
	@Test void testMatchLongId()
	{
		assertMatch("(conn=1234567890) remainder");
	}
	@Test void testMatchShortId()
	{
		assertMatch("(conn=4) remainder");
	}
	@Test void testMismatchOpeningParenthesis()
	{
		assertMismatch("conn=1234) remainder");
	}
	@Test void testMismatchConnStart()
	{
		assertMismatch("(onn=1234) remainder");
	}
	@Test void testMismatchConnEnd()
	{
		assertMismatch("(con=1234) remainder");
	}
	@Test void testSeparator()
	{
		assertMismatch("(conn1234) remainder");
	}
	@Test void testMismatchId()
	{
		assertMismatch("(conn=) remainder");
	}
	@Test void testClosingParenthesis()
	{
		assertMismatch("(conn=1234 remainder");
	}
	@Test void testSpace()
	{
		assertMismatch("(conn=1234)remainder");
	}
	@Test void testMatchLongIdColon()
	{
		assertMismatch("(conn:1234567890) remainder");
	}
	@Test void testMatchShortIdColon()
	{
		assertMismatch("(conn:4) remainder");
	}


	private static void assertMatch(final String s)
	{
		final Matcher matcher = PATTERN.matcher(s);
		assertEquals(true, matcher.matches());
		assertEquals("remainder", matcher.group(1));
	}

	private static void assertMismatch(final String s)
	{
		final Matcher matcher = PATTERN.matcher(s);
		assertEquals(false, matcher.matches());
	}

	private static final Pattern PATTERN = Pattern.compile(MysqlDialect.MARIA_DRIVER_CONN_PATTERN);
}
