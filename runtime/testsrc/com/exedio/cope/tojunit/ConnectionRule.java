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

import static org.junit.Assert.assertFalse;

import com.exedio.cope.Model;
import com.exedio.cope.SchemaInfo;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;
import org.junit.rules.TestRule;
import org.junit.runner.Description;

public final class ConnectionRule implements TestRule
{
	private final Model model;

	public ConnectionRule(final Model model)
	{
		this.model = Objects.requireNonNull(model);
	}


	private final BeforeCall before = new BeforeCall();
	private Connection connection;

	@Override
	public final org.junit.runners.model.Statement apply(
			final org.junit.runners.model.Statement base,
			final Description description)
	{
		return new org.junit.runners.model.Statement()
		{
			@Override
			@SuppressWarnings("synthetic-access")
			public void evaluate() throws Throwable
			{
				before.onCall();
				try
				{
					base.evaluate();
				}
				finally
				{
					if(connection!=null)
						connection.close();
				}
			}
		};
	}


	public boolean isConnected()
	{
		before.assertCalled();

		return connection!=null;
	}

	private Connection get() throws SQLException
	{
		before.assertCalled();
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

	@SuppressFBWarnings("SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE")
	public boolean execute(final String sql) throws SQLException
	{
		try(Statement statement = createStatement())
		{
			return statement.execute(sql);
		}
	}

	@SuppressFBWarnings("SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE")
	public int executeUpdate(final String sql) throws SQLException
	{
		try(Statement statement = createStatement())
		{
			return statement.executeUpdate(sql);
		}
	}

	@SuppressFBWarnings("SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE")
	public ResultSet executeQuery(final String sql) throws SQLException
	{
		try(Statement statement = createStatement())
		{
			return statement.executeQuery(sql);
		}
	}

	public void close() throws SQLException
	{
		before.assertCalled();
		assertFalse(model.hasCurrentTransaction());

		if(connection!=null)
		{
			connection.close();
			connection = null;
		}
	}
}
