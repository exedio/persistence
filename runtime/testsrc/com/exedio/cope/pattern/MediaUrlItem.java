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

import com.exedio.cope.CopeSchemaName;
import com.exedio.cope.Item;
import com.exedio.cope.StringField;
import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.instrument.WrapperInitial;

public final class MediaUrlItem extends Item implements MediaUrlCatchphraseProvider
{
	@WrapperInitial
	@CopeSchemaName("phrase")
	static final StringField catchphrase = new StringField().optional();


	@Wrapper(wrap="getURL", visibility=NONE)
	static final Media foto = new Media().optional().lengthMax(2000).contentType("image/jpeg");

	@Wrapper(wrap="getURL", visibility=NONE)
	@PreventUrlGuessing
	static final Media fotoSecure = new Media().optional().lengthMax(2000).contentType("image/jpeg");

	@Wrapper(wrap="getURL", visibility=NONE)
	@UrlFingerPrinting
	static final Media fotoFinger = new Media().optional().lengthMax(2000).contentType("image/jpeg");

	@Wrapper(wrap="getURL", visibility=NONE)
	@PreventUrlGuessing @UrlFingerPrinting
	static final Media fotoSecFin = new Media().optional().lengthMax(2000).contentType("image/jpeg");

	@Wrapper(wrap="getURL", visibility=NONE)
	static final Media file = new Media().optional().lengthMax(2000).contentType("foo/bar");

	@Wrapper(wrap="getURL", visibility=NONE)
	@PreventUrlGuessing
	static final Media fileSecure = new Media().optional().lengthMax(2000).contentType("foo/bar");

	@Wrapper(wrap="getURL", visibility=NONE)
	@UrlFingerPrinting
	static final Media fileFinger = new Media().optional().lengthMax(2000).contentType("foo/bar");

	@Wrapper(wrap="getURL", visibility=NONE)
	@PreventUrlGuessing @UrlFingerPrinting
	static final Media fileSecFin = new Media().optional().lengthMax(2000).contentType("foo/bar");


	@Override
	public String getMediaUrlCatchphrase(final MediaPath path)
	{
		return getCatchphrase();
	}


	/**
	 * Creates a new MediaUrlItem with all the fields initially needed.
	 * @param catchphrase the initial value for field {@link #catchphrase}.
	 * @throws com.exedio.cope.StringLengthViolationException if catchphrase violates its length constraint.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	MediaUrlItem(
				@javax.annotation.Nullable final java.lang.String catchphrase)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			MediaUrlItem.catchphrase.map(catchphrase),
		});
	}

	/**
	 * Creates a new MediaUrlItem and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private MediaUrlItem(final com.exedio.cope.SetValue<?>... setValues)
	{
		super(setValues);
	}

	/**
	 * Returns the value of {@link #catchphrase}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	java.lang.String getCatchphrase()
	{
		return MediaUrlItem.catchphrase.get(this);
	}

	/**
	 * Sets a new value for {@link #catchphrase}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setCatchphrase(@javax.annotation.Nullable final java.lang.String catchphrase)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		MediaUrlItem.catchphrase.set(this,catchphrase);
	}

	/**
	 * Returns a Locator the content of {@link #foto} is available under.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLocator")
	@javax.annotation.Nullable
	com.exedio.cope.pattern.MediaPath.Locator getFotoLocator()
	{
		return MediaUrlItem.foto.getLocator(this);
	}

	/**
	 * Returns whether media {@link #foto} is null.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="isNull")
	boolean isFotoNull()
	{
		return MediaUrlItem.foto.isNull(this);
	}

	/**
	 * Returns the last modification date of media {@link #foto}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLastModified")
	@javax.annotation.Nullable
	java.util.Date getFotoLastModified()
	{
		return MediaUrlItem.foto.getLastModified(this);
	}

	/**
	 * Returns the body length of the media {@link #foto}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLength")
	long getFotoLength()
	{
		return MediaUrlItem.foto.getLength(this);
	}

	/**
	 * Returns the body of the media {@link #foto}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
	@javax.annotation.Nullable
	byte[] getFotoBody()
	{
		return MediaUrlItem.foto.getBody(this);
	}

	/**
	 * Writes the body of media {@link #foto} into the given stream.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
	void getFotoBody(@javax.annotation.Nonnull final java.io.OutputStream body)
			throws
				java.io.IOException
	{
		MediaUrlItem.foto.getBody(this,body);
	}

	/**
	 * Writes the body of media {@link #foto} into the given file.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
	void getFotoBody(@javax.annotation.Nonnull final java.nio.file.Path body)
			throws
				java.io.IOException
	{
		MediaUrlItem.foto.getBody(this,body);
	}

	/**
	 * Writes the body of media {@link #foto} into the given file.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
	void getFotoBody(@javax.annotation.Nonnull final java.io.File body)
			throws
				java.io.IOException
	{
		MediaUrlItem.foto.getBody(this,body);
	}

	/**
	 * Sets the content of media {@link #foto}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setFoto(@javax.annotation.Nullable final com.exedio.cope.pattern.Media.Value foto)
			throws
				java.io.IOException
	{
		MediaUrlItem.foto.set(this,foto);
	}

	/**
	 * Sets the content of media {@link #foto}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setFoto(@javax.annotation.Nullable final byte[] body,@javax.annotation.Nullable final java.lang.String contentType)
	{
		MediaUrlItem.foto.set(this,body,contentType);
	}

	/**
	 * Sets the content of media {@link #foto}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setFoto(@javax.annotation.Nullable final java.io.InputStream body,@javax.annotation.Nullable final java.lang.String contentType)
			throws
				java.io.IOException
	{
		MediaUrlItem.foto.set(this,body,contentType);
	}

	/**
	 * Sets the content of media {@link #foto}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setFoto(@javax.annotation.Nullable final java.nio.file.Path body,@javax.annotation.Nullable final java.lang.String contentType)
			throws
				java.io.IOException
	{
		MediaUrlItem.foto.set(this,body,contentType);
	}

	/**
	 * Sets the content of media {@link #foto}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setFoto(@javax.annotation.Nullable final java.io.File body,@javax.annotation.Nullable final java.lang.String contentType)
			throws
				java.io.IOException
	{
		MediaUrlItem.foto.set(this,body,contentType);
	}

	/**
	 * Returns a Locator the content of {@link #fotoSecure} is available under.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLocator")
	@javax.annotation.Nullable
	com.exedio.cope.pattern.MediaPath.Locator getFotoSecureLocator()
	{
		return MediaUrlItem.fotoSecure.getLocator(this);
	}

	/**
	 * Returns whether media {@link #fotoSecure} is null.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="isNull")
	boolean isFotoSecureNull()
	{
		return MediaUrlItem.fotoSecure.isNull(this);
	}

	/**
	 * Returns the last modification date of media {@link #fotoSecure}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLastModified")
	@javax.annotation.Nullable
	java.util.Date getFotoSecureLastModified()
	{
		return MediaUrlItem.fotoSecure.getLastModified(this);
	}

	/**
	 * Returns the body length of the media {@link #fotoSecure}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLength")
	long getFotoSecureLength()
	{
		return MediaUrlItem.fotoSecure.getLength(this);
	}

	/**
	 * Returns the body of the media {@link #fotoSecure}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
	@javax.annotation.Nullable
	byte[] getFotoSecureBody()
	{
		return MediaUrlItem.fotoSecure.getBody(this);
	}

	/**
	 * Writes the body of media {@link #fotoSecure} into the given stream.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
	void getFotoSecureBody(@javax.annotation.Nonnull final java.io.OutputStream body)
			throws
				java.io.IOException
	{
		MediaUrlItem.fotoSecure.getBody(this,body);
	}

	/**
	 * Writes the body of media {@link #fotoSecure} into the given file.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
	void getFotoSecureBody(@javax.annotation.Nonnull final java.nio.file.Path body)
			throws
				java.io.IOException
	{
		MediaUrlItem.fotoSecure.getBody(this,body);
	}

	/**
	 * Writes the body of media {@link #fotoSecure} into the given file.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
	void getFotoSecureBody(@javax.annotation.Nonnull final java.io.File body)
			throws
				java.io.IOException
	{
		MediaUrlItem.fotoSecure.getBody(this,body);
	}

	/**
	 * Sets the content of media {@link #fotoSecure}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setFotoSecure(@javax.annotation.Nullable final com.exedio.cope.pattern.Media.Value fotoSecure)
			throws
				java.io.IOException
	{
		MediaUrlItem.fotoSecure.set(this,fotoSecure);
	}

	/**
	 * Sets the content of media {@link #fotoSecure}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setFotoSecure(@javax.annotation.Nullable final byte[] body,@javax.annotation.Nullable final java.lang.String contentType)
	{
		MediaUrlItem.fotoSecure.set(this,body,contentType);
	}

	/**
	 * Sets the content of media {@link #fotoSecure}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setFotoSecure(@javax.annotation.Nullable final java.io.InputStream body,@javax.annotation.Nullable final java.lang.String contentType)
			throws
				java.io.IOException
	{
		MediaUrlItem.fotoSecure.set(this,body,contentType);
	}

	/**
	 * Sets the content of media {@link #fotoSecure}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setFotoSecure(@javax.annotation.Nullable final java.nio.file.Path body,@javax.annotation.Nullable final java.lang.String contentType)
			throws
				java.io.IOException
	{
		MediaUrlItem.fotoSecure.set(this,body,contentType);
	}

	/**
	 * Sets the content of media {@link #fotoSecure}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setFotoSecure(@javax.annotation.Nullable final java.io.File body,@javax.annotation.Nullable final java.lang.String contentType)
			throws
				java.io.IOException
	{
		MediaUrlItem.fotoSecure.set(this,body,contentType);
	}

	/**
	 * Returns a Locator the content of {@link #fotoFinger} is available under.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLocator")
	@javax.annotation.Nullable
	com.exedio.cope.pattern.MediaPath.Locator getFotoFingerLocator()
	{
		return MediaUrlItem.fotoFinger.getLocator(this);
	}

	/**
	 * Returns whether media {@link #fotoFinger} is null.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="isNull")
	boolean isFotoFingerNull()
	{
		return MediaUrlItem.fotoFinger.isNull(this);
	}

	/**
	 * Returns the last modification date of media {@link #fotoFinger}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLastModified")
	@javax.annotation.Nullable
	java.util.Date getFotoFingerLastModified()
	{
		return MediaUrlItem.fotoFinger.getLastModified(this);
	}

	/**
	 * Returns the body length of the media {@link #fotoFinger}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLength")
	long getFotoFingerLength()
	{
		return MediaUrlItem.fotoFinger.getLength(this);
	}

	/**
	 * Returns the body of the media {@link #fotoFinger}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
	@javax.annotation.Nullable
	byte[] getFotoFingerBody()
	{
		return MediaUrlItem.fotoFinger.getBody(this);
	}

	/**
	 * Writes the body of media {@link #fotoFinger} into the given stream.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
	void getFotoFingerBody(@javax.annotation.Nonnull final java.io.OutputStream body)
			throws
				java.io.IOException
	{
		MediaUrlItem.fotoFinger.getBody(this,body);
	}

	/**
	 * Writes the body of media {@link #fotoFinger} into the given file.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
	void getFotoFingerBody(@javax.annotation.Nonnull final java.nio.file.Path body)
			throws
				java.io.IOException
	{
		MediaUrlItem.fotoFinger.getBody(this,body);
	}

	/**
	 * Writes the body of media {@link #fotoFinger} into the given file.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
	void getFotoFingerBody(@javax.annotation.Nonnull final java.io.File body)
			throws
				java.io.IOException
	{
		MediaUrlItem.fotoFinger.getBody(this,body);
	}

	/**
	 * Sets the content of media {@link #fotoFinger}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setFotoFinger(@javax.annotation.Nullable final com.exedio.cope.pattern.Media.Value fotoFinger)
			throws
				java.io.IOException
	{
		MediaUrlItem.fotoFinger.set(this,fotoFinger);
	}

	/**
	 * Sets the content of media {@link #fotoFinger}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setFotoFinger(@javax.annotation.Nullable final byte[] body,@javax.annotation.Nullable final java.lang.String contentType)
	{
		MediaUrlItem.fotoFinger.set(this,body,contentType);
	}

	/**
	 * Sets the content of media {@link #fotoFinger}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setFotoFinger(@javax.annotation.Nullable final java.io.InputStream body,@javax.annotation.Nullable final java.lang.String contentType)
			throws
				java.io.IOException
	{
		MediaUrlItem.fotoFinger.set(this,body,contentType);
	}

	/**
	 * Sets the content of media {@link #fotoFinger}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setFotoFinger(@javax.annotation.Nullable final java.nio.file.Path body,@javax.annotation.Nullable final java.lang.String contentType)
			throws
				java.io.IOException
	{
		MediaUrlItem.fotoFinger.set(this,body,contentType);
	}

	/**
	 * Sets the content of media {@link #fotoFinger}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setFotoFinger(@javax.annotation.Nullable final java.io.File body,@javax.annotation.Nullable final java.lang.String contentType)
			throws
				java.io.IOException
	{
		MediaUrlItem.fotoFinger.set(this,body,contentType);
	}

	/**
	 * Returns a Locator the content of {@link #fotoSecFin} is available under.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLocator")
	@javax.annotation.Nullable
	com.exedio.cope.pattern.MediaPath.Locator getFotoSecFinLocator()
	{
		return MediaUrlItem.fotoSecFin.getLocator(this);
	}

	/**
	 * Returns whether media {@link #fotoSecFin} is null.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="isNull")
	boolean isFotoSecFinNull()
	{
		return MediaUrlItem.fotoSecFin.isNull(this);
	}

	/**
	 * Returns the last modification date of media {@link #fotoSecFin}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLastModified")
	@javax.annotation.Nullable
	java.util.Date getFotoSecFinLastModified()
	{
		return MediaUrlItem.fotoSecFin.getLastModified(this);
	}

	/**
	 * Returns the body length of the media {@link #fotoSecFin}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLength")
	long getFotoSecFinLength()
	{
		return MediaUrlItem.fotoSecFin.getLength(this);
	}

	/**
	 * Returns the body of the media {@link #fotoSecFin}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
	@javax.annotation.Nullable
	byte[] getFotoSecFinBody()
	{
		return MediaUrlItem.fotoSecFin.getBody(this);
	}

	/**
	 * Writes the body of media {@link #fotoSecFin} into the given stream.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
	void getFotoSecFinBody(@javax.annotation.Nonnull final java.io.OutputStream body)
			throws
				java.io.IOException
	{
		MediaUrlItem.fotoSecFin.getBody(this,body);
	}

	/**
	 * Writes the body of media {@link #fotoSecFin} into the given file.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
	void getFotoSecFinBody(@javax.annotation.Nonnull final java.nio.file.Path body)
			throws
				java.io.IOException
	{
		MediaUrlItem.fotoSecFin.getBody(this,body);
	}

	/**
	 * Writes the body of media {@link #fotoSecFin} into the given file.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
	void getFotoSecFinBody(@javax.annotation.Nonnull final java.io.File body)
			throws
				java.io.IOException
	{
		MediaUrlItem.fotoSecFin.getBody(this,body);
	}

	/**
	 * Sets the content of media {@link #fotoSecFin}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setFotoSecFin(@javax.annotation.Nullable final com.exedio.cope.pattern.Media.Value fotoSecFin)
			throws
				java.io.IOException
	{
		MediaUrlItem.fotoSecFin.set(this,fotoSecFin);
	}

	/**
	 * Sets the content of media {@link #fotoSecFin}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setFotoSecFin(@javax.annotation.Nullable final byte[] body,@javax.annotation.Nullable final java.lang.String contentType)
	{
		MediaUrlItem.fotoSecFin.set(this,body,contentType);
	}

	/**
	 * Sets the content of media {@link #fotoSecFin}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setFotoSecFin(@javax.annotation.Nullable final java.io.InputStream body,@javax.annotation.Nullable final java.lang.String contentType)
			throws
				java.io.IOException
	{
		MediaUrlItem.fotoSecFin.set(this,body,contentType);
	}

	/**
	 * Sets the content of media {@link #fotoSecFin}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setFotoSecFin(@javax.annotation.Nullable final java.nio.file.Path body,@javax.annotation.Nullable final java.lang.String contentType)
			throws
				java.io.IOException
	{
		MediaUrlItem.fotoSecFin.set(this,body,contentType);
	}

	/**
	 * Sets the content of media {@link #fotoSecFin}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setFotoSecFin(@javax.annotation.Nullable final java.io.File body,@javax.annotation.Nullable final java.lang.String contentType)
			throws
				java.io.IOException
	{
		MediaUrlItem.fotoSecFin.set(this,body,contentType);
	}

	/**
	 * Returns a Locator the content of {@link #file} is available under.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLocator")
	@javax.annotation.Nullable
	com.exedio.cope.pattern.MediaPath.Locator getFileLocator()
	{
		return MediaUrlItem.file.getLocator(this);
	}

	/**
	 * Returns whether media {@link #file} is null.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="isNull")
	boolean isFileNull()
	{
		return MediaUrlItem.file.isNull(this);
	}

	/**
	 * Returns the last modification date of media {@link #file}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLastModified")
	@javax.annotation.Nullable
	java.util.Date getFileLastModified()
	{
		return MediaUrlItem.file.getLastModified(this);
	}

	/**
	 * Returns the body length of the media {@link #file}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLength")
	long getFileLength()
	{
		return MediaUrlItem.file.getLength(this);
	}

	/**
	 * Returns the body of the media {@link #file}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
	@javax.annotation.Nullable
	byte[] getFileBody()
	{
		return MediaUrlItem.file.getBody(this);
	}

	/**
	 * Writes the body of media {@link #file} into the given stream.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
	void getFileBody(@javax.annotation.Nonnull final java.io.OutputStream body)
			throws
				java.io.IOException
	{
		MediaUrlItem.file.getBody(this,body);
	}

	/**
	 * Writes the body of media {@link #file} into the given file.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
	void getFileBody(@javax.annotation.Nonnull final java.nio.file.Path body)
			throws
				java.io.IOException
	{
		MediaUrlItem.file.getBody(this,body);
	}

	/**
	 * Writes the body of media {@link #file} into the given file.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
	void getFileBody(@javax.annotation.Nonnull final java.io.File body)
			throws
				java.io.IOException
	{
		MediaUrlItem.file.getBody(this,body);
	}

	/**
	 * Sets the content of media {@link #file}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setFile(@javax.annotation.Nullable final com.exedio.cope.pattern.Media.Value file)
			throws
				java.io.IOException
	{
		MediaUrlItem.file.set(this,file);
	}

	/**
	 * Sets the content of media {@link #file}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setFile(@javax.annotation.Nullable final byte[] body,@javax.annotation.Nullable final java.lang.String contentType)
	{
		MediaUrlItem.file.set(this,body,contentType);
	}

	/**
	 * Sets the content of media {@link #file}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setFile(@javax.annotation.Nullable final java.io.InputStream body,@javax.annotation.Nullable final java.lang.String contentType)
			throws
				java.io.IOException
	{
		MediaUrlItem.file.set(this,body,contentType);
	}

	/**
	 * Sets the content of media {@link #file}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setFile(@javax.annotation.Nullable final java.nio.file.Path body,@javax.annotation.Nullable final java.lang.String contentType)
			throws
				java.io.IOException
	{
		MediaUrlItem.file.set(this,body,contentType);
	}

	/**
	 * Sets the content of media {@link #file}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setFile(@javax.annotation.Nullable final java.io.File body,@javax.annotation.Nullable final java.lang.String contentType)
			throws
				java.io.IOException
	{
		MediaUrlItem.file.set(this,body,contentType);
	}

	/**
	 * Returns a Locator the content of {@link #fileSecure} is available under.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLocator")
	@javax.annotation.Nullable
	com.exedio.cope.pattern.MediaPath.Locator getFileSecureLocator()
	{
		return MediaUrlItem.fileSecure.getLocator(this);
	}

	/**
	 * Returns whether media {@link #fileSecure} is null.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="isNull")
	boolean isFileSecureNull()
	{
		return MediaUrlItem.fileSecure.isNull(this);
	}

	/**
	 * Returns the last modification date of media {@link #fileSecure}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLastModified")
	@javax.annotation.Nullable
	java.util.Date getFileSecureLastModified()
	{
		return MediaUrlItem.fileSecure.getLastModified(this);
	}

	/**
	 * Returns the body length of the media {@link #fileSecure}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLength")
	long getFileSecureLength()
	{
		return MediaUrlItem.fileSecure.getLength(this);
	}

	/**
	 * Returns the body of the media {@link #fileSecure}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
	@javax.annotation.Nullable
	byte[] getFileSecureBody()
	{
		return MediaUrlItem.fileSecure.getBody(this);
	}

	/**
	 * Writes the body of media {@link #fileSecure} into the given stream.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
	void getFileSecureBody(@javax.annotation.Nonnull final java.io.OutputStream body)
			throws
				java.io.IOException
	{
		MediaUrlItem.fileSecure.getBody(this,body);
	}

	/**
	 * Writes the body of media {@link #fileSecure} into the given file.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
	void getFileSecureBody(@javax.annotation.Nonnull final java.nio.file.Path body)
			throws
				java.io.IOException
	{
		MediaUrlItem.fileSecure.getBody(this,body);
	}

	/**
	 * Writes the body of media {@link #fileSecure} into the given file.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
	void getFileSecureBody(@javax.annotation.Nonnull final java.io.File body)
			throws
				java.io.IOException
	{
		MediaUrlItem.fileSecure.getBody(this,body);
	}

	/**
	 * Sets the content of media {@link #fileSecure}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setFileSecure(@javax.annotation.Nullable final com.exedio.cope.pattern.Media.Value fileSecure)
			throws
				java.io.IOException
	{
		MediaUrlItem.fileSecure.set(this,fileSecure);
	}

	/**
	 * Sets the content of media {@link #fileSecure}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setFileSecure(@javax.annotation.Nullable final byte[] body,@javax.annotation.Nullable final java.lang.String contentType)
	{
		MediaUrlItem.fileSecure.set(this,body,contentType);
	}

	/**
	 * Sets the content of media {@link #fileSecure}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setFileSecure(@javax.annotation.Nullable final java.io.InputStream body,@javax.annotation.Nullable final java.lang.String contentType)
			throws
				java.io.IOException
	{
		MediaUrlItem.fileSecure.set(this,body,contentType);
	}

	/**
	 * Sets the content of media {@link #fileSecure}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setFileSecure(@javax.annotation.Nullable final java.nio.file.Path body,@javax.annotation.Nullable final java.lang.String contentType)
			throws
				java.io.IOException
	{
		MediaUrlItem.fileSecure.set(this,body,contentType);
	}

	/**
	 * Sets the content of media {@link #fileSecure}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setFileSecure(@javax.annotation.Nullable final java.io.File body,@javax.annotation.Nullable final java.lang.String contentType)
			throws
				java.io.IOException
	{
		MediaUrlItem.fileSecure.set(this,body,contentType);
	}

	/**
	 * Returns a Locator the content of {@link #fileFinger} is available under.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLocator")
	@javax.annotation.Nullable
	com.exedio.cope.pattern.MediaPath.Locator getFileFingerLocator()
	{
		return MediaUrlItem.fileFinger.getLocator(this);
	}

	/**
	 * Returns whether media {@link #fileFinger} is null.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="isNull")
	boolean isFileFingerNull()
	{
		return MediaUrlItem.fileFinger.isNull(this);
	}

	/**
	 * Returns the last modification date of media {@link #fileFinger}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLastModified")
	@javax.annotation.Nullable
	java.util.Date getFileFingerLastModified()
	{
		return MediaUrlItem.fileFinger.getLastModified(this);
	}

	/**
	 * Returns the body length of the media {@link #fileFinger}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLength")
	long getFileFingerLength()
	{
		return MediaUrlItem.fileFinger.getLength(this);
	}

	/**
	 * Returns the body of the media {@link #fileFinger}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
	@javax.annotation.Nullable
	byte[] getFileFingerBody()
	{
		return MediaUrlItem.fileFinger.getBody(this);
	}

	/**
	 * Writes the body of media {@link #fileFinger} into the given stream.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
	void getFileFingerBody(@javax.annotation.Nonnull final java.io.OutputStream body)
			throws
				java.io.IOException
	{
		MediaUrlItem.fileFinger.getBody(this,body);
	}

	/**
	 * Writes the body of media {@link #fileFinger} into the given file.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
	void getFileFingerBody(@javax.annotation.Nonnull final java.nio.file.Path body)
			throws
				java.io.IOException
	{
		MediaUrlItem.fileFinger.getBody(this,body);
	}

	/**
	 * Writes the body of media {@link #fileFinger} into the given file.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
	void getFileFingerBody(@javax.annotation.Nonnull final java.io.File body)
			throws
				java.io.IOException
	{
		MediaUrlItem.fileFinger.getBody(this,body);
	}

	/**
	 * Sets the content of media {@link #fileFinger}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setFileFinger(@javax.annotation.Nullable final com.exedio.cope.pattern.Media.Value fileFinger)
			throws
				java.io.IOException
	{
		MediaUrlItem.fileFinger.set(this,fileFinger);
	}

	/**
	 * Sets the content of media {@link #fileFinger}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setFileFinger(@javax.annotation.Nullable final byte[] body,@javax.annotation.Nullable final java.lang.String contentType)
	{
		MediaUrlItem.fileFinger.set(this,body,contentType);
	}

	/**
	 * Sets the content of media {@link #fileFinger}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setFileFinger(@javax.annotation.Nullable final java.io.InputStream body,@javax.annotation.Nullable final java.lang.String contentType)
			throws
				java.io.IOException
	{
		MediaUrlItem.fileFinger.set(this,body,contentType);
	}

	/**
	 * Sets the content of media {@link #fileFinger}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setFileFinger(@javax.annotation.Nullable final java.nio.file.Path body,@javax.annotation.Nullable final java.lang.String contentType)
			throws
				java.io.IOException
	{
		MediaUrlItem.fileFinger.set(this,body,contentType);
	}

	/**
	 * Sets the content of media {@link #fileFinger}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setFileFinger(@javax.annotation.Nullable final java.io.File body,@javax.annotation.Nullable final java.lang.String contentType)
			throws
				java.io.IOException
	{
		MediaUrlItem.fileFinger.set(this,body,contentType);
	}

	/**
	 * Returns a Locator the content of {@link #fileSecFin} is available under.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLocator")
	@javax.annotation.Nullable
	com.exedio.cope.pattern.MediaPath.Locator getFileSecFinLocator()
	{
		return MediaUrlItem.fileSecFin.getLocator(this);
	}

	/**
	 * Returns whether media {@link #fileSecFin} is null.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="isNull")
	boolean isFileSecFinNull()
	{
		return MediaUrlItem.fileSecFin.isNull(this);
	}

	/**
	 * Returns the last modification date of media {@link #fileSecFin}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLastModified")
	@javax.annotation.Nullable
	java.util.Date getFileSecFinLastModified()
	{
		return MediaUrlItem.fileSecFin.getLastModified(this);
	}

	/**
	 * Returns the body length of the media {@link #fileSecFin}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLength")
	long getFileSecFinLength()
	{
		return MediaUrlItem.fileSecFin.getLength(this);
	}

	/**
	 * Returns the body of the media {@link #fileSecFin}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
	@javax.annotation.Nullable
	byte[] getFileSecFinBody()
	{
		return MediaUrlItem.fileSecFin.getBody(this);
	}

	/**
	 * Writes the body of media {@link #fileSecFin} into the given stream.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
	void getFileSecFinBody(@javax.annotation.Nonnull final java.io.OutputStream body)
			throws
				java.io.IOException
	{
		MediaUrlItem.fileSecFin.getBody(this,body);
	}

	/**
	 * Writes the body of media {@link #fileSecFin} into the given file.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
	void getFileSecFinBody(@javax.annotation.Nonnull final java.nio.file.Path body)
			throws
				java.io.IOException
	{
		MediaUrlItem.fileSecFin.getBody(this,body);
	}

	/**
	 * Writes the body of media {@link #fileSecFin} into the given file.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
	void getFileSecFinBody(@javax.annotation.Nonnull final java.io.File body)
			throws
				java.io.IOException
	{
		MediaUrlItem.fileSecFin.getBody(this,body);
	}

	/**
	 * Sets the content of media {@link #fileSecFin}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setFileSecFin(@javax.annotation.Nullable final com.exedio.cope.pattern.Media.Value fileSecFin)
			throws
				java.io.IOException
	{
		MediaUrlItem.fileSecFin.set(this,fileSecFin);
	}

	/**
	 * Sets the content of media {@link #fileSecFin}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setFileSecFin(@javax.annotation.Nullable final byte[] body,@javax.annotation.Nullable final java.lang.String contentType)
	{
		MediaUrlItem.fileSecFin.set(this,body,contentType);
	}

	/**
	 * Sets the content of media {@link #fileSecFin}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setFileSecFin(@javax.annotation.Nullable final java.io.InputStream body,@javax.annotation.Nullable final java.lang.String contentType)
			throws
				java.io.IOException
	{
		MediaUrlItem.fileSecFin.set(this,body,contentType);
	}

	/**
	 * Sets the content of media {@link #fileSecFin}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setFileSecFin(@javax.annotation.Nullable final java.nio.file.Path body,@javax.annotation.Nullable final java.lang.String contentType)
			throws
				java.io.IOException
	{
		MediaUrlItem.fileSecFin.set(this,body,contentType);
	}

	/**
	 * Sets the content of media {@link #fileSecFin}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setFileSecFin(@javax.annotation.Nullable final java.io.File body,@javax.annotation.Nullable final java.lang.String contentType)
			throws
				java.io.IOException
	{
		MediaUrlItem.fileSecFin.set(this,body,contentType);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for mediaUrlItem.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<MediaUrlItem> TYPE = com.exedio.cope.TypesBound.newType(MediaUrlItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@SuppressWarnings("unused") private MediaUrlItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
