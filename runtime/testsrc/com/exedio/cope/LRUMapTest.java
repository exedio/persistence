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

import static com.exedio.cope.misc.TimeUtil.toMillies;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

import gnu.trove.TLongObjectHashMap;
import java.io.Serial;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class LRUMapTest
{
	@SuppressWarnings("ClassExtendsConcreteCollection") // OK: used for code injection in test
	private static final class DateMap<K, V> extends HashMap<K, V>
	{
		@Serial
		private static final long serialVersionUID = 1l;

		@SuppressWarnings({"unused", "FieldCanBeLocal"})
		private long date;

		/**
		 * make non-private
		 */
		DateMap()
		{
			// empty
		}

		@Override
		public V get(final Object key)
		{
			date = System.currentTimeMillis();
			return super.get(key);
		}
	}

	@Test void testIt()
	{
		final ArrayList<String> replaced = new ArrayList<>();
		final LRUMap<String, String> map = new LRUMap<>(3, eldest -> replaced.add(eldest.getKey()));
		assertIt(map, new String[]{}, replaced);

		map.put("key1", "val1");
		assertIt(map, new String[]{"key1"}, replaced);

		map.put("key2", "val2");
		assertIt(map, new String[]{"key1", "key2"}, replaced);

		map.put("key3", "val3");
		assertIt(map, new String[]{"key1", "key2", "key3"}, replaced);

		assertEquals("val2", map.get("key2"));
		assertIt(map, new String[]{"key1", "key3", "key2"}, replaced);

		map.put("key4", "val4");
		assertIt(map, new String[]{"key3", "key2", "key4"}, replaced, "key1");

		map.put("key5", "val5");
		assertIt(map, new String[]{"key2", "key4", "key5"}, replaced, "key3");
	}

	private static void assertIt(
			final LRUMap<String, String> map,
			final String[] keys,
			final ArrayList<String> actualReplaced,
			final String... expectedReplaced)
	{
		assertEquals(asList(keys), asList(map.keySet().toArray(new String[map.size()])));

		final ArrayList<String> values = new ArrayList<>();
		for(final String key : keys)
			values.add(key.replace("key", "val"));
		assertEquals(values, new ArrayList<>(map.values()));

		assertEquals(asList(expectedReplaced), actualReplaced);
		actualReplaced.clear();
	}

	@Disabled
	@Test void testPerformance()
	{
		final AtomicLong counter = new AtomicLong();
		for(int j = 0; j<8; j++)
		{
			assertPerformance(new HashMap<>());
			assertPerformance(new LRUMap<>(2_000_000, x -> counter.incrementAndGet()));
			assertPerformance(new DateMap<>());
			assertPerformance(new TLongObjectHashMap<>());
			System.out.println();
		}
	}

	private static void assertPerformance(final HashMap<Long, String> map)
	{
		System.out.print(' ' + map.getClass().getSimpleName() + ":");
		final long startMem = mem();
		for(long i = 0; i<1_000_000; i++)
			map.put(i, "val"+i);
		final long endMem = mem();
		System.out.print(" " + ((startMem+endMem)/1000000) + "MB");
		final long start = System.nanoTime();
		for(long i = 0; i<1_000_000; i++)
			map.get(i);
		final long end = System.nanoTime();
		System.out.print(" " + toMillies(end, start) + "ms");
	}

	private static void assertPerformance(final TLongObjectHashMap<String> map)
	{
		System.out.print(' ' + map.getClass().getSimpleName() + ":");
		final long startMem = mem();
		for(long i = 0; i<1_000_000; i++)
			map.put(i, "val"+i);
		final long endMem = mem();
		System.out.print(" " + ((startMem+endMem)/1000000) + "MB");
		final long start = System.nanoTime();
		for(long i = 0; i<1_000_000; i++)
			map.get(i);
		final long end = System.nanoTime();
		System.out.print(" " + toMillies(end, start) + "ms");
	}

	private static long mem()
	{
		System.gc();
		return Runtime.getRuntime().freeMemory();
	}
}
