package com.exedio.cope.pattern;

import com.exedio.cope.instrument.BooleanGetter;

final class HideWithoutLocator implements BooleanGetter<MediaPath>
{
	@Override
	public boolean get(final MediaPath feature)
	{
		return !feature.isWithLocator();
	}
}
