/*
 * Copyright (C) 2004-2006  exedio GmbH (www.exedio.com)
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

import java.util.Set;

import com.exedio.cope.Item;

public abstract class MediaFilter extends CachedMedia
{
	private final Media media;

	public MediaFilter(final Media media)
	{
		this.media = media;
		if(media==null)
			throw new NullPointerException("media must not be null");
	}

	public final Media getSource()
	{
		return media;
	}

	public abstract Set<String> getSupportedMediaContentTypes();

	@Override
	public final long getLastModified(final Item item)
	{
		return media.getLastModified(item);
	}
	
	public final String getURLWithFallbackToMedia(final Item item)
	{
		final String myURL = getURL(item);
		return (myURL!=null) ? myURL : media.getURL(item);
	}
}
