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

import static com.exedio.cope.tojunit.Assert.assertFails;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.tojunit.SensitiveMap;
import java.lang.Thread.State;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

public class SensitiveTest
{
	@Test void enumMap()
	{
		final EnumMap<State, String> map = new EnumMap<>(State.class);
		assertEquals(false, map.containsKey(null));
		assertEquals(false, EnumMapField.containsKeyNull(map));

		map.put(State.RUNNABLE, "runnable");
		assertEquals(false, map.containsKey(null));
		assertEquals(false, EnumMapField.containsKeyNull(map));
	}
	@Test void hashMap()
	{
		@SuppressWarnings("MapReplaceableByEnumMap") // OK: this is what needs to be tested
		final HashMap<State, String> map = new HashMap<>();
		assertEquals(false, map.containsKey(null));
		assertEquals(false, EnumMapField.containsKeyNull(map));

		map.put(State.RUNNABLE, "runnable");
		assertEquals(false, map.containsKey(null));
		assertEquals(false, EnumMapField.containsKeyNull(map));

		map.put(null, "valueForNull");
		assertEquals(true, map.containsKey(null));
		assertEquals(true, EnumMapField.containsKeyNull(map));
	}
	@Test void butContainsKeyMap()
	{
		@SuppressWarnings("MapReplaceableByEnumMap") // OK: this is what needs to be tested
		final Map<State, String> mapBack = new HashMap<>();
		@SuppressWarnings("MapReplaceableByEnumMap") // OK: this is what needs to be tested
		final Map<State, String> map = new EnumMapFieldTest.SensitiveButContainsKeyMap<>(mapBack, State.class, String.class);
		assertEquals(false, map.containsKey(null));
		assertEquals(false, EnumMapField.containsKeyNull(map));

		map.put(State.RUNNABLE, "runnable");
		assertEquals(false, map.containsKey(null));
		assertEquals(false, EnumMapField.containsKeyNull(map));

		mapBack.put(null, "valueForNull");
		assertEquals(true, map.containsKey(null));
		assertEquals(true, EnumMapField.containsKeyNull(map));

		assertFails(() -> map.put(null, "valueForNull"), AssertionFailedError.class, "null forbidden in key");
	}
	@Test void butContainsKeyNullPointerExceptionMap()
	{
		@SuppressWarnings("MapReplaceableByEnumMap") // OK: this is what needs to be tested
		final Map<State, String> mapBack = new HashMap<>();
		@SuppressWarnings("MapReplaceableByEnumMap") // OK: this is what needs to be tested
		final Map<State, String> map = new EnumMapFieldTest.SensitiveButContainsKeyNullPointerExceptionMap<>(mapBack, State.class, String.class);
		assertFails(() -> map.containsKey(null), NullPointerException.class, "SensitiveButContainsKeyNullPointerExceptionMap");
		assertEquals(false, EnumMapField.containsKeyNull(map));

		map.put(State.RUNNABLE, "runnable");
		assertFails(() -> map.containsKey(null), NullPointerException.class, "SensitiveButContainsKeyNullPointerExceptionMap");
		assertEquals(false, EnumMapField.containsKeyNull(map));

		mapBack.put(null, "valueForNull");
		assertFails(() -> map.containsKey(null), NullPointerException.class, "SensitiveButContainsKeyNullPointerExceptionMap");
		assertEquals(false, EnumMapField.containsKeyNull(map)); // is a bit weird, but that's the way NonNullCollections do work

		assertFails(() -> map.put(null, "valueForNull"), AssertionFailedError.class, "null forbidden in key");
	}
	@Test void map()
	{
		@SuppressWarnings("MapReplaceableByEnumMap") // OK: this is what needs to be tested
		final Map<State, String> mapBack = new HashMap<>();
		@SuppressWarnings("MapReplaceableByEnumMap") // OK: this is what needs to be tested
		final Map<State, String> map = new SensitiveMap<>(mapBack, State.class, String.class);
		assertFails(() -> map.containsKey(null), AssertionFailedError.class, "null forbidden in key");
		assertFails(() -> EnumMapField.containsKeyNull(map), AssertionFailedError.class, "null forbidden in key");

		map.put(State.RUNNABLE, "runnable");
		assertFails(() -> map.containsKey(null), AssertionFailedError.class, "null forbidden in key");
		assertFails(() -> EnumMapField.containsKeyNull(map), AssertionFailedError.class, "null forbidden in key");
		//noinspection SuspiciousMethodCalls
		assertFails(() -> map.containsKey(1), AssertionFailedError.class, "class forbidden in key: java.lang.Integer");

		mapBack.put(null, "valueForNull");
		assertFails(() -> map.containsKey(null), AssertionFailedError.class, "null forbidden in key");
		assertFails(() -> EnumMapField.containsKeyNull(map), AssertionFailedError.class, "null forbidden in key");

		assertFails(() -> map.put(null, "valueForNull"), AssertionFailedError.class, "null forbidden in key");
		//noinspection SuspiciousMethodCalls
		assertFails(() -> map.containsKey(1), AssertionFailedError.class, "class forbidden in key: java.lang.Integer");
	}
}
