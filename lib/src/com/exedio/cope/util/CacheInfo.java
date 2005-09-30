/*
 * Copyright (C) 2004-2005  exedio GmbH (www.exedio.com)
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

import com.exedio.cope.Type;

public final class CacheInfo
{
	private final Type type;
	private final int numberOfItemsInCache;
	private final int hits;
	private final int misses;
	
	public CacheInfo(
			final Type type,
			final int numberOfItemsInCache,
			final int hits, final int misses)
	{
		this.type = type;
		this.numberOfItemsInCache = numberOfItemsInCache;
		this.hits = hits;
		this.misses = misses;
	}
	
	public Type getType()
	{
		return type;
	}

	public int getNumberOfItemsInCache()
	{
		return numberOfItemsInCache;
	}

	public int getHits()
	{
		return hits;
	}

	public int getMisses()
	{
		return misses;
	}

}
