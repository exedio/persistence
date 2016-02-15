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

package com.exedio.cope.pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

public class DispatcherConfigTest
{
	@Test public void testOk()
	{
		final Dispatcher.Config config = new Dispatcher.Config(3, 2);
		assertEquals(3, config.getFailureLimit());
		assertEquals(2, config.getSearchSize());
	}
	@Test public void testMinimal()
	{
		final Dispatcher.Config config = new Dispatcher.Config(1, 1);
		assertEquals(1, config.getFailureLimit());
		assertEquals(1, config.getSearchSize());
	}
	@Test public void testFailureLimitZero()
	{
		try
		{
			new Dispatcher.Config(0, 0);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("failureLimit must be greater zero, but was 0", e.getMessage());
		}
	}
	@Test public void testFailureLimitNegative()
	{
		try
		{
			new Dispatcher.Config(-10, 0);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("failureLimit must be greater zero, but was -10", e.getMessage());
		}
	}
	@Test public void testSearchSizeZero()
	{
		try
		{
			new Dispatcher.Config(1000, 0);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("searchSize must be greater zero, but was 0", e.getMessage());
		}
	}
	@Test public void testSearchSizeNegative()
	{
		try
		{
			new Dispatcher.Config(1000, -10);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("searchSize must be greater zero, but was -10", e.getMessage());
		}
	}
}
