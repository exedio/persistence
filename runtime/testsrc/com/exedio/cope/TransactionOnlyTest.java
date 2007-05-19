package com.exedio.cope;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TransactionOnlyTest extends AbstractLibTest
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
			assertEquals( "cannot bind to closed transaction", e.getMessage() );
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
		assertSame(Thread.currentThread()/* TODO should be null */, copeTest.getBoundThread());
		assertSame(null, tx1.getBoundThread());
		assertEquals( true, copeTest.isClosed() );
		assertEquals( false, tx1.isClosed() );
		assertCurrentTransaction( null );
		
		model.joinTransaction( tx1 );
		assertContains( tx1, model.getOpenTransactions() );
		assertSame(Thread.currentThread()/* TODO should be null */, copeTest.getBoundThread());
		assertSame(Thread.currentThread(), tx1.getBoundThread());
		assertEquals( true, copeTest.isClosed() );
		assertEquals( false, tx1.isClosed() );
		assertCurrentTransaction( tx1 );
		
		model.rollback();
		assertContains( model.getOpenTransactions() );
		assertSame(Thread.currentThread()/* TODO should be null */, copeTest.getBoundThread());
		assertSame(Thread.currentThread()/* TODO should be null */, tx1.getBoundThread());
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
