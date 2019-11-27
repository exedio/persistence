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

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;
import org.opentest4j.AssertionFailedError;

@SuppressWarnings("RedundantThrows") // RedundantThrows: allow subclasses to throw exceptions
class AssertionFailedDriver implements Driver
{
	@Override
	public Connection connect(final String url, final Properties info) throws SQLException
	{
		throw new AssertionFailedError();
	}

	@Override
	public boolean acceptsURL(final String url) throws SQLException
	{
		throw new AssertionFailedError();
	}

	@Override
	public DriverPropertyInfo[] getPropertyInfo(final String url, final Properties info) throws SQLException
	{
		throw new AssertionFailedError();
	}

	@Override
	public int getMajorVersion()
	{
		throw new AssertionFailedError();
	}

	@Override
	public int getMinorVersion()
	{
		throw new AssertionFailedError();
	}

	@Override
	public boolean jdbcCompliant()
	{
		throw new AssertionFailedError();
	}

	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException
	{
		throw new AssertionFailedError();
	}
}
