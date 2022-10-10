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

import static com.exedio.cope.ConnectPropertiesTest.HSQLDB_PROBE;
import static com.exedio.cope.ModelMetrics.toEpoch;
import static com.exedio.cope.PrometheusMeterRegistrar.meter;
import static com.exedio.cope.SchemaTest.MODEL;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.tojunit.TestSources;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Tags;
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
				() -> assertEquals("2.5.1", ei.getDatabaseProductVersion()),
				() -> assertEquals(2, ei.getDatabaseMajorVersion()),
				() -> assertEquals(5, ei.getDatabaseMinorVersion()),
				() -> assertEquals("2.5.1", ei.getDatabaseVersionDescription()),
				() -> assertEquals("HSQL Database Engine Driver", ei.getDriverName()),
				() -> assertEquals("2.5.1", ei.getDriverVersion()),
				() -> assertEquals(2, ei.getDriverMajorVersion()),
				() -> assertEquals(5, ei.getDriverMinorVersion()),
				() -> assertEquals("2.5.1", ei.getDriverVersionDescription()),
				() -> assertEquals("org.hsqldb.jdbc.JDBCDriver", ei.getDriverClass()),
				() -> assertEquals(HSQLDB_PROBE, ei.toString()));

		final ConnectProperties props = MODEL.getConnectProperties();
		assertEquals(toEpoch(MODEL.getConnectInstant()), ((Gauge)meter(Model.class, "connectTime", Tags.of(
				"model", MODEL.toString()
		))).value());
		assertEquals(1.0, ((Gauge)meter(Model.class, "connect", Tags.of(
				"model", MODEL.toString(),
				"connectionUrl",      props.getConnectionUrl(),
				"connectionUsername", props.getConnectionUsername(),
				"dialect", props.getDialect(),
				"catalog", ei.getCatalog(),
				"databaseName",    ei.getDatabaseProductName(),
				"databaseVersion", ei.getDatabaseProductVersion(),
				"driverName",      ei.getDriverName(),
				"driverVersion",   ei.getDriverVersion(),
				"driverClass",     ei.getDriverClass()
		))).value());
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
