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

import static com.exedio.cope.pattern.JavaView.create;
import static com.exedio.cope.pattern.JavaView.createDate;
import static com.exedio.cope.pattern.JavaView.createInt;
import static com.exedio.cope.pattern.JavaView.createList;
import static com.exedio.cope.pattern.JavaView.createLong;
import static com.exedio.cope.pattern.JavaView.createMap;
import static com.exedio.cope.pattern.JavaView.createSet;
import static com.exedio.cope.pattern.JavaView.createString;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Date;
import org.junit.jupiter.api.Test;

public class JavaViewFunctionCreateTest
{
	@Test void testInt()
	{
		final JavaView jv = createInt(i -> null);
		assertEquals(Integer.class, jv.getValueType());
		assertEquals(Integer.class, jv.getValueGenericType());
	}
	@Test void testIntTargetNull()
	{
		assertFails(
				() -> createInt(null),
				NullPointerException.class,
				"target");
	}
	@Test void testLong()
	{
		final JavaView jv = createLong(i -> null);
		assertEquals(Long.class, jv.getValueType());
		assertEquals(Long.class, jv.getValueGenericType());
	}
	@Test void testLongTargetNull()
	{
		assertFails(
				() -> createLong(null),
				NullPointerException.class,
				"target");
	}
	@Test void testString()
	{
		final JavaView jv = createString(i -> null);
		assertEquals(String.class, jv.getValueType());
		assertEquals(String.class, jv.getValueGenericType());
	}
	@Test void testStringTargetNull()
	{
		assertFails(
				() -> createString(null),
				NullPointerException.class,
				"target");
	}
	@Test void testDate()
	{
		final JavaView jv = createDate(i -> null);
		assertEquals(Date.class, jv.getValueType());
		assertEquals(Date.class, jv.getValueGenericType());
	}
	@Test void testDateTargetNull()
	{
		assertFails(
				() -> createDate(null),
				NullPointerException.class,
				"target");
	}
	@Test void testValueTypeNull()
	{
		assertFails(
				() -> create(null, null),
				NullPointerException.class,
				"valueType");
	}
	@Test void testValueTypePrimitive()
	{
		assertFails(
				() -> create(double.class, null),
				IllegalArgumentException.class,
				"valueType must not be primitive, but was double");
	}
	@Test void testValueTypePrimitiveVoid()
	{
		assertFails(
				() -> create(void.class, null),
				IllegalArgumentException.class,
				"valueType must not be primitive, but was void");
	}
	@Test void testValueTypeVoid()
	{
		assertFails(
				() -> create(Void.class, null),
				IllegalArgumentException.class,
				"valueType must not be class java.lang.Void");
	}
	@Test void testTargetNull()
	{
		assertFails(
				() -> create(String.class, null),
				NullPointerException.class,
				"target");
	}
	@Test void testListElementTypeNull()
	{
		assertFails(
				() -> createList(null, null),
				NullPointerException.class,
				"actualTypeArguments[0]");
	}
	@Test void testListTargetNull()
	{
		assertFails(
				() -> createList(Float.class, null),
				NullPointerException.class,
				"target");
	}
	@Test void testSetElementTypeNull()
	{
		assertFails(
				() -> createSet(null, null),
				NullPointerException.class,
				"actualTypeArguments[0]");
	}
	@Test void testSetTargetNull()
	{
		assertFails(
				() -> createSet(Float.class, null),
				NullPointerException.class,
				"target");
	}
	@Test void testMapKeyTypeNull()
	{
		assertFails(
				() -> createMap(null, null, null),
				NullPointerException.class,
				"actualTypeArguments[0]");
	}
	@Test void testMapValueTypeNull()
	{
		assertFails(
				() -> createMap(Float.class, null, null),
				NullPointerException.class,
				"actualTypeArguments[1]");
	}
	@Test void testMapTargetNull()
	{
		assertFails(
				() -> createMap(Float.class, Double.class, null),
				NullPointerException.class,
				"target");
	}
}
