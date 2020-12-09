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

import static com.exedio.cope.pattern.Media.DEFAULT_LENGTH;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class MediaSmallBodyTest
{
	@Test void testVerySmall()
	{
		final Media m = new Media().lengthMax(50);
		assertEquals(50, m.getMaximumLength());
		assertEquals(true, m.isBodySmall);
	}
	@Test void testDefault()
	{
		final Media m = new Media();
		assertEquals(DEFAULT_LENGTH, m.getMaximumLength());
		assertEquals(true, m.isBodySmall);
	}
	// BEWARE:
	// If this threshold changes, MediaServletItem#contentLarge must be adapted as well.
	@Test void testLarge()
	{
		final Media m = new Media().lengthMax(DEFAULT_LENGTH+1);
		assertEquals(DEFAULT_LENGTH+1, m.getMaximumLength());
		assertEquals(false, m.isBodySmall);
	}
	@Test void testVeryLarge()
	{
		final Media m = new Media().lengthMax(50*1000*1000*1000l);
		assertEquals(50*1000*1000*1000l, m.getMaximumLength());
		assertEquals(false, m.isBodySmall);
	}
}
