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

import static com.exedio.cope.ICU.getRegularExpression;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.util.CharSet;
import org.junit.jupiter.api.Test;

public class ICUTest
{
	@Test void testRanges()
	{
		assertIt("^[A-Z]*$", new CharSet('A', 'Z'));
		assertIt("^[0-9A-Z]*$", new CharSet('0', '9', 'A', 'Z'));
	}
	@Test void testSingle()
	{
		assertIt("^[AE]*$", new CharSet('A', 'A', 'E', 'E'));
	}
	@Test void testRangeSingleMixed()
	{
		assertIt("^[0-9A]*$", new CharSet('0', '9', 'A', 'A'));
	}
	@Test void testNonHex()
	{
		assertIt("^[ -!]*$", new CharSet(' ', '!'));
		assertIt("^[z-~]*$", new CharSet('z', (char)126));
	}
	@Test void testHex()
	{
		//noinspection HardcodedLineSeparator OK: testing line separator characters
		assertIt("^[\\u000a-\\u000d]*$", new CharSet('\n', '\r'));
		assertIt("^[\\u00c4-\\u00d6]*$", new CharSet('\u00c4', '\u00d6'));
		assertIt("^[\\u001f-\\u007f]*$", new CharSet((char)31, (char)127));
	}
	@Test void testQuoted()
	{
		assertIt("^[\\&-\\-\\[-\\]]*$", new CharSet('&', '-', '[', ']'));
		assertIt("^[\\\\]*$", new CharSet('\\', '\\'));
	}

	private static void assertIt(final String expected, final CharSet actual)
	{
		assertEquals(expected, getRegularExpression(actual));
	}
}
