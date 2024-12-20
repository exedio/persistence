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

	/**
	 * For testing {@code Media#doGetAndCommit} in large mode.
	 * See MediaSmallBodyTest.
	 */
	@Wrapper(wrap="set", parameters={byte[].class,String.class}, internal=true)
	@Wrapper(wrap=Wrapper.ALL_WRAPS, visibility=NONE)
	static final Media contentLarge = new Media().optional().lengthMax(Media.DEFAULT_LENGTH+1);

	void setContentLarge(final byte[] body, final String contentType, final int hour) throws ParseException
	{
		setContentLargeInternal(body, contentType);
		setContentLargeLastModified(hour);
	}

	private void setContentLargeLastModified(final int hour) throws ParseException
	{
		contentLarge.getLastModified().set(this,
				df().parse("2010-08-11 " + new DecimalFormat("00").format(hour) + ":23:55.555"));
	}

	@Wrapper(wrap="set", parameters={byte[].class,String.class}, internal=true)
	@Wrapper(wrap="set", parameters={InputStream.class,String.class}, internal=true)
	@Wrapper(wrap=Wrapper.ALL_WRAPS, visibility=NONE)
	static final Media contentNoLocator = new Media().optional().withLocator(false);

	void setContentNoLocator(final InputStream body, final String contentType, final int hour) throws ParseException, IOException
	{
		setContentNoLocatorInternal(body, contentType);
		contentNoLocator.getLastModified().set(this,
				df().parse("2010-08-11 " + new DecimalFormat("00").format(hour) + ":23:55.555"));
	}

	void setContentNoLocator(final byte[] body, final String contentType, final int hour) throws ParseException
	{
		setContentNoLocatorInternal(body, contentType);
		contentNoLocator.getLastModified().set(this,
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
	static final MediaThumbnail thumbnail = new MediaThumbnail(content, 150, 150);

	@Wrapper(wrap="getURL", visibility=NONE)
	static final MediaThumbnail thumbnailOfNoLocator = new MediaThumbnail(contentNoLocator, 150, 150);

	static final MediaThumbnail noLocatorThumbnail = new MediaThumbnail(content, 150, 150).withLocator(false);

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
	@UsageEntryPoint
	static final MediaNameServer nameServer = new MediaNameServer(name, nameServerLastModified);


	static final StringField catchphrase = new StringField().optional();

	@Override
	public String getMediaUrlCatchphrase(final MediaPath path)
	{
		return getCatchphrase();
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
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	MediaServletItem(
				@javax.annotation.Nullable final java.lang.String name)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			com.exedio.cope.SetValue.map(MediaServletItem.name,name),
		});
	}

	/**
	 * Creates a new MediaServletItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private MediaServletItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #name}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.lang.String getName()
	{
		return MediaServletItem.name.get(this);
	}

	/**
	 * Sets a new value for {@link #name}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setName(@javax.annotation.Nullable final java.lang.String name)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		MediaServletItem.name.set(this,name);
	}

	/**
	 * Returns a Locator the content of {@link #content} is available under.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLocator")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	com.exedio.cope.pattern.MediaPath.Locator getContentLocator()
	{
		return MediaServletItem.content.getLocator(this);
	}

	/**
	 * Returns the content type of the media {@link #content}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getContentType")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.lang.String getContentContentType()
	{
		return MediaServletItem.content.getContentType(this);
	}

	/**
	 * Returns whether media {@link #content} is null.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="isNull")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	boolean isContentNull()
	{
		return MediaServletItem.content.isNull(this);
	}

	/**
	 * Returns the last modification date of media {@link #content}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLastModified")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.util.Date getContentLastModified()
	{
		return MediaServletItem.content.getLastModified(this);
	}

	/**
	 * Returns the body length of the media {@link #content}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLength")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	long getContentLength()
	{
		return MediaServletItem.content.getLength(this);
	}

	/**
	 * Returns the body of the media {@link #content}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getBody")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	byte[] getContentBody()
	{
		return MediaServletItem.content.getBody(this);
	}

	/**
	 * Writes the body of media {@link #content} into the given stream.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getBody")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void getContentBody(@javax.annotation.Nonnull final java.io.OutputStream body)
			throws
				java.io.IOException
	{
		MediaServletItem.content.getBody(this,body);
	}

	/**
	 * Writes the body of media {@link #content} into the given file.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getBody")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void getContentBody(@javax.annotation.Nonnull final java.nio.file.Path body)
			throws
				java.io.IOException
	{
		MediaServletItem.content.getBody(this,body);
	}

	/**
	 * Writes the body of media {@link #content} into the given file.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getBody")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void getContentBody(@javax.annotation.Nonnull final java.io.File body)
			throws
				java.io.IOException
	{
		MediaServletItem.content.getBody(this,body);
	}

	/**
	 * Sets the content of media {@link #content}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	private void setContentInternal(@javax.annotation.Nullable final byte[] body,@javax.annotation.Nullable final java.lang.String contentType)
	{
		MediaServletItem.content.set(this,body,contentType);
	}

	/**
	 * Sets the content of media {@link #content}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	private void setContentInternal(@javax.annotation.Nullable final java.io.InputStream body,@javax.annotation.Nullable final java.lang.String contentType)
			throws
				java.io.IOException
	{
		MediaServletItem.content.set(this,body,contentType);
	}

	/**
	 * Sets the content of media {@link #content}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	private void setContentInternal(@javax.annotation.Nullable final java.nio.file.Path body,@javax.annotation.Nullable final java.lang.String contentType)
			throws
				java.io.IOException
	{
		MediaServletItem.content.set(this,body,contentType);
	}

	/**
	 * Sets the content of media {@link #contentLarge}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	private void setContentLargeInternal(@javax.annotation.Nullable final byte[] body,@javax.annotation.Nullable final java.lang.String contentType)
	{
		MediaServletItem.contentLarge.set(this,body,contentType);
	}

	/**
	 * Sets the content of media {@link #contentNoLocator}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	private void setContentNoLocatorInternal(@javax.annotation.Nullable final byte[] body,@javax.annotation.Nullable final java.lang.String contentType)
	{
		MediaServletItem.contentNoLocator.set(this,body,contentType);
	}

	/**
	 * Sets the content of media {@link #contentNoLocator}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	private void setContentNoLocatorInternal(@javax.annotation.Nullable final java.io.InputStream body,@javax.annotation.Nullable final java.lang.String contentType)
			throws
				java.io.IOException
	{
		MediaServletItem.contentNoLocator.set(this,body,contentType);
	}

	/**
	 * Returns a Locator the content of {@link #thumbnail} is available under.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLocator")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	com.exedio.cope.pattern.MediaPath.Locator getThumbnailLocator()
	{
		return MediaServletItem.thumbnail.getLocator(this);
	}

	/**
	 * Returns a URL the content of {@link #thumbnail} is available under, falling back to source if necessary.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getURLWithFallbackToSource")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.lang.String getThumbnailURLWithFallbackToSource()
	{
		return MediaServletItem.thumbnail.getURLWithFallbackToSource(this);
	}

	/**
	 * Returns a Locator the content of {@link #thumbnail} is available under, falling back to source if necessary.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLocatorWithFallbackToSource")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	com.exedio.cope.pattern.MediaPath.Locator getThumbnailLocatorWithFallbackToSource()
	{
		return MediaServletItem.thumbnail.getLocatorWithFallbackToSource(this);
	}

	/**
	 * Returns the body of {@link #thumbnail}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	byte[] getThumbnail()
			throws
				java.io.IOException
	{
		return MediaServletItem.thumbnail.get(this);
	}

	/**
	 * Returns a Locator the content of {@link #thumbnailOfNoLocator} is available under.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLocator")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	com.exedio.cope.pattern.MediaPath.Locator getThumbnailOfNoLocatorLocator()
	{
		return MediaServletItem.thumbnailOfNoLocator.getLocator(this);
	}

	/**
	 * Returns the body of {@link #thumbnailOfNoLocator}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	byte[] getThumbnailOfNoLocator()
			throws
				java.io.IOException
	{
		return MediaServletItem.thumbnailOfNoLocator.get(this);
	}

	/**
	 * Returns the body of {@link #noLocatorThumbnail}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	byte[] getNoLocatorThumbnail()
			throws
				java.io.IOException
	{
		return MediaServletItem.noLocatorThumbnail.get(this);
	}

	/**
	 * Returns a Locator the content of {@link #tokened} is available under.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLocator")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	com.exedio.cope.pattern.MediaPath.Locator getTokenedLocator()
	{
		return MediaServletItem.tokened.getLocator(this);
	}

	/**
	 * Returns a URL the content of {@link #tokened} is available under, falling back to source if necessary.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getURLWithFallbackToSource")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.lang.String getTokenedURLWithFallbackToSource()
	{
		return MediaServletItem.tokened.getURLWithFallbackToSource(this);
	}

	/**
	 * Returns a Locator the content of {@link #tokened} is available under, falling back to source if necessary.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLocatorWithFallbackToSource")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	com.exedio.cope.pattern.MediaPath.Locator getTokenedLocatorWithFallbackToSource()
	{
		return MediaServletItem.tokened.getLocatorWithFallbackToSource(this);
	}

	/**
	 * Returns the body of {@link #tokened}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
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
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLocator")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	com.exedio.cope.pattern.MediaPath.Locator getFingerLocator()
	{
		return MediaServletItem.finger.getLocator(this);
	}

	/**
	 * Returns a URL the content of {@link #finger} is available under, falling back to source if necessary.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getURLWithFallbackToSource")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.lang.String getFingerURLWithFallbackToSource()
	{
		return MediaServletItem.finger.getURLWithFallbackToSource(this);
	}

	/**
	 * Returns a Locator the content of {@link #finger} is available under, falling back to source if necessary.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLocatorWithFallbackToSource")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	com.exedio.cope.pattern.MediaPath.Locator getFingerLocatorWithFallbackToSource()
	{
		return MediaServletItem.finger.getLocatorWithFallbackToSource(this);
	}

	/**
	 * Returns the body of {@link #finger}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
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
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLocator")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	com.exedio.cope.pattern.MediaPath.Locator getTokenedFingerLocator()
	{
		return MediaServletItem.tokenedFinger.getLocator(this);
	}

	/**
	 * Returns a URL the content of {@link #tokenedFinger} is available under, falling back to source if necessary.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getURLWithFallbackToSource")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.lang.String getTokenedFingerURLWithFallbackToSource()
	{
		return MediaServletItem.tokenedFinger.getURLWithFallbackToSource(this);
	}

	/**
	 * Returns a Locator the content of {@link #tokenedFinger} is available under, falling back to source if necessary.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLocatorWithFallbackToSource")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	com.exedio.cope.pattern.MediaPath.Locator getTokenedFingerLocatorWithFallbackToSource()
	{
		return MediaServletItem.tokenedFinger.getLocatorWithFallbackToSource(this);
	}

	/**
	 * Returns the body of {@link #tokenedFinger}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
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
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.util.Date getNameServerLastModified()
	{
		return MediaServletItem.nameServerLastModified.get(this);
	}

	/**
	 * Sets a new value for {@link #nameServerLastModified}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setNameServerLastModified(@javax.annotation.Nullable final java.util.Date nameServerLastModified)
	{
		MediaServletItem.nameServerLastModified.set(this,nameServerLastModified);
	}

	/**
	 * Sets the current date for the date field {@link #nameServerLastModified}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="touch")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void touchNameServerLastModified()
	{
		MediaServletItem.nameServerLastModified.touch(this);
	}

	/**
	 * Returns the value of {@link #catchphrase}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.lang.String getCatchphrase()
	{
		return MediaServletItem.catchphrase.get(this);
	}

	/**
	 * Sets a new value for {@link #catchphrase}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setCatchphrase(@javax.annotation.Nullable final java.lang.String catchphrase)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		MediaServletItem.catchphrase.set(this,catchphrase);
	}

	@com.exedio.cope.instrument.Generated
	@java.io.Serial
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for mediaServletItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<MediaServletItem> TYPE = com.exedio.cope.TypesBound.newType(MediaServletItem.class,MediaServletItem::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private MediaServletItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
