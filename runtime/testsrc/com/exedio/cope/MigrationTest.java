/*
 * Copyright (C) 2004-2006  exedio GmbH (www.exedio.com)
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

import java.util.Arrays;
import java.util.Iterator;

import com.exedio.cope.junit.CopeAssert;
import com.exedio.dsmf.Column;
import com.exedio.dsmf.Driver;
import com.exedio.dsmf.Schema;
import com.exedio.dsmf.Table;

public class MigrationTest extends CopeAssert
{
	private static final Model model1 = new Model(5, new Migration[0], MigrationItem1.TYPE);
	
	private static final Migration[] migrationsMissing = new Migration[]{
			new Migration(7, "nonsense7", "nonsense statement causing a test failure if executed for version 7"),
		};
	
	private static final Model model2 = new Model(7, migrationsMissing, MigrationItem2.TYPE);
	
	public void testMigrations()
	{
		try
		{
			new Migration(-1, null, (String[])null);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("version must not be negative", e.getMessage());
		}
		try
		{
			new Migration(0, null, (String[])null);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals("comment must not be null", e.getMessage());
		}
		try
		{
			new Migration(0, "some comment", (String[])null);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals("body must not be null", e.getMessage());
		}
		try
		{
			new Migration(0, "some comment", new String[0]);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("body must not be empty", e.getMessage());
		}
	}
		
	public void testModel()
	{
		try
		{
			new Model(0, null, (Type[])null);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals("migrations must not be null", e.getMessage());
		}
		try
		{
			new Model(0, new Migration[]{new Migration(1, "migration1", "nonsensesql1"), null}, (Type[])null);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals("migration must not be null, but was at index 1", e.getMessage());
		}
		try
		{
			new Model(0, new Migration[]{
					new Migration(6, "migration6", "nonsensesql6"), 
					new Migration(8, "migration8", "nonsensesql8"), 
					}, (Type[])null);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("inconsistent migration version at index 1, expected 7, but was 8", e.getMessage());
		}
	}
	
	public void testMigrate()
	{
		final Properties props = new Properties();
		
		assertTrue(model1.isMigrationSupported());
		assertEquals(5, model1.getMigrationVersion());
		assertEqualsUnmodifiable(list(), model1.getMigrations());
		
		model1.connect(props);
		model1.tearDownDatabase();
		model1.createDatabase();
		
		assertSchema(model1.getVerifiedSchema(), false, false);
		model1.disconnect();
		
		assertTrue(model2.isMigrationSupported());
		assertEquals(7, model2.getMigrationVersion());
		assertEqualsUnmodifiable(list(migrationsMissing[0]), model2.getMigrations());

		model2.connect(props);
		assertSchema(model2.getVerifiedSchema(), true, false);

		try
		{
			model2.migrateIfSupported();
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("no migration for versions [6] on migration from 5 to 7", e.getMessage());
		}
		assertSchema(model2.getVerifiedSchema(), true, false);
		
		final Database database = model2.getDatabase();
		final Driver driver = database.driver;
		final Migration[] migrations2 = new Migration[]{
				new Migration(4, "nonsense", "nonsense statement causing a test failure if executed for version 4"),
				new Migration(5, "nonsense", "nonsense statement causing a test failure if executed for version 5"),
				// BEWARE:
				// Never do this in real projects,
				// always use plain string literals
				// containing the sql statement!
				new Migration(6, "add column field2a", driver.createColumn(driver.protectName("MigrationItem"), driver.protectName("field2a"), database.getStringType(100))),
				new Migration(7, "add column field2b", driver.createColumn(driver.protectName("MigrationItem"), driver.protectName("field2b"), database.getStringType(100))),
				new Migration(8, "nonsense", "nonsense statement causing a test failure if executed for version 8"),
				new Migration(9, "nonsense", "nonsense statement causing a test failure if executed for version 9"),
			};
		assertEquals("M6:add column field2a", migrations2[2].toString());
		assertEquals("M7:add column field2b", migrations2[3].toString());
		
		model2.setMigrations(migrations2);
		assertTrue(model2.isMigrationSupported());
		assertEquals(7, model2.getMigrationVersion());
		assertEqualsUnmodifiable(Arrays.asList(migrations2), model2.getMigrations());
		model2.migrateIfSupported();
		assertSchema(model2.getVerifiedSchema(), true, true);
		
		// test, that MigrationStep is not executed again,
		// causing a SQLException because column does already exist
		model2.migrate();
		assertSchema(model2.getVerifiedSchema(), true, true);
		
		model2.tearDownDatabase();
	}
	
	private void assertSchema(final Schema schema, final boolean model2, final boolean migrated)
	{
		final Table table = schema.getTable("MigrationItem");
		assertEquals("MigrationItem", table.getName());
		assertEquals(true, table.required());
		assertEquals(true, table.exists());
		final Iterator<Column> columns = table.getColumns().iterator();

		final Column columnThis = columns.next();
		assertEquals("this", columnThis.getName());
		assertEquals(true, columnThis.required());
		assertEquals(true, columnThis.exists());
		assertNotNull(columnThis.getType());
		
		final Column column1 = columns.next();
		assertEquals("field1", column1.getName());
		assertEquals(true, column1.required());
		assertEquals(true, column1.exists());
		assertNotNull(column1.getType());
		
		if(model2)
		{
			final Column column2a = columns.next();
			assertEquals("field2a", column2a.getName());
			assertEquals(true, column2a.required());
			assertEquals(migrated, column2a.exists());
			assertNotNull(column2a.getType());

			final Column column2b = columns.next();
			assertEquals("field2b", column2b.getName());
			assertEquals(true, column2b.required());
			assertEquals(migrated, column2b.exists());
			assertNotNull(column2b.getType());
		}
		
		assertFalse(columns.hasNext());
		
		final Table migrationTable = schema.getTable("while");
		assertEquals("while", migrationTable.getName());
		assertEquals(true, migrationTable.required());
		assertEquals(true, migrationTable.exists());
	}
}
