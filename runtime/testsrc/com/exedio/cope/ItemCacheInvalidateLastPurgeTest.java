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
		quit = !model.getConnectProperties().itemCacheInvalidateLast.booleanValue();
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
		assertCache(0, 0, 0, 0, 0, 0, 0);

		item1.setName("item1a");
		assertCache(1, 0, 1, 0, 0, 0, 0);

		item2.setName("item2a");
		assertCache(2, 0, 2, 0, 0, 0, 0);

		model.commit();
		assertCache(0, 0, 2, 2, 2, 2, 0); // TODO invalidateLastSize should be 0

		model.startTransaction("ItemCacheInvalidateLastPurgeTest");
		assertCache(0, 0, 2, 2, 2, 2, 0); // TODO invalidateLastSize should be 0

		model.commit();
		assertCache(0, 0, 2, 2, 2, 2, 0); // TODO invalidateLastSize should be 0

		model.startTransaction("ItemCacheInvalidateLastPurgeTest2");
		assertCache(0, 0, 2, 2, 2, 2, 0); // TODO invalidateLastSize should be 0
	}

	public void testOverlappingOnce()
	{
		if(quit)
			return;
		assertCache(0, 0, 0, 0, 0, 0, 0);

		item1.setName("item1a");
		assertCache(1, 0, 1, 0, 0, 0, 0);

		item2.setName("item2a");
		assertCache(2, 0, 2, 0, 0, 0, 0);

		final Transaction modifyTx = model.leaveTransaction();
		model.startTransaction("ItemCacheInvalidateLastPurgeTest overlap");
		final Transaction overlapTx = model.leaveTransaction();
		model.joinTransaction(modifyTx);

		model.commit(); // modifyTx
		assertCache(0, 0, 2, 2, 2, 2, 0);

		model.joinTransaction(overlapTx);
		assertCache(0, 0, 2, 2, 2, 2, 0);

		model.commit(); // overlapTx
		assertCache(0, 0, 2, 2, 2, 2, 0); // TODO invalidateLastSize should be 0

		model.startTransaction("ItemCacheInvalidateLastPurgeTest2");
		assertCache(0, 0, 2, 2, 2, 2, 0); // TODO invalidateLastSize should be 0
	}


	private long initHits, initMisses, initInvalidationsOrdered, initInvalidationsDone, initInvalidationsLastHits;

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
	}

	private void assertCache(
			final int level,
			final long hits,
			final long misses,
			final long invalidationsOrdered,
			final long invalidationsDone,
			final int  invalidateLastSize,
			final long invalidateLastHits)
	{
		final ItemCacheInfo[] icis = model.getItemCacheInfo();
		assertEquals(1, icis.length);
		final ItemCacheInfo ici = icis[0];
		assertSame(TYPE, ici.getType());
		assertSame("level"               , level               , ici.getLevel());
		assertSame("hits"                , hits                , ici.getHits()                -initHits                );
		assertSame("misses"              , misses              , ici.getMisses()              -initMisses              );
		assertSame("invalidationsOrdered", invalidationsOrdered, ici.getInvalidationsOrdered()-initInvalidationsOrdered);
		assertSame("invalidationsDone"   , invalidationsDone   , ici.getInvalidationsDone()   -initInvalidationsDone   );
		assertSame("invalidateLastSize"  , invalidateLastSize  , ici.getInvalidateLastSize());
		assertSame("invalidateLastHits"  , invalidateLastHits  , ici.getInvalidateLastHits()  -initInvalidationsLastHits);
	}
}
