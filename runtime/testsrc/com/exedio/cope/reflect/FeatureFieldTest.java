/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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

import static com.exedio.cope.reflect.FeatureFieldItem.integer1;
import static com.exedio.cope.reflect.FeatureFieldItem.integer2;
import static com.exedio.cope.reflect.FeatureFieldItem.integer3;
import static com.exedio.cope.reflect.FeatureFieldItem.string1;
import static com.exedio.cope.reflect.FeatureFieldItem.string2;
import static com.exedio.cope.reflect.FeatureFieldItem.string3;

import com.exedio.cope.AbstractRuntimeTest;
import com.exedio.cope.FinalViolationException;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.Model;
import com.exedio.cope.SchemaInfo;
import com.exedio.cope.misc.Computed;

public class FeatureFieldTest extends AbstractRuntimeTest
{
	private static final Model MODEL = new Model(FeatureFieldItem.TYPE);
	
	static
	{
		MODEL.enableSerialization(FeatureFieldTest.class, "MODEL");
	}
	
	public FeatureFieldTest()
	{
		super(MODEL);
	}

	FeatureFieldItem item;
	
	public void testIt()
	{
		// test model
		assertEqualsUnmodifiable(list(item.TYPE), model.getTypes());
		assertEqualsUnmodifiable(list(item.TYPE), model.getTypesSortedByHierarchy());
		assertEquals(FeatureFieldItem.class, item.TYPE.getJavaClass());
		assertEquals(true, item.TYPE.isBound());
		assertEquals(null, item.TYPE.getPattern());
		assertEqualsUnmodifiable(list(FeatureFieldItem.feature.getIdField()), FeatureFieldItem.feature.getSourceFeatures());
		assertEqualsUnmodifiable(list(), FeatureFieldItem.feature.getSourceTypes());

		assertEqualsUnmodifiable(list(
				item.TYPE.getThis(),
				integer1, integer2, integer3,
				string1,  string2,  string3,
				item.feature, item.feature.getIdField(),
				item.featureFinal, item.featureFinal.getIdField(),
				item.featureOptional, item.featureOptional.getIdField(),
				item.featureRenamed, item.featureRenamed.getIdField(),
				item.string, item.string.getIdField()
			), item.TYPE.getFeatures());

		assertEquals(item.TYPE, item.feature.getType());
		assertEquals("feature", item.feature.getName());
		assertTrue(item.feature.getIdField().isAnnotationPresent(Computed.class));
		
		assertSerializedSame(FeatureFieldItem.feature, 389);
		
		try
		{
			FeatureField.newField(null);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals("valueClass", e.getMessage());
		}
		
		// test persistence
		assertEquals("feature", SchemaInfo.getColumnName(FeatureFieldItem.feature.getIdField()));
		assertEquals("featureNewname", SchemaInfo.getColumnName(FeatureFieldItem.featureRenamed.getIdField()));
		
		item = deleteOnTearDown(new FeatureFieldItem(FeatureFieldItem.string1, FeatureFieldItem.string2));
		assertSame(FeatureFieldItem.string1, item.getFeature());
		assertSame(FeatureFieldItem.string2, item.getFeatureFinal());
		assertSame(null, item.getFeatureOptional());
		assertSame(null, item.getString());
		
		item.setFeatureOptional(integer1);
		assertSame(FeatureFieldItem.string1, item.getFeature());
		assertSame(FeatureFieldItem.string2, item.getFeatureFinal());
		assertSame(integer1, item.getFeatureOptional());
		assertSame(null, item.getString());
		
		item.setFeatureOptional(null);
		assertSame(FeatureFieldItem.string1, item.getFeature());
		assertSame(FeatureFieldItem.string2, item.getFeatureFinal());
		assertSame(null, item.getFeatureOptional());
		assertSame(null, item.getString());
		
		item.setString(string2);
		assertSame(FeatureFieldItem.string1, item.getFeature());
		assertSame(FeatureFieldItem.string2, item.getFeatureFinal());
		assertSame(null, item.getFeatureOptional());
		assertSame(string2, item.getString());
		
		item.setString(null);
		assertSame(FeatureFieldItem.string1, item.getFeature());
		assertSame(FeatureFieldItem.string2, item.getFeatureFinal());
		assertSame(null, item.getFeatureOptional());
		assertSame(null, item.getString());
		
		try
		{
			item.setFeature(null);
			fail();
		}
		catch(MandatoryViolationException e)
		{
			assertEquals(item.feature, e.getFeature());
			assertEquals(item, e.getItem());
		}
		assertSame(FeatureFieldItem.string1, item.getFeature());
		assertSame(FeatureFieldItem.string2, item.getFeatureFinal());
		assertSame(null, item.getFeatureOptional());
		assertSame(null, item.getString());
		
		try
		{
			item.featureFinal.set(item, integer1);
			fail();
		}
		catch(FinalViolationException e)
		{
			assertEquals(item.featureFinal, e.getFeature());
			assertEquals(item, e.getItem());
		}
		assertSame(FeatureFieldItem.string1, item.getFeature());
		assertSame(FeatureFieldItem.string2, item.getFeatureFinal());
		assertSame(null, item.getFeatureOptional());
		assertSame(null, item.getString());
		
		item.feature.getIdField().set(item, "zack");
		try
		{
			item.getFeature();
			fail();
		}
		catch(IllegalStateException e)
		{
			assertEquals("zack", e.getMessage());
		}
		item.setFeature(string1);
		
		item.string.getIdField().set(item, integer1.getID());
		try
		{
			item.getString();
			fail();
		}
		catch(ClassCastException e)
		{
			assertEquals("expected a com.exedio.cope.StringField, but was a com.exedio.cope.IntegerField", e.getMessage());
		}
		item.setString(null);
	}
}
