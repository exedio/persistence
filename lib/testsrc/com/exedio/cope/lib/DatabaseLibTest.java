
package com.exedio.cope.lib;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import com.exedio.cope.lib.collision.CollisionItem1;
import com.exedio.cope.lib.collision.CollisionItem2;
import com.exedio.cope.lib.hierarchy.FirstSub;

/**
 * An abstract test class for tests creating/using some persistent data.
 */
public abstract class DatabaseLibTest extends AbstractLibTest
{
	private static boolean createdDatabase = false;
	private static boolean registeredDropDatabaseHook = false;
	private static Object lock = new Object(); 
	
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
	}
	
	protected void tearDown() throws Exception
	{
		model.checkEmptyDatabase();
		dropDatabase();
		super.tearDown();
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
			throw new SystemException(e);
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
			throw new SystemException(e);
		}
		final String prefix = "/medias/AttributeItem/someMedia/";
		final String pkString = String.valueOf(Search.pk2id(item.pk));
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

	public static void initializeExampleSystem()
	{
		try
		{
			{
				final ItemWithSingleUnique item1 = new ItemWithSingleUnique();
				item1.setUniqueString("item1");
				final ItemWithSingleUnique item2 = new ItemWithSingleUnique();
				item2.setUniqueString("item2");
			}
			
			new ItemWithSingleUniqueReadOnly("item1");
			new ItemWithSingleUniqueReadOnly("item2");
			
			new ItemWithSingleUniqueNotNull("item1");
			new ItemWithSingleUniqueNotNull("item2");
			
			new ItemWithDoubleUnique("string1", 1);
			new ItemWithDoubleUnique("string1", 2);
			new ItemWithDoubleUnique("string2", 1);
			new ItemWithDoubleUnique("string2", 2);
			
			final EmptyItem emptyItem1 = new EmptyItem();
			final EmptyItem emptyItem2 = new EmptyItem();
			final EmptyItem emptyItem3 = new EmptyItem();
			new EmptyItem2();
			
			new AttributeItem("someString1", 5, 6l, 2.2, true, emptyItem1, AttributeItem.SomeEnumeration.enumValue1);
			new AttributeItem("someString2", 6, 7l, 2.3, true, emptyItem2, AttributeItem.SomeEnumeration.enumValue2);
			new AttributeItem("someString3", 7, 8l, 2.4, false, emptyItem2, AttributeItem.SomeEnumeration.enumValue2);
			final Date date = new Date(1087368238214l);
			for(int i = 0; i<102; i++)
			{
				final AttributeItem attributeItem = new AttributeItem("running"+i, 7+i, 8l+i, 2.4+i, (i%2)==0, emptyItem2, AttributeItem.SomeEnumeration.enumValue2);
				attributeItem.setSomeDate(date);
			}

			{			
				final StringItem item1 = new StringItem();
				final StringItem item2 = new StringItem();
				final StringItem item3 = new StringItem();
				
				item1.setAny("any1");
				item1.setMin4("min4");
				item1.setMax4("max4");
				item1.setMin4Max8("min4max8");
				
				item2.setAny("any1");
				item2.setMin4("min4");
				item2.setMax4("max4");
				item2.setMin4Max8("m4x8");
			}
			
			new MediaItem();
			new MediaItem();
			new MediaItem();
			
			new SumItem(1, 2, 3);
			new SumItem(4, 5, 4);
			new SumItem(9, 2, 6);
			new SumItem(2, 8, 1);
			new SumItem(5, 6, 7);
			new SumItem(3, 5, 9);
			new SumItem(6, 4, 0);
			new SumItem(8, 1, 2);
			new SumItem(2, 9, 7);
			new SumItem(5, 2, 0);
			new SumItem(6, 7, 6);

			{
				final PointerItem2 item2a = new PointerItem2("hallo");
				final PointerItem2 item2b = new PointerItem2("bello");
				final PointerItem item1a = new PointerItem("bello", item2a);
				final PointerItem item1b = new PointerItem("collo", item2b);
				item1a.setSelf(item1a);
				item1b.setSelf(item1a);
			}
			
			new FirstSub(1);
			new FirstSub(2);
			
			new CollisionItem1(emptyItem1);
			new CollisionItem1(emptyItem2);
			new CollisionItem2(emptyItem1);
			new CollisionItem2(emptyItem2);
		}
		catch(ConstraintViolationException e)
		{
			throw new SystemException(e);
		}
	}

	public static void main(String[] args)
	{
		model.tearDownDatabase();
	}

}
