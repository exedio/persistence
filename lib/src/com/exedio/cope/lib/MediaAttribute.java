
package com.exedio.cope.lib;

import java.util.Arrays;
import java.util.List;

public final class MediaAttribute extends Attribute
{
	StringColumn mimeMajor;
	StringColumn mimeMinor;

	public MediaAttribute(final Search.Option option)
	{
		super(option);
	}
	
	protected List createColumns(final String name, final boolean notNull)
	{
		mimeMajor = new StringColumn(getType(), name + "Major", notNull);
		mimeMinor = new StringColumn(getType(), name + "Minor", notNull);
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
