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

import gnu.trove.TLongHashSet;
import gnu.trove.TLongIterator;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

final class ItemCache
{
	private static final TypeStats[] EMPTY_TYPE_STATS_ARRAY = new TypeStats[0];
	private static final ItemCacheInfo[] EMPTY_ITEM_CACHE_INFO_ARRAY = new ItemCacheInfo[0];

	private final LRUMap<Item,WrittenState> map;
	private final LinkedHashMap<Long,Set<Item>> stampList;

	private final TypeStats[] typeStats;

	ItemCache(final List<Type<?>> typesSorted, final ConnectProperties properties)
	{
		final int limit=properties.getItemCacheLimit();
		final List<TypeStats> typesStatsList=new ArrayList<>();
		for(final Type<?> type : typesSorted)
			if(!type.isAbstract)
			{
				final boolean cachingDisabled =
						limit==0 ||
						type.external ||
						CopeCacheWeightHelper.isDisabled(type);

				typesStatsList.add(cachingDisabled?null:new TypeStats(type));
			}

		typeStats=typesStatsList.toArray(EMPTY_TYPE_STATS_ARRAY);
		for(int i = 0; i<typeStats.length; i++)
		{
			final TypeStats stats = typeStats[i];
			if(stats==null)
				continue;

			final Type<?> type = stats.type;
			if(type.cacheIdTransiently!=i)
				throw new RuntimeException("" + type.cacheIdTransiently + '/' + type.id + '/' + i);
		}

		map = new LRUMap<>(limit, eldest ->
				typeStats[eldest.getKey().type.cacheIdTransiently].replacements++);
		if(properties.cacheStamps)
		{
			stampList=new LinkedHashMap<>();
		}
		else
		{
			stampList=null;
		}
	}

	private boolean stampsEnabled()
	{
		return stampList!=null;
	}

	private boolean isStamped(final Item item, final long connectionStamp)
	{
		if (stampsEnabled())
		{
			for (final Map.Entry<Long, Set<Item>> entry: stampList.entrySet())
			{
				if (entry.getKey()<connectionStamp) continue;
				if (entry.getValue().contains(item)) return true;
			}
		}
		return false;
	}

	WrittenState getState(final Transaction tx, final Item item)
	{
		final TypeStats typeStat=typeStats[item.type.cacheIdTransiently];

		WrittenState state;
		if (typeStat==null)
		{
			state = null;
		}
		else
		{
			synchronized (map)
			{
				state = map.get(item);
			}
		}

		if ( state==null )
		{
			state = tx.connect.database.load(tx.getConnection(), item);
			if (typeStat!=null)
			{
				typeStat.misses.incrementAndGet();
				synchronized (map)
				{
					if (isStamped(item, tx.getCacheStamp()))
					{
						typeStat.stampsHit++;
					}
					else
					{
						if(map.put(item, state)!=null)
						{
							typeStat.concurrentLoads++;
						}
					}
				}
			}
		}
		else
		{
			typeStat.hits.incrementAndGet();
		}
		return state;
	}

	/**
	 * @deprecated for unit tests only
	 */
	@Deprecated
	WrittenState getStateIfPresent(final Item item)
	{
		synchronized (map)
		{
			return map.get(item);
		}
	}

	void remove(final Item item)
	{
		synchronized (map)
		{
			map.remove(item);
		}
	}

	void invalidate(final TLongHashSet[] invalidations)
	{
		final boolean stampsEnabled = stampsEnabled();
		final long stamp = stampsEnabled?ItemCacheStamp.next():0;
		final Set<Item> invalidated=stampsEnabled?new HashSet<>():null;
		synchronized (map)
		{
			for(int typeTransiently=0; typeTransiently<invalidations.length; typeTransiently++)
			{
				final TLongHashSet invalidatedPKs = invalidations[typeTransiently];
				final TypeStats typeStat=typeStats[typeTransiently];
				if(invalidatedPKs!=null && typeStat!=null)
				{
					for(final TLongIterator i = invalidatedPKs.iterator(); i.hasNext(); )
					{
						final Type<?> type=typeStat.type;
						if (type.cacheIdTransiently!=typeTransiently) throw new RuntimeException();
						final Item item=type.activate(i.next());
						if (stampsEnabled) invalidated.add(item);
						typeStat.invalidationsOrdered++;
						if (map.remove(item)!=null)
						{
							typeStat.invalidationsDone++;
						}
					}
				}
			}
			if (stampsEnabled) stampList.put(stamp, invalidated);
		}
	}

	void clear()
	{
		synchronized (map)
		{
			map.clear();
		}
	}

	void purgeStamps(final long untilStamp)
	{
		if (stampsEnabled())
		{
			synchronized (map)
			{
				for (final Iterator<Map.Entry<Long, Set<Item>>> iter=stampList.entrySet().iterator(); iter.hasNext();)
				{
					final Map.Entry<Long, Set<Item>> entry=iter.next();
					if (entry.getKey()<untilStamp)
					{
						for (final Item item: entry.getValue())
						{
							// non-cached items don't get stamped, so the typeStats entry can't be null
							typeStats[item.type.cacheIdTransiently].stampsPurged++;
						}
						iter.remove();
					}
					else
					{
						break;
					}
				}
			}
		}
	}

	/**
	 * @deprecated for unit tests only
	 */
	@Deprecated
	void clearStamps()
	{
		if (stampsEnabled())
		{
			synchronized (map)
			{
				stampList.clear();
			}
		}
	}

	ItemCacheStatistics getStatistics(final List<Type<?>> typesInOriginalOrder)
	{
		final List<ItemCacheInfo> details = new ArrayList<>(typeStats.length);

		final int[] levels=new int[typeStats.length];
		final int[] stampsSizes=new int[typeStats.length];
		final int level;
		synchronized (map)
		{
			level=map.size();
			for (final Item item: map.keySet())
			{
				levels[item.type.cacheIdTransiently]++;
			}
			if (stampsEnabled())
			{
				for (final Set<Item> value: stampList.values())
				{
					for (final Item item: value)
					{
						stampsSizes[item.type.cacheIdTransiently]++;
					}
				}
			}
		}
		for(final Type<?> type : typesInOriginalOrder)
		{
			final TypeStats typeStat = typeStats[type.cacheIdTransiently];
			if (typeStat!=null)
				details.add( typeStat.createItemCacheInfo(levels, stampsSizes) );
		}

		return new ItemCacheStatistics(map.maxSize, level, details.toArray(EMPTY_ITEM_CACHE_INFO_ARRAY));
	}

	static class TypeStats
	{
		final Type<?> type;
		final AtomicLong hits = new AtomicLong();
		final AtomicLong misses = new AtomicLong();
		long concurrentLoads = 0;
		long replacements = 0;
		long invalidationsOrdered = 0;
		long invalidationsDone = 0;
		long stampsHit = 0;
		long stampsPurged = 0;

		TypeStats(final Type<?> type)
		{
			this.type=type;
		}

		ItemCacheInfo createItemCacheInfo(final int[] levels, final int[] stampsSizes)
		{
			return new ItemCacheInfo(
				type,
				levels[type.cacheIdTransiently],
				hits.get(),
				misses.get(),
				concurrentLoads,
				replacements,
				invalidationsOrdered, invalidationsDone,
				stampsSizes[type.cacheIdTransiently],
				stampsHit, stampsPurged
			);
		}
	}
}
