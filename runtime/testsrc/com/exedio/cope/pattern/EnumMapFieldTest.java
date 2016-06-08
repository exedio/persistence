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

import static com.exedio.cope.pattern.EnumMapFieldItem.name;
import static com.exedio.cope.pattern.EnumMapFieldItem.Language.DE;
import static com.exedio.cope.pattern.EnumMapFieldItem.Language.EN;
import static com.exedio.cope.pattern.EnumMapFieldItem.Language.PL;
import static com.exedio.cope.pattern.EnumMapFieldItem.Language.SUBCLASS;
import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static com.exedio.cope.tojunit.Assert.map;
import static java.lang.Integer.valueOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.pattern.EnumMapFieldItem.Language;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;

public class EnumMapFieldTest extends TestWithEnvironment
{
	public EnumMapFieldTest()
	{
		super(EnumMapFieldModelTest.MODEL);
	}

	EnumMapFieldItem item, itemX;

	@Before public final void setUp()
	{
		item = new EnumMapFieldItem();
		itemX = new EnumMapFieldItem();
	}

	@Test public void testIt()
	{
		assertEquals(null, item.getName(DE));
		assertEquals(null, item.getNameLength(DE));
		assertEquals(null, item.getName(EN));
		assertEquals(null, item.getNameLength(EN));
		assertEquals("defaultDE", item.getDefaults(DE));
		assertEquals(null, item.getDefaults(EN));
		assertEquals(null, item.getDefaults(PL));

		item.setName(DE, "nameDE");
		assertEquals("nameDE", item.getName(DE));
		assertEquals(null, item.getNameLength(DE));
		assertEquals(null, item.getName(EN));
		assertEquals(null, item.getNameLength(EN));
		assertEquals(null, itemX.getName(DE));

		item.setNameLength(DE, 5);
		assertEquals("nameDE", item.getName(DE));
		assertEquals(valueOf(5), item.getNameLength(DE));
		assertEquals(null, item.getName(EN));
		assertEquals(null, item.getNameLength(EN));
		assertEquals(null, itemX.getName(DE));

		item.setNameLength(DE, 6);
		assertEquals("nameDE", item.getName(DE));
		assertEquals(valueOf(6), item.getNameLength(DE));
		assertEquals(null, item.getName(EN));
		assertEquals(null, item.getNameLength(EN));
		assertEquals(null, itemX.getName(DE));

		item.setName(EN, "nameEN");
		assertEquals("nameDE", item.getName(DE));
		assertEquals(valueOf(6), item.getNameLength(DE));
		assertEquals("nameEN", item.getName(EN));
		assertEquals(null, item.getNameLength(EN));
		assertEquals(null, itemX.getName(DE));

		item.setName(DE, null);
		assertEquals(null, item.getName(DE));
		assertEquals(valueOf(6), item.getNameLength(DE));
		assertEquals("nameEN", item.getName(EN));
		assertEquals(null, item.getNameLength(EN));
		assertEquals(null, itemX.getName(DE));

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

	@Test public void testSettable()
	{
		assertEquals(null, item.getName(DE));
		assertEquals(null, item.getName(EN));
		assertEquals(null, item.getName(PL));

		final EnumMap<Language, String> map = new EnumMap<>(Language.class);
		map.put(DE, "nameDE");
		item.set(name.map(map));
		assertEquals("nameDE", item.getName(DE));
		assertEquals(null, item.getName(EN));
		assertEquals(null, item.getName(PL));

		map.remove(DE);
		map.put(PL, "namePL");
		item.set(name.map(map));
		assertEquals(null, item.getName(DE));
		assertEquals(null, item.getName(EN));
		assertEquals("namePL", item.getName(PL));

		map.remove(PL);
		item.set(name.map(map));
		assertEquals(null, item.getName(DE));
		assertEquals(null, item.getName(EN));
		assertEquals(null, item.getName(PL));
	}

	@Test public void testSettableNull()
	{
		try
		{
			item.set(name.map(null));
			fail();
		}
		catch(final MandatoryViolationException e)
		{
			assertEquals(name, e.getFeature());
			assertEquals(item, e.getItem());
		}
	}

	@Test public void testSettableNullValue()
	{
		final EnumMap<Language, String> map = new EnumMap<>(Language.class);
		map.put(DE, null);
		map.put(PL, "namePL");
		item.set(name.map(map));
		assertEquals(null, item.getName(DE));
		assertEquals(null, item.getName(EN));
		assertEquals("namePL", item.getName(PL));
	}

	@Test public void testMapSet()
	{
		final HashMap<Language, String> map = new HashMap<>();
		final Map<Language, String> mapU = Collections.unmodifiableMap(map);
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
		assertEqualsUnmodifiable(map(PL, "namePL", EN, "nameEN"), item.getNameMap());

		map.clear();
		item.setNameMap(mapU);
		assertEqualsUnmodifiable(map(), item.getNameMap());
	}

	@SuppressFBWarnings({"NP_NULL_PARAM_DEREF_ALL_TARGETS_DANGEROUS", "NP_NONNULL_PARAM_VIOLATION"})
	@Test public void testMapSetNull()
	{
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
	}

	@Test public void testMapSetKeyNull()
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
			assertEquals(name, e.getFeature());
			assertEquals(item, e.getItem());
		}
		assertEquals(map(PL, "namePL"), item.getNameMap());
	}

	@Test public void testMapSetValueNull()
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
			assertEquals(name, e.getFeature());
			assertEquals(item, e.getItem());
		}
		assertEquals(map(PL, "namePL"), item.getNameMap());
	}

	@Test public void testSubClass()
	{
		assertEquals(null, item.getName(SUBCLASS));

		item.setName(SUBCLASS, "withsubclass");
		assertEquals("withsubclass", item.getName(SUBCLASS));
	}
}
