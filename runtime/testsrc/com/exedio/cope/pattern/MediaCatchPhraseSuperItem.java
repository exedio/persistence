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

import static com.exedio.cope.instrument.Visibility.NONE;

import com.exedio.cope.Item;
import com.exedio.cope.instrument.Wrapper;

public class MediaCatchPhraseSuperItem extends Item
{
	@Wrapper(wrap="getURL", visibility=NONE)
	static final Media feature = new Media().lengthMax(2000).contentType("foo/bar");

	MediaCatchPhraseSuperItem()
	{
		this(Media.toValue(new byte[]{10}, "foo/bar"));
	}


	/**
	 * Creates a new MediaCatchPhraseSuperItem with all the fields initially needed.
	 * @param feature the initial value for field {@link #feature}.
	 * @throws com.exedio.cope.MandatoryViolationException if feature is null.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	MediaCatchPhraseSuperItem(
				@javax.annotation.Nonnull final com.exedio.cope.pattern.Media.Value feature)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			MediaCatchPhraseSuperItem.feature.map(feature),
		});
	}

	/**
	 * Creates a new MediaCatchPhraseSuperItem and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	protected MediaCatchPhraseSuperItem(final com.exedio.cope.SetValue<?>... setValues)
	{
		super(setValues);
	}

	/**
	 * Returns a Locator the content of {@link #feature} is available under.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLocator")
	@javax.annotation.Nonnull
	final com.exedio.cope.pattern.MediaPath.Locator getFeatureLocator()
	{
		return MediaCatchPhraseSuperItem.feature.getLocator(this);
	}

	/**
	 * Returns the last modification date of media {@link #feature}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLastModified")
	@javax.annotation.Nonnull
	final java.util.Date getFeatureLastModified()
	{
		return MediaCatchPhraseSuperItem.feature.getLastModified(this);
	}

	/**
	 * Returns the body length of the media {@link #feature}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLength")
	final long getFeatureLength()
	{
		return MediaCatchPhraseSuperItem.feature.getLength(this);
	}

	/**
	 * Returns the body of the media {@link #feature}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
	@javax.annotation.Nonnull
	final byte[] getFeatureBody()
	{
		return MediaCatchPhraseSuperItem.feature.getBody(this);
	}

	/**
	 * Writes the body of media {@link #feature} into the given stream.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing <tt>body</tt> throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
	final void getFeatureBody(@javax.annotation.Nonnull final java.io.OutputStream body)
			throws
				java.io.IOException
	{
		MediaCatchPhraseSuperItem.feature.getBody(this,body);
	}

	/**
	 * Writes the body of media {@link #feature} into the given file.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing <tt>body</tt> throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
	final void getFeatureBody(@javax.annotation.Nonnull final java.io.File body)
			throws
				java.io.IOException
	{
		MediaCatchPhraseSuperItem.feature.getBody(this,body);
	}

	/**
	 * Sets the content of media {@link #feature}.
	 * @throws java.io.IOException if accessing <tt>body</tt> throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setFeature(@javax.annotation.Nonnull final com.exedio.cope.pattern.Media.Value feature)
			throws
				java.io.IOException
	{
		MediaCatchPhraseSuperItem.feature.set(this,feature);
	}

	/**
	 * Sets the content of media {@link #feature}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setFeature(@javax.annotation.Nonnull final byte[] body,@javax.annotation.Nonnull final java.lang.String contentType)
	{
		MediaCatchPhraseSuperItem.feature.set(this,body,contentType);
	}

	/**
	 * Sets the content of media {@link #feature}.
	 * @throws java.io.IOException if accessing <tt>body</tt> throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setFeature(@javax.annotation.Nonnull final java.io.InputStream body,@javax.annotation.Nonnull final java.lang.String contentType)
			throws
				java.io.IOException
	{
		MediaCatchPhraseSuperItem.feature.set(this,body,contentType);
	}

	/**
	 * Sets the content of media {@link #feature}.
	 * @throws java.io.IOException if accessing <tt>body</tt> throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setFeature(@javax.annotation.Nonnull final java.io.File body,@javax.annotation.Nonnull final java.lang.String contentType)
			throws
				java.io.IOException
	{
		MediaCatchPhraseSuperItem.feature.set(this,body,contentType);
	}

	/**
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for mediaCatchPhraseSuperItem.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<MediaCatchPhraseSuperItem> TYPE = com.exedio.cope.TypesBound.newType(MediaCatchPhraseSuperItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	protected MediaCatchPhraseSuperItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
