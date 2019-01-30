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

	static final BooleanField accessControlAllowOriginWildcard = new BooleanField().defaultTo(false);


	@Wrapper(wrap="getURL", visibility=NONE)
	@RedirectFrom({"normalRedirect1", "normalRedirect2"})
	static final MediaPathFeature normal = new MediaPathFeature();


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
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	public MediaPathItem()
	{
		this(new com.exedio.cope.SetValue<?>[]{
		});
	}

	/**
	 * Creates a new MediaPathItem and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private MediaPathItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #catchphrase}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	java.lang.String getCatchphrase()
	{
		return MediaPathItem.catchphrase.get(this);
	}

	/**
	 * Sets a new value for {@link #catchphrase}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setCatchphrase(@javax.annotation.Nullable final java.lang.String catchphrase)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		MediaPathItem.catchphrase.set(this,catchphrase);
	}

	/**
	 * Returns the value of {@link #cacheControlMaximumAge}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	private java.lang.String getCacheControlMaximumAgeInternal()
	{
		return MediaPathItem.cacheControlMaximumAge.get(this);
	}

	/**
	 * Sets a new value for {@link #cacheControlMaximumAge}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setCacheControlMaximumAge(@javax.annotation.Nullable final java.lang.String cacheControlMaximumAge)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		MediaPathItem.cacheControlMaximumAge.set(this,cacheControlMaximumAge);
	}

	/**
	 * Returns the value of {@link #cacheControlPrivate}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	boolean getCacheControlPrivate()
	{
		return MediaPathItem.cacheControlPrivate.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #cacheControlPrivate}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setCacheControlPrivate(final boolean cacheControlPrivate)
	{
		MediaPathItem.cacheControlPrivate.set(this,cacheControlPrivate);
	}

	/**
	 * Returns the value of {@link #accessControlAllowOriginWildcard}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	boolean getAccessControlAllowOriginWildcard()
	{
		return MediaPathItem.accessControlAllowOriginWildcard.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #accessControlAllowOriginWildcard}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setAccessControlAllowOriginWildcard(final boolean accessControlAllowOriginWildcard)
	{
		MediaPathItem.accessControlAllowOriginWildcard.set(this,accessControlAllowOriginWildcard);
	}

	/**
	 * Returns a Locator the content of {@link #normal} is available under.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLocator")
	@javax.annotation.Nullable
	com.exedio.cope.pattern.MediaPath.Locator getNormalLocator()
	{
		return MediaPathItem.normal.getLocator(this);
	}

	/**
	 * Returns the content type of the media {@link #normal}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getContentType")
	@javax.annotation.Nullable
	java.lang.String getNormalContentType()
	{
		return MediaPathItem.normal.getContentType(this);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="setContentType")
	void setNormalContentType(final java.lang.String normal)
	{
		MediaPathItem.normal.setContentType(this,normal);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="setLastModified")
	void setNormalLastModified(final java.util.Date normal)
	{
		MediaPathItem.normal.setLastModified(this,normal);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="setResult")
	void setNormalResult(final com.exedio.cope.pattern.MediaPathFeature.Result normal)
	{
		MediaPathItem.normal.setResult(this,normal);
	}

	/**
	 * Returns a Locator the content of {@link #finger} is available under.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLocator")
	@javax.annotation.Nullable
	com.exedio.cope.pattern.MediaPath.Locator getFingerLocator()
	{
		return MediaPathItem.finger.getLocator(this);
	}

	/**
	 * Returns the content type of the media {@link #finger}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getContentType")
	@javax.annotation.Nullable
	java.lang.String getFingerContentType()
	{
		return MediaPathItem.finger.getContentType(this);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="setContentType")
	void setFingerContentType(final java.lang.String finger)
	{
		MediaPathItem.finger.setContentType(this,finger);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="setLastModified")
	void setFingerLastModified(final java.util.Date finger)
	{
		MediaPathItem.finger.setLastModified(this,finger);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="setResult")
	void setFingerResult(final com.exedio.cope.pattern.MediaPathFeature.Result finger)
	{
		MediaPathItem.finger.setResult(this,finger);
	}

	/**
	 * Returns a Locator the content of {@link #guess} is available under.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLocator")
	@javax.annotation.Nullable
	com.exedio.cope.pattern.MediaPath.Locator getGuessLocator()
	{
		return MediaPathItem.guess.getLocator(this);
	}

	/**
	 * Returns the content type of the media {@link #guess}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getContentType")
	@javax.annotation.Nullable
	java.lang.String getGuessContentType()
	{
		return MediaPathItem.guess.getContentType(this);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="setContentType")
	void setGuessContentType(final java.lang.String guess)
	{
		MediaPathItem.guess.setContentType(this,guess);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="setLastModified")
	void setGuessLastModified(final java.util.Date guess)
	{
		MediaPathItem.guess.setLastModified(this,guess);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="setResult")
	void setGuessResult(final com.exedio.cope.pattern.MediaPathFeature.Result guess)
	{
		MediaPathItem.guess.setResult(this,guess);
	}

	/**
	 * Returns a Locator the content of {@link #fingerGuess} is available under.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLocator")
	@javax.annotation.Nullable
	com.exedio.cope.pattern.MediaPath.Locator getFingerGuessLocator()
	{
		return MediaPathItem.fingerGuess.getLocator(this);
	}

	/**
	 * Returns the content type of the media {@link #fingerGuess}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getContentType")
	@javax.annotation.Nullable
	java.lang.String getFingerGuessContentType()
	{
		return MediaPathItem.fingerGuess.getContentType(this);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="setContentType")
	void setFingerGuessContentType(final java.lang.String fingerGuess)
	{
		MediaPathItem.fingerGuess.setContentType(this,fingerGuess);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="setLastModified")
	void setFingerGuessLastModified(final java.util.Date fingerGuess)
	{
		MediaPathItem.fingerGuess.setLastModified(this,fingerGuess);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="setResult")
	void setFingerGuessResult(final com.exedio.cope.pattern.MediaPathFeature.Result fingerGuess)
	{
		MediaPathItem.fingerGuess.setResult(this,fingerGuess);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for mediaPathItem.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<MediaPathItem> TYPE = com.exedio.cope.TypesBound.newType(MediaPathItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@SuppressWarnings("unused") private MediaPathItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
