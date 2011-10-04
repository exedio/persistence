/*
 * Copyright (C) 2004-2011  exedio GmbH (www.exedio.com)
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
