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

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import junit.framework.TestCase;

public class LRUMapTest extends TestCase
{
	private static final class LRUMap<K, V> extends LinkedHashMap<K, V>
	{
		private static final long serialVersionUID = 1l;

		private final int maxSize;
		volatile long replacements = 0;

		LRUMap(final int maxSize)
		{
			super(maxSize, 0.75f/*DEFAULT_LOAD_FACTOR*/, true);
			this.maxSize = maxSize;
		}

		@Override
		protected boolean removeEldestEntry(final Map.Entry<K,V> eldest)
		{
			//System.out.println("-----eldest("+size()+"):"+eldest.getKey());
			final boolean result = size() > maxSize;
			if(result)
				replacements++;
			return result;
		}
	}

	public void testIt()
	{
		final LRUMap<String, String> map = new LRUMap<String, String>(4);
		assertIt(map, new String[]{}, new String[]{});

		map.put("key1", "val1");
		assertIt(map, new String[]{"key1"}, new String[]{"val1"});

		map.put("key2", "val2");
		assertIt(map, new String[]{"key1", "key2"}, new String[]{"val1", "val2"});

		map.put("key3", "val3");
		assertIt(map, new String[]{"key1", "key2", "key3"}, new String[]{"val1", "val2", "val3"});

		assertEquals("val2", map.get("key2"));
		assertIt(map, new String[]{"key1", "key3", "key2"}, new String[]{"val1", "val3", "val2"});
	}

	private static void assertIt(final LRUMap<String, String> map, final String[] keys, final String[] values)
	{
		assertEquals(Arrays.asList(keys),   Arrays.asList(map.keySet().toArray(new String[map.size()])));
		assertEquals(Arrays.asList(values), Arrays.asList(map.values().toArray(new String[map.size()])));
	}
}
