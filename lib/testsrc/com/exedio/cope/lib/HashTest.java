/*
 * Copyright (C) 2004-2005  exedio GmbH (www.exedio.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.exedio.cope.lib;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import com.exedio.cope.lib.pattern.JavaHash;
import com.exedio.cope.lib.pattern.MD5Hash;
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
		
		try
		{
			new MD5Hash(item.hashed1MD5, "nixus");
			fail("should have thrown UnsupportedEncodingException");
		}
		catch(NestingRuntimeException e)
		{
			assertEquals("nixus", e.getMessage());
			assertEquals(UnsupportedEncodingException.class, e.getNestedCause().getClass());
		}
		try
		{
			new JavaHash(item.hashed1MD5, "nixus");
			fail("should have thrown NoSuchAlgorithmException");
		}
		catch(NestingRuntimeException e)
		{
			assertEquals("NIXUS MessageDigest not available", e.getMessage());
			assertEquals(NoSuchAlgorithmException.class, e.getNestedCause().getClass());
		}
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
