package com.exedio.cope.lib;


public abstract class AttributesTest extends DatabaseLibTest
{

	protected EmptyItem someItem, someItem2;
	protected ItemWithManyAttributes item;
	protected ItemWithManyAttributes item2;

	public void setUp() throws Exception
	{
		super.setUp();
		someItem = new EmptyItem();
		someItem2 = new EmptyItem();
		item = new ItemWithManyAttributes("someString", 5, 6l, 2.2, true, someItem, ItemWithManyAttributes.SomeEnumeration.enumValue1);
		item2 = new ItemWithManyAttributes("someString2", 6, 7l, 2.3, false, someItem2, ItemWithManyAttributes.SomeEnumeration.enumValue2);
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