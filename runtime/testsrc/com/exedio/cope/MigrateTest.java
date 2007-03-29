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

package com.exedio.cope;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import com.exedio.cope.junit.CopeAssert;
import com.exedio.dsmf.Column;
import com.exedio.dsmf.Driver;
import com.exedio.dsmf.SQLRuntimeException;
import com.exedio.dsmf.Schema;
import com.exedio.dsmf.Table;

public class MigrateTest extends CopeAssert
{
	private static final Migration[] migrations5 = new Migration[]{
		new Migration(5, "nonsense5", "nonsense statement causing a test failure if executed for version 5"),
	};
	
	private static final Model model5 = new Model(migrations5, MigrationItem1.TYPE);
	
	
	private static final Migration[] migrations7Missing = new Migration[]{
			new Migration(7, "nonsense7", "nonsense statement causing a test failure if executed for version 7"),
		};
	
	private static final Model model7 = new Model(migrations7Missing, MigrationItem2.TYPE);
	
	public void testMigrate() throws ParseException, UnknownHostException
	{
		final com.exedio.cope.Properties props = new com.exedio.cope.Properties();
		final SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
		final String hostname = InetAddress.getLocalHost().getHostName();
		
		assertTrue(model5.isMigrationSupported());
		assertEquals(5, model5.getMigrationVersion());
		assertEqualsUnmodifiable(Arrays.asList(migrations5), model5.getMigrations());
		
		model5.connect(props);
		model5.tearDownDatabase();
		final Date createBefore = new Date();
		model5.createDatabase();
		final Date createAfter = new Date();
		
		assertSchema(model5.getVerifiedSchema(), false, false);
		{
			final Map<Integer, byte[]> logs = model5.getMigrationLogs();
			final Properties log5 = log(logs.get(5));
			assertWithin(createBefore, createAfter, df.parse(log5.getProperty("date")));
			assertEquals(hostname, log5.getProperty("hostname"));
			assertEquals("true", log5.getProperty("create"));
			assertEquals(3, log5.size());
			assertEquals(1, logs.size());
		}
		model5.disconnect();
		
		assertTrue(model7.isMigrationSupported());
		assertEquals(7, model7.getMigrationVersion());
		assertEqualsUnmodifiable(list(migrations7Missing[0]), model7.getMigrations());

		model7.connect(props);
		assertSchema(model7.getVerifiedSchema(), true, false);
		{
			final Map<Integer, byte[]> logs = model7.getMigrationLogs();
			final Properties log5 = log(logs.get(5));
			assertWithin(createBefore, createAfter, df.parse(log5.getProperty("date")));
			assertEquals(hostname, log5.getProperty("hostname"));
			assertEquals("true", log5.getProperty("create"));
			assertEquals(3, log5.size());
			assertEquals(1, logs.size());
		}

		try
		{
			model7.migrateIfSupported();
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("attempt to migrate from 5 to 7, but declared migrations allow from 6 only", e.getMessage());
		}
		assertSchema(model7.getVerifiedSchema(), true, false);
		{
			final Map<Integer, byte[]> logs = model7.getMigrationLogs();
			final Properties log5 = log(logs.get(5));
			assertWithin(createBefore, createAfter, df.parse(log5.getProperty("date")));
			assertEquals(hostname, log5.getProperty("hostname"));
			assertEquals("true", log5.getProperty("create"));
			assertEquals(3, log5.size());
			assertEquals(1, logs.size());
		}
		
		final String blah =
			" blub blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah" +
			" blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah" +
			" blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah" +
			" blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blob";
		final Dialect dialect = model7.getDatabase().dialect;
		final Driver driver = dialect.driver;
		// BEWARE:
		// Never do this in real projects,
		// always use plain string literals
		// containing the sql statement!
		final String body70 = driver.createColumn(driver.protectName("MigrationItem"), driver.protectName("field7"), dialect.getStringType(100));
		final String body60 = driver.createColumn(driver.protectName("MigrationItem"), driver.protectName("field6"), dialect.getStringType(100));
		final Migration[] migrations7 = new Migration[]{
				new Migration(7, "add column field7" + blah, body70),
				new Migration(6, "add column field6",        body60),
				new Migration(5, "nonsense", "nonsense statement causing a test failure if executed for version 5"),
				new Migration(4, "nonsense", "nonsense statement causing a test failure if executed for version 4"),
			};
		model7.setMigrations(migrations7);
		assertTrue(model7.isMigrationSupported());
		assertEquals(7, model7.getMigrationVersion());
		assertEqualsUnmodifiable(Arrays.asList(migrations7), model7.getMigrations());

		final Date migrateBefore = new Date();
		model7.migrateIfSupported();
		final Date migrateAfter = new Date();
		assertSchema(model7.getVerifiedSchema(), true, true);
		{
			final Map<Integer, byte[]> logs = model7.getMigrationLogs();
			{
				final Properties log5 = log(logs.get(5));
				assertWithin(createBefore, createAfter, df.parse(log5.getProperty("date")));
				assertEquals(hostname, log5.getProperty("hostname"));
				assertEquals("true", log5.getProperty("create"));
				assertEquals(3, log5.size());
			}
			final Date date6;
			{
				final Properties log6 = log(logs.get(6));
				date6 = df.parse(log6.getProperty("date"));
				assertWithin(migrateBefore, migrateAfter, date6);
				assertEquals(hostname, log6.getProperty("hostname"));
				assertEquals(null, log6.getProperty("create"));
				assertEquals("add column field6", log6.getProperty("comment"));
				assertEquals(body60, log6.getProperty("body0.sql"));
				assertMinInt(0, log6.getProperty("body0.rows"));
				assertMinInt(0, log6.getProperty("body0.elapsed"));
				assertEquals(6, log6.size());
			}
			{
				final Properties log7 = log(logs.get(7));
				assertEquals(date6, df.parse(log7.getProperty("date")));
				assertEquals(hostname, log7.getProperty("hostname"));
				assertEquals(null, log7.getProperty("create"));
				assertEquals("add column field7" + blah, log7.getProperty("comment"));
				assertEquals(body70, log7.getProperty("body0.sql"));
				assertMinInt(0, log7.getProperty("body0.rows"));
				assertMinInt(0, log7.getProperty("body0.elapsed"));
				assertEquals(6, log7.size());
			}
			assertEquals(3, logs.size());
		}
		
		// test, that MigrationStep is not executed again,
		// causing a SQLException because column does already exist
		model7.migrate();
		assertSchema(model7.getVerifiedSchema(), true, true);
		{
			final Map<Integer, byte[]> logs = model7.getMigrationLogs();
			{
				final Properties log5 = log(logs.get(5));
				assertWithin(createBefore, createAfter, df.parse(log5.getProperty("date")));
				assertEquals(hostname, log5.getProperty("hostname"));
				assertEquals("true", log5.getProperty("create"));
				assertEquals(3, log5.size());
			}
			final Date date6;
			{
				final Properties log6 = log(logs.get(6));
				date6 = df.parse(log6.getProperty("date"));
				assertWithin(migrateBefore, migrateAfter, date6);
				assertEquals(hostname, log6.getProperty("hostname"));
				assertEquals(null, log6.getProperty("create"));
				assertEquals("add column field6", log6.getProperty("comment"));
				assertEquals(body60, log6.getProperty("body0.sql"));
				assertMinInt(0, log6.getProperty("body0.rows"));
				assertMinInt(0, log6.getProperty("body0.elapsed"));
				assertEquals(6, log6.size());
			}
			{
				final Properties log7 = log(logs.get(7));
				assertEquals(date6, df.parse(log7.getProperty("date")));
				assertEquals(hostname, log7.getProperty("hostname"));
				assertEquals(null, log7.getProperty("create"));
				assertEquals("add column field7" + blah, log7.getProperty("comment"));
				assertEquals(body70, log7.getProperty("body0.sql"));
				assertMinInt(0, log7.getProperty("body0.rows"));
				assertMinInt(0, log7.getProperty("body0.elapsed"));
				assertEquals(6, log7.size());
			}
			assertEquals(3, logs.size());
		}
		
		final Migration[] migrations8 = new Migration[]{
				new Migration(8, "nonsense8", "nonsense statement causing a test failure"),
			};
		model7.setMigrations(migrations8);
		assertTrue(model7.isMigrationSupported());
		assertEquals(8, model7.getMigrationVersion());
		assertEqualsUnmodifiable(Arrays.asList(migrations8), model7.getMigrations());

		try
		{
			model7.migrateIfSupported();
		}
		catch(SQLRuntimeException e)
		{
			assertEquals("nonsense statement causing a test failure", e.getMessage());
		}
		assertSchema(model7.getVerifiedSchema(), true, true);
		{
			final Map<Integer, byte[]> logs = model7.getMigrationLogs();
			{
				final Properties log5 = log(logs.get(5));
				assertWithin(createBefore, createAfter, df.parse(log5.getProperty("date")));
				assertEquals(hostname, log5.getProperty("hostname"));
				assertEquals("true", log5.getProperty("create"));
				assertEquals(3, log5.size());
			}
			final Date date6;
			{
				final Properties log6 = log(logs.get(6));
				date6 = df.parse(log6.getProperty("date"));
				assertWithin(migrateBefore, migrateAfter, date6);
				assertEquals(hostname, log6.getProperty("hostname"));
				assertEquals(null, log6.getProperty("create"));
				assertEquals("add column field6", log6.getProperty("comment"));
				assertEquals(body60, log6.getProperty("body0.sql"));
				assertMinInt(0, log6.getProperty("body0.rows"));
				assertMinInt(0, log6.getProperty("body0.elapsed"));
				assertEquals(6, log6.size());
			}
			{
				final Properties log7 = log(logs.get(7));
				assertEquals(date6, df.parse(log7.getProperty("date")));
				assertEquals(hostname, log7.getProperty("hostname"));
				assertEquals(null, log7.getProperty("create"));
				assertEquals("add column field7" + blah, log7.getProperty("comment"));
				assertEquals(body70, log7.getProperty("body0.sql"));
				assertMinInt(0, log7.getProperty("body0.rows"));
				assertMinInt(0, log7.getProperty("body0.elapsed"));
				assertEquals(6, log7.size());
			}
			assertEquals(3, logs.size());
		}
		
		try
		{
			model7.migrateIfSupported();
		}
		catch(IllegalStateException e)
		{
			assertEquals("Migration mutex set: Either a migration is currently underway, or a migration has failed unexpectedly.", e.getMessage());
		}
		assertSchema(model7.getVerifiedSchema(), true, true);
		{
			final Map<Integer, byte[]> logs = model7.getMigrationLogs();
			{
				final Properties log5 = log(logs.get(5));
				assertWithin(createBefore, createAfter, df.parse(log5.getProperty("date")));
				assertEquals(hostname, log5.getProperty("hostname"));
				assertEquals("true", log5.getProperty("create"));
				assertEquals(3, log5.size());
			}
			final Date date6;
			{
				final Properties log6 = log(logs.get(6));
				date6 = df.parse(log6.getProperty("date"));
				assertWithin(migrateBefore, migrateAfter, date6);
				assertEquals(hostname, log6.getProperty("hostname"));
				assertEquals(null, log6.getProperty("create"));
				assertEquals("add column field6", log6.getProperty("comment"));
				assertEquals(body60, log6.getProperty("body0.sql"));
				assertMinInt(0, log6.getProperty("body0.rows"));
				assertMinInt(0, log6.getProperty("body0.elapsed"));
				assertEquals(6, log6.size());
			}
			{
				final Properties log7 = log(logs.get(7));
				assertEquals(date6, df.parse(log7.getProperty("date")));
				assertEquals(hostname, log7.getProperty("hostname"));
				assertEquals(null, log7.getProperty("create"));
				assertEquals("add column field7" + blah, log7.getProperty("comment"));
				assertEquals(body70, log7.getProperty("body0.sql"));
				assertMinInt(0, log7.getProperty("body0.rows"));
				assertMinInt(0, log7.getProperty("body0.elapsed"));
				assertEquals(6, log7.size());
			}
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
	
	private static final Properties log(final byte[] log)
	{
		final Properties result = new Properties();
		try
		{
			result.load(new ByteArrayInputStream(log));
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}
		return result;
	}
	
	private static final void assertMinInt(final int expectedMinimum, final String actual)
	{
		assertTrue(actual, Integer.parseInt(actual)>=expectedMinimum);
	}
}
