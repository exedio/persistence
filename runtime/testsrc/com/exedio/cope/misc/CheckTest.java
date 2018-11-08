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
import static com.exedio.cope.tojunit.Assert.assertFails;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

@SuppressFBWarnings("NP_NULL_PARAM_DEREF_NONVIRTUAL")
public class CheckTest
{
	@Test void testRequireGreaterZeroInt()
	{
		assertEquals(1, requireGreaterZero(1, "name"));
	}
	@Test void testRequireGreaterZeroIntZero()
	{
		assertFails(() ->
			requireGreaterZero(0, "name"),
			IllegalArgumentException.class,
			"name must be greater zero, but was 0");
	}
	@Test void testRequireGreaterZeroIntNegative()
	{
		assertFails(() ->
			requireGreaterZero(-1, "name"),
			IllegalArgumentException.class,
			"name must be greater zero, but was -1");
	}

	@Test void testRequireGreaterZeroLong()
	{
		assertEquals(1, requireGreaterZero(1l, "name"));
	}
	@Test void testRequireGreaterZeroLongZero()
	{
		assertFails(() ->
			requireGreaterZero(0l, "name"),
			IllegalArgumentException.class,
			"name must be greater zero, but was 0");
	}
	@Test void testRequireGreaterZeroLongNegative()
	{
		assertFails(() ->
			requireGreaterZero(-1l, "name"),
			IllegalArgumentException.class,
			"name must be greater zero, but was -1");
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
		assertFails(() ->
			requireNonNegative(-1, "name"),
			IllegalArgumentException.class,
			"name must not be negative, but was -1");
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
		assertFails(() ->
			requireNonNegative(-1l, "name"),
			IllegalArgumentException.class,
			"name must not be negative, but was -1");
	}

	@SuppressFBWarnings("ES_COMPARING_STRINGS_WITH_EQ")
	@Test void testRequireNonEmptyString()
	{
		assertSame("x", requireNonEmpty("x", "name"));
	}
	@Test void testRequireNonEmptyStringNull()
	{
		assertFails(() ->
			requireNonEmpty(null, "name"),
			NullPointerException.class,
			"name");
	}
	@Test void testRequireNonEmptyStringEmpty()
	{
		assertFails(() ->
			requireNonEmpty("", "name"),
			IllegalArgumentException.class,
			"name must not be empty");
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
		assertFails(() ->
			requireNonEmptyAndCopy((Object[])null, "name"),
			NullPointerException.class,
			"name");
	}
	@Test void testRequireNonEmptyAndCopyObjectsEmpty()
	{
		assertFails(() ->
			requireNonEmptyAndCopy(new Object[0], "name"),
			IllegalArgumentException.class,
			"name must not be empty");
	}
	@Test void testRequireNonEmptyAndCopyObjectsElementNull()
	{
		assertFails(() ->
			requireNonEmptyAndCopy(new Object[]{"hallo", null}, "name"),
			NullPointerException.class,
			"name[1]");
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
		assertFails(() ->
			requireNonEmptyAndCopy((String[])null, "name"),
			NullPointerException.class,
			"name");
	}
	@Test void testRequireNonEmptyAndCopyStringsEmpty()
	{
		assertFails(() ->
			requireNonEmptyAndCopy(new String[0], "name"),
			IllegalArgumentException.class,
			"name must not be empty");
	}
	@Test void testRequireNonEmptyAndCopyStringsElementNull()
	{
		assertFails(() ->
			requireNonEmptyAndCopy(new String[]{"hallo", null}, "name"),
			NullPointerException.class,
			"name[1]");
	}
	@Test void testRequireNonEmptyAndCopyStringsElementEmpty()
	{
		assertFails(() ->
			requireNonEmptyAndCopy(new String[]{"hallo", ""}, "name"),
			IllegalArgumentException.class,
			"name[1] must not be empty");
	}
}
