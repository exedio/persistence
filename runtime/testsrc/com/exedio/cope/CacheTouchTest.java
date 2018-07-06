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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CacheTouchTest extends TestWithEnvironment
{
	public CacheTouchTest()
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
		assumeTrue(!oracle, "not oracle"); // TODO
		initCache();

		assertUpdateCount(0, NONE);
		assertCache(0, 0, 0, 0, 0, 0, 0, 0);
		model.commit();
		assertCache(0, 0, 0, 1, 0, 0, 0, 1);

		// touch row
		final Transaction loader = model.startTransaction("CacheTouchTest loader");
		assertUpdateCount(NONE, NONE);
		assertCache(0, 0, 0, 1, 0, 0, 0, 1);

		assertEquals(item, TYPE.searchSingleton(name.equal("itemName")));
		assertUpdateCount(NONE, NONE);
		assertCache(0, 0, 0, 1, 0, 0, 0, 1);

		assertSame(loader, model.leaveTransaction());

		// change row
		model.startTransaction("CacheTouchTest changer");
		assertUpdateCount(NONE, NONE);
		assertCache(0, 0, 0, 1, 0, 0, 0, 1);

		item.setName("itemName2");
		assertUpdateCount(1, 0);
		assertCache(1, 0, 1, 1, 0, 0, 0, 1);

		model.commit();
		assertCache(0, 0, 1, 2, 1, 1, 0, 1);

		// load row
		model.joinTransaction(loader);
		assertUpdateCount(NONE, NONE);
		assertCache(0, 0, 1, 2, 1, 1, 0, 1);

		final boolean st = model.getConnectProperties().itemCacheStamps;

		assertEquals("itemName", item.getName());
		assertUpdateCount(0, st?NONE:0);
		assertCache(st?0:1, 0, 2, 2, 1, 1, 1, 1);

		model.commit();
		assertCache(st?0:1, 0, 2, 2, 1, 0, 1, 2);

		// failure
		model.startTransaction("CacheTouchTest failer");
		assertUpdateCount(NONE, st?NONE:0);
		assertCache(st?0:1, 0, 2, 2, 1, 0, 1, 2);

		if(st)
		{
			// the following fails, if transaction does run in
			// repeatable-read isolation and does no itemCacheStamp.
			item.setName("itemName3");
			assertUpdateCount(2, 1);
			assertCache(1, 0, 3, 2, 1, 0, 1, 2);

			assertEquals("itemName3", item.getName());
		}
		else
		{
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
			assertCache(0, 1, 2, 2, 1, 0, 0, 0);

			assertEquals("itemName2", item.getName());
		}
	}

	@SuppressWarnings("deprecation") // OK: using special accessors for tests
	private void assertUpdateCount(final int expected, final int global)
	{
		assertEquals(expected, item.getUpdateCountIfActive(), "transaction");
		assertEquals(global, item.getUpdateCountGlobal(), "global");
	}

	private static final int NONE = Integer.MIN_VALUE;


	private long
			initHits, initMisses,
			initInvalidationsOrdered, initInvalidationsDone,
			initStampsSize, initStampsHits, initStampsPurged;

	private void initCache()
	{
		final ItemCacheInfo[] icis = model.getItemCacheStatistics().getDetails();
		assertEquals(1, icis.length);
		final ItemCacheInfo ici = icis[0];
		assertSame(TYPE, ici.getType());
		initHits = ici.getHits();
		initMisses = ici.getMisses();
		initInvalidationsOrdered = ici.getInvalidationsOrdered();
		initInvalidationsDone = ici.getInvalidationsDone();
		initStampsSize = ici.getStampsSize();
		initStampsHits = ici.getStampsHits();
		initStampsPurged = ici.getStampsPurged();
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
		final boolean st = model.getConnectProperties().itemCacheStamps;
		final ItemCacheInfo[] icis = model.getItemCacheStatistics().getDetails();
		assertEquals(1, icis.length);
		final ItemCacheInfo ici = icis[0];
		assertSame(TYPE, ici.getType());
		assertEquals(level,                ici.getLevel(), "level");
		assertEquals(hits,                 ici.getHits()                 - initHits,                 "hits");
		assertEquals(misses,               ici.getMisses()               - initMisses,               "misses");
		assertEquals(invalidationsOrdered, ici.getInvalidationsOrdered() - initInvalidationsOrdered, "invalidationsOrdered");
		assertEquals(invalidationsDone,    ici.getInvalidationsDone()    - initInvalidationsDone,    "invalidationsDone");
		assertEquals(st?stampsSize  :0,    ici.getStampsSize()           - initStampsSize,           "stampsSize");
		assertEquals(st?stampsHits  :0,    ici.getStampsHits()           - initStampsHits,           "stampsHits");
		assertEquals(st?stampsPurged:0,    ici.getStampsPurged()         - initStampsPurged,         "stampsPurged");
	}
}
