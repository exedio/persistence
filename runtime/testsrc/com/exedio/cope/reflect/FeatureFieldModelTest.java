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
import static com.exedio.cope.reflect.FeatureFieldItem.TYPE;
import static com.exedio.cope.reflect.FeatureFieldItem.feature;
import static com.exedio.cope.reflect.FeatureFieldItem.featureFinal;
import static com.exedio.cope.reflect.FeatureFieldItem.featureOptional;
import static com.exedio.cope.reflect.FeatureFieldItem.featureRenamed;
import static com.exedio.cope.reflect.FeatureFieldItem.featureUnique;
import static com.exedio.cope.reflect.FeatureFieldItem.integer1;
import static com.exedio.cope.reflect.FeatureFieldItem.integer2;
import static com.exedio.cope.reflect.FeatureFieldItem.integer3;
import static com.exedio.cope.reflect.FeatureFieldItem.string;
import static com.exedio.cope.reflect.FeatureFieldItem.string1;
import static com.exedio.cope.reflect.FeatureFieldItem.string2;
import static com.exedio.cope.reflect.FeatureFieldItem.string3;
import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static com.exedio.cope.tojunit.Assert.list;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.exedio.cope.Model;
import com.exedio.cope.misc.Computed;
import org.junit.Test;

public class FeatureFieldModelTest
{
	static final Model MODEL = new Model(TYPE);

	static
	{
		MODEL.enableSerialization(FeatureFieldModelTest.class, "MODEL");
	}

	@Test public void testIt()
	{
		assertEqualsUnmodifiable(list(TYPE), MODEL.getTypes());
		assertEqualsUnmodifiable(list(TYPE), MODEL.getTypesSortedByHierarchy());
		assertEquals(FeatureFieldItem.class, TYPE.getJavaClass());
		assertEquals(true, TYPE.isBound());
		assertEquals(null, TYPE.getPattern());
		assertEqualsUnmodifiable(list(feature.getIdField()), feature.getSourceFeatures());
		assertEqualsUnmodifiable(list(), feature.getSourceTypes());

		assertEqualsUnmodifiable(list(
				TYPE.getThis(),
				integer1, integer2, integer3,
				string1,  string2,  string3,
				feature, feature.getIdField(),
				featureFinal, featureFinal.getIdField(),
				featureOptional, featureOptional.getIdField(),
				featureUnique, featureUnique.getIdField(), featureUnique.getImplicitUniqueConstraint(),
				featureRenamed, featureRenamed.getIdField(),
				string, string.getIdField()
			), TYPE.getFeatures());

		assertEquals(TYPE, feature.getType());
		assertEquals("feature", feature.getName());
		assertTrue(feature.getIdField().isAnnotationPresent(Computed.class));

		assertEquals(true, feature.isMandatory());
		assertEquals(false, featureOptional.isMandatory());
		assertEquals(list(featureUnique.getIdField()), featureUnique.getImplicitUniqueConstraint().getFields());

		assertSerializedSame(feature, 394);

		assertEqualsUnmodifiable(TYPE.getFeatures(), feature.getValues());
		assertEqualsUnmodifiable(list(
				string1, string2, string3,
				feature.getIdField(),
				featureFinal.getIdField(),
				featureOptional.getIdField(),
				featureUnique.getIdField(),
				featureRenamed.getIdField(),
				string.getIdField()), string.getValues());

		assertEquals(
				"(" +
					"FeatureFieldItem.feature-id<>'FeatureFieldItem.this' AND " +
					"FeatureFieldItem.feature-id<>'FeatureFieldItem.integer1' AND " +
					"FeatureFieldItem.feature-id<>'FeatureFieldItem.integer2' AND " +
					"FeatureFieldItem.feature-id<>'FeatureFieldItem.integer3' AND " +
					"FeatureFieldItem.feature-id<>'FeatureFieldItem.string1' AND " +
					"FeatureFieldItem.feature-id<>'FeatureFieldItem.string2' AND " +
					"FeatureFieldItem.feature-id<>'FeatureFieldItem.string3' AND " +
					"FeatureFieldItem.feature-id<>'FeatureFieldItem.feature' AND " +
					"FeatureFieldItem.feature-id<>'FeatureFieldItem.feature-id' AND " +
					"FeatureFieldItem.feature-id<>'FeatureFieldItem.featureFinal' AND " +
					"FeatureFieldItem.feature-id<>'FeatureFieldItem.featureFinal-id' AND " +
					"FeatureFieldItem.feature-id<>'FeatureFieldItem.featureOptional' AND " +
					"FeatureFieldItem.feature-id<>'FeatureFieldItem.featureOptional-id' AND " +
					"FeatureFieldItem.feature-id<>'FeatureFieldItem.featureUnique' AND " +
					"FeatureFieldItem.feature-id<>'FeatureFieldItem.featureUnique-id' AND " +
					"FeatureFieldItem.feature-id<>'FeatureFieldItem.featureUnique-idImplicitUnique' AND " +
					"FeatureFieldItem.feature-id<>'FeatureFieldItem.featureRenamed' AND " +
					"FeatureFieldItem.feature-id<>'FeatureFieldItem.featureRenamed-id' AND " +
					"FeatureFieldItem.feature-id<>'FeatureFieldItem.string' AND " +
					"FeatureFieldItem.feature-id<>'FeatureFieldItem.string-id'" +
				")",
			feature.isInvalid().toString());
		assertEquals(
				"(" +
					"FeatureFieldItem.string-id<>'FeatureFieldItem.string1' AND " +
					"FeatureFieldItem.string-id<>'FeatureFieldItem.string2' AND " +
					"FeatureFieldItem.string-id<>'FeatureFieldItem.string3' AND " +
					"FeatureFieldItem.string-id<>'FeatureFieldItem.feature-id' AND " +
					"FeatureFieldItem.string-id<>'FeatureFieldItem.featureFinal-id' AND " +
					"FeatureFieldItem.string-id<>'FeatureFieldItem.featureOptional-id' AND " +
					"FeatureFieldItem.string-id<>'FeatureFieldItem.featureUnique-id' AND " +
					"FeatureFieldItem.string-id<>'FeatureFieldItem.featureRenamed-id' AND " +
					"FeatureFieldItem.string-id<>'FeatureFieldItem.string-id'" +
				")",
			string.isInvalid().toString());
	}

	@Test public void testValueClassNull()
	{
		try
		{
			FeatureField.create(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("valueClass", e.getMessage());
		}
	}
}
