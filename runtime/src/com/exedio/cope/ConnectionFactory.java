/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.exedio.cope.util.Pool;
import com.exedio.dsmf.SQLRuntimeException;

final class ConnectionFactory implements Pool.Factory<Connection>
{
	private static final Logger logger = Logger.getLogger(ConnectionFactory.class.getName());

	private final String url;
	private final Driver driver;
	private final Properties info;
	private final boolean transactionIsolationReadCommitted;
	private final boolean transactionIsolationRepeatableRead;
	private final int transactionIsolationRepeatableReadLevel;

	ConnectionFactory(
			final ConnectProperties properties,
			final Driver driver,
			final Dialect dialect)
	{
		this.url = properties.getConnectionUrl();
		this.driver = driver;

		info = properties.newConnectionInfo();
		dialect.completeConnectionInfo(info);

		this.transactionIsolationReadCommitted =
			properties.connectionTransactionIsolationReadCommitted.booleanValue();
		this.transactionIsolationRepeatableRead =
			properties.connectionTransactionIsolationRepeatableRead.booleanValue();
		this.transactionIsolationRepeatableReadLevel =
			dialect.filterTransationIsolation(Connection.TRANSACTION_REPEATABLE_READ);
	}

	public Connection create()
	{
		try
		{
			return createRaw();
		}
		catch(final SQLException ex)
		{
			throw new SQLRuntimeException(ex, "create");
		}
	}

	Connection createRaw() throws SQLException
	{
		final Connection result = driver.connect(url, info);
		if(result==null)
			throw new RuntimeException(driver.toString() + '/' + url);
		if(transactionIsolationReadCommitted)
			result.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
		if(transactionIsolationRepeatableRead)
			result.setTransactionIsolation(transactionIsolationRepeatableReadLevel);
		return result;
	}

	public boolean isValidOnGet(final Connection e)
	{
		try
		{
			//final long start = System.currentTimeMillis();
			// probably not the best idea
			final ResultSet rs = e.getMetaData().getTables(null, null, "zack", null);
			rs.next();
			rs.close();
			//timeInChecks += (System.currentTimeMillis()-start);
			//numberOfChecks++;
			//System.out.println("------------------"+timeInChecks+"---"+numberOfChecks+"---"+(timeInChecks/numberOfChecks));
			return true;
		}
		catch(final SQLException ex)
		{
			if(logger.isEnabledFor(Level.WARN))
				logger.warn( "invalid on get", ex );
			return false;
		}
	}

	public boolean isValidOnPut(final Connection e)
	{
		try
		{
			return !e.isClosed();
		}
		catch(final SQLException ex)
		{
			throw new SQLRuntimeException(ex, "isClosed");
		}
	}

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
