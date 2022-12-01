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

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.ConstraintViolationException;
import com.exedio.cope.Feature;
import com.exedio.cope.Item;
import com.exedio.cope.Query;
import com.exedio.cope.QueryInfo;
import com.exedio.cope.Transaction;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import org.junit.jupiter.api.function.Executable;

public final class Assert
{
	public static <T extends Throwable> T assertFails(
			final Executable executable,
			final Class<T> expectedType,
			final String expectedMessage)
	{
		return assertFails(executable, expectedType, expectedMessage, Function.identity());
	}

	public static <T extends Throwable> T assertFails(
			final Executable executable,
			final Class<T> expectedType,
			final String expectedMessage,
			final Function<String, String> actualMessageFilter)
	{
		final T result = assertThrows(expectedType, executable);
		assertSame(expectedType, result.getClass());
		assertEquals(expectedMessage, actualMessageFilter.apply(result.getMessage()));
		return result;
	}

	@SuppressWarnings("UnusedReturnValue") // OK: for later use
	public static <T extends ConstraintViolationException> T assertFails(
			final Executable executable,
			final Class<T> expectedType,
			final String expectedMessage,
			final Feature expectedFeature)
	{
		return assertFails(executable, expectedType, expectedMessage, expectedFeature, null);
	}

	public static <T extends ConstraintViolationException> T assertFails(
			final Executable executable,
			final Class<T> expectedType,
			final String expectedMessage,
			final Feature expectedFeature,
			final Item expectedItem)
	{
		final T result = assertFails(executable, expectedType, expectedMessage);
		assertSame(expectedFeature, result.getFeature());
		assertSame(expectedItem, result.getItem());
		return result;
	}

	public static <E> List<E> listOf(final Class<E> elementClass)
	{
		return listOf(Arrays.asList(), elementClass);
	}

	public static <E> List<E> listOf(final E e1, final Class<E> elementClass)
	{
		return listOf(Arrays.asList(e1), elementClass);
	}

	public static <E> List<E> listOf(final E e1, final E e2, final Class<E> elementClass)
	{
		return listOf(Arrays.asList(e1, e2), elementClass);
	}

	public static <E> List<E> listOf(final E e1, final E e2, final E e3, final Class<E> elementClass)
	{
		return listOf(Arrays.asList(e1, e2, e3), elementClass);
	}

	public static <E> List<E> listOf(final E e1, final E e2, final E e3, final E e4, final Class<E> elementClass)
	{
		return listOf(Arrays.asList(e1, e2, e3, e4), elementClass);
	}

	private static <E> List<E> listOf(final List<E> l, final Class<E> elementClass)
	{
		return Collections.unmodifiableList(new SensitiveList<>(l, elementClass));
	}

	public static <K,V> Map<K, V> sensitive(
			final Map<K,V> map,
			final Class<K> keyClass,
			final Class<V> valueClass)
	{
		return sensitive(map, m -> new SensitiveMap<>(m, keyClass, valueClass));
	}

	public static <K,V> Map<K,V> sensitive(
			final Map<K,V> map,
			final Function<Map<K,V>, SensitiveMap<K,V>> subClass)
	{
		return Collections.unmodifiableMap(subClass.apply(map));
	}

	public static void assertContainsList(final List<?> expected, final Collection<?> actual)
	{
		if(expected==null && actual==null)
			return;

		assertNotNull(expected, "expected null, but was " + actual);
		assertNotNull(actual, "expected " + expected + ", but was null");

		//noinspection SuspiciousMethodCalls OK: just for testing
		if(expected.size()!=actual.size() ||
			!expected.containsAll(actual) ||
			!actual.containsAll(expected))
			fail("expected "+expected+", but was "+actual);
	}

	public static void assertContains(final Collection<?> actual)
	{
		assertContainsList(Collections.emptyList(), actual);
	}

	public static void assertContains(final Object o, final Collection<?> actual)
	{
		assertContainsList(Collections.singletonList(o), actual);
	}

	public static void assertContains(final Object o1, final Object o2, final Collection<?> actual)
	{
		assertContainsList(Arrays.asList(o1, o2), actual);
	}

	public static void assertContains(final Object o1, final Object o2, final Object o3, final Collection<?> actual)
	{
		assertContainsList(Arrays.asList(o1, o2, o3), actual);
	}

	public static void assertContains(final Object o1, final Object o2, final Object o3, final Object o4, final Collection<?> actual)
	{
		assertContainsList(Arrays.asList(o1, o2, o3, o4), actual);
	}

	public static void assertContains(final Object o1, final Object o2, final Object o3, final Object o4, final Object o5, final Collection<?> actual)
	{
		assertContainsList(Arrays.asList(o1, o2, o3, o4, o5), actual);
	}

	public static void assertContains(final Object o1, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6, final Collection<?> actual)
	{
		assertContainsList(Arrays.asList(o1, o2, o3, o4, o5, o6), actual);
	}

	@SuppressWarnings("unused") // OK: for later use
	public static void assertContains(final Object o1, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6, final Object o7, final Collection<?> actual)
	{
		assertContainsList(Arrays.asList(o1, o2, o3, o4, o5, o6, o7), actual);
	}

	public static void assertContains(final Object o1, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6, final Object o7, final Object o8, final Collection<?> actual)
	{
		assertContainsList(Arrays.asList(o1, o2, o3, o4, o5, o6, o7, o8), actual);
	}

	@SuppressWarnings("unused") // OK: for later use
	public static void assertContainsUnmodifiable(final Collection<?> actual)
	{
		assertUnmodifiable(actual);
		assertContains(actual);
	}

	@SuppressWarnings("unused") // OK: for later use
	public static void assertContainsUnmodifiable(final Object o, final Collection<?> actual)
	{
		assertUnmodifiable(actual);
		assertContains(o, actual);
	}

	public static void assertContainsUnmodifiable(final Object o1, final Object o2, final Collection<?> actual)
	{
		assertUnmodifiable(actual);
		assertContains(o1, o2, actual);
	}

	@SuppressWarnings("unused") // OK: for later use
	public static void assertContainsUnmodifiable(final Object o1, final Object o2, final Object o3, final Collection<?> actual)
	{
		assertUnmodifiable(actual);
		assertContains(o1, o2, o3, actual);
	}

	@SuppressWarnings("unused") // OK: for later use
	public static void assertContainsUnmodifiable(final Object o1, final Object o2, final Object o3, final Object o4, final Collection<?> actual)
	{
		assertUnmodifiable(actual);
		assertContains(o1, o2, o3, o4, actual);
	}

	public static List<Object> list(final Object... o)
	{
		//noinspection Java9CollectionFactory OK: o may contain null
		return Collections.unmodifiableList(Arrays.asList(o));
	}

	public static Map<Object, Object> map()
	{
		return Collections.emptyMap();
	}

	public static Map<Object, Object> map(final Object key1, final Object value1)
	{
		return Collections.singletonMap(key1, value1);
	}

	public static Map<Object, Object> map(final Object key1, final Object value1, final Object key2, final Object value2)
	{
		final HashMap<Object, Object> result = new HashMap<>();
		result.put(key1, value1);
		result.put(key2, value2);
		//noinspection Java9CollectionFactory OK: parameters may contain null
		return Collections.unmodifiableMap(result);
	}

	public static <T> void assertUnmodifiable(final Collection<T> c)
	{
		final String name = c.getClass().getName();
		assertTrue(UNMODIFIABLE_COLLECTIONS.contains(name), name);
	}

	private static final HashSet<String> UNMODIFIABLE_COLLECTIONS = new HashSet<>(Arrays.asList(
			"java.util.ImmutableCollections$List12",
			"java.util.ImmutableCollections$ListN",
			"java.util.Collections$UnmodifiableCollection",
			"java.util.Collections$UnmodifiableRandomAccessList",
			"java.util.Collections$SingletonList",
			"java.util.Collections$EmptyList",
			"java.util.Collections$UnmodifiableSet",
			"java.util.Collections$UnmodifiableSortedSet",
			"java.util.Collections$UnmodifiableNavigableSet$EmptyNavigableSet"));

	public static void assertEqualsUnmodifiable(final List<?> expected, final Collection<?> actual)
	{
		assertUnmodifiable(actual);
		assertEquals(expected, actual);
	}

	public static void assertEqualsUnmodifiable(final Map<?,?> expected, final Map<?,?> actual)
	{
		assertUnmodifiable(actual);
		assertEquals(expected, actual);
	}

	public static void assertUnmodifiable(final Map<?,?> m)
	{
		final String name = m.getClass().getName();
		assertTrue(UNMODIFIABLE_MAPS.contains(name), name);
	}

	private static final HashSet<String> UNMODIFIABLE_MAPS = new HashSet<>(Arrays.asList(
			"java.util.Collections$UnmodifiableMap",
			"java.util.Collections$EmptyMap"));

	public static void assertEqualsUnmodifiable(final Set<?> expected, final Collection<?> actual)
	{
		assertUnmodifiable(actual);
		assertEquals(expected, actual);
	}

	private static final String DATE_FORMAT_FULL = "yyyy-MM-dd HH:mm:ss.SSS";

	public static void assertWithin(final Date expectedBefore, final Date expectedAfter, final Date actual)
	{
		final SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT_FULL, Locale.ENGLISH);
		final String message =
				"expected date within " + df.format(expectedBefore) +
				" and " + df.format(expectedAfter) +
				", but was " + df.format(actual);

		assertTrue(!expectedBefore.after(actual), message);
		assertTrue(!expectedAfter.before(actual), message);
	}

	public static void assertWithin(final Date expectedBefore, final Date expectedAfter, final Instant actual)
	{
		assertWithin(expectedBefore, expectedAfter, Date.from(actual));
	}

	public static void assertWithin(final Instant expectedBefore, final Instant expectedAfter, final Instant actual)
	{
		final String message =
				"expected date within " + expectedBefore +
				" and " + expectedAfter +
				", but was " + actual;

		assertTrue(!expectedBefore.isAfter(actual), message);
		assertTrue(!expectedAfter.isBefore(actual), message);
	}

	public static <S extends Serializable> S reserialize(final S value, final int expectedSize)
	{
		final byte[] bos = serialize(value);
		assertEquals(expectedSize, bos.length);
		@SuppressWarnings("unchecked")
		final S result = (S)deserialize(bos);
		return result;
	}

	public static byte[] serialize(final Serializable value)
	{
		requireNonNull(value);

		final ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try(ObjectOutputStream oos = new DeduplicateStringsObjectOutputStream(bos))
		{
			oos.writeObject(value);
		}
		catch(final IOException e)
		{
			throw new RuntimeException(e);
		}
		return bos.toByteArray();
	}

	public static Object deserialize(final byte[] bytes)
	{
		requireNonNull(bytes);

		try(ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes)))
		{
			return ois.readObject();
		}
		catch(final IOException | ClassNotFoundException e)
		{
			throw new RuntimeException(e);
		}
	}

	private static final class DeduplicateStringsObjectOutputStream extends ObjectOutputStream
	{
		private HashMap<String, String> strings = null;

		DeduplicateStringsObjectOutputStream(final ByteArrayOutputStream out) throws IOException
		{
			super(out);
			enableReplaceObject(true);
		}

		@Override
		protected Object replaceObject(final Object obj)
		{
			if(obj instanceof String)
			{
				final String string = (String)obj;
				if(strings==null)
					strings = new HashMap<>();

				final String replacement = strings.get(string);
				if(replacement==null)
				{
					strings.put(string, string);
					return string;
				}
				else
				{
					return replacement;
				}
			}
			else
				return obj;
		}
	}

	/**
	 * This method will not return until the result of System.currentTimeMillis() has increased
	 * by the given amount of milli seconds.
	 */
	public static void sleepLongerThan(final long millis) throws InterruptedException
	{
		final long start = System.currentTimeMillis();
		// The loop double-checks that currentTimeMillis() really returns a sufficiently higher
		// value ... needed for Windows.
		do
		{
			//noinspection BusyWait
			Thread.sleep(millis+1);
		}
		while((System.currentTimeMillis()-start)<=millis);
	}

	/**
	 * Calls {@link Query#search()} on the given query and returns the result.
	 * Prints the statement info to standard out.
	 * @deprecated for debugging only, should never be used in committed code
	 */
	@Deprecated // OK: for debugging
	public static Collection<?> infoSearch(final Query<?> query)
	{
		final Transaction transaction = query.getType().getModel().currentTransaction();
		transaction.setQueryInfoEnabled(true);
		final Collection<?> result = query.search();
		System.out.println("INFO-------------------");
		final List<QueryInfo> infos = transaction.getQueryInfos();
		transaction.setQueryInfoEnabled(false);
		//noinspection ConstantConditions OK: cannot be null after setQueryInfoEnabled(true)
		for(final QueryInfo info : infos)
			info.print(System.out);
		return result;
	}


	private Assert()
	{
		// prevent instantiation
	}
}
