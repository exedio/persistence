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

public class CacheIsolationTest extends AbstractRuntimeModelTest
{
	public static final Model MODEL = new Model(CacheIsolationItem.TYPE);

	CacheIsolationItem item, collisionItem;

	public CacheIsolationTest()
	{
		super(MODEL);
	}

	boolean unq;
	long setupInvalidationsOrdered;
	long setupInvalidationsDone;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		item = deleteOnTearDown(new CacheIsolationItem("blub"));
		collisionItem = deleteOnTearDown(new CacheIsolationItem("collision"));
		collisionItem.setUniqueString( "unique" );

		unq = model.connect().executor.supportsUniqueViolation;
		if(model.getConnectProperties().getItemCacheLimit()>0)
		{
			final ItemCacheInfo[] ci = model.getItemCacheInfo();
			setupInvalidationsOrdered = ci[0].getInvalidationsOrdered();
			setupInvalidationsDone    = ci[0].getInvalidationsDone();
		}
	}

	public void test() throws MandatoryViolationException
	{
		if(hsqldb) return;
		assertInvalidations(0, 0);
		model.commit();
		assertInvalidations(2, 0);
		final Transaction txChangeItem = model.startTransaction( "change item" );
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
		catch ( final UniqueViolationException e )
		{
			// fine
		}
		assertEquals( null, txChangeItem.getEntityIfActive(item.type, item.pk) );
		final ExpectingDatabaseListener listener = new ExpectingDatabaseListener();
		assertNull(model.setTestDatabaseListener(listener));
		listener.expectLoad( txChangeItem, item );
		assertEquals( "collision", collisionItem.getName() );
		assertEquals( "blub", item.getName() );
		listener.verifyExpectations();
		assertEquals( null, item.getUniqueString() );
		assertInvalidations(2, 0);
		model.commit();
		assertInvalidations(unq?3:2, 0);
		model.joinTransaction( txChangeCollisionItem );
		assertInvalidations(unq?3:2, 0);
		model.commit();
		assertInvalidations(unq?4:3, 1);
		model.startTransaction("just for tearDown");
		assertSame(listener, model.setTestDatabaseListener(null));
	}

	public void testRollback() throws MandatoryViolationException
	{
		if(hsqldb) return;
		assertInvalidations(0, 0);
		model.commit();
		assertInvalidations(2, 0);
		final Transaction txRollback = model.startTransaction("rollback");
		item.setName( "somenewname" );
		model.leaveTransaction();
		model.clearCache();
		final Transaction txLoadCache = model.startTransaction("loadcache");
		final ExpectingDatabaseListener listener = new ExpectingDatabaseListener();
		assertNull(model.setTestDatabaseListener(listener));
		listener.expectLoad( txLoadCache, item );
		assertEquals( "blub", item.getName() );
		listener.verifyExpectations();
		assertInvalidations(2, 0);
		model.commit();
		assertInvalidations(2, 0);
		model.joinTransaction( txRollback );
		model.rollback();
		model.startTransaction( "check" );
		listener.expectNoCall();
		assertEquals( "blub", item.getName() );
		listener.verifyExpectations();
		assertSame(listener, model.setTestDatabaseListener(null));
	}

	public void testSearch() throws MandatoryViolationException
	{
		if(hsqldb) return;

		assertContains( item, CacheIsolationItem.TYPE.search(CacheIsolationItem.name.equal("blub")) );
		assertInvalidations(0, 0);
		model.commit();
		assertInvalidations(2, 0);
		final Transaction txChange = model.startTransaction("change");
		item.setName("notblub");
		final ExpectingDatabaseListener listener = new ExpectingDatabaseListener();
		assertNull(model.setTestDatabaseListener(listener));
		listener.expectSearch( txChange, CacheIsolationItem.TYPE );
		assertContains( CacheIsolationItem.TYPE.search(CacheIsolationItem.name.equal("blub")) );
		listener.verifyExpectations();
		listener.expectSearch( txChange, CacheIsolationItem.TYPE );
		assertContains( item, CacheIsolationItem.TYPE.search(CacheIsolationItem.name.equal("notblub")) );
		listener.verifyExpectations();
		model.leaveTransaction();
		final Transaction txSearch = model.startTransaction("search");
		// TODO:
		// model.getDatabase().expectNoCall();
		listener.expectSearch( txSearch, CacheIsolationItem.TYPE );
		assertContains( item, CacheIsolationItem.TYPE.search(CacheIsolationItem.name.equal("blub")) );
		listener.verifyExpectations();
		listener.expectSearch( txSearch, CacheIsolationItem.TYPE );
		assertContains( CacheIsolationItem.TYPE.search(CacheIsolationItem.name.equal("notblub")) );
		listener.verifyExpectations();
		assertInvalidations(2, 0);
		model.commit();
		assertInvalidations(2, 0);
		model.joinTransaction( txChange );
		assertSame(listener, model.setTestDatabaseListener(null));
	}

	private final void assertInvalidations(final int ordered, final int done)
	{
		final ItemCacheInfo[] ci = model.getItemCacheInfo();
		if(model.getConnectProperties().getItemCacheLimit()>0)
		{
			assertEquals(1, ci.length);
			assertSame(CacheIsolationItem.TYPE, ci[0].getType());
			assertEquals("ordered", ordered, ci[0].getInvalidationsOrdered() - setupInvalidationsOrdered);
			assertEquals("done",    done,    ci[0].getInvalidationsDone() - setupInvalidationsDone);
		}
		else
		{
			assertEquals(0, ci.length);
		}
	}
}
