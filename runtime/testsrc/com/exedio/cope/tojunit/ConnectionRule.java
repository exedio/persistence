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

package com.exedio.cope.tojunit;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.exedio.cope.Model;
import com.exedio.cope.SchemaInfo;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public final class ConnectionRule extends MainRule
{
	private final Model model;

	public ConnectionRule(final Model model)
	{
		this.model = requireNonNull(model);
	}


	private Connection connection;

	@Override
	protected void after() throws SQLException
	{
		if(connection!=null)
			connection.close();
	}


	public boolean isConnected()
	{
		assertBeforeCalled();

		return connection!=null;
	}

	private Connection get() throws SQLException
	{
		assertBeforeCalled();
		assertFalse(model.hasCurrentTransaction());

		if(connection==null)
		{
			connection = SchemaInfo.newConnection(model);
			// must be auto commit, otherwise we cannot reuse connection because of repeatable read
			connection.setAutoCommit(true);
		}

		return connection;
	}

	public Statement createStatement() throws SQLException
	{
		return get().createStatement();
	}

	@SuppressWarnings("UnusedReturnValue")
	public boolean execute(final String sql) throws SQLException
	{
		try(Statement statement = createStatement())
		{
			return statement.execute(sql);
		}
	}

	public int executeUpdate(final String sql) throws SQLException
	{
		try(Statement statement = createStatement())
		{
			return statement.executeUpdate(sql);
		}
	}

	public ResultSet executeQuery(final String sql) throws SQLException
	{
		final Statement statement = createStatement();

		boolean mustReturn = true;
		try
		{
			final ResultSet result = new ProxyResultSet(statement.executeQuery(sql))
			{
				@Override public void close() throws SQLException
				{
					super.close();
					statement.close();
				}
			};

			mustReturn = false;
			return result;
		}
		finally
		{
			if(mustReturn)
				statement.close();
		}
	}

	public void close() throws SQLException
	{
		assertBeforeCalled();
		assertFalse(model.hasCurrentTransaction());

		if(connection!=null)
		{
			connection.close();
			connection = null;
		}
	}
}
