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

package com.exedio.cope.pattern;

import static com.exedio.cope.AbstractRuntimeTest.assertSerializedSame;
import static com.exedio.cope.pattern.EnumSetFieldItem.Language.DE;
import static com.exedio.cope.pattern.EnumSetFieldItem.Language.EN;
import static com.exedio.cope.pattern.EnumSetFieldItem.Language.PL;

import com.exedio.cope.BooleanField;
import com.exedio.cope.Model;
import com.exedio.cope.junit.CopeAssert;

public class EnumSetFieldTest extends CopeAssert
{
	public/*for web.xml*/ static final Model MODEL = new Model(EnumSetFieldItem.TYPE);

	static
	{
		MODEL.enableSerialization(EnumSetFieldTest.class, "MODEL");
	}

	EnumSetFieldItem item, itemX;

	public void testIt()
	{
		assertEquals(item.TYPE, item.activeLanguage.getType());
		assertEquals("activeLanguage", item.activeLanguage.getName());

		assertEquals(EnumSetFieldItem.Language.class, item.activeLanguage.getElementClass());

		assertEquals(BooleanField.class, item.activeLanguage.getField(DE).getClass());
		assertEquals("activeLanguage-DE", item.activeLanguage.getField(DE).getName());
		assertSame(item.TYPE, item.activeLanguage.getField(DE).getType());
		assertEquals(item.activeLanguage, item.activeLanguage.getField(DE).getPattern());
		assertEqualsUnmodifiable(list(item.activeLanguage.getField(DE), item.activeLanguage.getField(EN), item.activeLanguage.getField(PL)), item.activeLanguage.getSourceFeatures());

		assertEqualsUnmodifiable(
				list(
						item.TYPE.getThis(),
						item.activeLanguage,
						item.activeLanguage.getField(DE), item.activeLanguage.getField(EN), item.activeLanguage.getField(PL)),
				item.TYPE.getFeatures());
		assertEqualsUnmodifiable(
				list(
						item.activeLanguage.getField(DE), item.activeLanguage.getField(EN), item.activeLanguage.getField(PL)),
				item.TYPE.getFields());

		assertEqualsUnmodifiable(list(item.TYPE), MODEL.getTypes());
		assertEqualsUnmodifiable(list(item.TYPE), MODEL.getTypesSortedByHierarchy());

		assertSerializedSame(item.activeLanguage, 396);
	}
}
