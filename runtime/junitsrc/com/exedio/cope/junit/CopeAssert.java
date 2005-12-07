/*
 * Copyright (C) 2004-2005  exedio GmbH (www.exedio.com)
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
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import com.exedio.cope.Query;
import com.exedio.cope.StatementInfo;

public abstract class CopeAssert extends TestCase
{
	public final static void assertContainsList(final List expected, final Collection actual)
	{
		if(expected.size()!=actual.size() ||
				!expected.containsAll(actual) ||
				!actual.containsAll(expected))
			fail("expected "+expected+", but was "+actual);
	}

	public final static void assertContains(final Collection actual)
	{
		assertContainsList(Collections.EMPTY_LIST, actual);
	}

	public final static void assertContains(final Object o, final Collection actual)
	{
		assertContainsList(Collections.singletonList(o), actual);
	}

	public final static void assertContains(final Object o1, final Object o2, final Collection actual)
	{
		assertContainsList(Arrays.asList(new Object[]{o1, o2}), actual);
	}

	public final static void assertContains(final Object o1, final Object o2, final Object o3, final Collection actual)
	{
		assertContainsList(Arrays.asList(new Object[]{o1, o2, o3}), actual);
	}

	public final static void assertContains(final Object o1, final Object o2, final Object o3, final Object o4, final Collection actual)
	{
		assertContainsList(Arrays.asList(new Object[]{o1, o2, o3, o4}), actual);
	}

	public final static void assertContains(final Object o1, final Object o2, final Object o3, final Object o4, final Object o5, final Collection actual)
	{
		assertContainsList(Arrays.asList(new Object[]{o1, o2, o3, o4, o5}), actual);
	}

	public final static void assertContainsUnmodifiable(final Collection actual)
	{
		assertUnmodifiable(actual);
		assertContains(actual);
	}

	public final static void assertContainsUnmodifiable(final Object o, final Collection actual)
	{
		assertUnmodifiable(actual);
		assertContains(o, actual);
	}

	public final static void assertContainsUnmodifiable(final Object o1, final Object o2, final Collection actual)
	{
		assertUnmodifiable(actual);
		assertContains(o1, o2, actual);
	}

	public final static List list()
	{
		return Collections.EMPTY_LIST;
	}

	public final static List list(final Object o)
	{
		return Collections.singletonList(o);
	}
	
	public final static List list(final Object o1, final Object o2)
	{
		return Arrays.asList(new Object[]{o1, o2});
	}
	
	public final static List list(final Object o1, final Object o2, final Object o3)
	{
		return Arrays.asList(new Object[]{o1, o2, o3});
	}
	
	public final static List list(final Object o1, final Object o2, final Object o3, final Object o4)
	{
		return Arrays.asList(new Object[]{o1, o2, o3, o4});
	}
	
	public final static List list(final Object o1, final Object o2, final Object o3, final Object o4, final Object o5)
	{
		return Arrays.asList(new Object[]{o1, o2, o3, o4, o5});
	}
	
	public final static List list(final Object o1, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6)
	{
		return Arrays.asList(new Object[]{o1, o2, o3, o4, o5, o6});
	}
	
	public final static List list(final Object o1, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6, final Object o7)
	{
		return Arrays.asList(new Object[]{o1, o2, o3, o4, o5, o6, o7});
	}
	
	public final static List list(final Object o1, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6, final Object o7, final Object o8)
	{
		return Arrays.asList(new Object[]{o1, o2, o3, o4, o5, o6, o7, o8});
	}
	
	public final static List list(final Object o1, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6, final Object o7, final Object o8, final Object o9)
	{
		return Arrays.asList(new Object[]{o1, o2, o3, o4, o5, o6, o7, o8, o9});
	}
	
	public final static List list(final Object o1, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6, final Object o7, final Object o8, final Object o9, final Object o10)
	{
		return Arrays.asList(new Object[]{o1, o2, o3, o4, o5, o6, o7, o8, o9, o10});
	}
	
	public final static void assertUnmodifiable(final Collection c)
	{
		try
		{
			c.add(new Object());
			fail("should have thrown UnsupportedOperationException");
		}
		catch(UnsupportedOperationException e) {}
		try
		{
			c.addAll(Collections.singleton(new Object()));
			fail("should have thrown UnsupportedOperationException");
		}
		catch(UnsupportedOperationException e) {}
		
		if(!c.isEmpty())
		{
			final Object o = c.iterator().next();
			try
			{
				c.clear();
				fail("should have thrown UnsupportedOperationException");
			}
			catch(UnsupportedOperationException e) {}
			try
			{
				c.remove(o);
				fail("should have thrown UnsupportedOperationException");
			}
			catch(UnsupportedOperationException e) {}
			try
			{
				c.removeAll(Collections.singleton(o));
				fail("should have thrown UnsupportedOperationException");
			}
			catch(UnsupportedOperationException e) {}
			try
			{
				c.retainAll(Collections.EMPTY_LIST);
				fail("should have thrown UnsupportedOperationException");
			}
			catch(UnsupportedOperationException e) {}

			final Iterator iterator = c.iterator();
			try
			{
				iterator.remove();
				fail("should have thrown UnsupportedOperationException");
			}
			catch(UnsupportedOperationException e) {}
		}
	}
	
	public final static void assertEqualsUnmodifiable(final List expected, final Collection actual)
	{
		assertUnmodifiable(actual);
		assertEquals(expected, actual);
	}
	
	public static final void sleepForFileLastModified()
	{
		try
		{
			Thread.sleep(50); // more that resolution in assertWithinFileLastModified
		}
		catch(InterruptedException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	private static final String DATE_FORMAT_FULL = "dd.MM.yyyy HH:mm:ss.SSS";
	
	public final static void assertWithinFileLastModified(final Date expectedBefore, final Date expectedAfter, final Date actual)
	{
		final long resolution = 1000;
		final long leftTolerance = 10;
		final Date expectedBeforeFloor = new Date(((expectedBefore.getTime()-leftTolerance) / resolution) * resolution);
		final Date expectedAfterCeil   = new Date(((expectedAfter.getTime() / resolution) * resolution) + resolution);

		final SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT_FULL);
		final String message =
			"expected date within " + df.format(expectedBeforeFloor) + " (" + df.format(expectedBefore) + ")" +
			" and " + df.format(expectedAfterCeil) + " (" + df.format(expectedAfter) + ")" +
			", but was " + df.format(actual);

		assertTrue(message, !expectedBeforeFloor.after(actual));
		assertTrue(message, !expectedAfterCeil.before(actual));
	}

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
