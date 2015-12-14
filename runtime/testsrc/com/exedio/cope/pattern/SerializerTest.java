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

import static com.exedio.cope.RuntimeAssert.assertSerializedSame;
import static com.exedio.cope.pattern.SerializerItem.TYPE;
import static com.exedio.cope.pattern.SerializerItem.integer;
import static com.exedio.cope.pattern.SerializerItem.map;
import static com.exedio.cope.pattern.SerializerItem.mapWildcard;
import static java.lang.Integer.valueOf;

import com.exedio.cope.AbstractRuntimeModelTest;
import com.exedio.cope.Feature;
import com.exedio.cope.Model;
import com.exedio.cope.SchemaInfo;
import com.exedio.cope.SetValue;
import com.exedio.cope.misc.Computed;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

public class SerializerTest extends AbstractRuntimeModelTest
{
	static final Model MODEL = new Model(TYPE);

	static
	{
		MODEL.enableSerialization(SerializerTest.class, "MODEL");
	}

	public SerializerTest()
	{
		super(MODEL);
	}

	SerializerItem item;

	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		item = new SerializerItem();
	}

	@Test public void testSerializer()
	{
		// test model
		assertEquals(Arrays.asList(new Feature[]{
				TYPE.getThis(),
				integer,
				integer.getSource(),
				map,
				map.getSource(),
				mapWildcard,
				mapWildcard.getSource(),
			}), TYPE.getFeatures());
		assertEquals(Arrays.asList(new Feature[]{
				TYPE.getThis(),
				integer,
				integer.getSource(),
				map,
				map.getSource(),
				mapWildcard,
				mapWildcard.getSource(),
			}), TYPE.getDeclaredFeatures());

		assertEquals(TYPE, integer.getSource().getType());
		assertEquals(TYPE, integer.getType());
		assertEquals(TYPE, map.getSource().getType());
		assertEquals(TYPE, map.getType());
		assertEquals("integer-data", integer.getSource().getName());
		assertEquals("integer", integer.getName());
		assertEquals("map-data", map.getSource().getName());
		assertEquals("map", map.getName());

		assertEquals(integer, integer.getSource().getPattern());
		assertEquals(map, map.getSource().getPattern());

		assertEquals(false, integer.isInitial());
		assertEquals(false, integer.isMandatory());
		assertEquals(false, integer.isFinal());
		assertEquals(Integer.class, integer.getInitialType());
		assertContains(integer.getInitialExceptions());
		assertEquals(false, map.isInitial());
		assertEquals(false, map.isMandatory());
		assertEquals(false, map.isFinal());
		assertEquals(Map.class, map.getInitialType());
		assertContains(map.getInitialExceptions());

		assertFalse(integer            .isAnnotationPresent(Computed.class));
		assertFalse(map                .isAnnotationPresent(Computed.class));
		assertTrue (integer.getSource().isAnnotationPresent(Computed.class));
		assertTrue (map    .getSource().isAnnotationPresent(Computed.class));

		assertSerializedSame(integer, 385);
		assertSerializedSame(map    , 381);

		// test persistence
		assertEquals("integer_data", SchemaInfo.getColumnName(integer.getSource()));

		final HashMap<String, String> map1 = new HashMap<>();
		map1.put("key1a", "value1a");
		map1.put("key1b", "value1b");
		final HashMap<String, String> map2 = new HashMap<>();
		map1.put("key2a", "value2a");
		map1.put("key2b", "value2b");

		assertEquals(null, item.getInteger());
		assertEquals(null, item.getMap());

		item.setInteger(11);
		assertEquals(valueOf(11), item.getInteger());

		item.setMap(map1);
		assertEquals(map1, item.getMap());
		assertNotSame(map1, item.getMap());

		item.set(
				integer.map(22),
				map.map(map2)
		);
		assertEquals(valueOf(22), item.getInteger());
		assertEquals(map2, item.getMap());
		assertNotSame(map2, item.getMap());

		item.setInteger(null);
		assertNull(item.getInteger());
		assertEquals(map2, item.getMap());

		item.setMap(null);
		assertNull(item.getInteger());
		assertNull(item.getMap());

		final SerializerItem item2 = new SerializerItem(new SetValue<?>[]{
				integer.map(33),
				map.map(map1),
		});
		assertEquals(valueOf(33), item2.getInteger());
		assertEquals(map1, item2.getMap());
		assertNotSame(map1, item2.getMap());

		final SerializerItem item3 = SerializerItem.TYPE.newItem(
				integer.map(44),
				map.map(map2)
		);
		assertEquals(valueOf(44), item3.getInteger());
		assertEquals(map2, item3.getMap());
		assertNotSame(map2, item3.getMap());
	}

}
