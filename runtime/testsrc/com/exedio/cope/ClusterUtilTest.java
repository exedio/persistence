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

import static com.exedio.cope.ClusterUtil.marshal;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.util.Hex;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ClusterUtilTest
{
	private static final byte FILL = (byte)0xee;
	private byte[] buf;

	@BeforeEach final void setUp()
	{
		buf = new byte[100];
		Arrays.fill(buf, (byte)0xee);
	}


	@Test void testMarshalInt()
	{
		assertEquals(7, m(3, 0x456789ab));
		assertBuf(FILL, FILL, FILL, (byte)0xab, (byte)0x89, (byte)0x67, (byte)0x45);
	}

	@Test void testMarshalLong()
	{
		assertEquals(11, m(3, 0x456789aba9876543l));
		assertBuf(FILL, FILL, FILL,
				(byte)0x43, (byte)0x65, (byte)0x87, (byte)0xa9,
				(byte)0xab, (byte)0x89, (byte)0x67, (byte)0x45);
	}

	@Test void testMarshalNegative()
	{
		assertEquals(7, m(3, 0xab896745));
		assertBuf(FILL, FILL, FILL, (byte)0x45, (byte)0x67, (byte)0x89, (byte)0xab);
	}

	@Test void testMarshalNegativeLong()
	{
		assertEquals(11, m(3, 0xab896745a9876543l));
		assertBuf(FILL, FILL, FILL,
				(byte)0x43, (byte)0x65, (byte)0x87, (byte)0xa9,
				(byte)0x45, (byte)0x67, (byte)0x89, (byte)0xab);
	}


	private void assertBuf(final byte... expectedData)
	{
		int i = 0;
		for(; i<expectedData.length; i++)
			assertEqualsHex(i, expectedData[i], buf[i]);
		for(; i<buf.length; i++)
			assertEqualsHex(i, FILL, buf[i]);
	}

	private static void assertEqualsHex(final int index, final byte expected, final byte actual)
	{
		assertEquals(
				expected, actual, 
				"at " + index + " expected: " + toHex(expected) + " but was: " + toHex(actual));
	}

	private static String toHex(final byte b)
	{
		return "0x" + Hex.encodeLower(new byte[]{b});
	}

	private int m(final int pos, final int i)
	{
		return marshal(pos, buf, i);
	}

	private int m(final int pos, final long i)
	{
		return marshal(pos, buf, i);
	}
}
