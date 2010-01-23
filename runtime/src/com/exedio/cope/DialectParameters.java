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

package com.exedio.cope;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.exedio.cope.info.EnvironmentInfo;
import com.exedio.dsmf.SQLRuntimeException;

final class DialectParameters
{
	final ConnectProperties properties;

	// probed on the initial connection
	final boolean supportsTransactionIsolationLevel;
	final EnvironmentInfo environmentInfo;
	
	DialectParameters(final ConnectProperties properties, final Connection connection)
	{
		this.properties = properties;

		try
		{
			final DatabaseMetaData dmd = connection.getMetaData();
			supportsTransactionIsolationLevel = dmd.supportsTransactionIsolationLevel(Connection.TRANSACTION_READ_COMMITTED);
			this.environmentInfo = new EnvironmentInfo(dmd);
		}
		catch(SQLException e)
		{
			throw new SQLRuntimeException(e, "getMetaData");
		}
	}
	
	Map<String, String> getRevisionEnvironment()
	{
		final HashMap<String, String> store = new HashMap<String, String>();
		
		try
		{
			store.put("hostname", InetAddress.getLocalHost().getHostName());
		}
		catch(UnknownHostException e)
		{
			// do not put in hostname
		}
		
		store.put("jdbc.url",  properties.getDatabaseUrl());
		store.put("jdbc.user", properties.getDatabaseUser());
		store.put("database.name",    environmentInfo.getDatabaseProductName());
		store.put("database.version", environmentInfo.getDatabaseProductVersion());
		store.put("database.version.major", String.valueOf(environmentInfo.getDatabaseMajorVersion()));
		store.put("database.version.minor", String.valueOf(environmentInfo.getDatabaseMinorVersion()));
		store.put("driver.name",    environmentInfo.getDriverName());
		store.put("driver.version", environmentInfo.getDriverVersion());
		store.put("driver.version.major", String.valueOf(environmentInfo.getDriverMajorVersion()));
		store.put("driver.version.minor", String.valueOf(environmentInfo.getDriverMinorVersion()));
		
		return store;
	}
}
