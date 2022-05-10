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
import static com.exedio.cope.reflect.FeatureField.create;
import static com.exedio.cope.reflect.FeatureFieldItem.TYPE;
import static com.exedio.cope.reflect.FeatureFieldItem.integer1;
import static com.exedio.cope.reflect.FeatureFieldItem.integer2;
import static com.exedio.cope.reflect.FeatureFieldItem.integer3;
import static com.exedio.cope.reflect.FeatureFieldItem.isFinal;
import static com.exedio.cope.reflect.FeatureFieldItem.length;
import static com.exedio.cope.reflect.FeatureFieldItem.optional;
import static com.exedio.cope.reflect.FeatureFieldItem.renamed;
import static com.exedio.cope.reflect.FeatureFieldItem.restricted;
import static com.exedio.cope.reflect.FeatureFieldItem.standard;
import static com.exedio.cope.reflect.FeatureFieldItem.string1;
import static com.exedio.cope.reflect.FeatureFieldItem.string2;
import static com.exedio.cope.reflect.FeatureFieldItem.string3;
import static com.exedio.cope.reflect.FeatureFieldItem.unique;
import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static com.exedio.cope.tojunit.Assert.list;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.Model;
import com.exedio.cope.misc.Computed;
import org.junit.jupiter.api.Test;

public class FeatureFieldModelTest
{
	static final Model MODEL = new Model(TYPE);

	static
	{
		MODEL.enableSerialization(FeatureFieldModelTest.class, "MODEL");
	}

	@Test void testIt()
	{
		assertEqualsUnmodifiable(list(TYPE), MODEL.getTypes());
		assertEqualsUnmodifiable(list(TYPE), MODEL.getTypesSortedByHierarchy());
		assertEquals(FeatureFieldItem.class, TYPE.getJavaClass());
		assertEquals(true, TYPE.isBound());
		assertEquals(null, TYPE.getPattern());
		assertEqualsUnmodifiable(list(standard.getIdField()), standard.getSourceFeatures());
		assertEqualsUnmodifiable(list(), standard.getSourceTypes());

		assertEqualsUnmodifiable(list(
				TYPE.getThis(),
				integer1, integer2, integer3,
				string1,  string2,  string3,
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
		assertEquals(66, length  .getIdField().getMaximumLength());

		assertSerializedSame(standard, 395);

		assertEqualsUnmodifiable(TYPE.getFeatures(), standard.getValues());
		assertEqualsUnmodifiable(list(
				string1, string2, string3,
				standard.getIdField(),
				isFinal.getIdField(),
				optional.getIdField(),
				unique.getIdField(),
				length.getIdField(),
				renamed.getIdField(),
				restricted.getIdField()), restricted.getValues());

		assertEquals(
				"(" +
					"FeatureFieldItem.standard-id<>'FeatureFieldItem.this' AND " +
					"FeatureFieldItem.standard-id<>'FeatureFieldItem.integer1' AND " +
					"FeatureFieldItem.standard-id<>'FeatureFieldItem.integer2' AND " +
					"FeatureFieldItem.standard-id<>'FeatureFieldItem.integer3' AND " +
					"FeatureFieldItem.standard-id<>'FeatureFieldItem.string1' AND " +
					"FeatureFieldItem.standard-id<>'FeatureFieldItem.string2' AND " +
					"FeatureFieldItem.standard-id<>'FeatureFieldItem.string3' AND " +
					"FeatureFieldItem.standard-id<>'FeatureFieldItem.standard' AND " +
					"FeatureFieldItem.standard-id<>'FeatureFieldItem.standard-id' AND " +
					"FeatureFieldItem.standard-id<>'FeatureFieldItem.isFinal' AND " +
					"FeatureFieldItem.standard-id<>'FeatureFieldItem.isFinal-id' AND " +
					"FeatureFieldItem.standard-id<>'FeatureFieldItem.optional' AND " +
					"FeatureFieldItem.standard-id<>'FeatureFieldItem.optional-id' AND " +
					"FeatureFieldItem.standard-id<>'FeatureFieldItem.unique' AND " +
					"FeatureFieldItem.standard-id<>'FeatureFieldItem.unique-id' AND " +
					"FeatureFieldItem.standard-id<>'FeatureFieldItem.unique-idImplicitUnique' AND " +
					"FeatureFieldItem.standard-id<>'FeatureFieldItem.length' AND " +
					"FeatureFieldItem.standard-id<>'FeatureFieldItem.length-id' AND " +
					"FeatureFieldItem.standard-id<>'FeatureFieldItem.renamed' AND " +
					"FeatureFieldItem.standard-id<>'FeatureFieldItem.renamed-id' AND " +
					"FeatureFieldItem.standard-id<>'FeatureFieldItem.restricted' AND " +
					"FeatureFieldItem.standard-id<>'FeatureFieldItem.restricted-id'" +
				")",
			standard.isInvalid().toString());
		assertEquals(
				"(" +
					"FeatureFieldItem.restricted-id<>'FeatureFieldItem.string1' AND " +
					"FeatureFieldItem.restricted-id<>'FeatureFieldItem.string2' AND " +
					"FeatureFieldItem.restricted-id<>'FeatureFieldItem.string3' AND " +
					"FeatureFieldItem.restricted-id<>'FeatureFieldItem.standard-id' AND " +
					"FeatureFieldItem.restricted-id<>'FeatureFieldItem.isFinal-id' AND " +
					"FeatureFieldItem.restricted-id<>'FeatureFieldItem.optional-id' AND " +
					"FeatureFieldItem.restricted-id<>'FeatureFieldItem.unique-id' AND " +
					"FeatureFieldItem.restricted-id<>'FeatureFieldItem.length-id' AND " +
					"FeatureFieldItem.restricted-id<>'FeatureFieldItem.renamed-id' AND " +
					"FeatureFieldItem.restricted-id<>'FeatureFieldItem.restricted-id'" +
				")",
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
