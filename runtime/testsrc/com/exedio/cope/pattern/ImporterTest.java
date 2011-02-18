/*
 * Copyright (C) 2004-2011  exedio GmbH (www.exedio.com)
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

import static com.exedio.cope.pattern.ImporterItem.TYPE;
import static com.exedio.cope.pattern.ImporterItem.byCode;
import static com.exedio.cope.pattern.ImporterItem.code;
import static com.exedio.cope.pattern.ImporterItem.description;
import static com.exedio.cope.pattern.ImporterItem.description2;
import static com.exedio.cope.pattern.ImporterItem.importByCode;

import java.util.ArrayList;

import com.exedio.cope.AbstractRuntimeTest;
import com.exedio.cope.Model;
import com.exedio.cope.SetValue;
import com.exedio.cope.StringField;

public class ImporterTest extends AbstractRuntimeTest
{
	static final Model MODEL = new Model(ImporterItem.TYPE);

	static
	{
		MODEL.enableSerialization(ImporterTest.class, "MODEL");
	}

	public ImporterTest()
	{
		super(MODEL);
	}

	public void testIt()
	{
		// test model
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
		assertSerializedSame(byCode, 380);

		try
		{
			Importer.newImporter(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("key", e.getMessage());
		}
		try
		{
			Importer.newImporter(new StringField());
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("key must be final", e.getMessage());
		}
		try
		{
			Importer.newImporter(new StringField().toFinal().optional());
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("key must be mandatory", e.getMessage());
		}
		try
		{
			Importer.newImporter(new StringField().toFinal());
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("key must be unique", e.getMessage());
		}

		// test persistence
		assertEquals(list(), TYPE.search(null, TYPE.getThis(), true));

		final ImporterItem itemA = deleteOnTearDown(
			importByCode("codeA", description.map("descA"), description2.map("desc2A")));
		assertEquals(list(itemA), TYPE.search(null, TYPE.getThis(), true));
		assertEquals("codeA",  itemA.getCode());
		assertEquals("descA",  itemA.getDescription());
		assertEquals("desc2A", itemA.getDescription2());

		final ImporterItem itemB = deleteOnTearDown(
			importByCode("codeB", description.map("descB"), description2.map("desc2B")));
		assertEquals(list(itemA, itemB), TYPE.search(null, TYPE.getThis(), true));
		assertEquals("codeA",  itemA.getCode());
		assertEquals("descA",  itemA.getDescription());
		assertEquals("desc2A", itemA.getDescription2());
		assertEquals("codeB",  itemB.getCode());
		assertEquals("descB",  itemB.getDescription());
		assertEquals("desc2B", itemB.getDescription2());

		assertEquals(itemA,
			importByCode("codeA", description.map("descAx"), description2.map("desc2Ax")));
		assertEquals(list(itemA, itemB), TYPE.search(null, TYPE.getThis(), true));
		assertEquals("codeA", itemA.getCode());
		assertEquals("descAx", itemA.getDescription());
		assertEquals("desc2Ax",itemA.getDescription2());
		assertEquals("codeB",  itemB.getCode());
		assertEquals("descB",  itemB.getDescription());
		assertEquals("desc2B", itemB.getDescription2());

		final ArrayList<SetValue> list = new ArrayList<SetValue>();
		list.add(description.map("descBl"));
		list.add(description2.map("desc2Bl"));
		assertEquals(itemB, importByCode("codeB", list));
		assertEquals(list(itemA, itemB), TYPE.search(null, TYPE.getThis(), true));
		assertEquals("codeA", itemA.getCode());
		assertEquals("descAx", itemA.getDescription());
		assertEquals("desc2Ax",itemA.getDescription2());
		assertEquals("codeB",  itemB.getCode());
		assertEquals("descBl",  itemB.getDescription());
		assertEquals("desc2Bl", itemB.getDescription2());
	}
}
