/*
 * Copyright (C) 2004-2008  exedio GmbH (www.exedio.com)
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

import gnu.trove.TIntHashSet;
import gnu.trove.TIntIterator;
import gnu.trove.TIntObjectHashMap;
import gnu.trove.TIntObjectIterator;

import java.util.ArrayList;
import java.util.Date;

import com.exedio.cope.util.CacheInfo;

final class ItemCache
{
	/**
	 * Index of array is {@link Type#idTransiently}.
	 */
	private final Cachlet[] cachlets;
	
	ItemCache(final Type[] types, final int limit)
	{
		final int l = types.length;
		
		final int[] weights = new int[l];
		int weightSum = 0;
		for(int i = 0; i<l; i++)
		{
			final Type<?> type = types[i];
			final CopeCacheWeight weightAnnotation = type.getAnnotation(CopeCacheWeight.class);
			final int weight = weightAnnotation!=null ? weightAnnotation.value() : 100;
			if(weight<0)
				throw new IllegalArgumentException("illegal CopeCacheWeight for type " + type.getID() + ", must not be negative, but was " + weight);
			weights[i] = weight;
			weightSum += weight;
		}
		
		final int[] limits = new int[l];
		for(int i = 0; i<l; i++)
			limits[i] = weights[i] * limit / weightSum;
		
		cachlets = new Cachlet[l];
		for(int i=0; i<l; i++)
			cachlets[i] = (limits[i]>0) ? new Cachlet(types[i], limits[i]) : null;
	}
	
	WrittenState getState(final Transaction connectionSource, final Item item)
	{
		final Cachlet cachlet = cachlets[item.type.idTransiently];

		WrittenState state = null;
		if(cachlet!=null)
			state = cachlet.get(item.pk);
		
		if ( state==null )
		{
			state = new WrittenState(connectionSource.getConnection(), item);

			if(cachlet!=null)
				cachlet.put(state);
		}
		
		return state;
	}
	
	void invalidate(final TIntHashSet[] invalidations)
	{
		for(int typeTransiently=0; typeTransiently<invalidations.length; typeTransiently++)
		{
			final TIntHashSet invalidatedPKs = invalidations[typeTransiently];
			if(invalidatedPKs!=null)
			{
				final Cachlet cachlet = cachlets[typeTransiently];
				if(cachlet!=null)
					cachlet.invalidate(invalidatedPKs);
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

	CacheInfo[] getInfo()
	{
		final ArrayList<CacheInfo> result = new ArrayList<CacheInfo>(cachlets.length);
		
		for(int i = 0; i<cachlets.length; i++)
		{
			final Cachlet cachlet = cachlets[i];
			if(cachlet!=null)
				result.add(cachlet.getInfo());
		}
		
		return result.toArray(new CacheInfo[result.size()]);
	}
	
	private static final class Cachlet
	{
		private final Type type;
		private final int limit;
		private final TIntObjectHashMap<WrittenState> map;
		private volatile long hits = 0;
		private volatile long misses = 0;
		private int numberOfCleanups = 0;
		private int itemsCleanedUp = 0;
		private long lastCleanup = 0;

		Cachlet(final Type type, final int limit)
		{
			assert !type.isAbstract;
			assert limit>0;
			
			this.type = type;
			this.limit = limit;
			this.map = new TIntObjectHashMap<WrittenState>();
		}
		
		WrittenState get(final int pk)
		{
			final WrittenState result;
			synchronized(map)
			{
				result = map.get(pk);
			}

			if(result!=null)
			{
				result.notifyUsed();
				hits++;
			}
			else
				misses++;

			return result;
		}
		
		void put(final WrittenState state)
		{
			final Object oldValue;
			synchronized(map)
			{
				oldValue = map.put(state.pk, state);

				// TODO use a LRU map instead
				final int mapSize = map.size();
				if(mapSize>=limit)
				{
					final long now = System.currentTimeMillis();
					long ageSum = 0;
					for(TIntObjectIterator<WrittenState> i = map.iterator(); i.hasNext(); )
					{
						i.advance();
						final WrittenState currentState = i.value();
						final long currentLastUsage = currentState.getLastUsageMillis();
						ageSum+=(now-currentLastUsage);
					}
					final long age = ageSum / mapSize;
					final long ageLimit = (limit * age) / mapSize;
					final long timeLimit = now-ageLimit;
					for(TIntObjectIterator<WrittenState> i = map.iterator(); i.hasNext(); )
					{
						i.advance();
						final WrittenState currentState = i.value();
						final long currentLastUsage = currentState.getLastUsageMillis();
						if(timeLimit>currentLastUsage)
							i.remove();
					}
					numberOfCleanups++;
					itemsCleanedUp += (mapSize - map.size());
					lastCleanup = now;
				}
			}
			
			if(oldValue!=null)
				System.out.println("warning: duplicate computation of state " + type + '.' + state.pk);
		}
		
		void invalidate(final TIntHashSet invalidatedPKs)
		{
			synchronized(map)
			{
				// TODO implement and use a removeAll
				for(TIntIterator i = invalidatedPKs.iterator(); i.hasNext(); )
					map.remove(i.next());
			}
		}
		
		void clear()
		{
			synchronized(map)
			{
				map.clear();
			}
		}
		
		CacheInfo getInfo()
		{
			final long now = System.currentTimeMillis();
			final int level;
			final long lastCleanup;
			long ageSum = 0;
			long ageMin = Long.MAX_VALUE;
			long ageMax = 0;

			synchronized(map)
			{
				level = map.size();
				lastCleanup = this.lastCleanup;
				for(final TIntObjectIterator<WrittenState> stateMapI = map.iterator(); stateMapI.hasNext(); )
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
			}
			
			return new CacheInfo(
				type,
				limit, level,
				hits, misses,
				numberOfCleanups, itemsCleanedUp, (lastCleanup!=0 ? new Date(lastCleanup) : null),
				ageSum, ageMin, ageMax);
		}
	}
}
