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

import com.exedio.cope.ActivationParameters;
import com.exedio.cope.Item;
import org.junit.Test;

public class MediaFilterFinalTest
{
	@Test public void testIt()
	{
		assertEquals(false, AnItem.sourceNonFinal.isFinal());
		assertEquals(true , AnItem.sourceFinal   .isFinal());
		assertEquals(false, AnItem.filterNonFinal.isFinal());
		assertEquals(true , AnItem.filterFinal   .isFinal());
	}

	private static final class AnItem extends Item
	{
		static final Media sourceNonFinal = new Media();
		static final Media sourceFinal    = new Media().toFinal();

		static final MediaThumbnail filterNonFinal = new MediaThumbnail(sourceNonFinal, 10, 20);
		static final MediaThumbnail filterFinal    = new MediaThumbnail(sourceFinal   , 10, 20);

		private static final long serialVersionUID = 1l;
		private AnItem(final ActivationParameters ap){super(ap);}
	}
}
