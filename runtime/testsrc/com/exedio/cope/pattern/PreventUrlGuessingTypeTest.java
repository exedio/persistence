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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.exedio.cope.Item;
import com.exedio.cope.instrument.WrapperIgnore;
import com.exedio.cope.instrument.WrapperType;
import java.lang.annotation.Annotation;
import org.junit.jupiter.api.Test;

public class PreventUrlGuessingTypeTest
{
	@Test void testIt()
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
		assertEquals(expectedValue, feature.isUrlGuessingPrevented(), msg);
		assertEquals(expectedAnn, feature.isAnnotationPresent(PreventUrlGuessing.class), msg);
		final Annotation ann = feature.getAnnotation(PreventUrlGuessing.class);
		if(expectedAnn)
			assertNotNull(ann, msg);
		else
			assertNull(ann, msg);
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	static final class AbsentItem extends Item
	{
		@WrapperIgnore static final Media absent = new Media();
		@PreventUrlGuessing
		@WrapperIgnore static final Media present = new Media();

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		static final com.exedio.cope.Type<AbsentItem> TYPE = com.exedio.cope.TypesBound.newType(AbsentItem.class);

		@com.exedio.cope.instrument.Generated
		private AbsentItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@PreventUrlGuessing
	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	static final class PresentItem extends Item
	{
		@WrapperIgnore static final Media absent = new Media();
		@PreventUrlGuessing
		@WrapperIgnore static final Media present = new Media();

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		static final com.exedio.cope.Type<PresentItem> TYPE = com.exedio.cope.TypesBound.newType(PresentItem.class);

		@com.exedio.cope.instrument.Generated
		private PresentItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}
