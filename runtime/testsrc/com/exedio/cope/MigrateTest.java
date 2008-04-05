/*
 * Copyright (C) 2004-2008  exedio GmbH (www.exedio.com)
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

import java.net.InetAddress;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;

import com.exedio.cope.junit.CopeAssert;
import com.exedio.dsmf.Column;
import com.exedio.dsmf.Driver;
import com.exedio.dsmf.SQLRuntimeException;
import com.exedio.dsmf.Schema;
import com.exedio.dsmf.Table;

public class MigrateTest extends CopeAssert
{
	private static final Revision[] migrations5 = new Revision[]{
		new Revision(5, "nonsense5", "nonsense statement causing a test failure if executed for revision 5"),
	};
	
	private static final Model model5 = new Model(migrations5, MigrateItem1.TYPE);
	
	
	private static final Revision[] migrations7Missing = new Revision[]{
			new Revision(7, "nonsense7", "nonsense statement causing a test failure if executed for revision 7"),
		};
	
	private static final Model model7 = new Model(migrations7Missing, MigrateItem2.TYPE);
	
	private static final SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
	static
	{
		df.setTimeZone(TimeZone.getTimeZone("UTC"));
	}
	
	private String hostname;
	private com.exedio.cope.ConnectProperties props;
	
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		hostname = InetAddress.getLocalHost().getHostName();
		props = new com.exedio.cope.ConnectProperties(com.exedio.cope.ConnectProperties.getSystemPropertyContext());
	}

	String jdbcUrl;
	String jdbcUser;
	String databaseName;
	String databaseVersion;
	String driverName;
	String driverVersion;
	
	public void testMigrate() throws ParseException
	{
		jdbcUrl  = props.getDatabaseUrl();
		jdbcUser = props.getDatabaseUser();
		
		assertTrue(model5.isRevisionSupported());
		assertEquals(5, model5.getRevisionNumber());
		assertEqualsUnmodifiable(Arrays.asList(migrations5), model5.getMigrations());
		
		model5.connect(props);
		model5.tearDownDatabase();

		final Properties info = model5.getDatabaseInfo();
		databaseName = info.getProperty("database.name");
		databaseVersion = info.getProperty("database.version");
		driverName = info.getProperty("driver.name");
		driverVersion = info.getProperty("driver.version");
		
		final Date createBefore = new Date();
		model5.createDatabase();
		final Date createAfter = new Date();
		
		assertSchema(model5.getVerifiedSchema(), false, false);
		final Date createDate;
		{
			final Map<Integer, byte[]> logs = model5.getMigrationLogs();
			createDate = assertCreate(createBefore, createAfter, logs, 5);
			assertEquals(1, logs.size());
		}
		model5.disconnect();
		
		assertTrue(model7.isRevisionSupported());
		assertEquals(7, model7.getRevisionNumber());
		assertEqualsUnmodifiable(list(migrations7Missing[0]), model7.getMigrations());

		model7.connect(props);
		assertSchema(model7.getVerifiedSchema(), true, false);
		{
			final Map<Integer, byte[]> logs = model7.getMigrationLogs();
			assertCreate(createDate, logs, 5);
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
			assertCreate(createDate, logs, 5);
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
		final String body70 = driver.createColumn(driver.protectName(mysqlLower("MigrateItem")), driver.protectName("field7"), dialect.getStringType(100));
		final String body60 = driver.createColumn(driver.protectName(mysqlLower("MigrateItem")), driver.protectName("field6"), dialect.getStringType(100));
		final String body61 = driver.createColumn(driver.protectName(mysqlLower("MigrateItem")), driver.protectName("field6b"), dialect.getStringType(100));
		final Revision[] migrations7 = new Revision[]{
				new Revision(7, "add column field7" + blah, body70),
				new Revision(6, "add column field6",        body60, body61),
				new Revision(5, "nonsense", "nonsense statement causing a test failure if executed for revision 5"),
				new Revision(4, "nonsense", "nonsense statement causing a test failure if executed for revision 4"),
			};
		model7.setMigrations(migrations7);
		assertTrue(model7.isRevisionSupported());
		assertEquals(7, model7.getRevisionNumber());
		assertEqualsUnmodifiable(Arrays.asList(migrations7), model7.getMigrations());

		final Date migrateBefore = new Date();
		model7.migrateIfSupported();
		final Date migrateAfter = new Date();
		assertSchema(model7.getVerifiedSchema(), true, true);
		final Date migrateDate;
		{
			final Map<Integer, byte[]> logs = model7.getMigrationLogs();
			assertCreate(createDate, logs, 5);
			migrateDate = assertMigrate(migrateBefore, migrateAfter, migrations7[1], logs, 6);
			assertMigrate(migrateDate, migrations7[0], logs, 7);
			assertEquals(3, logs.size());
		}
		
		// test, that MigrationStep is not executed again,
		// causing a SQLException because column does already exist
		model7.migrate();
		assertSchema(model7.getVerifiedSchema(), true, true);
		{
			final Map<Integer, byte[]> logs = model7.getMigrationLogs();
			assertCreate(createDate, logs, 5);
			assertMigrate(migrateDate, migrations7[1], logs, 6);
			assertMigrate(migrateDate, migrations7[0], logs, 7);
			assertEquals(3, logs.size());
		}
		
		final Revision[] migrations8 = new Revision[]{
				new Revision(8, "nonsense8", "nonsense statement causing a test failure"),
			};
		model7.setMigrations(migrations8);
		assertTrue(model7.isRevisionSupported());
		assertEquals(8, model7.getRevisionNumber());
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
			assertCreate(createDate, logs, 5);
			assertMigrate(migrateDate, migrations7[1], logs, 6);
			assertMigrate(migrateDate, migrations7[0], logs, 7);
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
			assertCreate(createDate, logs, 5);
			assertMigrate(migrateDate, migrations7[1], logs, 6);
			assertMigrate(migrateDate, migrations7[0], logs, 7);
			assertEquals(3, logs.size());
		}
		
		model7.tearDownDatabase();
	}
	
	private void assertSchema(final Schema schema, final boolean model2, final boolean migrated)
	{
		final Table table = schema.getTable(mysqlLower(("MigrateItem")));
		assertEquals(mysqlLower("MigrateItem"), table.getName());
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

			final Column column6b = columns.next();
			assertEquals("field6b", column6b.getName());
			assertEquals(true, column6b.required());
			assertEquals(migrated, column6b.exists());
			assertNotNull(column6b.getType());

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
	
	private final Date assertCreate(final Date before, final Date after, final Map<Integer, byte[]> logs, final int revision) throws ParseException
	{
		final byte[] log = logs.get(revision);
		assertNotNull(log);
		final Properties logProps = parse(log);
		assertEquals(String.valueOf(revision), logProps.getProperty("revision"));
		final Date date = df.parse(logProps.getProperty("dateUTC"));
		assertWithin(before, after, date);
		assertEquals("true", logProps.getProperty("create"));
		assertMigrationEnvironment(logProps);
		assertEquals(14, logProps.size());
		return date;
	}
	
	private final void assertCreate(final Date date, final Map<Integer, byte[]> logs, final int revision) throws ParseException
	{
		assertEquals(date, assertCreate(date, date, logs, revision));
	}
	
	private final Date assertMigrate(final Date before, final Date after, final Revision migration, final Map<Integer, byte[]> logs, final int revision) throws ParseException
	{
		final byte[] log = logs.get(revision);
		assertNotNull(log);
		final Properties logProps = parse(log);
		assertEquals(String.valueOf(revision), logProps.getProperty("revision"));
		final Date date = df.parse(logProps.getProperty("dateUTC"));
		assertWithin(before, after, date);
		assertEquals(null, logProps.getProperty("create"));
		assertEquals(migration.comment, logProps.getProperty("comment"));
		for(int i = 0; i<migration.body.length; i++)
		{
			assertEquals(migration.body[i], logProps.getProperty("body" + i + ".sql"));
			assertMinInt(0, logProps.getProperty("body" + i + ".rows"));
			assertMinInt(0, logProps.getProperty("body" + i + ".elapsed"));
		}
		assertMigrationEnvironment(logProps);
		assertEquals(14 + (3*migration.body.length), logProps.size());
		return date;
	}
	
	private final void assertMigrate(final Date date, final Revision migration, final Map<Integer, byte[]> logs, final int revision) throws ParseException
	{
		assertEquals(date, assertMigrate(date, date, migration, logs, revision));
	}
	
	private final void assertMigrationEnvironment(final Properties p)
	{
		assertNotNull(hostname);
		assertNotNull(jdbcUrl);
		assertNotNull(jdbcUser);
		assertNotNull(databaseName);
		assertNotNull(databaseVersion);
		assertNotNull(driverName);
		assertNotNull(driverVersion);

		assertEquals(hostname, p.getProperty("hostname"));
		assertEquals(jdbcUrl, p.getProperty("jdbc.url"));
		assertEquals(jdbcUser, p.getProperty("jdbc.user"));
		assertEquals(databaseName, p.getProperty("database.name"));
		assertEquals(databaseVersion, p.getProperty("database.version") + " (" + p.getProperty("database.version.major") + '.' + p.getProperty("database.version.minor") + ')');
		assertEquals(driverName, p.getProperty("driver.name"));
		assertEquals(driverVersion, p.getProperty("driver.version") + " (" + p.getProperty("driver.version.major") + '.' + p.getProperty("driver.version.minor") + ')');
	}
	
	private static final Properties parse(final byte[] log)
	{
		return Revision.parse(log);
	}
	
	private static final void assertMinInt(final int expectedMinimum, final String actual)
	{
		assertTrue(actual, Integer.parseInt(actual)>=expectedMinimum);
	}
	
	final String mysqlLower(final String name)
	{
		return props.getMysqlLowerCaseTableNames() ? name.toLowerCase() : name;
	}
}
