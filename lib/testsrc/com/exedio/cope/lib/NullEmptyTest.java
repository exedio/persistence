package com.exedio.cope.lib;

/**
 * Test, whether database converts empty strings to null,
 * and how the framework hides such behaviour from the user.
 */ 
public class NullEmptyTest extends DatabaseLibTest
{
	ItemWithoutAttributes someItem;
	ItemWithManyAttributes item;
	ItemWithManyAttributes item2;
	String emptyString;

	public void setUp() throws Exception
	{
		super.setUp();
		someItem = new ItemWithoutAttributes();
		item = new ItemWithManyAttributes("someString", 5, 2.2, true, someItem, ItemWithManyAttributes.SomeEnumeration.enumValue1);
		item2 = new ItemWithManyAttributes("someString", 5, 2.2, false, someItem, ItemWithManyAttributes.SomeEnumeration.enumValue2);
		// TODO: database must hide this from the user
		if(HsqldbDatabase.class.equals(Database.theInstance.getClass()))
			emptyString = "";
		else
			emptyString = null;
	}

	public void tearDown() throws Exception
	{
		assertDelete(item);
		assertDelete(item2);
		assertDelete(someItem);
		super.tearDown();
	}

	public void testNullEmpty()
			throws IntegrityViolationException
	{
		assertEquals(null, item.getSomeString());

		item.setSomeString("");
		assertEquals("", item.getSomeString());
		item.passivate();
		assertEquals(emptyString, item.getSomeString());

		item.setSomeString(null);
		assertEquals(null, item.getSomeString());
		item.passivate();
		assertEquals(null, item.getSomeString());
	}

}
