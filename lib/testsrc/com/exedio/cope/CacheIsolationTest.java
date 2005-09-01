package com.exedio.cope;

public class CacheIsolationTest extends AbstractLibTest
{
	CacheIsolationItem item, collisionItem;
	
	public CacheIsolationTest()
	{
		super( Main.cacheIsolationModel );
	}
	
	protected void setUp() throws Exception
	{
		super.setUp();
		item = new CacheIsolationItem( "blub" );
		deleteOnTearDown( item );
		collisionItem = new CacheIsolationItem( "collision" );
		deleteOnTearDown( collisionItem );
		collisionItem.setUniqueString( "unique" );
	}
	
	public void test() throws MandatoryViolationException
	{
		model.commit();
		Transaction txChangeItem = null, txChangeCollisionItem = null;
		try
		{
			txChangeItem = model.startTransaction( "change item" );
			if ( ! model.supportsReadCommitted() )
			{
				// forced preload as work-around of hsql shortcoming:
				assertEquals( "collision", collisionItem.getName() );
			}
			model.leaveTransaction();
			txChangeCollisionItem = model.startTransaction( "change collision item" );
			collisionItem.setName( "othercollision" );
			assertEquals( "othercollision", collisionItem.getName() );
			assertEquals( txChangeCollisionItem, model.leaveTransaction() );
			model.joinTransaction( txChangeItem );
			assertEquals( "collision", collisionItem.getName() );
			try
			{
				item.setUniqueString( collisionItem.getUniqueString() );
				fail();
			}
			catch ( UniqueViolationException e )
			{
				// fine
			}
			assertEquals( null, txChangeItem.getEntityIfActive(item.type, item.pk) );
			model.getDatabase().expectLoad( txChangeItem, item );
			assertEquals( "collision", collisionItem.getName() );
			assertEquals( "blub", item.getName() );
			model.getDatabase().verifyExpectations();
			if ( model.supportsReadCommitted() )
			{
				assertEquals( null, item.getUniqueString() );
			}
			model.commit();
			model.joinTransaction( txChangeCollisionItem );
			model.commit();
		}
		finally
		{
			if ( model.hasCurrentTransaction() )
			{
				model.leaveTransaction();
			}
			if ( txChangeItem!=null && !txChangeItem.isClosed() ) txChangeItem.rollback();
			if ( txChangeCollisionItem!=null && !txChangeCollisionItem.isClosed() ) txChangeCollisionItem.rollback();
		}
		model.startTransaction("just for tearDown");
	}
	
}
