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

package com.exedio.cope.misc;

import static com.exedio.cope.misc.Compare.compare;
import static org.junit.Assert.assertEquals;

import com.exedio.cope.util.CharsetName;
import java.nio.charset.StandardCharsets;
import org.junit.Test;

public class CompareTest
{
	@Deprecated // OK: testing deprecated api
	@Test public void testIt()
	{
		assertEquals( 0, compare(0, 0));
		assertEquals(-1, compare(0, 1));
		assertEquals( 1, compare(1, 0));
	}

	@Deprecated // OK: testing deprecated api
	@Test public void testStandardCharsets()
	{
		assertEquals(CharsetName.UTF8, StandardCharsets.UTF_8.name());
	}
}