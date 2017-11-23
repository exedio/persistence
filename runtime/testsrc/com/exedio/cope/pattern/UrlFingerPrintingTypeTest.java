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
import com.exedio.cope.Item;
import com.exedio.cope.Type;
import com.exedio.cope.TypesBound;
import com.exedio.cope.instrument.WrapperIgnore;
import java.lang.annotation.Annotation;
import org.junit.jupiter.api.Test;

public class UrlFingerPrintingTypeTest
{
	@Test public void testIt()
	{
		assertPresent(false, false, AbsentItem .absent );
		assertPresent(true,  true,  AbsentItem .present);
		assertPresent(true,  false, PresentItem.absent );
		assertPresent(true,  true,  PresentItem.present);
	}

	private static void assertPresent(
			final boolean expectedValue,
			final boolean expectedAnn,
			final Media feature)
	{
		final String msg = feature.toString();
		assertEquals(msg, expectedValue, feature.isUrlFingerPrinted());
		assertEquals(msg, expectedAnn, feature.isAnnotationPresent(UrlFingerPrinting.class));
		final Annotation ann = feature.getAnnotation(UrlFingerPrinting.class);
		if(expectedAnn)
			assertNotNull(msg, ann);
		else
			assertNull(msg, ann);
	}

	@WrapperIgnore
	static final class AbsentItem extends Item
	{
		static final Media absent = new Media();
		@UrlFingerPrinting
		static final Media present = new Media();

		private static final long serialVersionUID = 1l;
		static final Type<AbsentItem> TYPE = TypesBound.newType(AbsentItem.class);
		private AbsentItem(final ActivationParameters ap) { super(ap); }
	}

	@WrapperIgnore
	@UrlFingerPrinting
	static final class PresentItem extends Item
	{
		static final Media absent = new Media();
		@UrlFingerPrinting
		static final Media present = new Media();

		private static final long serialVersionUID = 1l;
		static final Type<PresentItem> TYPE = TypesBound.newType(PresentItem.class);
		private PresentItem(final ActivationParameters ap) { super(ap); }
	}
}
