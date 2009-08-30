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

package com.exedio.cope.info;

import com.exedio.cope.Feature;

public final class SequenceInfo
{
	private final Feature feature;
	private final int start;
	private final int minimum;
	private final int maximum;
	
	private final boolean known;
	private final int count;
	private final int first;
	private final int last;
	
	public SequenceInfo(
			final Feature feature,
			final int start,
			final int minimum,
			final int maximum,
			final int count,
			final int first,
			final int last)
	{
		this.feature = feature;
		this.start = start;
		this.minimum = minimum;
		this.maximum = maximum;
		
		this.known = true;
		this.count = count;
		this.first = first;
		this.last = last;
	}
	
	public SequenceInfo(
			final Feature feature,
			final int start,
			final int minimum,
			final int maximum)
	{
		this.feature = feature;
		this.start = start;
		this.minimum = minimum;
		this.maximum = maximum;
		
		this.known = false;
		this.count = 0;
		this.first = 0;
		this.last = 0;
	}
	
	public Feature getFeature()
	{
		return feature;
	}
	
	public int getStart()
	{
		return start;
	}
	
	public int getMinimum()
	{
		return minimum;
	}
	
	public int getMaximum()
	{
		return maximum;
	}
	
	public int getCount()
	{
		return count;
	}
	
	public boolean isKnown()
	{
		return known;
	}
	
	/**
	 * Returns the first primary key number generated for the type since the startup of the application.
	 */
	public int getFirst()
	{
		if(!known)
			throw new IllegalStateException("not known");
		
		return first;
	}
	
	/**
	 * Returns the last primary key number generated for the type.
	 */
	public int getLast()
	{
		if(!known)
			throw new IllegalStateException("not known");
		
		return last;
	}
}
