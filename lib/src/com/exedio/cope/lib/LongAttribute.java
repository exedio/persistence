
package com.exedio.cope.lib;

import java.util.Collections;
import java.util.List;

public final class LongAttribute extends ObjectAttribute
{
	public LongAttribute(final Option option)
	{
		super(option);
	}
	
	protected List createColumns(final Table table, final String name, final boolean notNull)
	{
		return Collections.singletonList(new IntegerColumn(table, name, notNull, 20, true, null));
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
