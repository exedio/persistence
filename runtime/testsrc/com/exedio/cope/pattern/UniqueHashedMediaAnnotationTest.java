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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.exedio.cope.ActivationParameters;
import com.exedio.cope.Feature;
import com.exedio.cope.Item;
import com.exedio.cope.Type;
import com.exedio.cope.TypesBound;
import com.exedio.cope.instrument.WrapperIgnore;
import com.exedio.cope.misc.Computed;
import java.lang.annotation.Annotation;
import org.junit.jupiter.api.Test;

public class UniqueHashedMediaAnnotationTest
{
	@Test void testPreventUrlGuessing()
	{
		assertPresent(false, AnItem.simple,            PreventUrlGuessing.class);
		assertPresent(false, AnItem.simple.getMedia(), PreventUrlGuessing.class);
		assertPresent(true,  AnItem.secret,            PreventUrlGuessing.class);
		assertPresent(true,  AnItem.secret.getMedia(), PreventUrlGuessing.class);
		assertPresent(false, AnItem.finger,            PreventUrlGuessing.class);
		assertPresent(false, AnItem.finger.getMedia(), PreventUrlGuessing.class);
		assertPresent(true,  AnItem.secfin,            PreventUrlGuessing.class);
		assertPresent(true,  AnItem.secfin.getMedia(), PreventUrlGuessing.class);
	}

	@Test void testUrlFingerPrinting()
	{
		assertPresent(false, AnItem.simple,            UrlFingerPrinting.class);
		assertPresent(false, AnItem.simple.getMedia(), UrlFingerPrinting.class);
		assertPresent(false, AnItem.secret,            UrlFingerPrinting.class);
		assertPresent(false, AnItem.secret.getMedia(), UrlFingerPrinting.class);
		assertPresent(true,  AnItem.finger,            UrlFingerPrinting.class);
		assertPresent(true,  AnItem.finger.getMedia(), UrlFingerPrinting.class);
		assertPresent(true,  AnItem.secfin,            UrlFingerPrinting.class);
		assertPresent(true,  AnItem.secfin.getMedia(), UrlFingerPrinting.class);
	}

	@Test void testPreventUrlGuessingHash()
	{
		assertPresent(false, AnItem.simple.getHash(), PreventUrlGuessing.class);
		assertPresent(false, AnItem.secret.getHash(), PreventUrlGuessing.class);
		assertPresent(false, AnItem.finger.getHash(), PreventUrlGuessing.class);
		assertPresent(false, AnItem.secfin.getHash(), PreventUrlGuessing.class);
	}

	@Test void testUrlFingerPrintingHash()
	{
		assertPresent(false, AnItem.simple.getHash(), UrlFingerPrinting.class);
		assertPresent(false, AnItem.secret.getHash(), UrlFingerPrinting.class);
		assertPresent(false, AnItem.finger.getHash(), UrlFingerPrinting.class);
		assertPresent(false, AnItem.secfin.getHash(), UrlFingerPrinting.class);
	}

	@Test void testPreventUrlGuessingType()
	{
		assertPresent(false, SecretItem.simple,            PreventUrlGuessing.class);
		assertPresent(false, SecretItem.simple.getMedia(), PreventUrlGuessing.class);
		assertPresent(true,  SecretItem.secret,            PreventUrlGuessing.class);
		assertPresent(true,  SecretItem.secret.getMedia(), PreventUrlGuessing.class);
		assertPresent(false, FingerItem.simple,            PreventUrlGuessing.class);
		assertPresent(false, FingerItem.simple.getMedia(), PreventUrlGuessing.class);
		assertPresent(false, FingerItem.finger,            PreventUrlGuessing.class);
		assertPresent(false, FingerItem.finger.getMedia(), PreventUrlGuessing.class);
		assertPresent(false, SecFinItem.simple,            PreventUrlGuessing.class);
		assertPresent(false, SecFinItem.simple.getMedia(), PreventUrlGuessing.class);
		assertPresent(true,  SecFinItem.secfin,            PreventUrlGuessing.class);
		assertPresent(true,  SecFinItem.secfin.getMedia(), PreventUrlGuessing.class);
	}

	@Test void testUrlFingerPrintingType()
	{
		assertPresent(false, SecretItem.simple,            UrlFingerPrinting.class);
		assertPresent(false, SecretItem.simple.getMedia(), UrlFingerPrinting.class);
		assertPresent(false, SecretItem.secret,            UrlFingerPrinting.class);
		assertPresent(false, SecretItem.secret.getMedia(), UrlFingerPrinting.class);
		assertPresent(false, FingerItem.simple,            UrlFingerPrinting.class);
		assertPresent(false, FingerItem.simple.getMedia(), UrlFingerPrinting.class);
		assertPresent(true,  FingerItem.finger,            UrlFingerPrinting.class);
		assertPresent(true,  FingerItem.finger.getMedia(), UrlFingerPrinting.class);
		assertPresent(false, SecFinItem.simple,            UrlFingerPrinting.class);
		assertPresent(false, SecFinItem.simple.getMedia(), UrlFingerPrinting.class);
		assertPresent(true,  SecFinItem.secfin,            UrlFingerPrinting.class);
		assertPresent(true,  SecFinItem.secfin.getMedia(), UrlFingerPrinting.class);
	}

	@Test void testComputed()
	{
		assertPresent(false, AnItem.simple,            Computed.class);
		assertPresent(true,  AnItem.simple.getMedia(), Computed.class);
		assertPresent(true,  AnItem.simple.getHash(),  Computed.class);
		assertPresent(false, AnItem.secret,            Computed.class);
		assertPresent(true,  AnItem.secret.getMedia(), Computed.class);
		assertPresent(true,  AnItem.secret.getHash(),  Computed.class);
		assertPresent(false, AnItem.finger,            Computed.class);
		assertPresent(true,  AnItem.finger.getMedia(), Computed.class);
		assertPresent(true,  AnItem.finger.getHash(),  Computed.class);
		assertPresent(false, AnItem.secfin,            Computed.class);
		assertPresent(true,  AnItem.secfin.getMedia(), Computed.class);
		assertPresent(true,  AnItem.secfin.getHash(),  Computed.class);
	}

	@Test void testDeprecated()
	{
		assertPresent(false, AnItem.simple,            Deprecated.class);
		assertPresent(false, AnItem.simple.getMedia(), Deprecated.class);
		assertPresent(false, AnItem.secret,            Deprecated.class);
		assertPresent(false, AnItem.secret.getMedia(), Deprecated.class);
		assertPresent(false, AnItem.finger,            Deprecated.class);
		assertPresent(false, AnItem.finger.getMedia(), Deprecated.class);
		assertPresent(false, AnItem.secfin,            Deprecated.class);
		assertPresent(false, AnItem.secfin.getMedia(), Deprecated.class);
	}

	private static void assertPresent(
			final boolean expected,
			final Feature feature,
			final Class<? extends Annotation> annotationClass)
	{
		assertEquals(expected, feature.isAnnotationPresent(annotationClass));
		final Annotation ann = feature.getAnnotation(annotationClass);
		if(expected)
			assertNotNull(ann);
		else
			assertNull(ann);
	}


	@Test void testGetters()
	{
		assertPath(false, false, AnItem.simple);
		assertPath(true,  false, AnItem.secret);
		assertPath(false, true,  AnItem.finger);

		assertPath(true,  false, SecretItem.simple);
		assertPath(true,  false, SecretItem.secret);

		assertPath(false, true,  FingerItem.simple);
		assertPath(false, true,  FingerItem.finger);
	}

	private static void assertPath(
			final boolean expectedSecret,
			final boolean expectedFinger,
			final UniqueHashedMedia filter)
	{
		assertEquals(expectedSecret, filter.getMedia().isUrlGuessingPrevented(), "secret");
		assertEquals(expectedFinger, filter.getMedia().isUrlFingerPrinted(),     "finger");
	}


	@WrapperIgnore
	static final class AnItem extends Item
	{
		static final UniqueHashedMedia simple = new UniqueHashedMedia(new Media());
		@PreventUrlGuessing
		static final UniqueHashedMedia secret = new UniqueHashedMedia(new Media());
		@UrlFingerPrinting
		static final UniqueHashedMedia finger = new UniqueHashedMedia(new Media());
		@PreventUrlGuessing @UrlFingerPrinting
		static final UniqueHashedMedia secfin = new UniqueHashedMedia(new Media());

		@SuppressWarnings("unused") // OK: TYPE without Model
		static final Type<AnItem> TYPE = TypesBound.newType(AnItem.class);
		private static final long serialVersionUID = 1l;
		private AnItem(final ActivationParameters ap) { super(ap); }
	}

	@PreventUrlGuessing
	@WrapperIgnore
	static final class SecretItem extends Item
	{
		static final UniqueHashedMedia simple = new UniqueHashedMedia(new Media());
		@PreventUrlGuessing
		static final UniqueHashedMedia secret = new UniqueHashedMedia(new Media());

		private static final long serialVersionUID = 1l;
		@SuppressWarnings("unused") // OK: TYPE without Model
		static final Type<SecretItem> TYPE = TypesBound.newType(SecretItem.class);
		private SecretItem(final ActivationParameters ap) { super(ap); }
	}

	@UrlFingerPrinting
	@WrapperIgnore
	static final class FingerItem extends Item
	{
		static final UniqueHashedMedia simple = new UniqueHashedMedia(new Media());
		@UrlFingerPrinting
		static final UniqueHashedMedia finger = new UniqueHashedMedia(new Media());

		private static final long serialVersionUID = 1l;
		@SuppressWarnings("unused") // OK: TYPE without Model
		static final Type<FingerItem> TYPE = TypesBound.newType(FingerItem.class);
		private FingerItem(final ActivationParameters ap) { super(ap); }
	}

	@PreventUrlGuessing @UrlFingerPrinting
	@WrapperIgnore
	static final class SecFinItem extends Item
	{
		static final UniqueHashedMedia simple = new UniqueHashedMedia(new Media());
		@PreventUrlGuessing @UrlFingerPrinting
		static final UniqueHashedMedia secfin = new UniqueHashedMedia(new Media());

		private static final long serialVersionUID = 1l;
		@SuppressWarnings("unused") // OK: TYPE without Model
		static final Type<SecFinItem> TYPE = TypesBound.newType(SecFinItem.class);
		private SecFinItem(final ActivationParameters ap) { super(ap); }
	}
}
