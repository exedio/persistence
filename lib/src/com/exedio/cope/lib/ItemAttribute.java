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

package com.exedio.cope.lib;

import java.util.Collections;
import java.util.List;

public final class ItemAttribute extends ObjectAttribute
{

	private final Class targetTypeClass;

	/**
	 * @see Item#itemAttribute(Option, Class)
	 */
	ItemAttribute(final Option option, final Class targetTypeClass)
	{
		super(option);
		this.targetTypeClass = targetTypeClass;
		if(targetTypeClass==null)
			throw new RuntimeException("target type class for attribute "+this+" must not be null");
		if(!Item.class.isAssignableFrom(targetTypeClass))
			throw new RuntimeException("target type class "+targetTypeClass+" for attribute "+this+" must be a sub class of item");
	}

	/**
	 * Returns the type of items, this attribute accepts instances of.
	 */
	public Type getTargetType()
	{
		return Type.findByJavaClass(targetTypeClass);
	}
	
	protected List createColumns(final Table table, final String name, final boolean notNull)
	{
		return Collections.singletonList(new ItemColumn(table, name, notNull, targetTypeClass, this));
	}
	
	Object cacheToSurface(final Object cache)
	{
		return 
			cache==null ? 
				null : 
				getTargetType().getItem(((Integer)cache).intValue());
	}
		
	Object surfaceToCache(final Object surface)
	{
		return
			surface==null ? 
				null : 
				new Integer(((Item)surface).pk);
	}
	
	void checkValue( boolean initial, Object value, Item item )
		throws
			ReadOnlyViolationException,
			NotNullViolationException,
			LengthViolationException
	{
		super.checkValue( initial, value, item );
		if ( value!=null && ! (value instanceof Item) )
		{
			throw new ClassCastException("expected Item, got "+value.getClass().getName()+" for "+getName());
		}
	}
	
}
