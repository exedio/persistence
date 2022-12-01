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

package com.exedio.cope.misc;

import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static com.exedio.cope.tojunit.Assert.list;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import org.junit.jupiter.api.Test;

public class ListUtilTest
{
	@SuppressWarnings("deprecation") // OK: testing deprecated API
	@Test void testIt()
	{
		assertEqualsUnmodifiable(list(), ListUtil.trimUnmodifiable(l()));
		assertEqualsUnmodifiable(list("hallo"), ListUtil.trimUnmodifiable(l("hallo")));
		assertEqualsUnmodifiable(list(new Object[]{null}), ListUtil.trimUnmodifiable(l(new String[]{null})));
		assertEqualsUnmodifiable(list("hallo", "bello"), ListUtil.trimUnmodifiable(l("hallo", "bello")));
		assertEqualsUnmodifiable(list("hallo", null), ListUtil.trimUnmodifiable(l("hallo", null)));

		try
		{
			ListUtil.trimUnmodifiable(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals(null, e.getMessage());
		}
	}

	private static ArrayList<String> l(final String... strings)
	{
		return new ArrayList<>(java.util.Arrays.asList(strings));
	}
}
