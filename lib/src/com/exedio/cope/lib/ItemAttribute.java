
package com.exedio.cope.lib;

import java.util.Collections;
import java.util.List;

public final class ItemAttribute extends ObjectAttribute
{

	private final Class targetTypeClass;

	public ItemAttribute(final Option option, final Class targetTypeClass)
	{
		super(option);
		this.targetTypeClass = targetTypeClass;
		if(targetTypeClass==null)
			throw new InitializerRuntimeException("target type class for attribute "+this+" must not be null");
		if(!Item.class.isAssignableFrom(targetTypeClass))
			throw new InitializerRuntimeException("target type class "+targetTypeClass+" for attribute "+this+" must be a sub class of item");
	}

	/**
	 * Returns the type of items, this attribute accepts instances of.
	 */
	public Type getTargetType()
	{
		final Type result = Type.findByJavaClass(targetTypeClass);
		if(result==null)
			throw new NullPointerException("there is no type for class "+targetTypeClass);
		return result;
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
	
}
