
package com.exedio.cope.lib;

import com.exedio.cope.lib.Database;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
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
		
		dotestItemMethods();
		dotestItemWithSingleUnique();
		dotestItemWithSingleUniqueReadOnly();
		dotestItemWithSingleNotNull();
		dotestItemWithManyAttributes();
		dotestContinuousPrimaryKeyGeneration();
	}

	/**
	 * type, ID, equals, hashCode
	 */
	private void dotestItemMethods()
	{
		assertEquals(ItemWithoutAttributes.TYPE, Type.getType(ItemWithoutAttributes.class.getName()));
		assertEquals(ItemWithoutAttributes2.TYPE, Type.getType(ItemWithoutAttributes2.class.getName()));
		assertEquals(toSet(Arrays.asList(types)), toSet(Type.getTypes()));

		final ItemWithoutAttributes item1 = new ItemWithoutAttributes();
		final ItemWithoutAttributes item2 = new ItemWithoutAttributes();
		final ItemWithoutAttributes2 item3 = new ItemWithoutAttributes2();

		assertEquals(ItemWithoutAttributes.TYPE, item1.getType());
		assertEquals(ItemWithoutAttributes.TYPE, item2.getType());
		assertEquals(ItemWithoutAttributes2.TYPE, item3.getType());

		assertEquals(item1, Search.findByID(item1.getID()));
		assertEquals(item2, Search.findByID(item2.getID()));
		assertEquals(item3, Search.findByID(item3.getID()));

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
		
	private void dotestItemWithSingleUnique()
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
				assertEquals(list(item2.uniqueString), e.getConstraint().getUniqueAttributes());
			}
			assertEquals("uniqueString2", item2.getUniqueString());
			assertEquals(item2, ItemWithSingleUnique.findByUniqueString("uniqueString2"));
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
	}

	private void dotestItemWithSingleUniqueReadOnly()
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
	}


	private void dotestItemWithSingleNotNull()
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
	}

	private void dotestItemWithManyAttributes()
	{
		final ItemWithoutAttributes someItem = new ItemWithoutAttributes();
		final ItemWithoutAttributes someItem2 = new ItemWithoutAttributes();
		final ItemWithManyAttributes item;
		try
		{
			item = new ItemWithManyAttributes("someString", 5, true, someItem);
		}
		catch(NotNullViolationException e)
		{
			throw new SystemException(e);
		}
		dotestItemWithManyAttributesSomeString(item);
		dotestItemWithManyAttributesSomeNotNullString(item);

		dotestItemWithManyAttributesSomeInteger(item);
		dotestItemWithManyAttributesSomeNotNullInteger(item);

		dotestItemWithManyAttributesSomeBoolean(item);
		dotestItemWithManyAttributesSomeNotNullBoolean(item);

		dotestItemWithManyAttributesSomeItem(item, someItem);
		dotestItemWithManyAttributesSomeNotNullItem(item, someItem, someItem2);

		dotestItemWithManyAttributesSomeEnumeration(item);
		// TODO: dotestItemWithManyAttributesSomeNotNullEnumeration(item);

		dotestItemWithManyAttributesSomeMedia(item);
		// TODO: dotestItemWithManyAttributesSomeNotNullMedia(item);

		dotestItemWithManyAttributesSomeQualifiedAttribute(item, someItem);
	}

	private void dotestItemWithManyAttributesSomeString(final ItemWithManyAttributes item)
	{
		assertEquals(item.TYPE, item.someString.getType());
		assertEquals(item.TYPE, item.someStringUpperCase.getType());
		assertEquals(null, item.getSomeString());
		assertEquals(null, item.getSomeStringUpperCase());
		item.setSomeString("someString");
		assertEquals("someString", item.getSomeString());
		assertEquals("SOMESTRING", item.getSomeStringUpperCase());
		assertEquals(set(item), toSet(Search.search(item.TYPE, Search.equal(item.someString, "someString"))));
		assertEquals(set(),     toSet(Search.search(item.TYPE, Search.equal(item.someString, "SOMESTRING"))));
		assertEquals(set(item), toSet(Search.search(item.TYPE, Search.equal(item.someStringUpperCase, "SOMESTRING"))));
		assertEquals(set(),     toSet(Search.search(item.TYPE, Search.equal(item.someStringUpperCase, "someString"))));
		item.passivate();
		assertEquals("someString", item.getSomeString());
		assertEquals("SOMESTRING", item.getSomeStringUpperCase());
		item.setSomeString(null);
		assertEquals(null, item.getSomeString());
		assertEquals(null, item.getSomeStringUpperCase());
	}

	private void dotestItemWithManyAttributesSomeNotNullString(final ItemWithManyAttributes item)
	{
		assertEquals(item.TYPE, item.someNotNullString.getType());
		assertEquals("someString", item.getSomeNotNullString());
		try
		{
			item.setSomeNotNullString("someOtherString");
		}
		catch(NotNullViolationException e)
		{
			throw new SystemException(e);
		}
		assertEquals("someOtherString", item.getSomeNotNullString());
		try
		{
			item.setSomeNotNullString(null);
		}
		catch(NotNullViolationException e)
		{
			assertEquals(item.someNotNullString, e.getNotNullAttribute());
			assertEquals(item, e.getItem());
		}
	}

	private void dotestItemWithManyAttributesSomeInteger(final ItemWithManyAttributes item)
	{
		assertEquals(item.TYPE, item.someInteger.getType());
		assertEquals(null, item.getSomeInteger());
		// TODO: assertEquals(list(item), Search.search(item.TYPE, Search.equal(item.someInteger, null)));
		item.setSomeInteger(new Integer(10));
		assertEquals(new Integer(10), item.getSomeInteger());
		assertEquals(list(item), Search.search(item.TYPE, Search.equal(item.someInteger, 10)));
		item.setSomeInteger(null);
		assertEquals(null, item.getSomeInteger());
	}

	private void dotestItemWithManyAttributesSomeNotNullInteger(final ItemWithManyAttributes item)
	{
		assertEquals(item.TYPE, item.someNotNullInteger.getType());
		assertEquals(5, item.getSomeNotNullInteger());
		item.setSomeNotNullInteger(20);
		assertEquals(20, item.getSomeNotNullInteger());

		item.setSomeNotNullInteger(0);
		assertEquals(0, item.getSomeNotNullInteger());
		assertEquals(set(item), toSet(Search.search(item.TYPE, Search.equal(item.someNotNullInteger, 0))));

		item.setSomeNotNullInteger(Integer.MIN_VALUE);
		assertEquals(Integer.MIN_VALUE, item.getSomeNotNullInteger());
		assertEquals(set(item), toSet(Search.search(item.TYPE, Search.equal(item.someNotNullInteger, Integer.MIN_VALUE))));

		item.setSomeNotNullInteger(Integer.MAX_VALUE);
		assertEquals(Integer.MAX_VALUE, item.getSomeNotNullInteger());
		assertEquals(set(item), toSet(Search.search(item.TYPE, Search.equal(item.someNotNullInteger, Integer.MAX_VALUE))));
		
		final Collection searchResult = Search.search(item.TYPE, Search.equal(item.someNotNullInteger, Integer.MAX_VALUE));
		try
		{
			searchResult.add(new Object());
			fail("should have thrown UnsupportedOperationException");
		}
		catch(UnsupportedOperationException e) {}
	}

	private void dotestItemWithManyAttributesSomeBoolean(final ItemWithManyAttributes item)
	{
		assertEquals(item.TYPE, item.someBoolean.getType());
		assertEquals(null, item.getSomeBoolean());
		item.setSomeBoolean(Boolean.TRUE);
		assertEquals(Boolean.TRUE, item.getSomeBoolean());
		item.setSomeBoolean(Boolean.FALSE);
		assertEquals(Boolean.FALSE, item.getSomeBoolean());
		item.setSomeBoolean(null);
		assertEquals(null, item.getSomeBoolean());
	}

	private void dotestItemWithManyAttributesSomeNotNullBoolean(final ItemWithManyAttributes item)
	{
		assertEquals(item.TYPE, item.someNotNullBoolean.getType());
		assertEquals(true, item.getSomeNotNullBoolean());
		item.setSomeNotNullBoolean(false);
		assertEquals(false, item.getSomeNotNullBoolean());
	}

	private void dotestItemWithManyAttributesSomeItem(final ItemWithManyAttributes item, final ItemWithoutAttributes someItem)
	{
		assertEquals(item.TYPE, item.someItem.getType());
		assertEquals(ItemWithoutAttributes.TYPE, item.someItem.getTargetType());
		assertEquals(null, item.getSomeItem());
		item.setSomeItem(someItem);
		assertEquals(someItem, item.getSomeItem());
		item.passivate();
		assertEquals(someItem, item.getSomeItem());
		item.setSomeItem(null);
		assertEquals(null, item.getSomeItem());
	}

	private void dotestItemWithManyAttributesSomeNotNullItem(final ItemWithManyAttributes item, final ItemWithoutAttributes someItem, final ItemWithoutAttributes someItem2)
	{
		assertEquals(item.TYPE, item.someNotNullItem.getType());
		assertEquals(ItemWithoutAttributes.TYPE, item.someNotNullItem.getTargetType());
		assertEquals(someItem, item.getSomeNotNullItem());
		try
		{
			item.setSomeNotNullItem(someItem2);
		}
		catch(NotNullViolationException e)
		{
			throw new SystemException(e);
		}
		assertEquals(someItem2, item.getSomeNotNullItem());
		item.passivate();
		assertEquals(someItem2, item.getSomeNotNullItem());
		try
		{
			item.setSomeNotNullItem(null);
			fail("should have thrown NotNullViolationException");
		}
		catch(NotNullViolationException e)
		{
			assertEquals(item, e.getItem());
			assertEquals(item.someNotNullItem, e.getNotNullAttribute());
		}
		assertEquals(someItem2, item.getSomeNotNullItem());
	}

	private void dotestItemWithManyAttributesSomeEnumeration(final ItemWithManyAttributes item)
	{
		assertEquals(item.TYPE, item.someEnumeration.getType());
		assertEquals(list(ItemWithManyAttributes.SomeEnumeration.enumValue1), item.someEnumeration.getValues());
		assertEquals(ItemWithManyAttributes.SomeEnumeration.enumValue1, item.someEnumeration.getValue(ItemWithManyAttributes.SomeEnumeration.enumValue1NUM));
		ItemWithManyAttributes.SomeEnumeration someEnumeration = item.getSomeEnumeration();
		assertEquals(null, someEnumeration);
		if(someEnumeration!=ItemWithManyAttributes.SomeEnumeration.enumValue1)
			someEnumeration = ItemWithManyAttributes.SomeEnumeration.enumValue1;
		switch(someEnumeration.number)
		{
			case ItemWithManyAttributes.SomeEnumeration.enumValue1NUM:
				someEnumeration = ItemWithManyAttributes.SomeEnumeration.enumValue1;
				break;
			default:
				throw new RuntimeException("Ooooops");
		}
		item.setSomeEnumeration(someEnumeration);
		assertEquals(ItemWithManyAttributes.SomeEnumeration.enumValue1, item.getSomeEnumeration());
		item.setSomeEnumeration(ItemWithManyAttributes.SomeEnumeration.enumValue1);
		assertEquals(ItemWithManyAttributes.SomeEnumeration.enumValue1, item.getSomeEnumeration());
		item.passivate();
		assertEquals(ItemWithManyAttributes.SomeEnumeration.enumValue1, item.getSomeEnumeration());
		item.setSomeEnumeration(null);
		assertEquals(null, item.getSomeEnumeration());
	}

	private void dotestItemWithManyAttributesSomeMedia(final ItemWithManyAttributes item)
	{
		assertEquals(item.TYPE, item.someMedia.getType());
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
		final String prefix = "/medias/com.exedio.cope.lib.ItemWithManyAttributes/someMedia/";
		final String expectedURL = prefix+item.pk+".someMimeMajor.someMimeMinor";
		final String expectedURLSomeVariant = prefix+"SomeVariant/"+item.pk+".someMimeMajor.someMimeMinor";
		//System.out.println(expectedURL);
		//System.out.println(item.getSomeMediaURL());
		assertEquals(expectedURL, item.getSomeMediaURL());
		assertEquals(expectedURLSomeVariant, item.getSomeMediaURLSomeVariant());
		assertEquals(null/*somehow gets the data*/, item.getSomeMediaData());
		assertEquals("someMimeMajor", item.getSomeMediaMimeMajor());
		assertEquals("someMimeMinor", item.getSomeMediaMimeMinor());

		item.passivate();
		assertEquals(expectedURL, item.getSomeMediaURL());
		assertEquals(expectedURLSomeVariant, item.getSomeMediaURLSomeVariant());
		assertEquals(null/*somehow gets the data*/, item.getSomeMediaData());
		assertEquals("someMimeMajor", item.getSomeMediaMimeMajor());
		assertEquals("someMimeMinor", item.getSomeMediaMimeMinor());

		assertMediaMime(item, "image", "jpeg",  "jpg");
		assertMediaMime(item, "image", "pjpeg", "jpg");
		assertMediaMime(item, "image", "gif",   "gif");
		assertMediaMime(item, "image", "png",   "png");
		assertMediaMime(item, "image", "someMinor", "image.someMinor");

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
	}

	private void dotestItemWithManyAttributesSomeQualifiedAttribute(final ItemWithManyAttributes item, final ItemWithoutAttributes someItem)
	{
		assertEquals(item.TYPE, item.someQualifiedString.getType());
		final ItemWithoutAttributes someItem2 = new ItemWithoutAttributes();
		assertEquals(null, item.getSomeQualifiedString(someItem));
		assertEquals(null, item.getSomeQualifiedString(someItem2));
		item.setSomeQualifiedString(someItem, "someQualifiedValue");
		assertEquals("someQualifiedValue", item.getSomeQualifiedString(someItem));
		assertEquals("someQualifiedValue"/*null TODO*/, item.getSomeQualifiedString(someItem2));
		item.passivate();
		assertEquals("someQualifiedValue", item.getSomeQualifiedString(someItem));
		assertEquals("someQualifiedValue"/*null TODO*/, item.getSomeQualifiedString(someItem2));
		item.setSomeQualifiedString(someItem, null);
		assertEquals(null, item.getSomeQualifiedString(someItem));
		assertEquals(null, item.getSomeQualifiedString(someItem2));
	}

	private void dotestContinuousPrimaryKeyGeneration()
	{
		final ItemWithoutAttributes item1 = new ItemWithoutAttributes();
		item1.TYPE.flushPK();
		final ItemWithoutAttributes item2 = new ItemWithoutAttributes();
		assertNotEquals(item1, item2);
	}

	private void assertMediaMime(final ItemWithManyAttributes item,
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
