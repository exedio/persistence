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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import com.exedio.cope.util.CacheQueryInfo;

final class QueryCache
{
	private final LRUMap<Query.Key, ArrayList<Object>> queries;
	private volatile int hits = 0, misses = 0;
	private final boolean histogram;

	QueryCache(final int limit, final boolean histogram)
	{
		this.queries = limit>0 ? new LRUMap<Query.Key, ArrayList<Object>>(limit) : null;
		this.histogram = histogram;
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
			misses++;
		}
		else
		{
			if(histogram || query.makeStatementInfo)
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
			hits++;
		}
		
		return result;
	}
	
	boolean supportsQueryCaching()
	{
		return queries!=null;
	}
	
	void invalidate(final TIntHashSet[] invalidations)
	{
		final TIntArrayList invalidatedTypesTransientlyList = new TIntArrayList();

		for(int typeTransiently=0; typeTransiently<invalidations.length; typeTransiently++)
			if(invalidations[typeTransiently]!=null)
				invalidatedTypesTransientlyList.add(typeTransiently);
		
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
		if(queries!=null)
		{
			synchronized(queries)
			{
				queries.clear();
			}
		}
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
		
		return new int[]{hits, misses, queriesInCache};
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
