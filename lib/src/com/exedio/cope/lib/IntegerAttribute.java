
package com.exedio.cope.lib;

import java.util.Collections;
import java.util.List;

public final class IntegerAttribute extends ObjectAttribute implements IntegerFunction
{
	public IntegerAttribute(final Option option)
	{
		super(option);
	}
	
	protected List createColumns(final Table table, final String name, final boolean notNull)
	{
		return Collections.singletonList(new IntegerColumn(table, name, notNull, 10, false, null));
	}
	
	Object cacheToSurface(final Object cache)
	{
		return (Integer)cache;
	}
		
	Object surfaceToCache(final Object surface)
	{
		return (Integer)surface;
	}
	
}
