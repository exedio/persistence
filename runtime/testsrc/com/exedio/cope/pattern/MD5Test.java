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
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.Model;

public class MD5Test extends AbstractLibTest
{
	public/*for web.xml*/ static final Model MODEL = new Model(MD5Item.TYPE);
	
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
		deleteOnTearDown(item = new MD5Item("musso"));
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
		assertEquals(String.class, item.password.getWrapperSetterType());
		assertContains(item.password.getSetterExceptions());
		assertEquals("utf8", item.password.getEncoding());
		
		assertEquals(item.TYPE, item.passwordLatin.getType());
		assertEquals("passwordLatin", item.passwordLatin.getName());
		assertEqualsUnmodifiable(list(item.passwordLatin), item.passwordLatin.getStorage().getPatterns());
		assertEquals(false, item.passwordLatin.isInitial());
		assertEquals(false, item.passwordLatin.isFinal());
		assertEquals(false, item.passwordLatin.isMandatory());
		assertEquals(String.class, item.passwordLatin.getWrapperSetterType());
		assertContains(item.passwordLatin.getSetterExceptions());
		assertEquals("ISO-8859-1", item.passwordLatin.getEncoding());

		assertEquals(item.TYPE, item.passwordMandatory.getType());
		assertEquals("passwordMandatory", item.passwordMandatory.getName());
		assertEqualsUnmodifiable(list(item.passwordMandatory), item.passwordMandatory.getStorage().getPatterns());
		assertEquals(true, item.passwordMandatory.isInitial());
		assertEquals(false, item.passwordMandatory.isFinal());
		assertEquals(true, item.passwordMandatory.isMandatory());
		assertEquals(String.class, item.passwordMandatory.getWrapperSetterType());
		assertContains(MandatoryViolationException.class, item.passwordMandatory.getSetterExceptions());
		assertEquals("utf8", item.passwordMandatory.getEncoding());
		
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
			new JavaSecurityHash(true, "NIXUS", 66);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals(NoSuchAlgorithmException.class.getName()+": NIXUS MessageDigest not available", e.getMessage());
			assertEquals(NoSuchAlgorithmException.class, e.getCause().getClass());
		}

		assertNull(item.getPasswordHash());
		assertTrue(item.checkPassword(null));
		assertTrue(!item.checkPassword("bing"));
		assertContains(item, item.TYPE.search(item.password.equal(null)));
		assertContains(item.TYPE.search(item.password.equal("bing")));
		
		item.setPasswordHash("12345678901234567890123456789012");
		assertEquals("12345678901234567890123456789012", item.getPasswordHash());
		assertTrue(!item.checkPassword(null));
		assertTrue(!item.checkPassword("12345678901234567890123456789012"));
		assertContains(item.TYPE.search(item.password.equal(null)));
		assertContains(item.TYPE.search(item.password.equal("12345678901234567890123456789012")));

		item.setPassword("knollo");
		assertEquals("ad373a47d81949f466552edf29499b32", item.getPasswordHash());
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
		assertEquals("6ce62d0dbd8e8b3f453ba742c102cd0b", item.getPasswordHash());
		assertTrue(!item.checkPassword(null));
		assertTrue(!item.checkPassword("bello"));
		assertTrue(item.checkPassword(longPlainText));

		// Test, that special characters produce different hashes
		// with different encodings.
		final String specialPlainText = "Viele Gr\u00fc\u00dfe";
		item.setPassword(specialPlainText);
		item.setPasswordLatin(specialPlainText);
		assertEquals("b6f7c12664a57ad17298068b62c9053c", item.getPasswordHash());
		assertEquals("f80281c9b755508af7c42f585ed76e23", item.getPasswordLatinHash());
		assertTrue(!item.checkPassword(null));
		assertTrue(!item.checkPassword("bello"));
		assertTrue(item.checkPassword(specialPlainText));
		assertTrue(!item.checkPasswordLatin(null));
		assertTrue(!item.checkPasswordLatin("bello"));
		assertTrue(item.checkPasswordLatin(specialPlainText));
	
		assertEquals("780e05d22aa148f225ea2d9f0e97b109", item.getPasswordMandatoryHash());
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
		assertEquals("20e875db11d2cc3b3378cb905cbcd340", item.getPasswordMandatoryHash());
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
		assertEquals(EMPTY_HASH, item.getPasswordMandatoryHash());
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
		assertEquals(EMPTY_HASH, item.getPasswordMandatoryHash());
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
		final String appendix = "ranz jagt im komplett verwahrlosten Taxi quer durch Bayern";
		final String upper = "F" + appendix;
		final String lower = "f" + appendix;
		
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
		assertEquals(EMPTY_HASH, item.getPasswordHash());
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
	}
}
