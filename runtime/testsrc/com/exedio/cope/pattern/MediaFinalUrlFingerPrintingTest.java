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

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.Item;
import com.exedio.cope.Model;
import org.junit.jupiter.api.Test;

/**
 * This test became useless, as there is no connection between
 * toFinal and @UrlFingerPrinting anymore.
 */
public class MediaFinalUrlFingerPrintingTest
{
	@Test void testIt()
	{
		assertEquals(false, AnItem.nonFinal.isFinal());
		assertEquals(true,  AnItem.isFinal.isFinal());
		assertEquals(false, AnItem.nonFinalFinger.isFinal());
		assertEquals(true,  AnItem.isFinalFinger.isFinal());

		assertEquals(false, AnItem.nonFinal.isUrlFingerPrinted());
		assertEquals(false, AnItem.isFinal.isUrlFingerPrinted());
		assertEquals(true,  AnItem.nonFinalFinger.isUrlFingerPrinted());
		assertEquals(true,  AnItem.isFinalFinger.isUrlFingerPrinted());
	}


	private static class AnItem extends Item
	{
		static final Media nonFinal = new Media();
		static final Media isFinal = new Media().toFinal();
		@UrlFingerPrinting
		static final Media nonFinalFinger = new Media();
		@UrlFingerPrinting
		static final Media isFinalFinger = new Media().toFinal();

	/**
	 * Creates a new AnItem with all the fields initially needed.
	 * @param nonFinal the initial value for field {@link #nonFinal}.
	 * @param isFinal the initial value for field {@link #isFinal}.
	 * @param nonFinalFinger the initial value for field {@link #nonFinalFinger}.
	 * @param isFinalFinger the initial value for field {@link #isFinalFinger}.
	 * @throws com.exedio.cope.MandatoryViolationException if nonFinal, isFinal, nonFinalFinger, isFinalFinger is null.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	private AnItem(
				@javax.annotation.Nonnull final com.exedio.cope.pattern.Media.Value nonFinal,
				@javax.annotation.Nonnull final com.exedio.cope.pattern.Media.Value isFinal,
				@javax.annotation.Nonnull final com.exedio.cope.pattern.Media.Value nonFinalFinger,
				@javax.annotation.Nonnull final com.exedio.cope.pattern.Media.Value isFinalFinger)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			AnItem.nonFinal.map(nonFinal),
			AnItem.isFinal.map(isFinal),
			AnItem.nonFinalFinger.map(nonFinalFinger),
			AnItem.isFinalFinger.map(isFinalFinger),
		});
	}

	/**
	 * Creates a new AnItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	protected AnItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns a URL the content of {@link #nonFinal} is available under.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getURL")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	final java.lang.String getNonFinalURL()
	{
		return AnItem.nonFinal.getURL(this);
	}

	/**
	 * Returns a Locator the content of {@link #nonFinal} is available under.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLocator")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	final com.exedio.cope.pattern.MediaPath.Locator getNonFinalLocator()
	{
		return AnItem.nonFinal.getLocator(this);
	}

	/**
	 * Returns the content type of the media {@link #nonFinal}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getContentType")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	final java.lang.String getNonFinalContentType()
	{
		return AnItem.nonFinal.getContentType(this);
	}

	/**
	 * Returns the last modification date of media {@link #nonFinal}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLastModified")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	final java.util.Date getNonFinalLastModified()
	{
		return AnItem.nonFinal.getLastModified(this);
	}

	/**
	 * Returns the body length of the media {@link #nonFinal}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLength")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final long getNonFinalLength()
	{
		return AnItem.nonFinal.getLength(this);
	}

	/**
	 * Returns the body of the media {@link #nonFinal}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getBody")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	final byte[] getNonFinalBody()
	{
		return AnItem.nonFinal.getBody(this);
	}

	/**
	 * Writes the body of media {@link #nonFinal} into the given stream.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getBody")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void getNonFinalBody(@javax.annotation.Nonnull final java.io.OutputStream body)
			throws
				java.io.IOException
	{
		AnItem.nonFinal.getBody(this,body);
	}

	/**
	 * Writes the body of media {@link #nonFinal} into the given file.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getBody")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void getNonFinalBody(@javax.annotation.Nonnull final java.nio.file.Path body)
			throws
				java.io.IOException
	{
		AnItem.nonFinal.getBody(this,body);
	}

	/**
	 * Writes the body of media {@link #nonFinal} into the given file.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getBody")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void getNonFinalBody(@javax.annotation.Nonnull final java.io.File body)
			throws
				java.io.IOException
	{
		AnItem.nonFinal.getBody(this,body);
	}

	/**
	 * Sets the content of media {@link #nonFinal}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void setNonFinal(@javax.annotation.Nonnull final com.exedio.cope.pattern.Media.Value nonFinal)
			throws
				java.io.IOException
	{
		AnItem.nonFinal.set(this,nonFinal);
	}

	/**
	 * Sets the content of media {@link #nonFinal}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void setNonFinal(@javax.annotation.Nonnull final byte[] body,@javax.annotation.Nonnull final java.lang.String contentType)
	{
		AnItem.nonFinal.set(this,body,contentType);
	}

	/**
	 * Sets the content of media {@link #nonFinal}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void setNonFinal(@javax.annotation.Nonnull final java.io.InputStream body,@javax.annotation.Nonnull final java.lang.String contentType)
			throws
				java.io.IOException
	{
		AnItem.nonFinal.set(this,body,contentType);
	}

	/**
	 * Sets the content of media {@link #nonFinal}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void setNonFinal(@javax.annotation.Nonnull final java.nio.file.Path body,@javax.annotation.Nonnull final java.lang.String contentType)
			throws
				java.io.IOException
	{
		AnItem.nonFinal.set(this,body,contentType);
	}

	/**
	 * Sets the content of media {@link #nonFinal}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void setNonFinal(@javax.annotation.Nonnull final java.io.File body,@javax.annotation.Nonnull final java.lang.String contentType)
			throws
				java.io.IOException
	{
		AnItem.nonFinal.set(this,body,contentType);
	}

	/**
	 * Returns a URL the content of {@link #isFinal} is available under.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getURL")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	final java.lang.String getIsFinalURL()
	{
		return AnItem.isFinal.getURL(this);
	}

	/**
	 * Returns a Locator the content of {@link #isFinal} is available under.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLocator")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	final com.exedio.cope.pattern.MediaPath.Locator getIsFinalLocator()
	{
		return AnItem.isFinal.getLocator(this);
	}

	/**
	 * Returns the content type of the media {@link #isFinal}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getContentType")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	final java.lang.String getIsFinalContentType()
	{
		return AnItem.isFinal.getContentType(this);
	}

	/**
	 * Returns the last modification date of media {@link #isFinal}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLastModified")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	final java.util.Date getIsFinalLastModified()
	{
		return AnItem.isFinal.getLastModified(this);
	}

	/**
	 * Returns the body length of the media {@link #isFinal}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLength")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final long getIsFinalLength()
	{
		return AnItem.isFinal.getLength(this);
	}

	/**
	 * Returns the body of the media {@link #isFinal}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getBody")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	final byte[] getIsFinalBody()
	{
		return AnItem.isFinal.getBody(this);
	}

	/**
	 * Writes the body of media {@link #isFinal} into the given stream.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getBody")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void getIsFinalBody(@javax.annotation.Nonnull final java.io.OutputStream body)
			throws
				java.io.IOException
	{
		AnItem.isFinal.getBody(this,body);
	}

	/**
	 * Writes the body of media {@link #isFinal} into the given file.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getBody")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void getIsFinalBody(@javax.annotation.Nonnull final java.nio.file.Path body)
			throws
				java.io.IOException
	{
		AnItem.isFinal.getBody(this,body);
	}

	/**
	 * Writes the body of media {@link #isFinal} into the given file.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getBody")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void getIsFinalBody(@javax.annotation.Nonnull final java.io.File body)
			throws
				java.io.IOException
	{
		AnItem.isFinal.getBody(this,body);
	}

	/**
	 * Returns a URL the content of {@link #nonFinalFinger} is available under.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getURL")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	final java.lang.String getNonFinalFingerURL()
	{
		return AnItem.nonFinalFinger.getURL(this);
	}

	/**
	 * Returns a Locator the content of {@link #nonFinalFinger} is available under.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLocator")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	final com.exedio.cope.pattern.MediaPath.Locator getNonFinalFingerLocator()
	{
		return AnItem.nonFinalFinger.getLocator(this);
	}

	/**
	 * Returns the content type of the media {@link #nonFinalFinger}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getContentType")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	final java.lang.String getNonFinalFingerContentType()
	{
		return AnItem.nonFinalFinger.getContentType(this);
	}

	/**
	 * Returns the last modification date of media {@link #nonFinalFinger}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLastModified")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	final java.util.Date getNonFinalFingerLastModified()
	{
		return AnItem.nonFinalFinger.getLastModified(this);
	}

	/**
	 * Returns the body length of the media {@link #nonFinalFinger}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLength")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final long getNonFinalFingerLength()
	{
		return AnItem.nonFinalFinger.getLength(this);
	}

	/**
	 * Returns the body of the media {@link #nonFinalFinger}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getBody")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	final byte[] getNonFinalFingerBody()
	{
		return AnItem.nonFinalFinger.getBody(this);
	}

	/**
	 * Writes the body of media {@link #nonFinalFinger} into the given stream.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getBody")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void getNonFinalFingerBody(@javax.annotation.Nonnull final java.io.OutputStream body)
			throws
				java.io.IOException
	{
		AnItem.nonFinalFinger.getBody(this,body);
	}

	/**
	 * Writes the body of media {@link #nonFinalFinger} into the given file.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getBody")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void getNonFinalFingerBody(@javax.annotation.Nonnull final java.nio.file.Path body)
			throws
				java.io.IOException
	{
		AnItem.nonFinalFinger.getBody(this,body);
	}

	/**
	 * Writes the body of media {@link #nonFinalFinger} into the given file.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getBody")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void getNonFinalFingerBody(@javax.annotation.Nonnull final java.io.File body)
			throws
				java.io.IOException
	{
		AnItem.nonFinalFinger.getBody(this,body);
	}

	/**
	 * Sets the content of media {@link #nonFinalFinger}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void setNonFinalFinger(@javax.annotation.Nonnull final com.exedio.cope.pattern.Media.Value nonFinalFinger)
			throws
				java.io.IOException
	{
		AnItem.nonFinalFinger.set(this,nonFinalFinger);
	}

	/**
	 * Sets the content of media {@link #nonFinalFinger}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void setNonFinalFinger(@javax.annotation.Nonnull final byte[] body,@javax.annotation.Nonnull final java.lang.String contentType)
	{
		AnItem.nonFinalFinger.set(this,body,contentType);
	}

	/**
	 * Sets the content of media {@link #nonFinalFinger}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void setNonFinalFinger(@javax.annotation.Nonnull final java.io.InputStream body,@javax.annotation.Nonnull final java.lang.String contentType)
			throws
				java.io.IOException
	{
		AnItem.nonFinalFinger.set(this,body,contentType);
	}

	/**
	 * Sets the content of media {@link #nonFinalFinger}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void setNonFinalFinger(@javax.annotation.Nonnull final java.nio.file.Path body,@javax.annotation.Nonnull final java.lang.String contentType)
			throws
				java.io.IOException
	{
		AnItem.nonFinalFinger.set(this,body,contentType);
	}

	/**
	 * Sets the content of media {@link #nonFinalFinger}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void setNonFinalFinger(@javax.annotation.Nonnull final java.io.File body,@javax.annotation.Nonnull final java.lang.String contentType)
			throws
				java.io.IOException
	{
		AnItem.nonFinalFinger.set(this,body,contentType);
	}

	/**
	 * Returns a URL the content of {@link #isFinalFinger} is available under.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getURL")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	final java.lang.String getIsFinalFingerURL()
	{
		return AnItem.isFinalFinger.getURL(this);
	}

	/**
	 * Returns a Locator the content of {@link #isFinalFinger} is available under.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLocator")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	final com.exedio.cope.pattern.MediaPath.Locator getIsFinalFingerLocator()
	{
		return AnItem.isFinalFinger.getLocator(this);
	}

	/**
	 * Returns the content type of the media {@link #isFinalFinger}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getContentType")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	final java.lang.String getIsFinalFingerContentType()
	{
		return AnItem.isFinalFinger.getContentType(this);
	}

	/**
	 * Returns the last modification date of media {@link #isFinalFinger}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLastModified")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	final java.util.Date getIsFinalFingerLastModified()
	{
		return AnItem.isFinalFinger.getLastModified(this);
	}

	/**
	 * Returns the body length of the media {@link #isFinalFinger}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLength")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final long getIsFinalFingerLength()
	{
		return AnItem.isFinalFinger.getLength(this);
	}

	/**
	 * Returns the body of the media {@link #isFinalFinger}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getBody")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	final byte[] getIsFinalFingerBody()
	{
		return AnItem.isFinalFinger.getBody(this);
	}

	/**
	 * Writes the body of media {@link #isFinalFinger} into the given stream.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getBody")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void getIsFinalFingerBody(@javax.annotation.Nonnull final java.io.OutputStream body)
			throws
				java.io.IOException
	{
		AnItem.isFinalFinger.getBody(this,body);
	}

	/**
	 * Writes the body of media {@link #isFinalFinger} into the given file.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getBody")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void getIsFinalFingerBody(@javax.annotation.Nonnull final java.nio.file.Path body)
			throws
				java.io.IOException
	{
		AnItem.isFinalFinger.getBody(this,body);
	}

	/**
	 * Writes the body of media {@link #isFinalFinger} into the given file.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getBody")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void getIsFinalFingerBody(@javax.annotation.Nonnull final java.io.File body)
			throws
				java.io.IOException
	{
		AnItem.isFinalFinger.getBody(this,body);
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for anItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	private static final com.exedio.cope.Type<AnItem> TYPE = com.exedio.cope.TypesBound.newType(AnItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	protected AnItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}

	static final Model MODEL = new Model(AnItem.TYPE);

	static
	{
		MODEL.enableSerialization(MediaFinalUrlFingerPrintingTest.class, "MODEL");
	}
}
