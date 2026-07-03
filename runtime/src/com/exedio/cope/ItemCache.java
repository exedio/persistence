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
import io.micrometer.core.instrument.Counter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;

final class ItemCache
{
	private final LRUMap<Item,WrittenState> map;
	private final CacheStamp cacheStamp;
	private final ArrayDeque<Stamp> stampList;

	private final TypeStats[] typeStats;

	ItemCache(final ModelMetrics metricsTemplate, final Types types, final ConnectProperties properties, final CacheStamp cacheStamp)
	{
		final int limit=properties.getItemCacheLimit();
		final boolean unique = properties.itemCacheUnique;
		final Metrics metrics = new Metrics(metricsTemplate);
		final List<TypeStats> typesStatsList=new ArrayList<>();
		for(final Type<?> type : types.typeListSorted)
			if(!type.isAbstract)
				typesStatsList.add(
						limit!=0 && !type.external // cache enabled
						? new TypeStats(metrics.back, type, unique)
						: null);

		typeStats=typesStatsList.toArray(TypeStats[]::new);
		for(int i = 0; i<typeStats.length; i++)
		{
			final TypeStats stats = typeStats[i];
			if(stats==null)
				continue;

			final Type<?> type = stats.type;
			if(type.cacheIdTransiently!=i)
				throw new RuntimeException(type.cacheIdTransiently + "/" + type.id + '/' + i);
		}

		map = new LRUMap<>(limit, eldest -> {
			final Item item = eldest.getKey();
			final TypeStats typeStat = typeStats[item.type.cacheIdTransiently];
			for(final var uc : typeStat.uniqueCaches)
				uc.removeSynchronized(eldest.getValue(), item);
			//noinspection NonAtomicOperationOnVolatileField OK: happens while synchronized on "map"
			typeStat.level--;
			typeStat.evictions.increment();
		});
		this.cacheStamp = cacheStamp;
		if(properties.cacheStamps)
		{
			stampList=new ArrayDeque<>();
		}
		else
		{
			stampList=null;
		}

		metrics.gaugeD(c -> c.map.maxSize, "maximumSize", "The maximum number of entries in this cache, causing eviction if exceeded"); // name conforms to com.google.common.cache.CacheBuilder
		metrics.gaugeM(c -> c.map,         "size",        "The exact number of entries in this cache"); // name conforms to CacheMeterBinder
		metrics.gaugeL(c -> c.stampList,   "stamp.transactions", "Number of transactions in stamp list");
	}

	@SuppressWarnings("ClassCanBeRecord")
	private static final class Metrics
	{
		final ModelMetrics back;

		Metrics(final ModelMetrics metricsTemplate)
		{
			this.back = metricsTemplate.name(ItemCache.class);
		}

		void gaugeD(
				final ToDoubleFunction<ItemCache> f,
				final String nameSuffix,
				final String description)
		{
			back.gaugeConnect(
					c -> f.applyAsDouble(c.itemCache),
					nameSuffix, description);
		}

		void gaugeM(
				final Function<ItemCache, Map<?, ?>> f,
				final String nameSuffix,
				final String description)
		{
			back.gaugeConnect(c ->
					{
						final ItemCache cache = c.itemCache;
						final Map<?,?> map = f.apply(cache);
						if(map==null)
							return 0.0;

						synchronized(cache.map)
						{
							return map.size();
						}
					},
					nameSuffix, description);
		}

		void gaugeL(
				final Function<ItemCache, ArrayDeque<?>> f,
				final String nameSuffix,
				final String description)
		{
			back.gaugeConnect(c ->
					{
						final ItemCache cache = c.itemCache;
						final ArrayDeque<?> deque = f.apply(cache);
						if(deque==null)
							return 0.0;

						synchronized(cache.map)
						{
							return deque.size();
						}
					},
					nameSuffix, description);
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
			for (final Iterator<Stamp> i = stampList.descendingIterator(); i.hasNext(); )
			{
				final Stamp entry = i.next();
				if (entry.stamp<connectionStamp) break;
				if (entry.items.contains(item)) return true;
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
				typeStat.misses.increment();
				synchronized (map)
				{
					if (isStamped(item, tx.getCacheStamp()))
					{
						typeStat.stampsHit.increment();
					}
					else
					{
						if(map.put(item, state)!=null)
						{
							typeStat.concurrentLoads.increment();
						}
						else
						{
							typeStat.level++;
						}
						for(final var uc : typeStat.uniqueCaches)
							uc.putSynchronized(item, state);
					}
				}
			}
		}
		else
		{
			typeStat.hits.increment();
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
			if(map.remove(item)!=null)
				typeStats[item.type.cacheIdTransiently].level--;
		}
	}

	void invalidate(final TLongHashSet[] invalidations)
	{
		final boolean stampsEnabled = stampsEnabled();
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
						final WrittenState removed = map.remove(item);
						if (removed!=null)
						{
							for(final var uc : typeStat.uniqueCaches)
								uc.removeSynchronized(removed, item);
							typeStat.invalidationsActual.increment();
							typeStat.level--;
						}
						else
						{
							typeStat.invalidationsFutile.increment();
						}
					}
				}
			}
			if(stampsEnabled)
			{
				final long stamp = cacheStamp.next();
				assert stampList.peekLast()==null || stamp>stampList.peekLast().stamp;
				stampList.addLast(new Stamp(stamp, invalidated));
			}
		}
	}

	void clear()
	{
		synchronized (map)
		{
			map.clear();
			for(final var typeStat : typeStats)
				if(typeStat!=null)
					for(final var uc : typeStat.uniqueCaches)
						uc.clearSynchronized();
		}
	}

	void purgeStamps(final long untilStamp)
	{
		if (stampsEnabled())
		{
			synchronized (map)
			{
				for (final Iterator<Stamp> iter=stampList.iterator(); iter.hasNext();)
				{
					final Stamp entry=iter.next();
					if (entry.stamp<untilStamp)
					{
						for (final Item item: entry.items)
						{
							// non-cached items don't get stamped, so the typeStats entry can't be null
							typeStats[item.type.cacheIdTransiently].stampsPurged.increment();
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
	@Deprecated @SuppressWarnings("DeprecatedIsStillUsed") // OK: for unit tests only
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

		final int[] stampsSizes=new int[typeStats.length];
		final int level;
		synchronized (map)
		{
			level=map.size();
			if (stampsEnabled())
			{
				for (final Stamp value: stampList)
				{
					for (final Item item: value.items)
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
				details.add( typeStat.createItemCacheInfo(stampsSizes) );
		}

		return new ItemCacheStatistics(map.maxSize, level, details.toArray(ItemCacheInfo[]::new));
	}

	private static final class TypeStats
	{
		final Type<?> type;

		/**
		 * Modified when synchronized on {@link ItemCache#map}.
		 */
		volatile int level = 0;

		final Counter hits;
		final Counter misses;
		final Counter concurrentLoads;
		final Counter evictions;
		final Counter invalidationsFutile;
		final Counter invalidationsActual;
		final Counter stampsHit;
		final Counter stampsPurged;
		final UniqueCache[] uniqueCaches;

		TypeStats(final ModelMetrics metricsTemplate, final Type<?> type, final boolean unique)
		{
			this.type = type;
			final ModelMetrics metrics = metricsTemplate.name(ItemCache.class).tag(type);
			final int cacheIdTransiently = type.cacheIdTransiently;
			metrics.gaugeConnect(c -> c.itemCache.typeStats[cacheIdTransiently].level, "sizePerType", "The exact number of entries for the type in this cache");
			hits                = metrics.counter("gets", "result", "hit",  "The number of times cache lookup methods have returned a cached value."); // name conforms to CacheMeterBinder
			misses              = metrics.counter("gets", "result", "miss", "The number of times cache lookup methods have returned an uncached (newly loaded) value"); // name conforms to CacheMeterBinder
			concurrentLoads     = metrics.counter("concurrentLoad", "How often an item was loaded concurrently");
			evictions           = metrics.counter("evictions",      "Evictions in the item cache, as 'size' exceeded 'maximumSize'."); // name conforms to CacheMeterBinder
			invalidationsFutile = metrics.counter("invalidations", "effect", "futile", "Invalidations in the item cache, that were futile because the item was not in cache");
			invalidationsActual = metrics.counter("invalidations", "effect", "actual", "Invalidations in the item cache, that were effective because the item was in cache");
			stampsHit           = metrics.counter("stamp.hit",   "How often a stamp prevented an item from being stored");
			stampsPurged        = metrics.counter("stamp.purge", "How many stamps were purged because there was no transaction older than the stamp");

			if(unique)
			{
				ArrayList<UniqueCache> uniqueCaches = null;
				for(final var uq : type.getUniqueConstraints())
				{
					if(uq.getFields().size()>1) // TODO support unique constraints with more than one field as well
						continue;

					final var uc = uq.initCache(metricsTemplate);
					if(uniqueCaches==null)
						uniqueCaches = new ArrayList<>();
					uniqueCaches.add(uc);
				}
				this.uniqueCaches =
						uniqueCaches!=null
						? uniqueCaches.toArray(UniqueCache[]::new)
						: EMPTY_UNIQUE_CACHES;
			}
			else
				this.uniqueCaches = EMPTY_UNIQUE_CACHES;
		}

		private static final UniqueCache[] EMPTY_UNIQUE_CACHES = {};

		ItemCacheInfo createItemCacheInfo(final int[] stampsSizes)
		{
			return new ItemCacheInfo(
				type,
				level,
				hits,
				misses,
				concurrentLoads,
				evictions,
				invalidationsFutile, invalidationsActual,
				stampsSizes[type.cacheIdTransiently],
				stampsHit, stampsPurged
			);
		}
	}

	private record Stamp(long stamp, Set<Item> items)
	{
	}
}
