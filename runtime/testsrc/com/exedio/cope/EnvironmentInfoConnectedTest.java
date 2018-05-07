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

import static com.exedio.cope.SchemaTest.MODEL;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.tojunit.TestSources;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EnvironmentInfoConnectedTest
{
	@Test void testIt()
	{
		final EnvironmentInfo ei = MODEL.getEnvironmentInfo();
		assertAll(
				() -> assertEquals("PUBLIC", ei.getCatalog()),
				() -> assertEquals("HSQL Database Engine", ei.getDatabaseProductName()),
				() -> assertEquals("2.4.0", ei.getDatabaseProductVersion()),
				() -> assertEquals(2, ei.getDatabaseMajorVersion()),
				() -> assertEquals(4, ei.getDatabaseMinorVersion()),
				() -> assertEquals("2.4.0", ei.getDatabaseVersionDescription()),
				() -> assertEquals("HSQL Database Engine Driver", ei.getDriverName()),
				() -> assertEquals("2.4.0", ei.getDriverVersion()),
				() -> assertEquals(2, ei.getDriverMajorVersion()),
				() -> assertEquals(4, ei.getDriverMinorVersion()),
				() -> assertEquals("2.4.0", ei.getDriverVersionDescription()),
				() -> assertEquals("org.hsqldb.jdbc.JDBCDriver", ei.getDriverClass()),
				() -> assertEquals(
						"HSQL Database Engine 2.4.0 " +
						"HSQL Database Engine Driver 2.4.0 " +
						"org.hsqldb.jdbc.JDBCDriver " +
						"PUBLIC",
						ei.toString()));
	}

	@BeforeEach final void setUp()
	{
		MODEL.connect(ConnectProperties.create(TestSources.minimal()));
	}

	@AfterEach final void tearDown()
	{
		MODEL.disconnect();
	}
}
