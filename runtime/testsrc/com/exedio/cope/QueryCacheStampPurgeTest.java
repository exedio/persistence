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
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class QueryCacheStampPurgeTest extends TestWithEnvironment
{
	public QueryCacheStampPurgeTest()
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
			props.getQueryCacheLimit()==0 ||
			!props.cacheStamps;
		if(ignore)
			return;

		item1 = new CacheIsolationItem("item1");
		item2 = new CacheIsolationItem("item2");
		itemX = new CacheIsolationItem("itemX");
		model.commit();
		model.startTransaction("QueryCacheStampPurgeTest");
		initCache();
	}

	@Test void testSequential()
	{
		assumeCacheEnabled();

		assertCache(0, 0, 0, 0, 0, 0, 0);

		item1.setName("item1a");
		assertCache(0, 0, 0, 0, 0, 0, 0);

		item2.setName("item2a");
		assertCache(0, 0, 0, 0, 0, 0, 0);

		model.commit();
		assertCache(0, 0, 0, 0, 0, 0, 1);

		model.startTransaction("QueryCacheStampPurgeTest");
		assertEquals("item1a", item1.getName()); // make transaction acquire a connection
		assertCache(0, 0, 0, 0, 0, 0, 0);

		model.commit();
		assertCache(0, 0, 0, 0, 0, 0, 0);

		model.startTransaction("QueryCacheStampPurgeTest2");
		assertCache(0, 0, 0, 0, 0, 0, 0);
	}

	@Test void testOverlappingOnce()
	{
		assumeCacheEnabled();

		assertCache(0, 0, 0, 0, 0, 0, 0);

		item1.setName("item1a");
		assertCache(0, 0, 0, 0, 0, 0, 0);

		item2.setName("item2a");
		assertCache(0, 0, 0, 0, 0, 0, 0);

		final Transaction modifyTx = model.leaveTransaction();
		model.startTransaction("QueryCacheStampPurgeTest overlap");
		assertEquals(list(), TYPE.search(name.equal("testOverlappingOnce"))); // make transaction acquire a connection
		final Transaction overlapTx = model.leaveTransaction();
		model.joinTransaction(modifyTx);
		assertCache(1, 0, 1, 0, 0, 0, 0);

		model.commit(); // modifyTx
		assertCache(0, 0, 0, 1, 1, 0, 0);

		model.joinTransaction(overlapTx);
		assertCache(0, 0, 0, 0, 1, 0, 0);

		model.commit(); // overlapTx
		assertCache(0, 0, 0, 0, 0, 0, 1);

		model.startTransaction("QueryCacheStampPurgeTest2");
		assertCache(0, 0, 0, 0, 0, 0, 0);
	}

	@Test void testOverlappingOnceWithoutConnection()
	{
		assumeCacheEnabled();

		assertCache(0, 0, 0, 0, 0, 0, 0);

		item1.setName("item1a");
		assertCache(0, 0, 0, 0, 0, 0, 0);

		item2.setName("item2a");
		assertCache(0, 0, 0, 0, 0, 0, 0);

		final Transaction modifyTx = model.leaveTransaction();
		model.startTransaction("QueryCacheStampPurgeTest overlap");
		final Transaction overlapTx = model.leaveTransaction();
		model.joinTransaction(modifyTx);
		assertCache(0, 0, 0, 0, 0, 0, 0);

		model.commit(); // modifyTx
		assertCache(0, 0, 0, 0, 0, 0, 1);

		model.joinTransaction(overlapTx);
		assertCache(0, 0, 0, 0, 0, 0, 0);

		model.commit(); // overlapTx
		assertCache(0, 0, 0, 0, 0, 0, 0);

		model.startTransaction("QueryCacheStampPurgeTest2");
		assertCache(0, 0, 0, 0, 0, 0, 0);
	}

	@Test void testOverlappingTwice()
	{
		assumeCacheEnabled();

		assertCache(0, 0, 0, 0, 0, 0, 0);

		item1.setName("item1a");
		assertCache(0, 0, 0, 0, 0, 0, 0);

		item2.setName("item2a");
		assertCache(0, 0, 0, 0, 0, 0, 0);

		final Transaction modifyTx = model.leaveTransaction();
		model.startTransaction("QueryCacheStampPurgeTest overlap1");
		assertEquals(list(), TYPE.search(name.equal("testOverlappingTwice1"))); // make transaction acquire a connection
		final Transaction overlapTx1 = model.leaveTransaction();
		model.joinTransaction(modifyTx);
		assertCache(1, 0, 1, 0, 0, 0, 0);

		model.commit(); // modifyTx
		assertCache(0, 0, 0, 1, 1, 0, 0);

		model.startTransaction("QueryCacheStampPurgeTest overlap2");
		assertEquals(list(), TYPE.search(name.equal("testOverlappingTwice2"))); // make transaction acquire a connection
		final Transaction overlapTx2 = model.leaveTransaction();
		assertCache(1, 0, 1, 0, 1, 0, 0);

		model.joinTransaction(overlapTx1);
		assertCache(1, 0, 0, 0, 1, 0, 0);

		model.commit(); // overlapTx1
		assertCache(1, 0, 0, 0, 0, 0, 1);

		model.joinTransaction(overlapTx2);
		assertCache(1, 0, 0, 0, 0, 0, 0);

		model.commit(); // overlapTx2
		assertCache(1, 0, 0, 0, 0, 0, 0);

		model.startTransaction("QueryCacheStampPurgeTest2");
		assertCache(1, 0, 0, 0, 0, 0, 0);
	}


	private QueryCacheInfo last = null;

	private void initCache()
	{
		last = model.getQueryCacheInfo();
	}

	private void assertCache(
			final int level,
			final long hits,
			final long misses,
			final long invalidations,
			final int stampsSize,
			final long stampsHits,
			final long stampsPurged)
	{
		final QueryCacheInfo curr = model.getQueryCacheInfo();
		assertAll(
				() -> assertEquals(level,         curr.getLevel(),                                   "level(1)"),
				() -> assertEquals(hits,          curr.getHits()          - last.getHits(),          "hits(2)"),
				() -> assertEquals(misses,        curr.getMisses()        - last.getMisses(),        "misses(3)"),
				() -> assertEquals(invalidations, curr.getInvalidations() - last.getInvalidations(), "invalidations(4)")
		);
		last = curr;
	}

	private void assumeCacheEnabled()
	{
		assumeTrue(!ignore, "cache enabled");
	}
}
