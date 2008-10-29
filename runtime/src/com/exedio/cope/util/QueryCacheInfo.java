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

public final class QueryCacheInfo
{
	private final long hits;
	private final long misses;
	private final int level;
	
	public QueryCacheInfo(
			final long hits,
			final long misses,
			final int level)
	{
		this.hits = hits;
		this.misses = misses;
		this.level = level;
	}

	public long getHits()
	{
		return hits;
	}

	public long getMisses()
	{
		return misses;
	}

	public int getLevel()
	{
		return level;
	}
	
	@Override
	public boolean equals(final Object other)
	{
		if(!(other instanceof QueryCacheInfo))
			return false;
		
		final QueryCacheInfo o = (QueryCacheInfo)other;
		
		return hits==o.hits && misses==o.misses && level==o.level;
	}
	
	@Override
	public int hashCode()
	{
		return ((int)hits) ^ ((int)misses) ^ level ^ 938675923;
	}
	
	@Override
	public String toString()
	{
		return "QueryCacheInfo:" + hits + '/' + misses + '/' + level;
	}
}
