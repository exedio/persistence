/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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

import com.exedio.cope.AbstractRuntimeTest;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.Model;

public class MD5Test extends AbstractRuntimeTest
{
	public/*for web.xml*/ static final Model MODEL = new Model(MD5Item.TYPE);
	
	static
	{
		MODEL.enableSerialization(MD5Test.class, "MODEL");
	}
	
	private static final String EMPTY_HASH = "d41d8cd98f00b204e9800998ecf8427e";

	public MD5Test()
	{
		super(MODEL);
	}
	
	MD5Item item;
	
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		item = deleteOnTearDown(new MD5Item("musso"));
	}
	
	public void testMD5()
	{
		assertEquals("000ff0aa", JavaSecurityHash.encodeBytes(new byte[]{0x00, 0x0f, (byte)0xf0, (byte)0xaa}));
		assertEquals("0123456789abcdef", JavaSecurityHash.encodeBytes(new byte[]{0x01, 0x23, 0x45, 0x67, (byte)0x89, (byte)0xab, (byte)0xcd, (byte)0xef}));

		assertEquals(Arrays.asList(
				item.TYPE.getThis(),
				item.password,
				item.password.getStorage(),
				item.passwordLatin,
				item.passwordLatin.getStorage(),
				item.passwordMandatory,
				item.passwordMandatory.getStorage()),
			item.TYPE.getFeatures());

		assertEquals(item.TYPE, item.password.getType());
		assertEquals("password", item.password.getName());
		assertEquals("MD5", item.password.getAlgorithmName());
		assertEquals(item.TYPE, item.password.getStorage().getType());
		assertEquals("passwordMD5", item.password.getStorage().getName());
		assertEquals(false, item.password.getStorage().isFinal());
		assertEquals(false, item.password.getStorage().isMandatory());
		assertEquals(32, item.password.getStorage().getMinimumLength());
		assertEquals(32, item.password.getStorage().getMaximumLength());
		assertEquals(item.password, item.password.getStorage().getPattern());
		assertEquals(false, item.password.isInitial());
		assertEquals(false, item.password.isFinal());
		assertEquals(false, item.password.isMandatory());
		assertEquals(String.class, item.password.getInitialType());
		assertContains(item.password.getInitialExceptions());
		assertEquals("utf8", item.password.getEncoding());
		
		assertEquals(item.TYPE, item.passwordLatin.getType());
		assertEquals("passwordLatin", item.passwordLatin.getName());
		assertEquals(item.passwordLatin, item.passwordLatin.getStorage().getPattern());
		assertEquals(false, item.passwordLatin.isInitial());
		assertEquals(false, item.passwordLatin.isFinal());
		assertEquals(false, item.passwordLatin.isMandatory());
		assertEquals(String.class, item.passwordLatin.getInitialType());
		assertContains(item.passwordLatin.getInitialExceptions());
		assertEquals("ISO-8859-1", item.passwordLatin.getEncoding());

		assertEquals(item.TYPE, item.passwordMandatory.getType());
		assertEquals("passwordMandatory", item.passwordMandatory.getName());
		assertEquals("MD5", item.passwordMandatory.getAlgorithmName());
		assertEquals(item.passwordMandatory, item.passwordMandatory.getStorage().getPattern());
		assertEquals(true, item.passwordMandatory.isInitial());
		assertEquals(false, item.passwordMandatory.isFinal());
		assertEquals(true, item.passwordMandatory.isMandatory());
		assertEquals(String.class, item.passwordMandatory.getInitialType());
		assertContains(MandatoryViolationException.class, item.passwordMandatory.getInitialExceptions());
		assertEquals("utf8", item.passwordMandatory.getEncoding());
		
		assertSerializedSame(item.password         , 372);
		assertSerializedSame(item.passwordLatin    , 377);
		assertSerializedSame(item.passwordMandatory, 381);
		
		try
		{
			new MD5Hash("nixus");
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals(UnsupportedEncodingException.class.getName()+": nixus", e.getMessage());
			assertEquals(UnsupportedEncodingException.class, e.getCause().getClass());
		}
		try
		{
			new JavaSecurityHash(true, "NIXUS");
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals(NoSuchAlgorithmException.class.getName()+": NIXUS MessageDigest not available", e.getMessage());
			assertEquals(NoSuchAlgorithmException.class, e.getCause().getClass());
		}

		assertNull(item.getPasswordMD5());
		assertTrue(item.checkPassword(null));
		assertTrue(!item.checkPassword("bing"));
		assertContains(item, item.TYPE.search(item.password.equal(null)));
		assertContains(item.TYPE.search(item.password.equal("bing")));
		
		item.setPasswordMD5("12345678901234567890123456789012");
		assertEquals("12345678901234567890123456789012", item.getPasswordMD5());
		assertTrue(!item.checkPassword(null));
		assertTrue(!item.checkPassword("12345678901234567890123456789012"));
		assertContains(item.TYPE.search(item.password.equal(null)));
		assertContains(item.TYPE.search(item.password.equal("12345678901234567890123456789012")));

		item.setPassword("knollo");
		assertEquals("ad373a47d81949f466552edf29499b32", item.getPasswordMD5());
		assertTrue(!item.checkPassword(null));
		assertTrue(!item.checkPassword("bello"));
		assertTrue(item.checkPassword("knollo"));
		assertContains(item.TYPE.search(item.password.equal(null)));
		assertContains(item.TYPE.search(item.password.equal("bello")));
		assertContains(item, item.TYPE.search(item.password.equal("knollo")));
		
		final String longPlainText =
			"knolloknolloknolloknolloknolloknolloknolloknolloknolloknolloknollo" +
			"knolloknolloknolloknolloknolloknolloknolloknolloknolloknolloknollo" +
			"knolloknolloknolloknolloknollo";
		item.setPassword(longPlainText);
		assertEquals("6ce62d0dbd8e8b3f453ba742c102cd0b", item.getPasswordMD5());
		assertTrue(!item.checkPassword(null));
		assertTrue(!item.checkPassword("bello"));
		assertTrue(item.checkPassword(longPlainText));

		// Test, that special characters produce different hashes
		// with different encodings.
		final String specialPlainText = "Viele Gr\u00fc\u00dfe";
		item.setPassword(specialPlainText);
		item.setPasswordLatin(specialPlainText);
		assertEquals("b6f7c12664a57ad17298068b62c9053c", item.getPasswordMD5());
		assertEquals("f80281c9b755508af7c42f585ed76e23", item.getPasswordLatinMD5());
		assertTrue(!item.checkPassword(null));
		assertTrue(!item.checkPassword("bello"));
		assertTrue(item.checkPassword(specialPlainText));
		assertTrue(!item.checkPasswordLatin(null));
		assertTrue(!item.checkPasswordLatin("bello"));
		assertTrue(item.checkPasswordLatin(specialPlainText));
	
		assertEquals("780e05d22aa148f225ea2d9f0e97b109", item.getPasswordMandatoryMD5());
		assertTrue(item.checkPasswordMandatory("musso"));
		assertTrue(!item.checkPasswordMandatory("mussx"));
		assertTrue(!item.checkPasswordMandatory(""));
		assertTrue(!item.checkPasswordMandatory(null));
		assertContains(item, item.TYPE.search(item.passwordMandatory.equal("musso")));
		assertContains(item.TYPE.search(item.passwordMandatory.equal("mussx")));
		assertContains(item.TYPE.search(item.passwordMandatory.equal("")));
		assertContains(item.TYPE.search(item.passwordMandatory.equal(null)));
		assertContains(item.TYPE.search(item.passwordMandatory.notEqual("musso")));
		assertContains(item, item.TYPE.search(item.passwordMandatory.notEqual("mussx")));
		assertContains(item, item.TYPE.search(item.passwordMandatory.notEqual("")));
		assertContains(item, item.TYPE.search(item.passwordMandatory.notEqual(null)));
		
		item.setPasswordMandatory("mussx");
		assertEquals("20e875db11d2cc3b3378cb905cbcd340", item.getPasswordMandatoryMD5());
		assertTrue(!item.checkPasswordMandatory("musso"));
		assertTrue(item.checkPasswordMandatory("mussx"));
		assertTrue(!item.checkPasswordMandatory(""));
		assertTrue(!item.checkPasswordMandatory(null));
		assertContains(item.TYPE.search(item.passwordMandatory.equal("musso")));
		assertContains(item, item.TYPE.search(item.passwordMandatory.equal("mussx")));
		assertContains(item.TYPE.search(item.passwordMandatory.equal("")));
		assertContains(item.TYPE.search(item.passwordMandatory.equal(null)));
		assertContains(item, item.TYPE.search(item.passwordMandatory.notEqual("musso")));
		assertContains(item.TYPE.search(item.passwordMandatory.notEqual("mussx")));
		assertContains(item, item.TYPE.search(item.passwordMandatory.notEqual("")));
		assertContains(item, item.TYPE.search(item.passwordMandatory.notEqual(null)));
		
		item.setPasswordMandatory("");
		assertEquals(EMPTY_HASH, item.getPasswordMandatoryMD5());
		assertTrue(!item.checkPasswordMandatory("musso"));
		assertTrue(!item.checkPasswordMandatory("mussx"));
		assertTrue(item.checkPasswordMandatory(""));
		assertTrue(!item.checkPasswordMandatory(null));
		assertContains(item.TYPE.search(item.passwordMandatory.equal("musso")));
		assertContains(item.TYPE.search(item.passwordMandatory.equal("mussx")));
		assertContains(item, item.TYPE.search(item.passwordMandatory.equal("")));
		assertContains(item.TYPE.search(item.passwordMandatory.equal(null)));
		assertContains(item, item.TYPE.search(item.passwordMandatory.notEqual("musso")));
		assertContains(item, item.TYPE.search(item.passwordMandatory.notEqual("mussx")));
		assertContains(item.TYPE.search(item.passwordMandatory.notEqual("")));
		assertContains(item, item.TYPE.search(item.passwordMandatory.notEqual(null)));

		try
		{
			item.setPasswordMandatory(null);
			fail();
		}
		catch(MandatoryViolationException e)
		{
			assertEquals(item.passwordMandatory.getStorage(), e.getFeature());
		}
		assertEquals(EMPTY_HASH, item.getPasswordMandatoryMD5());
		assertTrue(!item.checkPasswordMandatory("musso"));
		assertTrue(!item.checkPasswordMandatory("mussx"));
		assertTrue(item.checkPasswordMandatory(""));
		assertTrue(!item.checkPasswordMandatory(null));
		assertContains(item.TYPE.search(item.passwordMandatory.equal("musso")));
		assertContains(item.TYPE.search(item.passwordMandatory.equal("mussx")));
		assertContains(item, item.TYPE.search(item.passwordMandatory.equal("")));
		assertContains(item.TYPE.search(item.passwordMandatory.equal(null)));
		assertContains(item, item.TYPE.search(item.passwordMandatory.notEqual("musso")));
		assertContains(item, item.TYPE.search(item.passwordMandatory.notEqual("mussx")));
		assertContains(item.TYPE.search(item.passwordMandatory.notEqual("")));
		assertContains(item, item.TYPE.search(item.passwordMandatory.notEqual(null)));
		
		// reference example from http://de.wikipedia.org/wiki/MD5
		final String upper = "Franz jagt im komplett verwahrlosten Taxi quer durch Bayern";
		final String lower = "Frank jagt im komplett verwahrlosten Taxi quer durch Bayern";
		
		item.setPassword(upper);
		assertEquals("a3cca2b2aa1e3b5b3b5aad99a8529074", item.getPasswordMD5());
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
		assertEquals("7e716d0e702df0505fc72e2b89467910", item.getPasswordMD5());
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

		item.setPasswordMD5("12345678901234567890123456789012");
		assertEquals("12345678901234567890123456789012", item.getPasswordMD5());
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
		assertEquals(EMPTY_HASH, item.getPasswordMD5());
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

		item.setPassword(null);
		assertEquals(null, item.getPasswordMD5());
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
	}
}
