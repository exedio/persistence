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

import static com.exedio.cope.tojunit.Assert.assertWithin;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import com.exedio.cope.tojunit.MainRule;
import com.exedio.cope.tojunit.TransactionIdRule;
import java.util.Date;
import org.junit.jupiter.api.Test;

@MainRule.Tag
public class TransactionEmptyTest extends TestWithEnvironment
{
	public TransactionEmptyTest()
	{
		super(CacheIsolationTest.MODEL);
		copeRule.omitTransaction();
	}

	private final TransactionIdRule txId = new TransactionIdRule(model);

	@Test public void testEmptyTransaction()
	{
		assertEquals(false, model.hasCurrentTransaction());

		final Date beforeCommit = new Date();
		final Transaction emptyCommit = model.startTransaction("emptyCommit");
		final Date afterCommit = new Date();
		assertEquals(true, model.hasCurrentTransaction());
		assertSame(emptyCommit, model.currentTransaction());
		txId.assertEquals(1, model.getNextTransactionId());
		final Date startCommit = model.getLastTransactionStartDate();
		assertWithin(beforeCommit, afterCommit, startCommit);

		txId.assertEquals(0, emptyCommit.getID());
		assertEquals("emptyCommit", emptyCommit.getName());
		assertEquals(startCommit, emptyCommit.getStartDate());
		assertEquals(false, emptyCommit.isClosed());
		assertSame(Thread.currentThread(), emptyCommit.getBoundThread());

		model.commit();
		assertEquals(false, model.hasCurrentTransaction());
		txId.assertEquals(1, model.getNextTransactionId());

		txId.assertEquals(0, emptyCommit.getID());
		assertEquals("emptyCommit", emptyCommit.getName());
		assertEquals(startCommit, emptyCommit.getStartDate());
		assertEquals(true, emptyCommit.isClosed());
		assertSame(null, emptyCommit.getBoundThread());

		final Date beforeRollback = new Date();
		final Transaction emptyRollback = model.startTransaction("emptyRollback");
		final Date afterRollback = new Date();
		assertEquals(true, model.hasCurrentTransaction());
		assertSame(emptyRollback, model.currentTransaction());
		txId.assertEquals(2, model.getNextTransactionId());
		final Date startRollback = model.getLastTransactionStartDate();
		assertWithin(beforeRollback, afterRollback, startRollback);

		txId.assertEquals(0, emptyCommit.getID());
		assertEquals("emptyCommit", emptyCommit.getName());
		assertEquals(startCommit, emptyCommit.getStartDate());
		assertEquals(true, emptyCommit.isClosed());
		assertSame(null, emptyCommit.getBoundThread());

		txId.assertEquals(1, emptyRollback.getID());
		assertEquals("emptyRollback", emptyRollback.getName());
		assertEquals(startRollback, emptyRollback.getStartDate());
		assertEquals(false, emptyRollback.isClosed());
		assertSame(Thread.currentThread(), emptyRollback.getBoundThread());

		model.rollback();
		assertEquals(false, model.hasCurrentTransaction());
		txId.assertEquals(2, model.getNextTransactionId());

		txId.assertEquals(0, emptyCommit.getID());
		assertEquals("emptyCommit", emptyCommit.getName());
		assertEquals(startCommit, emptyCommit.getStartDate());
		assertEquals(true, emptyCommit.isClosed());
		assertSame(null, emptyCommit.getBoundThread());

		txId.assertEquals(1, emptyRollback.getID());
		assertEquals("emptyRollback", emptyRollback.getName());
		assertEquals(startRollback, emptyRollback.getStartDate());
		assertEquals(true, emptyRollback.isClosed());
		assertSame(null, emptyRollback.getBoundThread());
	}
}
