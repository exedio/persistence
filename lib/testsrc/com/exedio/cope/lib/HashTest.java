
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
		
		final String longPlainText =
			"knolloknolloknolloknolloknolloknolloknolloknolloknolloknolloknollo" +
			"knolloknolloknolloknolloknolloknolloknolloknolloknolloknolloknollo" +
			"knolloknolloknolloknolloknollo";
		item.setHashed1(longPlainText);
		assertEquals("bOYtDb2Oiz9FO6dCwQLNCw==", item.getHashed1MD5());
		assertTrue(!item.checkHashed1(null));
		assertTrue(!item.checkHashed1("bello"));
		assertTrue(item.checkHashed1(longPlainText));

		// Test, that special characters produce different hashes
		// with different pre-MD5 encodings.
		final String specialPlainText = "Viele Gr\u00fc\u00dfe";
		
		item.setHashed1(specialPlainText);
		assertEquals("tvfBJmSletFymAaLYskFPA==", item.getHashed1MD5());
		assertTrue(!item.checkHashed1(null));
		assertTrue(!item.checkHashed1("bello"));
		assertTrue(item.checkHashed1(specialPlainText));
		assertTrue(!item.checkHashed1Latin(specialPlainText));

		item.setHashed1Latin(specialPlainText);
		assertEquals("+AKBybdVUIr3xC9YXtduIw==", item.getHashed1MD5());
		assertTrue(!item.checkHashed1Latin(null));
		assertTrue(!item.checkHashed1Latin("bello"));
		assertTrue(item.checkHashed1Latin(specialPlainText));
		assertTrue(!item.checkHashed1(specialPlainText));
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
