/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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
	private final long ageAverageMillis;
	private final long ageMaxMillis;
	private final long invalidationsOrdered;
	private final long invalidationsDone;
	private final long invalidationBucketHits;

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
		long allNum = 0;
		long ageMinMillis = Long.MAX_VALUE;
		long allSumAgeAverageMillis = 0l;
		long ageMaxMillis = 0l;
		long invalidationsOrdered = 0l;
		long invalidationsDone = 0l;
		long invalidationBucketHits = 0l;

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
				allNum++;

				final long minAge = info.getAgeMinMillis();
				if(ageMinMillis>minAge)
					ageMinMillis = minAge;

				allSumAgeAverageMillis += info.getAgeAverageMillis();

				final long maxAge = info.getAgeMaxMillis();
				if(ageMaxMillis<maxAge)
					ageMaxMillis = maxAge;
			}

			invalidationsOrdered += info.getInvalidationsOrdered();
			invalidationsDone += info.getInvalidationsDone();
			invalidationBucketHits += info.getInvalidationBucketHits();
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
		this.ageAverageMillis = allNum>0 ? allSumAgeAverageMillis/allNum : 0;
		this.ageMaxMillis = ageMaxMillis;
		this.invalidationsOrdered = invalidationsOrdered;
		this.invalidationsDone = invalidationsDone;
		this.invalidationBucketHits = invalidationBucketHits;
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

	public long getAgeMinMillis()
	{
		return ageMinMillis;
	}

	public long getAgeAverageMillis()
	{
		return ageAverageMillis;
	}

	public long getAgeMaxMillis()
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

	public long getInvalidationBucketHits()
	{
		return invalidationBucketHits;
	}
}
