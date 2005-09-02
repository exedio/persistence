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
		final Transaction txChangeItem = model.startTransaction( "change item" );
		if ( ! model.supportsReadCommitted() )
		{
			// forced preload as work-around of hsql shortcoming:
			assertEquals( "collision", collisionItem.getName() );
		}
		model.leaveTransaction();
		final Transaction txChangeCollisionItem = model.startTransaction( "change collision item" );
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
		model.startTransaction("just for tearDown");
	}
	
	public void testRollback() throws MandatoryViolationException
	{
		model.commit();
		final Transaction txRollback = model.startTransaction("rollback");
		item.setName( "somenewname" );
		model.leaveTransaction();
		model.clearCache();
		final Transaction txLoadCache = model.startTransaction("loadcache");
		if ( model.supportsReadCommitted() )
		{
			assertEquals( "blub", item.getName() );
		}
		else
		{
			assertEquals( "somenewname", item.getName() );
		}
		model.commit();
		model.joinTransaction( txRollback );
		model.rollback();
		model.startTransaction( "check" );
		assertEquals( "blub", item.getName() );
	}
	
}
