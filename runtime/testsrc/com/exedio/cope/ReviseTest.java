/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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

import static com.exedio.cope.RevisionInfo.parse;
import static com.exedio.cope.util.Properties.SYSTEM_PROPERTY_SOURCE;
import static java.lang.String.valueOf;

import java.net.InetAddress;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TimeZone;

import com.exedio.cope.junit.CopeAssert;
import com.exedio.cope.util.Hex;
import com.exedio.cope.util.Properties.Source;
import com.exedio.dsmf.Column;
import com.exedio.dsmf.SQLRuntimeException;
import com.exedio.dsmf.Schema;
import com.exedio.dsmf.Table;

public class ReviseTest extends CopeAssert
{
	private static final TestRevisionsFactory revisionsFactory5 = new TestRevisionsFactory();

	private static final Model model5 = new Model(revisionsFactory5, ReviseItem1.TYPE);


	private static final TestRevisionsFactory revisionsFactory7 = new TestRevisionsFactory();

	private static final Model model7 = new Model(revisionsFactory7, ReviseItem2.TYPE);

	private static final SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
	static
	{
		df.setTimeZone(TimeZone.getTimeZone("UTC"));
	}

	private String hostname;
	private ConnectProperties props;
	private TestLogAppender log = null;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		hostname = InetAddress.getLocalHost().getHostName();
		final TestSource testSource = new TestSource();
		testSource.putOverride("revise.auto.enabled", "true");
		props = new ConnectProperties(testSource, SYSTEM_PROPERTY_SOURCE);
		log = new TestLogAppender();
		Revisions.logger.addAppender(log);
	}

	@Override
	protected void tearDown() throws Exception
	{
		Revisions.logger.removeAppender(log);
		log = null;
		super.tearDown();
	}

	String connectionUrl;
	String connectionUser;
	EnvironmentInfo info;
	boolean longSyntheticNames = false;

	public void testRevise() throws ParseException
	{
		connectionUrl  = props.getConnectionUrl();
		connectionUser = props.getConnectionUser();
		revisionsFactory7.assertEmpty();

		try
		{
			model5.getRevisions();
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("model not yet connected, use Model#connect", e.getMessage());
		}

		model5.connect(props);
		final Revisions revisions5 = new Revisions(
				new Revision(5, "nonsense5", "nonsense statement causing a test failure if executed for revision 5")
			);
		revisionsFactory5.put(revisions5);
		assertSame(revisions5, model5.getRevisions());
		revisionsFactory5.assertEmpty();
		longSyntheticNames = model5.getConnectProperties().longSyntheticNames.booleanValue();
		model5.tearDownSchema();

		info = model5.getEnvironmentInfo();

		final Date createBefore = new Date();
		model5.createSchema();
		final Date createAfter = new Date();

		assertSchema(model5.getVerifiedSchema(), false, false);
		final Date createDate;
		{
			final Map<Integer, byte[]> logs = model5.getRevisionLogsAndMutex();
			createDate = assertCreate(createBefore, createAfter, logs, 5);
			assertEquals(1, logs.size());
			assertEqualsLog(logs, model5.getRevisionLogs());
		}
		model5.disconnect();

		try
		{
			model7.getRevisions();
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("model not yet connected, use Model#connect", e.getMessage());
		}

		model7.connect(props);
		final Revisions revisions7Missing = new Revisions(
				new Revision(7, "nonsense7", "nonsense statement causing a test failure if executed for revision 7")
			);
		revisionsFactory7.put(revisions7Missing);
		assertSame(revisions7Missing, model7.getRevisions());
		revisionsFactory7.assertEmpty();
		assertSchema(model7.getVerifiedSchema(), true, false);
		{
			final Map<Integer, byte[]> logs = model7.getRevisionLogsAndMutex();
			assertCreate(createDate, logs, 5);
			assertEquals(1, logs.size());
			assertEqualsLog(logs, model7.getRevisionLogs());
		}

		try
		{
			model7.reviseIfSupportedAndAutoEnabled();
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("attempt to revise from 5 to 7, but declared revisions allow from 6 only", e.getMessage());
		}
		assertSchema(model7.getVerifiedSchema(), true, false);
		{
			final Map<Integer, byte[]> logs = model7.getRevisionLogsAndMutex();
			assertCreate(createDate, logs, 5);
			assertEquals(1, logs.size());
			assertEqualsLog(logs, model7.getRevisionLogs());
		}

		final String blah =
			" " +
			"Auml \u00c4; Ouml \u00d6; Uuml \u00dc; " +
			"auml \u00e4; ouml \u00f6; uuml \u00fc; " +
			"szlig \u00df; paragraph \u00a7; kringel \u00b0; " +
			"abreve \u0102; hebrew \u05d8 euro \u20ac" +
			"Aringabove \u00c5;" +     // ISO-8859-1/4/9/10 (Latin1/4/5/6)
			"Lacute \u0139;" +         // ISO-8859-2 (Latin2)
			"Cdotabove \u010a;" +      // ISO-8859-3 (Latin3)
			"ha \u0425;" +             // ISO-8859-5 (Cyrillic)
			"AlefHamzaBelow \u0625;" + // ISO-8859-6 (Arabic)
			"Epsilon \u0395;" +        // ISO-8859-7 (Greek)
			" blub blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah" +
			" blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah" +
			" blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah" +
			" blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blob";
		final Dialect dialect = model7.connect().database.dialect;
		final com.exedio.dsmf.Dialect dsmfDialect = dialect.dsmfDialect;
		// BEWARE:
		// Never do this in real projects,
		// always use plain string literals
		// containing the sql statement!
		final String body70 = dsmfDialect.createColumn(dsmfDialect.quoteName(filterTableName("ReviseItem")), dsmfDialect.quoteName("field7"), dialect.getStringType(100));
		final String body60 = dsmfDialect.createColumn(dsmfDialect.quoteName(filterTableName("ReviseItem")), dsmfDialect.quoteName("field6"), dialect.getStringType(100));
		final String body61 = dsmfDialect.createColumn(dsmfDialect.quoteName(filterTableName("ReviseItem")), dsmfDialect.quoteName("field6b"), dialect.getStringType(100));
		final Revisions revisions7 = new Revisions(
				new Revision(7, "add column field7" + blah, body70),
				new Revision(6, "add column field6",        body60, body61),
				new Revision(5, "nonsense", "nonsense statement causing a test failure if executed for revision 5"),
				new Revision(4, "nonsense", "nonsense statement causing a test failure if executed for revision 4")
			);
		assertSame(revisions7Missing, model7.getRevisions());
		reconnect();
		revisionsFactory7.put(revisions7);
		assertSame(revisions7, model7.getRevisions());
		revisionsFactory7.assertEmpty();

		log.assertEmpty();
		final Date reviseBefore = new Date();
		model7.reviseIfSupportedAndAutoEnabled();
		final Date reviseAfter = new Date();
		assertSchema(model7.getVerifiedSchema(), true, true);
		final Date reviseDate;
		{
			final Map<Integer, byte[]> logs = model7.getRevisionLogsAndMutex();
			assertCreate(createDate, logs, 5);
			reviseDate = assertRevise(reviseBefore, reviseAfter, revisions7, 1, logs, 6);
			assertRevise(reviseDate, revisions7, 0, logs, 7);
			assertEquals(3, logs.size());
			assertEqualsLog(logs, model7.getRevisionLogs());
		}
		log.assertInfo("revise 6/0:" + body60);

		// test, that revision is not executed again,
		// causing a SQLException because column does already exist
		model7.revise();
		assertSchema(model7.getVerifiedSchema(), true, true);
		{
			final Map<Integer, byte[]> logs = model7.getRevisionLogsAndMutex();
			assertCreate(createDate, logs, 5);
			assertRevise(reviseDate, revisions7, 1, logs, 6);
			assertRevise(reviseDate, revisions7, 0, logs, 7);
			assertEquals(3, logs.size());
			assertEqualsLog(logs, model7.getRevisionLogs());
		}
		log.assertInfo("revise 6/1:" + body61);

		// test, that revision is not executed again,
		// even after reconnect
		model7.disconnect();
		model7.connect(props);
		revisionsFactory7.put(revisions7Missing);
		model7.revise();
		revisionsFactory7.assertEmpty();
		assertSchema(model7.getVerifiedSchema(), true, true);
		{
			final Map<Integer, byte[]> logs = model7.getRevisionLogsAndMutex();
			assertCreate(createDate, logs, 5);
			assertRevise(reviseDate, revisions7, 1, logs, 6);
			assertRevise(reviseDate, revisions7, 0, logs, 7);
			assertEquals(3, logs.size());
			assertEqualsLog(logs, model7.getRevisionLogs());
		}
		log.assertInfo("revise 7/0:" + body70);

		final Revisions revisions8 = new Revisions(
				new Revision(8, "nonsense8", "nonsense statement causing a test failure")
			);
		assertSame(revisions7Missing, model7.getRevisions());
		reconnect();
		revisionsFactory7.put(revisions8);
		assertSame(revisions8, model7.getRevisions());
		revisionsFactory7.assertEmpty();

		final Date failBefore = new Date();
		try
		{
			model7.reviseIfSupportedAndAutoEnabled();
		}
		catch(final SQLRuntimeException e)
		{
			assertEquals("nonsense statement causing a test failure", e.getMessage());
		}
		final Date failAfter = new Date();
		assertSchema(model7.getVerifiedSchema(), true, true);
		final Date failDate;
		{
			final Map<Integer, byte[]> logs = model7.getRevisionLogsAndMutex();
			assertCreate(createDate, logs, 5);
			assertRevise(reviseDate, revisions7, 1, logs, 6);
			assertRevise(reviseDate, revisions7, 0, logs, 7);
			failDate = assertMutex(failBefore, failAfter, 8, 7, logs);
			assertEquals(4, logs.size());
			assertEqualsLog(remove(-1, logs), model7.getRevisionLogs());
		}
		log.assertInfo("revise 8/0:nonsense statement causing a test failure");

		try
		{
			model7.reviseIfSupportedAndAutoEnabled();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("Revision mutex set: Either a revision is currently underway, or a revision has failed unexpectedly.", e.getMessage());
		}
		assertSchema(model7.getVerifiedSchema(), true, true);
		{
			final Map<Integer, byte[]> logs = model7.getRevisionLogsAndMutex();
			assertCreate(createDate, logs, 5);
			assertRevise(reviseDate, revisions7, 1, logs, 6);
			assertRevise(reviseDate, revisions7, 0, logs, 7);
			assertMutex(failDate, 8, 7, logs);
			assertEquals(4, logs.size());
			assertEqualsLog(remove(-1, logs), model7.getRevisionLogs());
		}
		log.assertEmpty();

		model7.tearDownSchema();
		log.assertEmpty();
		revisionsFactory7.assertEmpty();
	}

	private void assertSchema(final Schema schema, final boolean model2, final boolean revised)
	{
		final Table table = schema.getTable(filterTableName(("ReviseItem")));
		assertEquals(filterTableName("ReviseItem"), table.getName());
		assertEquals(true, table.required());
		assertEquals(true, table.exists());
		final Iterator<Column> columns = table.getColumns().iterator();

		final Column columnThis = columns.next();
		assertEquals(synthetic("this", "ReviseItem"), columnThis.getName());
		assertEquals(true, columnThis.required());
		assertEquals(true, columnThis.exists());
		assertNotNull(columnThis.getType());

		if(props.updateCounter.booleanValue())
		{
			final Column columnCatch = columns.next();
			assertEquals(synthetic("catch", "ReviseItem"), columnCatch.getName());
			assertEquals(true, columnCatch.required());
			assertEquals(true, columnCatch.exists());
			assertNotNull(columnCatch.getType());
		}

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

		final Table revisionTable = schema.getTable(props.revisionTableName.stringValue());
		assertEquals(props.revisionTableName.stringValue(), revisionTable.getName());
		assertEquals(true, revisionTable.required());
		assertEquals(true, revisionTable.exists());
	}

	public void testAutoRevise()
	{
		revisionsFactory5.put( new Revisions(0) );
		final TestSource testSource = new TestSource();
		testSource.putOverride("revise.auto.enabled", "true");
		model5.connect(new ConnectProperties(testSource, SYSTEM_PROPERTY_SOURCE));
		model5.createSchema();
		model5.reviseIfSupportedAndAutoEnabled();
		model5.disconnect();
		assertEquals( true, props.autoReviseEnabled.booleanValue() );
		revisionsFactory5.assertEmpty();

		testSource.putOverride("revise.auto.enabled", "false");
		final ConnectProperties cp = new ConnectProperties(testSource, SYSTEM_PROPERTY_SOURCE);
		model5.connect(cp);
		assertEquals( false, cp.autoReviseEnabled.booleanValue() );
		revisionsFactory5.put( new Revisions(0) );
		model5.reviseIfSupportedAndAutoEnabled();
		revisionsFactory5.assertEmpty();
		model5.disconnect();

		model5.connect( new ConnectProperties(testSource, SYSTEM_PROPERTY_SOURCE) );
		revisionsFactory5.put( new Revisions( new Revision(1, "rev1", "sql1") ) );
		try
		{
			model5.reviseIfSupportedAndAutoEnabled();
			fail();
		}
		catch ( final IllegalStateException e )
		{
			assertEquals( "Model#reviseIfSupportedAndAutoEnabled called with auto-revising disabled and 1 revisions pending (last revision in DB: 0; last revision in model: 1)", e.getMessage() );
		}
		revisionsFactory5.assertEmpty();
		model5.disconnect();

		testSource.putOverride("revise.auto.enabled", "true");
		model5.connect( new ConnectProperties(testSource, SYSTEM_PROPERTY_SOURCE) );
		revisionsFactory5.put( new Revisions( new Revision(1, "rev1", "sql1") ) );
		try
		{
			model5.reviseIfSupportedAndAutoEnabled();
			fail();
		}
		catch ( final SQLRuntimeException e )
		{
			// fine
		}
		revisionsFactory5.assertEmpty();
		model5.tearDownSchema();
		model5.disconnect();
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

	private final Date assertRevise(final Date before, final Date after, final Revisions revisions, final int revisionsIndex, final Map<Integer, byte[]> logs, final int number) throws ParseException
	{
		final Revision revision = revisions.getList().get(revisionsIndex);
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

	private final void assertRevise(final Date date, final Revisions revisions, final int revisionsIndex, final Map<Integer, byte[]> logs, final int number) throws ParseException
	{
		assertEquals(date, assertRevise(date, date, revisions, revisionsIndex, logs, number));
	}

	private final Date assertMutex(
			final Date before, final Date after,
			final int expected, final int actual,
			final Map<Integer, byte[]> logs) throws ParseException
	{
		final byte[] log = logs.get(-1);
		assertNotNull(log);
		final Properties logProps = parse(log);
		assertEquals(null, logProps.getProperty("revision"));
		final Date date = df.parse(logProps.getProperty("dateUTC"));
		assertWithin(before, after, date);
		assertEquals("true", logProps.getProperty("mutex"));
		assertEquals(String.valueOf(expected), logProps.getProperty("mutex.expected"));
		assertEquals(String.valueOf(actual  ), logProps.getProperty("mutex.actual"));
		assertEquals(null, logProps.getProperty("create"));
		assertRevisionEnvironment(logProps);
		assertEquals(15, logProps.size());
		return date;
	}

	private final void assertMutex(
			final Date date,
			final int expected, final int actual,
			final Map<Integer, byte[]> logs) throws ParseException
	{
		assertEquals(date, assertMutex(date, date, expected, actual, logs));
	}

	private final void assertRevisionEnvironment(final Properties p)
	{
		assertNotNull(hostname);
		assertNotNull(connectionUrl);
		assertNotNull(connectionUser);
		assertNotNull(info.getDatabaseProductName());
		assertNotNull(info.getDatabaseProductVersion());
		assertNotNull(info.getDriverName());
		assertNotNull(info.getDriverVersion());

		assertEquals(hostname, p.getProperty("env.hostname"));
		assertEquals(connectionUrl,  p.getProperty("env.connection.url"));
		assertEquals(connectionUser, p.getProperty("env.connection.user"));
		assertEquals(info.getDatabaseProductName(),             p.getProperty("env.database.name"));
		assertEquals(info.getDatabaseProductVersion(),          p.getProperty("env.database.version"));
		assertEquals(valueOf(info.getDatabaseMajorVersion()),   p.getProperty("env.database.version.major"));
		assertEquals(valueOf(info.getDatabaseMinorVersion()),   p.getProperty("env.database.version.minor"));
		assertEquals(info.getDriverName(),                  p.getProperty("env.driver.name"));
		assertEquals(info.getDriverVersion(),               p.getProperty("env.driver.version"));
		assertEquals(valueOf(info.getDriverMajorVersion()), p.getProperty("env.driver.version.major"));
		assertEquals(valueOf(info.getDriverMinorVersion()), p.getProperty("env.driver.version.minor"));
	}

	private static final void assertMinInt(final int expectedMinimum, final String actual)
	{
		assertTrue(actual, Integer.parseInt(actual)>=expectedMinimum);
	}

	final String filterTableName(final String name)
	{
		return props.filterTableName(name);
	}

	private static void reconnect()
	{
		final ConnectProperties c = model7.getConnectProperties();
		model7.disconnect();
		model7.connect(c);
	}

	private final String synthetic(final String name, final String global)
	{
		return
			longSyntheticNames
			? (name + global)
			: name;
	}

	private static void assertEqualsLog(
			final Map<Integer, byte[]> expected,
			final Map<Integer, byte[]> actual)
	{
		assertEquals(convert(expected), convert(actual));
	}

	private static Map<Integer, String> convert(final Map<Integer, byte[]> map)
	{
		final Map<Integer, String> result = new LinkedHashMap<Integer, String>();
		for(final Map.Entry<Integer, byte[]> e : map.entrySet())
			result.put(e.getKey(), Hex.encodeLower(e.getValue()));
		return result;
	}

	private static Map<Integer, byte[]> remove(final int key, final Map<Integer, byte[]> map)
	{
		final Map<Integer, byte[]> result = new LinkedHashMap<Integer, byte[]>();
		for(final Map.Entry<Integer, byte[]> e : map.entrySet())
			if(key!=e.getKey().intValue())
				result.put(e.getKey(), e.getValue());
		return result;
	}

	private static final class TestRevisionsFactory implements Revisions.Factory
	{
		private Revisions revisions = null;

		TestRevisionsFactory()
		{
			// make non-private
		}

		void put(final Revisions revisions)
		{
			assertNotNull(revisions);
			assertNull(this.revisions);
			this.revisions = revisions;
		}

		void assertEmpty()
		{
			assertNull(revisions);
		}

		public Revisions create(final Context ctx)
		{
			assertNotNull(ctx);
			assertNotNull(ctx.getEnvironment());
			assertNotNull(this.revisions);
			final Revisions revisions = this.revisions;
			this.revisions = null;
			return revisions;
		}
	}

	static final class TestSource implements Source
	{
		Source fallback;
		Map<String,String> overrides = new HashMap<String, String>();

		TestSource()
		{
			fallback = com.exedio.cope.util.Properties.getSource(ConnectProperties.getDefaultPropertyFile());
		}

		@Override()
		public String get( final String key )
		{
			final String override = overrides.get( key );
			return override==null ? fallback.get( key ) : override;
		}

		@Override()
		public Collection<String> keySet()
		{
			final Set<String> keys = new HashSet<String>();
			keys.addAll( overrides.keySet() );
			keys.addAll( fallback.keySet() );
			return keys;
		}

		@Override()
		public String getDescription()
		{
			return "TestSource";
		}

		void putOverride( final String key, final String value )
		{
			overrides.put( key, value );
		}

	}

}
