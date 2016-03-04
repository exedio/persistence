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
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.ResultSet;
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
	private final int transactionIsolation;

	ConnectionFactory(
			final ConnectProperties properties,
			final Driver driver,
			final Dialect dialect)
	{
		this.url = properties.getConnectionUrl();
		this.driver = driver;
		this.dialect = dialect;

		info = properties.newConnectionInfo();
		dialect.completeConnectionInfo(info);

		this.transactionIsolation = dialect.getTransationIsolation();
	}

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
			result.setTransactionIsolation(transactionIsolation);
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

	@SuppressFBWarnings("SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE")
	public boolean isValidOnGet(final Connection e)
	{
		final String sql = dialect.isValidOnGet42();
		final int result;
		try(
				java.sql.Statement stat = e.createStatement();
				ResultSet rs = stat.executeQuery(sql))
		{
			//final long start = System.currentTimeMillis();
			rs.next();
			result = rs.getInt(1);
			//timeInChecks += (System.currentTimeMillis()-start);
			//numberOfChecks++;
			//System.out.println("------------------"+timeInChecks+"---"+numberOfChecks+"---"+(timeInChecks/numberOfChecks));
		}
		catch(final SQLException ex)
		{
			if(logger.isWarnEnabled())
				logger.warn( "invalid on get", ex );
			if(isValidOnGetFails)
				throw new SQLRuntimeException(ex, sql);
			return false;
		}

		if(result!=42)
			throw new RuntimeException("expected 42, but was " + result);
		return true;
	}

	boolean isValidOnGetFails = false;

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
