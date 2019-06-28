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

package com.exedio.dsmf;

import static com.exedio.dsmf.Node.Color.ERROR;
import static com.exedio.dsmf.Node.Color.OK;
import static com.exedio.dsmf.Node.Color.WARNING;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

public class ColumnTest extends SchemaReadyTest
{
	private static final String TABLE1 = "SomeOtherTable";
	private static final String COLUMN1 = "someColumn";
	private static final String COLUMN1X = "someColumnWrong";

	private Table table;
	private Column column;

	@Override
	protected Schema getSchema()
	{
		final Schema result = newSchema();

		table = result.newTable(TABLE1);
		column = table.newColumn(COLUMN1, intType);
		table.newColumn("otherColumn", stringType);

		return result;
	}

	@Test void testColumns()
	{
		// OK
		{
			final Schema schema = getVerifiedSchema();

			assertSame(table, schema.getTable(TABLE1));
			assertNotNull(table);
			assertEquals(true, table.required());
			assertEquals(true, table.exists());
			assertEquals(null, table.getError());
			assertEquals(OK, table.getParticularColor());

			assertSame(column, table.getColumn(COLUMN1));
			assertEquals(true, column.required());
			assertEquals(true, column.exists());
			assertEquals(null, column.getError());
			assertEquals(OK, column.getParticularColor());
			assertEquals(intType, column.getType());

			column.renameTo(COLUMN1X);
		}
		// COLUMN RENAMED
		{
			final Schema schema = getVerifiedSchema();

			assertSame(table, schema.getTable(TABLE1));
			assertNotNull(table);
			assertEquals(true, table.required());
			assertEquals(true, table.exists());
			assertEquals(null, table.getError());
			assertEquals(OK, table.getParticularColor());

			{
				assertSame(column, table.getColumn(COLUMN1));
				assertEquals(true, column.required());
				assertEquals(false, column.exists());
				assertEquals("missing", column.getError());
				assertEquals(ERROR, column.getParticularColor());
				assertEquals(intType, column.getType());
			}
			{
				final Column columnX = table.getColumn(COLUMN1X);
				assertEquals(false, columnX.required());
				assertEquals(true, columnX.exists());
				assertEquals("not used", columnX.getError());
				assertEquals(WARNING, columnX.getParticularColor());
				assertEquals(intType, columnX.getType());
				assertNotSame(column, columnX);

				columnX.renameTo(COLUMN1);
			}
		}
		// OK
		{
			final Schema schema = getVerifiedSchema();

			assertSame(table, schema.getTable(TABLE1));
			assertNotNull(table);
			assertEquals(true, table.required());
			assertEquals(true, table.exists());
			assertEquals(null, table.getError());
			assertEquals(OK, table.getParticularColor());

			assertSame(column, table.getColumn(COLUMN1));
			assertEquals(true, column.required());
			assertEquals(true, column.exists());
			assertEquals(null, column.getError());
			assertEquals(OK, column.getParticularColor());
			assertEquals(intType, column.getType());

			column.modify(intType2);
		}
		// COLUMN MODIFIED
		{
			final Schema schema = getVerifiedSchema();

			assertSame(table, schema.getTable(TABLE1));
			assertNotNull(table);
			assertEquals(true, table.required());
			assertEquals(true, table.exists());
			assertEquals(null, table.getError());
			assertEquals(OK, table.getParticularColor());

			assertSame(column, table.getColumn(COLUMN1));
			assertEquals(true, column.required());
			assertEquals(true, column.exists());
			assertEquals("unexpected type >"+intType2+"<", column.getError());
			assertEquals(ERROR, column.getParticularColor());
			assertEquals(intType, column.getType());
			column.modify(intType);
		}
		// OK
		{
			final Schema schema = getVerifiedSchema();

			assertSame(table, schema.getTable(TABLE1));
			assertNotNull(table);
			assertEquals(true, table.required());
			assertEquals(true, table.exists());
			assertEquals(null, table.getError());
			assertEquals(OK, table.getParticularColor());

			assertSame(column, table.getColumn(COLUMN1));
			assertEquals(true, column.required());
			assertEquals(true, column.exists());
			assertEquals(null, column.getError());
			assertEquals(OK, column.getParticularColor());
			assertEquals(intType, column.getType());

			column.drop();
		}
		// COLUMN DROPPED
		{
			final Schema schema = getVerifiedSchema();

			assertSame(table, schema.getTable(TABLE1));
			assertNotNull(table);
			assertEquals(true, table.required());
			assertEquals(true, table.exists());
			assertEquals(null, table.getError());
			assertEquals(OK, table.getParticularColor());

			assertSame(column, table.getColumn(COLUMN1));
			assertEquals(true, column.required());
			assertEquals(false, column.exists());
			assertEquals("missing", column.getError());
			assertEquals(ERROR, column.getParticularColor());
			assertEquals(intType, column.getType());

			column.create();
		}
		// OK
		{
			final Schema schema = getVerifiedSchema();

			assertSame(table, schema.getTable(TABLE1));
			assertNotNull(table);
			assertEquals(true, table.required());
			assertEquals(true, table.exists());
			assertEquals(null, table.getError());
			assertEquals(OK, table.getParticularColor());

			assertSame(column, table.getColumn(COLUMN1));
			assertEquals(true, column.required());
			assertEquals(true, column.exists());
			assertEquals(null, column.getError());
			assertEquals(OK, column.getParticularColor());
			assertEquals(intType, column.getType());
		}
	}

}
