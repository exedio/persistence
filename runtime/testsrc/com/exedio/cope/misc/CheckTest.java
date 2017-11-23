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
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

public class CheckTest
{
	@Test void testRequireGreaterZeroInt()
	{
		assertEquals(1, requireGreaterZero(1, "name"));
	}
	@Test void testRequireGreaterZeroIntZero()
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
	@Test void testRequireGreaterZeroIntNegative()
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

	@Test void testRequireGreaterZeroLong()
	{
		assertEquals(1, requireGreaterZero(1l, "name"));
	}
	@Test void testRequireGreaterZeroLongZero()
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
	@Test void testRequireGreaterZeroLongNegative()
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

	@Test void testRequireNonNegativeInt()
	{
		assertEquals(1, requireNonNegative(1, "name"));
	}
	@Test void testRequireNonNegativeIntZero()
	{
		assertEquals(0, requireNonNegative(0, "name"));
	}
	@Test void testRequireNonNegativeIntNegative()
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

	@Test void testRequireNonNegativeLong()
	{
		assertEquals(1l, requireNonNegative(1l, "name"));
	}
	@Test void testRequireNonNegativeLongZero()
	{
		assertEquals(0l, requireNonNegative(0l, "name"));
	}
	@Test void testRequireNonNegativeLongNegative()
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
	@Test void testRequireNonEmptyString()
	{
		assertSame("x", requireNonEmpty("x", "name"));
	}
	@Test void testRequireNonEmptyStringNull()
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
	@Test void testRequireNonEmptyStringEmpty()
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

	@Test void testRequireNonEmptyAndCopyObjectsCopy()
	{
		final Object[] original = {"a", "b", "c"};
		final Object[] copy = requireNonEmptyAndCopy(original, "name");
		assertEquals(Arrays.asList("a", "b", "c"), Arrays.asList(copy));
		assertNotSame(original, copy);
	}
	@Test void testRequireNonEmptyAndCopyObjectsNull()
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
	@Test void testRequireNonEmptyAndCopyObjectsEmpty()
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
	@Test void testRequireNonEmptyAndCopyObjectsElementNull()
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

	@Test void testRequireNonEmptyAndCopyStringsCopy()
	{
		final String[] original = {"a", "b", "c"};
		final String[] copy = requireNonEmptyAndCopy(original, "name");
		assertEquals(Arrays.asList("a", "b", "c"), Arrays.asList(copy));
		assertNotSame(original, copy);
	}
	@Test void testRequireNonEmptyAndCopyStringsNull()
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
	@Test void testRequireNonEmptyAndCopyStringsEmpty()
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
	@Test void testRequireNonEmptyAndCopyStringsElementNull()
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
	@Test void testRequireNonEmptyAndCopyStringsElementEmpty()
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
