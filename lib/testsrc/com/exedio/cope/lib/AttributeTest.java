package com.exedio.cope.lib;

import com.exedio.cope.testmodel.AttributeItem;
import com.exedio.cope.testmodel.EmptyItem;


public abstract class AttributeTest extends DatabaseLibTest
{

	protected EmptyItem someItem, someItem2;
	protected AttributeItem item;
	protected AttributeItem item2;

	public void setUp() throws Exception
	{
		super.setUp();
		someItem = new EmptyItem();
		someItem2 = new EmptyItem();
		item = new AttributeItem("someString", 5, 6l, 2.2, true, someItem, AttributeItem.SomeEnumeration.enumValue1);
		item2 = new AttributeItem("someString2", 6, 7l, 2.3, false, someItem2, AttributeItem.SomeEnumeration.enumValue2);
	}
	
	public void tearDown() throws Exception
	{
		item.delete();
		item = null;
		item2.delete();
		item2 = null;
		someItem.delete();
		someItem = null;
		someItem2.delete();
		someItem2 = null;
		super.tearDown();
	}
}