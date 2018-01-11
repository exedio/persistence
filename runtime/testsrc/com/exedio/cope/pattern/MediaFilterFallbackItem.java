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

import com.exedio.cope.Item;

final class MediaFilterFallbackItem extends Item
{
	static final Media any = new Media();
	static final MediaFilterFallbackFeature anFilter = new MediaFilterFallbackFeature(any);

	static final Media nonSupported = new Media().contentTypes("supported/alpha", "nonSupported/beta");
	static final MediaFilterFallbackFeature nonSupportedFilter = new MediaFilterFallbackFeature(nonSupported);

	static final Media supported = new Media().contentType("supported/alpha");
	static final MediaFilterFallbackFeature supportedFilter = new MediaFilterFallbackFeature(supported);

	static final Media supportedTwo = new Media().contentTypes("supported/alpha", "supported/beta");
	static final MediaFilterFallbackFeature supportedTwoFilter = new MediaFilterFallbackFeature(supportedTwo);

	/**
	 * Creates a new MediaFilterFallbackItem with all the fields initially needed.
	 * @param any the initial value for field {@link #any}.
	 * @param nonSupported the initial value for field {@link #nonSupported}.
	 * @param supported the initial value for field {@link #supported}.
	 * @param supportedTwo the initial value for field {@link #supportedTwo}.
	 * @throws com.exedio.cope.MandatoryViolationException if any, nonSupported, supported, supportedTwo is null.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	MediaFilterFallbackItem(
				@javax.annotation.Nonnull final com.exedio.cope.pattern.Media.Value any,
				@javax.annotation.Nonnull final com.exedio.cope.pattern.Media.Value nonSupported,
				@javax.annotation.Nonnull final com.exedio.cope.pattern.Media.Value supported,
				@javax.annotation.Nonnull final com.exedio.cope.pattern.Media.Value supportedTwo)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			MediaFilterFallbackItem.any.map(any),
			MediaFilterFallbackItem.nonSupported.map(nonSupported),
			MediaFilterFallbackItem.supported.map(supported),
			MediaFilterFallbackItem.supportedTwo.map(supportedTwo),
		});
	}

	/**
	 * Creates a new MediaFilterFallbackItem and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private MediaFilterFallbackItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns a URL the content of {@link #any} is available under.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getURL")
	@javax.annotation.Nonnull
	java.lang.String getAnyURL()
	{
		return MediaFilterFallbackItem.any.getURL(this);
	}

	/**
	 * Returns a Locator the content of {@link #any} is available under.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLocator")
	@javax.annotation.Nonnull
	com.exedio.cope.pattern.MediaPath.Locator getAnyLocator()
	{
		return MediaFilterFallbackItem.any.getLocator(this);
	}

	/**
	 * Returns the content type of the media {@link #any}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getContentType")
	@javax.annotation.Nonnull
	java.lang.String getAnyContentType()
	{
		return MediaFilterFallbackItem.any.getContentType(this);
	}

	/**
	 * Returns the last modification date of media {@link #any}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLastModified")
	@javax.annotation.Nonnull
	java.util.Date getAnyLastModified()
	{
		return MediaFilterFallbackItem.any.getLastModified(this);
	}

	/**
	 * Returns the body length of the media {@link #any}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLength")
	long getAnyLength()
	{
		return MediaFilterFallbackItem.any.getLength(this);
	}

	/**
	 * Returns the body of the media {@link #any}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
	@javax.annotation.Nonnull
	byte[] getAnyBody()
	{
		return MediaFilterFallbackItem.any.getBody(this);
	}

	/**
	 * Writes the body of media {@link #any} into the given stream.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
	void getAnyBody(@javax.annotation.Nonnull final java.io.OutputStream body)
			throws
				java.io.IOException
	{
		MediaFilterFallbackItem.any.getBody(this,body);
	}

	/**
	 * Writes the body of media {@link #any} into the given file.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
	void getAnyBody(@javax.annotation.Nonnull final java.nio.file.Path body)
			throws
				java.io.IOException
	{
		MediaFilterFallbackItem.any.getBody(this,body);
	}

	/**
	 * Writes the body of media {@link #any} into the given file.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
	void getAnyBody(@javax.annotation.Nonnull final java.io.File body)
			throws
				java.io.IOException
	{
		MediaFilterFallbackItem.any.getBody(this,body);
	}

	/**
	 * Sets the content of media {@link #any}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setAny(@javax.annotation.Nonnull final com.exedio.cope.pattern.Media.Value any)
			throws
				java.io.IOException
	{
		MediaFilterFallbackItem.any.set(this,any);
	}

	/**
	 * Sets the content of media {@link #any}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setAny(@javax.annotation.Nonnull final byte[] body,@javax.annotation.Nonnull final java.lang.String contentType)
	{
		MediaFilterFallbackItem.any.set(this,body,contentType);
	}

	/**
	 * Sets the content of media {@link #any}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setAny(@javax.annotation.Nonnull final java.io.InputStream body,@javax.annotation.Nonnull final java.lang.String contentType)
			throws
				java.io.IOException
	{
		MediaFilterFallbackItem.any.set(this,body,contentType);
	}

	/**
	 * Sets the content of media {@link #any}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setAny(@javax.annotation.Nonnull final java.nio.file.Path body,@javax.annotation.Nonnull final java.lang.String contentType)
			throws
				java.io.IOException
	{
		MediaFilterFallbackItem.any.set(this,body,contentType);
	}

	/**
	 * Sets the content of media {@link #any}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setAny(@javax.annotation.Nonnull final java.io.File body,@javax.annotation.Nonnull final java.lang.String contentType)
			throws
				java.io.IOException
	{
		MediaFilterFallbackItem.any.set(this,body,contentType);
	}

	/**
	 * Returns a URL the content of {@link #anFilter} is available under.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getURL")
	@javax.annotation.Nullable
	java.lang.String getAnFilterURL()
	{
		return MediaFilterFallbackItem.anFilter.getURL(this);
	}

	/**
	 * Returns a Locator the content of {@link #anFilter} is available under.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLocator")
	@javax.annotation.Nullable
	com.exedio.cope.pattern.MediaPath.Locator getAnFilterLocator()
	{
		return MediaFilterFallbackItem.anFilter.getLocator(this);
	}

	/**
	 * Returns a URL the content of {@link #anFilter} is available under, falling back to source if necessary.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getURLWithFallbackToSource")
	@javax.annotation.Nonnull
	java.lang.String getAnFilterURLWithFallbackToSource()
	{
		return MediaFilterFallbackItem.anFilter.getURLWithFallbackToSource(this);
	}

	/**
	 * Returns a Locator the content of {@link #anFilter} is available under, falling back to source if necessary.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLocatorWithFallbackToSource")
	@javax.annotation.Nonnull
	com.exedio.cope.pattern.MediaPath.Locator getAnFilterLocatorWithFallbackToSource()
	{
		return MediaFilterFallbackItem.anFilter.getLocatorWithFallbackToSource(this);
	}

	/**
	 * Returns a URL the content of {@link #nonSupported} is available under.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getURL")
	@javax.annotation.Nonnull
	java.lang.String getNonSupportedURL()
	{
		return MediaFilterFallbackItem.nonSupported.getURL(this);
	}

	/**
	 * Returns a Locator the content of {@link #nonSupported} is available under.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLocator")
	@javax.annotation.Nonnull
	com.exedio.cope.pattern.MediaPath.Locator getNonSupportedLocator()
	{
		return MediaFilterFallbackItem.nonSupported.getLocator(this);
	}

	/**
	 * Returns the content type of the media {@link #nonSupported}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getContentType")
	@javax.annotation.Nonnull
	java.lang.String getNonSupportedContentType()
	{
		return MediaFilterFallbackItem.nonSupported.getContentType(this);
	}

	/**
	 * Returns the last modification date of media {@link #nonSupported}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLastModified")
	@javax.annotation.Nonnull
	java.util.Date getNonSupportedLastModified()
	{
		return MediaFilterFallbackItem.nonSupported.getLastModified(this);
	}

	/**
	 * Returns the body length of the media {@link #nonSupported}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLength")
	long getNonSupportedLength()
	{
		return MediaFilterFallbackItem.nonSupported.getLength(this);
	}

	/**
	 * Returns the body of the media {@link #nonSupported}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
	@javax.annotation.Nonnull
	byte[] getNonSupportedBody()
	{
		return MediaFilterFallbackItem.nonSupported.getBody(this);
	}

	/**
	 * Writes the body of media {@link #nonSupported} into the given stream.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
	void getNonSupportedBody(@javax.annotation.Nonnull final java.io.OutputStream body)
			throws
				java.io.IOException
	{
		MediaFilterFallbackItem.nonSupported.getBody(this,body);
	}

	/**
	 * Writes the body of media {@link #nonSupported} into the given file.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
	void getNonSupportedBody(@javax.annotation.Nonnull final java.nio.file.Path body)
			throws
				java.io.IOException
	{
		MediaFilterFallbackItem.nonSupported.getBody(this,body);
	}

	/**
	 * Writes the body of media {@link #nonSupported} into the given file.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
	void getNonSupportedBody(@javax.annotation.Nonnull final java.io.File body)
			throws
				java.io.IOException
	{
		MediaFilterFallbackItem.nonSupported.getBody(this,body);
	}

	/**
	 * Sets the content of media {@link #nonSupported}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setNonSupported(@javax.annotation.Nonnull final com.exedio.cope.pattern.Media.Value nonSupported)
			throws
				java.io.IOException
	{
		MediaFilterFallbackItem.nonSupported.set(this,nonSupported);
	}

	/**
	 * Sets the content of media {@link #nonSupported}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setNonSupported(@javax.annotation.Nonnull final byte[] body,@javax.annotation.Nonnull final java.lang.String contentType)
	{
		MediaFilterFallbackItem.nonSupported.set(this,body,contentType);
	}

	/**
	 * Sets the content of media {@link #nonSupported}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setNonSupported(@javax.annotation.Nonnull final java.io.InputStream body,@javax.annotation.Nonnull final java.lang.String contentType)
			throws
				java.io.IOException
	{
		MediaFilterFallbackItem.nonSupported.set(this,body,contentType);
	}

	/**
	 * Sets the content of media {@link #nonSupported}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setNonSupported(@javax.annotation.Nonnull final java.nio.file.Path body,@javax.annotation.Nonnull final java.lang.String contentType)
			throws
				java.io.IOException
	{
		MediaFilterFallbackItem.nonSupported.set(this,body,contentType);
	}

	/**
	 * Sets the content of media {@link #nonSupported}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setNonSupported(@javax.annotation.Nonnull final java.io.File body,@javax.annotation.Nonnull final java.lang.String contentType)
			throws
				java.io.IOException
	{
		MediaFilterFallbackItem.nonSupported.set(this,body,contentType);
	}

	/**
	 * Returns a URL the content of {@link #nonSupportedFilter} is available under.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getURL")
	@javax.annotation.Nullable
	java.lang.String getNonSupportedFilterURL()
	{
		return MediaFilterFallbackItem.nonSupportedFilter.getURL(this);
	}

	/**
	 * Returns a Locator the content of {@link #nonSupportedFilter} is available under.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLocator")
	@javax.annotation.Nullable
	com.exedio.cope.pattern.MediaPath.Locator getNonSupportedFilterLocator()
	{
		return MediaFilterFallbackItem.nonSupportedFilter.getLocator(this);
	}

	/**
	 * Returns a URL the content of {@link #nonSupportedFilter} is available under, falling back to source if necessary.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getURLWithFallbackToSource")
	@javax.annotation.Nonnull
	java.lang.String getNonSupportedFilterURLWithFallbackToSource()
	{
		return MediaFilterFallbackItem.nonSupportedFilter.getURLWithFallbackToSource(this);
	}

	/**
	 * Returns a Locator the content of {@link #nonSupportedFilter} is available under, falling back to source if necessary.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLocatorWithFallbackToSource")
	@javax.annotation.Nonnull
	com.exedio.cope.pattern.MediaPath.Locator getNonSupportedFilterLocatorWithFallbackToSource()
	{
		return MediaFilterFallbackItem.nonSupportedFilter.getLocatorWithFallbackToSource(this);
	}

	/**
	 * Returns a URL the content of {@link #supported} is available under.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getURL")
	@javax.annotation.Nonnull
	java.lang.String getSupportedURL()
	{
		return MediaFilterFallbackItem.supported.getURL(this);
	}

	/**
	 * Returns a Locator the content of {@link #supported} is available under.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLocator")
	@javax.annotation.Nonnull
	com.exedio.cope.pattern.MediaPath.Locator getSupportedLocator()
	{
		return MediaFilterFallbackItem.supported.getLocator(this);
	}

	/**
	 * Returns the last modification date of media {@link #supported}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLastModified")
	@javax.annotation.Nonnull
	java.util.Date getSupportedLastModified()
	{
		return MediaFilterFallbackItem.supported.getLastModified(this);
	}

	/**
	 * Returns the body length of the media {@link #supported}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLength")
	long getSupportedLength()
	{
		return MediaFilterFallbackItem.supported.getLength(this);
	}

	/**
	 * Returns the body of the media {@link #supported}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
	@javax.annotation.Nonnull
	byte[] getSupportedBody()
	{
		return MediaFilterFallbackItem.supported.getBody(this);
	}

	/**
	 * Writes the body of media {@link #supported} into the given stream.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
	void getSupportedBody(@javax.annotation.Nonnull final java.io.OutputStream body)
			throws
				java.io.IOException
	{
		MediaFilterFallbackItem.supported.getBody(this,body);
	}

	/**
	 * Writes the body of media {@link #supported} into the given file.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
	void getSupportedBody(@javax.annotation.Nonnull final java.nio.file.Path body)
			throws
				java.io.IOException
	{
		MediaFilterFallbackItem.supported.getBody(this,body);
	}

	/**
	 * Writes the body of media {@link #supported} into the given file.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
	void getSupportedBody(@javax.annotation.Nonnull final java.io.File body)
			throws
				java.io.IOException
	{
		MediaFilterFallbackItem.supported.getBody(this,body);
	}

	/**
	 * Sets the content of media {@link #supported}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setSupported(@javax.annotation.Nonnull final com.exedio.cope.pattern.Media.Value supported)
			throws
				java.io.IOException
	{
		MediaFilterFallbackItem.supported.set(this,supported);
	}

	/**
	 * Sets the content of media {@link #supported}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setSupported(@javax.annotation.Nonnull final byte[] body,@javax.annotation.Nonnull final java.lang.String contentType)
	{
		MediaFilterFallbackItem.supported.set(this,body,contentType);
	}

	/**
	 * Sets the content of media {@link #supported}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setSupported(@javax.annotation.Nonnull final java.io.InputStream body,@javax.annotation.Nonnull final java.lang.String contentType)
			throws
				java.io.IOException
	{
		MediaFilterFallbackItem.supported.set(this,body,contentType);
	}

	/**
	 * Sets the content of media {@link #supported}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setSupported(@javax.annotation.Nonnull final java.nio.file.Path body,@javax.annotation.Nonnull final java.lang.String contentType)
			throws
				java.io.IOException
	{
		MediaFilterFallbackItem.supported.set(this,body,contentType);
	}

	/**
	 * Sets the content of media {@link #supported}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setSupported(@javax.annotation.Nonnull final java.io.File body,@javax.annotation.Nonnull final java.lang.String contentType)
			throws
				java.io.IOException
	{
		MediaFilterFallbackItem.supported.set(this,body,contentType);
	}

	/**
	 * Returns a URL the content of {@link #supportedFilter} is available under.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getURL")
	@javax.annotation.Nonnull
	java.lang.String getSupportedFilterURL()
	{
		return MediaFilterFallbackItem.supportedFilter.getURL(this);
	}

	/**
	 * Returns a Locator the content of {@link #supportedFilter} is available under.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLocator")
	@javax.annotation.Nonnull
	com.exedio.cope.pattern.MediaPath.Locator getSupportedFilterLocator()
	{
		return MediaFilterFallbackItem.supportedFilter.getLocator(this);
	}

	/**
	 * Returns a URL the content of {@link #supportedTwo} is available under.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getURL")
	@javax.annotation.Nonnull
	java.lang.String getSupportedTwoURL()
	{
		return MediaFilterFallbackItem.supportedTwo.getURL(this);
	}

	/**
	 * Returns a Locator the content of {@link #supportedTwo} is available under.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLocator")
	@javax.annotation.Nonnull
	com.exedio.cope.pattern.MediaPath.Locator getSupportedTwoLocator()
	{
		return MediaFilterFallbackItem.supportedTwo.getLocator(this);
	}

	/**
	 * Returns the content type of the media {@link #supportedTwo}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getContentType")
	@javax.annotation.Nonnull
	java.lang.String getSupportedTwoContentType()
	{
		return MediaFilterFallbackItem.supportedTwo.getContentType(this);
	}

	/**
	 * Returns the last modification date of media {@link #supportedTwo}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLastModified")
	@javax.annotation.Nonnull
	java.util.Date getSupportedTwoLastModified()
	{
		return MediaFilterFallbackItem.supportedTwo.getLastModified(this);
	}

	/**
	 * Returns the body length of the media {@link #supportedTwo}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLength")
	long getSupportedTwoLength()
	{
		return MediaFilterFallbackItem.supportedTwo.getLength(this);
	}

	/**
	 * Returns the body of the media {@link #supportedTwo}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
	@javax.annotation.Nonnull
	byte[] getSupportedTwoBody()
	{
		return MediaFilterFallbackItem.supportedTwo.getBody(this);
	}

	/**
	 * Writes the body of media {@link #supportedTwo} into the given stream.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
	void getSupportedTwoBody(@javax.annotation.Nonnull final java.io.OutputStream body)
			throws
				java.io.IOException
	{
		MediaFilterFallbackItem.supportedTwo.getBody(this,body);
	}

	/**
	 * Writes the body of media {@link #supportedTwo} into the given file.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
	void getSupportedTwoBody(@javax.annotation.Nonnull final java.nio.file.Path body)
			throws
				java.io.IOException
	{
		MediaFilterFallbackItem.supportedTwo.getBody(this,body);
	}

	/**
	 * Writes the body of media {@link #supportedTwo} into the given file.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
	void getSupportedTwoBody(@javax.annotation.Nonnull final java.io.File body)
			throws
				java.io.IOException
	{
		MediaFilterFallbackItem.supportedTwo.getBody(this,body);
	}

	/**
	 * Sets the content of media {@link #supportedTwo}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setSupportedTwo(@javax.annotation.Nonnull final com.exedio.cope.pattern.Media.Value supportedTwo)
			throws
				java.io.IOException
	{
		MediaFilterFallbackItem.supportedTwo.set(this,supportedTwo);
	}

	/**
	 * Sets the content of media {@link #supportedTwo}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setSupportedTwo(@javax.annotation.Nonnull final byte[] body,@javax.annotation.Nonnull final java.lang.String contentType)
	{
		MediaFilterFallbackItem.supportedTwo.set(this,body,contentType);
	}

	/**
	 * Sets the content of media {@link #supportedTwo}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setSupportedTwo(@javax.annotation.Nonnull final java.io.InputStream body,@javax.annotation.Nonnull final java.lang.String contentType)
			throws
				java.io.IOException
	{
		MediaFilterFallbackItem.supportedTwo.set(this,body,contentType);
	}

	/**
	 * Sets the content of media {@link #supportedTwo}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setSupportedTwo(@javax.annotation.Nonnull final java.nio.file.Path body,@javax.annotation.Nonnull final java.lang.String contentType)
			throws
				java.io.IOException
	{
		MediaFilterFallbackItem.supportedTwo.set(this,body,contentType);
	}

	/**
	 * Sets the content of media {@link #supportedTwo}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setSupportedTwo(@javax.annotation.Nonnull final java.io.File body,@javax.annotation.Nonnull final java.lang.String contentType)
			throws
				java.io.IOException
	{
		MediaFilterFallbackItem.supportedTwo.set(this,body,contentType);
	}

	/**
	 * Returns a URL the content of {@link #supportedTwoFilter} is available under.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getURL")
	@javax.annotation.Nonnull
	java.lang.String getSupportedTwoFilterURL()
	{
		return MediaFilterFallbackItem.supportedTwoFilter.getURL(this);
	}

	/**
	 * Returns a Locator the content of {@link #supportedTwoFilter} is available under.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLocator")
	@javax.annotation.Nonnull
	com.exedio.cope.pattern.MediaPath.Locator getSupportedTwoFilterLocator()
	{
		return MediaFilterFallbackItem.supportedTwoFilter.getLocator(this);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for mediaFilterFallbackItem.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<MediaFilterFallbackItem> TYPE = com.exedio.cope.TypesBound.newType(MediaFilterFallbackItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private MediaFilterFallbackItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
