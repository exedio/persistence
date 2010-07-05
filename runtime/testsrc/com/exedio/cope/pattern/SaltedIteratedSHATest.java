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

public class SaltedIteratedSHATest extends AbstractRuntimeTest
{
	public/*for web.xml*/ static final Model MODEL = new Model(SaltedIteratedSHAItem.TYPE);

	static
	{
		MODEL.enableSerialization(SaltedIteratedSHATest.class, "MODEL");
	}

	private static final String EMPTY_HASH = "aeab417a9b5a7cf314339787d765de2fa913946ad6786572c9a4f22d16339411057e7a27c94421f5e6471998cc5a6301029f5272243a8dee889dd23fcd45410658556608a18f0d91";

	public SaltedIteratedSHATest()
	{
		super(MODEL);
	}

	SaltedIteratedSHAItem item;

	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		final Random byMandatory = ((MessageDigestAlgorithm)item.passwordMandatory.getAlgorithm()).setSaltSource(new Random(2345l));
		item = deleteOnTearDown(new SaltedIteratedSHAItem("musso"));
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
		assertEquals("SHA512s8i5", item.password.getAlgorithmName());
		assertEquals(item.TYPE, item.password.getStorage().getType());
		assertEquals("password-SHA512s8i5", item.password.getStorage().getName());
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
		assertEquals(5, ((MessageDigestAlgorithm)item.password.getAlgorithm()).getIterations());

		assertEquals(item.TYPE, item.passwordLatin.getType());
		assertEquals("passwordLatin", item.passwordLatin.getName());
		assertEquals(item.passwordLatin, item.passwordLatin.getStorage().getPattern());
		assertEquals(false, item.passwordLatin.isInitial());
		assertEquals(false, item.passwordLatin.isFinal());
		assertEquals(false, item.passwordLatin.isMandatory());
		assertEquals(String.class, item.passwordLatin.getInitialType());
		assertContains(item.passwordLatin.getInitialExceptions());
		assertEquals("ISO-8859-1", item.passwordLatin.getEncoding());
		assertEquals(5, ((MessageDigestAlgorithm)item.passwordLatin.getAlgorithm()).getIterations());

		assertEquals(item.TYPE, item.passwordMandatory.getType());
		assertEquals("passwordMandatory", item.passwordMandatory.getName());
		assertEquals("SHA512s8i5", item.passwordMandatory.getAlgorithmName());
		assertEquals(item.passwordMandatory, item.passwordMandatory.getStorage().getPattern());
		assertEquals(true, item.passwordMandatory.isInitial());
		assertEquals(false, item.passwordMandatory.isFinal());
		assertEquals(true, item.passwordMandatory.isMandatory());
		assertEquals(String.class, item.passwordMandatory.getInitialType());
		assertContains(MandatoryViolationException.class, item.passwordMandatory.getInitialExceptions());
		assertEquals("utf8", item.passwordMandatory.getEncoding());
		assertEquals(5, ((MessageDigestAlgorithm)item.passwordMandatory.getAlgorithm()).getIterations());

		assertSerializedSame(item.password         , 400);
		assertSerializedSame(item.passwordLatin    , 405);
		assertSerializedSame(item.passwordMandatory, 409);

		item.blindPassword(null);
		item.blindPassword("");
		item.blindPassword("bing");
		item.blindPasswordLatin(null);
		item.blindPasswordLatin("");
		item.blindPasswordLatin("bing");
		item.blindPasswordMandatory(null);
		item.blindPasswordMandatory("");
		item.blindPasswordMandatory("bing");

		assertNull(item.getPasswordSHA512s8i5());
		assertTrue(item.checkPassword(null));
		assertTrue(!item.checkPassword("bing"));
		assertContains(item, item.TYPE.search(item.password.isNull()));

		item.setPasswordSHA512s8i5("123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234");
		assertEquals("123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234", item.getPasswordSHA512s8i5());
		assertTrue(!item.checkPassword(null));
		assertTrue(!item.checkPassword("123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234"));
		assertContains(item.TYPE.search(item.password.isNull()));

		item.setPassword("knollo");
		assertEquals("aeab417a9b5a7cf39a91d054d056ff387266c789c26ccff7677c01cca150b1575db3db8bca64f7d027a606f692cb3f6e6ff3cfac5c4c458007a9fac9db7b877707f300f0a904ec4a", item.getPasswordSHA512s8i5());
		assertTrue(!item.checkPassword(null));
		assertTrue(!item.checkPassword("bello"));
		assertTrue(item.checkPassword("knollo"));
		assertContains(item.TYPE.search(item.password.isNull()));

		final String longPlainText =
			"knolloknolloknolloknolloknolloknolloknolloknolloknolloknolloknollo" +
			"knolloknolloknolloknolloknolloknolloknolloknolloknolloknolloknollo" +
			"knolloknolloknolloknolloknollo";
		item.setPassword(longPlainText);
		assertEquals("aeab417a9b5a7cf3868ff1153f6d0807b9e4e859112e559cb1c0ae0de8e00c9046e0722338d820408267487d618d5c5edbdeedf53d6fbd9949896dd92e38bcd386c2f651886b79db", item.getPasswordSHA512s8i5());
		assertTrue(!item.checkPassword(null));
		assertTrue(!item.checkPassword("bello"));
		assertTrue(item.checkPassword(longPlainText));

		// Test, that special characters produce different hashes
		// with different encodings.
		final String specialPlainText = "Viele Gr\u00fc\u00dfe";
		item.setPassword(specialPlainText);
		item.setPasswordLatin(specialPlainText);
		assertEquals("aeab417a9b5a7cf38932c06db7a26d916da5f03f1df9518806a376cd201d8d5c2ce45cea63ea11f8ecef42b20a59f6f1399e90667cd3bdd54009d3159247a8b5b39ad2905ea15f13", item.getPasswordSHA512s8i5());
		assertEquals("aeab417a9b5a7cf36f11ed939867723f176900657535468c42000af9ecafcc492d1f4b895577888069a027a8df8e9ba1bd2d953e14a4f0a1727ec4632562d56f18183dbb8bfa015e", item.getPasswordLatinSHA512s8i5());
		assertTrue(!item.checkPassword(null));
		assertTrue(!item.checkPassword("bello"));
		assertTrue(item.checkPassword(specialPlainText));
		assertTrue(!item.checkPasswordLatin(null));
		assertTrue(!item.checkPasswordLatin("bello"));
		assertTrue(item.checkPasswordLatin(specialPlainText));

		assertEquals("885406ef34cef302a5b5577715808f6cae847208da6117c46119ab13d30f7c464ccc72e28e9d9b52c0be210c0ac25f7938327f63c6c8b67557e1f011b5d0e68a5a7beab6485b495a", item.getPasswordMandatorySHA512s8i5());
		assertTrue(item.checkPasswordMandatory("musso"));
		assertTrue(!item.checkPasswordMandatory("mussx"));
		assertTrue(!item.checkPasswordMandatory(""));
		assertTrue(!item.checkPasswordMandatory(null));
		assertContains(item.TYPE.search(item.passwordMandatory.isNull()));
		assertContains(item, item.TYPE.search(item.passwordMandatory.isNotNull()));

		item.setPasswordMandatory("mussx");
		assertEquals("aeab417a9b5a7cf3d970dd5d411fe76615c3f8934a224cd32d98ecb34de62c27e1c488e895695f1d04655cd4ea8183d668a3093a9df730493e0cf61a75839a5cca91659d8ba551b6", item.getPasswordMandatorySHA512s8i5());
		assertTrue(!item.checkPasswordMandatory("musso"));
		assertTrue(item.checkPasswordMandatory("mussx"));
		assertTrue(!item.checkPasswordMandatory(""));
		assertTrue(!item.checkPasswordMandatory(null));
		assertContains(item.TYPE.search(item.passwordMandatory.isNull()));
		assertContains(item, item.TYPE.search(item.passwordMandatory.isNotNull()));

		item.setPasswordMandatory("");
		assertEquals(EMPTY_HASH, item.getPasswordMandatorySHA512s8i5());
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
		assertEquals(EMPTY_HASH, item.getPasswordMandatorySHA512s8i5());
		assertTrue(!item.checkPasswordMandatory("musso"));
		assertTrue(!item.checkPasswordMandatory("mussx"));
		assertTrue(item.checkPasswordMandatory(""));
		assertTrue(!item.checkPasswordMandatory(null));
		assertContains(item.TYPE.search(item.passwordMandatory.isNull()));
		assertContains(item, item.TYPE.search(item.passwordMandatory.isNotNull()));

		item.setPasswordSHA512s8i5("123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234");
		assertEquals("123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234", item.getPasswordSHA512s8i5());
		assertTrue(!item.checkPassword(""));
		assertTrue(!item.checkPassword(null));
		assertContains(item.TYPE.search(item.password.isNull()));
		assertContains(item, item.TYPE.search(item.password.isNotNull()));

		item.setPassword("");
		assertEquals(EMPTY_HASH, item.getPasswordSHA512s8i5());
		assertTrue(item.checkPassword(""));
		assertTrue(!item.checkPassword(null));
		assertContains(item.TYPE.search(item.password.isNull()));
		assertContains(item, item.TYPE.search(item.password.isNotNull()));

		item.setPassword(null);
		assertEquals(null, item.getPasswordSHA512s8i5());
		assertTrue(!item.checkPassword(""));
		assertTrue(item.checkPassword(null));
		assertContains(item, item.TYPE.search(item.password.isNull()));
		assertContains(item.TYPE.search(item.password.isNotNull()));
	}
}
