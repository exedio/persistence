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

import static com.exedio.cope.RuntimeTester.getItemCacheStatistics;
import static com.exedio.cope.tojunit.Assert.assertContains;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CacheIsolationTest extends TestWithEnvironment
{
	public static final Model MODEL = new Model(CacheIsolationItem.TYPE);

	static
	{
		MODEL.enableSerialization(CacheIsolationTest.class, "MODEL");
	}

	CacheIsolationItem item, collisionItem;

	public CacheIsolationTest()
	{
		super(MODEL);
	}

	boolean unq;
	ItemCacheInfo last;

	@BeforeEach final void setUp()
	{
		item = new CacheIsolationItem("blub");
		collisionItem = new CacheIsolationItem("collision");
		collisionItem.setUniqueString( "unique" );

		unq = model.connect().supportsUniqueViolation;
		if(model.getConnectProperties().getItemCacheLimit()>0)
		{
			final ItemCacheInfo[] ci = getItemCacheStatistics(model).getDetails();
			last = ci[0];
			assertSame(CacheIsolationItem.TYPE, last.getType());
		}
	}

	@Test void test() throws MandatoryViolationException
	{
		assertInvalidations(0, 0);
		model.commit();
		assertInvalidations(2, 0);
		final Transaction txChangeItem = model.startTransaction( "change item" );
		assertEquals(0, txChangeItem.getInvalidationSize());
		model.leaveTransaction();
		assertEquals(0, txChangeItem.getInvalidationSize());
		final Transaction txChangeCollisionItem = model.startTransaction( "change collision item" );
		assertEquals(0, txChangeCollisionItem.getInvalidationSize());
		collisionItem.setName( "othercollision" );
		assertEquals(1, txChangeCollisionItem.getInvalidationSize());
		assertEquals( "othercollision", collisionItem.getName() );
		assertEquals(1, txChangeCollisionItem.getInvalidationSize());
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
		assertEquals(unq?1:0, txChangeItem.getInvalidationSize());
		assertEquals( null, txChangeItem.getEntityIfActive(item.type, item.pk) );
		final ExpectingDatabaseListener listener = new ExpectingDatabaseListener();
		assertNull(model.setTestDatabaseListener(listener));
		listener.expectLoad( txChangeItem, item );
		assertEquals( "collision", collisionItem.getName() );
		assertEquals( "blub", item.getName() );
		listener.verifyExpectations();
		assertEquals( null, item.getUniqueString() );
		assertInvalidations(0, 0);
		model.commit();
		assertInvalidations(unq?1:0, 0);
		model.joinTransaction( txChangeCollisionItem );
		assertInvalidations(0, 0);
		model.commit();
		assertInvalidations(1, 1);
		model.startTransaction("just for tearDown");
		assertSame(listener, model.setTestDatabaseListener(null));
	}

	@Test void testRollback() throws MandatoryViolationException
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
		assertInvalidations(0, 0);
		model.commit();
		assertInvalidations(0, 0);
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

	@Test void testSearch() throws MandatoryViolationException
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
		// TODO: model.getDatabase().expectNoCall();
		listener.expectSearch( txSearch, CacheIsolationItem.TYPE );
		assertContains( item, CacheIsolationItem.TYPE.search(CacheIsolationItem.name.equal("blub")) );
		listener.verifyExpectations();
		listener.expectSearch( txSearch, CacheIsolationItem.TYPE );
		assertContains( CacheIsolationItem.TYPE.search(CacheIsolationItem.name.equal("notblub")) );
		listener.verifyExpectations();
		assertInvalidations(0, 0);
		model.commit();
		assertInvalidations(0, 0);
		model.joinTransaction( txChange );
		assertSame(listener, model.setTestDatabaseListener(null));
	}

	private void assertInvalidations(final int ordered, final int done)
	{
		final ItemCacheStatistics statistics = getItemCacheStatistics(model);
		final ItemCacheInfo[] ci = statistics.getDetails();
		if(model.getConnectProperties().getItemCacheLimit()>0)
		{
			assertEquals(1, ci.length);
			assertAll(
					() -> assertSame(CacheIsolationItem.TYPE, ci[0].getType()),
					() -> assertEquals(ordered, ci[0].getInvalidationsOrdered() - last.getInvalidationsOrdered(), "ordered(1)"),
					() -> assertEquals(done,    ci[0].getInvalidationsDone()    - last.getInvalidationsDone(),    "done(2)")
			);
			last = ci[0];
		}
		else
		{
			assertEquals(0, statistics.getLimit());
			assertEquals(0, ci.length, Stream.of(ci).map(ItemCacheInfo::getType).collect(Collectors.toList()).toString());
		}
	}
}
