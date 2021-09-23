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
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.misc.MediaSummary;
import org.junit.jupiter.api.Test;

public class MediaSummaryTest
{
	@Test void testIt()
	{
		final MediaInfo i1 = new MediaInfo(null, 11, 21, 111, 31, 41, 51, 61, 71, 91, 101);
		final MediaInfo i2 = new MediaInfo(null, 13, 23, 113, 33, 43, 53, 63, 73, 93, 103);

		final MediaSummary ms = new MediaSummary(new MediaInfo[]{i1, i2});
		assertEquals( 24, ms.getRedirectFrom());
		assertEquals( 44, ms.getException());
		assertEquals(224, ms.getInvalidSpecial());
		assertEquals( 64, ms.getGuessedUrl());
		assertEquals( 84, ms.getNotAnItem());
		assertEquals(104, ms.getNoSuchItem());
		assertEquals(124, ms.getMoved());
		assertEquals(144, ms.getIsNull());
		assertEquals(184, ms.getNotModified());
		assertEquals(204, ms.getDelivered());
	}

	@Test void testNull()
	{
		try
		{
			new MediaSummary(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals(null, e.getMessage());
		}
	}

	@Test void testNullElement()
	{
		final MediaInfo i1 = new MediaInfo(null, 11, 21, 31, 41, 51, 61, 71, 91, 101, 111);
		try
		{
			new MediaSummary(new MediaInfo[]{i1, null});
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals(null, e.getMessage());
		}
	}

	@Test void testEmpty()
	{
		final MediaSummary ms = new MediaSummary(new MediaInfo[]{});
		assertEquals(0, ms.getRedirectFrom());
		assertEquals(0, ms.getException());
		assertEquals(0, ms.getInvalidSpecial());
		assertEquals(0, ms.getGuessedUrl());
		assertEquals(0, ms.getNotAnItem());
		assertEquals(0, ms.getNoSuchItem());
		assertEquals(0, ms.getMoved());
		assertEquals(0, ms.getIsNull());
		assertEquals(0, ms.getNotModified());
		assertEquals(0, ms.getDelivered());
	}
}
