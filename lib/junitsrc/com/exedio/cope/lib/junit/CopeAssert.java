
package com.exedio.cope.lib.junit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import com.exedio.cope.lib.Item;
import com.exedio.cope.lib.NestingRuntimeException;

import junit.framework.TestCase;

public class CopeAssert extends TestCase
{
	private ArrayList deleteOnTearDown = null;
	
	protected final void deleteOnTearDown(final Item item)
	{
		deleteOnTearDown.add(item);
	}
	
	protected void setUp() throws Exception
	{
		super.setUp();

		// ensure, that last test did call tearDown()
		assertEquals(null, deleteOnTearDown);
		deleteOnTearDown = new ArrayList();
	}
	
	protected void tearDown() throws Exception
	{
		if(!deleteOnTearDown.isEmpty())
		{
			for(ListIterator i = deleteOnTearDown.listIterator(deleteOnTearDown.size()); i.hasPrevious(); )
				((Item)i.previous()).delete();
			deleteOnTearDown.clear();
		}
		deleteOnTearDown = null;

		super.tearDown();
	}
	
	protected final static void assertContainsList(final List expected, final Collection actual)
	{
		if(expected.size()!=actual.size() ||
				!expected.containsAll(actual) ||
				!actual.containsAll(expected))
			fail("expected "+expected+", but was "+actual);
	}

	protected final static void assertContains(final Collection actual)
	{
		assertContainsList(Collections.EMPTY_LIST, actual);
	}

	protected final static void assertContains(final Object o, final Collection actual)
	{
		assertContainsList(Collections.singletonList(o), actual);
	}

	protected final static void assertContains(final Object o1, final Object o2, final Collection actual)
	{
		assertContainsList(Arrays.asList(new Object[]{o1, o2}), actual);
	}

	protected final static void assertContains(final Object o1, final Object o2, final Object o3, final Collection actual)
	{
		assertContainsList(Arrays.asList(new Object[]{o1, o2, o3}), actual);
	}

	protected final static void assertContains(final Object o1, final Object o2, final Object o3, final Object o4, final Collection actual)
	{
		assertContainsList(Arrays.asList(new Object[]{o1, o2, o3, o4}), actual);
	}

	protected final static void assertContains(final Object o1, final Object o2, final Object o3, final Object o4, final Object o5, final Collection actual)
	{
		assertContainsList(Arrays.asList(new Object[]{o1, o2, o3, o4, o5}), actual);
	}

	protected final static List list()
	{
		return Collections.EMPTY_LIST;
	}

	protected final static List list(final Object o)
	{
		return Collections.singletonList(o);
	}
	
	protected final static List list(final Object o1, final Object o2)
	{
		return Arrays.asList(new Object[]{o1, o2});
	}
	
	protected final static List list(final Object o1, final Object o2, final Object o3)
	{
		return Arrays.asList(new Object[]{o1, o2, o3});
	}
	
	protected final static List list(final Object o1, final Object o2, final Object o3, final Object o4)
	{
		return Arrays.asList(new Object[]{o1, o2, o3, o4});
	}
	
	protected final static List list(final Object o1, final Object o2, final Object o3, final Object o4, final Object o5)
	{
		return Arrays.asList(new Object[]{o1, o2, o3, o4, o5});
	}
	
	protected void assertUnmodifiable(final Collection c)
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
	
	protected final static Object waitForKey(final Object o)
	{
		System.out.println("WAITING FOR KEY");
		try
		{
			System.in.read();
		}
		catch(IOException e)
		{
			throw new NestingRuntimeException(e);
		}
		return o;
	}

	protected final static void waitForKey()
	{
		waitForKey(null);
	}

}
