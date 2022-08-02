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

import gnu.trove.TIntArrayList;
import gnu.trove.TIntHashSet;
import gnu.trove.TLongHashSet;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Tags;
import java.nio.charset.Charset;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;

final class QueryCache
{
	// TODO use guava ComputingMap
	// https://guava-libraries.googlecode.com/svn/tags/release09/javadoc/com/google/common/collect/MapMaker.html#makeComputingMap%28com.google.common.base.Function%29
	private final LRUMap<Key, Value> map;
	private final CacheStamp cacheStamp;
	private final ArrayDeque<Stamp> stampList;
	private final Counter hits;
	private final Counter misses;
	private final Counter invalidations;
	private final Counter concurrentLoads;
	private final Counter replacements;
	private final Counter stampsHit;
	private final Counter stampsPurged;

	QueryCache(final Model model, final int limit, final boolean stamps, final CacheStamp cacheStamp)
	{
		final Metrics metrics = new Metrics(model);
		metrics.gaugeD(
				c -> c.map!=null ? c.map.maxSize : 0,
				"maximumSize",                                "The maximum number of entries in this cache, causing eviction if exceeded"); // name conforms to com.google.common.cache.CacheBuilder
		metrics.gaugeM(c -> c.map,        "size",           "The exact number of entries in this cache"); // name conforms to CacheMeterBinder
		hits            = metrics.counter("gets", "result", "hit",  "The number of times cache lookup methods have returned a cached value."); // name conforms to CacheMeterBinder
		misses          = metrics.counter("gets", "result", "miss", "The number of times cache lookup methods have returned an uncached (newly loaded) value"); // name conforms to CacheMeterBinder
		invalidations   = metrics.counter("invalidations",  "Invalidations in the query cache");
		concurrentLoads = metrics.counter("concurrentLoad", "How often a query was loaded concurrently");
		replacements    = metrics.counter("evictions",      "Evictions in the query cache, as 'size' exceeded 'maximumSize'."); // name conforms to CacheMeterBinder
		metrics.gaugeL(c -> c.stampList,  "stamp.transactions", "Number of transactions in stamp list");
		stampsHit       = metrics.counter("stamp.hit",      "How often a stamp prevented a query from being stored");
		stampsPurged    = metrics.counter("stamp.purge",    "How many stamps were purged because there was no transaction older than the stamp");

		this.map = limit>0 ? new LRUMap<>(limit, x -> replacements.increment()) : null;
		this.cacheStamp = cacheStamp;
		this.stampList = (stamps && map!=null) ? new ArrayDeque<>() : null;
	}

	private static final class Metrics
	{
		final MetricsBuilder back;
		final Model model;

		Metrics(final Model model)
		{
			this.back = new MetricsBuilder(QueryCache.class, model);
			this.model = model;
		}

		Counter counter(
				final String nameSuffix,
				final String description)
		{
			return back.counter(nameSuffix, description, Tags.empty());
		}

		Counter counter(
				final String nameSuffix,
				final String key,
				final String value,
				final String description)
		{
			return back.counter(nameSuffix, description, Tags.of(key, value));
		}

		void gaugeD(
				final ToDoubleFunction<QueryCache> f,
				final String nameSuffix,
				final String description)
		{
			back.gauge(model,
					m -> f.applyAsDouble(m.connect().queryCache),
					nameSuffix, description);
		}

		void gaugeM(
				final Function<QueryCache, Map<?, ?>> f,
				final String nameSuffix,
				final String description)
		{
			back.gauge(model, m ->
					{
						final QueryCache cache = m.connect().queryCache;
						final Map<?,?> map = f.apply(cache);
						if(map==null)
							return 0.0;

						synchronized(cache.map)
						{
							return map.size();
						}
					},
					nameSuffix, description);
		}

		void gaugeL(
				final Function<QueryCache, ArrayDeque<?>> f,
				final String nameSuffix,
				final String description)
		{
			back.gauge(model, m ->
					{
						final QueryCache cache = m.connect().queryCache;
						final ArrayDeque<?> deque = f.apply(cache);
						if(deque==null)
							return 0.0;

						synchronized(cache.map)
						{
							return deque.size();
						}
					},
					nameSuffix, description);
		}
	}

	@SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType") // method is not public
	ArrayList<Object> search(
			final Transaction transaction,
			final Query<?> query,
			final Query.Mode mode)
	{
		if(map==null)
		{
			throw new RuntimeException("search in cache must not be called if query caching is disabled");
		}
		final Key key = new Key(query, mode);
		Value result;
		synchronized(map)
		{
			result = map.get(key);
		}
		if ( result==null )
		{
			final ArrayList<Object> resultList =
				query.searchUncached(transaction, mode);

			if(!transaction.queryCacheDisabled && (
					!mode.isSearch() ||
					resultList.size()<=query.getSearchSizeCacheLimit()))
			{
				if(isStamped(query, transaction.getCacheStamp()))
				{
					stampsHit.increment();
				}
				else
				{
					result = new Value(query, resultList);
					final Object collision;
					synchronized(map)
					{
						collision = map.put(key, result);
					}
					if(collision!=null)
						concurrentLoads.increment();
				}
			}
			misses.increment();
			return resultList;
		}
		else
		{
			hits.increment();
			result.hits.incrementAndGet();

			final List<QueryInfo> queryInfos = transaction.queryInfos;
			if(queryInfos!=null)
				queryInfos.add(new QueryInfo("query cache hit #" + result.hits.get() + " for " + key.getText()));

			return result.list;
		}
	}

	private boolean isStamped(final Query<?> query, final long connectionStamp)
	{
		if(stampList!=null)
		{
			final int[] queryTypes = query.getTypeCacheIds();
			synchronized(map)
			{
				for(final Iterator<Stamp> i = stampList.descendingIterator(); i.hasNext(); )
				{
					final Stamp entry = i.next();
					if(entry.stamp<connectionStamp)
						break;

					final TIntArrayList value = entry.types;
					for(final int queryType : queryTypes)
						if(value.contains(queryType))
							return true;
				}
			}
		}
		return false;
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

			final boolean stampsEnabled = stampList!=null;
			synchronized(map)
			{
				final Iterator<Value> values = map.values().iterator();
				while(values.hasNext())
				{
					final Value value = values.next();
					query: for(final int queryTypeTransiently : value.invalidationTypesTransiently)
					{
						for(final int invalidatedTypeTransiently : invalidatedTypesTransiently)
						{
							if(queryTypeTransiently==invalidatedTypeTransiently)
							{
								values.remove();
								invalidationsCounter++;
								break query;
							}
						}
					}
				}
				if(stampsEnabled)
				{
					final long stamp = cacheStamp.next();
					assert stampList.peekLast()==null || stamp>stampList.peekLast().stamp;
					stampList.addLast(new Stamp(stamp, invalidatedTypesTransientlyList));
				}
			}
			this.invalidations.increment(invalidationsCounter);
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

	void purgeStamps(final long untilStamp)
	{
		if(stampList==null)
			return;

		long count = 0;
		synchronized(map)
		{
			for(final Iterator<Stamp> iter = stampList.iterator(); iter.hasNext(); )
			{
				final Stamp entry = iter.next();
				if(entry.stamp<untilStamp)
				{
					iter.remove();
					count++;
				}
				else
				{
					break;
				}
			}
		}
		if(count>0)
			stampsPurged.increment(count);
	}

	/**
	 * @deprecated for unit tests only
	 */
	@Deprecated
	void clearStamps()
	{
		if(stampList==null)
			return;

		synchronized(map)
		{
			stampList.clear();
		}
	}

	QueryCacheInfo getInfo()
	{
		final int level;
		final int stampListSize;

		if(map!=null)
		{
			synchronized(map)
			{
				level = map.size();
				stampListSize = stampList!=null ? stampList.size() : 0;
			}
		}
		else
		{
			level = 0;
			stampListSize = 0;
		}

		return new QueryCacheInfo(
				hits,
				misses,
				replacements,
				invalidations,
				concurrentLoads,
				stampListSize,
				stampsHit,
				stampsPurged,
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

		Key(final Query<?> query, final Query.Mode mode)
		{
			text = query.toString(true, mode).getBytes(CHARSET);
			// TODO compress
			hashCode = Arrays.hashCode(text);
		}

		@SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
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

	private static final class Stamp
	{
		final long stamp;
		final TIntArrayList types;

		private Stamp(final long stamp, final TIntArrayList types)
		{
			this.stamp = stamp;
			this.types = types;
		}
	}
}
