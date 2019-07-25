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
import static com.exedio.cope.pattern.JavaViewItem.TYPE;
import static com.exedio.cope.pattern.JavaViewItem.map;
import static com.exedio.cope.pattern.JavaViewItem.number;
import static com.exedio.cope.pattern.JavaViewItem.numberPrimitive;
import static com.exedio.cope.pattern.JavaViewItem.numberString;
import static com.exedio.cope.tojunit.EqualsAssert.assertEqualBits;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.exedio.cope.Feature;
import com.exedio.cope.Model;
import com.exedio.cope.TestWithEnvironment;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class JavaViewTest extends TestWithEnvironment
{
	private static final Model MODEL = new Model(TYPE);

	static
	{
		MODEL.enableSerialization(JavaViewTest.class, "MODEL");
	}

	private static final Double d2 = Double.valueOf(2.25d);

	public JavaViewTest()
	{
		super(MODEL);
	}

	JavaViewItem item;

	@BeforeEach final void setUp()
	{
		item = new JavaViewItem();
	}

	@Test void testNumber()
	{
		assertEquals(asList(new Feature[]{
				TYPE.getThis(),
				numberString,
				number,
				numberPrimitive,
				map,
			}), TYPE.getDeclaredFeatures());
		assertEquals(TYPE.getDeclaredFeatures(), TYPE.getFeatures());

		assertEquals(TYPE, number.getType());
		assertEquals("number", number.getName());
		assertEquals(null, numberString.getPattern());
		assertEquals(Double.class, number.getValueType());
		assertEquals(Double.class, number.getValueGenericType());
		assertEquals(Double.class, numberPrimitive.getValueType());
		assertEquals(Double.class, numberPrimitive.getValueGenericType());
		assertEquals(HashMap.class, map.getValueType());
		{
			final ParameterizedType mapType = (ParameterizedType)map.getValueGenericType();
			assertEquals(HashMap.class, mapType.getRawType());
			assertEquals(asList(Integer.class, Double.class), asList(mapType.getActualTypeArguments()));
		}

		assertSerializedSame(number         , 380);
		assertSerializedSame(numberPrimitive, 389);

		assertNull(item.getNumberString());
		assertNull(item.getNumber());
		assertNull(number.get(item));

		item.setNumberString("2.25");
		assertEquals("2.25", item.getNumberString());
		assertEquals(d2, item.getNumber());
		assertEquals(d2, number.get(item));
		assertEqualBits(2.25, item.getNumberPrimitive());
		assertEquals(2.25, numberPrimitive.get(item));

		item.setNumberString(null);
		assertNull(item.getNumberString());
		assertNull(item.getNumber());
		assertNull(number.get(item));
	}
}
