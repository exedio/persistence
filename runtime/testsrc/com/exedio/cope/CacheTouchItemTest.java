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
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CacheTouchItemTest extends TestWithEnvironment
{
	public CacheTouchItemTest()
	{
		super(CacheIsolationTest.MODEL);
	}

	CacheIsolationItem item;

	@BeforeEach final void setUp()
	{
		item = new CacheIsolationItem("itemName");
	}

	@Test void testIt()
	{
		assumeTrue(cache, "cache");
		int o = oracle ? 1 : 0; // oracle does not provide repeatable read
		initCache();

		assertUpdateCount(0, NONE);
		assertCache(0, 0, 0, 0, 0, 0, 0, 0);
		model.commit();
		assertCache(0, 0, 0, 1, 0, 0, 0, 1);

		// touch row
		final Transaction loader = model.startTransaction("CacheTouchItemTest loader");
		assertUpdateCount(NONE, NONE);
		assertCache(0, 0, 0, 0, 0, 0, 0, 0);

		assertEquals(item, TYPE.searchSingleton(name.equal("itemName")));
		assertUpdateCount(NONE, NONE);
		assertCache(0, 0, 0, 0, 0, 0, 0, 0);

		assertSame(loader, model.leaveTransaction());

		// change row
		model.startTransaction("CacheTouchItemTest changer");
		assertUpdateCount(NONE, NONE);
		assertCache(0, 0, 0, 0, 0, 0, 0, 0);

		item.setName("itemName2");
		assertUpdateCount(1, 0);
		assertCache(1, 0, 1, 0, 0, 0, 0, 0);

		model.commit();
		assertCache(0, 0, 0, 1, 1, 1, 0, 0);

		// load row
		model.joinTransaction(loader);
		assertUpdateCount(NONE, NONE);
		assertCache(0, 0, 0, 0, 0, 1, 0, 0);

		final boolean st = model.getConnectProperties().cacheStamps;

		assertEquals(oracle?"itemName2":"itemName", item.getName());
		assertUpdateCount(o, st?NONE:o);
		assertCache(st?0:1, 0, 1, 0, 0, 1, 1, 0);

		model.commit();
		assertCache(st?0:1, 0, 0, 0, 0, 0, 0, 1);

		// failure
		model.startTransaction("CacheTouchItemTest failer");
		assertUpdateCount(NONE, st?NONE:o);
		assertCache(st?0:1, 0, 0, 0, 0, 0, 0, 0);

		if(st||oracle)
		{
			if(st)
				o = 0;
			assertEquals("itemName2", item.getName());
			assertUpdateCount(1, 1);
			assertCache(1, o, 1-o, 0, 0, 0, 0, 0);

			item.setName("itemName3");
			assertUpdateCount(2, 1);
			assertCache(1, 0, 0, 0, 0, 0, 0, 0);

			assertEquals("itemName3", item.getName());
		}
		else
		{
			assertEquals("itemName", item.getName()); // this is wrong and fixed by itemCacheStamps
			assertUpdateCount(0, 0);
			assertCache(1, 1, 0, 0, 0, 0, 0, 0);

			try
			{
				item.setName("itemName3");
				fail();
			}
			catch(final TemporaryTransactionException e)
			{
				assertTrue(e.getMessage().startsWith("expected one row, but got 0 on statement: UPDATE"), e.getMessage());
			}
			assertUpdateCount(NONE, NONE);
			assertCache(0, 0, 0, 0, 0, 0, 0, 0);

			assertEquals("itemName2", item.getName());
		}
	}

	@SuppressWarnings("deprecation") // OK: using special accessors for tests
	private void assertUpdateCount(final int transaction, final int global)
	{
		assertAll(
				() -> assertEquals(transaction, item.getUpdateCountIfActive(), "transaction"),
				() -> assertEquals(global,      item.getUpdateCountGlobal(),   "global")
		);
	}

	private static final int NONE = Integer.MIN_VALUE;


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
			final long stampsSize,
			final long stampsHits,
			final long stampsPurged)
	{
		final boolean st = model.getConnectProperties().cacheStamps;
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
				() -> assertEquals(st?stampsSize  :0,    curr.getStampsSize(),                                            "stampsSize(6)"),
				() -> assertEquals(st?stampsHits  :0,    curr.getStampsHits()           - last.getStampsHits(),           "stampsHits(7)"),
				() -> assertEquals(st?stampsPurged:0,    curr.getStampsPurged()         - last.getStampsPurged(),         "stampsPurged(8)")
		);
		last = curr;
	}
}
