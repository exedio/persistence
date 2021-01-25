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

import com.exedio.cope.util.Pool;
import com.exedio.dsmf.SQLRuntimeException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class ConnectionFactory implements Pool.Factory<Connection>
{
	private static final Logger logger = LoggerFactory.getLogger(ConnectionFactory.class);

	private final String url;
	private final Driver driver;
	private final Dialect dialect;
	private final Properties info;
	private final int isValidOnGetTimeout;

	ConnectionFactory(
			final ConnectProperties properties,
			final Driver driver,
			final Dialect dialect)
	{
		this.url = properties.connection.url;
		this.driver = driver;
		this.dialect = dialect;

		info = properties.newInfo();
		dialect.completeConnectionInfo(info);

		this.isValidOnGetTimeout = properties.connection.isValidOnGetTimeout;
	}

	@Override
	public Connection create()
	{
		try
		{
			return createRaw();
		}
		catch(final SQLException ex)
		{
			throw new SQLRuntimeException(ex, "connect");
		}
	}

	Connection createRaw() throws SQLException
	{
		final Connection result = driver.connect(url, info);
		if(result==null)
			throw new RuntimeException(driver.toString() + '/' + url);

		boolean mustClose = true;
		try
		{
			result.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
			dialect.completeConnection(result);

			mustClose = false;
		}
		finally
		{
			if(mustClose)
				result.close();
		}
		// DO NOT WRITE ANYTHING HERE, BUT IN TRY BLOCK ONLY
		// OTHERWISE Connections MAY BE LOST
		return result;
	}

	@Override
	public boolean isValidOnGet(final Connection e)
	{
		final boolean result;
		try
		{
			result = e.isValid(isValidOnGetTimeout);
		}
		catch(final SQLException ex)
		{
			throw new SQLRuntimeException(ex, "isValid(" + isValidOnGetTimeout + ')');
		}

		if(!result)
		{
			if(logger.isWarnEnabled())
				logger.warn("invalid on get");
			if(isValidOnGetFails)
				throw new AssertionError("isValid(" + isValidOnGetTimeout + ')');
		}

		return result;
	}

	boolean isValidOnGetFails = false;

	@Override
	public boolean isValidOnPut(final Connection e)
	{
		final boolean closed;
		try
		{
			closed = e.isClosed();
		}
		catch(final SQLException ex)
		{
			throw new SQLRuntimeException(ex, "isClosed");
		}
		if(closed && logger.isWarnEnabled())
			logger.warn( "invalid on put" );
		return !closed;
	}

	@Override
	public void dispose(final Connection e)
	{
		try
		{
			e.close();
		}
		catch(final SQLException ex)
		{
			throw new SQLRuntimeException(ex, "close");
		}
	}
}
