
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
	
	public void testMD5()
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

	public void testWrap()
	{
		assertNull(item.getEncoded2Wrap());
		assertTrue(item.checkEncoded2(null));
		assertTrue(!item.checkEncoded2("bing"));
		
		item.setEncoded2Wrap("bello");
		assertEquals("bello", item.getEncoded2Wrap());
		assertTrue(!item.checkEncoded2(null));
		assertTrue(!item.checkEncoded2("bello"));
		
		item.setEncoded2("knollo");
		assertEquals("[knollo]", item.getEncoded2Wrap());
		assertTrue(!item.checkEncoded2(null));
		assertTrue(!item.checkEncoded2("bello"));
		assertTrue(item.checkEncoded2("knollo"));
	}

}
