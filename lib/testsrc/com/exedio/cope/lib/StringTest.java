
package com.exedio.cope.lib;

public class StringTest extends DatabaseLibTest
{
	StringItem item;

	public void setUp() throws Exception
	{
		super.setUp();
		item = new StringItem(); 
	}
	
	public void tearDown() throws Exception
	{
		item.delete();
		super.tearDown();
	}
	
	public void testStrings() throws LengthViolationException
	{
		assertEquals(0, item.any.getMinimumLength());
		assertEquals(false, item.any.isLengthConstrained());
		assertEquals(4, item.min4.getMinimumLength());
		assertEquals(true, item.min4.isLengthConstrained());

		item.setAny("1234");
		item.setMin4("1234");
		item.setAny("123");
		try
		{
			item.setMin4("123");
		}
		catch(LengthViolationException e)
		{
			assertEquals(item, e.getItem());
			assertEquals(item.min4, e.getStringAttribute());
			assertEquals("123", e.getValue());
		}
	}

}
