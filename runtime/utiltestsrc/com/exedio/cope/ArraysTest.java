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

package com.exedio.cope;

import junit.framework.TestCase;

import com.exedio.cope.misc.Arrays;

public class ArraysTest extends TestCase
{
	public void testIt()
	{
		try
		{
			str(null, 0);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("limit must be greater that zero, but was 0", e.getMessage());
		}
		assertEquals("null", str(null, 1));
		assertEquals("[]", str(new byte[]{}, 1));
		assertEquals("[20]",         str(new byte[]{20}, 1));
		assertEquals("[20, 21]",     str(new byte[]{20, 21}, 2));
		assertEquals("[20, 21, 22]", str(new byte[]{20, 21, 22}, 3));
		assertEquals("[20, 21, ... (3)]", str(new byte[]{20, 21, 22}, 2));
		assertEquals("[20, ... (3)]", str(new byte[]{20, 21, 22}, 1));
	}

	private static final String str(final byte[] a, final int limit)
	{
		return Arrays.toString(a, limit);
	}
}
