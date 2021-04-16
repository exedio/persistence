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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.fail;

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

	static final class AClass
	{
		static final IntegerField feature1 = new IntegerField();
		static final BooleanField feature2 = new BooleanField();
		static final Media        feature3 = new Media();
		private static final BooleanField feature4 = new BooleanField();

		private AClass()
		{
			// prevent instantiation
		}
	}

	@Test void testErrors()
	{
		try
		{
			getFeatures(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals(null, e.getMessage());
		}
	}
}
