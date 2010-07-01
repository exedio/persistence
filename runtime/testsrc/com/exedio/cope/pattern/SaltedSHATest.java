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

import java.util.Arrays;
import java.util.Random;

import com.exedio.cope.AbstractRuntimeTest;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.Model;

public class SaltedSHATest extends AbstractRuntimeTest
{
	public/*for web.xml*/ static final Model MODEL = new Model(SaltedSHAItem.TYPE);
	
	static
	{
		MODEL.enableSerialization(SaltedSHATest.class, "MODEL");
	}
	
	private static final String EMPTY_HASH = "aeab417a9b5a7cf379c224f53a48f3ba32de8c9f5e12a2d78e281665c88b4addfe9c5357e1edd5f74ce7b0a2822dbb4a4274627d5e87bc8f24db5999b18dfe812bb037e1196bb4bc";

	public SaltedSHATest()
	{
		super(MODEL);
	}
	
	SaltedSHAItem item;
	
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		final Random byMandatory = ((MessageDigestAlgorithm)item.passwordMandatory.getAlgorithm()).setSaltSource(new Random(2345l));
		item = deleteOnTearDown(new SaltedSHAItem("musso"));
		((MessageDigestAlgorithm)item.passwordMandatory.getAlgorithm()).setSaltSource(byMandatory);
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
		assertEquals("SHA512s8", item.password.getAlgorithmName());
		assertEquals(item.TYPE, item.password.getStorage().getType());
		assertEquals("password-SHA512s8", item.password.getStorage().getName());
		assertEquals(false, item.password.getStorage().isFinal());
		assertEquals(false, item.password.getStorage().isMandatory());
		assertEquals(144, item.password.getStorage().getMinimumLength());
		assertEquals(144, item.password.getStorage().getMaximumLength());
		assertEquals(item.password, item.password.getStorage().getPattern());
		assertEquals(false, item.password.isInitial());
		assertEquals(false, item.password.isFinal());
		assertEquals(false, item.password.isMandatory());
		assertEquals(String.class, item.password.getInitialType());
		assertContains(item.password.getInitialExceptions());
		assertEquals("utf8", item.password.getEncoding());
		assertEquals(1, ((MessageDigestAlgorithm)item.password.getAlgorithm()).getIterations());
		
		assertEquals(item.TYPE, item.passwordLatin.getType());
		assertEquals("passwordLatin", item.passwordLatin.getName());
		assertEquals(item.passwordLatin, item.passwordLatin.getStorage().getPattern());
		assertEquals(false, item.passwordLatin.isInitial());
		assertEquals(false, item.passwordLatin.isFinal());
		assertEquals(false, item.passwordLatin.isMandatory());
		assertEquals(String.class, item.passwordLatin.getInitialType());
		assertContains(item.passwordLatin.getInitialExceptions());
		assertEquals("ISO-8859-1", item.passwordLatin.getEncoding());
		assertEquals(1, ((MessageDigestAlgorithm)item.passwordLatin.getAlgorithm()).getIterations());

		assertEquals(item.TYPE, item.passwordMandatory.getType());
		assertEquals("passwordMandatory", item.passwordMandatory.getName());
		assertEquals("SHA512s8", item.passwordMandatory.getAlgorithmName());
		assertEquals(item.passwordMandatory, item.passwordMandatory.getStorage().getPattern());
		assertEquals(true, item.passwordMandatory.isInitial());
		assertEquals(false, item.passwordMandatory.isFinal());
		assertEquals(true, item.passwordMandatory.isMandatory());
		assertEquals(String.class, item.passwordMandatory.getInitialType());
		assertContains(MandatoryViolationException.class, item.passwordMandatory.getInitialExceptions());
		assertEquals("utf8", item.passwordMandatory.getEncoding());
		assertEquals(1, ((MessageDigestAlgorithm)item.passwordMandatory.getAlgorithm()).getIterations());
		
		assertSerializedSame(item.password         , 384);
		assertSerializedSame(item.passwordLatin    , 389);
		assertSerializedSame(item.passwordMandatory, 393);
		
		item.blindPassword(null);
		item.blindPassword("");
		item.blindPassword("bing");
		item.blindPasswordLatin(null);
		item.blindPasswordLatin("");
		item.blindPasswordLatin("bing");
		item.blindPasswordMandatory(null);
		item.blindPasswordMandatory("");
		item.blindPasswordMandatory("bing");
		
		assertNull(item.getPasswordSHA512s8());
		assertTrue(item.checkPassword(null));
		assertTrue(!item.checkPassword("bing"));
		assertContains(item, item.TYPE.search(item.password.isNull()));
		
		item.setPasswordSHA512s8("123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234");
		assertEquals("123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234", item.getPasswordSHA512s8());
		assertTrue(!item.checkPassword(null));
		assertTrue(!item.checkPassword("123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234"));
		assertContains(item.TYPE.search(item.password.isNull()));

		item.setPassword("knollo");
		assertEquals("aeab417a9b5a7cf385decb666a4d572c9962e9c042e0fc33718b2cbabc28a866c35594f6c17596dedb0437dedb652eb2854d9645e7aa80926538923763b733f8ad00747248df9ade", item.getPasswordSHA512s8());
		assertTrue(!item.checkPassword(null));
		assertTrue(!item.checkPassword("bello"));
		assertTrue(item.checkPassword("knollo"));
		assertContains(item.TYPE.search(item.password.isNull()));
		
		final String longPlainText =
			"knolloknolloknolloknolloknolloknolloknolloknolloknolloknolloknollo" +
			"knolloknolloknolloknolloknolloknolloknolloknolloknolloknolloknollo" +
			"knolloknolloknolloknolloknollo";
		item.setPassword(longPlainText);
		assertEquals("aeab417a9b5a7cf39904017f7a5e22767e17c88ec0b1442490df10531c7806f803b07dac383380623df954bef6ce5da18fdc82d1baf7146fbd3e95be7c00acf08c4062f624510b20", item.getPasswordSHA512s8());
		assertTrue(!item.checkPassword(null));
		assertTrue(!item.checkPassword("bello"));
		assertTrue(item.checkPassword(longPlainText));

		// Test, that special characters produce different hashes
		// with different encodings.
		final String specialPlainText = "Viele Gr\u00fc\u00dfe";
		item.setPassword(specialPlainText);
		item.setPasswordLatin(specialPlainText);
		assertEquals("aeab417a9b5a7cf39b34c0af850a7a5d37d8d44b68e54146f9d89e640236c2ccb70cf98727dde23892065bd720c1b8684e308a005b2f68521a4b6c61a6def51d80264c2af6122f6e", item.getPasswordSHA512s8());
		assertEquals("aeab417a9b5a7cf3c3f367f3c1a35efab4b40431e7b3e771c5c5cf39137e767da919877b3a549bef724250f4bd9f5814ddb2e52b34341a321b3b7d510f8f83663cbe186c2a63fe2e", item.getPasswordLatinSHA512s8());
		assertTrue(!item.checkPassword(null));
		assertTrue(!item.checkPassword("bello"));
		assertTrue(item.checkPassword(specialPlainText));
		assertTrue(!item.checkPasswordLatin(null));
		assertTrue(!item.checkPasswordLatin("bello"));
		assertTrue(item.checkPasswordLatin(specialPlainText));
	
		assertEquals("885406ef34cef3027d1d2bb87abb6b695a3f9b61acd946fcdbfa55d8818c84df4cdff978c951eacab0ffd49e8c8f1778b0bc4a969b6ba7cecb2573a22a3b55414496464b1b143acb", item.getPasswordMandatorySHA512s8());
		assertTrue(item.checkPasswordMandatory("musso"));
		assertTrue(!item.checkPasswordMandatory("mussx"));
		assertTrue(!item.checkPasswordMandatory(""));
		assertTrue(!item.checkPasswordMandatory(null));
		assertContains(item.TYPE.search(item.passwordMandatory.isNull()));
		assertContains(item, item.TYPE.search(item.passwordMandatory.isNotNull()));
		
		item.setPasswordMandatory("mussx");
		assertEquals("aeab417a9b5a7cf3c290abe28f8ecf35715d3c1d6423bb526498ad0359b0ffaaa40ef925727e85f585fb7df73c6ec3f6432afd7f2281a7d87ff98bc76788e98f7dd3335c2048cb7a", item.getPasswordMandatorySHA512s8());
		assertTrue(!item.checkPasswordMandatory("musso"));
		assertTrue(item.checkPasswordMandatory("mussx"));
		assertTrue(!item.checkPasswordMandatory(""));
		assertTrue(!item.checkPasswordMandatory(null));
		assertContains(item.TYPE.search(item.passwordMandatory.isNull()));
		assertContains(item, item.TYPE.search(item.passwordMandatory.isNotNull()));
		
		item.setPasswordMandatory("");
		assertEquals(EMPTY_HASH, item.getPasswordMandatorySHA512s8());
		assertTrue(!item.checkPasswordMandatory("musso"));
		assertTrue(!item.checkPasswordMandatory("mussx"));
		assertTrue(item.checkPasswordMandatory(""));
		assertTrue(!item.checkPasswordMandatory(null));
		assertContains(item.TYPE.search(item.passwordMandatory.isNull()));
		assertContains(item, item.TYPE.search(item.passwordMandatory.isNotNull()));

		try
		{
			item.setPasswordMandatory(null);
			fail();
		}
		catch(MandatoryViolationException e)
		{
			assertEquals(item.passwordMandatory.getStorage(), e.getFeature());
		}
		assertEquals(EMPTY_HASH, item.getPasswordMandatorySHA512s8());
		assertTrue(!item.checkPasswordMandatory("musso"));
		assertTrue(!item.checkPasswordMandatory("mussx"));
		assertTrue(item.checkPasswordMandatory(""));
		assertTrue(!item.checkPasswordMandatory(null));
		assertContains(item.TYPE.search(item.passwordMandatory.isNull()));
		assertContains(item, item.TYPE.search(item.passwordMandatory.isNotNull()));
		
		item.setPasswordSHA512s8("123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234");
		assertEquals("123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234", item.getPasswordSHA512s8());
		assertTrue(!item.checkPassword(""));
		assertTrue(!item.checkPassword(null));
		assertContains(item.TYPE.search(item.password.isNull()));
		assertContains(item, item.TYPE.search(item.password.isNotNull()));

		item.setPassword("");
		assertEquals(EMPTY_HASH, item.getPasswordSHA512s8());
		assertTrue(item.checkPassword(""));
		assertTrue(!item.checkPassword(null));
		assertContains(item.TYPE.search(item.password.isNull()));
		assertContains(item, item.TYPE.search(item.password.isNotNull()));

		item.setPassword(null);
		assertEquals(null, item.getPasswordSHA512s8());
		assertTrue(!item.checkPassword(""));
		assertTrue(item.checkPassword(null));
		assertContains(item, item.TYPE.search(item.password.isNull()));
		assertContains(item.TYPE.search(item.password.isNotNull()));
	}
}
