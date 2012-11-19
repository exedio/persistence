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

package com.exedio.cope.misc;

import static com.exedio.cope.misc.ListUtil.trimUnmodifiable;

import java.util.ArrayList;

import com.exedio.cope.junit.CopeAssert;

public class ListUtilTest extends CopeAssert
{
	public void testIt()
	{
		assertEqualsUnmodifiable(list(), trimUnmodifiable(l()));
		assertEqualsUnmodifiable(list("hallo"), trimUnmodifiable(l("hallo")));
		assertEqualsUnmodifiable(list("hallo", "bello"), trimUnmodifiable(l("hallo", "bello")));

		try
		{
			trimUnmodifiable(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals(null, e.getMessage());
		}
	}

	private static final ArrayList<String> l(final String... strings)
	{
		return new ArrayList<String>(java.util.Arrays.asList(strings));
	}
}
