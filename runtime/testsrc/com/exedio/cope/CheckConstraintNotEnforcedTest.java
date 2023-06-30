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

import static com.exedio.cope.SchemaInfo.getPrimaryKeyColumnName;
import static com.exedio.cope.SchemaInfo.getTableName;
import static com.exedio.cope.SchemaInfo.supportsCheckConstraint;
import static com.exedio.cope.instrument.Visibility.NONE;
import static com.exedio.dsmf.Node.Color.OK;
import static com.exedio.dsmf.Node.Color.WARNING;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.tojunit.ConnectionRule;
import com.exedio.cope.tojunit.SI;
import com.exedio.dsmf.Column;
import com.exedio.dsmf.Constraint;
import com.exedio.dsmf.Node.Color;
import com.exedio.dsmf.Schema;
import com.exedio.dsmf.Table;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;

public class CheckConstraintNotEnforcedTest extends TestWithEnvironment
{
	public CheckConstraintNotEnforcedTest()
	{
		super(MODEL);
		copeRule.omitTransaction();
	}

	private final ConnectionRule connection = new ConnectionRule(model);

	@Test void test() throws SQLException
	{
		final boolean supported = supportsCheckConstraint(MODEL);
		assertSchema(supported ? null: "unsupported", OK);

		assumeTrue(supported && mysql, "mysql8");

		connection.execute(
				"ALTER TABLE " + SI.tab(MyItem.TYPE) + " " +
				"ALTER CHECK " + NAME + " " +
				"NOT ENFORCED");

		assertSchema("missing", WARNING);
	}

	private void assertSchema(
			final String error, final Color color)
	{
		final Schema schema = model.getVerifiedSchema();
		final Table table = schema.getTable(getTableName(MyItem.TYPE));
		final Constraint constraint = table.getConstraint(NAME);
		final Column column = table.getColumn(getPrimaryKeyColumnName(MyItem.TYPE));
		assertAll(
				() -> assertEquals(error, constraint.getError(),           "constraint.error"),
				() -> assertEquals(color, constraint.getParticularColor(), "constraint.particularColor"),
				() -> assertEquals(color, constraint.getCumulativeColor(), "constraint.cumulativeColor"),
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

	private static final String NAME = "MyItem_this_MN";

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static final class MyItem extends Item
	{
		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<MyItem> TYPE = com.exedio.cope.TypesBound.newType(MyItem.class,MyItem::new);

		@com.exedio.cope.instrument.Generated
		private MyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	static final Model MODEL = new Model(MyItem.TYPE);
}
