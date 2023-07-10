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

import static com.exedio.cope.SchemaInfo.checkCompleteness;
import static com.exedio.cope.SchemaInfo.checkTypeColumn;
import static com.exedio.cope.SchemaInfo.getTableName;
import static com.exedio.cope.SchemaInfo.getTypeColumnName;
import static com.exedio.cope.SchemaInfo.getTypeColumnValue;
import static com.exedio.cope.tojunit.Assert.list;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.exedio.cope.tojunit.ConnectionRule;
import com.exedio.cope.tojunit.MainRule;
import com.exedio.cope.tojunit.SI;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;

@MainRule.Tag
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

	@Test void test() throws SQLException
	{
		assertEquals("Super"     , getTypeColumnValue(SchemaNamePolymorphicSuperItem.TYPE));
		assertEquals("SubRenamed", getTypeColumnValue(SchemaNamePolymorphicSubItem  .TYPE));

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

			assertEquals("SubRenamed", fetch("select " + q(column) + " from " + q(table)));
			assertEquals(
					hp(q(column)) + " IN ("+hp("'SubRenamed'")+","+sac()+hp("'Super'")+")",
					model.getSchema().getTable(table).getConstraint("Super_class_EN").getRequiredCondition());
		}
		{
			final String column = getTypeColumnName(SchemaNamePolymorphicRefItem.ref);
			final String table = getTableName(SchemaNamePolymorphicRefItem.TYPE);

			assertEquals("SubRenamed", fetch("select " + q(column) + " from " + q(table)));
			assertEquals(
					hp(q(column)) + " IN ("+hp("'SubRenamed'")+","+sac()+hp("'Super'")+")",
					model.getSchema().getTable(table).getConstraint("Ref_refType_EN").getRequiredCondition());
		}
		toModel();
		assertEquals(
				"SELECT COUNT(*) FROM " + q("SubRenamed") + "," + q("Super") + " " +
				"WHERE " + q("SubRenamed") + "." + SI.pk(SchemaNamePolymorphicSubItem.TYPE) + "=" + q("Super") + "." + SI.pk(SchemaNamePolymorphicSuperItem.TYPE) + " " +
				"AND 'SubRenamed'<>" + q("Super") + "." + SI.type(SchemaNamePolymorphicSuperItem.TYPE),
				checkTypeColumn(SchemaNamePolymorphicSubItem.TYPE.getThis()));
		assertEquals(
				"SELECT COUNT(*) FROM " + q("Super") + " " +
				"LEFT JOIN " + q("SubRenamed") + " " +
				"ON " + q("Super") + "." + SI.pk(SchemaNamePolymorphicSuperItem.TYPE) + "=" + q("SubRenamed") + "." + SI.pk(SchemaNamePolymorphicSubItem.TYPE) + " " +
				"WHERE " + q("SubRenamed") + "." + SI.pk(SchemaNamePolymorphicSubItem.TYPE) + " IS NULL " +
				"AND " + q("Super") + "." + SI.type(SchemaNamePolymorphicSuperItem.TYPE) + "='SubRenamed'",
				checkCompleteness(SchemaNamePolymorphicSuperItem.TYPE, SchemaNamePolymorphicSubItem.TYPE));
		final String alias1 = SchemaInfo.quoteName(model, "return");
		final String alias2 = SchemaInfo.quoteName(model, "break");
		assertEquals(
				"SELECT COUNT(*) FROM " + q("Ref") + " " + alias1 + "," + q("Super") + " " + alias2 + " " +
				"WHERE " + alias1 + "." + q("ref") + "=" + alias2 + "." + SI.pk(SchemaNamePolymorphicSuperItem.TYPE) + " " +
				"AND " + alias1 + "." + q("refType") + "<>" + alias2 + "." + SI.type(SchemaNamePolymorphicSuperItem.TYPE),
				checkTypeColumn(SchemaNamePolymorphicRefItem.ref));
		assertEquals(0, SchemaNamePolymorphicSubItem.TYPE.getThis().checkTypeColumnL());
		assertEquals(0, SchemaNamePolymorphicSuperItem.TYPE.checkCompletenessL(SchemaNamePolymorphicSubItem.TYPE));
		assertEquals(0, SchemaNamePolymorphicRefItem.ref.checkTypeColumnL());

		// test update
		final SchemaNamePolymorphicSuperItem item2 = new SchemaNamePolymorphicSubItem();
		refItem.setRef(item2);
		toSchema();
		{
			final String column = getTypeColumnName(SchemaNamePolymorphicRefItem.ref);
			final String table = getTableName(SchemaNamePolymorphicRefItem.TYPE);

			assertEquals("SubRenamed", fetch("select " + q(column) + " from " + q(table)));
		}
		toModel();
		assertEquals(0, SchemaNamePolymorphicSubItem.TYPE.getThis().checkTypeColumnL());
		assertEquals(0, SchemaNamePolymorphicSuperItem.TYPE.checkCompletenessL(SchemaNamePolymorphicSubItem.TYPE));
		assertEquals(0, SchemaNamePolymorphicRefItem.ref.checkTypeColumnL());

		// test delete
		refItem.setRef(item);
		item2.deleteCopeItem();
	}

	private void toSchema()
	{
		assertFalse(connection.isConnected());
		commit();
	}

	private void toModel() throws SQLException
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

	private String q(final String s)
	{
		return SchemaInfo.quoteName(model, s);
	}

	private static String hp(final String s)
	{
		return s;
	}
}
