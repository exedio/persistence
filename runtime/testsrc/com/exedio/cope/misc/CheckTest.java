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

import static com.exedio.cope.tojunit.Assert.assertFails;
import static java.time.Duration.ofNanos;
import static java.time.Duration.ofSeconds;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.time.Duration;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

@SuppressFBWarnings("NP_NULL_PARAM_DEREF_NONVIRTUAL")
public class CheckTest
{
	@Test void testRequireGreaterZeroInt()
	{
		assertEquals(1, Check.requireGreaterZero(1, "name"));
	}
	@Test void testRequireGreaterZeroIntZero()
	{
		assertFails(() ->
			Check.requireGreaterZero(0, "name"),
			IllegalArgumentException.class,
			"name must be greater zero, but was 0");
	}
	@Test void testRequireGreaterZeroIntNegative()
	{
		assertFails(() ->
			Check.requireGreaterZero(-1, "name"),
			IllegalArgumentException.class,
			"name must be greater zero, but was -1");
	}

	@Test void testRequireGreaterZeroLong()
	{
		assertEquals(1, Check.requireGreaterZero(1l, "name"));
	}
	@Test void testRequireGreaterZeroLongZero()
	{
		assertFails(() ->
			Check.requireGreaterZero(0l, "name"),
			IllegalArgumentException.class,
			"name must be greater zero, but was 0");
	}
	@Test void testRequireGreaterZeroLongNegative()
	{
		assertFails(() ->
			Check.requireGreaterZero(-1l, "name"),
			IllegalArgumentException.class,
			"name must be greater zero, but was -1");
	}

	@Test void testRequireNonNegativeInt()
	{
		assertEquals(1, Check.requireNonNegative(1, "name"));
	}
	@Test void testRequireNonNegativeIntZero()
	{
		assertEquals(0, Check.requireNonNegative(0, "name"));
	}
	@Test void testRequireNonNegativeIntNegative()
	{
		assertFails(() ->
			Check.requireNonNegative(-1, "name"),
			IllegalArgumentException.class,
			"name must not be negative, but was -1");
	}

	@Test void testRequireNonNegativeLong()
	{
		assertEquals(1l, Check.requireNonNegative(1l, "name"));
	}
	@Test void testRequireNonNegativeLongZero()
	{
		assertEquals(0l, Check.requireNonNegative(0l, "name"));
	}
	@Test void testRequireNonNegativeLongNegative()
	{
		assertFails(() ->
			Check.requireNonNegative(-1l, "name"),
			IllegalArgumentException.class,
			"name must not be negative, but was -1");
	}

	@Test void testRequireAtLeast()
	{
		final Duration value = ofSeconds(55);
		assertSame(value, Check.requireAtLeast(value, "name", ofSeconds(55)));
	}
	@Test void testRequireAtLeastFails()
	{
		assertFails(() ->
			Check.requireAtLeast(ofSeconds(55).minus(ofNanos(1)), "name", ofSeconds(55)),
			IllegalArgumentException.class,
			"name must be at least PT55S, but was PT54.999999999S");
	}
	@Test void testRequireAtLeastNull()
	{
		assertFails(() ->
			Check.requireAtLeast(null, "name", null),
			NullPointerException.class,
			"name");
	}
	@Test void testRequireAtLeastMinimumNull()
	{
		assertFails(() ->
			Check.requireAtLeast(ofSeconds(55), "name", null),
			NullPointerException.class,
			"minimum");
	}

	@SuppressFBWarnings("ES_COMPARING_STRINGS_WITH_EQ")
	@Test void testRequireNonEmptyString()
	{
		assertSame("x", Check.requireNonEmpty("x", "name"));
	}
	@Test void testRequireNonEmptyStringNull()
	{
		assertFails(() ->
			Check.requireNonEmpty(null, "name"),
			NullPointerException.class,
			"name");
	}
	@Test void testRequireNonEmptyStringEmpty()
	{
		assertFails(() ->
			Check.requireNonEmpty("", "name"),
			IllegalArgumentException.class,
			"name must not be empty");
	}

	@Test void testRequireNonEmptyAndCopyObjectsCopy()
	{
		final Object[] original = {"a", "b", "c"};
		final Object[] copy = Check.requireNonEmptyAndCopy(original, "name");
		assertEquals(Arrays.asList("a", "b", "c"), Arrays.asList(copy));
		assertNotSame(original, copy);
	}
	@Test void testRequireNonEmptyAndCopyObjectsNull()
	{
		assertFails(() ->
			Check.requireNonEmptyAndCopy((Object[])null, "name"),
			NullPointerException.class,
			"name");
	}
	@Test void testRequireNonEmptyAndCopyObjectsEmpty()
	{
		assertFails(() ->
			Check.requireNonEmptyAndCopy(new Object[0], "name"),
			IllegalArgumentException.class,
			"name must not be empty");
	}
	@Test void testRequireNonEmptyAndCopyObjectsElementNull()
	{
		assertFails(() ->
			Check.requireNonEmptyAndCopy(new Object[]{"hallo", null}, "name"),
			NullPointerException.class,
			"name[1]");
	}

	@Test void testRequireNonEmptyAndCopyStringsCopy()
	{
		final String[] original = {"a", "b", "c"};
		final String[] copy = Check.requireNonEmptyAndCopy(original, "name");
		assertEquals(Arrays.asList("a", "b", "c"), Arrays.asList(copy));
		assertNotSame(original, copy);
	}
	@Test void testRequireNonEmptyAndCopyStringsNull()
	{
		assertFails(() ->
			Check.requireNonEmptyAndCopy(null, "name"),
			NullPointerException.class,
			"name");
	}
	@Test void testRequireNonEmptyAndCopyStringsEmpty()
	{
		assertFails(() ->
			Check.requireNonEmptyAndCopy(new String[0], "name"),
			IllegalArgumentException.class,
			"name must not be empty");
	}
	@Test void testRequireNonEmptyAndCopyStringsElementNull()
	{
		assertFails(() ->
			Check.requireNonEmptyAndCopy(new String[]{"hallo", null}, "name"),
			NullPointerException.class,
			"name[1]");
	}
	@Test void testRequireNonEmptyAndCopyStringsElementEmpty()
	{
		assertFails(() ->
			Check.requireNonEmptyAndCopy(new String[]{"hallo", ""}, "name"),
			IllegalArgumentException.class,
			"name[1] must not be empty");
	}
}
