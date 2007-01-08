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

public class ColumnTest extends SchemaReadyTest
{
	private static final String TABLE1 = "SomeOtherTable";
	private static final String COLUMN1 = "someColumn";
	private static final String COLUMN1X = "someColumnWrong";

	@Override
	protected Schema getSchema()
	{
		final Schema result = newSchema();

		final Table table = new Table(result, TABLE1);
		new Column(table, COLUMN1, intType);
		new Column(table, "otherColumn", stringType);

		return result;
	}
	
	public void testColumns()
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
			
			column.renameTo(COLUMN1X);
		}
		// COLUMN RENAMED
		{
			final Schema schema = getVerifiedSchema();

			final Table table = schema.getTable(TABLE1);
			assertNotNull(table);
			assertEquals(true, table.required());
			assertEquals(true, table.exists());
			assertEquals(null, table.getError());
			assertEquals(Schema.Color.OK, table.getParticularColor());

			{
				final Column column = table.getColumn(COLUMN1);
				assertEquals(true, column.required());
				assertEquals(false, column.exists());
				assertEquals("missing", column.getError());
				assertEquals(Schema.Color.ERROR, column.getParticularColor());
				assertEquals(intType, column.getType());
			}
			{
				final Column columnX = table.getColumn(COLUMN1X);
				assertEquals(false, columnX.required());
				assertEquals(true, columnX.exists());
				assertEquals("not used", columnX.getError());
				assertEquals(Schema.Color.WARNING, columnX.getParticularColor());
				assertEquals(intType, columnX.getType());

				columnX.renameTo(COLUMN1);
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

			if(intType2!=null)
				column.modify(intType2);
		}
		// COLUMN MODIFIED
		if(intType2!=null)
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
			assertEquals("different type in database: >"+intType2+"<", column.getError());
			assertEquals(Schema.Color.ERROR, column.getParticularColor());
			assertEquals(intType, column.getType());
			column.modify(intType);
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

			column.drop();
		}
		// COLUMN DROPPED
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
			assertEquals(false, column.exists());
			assertEquals("missing", column.getError());
			assertEquals(Schema.Color.ERROR, column.getParticularColor());
			assertEquals(intType, column.getType());

			column.create();
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
