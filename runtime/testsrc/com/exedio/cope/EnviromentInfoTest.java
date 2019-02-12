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
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Properties;
import org.junit.jupiter.api.Test;

public class EnviromentInfoTest
{
	@Test void testInt() throws SQLException
	{
		final EnvironmentInfo i = new EnvironmentInfo(
				null,
				"getCatalog",
				new VersionDatabaseMetaData(5, 3, 14, 18));

		assertEquals("getCatalog", i.getCatalog());
		assertEquals("getDatabaseProductName", i.getDatabaseProductName());
		assertEquals("getDatabaseProductVersion", i.getDatabaseProductVersion());
		assertEquals("getDriverName", i.getDriverName());
		assertEquals("getDriverVersion", i.getDriverVersion());

		assertEquals( 5, i.getDatabaseMajorVersion());
		assertEquals( 3, i.getDatabaseMinorVersion());
		assertEquals(14, i.getDriverMajorVersion());
		assertEquals(18, i.getDriverMinorVersion());

		assertEquals("getDatabaseProductVersion (5.3)", i.getDatabaseVersionDescription());
		assertEquals("getDriverVersion (14.18)", i.getDriverVersionDescription());

		{
			final Properties expected = new Properties();
			expected.setProperty("database.name", "getDatabaseProductName");
			expected.setProperty("database.version", "getDatabaseProductVersion (5.3)");
			expected.setProperty("driver.name", "getDriverName");
			expected.setProperty("driver.version", "getDriverVersion (14.18)");
			assertEquals(expected, i.asProperties());
		}
		{
			final HashMap<String, String> expected = new HashMap<>();
			expected.put("database.name", "getDatabaseProductName");
			expected.put("database.version", "getDatabaseProductVersion");
			expected.put("database.version.major", "5");
			expected.put("database.version.minor", "3");
			expected.put("driver.name", "getDriverName");
			expected.put("driver.version", "getDriverVersion");
			expected.put("driver.version.major", "14");
			expected.put("driver.version.minor", "18");
			final HashMap<String, String> actual = new HashMap<>();
			i.putRevisionEnvironment(actual);
			assertEquals(expected, actual);
		}

		assertEquals(true,  i.isDatabaseVersionAtLeast(4, 2));
		assertEquals(true,  i.isDatabaseVersionAtLeast(4, 3));
		assertEquals(true,  i.isDatabaseVersionAtLeast(4, 4));
		assertEquals(true,  i.isDatabaseVersionAtLeast(5, 2));
		assertEquals(true,  i.isDatabaseVersionAtLeast(5, 3));
		assertEquals(false, i.isDatabaseVersionAtLeast(5, 4));
		assertEquals(false, i.isDatabaseVersionAtLeast(6, 2));
		assertEquals(false, i.isDatabaseVersionAtLeast(6, 3));
		assertEquals(false, i.isDatabaseVersionAtLeast(6, 4));

		assertEquals(true,  i.isDriverVersionAtLeast(13, 17));
		assertEquals(true,  i.isDriverVersionAtLeast(13, 18));
		assertEquals(true,  i.isDriverVersionAtLeast(13, 19));
		assertEquals(true,  i.isDriverVersionAtLeast(14, 17));
		assertEquals(true,  i.isDriverVersionAtLeast(14, 18));
		assertEquals(false, i.isDriverVersionAtLeast(14, 19));
		assertEquals(false, i.isDriverVersionAtLeast(15, 17));
		assertEquals(false, i.isDriverVersionAtLeast(15, 18));
		assertEquals(false, i.isDriverVersionAtLeast(15, 19));

		i.requireDatabaseVersionAtLeast(5, 3);
		assertFails(
				() -> i.requireDatabaseVersionAtLeast(5, 4),
				IllegalArgumentException.class,
				"requires database version 5.4 or later, " +
				"but was getDatabaseProductName getDatabaseProductVersion (5.3)");
		assertFails(
				() -> i.requireDatabaseVersionAtLeast(66, 77),
				IllegalArgumentException.class,
				"requires database version 66.77 or later, " +
				"but was getDatabaseProductName getDatabaseProductVersion (5.3)");
	}

	@Test void testShortDescription() throws SQLException
	{
		final EnvironmentInfo i = new EnvironmentInfo(
				null,
				"getCatalog",
				new VersionDatabaseMetaData("5.3.1", 5, 3, "14.18a", 14, 18));

		assertEquals("getDatabaseProductName", i.getDatabaseProductName());
		assertEquals("5.3.1", i.getDatabaseProductVersion());
		assertEquals("getDriverName", i.getDriverName());
		assertEquals("14.18a", i.getDriverVersion());

		assertEquals( 5, i.getDatabaseMajorVersion());
		assertEquals( 3, i.getDatabaseMinorVersion());
		assertEquals(14, i.getDriverMajorVersion());
		assertEquals(18, i.getDriverMinorVersion());

		assertEquals("5.3.1", i.getDatabaseVersionDescription());
		assertEquals("14.18a", i.getDriverVersionDescription());

		{
			final Properties expected = new Properties();
			expected.setProperty("database.name", "getDatabaseProductName");
			expected.setProperty("database.version", "5.3.1");
			expected.setProperty("driver.name", "getDriverName");
			expected.setProperty("driver.version", "14.18a");
			assertEquals(expected, i.asProperties());
		}
		{
			final HashMap<String, String> expected = new HashMap<>();
			expected.put("database.name", "getDatabaseProductName");
			expected.put("database.version", "5.3.1");
			expected.put("database.version.major", "5");
			expected.put("database.version.minor", "3");
			expected.put("driver.name", "getDriverName");
			expected.put("driver.version", "14.18a");
			expected.put("driver.version.major", "14");
			expected.put("driver.version.minor", "18");
			final HashMap<String, String> actual = new HashMap<>();
			i.putRevisionEnvironment(actual);
			assertEquals(expected, actual);
		}
	}

	@Test void testShortDescriptionExact() throws SQLException
	{
		final EnvironmentInfo i = new EnvironmentInfo(
				null,
				"getCatalog",
				new VersionDatabaseMetaData("5.3", 5, 3, "14.18", 14, 18));

		assertEquals("5.3", i.getDatabaseVersionDescription());
	}

	@Test void testShortDescriptionMismatchMajor() throws SQLException
	{
		final EnvironmentInfo i = new EnvironmentInfo(
				null,
				"getCatalog",
				new VersionDatabaseMetaData("6.3", 5, 3, "14.18", 14, 18));

		assertEquals("6.3 (5.3)", i.getDatabaseVersionDescription());
	}

	@Test void testShortDescriptionMismatchMinor() throws SQLException
	{
		final EnvironmentInfo i = new EnvironmentInfo(
				null,
				"getCatalog",
				new VersionDatabaseMetaData("5.4", 5, 3, "14.18", 14, 18));

		assertEquals("5.4 (5.3)", i.getDatabaseVersionDescription());
	}

	@Test void testShortDescriptionDotMissing() throws SQLException
	{
		final EnvironmentInfo i = new EnvironmentInfo(
				null,
				"getCatalog",
				new VersionDatabaseMetaData("53", 5, 3, "14.18", 14, 18));

		assertEquals("53 (5.3)", i.getDatabaseVersionDescription());
	}

	@Test void testShortDescriptionDotOther() throws SQLException
	{
		final EnvironmentInfo i = new EnvironmentInfo(
				null,
				"getCatalog",
				new VersionDatabaseMetaData("5x3", 5, 3, "14.18", 14, 18));

		assertEquals("5x3 (5.3)", i.getDatabaseVersionDescription());
	}

	@Test void testShortDescriptionDigitAfter() throws SQLException
	{
		final EnvironmentInfo i = new EnvironmentInfo(
				null,
				"getCatalog",
				new VersionDatabaseMetaData("5.31", 5, 3, "14.18", 14, 18));

		assertEquals("5.31 (5.3)", i.getDatabaseVersionDescription());
	}

	@Test void testShortDescriptionDigitBefore() throws SQLException
	{
		final EnvironmentInfo i = new EnvironmentInfo(
				null,
				"getCatalog",
				new VersionDatabaseMetaData("15.3", 5, 3, "14.18", 14, 18));

		assertEquals("15.3 (5.3)", i.getDatabaseVersionDescription());
	}

	@Test void testShortDescriptionOtherAfter() throws SQLException
	{
		final EnvironmentInfo i = new EnvironmentInfo(
				null,
				"getCatalog",
				new VersionDatabaseMetaData("5.3z", 5, 3, "14.18", 14, 18));

		assertEquals("5.3z", i.getDatabaseVersionDescription());
	}

	@Test void testShortDescriptionOtherBefore() throws SQLException
	{
		final EnvironmentInfo i = new EnvironmentInfo(
				null,
				"getCatalog",
				new VersionDatabaseMetaData("z5.3", 5, 3, "14.18", 14, 18));

		assertEquals("z5.3", i.getDatabaseVersionDescription());
	}

	@Test void testDriverDescriptionGitMatch() throws SQLException
	{
		final EnvironmentInfo i = new EnvironmentInfo(
				null,
				"getCatalog",
				new VersionDatabaseMetaData("XX", 77, 777,
						"revision: 9131eefa398531c7dc98776e8a3fe839e544c5b2 or so", 5, 1));

		assertEquals("revision: 9131eefa or so",
				i.getDriverVersion());
		assertEquals("revision: 9131eefa or so (5.1)",
				i.getDriverVersionDescription());
	}

	@Test void testDriverDescriptionGitTooShort() throws SQLException
	{
		final EnvironmentInfo i = new EnvironmentInfo(
				null,
				"getCatalog",
				new VersionDatabaseMetaData("XX", 77, 777,
						"revision: 9131eefa398531c7dc98776e8a3fe839e544c5b or so", 5, 1));

		assertEquals("revision: 9131eefa398531c7dc98776e8a3fe839e544c5b or so",
				i.getDriverVersion());
		assertEquals("revision: 9131eefa398531c7dc98776e8a3fe839e544c5b or so (5.1)",
				i.getDriverVersionDescription());
	}

	@Test void testDriverDescriptionGitTooLong() throws SQLException
	{
		final EnvironmentInfo i = new EnvironmentInfo(
				null,
				"getCatalog",
				new VersionDatabaseMetaData("XX", 77, 777,
						"revision: 9131eefa398531c7dc98776e8a3fe839e544c5b2a or so", 5, 1));

		assertEquals("revision: 9131eefa398531c7dc98776e8a3fe839e544c5b2a or so",
				i.getDriverVersion());
		assertEquals("revision: 9131eefa398531c7dc98776e8a3fe839e544c5b2a or so (5.1)",
				i.getDriverVersionDescription());
	}

	@Test void testDriverDescriptionGitNoHex() throws SQLException
	{
		final EnvironmentInfo i = new EnvironmentInfo(
				null,
				"getCatalog",
				new VersionDatabaseMetaData("XX", 77, 777,
						"revision: 9131eefa398531c7dc98776e8a3fe839e544c5bX or so", 5, 1));

		assertEquals("revision: 9131eefa398531c7dc98776e8a3fe839e544c5bX or so",
				i.getDriverVersion());
		assertEquals("revision: 9131eefa398531c7dc98776e8a3fe839e544c5bX or so (5.1)",
				i.getDriverVersionDescription());
	}
}
