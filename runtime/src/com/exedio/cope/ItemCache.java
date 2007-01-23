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

import com.exedio.cope.util.CacheInfo;

final class ItemCache
{
	private final int[] mapSizeLimits;
	private final TIntObjectHashMap<PersistentState>[] stateMaps;
	private final int[] hits, misses;
	
	ItemCache(final int[] mapSizeLimits)
	{
		this.mapSizeLimits = mapSizeLimits;
		final int numberOfConcreteTypes = mapSizeLimits.length;
		stateMaps = Transaction.cast(new TIntObjectHashMap[numberOfConcreteTypes]);
		for(int i=0; i<numberOfConcreteTypes; i++)
		{
			stateMaps[i] = (mapSizeLimits[i]>0) ? new TIntObjectHashMap<PersistentState>() : null;
		}
		hits = new int[numberOfConcreteTypes];
		misses = new int[numberOfConcreteTypes];
	}
	
	private TIntObjectHashMap<PersistentState> getStateMap(final Type type)
	{
		return stateMaps[type.idTransiently];
	}
	
	PersistentState getPersistentState( final Transaction connectionSource, final Item item )
	{
		PersistentState state;
		final TIntObjectHashMap<PersistentState> stateMap = getStateMap(item.type);
		if(stateMap!=null)
		{
			synchronized (stateMap)
			{
				state = stateMap.get(item.pk);
			}

			if(state!=null)
				state.notifyUsed();
		}
		else
			state = null;
		
		boolean hit = true;
		
		if ( state==null )
		{
			state = new PersistentState( connectionSource.getConnection(), item );

			if(stateMap!=null)
			{
				final int mapSizeLimit = mapSizeLimits[item.type.idTransiently];
				final Object oldValue;
				final int mapSize, newMapSize;
				synchronized (stateMap)
				{
					oldValue = stateMap.put( item.pk, state );

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
				
				if ( oldValue!=null )
				{
					System.out.println("warning: duplicate computation of state "+item.getCopeID());
				}
			}
			hit = false;
		}
		
		(hit ? hits : misses)[item.type.idTransiently]++;
		
		return state;
	}
	
	void invalidate(final TIntHashSet[] invalidations)
	{
		for(int typeTransiently=0; typeTransiently<invalidations.length; typeTransiently++)
		{
			final TIntHashSet invalidatedPKs = invalidations[typeTransiently];
			if(invalidatedPKs!=null)
			{
				final TIntObjectHashMap<PersistentState> stateMap = stateMaps[typeTransiently];
				if(stateMap!=null)
				{
					synchronized(stateMap)
					{
						for(TIntIterator i = invalidatedPKs.iterator(); i.hasNext(); )
							stateMap.remove(i.next());
					}
				}
			}
		}
	}
	
	void clear()
	{
		for(final TIntObjectHashMap<PersistentState> stateMap : stateMaps)
		{
			if(stateMap!=null)
			{
				synchronized ( stateMap )
				{
					stateMap.clear();
				}
			}
		}
	}

	CacheInfo[] getInfo(final Type[] concreteTypes)
	{
		assert concreteTypes.length==stateMaps.length;
		
		final CacheInfo[] result = new CacheInfo[stateMaps.length];
		
		for(int i=0; i<stateMaps.length; i++)
		{
			final long now = System.currentTimeMillis();
			final int numberOfItemsInCache;
			long ageSum = 0;
			long ageMin = Long.MAX_VALUE;
			long ageMax = 0;

			final TIntObjectHashMap<PersistentState> stateMap = stateMaps[i];
			if(stateMap!=null)
			{
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
			}
			else
				numberOfItemsInCache = 0;
			
			if(ageMin==Integer.MAX_VALUE)
				ageMin = 0;
			
			result[i] = new CacheInfo(concreteTypes[i], mapSizeLimits[i], numberOfItemsInCache, hits[i], misses[i], ageSum, ageMin, ageMax);
		}
		
		return result;
	}
}
