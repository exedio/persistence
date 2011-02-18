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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.exedio.cope.util.Pool;
import com.exedio.dsmf.SQLRuntimeException;

final class ConnectionFactory implements Pool.Factory<Connection>
{
	private final String url;
	private final java.util.Properties info;
	private final boolean transactionIsolationReadCommitted;

	ConnectionFactory(final ConnectProperties properties, final Dialect dialect)
	{
		this.url = properties.getConnectionUrl();

		info = new java.util.Properties();
		info.setProperty("user", properties.getConnectionUser());
		info.setProperty("password", properties.getConnectionPassword());
		dialect.completeConnectionInfo(info);

		this.transactionIsolationReadCommitted =
			properties.connectionTransactionIsolationReadCommitted.booleanValue();
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
		final Connection result = DriverManager.getConnection(url, info);
		if(transactionIsolationReadCommitted)
			result.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
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
			System.out.println("warning: pooled connection invalid: " + ex.getMessage());
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
