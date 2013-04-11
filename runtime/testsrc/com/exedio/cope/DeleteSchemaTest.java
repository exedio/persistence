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

import static com.exedio.cope.DeleteSchemaItem.nextSequence;

import java.util.Date;
import java.util.Map;

import com.exedio.cope.misc.DirectRevisionsFactory;

public class DeleteSchemaTest extends AbstractRuntimeTest
{
	private static final Model MODEL = new Model(
			DirectRevisionsFactory.make(new Revisions(5)),
			DeleteSchemaItem.TYPE);

	public DeleteSchemaTest()
	{
		super(MODEL);
		skipTransactionManagement();
	}

	public void testIt()
	{
		model.tearDownSchema();

		final Date createBefore = new Date();
		model.createSchema();
		final Date createAfter = new Date();

		final Date create;
		{
			final Map<Integer, byte[]> logs = model.getRevisionLogs();
			create = assertCreate(createBefore, createAfter, logs, 5);
			assertEquals(1, logs.size());
		}
		createEmptyAndCreate();

		model.deleteSchema();
		{
			final Map<Integer, byte[]> logs = model.getRevisionLogs();
			assertCreate(create, logs, 5);
			assertEquals(1, logs.size());
		}
		createEmptyAndCreate();
	}

	private static void createEmptyAndCreate()
	{
		try
		{
			MODEL.startTransaction(DeleteSchemaTest.class.getName());

			assertContains(DeleteSchemaItem.TYPE.search());

			final DeleteSchemaItem i1 = new DeleteSchemaItem("field1");
			final DeleteSchemaItem i2 = new DeleteSchemaItem("field2");

			assertEquals("DeleteSchemaItem-0", i1.getCopeID());
			assertEquals("DeleteSchemaItem-1", i2.getCopeID());

			assertEquals(1000, i1.getNext());
			assertEquals(1001, i2.getNext());

			assertEquals(2000, nextSequence());
			assertEquals(2001, nextSequence());

			MODEL.commit();
		}
		finally
		{
			MODEL.rollbackIfNotCommitted();
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
