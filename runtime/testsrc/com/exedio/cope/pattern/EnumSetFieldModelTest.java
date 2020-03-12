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
import static com.exedio.cope.pattern.EnumSetFieldItem.Language.DE;
import static com.exedio.cope.pattern.EnumSetFieldItem.Language.EN;
import static com.exedio.cope.pattern.EnumSetFieldItem.Language.PL;
import static com.exedio.cope.pattern.EnumSetFieldItem.Language.SUBCLASS;
import static com.exedio.cope.pattern.EnumSetFieldItem.TYPE;
import static com.exedio.cope.pattern.EnumSetFieldItem.activeLanguage;
import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static com.exedio.cope.tojunit.Assert.list;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.exedio.cope.BooleanField;
import com.exedio.cope.Model;
import com.exedio.cope.pattern.EnumSetFieldItem.Language;
import org.junit.jupiter.api.Test;

public class EnumSetFieldModelTest
{
	public/*for web.xml*/ static final Model MODEL = new Model(TYPE);

	static
	{
		MODEL.enableSerialization(EnumSetFieldModelTest.class, "MODEL");
	}

	@Test void testIt()
	{
		assertEquals(TYPE, activeLanguage.getType());
		assertEquals("activeLanguage", activeLanguage.getName());

		assertEquals(Language.class, activeLanguage.getElementClass());
		assertEquals(false, activeLanguage.isFinal());
		assertEquals(false, activeLanguage.getField(DE).isFinal());
		assertEquals(false, activeLanguage.getField(EN).isFinal());
		assertEquals(false, activeLanguage.getField(PL).isFinal());

		assertEquals(BooleanField.class, activeLanguage.getField(DE).getClass());
		assertEquals("activeLanguage-DE", activeLanguage.getField(DE).getName());
		assertSame(TYPE, activeLanguage.getField(DE).getType());
		assertEquals(activeLanguage, activeLanguage.getField(DE).getPattern());
		assertEqualsUnmodifiable(list(activeLanguage.getField(DE), activeLanguage.getField(EN), activeLanguage.getField(PL), activeLanguage.getField(SUBCLASS)), activeLanguage.getSourceFeatures());

		assertEqualsUnmodifiable(
				list(
						TYPE.getThis(),
						activeLanguage,
						activeLanguage.getField(DE), activeLanguage.getField(EN), activeLanguage.getField(PL), activeLanguage.getField(SUBCLASS)),
				TYPE.getFeatures());
		assertEqualsUnmodifiable(
				list(
						activeLanguage.getField(DE), activeLanguage.getField(EN), activeLanguage.getField(PL), activeLanguage.getField(SUBCLASS)),
				TYPE.getFields());

		assertEqualsUnmodifiable(list(TYPE), MODEL.getTypes());
		assertEqualsUnmodifiable(list(TYPE), MODEL.getTypesSortedByHierarchy());

		assertSerializedSame(activeLanguage, 401);
	}

	@Test void testContains()
	{
		assertEquals(
				"EnumSetFieldItem.activeLanguage-DE='true'",
				activeLanguage.contains(DE).toString());
	}

	@Test void testInitialType()
	{
		assertEquals("java.util.EnumSet<" + Language.class.getName() + ">", activeLanguage.getInitialType().toString());
	}
}
