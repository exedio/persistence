/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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
import static org.junit.Assert.assertTrue;

public final class EqualsAssert
{
	public static void assertEqualsAndHash(final Function<?> f1, final Function<?> f2)
	{
		assertEquals(f1, f2);
		assertEquals(f2, f1);
		if(f1!=null)
			assertEquals(f1.hashCode(), f2.hashCode());
	}

	public static void assertNotEqualsAndHash(final Function<?> f1, final Function<?> f2)
	{
		assertTrue(!f1.equals(f2));
		assertTrue(!f2.equals(f1));
		assertTrue(f1.hashCode()!=f2.hashCode());
	}

	public static void assertEqualsAndHash(final Condition c1, final Condition c2)
	{
		assertEquals(c1, c2);
		assertEquals(c2, c1);
		if(c1!=null)
			assertEquals(c1.hashCode(), c2.hashCode());
	}

	public static void assertNotEqualsAndHash(final Condition c1, final Condition c2)
	{
		assertTrue(!c1.equals(c2));
		assertTrue(!c2.equals(c1));
		assertTrue(c1.hashCode()!=c2.hashCode());
	}

	private EqualsAssert()
	{
		// prevent instantiation
	}
}
