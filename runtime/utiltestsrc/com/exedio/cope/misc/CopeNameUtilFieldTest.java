/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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

import com.exedio.cope.CopeName;
import com.exedio.cope.junit.CopeAssert;
import java.lang.reflect.Field;

@SuppressWarnings({"unused","deprecation"})
public class CopeNameUtilFieldTest extends CopeAssert
{
	public void testIt() throws NoSuchFieldException
	{
		assertField(null,           "fieldNaked",   "Naked");
		assertField("nameAnno",     "nameAnno",     "Name");
		assertField("idAnno",       "idAnno",       "Id");
		assertField("bothAnnoName", "bothAnnoName", "Both");

		assertClass(null,           "ClassNaked",   ClassNaked.class);
		assertClass("nameAnno",     "nameAnno",     ClassName .class);
		assertClass("idAnno",       "idAnno",       ClassId   .class);
		assertClass("bothAnnoName", "bothAnnoName", ClassBoth .class);
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
		throws NoSuchFieldException
	{
		assertEquals(expected, CopeNameUtil.get(clazz));
		assertEquals(expectedWithFallback, CopeNameUtil.getAndFallbackToSimpleName(clazz));
	}

	private static int fieldNaked = 0;
	private static class ClassNaked { /* empty */ }

	@CopeName("nameAnno") private static int fieldName = 0;
	@CopeName("nameAnno") private static class ClassName { /* empty */ }

	@com.exedio.cope.CopeID("idAnno") private static int fieldId = 0;
	@com.exedio.cope.CopeID("idAnno") private static class ClassId { /* empty */ }

	@CopeName("bothAnnoName") @com.exedio.cope.CopeID("bothAnnoID") private static int fieldBoth = 0;
	@CopeName("bothAnnoName") @com.exedio.cope.CopeID("bothAnnoID") private static class ClassBoth { /* empty */ }
}
