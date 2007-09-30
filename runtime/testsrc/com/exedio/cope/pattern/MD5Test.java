/*
 * Copyright (C) 2004-2007  exedio GmbH (www.exedio.com)
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

package com.exedio.cope.pattern;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import com.exedio.cope.AbstractLibTest;
import com.exedio.cope.Feature;
import com.exedio.cope.Model;
import com.exedio.cope.StringField;

public class MD5Test extends AbstractLibTest
{
	public/*for web.xml*/ static final Model MODEL = new Model(MD5Item.TYPE);

	public MD5Test()
	{
		super(MODEL);
	}
	
	MD5Item item;
	
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		deleteOnTearDown(item = new MD5Item());
	}
	
	public void testMD5()
	{
		assertEquals("000ff0aa", JavaSecurityHash.encodeBytes(new byte[]{0x00, 0x0F, (byte)0xF0, (byte)0xAA}));
		assertEquals("0123456789abcdef", JavaSecurityHash.encodeBytes(new byte[]{0x01, 0x23, 0x45, 0x67, (byte)0x89, (byte)0xab, (byte)0xcd, (byte)0xef}));

		assertEquals(Arrays.asList(new Feature[]{
				item.TYPE.getThis(),
				item.password,
				item.password.getStorage(),
				item.hashed1MD5,
				item.hashed1,
				item.hashed1Latin,
			}), item.TYPE.getFeatures());

		assertEquals(item.TYPE, item.password.getType());
		assertEquals("password", item.password.getName());
		assertEquals(item.TYPE, item.password.getStorage().getType());
		assertEquals("passwordHash", item.password.getStorage().getName());
		assertEquals(false, item.password.getStorage().isFinal());
		assertEquals(false, item.password.getStorage().isMandatory());
		assertEquals(32, item.password.getStorage().getMinimumLength());
		assertEquals(32, item.password.getStorage().getMaximumLength());
		assertEqualsUnmodifiable(list(item.password), item.password.getStorage().getPatterns());
		assertEquals(false, item.password.isInitial());
		assertEquals(false, item.password.isFinal());
		assertEquals(false, item.password.isMandatory());
		assertContains(item.password.getSetterExceptions());
		assertEquals("utf8", item.password.getEncoding());
		
		assertEquals(item.TYPE, item.hashed1.getType());
		assertEquals(item.TYPE, item.hashed1Latin.getType());
		assertEquals("hashed1", item.hashed1.getName());
		assertEquals("hashed1Latin", item.hashed1Latin.getName());
		assertEquals(item.hashed1MD5, item.hashed1.getStorage());
		assertEquals(item.hashed1MD5, item.hashed1Latin.getStorage());
		assertEqualsUnmodifiable(list(item.hashed1, item.hashed1Latin), item.hashed1MD5.getPatterns());
		assertEquals(false, item.hashed1.isInitial());
		assertEquals(false, item.hashed1Latin.isInitial());
		assertEquals(false, item.hashed1.isFinal());
		assertEquals(false, item.hashed1Latin.isFinal());
		assertEquals(false, item.hashed1.isMandatory());
		assertEquals(false, item.hashed1Latin.isMandatory());
		assertContains(item.hashed1.getSetterExceptions());
		assertContains(item.hashed1Latin.getSetterExceptions());
		assertEquals("utf8", item.hashed1.getEncoding());
		assertEquals("ISO-8859-1", item.hashed1Latin.getEncoding());

		try
		{
			new MD5Hash(new StringField(), "nixus");
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals(UnsupportedEncodingException.class.getName()+": nixus", e.getMessage());
			assertEquals(UnsupportedEncodingException.class, e.getCause().getClass());
		}
		try
		{
			new JavaSecurityHash(new StringField(), "NIXUS");
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals(NoSuchAlgorithmException.class.getName()+": NIXUS MessageDigest not available", e.getMessage());
			assertEquals(NoSuchAlgorithmException.class, e.getCause().getClass());
		}

		assertNull(item.getHashed1MD5());
		assertTrue(item.checkHashed1(null));
		assertTrue(!item.checkHashed1("bing"));
		assertContains(item, item.TYPE.search(item.hashed1.equal(null)));
		assertContains(item.TYPE.search(item.hashed1.equal("bing")));
		
		item.setHashed1MD5("bello");
		assertEquals("bello", item.getHashed1MD5());
		assertTrue(!item.checkHashed1(null));
		assertTrue(!item.checkHashed1("bello"));
		assertContains(item.TYPE.search(item.hashed1.equal(null)));
		assertContains(item.TYPE.search(item.hashed1.equal("bello")));

		item.setHashed1("knollo");
		assertEquals("ad373a47d81949f466552edf29499b32", item.getHashed1MD5());
		assertTrue(!item.checkHashed1(null));
		assertTrue(!item.checkHashed1("bello"));
		assertTrue(item.checkHashed1("knollo"));
		assertContains(item.TYPE.search(item.hashed1.equal(null)));
		assertContains(item.TYPE.search(item.hashed1.equal("bello")));
		assertContains(item, item.TYPE.search(item.hashed1.equal("knollo")));
		
		final String longPlainText =
			"knolloknolloknolloknolloknolloknolloknolloknolloknolloknolloknollo" +
			"knolloknolloknolloknolloknolloknolloknolloknolloknolloknolloknollo" +
			"knolloknolloknolloknolloknollo";
		item.setHashed1(longPlainText);
		assertEquals("6ce62d0dbd8e8b3f453ba742c102cd0b", item.getHashed1MD5());
		assertTrue(!item.checkHashed1(null));
		assertTrue(!item.checkHashed1("bello"));
		assertTrue(item.checkHashed1(longPlainText));

		// Test, that special characters produce different hashes
		// with different pre-MD5 encodings.
		final String specialPlainText = "Viele Gr\u00fc\u00dfe";
		
		item.setHashed1(specialPlainText);
		assertEquals("b6f7c12664a57ad17298068b62c9053c", item.getHashed1MD5());
		assertTrue(!item.checkHashed1(null));
		assertTrue(!item.checkHashed1("bello"));
		assertTrue(item.checkHashed1(specialPlainText));
		assertTrue(!item.checkHashed1Latin(specialPlainText));

		item.setHashed1Latin(specialPlainText);
		assertEquals("f80281c9b755508af7c42f585ed76e23", item.getHashed1MD5());
		assertTrue(!item.checkHashed1Latin(null));
		assertTrue(!item.checkHashed1Latin("bello"));
		assertTrue(item.checkHashed1Latin(specialPlainText));
		assertTrue(!item.checkHashed1(specialPlainText));
	}
	
	/**
	 * reference example from http://de.wikipedia.org/wiki/MD5
	 */
	public void testWikipedia()
	{
		final String appendix = "ranz jagt im komplett verwahrlosten Taxi quer durch Bayern";
		final String upper = "F" + appendix;
		final String lower = "f" + appendix;
		
		assertEquals(null, item.getPasswordHash());
		assertTrue(!item.checkPassword(upper));
		assertTrue(!item.checkPassword(lower));
		assertTrue(!item.checkPassword(""));
		assertTrue(item.checkPassword(null));
		assertContains(item.TYPE.search(item.password.equal(upper)));
		assertContains(item.TYPE.search(item.password.equal(lower)));
		assertContains(item.TYPE.search(item.password.equal("")));
		assertContains(item, item.TYPE.search(item.password.equal(null)));
		assertContains(item.TYPE.search(item.password.notEqual(upper)));
		assertContains(item.TYPE.search(item.password.notEqual(lower)));
		assertContains(item.TYPE.search(item.password.notEqual("")));
		assertContains(item.TYPE.search(item.password.notEqual(null)));
		
		item.setPassword(upper);
		assertEquals("a3cca2b2aa1e3b5b3b5aad99a8529074", item.getPasswordHash());
		assertTrue(item.checkPassword(upper));
		assertTrue(!item.checkPassword(lower));
		assertTrue(!item.checkPassword(""));
		assertTrue(!item.checkPassword(null));
		assertContains(item, item.TYPE.search(item.password.equal(upper)));
		assertContains(item.TYPE.search(item.password.equal(lower)));
		assertContains(item.TYPE.search(item.password.equal("")));
		assertContains(item.TYPE.search(item.password.equal(null)));
		assertContains(item.TYPE.search(item.password.notEqual(upper)));
		assertContains(item, item.TYPE.search(item.password.notEqual(lower)));
		assertContains(item, item.TYPE.search(item.password.notEqual("")));
		assertContains(item, item.TYPE.search(item.password.notEqual(null)));

		item.setPassword(lower);
		assertEquals("4679e94e07f9a61f42b3d7f50cae0aef", item.getPasswordHash());
		assertTrue(!item.checkPassword(upper));
		assertTrue(item.checkPassword(lower));
		assertTrue(!item.checkPassword(""));
		assertTrue(!item.checkPassword(null));
		assertContains(item.TYPE.search(item.password.equal(upper)));
		assertContains(item, item.TYPE.search(item.password.equal(lower)));
		assertContains(item.TYPE.search(item.password.equal("")));
		assertContains(item.TYPE.search(item.password.equal(null)));
		assertContains(item, item.TYPE.search(item.password.notEqual(upper)));
		assertContains(item.TYPE.search(item.password.notEqual(lower)));
		assertContains(item, item.TYPE.search(item.password.notEqual("")));
		assertContains(item, item.TYPE.search(item.password.notEqual(null)));

		item.setPasswordHash("12345678901234567890123456789012");
		assertEquals("12345678901234567890123456789012", item.getPasswordHash());
		assertTrue(!item.checkPassword(upper));
		assertTrue(!item.checkPassword(lower));
		assertTrue(!item.checkPassword(""));
		assertTrue(!item.checkPassword(null));
		assertContains(item.TYPE.search(item.password.equal(upper)));
		assertContains(item.TYPE.search(item.password.equal(lower)));
		assertContains(item.TYPE.search(item.password.equal("")));
		assertContains(item.TYPE.search(item.password.equal(null)));
		assertContains(item, item.TYPE.search(item.password.notEqual(upper)));
		assertContains(item, item.TYPE.search(item.password.notEqual(lower)));
		assertContains(item, item.TYPE.search(item.password.notEqual("")));
		assertContains(item, item.TYPE.search(item.password.notEqual(null)));

		item.setPassword("");
		assertEquals("d41d8cd98f00b204e9800998ecf8427e", item.getPasswordHash());
		assertTrue(!item.checkPassword(upper));
		assertTrue(!item.checkPassword(lower));
		assertTrue(item.checkPassword(""));
		assertTrue(!item.checkPassword(null));
		assertContains(item.TYPE.search(item.password.equal(upper)));
		assertContains(item.TYPE.search(item.password.equal(lower)));
		assertContains(item, item.TYPE.search(item.password.equal("")));
		assertContains(item.TYPE.search(item.password.equal(null)));
		assertContains(item, item.TYPE.search(item.password.notEqual(upper)));
		assertContains(item, item.TYPE.search(item.password.notEqual(lower)));
		assertContains(item.TYPE.search(item.password.notEqual("")));
		assertContains(item, item.TYPE.search(item.password.notEqual(null)));
	}
}
