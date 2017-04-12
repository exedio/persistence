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

package com.exedio.cope.tojunit;

import static org.junit.Assert.assertEquals;

import com.exedio.cope.Query;
import com.exedio.cope.junit.CopeAssert;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class Assert
{
	public static void assertContainsList(final List<?> expected, final Collection<?> actual)
	{
		CopeAssert.assertContainsList(expected, actual);
	}

	public static void assertContains(final Collection<?> actual)
	{
		CopeAssert.assertContains(actual);
	}

	public static void assertContains(final Object o, final Collection<?> actual)
	{
		CopeAssert.assertContains(o, actual);
	}

	public static void assertContains(final Object o1, final Object o2, final Collection<?> actual)
	{
		CopeAssert.assertContains(o1, o2, actual);
	}

	public static void assertContains(final Object o1, final Object o2, final Object o3, final Collection<?> actual)
	{
		CopeAssert.assertContains(o1, o2, o3, actual);
	}

	public static void assertContains(final Object o1, final Object o2, final Object o3, final Object o4, final Collection<?> actual)
	{
		CopeAssert.assertContains(o1, o2, o3, o4, actual);
	}

	public static void assertContains(final Object o1, final Object o2, final Object o3, final Object o4, final Object o5, final Collection<?> actual)
	{
		CopeAssert.assertContains(o1, o2, o3, o4, o5, actual);
	}

	public static void assertContains(final Object o1, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6, final Collection<?> actual)
	{
		CopeAssert.assertContains(o1, o2, o3, o4, o5, o6, actual);
	}

	public static void assertContains(final Object o1, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6, final Object o7, final Collection<?> actual)
	{
		CopeAssert.assertContains(o1, o2, o3, o4, o5, o6, o7, actual);
	}

	public static void assertContainsUnmodifiable(final Collection<?> actual)
	{
		CopeAssert.assertContainsUnmodifiable(actual);
	}

	public static void assertContainsUnmodifiable(final Object o, final Collection<?> actual)
	{
		CopeAssert.assertContainsUnmodifiable(o, actual);
	}

	public static void assertContainsUnmodifiable(final Object o1, final Object o2, final Collection<?> actual)
	{
		CopeAssert.assertContainsUnmodifiable(o1, o2, actual);
	}

	public static void assertContainsUnmodifiable(final Object o1, final Object o2, final Object o3, final Collection<?> actual)
	{
		CopeAssert.assertContainsUnmodifiable(o1, o2, o3, actual);
	}

	public static void assertContainsUnmodifiable(final Object o1, final Object o2, final Object o3, final Object o4, final Collection<?> actual)
	{
		CopeAssert.assertContainsUnmodifiable(o1, o2, o3, o4, actual);
	}

	public static List<Object> list(final Object... o)
	{
		return CopeAssert.list(o);
	}

	public static Map<Object, Object> map()
	{
		return CopeAssert.map();
	}

	public static Map<Object, Object> map(final Object key1, final Object value1)
	{
		return CopeAssert.map(key1, value1);
	}

	public static Map<Object, Object> map(final Object key1, final Object value1, final Object key2, final Object value2)
	{
		return CopeAssert.map(key1, value1, key2, value2);
	}

	public static <T> void assertUnmodifiable(final Collection<T> c)
	{
		CopeAssert.assertUnmodifiable(c);
	}

	public static void assertEqualsUnmodifiable(final List<?> expected, final Collection<?> actual)
	{
		CopeAssert.assertEqualsUnmodifiable(expected, actual);
	}

	public static void assertEqualsUnmodifiable(final Map<?,?> expected, final Map<?,?> actual)
	{
		CopeAssert.assertEqualsUnmodifiable(expected, actual);
	}

	public static void assertEqualsUnmodifiable(final Set<?> expected, final Collection<?> actual)
	{
		assertUnmodifiable(actual);
		assertEquals(expected, actual);
	}

	public static void assertEqualsStrict(final Object expected, final Object actual)
	{
		CopeAssert.assertEqualsStrict(expected, actual);
	}

	public static void assertNotEqualsStrict(final Object expected, final Object actual)
	{
		CopeAssert.assertNotEqualsStrict(expected, actual);
	}

	public static void assertWithin(final Date expectedBefore, final Date expectedAfter, final Date actual)
	{
		CopeAssert.assertWithin(expectedBefore, expectedAfter, actual);
	}

	public static <S extends Serializable> S reserialize(final S value, final int expectedSize)
	{
		return CopeAssert.reserialize(value, expectedSize);
	}

	public static byte[] serialize(final Serializable value)
	{
		return CopeAssert.serialize(value);
	}

	public static Object deserialize(final byte[] bytes)
	{
		return CopeAssert.deserialize(bytes);
	}

	public static <R> R waitForKey(final R o)
	{
		return CopeAssert.waitForKey(o);
	}

	public static void waitForKey()
	{
		CopeAssert.waitForKey();
	}

	public static void sleepLongerThan(final long millis) throws InterruptedException
	{
		CopeAssert.sleepLongerThan(millis);
	}

	public static Collection<?> infoSearch(final Query<?> query)
	{
		return CopeAssert.infoSearch(query);
	}


	private Assert()
	{
		// prevent instantiation
	}
}
