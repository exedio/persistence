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

import static org.junit.Assert.assertEquals;

import com.exedio.cope.Item;
import com.exedio.cope.Model;
import org.junit.Test;

/**
 * This test became useless, as there is no connection between
 * toFinal and @UrlFingerPrinting anymore.
 */
public class MediaFinalUrlFingerPrintingTest
{
	@Test public void testIt()
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


	static class AnItem extends Item
	{
		static final Media nonFinal = new Media();
		static final Media isFinal = new Media().toFinal();
		@UrlFingerPrinting()
		static final Media nonFinalFinger = new Media();
		@UrlFingerPrinting()
		static final Media isFinalFinger = new Media().toFinal();

	/**

	 **
	 * Creates a new AnItem with all the fields initially needed.
	 * @param nonFinal the initial value for field {@link #nonFinal}.
	 * @param isFinal the initial value for field {@link #isFinal}.
	 * @param nonFinalFinger the initial value for field {@link #nonFinalFinger}.
	 * @param isFinalFinger the initial value for field {@link #isFinalFinger}.
	 * @throws com.exedio.cope.MandatoryViolationException if nonFinal, isFinal, nonFinalFinger, isFinalFinger is null.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tags <tt>@cope.constructor public|package|protected|private|none</tt> in the class comment and <tt>@cope.initial</tt> in the comment of fields.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	AnItem(
				final com.exedio.cope.pattern.Media.Value nonFinal,
				final com.exedio.cope.pattern.Media.Value isFinal,
				final com.exedio.cope.pattern.Media.Value nonFinalFinger,
				final com.exedio.cope.pattern.Media.Value isFinalFinger)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			AnItem.nonFinal.map(nonFinal),
			AnItem.isFinal.map(isFinal),
			AnItem.nonFinalFinger.map(nonFinalFinger),
			AnItem.isFinalFinger.map(isFinalFinger),
		});
	}/**

	 **
	 * Creates a new AnItem and sets the given fields initially.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.generic.constructor public|package|protected|private|none</tt> in the class comment.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	protected AnItem(final com.exedio.cope.SetValue<?>... setValues)
	{
		super(setValues);
	}/**

	 **
	 * Returns a URL the content of {@link #nonFinal} is available under.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.getURL public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	final java.lang.String getNonFinalURL()
	{
		return AnItem.nonFinal.getURL(this);
	}/**

	 **
	 * Returns a Locator the content of {@link #nonFinal} is available under.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.getLocator public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	final com.exedio.cope.pattern.MediaPath.Locator getNonFinalLocator()
	{
		return AnItem.nonFinal.getLocator(this);
	}/**

	 **
	 * Returns the content type of the media {@link #nonFinal}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.getContentType public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	final java.lang.String getNonFinalContentType()
	{
		return AnItem.nonFinal.getContentType(this);
	}/**

	 **
	 * Returns the last modification date of media {@link #nonFinal}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.getLastModified public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	final java.util.Date getNonFinalLastModified()
	{
		return AnItem.nonFinal.getLastModified(this);
	}/**

	 **
	 * Returns the body length of the media {@link #nonFinal}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.getLength public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	final long getNonFinalLength()
	{
		return AnItem.nonFinal.getLength(this);
	}/**

	 **
	 * Returns the body of the media {@link #nonFinal}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.getBody public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	final byte[] getNonFinalBody()
	{
		return AnItem.nonFinal.getBody(this);
	}/**

	 **
	 * Writes the body of media {@link #nonFinal} into the given stream.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing <tt>body</tt> throws an IOException.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.getBody public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	final void getNonFinalBody(final java.io.OutputStream body)
			throws
				java.io.IOException
	{
		AnItem.nonFinal.getBody(this,body);
	}/**

	 **
	 * Writes the body of media {@link #nonFinal} into the given file.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing <tt>body</tt> throws an IOException.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.getBody public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	final void getNonFinalBody(final java.io.File body)
			throws
				java.io.IOException
	{
		AnItem.nonFinal.getBody(this,body);
	}/**

	 **
	 * Sets the content of media {@link #nonFinal}.
	 * @throws java.io.IOException if accessing <tt>body</tt> throws an IOException.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.set public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	final void setNonFinal(final com.exedio.cope.pattern.Media.Value nonFinal)
			throws
				java.io.IOException
	{
		AnItem.nonFinal.set(this,nonFinal);
	}/**

	 **
	 * Sets the content of media {@link #nonFinal}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.set public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	final void setNonFinal(final byte[] body,final java.lang.String contentType)
	{
		AnItem.nonFinal.set(this,body,contentType);
	}/**

	 **
	 * Sets the content of media {@link #nonFinal}.
	 * @throws java.io.IOException if accessing <tt>body</tt> throws an IOException.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.set public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	final void setNonFinal(final java.io.InputStream body,final java.lang.String contentType)
			throws
				java.io.IOException
	{
		AnItem.nonFinal.set(this,body,contentType);
	}/**

	 **
	 * Sets the content of media {@link #nonFinal}.
	 * @throws java.io.IOException if accessing <tt>body</tt> throws an IOException.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.set public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	final void setNonFinal(final java.io.File body,final java.lang.String contentType)
			throws
				java.io.IOException
	{
		AnItem.nonFinal.set(this,body,contentType);
	}/**

	 **
	 * Returns a URL the content of {@link #isFinal} is available under.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.getURL public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	final java.lang.String getIsFinalURL()
	{
		return AnItem.isFinal.getURL(this);
	}/**

	 **
	 * Returns a Locator the content of {@link #isFinal} is available under.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.getLocator public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	final com.exedio.cope.pattern.MediaPath.Locator getIsFinalLocator()
	{
		return AnItem.isFinal.getLocator(this);
	}/**

	 **
	 * Returns the content type of the media {@link #isFinal}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.getContentType public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	final java.lang.String getIsFinalContentType()
	{
		return AnItem.isFinal.getContentType(this);
	}/**

	 **
	 * Returns the last modification date of media {@link #isFinal}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.getLastModified public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	final java.util.Date getIsFinalLastModified()
	{
		return AnItem.isFinal.getLastModified(this);
	}/**

	 **
	 * Returns the body length of the media {@link #isFinal}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.getLength public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	final long getIsFinalLength()
	{
		return AnItem.isFinal.getLength(this);
	}/**

	 **
	 * Returns the body of the media {@link #isFinal}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.getBody public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	final byte[] getIsFinalBody()
	{
		return AnItem.isFinal.getBody(this);
	}/**

	 **
	 * Writes the body of media {@link #isFinal} into the given stream.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing <tt>body</tt> throws an IOException.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.getBody public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	final void getIsFinalBody(final java.io.OutputStream body)
			throws
				java.io.IOException
	{
		AnItem.isFinal.getBody(this,body);
	}/**

	 **
	 * Writes the body of media {@link #isFinal} into the given file.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing <tt>body</tt> throws an IOException.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.getBody public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	final void getIsFinalBody(final java.io.File body)
			throws
				java.io.IOException
	{
		AnItem.isFinal.getBody(this,body);
	}/**

	 **
	 * Returns a URL the content of {@link #nonFinalFinger} is available under.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.getURL public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	final java.lang.String getNonFinalFingerURL()
	{
		return AnItem.nonFinalFinger.getURL(this);
	}/**

	 **
	 * Returns a Locator the content of {@link #nonFinalFinger} is available under.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.getLocator public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	final com.exedio.cope.pattern.MediaPath.Locator getNonFinalFingerLocator()
	{
		return AnItem.nonFinalFinger.getLocator(this);
	}/**

	 **
	 * Returns the content type of the media {@link #nonFinalFinger}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.getContentType public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	final java.lang.String getNonFinalFingerContentType()
	{
		return AnItem.nonFinalFinger.getContentType(this);
	}/**

	 **
	 * Returns the last modification date of media {@link #nonFinalFinger}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.getLastModified public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	final java.util.Date getNonFinalFingerLastModified()
	{
		return AnItem.nonFinalFinger.getLastModified(this);
	}/**

	 **
	 * Returns the body length of the media {@link #nonFinalFinger}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.getLength public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	final long getNonFinalFingerLength()
	{
		return AnItem.nonFinalFinger.getLength(this);
	}/**

	 **
	 * Returns the body of the media {@link #nonFinalFinger}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.getBody public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	final byte[] getNonFinalFingerBody()
	{
		return AnItem.nonFinalFinger.getBody(this);
	}/**

	 **
	 * Writes the body of media {@link #nonFinalFinger} into the given stream.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing <tt>body</tt> throws an IOException.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.getBody public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	final void getNonFinalFingerBody(final java.io.OutputStream body)
			throws
				java.io.IOException
	{
		AnItem.nonFinalFinger.getBody(this,body);
	}/**

	 **
	 * Writes the body of media {@link #nonFinalFinger} into the given file.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing <tt>body</tt> throws an IOException.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.getBody public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	final void getNonFinalFingerBody(final java.io.File body)
			throws
				java.io.IOException
	{
		AnItem.nonFinalFinger.getBody(this,body);
	}/**

	 **
	 * Sets the content of media {@link #nonFinalFinger}.
	 * @throws java.io.IOException if accessing <tt>body</tt> throws an IOException.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.set public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	final void setNonFinalFinger(final com.exedio.cope.pattern.Media.Value nonFinalFinger)
			throws
				java.io.IOException
	{
		AnItem.nonFinalFinger.set(this,nonFinalFinger);
	}/**

	 **
	 * Sets the content of media {@link #nonFinalFinger}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.set public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	final void setNonFinalFinger(final byte[] body,final java.lang.String contentType)
	{
		AnItem.nonFinalFinger.set(this,body,contentType);
	}/**

	 **
	 * Sets the content of media {@link #nonFinalFinger}.
	 * @throws java.io.IOException if accessing <tt>body</tt> throws an IOException.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.set public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	final void setNonFinalFinger(final java.io.InputStream body,final java.lang.String contentType)
			throws
				java.io.IOException
	{
		AnItem.nonFinalFinger.set(this,body,contentType);
	}/**

	 **
	 * Sets the content of media {@link #nonFinalFinger}.
	 * @throws java.io.IOException if accessing <tt>body</tt> throws an IOException.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.set public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	final void setNonFinalFinger(final java.io.File body,final java.lang.String contentType)
			throws
				java.io.IOException
	{
		AnItem.nonFinalFinger.set(this,body,contentType);
	}/**

	 **
	 * Returns a URL the content of {@link #isFinalFinger} is available under.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.getURL public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	final java.lang.String getIsFinalFingerURL()
	{
		return AnItem.isFinalFinger.getURL(this);
	}/**

	 **
	 * Returns a Locator the content of {@link #isFinalFinger} is available under.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.getLocator public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	final com.exedio.cope.pattern.MediaPath.Locator getIsFinalFingerLocator()
	{
		return AnItem.isFinalFinger.getLocator(this);
	}/**

	 **
	 * Returns the content type of the media {@link #isFinalFinger}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.getContentType public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	final java.lang.String getIsFinalFingerContentType()
	{
		return AnItem.isFinalFinger.getContentType(this);
	}/**

	 **
	 * Returns the last modification date of media {@link #isFinalFinger}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.getLastModified public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	final java.util.Date getIsFinalFingerLastModified()
	{
		return AnItem.isFinalFinger.getLastModified(this);
	}/**

	 **
	 * Returns the body length of the media {@link #isFinalFinger}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.getLength public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	final long getIsFinalFingerLength()
	{
		return AnItem.isFinalFinger.getLength(this);
	}/**

	 **
	 * Returns the body of the media {@link #isFinalFinger}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.getBody public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	final byte[] getIsFinalFingerBody()
	{
		return AnItem.isFinalFinger.getBody(this);
	}/**

	 **
	 * Writes the body of media {@link #isFinalFinger} into the given stream.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing <tt>body</tt> throws an IOException.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.getBody public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	final void getIsFinalFingerBody(final java.io.OutputStream body)
			throws
				java.io.IOException
	{
		AnItem.isFinalFinger.getBody(this,body);
	}/**

	 **
	 * Writes the body of media {@link #isFinalFinger} into the given file.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing <tt>body</tt> throws an IOException.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.getBody public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	final void getIsFinalFingerBody(final java.io.File body)
			throws
				java.io.IOException
	{
		AnItem.isFinalFinger.getBody(this,body);
	}/**

	 **
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;/**

	 **
	 * The persistent type information for anItem.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.type public|package|protected|private|none</tt> in the class comment.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	static final com.exedio.cope.Type<AnItem> TYPE = com.exedio.cope.TypesBound.newType(AnItem.class);/**

	 **
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	protected AnItem(final com.exedio.cope.ActivationParameters ap){super(ap);
}}

	static final Model MODEL = new Model(AnItem.TYPE);

	static
	{
		MODEL.enableSerialization(MediaFinalUrlFingerPrintingTest.class, "MODEL");
	}
}