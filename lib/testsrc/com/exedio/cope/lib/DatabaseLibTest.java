
package com.exedio.cope.lib;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.exedio.cope.lib.collision.CollisionItem1;
import com.exedio.cope.lib.collision.CollisionItem2;
import com.exedio.cope.lib.hierarchy.FirstSub;
import com.exedio.cope.lib.hierarchy.Super;

/**
 * An abstract test class for tests creating/using some persistent data.
 */
public abstract class DatabaseLibTest extends AbstractLibTest
{
	protected static final Type[] types = new Type[]
	{
		ItemWithSingleUnique.TYPE,
		ItemWithSingleUniqueReadOnly.TYPE,
		ItemWithSingleUniqueNotNull.TYPE,
		ItemWithoutAttributes.TYPE,
		ItemWithoutAttributes2.TYPE,
		ItemWithManyAttributes.TYPE,
		PointerItem2.TYPE,
		PointerItem.TYPE,
		Super.TYPE,
		FirstSub.TYPE,
		CollisionItem1.TYPE,
		CollisionItem2.TYPE,
	};
	
	private static boolean createdDatabase = false;
	private static boolean registeredDropDatabaseHook = false;
	private static Object lock = new Object(); 
	
	private static void createDatabase()
	{
		synchronized(lock)
		{
			if(!createdDatabase)
			{
				Database.theInstance.createDatabase();
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
						Database.theInstance.dropDatabase();
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
		Database.theInstance.checkEmptyTables();
	}
	
	protected void tearDown() throws Exception
	{
		Database.theInstance.checkEmptyTables();
		dropDatabase();
		super.tearDown();
	}
	
	protected void assertData(final byte[] expectedData, final InputStream actualData)
	{
		try
		{
			final byte[] actualDataArray = new byte[2*expectedData.length];
			final int actualLength = actualData.read(actualDataArray);
			assertEquals(expectedData.length, actualLength);
			for(int i = 0; i<actualLength; i++)
				assertEquals(expectedData[i], actualDataArray[i]);
		}
		catch(IOException e)
		{
			throw new SystemException(e);
		}
	}
	
	protected void assertMediaMime(final ItemWithManyAttributes item,
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
			throw new SystemException(e);
		}
		final String prefix = "/medias/ItemWithManyAttributes/someMedia/";
		final String pkString = (item.pk>=0) ? String.valueOf(item.pk) : "m"+(-item.pk);
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
		Database.theInstance.tearDownDatabase();
	}

}
