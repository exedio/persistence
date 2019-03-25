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

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.exedio.cope.ActivationParameters;
import com.exedio.cope.Feature;
import com.exedio.cope.Item;
import com.exedio.cope.StringField;
import com.exedio.cope.Type;
import com.exedio.cope.TypesBound;
import com.exedio.cope.instrument.WrapperIgnore;
import com.exedio.cope.misc.Computed;
import java.lang.annotation.Annotation;
import org.junit.jupiter.api.Test;

public class TextUrlFilterAnnotationTest
{
	@Test void testIt()
	{
		final Media simpleVal = pasteValue(AnItem.simple);
		final Media secretVal = pasteValue(AnItem.secret);
		final Media fingerVal = pasteValue(AnItem.finger);
		final Media secfinVal = pasteValue(AnItem.secfin);
		final Media simpleRaw = AnItem.simple.getSource();
		final Media secretRaw = AnItem.secret.getSource();
		final Media fingerRaw = AnItem.finger.getSource();
		final Media secfinRaw = AnItem.secfin.getSource();

		assertPresent(false, simpleVal, PreventUrlGuessing.class);
		assertPresent(false, simpleRaw, PreventUrlGuessing.class);
		assertPresent(true,  secretVal, PreventUrlGuessing.class);
		assertPresent(true,  secretRaw, PreventUrlGuessing.class);
		assertPresent(false, fingerVal, PreventUrlGuessing.class);
		assertPresent(false, fingerRaw, PreventUrlGuessing.class);
		assertPresent(true,  secfinVal, PreventUrlGuessing.class);
		assertPresent(true,  secfinRaw, PreventUrlGuessing.class);

		assertPresent(false, simpleVal, UrlFingerPrinting.class);
		assertPresent(false, simpleRaw, UrlFingerPrinting.class);
		assertPresent(false, secretVal, UrlFingerPrinting.class);
		assertPresent(false, secretRaw, UrlFingerPrinting.class);
		assertPresent(true,  fingerVal, UrlFingerPrinting.class);
		assertPresent(true,  fingerRaw, UrlFingerPrinting.class);
		assertPresent(true,  secfinVal, UrlFingerPrinting.class);
		assertPresent(true,  secfinRaw, UrlFingerPrinting.class);

		assertPresent(false, simpleVal, Computed.class);
		assertPresent(false, simpleRaw, Computed.class);
		assertPresent(false, secretVal, Computed.class);
		assertPresent(false, secretRaw, Computed.class);
		assertPresent(false, fingerVal, Computed.class);
		assertPresent(false, fingerRaw, Computed.class);
		assertPresent(false, secfinVal, Computed.class);
		assertPresent(false, secfinRaw, Computed.class);

		assertPresent(false, simpleVal, Deprecated.class);
		assertPresent(false, simpleRaw, Deprecated.class);
		assertPresent(false, secretVal, Deprecated.class);
		assertPresent(false, secretRaw, Deprecated.class);
		assertPresent(false, fingerVal, Deprecated.class);
		assertPresent(false, fingerRaw, Deprecated.class);
		assertPresent(false, secfinVal, Deprecated.class);
		assertPresent(false, secfinRaw, Deprecated.class);
	}

	@Test void testKey()
	{
		final StringField simpleKey = pasteKey(AnItem.simple);
		final StringField secretKey = pasteKey(AnItem.secret);
		final StringField fingerKey = pasteKey(AnItem.finger);
		final StringField secfinKey = pasteKey(AnItem.secfin);

		assertPresent(false, simpleKey, PreventUrlGuessing.class);
		assertPresent(false, secretKey, PreventUrlGuessing.class);
		assertPresent(false, fingerKey, PreventUrlGuessing.class);
		assertPresent(false, secfinKey, PreventUrlGuessing.class);

		assertPresent(false, simpleKey, UrlFingerPrinting.class);
		assertPresent(false, secretKey, UrlFingerPrinting.class);
		assertPresent(false, fingerKey, UrlFingerPrinting.class);
		assertPresent(false, secfinKey, UrlFingerPrinting.class);

		assertPresent(false, simpleKey, Computed.class);
		assertPresent(false, secretKey, Computed.class);
		assertPresent(false, fingerKey, Computed.class);
		assertPresent(false, secfinKey, Computed.class);

		assertPresent(false, simpleKey, Deprecated.class);
		assertPresent(false, secretKey, Deprecated.class);
		assertPresent(false, fingerKey, Deprecated.class);
		assertPresent(false, secfinKey, Deprecated.class);
	}

	private static void assertPresent(
			final boolean expected,
			final Feature feature,
			final Class<? extends Annotation> annotationClass)
	{
		final String msg = feature.toString();
		assertEquals(expected, feature.isAnnotationPresent(annotationClass), msg);
		final Annotation ann = feature.getAnnotation(annotationClass);
		if(expected)
			assertNotNull(ann, msg);
		else
			assertNull(ann, msg);
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
			final TextUrlFilter filter)
	{
		assertEquals(expectedSecret, filter.isUrlGuessingPrevented(),             "secret");
		assertEquals(expectedFinger, filter.isUrlFingerPrinted(),                 "finger");
		assertEquals(expectedSecret, pasteValue(filter).isUrlGuessingPrevented(), "secret");
		assertEquals(expectedFinger, pasteValue(filter).isUrlFingerPrinted(),     "finger");
		assertEquals(expectedSecret, filter.getSource().isUrlGuessingPrevented(), "secret");
		assertEquals(expectedFinger, filter.getSource().isUrlFingerPrinted(),     "finger");
	}


	@WrapperIgnore
	static final class AnItem extends Item
	{
		static final TextUrlFilter simple = new ATextUrlFilter(new Media(), new Media());
		@PreventUrlGuessing
		static final TextUrlFilter secret = new ATextUrlFilter(new Media(), new Media());
		@UrlFingerPrinting
		static final TextUrlFilter finger = new ATextUrlFilter(new Media(), new Media());
		@PreventUrlGuessing @UrlFingerPrinting
		static final TextUrlFilter secfin = new ATextUrlFilter(new Media(), new Media());

		private static final long serialVersionUID = 1l;
		@SuppressWarnings("unused") // OK: TYPE without Model
		static final Type<AnItem> TYPE = TypesBound.newType(AnItem.class);
		private AnItem(final ActivationParameters ap) { super(ap); }
	}

	@PreventUrlGuessing
	@WrapperIgnore
	static final class SecretItem extends Item
	{
		static final TextUrlFilter simple = new ATextUrlFilter(new Media(), new Media());
		@PreventUrlGuessing
		static final TextUrlFilter secret = new ATextUrlFilter(new Media(), new Media());

		private static final long serialVersionUID = 1l;
		@SuppressWarnings("unused") // OK: TYPE without Model
		static final Type<SecretItem> TYPE = TypesBound.newType(SecretItem.class);
		private SecretItem(final ActivationParameters ap) { super(ap); }
	}

	@UrlFingerPrinting
	@WrapperIgnore
	static final class FingerItem extends Item
	{
		static final TextUrlFilter simple = new ATextUrlFilter(new Media(), new Media());
		@UrlFingerPrinting
		static final TextUrlFilter finger = new ATextUrlFilter(new Media(), new Media());

		private static final long serialVersionUID = 1l;
		@SuppressWarnings("unused") // OK: TYPE without Model
		static final Type<FingerItem> TYPE = TypesBound.newType(FingerItem.class);
		private FingerItem(final ActivationParameters ap) { super(ap); }
	}

	static final class ATextUrlFilter extends TextUrlFilter
	{
		private static final long serialVersionUID = 1l;

		ATextUrlFilter(final Media raw, final Media pasteValue)
		{
			super(raw, "text/plain", UTF_8, "<paste>", "</paste>", new StringField(), pasteValue);
		}
	}

	private static Media pasteValue(final TextUrlFilter filter)
	{
		final Type<?> type = filter.getSourceTypes().get(0);
		assertNotNull(type);
		final Media value = (Media)type.getFeature("value");
		assertNotNull(value);
		return value;
	}

	private static StringField pasteKey(final TextUrlFilter filter)
	{
		final Type<?> type = filter.getSourceTypes().get(0);
		assertNotNull(type);
		final StringField key = (StringField)type.getFeature("key");
		assertNotNull(key);
		return key;
	}
}
