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

import static com.exedio.cope.VaultTrail.truncate;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class VaultTrailTruncateTest
{
	@Test void test()
	{
		assertEquals("0123456",    truncate("0123456", 10));
		assertEquals("01234567",   truncate("01234567", 10));
		assertEquals("012345678",  truncate("012345678", 10));
		assertEquals("0123456789", truncate("0123456789", 10));
		assertEquals("012345 ...", truncate("01234567890", 10));
		assertEquals("012345 ...", truncate("012345678901", 10));
	}
	@Test void testMin()
	{
		assertEquals(null,   truncate("", 4));
		assertEquals("0",    truncate("0", 4));
		assertEquals("01",   truncate("01", 4));
		assertEquals("012",  truncate("012", 4));
		assertEquals("0123", truncate("0123", 4));
		assertEquals(" ...", truncate("01234", 4));
		assertEquals(" ...", truncate("012345", 4));
		assertEquals(" ...", truncate("0123456", 4));
	}
	@Test void testEmpty()
	{
		assertEquals(null, truncate(null, 10));
	}
	@Test void testNull()
	{
		assertEquals(null, truncate("", 10));
	}
}
