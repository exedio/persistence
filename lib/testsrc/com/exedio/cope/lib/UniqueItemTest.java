
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
			throw new NestingRuntimeException(e);
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
				throw new NestingRuntimeException(e);
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
			throw new NestingRuntimeException(e);
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
			throw new NestingRuntimeException(e);
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
			throw new NestingRuntimeException(e);
		}
		catch(NotNullViolationException e)
		{
			throw new NestingRuntimeException(e);
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
			throw new NestingRuntimeException(e);
		}
		catch(NotNullViolationException e)
		{
			throw new NestingRuntimeException(e);
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
			throw new NestingRuntimeException(e);
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
	
	public void testDoubleUnique()
		throws ConstraintViolationException
	{
		assertEquals("doubleUnique", ItemWithDoubleUnique.doubleUnique.getID());
		assertEquals(ItemWithDoubleUnique.TYPE, ItemWithDoubleUnique.doubleUnique.getType());
		assertEquals(
			list(ItemWithDoubleUnique.string, ItemWithDoubleUnique.integer),
			ItemWithDoubleUnique.doubleUnique.getUniqueAttributes());

		assertEquals(null, ItemWithDoubleUnique.findByDoubleUnique("a", 1));
		
		final ItemWithDoubleUnique a1 = new ItemWithDoubleUnique("a", 1);
		assertEquals(a1, ItemWithDoubleUnique.findByDoubleUnique("a", 1));
		
		final ItemWithDoubleUnique a2 = new ItemWithDoubleUnique("a", 2);
		assertEquals(a2, ItemWithDoubleUnique.findByDoubleUnique("a", 2));
		
		final ItemWithDoubleUnique b1 = new ItemWithDoubleUnique("b", 1);
		assertEquals(b1, ItemWithDoubleUnique.findByDoubleUnique("b", 1));
		
		final ItemWithDoubleUnique b2 = new ItemWithDoubleUnique("b", 2);
		assertEquals(b2, ItemWithDoubleUnique.findByDoubleUnique("b", 2));

		try
		{		
			new ItemWithDoubleUnique("b", 1);
			fail("should have thrown UniqueViolationException");
		}
		catch(UniqueViolationException e)
		{
			assertEquals(a1.doubleUnique, e.getConstraint());
		}
		assertEquals(b1, ItemWithDoubleUnique.findByDoubleUnique("b", 1));
		
		assertDelete(b2);
		assertDelete(b1);

		final ItemWithDoubleUnique b1X = new ItemWithDoubleUnique("b", 1);
		assertEquals(b1X, ItemWithDoubleUnique.findByDoubleUnique("b", 1));

		assertDelete(a2);
		assertDelete(a1);
		assertDelete(b1X);
	}

}
