/*
 * Copyright (C) 2004-2011  exedio GmbH (www.exedio.com)
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

import java.util.Date;

public final class ItemCacheInfo
{
	private final Type type;
	private final int limit;
	private final int level;
	private final long hits;
	private final long misses;
	private final long concurrentLoads;
	private final int replacementRuns;
	private final int replacements;
	private final long lastReplacementRun;
	private final long ageSum;
	private final long ageMin;
	private final long ageMax;
	private final long invalidationsOrdered;
	private final long invalidationsDone;
	private final int  invalidateLastSize;
	private final long invalidateLastHits;
	private final long invalidateLastPurged;

	ItemCacheInfo(
			final Type type,
			final int limit,
			final int level,
			final long hits, final long misses,
			final long concurrentLoads,
			final int replacementRuns, final int replacements, final Date lastReplacementRun,
			final long ageSum, final long ageMin, final long ageMax,
			final long invalidationsOrdered, final long invalidationsDone,
			final int invalidateLastSize, final long invalidateLastHits, final long invalidateLastPurged
			)
	{
		this.type = type;
		this.limit = limit;
		this.level = level;
		this.hits = hits;
		this.misses = misses;
		this.concurrentLoads = concurrentLoads;
		this.replacementRuns = replacementRuns;
		this.replacements = replacements;
		this.lastReplacementRun = lastReplacementRun!=null ? lastReplacementRun.getTime() : Long.MIN_VALUE;
		this.ageSum = ageSum;
		this.ageMin = ageMin;
		this.ageMax = ageMax;
		this.invalidationsOrdered = invalidationsOrdered;
		this.invalidationsDone = invalidationsDone;
		this.invalidateLastSize = invalidateLastSize;
		this.invalidateLastHits = invalidateLastHits;
		this.invalidateLastPurged = invalidateLastPurged;
	}

	public Type getType()
	{
		return type;
	}

	/**
	 * Returns the maximum number of items in the cache.
	 */
	public int getLimit()
	{
		return limit;
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

	public int getReplacementRuns()
	{
		return replacementRuns;
	}

	public int getReplacements()
	{
		return replacements;
	}

	public Date getLastReplacementRun()
	{
		return lastReplacementRun!=Long.MIN_VALUE ? new Date(lastReplacementRun) : null;
	}

	public long getAgeAverageMillis()
	{
		return (level!=0) ? (ageSum / level) : 0l;
	}

	public long getAgeMinimumMillis()
	{
		return ageMin;
	}

	public long getAgeMaximumMillis()
	{
		return ageMax;
	}

	public long getInvalidationsOrdered()
	{
		return invalidationsOrdered;
	}

	public long getInvalidationsDone()
	{
		return invalidationsDone;
	}

	public int getInvalidateLastSize()
	{
		return invalidateLastSize;
	}

	public long getInvalidateLastHits()
	{
		return invalidateLastHits;
	}

	public long getInvalidateLastPurged()
	{
		return invalidateLastPurged;
	}

	/**
	 * @deprecated Not supported anymore.
	 * @return Always returns 0.
	 */
	@Deprecated
	public long getInvalidationBucketHits()
	{
		return 0l;
	}

	// ------------------- deprecated stuff -------------------

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
}
