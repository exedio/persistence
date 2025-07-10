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

import org.junit.jupiter.api.Test;

public class MediaBase64Test
{
	@Test void testIt()
	{
		assertIt(   "",   0);
		assertIt(  "B",   1);
		assertIt(  "C",   2);
		assertIt(  "9",  61);
		assertIt(  "-",  62);
		assertIt(  "_",  63);
		assertIt( "AB",  64);
		assertIt( "BB",  65);
		assertIt( "CB",  66);
		assertIt( "DB",  67);

		assertIt( ".B",  -1);
		assertIt( ".C",  -2);
		assertIt( ".-", -62);
		assertIt(".DB", -67);

		assertIt("8_________H", Long.MAX_VALUE - 3);
		assertIt("9_________H", Long.MAX_VALUE - 2);
		assertIt("-_________H", Long.MAX_VALUE - 1);
		assertIt("__________H", Long.MAX_VALUE    );

		assertIt(".9_________H", Long.MIN_VALUE + 3);
		assertIt(".-_________H", Long.MIN_VALUE + 2);
		assertIt(".__________H", Long.MIN_VALUE + 1);
	}

	private static void assertIt(final String expected, final long actual)
	{
		final StringBuilder sb = new StringBuilder();
		MediaBase64.append(sb, actual);
		assertEquals(expected, sb.toString());
	}
}
