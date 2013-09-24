/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.exedio.cope.pattern;

import com.exedio.cope.Condition;
import com.exedio.cope.Item;
import com.exedio.cope.Join;
import com.exedio.cope.instrument.BooleanGetter;
import com.exedio.cope.instrument.Wrap;
import java.util.Date;
import java.util.List;
import java.util.Set;

public abstract class MediaFilter extends MediaPath
{
	private static final long serialVersionUID = 1l;

	private final Media source;

	public MediaFilter(final Media source)
	{
		this.source = source;
		if(source==null)
			throw new NullPointerException("source");
	}

	public final Media getSource()
	{
		return source;
	}

	public abstract Set<String> getSupportedSourceContentTypes();

	@Override
	public final Date getLastModified(final Item item)
	{
		return source.getLastModified(item);
	}

	/**
	 * Returns the same result as {@link #getURL(Item) getURL},
	 * if this filter supports filtering the {@link #getSource() source media} for this item.
	 * Otherwise it returns {@link #getSource()}.{@link #getURL(Item) getURL(item)}.
	 */
	@Wrap(order=10, doc="Returns a URL the content of {0} is available under.", hide=URLWithFallbackToSourceGetter.class) // TODO better text
	public final String getURLWithFallbackToSource(final Item item)
	{
		final String myURL = getURL(item);
		return (myURL!=null) ? myURL : source.getURL(item);
	}

	private static final class URLWithFallbackToSourceGetter implements BooleanGetter<MediaFilter>
	{
		@SuppressWarnings("synthetic-access")
		public boolean get(final MediaFilter feature)
		{
			return !feature.isURLWithFallbackToSourceWrapped();
		}
	}

	private boolean isURLWithFallbackToSourceWrapped()
	{
		final List<String> contentTypesAllowed = source.getContentTypesAllowed();
		if(contentTypesAllowed==null)
			return true;

		final Set<String> supportedSourceContentTypes = getSupportedSourceContentTypes();

		for(final String s : contentTypesAllowed)
			if(!supportedSourceContentTypes.contains(s))
				return true;

		return false;
	}

	@Override
	public final Condition isNull()
	{
		return source.isNull(); // TODO check for getSupportedSourceContentTypes
	}

	@Override
	public Condition isNull(final Join join)
	{
		return source.isNull(join); // TODO check for getSupportedSourceContentTypes
	}

	@Override
	public final Condition isNotNull()
	{
		return source.isNotNull(); // TODO check for getSupportedSourceContentTypes
	}

	@Override
	public Condition isNotNull(final Join join)
	{
		return source.isNotNull(join); // TODO check for getSupportedSourceContentTypes
	}
}
