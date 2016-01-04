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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.exedio.cope.ActivationParameters;
import com.exedio.cope.Feature;
import com.exedio.cope.Item;
import com.exedio.cope.Type;
import com.exedio.cope.TypesBound;
import com.exedio.cope.misc.Computed;
import java.lang.annotation.Annotation;
import org.junit.Test;

public class UniqueHashedMediaAnnotationTest
{
	@Test public void testPreventUrlGuessing()
	{
		assertPresent(false, AnItem.simple,            PreventUrlGuessing.class);
		assertPresent(false, AnItem.simple.getMedia(), PreventUrlGuessing.class);
		assertPresent(true,  AnItem.secret,            PreventUrlGuessing.class);
		assertPresent(true,  AnItem.secret.getMedia(), PreventUrlGuessing.class);
		assertPresent(false, AnItem.finger,            PreventUrlGuessing.class);
		assertPresent(false, AnItem.finger.getMedia(), PreventUrlGuessing.class);
	}

	@Test public void testUrlFingerPrinting()
	{
		assertPresent(false, AnItem.simple,            UrlFingerPrinting.class);
		assertPresent(false, AnItem.simple.getMedia(), UrlFingerPrinting.class);
		assertPresent(false, AnItem.secret,            UrlFingerPrinting.class);
		assertPresent(false, AnItem.secret.getMedia(), UrlFingerPrinting.class);
		assertPresent(true,  AnItem.finger,            UrlFingerPrinting.class);
		assertPresent(true,  AnItem.finger.getMedia(), UrlFingerPrinting.class);
	}

	@Test public void testComputed()
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
	}

	@Test public void testDeprecated()
	{
		assertPresent(false, AnItem.simple,            Deprecated.class);
		assertPresent(false, AnItem.simple.getMedia(), Deprecated.class);
		assertPresent(false, AnItem.secret,            Deprecated.class);
		assertPresent(false, AnItem.secret.getMedia(), Deprecated.class);
		assertPresent(false, AnItem.finger,            Deprecated.class);
		assertPresent(false, AnItem.finger.getMedia(), Deprecated.class);
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

	static class AnItem extends Item
	{
		static final UniqueHashedMedia simple = new UniqueHashedMedia(new Media());
		@PreventUrlGuessing
		static final UniqueHashedMedia secret = new UniqueHashedMedia(new Media());
		@UrlFingerPrinting
		static final UniqueHashedMedia finger = new UniqueHashedMedia(new Media());

		static final Type<AnItem> TYPE = TypesBound.newType(AnItem.class);
		private static final long serialVersionUID = 1l;
		private AnItem(final ActivationParameters ap) { super(ap); }
	}
}
