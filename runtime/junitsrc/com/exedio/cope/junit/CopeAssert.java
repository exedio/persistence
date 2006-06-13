/*
 * Copyright (C) 2004-2006  exedio GmbH (www.exedio.com)
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

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import com.exedio.cope.Query;
import com.exedio.cope.StatementInfo;

public abstract class CopeAssert extends TestCase
{
	public final static void assertContainsList(final List<? extends Object> expected, final Collection<? extends Object> actual)
	{
		if(expected==null && actual==null)
			return;
		
		assertNotNull("expected null, but was " + actual, expected);
		assertNotNull("expected " + expected + ", but was null", actual);
		
		if(expected.size()!=actual.size() ||
				!expected.containsAll(actual) ||
				!actual.containsAll(expected))
			fail("expected "+expected+", but was "+actual);
	}

	public final static void assertContains(final Collection<? extends Object> actual)
	{
		assertContainsList(Collections.<Object>emptyList(), actual);
	}

	public final static void assertContains(final Object o, final Collection<? extends Object> actual)
	{
		assertContainsList(Collections.singletonList(o), actual);
	}

	public final static void assertContains(final Object o1, final Object o2, final Collection<? extends Object> actual)
	{
		assertContainsList(Arrays.asList(new Object[]{o1, o2}), actual);
	}

	public final static void assertContains(final Object o1, final Object o2, final Object o3, final Collection<? extends Object> actual)
	{
		assertContainsList(Arrays.asList(new Object[]{o1, o2, o3}), actual);
	}

	public final static void assertContains(final Object o1, final Object o2, final Object o3, final Object o4, final Collection<? extends Object> actual)
	{
		assertContainsList(Arrays.asList(new Object[]{o1, o2, o3, o4}), actual);
	}

	public final static void assertContains(final Object o1, final Object o2, final Object o3, final Object o4, final Object o5, final Collection<? extends Object> actual)
	{
		assertContainsList(Arrays.asList(new Object[]{o1, o2, o3, o4, o5}), actual);
	}

	public final static void assertContainsUnmodifiable(final Collection<? extends Object> actual)
	{
		assertUnmodifiable(actual);
		assertContains(actual);
	}

	public final static void assertContainsUnmodifiable(final Object o, final Collection<? extends Object> actual)
	{
		assertUnmodifiable(actual);
		assertContains(o, actual);
	}

	public final static void assertContainsUnmodifiable(final Object o1, final Object o2, final Collection<? extends Object> actual)
	{
		assertUnmodifiable(actual);
		assertContains(o1, o2, actual);
	}

	public final static List<? extends Object> list()
	{
		return Collections.<Object>emptyList();
	}

	public final static List<? extends Object> list(final Object o)
	{
		return Collections.singletonList(o);
	}
	
	public final static List<? extends Object> list(final Object o1, final Object o2)
	{
		return Arrays.asList(new Object[]{o1, o2});
	}
	
	public final static List<? extends Object> list(final Object o1, final Object o2, final Object o3)
	{
		return Arrays.asList(new Object[]{o1, o2, o3});
	}
	
	public final static List<? extends Object> list(final Object o1, final Object o2, final Object o3, final Object o4)
	{
		return Arrays.asList(new Object[]{o1, o2, o3, o4});
	}
	
	public final static List<? extends Object> list(final Object o1, final Object o2, final Object o3, final Object o4, final Object o5)
	{
		return Arrays.asList(new Object[]{o1, o2, o3, o4, o5});
	}
	
	public final static List<? extends Object> list(final Object o1, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6)
	{
		return Arrays.asList(new Object[]{o1, o2, o3, o4, o5, o6});
	}
	
	public final static List<? extends Object> list(final Object o1, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6, final Object o7)
	{
		return Arrays.asList(new Object[]{o1, o2, o3, o4, o5, o6, o7});
	}
	
	public final static List<? extends Object> list(final Object o1, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6, final Object o7, final Object o8)
	{
		return Arrays.asList(new Object[]{o1, o2, o3, o4, o5, o6, o7, o8});
	}
	
	public final static List<? extends Object> list(final Object o1, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6, final Object o7, final Object o8, final Object o9)
	{
		return Arrays.asList(new Object[]{o1, o2, o3, o4, o5, o6, o7, o8, o9});
	}
	
	public final static List<? extends Object> list(final Object o1, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6, final Object o7, final Object o8, final Object o9, final Object o10)
	{
		return Arrays.asList(new Object[]{o1, o2, o3, o4, o5, o6, o7, o8, o9, o10});
	}
	
	public final static <T> List<T> listg()
	{
		return Collections.<T>emptyList();
	}

	public final static <T> List<T> listg(final T o)
	{
		return Collections.singletonList(o);
	}
	
	@SuppressWarnings("unchecked") // OK: no generic array creation
	public final static <T> List<T> listg(final T o1, final T o2)
	{
		return Arrays.asList((T[])new Object[]{o1, o2});
	}
	
	@SuppressWarnings("unchecked") // OK: no generic array creation
	public final static <T> List<T> listg(final T o1, final T o2, final T o3)
	{
		return Arrays.asList((T[])new Object[]{o1, o2, o3});
	}
	
	public final static Map<? extends Object, ? extends Object> map()
	{
		return Collections.<Object, Object>emptyMap();
	}

	public final static Map<? extends Object, ? extends Object> map(final Object key1, final Object value1)
	{
		return Collections.<Object, Object>singletonMap(key1, value1);
	}

	public final static Map<? extends Object, ? extends Object> map(final Object key1, final Object value1, final Object key2, final Object value2)
	{
		final HashMap<Object, Object> result = new HashMap<Object, Object>();
		result.put(key1, value1);
		result.put(key2, value2);
		return result;
	}

	public final static <T extends Object> void assertUnmodifiable(final Collection<T> c)
	{
		try
		{
			c.add((T)null);
			fail("should have thrown UnsupportedOperationException");
		}
		catch(UnsupportedOperationException e) {/*OK*/}
		try
		{
			c.addAll(Collections.singleton((T)null));
			fail("should have thrown UnsupportedOperationException");
		}
		catch(UnsupportedOperationException e) {/*OK*/}
		
		if(!c.isEmpty())
		{
			final Object o = c.iterator().next();
			try
			{
				c.clear();
				fail("should have thrown UnsupportedOperationException");
			}
			catch(UnsupportedOperationException e) {/*OK*/}
			try
			{
				c.remove(o);
				fail("should have thrown UnsupportedOperationException");
			}
			catch(UnsupportedOperationException e) {/*OK*/}
			try
			{
				c.removeAll(Collections.singleton(o));
				fail("should have thrown UnsupportedOperationException");
			}
			catch(UnsupportedOperationException e) {/*OK*/}
			try
			{
				c.retainAll(Collections.EMPTY_LIST);
				fail("should have thrown UnsupportedOperationException");
			}
			catch(UnsupportedOperationException e) {/*OK*/}

			final Iterator iterator = c.iterator();
			try
			{
				iterator.remove();
				fail("should have thrown UnsupportedOperationException");
			}
			catch(UnsupportedOperationException e) {/*OK*/}
		}
	}
	
	public final static void assertEqualsUnmodifiable(final List<? extends Object> expected, final Collection<? extends Object> actual)
	{
		assertUnmodifiable(actual);
		assertEquals(expected, actual);
	}
	
	public final static void assertEqualsUnmodifiable(final Map<? extends Object, ? extends Object> expected, final Map<? extends Object, ? extends Object> actual)
	{
		assertUnmodifiable(actual.keySet());
		assertUnmodifiable(actual.values());
		assertUnmodifiable(actual.entrySet());
		assertEquals(expected, actual);
	}
	
	private static final String DATE_FORMAT_FULL = "dd.MM.yyyy HH:mm:ss.SSS";
	
	public final static void assertWithin(final Date expectedBefore, final Date expectedAfter, final Date actual)
	{
		final SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT_FULL);
		final String message =
			"expected date within " + df.format(expectedBefore) +
			" and " + df.format(expectedAfter) +
			", but was " + df.format(actual);

		assertTrue(message, !expectedBefore.after(actual));
		assertTrue(message, !expectedAfter.before(actual));
	}
	
	public final static Object waitForKey(final Object o)
	{
		System.out.println("WAITING FOR KEY");
		try
		{
			System.in.read();
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}
		return o;
	}

	public final static void waitForKey()
	{
		waitForKey(null);
	}

	/**
	 * Calls {@link Query#search()} on the given query and returns the result.
	 * Prints the statement info to standard out.
	 */
	public static final Collection infoSearch(final Query query)
	{
		query.enableMakeStatementInfo();
		final Collection result = query.search();
		System.out.println("INFO-------------------");
		final StatementInfo info = query.getStatementInfo();
		if(info==null)
			System.out.println("NONE !!");
		else
			info.print(System.out);
		return result;
	}

}
