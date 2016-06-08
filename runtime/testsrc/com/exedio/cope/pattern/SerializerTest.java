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
import static com.exedio.cope.pattern.SerializerItem.mandatoryString;
import static com.exedio.cope.pattern.SerializerItem.map;
import static com.exedio.cope.pattern.SerializerItem.mapWildcard;
import static com.exedio.cope.tojunit.Assert.assertContains;
import static java.lang.Integer.valueOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.exedio.cope.Feature;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.Model;
import com.exedio.cope.SchemaInfo;
import com.exedio.cope.SetValue;
import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.misc.Computed;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;

public class SerializerTest extends TestWithEnvironment
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

	@Before public final void setUp()
	{
		item = new SerializerItem("mandatory");
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
				mandatoryString,
				mandatoryString.getSource(),
			}), TYPE.getFeatures());
		assertEquals(Arrays.asList(new Feature[]{
				TYPE.getThis(),
				integer,
				integer.getSource(),
				map,
				map.getSource(),
				mapWildcard,
				mapWildcard.getSource(),
				mandatoryString,
				mandatoryString.getSource(),
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

		assertEquals(true, mandatoryString.isInitial());
		assertEquals(true, mandatoryString.isMandatory());
		assertEquals(false, mandatoryString.isFinal());
		assertEquals(String.class, mandatoryString.getInitialType());
		assertContains(MandatoryViolationException.class, mandatoryString.getInitialExceptions());

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
				mandatoryString.map("")
		});
		assertEquals(valueOf(33), item2.getInteger());
		assertEquals(map1, item2.getMap());
		assertNotSame(map1, item2.getMap());
		assertEquals("", item2.getMandatoryString());

		final SerializerItem item3 = SerializerItem.TYPE.newItem(
				integer.map(44),
				map.map(map2),
				mandatoryString.map("x")
		);
		assertEquals(valueOf(44), item3.getInteger());
		assertEquals(map2, item3.getMap());
		assertNotSame(map2, item3.getMap());
		assertEquals("x", item3.getMandatoryString());
	}

	@SuppressFBWarnings("NP_NONNULL_PARAM_VIOLATION")
	@Test public void testMandatoryMustBeSet()
	{
		try
		{
			item.setMandatoryString(null);
			fail();
		}
		catch(final MandatoryViolationException e)
		{
			assertEquals(mandatoryString.getSource(), e.getFeature());
		}
		try
		{
			new SerializerItem((String)null);
			fail();
		}
		catch(final MandatoryViolationException e)
		{
			assertEquals(mandatoryString.getSource(), e.getFeature());
		}
	}
}
