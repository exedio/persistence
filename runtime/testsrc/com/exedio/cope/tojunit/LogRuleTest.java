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

import static com.exedio.cope.tojunit.LogRule.msFilter;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class LogRuleTest
{
	@Test void testIt()
	{
		assertEquals("", msFilter.apply(""));
		assertEquals("abc", msFilter.apply("abc"));
		assertEquals("abc XXmsdef", msFilter.apply("abc 0msdef"));
		assertEquals(" XXms", msFilter.apply(" 0ms"));
		assertEquals(" XXms", msFilter.apply(" 1ms"));
		assertEquals(" XXms", msFilter.apply(" 9ms"));
		assertEquals(" 00ms", msFilter.apply(" 00ms")); // TODO
		assertEquals(" 11ms", msFilter.apply(" 11ms")); // TODO
		assertEquals(" 99ms", msFilter.apply(" 99ms")); // TODO
		assertEquals("1ms", msFilter.apply("1ms"));
		assertEquals(" Ams", msFilter.apply(" Ams"));
		assertEquals(" 1m", msFilter.apply(" 1m"));
	}
}
