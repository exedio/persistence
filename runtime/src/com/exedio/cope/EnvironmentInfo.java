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

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Properties;

public final class EnvironmentInfo
{
	private final String databaseProductName;
	private final String databaseProductVersion;
	private final int databaseMajorVersion;
	private final int databaseMinorVersion;
	private final String driverName;
	private final String driverVersion;
	private final int driverMajorVersion;
	private final int driverMinorVersion;

	EnvironmentInfo(final DatabaseMetaData dmd) throws SQLException
	{
		databaseProductName = dmd.getDatabaseProductName();
		databaseProductVersion = dmd.getDatabaseProductVersion();
		databaseMajorVersion = dmd.getDatabaseMajorVersion();
		databaseMinorVersion = dmd.getDatabaseMinorVersion();
		driverName = dmd.getDriverName();
		driverVersion = dmd.getDriverVersion();
		driverMajorVersion = dmd.getDriverMajorVersion();
		driverMinorVersion = dmd.getDriverMinorVersion();
	}

	public String getDatabaseProductName()
	{
		return databaseProductName;
	}

	public String getDatabaseProductVersion()
	{
		return databaseProductVersion;
	}

	public int getDatabaseMajorVersion()
	{
		return databaseMajorVersion;
	}

	public int getDatabaseMinorVersion()
	{
		return databaseMinorVersion;
	}

	public String getDriverName()
	{
		return driverName;
	}

	public String getDriverVersion()
	{
		return driverVersion;
	}

	public int getDriverMajorVersion()
	{
		return driverMajorVersion;
	}

	public int getDriverMinorVersion()
	{
		return driverMinorVersion;
	}

	public String getDatabaseVersionDescription()
	{
		return databaseProductVersion + ' ' + '(' + databaseMajorVersion + '.' + databaseMinorVersion + ')';
	}

	public String getDriverVersionDescription()
	{
		return driverVersion + ' ' + '(' + driverMajorVersion + '.' + driverMinorVersion + ')';
	}

	public Properties asProperties()
	{
		final Properties result = new Properties();
		result.setProperty("database.name", databaseProductName);
		result.setProperty("database.version", getDatabaseVersionDescription());
		result.setProperty("driver.name", driverName);
		result.setProperty("driver.version", getDriverVersionDescription());
		return result;
	}

	void putRevisionEnvironment(final HashMap<String, String> e)
	{
		e.put("database.name",    databaseProductName);
		e.put("database.version", databaseProductVersion);
		e.put("database.version.major", String.valueOf(databaseMajorVersion));
		e.put("database.version.minor", String.valueOf(databaseMinorVersion));
		e.put("driver.name",    driverName);
		e.put("driver.version", driverVersion);
		e.put("driver.version.major", String.valueOf(driverMajorVersion));
		e.put("driver.version.minor", String.valueOf(driverMinorVersion));
	}

	public boolean isDatabaseVersionAtLeast(final int major, final int minor)
	{
		return isVersionAtLeast(major, minor, databaseMajorVersion, databaseMinorVersion);
	}

	public boolean isDriverVersionAtLeast(final int major, final int minor)
	{
		return isVersionAtLeast(major, minor, driverMajorVersion, driverMinorVersion);
	}

	private static boolean isVersionAtLeast(
			final int expectedMajor,
			final int expectedMinor,
			final int actualMajor,
			final int actualMinor)
	{
		if(expectedMajor<actualMajor)
			return true;
		else if(expectedMajor>actualMajor)
			return false;
		else
			return expectedMinor<=actualMinor;
	}
}
