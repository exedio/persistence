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

import com.exedio.cope.Item;
import com.exedio.cope.instrument.WrapperIgnore;
import com.exedio.cope.instrument.WrapperType;
import org.junit.jupiter.api.Test;

public class MediaFilterFinalTest
{
	@Test void testIt()
	{
		assertEquals(false, AnItem.sourceNonFinal.isFinal());
		assertEquals(true , AnItem.sourceFinal   .isFinal());
		assertEquals(false, AnItem.filterNonFinal.isFinal());
		assertEquals(true , AnItem.filterFinal   .isFinal());
	}

	@WrapperType(type=NONE, constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static final class AnItem extends Item
	{
		@WrapperIgnore static final Media sourceNonFinal = new Media();
		@WrapperIgnore static final Media sourceFinal    = new Media().toFinal();

		@WrapperIgnore static final MediaThumbnail filterNonFinal = new MediaThumbnail(sourceNonFinal, 10, 20);
		@WrapperIgnore static final MediaThumbnail filterFinal    = new MediaThumbnail(sourceFinal   , 10, 20);

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private AnItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}
