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
		ExpectingDatabase expectingDB = new ExpectingDatabase( model.getDatabase() );
		model.replaceDatabase( expectingDB );
		expectingDB.expectLoad( txChangeItem, item );
		assertEquals( "collision", collisionItem.getName() );
		assertEquals( "blub", item.getName() );
		expectingDB.verifyExpectations();
		if ( model.supportsReadCommitted() )
		{
			assertEquals( null, item.getUniqueString() );
		}
		model.commit();
		model.joinTransaction( txChangeCollisionItem );
		model.commit();
		model.startTransaction("just for tearDown");
		model.replaceDatabase( expectingDB.getNestedDatabase() );
	}
	
	public void testRollback() throws MandatoryViolationException
	{
		model.commit();
		final Transaction txRollback = model.startTransaction("rollback");
		item.setName( "somenewname" );
		model.leaveTransaction();
		model.clearCache();
		final Transaction txLoadCache = model.startTransaction("loadcache");
		ExpectingDatabase expectingDB = new ExpectingDatabase( model.getDatabase() );
		model.replaceDatabase( expectingDB );
		expectingDB.expectLoad( txLoadCache, item );
		if ( model.supportsReadCommitted() )
		{
			assertEquals( "blub", item.getName() );
		}
		else
		{
			assertEquals( "somenewname", item.getName() );
		}
		expectingDB.verifyExpectations();
		model.commit();
		model.joinTransaction( txRollback );
		model.rollback();
		Transaction txCheck = model.startTransaction( "check" );
		if ( model.supportsReadCommitted() )
		{
			expectingDB.expectNoCall();
		}
		else
		{
			expectingDB.expectLoad( txCheck, item );
		}
		assertEquals( "blub", item.getName() );
		expectingDB.verifyExpectations();
		model.replaceDatabase( expectingDB.getNestedDatabase() );
	}

	public void testSearch() throws MandatoryViolationException
	{
		if ( ! model.supportsReadCommitted() ) return;
		
		assertContains( item, CacheIsolationItem.TYPE.search(CacheIsolationItem.name.equal("blub")) );
		model.commit();
		Transaction txChange = model.startTransaction("change");
		item.setName("notblub");
		ExpectingDatabase expectingDB = new ExpectingDatabase( model.getDatabase() );
		model.replaceDatabase( expectingDB );		
		expectingDB.expectSearch( txChange, CacheIsolationItem.TYPE );
		assertContains( CacheIsolationItem.TYPE.search(CacheIsolationItem.name.equal("blub")) );
		expectingDB.verifyExpectations();
		expectingDB.expectSearch( txChange, CacheIsolationItem.TYPE );
		assertContains( item, CacheIsolationItem.TYPE.search(CacheIsolationItem.name.equal("notblub")) );
		expectingDB.verifyExpectations();
		model.leaveTransaction();
		Transaction txSearch = model.startTransaction("search");
		// TODO:
		// model.getDatabase().expectNoCall();
		expectingDB.expectSearch( txSearch, CacheIsolationItem.TYPE );
		assertContains( item, CacheIsolationItem.TYPE.search(CacheIsolationItem.name.equal("blub")) );
		expectingDB.verifyExpectations();
		expectingDB.expectSearch( txSearch, CacheIsolationItem.TYPE );
		assertContains( CacheIsolationItem.TYPE.search(CacheIsolationItem.name.equal("notblub")) );
		expectingDB.verifyExpectations();
		model.commit();
		model.joinTransaction( txChange );
		model.replaceDatabase( expectingDB.getNestedDatabase() );
	}
}
