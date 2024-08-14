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

import static com.exedio.cope.pattern.MapFieldItem.Language.DE;
import static com.exedio.cope.pattern.MapFieldItem.Language.EN;
import static com.exedio.cope.pattern.MapFieldItem.Language.PL;
import static com.exedio.cope.pattern.MapFieldItem.TYPE;
import static com.exedio.cope.pattern.MapFieldItem.name;
import static com.exedio.cope.tojunit.Assert.assertContains;
import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static com.exedio.cope.tojunit.Assert.map;
import static com.exedio.cope.tojunit.Assert.sensitive;
import static java.lang.Integer.valueOf;
import static java.util.Collections.emptyMap;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.Join;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.Query;
import com.exedio.cope.StringLengthViolationException;
import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.pattern.MapFieldItem.Language;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SuppressWarnings("MapReplaceableByEnumMap")
public class MapFieldTest extends TestWithEnvironment
{
	public MapFieldTest()
	{
		super(MapFieldModelTest.MODEL);
	}

	MapFieldItem item, itemX;

	@BeforeEach final void setUp()
	{
		item = new MapFieldItem();
		itemX = new MapFieldItem();
	}

	@Test void testIt()
	{
		assertEquals(null, item.getName(DE));
		assertEquals(null, item.getNameLength(DE));
		assertEquals(null, item.getName(EN));
		assertEquals(null, item.getNameLength(EN));
		assertEqualsUnmodifiable(map(), item.getNameMap());
		assertEqualsUnmodifiable(map(), item.getNameLengthMap());

		item.setName(DE, "nameDE");
		assertEquals("nameDE", item.getName(DE));
		assertEquals(null, item.getNameLength(DE));
		assertEquals(null, item.getName(EN));
		assertEquals(null, item.getNameLength(EN));
		assertEquals(null, itemX.getName(DE));
		assertEqualsUnmodifiable(map(DE, "nameDE"), item.getNameMap());
		assertEqualsUnmodifiable(map(), item.getNameLengthMap());
		{
			final Query<MapFieldItem> q = TYPE.newQuery(name.getValue().equal("nameDE"));
			final Join join = name.join(q, DE);
			assertEquals(Join.Kind.OUTER_LEFT, join.getKind());
			assertEquals(name.getEntryType(), join.getType());
			assertEquals(
					"(MapFieldItem-name.parent=MapFieldItem.this and MapFieldItem-name.key='DE')",
					join.getCondition().toString());
			assertContains(item, q.search());
		}
		{
			final Query<MapFieldItem> q = TYPE.newQuery(name.getValue().equal("nameEN"));
			name.join(q, DE);
			assertContains(q.search());
		}
		{
			final Query<MapFieldItem> q = TYPE.newQuery(name.getValue().equal("nameDE"));
			name.join(q, EN);
			assertContains(q.search());
		}

		item.setNameLength(DE, 5);
		assertEquals("nameDE", item.getName(DE));
		assertEquals(valueOf(5), item.getNameLength(DE));
		assertEquals(null, item.getName(EN));
		assertEquals(null, item.getNameLength(EN));
		assertEquals(null, itemX.getName(DE));
		assertEqualsUnmodifiable(map(DE, "nameDE"), item.getNameMap());
		assertEqualsUnmodifiable(map(DE, 5), item.getNameLengthMap());

		item.setNameLength(DE, 6);
		assertEquals("nameDE", item.getName(DE));
		assertEquals(valueOf(6), item.getNameLength(DE));
		assertEquals(null, item.getName(EN));
		assertEquals(null, item.getNameLength(EN));
		assertEquals(null, itemX.getName(DE));
		assertEqualsUnmodifiable(map(DE, "nameDE"), item.getNameMap());
		assertEqualsUnmodifiable(map(DE, 6), item.getNameLengthMap());

		item.setName(EN, "nameEN");
		assertEquals("nameDE", item.getName(DE));
		assertEquals(valueOf(6), item.getNameLength(DE));
		assertEquals("nameEN", item.getName(EN));
		assertEquals(null, item.getNameLength(EN));
		assertEquals(null, itemX.getName(DE));
		assertEqualsUnmodifiable(map(DE, "nameDE", EN, "nameEN"), item.getNameMap());
		assertEqualsUnmodifiable(map(DE, 6), item.getNameLengthMap());

		item.setName(DE, null);
		assertEquals(null, item.getName(DE));
		assertEquals(valueOf(6), item.getNameLength(DE));
		assertEquals("nameEN", item.getName(EN));
		assertEquals(null, item.getNameLength(EN));
		assertEquals(null, itemX.getName(DE));
		assertEqualsUnmodifiable(map(EN, "nameEN"), item.getNameMap());
		assertEqualsUnmodifiable(map(DE, 6), item.getNameLengthMap());

		assertFails(
				() -> item.getName(null),
				NullPointerException.class,
				"key");
		assertFails(
				() -> item.setName(null, "hallo"),
				NullPointerException.class,
				"key");
	}

	@Test void testMapSet()
	{
		final HashMap<Language, String> map = new HashMap<>();
		final Map<Language, String> mapU = sensitive(map, Language.class, String.class);
		assertEqualsUnmodifiable(map(), item.getNameMap());

		item.setNameMap(mapU);
		assertEqualsUnmodifiable(map(), item.getNameMap());

		map.put(DE, "nameDE");
		item.setNameMap(mapU);
		assertEqualsUnmodifiable(map(DE, "nameDE"), item.getNameMap());

		map.put(EN, "nameEN");
		map.put(DE, "nameDE2");
		item.setNameMap(mapU);
		assertEqualsUnmodifiable(map(DE, "nameDE2", EN, "nameEN"), item.getNameMap());

		map.put(PL, "namePL");
		map.remove(DE);
		item.setNameMap(mapU);
		assertEqualsUnmodifiable(map(EN, "nameEN", PL, "namePL"), item.getNameMap());

		map.clear();
		item.setNameMap(mapU);
		assertEqualsUnmodifiable(map(), item.getNameMap());
	}

	@Test void testMapSetNull()
	{
		final HashMap<Language, String> map = new HashMap<>();
		final Map<Language, String> mapU = sensitive(map, Language.class, String.class);
		map.put(PL, "namePL");
		item.setNameMap(mapU);
		assertEqualsUnmodifiable(map(PL, "namePL"), item.getNameMap());

		assertFails(
				() -> item.setNameMap(null),
				MandatoryViolationException.class,
				"mandatory violation on MapFieldItem-0 for MapFieldItem.name",
				name, item);
		assertEqualsUnmodifiable(map(PL, "namePL"), item.getNameMap());
	}

	@Test void testMapSetKeyNull()
	{
		final HashMap<Language, String> map = new HashMap<>();
		final Map<Language, String> mapU = sensitive(map, Language.class, String.class);
		map.put(PL, "namePL");
		item.setNameMap(mapU);
		assertEqualsUnmodifiable(map(PL, "namePL"), item.getNameMap());

		map.put(null, "nameNull");
		assertFails(
				() -> item.setNameMap(mapU),
				MandatoryViolationException.class,
				"mandatory violation for MapFieldItem-name.key",
				name.getKey());
		assertEqualsUnmodifiable(map(PL, "namePL"), item.getNameMap());
	}

	@Test void testMapSetValueNull()
	{
		final HashMap<Language, String> map = new HashMap<>();
		final Map<Language, String> mapU = sensitive(map, Language.class, String.class);
		map.put(PL, "namePL");
		item.setNameMap(mapU);
		assertEqualsUnmodifiable(map(PL, "namePL"), item.getNameMap());

		map.put(PL, null);
		assertFails(
				() -> item.setNameMap(mapU),
				MandatoryViolationException.class,
				"mandatory violation for MapFieldItem-name.value",
				name.getValue());
		assertEqualsUnmodifiable(map(PL, "namePL"), item.getNameMap());
	}

	@Test void testMapSetKeyOtherViolation()
	{
		final LinkedHashMap<String, String> map = new LinkedHashMap<>();
		final Map<String, String> mapU = sensitive(map, String.class, String.class);
		map.put("one1",  "value1");
		map.put("two",   "value2");
		map.put("three", "value3");
		final StringLengthViolationException e = assertFails(
				() -> item.setStringMap(mapU),
				StringLengthViolationException.class,
				"length violation, 'two' is too short for MapFieldItem-string.key, " +
				"must be at least 4 characters, but was 3",
				MapFieldItem.string.getKey());
		assertEquals("two", e.getValue());
		assertEqualsUnmodifiable(emptyMap(), item.getStringMap());
	}

	@Test void testMapSetValueOtherViolation()
	{
		final LinkedHashMap<String, String> map = new LinkedHashMap<>();
		final Map<String, String> mapU = sensitive(map, String.class, String.class);
		map.put("key1", "one1");
		map.put("key2", "two");
		map.put("key3", "three");
		final StringLengthViolationException e = assertFails(
				() -> item.setStringMap(mapU),
				StringLengthViolationException.class,
				"length violation, 'two' is too short for MapFieldItem-string.value, " +
				"must be at least 4 characters, but was 3",
				MapFieldItem.string.getValue());
		assertEquals("two", e.getValue());
		assertEqualsUnmodifiable(emptyMap(), item.getStringMap());
	}

	@Test void testGetAndCast()
	{
		item.setName(DE, "NAMEde");
		assertEquals("NAMEde", name.getAndCast(item, DE));
		assertFails(
				() -> name.getAndCast(item, "DE"),
				ClassCastException.class,
				"Cannot cast " + String.class.getName() + " to " + Language.class.getName());
		assertFails(
				() -> name.getAndCast(item, null),
				NullPointerException.class,
				"key");
	}

	@Test void testSetAndCast()
	{
		name.setAndCast(item, DE, "NAMEde");
		assertEquals("NAMEde", item.getName(DE));
		assertFails(
				() -> name.setAndCast(item, "DE", "nameDE"),
				ClassCastException.class,
				"Cannot cast " + String.class.getName() + " to " + Language.class.getName());
		assertFails(
				() -> name.setAndCast(item, DE, 1),
				ClassCastException.class,
				"Cannot cast " + Integer.class.getName() + " to " + String.class.getName());
		assertEquals("NAMEde", item.getName(DE));
		assertFails(
				() -> name.setAndCast(item, null, "nameDE"),
				NullPointerException.class,
				"key");
		name.setAndCast(item, DE, null);
		assertEquals(null, item.getName(DE));
	}

	@Test void testOrder()
	{
		assertEqualsUnmodifiable(map(), item.getStringMap());

		item.setString("2two", "twoV");
		assertEqualsUnmodifiable(map(
				"2two", "twoV"),
				item.getStringMap());

		item.setString("1one", "oneV");
		assertEqualsUnmodifiable(map(
				"1one", "oneV",
				"2two", "twoV"),
				item.getStringMap());

		item.setString("3three", "threeV");
		assertEqualsUnmodifiable(map(
				"1one", "oneV",
				"2two", "twoV",
				"3three", "threeV"),
				item.getStringMap());
	}
}
