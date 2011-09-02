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

import static com.exedio.cope.SchemaInfo.getTableName;
import static com.exedio.cope.SchemaInfo.getTypeColumnName;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SchemaNamePolymorphicTest extends AbstractRuntimeTest
{
	public static final Model MODEL = new Model(
			SchemaNamePolymorphicSuperItem.TYPE,
			SchemaNamePolymorphicSubItem.TYPE,
			SchemaNamePolymorphicRefItem.TYPE);

	public SchemaNamePolymorphicTest()
	{
		super(MODEL);
	}

	SchemaNamePolymorphicSuperItem item;
	SchemaNamePolymorphicRefItem refItem;
	Connection connection;

	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		item = deleteOnTearDown(new SchemaNamePolymorphicSubItem());
		refItem = deleteOnTearDown(new SchemaNamePolymorphicRefItem(item));
		connection = SchemaInfo.newConnection(model);
	}

	@Override
	public void tearDown() throws Exception
	{
		connection.close();
		super.tearDown();
	}

	public void test() throws SQLException
	{
		if(hsqldb) // TODO
			return;

		restartTransaction();
		{
			final String column = getTypeColumnName(SchemaNamePolymorphicSuperItem.TYPE);
			final String table = getTableName(SchemaNamePolymorphicSuperItem.TYPE);

			assertEquals(
					"SchemaNamePolymorphicSubItem", // TODO
					fetch("select " + column + " from " + table));

			assertEquals(
					notNull(column, q(column) + " IN ('SchemaNamePolymorphicSuperItem','SchemaNamePolymorphicSubItem')"), // TODO
					model.getSchema().getTable(table).getConstraint("ScheNamePolySupeIte_cl_Ck").getRequiredCondition());
		}
		{
			final String column = getTypeColumnName(SchemaNamePolymorphicRefItem.ref);
			final String table = getTableName(SchemaNamePolymorphicRefItem.TYPE);

			assertEquals(
					notNull(column, "SchemaNamePolymorphicSubItem"), // TODO
					fetch("select " + column + " from " + table));

			assertEquals(
					q(column) + " IN ('SchemaNamePolymorphicSuperItem','SchemaNamePolymorphicSubItem')", // TODO
					model.getSchema().getTable(table).getConstraint("ScheNamPolRefIte_reTyp_Ck").getRequiredCondition());
		}
	}

	private String fetch(final String sql) throws SQLException
	{
		final Statement stmt = connection.createStatement();
		final ResultSet rs = stmt.executeQuery(sql);
		assertTrue(rs.next());
		final String result = rs.getString(1);
		assertFalse(rs.next());
		return result;
	}

	private final String q(final String s)
	{
		return SchemaInfo.quoteName(model, s);
	}
}
