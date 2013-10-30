/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Properties;
import junit.framework.TestCase;

public class EnviromentInfoTest extends TestCase
{
	public void testInt() throws SQLException
	{
		final EnvironmentInfo i = new EnvironmentInfo(new VersionDatabaseMetaData(5, 3, 14, 18));

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
			final HashMap<String, String> expected = new HashMap<String, String>();
			expected.put("database.name", "getDatabaseProductName");
			expected.put("database.version", "getDatabaseProductVersion");
			expected.put("database.version.major", "5");
			expected.put("database.version.minor", "3");
			expected.put("driver.name", "getDriverName");
			expected.put("driver.version", "getDriverVersion");
			expected.put("driver.version.major", "14");
			expected.put("driver.version.minor", "18");
			final HashMap<String, String> actual = new HashMap<String, String>();
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
	}
}
