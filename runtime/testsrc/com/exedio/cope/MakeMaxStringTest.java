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

import static com.exedio.cope.util.Hex.encodeUpper;
import static java.nio.charset.StandardCharsets.UTF_8;

import com.exedio.cope.junit.CopeAssert;
import com.exedio.dsmf.SQLRuntimeException;
import java.util.ArrayList;

public class MakeMaxStringTest extends CopeAssert
{
	public void testBase()
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

	public void testExtended()
	{
		final String grinningFace = "\ud83d\ude00"; // Unicode code point U+1F600 / UTF-8 F0 9F 98 80
		final String unamusedFace = "\ud83d\ude12"; // Unicode code point U+1F612 / UTF-8 F0 9F 98 92
		//System.out.println("--" + grinningFace + "--" + unamusedFace + "--");
		assertEquals(2, grinningFace.length());
		assertEquals(2, unamusedFace.length());
		assertEquals("F09F9880", encodeUpper(grinningFace.getBytes(UTF_8)));
		assertEquals("F09F9892", encodeUpper(unamusedFace.getBytes(UTF_8)));
		assertEquals(4, utf8len(grinningFace));
		assertEquals(4, utf8len(unamusedFace));
		assertEquals(1, grinningFace.codePointCount(0, 2));
		assertEquals(1, unamusedFace.codePointCount(0, 2));
		assertEquals(0x1F600, grinningFace.codePointAt(0));
		assertEquals(0x1F612, unamusedFace.codePointAt(0));

		assertEquals(                                                   "\u8de5", makeMax4(1));
		assertEquals("\ud83d\ude00"                                             , makeMax4(2));
		assertEquals("\ud83d\ude00"                                    +"\u8de5", makeMax4(3));
		assertEquals("\ud83d\ude00\ud83d\ude12"                                 , makeMax4(4));
		assertEquals("\ud83d\ude00\ud83d\ude12"                        +"\u8de5", makeMax4(5));
		assertEquals("\ud83d\ude00\ud83d\ude12\ud83d\ude00"                     , makeMax4(6));
		assertEquals("\ud83d\ude00\ud83d\ude12\ud83d\ude00"            +"\u8de5", makeMax4(7));
		assertEquals("\ud83d\ude00\ud83d\ude12\ud83d\ude00\ud83d\ude12"         , makeMax4(8));
		assertEquals("\ud83d\ude00\ud83d\ude12\ud83d\ude00\ud83d\ude12"+"\u8de5", makeMax4(9));
	}

	private static String makeMax4(final int length)
	{
		return makeMax4(new StringField().lengthMax(length));
	}

	static String makeMax4(final StringField field)
	{
		final int length = Math.min(field.getMaximumLength(), 3*1000*1000);
		final char[] buf = new char[length];

		int i = 0;
		while(i<length)
		{
			if((i+1)<length)
			{
				final boolean grin = (i%4==0);
				buf[i++] = '\ud83d';
				buf[i++] = grin ? '\ude00' : '\ude12';
			}
			else
			{
				buf[i++] = '\u8de5';
			}
		}

		//System.out.println("--" + new String(buf) + "--");
		return new String(buf);
	}

	private static int utf8len(final String s)
	{
		return s.getBytes(UTF_8).length;
	}

	static <I extends Item> I newItem(
			final Type<I> type,
			final ArrayList<SetValue<?>> sv,
			final boolean mb4)
	{
		if(mb4)
			return type.newItem(sv);

		try
		{
			type.newItem(sv);
			fail(type.getID());
		}
		catch(final SQLRuntimeException e)
		{
			// expected
		}
		return null;
	}
}
