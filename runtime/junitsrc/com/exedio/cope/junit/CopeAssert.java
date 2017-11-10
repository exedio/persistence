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

package com.exedio.cope.junit;

import static java.util.Objects.requireNonNull;

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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import junit.framework.TestCase;
import org.junit.Assert;

public abstract class CopeAssert extends TestCase
{
	public static final void assertContainsList(final List<?> expected, final Collection<?> actual)
	{
		if(expected==null && actual==null)
			return;

		Assert.assertNotNull("expected null, but was " + actual, expected);
		Assert.assertNotNull("expected " + expected + ", but was null", actual);

		if(expected.size()!=actual.size() ||
				!expected.containsAll(actual) ||
				!actual.containsAll(expected))
			Assert.fail("expected "+expected+", but was "+actual);
	}

	public static final void assertContains(final Collection<?> actual)
	{
		assertContainsList(Collections.emptyList(), actual);
	}

	public static final void assertContains(final Object o, final Collection<?> actual)
	{
		assertContainsList(Collections.singletonList(o), actual);
	}

	public static final void assertContains(final Object o1, final Object o2, final Collection<?> actual)
	{
		assertContainsList(Arrays.asList(o1, o2), actual);
	}

	public static final void assertContains(final Object o1, final Object o2, final Object o3, final Collection<?> actual)
	{
		assertContainsList(Arrays.asList(o1, o2, o3), actual);
	}

	public static final void assertContains(final Object o1, final Object o2, final Object o3, final Object o4, final Collection<?> actual)
	{
		assertContainsList(Arrays.asList(o1, o2, o3, o4), actual);
	}

	public static final void assertContains(final Object o1, final Object o2, final Object o3, final Object o4, final Object o5, final Collection<?> actual)
	{
		assertContainsList(Arrays.asList(o1, o2, o3, o4, o5), actual);
	}

	public static final void assertContains(final Object o1, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6, final Collection<?> actual)
	{
		assertContainsList(Arrays.asList(o1, o2, o3, o4, o5, o6), actual);
	}

	public static final void assertContains(final Object o1, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6, final Object o7, final Collection<?> actual)
	{
		assertContainsList(Arrays.asList(o1, o2, o3, o4, o5, o6, o7), actual);
	}

	public static final void assertContainsUnmodifiable(final Collection<?> actual)
	{
		assertUnmodifiable(actual);
		assertContains(actual);
	}

	public static final void assertContainsUnmodifiable(final Object o, final Collection<?> actual)
	{
		assertUnmodifiable(actual);
		assertContains(o, actual);
	}

	public static final void assertContainsUnmodifiable(final Object o1, final Object o2, final Collection<?> actual)
	{
		assertUnmodifiable(actual);
		assertContains(o1, o2, actual);
	}

	public static final void assertContainsUnmodifiable(final Object o1, final Object o2, final Object o3, final Collection<?> actual)
	{
		assertUnmodifiable(actual);
		assertContains(o1, o2, o3, actual);
	}

	public static final void assertContainsUnmodifiable(final Object o1, final Object o2, final Object o3, final Object o4, final Collection<?> actual)
	{
		assertUnmodifiable(actual);
		assertContains(o1, o2, o3, o4, actual);
	}

	public static final List<Object> list(final Object... o)
	{
		return Collections.unmodifiableList(Arrays.asList(o));
	}

	public static final Map<Object, Object> map()
	{
		return Collections.unmodifiableMap(Collections.emptyMap());
	}

	public static final Map<Object, Object> map(final Object key1, final Object value1)
	{
		return Collections.unmodifiableMap(Collections.singletonMap(key1, value1));
	}

	public static final Map<Object, Object> map(final Object key1, final Object value1, final Object key2, final Object value2)
	{
		final HashMap<Object, Object> result = new HashMap<>();
		result.put(key1, value1);
		result.put(key2, value2);
		return Collections.unmodifiableMap(result);
	}

	@SuppressWarnings("SuspiciousMethodCalls")
	public static final <T> void assertUnmodifiable(final Collection<T> c)
	{
		try
		{
			c.add(null);
			Assert.fail("should have thrown UnsupportedOperationException");
		}
		catch(final UnsupportedOperationException ignored) {/*OK*/}
		try
		{
			c.addAll(Collections.singleton(null));
			Assert.fail("should have thrown UnsupportedOperationException");
		}
		catch(final UnsupportedOperationException ignored) {/*OK*/}

		if(!c.isEmpty())
		{
			final Object o = c.iterator().next();
			try
			{
				c.clear();
				Assert.fail("should have thrown UnsupportedOperationException");
			}
			catch(final UnsupportedOperationException ignored) {/*OK*/}
			try
			{
				c.remove(o);
				Assert.fail("should have thrown UnsupportedOperationException");
			}
			catch(final UnsupportedOperationException ignored) {/*OK*/}
			try
			{
				c.removeAll(Collections.singleton(o));
				Assert.fail("should have thrown UnsupportedOperationException");
			}
			catch(final UnsupportedOperationException ignored) {/*OK*/}
			try
			{
				c.retainAll(Collections.emptyList());
				Assert.fail("should have thrown UnsupportedOperationException");
			}
			catch(final UnsupportedOperationException ignored) {/*OK*/}

			final Iterator<?> iterator = c.iterator();
			try
			{
				iterator.next();
				iterator.remove();
				Assert.fail("should have thrown UnsupportedOperationException");
			}
			catch(final UnsupportedOperationException ignored) {/*OK*/}
		}

		if(c instanceof List<?>)
		{
			final List<T> l = (List<T>)c;

			if(!l.isEmpty())
			{
				try
				{
					l.set(0, null);
					Assert.fail("should have thrown UnsupportedOperationException");
				}
				catch(final UnsupportedOperationException ignored) {/*OK*/}
			}
		}
	}

	public static final void assertEqualsUnmodifiable(final List<?> expected, final Collection<?> actual)
	{
		assertUnmodifiable(actual);
		Assert.assertEquals(expected, actual);
	}

	public static final void assertEqualsUnmodifiable(final Map<?,?> expected, final Map<?,?> actual)
	{
		try
		{
			actual.clear();
			Assert.fail("should have thrown UnsupportedOperationException");
		}
		catch(final UnsupportedOperationException ignored) {/*OK*/}
		try
		{
			actual.put(null, null);
			Assert.fail("should have thrown UnsupportedOperationException");
		}
		catch(final UnsupportedOperationException ignored) {/*OK*/}
		try
		{
			actual.putAll(Collections.emptyMap());
			Assert.fail("should have thrown UnsupportedOperationException");
		}
		catch(final UnsupportedOperationException ignored) {/*OK*/}
		try
		{
			actual.remove(null);
			Assert.fail("should have thrown UnsupportedOperationException");
		}
		catch(final UnsupportedOperationException ignored) {/*OK*/}
		assertUnmodifiable(actual.keySet());
		assertUnmodifiable(actual.values());
		assertUnmodifiable(actual.entrySet());
		Assert.assertEquals(expected, actual);
	}

	public static void assertEqualsStrict(final Object expected, final Object actual)
	{
		Assert.assertEquals(expected, actual);
		Assert.assertEquals(actual, expected);
		if(expected!=null)
			Assert.assertEquals(expected.hashCode(), actual.hashCode());
	}

	public static void assertNotEqualsStrict(final Object expected, final Object actual)
	{
		Assert.assertTrue(!expected.equals(actual));
		Assert.assertTrue(!actual.equals(expected));
		Assert.assertTrue(expected.hashCode()!=actual.hashCode());
	}

	private static final String DATE_FORMAT_FULL = "dd.MM.yyyy HH:mm:ss.SSS";

	public static final void assertWithin(final Date expectedBefore, final Date expectedAfter, final Date actual)
	{
		final SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT_FULL, Locale.ENGLISH);
		final String message =
			"expected date within " + df.format(expectedBefore) +
			" and " + df.format(expectedAfter) +
			", but was " + df.format(actual);

		Assert.assertTrue(message, !expectedBefore.after(actual));
		Assert.assertTrue(message, !expectedAfter.before(actual));
	}

	public static final <S extends Serializable> S reserialize(final S value, final int expectedSize)
	{
		final byte[] bos = serialize(value);
		Assert.assertEquals(expectedSize, bos.length);
		@SuppressWarnings("unchecked")
		final S result = (S)deserialize(bos);
		return result;
	}

	public static final byte[] serialize(final Serializable value)
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

	public static final Object deserialize(final byte[] bytes)
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
	public static final void sleepLongerThan(final long millis) throws InterruptedException
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
	 */
	public static final Collection<?> infoSearch(final Query<?> query)
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
}
