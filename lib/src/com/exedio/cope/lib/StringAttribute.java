
package com.exedio.cope.lib;

import java.util.Collections;
import java.util.List;

public final class StringAttribute extends Attribute
{
	public StringAttribute(final Search.Option option)
	{
		super(option);
	}
	
	public StringAttribute(final Search.Option option, final AttributeMapping mapping)
	{
		super(option, mapping);
	}
	
	protected List createColumns(final String name, final boolean notNull)
	{
		if(mapping==null)
			return Collections.singletonList(new StringColumn(getType(), name, notNull));
		else
			return Collections.EMPTY_LIST;
	}
	
	Object cacheToSurface(final Object cache)
	{
		return (String)cache;
	}
		
	Object surfaceToCache(final Object surface)
	{
		return (String)surface;
	}
	
}
