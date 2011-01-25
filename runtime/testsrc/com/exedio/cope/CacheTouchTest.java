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
import static com.exedio.cope.SchemaInfo.isConcurrentModificationDetectionEnabled;
import static java.lang.Integer.MIN_VALUE;

public class CacheTouchTest extends AbstractRuntimeTest
{
	public CacheTouchTest()
	{
		super(CacheIsolationTest.MODEL);
	}

	CacheIsolationItem item;

	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		item = deleteOnTearDown(new CacheIsolationItem("itemName"));
	}

	public void testIt()
	{
		if(hsqldb||oracle) return; // TODO
		initCache();

		assertModificationCount(0, MIN_VALUE);
		assertCache(0, 0, 0, 0, 0);
		model.commit();

		// touch row
		final Transaction loader = model.startTransaction("CacheTouchTest loader");
		assertModificationCount(MIN_VALUE, MIN_VALUE);
		assertCache(0, 0, 0, 1, 0);

		assertEquals(item, TYPE.searchSingleton(name.equal("itemName")));
		assertModificationCount(MIN_VALUE, MIN_VALUE);
		assertCache(0, 0, 0, 1, 0);

		assertSame(loader, model.leaveTransaction());

		// change row
		model.startTransaction("CacheTouchTest changer");
		assertModificationCount(MIN_VALUE, MIN_VALUE);
		assertCache(0, 0, 0, 1, 0);

		item.setName("itemName2");
		assertModificationCount(1, 0);
		assertCache(1, 0, 1, 1, 0);

		model.commit();

		// load row
		model.joinTransaction(loader);
		assertModificationCount(MIN_VALUE, MIN_VALUE);
		assertCache(0, 0, 1, 2, 1);

	if(!model.getConnectProperties().connectionTransactionIsolationReadCommitted.booleanValue())
	{
		assertEquals("itemName", item.getName());
		assertModificationCount(0, 0);
		assertCache(1, 0, 2, 2, 1);

		model.commit();

		// failure
		model.startTransaction("CacheTouchTest failer");
		assertModificationCount(MIN_VALUE, 0);
		assertCache(1, 0, 2, 2, 1);

		if(isConcurrentModificationDetectionEnabled(model))
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
			assertModificationCount(MIN_VALUE, MIN_VALUE);
			assertCache(0, 1, 2, 2, 1);

			assertEquals("itemName2", item.getName());
		}
		else
		{
			item.setName("itemName3");
			assertModificationCount(MIN_VALUE, 0);
			assertCache(1, 1, 2, 2, 1);

			assertEquals("itemName3", item.getName());
		}
	}
	else
	{
		assertEquals("itemName2", item.getName());
		assertModificationCount(1, 1);
		assertCache(1, 0, 2, 2, 1);

		model.commit();

		// failure
		model.startTransaction("CacheTouchTest failer");
		assertModificationCount(MIN_VALUE, 1);
		assertCache(1, 0, 2, 2, 1);

		// the following fails, if transaction does run in
		// repeatable-read isolation.
		item.setName("itemName3");
		assertModificationCount(2, 1);
		assertCache(1, 1, 2, 2, 1);

		assertEquals("itemName3", item.getName());
	}
	}

	@SuppressWarnings("deprecation") // OK: using special accessors for tests
	private void assertModificationCount(final int expected, final int global)
	{
		final ConnectProperties props = model.getConnectProperties();
		if(isConcurrentModificationDetectionEnabled(model))
		{
			assertEquals("transaction", expected, item.getModificationCountIfActive());
			if(props.getItemCacheLimit()>0)
				assertEquals("global", global, item.getModificationCountGlobal());
			else
				assertEquals("global", Integer.MIN_VALUE, item.getModificationCountGlobal());
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
		assertSame("level"               , level               , ici.getLevel());
		assertSame("hits"                , hits                , ici.getHits()                -initHits                );
		assertSame("misses"              , misses              , ici.getMisses()              -initMisses              );
		assertSame("invalidationsOrdered", invalidationsOrdered, ici.getInvalidationsOrdered()-initInvalidationsOrdered);
		assertSame("invalidationsDone"   , invalidationsDone   , ici.getInvalidationsDone()   -initInvalidationsDone   );
	}
}
