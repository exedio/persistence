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

import static com.exedio.cope.SchemaInfo.getTableName;
import static com.exedio.cope.SchemaInfo.getTypeColumnName;
import static com.exedio.cope.SchemaInfo.getTypeColumnValue;
import static com.exedio.cope.tojunit.Assert.list;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.exedio.cope.tojunit.ConnectionRule;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

public class SchemaNamePolymorphicTest extends TestWithEnvironment
{
	public static final Model MODEL = new Model(
			SchemaNamePolymorphicSuperItem.TYPE,
			SchemaNamePolymorphicSubItem.TYPE,
			SchemaNamePolymorphicRefItem.TYPE);

	public SchemaNamePolymorphicTest()
	{
		super(MODEL);
	}

	private final ConnectionRule connection = new ConnectionRule(model);

	@Rule public final RuleChain ruleChain = RuleChain.outerRule(connection);

	@Test public void test() throws SQLException
	{
		assertEquals(
				"SchemaNamePolymorphicSuperItem",
				getTypeColumnValue(SchemaNamePolymorphicSuperItem.TYPE));
		assertEquals(
				"SchemaNamePolymorphicSubItemRenamed",
				getTypeColumnValue(SchemaNamePolymorphicSubItem.TYPE));

		final SchemaNamePolymorphicSuperItem item = new SchemaNamePolymorphicSubItem();
		final SchemaNamePolymorphicRefItem refItem = new SchemaNamePolymorphicRefItem(item);

		assertEquals(item, refItem.getRef());
		assertEquals(list(item), new Query<>(SchemaNamePolymorphicRefItem.ref).search());

		restartTransaction();
		model.clearCache();
		assertEquals(item, refItem.getRef());
		assertEquals(list(item), new Query<>(SchemaNamePolymorphicRefItem.ref).search());
		// test whether InstanceOfCondition actually uses schemaId
		assertEquals(list(refItem), SchemaNamePolymorphicRefItem.TYPE.search(SchemaNamePolymorphicRefItem.ref.instanceOf(SchemaNamePolymorphicSuperItem.TYPE)));
		assertEquals(list(refItem), SchemaNamePolymorphicRefItem.TYPE.search(SchemaNamePolymorphicRefItem.ref.instanceOf(SchemaNamePolymorphicSubItem.TYPE)));
		assertEquals(list(item), SchemaNamePolymorphicSuperItem.TYPE.search(SchemaNamePolymorphicSuperItem.TYPE.getThis().instanceOf(SchemaNamePolymorphicSuperItem.TYPE)));
		assertEquals(list(item), SchemaNamePolymorphicSuperItem.TYPE.search(SchemaNamePolymorphicSuperItem.TYPE.getThis().instanceOf(SchemaNamePolymorphicSubItem.TYPE)));

		toSchema();
		{
			final String column = getTypeColumnName(SchemaNamePolymorphicSuperItem.TYPE);
			final String table = getTableName(SchemaNamePolymorphicSuperItem.TYPE);

			assertEquals(
					"SchemaNamePolymorphicSubItemRenamed",
					fetch("select " + q(column) + " from " + q(table)));

			assertEquals(
					hp(q(column)) + " IN ("+hp("'SchemaNamePolymorphicSuperItem'")+","+hp("'SchemaNamePolymorphicSubItemRenamed'")+")",
					model.getSchema().getTable(table).getConstraint("ScheNamePolySupeIte_cl_EN").getRequiredCondition());
		}
		{
			final String column = getTypeColumnName(SchemaNamePolymorphicRefItem.ref);
			final String table = getTableName(SchemaNamePolymorphicRefItem.TYPE);

			assertEquals(
					"SchemaNamePolymorphicSubItemRenamed",
					fetch("select " + q(column) + " from " + q(table)));

			assertEquals(
					hp(q(column)) + " IN ("+hp("'SchemaNamePolymorphicSuperItem'")+","+hp("'SchemaNamePolymorphicSubItemRenamed'")+")",
					model.getSchema().getTable(table).getConstraint("ScheNamPolRefIte_reTyp_EN").getRequiredCondition());
		}
		toModel();
		assertEquals(0, SchemaNamePolymorphicSubItem.TYPE.getThis().checkTypeColumn());
		assertEquals(0, SchemaNamePolymorphicSuperItem.TYPE.checkCompleteness(SchemaNamePolymorphicSubItem.TYPE));
		assertEquals(0, SchemaNamePolymorphicRefItem.ref.checkTypeColumn());

		// test update
		final SchemaNamePolymorphicSuperItem item2 = new SchemaNamePolymorphicSubItem();
		refItem.setRef(item2);
		toSchema();
		{
			final String column = getTypeColumnName(SchemaNamePolymorphicRefItem.ref);
			final String table = getTableName(SchemaNamePolymorphicRefItem.TYPE);

			assertEquals(
					"SchemaNamePolymorphicSubItemRenamed",
					fetch("select " + q(column) + " from " + q(table)));
		}
		toModel();
		assertEquals(0, SchemaNamePolymorphicSubItem.TYPE.getThis().checkTypeColumn());
		assertEquals(0, SchemaNamePolymorphicSuperItem.TYPE.checkCompleteness(SchemaNamePolymorphicSubItem.TYPE));
		assertEquals(0, SchemaNamePolymorphicRefItem.ref.checkTypeColumn());

		// test delete
		refItem.setRef(item);
		item2.deleteCopeItem();
	}

	private final void toSchema()
	{
		assertFalse(connection.isConnected());
		commit();
	}

	private final void toModel() throws SQLException
	{
		connection.close();
		startTransaction();
	}

	private String fetch(final String sql) throws SQLException
	{
		try(ResultSet rs = connection.executeQuery(sql))
		{
			assertTrue(rs.next());
			final String result = rs.getString(1);
			assertFalse(rs.next());
			return result;
		}
	}

	private final String q(final String s)
	{
		return SchemaInfo.quoteName(model, s);
	}

	private final String hp(final String s)
	{
		if(hsqldb)
			return "(" + s + ")";
		else
			return s;
	}
}
