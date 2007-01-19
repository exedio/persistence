/*
 * Copyright (C) 2004-2007  exedio GmbH (www.exedio.com)
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


public final class CacheQueryInfo implements Comparable
{
	private final String query;
	private final int hits;
	
	public CacheQueryInfo(
			final String query,
			final int hits)
	{
		if(query==null)
			throw new NullPointerException();
		
		this.query = query;
		this.hits = hits;
	}
	
	public String getQuery()
	{
		return query;
	}

	public int getHits()
	{
		return hits;
	}
	
	@Override
	public boolean equals(final Object other)
	{
		if(!(other instanceof CacheQueryInfo))
			return false;
		
		final CacheQueryInfo o = (CacheQueryInfo)other;
		
		return query.equals(o.query) && hits==o.hits;
	}
	
	@Override
	public int hashCode()
	{
		return query.hashCode() ^ 298742165;
	}
	
	public int compareTo(final Object other)
	{
		final CacheQueryInfo o = (CacheQueryInfo)other;
		
		if(hits<o.hits)
			return 1;
		else
		{
			if(hits==o.hits)
				return query.compareTo(o.query);
			else
				return -1;
		}
	}
	
	@Override
	public String toString()
	{
		return query + ':' + hits;
	}

}
