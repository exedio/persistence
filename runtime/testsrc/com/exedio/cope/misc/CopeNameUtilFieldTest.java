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

package com.exedio.cope.misc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.CopeName;
import java.lang.reflect.Field;
import org.junit.jupiter.api.Test;

@SuppressWarnings("unused")
public class CopeNameUtilFieldTest
{
	@Test void testIt() throws NoSuchFieldException
	{
		assertField(null,           "fieldNaked",   "Naked");
		assertField("nameAnno",     "nameAnno",     "Name");
		assertClass(null,           "ClassNaked",   ClassNaked.class);
		assertClass("nameAnno",     "nameAnno",     ClassName .class);
	}

	private static void assertField(
			final String expected,
			final String expectedWithFallback,
			final String name)
		throws NoSuchFieldException
	{
		final Field field = CopeNameUtilFieldTest.class.getDeclaredField("field" + name);
		assertEquals(expected, CopeNameUtil.get(field));
		assertEquals(expectedWithFallback, CopeNameUtil.getAndFallbackToName(field));
	}

	private static void assertClass(
			final String expected,
			final String expectedWithFallback,
			final Class<?> clazz)
	{
		assertEquals(expected, CopeNameUtil.get(clazz));
		assertEquals(expectedWithFallback, CopeNameUtil.getAndFallbackToSimpleName(clazz));
	}

	private static final int fieldNaked = 0;
	private static class ClassNaked { /* empty */ }

	@CopeName("nameAnno") private static final int fieldName = 0;
	@CopeName("nameAnno") private static class ClassName { /* empty */ }


	private enum MyEnum
	{
		normal,
		@CopeName("actual") pure
	}
	@Test void testEnum()
	{
		assertEquals("normal",   CopeNameUtil.getAndFallbackToName(MyEnum.normal));
		assertEquals("actual",   CopeNameUtil.getAndFallbackToName(MyEnum.pure));
	}
}
