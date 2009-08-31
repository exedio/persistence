/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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
import com.exedio.dsmf.SQLRuntimeException;
import com.exedio.dsmf.Schema;
import com.exedio.dsmf.Table;

public class ReviseTest extends CopeAssert
{
	private static final Revisions revisions5 = new Revisions(
		new Revision(5, "nonsense5", "nonsense statement causing a test failure if executed for revision 5")
	);
	
	private static final Model model5 = new Model(revisions5, ReviseItem1.TYPE);
	
	
	private static final Revisions revisions7Missing = new Revisions(
			new Revision(7, "nonsense7", "nonsense statement causing a test failure if executed for revision 7")
		);
	
	private static final Model model7 = new Model(revisions7Missing, ReviseItem2.TYPE);
	
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
		props = new com.exedio.cope.ConnectProperties(com.exedio.cope.ConnectProperties.getSystemPropertySource());
	}

	String jdbcUrl;
	String jdbcUser;
	String databaseName;
	String databaseVersion;
	String driverName;
	String driverVersion;
	
	public void testRevise() throws ParseException
	{
		jdbcUrl  = props.getDatabaseUrl();
		jdbcUser = props.getDatabaseUser();
		
		assertNotNull(model5.getRevisions());
		assertEquals(5, model5.getRevisions().getNumber());
		assertEqualsUnmodifiable(revisions5.getList(), model5.getRevisions().getList());
		
		model5.connect(props);
		model5.tearDownSchema();

		final Properties info = model5.getDatabaseInfo();
		databaseName = info.getProperty("database.name");
		databaseVersion = info.getProperty("database.version");
		driverName = info.getProperty("driver.name");
		driverVersion = info.getProperty("driver.version");
		
		final Date createBefore = new Date();
		model5.createSchema();
		final Date createAfter = new Date();
		
		assertSchema(model5.getVerifiedSchema(), false, false);
		final Date createDate;
		{
			final Map<Integer, byte[]> logs = model5.getRevisionLogs();
			createDate = assertCreate(createBefore, createAfter, logs, 5);
			assertEquals(1, logs.size());
		}
		model5.disconnect();
		
		assertNotNull(model7.getRevisions());
		assertEquals(7, model7.getRevisions().getNumber());
		assertEqualsUnmodifiable(list(revisions7Missing.getList().get(0)), model7.getRevisions().getList());

		model7.connect(props);
		assertSchema(model7.getVerifiedSchema(), true, false);
		{
			final Map<Integer, byte[]> logs = model7.getRevisionLogs();
			assertCreate(createDate, logs, 5);
			assertEquals(1, logs.size());
		}

		try
		{
			model7.reviseIfSupported();
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("attempt to revise from 5 to 7, but declared revisions allow from 6 only", e.getMessage());
		}
		assertSchema(model7.getVerifiedSchema(), true, false);
		{
			final Map<Integer, byte[]> logs = model7.getRevisionLogs();
			assertCreate(createDate, logs, 5);
			assertEquals(1, logs.size());
		}
		
		final String blah =
			" blub blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah" +
			" blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah" +
			" blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah" +
			" blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blob";
		final Dialect dialect = model7.getDatabase().dialect;
		final com.exedio.dsmf.Dialect dsmfDialect = dialect.dsmfDialect;
		// BEWARE:
		// Never do this in real projects,
		// always use plain string literals
		// containing the sql statement!
		final String body70 = dsmfDialect.createColumn(dsmfDialect.protectName(mysqlLower("ReviseItem")), dsmfDialect.protectName("field7"), dialect.getStringType(100));
		final String body60 = dsmfDialect.createColumn(dsmfDialect.protectName(mysqlLower("ReviseItem")), dsmfDialect.protectName("field6"), dialect.getStringType(100));
		final String body61 = dsmfDialect.createColumn(dsmfDialect.protectName(mysqlLower("ReviseItem")), dsmfDialect.protectName("field6b"), dialect.getStringType(100));
		final Revision[] revisions7 = new Revision[]{
				new Revision(7, "add column field7" + blah, body70),
				new Revision(6, "add column field6",        body60, body61),
				new Revision(5, "nonsense", "nonsense statement causing a test failure if executed for revision 5"),
				new Revision(4, "nonsense", "nonsense statement causing a test failure if executed for revision 4"),
			};
		model7.setRevisions(revisions7);
		assertNotNull(model7.getRevisions());
		assertEquals(7, model7.getRevisions().getNumber());
		assertEqualsUnmodifiable(Arrays.asList(revisions7), model7.getRevisions().getList());

		final Date reviseBefore = new Date();
		model7.reviseIfSupported();
		final Date reviseAfter = new Date();
		assertSchema(model7.getVerifiedSchema(), true, true);
		final Date reviseDate;
		{
			final Map<Integer, byte[]> logs = model7.getRevisionLogs();
			assertCreate(createDate, logs, 5);
			reviseDate = assertRevise(reviseBefore, reviseAfter, revisions7[1], logs, 6);
			assertRevise(reviseDate, revisions7[0], logs, 7);
			assertEquals(3, logs.size());
		}
		
		// test, that revision is not executed again,
		// causing a SQLException because column does already exist
		model7.revise();
		assertSchema(model7.getVerifiedSchema(), true, true);
		{
			final Map<Integer, byte[]> logs = model7.getRevisionLogs();
			assertCreate(createDate, logs, 5);
			assertRevise(reviseDate, revisions7[1], logs, 6);
			assertRevise(reviseDate, revisions7[0], logs, 7);
			assertEquals(3, logs.size());
		}
		
		final Revision[] revisions8 = new Revision[]{
				new Revision(8, "nonsense8", "nonsense statement causing a test failure"),
			};
		model7.setRevisions(revisions8);
		assertNotNull(model7.getRevisions());
		assertEquals(8, model7.getRevisions().getNumber());
		assertEqualsUnmodifiable(Arrays.asList(revisions8), model7.getRevisions().getList());

		try
		{
			model7.reviseIfSupported();
		}
		catch(SQLRuntimeException e)
		{
			assertEquals("nonsense statement causing a test failure", e.getMessage());
		}
		assertSchema(model7.getVerifiedSchema(), true, true);
		{
			final Map<Integer, byte[]> logs = model7.getRevisionLogs();
			assertCreate(createDate, logs, 5);
			assertRevise(reviseDate, revisions7[1], logs, 6);
			assertRevise(reviseDate, revisions7[0], logs, 7);
			assertEquals(3, logs.size());
		}
		
		try
		{
			model7.reviseIfSupported();
		}
		catch(IllegalStateException e)
		{
			assertEquals("Revision mutex set: Either a revision is currently underway, or a revision has failed unexpectedly.", e.getMessage());
		}
		assertSchema(model7.getVerifiedSchema(), true, true);
		{
			final Map<Integer, byte[]> logs = model7.getRevisionLogs();
			assertCreate(createDate, logs, 5);
			assertRevise(reviseDate, revisions7[1], logs, 6);
			assertRevise(reviseDate, revisions7[0], logs, 7);
			assertEquals(3, logs.size());
		}
		
		model7.tearDownSchema();
	}
	
	private void assertSchema(final Schema schema, final boolean model2, final boolean revised)
	{
		final Table table = schema.getTable(mysqlLower(("ReviseItem")));
		assertEquals(mysqlLower("ReviseItem"), table.getName());
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
			assertEquals(revised, column6.exists());
			assertNotNull(column6.getType());

			final Column column6b = columns.next();
			assertEquals("field6b", column6b.getName());
			assertEquals(true, column6b.required());
			assertEquals(revised, column6b.exists());
			assertNotNull(column6b.getType());

			final Column column7 = columns.next();
			assertEquals("field7", column7.getName());
			assertEquals(true, column7.required());
			assertEquals(revised, column7.exists());
			assertNotNull(column7.getType());
		}
		
		assertFalse(columns.hasNext());
		
		final Table revisionTable = schema.getTable("while");
		assertEquals("while", revisionTable.getName());
		assertEquals(true, revisionTable.required());
		assertEquals(true, revisionTable.exists());
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
		assertRevisionEnvironment(logProps);
		assertEquals(14, logProps.size());
		return date;
	}
	
	private final void assertCreate(final Date date, final Map<Integer, byte[]> logs, final int revision) throws ParseException
	{
		assertEquals(date, assertCreate(date, date, logs, revision));
	}
	
	private final Date assertRevise(final Date before, final Date after, final Revision revision, final Map<Integer, byte[]> logs, final int number) throws ParseException
	{
		final byte[] log = logs.get(number);
		assertNotNull(log);
		final Properties logProps = parse(log);
		assertEquals(String.valueOf(number), logProps.getProperty("revision"));
		final Date date = df.parse(logProps.getProperty("dateUTC"));
		assertWithin(before, after, date);
		assertEquals(null, logProps.getProperty("create"));
		assertEquals(revision.comment, logProps.getProperty("comment"));
		for(int i = 0; i<revision.body.length; i++)
		{
			assertEquals(revision.body[i], logProps.getProperty("body" + i + ".sql"));
			assertMinInt(0, logProps.getProperty("body" + i + ".rows"));
			assertMinInt(0, logProps.getProperty("body" + i + ".elapsed"));
		}
		assertRevisionEnvironment(logProps);
		assertEquals(14 + (3*revision.body.length), logProps.size());
		return date;
	}
	
	private final void assertRevise(final Date date, final Revision revision, final Map<Integer, byte[]> logs, final int number) throws ParseException
	{
		assertEquals(date, assertRevise(date, date, revision, logs, number));
	}
	
	private final void assertRevisionEnvironment(final Properties p)
	{
		assertNotNull(hostname);
		assertNotNull(jdbcUrl);
		assertNotNull(jdbcUser);
		assertNotNull(databaseName);
		assertNotNull(databaseVersion);
		assertNotNull(driverName);
		assertNotNull(driverVersion);

		assertEquals(hostname, p.getProperty("env.hostname"));
		assertEquals(jdbcUrl, p.getProperty("env.jdbc.url"));
		assertEquals(jdbcUser, p.getProperty("env.jdbc.user"));
		assertEquals(databaseName, p.getProperty("env.database.name"));
		assertEquals(databaseVersion, p.getProperty("env.database.version") + " (" + p.getProperty("env.database.version.major") + '.' + p.getProperty("env.database.version.minor") + ')');
		assertEquals(driverName, p.getProperty("env.driver.name"));
		assertEquals(driverVersion, p.getProperty("env.driver.version") + " (" + p.getProperty("env.driver.version.major") + '.' + p.getProperty("env.driver.version.minor") + ')');
	}
	
	private static final Properties parse(final byte[] log)
	{
		return RevisionInfo.parse(log);
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
