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
import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static com.exedio.cope.tojunit.Assert.list;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.FunctionField;
import com.exedio.cope.Model;
import com.exedio.cope.StringField;
import com.exedio.cope.UniqueConstraint;
import org.junit.jupiter.api.Test;

public class ImporterModelTest
{
	static final Model model = new Model(TYPE);

	static
	{
		model.enableSerialization(ImporterModelTest.class, "model");
	}

	@Test void testIt()
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
		assertSame(code.getImplicitUniqueConstraint(), byCode.getUniqueConstraint());
		assertEquals(list(), byCode.getSourceFeatures());
		assertEquals(list(), byCode.getSourceTypes());
		assertSerializedSame(byCode, 393);

		try
		{
			Importer.create((FunctionField<?>) null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("key", e.getMessage());
		}
		try
		{
			Importer.create((UniqueConstraint) null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("constraint", e.getMessage());
		}
		try
		{
			Importer.create(new StringField().unique());
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("key 0 must be final", e.getMessage());
		}
		try
		{
			Importer.create(new StringField().unique().toFinal().optional());
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("key 0 must be mandatory", e.getMessage());
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
