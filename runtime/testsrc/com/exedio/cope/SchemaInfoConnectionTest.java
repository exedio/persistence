/*
 * Copyright (C) 2004-2011  exedio GmbH (www.exedio.com)
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

import static com.exedio.cope.SchemaInfo.getColumnName;
import static com.exedio.cope.SchemaInfo.getPrimaryKeyColumnName;
import static com.exedio.cope.SchemaInfo.getTableName;
import static com.exedio.cope.SchemaInfo.getTypeColumnName;
import static com.exedio.cope.SchemaInfo.getUpdateCounterColumnName;
import static com.exedio.cope.SchemaInfo.isUpdateCounterEnabled;
import static com.exedio.cope.SchemaInfo.newConnection;
import static com.exedio.cope.SchemaInfo.quoteName;

import java.sql.Connection;
import java.sql.SQLException;

public class SchemaInfoConnectionTest extends AbstractRuntimeTest
{
	public SchemaInfoConnectionTest()
	{
		super(InstanceOfTest.MODEL);
	}

	private Connection c;

	@Override
	protected void tearDown() throws Exception
	{
		if(c!=null)
			c.close();

		super.tearDown();
	}

	@edu.umd.cs.findbugs.annotations.SuppressWarnings("SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE")
	public void testIt() throws SQLException
	{
		final StringBuilder bf = new StringBuilder();
		bf.append("select ").
			append(q(getPrimaryKeyColumnName(InstanceOfAItem.TYPE))).
			append(',').
			append(q(getTypeColumnName(InstanceOfAItem.TYPE)));
		if(isUpdateCounterEnabled(model))
			bf.append(',').
			append(q(getUpdateCounterColumnName(InstanceOfAItem.TYPE)));
		bf.append(',').
			append(q(getColumnName(InstanceOfAItem.code))).
			append(" from ").
			append(q(getTableName(InstanceOfAItem.TYPE)));

		c = newConnection(model);
		final java.sql.Statement statement = c.createStatement();
		try
		{
			statement.execute(bf.toString());
		}
		finally
		{
			statement.close();
		}
	}

	@edu.umd.cs.findbugs.annotations.SuppressWarnings("SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE")
	public void testTypeColumn() throws SQLException
	{
		final StringBuilder bf = new StringBuilder();
		bf.append("select ").
			append(q(getPrimaryKeyColumnName(InstanceOfRefItem.TYPE)));
		if(isUpdateCounterEnabled(model))
			bf.append(',').
				append(q(getUpdateCounterColumnName(InstanceOfRefItem.TYPE)));
		bf.append(',').
			append(q(getColumnName(InstanceOfRefItem.ref))).
			append(',').
			append(q(getTypeColumnName(InstanceOfRefItem.ref))).
			append(" from ").
			append(q(getTableName(InstanceOfRefItem.TYPE)));

		c = newConnection(model);
		final java.sql.Statement statement = c.createStatement();
		try
		{
			statement.execute(bf.toString());
		}
		finally
		{
			statement.close();
		}
	}

	private String q(final String name)
	{
		return quoteName(model, name);
	}
}
