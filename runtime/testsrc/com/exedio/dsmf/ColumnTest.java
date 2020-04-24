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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
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


	@Test void testUpdate()
	{
		final Schema schema = getSchema();
		final String tableName = schema.quoteName(table.getName());
		final String columnName = schema.quoteName(column.getName());
		schema.querySQL("SELECT * FROM " + tableName, new UpdateResultSetHandler());

		schema.executeSQL("INSERT INTO " + tableName + " (" + columnName + ") VALUES (NULL)", null);
		schema.executeSQL("INSERT INTO " + tableName + " (" + columnName + ") VALUES (55)", null);
		schema.executeSQL("INSERT INTO " + tableName + " (" + columnName + ") VALUES (66)", null);
		final String selectSQL = "SELECT " + columnName + " FROM " + tableName;
		schema.querySQL(selectSQL, new UpdateResultSetHandler(null, "55", "66"));

		column.update("88", null);
		schema.querySQL(selectSQL, new UpdateResultSetHandler("88", "88", "88"));
	}

	private static final class UpdateResultSetHandler implements Dialect.ResultSetHandler
	{
		private final List<String> expected;

		private UpdateResultSetHandler(final String... expected)
		{
			this.expected = Arrays.asList(expected);
		}

		@Override
		public void run(final ResultSet resultSet) throws SQLException
		{
			final ArrayList<String> actual = new ArrayList<>();
			while(resultSet.next())
				actual.add(resultSet.getString(1));
			actual.sort(Comparator.nullsFirst(Comparator.naturalOrder()));
			assertEquals(expected, actual);
		}
	}
}
