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

import static com.exedio.cope.MysqlDialect.assertDriverVersion;
import static com.exedio.cope.tojunit.Assert.assertFails;

import java.sql.SQLException;
import org.junit.jupiter.api.Test;

public class MysqlDriverVersionAssertTest
{
	@Test void testOkFrom() throws SQLException
	{
		final EnvironmentInfo i = newEnvDriver(
				"mysql-connector-java-8.0.21 (Revision: 0123456789abcdef)",
				8, 0);

		assertDriverVersion(i);
	}
	@Test void testOkTo() throws SQLException
	{
		final EnvironmentInfo i = newEnvDriver(
				"mysql-connector-java-8.0.27 (Revision: 0123456789abcdef)",
				8, 0);

		assertDriverVersion(i);
	}
	@Test void testPatchBefore() throws SQLException
	{
		final EnvironmentInfo i = newEnvDriver(
				"mysql-connector-java-8.0.20 (Revision: 0123456789abcdef)",
				8, 0);

		assertFails(
				() -> assertDriverVersion(i),
				IllegalArgumentException.class,
				"driver version must be between 8.0.21 and 8.0.27, " +
				"but was forbidden patch level 20 in version: " +
				"mysql-connector-java-8.0.20 (Revision: 0123456789abcdef)");
	}
	@Test void testPatchAfter() throws SQLException
	{
		final EnvironmentInfo i = newEnvDriver(
				"mysql-connector-java-8.0.28 (Revision: 0123456789abcdef)",
				8, 0);

		assertFails(
				() -> assertDriverVersion(i),
				IllegalArgumentException.class,
				"driver version must be between 8.0.21 and 8.0.27, " +
				"but was forbidden patch level 28 in version: " +
				"mysql-connector-java-8.0.28 (Revision: 0123456789abcdef)");
	}
	@Test void testPatchIllegalPattern() throws SQLException
	{
		final EnvironmentInfo i = newEnvDriver(
				"mysql-connector-java-8.0.x (Revision: 0123456789abcdef)",
				8, 0);

		assertFails(
				() -> assertDriverVersion(i),
				IllegalArgumentException.class,
				"driver version must be between 8.0.21 and 8.0.27, " +
				"but was illegal pattern: " +
				"mysql-connector-java-8.0.x (Revision: 0123456789abcdef)");
	}
	@Test void testPatchIllegalInteger() throws SQLException
	{
		final EnvironmentInfo i = newEnvDriver(
				"mysql-connector-java-8.0.99999999999 (Revision: 0123456789abcdef)",
				8, 0);

		assertFails(
				() -> assertDriverVersion(i),
				IllegalArgumentException.class,
				"driver version must be between 8.0.21 and 8.0.27, " +
				"but was illegal integer: 99999999999");
	}
	@Test void testMinorBefore() throws SQLException
	{
		final EnvironmentInfo i = newEnvDriver("drXX", 7, 555);

		assertFails(
				() -> assertDriverVersion(i),
				IllegalArgumentException.class,
				"driver version must be between 8.0.21 and 8.0.27, " +
				"but was forbidden minor level: drXX (7.555)");
	}
	@Test void testMinorAfter() throws SQLException
	{
		final EnvironmentInfo i = newEnvDriver("drXX", 8, 1);

		assertFails(
				() -> assertDriverVersion(i),
				IllegalArgumentException.class,
				"driver version must be between 8.0.21 and 8.0.27, " +
				"but was forbidden minor level: drXX (8.1)");
	}

	private static EnvironmentInfo newEnvDriver(
			final String driverVersion,
			final int    driverMajor,
			final int    driverMinor)
			throws SQLException
	{
		return new EnvironmentInfo(
				null,
				null,
				new VersionDatabaseMetaData(
						"dbXX", -55, -66,
						driverVersion, driverMajor, driverMinor));
	}
}
