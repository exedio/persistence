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
import static com.exedio.cope.pattern.ImporterItem.TYPE;
import static com.exedio.cope.pattern.ImporterItem.byCode;
import static com.exedio.cope.pattern.ImporterItem.code;
import static com.exedio.cope.pattern.ImporterItem.description;
import static com.exedio.cope.pattern.ImporterItem.description2;

import com.exedio.cope.Model;
import com.exedio.cope.StringField;
import com.exedio.cope.junit.CopeAssert;
import org.junit.Test;

public class ImporterModelTest extends CopeAssert
{
	static final Model model = new Model(ImporterItem.TYPE);

	static
	{
		model.enableSerialization(ImporterModelTest.class, "model");
	}

	@Test public void testIt()
	{
		assertEqualsUnmodifiable(list(TYPE), model.getTypes());
		assertEqualsUnmodifiable(list(TYPE), model.getTypesSortedByHierarchy());
		assertEquals(ImporterItem.class, TYPE.getJavaClass());
		assertEquals(true, TYPE.isBound());
		assertEquals(null, TYPE.getPattern());

		assertEqualsUnmodifiable(list(
				TYPE.getThis(),
				code,
				code.getImplicitUniqueConstraint(),
				byCode,
				description,
				description2
			), TYPE.getFeatures());

		assertEquals(TYPE, byCode.getType());
		assertEquals("byCode", byCode.getName());
		assertSame(code, byCode.getKey());
		assertEquals(list(), byCode.getSourceFeatures());
		assertEquals(list(), byCode.getSourceTypes());
		assertSerializedSame(byCode, 385);

		try
		{
			Importer.create(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("key", e.getMessage());
		}
		try
		{
			Importer.create(new StringField());
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("key must be final", e.getMessage());
		}
		try
		{
			Importer.create(new StringField().toFinal().optional());
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("key must be mandatory", e.getMessage());
		}
		try
		{
			Importer.create(new StringField().toFinal());
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("key must be unique", e.getMessage());
		}
	}
}
