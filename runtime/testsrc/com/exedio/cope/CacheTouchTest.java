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
import static com.exedio.cope.SchemaInfo.isUpdateCounterEnabled;
import static java.lang.Integer.MIN_VALUE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

public class CacheTouchTest extends TestWithEnvironment
{
	public CacheTouchTest()
	{
		super(CacheIsolationTest.MODEL);
	}

	CacheIsolationItem item;

	@Before public final void setUp()
	{
		item = new CacheIsolationItem("itemName");
	}

	@Test public void testIt()
	{
		if(!cache || oracle) return; // TODO
		initCache();

		assertUpdateCount(0, MIN_VALUE);
		assertCache(0, 0, 0, 0, 0);
		model.commit();

		// touch row
		final Transaction loader = model.startTransaction("CacheTouchTest loader");
		assertUpdateCount(MIN_VALUE, MIN_VALUE);
		assertCache(0, 0, 0, 1, 0);

		assertEquals(item, TYPE.searchSingleton(name.equal("itemName")));
		assertUpdateCount(MIN_VALUE, MIN_VALUE);
		assertCache(0, 0, 0, 1, 0);

		assertSame(loader, model.leaveTransaction());

		// change row
		model.startTransaction("CacheTouchTest changer");
		assertUpdateCount(MIN_VALUE, MIN_VALUE);
		assertCache(0, 0, 0, 1, 0);

		item.setName("itemName2");
		assertUpdateCount(1, 0);
		assertCache(1, 0, 1, 1, 0);

		model.commit();

		// load row
		model.joinTransaction(loader);
		assertUpdateCount(MIN_VALUE, MIN_VALUE);
		assertCache(0, 0, 1, 2, 1);

	final ConnectProperties props = model.getConnectProperties();
	if(!props.itemCacheStamps)
	{
		assertEquals("itemName", item.getName());
		assertUpdateCount(0, 0);
		assertCache(1, 0, 2, 2, 1);

		model.commit();

		// failure
		model.startTransaction("CacheTouchTest failer");
		assertUpdateCount(MIN_VALUE, 0);
		assertCache(1, 0, 2, 2, 1);

		{
			try
			{
				// TODO
				// The exception is a bug that needs to be fixed.
				item.setName("itemName3");
				fail();
			}
			catch(final TemporaryTransactionException e)
			{
				// ok
			}
			assertUpdateCount(MIN_VALUE, MIN_VALUE);
			assertCache(0, 1, 2, 2, 1);

			assertEquals("itemName2", item.getName());
		}
	}
	else
	{
		final boolean il = props.itemCacheStamps;

		assertEquals("itemName", item.getName());
		assertUpdateCount(0, il?MIN_VALUE:1);
		assertCache(il?0:1, 0, 2, 2, 1);

		model.commit();

		// failure
		model.startTransaction("CacheTouchTest failer");
		assertUpdateCount(MIN_VALUE, il?MIN_VALUE:1);
		assertCache(il?0:1, 0, 2, 2, 1);

		// the following fails, if transaction does run in
		// repeatable-read isolation and does no itemCacheStamp.
		item.setName("itemName3");
		assertUpdateCount(2, 1);
		assertCache(1, il?0:1, il?3:2, 2, 1);

		assertEquals("itemName3", item.getName());
	}
	}

	@SuppressWarnings("deprecation") // OK: using special accessors for tests
	private void assertUpdateCount(final int expected, final int global)
	{
		final ConnectProperties props = model.getConnectProperties();
		if(isUpdateCounterEnabled(model))
		{
			assertEquals("transaction", expected, item.getUpdateCountIfActive());
			if(props.getItemCacheLimit()>0)
				assertEquals("global", global, item.getUpdateCountGlobal());
			else
				assertEquals("global", Integer.MIN_VALUE, item.getUpdateCountGlobal());
		}
	}


	private long initHits, initMisses, initInvalidationsOrdered, initInvalidationsDone;

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
	}

	private void assertCache(
			final int level,
			final long hits,
			final long misses,
			final long invalidationsOrdered,
			final long invalidationsDone)
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
	}
}
