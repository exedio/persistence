package com.exedio.cope;

public class TransactionOnlyTest extends AbstractLibTest
{
	
	public TransactionOnlyTest()
	{
		super( Main.cacheIsolationModel );
	}
	
	public void testNesting() 
	{
		assertEquals( true, model.hasCurrentTransaction() );
		Transaction tx = model.getCurrentTransaction();
		try
		{
			model.startTransaction("nested");
			fail();
		}
		catch ( RuntimeException e )
		{
			assertEquals( "there is already a transaction bound to current thread", e.getMessage() );
		}
		assertEquals( tx, model.getCurrentTransaction() );
	}
	
	public void testJoinClosed()
	{
		Transaction tx = model.getCurrentTransaction();
		model.commit();
		try
		{
			model.joinTransaction( tx );
		}
		catch ( RuntimeException e )
		{
			assertEquals( "cannot bind to closed transaction", e.getMessage() );
		}
		assertEquals( false, model.hasCurrentTransaction() );
		model.startTransaction("just for tearDown");
		assertEquals( "just for tearDown", model.getCurrentTransaction().getName() );
	}
	
	public void testTransactionLifecycle()
	{
		final Transaction copeTest = model.getCurrentTransaction();
		assertContains( copeTest, model.getOpenTransactions() );
		assertUnmodifiable( model.getOpenTransactions() );
		assertEquals( false, copeTest.isClosed() );
		
		model.leaveTransaction();
		assertContains( copeTest, model.getOpenTransactions() );
		assertEquals( false, copeTest.isClosed() );
		
		final Transaction tx1 = model.startTransaction( "tx1" );
		assertContains( copeTest, tx1, model.getOpenTransactions() );
		assertEquals( false, copeTest.isClosed() );
		assertEquals( false, tx1.isClosed() );
		
		model.leaveTransaction();
		assertContains( copeTest, tx1, model.getOpenTransactions() );
		assertEquals( false, copeTest.isClosed() );
		assertEquals( false, tx1.isClosed() );
		
		model.joinTransaction( copeTest );
		assertContains( copeTest, tx1, model.getOpenTransactions() );
		assertEquals( false, copeTest.isClosed() );
		assertEquals( false, tx1.isClosed() );
		
		model.commit();
		assertContains( tx1, model.getOpenTransactions() );
		assertEquals( true, copeTest.isClosed() );
		assertEquals( false, tx1.isClosed() );
		
		model.joinTransaction( tx1 );
		assertContains( tx1, model.getOpenTransactions() );
		assertEquals( true, copeTest.isClosed() );
		assertEquals( false, tx1.isClosed() );
		
		model.rollback();
		assertContains( model.getOpenTransactions() );
		assertEquals( true, copeTest.isClosed() );
		assertEquals( true, tx1.isClosed() );
		
		model.startTransaction( "forTearDown" );
	}
}
