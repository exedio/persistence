/*
 * Copyright (C) 2004-2007  exedio GmbH (www.exedio.com)
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

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import com.exedio.dsmf.SQLRuntimeException;

final class DialectParameters
{
	final Properties properties;

	// probed on the initial connection
	final boolean supportsTransactionIsolationLevel;
	final String databaseProductName;
	final String databaseProductVersion;
	final int databaseMajorVersion;
	final int databaseMinorVersion;
	final String driverName;
	final String driverVersion;
	final int driverMajorVersion;
	final int driverMinorVersion;
	
	DialectParameters(final Properties properties, final Connection connection)
	{
		this.properties = properties;

		try
		{
			final DatabaseMetaData dmd = connection.getMetaData();
			supportsTransactionIsolationLevel = dmd.supportsTransactionIsolationLevel(Connection.TRANSACTION_READ_COMMITTED);
			databaseProductName = dmd.getDatabaseProductName();
			databaseProductVersion = dmd.getDatabaseProductVersion();
			databaseMajorVersion = dmd.getDatabaseMajorVersion();
			databaseMinorVersion = dmd.getDatabaseMinorVersion();
			driverName = dmd.getDriverName();
			driverVersion = dmd.getDriverVersion();
			driverMajorVersion = dmd.getDriverMajorVersion();
			driverMinorVersion = dmd.getDriverMinorVersion();
		}
		catch(SQLException e)
		{
			throw new SQLRuntimeException(e, "getMetaData");
		}
	}
	
	void setVersions(final java.util.Properties p)
	{
		p.setProperty("database.name", databaseProductName);
		p.setProperty("database.version", databaseProductVersion);
		p.setProperty("database.version.major", String.valueOf(databaseMajorVersion));
		p.setProperty("database.version.minor", String.valueOf(databaseMinorVersion));
		p.setProperty("driver.name", driverName);
		p.setProperty("driver.version", driverVersion);
		p.setProperty("driver.version.major", String.valueOf(driverMajorVersion));
		p.setProperty("driver.version.minor", String.valueOf(driverMinorVersion));
	}
}
