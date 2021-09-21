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
import static com.exedio.cope.tojunit.Assert.assertFails;
import static com.exedio.cope.tojunit.TestSources.single;
import static com.exedio.cope.util.Sources.cascade;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.ConnectProperties;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.instrument.WrapperIgnore;
import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.pattern.MediaPath.Locator;
import com.exedio.cope.tojunit.TestSources;
import java.time.Duration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

public class MediaServletDefaultTest
{
	private final MediaServlet servlet =  new MediaServlet();


	@Test void testMaximumAge()
	{
		final Locator locator = MaximumAgeItem.path.newLocator();
		assertFails(
				() -> servlet.getMaximumAge(locator),
				Model.NotConnectedException.class,
				"model not connected, use Model#connect for " + MaximumAgeItemModel);
		MaximumAgeItemModel.connect(ConnectProperties.create(cascade(
				TestSources.minimal(),
				single("media.offsetExpires", "PT76.543S")
		)));
		assertEquals(Duration.ofSeconds(76), servlet.getMaximumAge(locator));
	}
	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static final class MaximumAgeItem extends Item
	{
		@WrapperIgnore static final Media path = new Media();

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<MaximumAgeItem> TYPE = com.exedio.cope.TypesBound.newType(MaximumAgeItem.class);

		@com.exedio.cope.instrument.Generated
		private MaximumAgeItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
	private static final Model MaximumAgeItemModel = new Model(MaximumAgeItem.TYPE);
	@AfterEach void tearDownMaximumAge()
	{
		if(MaximumAgeItemModel.isConnected())
			MaximumAgeItemModel.disconnect();
	}


	@Test void testCacheControlPrivateSimple()
	{
		assertEquals(false, servlet.isCacheControlPrivate(CacheControlPrivateItem.simple.newLocator()));
	}
	@Test void testCacheControlPrivateSecret()
	{
		assertEquals(true,  servlet.isCacheControlPrivate(CacheControlPrivateItem.secret.newLocator()));
	}
	@Test void testCacheControlPrivateFinger()
	{
		assertEquals(false, servlet.isCacheControlPrivate(CacheControlPrivateItem.finger.newLocator()));
	}
	@Test void testCacheControlPrivateSecretFinger()
	{
		assertEquals(true,  servlet.isCacheControlPrivate(CacheControlPrivateItem.secfin.newLocator()));
	}
	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static final class CacheControlPrivateItem extends Item
	{
		@WrapperIgnore static final Media simple = new Media();
		@PreventUrlGuessing
		@WrapperIgnore static final Media secret = new Media();
		@UrlFingerPrinting
		@WrapperIgnore static final Media finger = new Media();
		@PreventUrlGuessing @UrlFingerPrinting
		@WrapperIgnore static final Media secfin = new Media();

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<CacheControlPrivateItem> TYPE = com.exedio.cope.TypesBound.newType(CacheControlPrivateItem.class);

		@com.exedio.cope.instrument.Generated
		private CacheControlPrivateItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}


	@Test void testAccessControlAllowOriginWildcard()
	{
		assertEquals(false, servlet.isAccessControlAllowOriginWildcard(null));
	}
	@Test void testFlushBufferOnNotModified()
	{
		assertEquals(false, servlet.doFlushBufferOnNotModified(null));
	}
}
