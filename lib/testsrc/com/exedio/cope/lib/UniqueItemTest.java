
package com.exedio.cope.lib;


public class UniqueItemTest extends DatabaseLibTest
{
	public UniqueItemTest()
	{
	}

	public void testItemWithSingleUnique()
			throws IntegrityViolationException
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
		assertEquals("uniqueString", item.getUniqueString());
		assertEquals(item, ItemWithSingleUnique.findByUniqueString("uniqueString"));

		// test unique violation
		{
			final ItemWithSingleUnique item2 = new ItemWithSingleUnique();
			try
			{
				item2.setUniqueString("uniqueString2");
			}
			catch(UniqueViolationException e)
			{
				throw new SystemException(e);
			}
			try
			{
				item2.setUniqueString("uniqueString");
				fail("should have thrown UniqueViolationException");
			}
			catch(UniqueViolationException e)
			{
				assertEquals(item2.uniqueString.getSingleUniqueConstaint(), e.getConstraint());
			}
			assertEquals("uniqueString2", item2.getUniqueString());
			assertEquals(item2, ItemWithSingleUnique.findByUniqueString("uniqueString2"));

			assertDelete(item2);
		}

		item.passivate();
		assertTrue(!item.isActive());
		assertEquals("uniqueString", item.getUniqueString());
		assertTrue(item.isActive());

		final ItemWithSingleUnique firstFoundItem;
		{
			item.passivate();
			assertTrue(!item.isActive());
			final ItemWithSingleUnique foundItem = ItemWithSingleUnique.findByUniqueString("uniqueString");
			assertEquals(item, foundItem);
			assertEquals(item.getID(), foundItem.getID());
			assertEquals(item.hashCode(), foundItem.hashCode());
			assertNotSame(item, foundItem);
			assertTrue(!item.isActive());
			assertTrue(!foundItem.isActive());

			assertSame(item, item.activeItem());
			assertTrue(item.isActive());
			assertTrue(!foundItem.isActive());

			assertSame(item, foundItem.activeItem());
			assertTrue(item.isActive());
			assertTrue(!foundItem.isActive());

			firstFoundItem = foundItem;
		}
		{
			item.passivate();
			assertTrue(!item.isActive());
			final ItemWithSingleUnique foundItem = ItemWithSingleUnique.findByUniqueString("uniqueString");
			assertEquals("uniqueString", foundItem.getUniqueString());
			assertEquals("uniqueString", item.getUniqueString());
			assertEquals(item, foundItem);
			assertEquals(item.getID(), foundItem.getID());
			assertEquals(item.hashCode(), foundItem.hashCode());
			assertNotSame(item, foundItem);
			assertNotSame(item, firstFoundItem);
			assertNotSame(foundItem, firstFoundItem);
			assertTrue(!item.isActive());
			assertTrue(foundItem.isActive());
			assertSame(foundItem, item.activeItem());
			assertSame(foundItem, foundItem.activeItem());
		}
		assertDelete(item);
	}

	public void testItemWithSingleUniqueReadOnly()
			throws IntegrityViolationException
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
		assertEquals("uniqueString", item.getUniqueReadOnlyString());
		assertEquals(item, ItemWithSingleUniqueReadOnly.findByUniqueReadOnlyString("uniqueString"));

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
		assertEquals("uniqueString", item.getUniqueReadOnlyString());
		assertEquals(item, ItemWithSingleUniqueReadOnly.findByUniqueReadOnlyString("uniqueString"));

		assertDelete(item);
	}

	public void testItemWithSingleUniqueNotNull()
			throws IntegrityViolationException
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
		assertEquals("uniqueString", item.getUniqueNotNullString());
		assertEquals(item, ItemWithSingleUniqueNotNull.findByUniqueNotNullString("uniqueString"));
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
		assertEquals("uniqueString2", item.getUniqueNotNullString());
		assertEquals(null, ItemWithSingleUniqueNotNull.findByUniqueNotNullString("uniqueString"));
		assertEquals(item, ItemWithSingleUniqueNotNull.findByUniqueNotNullString("uniqueString2"));

		try
		{
			item.setUniqueNotNullString(null);
			fail("should have thrown NotNullViolationException");
		}
		catch(UniqueViolationException e)
		{
			throw new SystemException(e);
		}
		catch(NotNullViolationException e)
		{
			assertEquals(item.uniqueNotNullString, e.getNotNullAttribute());
			assertEquals(item, e.getItem());
		}
		assertEquals("uniqueString2", item.getUniqueNotNullString());
		assertEquals(null, ItemWithSingleUniqueNotNull.findByUniqueNotNullString("uniqueString"));
		assertEquals(item, ItemWithSingleUniqueNotNull.findByUniqueNotNullString("uniqueString2"));

		assertDelete(item);
	}

}
