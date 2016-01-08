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

package com.exedio.cope;

import static com.exedio.cope.DeleteSchemaItem.nextSequence;
import static com.exedio.cope.tojunit.Assert.assertContains;
import static com.exedio.cope.tojunit.Assert.assertWithin;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.exedio.cope.misc.DirectRevisionsFactory;
import com.exedio.cope.tojunit.TestLogAppender;
import java.util.Date;
import java.util.Map;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DeleteSchemaTest extends AbstractRuntimeModelTest
{
	private static final Logger logger = Logger.getLogger(Database.class.getName() + "#deleteSchema");

	private static final Model MODEL = new Model(
			DirectRevisionsFactory.make(new Revisions(5)),
			DeleteSchemaItem.TYPE,
			DeleteSchemaItemSuper.TYPE,
			DeleteSchemaItemUnused.TYPE,
			DeleteSchemaPointerA.TYPE,
			DeleteSchemaPointerB.TYPE);

	public DeleteSchemaTest()
	{
		super(MODEL);
	}

	@Override
	protected boolean doesManageTransactions()
	{
		return false;
	}

	TestLogAppender log = null;
	Level logLevel = null;
	private Date create;

	@Before public final void setUp()
	{
		log = new TestLogAppender();
		logger.addAppender(log);

		logLevel = logger.getLevel();
		logger.setLevel(Level.DEBUG);

		{
			final Map<Integer, byte[]> logs = model.getRevisionLogs();
			final byte[] log = logs.get(5);
			assertNotNull(log);
			final RevisionInfoCreate c = (RevisionInfoCreate)RevisionInfo.read(log);
			assertNotNull(c);
			assertEquals(5, c.getNumber());
			create = c.getDate();
			assertEquals(1, logs.size());
		}
	}

	@After public final void tearDown()
	{
		create = null;

		if(logLevel!=null)
			logger.setLevel(logLevel);
		logLevel = null;

		logger.removeAppender(log);
		log = null;
	}

	private static final String ALL =
			"deleteSchemaForTest  " +
			"tables 5 [" +
				"DeleteSchemaItemSuper, " +
				"DeleteSchemaItem, " +
				"DeleteSchemaItemUnused, " +
				"DeleteSchemaPointerA, " +
				"DeleteSchemaPointerB] " +
			"sequences 8 [" +
				"DeleteSchemaItemSuper.this, " +
				"DeleteSchemaItem.next, " +
				"DeleteSchemaItem.sequence, " +
				"DeleteSchemaItem.nextUnused, " +
				"DeleteSchemaItem.sequenceUnused, " +
				"DeleteSchemaItemUnused.this, " +
				"DeleteSchemaPointerA.this, " +
				"DeleteSchemaPointerB.this]";

	private static final String ALL_BUT_UNUSED =
			"deleteSchemaForTest  " +
			"tables 4 [" +
				"DeleteSchemaItemSuper, " +
				"DeleteSchemaItem, " +
				"DeleteSchemaPointerA, " +
				"DeleteSchemaPointerB] " +
			"sequences 5 [" +
				"DeleteSchemaItemSuper.this, " +
				"DeleteSchemaItem.next, " +
				"DeleteSchemaItem.sequence, " +
				"DeleteSchemaPointerA.this, " +
				"DeleteSchemaPointerB.this]";

	private static final String USED_SEQUENCES =
			"deleteSchemaForTest  " +
			"tables 0 [] " +
			"sequences 5 [" +
				"DeleteSchemaItemSuper.this, " +
				"DeleteSchemaItem.next, " +
				"DeleteSchemaItem.sequence, " +
				"DeleteSchemaPointerA.this, " +
				"DeleteSchemaPointerB.this]";

	private static final String EMPTY =
			"deleteSchemaForTest  " +
			"tables 0 [] " +
			"sequences 0 []";

	@Test public void testVirgin()
	{
		log.assertEmpty();
		model.deleteSchema();
		log.assertMessage(Level.DEBUG, ALL);
		assertRevisionLogs();

		assertEmptyAndCreate(true);

		log.assertEmpty();
		model.deleteSchema();
		log.assertMessage(Level.DEBUG, ALL);
		assertRevisionLogs();

		assertEmptyAndCreate(true);
	}

	@Test public void testVirginForTest()
	{
		log.assertEmpty();
		model.deleteSchemaForTest();
		log.assertMessage(Level.DEBUG, EMPTY);
		assertRevisionLogs();

		assertEmptyAndCreate(true);

		log.assertEmpty();
		model.deleteSchemaForTest();
		log.assertMessage(Level.DEBUG, ALL_BUT_UNUSED);
		assertRevisionLogs();

		assertEmptyAndCreate(true);
	}

	@Test public void testVirginForTestRepeat()
	{
		log.assertEmpty();
		model.deleteSchemaForTest();
		log.assertMessage(Level.DEBUG, EMPTY);
		assertRevisionLogs();

		log.assertEmpty();
		model.deleteSchemaForTest();
		log.assertMessage(Level.DEBUG, EMPTY);
		assertRevisionLogs();

		assertEmptyAndCreate(true);
	}

	@Test public void testCommitted()
	{
		assertEmptyAndCreate(true);

		log.assertEmpty();
		model.deleteSchema();
		log.assertMessage(Level.DEBUG, ALL);
		assertRevisionLogs();

		assertEmptyAndCreate(true);

		log.assertEmpty();
		model.deleteSchema();
		log.assertMessage(Level.DEBUG, ALL);
		assertRevisionLogs();

		assertEmptyAndCreate(true);
	}

	@Test public void testCommittedForTest()
	{
		assertEmptyAndCreate(true);

		log.assertEmpty();
		model.deleteSchemaForTest();
		log.assertMessage(Level.DEBUG, ALL_BUT_UNUSED);
		assertRevisionLogs();

		assertEmptyAndCreate(true);

		log.assertEmpty();
		model.deleteSchemaForTest();
		log.assertMessage(Level.DEBUG, ALL_BUT_UNUSED);
		assertRevisionLogs();

		assertEmptyAndCreate(true);
	}

	@Test public void testCommittedForTestRepeat()
	{
		assertEmptyAndCreate(true);

		log.assertEmpty();
		model.deleteSchemaForTest();
		log.assertMessage(Level.DEBUG, ALL_BUT_UNUSED);
		assertRevisionLogs();

		log.assertEmpty();
		model.deleteSchemaForTest();
		log.assertMessage(Level.DEBUG, EMPTY);
		assertRevisionLogs();

		assertEmptyAndCreate(true);
	}

	@Test public void testRolledback()
	{
		assertEmptyAndCreate(false);

		log.assertEmpty();
		model.deleteSchema();
		log.assertMessage(Level.DEBUG, ALL);
		assertRevisionLogs();

		assertEmptyAndCreate(false);

		log.assertEmpty();
		model.deleteSchema();
		log.assertMessage(Level.DEBUG, ALL);
		assertRevisionLogs();

		assertEmptyAndCreate(true);
	}

	@Test public void testRolledbackForTest()
	{
		assertEmptyAndCreate(false);

		log.assertEmpty();
		model.deleteSchemaForTest();
		log.assertMessage(Level.DEBUG, USED_SEQUENCES);
		assertRevisionLogs();

		assertEmptyAndCreate(false);

		log.assertEmpty();
		model.deleteSchemaForTest();
		log.assertMessage(Level.DEBUG, USED_SEQUENCES);
		assertRevisionLogs();

		assertEmptyAndCreate(true);
	}

	@Test public void testRolledbackForTestRepeat()
	{
		assertEmptyAndCreate(false);

		log.assertEmpty();
		model.deleteSchemaForTest();
		log.assertMessage(Level.DEBUG, USED_SEQUENCES);
		assertRevisionLogs();

		log.assertEmpty();
		model.deleteSchemaForTest();
		log.assertMessage(Level.DEBUG, EMPTY);
		assertRevisionLogs();

		assertEmptyAndCreate(true);
	}

	private void assertRevisionLogs()
	{
		final Map<Integer, byte[]> logs = model.getRevisionLogs();
		assertCreate(create, logs, 5);
		assertEquals(1, logs.size());
	}

	private static void assertEmptyAndCreate(final boolean commit)
	{
		try(TransactionTry tx = MODEL.startTransactionTry(DeleteSchemaTest.class.getName()))
		{
			assertContains(DeleteSchemaItem.TYPE.search());
			assertContains(DeleteSchemaItemUnused.TYPE.search());
			assertContains(DeleteSchemaPointerA.TYPE.search());
			assertContains(DeleteSchemaPointerB.TYPE.search());

			final DeleteSchemaItem i1 = new DeleteSchemaItem("field1");
			final DeleteSchemaItem i2 = new DeleteSchemaItem("field2");

			assertEquals("DeleteSchemaItem-0", i1.getCopeID());
			assertEquals("DeleteSchemaItem-1", i2.getCopeID());

			assertEquals(1000, i1.getNext());
			assertEquals(1001, i2.getNext());

			assertEquals(-10, i1.getNextUnused());
			assertEquals(-10, i2.getNextUnused());

			assertEquals(2000, nextSequence());
			assertEquals(2001, nextSequence());

			final DeleteSchemaPointerA a1 = new DeleteSchemaPointerA(100);
			final DeleteSchemaPointerA a2 = new DeleteSchemaPointerA(100);
			final DeleteSchemaPointerA a3 = new DeleteSchemaPointerA(100);
			a1.setSelf(a2); // reference forward
			a2.setSelf(a2); // reference self
			a3.setSelf(a2); // reference backward
			final DeleteSchemaPointerB b = new DeleteSchemaPointerB(100);
			a1.setOther(b);
			b.setOther(a1);

			if(commit)
				tx.commit();
		}
	}

	private static final void assertCreate(final Date date, final Map<Integer, byte[]> logs, final int revision)
	{
		assertCreate(date, date, logs, revision);
	}

	private static final Date assertCreate(final Date before, final Date after, final Map<Integer, byte[]> logs, final int revision)
	{
		final byte[] log = logs.get(revision);
		assertNotNull(log);
		final RevisionInfoCreate c = (RevisionInfoCreate)RevisionInfo.read(log);
		assertNotNull(c);
		assertEquals(revision, c.getNumber());
		final Date date = c.getDate();
		assertWithin(before, after, date);
		return date;
	}
}
