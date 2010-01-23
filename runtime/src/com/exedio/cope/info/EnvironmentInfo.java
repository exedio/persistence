/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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

package com.exedio.cope.info;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Properties;

public final class EnvironmentInfo
{
	final String databaseProductName;
	final String databaseProductVersion;
	final int databaseMajorVersion;
	final int databaseMinorVersion;
	final String driverName;
	final String driverVersion;
	final int driverMajorVersion;
	final int driverMinorVersion;
	
	public EnvironmentInfo(final DatabaseMetaData dmd) throws SQLException
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
}
