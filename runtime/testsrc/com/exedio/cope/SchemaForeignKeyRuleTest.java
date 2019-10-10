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

import static com.exedio.cope.SchemaInfo.getColumnName;
import static com.exedio.cope.SchemaInfo.getTableName;
import static com.exedio.cope.instrument.Visibility.NONE;
import static com.exedio.dsmf.Node.Color.ERROR;
import static com.exedio.dsmf.Node.Color.OK;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.instrument.WrapperIgnore;
import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.tojunit.ConnectionRule;
import com.exedio.cope.tojunit.SI;
import com.exedio.dsmf.Column;
import com.exedio.dsmf.Constraint;
import com.exedio.dsmf.Node.Color;
import com.exedio.dsmf.Schema;
import com.exedio.dsmf.Table;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;

@SuppressFBWarnings("SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE")
public class SchemaForeignKeyRuleTest extends TestWithEnvironment
{
	public SchemaForeignKeyRuleTest()
	{
		super(MODEL);
		copeRule.omitTransaction();
	}

	private final ConnectionRule connection = new ConnectionRule(model);

	@Test void test() throws SQLException
	{
		assertSchema(null, OK);

		testRules(
				"ON DELETE Cascade",
				"unexpected delete rule CASCADE");

		if(oracle) // does not support update rules
			return;

		testRules(
				"ON UPDATE set NULL",
				"unexpected update rule SET NULL");

		testRules(
				"ON DELETE SET NULL ON UPDATE CASCADE",
				"unexpected delete rule SET NULL, unexpected update rule CASCADE");
	}

	private void assertSchema(final String error, final Color color)
	{
		final Schema schema = model.getVerifiedSchema();
		final Table table = schema.getTable(getTableName(MyItem.TYPE));
		final Column column = table.getColumn(getColumnName(MyItem.field));
		final Constraint fk = table.getConstraint(FK_NAME);
		assertAll(
				() -> assertEquals(error, fk.getError(),               "fk.error"),
				() -> assertEquals(color, fk.getParticularColor(),     "fk.particularColor"),
				() -> assertEquals(color, fk.getCumulativeColor(),     "fk.cumulativeColor"),
				() -> assertEquals(null,  column.getError(),           "column.error"),
				() -> assertEquals(OK,    column.getParticularColor(), "column.particularColor"),
				() -> assertEquals(color, column.getCumulativeColor(), "column.cumulativeColor"),
				() -> assertEquals(null,  table .getError(),           "table.error"),
				() -> assertEquals(OK,    table .getParticularColor(), "table.particularColor"),
				() -> assertEquals(color, table .getCumulativeColor(), "table.cumulativeColor"),
				() -> assertEquals(null,  schema.getError(),           "schema.error"),
				() -> assertEquals(OK,    schema.getParticularColor(), "schema.particularColor"),
				() -> assertEquals(color, schema.getCumulativeColor(), "schema.cumulativeColor"));
	}

	private void testRules(final String rule, final String error) throws SQLException
	{
		model.
				getSchema().
				getTable(getTableName(MyItem.TYPE)).
				getConstraint(FK_NAME).
				drop();
		assertSchema("missing", ERROR);

		connection.execute(
				"ALTER TABLE " + SI.tab(MyItem.TYPE) + " " +
				"ADD CONSTRAINT " + SchemaInfo.quoteName(model, FK_NAME) + " " +
				"FOREIGN KEY (" + SI.col(MyItem.field) + ") " +
				"REFERENCES " + SI.tab(MyItem.TYPE) + "(" + SI.pk(MyItem.TYPE) + ") " +
				rule);
		assertSchema(error, ERROR);
	}

	private static final String FK_NAME = "ForeignKeyRule_field_Fk";

	@CopeSchemaName("ForeignKeyRule")
	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	static final class MyItem extends Item
	{
		@WrapperIgnore
		static final ItemField<MyItem> field = ItemField.create(MyItem.class).optional(); // optional needed for SET NULL

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		static final com.exedio.cope.Type<MyItem> TYPE = com.exedio.cope.TypesBound.newType(MyItem.class);

		@com.exedio.cope.instrument.Generated
		private MyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	static final Model MODEL = new Model(MyItem.TYPE);
}
