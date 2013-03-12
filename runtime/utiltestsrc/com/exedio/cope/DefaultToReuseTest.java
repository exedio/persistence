/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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

import java.util.Random;

import com.exedio.cope.junit.CopeAssert;

public class DefaultToReuseTest extends CopeAssert
{
	public void testRandom()
	{
		final LongField f = new LongField().defaultToRandom(new Random());
		assertEquals(true, f.isMandatory());
		assertEquals(true, f.hasDefault());

		try
		{
			f.optional();
			fail();
		}
		catch(final AssertionError e)
		{
			assertEquals(null, e.getMessage());
		}
		try
		{
			f.min(0);
			fail();
		}
		catch(final AssertionError e)
		{
			assertEquals(null, e.getMessage());
		}
	}

	public void testNext()
	{
		final IntegerField f = new IntegerField().defaultToNext(0);
		assertEquals(true, f.isMandatory());
		assertEquals(true, f.hasDefault());
		assertEquals(true, f.isDefaultNext());

		try
		{
			f.optional();
			fail();
		}
		catch(final AssertionError e)
		{
			assertEquals(null, e.getMessage());
		}
		try
		{
			f.min(0);
			fail();
		}
		catch(final AssertionError e)
		{
			assertEquals(null, e.getMessage());
		}
	}
}
