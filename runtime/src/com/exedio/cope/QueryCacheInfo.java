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

import static com.exedio.cope.InfoRegistry.count;

import io.micrometer.core.instrument.Counter;

public final class QueryCacheInfo
{
	private final long hits;
	private final long misses;
	private final long replacements;
	private final long invalidations;
	private final long concurrentLoads;
	private final int  stampsSize;
	private final long stampsHits;
	private final long stampsPurged;
	private final int level;

	QueryCacheInfo(
			final Counter hits,
			final Counter misses,
			final Counter replacements,
			final Counter invalidations,
			final Counter concurrentLoads,
			final int  stampsSize,
			final Counter stampsHits,
			final Counter stampsPurged,
			final int level)
	{
		this.hits = count(hits);
		this.misses = count(misses);
		this.replacements = count(replacements);
		this.invalidations = count(invalidations);
		this.concurrentLoads = count(concurrentLoads);
		this.stampsSize   = stampsSize;
		this.stampsHits   = count(stampsHits);
		this.stampsPurged = count(stampsPurged);
		this.level = level;
	}

	public long getHits()
	{
		return hits;
	}

	public long getMisses()
	{
		return misses;
	}

	public long getReplacements()
	{
		return replacements;
	}

	public long getInvalidations()
	{
		return invalidations;
	}

	public long getConcurrentLoads()
	{
		return concurrentLoads;
	}

	public int getStampsSize()
	{
		return stampsSize;
	}

	public long getStampsHits()
	{
		return stampsHits;
	}

	public long getStampsPurged()
	{
		return stampsPurged;
	}

	public int getLevel()
	{
		return level;
	}

	@Override
	public boolean equals(final Object other)
	{
		if(!(other instanceof QueryCacheInfo))
			return false;

		final QueryCacheInfo o = (QueryCacheInfo)other;

		return
				hits==o.hits &&
				misses==o.misses &&
				replacements==o.replacements &&
				invalidations==o.invalidations &&
				concurrentLoads==o.concurrentLoads &&
				stampsSize==o.stampsSize &&
				stampsHits==o.stampsHits &&
				stampsPurged==o.stampsPurged &&
				level==o.level;
	}

	@Override
	public int hashCode()
	{
		return
				((int)hits) ^
				((int)misses) ^
				((int)replacements) ^
				((int)invalidations) ^
				((int)concurrentLoads) ^
				stampsSize ^
				((int)stampsHits) ^
				((int)stampsPurged) ^
				level ^
				938675923;
	}

	@Override
	public String toString()
	{
		return "QueryCacheInfo:" +
				hits + '/' +
				misses + '/' +
				replacements + '/' +
				invalidations + '/' +
				concurrentLoads + '/' +
				stampsSize + '/' +
				stampsHits + '/' +
				stampsPurged + '/' +
				level;
	}
}
