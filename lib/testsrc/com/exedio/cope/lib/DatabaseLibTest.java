
package com.exedio.cope.lib;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.ListIterator;

import com.exedio.cope.testmodel.AttributeItem;

/**
 * An abstract test class for tests creating/using some persistent data.
 */
public abstract class DatabaseLibTest extends AbstractLibTest
{
	private static boolean createdDatabase = false;
	private static boolean registeredDropDatabaseHook = false;
	private static Object lock = new Object(); 
	
	private ArrayList deleteOnTearDown = null;

	private static void createDatabase()
	{
		synchronized(lock)
		{
			if(!createdDatabase)
			{
				model.createDatabase();
				createdDatabase = true;
			}
		}
	}
	
	private void dropDatabase()
	{
		synchronized(lock)
		{
			if(!registeredDropDatabaseHook)
			{
				Runtime.getRuntime().addShutdownHook(new Thread(new Runnable(){
					public void run()
					{
						model.dropDatabase();
					}
				}));
				registeredDropDatabaseHook = true;
			}
		}
	}

	protected void setUp() throws Exception
	{
		super.setUp();
		createDatabase();
		model.checkEmptyDatabase();

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

		model.checkEmptyDatabase();
		dropDatabase();
		super.tearDown();
	}
	
	protected void deleteOnTearDown(final Item item)
	{
		deleteOnTearDown.add(item);
	}
	
	protected InputStream stream(byte[] data)
	{
		return new ByteArrayInputStream(data);
	}
	
	protected void assertData(final byte[] expectedData, final InputStream actualData)
	{
		try
		{
			final byte[] actualDataArray = new byte[2*expectedData.length];
			final int actualLength = actualData.read(actualDataArray);
			actualData.close();
			assertEquals(expectedData.length, actualLength);
			for(int i = 0; i<actualLength; i++)
				assertEquals(expectedData[i], actualDataArray[i]);
		}
		catch(IOException e)
		{
			throw new NestingRuntimeException(e);
		}
	}
	
	protected void assertMediaMime(final AttributeItem item,
											final String mimeMajor,
											final String mimeMinor,
											final byte[] data,
											final String url)
	{
		try
		{
			item.setSomeMediaData(new ByteArrayInputStream(data), mimeMajor, mimeMinor);
		}
		catch(IOException e)
		{
			throw new NestingRuntimeException(e);
		}
		final String prefix = "/medias/AttributeItem/someMedia/";
		final String pkString = pkString(item);
		final String expectedURL = prefix+pkString+'.'+url;
		final String expectedURLSomeVariant = prefix+"SomeVariant/"+pkString+'.'+url;
		//System.out.println(expectedURL);
		//System.out.println(item.getSomeMediaURL());
		assertEquals(expectedURL, item.getSomeMediaURL());
		assertEquals(expectedURLSomeVariant, item.getSomeMediaURLSomeVariant());
		//System.out.println(expectedURLSomeVariant);
		//System.out.println(item.getSomeMediaURL());
		assertData(data, item.getSomeMediaData());
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
	
	protected void assertID(final int id, final Item item)
	{
		assertTrue(item.getID()+"/"+id, item.getID().endsWith("."+id));
	}

	protected void assertDelete(final Item item)
			throws IntegrityViolationException
	{
		assertTrue(!item.isDeleted());
		item.delete();
		assertTrue(item.isDeleted());
	}

	public static void main(String[] args)
	{
		model.setProperties(new Properties());
		model.tearDownDatabase();
	}

}
