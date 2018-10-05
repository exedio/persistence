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

import static com.exedio.cope.CacheIsolationItem.TYPE;
import static com.exedio.cope.CacheIsolationItem.name;
import static com.exedio.cope.tojunit.Assert.list;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ItemCacheStampPurgeTest extends TestWithEnvironment
{
	public ItemCacheStampPurgeTest()
	{
		super(CacheIsolationTest.MODEL);
	}

	boolean ignore;
	@SuppressFBWarnings("URF_UNREAD_FIELD")
	CacheIsolationItem item1, item2, itemX;

	@BeforeEach final void setUp()
	{
		final ConnectProperties props = model.getConnectProperties();
		ignore =
			props.getItemCacheLimit()==0 ||
			!props.itemCacheStamps;
		if(ignore)
			return;

		item1 = new CacheIsolationItem("item1");
		item2 = new CacheIsolationItem("item2");
		itemX = new CacheIsolationItem("itemX");
		model.commit();
		clearStamps(model.connect().itemCache);
		model.startTransaction("ItemCacheStampPurgeTest");
		initCache();
	}

	@SuppressWarnings("deprecation")
	private static void clearStamps(final ItemCache cache)
	{
		cache.clearStamps();
	}

	@Test void testSequential()
	{
		assumeCacheEnabled();

		assertCache(0, 0, 0, 0, 0, 0, 0, 0);

		item1.setName("item1a");
		assertCache(1, 0, 1, 0, 0, 0, 0, 0);

		item2.setName("item2a");
		assertCache(2, 0, 2, 0, 0, 0, 0, 0);

		model.commit();
		assertCache(0, 0, 2, 2, 2, 0, 0, 2);

		model.startTransaction("ItemCacheStampPurgeTest");
		assertEquals(list(), TYPE.search(name.equal("testSequential"))); // make transaction acquire a connection
		assertCache(0, 0, 2, 2, 2, 0, 0, 2);

		model.commit();
		assertCache(0, 0, 2, 2, 2, 0, 0, 2);

		model.startTransaction("ItemCacheStampPurgeTest2");
		assertCache(0, 0, 2, 2, 2, 0, 0, 2);
	}

	@Test void testOverlappingOnce()
	{
		assumeCacheEnabled();

		assertCache(0, 0, 0, 0, 0, 0, 0, 0);

		item1.setName("item1a");
		assertCache(1, 0, 1, 0, 0, 0, 0, 0);

		item2.setName("item2a");
		assertCache(2, 0, 2, 0, 0, 0, 0, 0);

		final Transaction modifyTx = model.leaveTransaction();
		model.startTransaction("ItemCacheStampPurgeTest overlap");
		assertEquals(list(), TYPE.search(name.equal("testOverlappingOnce"))); // make transaction acquire a connection
		final Transaction overlapTx = model.leaveTransaction();
		model.joinTransaction(modifyTx);

		model.commit(); // modifyTx
		assertCache(0, 0, 2, 2, 2, 2, 0, 0);

		model.joinTransaction(overlapTx);
		assertCache(0, 0, 2, 2, 2, 2, 0, 0);

		model.commit(); // overlapTx
		assertCache(0, 0, 2, 2, 2, 0, 0, 2);

		model.startTransaction("ItemCacheStampPurgeTest2");
		assertCache(0, 0, 2, 2, 2, 0, 0, 2);
	}

	@Test void testOverlappingOnceWithoutConnection()
	{
		assumeCacheEnabled();

		assertCache(0, 0, 0, 0, 0, 0, 0, 0);

		item1.setName("item1a");
		assertCache(1, 0, 1, 0, 0, 0, 0, 0);

		item2.setName("item2a");
		assertCache(2, 0, 2, 0, 0, 0, 0, 0);

		final Transaction modifyTx = model.leaveTransaction();
		model.startTransaction("ItemCacheStampPurgeTest overlap");
		final Transaction overlapTx = model.leaveTransaction();
		model.joinTransaction(modifyTx);

		model.commit(); // modifyTx
		assertCache(0, 0, 2, 2, 2, 0, 0, 2);

		model.joinTransaction(overlapTx);
		assertCache(0, 0, 2, 2, 2, 0, 0, 2);

		model.commit(); // overlapTx
		assertCache(0, 0, 2, 2, 2, 0, 0, 2);

		model.startTransaction("ItemCacheStampPurgeTest2");
		assertCache(0, 0, 2, 2, 2, 0, 0, 2);
	}

	@Test void testOverlappingTwice()
	{
		assumeCacheEnabled();

		assertCache(0, 0, 0, 0, 0, 0, 0, 0);

		item1.setName("item1a");
		assertCache(1, 0, 1, 0, 0, 0, 0, 0);

		item2.setName("item2a");
		assertCache(2, 0, 2, 0, 0, 0, 0, 0);

		final Transaction modifyTx = model.leaveTransaction();
		model.startTransaction("ItemCacheStampPurgeTest overlap1");
		assertEquals(list(), TYPE.search(name.equal("testOverlappingTwice1"))); // make transaction acquire a connection
		final Transaction overlapTx1 = model.leaveTransaction();
		model.joinTransaction(modifyTx);

		model.commit(); // modifyTx
		assertCache(0, 0, 2, 2, 2, 2, 0, 0);

		model.startTransaction("ItemCacheStampPurgeTest overlap2");
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

		model.startTransaction("ItemCacheStampPurgeTest2");
		assertCache(0, 0, 2, 2, 2, 0, 0, 2);
	}


	private ItemCacheInfo last = null;

	private void initCache()
	{
		final ItemCacheInfo[] icis = model.getItemCacheStatistics().getDetails();
		assertEquals(1, icis.length);
		last = icis[0];
		assertSame(TYPE, last.getType());
	}

	private void assertCache(
			final int level,
			final long hits,
			final long misses,
			final long invalidationsOrdered,
			final long invalidationsDone,
			final int  stampsSize,
			final long stampsHits,
			final long stampsPurged)
	{
		final ItemCacheInfo[] icis = model.getItemCacheStatistics().getDetails();
		assertEquals(1, icis.length);
		final ItemCacheInfo curr = icis[0];
		assertAll(
				() -> assertSame(TYPE, curr.getType()),
				() -> assertEquals(level,                curr.getLevel(),                                                 "level(1)"),
				() -> assertEquals(hits,                 curr.getHits()                 - last.getHits(),                 "hits(2)"),
				() -> assertEquals(misses,               curr.getMisses()               - last.getMisses(),               "misses(3)"),
				() -> assertEquals(invalidationsOrdered, curr.getInvalidationsOrdered() - last.getInvalidationsOrdered(), "invalidationsOrdered(4)"),
				() -> assertEquals(invalidationsDone,    curr.getInvalidationsDone()    - last.getInvalidationsDone(),    "invalidationsDone(5)"),
				() -> assertEquals(stampsSize,           curr.getStampsSize(),                                            "stampsSize(6)"),
				() -> assertEquals(stampsHits,           curr.getStampsHits()           - last.getStampsHits(),           "stampsHits(7)"),
				() -> assertEquals(stampsPurged,         curr.getStampsPurged()         - last.getStampsPurged(),         "stampsPurged(8)")
		);
	}

	private void assumeCacheEnabled()
	{
		assumeTrue(!ignore, "cache enabled");
	}
}
