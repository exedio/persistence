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

package com.exedio.cope.misc;

import java.util.Date;

import com.exedio.cope.ItemCacheInfo;

public final class ItemCacheSummary
{
	private final int limit;
	private final int level;
	private final long hits;
	private final long misses;
	private final long concurrentLoads;
	private final int replacementRuns;
	private final int replacements;
	private final long lastReplacementRun;
	private final long ageMinMillis;
	private final long ageAvgMillis;
	private final long ageMaxMillis;
	private final long invalidationsOrdered;
	private final long invalidationsDone;
	private final int  invalidateLastSize;
	private final long invalidateLastHits;
	private final long invalidateLastPurged;

	public ItemCacheSummary(final ItemCacheInfo[] infos)
	{
		int limit = 0;
		int level = 0;
		long hits = 0;
		long misses = 0;
		long concurrentLoads = 0;
		int replacementRuns = 0;
		int replacements = 0;
		long lastReplacementRun = Long.MIN_VALUE;
		long numAgeAverageMillis = 0;
		long ageMinMillis = Long.MAX_VALUE;
		long sumAgeAvgMillis = 0l;
		long ageMaxMillis = 0l;
		long invalidationsOrdered = 0l;
		long invalidationsDone = 0l;
		int  invalidateLastSize = 0;
		long invalidateLastHits = 0l;
		long invalidateLastPurged = 0l;

		for(final ItemCacheInfo info : infos)
		{
			limit += info.getLimit();
			level += info.getLevel();
			hits += info.getHits();
			misses += info.getMisses();
			concurrentLoads += info.getConcurrentLoads();

			replacementRuns += info.getReplacementRuns();
			replacements += info.getReplacements();

			final Date currentLastReplacementRunDate = info.getLastReplacementRun();
			if(currentLastReplacementRunDate!=null)
			{
				final long currentLastReplacementRun = currentLastReplacementRunDate.getTime();
				if(lastReplacementRun<currentLastReplacementRun)
					lastReplacementRun = currentLastReplacementRun;
			}

			if(info.getLevel()>0)
			{
				numAgeAverageMillis++;

				final long currentMinAgeMillis = info.getAgeMinimumMillis();
				if(ageMinMillis>currentMinAgeMillis)
					ageMinMillis = currentMinAgeMillis;

				sumAgeAvgMillis += info.getAgeAverageMillis();

				final long currentMaxAgeMillis = info.getAgeMaximumMillis();
				if(ageMaxMillis<currentMaxAgeMillis)
					ageMaxMillis = currentMaxAgeMillis;
			}

			invalidationsOrdered += info.getInvalidationsOrdered();
			invalidationsDone += info.getInvalidationsDone();
			invalidateLastSize += info.getInvalidateLastSize();
			invalidateLastHits += info.getInvalidateLastHits();
			invalidateLastPurged += info.getInvalidateLastPurged();
		}
		this.limit = limit;
		this.level = level;
		this.hits = hits;
		this.misses = misses;
		this.concurrentLoads = concurrentLoads;
		this.replacementRuns = replacementRuns;
		this.replacements = replacements;
		this.lastReplacementRun = lastReplacementRun;
		this.ageMinMillis = ageMinMillis!=Long.MAX_VALUE ? ageMinMillis : 0;
		this.ageAvgMillis = numAgeAverageMillis>0 ? sumAgeAvgMillis/numAgeAverageMillis : 0;
		this.ageMaxMillis = ageMaxMillis;
		this.invalidationsOrdered = invalidationsOrdered;
		this.invalidationsDone = invalidationsDone;
		this.invalidateLastSize = invalidateLastSize;
		this.invalidateLastHits = invalidateLastHits;
		this.invalidateLastPurged = invalidateLastPurged;
	}

	public int getLimit()
	{
		return limit;
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

	public long getAgeMinimumMillis()
	{
		return ageMinMillis;
	}

	public long getAgeAverageMillis()
	{
		return ageAvgMillis;
	}

	public long getAgeMaximumMillis()
	{
		return ageMaxMillis;
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
