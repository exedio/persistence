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

import static com.exedio.cope.PrometheusMeterRegistrar.meter;
import static com.exedio.cope.PrometheusMeterRegistrar.tag;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.util.Pool;
import com.exedio.cope.util.PoolCounter;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TransactionCountersTest extends TestWithEnvironment
{
	public TransactionCountersTest()
	{
		super(CacheIsolationTest.MODEL);
		copeRule.omitTransaction();
	}

	private long commitWithoutConnectionStart;
	private long commitWithConnectionStart;
	private long rollbackWithoutConnectionStart;
	private long rollbackWithConnectionStart;
	private int poolGetStart;
	private int poolPutStart;
	private double poolGetMicrometerStart;
	private double poolPutMicrometerStart;
	private boolean m;

	@BeforeEach final void setUp()
	{
		final TransactionCounters c = model.getTransactionCounters();
		commitWithoutConnectionStart   = c.getCommitWithoutConnection();
		commitWithConnectionStart      = c.getCommitWithConnection();
		rollbackWithoutConnectionStart = c.getRollbackWithoutConnection();
		rollbackWithConnectionStart    = c.getRollbackWithConnection();
		final Pool.Info pi = model.getConnectionPoolInfo();
		poolGetStart = pi.getCounter().getGetCounter();
		poolPutStart = pi.getCounter().getPutCounter();
		poolGetMicrometerStart = countPool("usage", "get");
		poolPutMicrometerStart = countPool("usage", "put");
		m = model.getConnectProperties().primaryKeyGenerator!=PrimaryKeyGenerator.sequence;
	}

	@Test void testIt()
	{
		assertEquals(false, model.hasCurrentTransaction());
		assertIt(0, 0, 0, 0, 0, 0);

		model.startTransaction("emptyCommit");
		assertIt(0, 0, 0, 0, 0, 0);

		model.commit();
		assertIt(1, 0, 0, 0, 0, 0);

		model.startTransaction("nonemptyCommit");
		new CacheIsolationItem("commit").deleteCopeItem();
		assertIt(1, 0, 0, 0, 2, 1);

		model.commit();
		assertIt(1, 1, 0, 0, 2, 2);

		model.startTransaction("emptyRollback");
		assertIt(1, 1, 0, 0, 2, 2);

		model.rollback();
		assertIt(1, 1, 1, 0, 2, 2);

		model.startTransaction("nonemptyRollback");
		new CacheIsolationItem("rollback");
		assertIt(1, 1, 1, 0, m?3:4, m?2:3);

		model.rollback();
		assertIt(1, 1, 1, 1, m?3:4, m?3:4);
	}

	private void assertIt(
			final long commitWithoutConnection,
			final long commitWithConnection,
			final long rollbackWithoutConnection,
			final long rollbackWithConnection,
			final int poolGet,
			final int poolPut)
	{
		final TransactionCounters c = model.getTransactionCounters();
		assertEquals(  commitWithoutConnectionStart +   commitWithoutConnection, c.getCommitWithoutConnection());
		assertEquals(     commitWithConnectionStart +      commitWithConnection, c.getCommitWithConnection());
		assertEquals(rollbackWithoutConnectionStart + rollbackWithoutConnection, c.getRollbackWithoutConnection());
		assertEquals(   rollbackWithConnectionStart +    rollbackWithConnection, c.getRollbackWithConnection());

		assertEquals(c.getCommit  (), c.getCommitWithoutConnection  () + c.getCommitWithConnection  ());
		assertEquals(c.getRollback(), c.getRollbackWithoutConnection() + c.getRollbackWithConnection());

		assertEquals(c.getCommitWithoutConnection(),   count("commit",   "without"));
		assertEquals(c.getCommitWithConnection(),      count("commit",   "with"));
		assertEquals(c.getRollbackWithoutConnection(), count("rollback", "without"));
		assertEquals(c.getRollbackWithConnection(),    count("rollback", "with"));

		final Pool.Info pi = model.getConnectionPoolInfo();
		final PoolCounter pc = pi.getCounter();
		assertAll(
				() -> assertEquals(poolGet, pc.getGetCounter() - poolGetStart, "pool get"),
				() -> assertEquals(poolPut, pc.getPutCounter() - poolPutStart, "pool put"),
				() -> assertEquals(0, pi.getInvalidOnGet(), "pool invalid on get"),
				() -> assertEquals(0, pi.getInvalidOnPut(), "pool invalid on put"));

		assertEquals(poolGet, countPool("usage", "get") - poolGetMicrometerStart);
		assertEquals(poolPut, countPool("usage", "put") - poolPutMicrometerStart);
		assertEquals(0, countPool("invalid", "get"));
		assertEquals(0, countPool("invalid", "put"));
	}

	private double count(final String end, final String connection)
	{
		return ((Timer)meter(
				Transaction.class, "finished",
				tag(model).and("end", end, "connection", connection))).count();
	}

	private double countPool(final String nameSuffix, final String operation)
	{
		return ((Counter)meter(
				ConnectionPool.class, nameSuffix,
				tag(model).and("operation", operation))).count();
	}
}
