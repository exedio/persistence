/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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

import static com.exedio.cope.CacheIsolationItem.TYPE;
import static com.exedio.cope.CacheIsolationItem.name;

public class ItemCacheInvalidateLastPurgeTest extends AbstractRuntimeTest
{
	public ItemCacheInvalidateLastPurgeTest()
	{
		super(CacheIsolationTest.MODEL);
	}

	boolean quit;
	CacheIsolationItem item1, item2, itemX;

	@Override
	public void setUp() throws Exception
	{
		super.setUp();

		final ConnectProperties props = model.getConnectProperties();
		quit =
			props.getItemCacheLimit()==0 ||
			!props.itemCacheInvalidateLast.booleanValue();
		if(quit)
			return;

		item1 = deleteOnTearDown(new CacheIsolationItem("item1"));
		item2 = deleteOnTearDown(new CacheIsolationItem("item2"));
		itemX = deleteOnTearDown(new CacheIsolationItem("itemX"));
		model.commit();
		clearInvalidateLast(model.connect().itemCache);
		model.startTransaction("ItemCacheInvalidateLastPurgeTest");
		initCache();
	}

	@SuppressWarnings("deprecation")
	private static void clearInvalidateLast(final ItemCache cache)
	{
		cache.clearInvalidateLast();
	}

	public void testSequential()
	{
		if(quit)
			return;
		assertCache(0, 0, 0, 0, 0, 0, 0, 0);

		item1.setName("item1a");
		assertCache(1, 0, 1, 0, 0, 0, 0, 0);

		item2.setName("item2a");
		assertCache(2, 0, 2, 0, 0, 0, 0, 0);

		model.commit();
		assertCache(0, 0, 2, 2, 2, 0, 0, 2);

		model.startTransaction("ItemCacheInvalidateLastPurgeTest");
		assertEquals(list(), TYPE.search(name.equal("testSequential"))); // make transaction acquire a connection
		assertCache(0, 0, 2, 2, 2, 0, 0, 2);

		model.commit();
		assertCache(0, 0, 2, 2, 2, 0, 0, 2);

		model.startTransaction("ItemCacheInvalidateLastPurgeTest2");
		assertCache(0, 0, 2, 2, 2, 0, 0, 2);
	}

	public void testOverlappingOnce()
	{
		if(quit)
			return;
		assertCache(0, 0, 0, 0, 0, 0, 0, 0);

		item1.setName("item1a");
		assertCache(1, 0, 1, 0, 0, 0, 0, 0);

		item2.setName("item2a");
		assertCache(2, 0, 2, 0, 0, 0, 0, 0);

		final Transaction modifyTx = model.leaveTransaction();
		model.startTransaction("ItemCacheInvalidateLastPurgeTest overlap");
		assertEquals(list(), TYPE.search(name.equal("testOverlappingOnce"))); // make transaction acquire a connection
		final Transaction overlapTx = model.leaveTransaction();
		model.joinTransaction(modifyTx);

		model.commit(); // modifyTx
		assertCache(0, 0, 2, 2, 2, 2, 0, 0);

		model.joinTransaction(overlapTx);
		assertCache(0, 0, 2, 2, 2, 2, 0, 0);

		model.commit(); // overlapTx
		assertCache(0, 0, 2, 2, 2, 0, 0, 2);

		model.startTransaction("ItemCacheInvalidateLastPurgeTest2");
		assertCache(0, 0, 2, 2, 2, 0, 0, 2);
	}

	public void testOverlappingOnceWithoutConnection()
	{
		if(quit)
			return;
		assertCache(0, 0, 0, 0, 0, 0, 0, 0);

		item1.setName("item1a");
		assertCache(1, 0, 1, 0, 0, 0, 0, 0);

		item2.setName("item2a");
		assertCache(2, 0, 2, 0, 0, 0, 0, 0);

		final Transaction modifyTx = model.leaveTransaction();
		model.startTransaction("ItemCacheInvalidateLastPurgeTest overlap");
		final Transaction overlapTx = model.leaveTransaction();
		model.joinTransaction(modifyTx);

		model.commit(); // modifyTx
		assertCache(0, 0, 2, 2, 2, 0, 0, 2);

		model.joinTransaction(overlapTx);
		assertCache(0, 0, 2, 2, 2, 0, 0, 2);

		model.commit(); // overlapTx
		assertCache(0, 0, 2, 2, 2, 0, 0, 2);

		model.startTransaction("ItemCacheInvalidateLastPurgeTest2");
		assertCache(0, 0, 2, 2, 2, 0, 0, 2);
	}

	public void testOverlappingTwice()
	{
		if(quit)
			return;
		assertCache(0, 0, 0, 0, 0, 0, 0, 0);

		item1.setName("item1a");
		assertCache(1, 0, 1, 0, 0, 0, 0, 0);

		item2.setName("item2a");
		assertCache(2, 0, 2, 0, 0, 0, 0, 0);

		final Transaction modifyTx = model.leaveTransaction();
		model.startTransaction("ItemCacheInvalidateLastPurgeTest overlap1");
		assertEquals(list(), TYPE.search(name.equal("testOverlappingTwice1"))); // make transaction acquire a connection
		final Transaction overlapTx1 = model.leaveTransaction();
		model.joinTransaction(modifyTx);

		model.commit(); // modifyTx
		assertCache(0, 0, 2, 2, 2, 2, 0, 0);

		model.startTransaction("ItemCacheInvalidateLastPurgeTest overlap2");
		assertEquals(list(), TYPE.search(name.equal("testOverlappingTwice2"))); // make transaction acquire a connection
		final Transaction overlapTx2 = model.leaveTransaction();

		model.joinTransaction(overlapTx1);
		assertCache(0, 0, 2, 2, 2, 2, 0, 0);

		model.commit(); // overlapTx1
		assertCache(0, 0, 2, 2, 2, 0, 0, 2);

		model.joinTransaction(overlapTx2);
		assertCache(0, 0, 2, 2, 2, 0, 0, 2);

		model.commit(); // overlapTx2
		assertCache(0, 0, 2, 2, 2, 0, 0, 2);

		model.startTransaction("ItemCacheInvalidateLastPurgeTest2");
		assertCache(0, 0, 2, 2, 2, 0, 0, 2);
	}


	private long initHits, initMisses, initInvalidationsOrdered, initInvalidationsDone, initInvalidationsLastHits, initInvalidationsLastPurged;

	private void initCache()
	{
		final ItemCacheInfo[] icis = model.getItemCacheInfo();
		assertEquals(1, icis.length);
		final ItemCacheInfo ici = icis[0];
		assertSame(TYPE, ici.getType());
		initHits = ici.getHits();
		initMisses = ici.getMisses();
		initInvalidationsOrdered = ici.getInvalidationsOrdered();
		initInvalidationsDone = ici.getInvalidationsDone();
		initInvalidationsLastHits = ici.getInvalidateLastHits();
		initInvalidationsLastPurged = ici.getInvalidateLastPurged();
	}

	private void assertCache(
			final int level,
			final long hits,
			final long misses,
			final long invalidationsOrdered,
			final long invalidationsDone,
			final int  invalidateLastSize,
			final long invalidateLastHits,
			final long invalidateLastPurged)
	{
		final ItemCacheInfo[] icis = model.getItemCacheInfo();
		assertEquals(1, icis.length);
		final ItemCacheInfo ici = icis[0];
		assertSame(TYPE, ici.getType());
		assertEquals("level"               , level               , ici.getLevel());
		assertEquals("hits"                , hits                , ici.getHits()                -initHits                );
		assertEquals("misses"              , misses              , ici.getMisses()              -initMisses              );
		assertEquals("invalidationsOrdered", invalidationsOrdered, ici.getInvalidationsOrdered()-initInvalidationsOrdered);
		assertEquals("invalidationsDone"   , invalidationsDone   , ici.getInvalidationsDone()   -initInvalidationsDone   );
		assertEquals("invalidateLastSize"  , invalidateLastSize  , ici.getInvalidateLastSize());
		assertEquals("invalidateLastHits"  , invalidateLastHits  , ici.getInvalidateLastHits()  -initInvalidationsLastHits);
		assertEquals("invalidateLastPurged", invalidateLastPurged, ici.getInvalidateLastPurged()-initInvalidationsLastPurged);
	}
}
