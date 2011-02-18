/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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

import com.exedio.cope.junit.CopeAssert;
import com.exedio.cope.misc.MediaSummary;

public class MediaSummaryTest extends CopeAssert
{
	public void testIt()
	{
		final MediaInfo i1 = new MediaInfo(null, 11, 21, 31, 41, 51, 61, 71, 81, 91, 101);
		final MediaInfo i2 = new MediaInfo(null, 13, 23, 33, 43, 53, 63, 73, 83, 93, 103);

		final MediaSummary ms = new MediaSummary(new MediaInfo[]{i1, i2});
		assertEquals( 24, ms.getRedirectFrom());
		assertEquals( 44, ms.getException());
		assertEquals( 64, ms.getGuessedUrl());
		assertEquals( 84, ms.getNotAnItem());
		assertEquals(104, ms.getNoSuchItem());
		assertEquals(124, ms.getMoved());
		assertEquals(144, ms.getIsNull());
		assertEquals(164, ms.getNotComputable());
		assertEquals(184, ms.getNotModified());
		assertEquals(204, ms.getDelivered());
	}

	public void testNull()
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

	public void testNullElement()
	{
		final MediaInfo i1 = new MediaInfo(null, 11, 21, 31, 41, 51, 61, 71, 81, 91, 101);
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

	public void testEmpty()
	{
		final MediaSummary ms = new MediaSummary(new MediaInfo[]{});
		assertEquals(0, ms.getRedirectFrom());
		assertEquals(0, ms.getException());
		assertEquals(0, ms.getGuessedUrl());
		assertEquals(0, ms.getNotAnItem());
		assertEquals(0, ms.getNoSuchItem());
		assertEquals(0, ms.getMoved());
		assertEquals(0, ms.getIsNull());
		assertEquals(0, ms.getNotComputable());
		assertEquals(0, ms.getNotModified());
		assertEquals(0, ms.getDelivered());
	}
}
