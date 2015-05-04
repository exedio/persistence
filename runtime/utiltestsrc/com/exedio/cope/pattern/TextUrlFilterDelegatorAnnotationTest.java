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

import static com.exedio.cope.TypesBound.newType;
import static java.nio.charset.StandardCharsets.UTF_8;

import com.exedio.cope.ActivationParameters;
import com.exedio.cope.Feature;
import com.exedio.cope.Item;
import com.exedio.cope.StringField;
import com.exedio.cope.Type;
import com.exedio.cope.junit.CopeAssert;
import java.lang.annotation.Annotation;

public class TextUrlFilterDelegatorAnnotationTest extends CopeAssert
{
	public void testIt()
	{
		newType(AnItem.class);

		assertPresent(false, pasteValue(AnItem.simple), PreventUrlGuessing.class);
		assertPresent(false, AnItem.simple.getSource(), PreventUrlGuessing.class);
		assertPresent(false, pasteValue(AnItem.secret), PreventUrlGuessing.class);
		assertPresent(true,  AnItem.secret.getSource(), PreventUrlGuessing.class);
		assertPresent(false, pasteValue(AnItem.finger), PreventUrlGuessing.class);
		assertPresent(false, AnItem.finger.getSource(), PreventUrlGuessing.class);

		assertPresent(false, pasteValue(AnItem.simple), UrlFingerPrinting.class);
		assertPresent(false, AnItem.simple.getSource(), UrlFingerPrinting.class);
		assertPresent(false, pasteValue(AnItem.secret), UrlFingerPrinting.class);
		assertPresent(false, AnItem.secret.getSource(), UrlFingerPrinting.class);
		assertPresent(false, pasteValue(AnItem.finger), UrlFingerPrinting.class);
		assertPresent(true,  AnItem.finger.getSource(), UrlFingerPrinting.class);

		assertPresent(false, pasteValue(AnItem.simple), Deprecated.class);
		assertPresent(false, AnItem.simple.getSource(), Deprecated.class);
		assertPresent(false, pasteValue(AnItem.secret), Deprecated.class);
		assertPresent(false, AnItem.secret.getSource(), Deprecated.class);
		assertPresent(false, pasteValue(AnItem.finger), Deprecated.class);
		assertPresent(false, AnItem.finger.getSource(), Deprecated.class);
	}

	private static final void assertPresent(
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

	static final class AnItem extends Item
	{
		static final TextUrlFilter delegate = new TextUrlFilter(new Media(), "text/plain", UTF_8, "<paste>", "</paste>", new StringField(), new Media());

		static final TextUrlFilterDelegator simple = new ATextUrlFilterDelegator(new Media(), delegate);
		@PreventUrlGuessing
		static final TextUrlFilterDelegator secret = new ATextUrlFilterDelegator(new Media(), delegate);
		@UrlFingerPrinting
		static final TextUrlFilterDelegator finger = new ATextUrlFilterDelegator(new Media(), delegate);

		private static final long serialVersionUID = 1l;
		private AnItem(final ActivationParameters ap) { super(ap); }
	}

	static final class ATextUrlFilterDelegator extends TextUrlFilterDelegator
	{
		private static final long serialVersionUID = 1l;

		public ATextUrlFilterDelegator(final Media raw, final TextUrlFilter delegate)
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
