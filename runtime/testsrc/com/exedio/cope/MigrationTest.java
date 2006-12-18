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
import java.util.Map;

import com.exedio.cope.junit.CopeAssert;
import com.exedio.dsmf.Column;
import com.exedio.dsmf.Driver;
import com.exedio.dsmf.Schema;
import com.exedio.dsmf.Table;

public class MigrationTest extends CopeAssert
{
	public void testMigration()
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
		
		assertEquals("M123:test-comment", new Migration(123, "test-comment", "sql1", "sql2").toString());
	}
		
	public void testModel()
	{
		try
		{
			new Model(null, (Type[])null);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals("migrations must not be null", e.getMessage());
		}
		try
		{
			new Model(new Migration[]{new Migration(1, "migration1", "nonsensesql1"), null}, (Type[])null);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals("migration must not be null, but was at index 1", e.getMessage());
		}
		try
		{
			new Model(new Migration[]{
					new Migration(8, "migration8", "nonsensesql8"), 
					new Migration(6, "migration6", "nonsensesql6"), 
					}, (Type[])null);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("inconsistent migration version at index 1, expected 7, but was 6", e.getMessage());
		}
	}
	
	private static final Migration[] migrations5 = new Migration[]{
		new Migration(5, "nonsense5", "nonsense statement causing a test failure if executed for version 5"),
	};
	
	private static final Model model5 = new Model(migrations5, MigrationItem1.TYPE);
	
	
	private static final Migration[] migrations7Missing = new Migration[]{
			new Migration(7, "nonsense7", "nonsense statement causing a test failure if executed for version 7"),
		};
	
	private static final Model model7 = new Model(migrations7Missing, MigrationItem2.TYPE);
	
	public void testMigrate()
	{
		final Properties props = new Properties();
		
		assertTrue(model5.isMigrationSupported());
		assertEquals(5, model5.getMigrationVersion());
		assertEqualsUnmodifiable(Arrays.asList(migrations5), model5.getMigrations());
		
		model5.connect(props);
		model5.tearDownDatabase();
		model5.createDatabase();
		
		assertSchema(model5.getVerifiedSchema(), false, false);
		{
			final Map<Integer, String> logs = model5.getMigrationLogs();
			assertNotNull(logs.toString(), logs.get(5));
			assertTrue(logs.toString(), logs.get(5).endsWith(":created schema"));
			assertEquals(1, logs.size());
		}
		model5.disconnect();
		
		assertTrue(model7.isMigrationSupported());
		assertEquals(7, model7.getMigrationVersion());
		assertEqualsUnmodifiable(list(migrations7Missing[0]), model7.getMigrations());

		model7.connect(props);
		assertSchema(model7.getVerifiedSchema(), true, false);
		{
			final Map<Integer, String> logs = model7.getMigrationLogs();
			assertNotNull(logs.toString(), logs.get(5));
			assertTrue(logs.toString(), logs.get(5).endsWith(":created schema"));
			assertEquals(1, logs.size());
		}

		try
		{
			model7.migrateIfSupported();
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("no migration for versions [6] on migration from 5 to 7", e.getMessage());
		}
		assertSchema(model7.getVerifiedSchema(), true, false);
		{
			final Map<Integer, String> logs = model7.getMigrationLogs();
			assertNotNull(logs.toString(), logs.get(5));
			assertTrue(logs.toString(), logs.get(5).endsWith(":created schema"));
			assertEquals(1, logs.size());
		}
		
		final Database database = model7.getDatabase();
		final Driver driver = database.driver;
		final Migration[] migrations7 = new Migration[]{
				// BEWARE:
				// Never do this in real projects,
				// always use plain string literals
				// containing the sql statement!
				new Migration(7, "add column field7", driver.createColumn(driver.protectName("MigrationItem"), driver.protectName("field7"), database.getStringType(100))),
				new Migration(6, "add column field6", driver.createColumn(driver.protectName("MigrationItem"), driver.protectName("field6"), database.getStringType(100))),
				new Migration(5, "nonsense", "nonsense statement causing a test failure if executed for version 5"),
				new Migration(4, "nonsense", "nonsense statement causing a test failure if executed for version 4"),
			};
		model7.setMigrations(migrations7);
		assertTrue(model7.isMigrationSupported());
		assertEquals(7, model7.getMigrationVersion());
		assertEqualsUnmodifiable(Arrays.asList(migrations7), model7.getMigrations());

		model7.migrateIfSupported();
		assertSchema(model7.getVerifiedSchema(), true, true);
		{
			final Map<Integer, String> logs = model7.getMigrationLogs();
			assertNotNull(logs.toString(), logs.get(5));
			assertTrue(logs.toString(), logs.get(5).endsWith(":created schema"));
			assertNotNull(logs.toString(), logs.get(6));
			assertTrue(logs.toString(), logs.get(6).endsWith(":add column field6 [0]"));
			assertNotNull(logs.toString(), logs.get(7));
			assertTrue(logs.toString(), logs.get(7).endsWith(":add column field7 [0]"));
			assertEquals(3, logs.size());
		}
		
		// test, that MigrationStep is not executed again,
		// causing a SQLException because column does already exist
		model7.migrate();
		assertSchema(model7.getVerifiedSchema(), true, true);
		{
			final Map<Integer, String> logs = model7.getMigrationLogs();
			assertNotNull(logs.toString(), logs.get(5));
			assertTrue(logs.toString(), logs.get(5).endsWith(":created schema"));
			assertNotNull(logs.toString(), logs.get(6));
			assertTrue(logs.toString(), logs.get(6).endsWith(":add column field6 [0]"));
			assertNotNull(logs.toString(), logs.get(7));
			assertTrue(logs.toString(), logs.get(7).endsWith(":add column field7 [0]"));
			assertEquals(3, logs.size());
		}
		
		model7.tearDownDatabase();
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
		
		final Column column5 = columns.next();
		assertEquals("field5", column5.getName());
		assertEquals(true, column5.required());
		assertEquals(true, column5.exists());
		assertNotNull(column5.getType());
		
		if(model2)
		{
			final Column column6 = columns.next();
			assertEquals("field6", column6.getName());
			assertEquals(true, column6.required());
			assertEquals(migrated, column6.exists());
			assertNotNull(column6.getType());

			final Column column7 = columns.next();
			assertEquals("field7", column7.getName());
			assertEquals(true, column7.required());
			assertEquals(migrated, column7.exists());
			assertNotNull(column7.getType());
		}
		
		assertFalse(columns.hasNext());
		
		final Table migrationTable = schema.getTable("while");
		assertEquals("while", migrationTable.getName());
		assertEquals(true, migrationTable.required());
		assertEquals(true, migrationTable.exists());
	}
}
