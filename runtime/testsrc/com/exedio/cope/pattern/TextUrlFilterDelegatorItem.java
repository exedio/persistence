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
import com.exedio.cope.StringField;
import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.pattern.sub.TextUrlFilterDelegatorOverride;
import com.exedio.cope.pattern.sub.TextUrlFilterOverride;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public final class TextUrlFilterDelegatorItem extends Item
{
	static final StringField name = new StringField().optional();

	@Wrapper(wrap="getURL", visibility=NONE)
	static final Media roh = new Media().optional();

	@Wrapper(wrap="getURL", visibility=NONE)
	static final TextUrlFilter fertig = new TextUrlFilterOverride(
			roh,
			"text/plain", StandardCharsets.UTF_8,
			new StringField(),
			new Media().lengthMax(3).contentType(MediaType.PNG));

	@Wrapper(wrap="getURL", visibility=NONE)
	static final Media roh2 = new Media().optional();

	@Wrapper(wrap="getURL", visibility=NONE)
	static final TextUrlFilterDelegator fertig2 = new TextUrlFilterDelegatorOverride(
			roh2,
			fertig,
			"text/plain", StandardCharsets.UTF_8);

	String addFertigPaste(final String key)
	{
		return "/contextPath/servletPath/" + addFertigPaste(key, Media.toValue(new byte[]{1, 2, 3}, MediaType.PNG)).getLocator().getPath();
	}

	void setFertig2Raw(final String value) throws IOException
	{
		setFertig2Raw(Media.toValue(value.getBytes(StandardCharsets.UTF_8), "text/plain"));
	}

	String getFertig2ContentType()
	{
		return fertig2.getContentType(this);
	}

	/**
	 * Creates a new TextUrlFilterDelegatorItem with all the fields initially needed.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	public TextUrlFilterDelegatorItem()
	{
		this(new com.exedio.cope.SetValue<?>[]{
		});
	}

	/**
	 * Creates a new TextUrlFilterDelegatorItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private TextUrlFilterDelegatorItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #name}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.lang.String getName()
	{
		return TextUrlFilterDelegatorItem.name.get(this);
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
		TextUrlFilterDelegatorItem.name.set(this,name);
	}

	/**
	 * Returns a Locator the content of {@link #roh} is available under.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLocator")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	com.exedio.cope.pattern.MediaPath.Locator getRohLocator()
	{
		return TextUrlFilterDelegatorItem.roh.getLocator(this);
	}

	/**
	 * Returns the content type of the media {@link #roh}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getContentType")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.lang.String getRohContentType()
	{
		return TextUrlFilterDelegatorItem.roh.getContentType(this);
	}

	/**
	 * Returns whether media {@link #roh} is null.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="isNull")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	boolean isRohNull()
	{
		return TextUrlFilterDelegatorItem.roh.isNull(this);
	}

	/**
	 * Returns the last modification date of media {@link #roh}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLastModified")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.util.Date getRohLastModified()
	{
		return TextUrlFilterDelegatorItem.roh.getLastModified(this);
	}

	/**
	 * Returns the body length of the media {@link #roh}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLength")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	long getRohLength()
	{
		return TextUrlFilterDelegatorItem.roh.getLength(this);
	}

	/**
	 * Returns the body of the media {@link #roh}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getBody")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	byte[] getRohBody()
	{
		return TextUrlFilterDelegatorItem.roh.getBody(this);
	}

	/**
	 * Writes the body of media {@link #roh} into the given stream.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getBody")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void getRohBody(@javax.annotation.Nonnull final java.io.OutputStream body)
			throws
				java.io.IOException
	{
		TextUrlFilterDelegatorItem.roh.getBody(this,body);
	}

	/**
	 * Writes the body of media {@link #roh} into the given file.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getBody")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void getRohBody(@javax.annotation.Nonnull final java.nio.file.Path body)
			throws
				java.io.IOException
	{
		TextUrlFilterDelegatorItem.roh.getBody(this,body);
	}

	/**
	 * Writes the body of media {@link #roh} into the given file.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getBody")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void getRohBody(@javax.annotation.Nonnull final java.io.File body)
			throws
				java.io.IOException
	{
		TextUrlFilterDelegatorItem.roh.getBody(this,body);
	}

	/**
	 * Sets the content of media {@link #roh}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setRoh(@javax.annotation.Nullable final com.exedio.cope.pattern.Media.Value roh)
			throws
				java.io.IOException
	{
		TextUrlFilterDelegatorItem.roh.set(this,roh);
	}

	/**
	 * Sets the content of media {@link #roh}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setRoh(@javax.annotation.Nullable final byte[] body,@javax.annotation.Nullable final java.lang.String contentType)
	{
		TextUrlFilterDelegatorItem.roh.set(this,body,contentType);
	}

	/**
	 * Sets the content of media {@link #roh}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setRoh(@javax.annotation.Nullable final java.io.InputStream body,@javax.annotation.Nullable final java.lang.String contentType)
			throws
				java.io.IOException
	{
		TextUrlFilterDelegatorItem.roh.set(this,body,contentType);
	}

	/**
	 * Sets the content of media {@link #roh}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setRoh(@javax.annotation.Nullable final java.nio.file.Path body,@javax.annotation.Nullable final java.lang.String contentType)
			throws
				java.io.IOException
	{
		TextUrlFilterDelegatorItem.roh.set(this,body,contentType);
	}

	/**
	 * Sets the content of media {@link #roh}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setRoh(@javax.annotation.Nullable final java.io.File body,@javax.annotation.Nullable final java.lang.String contentType)
			throws
				java.io.IOException
	{
		TextUrlFilterDelegatorItem.roh.set(this,body,contentType);
	}

	/**
	 * Returns a Locator the content of {@link #fertig} is available under.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLocator")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	com.exedio.cope.pattern.MediaPath.Locator getFertigLocator()
	{
		return TextUrlFilterDelegatorItem.fertig.getLocator(this);
	}

	/**
	 * Returns a URL the content of {@link #fertig} is available under, falling back to source if necessary.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getURLWithFallbackToSource")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.lang.String getFertigURLWithFallbackToSource()
	{
		return TextUrlFilterDelegatorItem.fertig.getURLWithFallbackToSource(this);
	}

	/**
	 * Returns a Locator the content of {@link #fertig} is available under, falling back to source if necessary.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLocatorWithFallbackToSource")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	com.exedio.cope.pattern.MediaPath.Locator getFertigLocatorWithFallbackToSource()
	{
		return TextUrlFilterDelegatorItem.fertig.getLocatorWithFallbackToSource(this);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="setRaw")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setFertigRaw(@javax.annotation.Nullable final com.exedio.cope.pattern.Media.Value raw)
			throws
				java.io.IOException
	{
		TextUrlFilterDelegatorItem.fertig.setRaw(this,raw);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="addPaste")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	com.exedio.cope.pattern.TextUrlFilter.Paste addFertigPaste(@javax.annotation.Nonnull final java.lang.String key,@javax.annotation.Nonnull final com.exedio.cope.pattern.Media.Value value)
	{
		return TextUrlFilterDelegatorItem.fertig.addPaste(this,key,value);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="modifyPaste")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void modifyFertigPaste(@javax.annotation.Nonnull final java.lang.String key,@javax.annotation.Nonnull final com.exedio.cope.pattern.Media.Value value)
			throws
				java.io.IOException
	{
		TextUrlFilterDelegatorItem.fertig.modifyPaste(this,key,value);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="putPaste")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	com.exedio.cope.pattern.TextUrlFilter.Paste putFertigPaste(@javax.annotation.Nonnull final java.lang.String key,@javax.annotation.Nonnull final com.exedio.cope.pattern.Media.Value value)
			throws
				java.io.IOException
	{
		return TextUrlFilterDelegatorItem.fertig.putPaste(this,key,value);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getContent")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	java.lang.String getFertigContent(@javax.annotation.Nonnull final javax.servlet.http.HttpServletRequest request)
			throws
				com.exedio.cope.pattern.MediaPath.NotFound
	{
		return TextUrlFilterDelegatorItem.fertig.getContent(this,request);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="check")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	java.util.Set<java.lang.String> checkFertig()
			throws
				com.exedio.cope.pattern.MediaPath.NotFound
	{
		return TextUrlFilterDelegatorItem.fertig.check(this);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="putPastesFromZip")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void putFertigPastesFromZip(@javax.annotation.Nonnull final java.io.File file)
			throws
				java.io.IOException
	{
		TextUrlFilterDelegatorItem.fertig.putPastesFromZip(this,file);
	}

	/**
	 * Returns a Locator the content of {@link #roh2} is available under.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLocator")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	com.exedio.cope.pattern.MediaPath.Locator getRoh2Locator()
	{
		return TextUrlFilterDelegatorItem.roh2.getLocator(this);
	}

	/**
	 * Returns the content type of the media {@link #roh2}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getContentType")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.lang.String getRoh2ContentType()
	{
		return TextUrlFilterDelegatorItem.roh2.getContentType(this);
	}

	/**
	 * Returns whether media {@link #roh2} is null.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="isNull")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	boolean isRoh2Null()
	{
		return TextUrlFilterDelegatorItem.roh2.isNull(this);
	}

	/**
	 * Returns the last modification date of media {@link #roh2}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLastModified")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.util.Date getRoh2LastModified()
	{
		return TextUrlFilterDelegatorItem.roh2.getLastModified(this);
	}

	/**
	 * Returns the body length of the media {@link #roh2}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLength")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	long getRoh2Length()
	{
		return TextUrlFilterDelegatorItem.roh2.getLength(this);
	}

	/**
	 * Returns the body of the media {@link #roh2}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getBody")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	byte[] getRoh2Body()
	{
		return TextUrlFilterDelegatorItem.roh2.getBody(this);
	}

	/**
	 * Writes the body of media {@link #roh2} into the given stream.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getBody")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void getRoh2Body(@javax.annotation.Nonnull final java.io.OutputStream body)
			throws
				java.io.IOException
	{
		TextUrlFilterDelegatorItem.roh2.getBody(this,body);
	}

	/**
	 * Writes the body of media {@link #roh2} into the given file.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getBody")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void getRoh2Body(@javax.annotation.Nonnull final java.nio.file.Path body)
			throws
				java.io.IOException
	{
		TextUrlFilterDelegatorItem.roh2.getBody(this,body);
	}

	/**
	 * Writes the body of media {@link #roh2} into the given file.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getBody")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void getRoh2Body(@javax.annotation.Nonnull final java.io.File body)
			throws
				java.io.IOException
	{
		TextUrlFilterDelegatorItem.roh2.getBody(this,body);
	}

	/**
	 * Sets the content of media {@link #roh2}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setRoh2(@javax.annotation.Nullable final com.exedio.cope.pattern.Media.Value roh2)
			throws
				java.io.IOException
	{
		TextUrlFilterDelegatorItem.roh2.set(this,roh2);
	}

	/**
	 * Sets the content of media {@link #roh2}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setRoh2(@javax.annotation.Nullable final byte[] body,@javax.annotation.Nullable final java.lang.String contentType)
	{
		TextUrlFilterDelegatorItem.roh2.set(this,body,contentType);
	}

	/**
	 * Sets the content of media {@link #roh2}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setRoh2(@javax.annotation.Nullable final java.io.InputStream body,@javax.annotation.Nullable final java.lang.String contentType)
			throws
				java.io.IOException
	{
		TextUrlFilterDelegatorItem.roh2.set(this,body,contentType);
	}

	/**
	 * Sets the content of media {@link #roh2}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setRoh2(@javax.annotation.Nullable final java.nio.file.Path body,@javax.annotation.Nullable final java.lang.String contentType)
			throws
				java.io.IOException
	{
		TextUrlFilterDelegatorItem.roh2.set(this,body,contentType);
	}

	/**
	 * Sets the content of media {@link #roh2}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setRoh2(@javax.annotation.Nullable final java.io.File body,@javax.annotation.Nullable final java.lang.String contentType)
			throws
				java.io.IOException
	{
		TextUrlFilterDelegatorItem.roh2.set(this,body,contentType);
	}

	/**
	 * Returns a Locator the content of {@link #fertig2} is available under.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLocator")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	com.exedio.cope.pattern.MediaPath.Locator getFertig2Locator()
	{
		return TextUrlFilterDelegatorItem.fertig2.getLocator(this);
	}

	/**
	 * Returns a URL the content of {@link #fertig2} is available under, falling back to source if necessary.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getURLWithFallbackToSource")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.lang.String getFertig2URLWithFallbackToSource()
	{
		return TextUrlFilterDelegatorItem.fertig2.getURLWithFallbackToSource(this);
	}

	/**
	 * Returns a Locator the content of {@link #fertig2} is available under, falling back to source if necessary.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLocatorWithFallbackToSource")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	com.exedio.cope.pattern.MediaPath.Locator getFertig2LocatorWithFallbackToSource()
	{
		return TextUrlFilterDelegatorItem.fertig2.getLocatorWithFallbackToSource(this);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="setRaw")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setFertig2Raw(@javax.annotation.Nullable final com.exedio.cope.pattern.Media.Value raw)
			throws
				java.io.IOException
	{
		TextUrlFilterDelegatorItem.fertig2.setRaw(this,raw);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getContent")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	java.lang.String getFertig2Content(@javax.annotation.Nonnull final javax.servlet.http.HttpServletRequest request)
			throws
				com.exedio.cope.pattern.MediaPath.NotFound
	{
		return TextUrlFilterDelegatorItem.fertig2.getContent(this,request);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="check")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	java.util.Set<java.lang.String> checkFertig2()
			throws
				com.exedio.cope.pattern.MediaPath.NotFound
	{
		return TextUrlFilterDelegatorItem.fertig2.check(this);
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for textUrlFilterDelegatorItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<TextUrlFilterDelegatorItem> TYPE = com.exedio.cope.TypesBound.newType(TextUrlFilterDelegatorItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private TextUrlFilterDelegatorItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
