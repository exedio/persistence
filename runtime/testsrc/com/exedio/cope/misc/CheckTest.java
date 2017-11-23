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

import static com.exedio.cope.misc.Check.requireGreaterZero;
import static com.exedio.cope.misc.Check.requireNonEmpty;
import static com.exedio.cope.misc.Check.requireNonEmptyAndCopy;
import static com.exedio.cope.misc.Check.requireNonNegative;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

public class CheckTest
{
	@Test public void testRequireGreaterZeroInt()
	{
		assertEquals(1, requireGreaterZero(1, "name"));
	}
	@Test public void testRequireGreaterZeroIntZero()
	{
		try
		{
			requireGreaterZero(0, "name");
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("name must be greater zero, but was 0", e.getMessage());
		}
	}
	@Test public void testRequireGreaterZeroIntNegative()
	{
		try
		{
			requireGreaterZero(-1, "name");
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("name must be greater zero, but was -1", e.getMessage());
		}
	}

	@Test public void testRequireGreaterZeroLong()
	{
		assertEquals(1, requireGreaterZero(1l, "name"));
	}
	@Test public void testRequireGreaterZeroLongZero()
	{
		try
		{
			requireGreaterZero(0l, "name");
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("name must be greater zero, but was 0", e.getMessage());
		}
	}
	@Test public void testRequireGreaterZeroLongNegative()
	{
		try
		{
			requireGreaterZero(-1l, "name");
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("name must be greater zero, but was -1", e.getMessage());
		}
	}

	@Test public void testRequireNonNegativeInt()
	{
		assertEquals(1, requireNonNegative(1, "name"));
	}
	@Test public void testRequireNonNegativeIntZero()
	{
		assertEquals(0, requireNonNegative(0, "name"));
	}
	@Test public void testRequireNonNegativeIntNegative()
	{
		try
		{
			requireNonNegative(-1, "name");
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("name must not be negative, but was -1", e.getMessage());
		}
	}

	@Test public void testRequireNonNegativeLong()
	{
		assertEquals(1l, requireNonNegative(1l, "name"));
	}
	@Test public void testRequireNonNegativeLongZero()
	{
		assertEquals(0l, requireNonNegative(0l, "name"));
	}
	@Test public void testRequireNonNegativeLongNegative()
	{
		try
		{
			requireNonNegative(-1l, "name");
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("name must not be negative, but was -1", e.getMessage());
		}
	}

	@SuppressFBWarnings("ES_COMPARING_STRINGS_WITH_EQ")
	@Test public void testRequireNonEmptyString()
	{
		assertSame("x", requireNonEmpty("x", "name"));
	}
	@Test public void testRequireNonEmptyStringNull()
	{
		try
		{
			requireNonEmpty(null, "name");
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("name", e.getMessage());
		}
	}
	@Test public void testRequireNonEmptyStringEmpty()
	{
		try
		{
			requireNonEmpty("", "name");
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("name must not be empty", e.getMessage());
		}
	}

	@Test public void testRequireNonEmptyAndCopyObjectsCopy()
	{
		final Object[] original = {"a", "b", "c"};
		final Object[] copy = requireNonEmptyAndCopy(original, "name");
		assertEquals(Arrays.asList("a", "b", "c"), Arrays.asList(copy));
		assertNotSame(original, copy);
	}
	@Test public void testRequireNonEmptyAndCopyObjectsNull()
	{
		try
		{
			requireNonEmptyAndCopy((Object[])null, "name");
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("name", e.getMessage());
		}
	}
	@Test public void testRequireNonEmptyAndCopyObjectsEmpty()
	{
		try
		{
			requireNonEmptyAndCopy(new Object[0], "name");
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("name must not be empty", e.getMessage());
		}
	}
	@Test public void testRequireNonEmptyAndCopyObjectsElementNull()
	{
		try
		{
			requireNonEmptyAndCopy(new Object[]{"hallo", null}, "name");
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("name[1]", e.getMessage());
		}
	}

	@Test public void testRequireNonEmptyAndCopyStringsCopy()
	{
		final String[] original = {"a", "b", "c"};
		final String[] copy = requireNonEmptyAndCopy(original, "name");
		assertEquals(Arrays.asList("a", "b", "c"), Arrays.asList(copy));
		assertNotSame(original, copy);
	}
	@Test public void testRequireNonEmptyAndCopyStringsNull()
	{
		try
		{
			requireNonEmptyAndCopy((String[])null, "name");
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("name", e.getMessage());
		}
	}
	@Test public void testRequireNonEmptyAndCopyStringsEmpty()
	{
		try
		{
			requireNonEmptyAndCopy(new String[0], "name");
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("name must not be empty", e.getMessage());
		}
	}
	@Test public void testRequireNonEmptyAndCopyStringsElementNull()
	{
		try
		{
			requireNonEmptyAndCopy(new String[]{"hallo", null}, "name");
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("name[1]", e.getMessage());
		}
	}
	@Test public void testRequireNonEmptyAndCopyStringsElementEmpty()
	{
		try
		{
			requireNonEmptyAndCopy(new String[]{"hallo", ""}, "name");
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("name[1] must not be empty", e.getMessage());
		}
	}
}
