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
import static org.junit.Assert.assertEquals;

import com.exedio.cope.Item;
import com.exedio.cope.instrument.WrapperIgnore;
import com.exedio.cope.instrument.WrapperType;
import org.junit.jupiter.api.Test;

public class MediaServletDefaultTest
{
	private final MediaServlet servlet =  new MediaServlet();

	@Test public void testCacheControlPrivateSimple()
	{
		assertEquals(false, servlet.isCacheControlPrivate(CacheControlPrivateItem.simple, null));
	}
	@Test public void testCacheControlPrivateSecret()
	{
		assertEquals(true,  servlet.isCacheControlPrivate(CacheControlPrivateItem.secret, null));
	}
	@Test public void testCacheControlPrivateFinger()
	{
		assertEquals(false, servlet.isCacheControlPrivate(CacheControlPrivateItem.finger, null));
	}
	@Test public void testCacheControlPrivateSecretFinger()
	{
		assertEquals(true,  servlet.isCacheControlPrivate(CacheControlPrivateItem.secfin, null));
	}
	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	static final class CacheControlPrivateItem extends Item
	{
		@WrapperIgnore static final Media simple = new Media();
		@PreventUrlGuessing
		@WrapperIgnore static final Media secret = new Media();
		@UrlFingerPrinting
		@WrapperIgnore static final Media finger = new Media();
		@PreventUrlGuessing @UrlFingerPrinting
		@WrapperIgnore static final Media secfin = new Media();

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		@javax.annotation.Generated("com.exedio.cope.instrument")
		static final com.exedio.cope.Type<CacheControlPrivateItem> TYPE = com.exedio.cope.TypesBound.newType(CacheControlPrivateItem.class);

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@SuppressWarnings("unused") private CacheControlPrivateItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}


	@Test public void testAccessControlAllowOriginWildcard()
	{
		assertEquals(false, servlet.isAccessControlAllowOriginWildcard(null, null));
	}
	@Test public void testFlushBufferOnNotModified()
	{
		assertEquals(true, servlet.doFlushBufferOnNotModified(null, null));
	}
}
