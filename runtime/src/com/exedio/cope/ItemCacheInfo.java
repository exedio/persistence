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

import static com.exedio.cope.CastUtils.toIntCapped;
import static com.exedio.cope.InfoRegistry.count;

import io.micrometer.core.instrument.Counter;
import java.util.Date;

public final class ItemCacheInfo
{
	private final Type<?> type;
	private final int level;
	private final double hits;
	private final double misses;
	private final double concurrentLoads;
	private final double replacements;
	private final double invalidationsOrdered;
	private final double invalidationsDone;
	private final int  stampsSize;
	private final double stampsHits;
	private final double stampsPurged;

	ItemCacheInfo(
			final Type<?> type,
			final int level,
			final Counter hits, final Counter misses,
			final Counter concurrentLoads,
			final Counter replacements,
			final Counter invalidationsFutile, final Counter invalidationsDone,
			final int stampsSize, final Counter stampsHits, final Counter stampsPurged
			)
	{
		this.type = type;
		this.level = level;
		this.hits = hits.count();
		this.misses = misses.count();
		this.concurrentLoads = concurrentLoads.count();
		this.replacements = replacements.count();
		this.invalidationsDone = invalidationsDone.count();
		this.invalidationsOrdered = invalidationsFutile.count() + this.invalidationsDone;
		this.stampsSize   = stampsSize;
		this.stampsHits   = stampsHits.count();
		this.stampsPurged = stampsPurged.count();
	}

	public Type<?> getType()
	{
		return type;
	}

	/**
	 * Returns the current number of items in the cache.
	 */
	public int getLevel()
	{
		return level;
	}

	public long getHits()
	{
		return count(hits);
	}

	public long getMisses()
	{
		return count(misses);
	}

	public long getConcurrentLoads()
	{
		return count(concurrentLoads);
	}

	public long getReplacementsL()
	{
		return count(replacements);
	}

	public long getInvalidationsOrdered()
	{
		return count(invalidationsOrdered);
	}

	public long getInvalidationsDone()
	{
		return count(invalidationsDone);
	}

	public int getStampsSize()
	{
		return stampsSize;
	}

	public long getStampsHits()
	{
		return count(stampsHits);
	}

	public long getStampsPurged()
	{
		return count(stampsPurged);
	}

	// ------------------- deprecated stuff -------------------

	/**
	 * use {@link ItemCacheStatistics#getLimit()}
	 */
	@Deprecated
	public int getLimit()
	{
		return 0;
	}

	/** @deprecated due to changes to the cache implementation, this value is no longer meaningful */
	@Deprecated
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

	/** @deprecated due to changes to the cache implementation, this value is no longer meaningful */
	@Deprecated
	public Date getLastReplacementRun()
	{
		return null;
	}

	/** @deprecated due to changes to the cache implementation, this value is no longer meaningful */
	@Deprecated
	public long getAgeAverageMillis()
	{
		return 0l;
	}

	/** @deprecated due to changes to the cache implementation, this value is no longer meaningful */
	@Deprecated
	public long getAgeMinimumMillis()
	{
		return 0l;
	}

	/** @deprecated due to changes to the cache implementation, this value is no longer meaningful */
	@Deprecated
	public long getAgeMaximumMillis()
	{
		return 0l;
	}
}
