
package com.exedio.cope.lib;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

public abstract class AbstractLibTest extends TestCase
{
	protected static final Type[] types = new Type[]
	{
		ItemWithSingleUnique.TYPE,
		ItemWithSingleUniqueReadOnly.TYPE,
		ItemWithSingleUniqueNotNull.TYPE,
		ItemWithoutAttributes.TYPE,
		ItemWithoutAttributes2.TYPE,
		ItemWithManyAttributes.TYPE,
	};
	
	private static boolean createdTables = false;
	private static boolean registeredDropTableHook = false;
	private static Object lock = new Object(); 
	
	private static void createTables()
	{
		synchronized(lock)
		{
			if(!createdTables)
			{
				Database.theInstance.createTables();
				createdTables = true;
			}
		}
	}
	
	private void dropTables()
	{
		synchronized(lock)
		{
			if(!registeredDropTableHook)
			{
				Runtime.getRuntime().addShutdownHook(new Thread(new Runnable(){
					public void run()
					{
						Database.theInstance.dropTables();
					}
				}));
				registeredDropTableHook = true;
			}
		}
	}
	
	public AbstractLibTest()
	{}
	
	public void setUp() throws Exception
	{
		super.setUp();
		createTables();
	}
	
	public void tearDown() throws Exception
	{
		dropTables();
		super.tearDown();
	}
	
	protected void assertMediaMime(final ItemWithManyAttributes item,
											final String mimeMajor,
											final String mimeMinor,
											final String url)
	{
		try
		{
			item.setSomeMediaData(null/*some data*/, mimeMajor, mimeMinor);
		}
		catch(IOException e)
		{
			throw new SystemException(e);
		}
		final String prefix = "/medias/com.exedio.cope.lib.ItemWithManyAttributes/someMedia/";
		final String expectedURL = prefix+item.pk+'.'+url;
		final String expectedURLSomeVariant = prefix+"SomeVariant/"+item.pk+'.'+url;
		//System.out.println(expectedURL);
		//System.out.println(item.getSomeMediaURL());
		assertEquals(expectedURL, item.getSomeMediaURL());
		assertEquals(expectedURLSomeVariant, item.getSomeMediaURLSomeVariant());
		//System.out.println(expectedURLSomeVariant);
		//System.out.println(item.getSomeMediaURL());
		assertEquals(null/*somehow gets the data*/, item.getSomeMediaData());
		assertEquals(mimeMajor, item.getSomeMediaMimeMajor());
		assertEquals(mimeMinor, item.getSomeMediaMimeMinor());
	}

	protected void assertNotEquals(final Item item1, final Item item2)
	{
		assertFalse(item1.equals(item2));
		assertFalse(item2.equals(item1));
		assertFalse(item1.getID().equals(item2.getID()));
		assertFalse(item1.hashCode()==item2.hashCode());
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

	protected List list(final Object o)
	{
		return Collections.singletonList(o);
	}
	
	protected Object waitForKey(final Object o)
	{
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

	public static void main(String[] args)
	{
		Database.theInstance.tearDownTables();
	}

}
