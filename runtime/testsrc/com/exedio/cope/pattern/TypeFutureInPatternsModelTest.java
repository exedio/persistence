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

import static com.exedio.cope.ItemField.DeletePolicy.FORBID;
import static com.exedio.cope.RuntimeAssert.assertSerializedSame;
import static com.exedio.cope.pattern.TypeFutureInPatternsItem.TYPE;
import static com.exedio.cope.pattern.TypeFutureInPatternsItem.feature;
import static com.exedio.cope.pattern.TypeFutureInPatternsItem.feature2;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.IntegerField;
import com.exedio.cope.ItemField;
import com.exedio.cope.Model;
import com.exedio.cope.Type;
import com.exedio.cope.pattern.TypeFutureInPatternsFeature.TypeItem;
import org.junit.jupiter.api.Test;

public class TypeFutureInPatternsModelTest
{
	static final Model MODEL = new Model(TYPE);

	static
	{
		MODEL.enableSerialization(TypeFutureInPatternsModelTest.class, "MODEL");
	}

	static final Type<?> featureType = feature.sourceType();
	static final IntegerField featureField = feature.field;
	static final ItemField<?> featureSelf = feature.self();

	@Test void testIt()
	{
		assertEquals(asList(TYPE, featureType, feature2.sourceType()), MODEL.getTypes());
		assertEquals(asList(TYPE.getThis(), feature, feature2), TYPE.getDeclaredFeatures());

		assertEquals(TYPE, feature.getType());
		assertEquals("feature", feature.getName());

		assertEquals(asList(), feature.getSourceFeatures());
		assertEquals(asList(featureType), feature.getSourceTypes());
		assertEquals(feature, featureType.getPattern());

		assertEquals(asList(featureType.getThis(), featureField, featureSelf), featureType.getDeclaredFeatures());

		assertEquals(featureType, featureField.getType());
		assertEquals("field", featureField.getName());

		assertEquals(featureType, featureSelf.getType());
		assertEquals("self", featureSelf.getName());
		assertEquals(TypeItem.class, featureSelf.getValueClass());
		assertEquals(featureType, featureSelf.getValueType());
	}

	@Test void testValueClassNull()
	{
		try
		{
			ItemField.create(null, null, FORBID);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("valueClass", e.getMessage());
		}
	}

	@Test void testTypeFutureNull()
	{
		try
		{
			ItemField.create(TypeFutureInPatternsItem.class, null, FORBID);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("valueType", e.getMessage());
		}
	}

	@Test void testSerialize()
	{
		assertSerializedSame(feature, 418);
	}
}
