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
import static com.exedio.cope.instrument.Visibility.NONE;
import static com.exedio.dsmf.Node.Color.ERROR;
import static com.exedio.dsmf.Node.Color.OK;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.tojunit.ConnectionRule;
import com.exedio.cope.tojunit.SI;
import com.exedio.dsmf.Column;
import com.exedio.dsmf.Node.Color;
import com.exedio.dsmf.Schema;
import com.exedio.dsmf.Table;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;

@SuppressFBWarnings("SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE")
public class MysqlSchemaEngineTest extends TestWithEnvironment
{
	public MysqlSchemaEngineTest()
	{
		super(MODEL);
		copeRule.omitTransaction();
	}

	private final ConnectionRule connection = new ConnectionRule(model);

	@Test void test() throws SQLException
	{
		assertSchema(null, OK);

		assumeTrue(mysql, "mysql");

		connection.execute(
				"ALTER TABLE " + SI.tab(MyItem.TYPE) + " ENGINE=myisam");

		assertSchema("unexpected engine >MyISAM<", ERROR);

		// test additionalError together with error in nested node
		model.getSchema().
				getTable(getTableName(MyItem.TYPE)).
				getColumn(getPrimaryKeyColumnName(MyItem.TYPE)).
				modify("bigint");

		assertSchema("unexpected engine >MyISAM<", ERROR, "unexpected type >bigint<", ERROR);
	}

	private void assertSchema(
			final String tableError, final Color tableColor)
	{
		assertSchema(tableError, tableColor, null, OK);
	}

	private void assertSchema(
			final String tableError, final Color tableColor,
			final String columnError, final Color columnColor)
	{
		final Schema schema = model.getVerifiedSchema();
		final Table table = schema.getTable(getTableName(MyItem.TYPE));
		final Column column = table.getColumn(getPrimaryKeyColumnName(MyItem.TYPE));
		assertEquals(columnError, column.getError(),           "column.error");
		assertEquals(columnColor, column.getParticularColor(), "column.particularColor");
		assertEquals(columnColor, column.getCumulativeColor(), "column.cumulativeColor");
		assertEquals(tableError,  table .getError(),           "table.error");
		assertEquals(tableColor,  table .getParticularColor(), "table.particularColor");
		assertEquals(tableColor,  table .getCumulativeColor(), "table.cumulativeColor");
		assertEquals(null,        schema.getError(),           "schema.error");
		assertEquals(OK,          schema.getParticularColor(), "schema.particularColor");
		assertEquals(tableColor,  schema.getCumulativeColor(), "schema.cumulativeColor");
	}

	@CopeSchemaName("MysqlSchemaEngineTest")
	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	static final class MyItem extends Item
	{
		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		static final com.exedio.cope.Type<MyItem> TYPE = com.exedio.cope.TypesBound.newType(MyItem.class);

		@com.exedio.cope.instrument.Generated
		private MyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	static final Model MODEL = new Model(MyItem.TYPE);
}
