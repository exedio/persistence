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

import static com.exedio.cope.ConnectProperties.getDefaultPropertyFile;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.exedio.cope.util.Properties;
import com.exedio.cope.util.Sources;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;

public class SchemaSavepointTest extends TestWithEnvironment
{
	public SchemaSavepointTest()
	{
		super(SchemaTest.MODEL);
		copeRule.omitTransaction();
	}

	@Test void test()
	{
		final String expected = new Props().schemaSavepoint;
		try
		{
			assertMatches(expected, "OK: " + model.getSchemaSavepoint());
		}
		catch(final SQLException e)
		{
			assertMatches(expected, "FAILS: " + e.getMessage());
		}
	}

	private static void assertMatches(
			final String expected,
			final String actual)
	{
		if(!NOT_SUPPORTED.equals(expected) ||
			!NOT_SUPPORTED.equals(actual))
		{
			System.out.println(SchemaSavepointTest.class.getName());
			System.out.println("---" + expected + "---");
			System.out.println("---" + actual   + "---");
		}
		assertNotNull(expected);
		assertNotNull(actual);
		assertTrue(actual.matches(expected), () -> "---" + expected + "---" + actual + "---");
	}

	private static final class Props extends Properties
	{
		final String schemaSavepoint = value("x-build.schemasavepoint", NOT_SUPPORTED);

		Props()
		{
			super(Sources.load(getDefaultPropertyFile()));
		}
	}

	private static final String NOT_SUPPORTED = "FAILS: not supported";
}
