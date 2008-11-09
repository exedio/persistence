/*
 * Copyright (C) 2004-2008  exedio GmbH (www.exedio.com)
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

import java.io.PrintStream;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import com.exedio.cope.Model;
import com.exedio.cope.util.ItemCacheInfo;

final class ItemCacheCop extends ConsoleCop
{
	ItemCacheCop(final Args args)
	{
		super(TAB_ITEM_CACHE, "item cache", args);
	}

	@Override
	protected ItemCacheCop newArgs(final Args args)
	{
		return new ItemCacheCop(args);
	}

	@Override
	final void writeBody(
			final PrintStream out,
			final Model model,
			final HttpServletRequest request,
			final History history,
			final boolean historyModelShown)
	{
		final ItemCacheInfo[] infos = model.getItemCacheInfo();
		
		int allLimit = 0;
		int allLevel = 0;
		long allHits = 0;
		long allMisses = 0;
		int allReplacementRuns = 0;
		int allReplacements = 0;
		Date allLastReplacementRun = null;
		long allNum = 0;
		long allAgeMinMillis = Long.MAX_VALUE;
		long allSumAgeAverageMillis = 0l;
		long allAgeMaxMillis = 0l;
		
		for(final ItemCacheInfo info : infos)
		{
			allLimit += info.getLimit();
			allLevel += info.getLevel();
			allHits += info.getHits();
			allMisses += info.getMisses();
			
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
		}
		
		ItemCache_Jspm.writeBody(this, out,
				allLimit, allLevel,
				allHits, allMisses,
				allReplacementRuns, allReplacements, allLastReplacementRun,
				allAgeMinMillis!=Long.MAX_VALUE ? allAgeMinMillis : 0,
				allNum>0 ? allSumAgeAverageMillis/allNum : 0,
				allAgeMaxMillis,
				infos);
	}
}
