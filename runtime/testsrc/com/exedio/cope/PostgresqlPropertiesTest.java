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

import static com.exedio.cope.tojunit.Assert.assertFails;
import static com.exedio.cope.tojunit.TestSources.describe;
import static com.exedio.cope.tojunit.TestSources.single;
import static com.exedio.cope.util.Sources.cascade;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.exedio.cope.util.IllegalPropertiesException;
import com.exedio.cope.util.Properties.Field;
import com.exedio.cope.util.Properties.Source;
import com.exedio.cope.util.Sources;
import org.junit.jupiter.api.Test;

public class PostgresqlPropertiesTest
{
	/**
	 * This tests makes sure, that no properties are changed by accident.
	 * Adapt if necessary.
	 */
	@Test void testRegression()
	{
		final PostgresqlProperties p = new PostgresqlProperties(loadProperties());

		for(final Field<?> field : p.getFields())
		{
			final String key = field.getKey();
			assertTrue(field.isSpecified(), "not specified: " + key);
			assertEquals(
					field.getDefaultValue(),
					field.getValue(),
					key);
		}

		p.ensureValidity();
	}

	@Test void testTimeZoneContainsQuote()
	{
		final String propKey = "connection.timeZone";
		final Source source =
				describe("DESC", cascade(
						single(propKey, "123'567"),
						loadProperties()
				));

		assertFails(
				() -> new PostgresqlProperties(source),
				IllegalPropertiesException.class,
				"property " + propKey + " in DESC must not contain ''', "+
				"but did at position 3 and was '123'567'");
	}

	@Test void testPostgresqlSearchPath()
	{
		final String propKey = "search_path";
		final Source source =
				describe("DESC", cascade(
						single(propKey, "123,567"),
						loadProperties()
				));
		try
		{
			new PostgresqlProperties(source);
			fail();
		}
		catch(final IllegalPropertiesException e)
		{
			assertEquals(
					"property " + propKey + " in DESC must not contain ',', "+
					"but did at position 3 and was '123,567'",
					e.getMessage());
		}
	}

	@Test void testPgcryptoSchema()
	{
		final String propKey = "pgcryptoSchema";
		final Source source =
				describe("DESC", cascade(
						single(propKey, "123\"567"),
						loadProperties()
				));
		assertFails(
				() -> new PostgresqlProperties(source),
				IllegalPropertiesException.class,
				"property " + propKey + " in DESC must not contain '\"', "+
				"but did at position 3 and was '123\"567'");
	}


	private static Source loadProperties()
	{
		return Sources.load(PostgresqlPropertiesTest.class.getResource("postgresqlPropertiesTest.properties"));
	}
}
