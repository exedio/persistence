/*
 * Copyright (C) 2004-2006  exedio GmbH (www.exedio.com)
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
	private final MyLRUMap queryCachesRaw;
	private final Map<Query.Key, List<?>> queryCaches;
	private int queryHits=0, queryMisses=0;
	private final boolean queryHistogram;
	
	Cache(final int[] mapSizeLimits, final int queryCacheSizeLimit, final boolean queryHistogram)
	{
		this.mapSizeLimits = mapSizeLimits;
		queryCachesRaw = queryCacheSizeLimit>0 ? new MyLRUMap(queryCacheSizeLimit) : null;
		queryCaches = castQueryCaches(queryCachesRaw);
		final int numberOfConcreteTypes = mapSizeLimits.length;
		stateMaps = new IntKeyOpenHashMap[numberOfConcreteTypes];
		for(int i=0; i<numberOfConcreteTypes; i++)
		{
			stateMaps[i] = (mapSizeLimits[i]>0) ? new IntKeyOpenHashMap() : null;
		}
		hits = new int[numberOfConcreteTypes];
		misses = new int[numberOfConcreteTypes];
		this.queryHistogram = queryHistogram;
	}
	
	@SuppressWarnings("unchecked") // TODO commons-collections do not support generics
	private static final Map<Query.Key, List<?>> castQueryCaches(final MyLRUMap m)
	{
		return (Map<Query.Key, List<?>>)m;
	}
	
	private IntKeyOpenHashMap getStateMap( Type type )
	{
		return stateMaps[type.transientNumber];
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
	
	<R> List<R> search(final Query<R> query)
	{
		if ( queryCaches==null )
		{
			throw new RuntimeException( "search in cache must not be called if query caching is disabled" );
		}
		Query.Key key = new Query.Key( query );
		List<R> result;
		synchronized ( queryCaches )
		{
			result = Cache.<R>castQL(queryCaches.get(key));
		}
		if ( result==null )
		{
			result = query.searchUncached();
			synchronized ( queryCaches )
			{
				queryCaches.put( key, result );				
			}
			queryMisses++;
		}
		else
		{
			if(queryHistogram || query.makeStatementInfo)
			{
				final Query.Key originalKey;
				synchronized(queryCaches)
				{
					originalKey = (Query.Key)queryCachesRaw.getKey(key);
				}
				originalKey.hits++;
				
				if(query.makeStatementInfo)
					query.addStatementInfo(new StatementInfo("from query cache, hit #" + originalKey.hits));
			}
			queryHits++;
		}
		
		return result;		
	}
	
	@SuppressWarnings("unchecked") // OK: generic maps cannot ensure fit between key and value
	private static final <R> List<R> castQL(final List l)
	{
		return (List<R>)l;
	}
	
	void invalidate(final IntOpenHashSet[] invalidations)
	{
		for(int transientTypeNumber=0; transientTypeNumber<invalidations.length; transientTypeNumber++)
		{
			final IntOpenHashSet invalidatedPKs = invalidations[transientTypeNumber];
			if(invalidatedPKs!=null)
				invalidate(transientTypeNumber, invalidatedPKs);
		}
	}
	
	private void invalidate(final int transientTypeNumber, final IntOpenHashSet invalidatedPKs)
	{
		final IntKeyOpenHashMap stateMap = stateMaps[transientTypeNumber];
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
				final Iterator<Query.Key> keys = queryCaches.keySet().iterator();
				while ( keys.hasNext() )
				{
					final Query.Key key = keys.next();
					if(matchesType(key.type, transientTypeNumber))
					{
						keys.remove();
					}
					else if ( key.joins!=null )
					{
						for(final Join nextJoin : key.joins)
						{
							if(matchesType(nextJoin.type, transientTypeNumber))
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
	
	private static final boolean matchesType(final Type<?> queryType, final int invalidatedTypeNumber)
	{
		for(final Type t : queryType.getTypesOfInstances())
			if(t.transientNumber == invalidatedTypeNumber)
				return true;
		return false;
	}

	void clear()
	{
		for(final IntKeyOpenHashMap stateMap : stateMaps)
		{
			if(stateMap!=null)
			{
				synchronized ( stateMap )
				{
					stateMap.clear();
				}
			}
		}
		if(queryCaches!=null)
		{
			synchronized(queryCaches)
			{
				queryCaches.clear();
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
			long ageMin = Integer.MAX_VALUE;
			long ageMax = 0;

			final IntKeyOpenHashMap stateMap = stateMaps[i];
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
		final ArrayList<Query.Key> unsortedResult = new ArrayList<Query.Key>();
		
		if(queryCaches!=null)
		{
			synchronized(queryCaches)
			{
				unsortedResult.addAll(queryCaches.keySet());
				// NOTE:
				// It is important to keep Key.toString()
				// out of the synchronized block.
			}
		}

		final TreeSet<CacheQueryInfo> result = new TreeSet<CacheQueryInfo>();
		for(final Query.Key key : unsortedResult)
			result.add(new CacheQueryInfo(key.toString(), key.hits));
		
		return result.toArray(new CacheQueryInfo[result.size()]);
	}
	
	private static final class MyLRUMap extends LRUMap
	{
		private static final long serialVersionUID = 19641264861283476l;
		
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
