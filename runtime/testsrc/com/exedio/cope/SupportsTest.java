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

import static com.exedio.cope.SchemaInfo.supportsCheckConstraints;
import static com.exedio.cope.SchemaInfo.supportsNativeDate;
import static com.exedio.cope.SchemaInfo.supportsUniqueViolation;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;

public class SupportsTest extends TestWithEnvironment
{
	public SupportsTest()
	{
		super(SchemaTest.MODEL);
		copeRule.omitTransaction();
	}

	@Test public void testSupports()
	{
		final ConnectProperties props = model.getConnectProperties();

		boolean emptyStrings = true;
		boolean utf8mb4 = true;
		final ArrayList<String> dataHashAlgorithms = new ArrayList<>(asList("MD5", "SHA", "SHA-224", "SHA-256", "SHA-384", "SHA-512"));
		boolean random = false;
		boolean checkConstraints = true;
		boolean nativeDate = true;
		boolean uniqueViolation = false;

		switch(dialect)
		{
			case hsqldb:
				emptyStrings = !propertiesHsqldbOracle();
				dataHashAlgorithms.clear(); // TODO support more
				nativeDate = !propertiesHsqldbMysql();
				break;
			case mysql:
				utf8mb4 = propertiesUtf8mb4();
				random = true;
				checkConstraints = false;
				nativeDate = false;
				uniqueViolation = true;
				break;
			case oracle:
				emptyStrings = false;
				dataHashAlgorithms.clear(); // TODO support more
				break;
			case postgresql:
				dataHashAlgorithms.retainAll(asList("MD5")); // TODO support more
				break;
			default:
				fail(dialect.name());
		}

		assertEquals(emptyStrings && !props.isSupportDisabledForEmptyStrings(), model.supportsEmptyStrings());
		assertEquals(utf8mb4, model.supportsUTF8mb4());
		assertEquals(dataHashAlgorithms, new ArrayList<>(model.getSupportedDataHashAlgorithms()));
		assertEquals(random, model.supportsRandom());

		// SchemaInfo
		assertEquals(checkConstraints, supportsCheckConstraints(model));
		assertEquals(nativeDate      && !props.isSupportDisabledForNativeDate(),      supportsNativeDate     (model));
		assertEquals(uniqueViolation && !props.isSupportDisabledForUniqueViolation(), supportsUniqueViolation(model));
	}

	@Test public void testSchemaSavepoint()
	{
		switch(dialect)
		{
			case mysql:
				try
				{
					final String result = model.getSchemaSavepoint();
					//System.out.println(result);
					assertNotNull(result);
				}
				catch(final SQLException ignored)
				{
					// ok
				}
				break;
			case hsqldb:
			case oracle:
			case postgresql:
				try
				{
					final String result = model.getSchemaSavepoint();
					fail(result);
				}
				catch(final SQLException e)
				{
					assertEquals("not supported", e.getMessage());
				}
				break;
			default:
				fail("" + dialect);
		}
	}

	@Deprecated
	@Test public void testDeprecated()
	{
		assertEquals(true, SchemaInfo.supportsSequences(model));
		assertEquals(true, SchemaInfo.supportsNotNull(model));
		assertEquals(true, model.nullsAreSortedLow());
	}
}
