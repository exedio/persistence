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

package com.exedio.cope.sample;

import com.exedio.cope.ItemCacheInfo;

final class ItemCacheSummary
{
	final long hits;
	final long misses;
	final long concurrentLoads;
	final int replacementRuns;
	final int replacements;
	final long invalidationsOrdered;
	final long invalidationsDone;

	ItemCacheSummary(final ItemCacheInfo[] infos)
	{
		long allHits = 0;
		long allMisses = 0;
		long allConcurrentLoads = 0;
		int allReplacementRuns = 0;
		int allReplacements = 0;
		long allInvalidationsOrdered = 0l;
		long allInvalidationsDone = 0l;

		for(final ItemCacheInfo info : infos)
		{
			allHits += info.getHits();
			allMisses += info.getMisses();
			allConcurrentLoads += info.getConcurrentLoads();

			allReplacementRuns += info.getReplacementRuns();
			allReplacements += info.getReplacements();

			allInvalidationsOrdered += info.getInvalidationsOrdered();
			allInvalidationsDone += info.getInvalidationsDone();
		}
		this.hits = allHits;
		this.misses = allMisses;
		this.concurrentLoads = allConcurrentLoads;
		this.replacementRuns = allReplacementRuns;
		this.replacements = allReplacements;
		this.invalidationsOrdered = allInvalidationsOrdered;
		this.invalidationsDone = allInvalidationsDone;
	}
}
