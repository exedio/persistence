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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.exedio.cope.util.CacheQueryInfo;

final class QueryCache
{
	private final LRUMap map;
	private volatile int hits = 0, misses = 0;

	QueryCache(final int limit)
	{
		this.map = limit>0 ? new LRUMap(limit) : null;
	}
	
	ArrayList<Object> search(final Transaction transaction, final Query<?> query, final boolean doCountOnly)
	{
		if(map==null)
		{
			throw new RuntimeException( "search in cache must not be called if query caching is disabled" );
		}
		final Key key = new Key(query, doCountOnly);
		Value result;
		synchronized(map)
		{
			result = map.get(key);
		}
		if ( result==null )
		{
			result = new Value(query, query.searchUncached(transaction, doCountOnly));
			synchronized(map)
			{
				map.put(key, result);
			}
			misses++;
		}
		else
		{
			hits++;
			result.hits++;
			
			final List<QueryInfo> queryInfos = transaction.queryInfos;
			if(queryInfos!=null)
				queryInfos.add(new QueryInfo("query cache hit #" + result.hits + " for " + key.getText()));
		}
		
		return result.list;
	}
	
	boolean isEnabled()
	{
		return map!=null;
	}
	
	void invalidate(final TIntHashSet[] invalidations)
	{
		final TIntArrayList invalidatedTypesTransientlyList = new TIntArrayList();

		for(int typeTransiently=0; typeTransiently<invalidations.length; typeTransiently++)
			if(invalidations[typeTransiently]!=null)
				invalidatedTypesTransientlyList.add(typeTransiently);
		
		if(map!=null && !invalidatedTypesTransientlyList.isEmpty())
		{
			final int[] invalidatedTypesTransiently = invalidatedTypesTransientlyList.toNativeArray();
			
			synchronized(map)
			{
				final Iterator<Value> values = map.values().iterator();
				while(values.hasNext())
				{
					final Value value = values.next();
					query: for(final int queryTypeTransiently : value.invalidationTypesTransiently)
					{
						for(final int invalidatedTypeTransiently : invalidatedTypesTransiently)
						if(queryTypeTransiently==invalidatedTypeTransiently)
						{
							values.remove();
							break query;
						}
					}
				}
			}
		}
	}
	
	void clear()
	{
		if(map!=null)
		{
			synchronized(map)
			{
				map.clear();
			}
		}
	}
	
	int[] getQueryInfo()
	{
		final int level;
		
		if(map!=null)
		{
			synchronized(map)
			{
				level = map.size();
			}
		}
		else
			level = 0;
		
		return new int[]{hits, misses, level};
	}
	
	CacheQueryInfo[] getHistogram()
	{
		if(map==null)
			return new CacheQueryInfo[0];
		
		final Key[] keys;
		final Value[] values;
		synchronized(map)
		{
			final int size = map.size();
			keys   = map.keySet().toArray(new Key[size]);
			values = map.values().toArray(new Value[size]);
		}

		final CacheQueryInfo[] result = new CacheQueryInfo[keys.length];
		int i = result.length-1;
		int j = 0;
		for(final Key key : keys)
			result[i--] = new CacheQueryInfo(key.getText(), values[j].list.size(), values[j++].hits);
		
		return result;
	}
	
	private static final class Key
	{
		private final byte[] text;
		private final int hashCode;
		
		private static final String CHARSET = "utf8";
		
		Key(final Query<? extends Object> query, final boolean doCountOnly)
		{
			try
			{
				text = query.toString(true, doCountOnly).getBytes(CHARSET);
			}
			catch(UnsupportedEncodingException e)
			{
				throw new RuntimeException(e);
			}
			// TODO compress

			hashCode = Arrays.hashCode(text);
		}
		
		@Override
		public boolean equals(final Object obj)
		{
			final Key other = (Key)obj;
			return Arrays.equals(text, other.text);
		}
		
		@Override
		public int hashCode()
		{
			return hashCode;
		}
		
		String getText()
		{
			try
			{
				return new String(text, CHARSET);
			}
			catch(UnsupportedEncodingException e)
			{
				throw new RuntimeException(e);
			}
		}
		
		@Override
		public String toString()
		{
			return getText();
		}
	}
	
	private static final class Value
	{
		final ArrayList<Object> list;
		final int[] invalidationTypesTransiently;
		volatile int hits = 0;
		
		Value(final Query<? extends Object> query, final ArrayList<Object> list)
		{
			final ArrayList<Join> joins = query.joins;
			final TIntHashSet typeSet = new TIntHashSet();
			for(final Type<?> t : query.type.getTypesOfInstances())
				typeSet.add(t.idTransiently);
			if(joins!=null)
			{
				for(final Join join : joins)
					for(final Type t : join.type.getTypesOfInstances())
						typeSet.add(t.idTransiently);
			}
			this.invalidationTypesTransiently = typeSet.toArray();

			this.list = list;
		}
	}
	
	private static final class LRUMap extends LinkedHashMap<Key, Value>
	{
		private static final long serialVersionUID = 1l;
		
		private final int maxSize;
		
		LRUMap(final int maxSize)
		{
			super(maxSize, 0.75f/*DEFAULT_LOAD_FACTOR*/, true);
			this.maxSize = maxSize;
		}
		
		@Override
		protected boolean removeEldestEntry(final Map.Entry<Key,Value> eldest)
		{
			//System.out.println("-----eldest("+size()+"):"+eldest.getKey());
			return size() > maxSize;
		}
	}
}
