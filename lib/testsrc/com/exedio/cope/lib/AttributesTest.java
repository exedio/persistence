package com.exedio.cope.lib;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class AttributesTest extends DatabaseLibTest
{

	private ItemWithoutAttributes someItem, someItem2;
	private ItemWithManyAttributes item;

	public void setUp() throws Exception
	{
		super.setUp();
		someItem = new ItemWithoutAttributes();
		someItem2 = new ItemWithoutAttributes();
		item = new ItemWithManyAttributes("someString", 5, true, someItem, ItemWithManyAttributes.SomeEnumeration.enumValue1);
	}
	
	public void tearDown() throws Exception
	{
		item.delete();
		item = null;
		someItem.delete();
		someItem = null;
		someItem2.delete();
		someItem2 = null;
		super.tearDown();
	}
	
	public void testSomeString()
	{
		assertEquals(item.TYPE, item.someString.getType());
		assertEquals(item.TYPE, item.someStringUpperCase.getType());
		assertEquals(null, item.getSomeString());
		assertEquals(null, item.getSomeStringUpperCase());
		item.setSomeString("someString");
		assertEquals("someString", item.getSomeString());
		assertEquals("SOMESTRING", item.getSomeStringUpperCase());
		assertEquals(
			set(item),
			toSet(
				Search.search(
					item.TYPE,
					Search.equal(item.someString, "someString"))));
		assertEquals(
			set(),
			toSet(
				Search.search(
					item.TYPE,
					Search.equal(item.someString, "SOMESTRING"))));
		assertEquals(
			set(item),
			toSet(
				Search.search(
					item.TYPE,
					Search.equal(item.someStringUpperCase, "SOMESTRING"))));
		assertEquals(
			set(),
			toSet(
				Search.search(
					item.TYPE,
					Search.equal(item.someStringUpperCase, "someString"))));
		item.passivate();
		assertEquals("someString", item.getSomeString());
		assertEquals("SOMESTRING", item.getSomeStringUpperCase());
		item.setSomeString(null);
		assertEquals(null, item.getSomeString());
		assertEquals(null, item.getSomeStringUpperCase());
	}

	public void testSomeNotNullString()
	{
		assertEquals(item.TYPE, item.someNotNullString.getType());
		assertEquals("someString", item.getSomeNotNullString());
		try
		{
			item.setSomeNotNullString("someOtherString");
		}
		catch (NotNullViolationException e)
		{
			throw new SystemException(e);
		}
		assertEquals("someOtherString", item.getSomeNotNullString());

		try
		{
			item.setSomeNotNullString(null);
			fail("should have thrown NotNullViolationException");
		}
		catch (NotNullViolationException e)
		{
			assertEquals(item.someNotNullString, e.getNotNullAttribute());
			assertEquals(item, e.getItem());
		}

		try
		{
			new ItemWithManyAttributes(null, 5, true, someItem, ItemWithManyAttributes.SomeEnumeration.enumValue1);
			fail("should have thrown NotNullViolationException");
		}
		catch(NotNullViolationException e)
		{
			assertEquals(item.someNotNullString, e.getNotNullAttribute());
		}
	}

	public void testSomeInteger()
	{
		assertEquals(item.TYPE, item.someInteger.getType());
		assertEquals(null, item.getSomeInteger());
		assertEquals(list(item), Search.search(item.TYPE, Search.equal(item.someInteger, null)));
		assertEquals(list(item), Search.search(item.TYPE, Search.isNull(item.someInteger)));
		item.setSomeInteger(new Integer(10));
		assertEquals(new Integer(10), item.getSomeInteger());
		assertEquals(
			list(item),
			Search.search(item.TYPE, Search.equal(item.someInteger, 10)));
		assertEquals(list(), Search.search(item.TYPE, Search.equal(item.someInteger, null)));
		assertEquals(list(), Search.search(item.TYPE, Search.isNull(item.someInteger)));
		item.setSomeInteger(null);
		assertEquals(null, item.getSomeInteger());
	}

	public void testSomeNotNullInteger()
	{
		assertEquals(item.TYPE, item.someNotNullInteger.getType());
		assertEquals(5, item.getSomeNotNullInteger());
		item.setSomeNotNullInteger(20);
		assertEquals(20, item.getSomeNotNullInteger());

		item.setSomeNotNullInteger(0);
		assertEquals(0, item.getSomeNotNullInteger());
		assertEquals(
			set(item),
			toSet(
				Search.search(
					item.TYPE,
					Search.equal(item.someNotNullInteger, 0))));

		item.setSomeNotNullInteger(Integer.MIN_VALUE);
		assertEquals(Integer.MIN_VALUE, item.getSomeNotNullInteger());
		assertEquals(
			set(item),
			toSet(
				Search.search(
					item.TYPE,
					Search.equal(item.someNotNullInteger, Integer.MIN_VALUE))));

		item.setSomeNotNullInteger(Integer.MAX_VALUE);
		assertEquals(Integer.MAX_VALUE, item.getSomeNotNullInteger());
		assertEquals(
			set(item),
			toSet(
				Search.search(
					item.TYPE,
					Search.equal(item.someNotNullInteger, Integer.MAX_VALUE))));
	}

	public void testSomeBoolean()
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

	public void testSomeNotNullBoolean()
	{
		assertEquals(item.TYPE, item.someNotNullBoolean.getType());
		assertEquals(true, item.getSomeNotNullBoolean());
		item.setSomeNotNullBoolean(false);
		assertEquals(false, item.getSomeNotNullBoolean());
	}

	public void testSomeItem()
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

	public void testSomeNotNullItem()
	{
		assertEquals(item.TYPE, item.someNotNullItem.getType());
		assertEquals(
			ItemWithoutAttributes.TYPE,
			item.someNotNullItem.getTargetType());
		assertEquals(someItem, item.getSomeNotNullItem());
		try
		{
			item.setSomeNotNullItem(someItem2);
		}
		catch (NotNullViolationException e)
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
		catch (NotNullViolationException e)
		{
			assertEquals(item, e.getItem());
			assertEquals(item.someNotNullItem, e.getNotNullAttribute());
		}
		assertEquals(someItem2, item.getSomeNotNullItem());
		try
		{
			someItem2.delete();
		}
		catch(IntegrityViolationException e)
		{
			assertEquals(item.someNotNullItem, e.getAttribute());
			assertEquals(null/*TODO someItem*/, e.getItem());
		}

		try
		{
			new ItemWithManyAttributes("someString", 5, true, null, ItemWithManyAttributes.SomeEnumeration.enumValue1);
			fail("should have thrown NotNullViolationException");
		}
		catch(NotNullViolationException e)
		{
			assertEquals(item.someNotNullItem, e.getNotNullAttribute());
		}
	}

	public void testSomeEnumeration()
	{
		assertEquals(null, item.getSomeEnumeration());
		item.setSomeEnumeration(ItemWithManyAttributes.SomeEnumeration.enumValue1);
		assertEquals(
			ItemWithManyAttributes.SomeEnumeration.enumValue1,
			item.getSomeEnumeration());
		item.setSomeEnumeration(
			ItemWithManyAttributes.SomeEnumeration.enumValue2);
		assertEquals(
			ItemWithManyAttributes.SomeEnumeration.enumValue2,
			item.getSomeEnumeration());
		item.passivate();
		assertEquals(
			ItemWithManyAttributes.SomeEnumeration.enumValue2,
			item.getSomeEnumeration());
		item.setSomeEnumeration(null);
		assertEquals(null, item.getSomeEnumeration());
	}

	public void testNotNullSomeEnumeration()
			throws NotNullViolationException
	{
		assertEquals(ItemWithManyAttributes.SomeEnumeration.enumValue1, item.getSomeNotNullEnumeration());
		item.setSomeNotNullEnumeration(ItemWithManyAttributes.SomeEnumeration.enumValue2);
		assertEquals(
			ItemWithManyAttributes.SomeEnumeration.enumValue2,
			item.getSomeNotNullEnumeration());
		item.setSomeNotNullEnumeration(
			ItemWithManyAttributes.SomeEnumeration.enumValue3);
		assertEquals(
			ItemWithManyAttributes.SomeEnumeration.enumValue3,
			item.getSomeNotNullEnumeration());
		item.passivate();
		assertEquals(
			ItemWithManyAttributes.SomeEnumeration.enumValue3,
			item.getSomeNotNullEnumeration());
		try
		{
			item.setSomeNotNullEnumeration(null);
		}
		catch(NotNullViolationException e)
		{
			assertEquals(item.someNotNullEnumeration, e.getNotNullAttribute());
		}
		assertEquals(
			ItemWithManyAttributes.SomeEnumeration.enumValue3,
			item.getSomeNotNullEnumeration());

		try
		{
			new ItemWithManyAttributes("someString", 5, true, someItem, null);
			fail("should have thrown NotNullViolationException");
		}
		catch(NotNullViolationException e)
		{
			assertEquals(item.someNotNullEnumeration, e.getNotNullAttribute());
		}
	}

	public void testSomeMedia()
	{
		// TODO: dotestItemWithManyAttributesSomeNotNullMedia(item);
		assertEquals(item.TYPE, item.someMedia.getType());
		assertEquals(null, item.getSomeMediaURL());
		assertEquals(null, item.getSomeMediaURLSomeVariant());
		assertEquals(null, item.getSomeMediaData());
		assertEquals(null, item.getSomeMediaMimeMajor());
		assertEquals(null, item.getSomeMediaMimeMinor());

		final byte[] bytes = new byte[]{3,7,1,4};
		try
		{
			item.setSomeMediaData(new ByteArrayInputStream(bytes),
			"someMimeMajor", "someMimeMinor");
		}
		catch (IOException e)
		{
			throw new SystemException(e);
		}
		final String prefix =
			"/medias/com.exedio.cope.lib.ItemWithManyAttributes/someMedia/";
		final String pkString = (item.pk>=0) ? String.valueOf(item.pk) : "m"+(-item.pk);
		final String expectedURL =
			prefix + pkString + ".someMimeMajor.someMimeMinor";
		final String expectedURLSomeVariant =
			prefix + "SomeVariant/" + pkString + ".someMimeMajor.someMimeMinor";
		//System.out.println(expectedURL);
		//System.out.println(item.getSomeMediaURL());
		assertEquals(expectedURL, item.getSomeMediaURL());
		assertEquals(expectedURLSomeVariant, item.getSomeMediaURLSomeVariant());
		assertData(bytes, item.getSomeMediaData());
		assertEquals("someMimeMajor", item.getSomeMediaMimeMajor());
		assertEquals("someMimeMinor", item.getSomeMediaMimeMinor());

		item.passivate();
		assertEquals(expectedURL, item.getSomeMediaURL());
		assertEquals(expectedURLSomeVariant, item.getSomeMediaURLSomeVariant());
		assertData(bytes, item.getSomeMediaData());
		assertEquals("someMimeMajor", item.getSomeMediaMimeMajor());
		assertEquals("someMimeMinor", item.getSomeMediaMimeMinor());

		assertMediaMime(item, "image", "jpeg", bytes, "jpg");
		assertMediaMime(item, "image", "pjpeg", bytes, "jpg");
		assertMediaMime(item, "image", "gif", bytes, "gif");
		assertMediaMime(item, "image", "png", bytes, "png");
		assertMediaMime(item, "image", "someMinor", bytes, "image.someMinor");

		try
		{
			item.setSomeMediaData(null, null, null);
		}
		catch (IOException e)
		{
			throw new SystemException(e);
		}
		assertEquals(null, item.getSomeMediaURL());
		assertEquals(null, item.getSomeMediaURLSomeVariant());
		assertEquals(null, item.getSomeMediaData());
		assertEquals(null, item.getSomeMediaMimeMajor());
		assertEquals(null, item.getSomeMediaMimeMinor());
	}

	public void testSomeQualifiedAttribute()
			throws IntegrityViolationException
	{
		assertEquals(item.TYPE, item.someQualifiedString.getType());
		final ItemWithoutAttributes someItem2 = new ItemWithoutAttributes();
		assertEquals(null, item.getSomeQualifiedString(someItem));
		assertEquals(null, item.getSomeQualifiedString(someItem2));
		item.setSomeQualifiedString(someItem, "someQualifiedValue");
		assertEquals("someQualifiedValue", item.getSomeQualifiedString(someItem));
		assertEquals("someQualifiedValue" /*null TODO*/, item.getSomeQualifiedString(someItem2));
		item.passivate();
		assertEquals("someQualifiedValue", item.getSomeQualifiedString(someItem));
		assertEquals("someQualifiedValue" /*null TODO*/, item.getSomeQualifiedString(someItem2));
		item.setSomeQualifiedString(someItem, null);
		assertEquals(null, item.getSomeQualifiedString(someItem));
		assertEquals(null, item.getSomeQualifiedString(someItem2));

		assertDelete(someItem2);
	}
}