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

import com.exedio.cope.Type;

public final class PrimaryKeyInfo
{
	private final Type type;
	private final int minimum;
	private final int maximum;
	
	private final boolean known;
	private final int count;
	private final int first;
	private final int last;
	
	public PrimaryKeyInfo(
			final Type type,
			final int minimum,
			final int maximum,
			final int count,
			final int first,
			final int last)
	{
		this.type = type;
		this.minimum = minimum;
		this.maximum = maximum;
		
		this.known = true;
		this.count = count;
		this.first = first;
		this.last = last;
	}
	
	public PrimaryKeyInfo(
			final Type type,
			final int minimum,
			final int maximum)
	{
		this.type = type;
		this.minimum = minimum;
		this.maximum = maximum;
		
		this.known = false;
		this.count = 0;
		this.first = 0;
		this.last = 0;
	}
	
	public Type getType()
	{
		return type;
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
