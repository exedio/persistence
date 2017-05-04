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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.exedio.cope.misc.DirectRevisionsFactory;
import com.exedio.cope.tojunit.LogRule;
import java.util.Date;
import java.util.Map;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

public class DeleteSchemaTest extends TestWithEnvironment
{
	private static final Model MODEL = Model.builder().
			add(DirectRevisionsFactory.make(new Revisions(5))).
			add(
				DeleteSchemaItem.TYPE,
				DeleteSchemaItemSuper.TYPE,
				DeleteSchemaItemUnused.TYPE,
				DeleteSchemaPointerA.TYPE,
				DeleteSchemaPointerB.TYPE).
			build();

	public DeleteSchemaTest()
	{
		super(MODEL);
		copeRule.omitTransaction();
	}

	private final LogRule log = new LogRule(Database.class.getName() + ".deleteSchema");

	@Rule public final RuleChain ruleChain = RuleChain.outerRule(log);

	private Date create;

	@Before public final void setUp()
	{
		log.setLevelDebug();

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
		log.assertDebug(ALL);
		assertRevisionLogs();

		assertEmptyAndCreate(true);

		log.assertEmpty();
		model.deleteSchema();
		log.assertDebug(ALL);
		assertRevisionLogs();

		assertEmptyAndCreate(true);
	}

	@Test public void testVirginForTest()
	{
		log.assertEmpty();
		model.deleteSchemaForTest();
		log.assertDebug(EMPTY);
		assertRevisionLogs();

		assertEmptyAndCreate(true);

		log.assertEmpty();
		model.deleteSchemaForTest();
		log.assertDebug(ALL_BUT_UNUSED);
		assertRevisionLogs();

		assertEmptyAndCreate(true);
	}

	@Test public void testVirginForTestRepeat()
	{
		log.assertEmpty();
		model.deleteSchemaForTest();
		log.assertDebug(EMPTY);
		assertRevisionLogs();

		log.assertEmpty();
		model.deleteSchemaForTest();
		log.assertDebug(EMPTY);
		assertRevisionLogs();

		assertEmptyAndCreate(true);
	}

	@Test public void testCommitted()
	{
		assertEmptyAndCreate(true);

		log.assertEmpty();
		model.deleteSchema();
		log.assertDebug(ALL);
		assertRevisionLogs();

		assertEmptyAndCreate(true);

		log.assertEmpty();
		model.deleteSchema();
		log.assertDebug(ALL);
		assertRevisionLogs();

		assertEmptyAndCreate(true);
	}

	@Test public void testCommittedForTest()
	{
		assertEmptyAndCreate(true);

		log.assertEmpty();
		model.deleteSchemaForTest();
		log.assertDebug(ALL_BUT_UNUSED);
		assertRevisionLogs();

		assertEmptyAndCreate(true);

		log.assertEmpty();
		model.deleteSchemaForTest();
		log.assertDebug(ALL_BUT_UNUSED);
		assertRevisionLogs();

		assertEmptyAndCreate(true);
	}

	@Test public void testCommittedForTestRepeat()
	{
		assertEmptyAndCreate(true);

		log.assertEmpty();
		model.deleteSchemaForTest();
		log.assertDebug(ALL_BUT_UNUSED);
		assertRevisionLogs();

		log.assertEmpty();
		model.deleteSchemaForTest();
		log.assertDebug(EMPTY);
		assertRevisionLogs();

		assertEmptyAndCreate(true);
	}

	@Test public void testRolledback()
	{
		assertEmptyAndCreate(false);

		log.assertEmpty();
		model.deleteSchema();
		log.assertDebug(ALL);
		assertRevisionLogs();

		assertEmptyAndCreate(false);

		log.assertEmpty();
		model.deleteSchema();
		log.assertDebug(ALL);
		assertRevisionLogs();

		assertEmptyAndCreate(true);
	}

	@Test public void testRolledbackForTest()
	{
		assertEmptyAndCreate(false);

		log.assertEmpty();
		model.deleteSchemaForTest();
		log.assertDebug(USED_SEQUENCES);
		assertRevisionLogs();

		assertEmptyAndCreate(false);

		log.assertEmpty();
		model.deleteSchemaForTest();
		log.assertDebug(USED_SEQUENCES);
		assertRevisionLogs();

		assertEmptyAndCreate(true);
	}

	@Test public void testRolledbackForTestRepeat()
	{
		assertEmptyAndCreate(false);

		log.assertEmpty();
		model.deleteSchemaForTest();
		log.assertDebug(USED_SEQUENCES);
		assertRevisionLogs();

		log.assertEmpty();
		model.deleteSchemaForTest();
		log.assertDebug(EMPTY);
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

	private static void assertCreate(final Date date, final Map<Integer, byte[]> logs, final int revision)
	{
		final byte[] log = logs.get(revision);
		assertNotNull(log);
		final RevisionInfoCreate c = (RevisionInfoCreate)RevisionInfo.read(log);
		assertNotNull(c);
		assertEquals(revision, c.getNumber());
		assertEquals(date, c.getDate());
	}
}
