/*
 * Copyright (C) 2004-2007  exedio GmbH (www.exedio.com)
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


public class TableTest extends SchemaReadyTest
{
	private static final String TABLE1 = "SomeTable";
	private static final String TABLE1X = "SomeTableWrong";
	private static final String COLUMN1 = "someColumn";
	
	@Override
	protected Schema getSchema()
	{
		final Schema result = newSchema();

		final Table table = new Table(result, TABLE1);
		new Column(table, COLUMN1, intType);

		return result;
	}

	public void testTables()
	{
		// OK
		{
			final Schema schema = getVerifiedSchema();

			final Table table = schema.getTable(TABLE1);
			assertNotNull(table);
			assertEquals(true, table.required());
			assertEquals(true, table.exists());
			assertEquals(null, table.getError());
			assertEquals(Schema.Color.OK, table.getParticularColor());

			final Column column = table.getColumn(COLUMN1);
			assertEquals(true, column.required());
			assertEquals(true, column.exists());
			assertEquals(null, column.getError());
			assertEquals(Schema.Color.OK, column.getParticularColor());
			assertEquals(intType, column.getType());
			
			table.renameTo(TABLE1X);
		}
		// TABLE RENAMED
		{
			final Schema schema = getVerifiedSchema();

			{
				final Table table = schema.getTable(TABLE1);
				assertNotNull(table);
				assertEquals(true, table.required());
				assertEquals(false, table.exists());
				assertEquals("MISSING !!!", table.getError());
				assertEquals(Schema.Color.ERROR, table.getParticularColor());

				final Column column = table.getColumn(COLUMN1);
				assertEquals(true, column.required());
				assertEquals(false, column.exists());
				assertEquals("missing", column.getError());
				assertEquals(Schema.Color.ERROR, column.getParticularColor());
				assertEquals(intType, column.getType());
			}
			{
				final Table tableX = schema.getTable(TABLE1X);
				assertNotNull(tableX);
				assertEquals(false, tableX.required());
				assertEquals(true, tableX.exists());
				assertEquals("not used", tableX.getError());
				assertEquals(Schema.Color.WARNING, tableX.getParticularColor());

				final Column column = tableX.getColumn(COLUMN1);
				assertEquals(false, column.required());
				assertEquals(true, column.exists());
				assertEquals("not used", column.getError());
				assertEquals(Schema.Color.WARNING, column.getParticularColor());
				assertEquals(intType, column.getType());

				tableX.renameTo(TABLE1);
			}
		}
		// OK
		{
			final Schema schema = getVerifiedSchema();

			final Table table = schema.getTable(TABLE1);
			assertNotNull(table);
			assertEquals(true, table.required());
			assertEquals(true, table.exists());
			assertEquals(null, table.getError());
			assertEquals(Schema.Color.OK, table.getParticularColor());

			final Column column = table.getColumn(COLUMN1);
			assertEquals(true, column.required());
			assertEquals(true, column.exists());
			assertEquals(null, column.getError());
			assertEquals(Schema.Color.OK, column.getParticularColor());
			assertEquals(intType, column.getType());
			
			table.drop();
		}
		// TABLE DROPPED
		{
			final Schema schema = getVerifiedSchema();

			{
				final Table table = schema.getTable(TABLE1);
				assertNotNull(table);
				assertEquals(true, table.required());
				assertEquals(false, table.exists());
				assertEquals("MISSING !!!", table.getError());
				assertEquals(Schema.Color.ERROR, table.getParticularColor());

				final Column column = table.getColumn(COLUMN1);
				assertEquals(true, column.required());
				assertEquals(false, column.exists());
				assertEquals("missing", column.getError());
				assertEquals(Schema.Color.ERROR, column.getParticularColor());
				assertEquals(intType, column.getType());

				table.create();
			}
		}
		// OK
		{
			final Schema schema = getVerifiedSchema();

			final Table table = schema.getTable(TABLE1);
			assertNotNull(table);
			assertEquals(true, table.required());
			assertEquals(true, table.exists());
			assertEquals(null, table.getError());
			assertEquals(Schema.Color.OK, table.getParticularColor());

			final Column column = table.getColumn(COLUMN1);
			assertEquals(true, column.required());
			assertEquals(true, column.exists());
			assertEquals(null, column.getError());
			assertEquals(Schema.Color.OK, column.getParticularColor());
			assertEquals(intType, column.getType());
		}
	}
	
}
