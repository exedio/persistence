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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.BooleanField;
import com.exedio.cope.CopeSchemaName;
import com.exedio.cope.Item;
import com.exedio.cope.StringField;
import com.exedio.cope.instrument.Wrapper;
import java.time.Duration;

public final class MediaPathItem extends Item implements MediaUrlCatchphraseProvider
{
	@CopeSchemaName("phrase")
	static final StringField catchphrase = new StringField().optional();

	@Wrapper(wrap="get", internal=true)
	static final StringField cacheControlMaximumAge = new StringField().optional().defaultTo("FAILS");

	Duration getCacheControlMaximumAge()
	{
		final String result = getCacheControlMaximumAgeInternal();
		if(cacheControlMaximumAge.getDefaultConstant().equals(result))
			fail(result+cacheControlMaximumAge.getID());
		if(result==null)
			return null;
		return Duration.parse(result);
	}

	static final BooleanField cacheControlPrivate = new BooleanField().defaultTo(false);

	static final ListField<String> headers = ListField.create(new StringField());

	static final BooleanField accessControlAllowOriginWildcard = new BooleanField().defaultTo(false);


	@Wrapper(wrap="getURL", visibility=NONE)
	@RedirectFrom({"normalRedirect1", "normalRedirect2"})
	static final MediaPathFeature normal = new MediaPathFeature();


	@Wrapper(wrap="getURL", visibility=NONE)
	static final MediaPathFeature mandat = new MediaPathFeature().mandatory();


	@Wrapper(wrap="getURL", visibility=NONE)
	@RedirectFrom({"fingerRedirect1", "fingerRedirect2"})
	@UrlFingerPrinting
	static final MediaPathFeature finger = new MediaPathFeature();


	@Wrapper(wrap="getURL", visibility=NONE)
	@PreventUrlGuessing
	static final MediaPathFeature guess = new MediaPathFeature();


	@Wrapper(wrap="getURL", visibility=NONE)
	@UrlFingerPrinting @PreventUrlGuessing
	static final MediaPathFeature fingerGuess = new MediaPathFeature();


	@Override
	public String getMediaUrlCatchphrase(final MediaPath path)
	{
		assertNotNull(path);
		return getCatchphrase();
	}


	/**
	 * Creates a new MediaPathItem with all the fields initially needed.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	public MediaPathItem()
	{
		this(com.exedio.cope.SetValue.EMPTY_ARRAY);
	}

	/**
	 * Creates a new MediaPathItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private MediaPathItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #catchphrase}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.lang.String getCatchphrase()
	{
		return MediaPathItem.catchphrase.get(this);
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
		MediaPathItem.catchphrase.set(this,catchphrase);
	}

	/**
	 * Returns the value of {@link #cacheControlMaximumAge}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	private java.lang.String getCacheControlMaximumAgeInternal()
	{
		return MediaPathItem.cacheControlMaximumAge.get(this);
	}

	/**
	 * Sets a new value for {@link #cacheControlMaximumAge}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setCacheControlMaximumAge(@javax.annotation.Nullable final java.lang.String cacheControlMaximumAge)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		MediaPathItem.cacheControlMaximumAge.set(this,cacheControlMaximumAge);
	}

	/**
	 * Returns the value of {@link #cacheControlPrivate}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	boolean getCacheControlPrivate()
	{
		return MediaPathItem.cacheControlPrivate.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #cacheControlPrivate}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setCacheControlPrivate(final boolean cacheControlPrivate)
	{
		MediaPathItem.cacheControlPrivate.set(this,cacheControlPrivate);
	}

	/**
	 * Returns the value of {@link #headers}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	java.util.List<String> getHeaders()
	{
		return MediaPathItem.headers.get(this);
	}

	/**
	 * Returns a query for the value of {@link #headers}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getQuery")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	com.exedio.cope.Query<String> getHeadersQuery()
	{
		return MediaPathItem.headers.getQuery(this);
	}

	/**
	 * Returns the items, for which field list {@link #headers} contains the given element.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getDistinctParentsOf")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	static java.util.List<MediaPathItem> getDistinctParentsOfHeaders(final String element)
	{
		return MediaPathItem.headers.getDistinctParents(MediaPathItem.class,element);
	}

	/**
	 * Adds a new value for {@link #headers}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="addTo")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void addToHeaders(@javax.annotation.Nonnull final String headers)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException,
				java.lang.ClassCastException
	{
		MediaPathItem.headers.add(this,headers);
	}

	/**
	 * Removes all occurrences of {@code element} from {@link #headers}.
	 * @return {@code true} if the field set changed as a result of the call.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="removeAllFrom")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	boolean removeAllFromHeaders(@javax.annotation.Nonnull final String headers)
	{
		return MediaPathItem.headers.removeAll(this,headers);
	}

	/**
	 * Sets a new value for {@link #headers}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setHeaders(@javax.annotation.Nonnull final java.util.Collection<? extends String> headers)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException,
				java.lang.ClassCastException
	{
		MediaPathItem.headers.set(this,headers);
	}

	/**
	 * Returns the parent field of the type of {@link #headers}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="Parent")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	static com.exedio.cope.ItemField<MediaPathItem> headersParent()
	{
		return MediaPathItem.headers.getParent(MediaPathItem.class);
	}

	/**
	 * Returns the value of {@link #accessControlAllowOriginWildcard}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	boolean getAccessControlAllowOriginWildcard()
	{
		return MediaPathItem.accessControlAllowOriginWildcard.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #accessControlAllowOriginWildcard}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setAccessControlAllowOriginWildcard(final boolean accessControlAllowOriginWildcard)
	{
		MediaPathItem.accessControlAllowOriginWildcard.set(this,accessControlAllowOriginWildcard);
	}

	/**
	 * Returns a Locator the content of {@link #normal} is available under.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLocator")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	com.exedio.cope.pattern.MediaPath.Locator getNormalLocator()
	{
		return MediaPathItem.normal.getLocator(this);
	}

	/**
	 * Returns the content type of the media {@link #normal}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getContentType")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.lang.String getNormalContentType()
	{
		return MediaPathItem.normal.getContentType(this);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="setContentType")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setNormalContentType(final java.lang.String normal)
	{
		MediaPathItem.normal.setContentType(this,normal);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="setLastModified")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setNormalLastModified(final java.util.Date normal)
	{
		MediaPathItem.normal.setLastModified(this,normal);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="setResult")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setNormalResult(final com.exedio.cope.pattern.MediaPathFeature.Result normal)
	{
		MediaPathItem.normal.setResult(this,normal);
	}

	/**
	 * Returns a Locator the content of {@link #mandat} is available under.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLocator")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	com.exedio.cope.pattern.MediaPath.Locator getMandatLocator()
	{
		return MediaPathItem.mandat.getLocator(this);
	}

	/**
	 * Returns the content type of the media {@link #mandat}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getContentType")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	java.lang.String getMandatContentType()
	{
		return MediaPathItem.mandat.getContentType(this);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="setContentType")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setMandatContentType(final java.lang.String mandat)
	{
		MediaPathItem.mandat.setContentType(this,mandat);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="setLastModified")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setMandatLastModified(final java.util.Date mandat)
	{
		MediaPathItem.mandat.setLastModified(this,mandat);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="setResult")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setMandatResult(final com.exedio.cope.pattern.MediaPathFeature.Result mandat)
	{
		MediaPathItem.mandat.setResult(this,mandat);
	}

	/**
	 * Returns a Locator the content of {@link #finger} is available under.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLocator")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	com.exedio.cope.pattern.MediaPath.Locator getFingerLocator()
	{
		return MediaPathItem.finger.getLocator(this);
	}

	/**
	 * Returns the content type of the media {@link #finger}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getContentType")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.lang.String getFingerContentType()
	{
		return MediaPathItem.finger.getContentType(this);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="setContentType")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setFingerContentType(final java.lang.String finger)
	{
		MediaPathItem.finger.setContentType(this,finger);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="setLastModified")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setFingerLastModified(final java.util.Date finger)
	{
		MediaPathItem.finger.setLastModified(this,finger);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="setResult")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setFingerResult(final com.exedio.cope.pattern.MediaPathFeature.Result finger)
	{
		MediaPathItem.finger.setResult(this,finger);
	}

	/**
	 * Returns a Locator the content of {@link #guess} is available under.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLocator")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	com.exedio.cope.pattern.MediaPath.Locator getGuessLocator()
	{
		return MediaPathItem.guess.getLocator(this);
	}

	/**
	 * Returns the content type of the media {@link #guess}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getContentType")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.lang.String getGuessContentType()
	{
		return MediaPathItem.guess.getContentType(this);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="setContentType")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setGuessContentType(final java.lang.String guess)
	{
		MediaPathItem.guess.setContentType(this,guess);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="setLastModified")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setGuessLastModified(final java.util.Date guess)
	{
		MediaPathItem.guess.setLastModified(this,guess);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="setResult")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setGuessResult(final com.exedio.cope.pattern.MediaPathFeature.Result guess)
	{
		MediaPathItem.guess.setResult(this,guess);
	}

	/**
	 * Returns a Locator the content of {@link #fingerGuess} is available under.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLocator")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	com.exedio.cope.pattern.MediaPath.Locator getFingerGuessLocator()
	{
		return MediaPathItem.fingerGuess.getLocator(this);
	}

	/**
	 * Returns the content type of the media {@link #fingerGuess}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getContentType")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.lang.String getFingerGuessContentType()
	{
		return MediaPathItem.fingerGuess.getContentType(this);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="setContentType")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setFingerGuessContentType(final java.lang.String fingerGuess)
	{
		MediaPathItem.fingerGuess.setContentType(this,fingerGuess);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="setLastModified")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setFingerGuessLastModified(final java.util.Date fingerGuess)
	{
		MediaPathItem.fingerGuess.setLastModified(this,fingerGuess);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="setResult")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setFingerGuessResult(final com.exedio.cope.pattern.MediaPathFeature.Result fingerGuess)
	{
		MediaPathItem.fingerGuess.setResult(this,fingerGuess);
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for mediaPathItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<MediaPathItem> TYPE = com.exedio.cope.TypesBound.newType(MediaPathItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private MediaPathItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
