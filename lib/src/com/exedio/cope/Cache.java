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
import bak.pcj.set.IntSet;

import com.exedio.cope.util.CacheInfo;

final class Cache
{
	private final IntKeyOpenHashMap[] stateMaps;
	private final int[] hits, misses;
	
	Cache( int numberOfTypes )
	{
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
			final Object oldValue;
			
			synchronized (stateMap)
			{
				oldValue = stateMap.put( item.pk, state );

				final int mapSize = stateMap.size();
				final int mapSizeLimit = 2000;
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
					System.out.println("cleanup "+item.type.getID()+": "+mapSize+"->"+stateMap.size()); // TODO move outside synchronized block !!!
				}
			}
			if ( oldValue!=null )
			{
				System.out.println("warning: duplicate computation of state "+item.getCopeID());
			}
			hit = false;
		}
		
		(hit ? hits : misses)[item.type.transientNumber]++;
		
		return state;
	}
	
	void invalidate( int transientTypeNumber, IntSet invalidatedPKs )
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
			final int numberOfItemsInCache;

			synchronized(stateMap)
			{
				numberOfItemsInCache = stateMap.size();
			}
			
			result[i] = new CacheInfo(types[i], numberOfItemsInCache, hits[i], misses[i]);
		}
		
		return result;
	}
}
