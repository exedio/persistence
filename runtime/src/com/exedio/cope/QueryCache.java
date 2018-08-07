/*
 * Copyright (C) 2004-2015  exedio GmbH (www.exedio.com)
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

import static java.nio.charset.StandardCharsets.UTF_8;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import gnu.trove.TIntArrayList;
import gnu.trove.TIntHashSet;
import gnu.trove.TLongHashSet;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

final class QueryCache
{
	// TODO use guava ComputingMap
	// https://guava-libraries.googlecode.com/svn/tags/release09/javadoc/com/google/common/collect/MapMaker.html#makeComputingMap%28com.google.common.base.Function%29
	private final LRUMap<Key, Value> map;
	private final AtomicLong hits = new AtomicLong();
	private final AtomicLong misses = new AtomicLong();
	private final AtomicLong invalidations = new AtomicLong();
	private final AtomicLong concurrentLoads = new AtomicLong();
	private final AtomicLong replacements = new AtomicLong();

	QueryCache(final int limit)
	{
		this.map = limit>0 ? new LRUMap<>(limit, x -> replacements.incrementAndGet()) : null;
	}

	@SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType") // method is not public
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

			if(totalOnly ||
				resultList.size()<=query.getSearchSizeCacheLimit())
			{
				result = new Value(query, resultList);
				final Object collision;
				synchronized(map)
				{
					collision = map.put(key, result);
				}
				if(collision!=null)
					concurrentLoads.incrementAndGet();
			}
			misses.incrementAndGet();
			return resultList;
		}
		else
		{
			hits.incrementAndGet();
			result.hits.incrementAndGet();

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

	void invalidate(final TLongHashSet[] invalidations)
	{
		if(map==null)
			return;

		final TIntArrayList invalidatedTypesTransientlyList = new TIntArrayList();

		for(int typeTransiently=0; typeTransiently<invalidations.length; typeTransiently++)
			if(invalidations[typeTransiently]!=null)
				invalidatedTypesTransientlyList.add(typeTransiently);

		if(!invalidatedTypesTransientlyList.isEmpty())
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
			this.invalidations.addAndGet(invalidationsCounter);
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

		return new QueryCacheInfo(
				hits.get(),
				misses.get(),
				replacements.get(),
				invalidations.get(),
				concurrentLoads.get(),
				level);
	}

	QueryCacheHistogram[] getHistogram()
	{
		if(map==null)
			return EMPTY_HISTOGRAM;

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

	private static final QueryCacheHistogram[] EMPTY_HISTOGRAM = {};

	private static final class Key
	{
		private final byte[] text;
		private final int hashCode;

		private static final Charset CHARSET = UTF_8;

		Key(final Query<?> query, final boolean totalOnly)
		{
			text = query.toString(true, totalOnly).getBytes(CHARSET);
			// TODO compress
			hashCode = Arrays.hashCode(text);
		}

		@SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
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
			return new String(text, CHARSET);
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
		final AtomicLong hits = new AtomicLong();

		@SuppressWarnings("AssignmentToCollectionOrArrayFieldFromParameter")
		Value(final Query<?> query, final ArrayList<Object> list)
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
}
