/*
 * Copyright (C) 2004-2005  exedio GmbH (www.exedio.com)
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

import java.util.Iterator;

import bak.pcj.map.IntKeyOpenHashMap;
import bak.pcj.set.IntOpenHashSet;

import com.exedio.cope.util.CacheInfo;

final class Cache
{
	private final int[] mapSizeLimits;
	private final IntKeyOpenHashMap[] stateMaps;
	private final int[] hits, misses;
	
	Cache(final int[] mapSizeLimits)
	{
		this.mapSizeLimits = mapSizeLimits;
		final int numberOfTypes = mapSizeLimits.length;
		stateMaps = new IntKeyOpenHashMap[numberOfTypes];
		for ( int i=0; i<numberOfTypes; i++ )
		{
			stateMaps[i] = new IntKeyOpenHashMap();
		}
		hits = new int[numberOfTypes];
		misses = new int[numberOfTypes];
	}
	
	private IntKeyOpenHashMap getStateMap( Type type )
	{
		return getStateMap( type.transientNumber );
	}
	
	private IntKeyOpenHashMap getStateMap( int transientTypeNumber )
	{
		return stateMaps[ transientTypeNumber ];
	}
	
	PersistentState getPersistentState( final Transaction connectionSource, final Item item )
	{
		PersistentState state;
		final IntKeyOpenHashMap stateMap = getStateMap( item.type );
		synchronized (stateMap)
		{
			state = (PersistentState)stateMap.get( item.pk );
		}

		if(state!=null)
			state.notifyUsed();
		
		boolean hit = true;
		
		if ( state==null )
		{
			state = new PersistentState( connectionSource.getConnection(), item );

			final int mapSizeLimit = mapSizeLimits[item.type.transientNumber];
			if(mapSizeLimit>0)
			{
				final Object oldValue;
				final int mapSize, newMapSize;
				synchronized (stateMap)
				{
					oldValue = stateMap.put( item.pk, state );
	
					mapSize = stateMap.size();
					if(mapSize>=mapSizeLimit)
					{
						final long now = System.currentTimeMillis();
						long ageSum = 0;
						for(Iterator i = stateMap.values().iterator(); i.hasNext(); )
						{
							final PersistentState currentState = (PersistentState)i.next();
							final long currentLastUsage = currentState.getLastUsageMillis();
							ageSum+=(now-currentLastUsage);
						}
						final long age = ageSum / mapSize;
						final long ageLimit = (mapSizeLimit * age) / mapSize;
						final long timeLimit = now-ageLimit;
						for(Iterator i = stateMap.values().iterator(); i.hasNext(); )
						{
							final PersistentState currentState = (PersistentState)i.next();
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
					System.out.println("cope cache cleanup "+item.type.getID()+": "+mapSize+"->"+newMapSize);
				
				if ( oldValue!=null )
				{
					System.out.println("warning: duplicate computation of state "+item.getCopeID());
				}
			}
			hit = false;
		}
		
		(hit ? hits : misses)[item.type.transientNumber]++;
		
		return state;
	}
	
	void invalidate( int transientTypeNumber, IntOpenHashSet invalidatedPKs )
	{
		final IntKeyOpenHashMap stateMap = getStateMap( transientTypeNumber );
		synchronized ( stateMap )
		{
			stateMap.keySet().removeAll( invalidatedPKs );
		}
	}

	void clear()
	{
		for ( int i=0; i<stateMaps.length; i++ )
		{
			final IntKeyOpenHashMap stateMap = getStateMap( i );
			synchronized ( stateMap )
			{
				stateMap.clear();
			}
		}
	}

	CacheInfo[] getInfo(final Type[] types)
	{
		final CacheInfo[] result = new CacheInfo[stateMaps.length];
		
		for(int i=0; i<stateMaps.length; i++ )
		{
			final IntKeyOpenHashMap stateMap = getStateMap(i);
			final long now = System.currentTimeMillis();
			final int numberOfItemsInCache;
			long ageSum = 0;
			long ageMin = Integer.MAX_VALUE;
			long ageMax = 0;

			synchronized(stateMap)
			{
				numberOfItemsInCache = stateMap.size();
				for(Iterator stateMapI = stateMap.values().iterator(); stateMapI.hasNext(); )
				{
					final PersistentState currentState = (PersistentState)stateMapI.next();
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
			
			result[i] = new CacheInfo(types[i], mapSizeLimits[i], numberOfItemsInCache, hits[i], misses[i], ageSum, ageMin, ageMax);
		}
		
		return result;
	}
}
