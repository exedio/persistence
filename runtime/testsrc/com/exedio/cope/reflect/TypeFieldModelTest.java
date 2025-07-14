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

package com.exedio.cope.reflect;

import static com.exedio.cope.RuntimeAssert.assertSerializedSame;
import static com.exedio.cope.reflect.TypeField.create;
import static com.exedio.cope.reflect.TypeFieldItem.TYPE;
import static com.exedio.cope.reflect.TypeFieldItem.isFinal;
import static com.exedio.cope.reflect.TypeFieldItem.length;
import static com.exedio.cope.reflect.TypeFieldItem.optional;
import static com.exedio.cope.reflect.TypeFieldItem.renamed;
import static com.exedio.cope.reflect.TypeFieldItem.restricted;
import static com.exedio.cope.reflect.TypeFieldItem.standard;
import static com.exedio.cope.reflect.TypeFieldItem.unique;
import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static com.exedio.cope.tojunit.Assert.list;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.Model;
import com.exedio.cope.misc.Computed;
import org.junit.jupiter.api.Test;

public class TypeFieldModelTest
{
	static final Model MODEL = new Model(TYPE, TypeFieldSubItem.TYPE);

	static
	{
		MODEL.enableSerialization(TypeFieldModelTest.class, "MODEL");
	}

	@Test void testIt()
	{
		assertEqualsUnmodifiable(list(TYPE, TypeFieldSubItem.TYPE), MODEL.getTypes());
		assertEqualsUnmodifiable(list(TYPE, TypeFieldSubItem.TYPE), MODEL.getTypesSortedByHierarchy());
		assertEquals(TypeFieldItem.class, TYPE.getJavaClass());
		assertEquals(true, TYPE.isBound());
		assertEquals(null, TYPE.getPattern());
		assertEqualsUnmodifiable(list(standard.getIdField()), standard.getSourceFeatures());
		assertEqualsUnmodifiable(list(), standard.getSourceTypes());

		assertEqualsUnmodifiable(list(
				TYPE.getThis(),
				standard, standard.getIdField(),
				isFinal,  isFinal .getIdField(),
				optional, optional.getIdField(),
				unique,   unique  .getIdField(), unique.getImplicitUniqueConstraint(),
				length,   length  .getIdField(),
				renamed,  renamed .getIdField(),
				restricted, restricted.getIdField()
			), TYPE.getFeatures());

		assertEquals(TYPE, standard.getType());
		assertEquals("standard", standard.getName());
		assertTrue(standard.getIdField().isAnnotationPresent(Computed.class));

		assertEquals(false, standard             .isFinal());
		assertEquals(false, standard.getIdField().isFinal());
		assertEquals(true,  isFinal              .isFinal());
		assertEquals(true,  isFinal .getIdField().isFinal());

		assertEquals(true,  standard             .isMandatory());
		assertEquals(true,  standard.getIdField().isMandatory());
		assertEquals(false, optional             .isMandatory());
		assertEquals(false, optional.getIdField().isMandatory());

		assertEquals(null, standard.getImplicitUniqueConstraint());
		assertEquals(null, standard.getIdField().getImplicitUniqueConstraint());
		assertEquals(list(unique.getIdField()), unique.getImplicitUniqueConstraint().getFields());

		assertEquals(1,  standard.getIdField().getMinimumLength());
		assertEquals(80, standard.getIdField().getMaximumLength());
		assertEquals(1,  length  .getIdField().getMinimumLength());
		assertEquals(77, length  .getIdField().getMaximumLength());

		assertSerializedSame(standard, 397);

		assertEqualsUnmodifiable(MODEL.getTypes(), standard.getValues());
		assertEqualsUnmodifiable(list(TypeFieldSubItem.TYPE), restricted.getValues());

		assertEquals(
				"(" +
					"TypeFieldItem.standard-id<>'TypeFieldItem' and " +
					"TypeFieldItem.standard-id<>'TypeFieldSubItem'" +
				")",
			standard.isInvalid().toString());
		assertEquals(
			"TypeFieldItem.restricted-id<>'TypeFieldSubItem'",
			restricted.isInvalid().toString());
	}

	@Test void testValueClassNull()
	{
		try
		{
			create(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("valueClass", e.getMessage());
		}
	}
}
