/*
 * Copyright (C) 2004-2007  exedio GmbH (www.exedio.com)
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

import com.exedio.cope.util.CacheInfo;

final class ItemCache
{
	private final Cachlet[] cachlets;
	
	ItemCache(final Type[] types, final int[] limits)
	{
		assert types.length==limits.length;
		
		final int l = limits.length;
		cachlets = new Cachlet[l];
		for(int i=0; i<l; i++)
			cachlets[i] = (limits[i]>0) ? new Cachlet(types[i], limits[i]) : null;
	}
	
	PersistentState getPersistentState( final Transaction connectionSource, final Item item )
	{
		final Cachlet cachlet = cachlets[item.type.idTransiently];

		PersistentState state = null;
		if(cachlet!=null)
			state = cachlet.get(item.pk);
		
		if ( state==null )
		{
			state = new PersistentState( connectionSource.getConnection(), item );

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
		private final TIntObjectHashMap<PersistentState> map;
		private volatile int hits = 0, misses = 0;

		Cachlet(final Type type, final int limit)
		{
			assert !type.isAbstract;
			
			this.type = type;
			this.limit = limit;
			this.map = new TIntObjectHashMap<PersistentState>();
		}
		
		PersistentState get(final int pk)
		{
			final PersistentState result;
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
		
		void put(final PersistentState state)
		{
			final Object oldValue;
			final int mapSize, newMapSize;
			synchronized(map)
			{
				oldValue = map.put(state.pk, state);

				// TODO use a LRU map instead
				mapSize = map.size();
				if(mapSize>=limit)
				{
					final long now = System.currentTimeMillis();
					long ageSum = 0;
					for(TIntObjectIterator<PersistentState> i = map.iterator(); i.hasNext(); )
					{
						i.advance();
						final PersistentState currentState = i.value();
						final long currentLastUsage = currentState.getLastUsageMillis();
						ageSum+=(now-currentLastUsage);
					}
					final long age = ageSum / mapSize;
					final long ageLimit = (limit * age) / mapSize;
					final long timeLimit = now-ageLimit;
					for(TIntObjectIterator<PersistentState> i = map.iterator(); i.hasNext(); )
					{
						i.advance();
						final PersistentState currentState = i.value();
						final long currentLastUsage = currentState.getLastUsageMillis();
						if(timeLimit>currentLastUsage)
							i.remove();
					}
					newMapSize = map.size();
				}
				else
					newMapSize = -1;
			}
			
			// logging must be outside synchronized block
			if(newMapSize>=0)
				System.out.println("cope cache cleanup " + type + ": " + mapSize + "->" + newMapSize);
			
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
			long ageSum = 0;
			long ageMin = Long.MAX_VALUE;
			long ageMax = 0;

			synchronized(map)
			{
				level = map.size();
				for(final TIntObjectIterator<PersistentState> stateMapI = map.iterator(); stateMapI.hasNext(); )
				{
					stateMapI.advance();
					final PersistentState currentState = stateMapI.value();
					final long currentLastUsage = currentState.getLastUsageMillis();
					final long age = now-currentLastUsage;
					ageSum += age;
					if(ageMin>age)
						ageMin = age;
					if(ageMax<age)
						ageMax = age;
				}
			}
			
			if(ageMin==Integer.MAX_VALUE)
				ageMin = 0;

			return new CacheInfo(type, limit, level, hits, misses, ageSum, ageMin, ageMax);
		}
	}
}
