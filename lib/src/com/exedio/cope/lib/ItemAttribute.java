
package com.exedio.cope.lib;

import java.util.Collections;
import java.util.List;

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
	
	static final int SYNTETIC_PRIMARY_KEY_PRECISION = 10;

	protected List createColumns(final String name, final boolean notNull)
	{
		return Collections.singletonList(new IntegerColumn(getType(), name, notNull, SYNTETIC_PRIMARY_KEY_PRECISION));
	}
	
	Object cacheToSurface(final Object cache)
	{
		return 
			cache==null ? 
				null : 
				targetType.getItem(((Integer)cache).intValue());
	}
		
	Object surfaceToCache(final Object surface)
	{
		return
			surface==null ? 
				null : 
				new Integer(((Item)surface).pk);
	}
	
}
