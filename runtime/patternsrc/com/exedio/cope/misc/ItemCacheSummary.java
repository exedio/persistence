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

package com.exedio.cope.misc;

import static java.lang.Integer.MAX_VALUE;
import static java.lang.Integer.MIN_VALUE;

import com.exedio.cope.ItemCacheInfo;
import com.exedio.cope.ItemCacheStatistics;
import java.util.Date;

/** @deprecated Use summary methods at {@link ItemCacheStatistics} instead */
@Deprecated
public final class ItemCacheSummary
{
	private final int level;
	private final long hits;
	private final long misses;
	private final long concurrentLoads;
	private final long replacements;
	private final long invalidationsOrdered;
	private final long invalidationsDone;
	private final int  stampsSize;
	private final long stampsHits;
	private final long stampsPurged;

	public ItemCacheSummary(final ItemCacheInfo[] infos)
	{
		int level = 0;
		long hits = 0;
		long misses = 0;
		long concurrentLoads = 0;
		long replacements = 0;
		long invalidationsOrdered = 0l;
		long invalidationsDone = 0l;
		int  stampsSize = 0;
		long stampsHits = 0l;
		long stampsPurged = 0l;

		for(final ItemCacheInfo info : infos)
		{
			level += info.getLevel();
			hits += info.getHits();
			misses += info.getMisses();
			concurrentLoads += info.getConcurrentLoads();
			replacements += info.getReplacementsL();
			invalidationsOrdered += info.getInvalidationsOrdered();
			invalidationsDone += info.getInvalidationsDone();
			stampsSize   += info.getStampsSize();
			stampsHits   += info.getStampsHits();
			stampsPurged += info.getStampsPurged();
		}
		this.level = level;
		this.hits = hits;
		this.misses = misses;
		this.concurrentLoads = concurrentLoads;
		this.replacements = replacements;
		this.invalidationsOrdered = invalidationsOrdered;
		this.invalidationsDone = invalidationsDone;
		this.stampsSize = stampsSize;
		this.stampsHits = stampsHits;
		this.stampsPurged = stampsPurged;
	}

	/** @deprecated use {@link ItemCacheStatistics#getLimit()} */
	@Deprecated
	@SuppressWarnings("static-method")
	public int getLimit()
	{
		return 0;
	}

	public int getLevel()
	{
		return level;
	}

	public long getHits()
	{
		return hits;
	}

	public long getMisses()
	{
		return misses;
	}

	public long getConcurrentLoads()
	{
		return concurrentLoads;
	}

	/** @deprecated due to changes to the cache implementation, this value is no longer meaningful */
	@Deprecated
	@SuppressWarnings("static-method")
	public int getReplacementRuns()
	{
		return 0;
	}

	/**
	 * @deprecated Use {@link #getReplacementsL()} instead
	 */
	@Deprecated
	public int getReplacements()
	{
		return toIntCapped(getReplacementsL());
	}

	public long getReplacementsL()
	{
		return replacements;
	}

	/** @deprecated due to changes to the cache implementation, this value is no longer meaningful */
	@Deprecated
	@SuppressWarnings("static-method")
	public Date getLastReplacementRun()
	{
		return null;
	}

	/** @deprecated due to changes to the cache implementation, this value is no longer meaningful */
	@Deprecated
	@SuppressWarnings("static-method")
	public long getAgeMinimumMillis()
	{
		return -1;
	}

	/** @deprecated due to changes to the cache implementation, this value is no longer meaningful */
	@Deprecated
	@SuppressWarnings("static-method")
	public long getAgeAverageMillis()
	{
		return -1;
	}

	/** @deprecated due to changes to the cache implementation, this value is no longer meaningful */
	@Deprecated
	@SuppressWarnings("static-method")
	public long getAgeMaximumMillis()
	{
		return -1;
	}

	public long getInvalidationsOrdered()
	{
		return invalidationsOrdered;
	}

	public long getInvalidationsDone()
	{
		return invalidationsDone;
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

	// ------------------- deprecated stuff -------------------

	/**
	 * @deprecated Copied from com.exedio.ope.CastUtils
	 */
	@Deprecated
	private static int toIntCapped(final long longValue)
	{
		if (longValue > MAX_VALUE)
		{
			return MAX_VALUE;
		}
		if (longValue < MIN_VALUE)
		{
			return MIN_VALUE;
		}
		return (int)longValue;
	}
}
