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
import static com.exedio.cope.tojunit.Assert.map;
import static java.lang.Integer.valueOf;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.Join;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.Query;
import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.pattern.MapFieldItem.Language;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Collections;
import java.util.HashMap;
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
			assertEquals(name.getRelationType(), join.getType());
			assertEquals(
					"(MapFieldItem-name.parent=MapFieldItem.this AND MapFieldItem-name.key='DE')",
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

		try
		{
			item.getName(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("key", e.getMessage());
		}
		try
		{
			item.setName(null, "hallo");
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("key", e.getMessage());
		}
	}

	@Test void testMapSet()
	{
		final HashMap<Language, String> map = new HashMap<>();
		final Map<Language, String> mapU = Collections.unmodifiableMap(map);
		assertEquals(map(), item.getNameMap());

		item.setNameMap(mapU);
		assertEquals(map(), item.getNameMap());

		map.put(DE, "nameDE");
		item.setNameMap(mapU);
		assertEquals(map(DE, "nameDE"), item.getNameMap());

		map.put(EN, "nameEN");
		map.put(DE, "nameDE2");
		item.setNameMap(mapU);
		assertEquals(map(DE, "nameDE2", EN, "nameEN"), item.getNameMap());

		map.put(PL, "namePL");
		map.remove(DE);
		item.setNameMap(mapU);
		assertEquals(map(PL, "namePL", EN, "nameEN"), item.getNameMap());

		map.clear();
		item.setNameMap(mapU);
		assertEquals(map(), item.getNameMap());
	}

	@SuppressFBWarnings({"NP_NULL_PARAM_DEREF_ALL_TARGETS_DANGEROUS", "NP_NONNULL_PARAM_VIOLATION"})
	@Test void testMapSetNull()
	{
		final HashMap<Language, String> map = new HashMap<>();
		final Map<Language, String> mapU = Collections.unmodifiableMap(map);
		map.put(PL, "namePL");
		item.setNameMap(mapU);
		assertEquals(map(PL, "namePL"), item.getNameMap());

		try
		{
			item.setNameMap(null);
			fail();
		}
		catch(final MandatoryViolationException e)
		{
			assertEquals(name, e.getFeature());
			assertEquals(item, e.getItem());
		}
		assertEquals(map(PL, "namePL"), item.getNameMap());
	}

	@Test void testMapSetKeyNull()
	{
		final HashMap<Language, String> map = new HashMap<>();
		final Map<Language, String> mapU = Collections.unmodifiableMap(map);
		map.put(PL, "namePL");
		item.setNameMap(mapU);
		assertEquals(map(PL, "namePL"), item.getNameMap());

		map.put(null, "nameNull");
		try
		{
			item.setNameMap(mapU);
			fail();
		}
		catch(final MandatoryViolationException e)
		{
			assertEquals(name.getKey(), e.getFeature());
		}
		assertEquals(map(PL, "namePL"), item.getNameMap());
	}

	@Test void testMapSetValueNull()
	{
		final HashMap<Language, String> map = new HashMap<>();
		final Map<Language, String> mapU = Collections.unmodifiableMap(map);
		map.put(PL, "namePL");
		item.setNameMap(mapU);
		assertEquals(map(PL, "namePL"), item.getNameMap());

		map.put(PL, null);
		try
		{
			item.setNameMap(mapU);
			fail();
		}
		catch(final MandatoryViolationException e)
		{
			assertEquals(name.getValue(), e.getFeature());
		}
		assertEquals(map(PL, "namePL"), item.getNameMap());
	}

	@Test void testGetAndCast()
	{
		item.setName(DE, "NAMEde");
		Object o = DE;
		assertEquals("NAMEde", name.getAndCast(item, o));
		o = "DE";
		try
		{
			name.getAndCast(item, o);
			fail();
		}
		catch (final ClassCastException e)
		{
			assertEquals("Cannot cast " + String.class.getName() + " to " + Language.class.getName(), e.getMessage());
		}
		try
		{
			name.getAndCast(item, null);
			fail();
		}
		catch (final NullPointerException e)
		{
			assertEquals("key", e.getMessage());
		}
	}

	@Test void testSetAndCast()
	{
		Object key = DE;
		Object value = "NAMEde";
		name.setAndCast(item, key, value);
		assertEquals("NAMEde", item.getName(DE));
		key = "DE";
		value = "nameDE";
		try
		{
			name.setAndCast(item, key, value);
			fail();
		}
		catch (final ClassCastException e)
		{
			assertEquals("Cannot cast " + String.class.getName() + " to " + Language.class.getName(), e.getMessage());
		}
		key = DE;
		value = 1;
		try
		{
			name.setAndCast(item, key, value);
			fail();
		}
		catch (final ClassCastException e)
		{
			assertEquals("Cannot cast " + Integer.class.getName() + " to " + String.class.getName(), e.getMessage());
			assertEquals("NAMEde", item.getName(DE));
		}
		value = "nameDE";
		try
		{
			name.setAndCast(item, null, value);
			fail();
		}
		catch (final NullPointerException e)
		{
			assertEquals("key", e.getMessage());
		}
		name.setAndCast(item, key, null);
		assertEquals(null, item.getName(DE));
	}
}
