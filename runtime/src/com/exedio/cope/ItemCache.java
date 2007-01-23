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
	
	ItemCache(final int[] mapSizeLimits)
	{
		final int numberOfConcreteTypes = mapSizeLimits.length;
		cachlets = new Cachlet[numberOfConcreteTypes];
		for(int i=0; i<numberOfConcreteTypes; i++)
			cachlets[i] = (mapSizeLimits[i]>0) ? new Cachlet(mapSizeLimits[i]) : null;
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
				cachlet.put(state, item);
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

	CacheInfo[] getInfo(final Type[] concreteTypes)
	{
		assert concreteTypes.length==cachlets.length;
		
		final ArrayList<CacheInfo> result = new ArrayList<CacheInfo>(cachlets.length);
		
		for(int i = 0; i<cachlets.length; i++)
		{
			final Cachlet cachlet = cachlets[i];
			if(cachlet!=null)
				result.add(cachlet.getInfo(concreteTypes[i]));
		}
		
		return result.toArray(new CacheInfo[result.size()]);
	}
	
	private static final class Cachlet
	{
		private final int mapSizeLimit;
		private final TIntObjectHashMap<PersistentState> stateMap;
		private volatile int hits = 0, misses = 0;

		Cachlet(final int mapSizeLimit)
		{
			this.mapSizeLimit = mapSizeLimit;
			this.stateMap = new TIntObjectHashMap<PersistentState>();
		}
		
		PersistentState get(final int pk)
		{
			final PersistentState result;
			synchronized(stateMap)
			{
				result = stateMap.get(pk);
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
		
		void put(final PersistentState state, final Item item)
		{
			final Object oldValue;
			final int mapSize, newMapSize;
			synchronized(stateMap)
			{
				oldValue = stateMap.put(item.pk, state);

				// TODO use a LRU map instead
				mapSize = stateMap.size();
				if(mapSize>=mapSizeLimit)
				{
					final long now = System.currentTimeMillis();
					long ageSum = 0;
					for(TIntObjectIterator<PersistentState> i = stateMap.iterator(); i.hasNext(); )
					{
						i.advance();
						final PersistentState currentState = i.value();
						final long currentLastUsage = currentState.getLastUsageMillis();
						ageSum+=(now-currentLastUsage);
					}
					final long age = ageSum / mapSize;
					final long ageLimit = (mapSizeLimit * age) / mapSize;
					final long timeLimit = now-ageLimit;
					for(TIntObjectIterator<PersistentState> i = stateMap.iterator(); i.hasNext(); )
					{
						i.advance();
						final PersistentState currentState = i.value();
						final long currentLastUsage = currentState.getLastUsageMillis();
						if(timeLimit>currentLastUsage)
							i.remove();
					}
					newMapSize = stateMap.size();
				}
				else
					newMapSize = -1;
			}
			
			// logging must be outside synchronized block
			if(newMapSize>=0)
				System.out.println("cope cache cleanup "+item.type.id+": "+mapSize+"->"+newMapSize);
			
			if(oldValue!=null)
				System.out.println("warning: duplicate computation of state "+item.getCopeID());
		}
		
		void invalidate(final TIntHashSet invalidatedPKs)
		{
			synchronized(stateMap)
			{
				// TODO implement and use a removeAll
				for(TIntIterator i = invalidatedPKs.iterator(); i.hasNext(); )
					stateMap.remove(i.next());
			}
		}
		
		void clear()
		{
			synchronized(stateMap)
			{
				stateMap.clear();
			}
		}
		
		CacheInfo getInfo(final Type type)
		{
			final long now = System.currentTimeMillis();
			final int numberOfItemsInCache;
			long ageSum = 0;
			long ageMin = Long.MAX_VALUE;
			long ageMax = 0;

			synchronized(stateMap)
			{
				numberOfItemsInCache = stateMap.size();
				for(final TIntObjectIterator<PersistentState> stateMapI = stateMap.iterator(); stateMapI.hasNext(); )
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

			return new CacheInfo(type, mapSizeLimit, numberOfItemsInCache, hits, misses, ageSum, ageMin, ageMax);
		}
	}
}
