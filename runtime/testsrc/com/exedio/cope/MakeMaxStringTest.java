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

import static java.nio.charset.StandardCharsets.UTF_8;

import com.exedio.cope.junit.CopeAssert;

public class MakeMaxStringTest extends CopeAssert
{
	public void testIt()
	{
		final StringField f = new StringField().lengthMax(40);
		assertEquals( 40, utf8len(makeMax1(f)));
		assertEquals( 80, utf8len(makeMax2(f)));
		assertEquals(120, utf8len(makeMax3(f)));

		assertEquals("ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMN", makeMax1(f));
		assertEquals(
				"\u0410\u0411\u0412\u0413\u0414\u0415" +
				"\u0410\u0411\u0412\u0413\u0414\u0415" +
				"\u0410\u0411\u0412\u0413\u0414\u0415" +
				"\u0410\u0411\u0412\u0413\u0414\u0415" +
				"\u0410\u0411\u0412\u0413\u0414\u0415" +
				"\u0410\u0411\u0412\u0413\u0414\u0415" +
				"\u0410\u0411\u0412\u0413",
				makeMax2(f));
		assertEquals(
				"\u8de5\u8de6\u8de7\u8de8\u8de9\u8dea" +
				"\u8de5\u8de6\u8de7\u8de8\u8de9\u8dea" +
				"\u8de5\u8de6\u8de7\u8de8\u8de9\u8dea" +
				"\u8de5\u8de6\u8de7\u8de8\u8de9\u8dea" +
				"\u8de5\u8de6\u8de7\u8de8\u8de9\u8dea" +
				"\u8de5\u8de6\u8de7\u8de8\u8de9\u8dea" +
				"\u8de5\u8de6\u8de7\u8de8",
				makeMax3(f));
	}

	static String makeMax1(final StringField field)
	{
		// TODO test with multi-byte characters, also utf8mb4
		return makeMax(field, 'A', 'Z');
	}

	static String makeMax2(final StringField field)
	{
		return makeMax(field, '\u0410', '\u0415');
	}

	static String makeMax3(final StringField field)
	{
		return makeMax(field, '\u8de5', '\u8dea');
	}

	private static String makeMax(final StringField field, final char from, final char to)
	{
		assertTrue(from<to);
		final int length = Math.min(field.getMaximumLength(), 3*1000*1000);
		final char[] buf = new char[length];

		char val = from;
		for(int i = 0; i<length; i++)
		{
			buf[i] = val;
			val++;
			if(val>to)
				val = from;
		}

		return new String(buf);
	}

	private static int utf8len(final String s)
	{
		return s.getBytes(UTF_8).length;
	}
}
