/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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

import com.exedio.cope.util.CharsetName;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import gnu.trove.TIntArrayList;
import gnu.trove.TIntHashSet;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

final class QueryCache
{
	// TODO use guava ComputingMap
	// http://guava-libraries.googlecode.com/svn/tags/release09/javadoc/com/google/common/collect/MapMaker.html#makeComputingMap%28com.google.common.base.Function%29
	private final LRUMap map;
	private final VolatileLong hits = new VolatileLong(), misses = new VolatileLong(), invalidations = new VolatileLong();

	QueryCache(final int limit)
	{
		this.map = limit>0 ? new LRUMap(limit) : null;
	}

	ArrayList<Object> search(
			final Transaction transaction,
			final Query<?> query,
			final boolean totalOnly)
	{
		if(map==null)
		{
			throw new RuntimeException("search in cache must not be called if query caching is disabled");
		}
		final Key key = new Key(query, totalOnly);
		Value result;
		synchronized(map)
		{
			result = map.get(key);
		}
		if ( result==null )
		{
			final ArrayList<Object> resultList =
				query.searchUncached(transaction, totalOnly);
		if(totalOnly || resultList.size()<=query.getSearchSizeCacheLimit())
		{
			result = new Value(query, resultList);
			synchronized(map)
			{
				map.put(key, result);
			}
		}
			misses.inc();
			return resultList;
		}
		else
		{
			hits.inc();
			result.hits.inc();

			final List<QueryInfo> queryInfos = transaction.queryInfos;
			if(queryInfos!=null)
				queryInfos.add(new QueryInfo("query cache hit #" + result.hits.get() + " for " + key.getText()));

			return result.list;
		}
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
			long invalidationsCounter = 0;

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
							invalidationsCounter++;
							break query;
						}
					}
				}
			}
			this.invalidations.inc(invalidationsCounter);
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

	QueryCacheInfo getInfo()
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

		return new QueryCacheInfo(hits.get(), misses.get(), map!=null ? map.replacements.get() : 0l, invalidations.get(), level);
	}

	QueryCacheHistogram[] getHistogram()
	{
		if(map==null)
			return new QueryCacheHistogram[0];

		final Key[] keys;
		final Value[] values;
		synchronized(map)
		{
			final int size = map.size();
			keys   = map.keySet().toArray(new Key[size]);
			values = map.values().toArray(new Value[size]);
		}

		final QueryCacheHistogram[] result = new QueryCacheHistogram[keys.length];
		int i = result.length-1;
		int j = 0;
		for(final Key key : keys)
			result[i--] = new QueryCacheHistogram(key.getText(), values[j].list.size(), values[j++].hits.get());

		return result;
	}

	private static final class Key
	{
		private final byte[] text;
		private final int hashCode;

		private static final String CHARSET = CharsetName.UTF8;

		Key(final Query<? extends Object> query, final boolean totalOnly)
		{
			try
			{
				text = query.toString(true, totalOnly).getBytes(CHARSET);
			}
			catch(final UnsupportedEncodingException e)
			{
				throw new RuntimeException(CHARSET, e);
			}
			// TODO compress

			hashCode = Arrays.hashCode(text);
		}

		@SuppressFBWarnings({"BC_EQUALS_METHOD_SHOULD_WORK_FOR_ALL_OBJECTS", "NP_EQUALS_SHOULD_HANDLE_NULL_ARGUMENT"})
		@Override
		public boolean equals(final Object other)
		{
			final Key o = (Key)other;
			return Arrays.equals(text, o.text);
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
			catch(final UnsupportedEncodingException e)
			{
				throw new RuntimeException(CHARSET, e);
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
		final VolatileLong hits = new VolatileLong();

		Value(final Query<? extends Object> query, final ArrayList<Object> list)
		{
			final ArrayList<Join> joins = query.joins;
			final TIntHashSet typeSet = new TIntHashSet();
			for(final Type<?> t : query.type.getTypesOfInstances())
				typeSet.add(t.cacheIdTransiently);
			if(joins!=null)
			{
				for(final Join join : joins)
					for(final Type<?> t : join.type.getTypesOfInstances())
						typeSet.add(t.cacheIdTransiently);
			}
			this.invalidationTypesTransiently = typeSet.toArray();

			this.list = list;
		}
	}

	private static final class LRUMap extends LinkedHashMap<Key, Value>
	{
		private static final long serialVersionUID = 1l;

		private final int maxSize;
		final VolatileLong replacements = new VolatileLong();

		LRUMap(final int maxSize)
		{
			super(maxSize, 0.75f/*DEFAULT_LOAD_FACTOR*/, true);
			this.maxSize = maxSize;
		}

		@Override
		protected boolean removeEldestEntry(final Map.Entry<Key,Value> eldest)
		{
			//System.out.println("-----eldest("+size()+"):"+eldest.getKey());
			final boolean result = size() > maxSize;
			if(result)
				replacements.inc();
			return result;
		}
	}
}
