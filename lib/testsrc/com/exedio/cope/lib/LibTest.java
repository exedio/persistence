
package com.exedio.cope.lib;

import com.exedio.cope.lib.database.Database;
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
		
		
		// ItemWithSingleUnique
		{
			assertEquals(null, ItemWithSingleUnique.findByUniqueString("uniqueString"));

			final ItemWithSingleUnique itemWithSingleUnique = new ItemWithSingleUnique();
			assertEquals(null, itemWithSingleUnique.getUniqueString());
			assertEquals(null, ItemWithSingleUnique.findByUniqueString("uniqueString"));

			try
			{
				itemWithSingleUnique.setUniqueString("uniqueString");
			}
			catch(UniqueViolationException e)
			{
				throw new SystemException(e);
			}
			assertEquals(null, itemWithSingleUnique.getUniqueString());
			assertEquals(null, ItemWithSingleUnique.findByUniqueString("uniqueString"));
		}
		

		// ItemWithSingleUniqueReadOnly
		{
			assertEquals(null, ItemWithSingleUniqueReadOnly.findByUniqueReadOnlyString("uniqueString"));

			final ItemWithSingleUniqueReadOnly itemWithSingleUniqueReadOnly;
			try
			{
				itemWithSingleUniqueReadOnly = new ItemWithSingleUniqueReadOnly("uniqueString");
			}
			catch(UniqueViolationException e)
			{
				throw new SystemException(e);
			}
			assertEquals(null, itemWithSingleUniqueReadOnly.getUniqueReadOnlyString());
			assertEquals(null, ItemWithSingleUniqueReadOnly.findByUniqueReadOnlyString("uniqueString"));
			
			try
			{
				itemWithSingleUniqueReadOnly.setAttribute(itemWithSingleUniqueReadOnly.uniqueReadOnlyString, "zapp");
				fail("should have thrown ReadOnlyViolationException");
			}
			catch(ReadOnlyViolationException e)
			{
				assertEquals(itemWithSingleUniqueReadOnly.uniqueReadOnlyString, e.getReadOnlyAttribute());
				// TODO: use equals, once equals is working
				assertTrue(itemWithSingleUniqueReadOnly==e.getItem());
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
