
package com.exedio.cope.lib;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

public abstract class AbstractLibTest extends TestCase
{
	
	public AbstractLibTest()
	{}
	
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
			c.addAll(Collections.EMPTY_LIST);
			fail("should have thrown UnsupportedOperationException");
		}
		catch(UnsupportedOperationException e) {}
		try
		{
			c.clear();
			fail("should have thrown UnsupportedOperationException");
		}
		catch(UnsupportedOperationException e) {}
		try
		{
			c.remove(new Object());
			fail("should have thrown UnsupportedOperationException");
		}
		catch(UnsupportedOperationException e) {}
		try
		{
			c.removeAll(Collections.EMPTY_LIST);
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
	
	protected Set toSet(final Collection collection)
	{
		return new HashSet(collection);
	}

	protected Set set()
	{
		return Collections.EMPTY_SET;
	}

	protected Set set(final Object o)
	{
		return Collections.singleton(o);
	}

	protected Set set(final Object o1, final Object o2)
	{
		return new HashSet(Arrays.asList(new Object[]{o1, o2}));
	}

	protected Set set(final Object o1, final Object o2, final Object o3)
	{
		return new HashSet(Arrays.asList(new Object[]{o1, o2, o3}));
	}

	protected List list()
	{
		return Collections.EMPTY_LIST;
	}

	protected List list(final Object o)
	{
		return Collections.singletonList(o);
	}
	
	protected List list(final Object o1, final Object o2)
	{
		return Arrays.asList(new Object[]{o1, o2});
	}
	
	protected List list(final Object o1, final Object o2, final Object o3)
	{
		return Arrays.asList(new Object[]{o1, o2, o3});
	}
	
	protected Object waitForKey(final Object o)
	{
		System.out.println("WAITING FOR KEY");
		try
		{
			System.in.read();
		}
		catch(IOException e)
		{
			throw new SystemException(e);
		}
		return o;
	}

	protected void waitForKey()
	{
		waitForKey(null);
	}

}
