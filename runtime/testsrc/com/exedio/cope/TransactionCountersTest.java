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

public class TransactionCountersTest extends AbstractRuntimeTest
{
	public TransactionCountersTest()
	{
		super(CacheIsolationTest.MODEL);
		skipTransactionManagement();
	}
	
	private long commitWithoutConnectionStart;
	private long commitWithConnectionStart;
	private long rollbackWithoutConnectionStart;
	private long rollbackWithConnectionStart;
	
	@Override
	public void setUp()
	{
		final TransactionCounters c = model.getTransactionCounters();
		commitWithoutConnectionStart   = c.getCommitWithoutConnection();
		commitWithConnectionStart      = c.getCommitWithConnection();
		rollbackWithoutConnectionStart = c.getRollbackWithoutConnection();
		rollbackWithConnectionStart    = c.getRollbackWithConnection();
	}
	
	public void testIt()
	{
		assertEquals(false, model.hasCurrentTransaction());
		assertIt(0, 0, 0, 0);

		model.startTransaction("emptyCommit");
		assertIt(0, 0, 0, 0);

		model.commit();
		assertIt(1, 0, 0, 0);

		model.startTransaction("nonemptyCommit");
		new CacheIsolationItem("commit").deleteCopeItem();
		assertIt(1, 0, 0, 0);

		model.commit();
		assertIt(1, 1, 0, 0);
		
		model.startTransaction("emptyRollback");
		assertIt(1, 1, 0, 0);

		model.rollback();
		assertIt(1, 1, 1, 0);
		
		model.startTransaction("nonemptyRollback");
		new CacheIsolationItem("rollback");
		assertIt(1, 1, 1, 0);

		model.rollback();
		assertIt(1, 1, 1, 1);
	}
	
	private void assertIt(
			final long commitWithoutConnection,
			final long commitWithConnection,
			final long rollbackWithoutConnection,
			final long rollbackWithConnection)
	{
		final TransactionCounters c = model.getTransactionCounters();
		assertEquals(  commitWithoutConnectionStart +   commitWithoutConnection, c.getCommitWithoutConnection());
		assertEquals(     commitWithConnectionStart +      commitWithConnection, c.getCommitWithConnection());
		assertEquals(rollbackWithoutConnectionStart + rollbackWithoutConnection, c.getRollbackWithoutConnection());
		assertEquals(   rollbackWithConnectionStart +    rollbackWithConnection, c.getRollbackWithConnection());
	}
}
