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

import static com.exedio.cope.instrument.Visibility.PACKAGE;

import com.exedio.cope.Item;
import com.exedio.cope.instrument.WrapperType;

@WrapperType(genericConstructor=PACKAGE)
public final class UniqueHashedMediaItem extends Item
{
	static final UniqueHashedMedia value = new UniqueHashedMedia(new Media());

	static final MediaThumbnail w200 = new MediaThumbnail(value.getMedia(), 200, 200);
	static final MediaThumbnail w300 = new MediaThumbnail(value.getMedia(), 300, 300);

	/**
	 * Creates a new UniqueHashedMediaItem with all the fields initially needed.
	 * @param value the initial value for field {@link #value}.
	 * @throws com.exedio.cope.MandatoryViolationException if value is null.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	UniqueHashedMediaItem(
				@javax.annotation.Nonnull final com.exedio.cope.pattern.Media.Value value)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			com.exedio.cope.SetValue.map(UniqueHashedMediaItem.value,value),
		});
	}

	/**
	 * Creates a new UniqueHashedMediaItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	UniqueHashedMediaItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns a URL the content of {@link #value} is available under.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getURL")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	java.lang.String getURL()
	{
		return UniqueHashedMediaItem.value.getURL(this);
	}

	/**
	 * Returns a Locator the content of {@link #value} is available under.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLocator")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	com.exedio.cope.pattern.MediaPath.Locator getLocator()
	{
		return UniqueHashedMediaItem.value.getLocator(this);
	}

	/**
	 * Returns the content type of the media {@link #value}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getContentType")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	java.lang.String getContentType()
	{
		return UniqueHashedMediaItem.value.getContentType(this);
	}

	/**
	 * Returns the last modification date of media {@link #value}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLastModified")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	java.util.Date getLastModified()
	{
		return UniqueHashedMediaItem.value.getLastModified(this);
	}

	/**
	 * Returns the body length of the media {@link #value}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLength")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	long getLength()
	{
		return UniqueHashedMediaItem.value.getLength(this);
	}

	/**
	 * Returns the body of the media {@link #value}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getBody")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	byte[] getBody()
	{
		return UniqueHashedMediaItem.value.getBody(this);
	}

	/**
	 * Returns the hash of the media body {@link #value}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getHash")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	java.lang.String getHash()
	{
		return UniqueHashedMediaItem.value.getHash(this);
	}

	/**
	 * Finds a uniqueHashedMediaItem by its hash.
	 * @return null if there is no matching item.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="forHash")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	static UniqueHashedMediaItem forHash(@javax.annotation.Nonnull final java.lang.String valueHash)
	{
		return UniqueHashedMediaItem.value.forHash(UniqueHashedMediaItem.class,valueHash);
	}

	/**
	 * Returns a uniqueHashedMediaItem containing given media value or creates a new one.
	 * @param value shall be equal to field {@link #value}.
	 * @throws java.io.IOException if reading {@code value} throws an IOException.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getOrCreate")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	static UniqueHashedMediaItem getOrCreate(@javax.annotation.Nullable final com.exedio.cope.pattern.Media.Value value)
			throws
				java.io.IOException
	{
		return UniqueHashedMediaItem.value.getOrCreate(UniqueHashedMediaItem.class,value);
	}

	/**
	 * Returns a URL the content of {@link #w200} is available under.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getURL")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.lang.String getW200URL()
	{
		return UniqueHashedMediaItem.w200.getURL(this);
	}

	/**
	 * Returns a Locator the content of {@link #w200} is available under.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLocator")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	com.exedio.cope.pattern.MediaPath.Locator getW200Locator()
	{
		return UniqueHashedMediaItem.w200.getLocator(this);
	}

	/**
	 * Returns a URL the content of {@link #w200} is available under, falling back to source if necessary.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getURLWithFallbackToSource")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	java.lang.String getW200URLWithFallbackToSource()
	{
		return UniqueHashedMediaItem.w200.getURLWithFallbackToSource(this);
	}

	/**
	 * Returns a Locator the content of {@link #w200} is available under, falling back to source if necessary.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLocatorWithFallbackToSource")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	com.exedio.cope.pattern.MediaPath.Locator getW200LocatorWithFallbackToSource()
	{
		return UniqueHashedMediaItem.w200.getLocatorWithFallbackToSource(this);
	}

	/**
	 * Returns the body of {@link #w200}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	byte[] getW200()
			throws
				java.io.IOException
	{
		return UniqueHashedMediaItem.w200.get(this);
	}

	/**
	 * Returns a URL the content of {@link #w300} is available under.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getURL")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.lang.String getW300URL()
	{
		return UniqueHashedMediaItem.w300.getURL(this);
	}

	/**
	 * Returns a Locator the content of {@link #w300} is available under.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLocator")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	com.exedio.cope.pattern.MediaPath.Locator getW300Locator()
	{
		return UniqueHashedMediaItem.w300.getLocator(this);
	}

	/**
	 * Returns a URL the content of {@link #w300} is available under, falling back to source if necessary.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getURLWithFallbackToSource")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	java.lang.String getW300URLWithFallbackToSource()
	{
		return UniqueHashedMediaItem.w300.getURLWithFallbackToSource(this);
	}

	/**
	 * Returns a Locator the content of {@link #w300} is available under, falling back to source if necessary.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLocatorWithFallbackToSource")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	com.exedio.cope.pattern.MediaPath.Locator getW300LocatorWithFallbackToSource()
	{
		return UniqueHashedMediaItem.w300.getLocatorWithFallbackToSource(this);
	}

	/**
	 * Returns the body of {@link #w300}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	byte[] getW300()
			throws
				java.io.IOException
	{
		return UniqueHashedMediaItem.w300.get(this);
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for uniqueHashedMediaItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<UniqueHashedMediaItem> TYPE = com.exedio.cope.TypesBound.newType(UniqueHashedMediaItem.class,UniqueHashedMediaItem::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private UniqueHashedMediaItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
