
package com.exedio.cope.lib;

import java.util.Arrays;
import java.util.List;

public final class MediaAttribute extends Attribute
{
	StringColumn mimeMajor;
	StringColumn mimeMinor;

	public MediaAttribute(final Item.Option option)
	{
		super(option);
	}
	
	protected List createColumns(final String name, final boolean notNull)
	{
		// TODO: create column only, if major mime type is not fixed
		mimeMajor = new StringColumn(getType(), name + "Major", notNull);
		// TODO: create column only, if minor mime type is not fixed
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
