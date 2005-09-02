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
}
