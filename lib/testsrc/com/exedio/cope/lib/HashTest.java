
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
		assertNull(item.getEncoded1MD5());
		assertTrue(item.checkEncoded1(null));
		assertTrue(!item.checkEncoded1("bing"));
		
		item.setEncoded1MD5("bello");
		assertEquals("bello", item.getEncoded1MD5());
		assertTrue(!item.checkEncoded1(null));
		assertTrue(!item.checkEncoded1("bello"));
		
		item.setEncoded1("knollo");
		assertNotNull(item.getEncoded1MD5());
		assertTrue(!item.checkEncoded1(null));
		assertTrue(!item.checkEncoded1("bello"));
		assertTrue(item.checkEncoded1("knollo"));
	}

}
