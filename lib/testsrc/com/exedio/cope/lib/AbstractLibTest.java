
package com.exedio.cope.lib;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

public class AbstractLibTest extends TestCase
{
	public AbstractLibTest()
	{}
	
	public void setUp()
	{
		Database.theInstance.createTables();
	}
	
	public void tearDown()
	{
		Database.theInstance.dropTables();
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


}
