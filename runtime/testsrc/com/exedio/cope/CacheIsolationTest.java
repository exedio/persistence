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

import static com.exedio.cope.tojunit.Assert.assertContains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Before;
import org.junit.jupiter.api.Test;

public class CacheIsolationTest extends TestWithEnvironment
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

	@Before public final void setUp()
	{
		item = new CacheIsolationItem("blub");
		collisionItem = new CacheIsolationItem("collision");
		collisionItem.setUniqueString( "unique" );

		unq = model.connect().supportsUniqueViolation;
		if(model.getConnectProperties().getItemCacheLimit()>0)
		{
			final ItemCacheInfo[] ci = model.getItemCacheStatistics().getDetails();
			setupInvalidationsOrdered = ci[0].getInvalidationsOrdered();
			setupInvalidationsDone    = ci[0].getInvalidationsDone();
		}
	}

	@Test public void test() throws MandatoryViolationException
	{
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
			assertEquals( CacheIsolationItem.uniqueString.getImplicitUniqueConstraint(), e.getFeature() );
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

	@Test public void testRollback() throws MandatoryViolationException
	{
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
		final Transaction txCheck = model.startTransaction( "check" );
		if(model.getConnectProperties().getItemCacheLimit()>0)
			listener.expectNoCall();
		else
			listener.expectLoad( txCheck, item );
		assertEquals( "blub", item.getName() );
		listener.verifyExpectations();
		assertSame(listener, model.setTestDatabaseListener(null));
	}

	@Test public void testSearch() throws MandatoryViolationException
	{
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

	private void assertInvalidations(final int ordered, final int done)
	{
		final ItemCacheStatistics statistics = model.getItemCacheStatistics();
		final ItemCacheInfo[] ci = statistics.getDetails();
		if(model.getConnectProperties().getItemCacheLimit()>0)
		{
			assertEquals(1, ci.length);
			assertSame(CacheIsolationItem.TYPE, ci[0].getType());
			assertEquals("ordered", ordered, ci[0].getInvalidationsOrdered() - setupInvalidationsOrdered);
			assertEquals("done",    done,    ci[0].getInvalidationsDone() - setupInvalidationsDone);
		}
		else
		{
			assertEquals(0, statistics.getLimit());
			assertEquals(Stream.of(ci).map(ItemCacheInfo::getType).collect(Collectors.toList()).toString(), 0, ci.length);
		}
	}
}
