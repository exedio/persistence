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

import static com.exedio.cope.misc.Arrays.copyOf;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.misc.Arrays;
import org.junit.jupiter.api.Test;

public class ArraysTest
{
	@Test void testCopyOf()
	{
		assertArrayEquals(
				new boolean[]{true,false,true}, copyOf(
				new boolean[]{true,false,true}));
		assertArrayEquals(
				new boolean[]{true}, copyOf(
				new boolean[]{true}));
		assertArrayEquals(
				new boolean[]{}, copyOf(
				new boolean[]{}));
		assertArrayEquals(
				new byte[]{1,2,3}, copyOf(
				new byte[]{1,2,3}));
		assertArrayEquals(
				new byte[]{1}, copyOf(
				new byte[]{1}));
		assertArrayEquals(
				new byte[]{}, copyOf(
				new byte[]{}));
		assertArrayEquals(
				new int[]{1,2,3}, copyOf(
				new int[]{1,2,3}));
		assertArrayEquals(
				new int[]{1}, copyOf(
				new int[]{1}));
		assertArrayEquals(
				new int[]{}, copyOf(
				new int[]{}));
		assertArrayEquals(
				new String[]{"1","2","3"}, copyOf(
				new String[]{"1","2","3"}));
		assertArrayEquals(
				new String[]{"1"}, copyOf(
				new String[]{"1"}));
		assertArrayEquals(
				new String[]{}, copyOf(
				new String[]{}));
	}

	@Test void testCopyOfNull()
	{
		assertFails(() -> copyOf((boolean[])null), NullPointerException.class, null);
		assertFails(() -> copyOf((byte   [])null), NullPointerException.class, null);
		assertFails(() -> copyOf((int    [])null), NullPointerException.class, null);
		assertFails(() -> copyOf((String [])null), NullPointerException.class, null);
	}

	@Test void testAppendElement()
	{
		assertArrayEquals(
				new boolean[]{true,false,true}, Arrays.append(
				new boolean[]{true,false},true));
		assertArrayEquals(
				new boolean[]{true,false}, Arrays.append(
				new boolean[]{true},false));
		assertArrayEquals(
				new boolean[]{true}, Arrays.append(
				new boolean[]{},true));
		assertArrayEquals(
				new String[]{"1","2","3"}, Arrays.append(
				new String[]{"1","2"},"3"));
		assertArrayEquals(
				new String[]{"1","2"}, Arrays.append(
				new String[]{"1"},"2"));
		assertArrayEquals(
				new String[]{"1"}, Arrays.append(
				new String[]{},"1"));
		assertArrayEquals(
				new String[]{"1","2",null}, Arrays.append(
				new String[]{"1","2"},(String)null));
	}

	@Test void testAppendElementNull()
	{
		assertFails(() -> Arrays.append(null, true), NullPointerException.class, null);
		assertFails(() -> Arrays.append(null, "x" ), NullPointerException.class, null);
	}

	@Test void testPrependElement()
	{
		assertArrayEquals(
				new String[]{"1","2","3"}, Arrays.prepend(
				"1",new String[]{"2","3"}));
		assertArrayEquals(
				new String[]{"1","2"}, Arrays.prepend(
				"1",new String[]{"2"}));
		assertArrayEquals(
				new String[]{"1"}, Arrays.prepend(
				"1",new String[]{}));
		assertArrayEquals(
				new String[]{null,"1","2"}, Arrays.prepend(
				null, new String[]{"1","2"}));
	}

	@Test void testPrependElementNull()
	{
		assertFails(() -> Arrays.prepend("x", null), NullPointerException.class, null);
	}

	@Test void testAppendArray()
	{
		assertArrayEquals(
				new String[]{"1","2","3","4"}, Arrays.append(
				new String[]{"1","2"},new String[]{"3","4"}));
		assertArrayEquals(
				new String[]{"1","2"}, Arrays.append(
				new String[]{"1"},new String[]{"2"}));
		assertArrayEquals(
				new String[]{}, Arrays.append(
				new String[]{},new String[]{}));
	}

	@Test void testAppendArrayNull()
	{
		assertFails(() -> Arrays.append(new String[]{"x"}, null), NullPointerException.class, null);
		assertFails(() -> Arrays.append(null, new String[]{"x"}), NullPointerException.class, null);
	}

	@Test void testIt()
	{
		assertFails(
				() -> Arrays.toString(null, 0),
				IllegalArgumentException.class,
				"limit must be greater that zero, but was 0");
		assertFails(
				() -> Arrays.append(null, null, 0),
				IllegalArgumentException.class,
				"limit must be greater that zero, but was 0");

		assertIt("null", null, 1);
		assertIt("[]", new byte[]{}, 1);
		assertIt("[20]",         new byte[]{20}, 1);
		assertIt("[20]",         new byte[]{20}, 2);
		assertIt("[20, 21]",     new byte[]{20, 21}, 2);
		assertIt("[20, 21]",     new byte[]{20, 21}, 3);
		assertIt("[20, 21, 22]", new byte[]{20, 21, 22}, 3);
		assertIt("[20, 21 ... (3)]", new byte[]{20, 21, 22}, 2);
		assertIt("[20 ... (3)]", new byte[]{20, 21, 22}, 1);
	}

	private static void assertIt(final String expected, final byte[] a, final int limit)
	{
		assertEquals(expected, Arrays.toString(a, limit));
		final StringBuilder bf = new StringBuilder();
		Arrays.append(bf, a, limit);
		assertEquals(expected, bf.toString());
	}
}
