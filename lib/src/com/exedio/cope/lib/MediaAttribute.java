
package com.exedio.cope.lib;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class MediaAttribute extends Attribute
{
	StringColumn mimeMajor;
	StringColumn mimeMinor;

	public MediaAttribute()
	{
	}
	
	protected List createColumns(final String name)
	{
		mimeMajor = new StringColumn(getType(), name + "Major");
		mimeMinor = new StringColumn(getType(), name + "Minor");
		return Arrays.asList(new StringColumn[]{mimeMajor, mimeMinor});
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
