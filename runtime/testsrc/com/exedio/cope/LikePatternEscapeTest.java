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

import static com.exedio.cope.HsqldbDialect.maskLikePatternInternal;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

public class LikePatternEscapeTest
{
	@Test void testEmpty()
	{
		assertIt("");
	}
	@Test void testNormal()
	{
		assertIt("beforeAfter");
	}
	@Test void testSpecial()
	{
		assertIt("before%After");
		assertIt("before%");
		assertIt(      "%After");

		assertIt("before_After");
		assertIt("before_");
		assertIt(      "_After");
	}
	@Test void testSpecialEscaped()
	{
		assertIt("before\\%After");
		assertIt("before\\%");
		assertIt(      "\\%After");

		assertIt("before\\_After");
		assertIt("before\\_");
		assertIt(      "\\_After");

		assertIt("before\\\\After");
		assertIt("before\\\\");
		assertIt(      "\\\\After");
	}
	@Test void testSpecialInvalid()
	{
		assertIt("before\\After", "beforeAfter");
		assertIt("before\\"     , "before"     );
		assertIt(      "\\After",       "After");
	}

	private static void assertIt(final String pattern)
	{
		assertSame(pattern, maskLikePatternInternal(pattern));
	}

	private static void assertIt(final String pattern, final String result)
	{
		assertNotEquals(pattern, result, "USE assertIt(String) INSTEAD");
		assertEquals(result, maskLikePatternInternal(pattern));
	}
}
