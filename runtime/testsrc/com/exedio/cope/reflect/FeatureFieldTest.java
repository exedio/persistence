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

import static com.exedio.cope.reflect.FeatureFieldItem.TYPE;
import static com.exedio.cope.reflect.FeatureFieldItem.feature;
import static com.exedio.cope.reflect.FeatureFieldItem.featureFinal;
import static com.exedio.cope.reflect.FeatureFieldItem.featureOptional;
import static com.exedio.cope.reflect.FeatureFieldItem.forFeatureUnique;
import static com.exedio.cope.reflect.FeatureFieldItem.integer1;
import static com.exedio.cope.reflect.FeatureFieldItem.string;
import static com.exedio.cope.reflect.FeatureFieldItem.string1;
import static com.exedio.cope.reflect.FeatureFieldItem.string2;

import com.exedio.cope.AbstractRuntimeModelTest;
import com.exedio.cope.FinalViolationException;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.SchemaInfo;

public class FeatureFieldTest extends AbstractRuntimeModelTest
{
	public FeatureFieldTest()
	{
		super(FeatureFieldModelTest.MODEL);
	}

	FeatureFieldItem item;

	@Test public void testIt()
	{
		assertEquals("feature", SchemaInfo.getColumnName(FeatureFieldItem.feature.getIdField()));
		assertEquals("featureNewname", SchemaInfo.getColumnName(FeatureFieldItem.featureRenamed.getIdField()));

		item = new FeatureFieldItem(FeatureFieldItem.string1, FeatureFieldItem.string2);
		assertSame(FeatureFieldItem.string1, item.getFeature());
		assertSame(FeatureFieldItem.string2, item.getFeatureFinal());
		assertSame(null, item.getFeatureOptional());
		assertSame(null, item.getString());

		item.setFeatureOptional(integer1);
		assertSame(FeatureFieldItem.string1, item.getFeature());
		assertSame(FeatureFieldItem.string2, item.getFeatureFinal());
		assertSame(integer1, item.getFeatureOptional());
		assertSame(null, item.getString());
		assertEquals(list(), TYPE.search(featureOptional.isInvalid()));

		item.setFeatureOptional(null);
		assertSame(FeatureFieldItem.string1, item.getFeature());
		assertSame(FeatureFieldItem.string2, item.getFeatureFinal());
		assertSame(null, item.getFeatureOptional());
		assertSame(null, item.getString());
		assertEquals(list(), TYPE.search(featureOptional.isInvalid()));

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
		catch(final MandatoryViolationException e)
		{
			assertEquals(feature, e.getFeature());
			assertEquals(item, e.getItem());
		}
		assertSame(FeatureFieldItem.string1, item.getFeature());
		assertSame(FeatureFieldItem.string2, item.getFeatureFinal());
		assertSame(null, item.getFeatureOptional());
		assertSame(null, item.getString());

		try
		{
			featureFinal.set(item, integer1);
			fail();
		}
		catch(final FinalViolationException e)
		{
			assertEquals(featureFinal, e.getFeature());
			assertEquals(item, e.getItem());
		}
		assertSame(FeatureFieldItem.string1, item.getFeature());
		assertSame(FeatureFieldItem.string2, item.getFeatureFinal());
		assertSame(null, item.getFeatureOptional());
		assertSame(null, item.getString());

		feature.getIdField().set(item, "zack");
		try
		{
			item.getFeature();
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("zack", e.getMessage());
		}
		item.setFeature(string1);

		string.getIdField().set(item, integer1.getID());
		try
		{
			item.getString();
			fail();
		}
		catch(final ClassCastException e)
		{
			assertEquals("expected a com.exedio.cope.StringField, but was a com.exedio.cope.IntegerField", e.getMessage());
		}
		item.setString(null);

		assertEquals(null, forFeatureUnique(string1));
		assertEquals(null, forFeatureUnique(string2));
		item.setFeatureUnique(string1);
		assertEquals(item, forFeatureUnique(string1));
		assertEquals(null, forFeatureUnique(string2));
		try
		{
			forFeatureUnique(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals(null, e.getMessage());
		}
	}
}
