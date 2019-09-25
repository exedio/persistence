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

import java.util.Date;

public final class ItemCacheInfo
{
	private final Type<?> type;
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

	ItemCacheInfo(
			final Type<?> type,
			final int level,
			final long hits, final long misses,
			final long concurrentLoads,
			final long replacements,
			final long invalidationsFutile, final long invalidationsDone,
			final int stampsSize, final long stampsHits, final long stampsPurged
			)
	{
		this.type = type;
		this.level = level;
		this.hits = hits;
		this.misses = misses;
		this.concurrentLoads = concurrentLoads;
		this.replacements = replacements;
		this.invalidationsOrdered = invalidationsFutile + invalidationsDone;
		this.invalidationsDone = invalidationsDone;
		this.stampsSize   = stampsSize;
		this.stampsHits   = stampsHits;
		this.stampsPurged = stampsPurged;
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

	public long getReplacementsL()
	{
		return replacements;
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
	 * use {@link ItemCacheStatistics#getLimit()}
	 */
	@Deprecated
	@SuppressWarnings("static-method")
	public int getLimit()
	{
		return 0;
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
	public long getAgeAverageMillis()
	{
		return 0l;
	}

	/** @deprecated due to changes to the cache implementation, this value is no longer meaningful */
	@Deprecated
	@SuppressWarnings("static-method")
	public long getAgeMinimumMillis()
	{
		return 0l;
	}

	/** @deprecated due to changes to the cache implementation, this value is no longer meaningful */
	@Deprecated
	@SuppressWarnings("static-method")
	public long getAgeMaximumMillis()
	{
		return 0l;
	}

	/**
	 * @deprecated Use {@link #getAgeMinimumMillis()} instead
	 */
	@Deprecated
	public long getAgeMinMillis()
	{
		return getAgeMinimumMillis();
	}

	/**
	 * @deprecated Use {@link #getAgeMaximumMillis()} instead
	 */
	@Deprecated
	public long getAgeMaxMillis()
	{
		return getAgeMaximumMillis();
	}

	/**
	 * @deprecated Not supported anymore.
	 * @return Always returns 0.
	 */
	@Deprecated
	@SuppressWarnings("static-method")
	public long getInvalidationBucketHits()
	{
		return 0l;
	}

	/**
	 * @deprecated Use {@link #getStampsSize()} instead
	 */
	@Deprecated
	public int getInvalidateLastSize()
	{
		return getStampsSize();
	}

	/**
	 * @deprecated Use {@link #getStampsHits()} instead
	 */
	@Deprecated
	public long getInvalidateLastHits()
	{
		return getStampsHits();
	}

	/**
	 * @deprecated Use {@link #getStampsPurged()} instead
	 */
	@Deprecated
	public long getInvalidateLastPurged()
	{
		return getStampsPurged();
	}
}
