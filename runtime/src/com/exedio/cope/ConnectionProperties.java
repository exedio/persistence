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

import com.exedio.dsmf.SQLRuntimeException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;

final class ConnectionProperties extends com.exedio.cope.util.Properties
{
	final String url = value("url", (String)null);
	final String username = value("username", (String)null);
	final String password = valueHidden("password", (String)null);

	final String postgresqlSearchPath = valueX("postgresql.search_path", username, ',');


	Probe probe(final ConnectProperties outer)
	{
		final Driver driver;
		try
		{
			driver = DriverManager.getDriver(url);
		}
		catch(final SQLException e)
		{
			throw new SQLRuntimeException(e, url);
		}
		if(driver==null)
			throw new RuntimeException(url);

		try(Connection connection = driver.connect(url, newInfo()))
		{
			return new Probe(outer, driver, connection);
		}
		catch(final SQLException e)
		{
			throw new SQLRuntimeException(e, url);
		}
	}

	java.util.Properties newInfo()
	{
		final java.util.Properties result = new java.util.Properties();
		result.setProperty("user", username);
		result.setProperty("password", password);
		return result;
	}

	void putRevisionEnvironment(final String prefix, final HashMap<String, String> e)
	{
		e.put(prefix + ".url", url);
		e.put(prefix + ".user", username);
	}


	private String valueX(final String key, final String defaultValue, final char forbidden)
	{
		final String result = value(key, defaultValue);

		final int position = result.indexOf(forbidden);
		if(position>=0)
			throw newException(key,
				"must not contain '" + forbidden + "', " +
				"but did at position " + position + " and was '" + result + '\'');

		return result;
	}

	ConnectionProperties(final Source source) { super(source); }
}