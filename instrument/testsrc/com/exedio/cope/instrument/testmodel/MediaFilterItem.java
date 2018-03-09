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

package com.exedio.cope.instrument.testmodel;

import com.exedio.cope.Item;
import com.exedio.cope.instrument.testfeature.MediaFilterThatConvertsText;
import com.exedio.cope.pattern.Media;

public class MediaFilterItem extends Item
{
	static final Media text = new Media().contentType("text/plain");
	static final MediaFilterThatConvertsText textConverted = new MediaFilterThatConvertsText(text);

	static final Media optionalText = new Media().contentType("text/plain").optional();
	static final MediaFilterThatConvertsText optionalTextConverted = new MediaFilterThatConvertsText(optionalText);

	static final Media textOrImage = new Media().contentType("text/plain", "image/gif");
	static final MediaFilterThatConvertsText textOrImageConverted = new MediaFilterThatConvertsText(textOrImage);

	/**
	 * Creates a new MediaFilterItem with all the fields initially needed.
	 * @param text the initial value for field {@link #text}.
	 * @param textOrImage the initial value for field {@link #textOrImage}.
	 * @throws com.exedio.cope.MandatoryViolationException if text, textOrImage is null.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	MediaFilterItem(
				@javax.annotation.Nonnull final com.exedio.cope.pattern.Media.Value text,
				@javax.annotation.Nonnull final com.exedio.cope.pattern.Media.Value textOrImage)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			MediaFilterItem.text.map(text),
			MediaFilterItem.textOrImage.map(textOrImage),
		});
	}

	/**
	 * Creates a new MediaFilterItem and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	protected MediaFilterItem(final com.exedio.cope.SetValue<?>... setValues)
	{
		super(setValues);
	}

	/**
	 * Returns a URL the content of {@link #text} is available under.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getURL")
	@javax.annotation.Nonnull
	final java.lang.String getTextURL()
	{
		return MediaFilterItem.text.getURL(this);
	}

	/**
	 * Returns a Locator the content of {@link #text} is available under.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLocator")
	@javax.annotation.Nonnull
	final com.exedio.cope.pattern.MediaPath.Locator getTextLocator()
	{
		return MediaFilterItem.text.getLocator(this);
	}

	/**
	 * Returns the last modification date of media {@link #text}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLastModified")
	@javax.annotation.Nonnull
	final java.util.Date getTextLastModified()
	{
		return MediaFilterItem.text.getLastModified(this);
	}

	/**
	 * Returns the body length of the media {@link #text}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLength")
	final long getTextLength()
	{
		return MediaFilterItem.text.getLength(this);
	}

	/**
	 * Returns the body of the media {@link #text}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
	@javax.annotation.Nonnull
	final byte[] getTextBody()
	{
		return MediaFilterItem.text.getBody(this);
	}

	/**
	 * Writes the body of media {@link #text} into the given stream.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing <tt>body</tt> throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
	final void getTextBody(@javax.annotation.Nonnull final java.io.OutputStream body)
			throws
				java.io.IOException
	{
		MediaFilterItem.text.getBody(this,body);
	}

	/**
	 * Writes the body of media {@link #text} into the given file.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing <tt>body</tt> throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
	final void getTextBody(@javax.annotation.Nonnull final java.io.File body)
			throws
				java.io.IOException
	{
		MediaFilterItem.text.getBody(this,body);
	}

	/**
	 * Sets the content of media {@link #text}.
	 * @throws java.io.IOException if accessing <tt>body</tt> throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setText(@javax.annotation.Nonnull final com.exedio.cope.pattern.Media.Value text)
			throws
				java.io.IOException
	{
		MediaFilterItem.text.set(this,text);
	}

	/**
	 * Sets the content of media {@link #text}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setText(@javax.annotation.Nonnull final byte[] body,@javax.annotation.Nonnull final java.lang.String contentType)
	{
		MediaFilterItem.text.set(this,body,contentType);
	}

	/**
	 * Sets the content of media {@link #text}.
	 * @throws java.io.IOException if accessing <tt>body</tt> throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setText(@javax.annotation.Nonnull final java.io.InputStream body,@javax.annotation.Nonnull final java.lang.String contentType)
			throws
				java.io.IOException
	{
		MediaFilterItem.text.set(this,body,contentType);
	}

	/**
	 * Sets the content of media {@link #text}.
	 * @throws java.io.IOException if accessing <tt>body</tt> throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setText(@javax.annotation.Nonnull final java.io.File body,@javax.annotation.Nonnull final java.lang.String contentType)
			throws
				java.io.IOException
	{
		MediaFilterItem.text.set(this,body,contentType);
	}

	/**
	 * Returns a URL the content of {@link #textConverted} is available under.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getURL")
	@javax.annotation.Nonnull
	final java.lang.String getTextConvertedURL()
	{
		return MediaFilterItem.textConverted.getURL(this);
	}

	/**
	 * Returns a Locator the content of {@link #textConverted} is available under.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLocator")
	@javax.annotation.Nonnull
	final com.exedio.cope.pattern.MediaPath.Locator getTextConvertedLocator()
	{
		return MediaFilterItem.textConverted.getLocator(this);
	}

	/**
	 * Returns the content type of the media {@link #textConverted}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getContentType")
	@javax.annotation.Nonnull
	final java.lang.String getTextConvertedContentType()
	{
		return MediaFilterItem.textConverted.getContentType(this);
	}

	/**
	 * Returns a URL the content of {@link #optionalText} is available under.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getURL")
	@javax.annotation.Nullable
	final java.lang.String getOptionalTextURL()
	{
		return MediaFilterItem.optionalText.getURL(this);
	}

	/**
	 * Returns a Locator the content of {@link #optionalText} is available under.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLocator")
	@javax.annotation.Nullable
	final com.exedio.cope.pattern.MediaPath.Locator getOptionalTextLocator()
	{
		return MediaFilterItem.optionalText.getLocator(this);
	}

	/**
	 * Returns whether media {@link #optionalText} is null.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="isNull")
	final boolean isOptionalTextNull()
	{
		return MediaFilterItem.optionalText.isNull(this);
	}

	/**
	 * Returns the last modification date of media {@link #optionalText}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLastModified")
	@javax.annotation.Nullable
	final java.util.Date getOptionalTextLastModified()
	{
		return MediaFilterItem.optionalText.getLastModified(this);
	}

	/**
	 * Returns the body length of the media {@link #optionalText}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLength")
	final long getOptionalTextLength()
	{
		return MediaFilterItem.optionalText.getLength(this);
	}

	/**
	 * Returns the body of the media {@link #optionalText}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
	@javax.annotation.Nullable
	final byte[] getOptionalTextBody()
	{
		return MediaFilterItem.optionalText.getBody(this);
	}

	/**
	 * Writes the body of media {@link #optionalText} into the given stream.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing <tt>body</tt> throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
	final void getOptionalTextBody(@javax.annotation.Nonnull final java.io.OutputStream body)
			throws
				java.io.IOException
	{
		MediaFilterItem.optionalText.getBody(this,body);
	}

	/**
	 * Writes the body of media {@link #optionalText} into the given file.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing <tt>body</tt> throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
	final void getOptionalTextBody(@javax.annotation.Nonnull final java.io.File body)
			throws
				java.io.IOException
	{
		MediaFilterItem.optionalText.getBody(this,body);
	}

	/**
	 * Sets the content of media {@link #optionalText}.
	 * @throws java.io.IOException if accessing <tt>body</tt> throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setOptionalText(@javax.annotation.Nullable final com.exedio.cope.pattern.Media.Value optionalText)
			throws
				java.io.IOException
	{
		MediaFilterItem.optionalText.set(this,optionalText);
	}

	/**
	 * Sets the content of media {@link #optionalText}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setOptionalText(@javax.annotation.Nullable final byte[] body,@javax.annotation.Nullable final java.lang.String contentType)
	{
		MediaFilterItem.optionalText.set(this,body,contentType);
	}

	/**
	 * Sets the content of media {@link #optionalText}.
	 * @throws java.io.IOException if accessing <tt>body</tt> throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setOptionalText(@javax.annotation.Nullable final java.io.InputStream body,@javax.annotation.Nullable final java.lang.String contentType)
			throws
				java.io.IOException
	{
		MediaFilterItem.optionalText.set(this,body,contentType);
	}

	/**
	 * Sets the content of media {@link #optionalText}.
	 * @throws java.io.IOException if accessing <tt>body</tt> throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setOptionalText(@javax.annotation.Nullable final java.io.File body,@javax.annotation.Nullable final java.lang.String contentType)
			throws
				java.io.IOException
	{
		MediaFilterItem.optionalText.set(this,body,contentType);
	}

	/**
	 * Returns a URL the content of {@link #optionalTextConverted} is available under.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getURL")
	@javax.annotation.Nullable
	final java.lang.String getOptionalTextConvertedURL()
	{
		return MediaFilterItem.optionalTextConverted.getURL(this);
	}

	/**
	 * Returns a Locator the content of {@link #optionalTextConverted} is available under.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLocator")
	@javax.annotation.Nullable
	final com.exedio.cope.pattern.MediaPath.Locator getOptionalTextConvertedLocator()
	{
		return MediaFilterItem.optionalTextConverted.getLocator(this);
	}

	/**
	 * Returns the content type of the media {@link #optionalTextConverted}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getContentType")
	@javax.annotation.Nullable
	final java.lang.String getOptionalTextConvertedContentType()
	{
		return MediaFilterItem.optionalTextConverted.getContentType(this);
	}

	/**
	 * Returns a URL the content of {@link #textOrImage} is available under.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getURL")
	@javax.annotation.Nonnull
	final java.lang.String getTextOrImageURL()
	{
		return MediaFilterItem.textOrImage.getURL(this);
	}

	/**
	 * Returns a Locator the content of {@link #textOrImage} is available under.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLocator")
	@javax.annotation.Nonnull
	final com.exedio.cope.pattern.MediaPath.Locator getTextOrImageLocator()
	{
		return MediaFilterItem.textOrImage.getLocator(this);
	}

	/**
	 * Returns the content type of the media {@link #textOrImage}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getContentType")
	@javax.annotation.Nonnull
	final java.lang.String getTextOrImageContentType()
	{
		return MediaFilterItem.textOrImage.getContentType(this);
	}

	/**
	 * Returns the last modification date of media {@link #textOrImage}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLastModified")
	@javax.annotation.Nonnull
	final java.util.Date getTextOrImageLastModified()
	{
		return MediaFilterItem.textOrImage.getLastModified(this);
	}

	/**
	 * Returns the body length of the media {@link #textOrImage}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLength")
	final long getTextOrImageLength()
	{
		return MediaFilterItem.textOrImage.getLength(this);
	}

	/**
	 * Returns the body of the media {@link #textOrImage}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
	@javax.annotation.Nonnull
	final byte[] getTextOrImageBody()
	{
		return MediaFilterItem.textOrImage.getBody(this);
	}

	/**
	 * Writes the body of media {@link #textOrImage} into the given stream.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing <tt>body</tt> throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
	final void getTextOrImageBody(@javax.annotation.Nonnull final java.io.OutputStream body)
			throws
				java.io.IOException
	{
		MediaFilterItem.textOrImage.getBody(this,body);
	}

	/**
	 * Writes the body of media {@link #textOrImage} into the given file.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing <tt>body</tt> throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
	final void getTextOrImageBody(@javax.annotation.Nonnull final java.io.File body)
			throws
				java.io.IOException
	{
		MediaFilterItem.textOrImage.getBody(this,body);
	}

	/**
	 * Sets the content of media {@link #textOrImage}.
	 * @throws java.io.IOException if accessing <tt>body</tt> throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setTextOrImage(@javax.annotation.Nonnull final com.exedio.cope.pattern.Media.Value textOrImage)
			throws
				java.io.IOException
	{
		MediaFilterItem.textOrImage.set(this,textOrImage);
	}

	/**
	 * Sets the content of media {@link #textOrImage}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setTextOrImage(@javax.annotation.Nonnull final byte[] body,@javax.annotation.Nonnull final java.lang.String contentType)
	{
		MediaFilterItem.textOrImage.set(this,body,contentType);
	}

	/**
	 * Sets the content of media {@link #textOrImage}.
	 * @throws java.io.IOException if accessing <tt>body</tt> throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setTextOrImage(@javax.annotation.Nonnull final java.io.InputStream body,@javax.annotation.Nonnull final java.lang.String contentType)
			throws
				java.io.IOException
	{
		MediaFilterItem.textOrImage.set(this,body,contentType);
	}

	/**
	 * Sets the content of media {@link #textOrImage}.
	 * @throws java.io.IOException if accessing <tt>body</tt> throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setTextOrImage(@javax.annotation.Nonnull final java.io.File body,@javax.annotation.Nonnull final java.lang.String contentType)
			throws
				java.io.IOException
	{
		MediaFilterItem.textOrImage.set(this,body,contentType);
	}

	/**
	 * Returns a URL the content of {@link #textOrImageConverted} is available under.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getURL")
	@javax.annotation.Nullable
	final java.lang.String getTextOrImageConvertedURL()
	{
		return MediaFilterItem.textOrImageConverted.getURL(this);
	}

	/**
	 * Returns a Locator the content of {@link #textOrImageConverted} is available under.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLocator")
	@javax.annotation.Nullable
	final com.exedio.cope.pattern.MediaPath.Locator getTextOrImageConvertedLocator()
	{
		return MediaFilterItem.textOrImageConverted.getLocator(this);
	}

	/**
	 * Returns the content type of the media {@link #textOrImageConverted}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getContentType")
	@javax.annotation.Nullable
	final java.lang.String getTextOrImageConvertedContentType()
	{
		return MediaFilterItem.textOrImageConverted.getContentType(this);
	}

	/**
	 * Returns a URL the content of {@link #textOrImageConverted} is available under, falling back to source if necessary.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getURLWithFallbackToSource")
	@javax.annotation.Nonnull
	final java.lang.String getTextOrImageConvertedURLWithFallbackToSource()
	{
		return MediaFilterItem.textOrImageConverted.getURLWithFallbackToSource(this);
	}

	/**
	 * Returns a Locator the content of {@link #textOrImageConverted} is available under, falling back to source if necessary.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLocatorWithFallbackToSource")
	@javax.annotation.Nonnull
	final com.exedio.cope.pattern.MediaPath.Locator getTextOrImageConvertedLocatorWithFallbackToSource()
	{
		return MediaFilterItem.textOrImageConverted.getLocatorWithFallbackToSource(this);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for mediaFilterItem.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<MediaFilterItem> TYPE = com.exedio.cope.TypesBound.newType(MediaFilterItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	protected MediaFilterItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
