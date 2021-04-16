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

import static com.exedio.cope.PrometheusMeterRegistrar.meterCope;
import static com.exedio.cope.PrometheusMeterRegistrar.tag;
import static com.exedio.cope.tojunit.Assert.assertContains;
import static com.exedio.cope.tojunit.Assert.assertUnmodifiable;
import static com.exedio.cope.tojunit.Assert.assertWithin;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.fail;

import io.micrometer.core.instrument.Gauge;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.junit.jupiter.api.Test;

public class TransactionOnlyTest extends TestWithEnvironment
{
	public TransactionOnlyTest()
	{
		super(CacheIsolationTest.MODEL);
	}

	@Test void testNesting()
	{
		assertEquals( true, model.hasCurrentTransaction() );
		final Transaction tx = model.currentTransaction();
		try
		{
			model.startTransaction("nested");
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals(
					"tried to start a new transaction nested, " +
					"but there is already a transaction " + model.currentTransaction().getID() +
					":tx:com.exedio.cope.TransactionOnlyTest " +
					"started on " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS z (Z)", Locale.ENGLISH).format(tx.getStartDate()) +
					" bound to current thread",
					e.getMessage());
		}
		assertEquals( tx, model.currentTransaction() );
		try
		{
			model.startTransaction(null);
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals(
					"tried to start a new transaction ANONYMOUS_TRANSACTION, " +
					"but there is already a transaction " + model.currentTransaction().getID() +
					":tx:com.exedio.cope.TransactionOnlyTest " +
					"started on " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS z (Z)", Locale.ENGLISH).format(tx.getStartDate()) +
					" bound to current thread",
					e.getMessage());
		}
		assertEquals(tx, model.currentTransaction());
	}

	@Test void testJoinClosed()
	{
		final Transaction tx = model.currentTransaction();
		model.commit();
		try
		{
			model.joinTransaction( tx );
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("cannot join closed transaction " + tx.id, e.getMessage());
		}
		assertEquals( false, model.hasCurrentTransaction() );
		model.startTransaction("just for tearDown");
		assertEquals( "just for tearDown", model.currentTransaction().getName() );
	}

	static class IllegalStateExceptionReference
	{
		IllegalStateException e = null;
	}

	@Test void testJoinMultiple() throws InterruptedException
	{
		final Transaction tx = model.currentTransaction();
		final IllegalStateExceptionReference rer = new IllegalStateExceptionReference();

		final Thread t2 = new Thread(() ->
		{
			try
			{
				model.joinTransaction(tx);
			}
			catch(final IllegalStateException e)
			{
				rer.e = e;
			}
		});
		t2.start();
		t2.join();
		assertNotNull(rer.e);
		assertEquals("transaction already bound to other thread: " + Thread.currentThread().getId(), rer.e.getMessage());
	}

	@Test void testTransactionLifecycle()
	{
		final Transaction copeTest = model.currentTransaction();
		assertContains( copeTest, model.getOpenTransactions() );
		assertEquals(1, count());
		assertUnmodifiable( model.getOpenTransactions() );
		assertSame(Thread.currentThread(), copeTest.getBoundThread());
		assertEquals( false, copeTest.isClosed() );
		assertCurrentTransaction( copeTest );

		model.leaveTransaction();
		assertContains( copeTest, model.getOpenTransactions() );
		assertEquals(1, count());
		assertSame(null, copeTest.getBoundThread());
		assertEquals( false, copeTest.isClosed() );
		assertCurrentTransaction( null );

		final Date before = new Date();
		final Transaction tx1 = model.startTransaction( "tx1" );
		final Date after = new Date();
		assertContains( copeTest, tx1, model.getOpenTransactions() );
		assertEquals(2, count());
		assertEquals(copeTest.getID()+1, tx1.getID());
		assertEquals("tx1", tx1.getName());
		assertWithin(before, after, tx1.getStartDate());
		assertSame(null, copeTest.getBoundThread());
		assertSame(Thread.currentThread(), tx1.getBoundThread());
		assertEquals( false, copeTest.isClosed() );
		assertEquals( false, tx1.isClosed() );
		assertCurrentTransaction( tx1 );

		model.leaveTransaction();
		assertContains( copeTest, tx1, model.getOpenTransactions() );
		assertEquals(2, count());
		assertSame(null, copeTest.getBoundThread());
		assertSame(null, tx1.getBoundThread());
		assertEquals( false, copeTest.isClosed() );
		assertEquals( false, tx1.isClosed() );
		assertCurrentTransaction( null );

		model.joinTransaction( copeTest );
		assertContains( copeTest, tx1, model.getOpenTransactions() );
		assertEquals(2, count());
		assertSame(Thread.currentThread(), copeTest.getBoundThread());
		assertSame(null, tx1.getBoundThread());
		assertEquals( false, copeTest.isClosed() );
		assertEquals( false, tx1.isClosed() );
		assertCurrentTransaction( copeTest );

		model.commit();
		assertContains( tx1, model.getOpenTransactions() );
		assertEquals(1, count());
		assertSame(null, copeTest.getBoundThread());
		assertSame(null, tx1.getBoundThread());
		assertEquals( true, copeTest.isClosed() );
		assertEquals( false, tx1.isClosed() );
		assertCurrentTransaction( null );

		model.joinTransaction( tx1 );
		assertContains( tx1, model.getOpenTransactions() );
		assertEquals(1, count());
		assertSame(null, copeTest.getBoundThread());
		assertSame(Thread.currentThread(), tx1.getBoundThread());
		assertEquals( true, copeTest.isClosed() );
		assertEquals( false, tx1.isClosed() );
		assertCurrentTransaction( tx1 );

		model.rollback();
		assertContains( model.getOpenTransactions() );
		assertEquals(0, count());
		assertSame(null, copeTest.getBoundThread());
		assertSame(null, tx1.getBoundThread());
		assertEquals( true, copeTest.isClosed() );
		assertEquals( true, tx1.isClosed() );
		assertCurrentTransaction( null );

		model.startTransaction( "forTearDown" );
	}

	@Test void testRollbackIfNotCommitted()
	{
		assertEquals(true, model.hasCurrentTransaction());

		model.rollbackIfNotCommitted();
		assertEquals(false, model.hasCurrentTransaction());

		model.rollbackIfNotCommitted();
		assertEquals(false, model.hasCurrentTransaction());
	}

	@Test void testRollbackIfNotCommittedVerbosely()
	{
		assertEquals(true, model.hasCurrentTransaction());

		assertEquals(true, model.rollbackIfNotCommittedVerbosely());
		assertEquals(false, model.hasCurrentTransaction());

		assertEquals(false, model.rollbackIfNotCommittedVerbosely());
		assertEquals(false, model.hasCurrentTransaction());
	}

	private void assertCurrentTransaction( final Transaction tx )
	{
		assertEquals( tx!=null, model.hasCurrentTransaction() );
		if ( tx==null )
		{
			try
			{
				model.currentTransaction();
				fail();
			}
			catch(final IllegalStateException e)
			{
				assertEquals( "there is no cope transaction bound to this thread, see Model#startTransaction", e.getMessage() );
			}
		}
		else
		{
			assertEquals( tx, model.currentTransaction() );
		}
	}

	private double count()
	{
		return ((Gauge)meterCope(Transaction.class, "open", tag(model))).value();
	}
}
