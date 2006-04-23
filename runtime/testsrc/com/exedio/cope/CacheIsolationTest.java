/*
 * Copyright (C) 2004-2006  exedio GmbH (www.exedio.com)
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
		final ExpectingDatabase expectingDB = new ExpectingDatabase(); // TODO rename
		assertNull(model.setDatabaseListener(expectingDB));
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
		assertSame(expectingDB, model.setDatabaseListener(null));
	}
	
	public void testRollback() throws MandatoryViolationException
	{
		model.commit();
		final Transaction txRollback = model.startTransaction("rollback");
		item.setName( "somenewname" );
		model.leaveTransaction();
		model.clearCache();
		final Transaction txLoadCache = model.startTransaction("loadcache");
		final ExpectingDatabase expectingDB = new ExpectingDatabase();
		assertNull(model.setDatabaseListener(expectingDB));
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
		assertSame(expectingDB, model.setDatabaseListener(null));
	}

	public void testSearch() throws MandatoryViolationException
	{
		if ( ! model.supportsReadCommitted() ) return;
		
		assertContains( item, CacheIsolationItem.TYPE.search(CacheIsolationItem.name.equal("blub")) );
		model.commit();
		Transaction txChange = model.startTransaction("change");
		item.setName("notblub");
		final ExpectingDatabase expectingDB = new ExpectingDatabase();
		assertNull(model.setDatabaseListener(expectingDB));		
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
		assertSame(expectingDB, model.setDatabaseListener(null));
	}
}
