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

package com.exedio.cope;

import static com.exedio.cope.TypesBound.getFeatures;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.exedio.cope.pattern.Media;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import org.junit.jupiter.api.Test;

public class TypesBoundFeaturesTest
{
	@Test void testIt() throws NoSuchFieldException
	{
		final SortedMap<Feature, Field> m = getFeatures(AClass.class);
		final Iterator<Map.Entry<Feature, Field>> iterator = m.entrySet().iterator();
		{
			final Map.Entry<Feature, Field> entry = iterator.next();
			assertSame(AClass.feature1, entry.getKey());
			assertEquals(AClass.class.getDeclaredField("feature1"), entry.getValue());
		}
		{
			final Map.Entry<Feature, Field> entry = iterator.next();
			assertSame(AClass.feature2, entry.getKey());
			assertEquals(AClass.class.getDeclaredField("feature2"), entry.getValue());
		}
		{
			final Map.Entry<Feature, Field> entry = iterator.next();
			assertSame(AClass.feature3, entry.getKey());
			assertEquals(AClass.class.getDeclaredField("feature3"), entry.getValue());
		}
		{
			final Map.Entry<Feature, Field> entry = iterator.next();
			assertSame(AClass.feature4, entry.getKey());
			assertEquals(AClass.class.getDeclaredField("feature4"), entry.getValue());
		}
		assertFalse(iterator.hasNext());
	}
	private static final class AClass
	{
		static final IntegerField feature1 = new IntegerField();
		static final BooleanField feature2 = new BooleanField();
		static final Media        feature3 = new Media();
		private static final BooleanField feature4 = new BooleanField();

		@SuppressWarnings("unused") // OK: is to be ignored
		static IntegerField nonFinal = new IntegerField();
		@SuppressWarnings("unused") // OK: is to be ignored
		final IntegerField nonStatic = new IntegerField();
		@SuppressWarnings("unused") // OK: is to be ignored
		final Object nonFeature = new IntegerField();
		@SuppressWarnings("unused") // OK: is to be ignored
		@CopeIgnore static final IntegerField ignored = new IntegerField();
	}


	@Test void testNull()
	{
		assertFails(
				() -> getFeatures(NullClass.class),
				NullPointerException.class,
				"com.exedio.cope.TypesBoundFeaturesTest$NullClass#nullFeature");
	}
	private static final class NullClass
	{
		@SuppressWarnings("unused") // OK: is to be ignored
		static final IntegerField nullFeature = null;
	}


	@Test void testDuplicate()
	{
		assertFails(
				() -> getFeatures(DuplicateClass.class),
				IllegalArgumentException.class,
				"com.exedio.cope.TypesBoundFeaturesTest$DuplicateClass#duplicate is same as #original");
	}
	private static final class DuplicateClass
	{
		@SuppressWarnings("unused") // OK: is to be ignored
		static final IntegerField original = new IntegerField();
		@SuppressWarnings("unused") // OK: is to be ignored
		static final IntegerField duplicate = original;
	}


	/**
	 * Tests, that order of features does not depend on order of java fields,
	 * but on sequence on instantiation.
	 */
	@Test void testInstantiationOrder() throws NoSuchFieldException
	{
		final SortedMap<Feature, Field> m = getFeatures(OrderClass.class);
		final Iterator<Map.Entry<Feature, Field>> iterator = m.entrySet().iterator();
		{
			final Map.Entry<Feature, Field> entry = iterator.next();
			assertSame(OrderClass.feature1, entry.getKey());
			assertEquals(OrderClass.class.getDeclaredField("feature1"), entry.getValue());
		}
		{
			final Map.Entry<Feature, Field> entry = iterator.next();
			assertSame(OrderClass.feature2, entry.getKey());
			assertEquals(OrderClass.class.getDeclaredField("feature2"), entry.getValue());
		}
		assertFalse(iterator.hasNext());
	}
	private static final IntegerField feature1Instantiation = new IntegerField();
	private static final class OrderClass
	{
		static final IntegerField feature2 = new IntegerField();
		static final IntegerField feature1 = feature1Instantiation;
	}


	@Test void testErrors()
	{
		assertFails(
				() -> getFeatures(null),
				NullPointerException.class,
				null);
	}
}
