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

import static com.exedio.cope.junit.CopeAssert.assertContainsList;
import static com.exedio.cope.junit.CopeAssert.assertUnmodifiable;
import static com.exedio.cope.junit.CopeAssert.reserialize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class RuntimeAssert
{
	private RuntimeAssert()
	{
		// prevent instantiation
	}

	public static void assertData(final byte[] expectedData, final byte[] actualData)
	{
		if(!Arrays.equals(expectedData, actualData))
			fail("expected " + Arrays.toString(expectedData) + ", but was " + Arrays.toString(actualData));
	}


	protected static final <T extends Item> void assertCondition(final Type<T> type, final Condition actual)
	{
		assertCondition(Collections.<T>emptyList(), type, actual);
	}

	protected static final <T extends Item> void assertCondition(final T o1, final Type<T> type, final Condition actual)
	{
		final ArrayList<T> l = new ArrayList<>();
		l.add(o1);
		assertCondition(l, type, actual);
	}

	protected static final <T extends Item> void assertCondition(final T o1, final T o2, final Type<T> type, final Condition actual)
	{
		final ArrayList<T> l = new ArrayList<>();
		l.add(o1);
		l.add(o2);
		assertCondition(l, type, actual);
	}

	protected static final <T extends Item> void assertCondition(final T o1, final T o2, final T o3, final Type<T> type, final Condition actual)
	{
		final ArrayList<T> l = new ArrayList<>();
		l.add(o1);
		l.add(o2);
		l.add(o3);
		assertCondition(l, type, actual);
	}

	protected static final <T extends Item> void assertCondition(final T o1, final T o2, final T o3, final T o4, final Type<T> type, final Condition actual)
	{
		final ArrayList<T> l = new ArrayList<>();
		l.add(o1);
		l.add(o2);
		l.add(o3);
		l.add(o4);
		assertCondition(l, type, actual);
	}

	protected static final <T extends Item> void assertCondition(final T o1, final T o2, final T o3, final T o4, final T o5, final Type<T> type, final Condition actual)
	{
		final ArrayList<T> l = new ArrayList<>();
		l.add(o1);
		l.add(o2);
		l.add(o3);
		l.add(o4);
		l.add(o5);
		assertCondition(l, type, actual);
	}

	protected static final <T extends Item> void assertCondition(final T o1, final T o2, final T o3, final T o4, final T o5, final T o6, final Type<T> type, final Condition actual)
	{
		final ArrayList<T> l = new ArrayList<>();
		l.add(o1);
		l.add(o2);
		l.add(o3);
		l.add(o4);
		l.add(o5);
		l.add(o6);
		assertCondition(l, type, actual);
	}

	public static final <T extends Item> void assertCondition(final List<T> expected, final Type<T> type, final Condition actual)
	{
		final List<T> actualResult = type.search(actual);
		assertContainsList(expected, actualResult);
		assertUnmodifiable(actualResult);
		for(final T item : type.search())
			assertEquals(expected.contains(item), actual.get(item));
	}


	public static final void assertSerializedSame(final Serializable value, final int expectedSize)
	{
		assertSame(value, reserialize(value, expectedSize));
	}
}
