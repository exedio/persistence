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

import java.util.Collection;
import java.util.Iterator;
import java.util.TreeSet;

import org.apache.commons.collections.map.LRUMap;

import bak.pcj.map.IntKeyOpenHashMap;
import bak.pcj.set.IntOpenHashSet;

import com.exedio.cope.util.CacheInfo;
import com.exedio.cope.util.CacheQueryInfo;

final class Cache
{
	private final int[] mapSizeLimits;
	private final IntKeyOpenHashMap[] stateMaps;
	private final int[] hits, misses;
	private final MyLRUMap queryCaches;
	private int queryHits=0, queryMisses=0;
	private final boolean logQueryCache;
	
	Cache(final int[] mapSizeLimits, final int queryCacheSizeLimit, final boolean logQueryCache)
	{
		this.mapSizeLimits = mapSizeLimits;
		queryCaches = queryCacheSizeLimit>0 ? new MyLRUMap(queryCacheSizeLimit) : null;
		final int numberOfConcreteTypes = mapSizeLimits.length;
		stateMaps = new IntKeyOpenHashMap[numberOfConcreteTypes];
		for(int i=0; i<numberOfConcreteTypes; i++)
		{
			stateMaps[i] = (mapSizeLimits[i]>0) ? new IntKeyOpenHashMap() : null;
		}
		hits = new int[numberOfConcreteTypes];
		misses = new int[numberOfConcreteTypes];
		this.logQueryCache = logQueryCache;
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
		if(stateMap!=null)
		{
			synchronized (stateMap)
			{
				state = (PersistentState)stateMap.get( item.pk );
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
				final int mapSizeLimit = mapSizeLimits[item.type.transientNumber];
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
					System.out.println("cope cache cleanup "+item.type.id+": "+mapSize+"->"+newMapSize);
				
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
	
	boolean supportsQueryCaching()
	{
		return queryCaches!=null;
	}
	
	Collection search( Query query )
	{
		if ( queryCaches==null )
		{
			throw new RuntimeException( "search in cache must not be called if query caching is disabled" );
		}
		Query.Key key = new Query.Key( query );
		Collection result;
		synchronized ( queryCaches )
		{
			result = (Collection)queryCaches.get( key );
		}
		if ( result==null )
		{
			result = query.searchUncached();
			synchronized ( queryCaches )
			{
				queryCaches.put( key, result );				
			}
			if(logQueryCache)
				key.name = query.toString();
			
			queryMisses++;
		}
		else
		{
			if(logQueryCache)
			{
				final Query.Key originalKey;
				synchronized(queryCaches)
				{
					originalKey = (Query.Key)queryCaches.getKey(key);
				} 
				originalKey.hits++;
			}
			queryHits++;
		}
		
		return result;		
	}
	
	void invalidate( int transientTypeNumber, IntOpenHashSet invalidatedPKs )
	{
		final IntKeyOpenHashMap stateMap = getStateMap( transientTypeNumber );
		if(stateMap!=null)
		{
			synchronized ( stateMap )
			{
				stateMap.keySet().removeAll( invalidatedPKs );
			}
		}
		if ( queryCaches!=null )
		{
			synchronized ( queryCaches )
			{
				Iterator keys = queryCaches.keySet().iterator();
				while ( keys.hasNext() )
				{
					Query.Key key = (Query.Key)keys.next();
					if ( key.type.transientNumber==transientTypeNumber )
					{
						keys.remove();
					}
					else if ( key.joins!=null )
					{
						for ( Iterator iter = key.joins.iterator(); iter.hasNext(); )
						{
							Join nextJoin = (Join)iter.next();
							if ( nextJoin.type.transientNumber==transientTypeNumber )
							{
								keys.remove();
								break;
							}
						}
					}					
				}
			}
		}
	}

	void clear()
	{
		for ( int i=0; i<stateMaps.length; i++ )
		{
			final IntKeyOpenHashMap stateMap = getStateMap( i );
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
		assert concreteTypes.length!=stateMaps.length;
		
		final CacheInfo[] result = new CacheInfo[stateMaps.length];
		
		for(int i=0; i<stateMaps.length; i++ )
		{
			final long now = System.currentTimeMillis();
			final int numberOfItemsInCache;
			long ageSum = 0;
			long ageMin = Integer.MAX_VALUE;
			long ageMax = 0;

			final IntKeyOpenHashMap stateMap = getStateMap(i);
			if(stateMap!=null)
			{
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
			}
			else
				numberOfItemsInCache = 0;
			
			if(ageMin==Integer.MAX_VALUE)
				ageMin = 0;
			
			result[i] = new CacheInfo(concreteTypes[i], mapSizeLimits[i], numberOfItemsInCache, hits[i], misses[i], ageSum, ageMin, ageMax);
		}
		
		return result;
	}
	
	int[] getQueryInfo()
	{
		final int queriesInCache;
		
		if(queryCaches!=null)
		{
			synchronized(queryCaches)
			{
				queriesInCache = queryCaches.size();
			}
		}
		else
			queriesInCache = 0;
		
		return new int[]{queryHits, queryMisses, queriesInCache};
	}
	
	CacheQueryInfo[] getQueryHistogram()
	{
		if(!logQueryCache)
			return null;
		
		final TreeSet result = new TreeSet();
		
		if(queryCaches!=null)
		{
			synchronized(queryCaches)
			{
				for(Iterator i = queryCaches.keySet().iterator(); i.hasNext(); )
				{
					final Query.Key key = (Query.Key)i.next();
					result.add(new CacheQueryInfo(key.name, key.hits));
				}
			}
		}
		
		return (CacheQueryInfo[])result.toArray(new CacheQueryInfo[result.size()]);
	}
	
	private static final class MyLRUMap extends LRUMap
	{
		MyLRUMap(final int maxSize)
		{
			super(maxSize);
		}
		
		Object getKey(final Object key)
		{
			final HashEntry entry = getEntry(key);
			return (entry == null) ? null : entry.getKey();
		}
	}

}
