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
import static java.lang.Integer.valueOf;

import com.exedio.cope.AbstractRuntimeModelTest;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.pattern.EnumMapFieldItem.Language;
import java.util.EnumMap;

public class EnumMapFieldTest extends AbstractRuntimeModelTest
{
	public EnumMapFieldTest()
	{
		super(EnumMapFieldModelTest.MODEL);
	}

	EnumMapFieldItem item, itemX;

	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		item = new EnumMapFieldItem();
		itemX = new EnumMapFieldItem();
	}

	public void testIt()
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

	public void testSettable()
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

	public void testSettableNull()
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

	public void testSettableNullValue()
	{
		final EnumMap<Language, String> map = new EnumMap<>(Language.class);
		map.put(DE, null);
		map.put(PL, "namePL");
		item.set(name.map(map));
		assertEquals(null, item.getName(DE));
		assertEquals(null, item.getName(EN));
		assertEquals("namePL", item.getName(PL));
	}

	public void testGetSetMap()
	{
		item.setNameMap(map(DE, "NAMEde", EN, "NAMEen", PL, "NAMEpl", SUBCLASS, "NAMEsubclass"));
		assertEquals("NAMEde", item.getName(DE));
		assertEquals("NAMEen", item.getName(EN));
		assertEquals("NAMEpl", item.getName(PL));
		assertEquals("NAMEsubclass", item.getName(SUBCLASS));
		assertEquals(map(DE, "NAMEde", EN, "NAMEen", PL, "NAMEpl", SUBCLASS, "NAMEsubclass"), item.getNameMap());

		item.setNameMap(map(DE, "nameDE", EN, "nameEN", PL, null));
		assertEquals("nameDE", item.getName(DE));
		assertEquals("nameEN", item.getName(EN));
		assertEquals(null, item.getName(PL));
		assertEquals(null, item.getName(SUBCLASS));
		assertEqualsUnmodifiable(map(DE, "nameDE", EN, "nameEN", PL, null, SUBCLASS, null), item.getNameMap());
	}

	public void testSubClass()
	{
		assertEquals(null, item.getName(SUBCLASS));

		item.setName(SUBCLASS, "withsubclass");
		assertEquals("withsubclass", item.getName(SUBCLASS));
	}
}
