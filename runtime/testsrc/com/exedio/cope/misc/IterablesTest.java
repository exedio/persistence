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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Iterator;
import org.junit.jupiter.api.Test;

public class IterablesTest
{
	@Test void testIt()
	{
		final Iterator<String> iterator = new Iterator<String>(){

			@Override
			public boolean hasNext()
			{
				throw new AssertionError();
			}

			@Override
			public String next()
			{
				throw new AssertionError();
			}

			@Override
			public void remove()
			{
				throw new AssertionError();
			}
		};

		final Iterable<String> iterable = Iterables.once(iterator);
		assertSame(iterator, iterable.iterator());

		try
		{
			iterable.iterator();
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("exhausted", e.getMessage());
		}
		try
		{
			iterable.iterator();
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("exhausted", e.getMessage());
		}
	}

	@Test void testNull()
	{
		try
		{
			Iterables.once(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("iterator", e.getMessage());
		}
	}
}
