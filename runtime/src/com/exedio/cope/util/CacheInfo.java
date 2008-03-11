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

package com.exedio.cope.util;

import java.util.Date;

import com.exedio.cope.Type;

public final class CacheInfo
{
	private final Type type;
	private final int limit;
	private final int level;
	private final long hits;
	private final long misses;
	private final int numberOfCleanups;
	private final int itemsCleanedUp;
	private final Date lastCleanup;
	private final long ageSum;
	private final long ageMin;
	private final long ageMax;
	
	public CacheInfo(
			final Type type,
			final int limit,
			final int level,
			final long hits, final long misses,
			final int numberOfCleanups, final int itemsCleanedUp, final Date lastCleanup,
			final long ageSum, final long ageMin, final long ageMax)
	{
		this.type = type;
		this.limit = limit;
		this.level = level;
		this.hits = hits;
		this.misses = misses;
		this.numberOfCleanups = numberOfCleanups;
		this.itemsCleanedUp = itemsCleanedUp;
		this.lastCleanup = lastCleanup;
		this.ageSum = ageSum;
		this.ageMin = ageMin;
		this.ageMax = ageMax;
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
	
	public int getNumberOfCleanups()
	{
		return numberOfCleanups;
	}
	
	public int getItemsCleanedUp()
	{
		return itemsCleanedUp;
	}
	
	public Date getLastCleanup()
	{
		return lastCleanup;
	}
	
	public long getAgeAverageMillis()
	{
		return (level!=0) ? (ageSum / level) : 0l;
	}
	
	public long getAgeMinMillis()
	{
		return ageMin;
	}
	
	public long getAgeMaxMillis()
	{
		return ageMax;
	}
}
