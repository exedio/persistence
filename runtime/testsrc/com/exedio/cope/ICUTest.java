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
		assertIt("\\A[A-Z]*\\z", new CharSet('A', 'Z'));
		assertIt("\\A[0-9A-Z]*\\z", new CharSet('0', '9', 'A', 'Z'));
	}
	@Test void testSingle()
	{
		assertIt("\\A[AE]*\\z", new CharSet('A', 'A', 'E', 'E'));
	}
	@Test void testRangeSingleMixed()
	{
		assertIt("\\A[0-9A]*\\z", new CharSet('0', '9', 'A', 'A'));
	}
	@Test void testNonHex()
	{
		assertIt("\\A[ -!]*\\z", new CharSet(' ', '!'));
		assertIt("\\A[z-~]*\\z", new CharSet('z', (char)126));
	}
	@Test void testHex()
	{
		assertIt("\\A[\\x00-\\x01]*\\z", new CharSet('\0', (char)1));
		//noinspection HardcodedLineSeparator OK: testing line separator characters
		assertIt("\\A[\\x0a-\\x0d]*\\z", new CharSet('\n', '\r'));
		assertIt("\\A[\\xc4-\\xd6]*\\z", new CharSet('\u00c4', '\u00d6'));
		assertIt("\\A[\\x1f-\\x7f]*\\z", new CharSet((char)31, (char)127));
		assertIt("\\A[\\xff-\\u0100]*\\z",   new CharSet((char)255,    (char)256));
		assertIt("\\A[\\u0fff-\\u1000]*\\z", new CharSet((char)0xfff,  (char)0x1000));
		assertIt("\\A[\\ufffe-\\uffff]*\\z", new CharSet((char)0xfffe, (char)0xffff));
	}
	@Test void testQuoted()
	{
		assertIt("\\A[\\&-\\-\\[-\\]]*\\z", new CharSet('&', '-', '[', ']'));
		assertIt("\\A[\\\\]*\\z", new CharSet('\\', '\\'));
	}
	@Test void testSupplementary()
	{
		assertIt("\\A[ -\\uffff\\U00010000-\\U0010ffff]*\\z", new CharSet(' ', '\uffff'));
		assertIt("\\A[\\ud800-\\udfff\\U00010000-\\U0010ffff]*\\z", new CharSet(Character.MIN_SURROGATE, Character.MAX_SURROGATE));
	}

	private static void assertIt(final String expected, final CharSet actual)
	{
		assertEquals(expected, getRegularExpression(actual));
	}
}
