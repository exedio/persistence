package com.exedio.cope.lib;

import com.exedio.cope.testmodel.AttributeItem;
import com.exedio.cope.testmodel.EmptyItem;

/**
 * Test, whether database converts empty strings to null,
 * and how the framework hides such behaviour from the user.
 */ 
public class NullEmptyTest extends DatabaseLibTest
{
	EmptyItem someItem;
	AttributeItem item;
	AttributeItem item2;
	String emptyString;

	public void setUp() throws Exception
	{
		super.setUp();
		deleteOnTearDown(someItem = new EmptyItem());
		deleteOnTearDown(item = new AttributeItem("someString", 5, 6l, 2.2, true, someItem, AttributeItem.SomeEnumeration.enumValue1));
		deleteOnTearDown(item2 = new AttributeItem("someString", 5, 6l, 2.2, false, someItem, AttributeItem.SomeEnumeration.enumValue2));
		// TODO: database must hide this from the user
		if(HsqldbDatabase.class.equals(model.getDatabase().getClass()))
			emptyString = "";
		else
			emptyString = null;
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
