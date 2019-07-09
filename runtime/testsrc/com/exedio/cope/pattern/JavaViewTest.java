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
import static com.exedio.cope.pattern.JavaViewItem.n;
import static com.exedio.cope.pattern.JavaViewItem.number;
import static com.exedio.cope.pattern.JavaViewItem.numberPrimitive;
import static com.exedio.cope.pattern.JavaViewItem.numberString;
import static com.exedio.cope.pattern.JavaViewItem.privat;
import static com.exedio.cope.tojunit.EqualsAssert.assertEqualBits;
import static java.util.Arrays.asList;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.exedio.cope.Feature;
import com.exedio.cope.Model;
import com.exedio.cope.TestWithEnvironment;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class JavaViewTest extends TestWithEnvironment
{
	private static final Model MODEL = new Model(TYPE, JavaViewItem2.TYPE);

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
	JavaViewItem2 item2;

	@BeforeEach final void setUp()
	{
		item = new JavaViewItem();
		item2 = new JavaViewItem2();
	}

	@Test void testNumber()
	{
		assertEquals(asList(new Feature[]{
				TYPE.getThis(),
				numberString,
				number,
				numberPrimitive,
				n,
				privat,
				map,
			}), TYPE.getDeclaredFeatures());
		assertEquals(TYPE.getDeclaredFeatures(), TYPE.getFeatures());

		assertEquals(TYPE, number.getType());
		assertEquals("number", number.getName());
		assertEquals(null, numberString.getPattern());
		assertEquals(Double.class, number.getValueType());
		assertEquals(Double.class, number.getValueGenericType());
		assertEquals(double.class, numberPrimitive.getValueType());
		assertEquals(double.class, numberPrimitive.getValueGenericType());
		assertEquals(String.class, privat.getValueType());
		assertEquals(String.class, privat.getValueGenericType());
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
		try
		{
			item.getNumberPrimitive();
			fail();
		}
		catch(final UnsupportedOperationException e)
		{
			assertEquals("numberPrimitive", e.getMessage());
		}
		try
		{
			numberPrimitive.get(item);
			fail();
		}
		catch(final RuntimeException e)
		{
			assertEquals("JavaViewItem.numberPrimitive", e.getMessage());
			final Throwable cause = e.getCause();
			assertEquals(InvocationTargetException.class, cause.getClass());
			final Throwable cause2 = cause.getCause();
			assertEquals(UnsupportedOperationException.class, cause2.getClass());
			assertEquals("numberPrimitive", cause2.getMessage());
		}

		item.setNumberString("2.25");
		assertEquals("2.25", item.getNumberString());
		assertEquals(d2, item.getNumber());
		assertEquals(d2, number.get(item));
		assertEqualBits(2.25, item.getNumberPrimitive());
		assertEquals(2.25, numberPrimitive.get(item));
		assertEquals("N2.25", item.getN());
		assertEquals("N2.25", n.get(item));
		assertEquals("Privat2.25", privat.get(item));

		item.setNumberString(null);
		assertNull(item.getNumberString());
		assertNull(item.getNumber());
		assertNull(number.get(item));
		try
		{
			item.getNumberPrimitive();
			fail();
		}
		catch(final UnsupportedOperationException e)
		{
			assertEquals("numberPrimitive", e.getMessage());
		}
		try
		{
			numberPrimitive.get(item);
			fail();
		}
		catch(final RuntimeException e)
		{
			assertEquals("JavaViewItem.numberPrimitive", e.getMessage());
			final Throwable cause = e.getCause();
			assertEquals(InvocationTargetException.class, cause.getClass());
			final Throwable cause2 = cause.getCause();
			assertEquals(UnsupportedOperationException.class, cause2.getClass());
			assertEquals("numberPrimitive", cause2.getMessage());
		}

		try
		{
			number.get(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals(null, e.getMessage());
		}
		try
		{
			number.get(item2);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("object is not an instance of declaring class", e.getMessage());
		}
	}
}
