
package com.exedio.cope.lib;

import java.math.BigDecimal;

public final class ItemAttribute extends Attribute
{
	private Type targetType;

	public void initialize(final String name, final boolean readOnly, final boolean notNull,
								  final Type targetType)
	{
		super.initialize(name, readOnly, notNull);
		this.targetType = targetType;
	}
	
	/**
	 * Returns the type of items, this attribute accepts instances of.
	 */
	public Type getTargetType()
	{
		return this.targetType;
	}

	Object databaseToCache(final Object cell)
	{
		if(cell==null)
			return null;
		else
			return new Integer(((BigDecimal)cell).intValue()); // TODO: use ResultSet.getInt() somehow
	}

	Object cacheToDatabase(final Object cache)
	{
		if(cache==null)
			return "NULL";
		else
			return ((Integer)cache).toString();
	}

	Object cacheToSurface(final Object cache)
	{
		return cache==null ? null : targetType.getItem(((Integer)cache).intValue());
	}
		
	Object surfaceToCache(final Object surface)
	{
		return surface==null ? null : new Integer(((Item)surface).pk);
	}
	
}
