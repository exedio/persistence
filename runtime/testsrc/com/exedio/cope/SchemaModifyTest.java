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
import static com.exedio.dsmf.Node.Color.ERROR;
import static com.exedio.dsmf.Node.Color.OK;
import static com.exedio.dsmf.Node.Color.WARNING;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.exedio.dsmf.Column;
import com.exedio.dsmf.Constraint;
import com.exedio.dsmf.Schema;
import org.junit.jupiter.api.Test;

public class SchemaModifyTest extends TestWithEnvironment
{
	public SchemaModifyTest()
	{
		super(SchemaTest.MODEL);
		copeRule.omitTransaction();
	}

	private static final String TABLE1X = "SchemaItemX";
	private static final String COLUMN1X = "nonFinalIntegerX";

	@Test void testSchema()
	{
		final String TABLE1 = getTableName(SchemaItem.TYPE);
		final String COLUMN1 = getColumnName(SchemaItem.nonFinalInteger);
		assertEquals(filterTableName("Main"), TABLE1);
		assertEquals("nonFinalInteger", COLUMN1);

		final String column1Type;
		// OK
		{
			final Schema schema = model.getVerifiedSchema();

			final com.exedio.dsmf.Table table = schema.getTable(TABLE1);
			assertNotNull(table);
			assertEquals(true, table.required());
			assertEquals(true, table.exists());
			assertEquals(null, table.getError());
			assertEquals(OK, table.getParticularColor());
			assertEquals(OK, table.getCumulativeColor());

			final Column column = table.getColumn(COLUMN1);
			assertEquals(true, column.required());
			assertEquals(true, column.exists());
			assertEquals(null, column.getError());
			assertEquals(OK, column.getParticularColor());
			column1Type = column.getType();
			assertNotNull(column1Type);

			column.renameTo(COLUMN1X);
		}
		// OK without verify
		{
			final Schema schema = model.getSchema();

			final com.exedio.dsmf.Table table = schema.getTable(TABLE1);
			assertNotNull(table);
			assertEquals(true, table.required());
			assertEquals(false, table.exists());

			final Column column = table.getColumn(COLUMN1);
			assertEquals(true, column.required());
			assertEquals(false, column.exists());
			assertEquals(column1Type, column.getType());
		}
		// COLUMN RENAMED
		{
			final Schema schema = model.getVerifiedSchema();

			final com.exedio.dsmf.Table table = schema.getTable(TABLE1);
			assertNotNull(table);
			assertEquals(true, table.required());
			assertEquals(true, table.exists());
			assertEquals(null, table.getError());
			assertEquals(OK, table.getParticularColor());

			{
				final Column column = table.getColumn(COLUMN1);
				assertEquals(true, column.required());
				assertEquals(false, column.exists());
				assertEquals("missing", column.getError());
				assertEquals(ERROR, column.getParticularColor());
				assertEquals(column1Type, column.getType());
			}
			{
				final Column columnX = table.getColumn(COLUMN1X);
				assertEquals(false, columnX.required());
				assertEquals(true, columnX.exists());
				assertEquals("unused", columnX.getError());
				assertEquals(WARNING, columnX.getParticularColor());
				assertEquals(column1Type, columnX.getType());

				columnX.renameTo(COLUMN1);
			}
		}
		// OK
		{
			final Schema schema = model.getVerifiedSchema();

			final com.exedio.dsmf.Table table = schema.getTable(TABLE1);
			assertNotNull(table);
			assertEquals(true, table.required());
			assertEquals(true, table.exists());
			assertEquals(null, table.getError());
			assertEquals(OK, table.getParticularColor());

			final Constraint constraint = table.getConstraint("Main_nonFinalInteger_MN");
			if(SchemaInfo.supportsCheckConstraints(model))
				constraint.drop();

			final Column column = table.getColumn(COLUMN1);
			assertEquals(true, column.required());
			assertEquals(true, column.exists());
			assertEquals(null, column.getError());
			assertEquals(OK, column.getParticularColor());
			assertEquals(column1Type, column.getType());

			column.drop();
		}
		// COLUMN DROPPED
		{
			final Schema schema = model.getVerifiedSchema();

			final com.exedio.dsmf.Table table = schema.getTable(TABLE1);
			assertNotNull(table);
			assertEquals(true, table.required());
			assertEquals(true, table.exists());
			assertEquals(null, table.getError());
			assertEquals(OK, table.getParticularColor());

			final Column column = table.getColumn(COLUMN1);
			assertEquals(true, column.required());
			assertEquals(false, column.exists());
			assertEquals("missing", column.getError());
			assertEquals(ERROR, column.getParticularColor());
			assertEquals(column1Type, column.getType());

			column.create();
		}
		// OK
		{
			final Schema schema = model.getVerifiedSchema();

			final com.exedio.dsmf.Table table = schema.getTable(TABLE1);
			assertNotNull(table);
			assertEquals(true, table.required());
			assertEquals(true, table.exists());
			assertEquals(null, table.getError());
			assertEquals(OK, table.getParticularColor());

			final Column column = table.getColumn(COLUMN1);
			assertEquals(true, column.required());
			assertEquals(true, column.exists());
			assertEquals(null, column.getError());
			assertEquals(OK, column.getParticularColor());
			assertEquals(column1Type, column.getType());

			table.renameTo(TABLE1X);
		}
		// TABLE RENAMED
		{
			final Schema schema = model.getVerifiedSchema();

			{
				final com.exedio.dsmf.Table table = schema.getTable(TABLE1);
				assertNotNull(table);
				assertEquals(true, table.required());
				assertEquals(false, table.exists());
				assertEquals("missing", table.getError());
				assertEquals(ERROR, table.getParticularColor());

				final Column column = table.getColumn(COLUMN1);
				assertEquals(true, column.required());
				assertEquals(false, column.exists());
				assertEquals("missing", column.getError());
				assertEquals(ERROR, column.getParticularColor());
				assertEquals(column1Type, column.getType());
			}
			{
				final com.exedio.dsmf.Table tableX = schema.getTable(TABLE1X);
				assertNotNull(tableX);
				assertEquals(false, tableX.required());
				assertEquals(true, tableX.exists());
				assertEquals("unused", tableX.getError());
				assertEquals(WARNING, tableX.getParticularColor());

				final Column column = tableX.getColumn(COLUMN1);
				assertEquals(false, column.required());
				assertEquals(true, column.exists());
				assertEquals("unused", column.getError());
				assertEquals(WARNING, column.getParticularColor());
				assertEquals(column1Type, column.getType());

				tableX.renameTo(TABLE1);
			}
		}
		// OK
		{
			final Schema schema = model.getVerifiedSchema();

			final com.exedio.dsmf.Table table = schema.getTable(TABLE1);
			assertNotNull(table);
			assertEquals(true, table.required());
			assertEquals(true, table.exists());
			assertEquals(null, table.getError());
			assertEquals(OK, table.getParticularColor());

			final Column column = table.getColumn(COLUMN1);
			assertEquals(true, column.required());
			assertEquals(true, column.exists());
			assertEquals(null, column.getError());
			assertEquals(OK, column.getParticularColor());
			assertEquals(column1Type, column.getType());

			table.drop();
		}
		// TABLE DROPPED
		{
			final Schema schema = model.getVerifiedSchema();

			{
				final com.exedio.dsmf.Table table = schema.getTable(TABLE1);
				assertNotNull(table);
				assertEquals(true, table.required());
				assertEquals(false, table.exists());
				assertEquals("missing", table.getError());
				assertEquals(ERROR, table.getParticularColor());

				final Column column = table.getColumn(COLUMN1);
				assertEquals(true, column.required());
				assertEquals(false, column.exists());
				assertEquals("missing", column.getError());
				assertEquals(ERROR, column.getParticularColor());
				assertEquals(column1Type, column.getType());

				table.create();
			}
		}
		// Foreign Key Constraint still missing
		{
			final Schema schema = model.getVerifiedSchema();

			final com.exedio.dsmf.Table table = schema.getTable(TABLE1);
			assertNotNull(table);
			assertEquals(true, table.required());
			assertEquals(true, table.exists());
			assertEquals(null, table.getError());
			assertEquals(OK, table.getParticularColor());
			assertEquals(ERROR, table.getCumulativeColor());

			final Column column = table.getColumn(COLUMN1);
			assertEquals(true, column.required());
			assertEquals(true, column.exists());
			assertEquals(null, column.getError());
			assertEquals(OK, column.getParticularColor());
			assertEquals(column1Type, column.getType());

			table.getConstraint("Main_item_Fk").create();
			table.getConstraint("Main_itemOpt_Fk").create();
			table.getConstraint("Main_poly_Fk").create();
			table.getConstraint("Main_polyOpt_Fk").create();
		}
		// OK
		{
			final Schema schema = model.getVerifiedSchema();

			final com.exedio.dsmf.Table table = schema.getTable(TABLE1);
			assertNotNull(table);
			assertEquals(true, table.required());
			assertEquals(true, table.exists());
			assertEquals(null, table.getError());
			assertEquals(OK, table.getParticularColor());
			assertEquals(OK, table.getCumulativeColor());

			final Column column = table.getColumn(COLUMN1);
			assertEquals(true, column.required());
			assertEquals(true, column.exists());
			assertEquals(null, column.getError());
			assertEquals(OK, column.getParticularColor());
			assertEquals(column1Type, column.getType());
		}
	}
}
