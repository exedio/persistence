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

package com.exedio.cope.console;

import java.util.Date;

import com.exedio.cope.ItemCacheInfo;

final class ItemCacheSummary
{
	final int limit;
	final int level;
	final long hits;
	final long misses;
	final long concurrentLoads;
	final int replacementRuns;
	final int replacements;
	final Date lastReplacementRun;
	final long ageMinMillis;
	final long ageAverageMillis;
	final long ageMaxMillis;
	final long invalidationsOrdered;
	final long invalidationsDone;
	
	ItemCacheSummary(final ItemCacheInfo[] infos)
	{
		int allLimit = 0;
		int allLevel = 0;
		long allHits = 0;
		long allMisses = 0;
		long allConcurrentLoads = 0;
		int allReplacementRuns = 0;
		int allReplacements = 0;
		Date allLastReplacementRun = null;
		long allNum = 0;
		long allAgeMinMillis = Long.MAX_VALUE;
		long allSumAgeAverageMillis = 0l;
		long allAgeMaxMillis = 0l;
		long allInvalidationsOrdered = 0l;
		long allInvalidationsDone = 0l;
		
		for(final ItemCacheInfo info : infos)
		{
			allLimit += info.getLimit();
			allLevel += info.getLevel();
			allHits += info.getHits();
			allMisses += info.getMisses();
			allConcurrentLoads += info.getConcurrentLoads();
			
			allReplacementRuns += info.getReplacementRuns();
			allReplacements += info.getReplacements();
			
			final Date lastReplacementRun = info.getLastReplacementRun();
			if(allLastReplacementRun==null || (lastReplacementRun!=null && allLastReplacementRun.before(lastReplacementRun)))
				allLastReplacementRun = lastReplacementRun;

			if(info.getLevel()>0)
			{
				allNum++;

				final long minAge = info.getAgeMinMillis();
				if(allAgeMinMillis>minAge)
					allAgeMinMillis = minAge;
				
				allSumAgeAverageMillis += info.getAgeAverageMillis();
	
				final long maxAge = info.getAgeMaxMillis();
				if(allAgeMaxMillis<maxAge)
					allAgeMaxMillis = maxAge;
			}
			
			allInvalidationsOrdered += info.getInvalidationsOrdered();
			allInvalidationsDone += info.getInvalidationsDone();
		}
		this.limit = allLimit;
		this.level = allLevel;
		this.hits = allHits;
		this.misses = allMisses;
		this.concurrentLoads = allConcurrentLoads;
		this.replacementRuns = allReplacementRuns;
		this.replacements = allReplacements;
		this.lastReplacementRun = allLastReplacementRun;
		this.ageMinMillis = allAgeMinMillis!=Long.MAX_VALUE ? allAgeMinMillis : 0;
		this.ageAverageMillis = allNum>0 ? allSumAgeAverageMillis/allNum : 0;
		this.ageMaxMillis = allAgeMaxMillis;
		this.invalidationsOrdered = allInvalidationsOrdered;
		this.invalidationsDone = allInvalidationsDone;
	}
}
