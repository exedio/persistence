
package com.exedio.cope.lib;

import java.util.Collections;
import java.util.List;

public class DoubleAttribute extends ObjectAttribute
{
	public DoubleAttribute(final Option option)
	{
		super(option);
	}

	protected List createColumns(final String name, final boolean notNull)
	{
		return Collections.singletonList(new DoubleColumn(getType(), name, notNull, 30));
	}

	Object cacheToSurface(final Object cache)
	{
		return (Double)cache;
	}
	
	Object surfaceToCache(final Object surface)
	{
		return (Double)surface;
	}
	
}
