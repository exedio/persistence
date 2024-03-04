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

import static com.exedio.cope.tojunit.Assert.assertFails;
import static com.exedio.cope.util.Hex.decodeLower;
import static com.exedio.cope.util.Hex.encodeLower;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class DataConsumerTest
{
	@Test void testOneUpdate()
	{
		final DataConsumer c = new DataConsumer(4);
		c.acceptLength(50);
		assertEquals("", encodeLower(c.start()));

		c.acceptBytes(decodeLower("01020304"), 4);
		assertEquals("01020304", encodeLower(c.start()));

		c.acceptBytes(decodeLower("abcd"), 2);
		assertEquals("01020304", encodeLower(c.start()));
	}
	@Test void testOneUpdateOver()
	{
		final DataConsumer c = new DataConsumer(4);
		c.acceptLength(50);
		assertEquals("", encodeLower(c.start()));

		c.acceptBytes(decodeLower("0102030405"), 5);
		assertEquals("01020304", encodeLower(c.start()));

		c.acceptBytes(decodeLower("abcd"), 2);
		assertEquals("01020304", encodeLower(c.start()));
	}
	@Test void testTwoUpdates()
	{
		final DataConsumer c = new DataConsumer(6);
		c.acceptLength(50);
		assertEquals("", encodeLower(c.start()));

		c.acceptBytes(decodeLower("01020304"), 4);
		assertEquals("01020304", encodeLower(c.start()));

		c.acceptBytes(decodeLower("abcd"), 2);
		assertEquals("01020304abcd", encodeLower(c.start()));

		c.acceptBytes(decodeLower("ee"), 1);
		assertEquals("01020304abcd", encodeLower(c.start()));
	}
	@Test void testEmptyUpdate()
	{
		final DataConsumer c = new DataConsumer(4);
		c.acceptLength(50);
		assertEquals("", encodeLower(c.start()));

		c.acceptBytes(decodeLower(""), 0);
		assertEquals("", encodeLower(c.start()));
	}
	@Test void testLengthMore()
	{
		final DataConsumer c = new DataConsumer(20);
		c.acceptLength(50);
		c.acceptBytes(decodeLower("01020304"), 3);
		assertEquals("010203", encodeLower(c.start()));
	}
	@Test void testLengthLess()
	{
		final DataConsumer c = new DataConsumer(20);
		c.acceptLength(50);
		final byte[] input = decodeLower("01020304");
		assertFails(
				() -> c.acceptBytes(input, 5),
				ArrayIndexOutOfBoundsException.class,
				"arraycopy: last source index 5 out of bounds for byte[4]");
	}
	@Test void testDisabled()
	{
		final DataConsumer c = new DataConsumer(0);
		c.acceptLength(50);
		assertEquals("", encodeLower(c.start()));
		c.acceptBytes(decodeLower("010203"), 3);
		assertEquals("", encodeLower(c.start()));
		c.acceptBytes(decodeLower(""), 0);
	}
}
