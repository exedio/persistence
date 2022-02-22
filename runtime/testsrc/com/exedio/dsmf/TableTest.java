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
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.ArrayList;
import org.junit.jupiter.api.Test;

public class TableTest extends SchemaReadyTest
{
	private static final String TABLE1 = "SomeTable";
	private static final String TABLE1X = "SomeTableWrong";
	private static final String COLUMN1 = "someColumn";

	private Table table;
	private Column column;

	@Override
	protected Schema getSchema()
	{
		final Schema result = newSchema();

		table = result.newTable(TABLE1);
		column = table.newColumn(COLUMN1, intType);

		return result;
	}

	@Test void testTables()
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

			table.renameTo(TABLE1X);
		}
		// TABLE RENAMED
		{
			final Schema schema = getVerifiedSchema();

			{
				assertSame(table, schema.getTable(TABLE1));
				assertNotNull(table);
				assertEquals(true, table.required());
				assertEquals(false, table.exists());
				assertEquals("missing", table.getError());
				assertEquals(ERROR, table.getParticularColor());

				assertSame(column, table.getColumn(COLUMN1));
				assertEquals(true, column.required());
				assertEquals(false, column.exists());
				assertEquals("missing", column.getError());
				assertEquals(ERROR, column.getParticularColor());
				assertEquals(intType, column.getType());
			}
			{
				final Table tableX = schema.getTable(TABLE1X);
				assertNotNull(tableX);
				assertEquals(false, tableX.required());
				assertEquals(true, tableX.exists());
				assertEquals("unused", tableX.getError());
				assertEquals(WARNING, tableX.getParticularColor());
				assertNotSame(table, tableX);

				final Column columnX = tableX.getColumn(COLUMN1);
				assertEquals(false, columnX.required());
				assertEquals(true, columnX.exists());
				assertEquals("unused", columnX.getError());
				assertEquals(WARNING, columnX.getParticularColor());
				assertEquals(intType, columnX.getType());
				assertNotSame(column, columnX);

				tableX.renameTo(TABLE1);
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

			table.drop();
		}
		// TABLE DROPPED
		{
			final Schema schema = getVerifiedSchema();

			{
				assertSame(table, schema.getTable(TABLE1));
				assertNotNull(table);
				assertEquals(true, table.required());
				assertEquals(false, table.exists());
				assertEquals("missing", table.getError());
				assertEquals(ERROR, table.getParticularColor());

				assertSame(column, table.getColumn(COLUMN1));
				assertEquals(true, column.required());
				assertEquals(false, column.exists());
				assertEquals("missing", column.getError());
				assertEquals(ERROR, column.getParticularColor());
				assertEquals(intType, column.getType());

				table.create();
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
		}
	}

	@Test void testStatementListener()
	{
		final Schema schema = getVerifiedSchema();
		final Table table = schema.getTable(TABLE1);
		final MyListener listener = new MyListener();
		table.drop(listener);
		final String name = schema.dialect.quoteName(TABLE1);
		assertEquals(asList(
				"BEFORE: DROP TABLE " + name,
				"AFTER:  DROP TABLE " + name + " (0)"),
				listener.log);
	}

	static final class MyListener implements StatementListener
	{
		final ArrayList<String> log = new ArrayList<>();

		@Override
		public boolean beforeExecute(final String statement)
		{
			log.add("BEFORE: " + statement);
			return true;
		}

		@Override
		public void afterExecute(final String statement, final int rows)
		{
			log.add("AFTER:  " + statement + " (" + rows + ")");
		}
	}
}
