
package com.exedio.cope.lib;

import java.util.Collections;
import java.util.List;

public class DoubleAttribute extends ObjectAttribute
{
	/**
	 * @see Item#doubleAttribute(Option)
	 */
	DoubleAttribute(final Option option)
	{
		super(option);
	}

	protected List createColumns(final Table table, final String name, final boolean notNull)
	{
		return Collections.singletonList(new DoubleColumn(table, name, notNull, 30));
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
