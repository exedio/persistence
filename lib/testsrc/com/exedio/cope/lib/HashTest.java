
package com.exedio.cope.lib;

import com.exedio.cope.testmodel.StringItem;

public class HashTest extends DatabaseLibTest
{
	StringItem item;
	
	public void setUp() throws Exception
	{
		super.setUp();
		deleteOnTearDown(item = new StringItem());
	}
	
	public void testEncoders()
	{
		assertNull(item.getAny());
		assertTrue(item.checkAnyMD5(null));
		assertTrue(!item.checkAnyMD5("bing"));
		
		item.setAny("bello");
		assertEquals("bello", item.getAny());
		assertTrue(!item.checkAnyMD5(null));
		assertTrue(!item.checkAnyMD5("bello"));
		
		item.setAnyMD5("knollo");
		assertNotNull(item.getAny());
		assertTrue(!item.checkAnyMD5(null));
		assertTrue(!item.checkAnyMD5("bello"));
		assertTrue(item.checkAnyMD5("knollo"));
	}

}
