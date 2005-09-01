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
		Transaction txChangeItem = model.startTransaction( "change item" );
		assertEquals( "collision", collisionItem.getName() );
		model.leaveTransaction();
		Transaction txChangeCollisionItem = model.startTransaction( "change collision item" );
		collisionItem.setName( "othercollision" );
		assertEquals( "othercollision", collisionItem.getName() );
		assertEquals( txChangeCollisionItem, model.leaveTransaction() );
		model.joinTransaction( txChangeItem );
		assertEquals( "collision", collisionItem.getName() );
		model.commit();
		model.joinTransaction( txChangeCollisionItem );
		model.commit();
		model.startTransaction("just for tearDown");
	}
	
}
