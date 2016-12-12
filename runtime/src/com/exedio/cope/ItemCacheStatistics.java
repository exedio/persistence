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

import java.util.Arrays;

public final class ItemCacheStatistics
{
	private final int limit;
	private final int level;
	private final ItemCacheInfo[] itemCacheInfos;

	private boolean summaryComputed=false;
	private long summarizedHits;
	private long summarizedMisses;
	private long summarizedConcurrentLoads;
	private long summarizedReplacements;
	private long summarizedInvalidationsOrdered;
	private long summarizedInvalidationsDone;
	private int  summarizedStampsSize;
	private long summarizedStampsHits;
	private long summarizedStampsPurged;

	ItemCacheStatistics(final int limit, final int level, final ItemCacheInfo[] itemCacheInfos)
	{
		this.limit = limit;
		this.level = level;
		this.itemCacheInfos = itemCacheInfos;
	}

	private void requireSummary()
	{
		if (!this.summaryComputed)
		{
			long hits = 0;
			long misses = 0;
			long concurrentLoads = 0;
			long replacements = 0;
			long invalidationsOrdered = 0l;
			long invalidationsDone = 0l;
			int  stampsSize = 0;
			long stampsHits = 0l;
			long stampsPurged = 0l;

			for(final ItemCacheInfo info : itemCacheInfos)
			{
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
			this.summarizedHits = hits;
			this.summarizedMisses = misses;
			this.summarizedConcurrentLoads = concurrentLoads;
			this.summarizedReplacements = replacements;
			this.summarizedInvalidationsOrdered = invalidationsOrdered;
			this.summarizedInvalidationsDone = invalidationsDone;
			this.summarizedStampsSize = stampsSize;
			this.summarizedStampsHits = stampsHits;
			this.summarizedStampsPurged = stampsPurged;
			this.summaryComputed=true;
		}
	}

	/**
	 * Returns the maximum number of items in the cache.
	 */
	public int getLimit()
	{
		return limit;
	}

	public int getLevel()
	{
		return level;
	}

	public ItemCacheInfo[] getItemCacheInfos()
	{
		return Arrays.copyOf(itemCacheInfos, itemCacheInfos.length);
	}

	public long getSummarizedHits()
	{
		requireSummary();
		return summarizedHits;
	}

	public long getSummarizedMisses()
	{
		requireSummary();
		return summarizedMisses;
	}

	public long getSummarizedConcurrentLoads()
	{
		requireSummary();
		return summarizedConcurrentLoads;
	}

	public long getSummarizedReplacements()
	{
		requireSummary();
		return summarizedReplacements;
	}

	public long getSummarizedInvalidationsOrdered()
	{
		requireSummary();
		return summarizedInvalidationsOrdered;
	}

	public long getSummarizedInvalidationsDone()
	{
		requireSummary();
		return summarizedInvalidationsDone;
	}

	public int getSummarizedStampsSize()
	{
		requireSummary();
		return summarizedStampsSize;
	}

	public long getSummarizedStampsHits()
	{
		requireSummary();
		return summarizedStampsHits;
	}

	public long getSummarizedStampsPurged()
	{
		requireSummary();
		return summarizedStampsPurged;
	}
}
