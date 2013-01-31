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
import static com.exedio.cope.pattern.EnumMapFieldItem.TYPE;
import static com.exedio.cope.pattern.EnumMapFieldItem.defaults;
import static com.exedio.cope.pattern.EnumMapFieldItem.name;
import static com.exedio.cope.pattern.EnumMapFieldItem.nameLength;

import com.exedio.cope.Model;
import com.exedio.cope.junit.CopeAssert;

public class EnumMapFieldTest extends CopeAssert
{
	public/*for web.xml*/ static final Model MODEL = new Model(EnumMapFieldItem.TYPE);

	static
	{
		MODEL.enableSerialization(EnumMapFieldTest.class, "MODEL");
	}

	private static final EnumMapFieldItem.Language DE = EnumMapFieldItem.Language.DE;
	private static final EnumMapFieldItem.Language EN = EnumMapFieldItem.Language.EN;
	private static final EnumMapFieldItem.Language PL = EnumMapFieldItem.Language.PL;

	public void testIt()
	{
		assertEquals(TYPE, name.getType());
		assertEquals("name", name.getName());

		assertEquals(EnumMapFieldItem.Language.class, name.getKeyClass());

		assertEquals(String.class, name.getField(DE).getValueClass());
		assertEquals("name-DE", name.getField(DE).getName());
		assertSame(TYPE, name.getField(DE).getType());
		assertEquals(name, name.getField(DE).getPattern());
		assertEquals(null, name.getField(DE).getDefaultConstant());
		assertEqualsUnmodifiable(list(name.getField(DE), name.getField(EN), name.getField(PL)), name.getSourceFeatures());

		assertEquals("defaultDE", defaults.getField(DE).getDefaultConstant());
		assertEquals(null, defaults.getField(EN).getDefaultConstant());
		assertEquals(null, defaults.getField(PL).getDefaultConstant());

		assertEqualsUnmodifiable(
				list(
						TYPE.getThis(),
						name, name.getField(DE), name.getField(EN), name.getField(PL),
						nameLength, nameLength.getField(DE), nameLength.getField(EN), nameLength.getField(PL),
						defaults, defaults.getField(DE), defaults.getField(EN), defaults.getField(PL)),
				TYPE.getFeatures());
		assertEqualsUnmodifiable(
				list(
						name.getField(DE), name.getField(EN), name.getField(PL),
						nameLength.getField(DE), nameLength.getField(EN), nameLength.getField(PL),
						defaults.getField(DE), defaults.getField(EN), defaults.getField(PL)),
				TYPE.getFields());

		assertEqualsUnmodifiable(list(TYPE), MODEL.getTypes());
		assertEqualsUnmodifiable(list(TYPE), MODEL.getTypesSortedByHierarchy());

		assertSerializedSame(name      , 386);
		assertSerializedSame(nameLength, 392);
	}

	@SuppressWarnings({"unchecked", "rawtypes"}) // OK: test bad API usage
	public void testUnchecked()
	{
		try
		{
			((EnumMapField)name).get((EnumMapFieldItem)null, X.A);
			fail();
		}
		catch(final ClassCastException e)
		{
			assertEquals("expected a com.exedio.cope.pattern.EnumMapFieldItem$Language, but was a com.exedio.cope.pattern.EnumMapFieldTest$X", e.getMessage());
		}
		try
		{
			((EnumMapField)name).set((EnumMapFieldItem)null, X.A, "hallo");
			fail();
		}
		catch(final ClassCastException e)
		{
			assertEquals("expected a com.exedio.cope.pattern.EnumMapFieldItem$Language, but was a com.exedio.cope.pattern.EnumMapFieldTest$X", e.getMessage());
		}
	}

	enum X
	{
		A, B, C;
	}
}
