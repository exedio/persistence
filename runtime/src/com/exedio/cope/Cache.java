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

import gnu.trove.TIntArrayList;
import gnu.trove.TIntHashSet;
import gnu.trove.TIntIterator;
import gnu.trove.TIntObjectHashMap;
import gnu.trove.TIntObjectIterator;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import com.exedio.cope.util.CacheInfo;
import com.exedio.cope.util.CacheQueryInfo;

final class Cache
{
	private final int[] mapSizeLimits;
	private final TIntObjectHashMap<PersistentState>[] stateMaps;
	private final int[] hits, misses;
	private final LRUMap<Query.Key, ArrayList<Object>> queries;
	private volatile int queryHits = 0, queryMisses = 0;
	private final boolean queryHistogram;
	
	Cache(final int[] mapSizeLimits, final int queryCacheSizeLimit, final boolean queryHistogram)
	{
		this.mapSizeLimits = mapSizeLimits;
		queries = queryCacheSizeLimit>0 ? new LRUMap<Query.Key, ArrayList<Object>>(queryCacheSizeLimit) : null;
		final int numberOfConcreteTypes = mapSizeLimits.length;
		stateMaps = Transaction.cast(new TIntObjectHashMap[numberOfConcreteTypes]);
		for(int i=0; i<numberOfConcreteTypes; i++)
		{
			stateMaps[i] = (mapSizeLimits[i]>0) ? new TIntObjectHashMap<PersistentState>() : null;
		}
		hits = new int[numberOfConcreteTypes];
		misses = new int[numberOfConcreteTypes];
		this.queryHistogram = queryHistogram;
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
	
	boolean supportsQueryCaching()
	{
		return queries!=null;
	}
	
	ArrayList<Object> search(final Transaction transaction, final Query<?> query, final boolean doCountOnly)
	{
		if(queries==null)
		{
			throw new RuntimeException( "search in cache must not be called if query caching is disabled" );
		}
		final Query.Key key = new Query.Key(query, doCountOnly);
		ArrayList<Object> result;
		synchronized(queries)
		{
			result = queries.get(key);
		}
		if ( result==null )
		{
			result = query.searchUncached(transaction, doCountOnly);
			key.prepareForPut(query);
			synchronized(queries)
			{
				queries.put(key, result);
			}
			queryMisses++;
		}
		else
		{
			if(queryHistogram || query.makeStatementInfo)
			{
				final Query.Key originalKey;
				synchronized(queries)
				{
					originalKey = queries.getKeyIfHackSucceeded(key);
				}
				if(originalKey!=null)
					originalKey.hits++;
				
				if(query.makeStatementInfo)
					query.addStatementInfo(new StatementInfo("query cache hit #" + originalKey.hits + " for " + originalKey.getText()));
			}
			queryHits++;
		}
		
		return result;
	}
	
	void invalidate(final TIntHashSet[] invalidations)
	{
		final TIntArrayList invalidatedTypesTransientlyList = new TIntArrayList();

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
				invalidatedTypesTransientlyList.add(typeTransiently);
			}
		}
		
		if(queries!=null && !invalidatedTypesTransientlyList.isEmpty())
		{
			final int[] invalidatedTypesTransiently = invalidatedTypesTransientlyList.toNativeArray();
			
			synchronized(queries)
			{
				final Iterator<Query.Key> keys = queries.keySet().iterator();
				while(keys.hasNext())
				{
					final Query.Key key = keys.next();
					query: for(final int queryTypeTransiently : key.invalidationTypesTransiently)
					{
						for(final int invalidatedTypeTransiently : invalidatedTypesTransiently)
						if(queryTypeTransiently==invalidatedTypeTransiently)
						{
							keys.remove();
							break query;
						}
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
		if(queries!=null)
		{
			synchronized(queries)
			{
				queries.clear();
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
	
	int[] getQueryInfo()
	{
		final int queriesInCache;
		
		if(queries!=null)
		{
			synchronized(queries)
			{
				queriesInCache = queries.size();
			}
		}
		else
			queriesInCache = 0;
		
		return new int[]{queryHits, queryMisses, queriesInCache};
	}
	
	CacheQueryInfo[] getQueryHistogram()
	{
		if(queries==null)
			return new CacheQueryInfo[0];
		
		final Query.Key[] keys;
		final ArrayList[] values;
		synchronized(queries)
		{
			keys = queries.keySet().toArray(new Query.Key[queries.size()]);
			values = queries.values().toArray(new ArrayList[queries.size()]);
		}

		final CacheQueryInfo[] result = new CacheQueryInfo[keys.length];
		int i = result.length-1;
		int j = 0;
		for(final Query.Key key : keys)
			result[i--] = new CacheQueryInfo(key.getText(), values[j++].size(), key.hits);
		
		return result;
	}
	
	private static final class LRUMap<K,V> extends LinkedHashMap<K,V>
	{
		private static final long serialVersionUID = 19641264861283476l;
		
		private final int maxSize;
		
		LRUMap(final int maxSize)
		{
			super(maxSize, 0.75f/*DEFAULT_LOAD_FACTOR*/, true);
			this.maxSize = maxSize;
		}
		
		private static Method getEntry = null;

		static
		{
			try
			{
				final Method m = HashMap.class.getDeclaredMethod("getEntry", Object.class);
				m.setAccessible(true);
				getEntry = m;
			}
			catch(Exception e)
			{
				System.out.println("Accessing getEntry method failed, no query histograms available: " + e.getMessage());;
			}
		}
		
		@SuppressWarnings("unchecked") // OK: reflection does not support generics
		K getKeyIfHackSucceeded(final K key)
		{
			if(getEntry==null)
				return null;
			
			try
			{
				final Map.Entry e = (Map.Entry)getEntry.invoke(this, key);
				return (K)e.getKey();
			}
			catch(InvocationTargetException e)
			{
				throw new RuntimeException(e);
			}
			catch(IllegalAccessException e)
			{
				throw new RuntimeException(e);
			}
		}
		
		@Override
		protected boolean removeEldestEntry(final Map.Entry<K,V> eldest)
		{
			//System.out.println("-----eldest("+size()+"):"+eldest.getKey());
			return size() > maxSize;
		}
	}

}
