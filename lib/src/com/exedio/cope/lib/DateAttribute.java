
package com.exedio.cope.lib;

import java.util.Collections;
import java.util.Date;
import java.util.List;

public final class DateAttribute extends ObjectAttribute
{
	public DateAttribute(final Option option)
	{
		super(option);
	}
	
	protected List createColumns(final String name, final boolean notNull)
	{
		return Collections.singletonList(new IntegerColumn(getType(), name, notNull, 20, true, null));
	}
	
	Object cacheToSurface(final Object cache)
	{
		return cache==null ? null : new Date(((Long)cache).longValue());
	}
		
	Object surfaceToCache(final Object surface)
	{
		return surface==null ? null : new Long(((Date)surface).getTime());
	}
	
}
