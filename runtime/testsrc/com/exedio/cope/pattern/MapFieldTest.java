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

import static com.exedio.cope.pattern.MapFieldItem.TYPE;
import static com.exedio.cope.pattern.MapFieldItem.name;
import static com.exedio.cope.pattern.MapFieldItem.Language.DE;
import static com.exedio.cope.pattern.MapFieldItem.Language.EN;
import static com.exedio.cope.pattern.MapFieldItem.Language.PL;
import static java.lang.Integer.valueOf;

import com.exedio.cope.AbstractRuntimeTest;
import com.exedio.cope.Query;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MapFieldTest extends AbstractRuntimeTest
{
	public MapFieldTest()
	{
		super(MapFieldModelTest.MODEL);
	}

	MapFieldItem item, itemX;

	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		item = deleteOnTearDown(new MapFieldItem());
		itemX = deleteOnTearDown(new MapFieldItem());
	}

	public void testIt()
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
			name.join(q, DE);
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
			assertEquals("cannot search uniquely for null on MapFieldItem-name.uniqueConstraint for MapFieldItem-name.key", e.getMessage());
		}
		try
		{
			item.setName(null, "hallo");
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("cannot search uniquely for null on MapFieldItem-name.uniqueConstraint for MapFieldItem-name.key", e.getMessage());
		}
	}

	public void testMapSet()
	{
		final HashMap<MapFieldItem.Language, String> map = new HashMap<>();
		final Map<MapFieldItem.Language, String> mapU = Collections.unmodifiableMap(map);
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
}
