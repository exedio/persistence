package com.exedio.cope.lib;

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
		item = new ItemWithManyAttributes("someString", 5, true, someItem);
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
		}
		catch (NotNullViolationException e)
		{
			assertEquals(item.someNotNullString, e.getNotNullAttribute());
			assertEquals(item, e.getItem());
		}
	}

	public void testSomeInteger()
	{
		assertEquals(item.TYPE, item.someInteger.getType());
		assertEquals(null, item.getSomeInteger());
		// TODO: assertEquals(list(item), Search.search(item.TYPE, Search.equal(item.someInteger, null)));
		item.setSomeInteger(new Integer(10));
		assertEquals(new Integer(10), item.getSomeInteger());
		assertEquals(
			list(item),
			Search.search(item.TYPE, Search.equal(item.someInteger, 10)));
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
	}

	public void testSomeEnumeration()
	{
		assertEquals(item.TYPE, item.someEnumeration.getType());
		assertEquals(
			list(
				ItemWithManyAttributes.SomeEnumeration.enumValue1,
				ItemWithManyAttributes.SomeEnumeration.enumValue2,
				ItemWithManyAttributes.SomeEnumeration.enumValue3),
			item.someEnumeration.getValues());

		assertEquals(
			ItemWithManyAttributes.SomeEnumeration.enumValue1,
			item.someEnumeration.getValue(
				ItemWithManyAttributes.SomeEnumeration.enumValue1NUM));
		assertEquals(
			ItemWithManyAttributes.SomeEnumeration.enumValue2,
			item.someEnumeration.getValue(
				ItemWithManyAttributes.SomeEnumeration.enumValue2NUM));
		assertEquals(
			ItemWithManyAttributes.SomeEnumeration.enumValue3,
			item.someEnumeration.getValue(
				ItemWithManyAttributes.SomeEnumeration.enumValue3NUM));

		assertTrue(!
			ItemWithManyAttributes.SomeEnumeration.enumValue1.equals(
			ItemWithManyAttributes.SomeEnumeration.enumValue2));
		assertTrue(!
			ItemWithManyAttributes.SomeEnumeration.enumValue2.equals(
			ItemWithManyAttributes.SomeEnumeration.enumValue3));
		assertTrue(!
			ItemWithManyAttributes.SomeEnumeration.enumValue3.equals(
			ItemWithManyAttributes.SomeEnumeration.enumValue1));

		ItemWithManyAttributes.SomeEnumeration someEnumeration = ItemWithManyAttributes.SomeEnumeration.enumValue1;
		switch (someEnumeration.number)
		{
			case ItemWithManyAttributes.SomeEnumeration.enumValue1NUM :
				someEnumeration = ItemWithManyAttributes.SomeEnumeration.enumValue2;
				break;
			case ItemWithManyAttributes.SomeEnumeration.enumValue2NUM :
				someEnumeration = ItemWithManyAttributes.SomeEnumeration.enumValue3;
				break;
			case ItemWithManyAttributes.SomeEnumeration.enumValue3NUM :
				someEnumeration = ItemWithManyAttributes.SomeEnumeration.enumValue1;
				break;
			default :
				throw new RuntimeException("Ooooops");
		}
		assertEquals(someEnumeration, ItemWithManyAttributes.SomeEnumeration.enumValue2);
		// TODO: put the above into some model test 

		// TODO: dotestItemWithManyAttributesSomeNotNullEnumeration(item);
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

	public void testSomeMedia()
	{
		// TODO: dotestItemWithManyAttributesSomeNotNullMedia(item);
		assertEquals(item.TYPE, item.someMedia.getType());
		assertEquals(null, item.getSomeMediaURL());
		assertEquals(null, item.getSomeMediaURLSomeVariant());
		assertEquals(null, item.getSomeMediaData());
		assertEquals(null, item.getSomeMediaMimeMajor());
		assertEquals(null, item.getSomeMediaMimeMinor());

		try
		{
			item.setSomeMediaData(null /*some data*/
			, "someMimeMajor", "someMimeMinor");
		}
		catch (IOException e)
		{
			throw new SystemException(e);
		}
		final String prefix =
			"/medias/com.exedio.cope.lib.ItemWithManyAttributes/someMedia/";
		final String expectedURL =
			prefix + item.pk + ".someMimeMajor.someMimeMinor";
		final String expectedURLSomeVariant =
			prefix + "SomeVariant/" + item.pk + ".someMimeMajor.someMimeMinor";
		//System.out.println(expectedURL);
		//System.out.println(item.getSomeMediaURL());
		assertEquals(expectedURL, item.getSomeMediaURL());
		assertEquals(expectedURLSomeVariant, item.getSomeMediaURLSomeVariant());
		assertEquals(null /*somehow gets the data*/
		, item.getSomeMediaData());
		assertEquals("someMimeMajor", item.getSomeMediaMimeMajor());
		assertEquals("someMimeMinor", item.getSomeMediaMimeMinor());

		item.passivate();
		assertEquals(expectedURL, item.getSomeMediaURL());
		assertEquals(expectedURLSomeVariant, item.getSomeMediaURLSomeVariant());
		assertEquals(null /*somehow gets the data*/
		, item.getSomeMediaData());
		assertEquals("someMimeMajor", item.getSomeMediaMimeMajor());
		assertEquals("someMimeMinor", item.getSomeMediaMimeMinor());

		assertMediaMime(item, "image", "jpeg", "jpg");
		assertMediaMime(item, "image", "pjpeg", "jpg");
		assertMediaMime(item, "image", "gif", "gif");
		assertMediaMime(item, "image", "png", "png");
		assertMediaMime(item, "image", "someMinor", "image.someMinor");

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