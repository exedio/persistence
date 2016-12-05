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

final class ItemCache
{
	private final LRUMap<Item,WrittenState> map;
	private final Map<Long,Set<Item>> stampList=new LinkedHashMap<>();

	private final TypeStats[] typeStats;

	ItemCache(final List<Type<?>> typesSorted, final ConnectProperties properties)
	{
		final int itemCacheLimit=properties.getItemCacheLimit();
		final List<TypeStats> typesStatsList=new ArrayList<>();
		for(final Type<?> type : typesSorted)
			if(!type.isAbstract)
			{
				@SuppressWarnings("deprecation")
				final CopeCacheWeight cacheWeight=type.getAnnotation(CopeCacheWeight.class);
				final boolean cachingDisabled = itemCacheLimit==0 || (cacheWeight!=null && cacheWeight.value()==0) || type.external;
				typesStatsList.add(cachingDisabled?null:new TypeStats(type));
			}
		typeStats=typesStatsList.toArray(new TypeStats[typesStatsList.size()]);
		map = new LRUMap<>(itemCacheLimit, eldest ->
		{
			typeStats[eldest.getKey().type.cacheIdTransiently].replacements.inc();
		});
	}

	private boolean isStamped(final Item item, final long connectionStamp)
	{
		for (final Map.Entry<Long, Set<Item>> entry: stampList.entrySet())
		{
			if (entry.getKey()<connectionStamp) continue;
			if (entry.getValue().contains(item)) return true;
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
				typeStat.misses.inc();
				synchronized (map)
				{
					if (isStamped(item, tx.getCacheStamp()))
					{
						typeStat.stampsHit.inc();
					}
					else
					{
						if(map.put(item, state)!=null)
						{
							typeStat.concurrentLoads.inc();
						}
					}
				}
			}
		}
		else
		{
			typeStat.hits.inc();
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
		final long stamp = ItemCacheStamp.next();
		synchronized (map)
		{
			final Set<Item> invalidated=new HashSet<>();
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
						invalidated.add(item);
						typeStat.invalidationsOrdered.inc();
						if (map.remove(item)!=null)
						{
							typeStat.invalidationsDone.inc();
						}
					}
				}
			}
			stampList.put(stamp, invalidated);
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
						typeStats[item.type.cacheIdTransiently].stampsPurged.inc();
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

	/**
	 * @deprecated for unit tests only
	 */
	@Deprecated
	void clearStamps()
	{
		synchronized (map)
		{
			stampList.clear();
		}
	}

	ItemCacheStatistics getStatistics(final List<Type<?>> typesInOriginalOrder)
	{
		final List<ItemCacheInfo> itemCacheInfos = new ArrayList<>(typeStats.length);

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
			for (final Set<Item> value: stampList.values())
			{
				for (final Item item: value)
				{
					stampsSizes[item.type.cacheIdTransiently]++;
				}
			}
		}
		for(final Type<?> type : typesInOriginalOrder)
		{
			final TypeStats typeStat = typeStats[type.cacheIdTransiently];
			if (typeStat!=null)
				itemCacheInfos.add( typeStat.createItemCacheInfo(levels, stampsSizes) );
		}

		return new ItemCacheStatistics(map.maxSize, level, itemCacheInfos.toArray(new ItemCacheInfo[itemCacheInfos.size()]));
	}

	static class TypeStats
	{
		private final Type<?> type;
		private final VolatileLong hits = new VolatileLong();
		private final VolatileLong misses = new VolatileLong();
		private final VolatileLong concurrentLoads = new VolatileLong();
		private final VolatileLong replacements = new VolatileLong();
		private final VolatileLong invalidationsOrdered = new VolatileLong();
		private final VolatileLong invalidationsDone = new VolatileLong();
		private final VolatileLong stampsHit = new VolatileLong();
		private final VolatileLong stampsPurged = new VolatileLong();

		TypeStats(final Type<?> type)
		{
			this.type=type;
		}

		private ItemCacheInfo createItemCacheInfo(final int[] levels, final int[] stampsSizes)
		{
			return new ItemCacheInfo(
				type,
				levels[type.cacheIdTransiently],
				hits.get(),
				misses.get(),
				concurrentLoads.get(),
				replacements.get(),
				invalidationsOrdered.get(), invalidationsDone.get(),
				stampsSizes[type.cacheIdTransiently],
				stampsHit.get(), stampsPurged.get()
			);
		}
	}
}
