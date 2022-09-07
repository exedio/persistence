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

import static com.exedio.dsmf.Dialect.notifyExistentColumn;
import static com.exedio.dsmf.GraphTest.newHsqldbDialect;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.fail;

import java.sql.Connection;
import org.junit.jupiter.api.Test;

public class NodeTest
{
	@Test void testColumnOk() throws ReflectiveOperationException
	{
		final Schema schema = new Schema(newHsqldbDialect(), connectionProvider);
		final Table table = schema.newTable("tabName");
		final Column c = table.newColumn("colName", "requiredType");

		assertSame(table, c.getTable());
		assertEquals("colName", c.getName());
		assertEquals("requiredType", c.getType());
		assertEquals(true, c.required());
		assertEquals(false, c.exists());
		assertEquals(false, c.mismatchesType());
		assertEquals("requiredType", c.getRequiredType());
		try
		{
			c.getExistingType();
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("not existing", e.getMessage());
		}

		assertSame(c, notifyExistentColumn(table, "colName", "requiredType"));
		assertSame(table, c.getTable());
		assertEquals("colName", c.getName());
		assertEquals("requiredType", c.getType());
		assertEquals(true, c.required());
		assertEquals(true, c.exists());
		assertEquals(false, c.mismatchesType());
		assertEquals("requiredType", c.getRequiredType());
		assertEquals("requiredType", c.getExistingType());
	}

	@Test void testColumnWrongName() throws ReflectiveOperationException
	{
		final Schema schema = new Schema(newHsqldbDialect(), connectionProvider);
		final Table table = schema.newTable("tabName");
		final Column c = table.newColumn("colName", "requiredType");

		assertSame(table, c.getTable());
		assertEquals("colName", c.getName());
		assertEquals("requiredType", c.getType());
		assertEquals(true, c.required());
		assertEquals(false, c.exists());
		assertEquals(false, c.mismatchesType());
		assertEquals("requiredType", c.getRequiredType());
		try
		{
			c.getExistingType();
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("not existing", e.getMessage());
		}

		final Column c2 = notifyExistentColumn(table, "colName2", "requiredType");
		assertNotSame(c, c2);

		assertSame(table, c.getTable());
		assertEquals("colName", c.getName());
		assertEquals("requiredType", c.getType());
		assertEquals(true, c.required());
		assertEquals(false, c.exists());
		assertEquals(false, c.mismatchesType());
		assertEquals("requiredType", c.getRequiredType());
		try
		{
			c.getExistingType();
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("not existing", e.getMessage());
		}

		assertSame(table, c2.getTable());
		assertEquals("colName2", c2.getName());
		assertEquals("requiredType", c2.getType());
		assertEquals(false, c2.required());
		assertEquals(true, c2.exists());
		assertEquals(false, c2.mismatchesType());
		try
		{
			c2.getRequiredType();
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("not required", e.getMessage());
		}
		assertEquals("requiredType", c2.getExistingType());
	}

	@Test void testColumnWrongType() throws ReflectiveOperationException
	{
		final Schema schema = new Schema(newHsqldbDialect(), connectionProvider);
		final Table table = schema.newTable("tabName");
		final Column c = table.newColumn("colName", "requiredType");

		assertSame(table, c.getTable());
		assertEquals("colName", c.getName());
		assertEquals("requiredType", c.getType());
		assertEquals(true, c.required());
		assertEquals(false, c.exists());
		assertEquals(false, c.mismatchesType());
		assertEquals("requiredType", c.getRequiredType());
		try
		{
			c.getExistingType();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("not existing", e.getMessage());
		}

		assertSame(c, notifyExistentColumn(table, "colName", "existingType"));
		assertSame(table, c.getTable());
		assertEquals("colName", c.getName());
		assertEquals("requiredType", c.getType());
		assertEquals(true, c.required());
		assertEquals(true, c.exists());
		assertEquals(true, c.mismatchesType());
		assertEquals("requiredType", c.getRequiredType());
		assertEquals("existingType", c.getExistingType());
	}

	@Test void testColumnNonRequires() throws ReflectiveOperationException
	{
		final Schema schema = new Schema(newHsqldbDialect(), connectionProvider);
		final Table table = schema.newTable("tabName");

		final Column c = notifyExistentColumn(table, "colName", "existingType");
		assertSame(table, c.getTable());
		assertEquals("colName", c.getName());
		assertEquals("existingType", c.getType());
		assertEquals(false, c.required());
		assertEquals(true, c.exists());
		assertEquals(false, c.mismatchesType());
		try
		{
			c.getRequiredType();
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("not required", e.getMessage());
		}
		assertEquals("existingType", c.getExistingType());
	}


	private static final ConnectionProvider connectionProvider = new ConnectionProvider()
	{
		@Override
		public Connection getConnection()
		{
			throw new RuntimeException();
		}

		@Override
		public void putConnection(final Connection connection)
		{
			throw new RuntimeException();
		}
	};
}
