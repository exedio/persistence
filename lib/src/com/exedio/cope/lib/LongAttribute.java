
package com.exedio.cope.lib;

import java.util.Collections;
import java.util.List;

public final class LongAttribute extends Attribute
{
	public LongAttribute(final Option option)
	{
		super(option);
	}
	
	protected List createColumns(final String name, final boolean notNull)
	{
		return Collections.singletonList(new IntegerColumn(getType(), name, notNull, 20, true));
	}
	
	Object cacheToSurface(final Object cache)
	{
		return (Long)cache;
	}
		
	Object surfaceToCache(final Object surface)
	{
		return (Long)surface;
	}
	
}
