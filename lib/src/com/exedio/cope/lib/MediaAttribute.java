
package com.exedio.cope.lib;

import java.util.Collections;
import java.util.List;

public final class MediaAttribute extends Attribute
{
	public MediaAttribute()
	{
	}
	
	protected List createColumns(final String name)
	{
		return Collections.EMPTY_LIST;
	}
	
	Object cacheToSurface(final Object cache)
	{
		return cache;
	}
		
	Object surfaceToCache(final Object surface)
	{
		return surface;
	}
	
}
