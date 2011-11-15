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

package com.exedio.cope.reflect;

import static com.exedio.cope.reflect.FeatureFieldItem.TYPE;
import static com.exedio.cope.reflect.FeatureFieldItem.feature;
import static com.exedio.cope.reflect.FeatureFieldItem.featureFinal;
import static com.exedio.cope.reflect.FeatureFieldItem.featureOptional;
import static com.exedio.cope.reflect.FeatureFieldItem.featureRenamed;
import static com.exedio.cope.reflect.FeatureFieldItem.integer1;
import static com.exedio.cope.reflect.FeatureFieldItem.integer2;
import static com.exedio.cope.reflect.FeatureFieldItem.integer3;
import static com.exedio.cope.reflect.FeatureFieldItem.string;
import static com.exedio.cope.reflect.FeatureFieldItem.string1;
import static com.exedio.cope.reflect.FeatureFieldItem.string2;
import static com.exedio.cope.reflect.FeatureFieldItem.string3;

import com.exedio.cope.AbstractRuntimeTest;
import com.exedio.cope.Model;
import com.exedio.cope.junit.CopeAssert;
import com.exedio.cope.misc.Computed;

public class FeatureFieldModelTest extends CopeAssert
{
	static final Model MODEL = new Model(FeatureFieldItem.TYPE);

	static
	{
		MODEL.enableSerialization(FeatureFieldModelTest.class, "MODEL");
	}

	public void testIt()
	{
		final Model model = MODEL;
		assertEqualsUnmodifiable(list(TYPE), model.getTypes());
		assertEqualsUnmodifiable(list(TYPE), model.getTypesSortedByHierarchy());
		assertEquals(FeatureFieldItem.class, TYPE.getJavaClass());
		assertEquals(true, TYPE.isBound());
		assertEquals(null, TYPE.getPattern());
		assertEqualsUnmodifiable(list(FeatureFieldItem.feature.getIdField()), FeatureFieldItem.feature.getSourceFeatures());
		assertEqualsUnmodifiable(list(), FeatureFieldItem.feature.getSourceTypes());

		assertEqualsUnmodifiable(list(
				TYPE.getThis(),
				integer1, integer2, integer3,
				string1,  string2,  string3,
				feature, feature.getIdField(),
				featureFinal, featureFinal.getIdField(),
				featureOptional, featureOptional.getIdField(),
				featureRenamed, featureRenamed.getIdField(),
				string, string.getIdField()
			), TYPE.getFeatures());

		assertEquals(TYPE, feature.getType());
		assertEquals("feature", feature.getName());
		assertTrue(feature.getIdField().isAnnotationPresent(Computed.class));

		assertEquals(true, feature.isMandatory());
		assertEquals(false, featureOptional.isMandatory());

		AbstractRuntimeTest.assertSerializedSame(FeatureFieldItem.feature, 394);

		assertEqualsUnmodifiable(TYPE.getFeatures(), feature.getValues());
		assertEqualsUnmodifiable(list(
				string1, string2, string3,
				feature.getIdField(),
				featureFinal.getIdField(),
				featureOptional.getIdField(),
				featureRenamed.getIdField(),
				string.getIdField()), string.getValues());

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
