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

public class TransactionEmptyTest extends AbstractLibTest
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

		final Transaction emptyCommit = model.startTransaction("emptyCommit");
		assertEquals(true, model.hasCurrentTransaction());
		assertSame(emptyCommit, model.getCurrentTransaction());
		assertEquals(id+1, model.getNextTransactionId());
		assertEquals(id, emptyCommit.getID());
		assertEquals("emptyCommit", emptyCommit.getName());
		assertEquals(false, emptyCommit.isClosed());
		assertSame(Thread.currentThread(), emptyCommit.getBoundThread());

		model.commit();
		assertEquals(false, model.hasCurrentTransaction());
		assertEquals(id+1, model.getNextTransactionId());
		assertEquals(id, emptyCommit.getID());
		assertEquals("emptyCommit", emptyCommit.getName());
		assertEquals(true, emptyCommit.isClosed());
		assertSame(null, emptyCommit.getBoundThread());

		final Transaction emptyRollback = model.startTransaction("emptyRollback");
		assertEquals(true, model.hasCurrentTransaction());
		assertSame(emptyRollback, model.getCurrentTransaction());
		assertEquals(id+2, model.getNextTransactionId());
		assertEquals(id, emptyCommit.getID());
		assertEquals("emptyCommit", emptyCommit.getName());
		assertEquals(true, emptyCommit.isClosed());
		assertSame(null, emptyCommit.getBoundThread());
		assertEquals(id+1, emptyRollback.getID());
		assertEquals("emptyRollback", emptyRollback.getName());
		assertEquals(false, emptyRollback.isClosed());
		assertSame(Thread.currentThread(), emptyRollback.getBoundThread());

		model.rollback();
		assertEquals(false, model.hasCurrentTransaction());
		assertEquals(id+2, model.getNextTransactionId());
		assertEquals(id, emptyCommit.getID());
		assertEquals("emptyCommit", emptyCommit.getName());
		assertEquals(true, emptyCommit.isClosed());
		assertSame(null, emptyCommit.getBoundThread());
		assertEquals(id+1, emptyRollback.getID());
		assertEquals("emptyRollback", emptyRollback.getName());
		assertEquals(true, emptyRollback.isClosed());
		assertSame(null, emptyRollback.getBoundThread());
	}
}
