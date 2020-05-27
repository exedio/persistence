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

package com.exedio.cope.tojunit;

import static com.exedio.cope.tojunit.LogRule.milliSecondsFilter;
import static com.exedio.cope.tojunit.LogRule.nanoSecondsFilter;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class LogRuleTest
{
	@Test void testMillisecondsFilter()
	{
		assertEquals("", milliSecondsFilter.apply(""));
		assertEquals("abc", milliSecondsFilter.apply("abc"));
		assertEquals("abc XXmsdef", milliSecondsFilter.apply("abc 0msdef"));
		assertEquals(" XXms", milliSecondsFilter.apply(" 0ms"));
		assertEquals(" XXms", milliSecondsFilter.apply(" 1ms"));
		assertEquals(" XXms", milliSecondsFilter.apply(" 9ms"));
		assertEquals(" XXms", milliSecondsFilter.apply(" 000ms"));
		assertEquals(" XXms", milliSecondsFilter.apply(" 111ms"));
		assertEquals(" XXms", milliSecondsFilter.apply(" 999ms"));
		assertEquals(" 0000ms", milliSecondsFilter.apply(" 0000ms"));
		assertEquals(" 1111ms", milliSecondsFilter.apply(" 1111ms"));
		assertEquals(" 9999ms", milliSecondsFilter.apply(" 9999ms"));
		assertEquals("1ms", milliSecondsFilter.apply("1ms"));
		assertEquals(" Ams", milliSecondsFilter.apply(" Ams"));
		assertEquals(" 1m", milliSecondsFilter.apply(" 1m"));
		assertEquals(" 1ns", milliSecondsFilter.apply(" 1ns"));
		assertEquals(" 1,1ms", milliSecondsFilter.apply(" 1,1ms"));
		assertEquals(" 1.1ms", milliSecondsFilter.apply(" 1.1ms"));
	}

	@Test void testNanosecondsFilter()
	{
		assertEquals("", nanoSecondsFilter.apply(""));
		assertEquals("abc", nanoSecondsFilter.apply("abc"));
		assertEquals("abc XXnsdef", nanoSecondsFilter.apply("abc 0nsdef"));
		assertEquals(" XXns", nanoSecondsFilter.apply(" 0ns"));
		assertEquals(" XXns", nanoSecondsFilter.apply(" 1ns"));
		assertEquals(" XXns", nanoSecondsFilter.apply(" 9ns"));
		assertEquals(" XXns", nanoSecondsFilter.apply(" 000000000ns"));
		assertEquals(" XXns", nanoSecondsFilter.apply(" 111111111ns"));
		assertEquals(" XXns", nanoSecondsFilter.apply(" 999999999ns"));
		assertEquals(" 0000000000ns", nanoSecondsFilter.apply(" 0000000000ns"));
		assertEquals(" 1111111111ns", nanoSecondsFilter.apply(" 1111111111ns"));
		assertEquals(" 9999999999ns", nanoSecondsFilter.apply(" 9999999999ns"));
		assertEquals("1ns", nanoSecondsFilter.apply("1ns"));
		assertEquals(" Ans", nanoSecondsFilter.apply(" Ans"));
		assertEquals(" 1n", nanoSecondsFilter.apply(" 1n"));
		assertEquals(" 1ms", nanoSecondsFilter.apply(" 1ms"));
	}
}
