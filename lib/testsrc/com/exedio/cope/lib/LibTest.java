
package com.exedio.cope.lib;

import com.exedio.cope.lib.database.Database;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import junit.framework.TestCase;

public class LibTest extends TestCase
{
	
	final Type[] types = new Type[]
	{
		ItemWithSingleUnique.TYPE,
		ItemWithSingleUniqueReadOnly.TYPE,
		ItemWithSingleUniqueNotNull.TYPE,
		ItemWithoutAttributes.TYPE,
		ItemWithoutAttributes2.TYPE,
		ItemWithManyAttributes.TYPE,
	};
	
	public LibTest()
	{}
	
	public void setUp()
	{
		Database.theInstance.createTables();
	}
	
	public void tearDown()
	{
		Database.theInstance.dropTables();
	}
	
	public void testLib()
	{
		// BEWARE:
		// if something does not compile,
		// it may be an error in the 
		// instrumentor as well.
		
		
		// ID, equals, hashCode
		{
			final ItemWithoutAttributes item1 = new ItemWithoutAttributes();
			final ItemWithoutAttributes item2 = new ItemWithoutAttributes();
			final ItemWithoutAttributes2 item3 = new ItemWithoutAttributes2();
			
			assertEquals(item1, item1);
			assertEquals(item2, item2);
			assertEquals(item3, item3);

			assertFalse(item1.equals(null));
			assertFalse(item2.equals(null));
			assertFalse(item3.equals(null));
			
			assertNotEquals(item1, item2);
			assertNotEquals(item1, item3);
			assertNotEquals(item2, item3);
			
			assertFalse(item1.equals("hello"));
			assertFalse(item1.equals(new Integer(1)));
			assertFalse(item1.equals(Boolean.TRUE));
		}
		
		// ItemWithSingleUnique
		{
			assertEquals(null, ItemWithSingleUnique.findByUniqueString("uniqueString"));

			final ItemWithSingleUnique item = new ItemWithSingleUnique();
			assertEquals(null, item.getUniqueString());
			assertEquals(null, item.findByUniqueString("uniqueString"));

			try
			{
				item.setUniqueString("uniqueString");
			}
			catch(UniqueViolationException e)
			{
				throw new SystemException(e);
			}
			assertEquals(null, item.getUniqueString());
			assertEquals(null, ItemWithSingleUnique.findByUniqueString("uniqueString"));
		}
		

		// ItemWithSingleUniqueReadOnly
		{
			assertEquals(null, ItemWithSingleUniqueReadOnly.findByUniqueReadOnlyString("uniqueString"));

			final ItemWithSingleUniqueReadOnly item;
			try
			{
				item = new ItemWithSingleUniqueReadOnly("uniqueString");
			}
			catch(UniqueViolationException e)
			{
				throw new SystemException(e);
			}
			assertEquals(null, item.getUniqueReadOnlyString());
			assertEquals(null, ItemWithSingleUniqueReadOnly.findByUniqueReadOnlyString("uniqueString"));
			
			try
			{
				item.setAttribute(item.uniqueReadOnlyString, "zapp");
				fail("should have thrown ReadOnlyViolationException");
			}
			catch(ReadOnlyViolationException e)
			{
				assertEquals(item.uniqueReadOnlyString, e.getReadOnlyAttribute());
				assertEquals(item, e.getItem());
			}
			catch(ConstraintViolationException e)
			{
				throw new SystemException(e);
			}
		}

	
		// ItemWithSingleNotNull
		{
			assertEquals(null, ItemWithSingleUniqueNotNull.findByUniqueNotNullString("uniqueString"));
			assertEquals(null, ItemWithSingleUniqueNotNull.findByUniqueNotNullString("uniqueString2"));

			final ItemWithSingleUniqueNotNull item;
			try
			{
				item = new ItemWithSingleUniqueNotNull("uniqueString");
			}
			catch(UniqueViolationException e)
			{
				throw new SystemException(e);
			}
			catch(NotNullViolationException e)
			{
				throw new SystemException(e);
			}
			assertEquals(null, item.getUniqueNotNullString());
			assertEquals(null, ItemWithSingleUniqueNotNull.findByUniqueNotNullString("uniqueString"));
			assertEquals(null, ItemWithSingleUniqueNotNull.findByUniqueNotNullString("uniqueString2"));

			try
			{
				item.setUniqueNotNullString("uniqueString2");
			}
			catch(UniqueViolationException e)
			{
				throw new SystemException(e);
			}
			catch(NotNullViolationException e)
			{
				throw new SystemException(e);
			}
			assertEquals(null, item.getUniqueNotNullString());
			assertEquals(null, ItemWithSingleUniqueNotNull.findByUniqueNotNullString("uniqueString"));
			assertEquals(null, ItemWithSingleUniqueNotNull.findByUniqueNotNullString("uniqueString2"));
		}
		
		// ItemWithManyAttributes
		{
			final ItemWithManyAttributes item = new ItemWithManyAttributes(5);
			assertEquals(null, item.getSomeString());
			assertEquals(null, item.getSomeInteger());
			try
			{
				assertEquals(5, item.getSomeNotNullInteger());
				fail("should have thrown NullPointerException, until there is some persistence mechanism");
			}
			catch(NullPointerException e)
			{}

			item.setSomeString("someString");
			assertEquals(null/*"someString"*/, item.getSomeString());

			item.setSomeInteger(new Integer(10));
			assertEquals(null/*new Integer(10)*/, item.getSomeInteger());

			item.setSomeNotNullInteger(20);
			try
			{
				assertEquals(20, item.getSomeNotNullInteger());
				fail("should have thrown NullPointerException, until there is some persistence mechanism");
			}
			catch(NullPointerException e)
			{}
			
			item.setSomeItem(null);
			assertEquals(null, item.getSomeItem());
			final ItemWithoutAttributes someItem = new ItemWithoutAttributes();
			item.setSomeItem(null);
			assertEquals(null/*someItem*/, item.getSomeItem());
			
			assertEquals(null, item.getSomeMediaURL());
			assertEquals(null, item.getSomeMediaURLSomeVariant());
			assertEquals(null, item.getSomeMediaData());
			assertEquals(null, item.getSomeMediaMimeMajor());
			assertEquals(null, item.getSomeMediaMimeMinor());
			try
			{
				item.setSomeMediaData(null/*some data*/, "someMimeMajor", "someMimeMinor");
			}
			catch(IOException e)
			{
				throw new SystemException(e);
			}
			assertEquals(null/*somehow gets the data*/, item.getSomeMediaURL());
			assertEquals(null/*somehow gets the data*/, item.getSomeMediaURLSomeVariant());
			assertEquals(null/*somehow gets the data*/, item.getSomeMediaData());
			assertEquals(null/*"someMimeMajor"*/, item.getSomeMediaMimeMajor());
			assertEquals(null/*"someMimeMinor"*/, item.getSomeMediaMimeMinor());
			try
			{
				item.setSomeMediaData(null, null, null);
			}
			catch(IOException e)
			{
				throw new SystemException(e);
			}
			assertEquals(null, item.getSomeMediaURL());
			assertEquals(null, item.getSomeMediaURLSomeVariant());
			assertEquals(null, item.getSomeMediaData());
			assertEquals(null, item.getSomeMediaMimeMajor());
			assertEquals(null, item.getSomeMediaMimeMinor());
			
			final ItemWithoutAttributes someItem2 = new ItemWithoutAttributes();
			assertEquals(null, item.getSomeQualifiedString(someItem));
			assertEquals(null, item.getSomeQualifiedString(someItem2));
			item.setSomeQualifiedString(someItem, "someQualifiedValue");
			assertEquals(null/*"someQualifiedValue"*/, item.getSomeQualifiedString(someItem));
			assertEquals(null, item.getSomeQualifiedString(someItem2));
			item.setSomeQualifiedString(someItem, null);
			assertEquals(null, item.getSomeQualifiedString(someItem));
			assertEquals(null, item.getSomeQualifiedString(someItem2));
		}
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

}
