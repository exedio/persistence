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

package com.exedio.cope;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.exedio.cope.misc.Arrays;
import org.junit.Test;

public class ArraysTest
{
	@Test public void testIt()
	{
		try
		{
			Arrays.toString(null, 0);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("limit must be greater that zero, but was 0", e.getMessage());
		}
		try
		{
			Arrays.append(null, null, 0);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("limit must be greater that zero, but was 0", e.getMessage());
		}
		assertIt("null", null, 1);
		assertIt("[]", new byte[]{}, 1);
		assertIt("[20]",         new byte[]{20}, 1);
		assertIt("[20]",         new byte[]{20}, 2);
		assertIt("[20, 21]",     new byte[]{20, 21}, 2);
		assertIt("[20, 21]",     new byte[]{20, 21}, 3);
		assertIt("[20, 21, 22]", new byte[]{20, 21, 22}, 3);
		assertIt("[20, 21 ... (3)]", new byte[]{20, 21, 22}, 2);
		assertIt("[20 ... (3)]", new byte[]{20, 21, 22}, 1);
	}

	private static void assertIt(final String expected, final byte[] a, final int limit)
	{
		assertEquals(expected, Arrays.toString(a, limit));
		final StringBuilder bf = new StringBuilder();
		Arrays.append(bf, a, limit);
		assertEquals(expected, bf.toString());
	}
}
