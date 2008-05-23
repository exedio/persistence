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

import java.text.SimpleDateFormat;
import java.util.Date;

public class TransactionOnlyTest extends AbstractRuntimeTest
{
	public TransactionOnlyTest()
	{
		super(CacheIsolationTest.MODEL);
	}
	
	public void testNesting()
	{
		assertEquals( true, model.hasCurrentTransaction() );
		final Transaction tx = model.getCurrentTransaction();
		try
		{
			model.startTransaction("nested");
			fail();
		}
		catch(IllegalStateException e)
		{
			assertEquals(
					"tried to start a new transaction with name >nested<, " +
					"but there is already a transaction CT." + model.getCurrentTransaction().getID() +
					" with name >CopeTest< " +
					"started on " + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS").format(tx.getStartDate()) +
					" bound to current thread",
					e.getMessage());
		}
		assertEquals( tx, model.getCurrentTransaction() );
		try
		{
			model.startTransaction(null);
			fail();
		}
		catch(IllegalStateException e)
		{
			assertEquals(
					"tried to start a new transaction without a name, " +
					"but there is already a transaction CT." + model.getCurrentTransaction().getID() +
					" with name >CopeTest< " +
					"started on " + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS").format(tx.getStartDate()) +
					" bound to current thread",
					e.getMessage());
		}
		assertEquals(tx, model.getCurrentTransaction());
	}
	
	public void testJoinClosed()
	{
		Transaction tx = model.getCurrentTransaction();
		model.commit();
		try
		{
			model.joinTransaction( tx );
			fail();
		}
		catch(IllegalStateException e)
		{
			assertEquals("cannot join closed transaction " + tx.id, e.getMessage());
		}
		assertEquals( false, model.hasCurrentTransaction() );
		model.startTransaction("just for tearDown");
		assertEquals( "just for tearDown", model.getCurrentTransaction().getName() );
	}
	
	static class IllegalStateExceptionReference
	{
		IllegalStateException e = null;
	}
	
	public void testJoinMultiple() throws InterruptedException
	{
		final Transaction tx = model.getCurrentTransaction();
		final IllegalStateExceptionReference rer = new IllegalStateExceptionReference();
		
		final Thread t2 = new Thread(new Runnable(){
			public void run()
			{
				try
				{
					model.joinTransaction(tx);
				}
				catch(IllegalStateException e)
				{
					rer.e = e;
				}
			}
		});
		t2.start();
		t2.join();
		assertNotNull(rer.e);
		assertEquals("transaction already bound to other thread: " + Thread.currentThread().getId(), rer.e.getMessage());
	}
	
	public void testTransactionLifecycle()
	{
		final Transaction copeTest = model.getCurrentTransaction();
		assertContains( copeTest, model.getOpenTransactions() );
		assertUnmodifiable( model.getOpenTransactions() );
		assertSame(Thread.currentThread(), copeTest.getBoundThread());
		assertEquals( false, copeTest.isClosed() );
		assertCurrentTransaction( copeTest );
		
		model.leaveTransaction();
		assertContains( copeTest, model.getOpenTransactions() );
		assertSame(null, copeTest.getBoundThread());
		assertEquals( false, copeTest.isClosed() );
		assertCurrentTransaction( null );
		
		final Date before = new Date();
		final Transaction tx1 = model.startTransaction( "tx1" );
		final Date after = new Date();
		assertContains( copeTest, tx1, model.getOpenTransactions() );
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
		assertSame(null, copeTest.getBoundThread());
		assertSame(null, tx1.getBoundThread());
		assertEquals( false, copeTest.isClosed() );
		assertEquals( false, tx1.isClosed() );
		assertCurrentTransaction( null );
		
		model.joinTransaction( copeTest );
		assertContains( copeTest, tx1, model.getOpenTransactions() );
		assertSame(Thread.currentThread(), copeTest.getBoundThread());
		assertSame(null, tx1.getBoundThread());
		assertEquals( false, copeTest.isClosed() );
		assertEquals( false, tx1.isClosed() );
		assertCurrentTransaction( copeTest );
		
		model.commit();
		assertContains( tx1, model.getOpenTransactions() );
		assertSame(null, copeTest.getBoundThread());
		assertSame(null, tx1.getBoundThread());
		assertEquals( true, copeTest.isClosed() );
		assertEquals( false, tx1.isClosed() );
		assertCurrentTransaction( null );
		
		model.joinTransaction( tx1 );
		assertContains( tx1, model.getOpenTransactions() );
		assertSame(null, copeTest.getBoundThread());
		assertSame(Thread.currentThread(), tx1.getBoundThread());
		assertEquals( true, copeTest.isClosed() );
		assertEquals( false, tx1.isClosed() );
		assertCurrentTransaction( tx1 );
		
		model.rollback();
		assertContains( model.getOpenTransactions() );
		assertSame(null, copeTest.getBoundThread());
		assertSame(null, tx1.getBoundThread());
		assertEquals( true, copeTest.isClosed() );
		assertEquals( true, tx1.isClosed() );
		assertCurrentTransaction( null );
		
		model.startTransaction( "forTearDown" );
	}
	
	private void assertCurrentTransaction( Transaction tx )
	{
		assertEquals( tx!=null, model.hasCurrentTransaction() );
		if ( tx==null )
		{
			try
			{
				model.getCurrentTransaction();
				fail();
			}
			catch(IllegalStateException e)
			{
				assertEquals( "there is no cope transaction bound to this thread, see Model#startTransaction", e.getMessage() );
			}
		}
		else
		{
			assertEquals( tx, model.getCurrentTransaction() );
		}
	}
}
