
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
		assertNull(item.getHashed1MD5());
		assertTrue(item.checkHashed1(null));
		assertTrue(!item.checkHashed1("bing"));
		
		item.setHashed1MD5("bello");
		assertEquals("bello", item.getHashed1MD5());
		assertTrue(!item.checkHashed1(null));
		assertTrue(!item.checkHashed1("bello"));
		
		item.setHashed1("knollo");
		assertEquals("rTc6R9gZSfRmVS7fKUmbMg==", item.getHashed1MD5());
		assertTrue(!item.checkHashed1(null));
		assertTrue(!item.checkHashed1("bello"));
		assertTrue(item.checkHashed1("knollo"));
	}

	public void testWrap()
	{
		assertNull(item.getHashed2Wrap());
		assertTrue(item.checkHashed2(null));
		assertTrue(!item.checkHashed2("bing"));
		
		item.setHashed2Wrap("bello");
		assertEquals("bello", item.getHashed2Wrap());
		assertTrue(!item.checkHashed2(null));
		assertTrue(!item.checkHashed2("bello"));
		
		item.setHashed2("knollo");
		assertEquals("[knollo]", item.getHashed2Wrap());
		assertTrue(!item.checkHashed2(null));
		assertTrue(!item.checkHashed2("bello"));
		assertTrue(item.checkHashed2("knollo"));
	}

}
