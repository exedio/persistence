/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.exedio.cope.Condition;
import com.exedio.cope.Item;
import com.exedio.cope.instrument.Wrapper;

public abstract class MediaFilter extends CachedMedia
{
	private final Media source;

	public MediaFilter(final Media source)
	{
		this.source = source;
		if(source==null)
			throw new NullPointerException("source must not be null");
	}

	public final Media getSource()
	{
		return source;
	}

	public abstract Set<String> getSupportedSourceContentTypes();

	@Override
	public List<Wrapper> getWrappers()
	{
		final ArrayList<Wrapper> result = new ArrayList<Wrapper>();
		result.addAll(super.getWrappers());

		result.add(
			new Wrapper("getURLWithFallbackToSource").
			addComment("Returns a URL the content of {0} is available under."). // TODO better text
			setReturn(String.class));
		
		return Collections.unmodifiableList(result);
	}

	@Override
	public final long getLastModified(final Item item)
	{
		return source.getLastModified(item);
	}
	
	public final String getURLWithFallbackToSource(final Item item)
	{
		final String myURL = getURL(item);
		return (myURL!=null) ? myURL : source.getURL(item);
	}

	@Override
	public final Condition isNull()
	{
		return source.isNull(); // TODO check for getSupportedSourceContentTypes
	}

	@Override
	public final Condition isNotNull()
	{
		return source.isNotNull(); // TODO check for getSupportedSourceContentTypes
	}
}
