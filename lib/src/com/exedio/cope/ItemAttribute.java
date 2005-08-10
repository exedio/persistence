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

import com.exedio.cope.search.EqualCondition;
import com.exedio.cope.search.NotEqualCondition;

public final class ItemAttribute extends ObjectAttribute
{

	private final Class targetTypeClass;

	/**
	 * @see Item#itemAttribute(Option, Class)
	 */
	ItemAttribute(final Option option, final Class targetTypeClass)
	{
		super(option, targetTypeClass, targetTypeClass.getName());
		this.targetTypeClass = targetTypeClass;
		if(targetTypeClass==null)
			throw new RuntimeException("target type class for attribute "+this+" must not be null");
		if(!Item.class.isAssignableFrom(targetTypeClass))
			throw new RuntimeException("target type class "+targetTypeClass+" for attribute "+this+" must be a sub class of item");
	}
	
	Type targetType = null;

	public ObjectAttribute copyAsTemplate()
	{
		return new ItemAttribute(getTemplateOption(), targetTypeClass);
	}
	
	/**
	 * Returns the type of items, this attribute accepts instances of.
	 */
	public Type getTargetType()
	{
		if(targetType==null)
			throw new RuntimeException();

		return targetType;
	}
	
	protected List createColumns(final Table table, final String name, final boolean notNull)
	{
		if(targetType!=null)
			throw new RuntimeException();
		
		targetType = Type.findByJavaClass(targetTypeClass);
		targetType.registerReference(this);

		return Collections.singletonList(new ItemColumn(table, name, notNull, targetTypeClass, this));
	}
	
	Object cacheToSurface(final Object cache)
	{
		return 
			cache==null ? 
				null : 
				getTargetType().createItemObject(((Integer)cache).intValue());
	}
		
	Object surfaceToCache(final Object surface)
	{
		return
			surface==null ? 
				null : 
				new Integer(((Item)surface).pk);
	}
	
	public final EqualCondition equal(final Item value)
	{
		return new EqualCondition(null, this, value);
	}
	
	public final NotEqualCondition notEqual(final Item value)
	{
		return new NotEqualCondition(this, value);
	}
	
}
