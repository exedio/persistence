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

public class SHATest extends AbstractRuntimeTest
{
	public/*for web.xml*/ static final Model MODEL = new Model(SHAItem.TYPE);
	
	private static final String EMPTY_HASH = "cf83e1357eefb8bdf1542850d66d8007d620e4050b5715dc83f4a921d36ce9ce47d0d13c5d85f2b0ff8318d2877eec2f63b931bd47417a81a538327af927da3e";

	public SHATest()
	{
		super(MODEL);
	}
	
	SHAItem item;
	
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		item = deleteOnTearDown(new SHAItem("musso"));
	}
	
	public void testMD5()
	{
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
		assertEquals("SHA512", item.password.getAlgorithmName());
		assertEquals(item.TYPE, item.password.getStorage().getType());
		assertEquals("passwordSHA512", item.password.getStorage().getName());
		assertEquals(false, item.password.getStorage().isFinal());
		assertEquals(false, item.password.getStorage().isMandatory());
		assertEquals(128, item.password.getStorage().getMinimumLength());
		assertEquals(128, item.password.getStorage().getMaximumLength());
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
		assertEquals("SHA512", item.passwordMandatory.getAlgorithmName());
		assertEquals(item.passwordMandatory, item.passwordMandatory.getStorage().getPattern());
		assertEquals(true, item.passwordMandatory.isInitial());
		assertEquals(false, item.passwordMandatory.isFinal());
		assertEquals(true, item.passwordMandatory.isMandatory());
		assertEquals(String.class, item.passwordMandatory.getInitialType());
		assertContains(MandatoryViolationException.class, item.passwordMandatory.getInitialExceptions());
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
			new JavaSecurityHash(true, "NIXUS");
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals(NoSuchAlgorithmException.class.getName()+": NIXUS MessageDigest not available", e.getMessage());
			assertEquals(NoSuchAlgorithmException.class, e.getCause().getClass());
		}

		assertNull(item.getPasswordSHA512());
		assertTrue(item.checkPassword(null));
		assertTrue(!item.checkPassword("bing"));
		assertContains(item, item.TYPE.search(item.password.equal(null)));
		assertContains(item.TYPE.search(item.password.equal("bing")));
		
		item.setPasswordSHA512("12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678");
		assertEquals("12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678", item.getPasswordSHA512());
		assertTrue(!item.checkPassword(null));
		assertTrue(!item.checkPassword("12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678"));
		assertContains(item.TYPE.search(item.password.equal(null)));
		assertContains(item.TYPE.search(item.password.equal("12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678")));

		item.setPassword("knollo");
		assertEquals("1835861e09d3f89ee0f3f0e875366cad0d1877615ad322be2fff1135eb8e6f1ee1f55ce00edd17ae1c2ad89a96e676dfb106a0a8e78a7ea71e3ac373a5426af6", item.getPasswordSHA512());
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
		assertEquals("cb7e2023fc2372d06a876501b2f8f6c5347ac23fd8e92c02b5efd8ce60e03240a60ca82d760849103455dfd26cd1c28695e5f51b41001f71496e8126d168eb84", item.getPasswordSHA512());
		assertTrue(!item.checkPassword(null));
		assertTrue(!item.checkPassword("bello"));
		assertTrue(item.checkPassword(longPlainText));

		// Test, that special characters produce different hashes
		// with different encodings.
		final String specialPlainText = "Viele Gr\u00fc\u00dfe";
		item.setPassword(specialPlainText);
		item.setPasswordLatin(specialPlainText);
		assertEquals("588a46bb7275c6a4ecc1254348e98a8abff50b148f7712cd8f5d9b85718294fc48f9671cab2ab3008491cd54b92395ad7f90b635e76d564123d07b44c1d527a2", item.getPasswordSHA512());
		assertEquals("1ce6008f9153bdf7c7e7b07db950d5824922d2245d0f7f47b89fe76cd2f4a62da38d54f19b608b292ee2268c1201da817f8aeb8ef47e4c9734a268c48d02ce63", item.getPasswordLatinSHA512());
		assertTrue(!item.checkPassword(null));
		assertTrue(!item.checkPassword("bello"));
		assertTrue(item.checkPassword(specialPlainText));
		assertTrue(!item.checkPasswordLatin(null));
		assertTrue(!item.checkPasswordLatin("bello"));
		assertTrue(item.checkPasswordLatin(specialPlainText));
	
		assertEquals("5ffb7d043d1cc5cf87dc71b29d8387b7b9d4a9d9858b68777cb83e61c1381538641971c4fbe08301b88c0bd39b107478de48c945448b21cfe8f76bb17be75333", item.getPasswordMandatorySHA512());
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
		assertEquals("fcb991d0f9270258af56da9a1d6f007ddd102ee4d3a3db08f6d9071df31d1cc5154cd51af2083e70e09acceddb16c99895cd9df2639758f10b7fe31388fcd83f", item.getPasswordMandatorySHA512());
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
		assertEquals(EMPTY_HASH, item.getPasswordMandatorySHA512());
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
		assertEquals(EMPTY_HASH, item.getPasswordMandatorySHA512());
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
		
		// reference example from http://de.wikipedia.org/wiki/Secure_Hash_Algorithm
		final String upper = "Franz jagt im komplett verwahrlosten Taxi quer durch Bayern";
		final String lower = "Frank jagt im komplett verwahrlosten Taxi quer durch Bayern";
		
		item.setPassword(upper);
		assertEquals("af9ed2de700433b803240a552b41b5a472a6ef3fe1431a722b2063c75e9f07451f67a28e37d09cde769424c96aea6f8971389db9e1993d6c565c3c71b855723c", item.getPasswordSHA512());
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
		assertEquals("90b30ef9902ae4c4c691d2d78c2f8fa0aa785afbc5545286b310f68e91dd2299c84a2484f0419fc5eaa7de598940799e1091c4948926ae1c9488dddae180bb80", item.getPasswordSHA512());
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

		item.setPasswordSHA512("12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678");
		assertEquals("12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678", item.getPasswordSHA512());
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
		assertEquals(EMPTY_HASH, item.getPasswordSHA512());
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
		assertEquals(null, item.getPasswordSHA512());
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
