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

import static com.exedio.cope.CacheIsolationItem.name;
import static com.exedio.cope.PrometheusMeterRegistrar.meter;
import static com.exedio.cope.PrometheusMeterRegistrar.tag;
import static com.exedio.cope.RuntimeTester.getQueryCacheInfo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CacheTouchQueryTest extends TestWithEnvironment
{
	public CacheTouchQueryTest()
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
		assertEquals(model.getConnectProperties().getQueryCacheLimit(), gauge("maximumSize"));
		assumeTrue(cache, "cache");
		initCache();

		assertCache(0, 0, 0, 0, 0, 0, 0);
		model.commit();
		assertCache(0, 0, 0, 0, 0, 0, 1);

		// touch row
		final Transaction loader = model.startTransaction("CacheTouchTest loader");
		assertCache(0, 0, 0, 0, 0, 0, 0);

		assertEquals("itemName", item.getName());
		assertCache(0, 0, 0, 0, 0, 0, 0);

		assertSame(loader, model.leaveTransaction());

		// change row
		model.startTransaction("CacheTouchTest changer");
		assertCache(0, 0, 0, 0, 0, 0, 0);

		item.setName("itemName2");
		assertCache(0, 0, 0, 0, 0, 0, 0);

		model.commit();
		assertCache(0, 0, 0, 0, 1, 0, 0);

		// load row
		model.joinTransaction(loader);
		assertCache(0, 0, 0, 0, 1, 0, 0);

		final boolean st = model.getConnectProperties().cacheStamps;

		final Query<String> query = new Query<>(name);
		assertEquals("itemName", query.searchSingleton());
		assertCache(st?0:1, 0, 1, 0, 1, 1, 0);

		model.commit();
		assertCache(st?0:1, 0, 0, 0, 0, 0, 1);

		// failure
		model.startTransaction("CacheTouchTest failer");
		assertCache(st?0:1, 0, 0, 0, 0, 0, 0);

		if(st)
		{
			assertEquals("itemName2", query.searchSingleton());
			assertCache(1, 0, 1, 0, 0, 0, 0);
		}
		else
		{
			assertEquals("itemName", query.searchSingleton()); // this is wrong and fixed by cacheStamps
			assertCache(1, 1, 0, 0, 55, 66, 77);
		}
	}


	private QueryCacheInfo last = null;

	private void initCache()
	{
		last = getQueryCacheInfo(model);
	}

	private void assertCache(
			final int level,
			final long hits,
			final long misses,
			final long invalidations,
			final int  stampsSize,
			final long stampsHits,
			final long stampsPurged)
	{
		final boolean st = model.getConnectProperties().cacheStamps;
		final QueryCacheInfo curr = getQueryCacheInfo(model);
		assertAll(
				() -> assertEquals(level,             curr.getLevel(),                                   "level(1)"),
				() -> assertEquals(hits,              curr.getHits()          - last.getHits(),          "hits(2)"),
				() -> assertEquals(misses,            curr.getMisses()        - last.getMisses(),        "misses(3)"),
				() -> assertEquals(0,                 curr.getReplacements()  - last.getReplacements(),  "replacements"),
				() -> assertEquals(invalidations,     curr.getInvalidations() - last.getInvalidations(), "invalidations(4)"),
				() -> assertEquals(0,                 curr.getConcurrentLoads()- last.getConcurrentLoads(),"concurrentLoads"),
				() -> assertEquals(st?stampsSize  :0, curr.getStampsSize(),                              "stampsSize(5)"),
				() -> assertEquals(st?stampsHits  :0, curr.getStampsHits()    - last.getStampsHits(),    "stampsHits(6)"),
				() -> assertEquals(st?stampsPurged:0, curr.getStampsPurged()  - last.getStampsPurged(),  "stampsPurged(7)")
		);
		last = curr;
		assertEquals(curr.getLevel(),  gauge("size"));
		assertEquals(curr.getHits(),   count("gets", "result", "hit"));
		assertEquals(curr.getMisses(), count("gets", "result", "miss"));
		assertEquals(curr.getReplacements(),    count("evictions"));
		assertEquals(curr.getInvalidations(),   count("invalidations"));
		assertEquals(curr.getConcurrentLoads(), count("concurrentLoad"));
		assertEquals(curr.getStampsSize(),   gauge("stamp.transactions"));
		assertEquals(curr.getStampsHits(),   count("stamp.hit"));
		assertEquals(curr.getStampsPurged(), count("stamp.purge"));
	}

	private double count(final String nameSuffix)
	{
		return ((Counter)meter(QueryCache.class, nameSuffix, tag(model))).count();
	}

	private double count(final String nameSuffix, final String key, final String value)
	{
		return ((Counter)meter(QueryCache.class, nameSuffix, tag(model).and(key, value))).count();
	}

	private double gauge(final String nameSuffix)
	{
		return ((Gauge)meter(QueryCache.class, nameSuffix, tag(model))).value();
	}
}
