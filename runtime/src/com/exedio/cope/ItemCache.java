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

import static com.exedio.cope.IntRatio.ratio;

import gnu.trove.TLongHashSet;
import gnu.trove.TLongIterator;
import gnu.trove.TLongLongHashMap;
import gnu.trove.TLongLongIterator;
import gnu.trove.TLongObjectHashMap;
import gnu.trove.TLongObjectIterator;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

final class ItemCache
{
	/**
	 * Index of array is {@link Type#cacheIdTransiently}.
	 */
	private final Cachlet[] cachlets;

	ItemCache(final List<Type<?>> typesSorted, final ConnectProperties properties)
	{
		final ArrayList<Type<?>> types = new ArrayList<>(typesSorted.size());
		for(final Type<?> type : typesSorted)
			if(!type.isAbstract)
				types.add(type);

		final int l = types.size();

		final int[] weights = new int[l];
		int weightSum = 0;
		for(int i = 0; i<l; i++)
		{
			final Type<?> type = types.get(i);
			final CopeCacheWeight weightAnnotation = type.getAnnotation(CopeCacheWeight.class);
			final boolean noCache = type.isAnnotationPresent(CopeNoCache.class);
			final int weight = noCache ? 0 : (weightAnnotation!=null ? weightAnnotation.value() : 100);
			if(weight<0)
				throw new IllegalArgumentException("illegal CopeCacheWeight for type " + type.getID() + ", must not be negative, but was " + weight);
			weights[i] = weight;
			weightSum += weight;
		}

		final boolean enableStamps = properties.itemCacheStamps;
		cachlets = new Cachlet[l];
		final int limit = properties.getItemCacheLimit();
		for(int i=0; i<l; i++)
		{
			final Type<?> type = types.get(i);
			assert !type.isAbstract : type.id;
			assert type.cacheIdTransiently>=0 : String.valueOf(type.cacheIdTransiently) + '/' + type.id;
			assert type.cacheIdTransiently <l : String.valueOf(type.cacheIdTransiently) + '/' + type.id;
			assert type.cacheIdTransiently==i : String.valueOf(type.cacheIdTransiently) + '/' + type.id + '/' + i;

			final int iLimit = ratio(weights[i], limit, weightSum);
			cachlets[i] = (iLimit>0) ? new Cachlet(type, iLimit, enableStamps) : null;
		}
	}

	WrittenState getState(final Transaction tx, final Item item)
	{
		final Cachlet cachlet = cachlets[item.type.cacheIdTransiently];

		WrittenState state = null;
		if(cachlet!=null)
			state = cachlet.get(item.pk);

		if ( state==null )
		{
			state = tx.connect.database.load(tx.getConnection(), item);

			if(cachlet!=null)
				cachlet.put(state, tx.getCacheStamp());
		}

		return state;
	}

	/**
	 * @deprecated for unit tests only
	 */
	@Deprecated
	WrittenState getStateIfPresent(final Item item)
	{
		final Cachlet cachlet = cachlets[item.type.cacheIdTransiently];
		if(cachlet==null)
			return null;

		return cachlet.getInternal(item.pk);
	}

	void remove(final Item item)
	{
		final Cachlet cachlet = cachlets[item.type.cacheIdTransiently];
		if(cachlet!=null)
			cachlet.remove(item.pk);
	}

	void invalidate(final TLongHashSet[] invalidations)
	{
		final long stamp = ItemCacheStamp.get();
		for(int typeTransiently=0; typeTransiently<invalidations.length; typeTransiently++)
		{
			final TLongHashSet invalidatedPKs = invalidations[typeTransiently];
			if(invalidatedPKs!=null)
			{
				final Cachlet cachlet = cachlets[typeTransiently];
				if(cachlet!=null)
					cachlet.invalidate(invalidatedPKs, stamp);
			}
		}
	}

	void clear()
	{
		for(final Cachlet cachlet : cachlets)
		{
			if(cachlet!=null)
				cachlet.clear();
		}
	}

	void purgeStamps(final long untilStamp)
	{
		for(final Cachlet cachlet : cachlets)
		{
			if(cachlet!=null)
				cachlet.purgeStamps(untilStamp);
		}
	}

	/**
	 * @deprecated for unit tests only
	 */
	@Deprecated
	void clearStamps()
	{
		for(final Cachlet cachlet : cachlets)
		{
			if(cachlet!=null)
				cachlet.clearStamps();
		}
	}

	ItemCacheInfo[] getInfo(final List<Type<?>> typesInOriginalOrder)
	{
		final ArrayList<ItemCacheInfo> result = new ArrayList<>(cachlets.length);

		for(final Type<?> type : typesInOriginalOrder)
		{
			final Cachlet cachlet = cachlets[type.cacheIdTransiently];
			if(cachlet!=null)
				result.add(cachlet.getInfo());
		}

		return result.toArray(new ItemCacheInfo[result.size()]);
	}

	private static final class Cachlet
	{
		private final Type<?> type;
		private final int limit;
		private final TLongObjectHashMap<WrittenState> map;
		private final TLongLongHashMap stamps;

		private final VolatileLong hits = new VolatileLong();
		private final VolatileLong misses = new VolatileLong();
		private long concurrentLoads = 0;
		private int replacementRuns = 0;
		private int replacements = 0;
		private long lastReplacementRun = 0;
		private long invalidationsOrdered = 0;
		private long invalidationsDone = 0;
		private long stampsHits = 0;
		private long stampsPurged = 0;

		Cachlet(final Type<?> type, final int limit, final boolean enableStamps)
		{
			assert !type.isAbstract;
			assert limit>0;

			this.type = type;
			this.limit = limit;
			this.map = new TLongObjectHashMap<>();
			this.stamps = enableStamps ? new TLongLongHashMap() : null;
		}

		WrittenState get(final long pk)
		{
			final WrittenState result;
			synchronized(map)
			{
				result = map.get(pk);
			}

			if(result!=null)
			{
				result.notifyUsed();
				hits.inc();
			}
			else
				misses.inc();

			return result;
		}

		/**
		 * @deprecated for unit tests only
		 */
		@Deprecated
		WrittenState getInternal(final long pk)
		{
			synchronized(map)
			{
				return map.get(pk);
			}
		}

		void put(final WrittenState state, final long connectionStamp)
		{
			synchronized(map)
			{
				if(stamps!=null)
				{
					final long stamp = this.stamps.get(state.pk);
					if(stamp!=0 && stamp>=connectionStamp)
					{
						stampsHits++;
						return;
					}
				}

				if(map.put(state.pk, state)!=null)
					concurrentLoads++;

				// TODO use LRU with guava ComputingMap
				// http://guava-libraries.googlecode.com/svn/tags/release09/javadoc/com/google/common/collect/MapMaker.html#makeComputingMap%28com.google.common.base.Function%29
				final int mapSize = map.size();
				if(mapSize>=limit)
				{
					final long now = System.currentTimeMillis();
					long ageSum = 0;
					for(final TLongObjectIterator<WrittenState> i = map.iterator(); i.hasNext(); )
					{
						i.advance();
						final WrittenState currentState = i.value();
						final long currentLastUsage = currentState.getLastUsageMillis();
						ageSum+=(now-currentLastUsage);
					}
					final long age = ageSum / mapSize;
					final long ageLimit = (limit * age) / mapSize;
					final long timeLimit = now-ageLimit;
					for(final TLongObjectIterator<WrittenState> i = map.iterator(); i.hasNext(); )
					{
						i.advance();
						final WrittenState currentState = i.value();
						final long currentLastUsage = currentState.getLastUsageMillis();
						if(timeLimit>currentLastUsage)
							i.remove();
					}
					replacementRuns++;
					replacements += (mapSize - map.size());
					lastReplacementRun = now;
				}
			}
		}

		void remove(final long pk)
		{
			synchronized(map)
			{
				map.remove(pk);
			}
		}

		void invalidate(final TLongHashSet invalidatedPKs, final long stamp)
		{
			synchronized(map)
			{
				final int mapSizeBefore = map.size();

				// TODO implement and use a removeAll
				for(final TLongIterator i = invalidatedPKs.iterator(); i.hasNext(); )
				{
					final long pk = i.next();
					map.remove(pk);

					if(stamps!=null)
						stamps.put(pk, stamp);
				}

				invalidationsOrdered += invalidatedPKs.size();
				invalidationsDone    += (mapSizeBefore - map.size());
			}
		}

		void clear()
		{
			synchronized(map)
			{
				map.clear();
			}
		}

		void purgeStamps(final long untilStamp)
		{
			if(stamps!=null)
			{
				synchronized(map)
				{
					final int size = stamps.size();
					if(size==0)
						return;

					if(untilStamp==Long.MAX_VALUE)
					{
						stamps.clear();
						stampsPurged += size;
					}
					else
					{
						int purged = 0;
						for(final TLongLongIterator i = stamps.iterator(); i.hasNext(); )
						{
							i.advance();
							if(i.value()<untilStamp)
							{
								purged++;
								i.remove();
							}
						}
						if(purged>0)
							stampsPurged += purged;
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
			if(stamps!=null)
			{
				synchronized(map)
				{
					stamps.clear();
				}
			}
		}

		ItemCacheInfo getInfo()
		{
			final long now = System.currentTimeMillis();
			final int level;
			final long lastReplacementRun;
			long ageSum = 0;
			long ageMin = Long.MAX_VALUE;
			long ageMax = 0;
			final int stampsSize;

			synchronized(map)
			{
				level = map.size();
				lastReplacementRun = this.lastReplacementRun;
				for(final TLongObjectIterator<WrittenState> stateMapI = map.iterator(); stateMapI.hasNext(); )
				{
					stateMapI.advance();
					final WrittenState currentState = stateMapI.value();
					final long currentLastUsage = currentState.getLastUsageMillis();
					final long age = now-currentLastUsage;
					ageSum += age;
					if(ageMin>age)
						ageMin = age;
					if(ageMax<age)
						ageMax = age;
				}
				stampsSize = stamps!=null ? stamps.size() : 0;
			}

			return new ItemCacheInfo(
				type,
				limit, level,
				hits.get(), misses.get(),
				concurrentLoads,
				replacementRuns, replacements, (lastReplacementRun!=0 ? new Date(lastReplacementRun) : null),
				ageSum, ageMin, ageMax,
				invalidationsOrdered, invalidationsDone,
				stampsSize, stampsHits, stampsPurged
				);
		}
	}
}
