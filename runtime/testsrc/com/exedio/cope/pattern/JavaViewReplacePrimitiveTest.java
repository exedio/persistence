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

import static com.exedio.cope.pattern.JavaView.replacePrimitive;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.lang.reflect.Type;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class JavaViewReplacePrimitiveTest
{
	@Test void testWrapper()
	{
		assertSame(Boolean  .class, replacePrimitive(Boolean  .class));
		assertSame(Character.class, replacePrimitive(Character.class));
		assertSame(Byte     .class, replacePrimitive(Byte     .class));
		assertSame(Short    .class, replacePrimitive(Short    .class));
		assertSame(Integer  .class, replacePrimitive(Integer  .class));
		assertSame(Long     .class, replacePrimitive(Long     .class));
		assertSame(Float    .class, replacePrimitive(Float    .class));
		assertSame(Double   .class, replacePrimitive(Double   .class));
	}
	@Test void testWrapperType()
	{
		assertSame(Boolean  .class, replacePrimitive((Type)Boolean  .class));
		assertSame(Character.class, replacePrimitive((Type)Character.class));
		assertSame(Byte     .class, replacePrimitive((Type)Byte     .class));
		assertSame(Short    .class, replacePrimitive((Type)Short    .class));
		assertSame(Integer  .class, replacePrimitive((Type)Integer  .class));
		assertSame(Long     .class, replacePrimitive((Type)Long     .class));
		assertSame(Float    .class, replacePrimitive((Type)Float    .class));
		assertSame(Double   .class, replacePrimitive((Type)Double   .class));
	}
	@Test void testPrimitive()
	{
		assertSame(Boolean  .class, replacePrimitive(boolean.class));
		assertSame(Character.class, replacePrimitive(char   .class));
		assertSame(Byte     .class, replacePrimitive(byte   .class));
		assertSame(Short    .class, replacePrimitive(short  .class));
		assertSame(Integer  .class, replacePrimitive(int    .class));
		assertSame(Long     .class, replacePrimitive(long   .class));
		assertSame(Float    .class, replacePrimitive(float  .class));
		assertSame(Double   .class, replacePrimitive(double .class));
	}
	@Test void testPrimitiveType()
	{
		assertSame(Boolean  .class, replacePrimitive((Type)boolean.class));
		assertSame(Character.class, replacePrimitive((Type)char   .class));
		assertSame(Byte     .class, replacePrimitive((Type)byte   .class));
		assertSame(Short    .class, replacePrimitive((Type)short  .class));
		assertSame(Integer  .class, replacePrimitive((Type)int    .class));
		assertSame(Long     .class, replacePrimitive((Type)long   .class));
		assertSame(Float    .class, replacePrimitive((Type)float  .class));
		assertSame(Double   .class, replacePrimitive((Type)double .class));
	}
	@Test void testOther()
	{
		assertSame(String.class, replacePrimitive(String.class));
		assertSame(Number.class, replacePrimitive(Number.class));
		assertSame(Map   .class, replacePrimitive(Map   .class));
	}
	@Test void testOtherType()
	{
		assertSame(String.class, replacePrimitive((Type)String.class));
		assertSame(Number.class, replacePrimitive((Type)Number.class));
		assertSame(Map   .class, replacePrimitive((Type)Map   .class));
	}
}
