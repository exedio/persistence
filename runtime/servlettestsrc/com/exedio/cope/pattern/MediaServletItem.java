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
import static com.exedio.cope.util.TimeZoneStrict.getTimeZone;

import com.exedio.cope.DateField;
import com.exedio.cope.Item;
import com.exedio.cope.StringField;
import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.instrument.WrapperIgnore;
import com.exedio.cope.instrument.WrapperInitial;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

@SuppressWarnings("UnusedReturnValue")
final class MediaServletItem extends Item
	implements MediaUrlCatchphraseProvider
{
	@WrapperInitial
	static final StringField name = new StringField().optional();

	@Wrapper(wrap="set", internal=true)
	@Wrapper(wrap="set", parameters=Media.Value.class, visibility=NONE)
	@Wrapper(wrap="set", parameters={File.class, String.class}, visibility=NONE)
	@Wrapper(wrap="getURL", visibility=NONE)
	@RedirectFrom({"contentAlt1", "contentAlt2"})
	static final Media content = new Media().optional();

	void setContent(final byte[] body, final String contentType, final int hour) throws ParseException
	{
		setContentInternal(body, contentType);
		setContentLastModified(hour);
	}

	void setContent(final InputStream body, final String contentType, final int hour) throws ParseException, IOException
	{
		setContentInternal(body, contentType);
		setContentLastModified(hour);
	}

	private void setContentLastModified(final int hour) throws ParseException
	{
		content.getLastModified().set(this,
			df().parse("2010-08-11 " + new DecimalFormat("00").format(hour) + ":23:55.555"));
	}

	void setNameServerLastModified(final int hour) throws ParseException
	{
		setNameServerLastModified(
			df().parse("2010-08-11 " + new DecimalFormat("00").format(hour) + ":23:55.555"));
	}

	private static SimpleDateFormat df()
	{
		final SimpleDateFormat result = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH);
		result.setTimeZone(getTimeZone("Europe/Berlin"));
		result.setLenient(false);
		return result;
	}

	@Wrapper(wrap="getURL", visibility=NONE)
	@Deprecated
	static final MediaRedirect redirect = new MediaRedirect(content);

	@Wrapper(wrap="getURL", visibility=NONE)
	static final MediaThumbnail thumbnail = new MediaThumbnail(content, 150, 150);

	@Wrapper(wrap="getURL", visibility=NONE)
	@Deprecated
	static final MediaRedirect thumbnailRedirect = new MediaRedirect(thumbnail);

	@Wrapper(wrap="getURL", visibility=NONE)
	static final TextUrlFilter html = new TextUrlFilter(
			content,
			"text/html", StandardCharsets.UTF_8,
			"(", ")",
			new StringField(),
			new Media());

	@Wrapper(wrap="getURL", visibility=NONE)
	@PreventUrlGuessing
	static final MediaThumbnail tokened = new MediaThumbnail(content, 25, 25);

	@Wrapper(wrap="getURL", visibility=NONE)
	@UrlFingerPrinting
	static final MediaThumbnail finger = new MediaThumbnail(content, 150, 150);

	@Wrapper(wrap="getURL", visibility=NONE)
	@PreventUrlGuessing
	@UrlFingerPrinting
	static final MediaThumbnail tokenedFinger = new MediaThumbnail(content, 25, 25);

	static final DateField nameServerLastModified = new DateField().optional();

	@WrapperIgnore // required because MediaNameServer inherits @WrapFeature
	static final MediaNameServer nameServer = new MediaNameServer(name, nameServerLastModified);


	static final StringField catchPhrase = new StringField().optional();

	@Override
	public String getMediaUrlCatchphrase(final MediaPath path)
	{
		return getCatchPhrase();
	}


	MediaServletItem()
	{
		this((String)null);
	}

	/**
	 * Creates a new MediaServletItem with all the fields initially needed.
	 * @param name the initial value for field {@link #name}.
	 * @throws com.exedio.cope.StringLengthViolationException if name violates its length constraint.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	MediaServletItem(
				@javax.annotation.Nullable final java.lang.String name)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			MediaServletItem.name.map(name),
		});
	}

	/**
	 * Creates a new MediaServletItem and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private MediaServletItem(final com.exedio.cope.SetValue<?>... setValues)
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
		return MediaServletItem.name.get(this);
	}

	/**
	 * Sets a new value for {@link #name}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setName(@javax.annotation.Nullable final java.lang.String name)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		MediaServletItem.name.set(this,name);
	}

	/**
	 * Returns a Locator the content of {@link #content} is available under.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLocator")
	@javax.annotation.Nullable
	com.exedio.cope.pattern.MediaPath.Locator getContentLocator()
	{
		return MediaServletItem.content.getLocator(this);
	}

	/**
	 * Returns the content type of the media {@link #content}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getContentType")
	@javax.annotation.Nullable
	java.lang.String getContentContentType()
	{
		return MediaServletItem.content.getContentType(this);
	}

	/**
	 * Returns whether media {@link #content} is null.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="isNull")
	boolean isContentNull()
	{
		return MediaServletItem.content.isNull(this);
	}

	/**
	 * Returns the last modification date of media {@link #content}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLastModified")
	@javax.annotation.Nullable
	java.util.Date getContentLastModified()
	{
		return MediaServletItem.content.getLastModified(this);
	}

	/**
	 * Returns the body length of the media {@link #content}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLength")
	long getContentLength()
	{
		return MediaServletItem.content.getLength(this);
	}

	/**
	 * Returns the body of the media {@link #content}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
	@javax.annotation.Nullable
	byte[] getContentBody()
	{
		return MediaServletItem.content.getBody(this);
	}

	/**
	 * Writes the body of media {@link #content} into the given stream.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing <tt>body</tt> throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
	void getContentBody(@javax.annotation.Nonnull final java.io.OutputStream body)
			throws
				java.io.IOException
	{
		MediaServletItem.content.getBody(this,body);
	}

	/**
	 * Writes the body of media {@link #content} into the given file.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing <tt>body</tt> throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
	void getContentBody(@javax.annotation.Nonnull final java.io.File body)
			throws
				java.io.IOException
	{
		MediaServletItem.content.getBody(this,body);
	}

	/**
	 * Sets the content of media {@link #content}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	private void setContentInternal(@javax.annotation.Nullable final byte[] body,@javax.annotation.Nullable final java.lang.String contentType)
	{
		MediaServletItem.content.set(this,body,contentType);
	}

	/**
	 * Sets the content of media {@link #content}.
	 * @throws java.io.IOException if accessing <tt>body</tt> throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	private void setContentInternal(@javax.annotation.Nullable final java.io.InputStream body,@javax.annotation.Nullable final java.lang.String contentType)
			throws
				java.io.IOException
	{
		MediaServletItem.content.set(this,body,contentType);
	}

	/**
	 * Sets the content of media {@link #content}.
	 * @throws java.io.IOException if accessing <tt>body</tt> throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	private void setContentInternal(@javax.annotation.Nullable final java.nio.file.Path body,@javax.annotation.Nullable final java.lang.String contentType)
			throws
				java.io.IOException
	{
		MediaServletItem.content.set(this,body,contentType);
	}

	/**
	 * Returns a Locator the content of {@link #redirect} is available under.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLocator")
	@java.lang.Deprecated
	@javax.annotation.Nullable
	com.exedio.cope.pattern.MediaPath.Locator getRedirectLocator()
	{
		return MediaServletItem.redirect.getLocator(this);
	}

	/**
	 * Returns the content type of the media {@link #redirect}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getContentType")
	@java.lang.Deprecated
	@javax.annotation.Nullable
	java.lang.String getRedirectContentType()
	{
		return MediaServletItem.redirect.getContentType(this);
	}

	/**
	 * Returns a Locator the content of {@link #thumbnail} is available under.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLocator")
	@javax.annotation.Nullable
	com.exedio.cope.pattern.MediaPath.Locator getThumbnailLocator()
	{
		return MediaServletItem.thumbnail.getLocator(this);
	}

	/**
	 * Returns a URL the content of {@link #thumbnail} is available under, falling back to source if necessary.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getURLWithFallbackToSource")
	@javax.annotation.Nullable
	java.lang.String getThumbnailURLWithFallbackToSource()
	{
		return MediaServletItem.thumbnail.getURLWithFallbackToSource(this);
	}

	/**
	 * Returns a Locator the content of {@link #thumbnail} is available under, falling back to source if necessary.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLocatorWithFallbackToSource")
	@javax.annotation.Nullable
	com.exedio.cope.pattern.MediaPath.Locator getThumbnailLocatorWithFallbackToSource()
	{
		return MediaServletItem.thumbnail.getLocatorWithFallbackToSource(this);
	}

	/**
	 * Returns the body of {@link #thumbnail}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	byte[] getThumbnail()
			throws
				java.io.IOException
	{
		return MediaServletItem.thumbnail.get(this);
	}

	/**
	 * Returns a Locator the content of {@link #thumbnailRedirect} is available under.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLocator")
	@java.lang.Deprecated
	@javax.annotation.Nullable
	com.exedio.cope.pattern.MediaPath.Locator getThumbnailRedirectLocator()
	{
		return MediaServletItem.thumbnailRedirect.getLocator(this);
	}

	/**
	 * Returns a Locator the content of {@link #html} is available under.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLocator")
	@javax.annotation.Nullable
	com.exedio.cope.pattern.MediaPath.Locator getHtmlLocator()
	{
		return MediaServletItem.html.getLocator(this);
	}

	/**
	 * Returns a URL the content of {@link #html} is available under, falling back to source if necessary.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getURLWithFallbackToSource")
	@javax.annotation.Nullable
	java.lang.String getHtmlURLWithFallbackToSource()
	{
		return MediaServletItem.html.getURLWithFallbackToSource(this);
	}

	/**
	 * Returns a Locator the content of {@link #html} is available under, falling back to source if necessary.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLocatorWithFallbackToSource")
	@javax.annotation.Nullable
	com.exedio.cope.pattern.MediaPath.Locator getHtmlLocatorWithFallbackToSource()
	{
		return MediaServletItem.html.getLocatorWithFallbackToSource(this);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="setRaw")
	void setHtmlRaw(@javax.annotation.Nullable final com.exedio.cope.pattern.Media.Value raw)
			throws
				java.io.IOException
	{
		MediaServletItem.html.setRaw(this,raw);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="addPaste")
	@javax.annotation.Nonnull
	com.exedio.cope.pattern.TextUrlFilter.Paste addHtmlPaste(@javax.annotation.Nonnull final java.lang.String key,@javax.annotation.Nonnull final com.exedio.cope.pattern.Media.Value value)
	{
		return MediaServletItem.html.addPaste(this,key,value);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="modifyPaste")
	void modifyHtmlPaste(@javax.annotation.Nonnull final java.lang.String key,@javax.annotation.Nonnull final com.exedio.cope.pattern.Media.Value value)
			throws
				java.io.IOException
	{
		MediaServletItem.html.modifyPaste(this,key,value);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="putPaste")
	@javax.annotation.Nonnull
	com.exedio.cope.pattern.TextUrlFilter.Paste putHtmlPaste(@javax.annotation.Nonnull final java.lang.String key,@javax.annotation.Nonnull final com.exedio.cope.pattern.Media.Value value)
			throws
				java.io.IOException
	{
		return MediaServletItem.html.putPaste(this,key,value);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getContent")
	@javax.annotation.Nonnull
	java.lang.String getHtmlContent(@javax.annotation.Nonnull final javax.servlet.http.HttpServletRequest request)
			throws
				com.exedio.cope.pattern.MediaPath.NotFound
	{
		return MediaServletItem.html.getContent(this,request);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="check")
	@javax.annotation.Nonnull
	java.util.Set<java.lang.String> checkHtml()
			throws
				com.exedio.cope.pattern.MediaPath.NotFound
	{
		return MediaServletItem.html.check(this);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="putPastesFromZip")
	void putHtmlPastesFromZip(@javax.annotation.Nonnull final java.io.File file)
			throws
				java.io.IOException
	{
		MediaServletItem.html.putPastesFromZip(this,file);
	}

	/**
	 * Returns a Locator the content of {@link #tokened} is available under.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLocator")
	@javax.annotation.Nullable
	com.exedio.cope.pattern.MediaPath.Locator getTokenedLocator()
	{
		return MediaServletItem.tokened.getLocator(this);
	}

	/**
	 * Returns a URL the content of {@link #tokened} is available under, falling back to source if necessary.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getURLWithFallbackToSource")
	@javax.annotation.Nullable
	java.lang.String getTokenedURLWithFallbackToSource()
	{
		return MediaServletItem.tokened.getURLWithFallbackToSource(this);
	}

	/**
	 * Returns a Locator the content of {@link #tokened} is available under, falling back to source if necessary.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLocatorWithFallbackToSource")
	@javax.annotation.Nullable
	com.exedio.cope.pattern.MediaPath.Locator getTokenedLocatorWithFallbackToSource()
	{
		return MediaServletItem.tokened.getLocatorWithFallbackToSource(this);
	}

	/**
	 * Returns the body of {@link #tokened}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	byte[] getTokened()
			throws
				java.io.IOException
	{
		return MediaServletItem.tokened.get(this);
	}

	/**
	 * Returns a Locator the content of {@link #finger} is available under.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLocator")
	@javax.annotation.Nullable
	com.exedio.cope.pattern.MediaPath.Locator getFingerLocator()
	{
		return MediaServletItem.finger.getLocator(this);
	}

	/**
	 * Returns a URL the content of {@link #finger} is available under, falling back to source if necessary.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getURLWithFallbackToSource")
	@javax.annotation.Nullable
	java.lang.String getFingerURLWithFallbackToSource()
	{
		return MediaServletItem.finger.getURLWithFallbackToSource(this);
	}

	/**
	 * Returns a Locator the content of {@link #finger} is available under, falling back to source if necessary.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLocatorWithFallbackToSource")
	@javax.annotation.Nullable
	com.exedio.cope.pattern.MediaPath.Locator getFingerLocatorWithFallbackToSource()
	{
		return MediaServletItem.finger.getLocatorWithFallbackToSource(this);
	}

	/**
	 * Returns the body of {@link #finger}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	byte[] getFinger()
			throws
				java.io.IOException
	{
		return MediaServletItem.finger.get(this);
	}

	/**
	 * Returns a Locator the content of {@link #tokenedFinger} is available under.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLocator")
	@javax.annotation.Nullable
	com.exedio.cope.pattern.MediaPath.Locator getTokenedFingerLocator()
	{
		return MediaServletItem.tokenedFinger.getLocator(this);
	}

	/**
	 * Returns a URL the content of {@link #tokenedFinger} is available under, falling back to source if necessary.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getURLWithFallbackToSource")
	@javax.annotation.Nullable
	java.lang.String getTokenedFingerURLWithFallbackToSource()
	{
		return MediaServletItem.tokenedFinger.getURLWithFallbackToSource(this);
	}

	/**
	 * Returns a Locator the content of {@link #tokenedFinger} is available under, falling back to source if necessary.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLocatorWithFallbackToSource")
	@javax.annotation.Nullable
	com.exedio.cope.pattern.MediaPath.Locator getTokenedFingerLocatorWithFallbackToSource()
	{
		return MediaServletItem.tokenedFinger.getLocatorWithFallbackToSource(this);
	}

	/**
	 * Returns the body of {@link #tokenedFinger}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	byte[] getTokenedFinger()
			throws
				java.io.IOException
	{
		return MediaServletItem.tokenedFinger.get(this);
	}

	/**
	 * Returns the value of {@link #nameServerLastModified}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	java.util.Date getNameServerLastModified()
	{
		return MediaServletItem.nameServerLastModified.get(this);
	}

	/**
	 * Sets a new value for {@link #nameServerLastModified}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setNameServerLastModified(@javax.annotation.Nullable final java.util.Date nameServerLastModified)
	{
		MediaServletItem.nameServerLastModified.set(this,nameServerLastModified);
	}

	/**
	 * Sets the current date for the date field {@link #nameServerLastModified}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="touch")
	void touchNameServerLastModified()
	{
		MediaServletItem.nameServerLastModified.touch(this);
	}

	/**
	 * Returns the value of {@link #catchPhrase}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	java.lang.String getCatchPhrase()
	{
		return MediaServletItem.catchPhrase.get(this);
	}

	/**
	 * Sets a new value for {@link #catchPhrase}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setCatchPhrase(@javax.annotation.Nullable final java.lang.String catchPhrase)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		MediaServletItem.catchPhrase.set(this,catchPhrase);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for mediaServletItem.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<MediaServletItem> TYPE = com.exedio.cope.TypesBound.newType(MediaServletItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@SuppressWarnings("unused") private MediaServletItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
