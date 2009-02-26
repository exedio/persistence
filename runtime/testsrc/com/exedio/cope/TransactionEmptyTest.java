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

import java.util.Date;

public class TransactionEmptyTest extends AbstractRuntimeTest
{
	public TransactionEmptyTest()
	{
		super(CacheIsolationTest.MODEL);
		skipTransactionManagement();
	}
	
	public void testEmptyTransaction()
	{
		assertEquals(false, model.hasCurrentTransaction());
		final long id = model.getNextTransactionId();
		assertEquals(0, id);
		assertNull(model.getLastTransactionStartDate());

		final Date beforeCommit = new Date();
		final Transaction emptyCommit = model.startTransaction("emptyCommit");
		final Date afterCommit = new Date();
		assertEquals(true, model.hasCurrentTransaction());
		assertSame(emptyCommit, model.getCurrentTransaction());
		assertEquals(id+1, model.getNextTransactionId());
		final Date startCommit = model.getLastTransactionStartDate();
		assertWithin(beforeCommit, afterCommit, startCommit);

		assertEquals(id, emptyCommit.getID());
		assertEquals("emptyCommit", emptyCommit.getName());
		assertEquals(startCommit, emptyCommit.getStartDate());
		assertEquals(false, emptyCommit.isClosed());
		assertSame(Thread.currentThread(), emptyCommit.getBoundThread());

		model.commit();
		assertEquals(false, model.hasCurrentTransaction());
		assertEquals(id+1, model.getNextTransactionId());

		assertEquals(id, emptyCommit.getID());
		assertEquals("emptyCommit", emptyCommit.getName());
		assertEquals(startCommit, emptyCommit.getStartDate());
		assertEquals(true, emptyCommit.isClosed());
		assertSame(null, emptyCommit.getBoundThread());

		final Date beforeRollback = new Date();
		final Transaction emptyRollback = model.startTransaction("emptyRollback");
		final Date afterRollback = new Date();
		assertEquals(true, model.hasCurrentTransaction());
		assertSame(emptyRollback, model.getCurrentTransaction());
		assertEquals(id+2, model.getNextTransactionId());
		final Date startRollback = model.getLastTransactionStartDate();
		assertWithin(beforeRollback, afterRollback, startRollback);

		assertEquals(id, emptyCommit.getID());
		assertEquals("emptyCommit", emptyCommit.getName());
		assertEquals(startCommit, emptyCommit.getStartDate());
		assertEquals(true, emptyCommit.isClosed());
		assertSame(null, emptyCommit.getBoundThread());

		assertEquals(id+1, emptyRollback.getID());
		assertEquals("emptyRollback", emptyRollback.getName());
		assertEquals(startRollback, emptyRollback.getStartDate());
		assertEquals(false, emptyRollback.isClosed());
		assertSame(Thread.currentThread(), emptyRollback.getBoundThread());

		model.rollback();
		assertEquals(false, model.hasCurrentTransaction());
		assertEquals(id+2, model.getNextTransactionId());
		
		assertEquals(id, emptyCommit.getID());
		assertEquals("emptyCommit", emptyCommit.getName());
		assertEquals(startCommit, emptyCommit.getStartDate());
		assertEquals(true, emptyCommit.isClosed());
		assertSame(null, emptyCommit.getBoundThread());

		assertEquals(id+1, emptyRollback.getID());
		assertEquals("emptyRollback", emptyRollback.getName());
		assertEquals(startRollback, emptyRollback.getStartDate());
		assertEquals(true, emptyRollback.isClosed());
		assertSame(null, emptyRollback.getBoundThread());
	}
}
