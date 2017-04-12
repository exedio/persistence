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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.exedio.cope.ActivationParameters;
import com.exedio.cope.Feature;
import com.exedio.cope.Item;
import com.exedio.cope.StringField;
import com.exedio.cope.Type;
import com.exedio.cope.TypesBound;
import com.exedio.cope.instrument.WrapperIgnore;
import com.exedio.cope.misc.Computed;
import java.lang.annotation.Annotation;
import org.junit.Test;

public class TextUrlFilterDelegatorAnnotationTest
{
	@Test public void testIt()
	{
		final Media simpleVal = pasteValue(AnItem.simple);
		final Media secretVal = pasteValue(AnItem.secret);
		final Media fingerVal = pasteValue(AnItem.finger);
		final Media simpleRaw = AnItem.simple.getSource();
		final Media secretRaw = AnItem.secret.getSource();
		final Media fingerRaw = AnItem.finger.getSource();

		assertPresent(false, simpleVal, PreventUrlGuessing.class);
		assertPresent(false, simpleRaw, PreventUrlGuessing.class);
		assertPresent(false, secretVal, PreventUrlGuessing.class);
		assertPresent(true,  secretRaw, PreventUrlGuessing.class);
		assertPresent(false, fingerVal, PreventUrlGuessing.class);
		assertPresent(false, fingerRaw, PreventUrlGuessing.class);

		assertPresent(false, simpleVal, UrlFingerPrinting.class);
		assertPresent(false, simpleRaw, UrlFingerPrinting.class);
		assertPresent(false, secretVal, UrlFingerPrinting.class);
		assertPresent(false, secretRaw, UrlFingerPrinting.class);
		assertPresent(false, fingerVal, UrlFingerPrinting.class);
		assertPresent(true,  fingerRaw, UrlFingerPrinting.class);

		assertPresent(false, simpleVal, Computed.class);
		assertPresent(false, simpleRaw, Computed.class);
		assertPresent(false, secretVal, Computed.class);
		assertPresent(false, secretRaw, Computed.class);
		assertPresent(false, fingerVal, Computed.class);
		assertPresent(false, fingerRaw, Computed.class);

		assertPresent(false, simpleVal, Deprecated.class);
		assertPresent(false, simpleRaw, Deprecated.class);
		assertPresent(false, secretVal, Deprecated.class);
		assertPresent(false, secretRaw, Deprecated.class);
		assertPresent(false, fingerVal, Deprecated.class);
		assertPresent(false, fingerRaw, Deprecated.class);
	}

	private static void assertPresent(
			final boolean expected,
			final Feature feature,
			final Class<? extends Annotation> annotationClass)
	{
		final String msg = feature.toString();
		assertEquals(msg, expected, feature.isAnnotationPresent(annotationClass));
		final Annotation ann = feature.getAnnotation(annotationClass);
		if(expected)
			assertNotNull(msg, ann);
		else
			assertNull(msg, ann);
	}


	@Test public void testGetters()
	{
		assertPath(false, false, AnItem.simple);
		assertPath(true,  false, AnItem.secret);
		assertPath(false, true,  AnItem.finger);
	}

	private static void assertPath(
			final boolean expectedSecret,
			final boolean expectedFinger,
			final TextUrlFilterDelegator filter)
	{
		assertEquals("secret", expectedSecret, filter.isUrlGuessingPrevented());
		assertEquals("finger", expectedFinger, filter.isUrlFingerPrinted());
		assertEquals("secret", false, pasteValue(filter).isUrlGuessingPrevented());
		assertEquals("finger", false, pasteValue(filter).isUrlFingerPrinted());
		assertEquals("secret", expectedSecret, filter.getSource().isUrlGuessingPrevented());
		assertEquals("finger", expectedFinger, filter.getSource().isUrlFingerPrinted());
	}


	@WrapperIgnore
	static final class AnItem extends Item
	{
		static final TextUrlFilter delegate = new TextUrlFilter(new Media(), "text/plain", UTF_8, "<paste>", "</paste>", new StringField(), new Media());

		static final TextUrlFilterDelegator simple = new ATextUrlFilterDelegator(new Media(), delegate);
		@PreventUrlGuessing
		static final TextUrlFilterDelegator secret = new ATextUrlFilterDelegator(new Media(), delegate);
		@UrlFingerPrinting
		static final TextUrlFilterDelegator finger = new ATextUrlFilterDelegator(new Media(), delegate);

		private static final long serialVersionUID = 1l;
		static final Type<AnItem> TYPE = TypesBound.newType(AnItem.class);
		private AnItem(final ActivationParameters ap) { super(ap); }
	}

	static final class ATextUrlFilterDelegator extends TextUrlFilterDelegator
	{
		private static final long serialVersionUID = 1l;

		ATextUrlFilterDelegator(final Media raw, final TextUrlFilter delegate)
		{
			super(raw, delegate, "text/plain", UTF_8, "<paste>", "</paste>");
		}
	}

	private static Media pasteValue(final TextUrlFilterDelegator filter)
	{
		final Type<?> type = filter.delegate.getSourceTypes().get(0);
		assertNotNull(type);
		final Media value = (Media)type.getFeature("value");
		assertNotNull(value);
		return value;
	}
}
