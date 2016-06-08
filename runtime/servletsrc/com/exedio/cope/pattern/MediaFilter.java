/*
 * Copyright (C) 2004-2015  exedio GmbH (www.exedio.com)
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

import static java.util.Objects.requireNonNull;

import com.exedio.cope.Condition;
import com.exedio.cope.Item;
import com.exedio.cope.Join;
import com.exedio.cope.instrument.BooleanGetter;
import com.exedio.cope.instrument.Nullability;
import com.exedio.cope.instrument.NullabilityGetter;
import com.exedio.cope.instrument.Wrap;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;

public abstract class MediaFilter extends MediaPath
{
	private static final long serialVersionUID = 1l;

	private final Media source;

	public MediaFilter(final Media source)
	{
		this.source = requireNonNull(source, "source");
	}

	public final Media getSource()
	{
		return source;
	}

	public abstract Set<String> getSupportedSourceContentTypes();

	@Override
	public boolean isFinal()
	{
		return source.isFinal();
	}

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
	@Wrap(order=10, doc="Returns a URL the content of {0} is available under, falling back to source if necessary.", hide=URLWithFallbackToSourceGetter.class, nullability=NullableIfSourceOptional.class)
	public final String getURLWithFallbackToSource(@Nonnull final Item item)
	{
		final String myURL = getURL(item);
		return (myURL!=null) ? myURL : source.getURL(item);
	}

	/**
	 * Returns the same result as {@link #getLocator(Item) getLocator},
	 * if this filter supports filtering the {@link #getSource() source media} for this item.
	 * Otherwise it returns {@link #getSource()}.{@link #getLocator(Item) getLocator(item)}.
	 */
	@Wrap(order=20, doc="Returns a Locator the content of {0} is available under, falling back to source if necessary.", hide=URLWithFallbackToSourceGetter.class, nullability=NullableIfSourceOptional.class)
	public final Locator getLocatorWithFallbackToSource(@Nonnull final Item item)
	{
		final Locator myURL = getLocator(item);
		return (myURL!=null) ? myURL : source.getLocator(item);
	}

	private static final class URLWithFallbackToSourceGetter implements BooleanGetter<MediaFilter>
	{
		@SuppressWarnings("synthetic-access")
		public boolean get(final MediaFilter feature)
		{
			return feature.canFilterAllSourceContentTypes();
		}
	}

	private boolean canFilterAllSourceContentTypes()
	{
		final List<String> contentTypesAllowed = source.getContentTypesAllowed();
		if(contentTypesAllowed==null)
			return false;

		final Set<String> supportedSourceContentTypes = getSupportedSourceContentTypes();

		for(final String s : contentTypesAllowed)
			if(!supportedSourceContentTypes.contains(s))
				return false;

		return true;
	}

	@Override
	public final boolean isMandatory()
	{
		return source.isMandatory() && canFilterAllSourceContentTypes();
	}

	@Override
	public final Condition isNull()
	{
		return source.isNull(); // TODO check for getSupportedSourceContentTypes
	}

	@Override
	public final Condition isNull(final Join join)
	{
		return source.isNull(join); // TODO check for getSupportedSourceContentTypes
	}

	@Override
	public final Condition isNotNull()
	{
		return source.isNotNull(); // TODO check for getSupportedSourceContentTypes
	}

	@Override
	public final Condition isNotNull(final Join join)
	{
		return source.isNotNull(join); // TODO check for getSupportedSourceContentTypes
	}

	static final class NullableIfSourceOptional implements NullabilityGetter<MediaFilter>
	{
		@Override
		public Nullability getNullability(final MediaFilter feature)
		{
			return Nullability.forMandatory(feature.source.isMandatory());
		}
	}
}
