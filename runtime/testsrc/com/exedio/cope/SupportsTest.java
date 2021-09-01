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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;

public class SupportsTest extends TestWithEnvironment
{
	public SupportsTest()
	{
		super(SchemaTest.MODEL);
		copeRule.omitTransaction();
	}

	@Test void testSupports()
	{
		final ConnectProperties props = model.getConnectProperties();
		final EnvironmentInfo env = model.getEnvironmentInfo();

		boolean utf8mb4 = true;
		final ArrayList<String> dataHashAlgorithms = new ArrayList<>(asList("MD5", "SHA", "SHA-224", "SHA-256", "SHA-384", "SHA-512"));
		boolean random = false;
		boolean checkConstraints = true;
		boolean nativeDate = true;
		boolean uniqueViolation = false;

		switch(dialect)
		{
			case hsqldb:
				dataHashAlgorithms.clear(); // TODO support more
				checkConstraints = !propertiesHsqldbMysql55() && !propertiesHsqldbMysql56();
				break;
			case mysql:
				utf8mb4 = propertiesUtf8mb4();
				random = true;
				checkConstraints = false;
				nativeDate = env.isDatabaseVersionAtLeast(5, 6);
				uniqueViolation = true;
				break;
			case postgresql:
				if(model.getConnectProperties().getField("dialect.pgcryptoSchema").get().equals("<disabled>"))
					dataHashAlgorithms.retainAll(asList("MD5"));
				break;
			default:
				fail(dialect.name());
		}

		assertEquals(utf8mb4, model.supportsUTF8mb4());
		assertEquals(dataHashAlgorithms, new ArrayList<>(model.getSupportedDataHashAlgorithms()));
		assertEquals(random, model.supportsRandom());

		// SchemaInfo
		assertEquals(checkConstraints, supportsCheckConstraints(model));
		assertEquals(nativeDate      && !props.isSupportDisabledForNativeDate(),      supportsNativeDate     (model));
		assertEquals(uniqueViolation && !props.isSupportDisabledForUniqueViolation(), supportsUniqueViolation(model));
	}

	@Test void testCompression() throws SQLException
	{
		assumeTrue(mysql);

		final boolean enabled = (Boolean)model.getConnectProperties().getField("dialect.connection.compress").get();

		try(Connection c = SchemaInfo.newConnection(model);
			 Statement s = c.createStatement();
			 ResultSet rs = s.executeQuery("SHOW STATUS LIKE 'Compression'"))
		{
			assertTrue(rs.next());
			assertEquals("Compression", rs.getString(1));
			assertEquals(enabled ? "ON" : "OFF", rs.getString(2));
			assertFalse(rs.next());
		}
	}

	@Test void testSchema() throws SQLException
	{
		assumeTrue(postgresql);

		final ConnectProperties props = model.getConnectProperties();
		final String property = (String)props.getField("dialect.connection.schema").get();

		try(Connection c = SchemaInfo.newConnection(model);
			 Statement s = c.createStatement();
			 ResultSet rs = s.executeQuery("SHOW search_path"))
		{
			assertTrue(rs.next());
			assertEquals("$user".equals(property) ? "\"$user\"" : property, rs.getString(1));
			assertFalse(rs.next());
		}
	}
}
