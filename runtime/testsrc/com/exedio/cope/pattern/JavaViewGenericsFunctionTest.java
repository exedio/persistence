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

import static com.exedio.cope.AbstractRuntimeTest.activate;
import static com.exedio.cope.RuntimeAssert.assertSerializedSame;
import static com.exedio.cope.instrument.Visibility.NONE;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.instrument.WrapperType;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

public class JavaViewGenericsFunctionTest
{
	@Test void testSerialize()
	{
		assertSerializedSame(MyItem.list, 388);
		assertSerializedSame(MyItem.set,  387);
		assertSerializedSame(MyItem.map,  387);
	}

	@Test void testList()
	{
		assertValueType(List.class, asList(Double.class), MyItem.list);

		final MyItem item = activate(MyItem.TYPE, 55);
		assertSame(MyItem.listResult, item.getList());
		assertSame(MyItem.listResult, MyItem.list.get(item));
	}

	@Test void testSet()
	{
		assertValueType(Set.class, asList(Float.class), MyItem.set);

		final MyItem item = activate(MyItem.TYPE, 55);
		assertSame(MyItem.setResult, item.getSet());
		assertSame(MyItem.setResult, MyItem.set.get(item));
	}

	@Test void testMap()
	{
		assertValueType(Map.class, asList(Integer.class, Long.class), MyItem.map);

		final MyItem item = activate(MyItem.TYPE, 55);
		assertSame(MyItem.mapResult, item.getMap());
		assertSame(MyItem.mapResult, MyItem.map.get(item));
	}


	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	@SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType") // OK: just tested for identity
	private static final class MyItem extends Item
	{
		static final JavaView list = JavaView.createList(Double.class, MyItem::getList);
		static final ArrayList<Double> listResult = new ArrayList<>();
		@SuppressWarnings("TypeParameterExtendsFinalClass") ArrayList<? extends Double> getList()
		{
			return listResult;
		}

		static final JavaView set = JavaView.createSet(Float.class, MyItem::getSet);
		static final HashSet<Float> setResult = new HashSet<>();
		@SuppressWarnings("TypeParameterExtendsFinalClass") HashSet<? extends Float> getSet()
		{
			return setResult;
		}

		static final JavaView map = JavaView.createMap(Integer.class, Long.class, MyItem::getMap);
		static final HashMap<Integer,Long> mapResult = new HashMap<>();
		@SuppressWarnings("TypeParameterExtendsFinalClass") HashMap<? extends Integer,? extends Long> getMap()
		{
			return mapResult;
		}


		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<MyItem> TYPE = com.exedio.cope.TypesBound.newType(MyItem.class,MyItem::new);

		@com.exedio.cope.instrument.Generated
		private MyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	private static final Model MODEL = new Model(MyItem.TYPE);

	static
	{
		MODEL.enableSerialization(JavaViewGenericsFunctionTest.class, "MODEL");
	}

	private static void assertValueType(
			final Class<?> rawType,
			final List<Class<?>> actualTypeArguments,
			final JavaView expected)
	{
		assertEquals(rawType, expected.getValueType());
		final ParameterizedType genericType = (ParameterizedType)expected.getValueGenericType();
		assertEquals(rawType, genericType.getRawType());
		assertEquals(actualTypeArguments, asList(genericType.getActualTypeArguments()));
		assertEquals(null, genericType.getOwnerType());
	}
}
