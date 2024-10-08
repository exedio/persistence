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
import com.exedio.cope.pattern.sub.TextUrlFilterOverride;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public final class TextUrlFilterItem extends Item
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

	void setFertigRaw(final String value) throws IOException
	{
		setFertigRaw(Media.toValue(value.getBytes(StandardCharsets.UTF_8), "text/plain"));
	}

	String addFertigPaste(final String key)
	{
		return "/contextPath/servletPath/" + addFertigPaste(key, Media.toValue(new byte[]{1, 2, 3}, MediaType.PNG)).getLocator().getPath();
	}

	// must not be generated by the instrumentor
	// because photo has a fixed contentType
	String getFertigContentType()
	{
		return fertig.getContentType(this);
	}


	/**
	 * Creates a new TextUrlFilterItem with all the fields initially needed.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	public TextUrlFilterItem()
	{
		this(com.exedio.cope.SetValue.EMPTY_ARRAY);
	}

	/**
	 * Creates a new TextUrlFilterItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private TextUrlFilterItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #name}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.lang.String getName()
	{
		return TextUrlFilterItem.name.get(this);
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
		TextUrlFilterItem.name.set(this,name);
	}

	/**
	 * Returns a Locator the content of {@link #roh} is available under.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLocator")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	com.exedio.cope.pattern.MediaPath.Locator getRohLocator()
	{
		return TextUrlFilterItem.roh.getLocator(this);
	}

	/**
	 * Returns the content type of the media {@link #roh}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getContentType")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.lang.String getRohContentType()
	{
		return TextUrlFilterItem.roh.getContentType(this);
	}

	/**
	 * Returns whether media {@link #roh} is null.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="isNull")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	boolean isRohNull()
	{
		return TextUrlFilterItem.roh.isNull(this);
	}

	/**
	 * Returns the last modification date of media {@link #roh}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLastModified")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.util.Date getRohLastModified()
	{
		return TextUrlFilterItem.roh.getLastModified(this);
	}

	/**
	 * Returns the body length of the media {@link #roh}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLength")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	long getRohLength()
	{
		return TextUrlFilterItem.roh.getLength(this);
	}

	/**
	 * Returns the body of the media {@link #roh}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getBody")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	byte[] getRohBody()
	{
		return TextUrlFilterItem.roh.getBody(this);
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
		TextUrlFilterItem.roh.getBody(this,body);
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
		TextUrlFilterItem.roh.getBody(this,body);
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
		TextUrlFilterItem.roh.getBody(this,body);
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
		TextUrlFilterItem.roh.set(this,roh);
	}

	/**
	 * Sets the content of media {@link #roh}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setRoh(@javax.annotation.Nullable final byte[] body,@javax.annotation.Nullable final java.lang.String contentType)
	{
		TextUrlFilterItem.roh.set(this,body,contentType);
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
		TextUrlFilterItem.roh.set(this,body,contentType);
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
		TextUrlFilterItem.roh.set(this,body,contentType);
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
		TextUrlFilterItem.roh.set(this,body,contentType);
	}

	/**
	 * Returns a Locator the content of {@link #fertig} is available under.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLocator")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	com.exedio.cope.pattern.MediaPath.Locator getFertigLocator()
	{
		return TextUrlFilterItem.fertig.getLocator(this);
	}

	/**
	 * Returns a URL the content of {@link #fertig} is available under, falling back to source if necessary.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getURLWithFallbackToSource")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.lang.String getFertigURLWithFallbackToSource()
	{
		return TextUrlFilterItem.fertig.getURLWithFallbackToSource(this);
	}

	/**
	 * Returns a Locator the content of {@link #fertig} is available under, falling back to source if necessary.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLocatorWithFallbackToSource")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	com.exedio.cope.pattern.MediaPath.Locator getFertigLocatorWithFallbackToSource()
	{
		return TextUrlFilterItem.fertig.getLocatorWithFallbackToSource(this);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="setRaw")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setFertigRaw(@javax.annotation.Nullable final com.exedio.cope.pattern.Media.Value raw)
			throws
				java.io.IOException
	{
		TextUrlFilterItem.fertig.setRaw(this,raw);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="addPaste")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	com.exedio.cope.pattern.TextUrlFilter.Paste addFertigPaste(@javax.annotation.Nonnull final java.lang.String key,@javax.annotation.Nonnull final com.exedio.cope.pattern.Media.Value value)
	{
		return TextUrlFilterItem.fertig.addPaste(this,key,value);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="modifyPaste")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void modifyFertigPaste(@javax.annotation.Nonnull final java.lang.String key,@javax.annotation.Nonnull final com.exedio.cope.pattern.Media.Value value)
			throws
				java.io.IOException
	{
		TextUrlFilterItem.fertig.modifyPaste(this,key,value);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="putPaste")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	com.exedio.cope.pattern.TextUrlFilter.Paste putFertigPaste(@javax.annotation.Nonnull final java.lang.String key,@javax.annotation.Nonnull final com.exedio.cope.pattern.Media.Value value)
			throws
				java.io.IOException
	{
		return TextUrlFilterItem.fertig.putPaste(this,key,value);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getContent")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	java.lang.String getFertigContent(@javax.annotation.Nonnull final javax.servlet.http.HttpServletRequest request)
			throws
				com.exedio.cope.pattern.MediaPath.NotFound
	{
		return TextUrlFilterItem.fertig.getContent(this,request);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="check")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	java.util.Set<java.lang.String> checkFertig()
			throws
				com.exedio.cope.pattern.MediaPath.NotFound
	{
		return TextUrlFilterItem.fertig.check(this);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="putPastesFromZip")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void putFertigPastesFromZip(@javax.annotation.Nonnull final java.io.File file)
			throws
				java.io.IOException
	{
		TextUrlFilterItem.fertig.putPastesFromZip(this,file);
	}

	@com.exedio.cope.instrument.Generated
	@java.io.Serial
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for textUrlFilterItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<TextUrlFilterItem> TYPE = com.exedio.cope.TypesBound.newType(TextUrlFilterItem.class,TextUrlFilterItem::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private TextUrlFilterItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
