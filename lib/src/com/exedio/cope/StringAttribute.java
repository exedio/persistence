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

package com.exedio.cope;

import java.util.Collections;
import java.util.List;

public final class StringAttribute extends ObjectAttribute implements StringFunction
{
	private final int minimumLength;
	private final int maximumLength;

	/**
	 * @see Item#stringAttribute(Option)
	 */
	StringAttribute(final Option option)
	{
		this(option, 0, Integer.MAX_VALUE);
	}
	
	/**
	 * @see Item#stringAttribute(Option, int)
	 */
	StringAttribute(final Option option, final int minimumLength)
	{
		this(option, minimumLength, Integer.MAX_VALUE);
	}
	
	/**
	 * @see Item#stringAttribute(Option, int, int)
	 */
	StringAttribute(final Option option, final int minimumLength, final int maximumLength)
	{
		super(option, String.class, "string");
		this.minimumLength = minimumLength;
		this.maximumLength = maximumLength;
		if(minimumLength<0)
			throw new RuntimeException("mimimum length must be positive.");
		if(minimumLength>maximumLength)
			throw new RuntimeException("maximum length must be greater or equal mimimum length.");
	}
	
	public final int getMinimumLength()
	{
		return minimumLength;
	}
	
	public final int getMaximumLength()
	{
		return maximumLength;
	}
	
	public final boolean isLengthConstrained()
	{
		return minimumLength!=0 || maximumLength!=Integer.MAX_VALUE;
	}
	
	protected List createColumns(final Table table, final String name, final boolean notNull)
	{
		return Collections.singletonList(new StringColumn(table, name, notNull, minimumLength, maximumLength));
	}
	
	Object cacheToSurface(final Object cache)
	{
		return (String)cache;
	}
		
	Object surfaceToCache(final Object surface)
	{
		return (String)surface;
	}
	
	void checkNotNullValue(final Object value, final Item item)
		throws
			LengthViolationException
	{
		final String stringValue = (String)value;
		if(stringValue.length()<minimumLength)
			throw new LengthViolationException(item, this, stringValue, true);
		if(stringValue.length()>maximumLength)
			throw new LengthViolationException(item, this, stringValue, false);
	}
	
}
