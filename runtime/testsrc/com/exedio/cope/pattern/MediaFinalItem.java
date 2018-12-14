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
import static com.exedio.cope.instrument.Visibility.PACKAGE;

import com.exedio.cope.Item;
import com.exedio.cope.StringField;
import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.instrument.WrapperType;

@WrapperType(genericConstructor=PACKAGE)
public final class MediaFinalItem extends Item
{
	static final StringField name = new StringField().optional();

	@Wrapper(wrap="getURL", visibility=NONE)
	static final Media file = new Media().toFinal().optional().lengthMax(20);

	/**
	 * Creates a new MediaMandatoryItem with all the fields initially needed.
	 */
	public MediaFinalItem(final byte[] fileBody, final String fileContentType)
	{
		this(Media.toValue(fileBody, fileContentType));
	}


	/**
	 * Creates a new MediaFinalItem with all the fields initially needed.
	 * @param file the initial value for field {@link #file}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	MediaFinalItem(
				@javax.annotation.Nullable final com.exedio.cope.pattern.Media.Value file)
	{
		this(new com.exedio.cope.SetValue<?>[]{
			MediaFinalItem.file.map(file),
		});
	}

	/**
	 * Creates a new MediaFinalItem and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	MediaFinalItem(final com.exedio.cope.SetValue<?>... setValues)
	{
		super(setValues);
	}

	/**
	 * Returns the value of {@link #name}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	java.lang.String getName()
	{
		return MediaFinalItem.name.get(this);
	}

	/**
	 * Sets a new value for {@link #name}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setName(@javax.annotation.Nullable final java.lang.String name)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		MediaFinalItem.name.set(this,name);
	}

	/**
	 * Returns a Locator the content of {@link #file} is available under.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLocator")
	@javax.annotation.Nullable
	com.exedio.cope.pattern.MediaPath.Locator getFileLocator()
	{
		return MediaFinalItem.file.getLocator(this);
	}

	/**
	 * Returns the content type of the media {@link #file}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getContentType")
	@javax.annotation.Nullable
	java.lang.String getFileContentType()
	{
		return MediaFinalItem.file.getContentType(this);
	}

	/**
	 * Returns whether media {@link #file} is null.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="isNull")
	boolean isFileNull()
	{
		return MediaFinalItem.file.isNull(this);
	}

	/**
	 * Returns the last modification date of media {@link #file}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLastModified")
	@javax.annotation.Nullable
	java.util.Date getFileLastModified()
	{
		return MediaFinalItem.file.getLastModified(this);
	}

	/**
	 * Returns the body length of the media {@link #file}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLength")
	long getFileLength()
	{
		return MediaFinalItem.file.getLength(this);
	}

	/**
	 * Returns the body of the media {@link #file}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
	@javax.annotation.Nullable
	byte[] getFileBody()
	{
		return MediaFinalItem.file.getBody(this);
	}

	/**
	 * Writes the body of media {@link #file} into the given stream.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing <tt>body</tt> throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
	void getFileBody(@javax.annotation.Nonnull final java.io.OutputStream body)
			throws
				java.io.IOException
	{
		MediaFinalItem.file.getBody(this,body);
	}

	/**
	 * Writes the body of media {@link #file} into the given file.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing <tt>body</tt> throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
	void getFileBody(@javax.annotation.Nonnull final java.nio.file.Path body)
			throws
				java.io.IOException
	{
		MediaFinalItem.file.getBody(this,body);
	}

	/**
	 * Writes the body of media {@link #file} into the given file.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing <tt>body</tt> throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
	void getFileBody(@javax.annotation.Nonnull final java.io.File body)
			throws
				java.io.IOException
	{
		MediaFinalItem.file.getBody(this,body);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for mediaFinalItem.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<MediaFinalItem> TYPE = com.exedio.cope.TypesBound.newType(MediaFinalItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@SuppressWarnings("unused") private MediaFinalItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
