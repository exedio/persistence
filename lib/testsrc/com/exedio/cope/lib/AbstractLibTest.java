
package com.exedio.cope.lib;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import com.exedio.cope.lib.collision.CollisionItem1;
import com.exedio.cope.lib.collision.CollisionItem2;
import com.exedio.cope.lib.hierarchy.FirstSub;
import com.exedio.cope.lib.hierarchy.SecondSub;
import com.exedio.cope.lib.hierarchy.Super;

public abstract class AbstractLibTest extends TestCase
{
	
	public static final Type[] modelTypes = new Type[]
	{
		ItemWithSingleUnique.TYPE,
		ItemWithSingleUniqueReadOnly.TYPE,
		ItemWithSingleUniqueNotNull.TYPE,
		ItemWithDoubleUnique.TYPE,
		EmptyItem.TYPE,
		EmptyItem2.TYPE,
		AttributeItem.TYPE,
		AttributeEmptyItem.TYPE,
		StringItem.TYPE,
		MediaItem.TYPE,
		SumItem.TYPE,
		QualifiedItem.TYPE,
		QualifiedEmptyQualifier.TYPE,
		PointerItem2.TYPE,
		PointerItem.TYPE,
		Super.TYPE,
		FirstSub.TYPE,
		SecondSub.TYPE,
		CollisionItem1.TYPE,
		CollisionItem2.TYPE,
	};

	public static final Model model = new Model(modelTypes);
	
	protected void setUp() throws Exception
	{
		super.setUp();
		if(!model.hasProperties())
			model.setProperties(new Properties());
	}

	public AbstractLibTest()
	{}
	
	final static Integer i1 = new Integer(1);
	final static Integer i2 = new Integer(2);
	final static Integer i3 = new Integer(3);
	final static Integer i4 = new Integer(4);
	final static Integer i5 = new Integer(5);
	final static Integer i6 = new Integer(6);
	final static Integer i7 = new Integer(7);
	final static Integer i8 = new Integer(8);

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
	
	protected void assertContainsList(final List expected, final Collection actual)
	{
		if(expected.size()!=actual.size() ||
				!expected.containsAll(actual) ||
				!actual.containsAll(expected))
			fail("expected "+expected+", but was "+actual);
	}

	protected void assertContains(final Collection actual)
	{
		assertContainsList(Collections.EMPTY_LIST, actual);
	}

	protected void assertContains(final Object o, final Collection actual)
	{
		assertContainsList(Collections.singletonList(o), actual);
	}

	protected void assertContains(final Object o1, final Object o2, final Collection actual)
	{
		assertContainsList(Arrays.asList(new Object[]{o1, o2}), actual);
	}

	protected void assertContains(final Object o1, final Object o2, final Object o3, final Collection actual)
	{
		assertContainsList(Arrays.asList(new Object[]{o1, o2, o3}), actual);
	}

	protected void assertContains(final Object o1, final Object o2, final Object o3, final Object o4, final Collection actual)
	{
		assertContainsList(Arrays.asList(new Object[]{o1, o2, o3, o4}), actual);
	}

	protected void assertContains(final Object o1, final Object o2, final Object o3, final Object o4, final Object o5, final Collection actual)
	{
		assertContainsList(Arrays.asList(new Object[]{o1, o2, o3, o4, o5}), actual);
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
	
	protected List list(final Object o1, final Object o2, final Object o3, final Object o4)
	{
		return Arrays.asList(new Object[]{o1, o2, o3, o4});
	}
	
	protected List list(final Object o1, final Object o2, final Object o3, final Object o4, final Object o5)
	{
		return Arrays.asList(new Object[]{o1, o2, o3, o4, o5});
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
			throw new NestingRuntimeException(e);
		}
		return o;
	}

	protected void waitForKey()
	{
		waitForKey(null);
	}

}
