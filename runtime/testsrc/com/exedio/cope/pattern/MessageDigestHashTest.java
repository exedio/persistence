/*
 * Copyright (C) 2004-2015  exedio GmbH (www.exedio.com)
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

import static com.exedio.cope.RuntimeAssert.assertSerializedSame;
import static com.exedio.cope.pattern.MessageDigestHashItem.TYPE;
import static com.exedio.cope.pattern.MessageDigestHashItem.blindPassword;
import static com.exedio.cope.pattern.MessageDigestHashItem.blindPasswordLatin;
import static com.exedio.cope.pattern.MessageDigestHashItem.blindPasswordMandatory;
import static com.exedio.cope.pattern.MessageDigestHashItem.password;
import static com.exedio.cope.pattern.MessageDigestHashItem.passwordFinal;
import static com.exedio.cope.pattern.MessageDigestHashItem.passwordLatin;
import static com.exedio.cope.pattern.MessageDigestHashItem.passwordMandatory;
import static com.exedio.cope.tojunit.Assert.assertContains;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.FinalViolationException;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.Model;
import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.util.Hex;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MessageDigestHashTest extends TestWithEnvironment
{
	public/*for web.xml*/ static final Model MODEL = new Model(TYPE);

	static
	{
		MODEL.enableSerialization(MessageDigestHashTest.class, "MODEL");
	}

	private static final String EMPTY_HASH = "aeab417a9b5a7cf314339787d765de2fa913946ad6786572c9a4f22d16339411057e7a27c94421f5e6471998cc5a6301029f5272243a8dee889dd23fcd45410658556608a18f0d91";

	public MessageDigestHashTest()
	{
		super(MODEL);
	}

	MessageDigestHashItem item;

	@BeforeEach final void setUp()
	{
		expectSalt(passwordFinal, "885406ef34cef302");
		expectSalt(passwordMandatory, "885406ef34cef302");
		item = new MessageDigestHashItem("finalo", "musso");
	}

	private static void expectSalt(final Hash hash, final String bytes)
	{
		((MockSecureRandom)algo(hash).getSaltSource()).expectNextBytes(Hex.decodeLower(bytes));
	}

	@Test void testMD5()
	{
		assertEquals(Arrays.asList(
				TYPE.getThis(),
				password,
				password.getStorage(),
				passwordLatin,
				passwordLatin.getStorage(),
				passwordFinal,
				passwordFinal.getStorage(),
				passwordMandatory,
				passwordMandatory.getStorage()),
			TYPE.getFeatures());

		assertEquals(TYPE, password.getType());
		assertEquals("password", password.getName());
		assertEquals("SHA512s8i5", password.getAlgorithmID());
		assertEquals("SHA512s8i5", password.getAlgorithm2().getID());
		assertEquals("SHA512s8i5", password.getAlgorithm2().getDescription());
		assertEquals(TYPE, password.getStorage().getType());
		assertEquals("password-SHA512s8i5", password.getStorage().getName());
		assertEquals(144, password.getStorage().getMinimumLength());
		assertEquals(144, password.getStorage().getMaximumLength());
		assertEquals(password, password.getStorage().getPattern());
		assertEquals(false, password.isInitial());
		assertEquals(false, password.isFinal());
		assertEquals(false, password.getStorage().isFinal());
		assertEquals(false, password.isMandatory());
		assertEquals(false, password.getStorage().isMandatory());
		assertEquals(String.class, password.getInitialType());
		assertContains(password.getInitialExceptions());
		assertEquals(UTF_8.name(), encoding(password));
		assertEquals(5, algo(password).getIterations());

		assertEquals(TYPE, passwordLatin.getType());
		assertEquals("passwordLatin", passwordLatin.getName());
		assertEquals(passwordLatin, passwordLatin.getStorage().getPattern());
		assertEquals(false, passwordLatin.isInitial());
		assertEquals(false, passwordLatin.isFinal());
		assertEquals(false, passwordLatin.isMandatory());
		assertEquals(String.class, passwordLatin.getInitialType());
		assertContains(passwordLatin.getInitialExceptions());
		assertEquals("ISO-8859-1", encoding(passwordLatin));
		assertEquals(5, algo(passwordLatin).getIterations());

		assertEquals(TYPE, passwordFinal.getType());
		assertEquals("passwordFinal", passwordFinal.getName());
		assertEquals("SHA512s8i5", passwordFinal.getAlgorithmID());
		assertEquals(passwordFinal, passwordFinal.getStorage().getPattern());
		assertEquals(true, passwordFinal.isInitial());
		assertEquals(true, passwordFinal.isFinal());
		assertEquals(true, passwordFinal.getStorage().isFinal());
		assertEquals(true, passwordFinal.isMandatory());
		assertEquals(true, passwordFinal.getStorage().isMandatory());
		assertEquals(String.class, passwordFinal.getInitialType());
		assertContains(MandatoryViolationException.class, FinalViolationException.class, passwordFinal.getInitialExceptions());
		assertEquals(UTF_8.name(), encoding(passwordFinal));
		assertEquals(5, algo(passwordFinal).getIterations());

		assertEquals(TYPE, passwordMandatory.getType());
		assertEquals("passwordMandatory", passwordMandatory.getName());
		assertEquals("SHA512s8i5", passwordMandatory.getAlgorithmID());
		assertEquals(passwordMandatory, passwordMandatory.getStorage().getPattern());
		assertEquals(true, passwordMandatory.isInitial());
		assertEquals(false, passwordMandatory.isFinal());
		assertEquals(false, passwordMandatory.getStorage().isFinal());
		assertEquals(true, passwordMandatory.isMandatory());
		assertEquals(true, passwordMandatory.getStorage().isMandatory());
		assertEquals(String.class, passwordMandatory.getInitialType());
		assertContains(MandatoryViolationException.class, passwordMandatory.getInitialExceptions());
		assertEquals(UTF_8.name(), encoding(passwordMandatory));
		assertEquals(5, algo(passwordMandatory).getIterations());

		assertSerializedSame(password         , 408);
		assertSerializedSame(passwordLatin    , 413);
		assertSerializedSame(passwordMandatory, 417);

		blindPassword(null);
		expectSalt(password, "885406ef34cef302");
		blindPassword("");
		blindPassword("bing");
		blindPasswordLatin(null);
		expectSalt(passwordLatin, "885406ef34cef302");
		blindPasswordLatin("");
		blindPasswordLatin("bing");
		blindPasswordMandatory(null);
		expectSalt(passwordMandatory, "885406ef34cef302");
		blindPasswordMandatory("");
		blindPasswordMandatory("bing");

		assertNull(item.getPasswordSHA512s8i5());
		assertTrue(item.checkPassword(null));
		assertTrue(!item.checkPassword("bing"));
		assertContains(item, TYPE.search(password.isNull()));

		item.setPasswordSHA512s8i5("123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234");
		assertEquals("123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234", item.getPasswordSHA512s8i5());
		assertTrue(!item.checkPassword(null));
		assertTrue(!item.checkPassword("123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234"));
		assertContains(TYPE.search(password.isNull()));

		// duplicated in  MessageDigestAlgorithmTest
		item.setPassword("knollo");
		assertEquals("aeab417a9b5a7cf39a91d054d056ff387266c789c26ccff7677c01cca150b1575db3db8bca64f7d027a606f692cb3f6e6ff3cfac5c4c458007a9fac9db7b877707f300f0a904ec4a", item.getPasswordSHA512s8i5());
		assertTrue(!item.checkPassword(null));
		assertTrue(!item.checkPassword("bello"));
		assertTrue(item.checkPassword("knollo"));
		assertContains(TYPE.search(password.isNull()));

		// duplicated in  MessageDigestAlgorithmTest
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

		assertEquals("885406ef34cef302ae05cffbfc7d490a7a38e92c241014c2cb8667fa30f039590c649c0d80b5ab21a0d5bae8ab016dcb43d9b233962917c61827f1e924c98ffed30e4675ab08230c", item.getPasswordFinalSHA512s8i5());
		assertTrue(item.checkPasswordFinal("finalo"));
		assertTrue(!item.checkPasswordFinal("finalox"));
		assertTrue(!item.checkPasswordFinal(""));
		assertTrue(!item.checkPasswordFinal(null));
		assertContains(TYPE.search(passwordFinal.isNull()));
		assertContains(item, TYPE.search(passwordFinal.isNotNull()));

		expectSalt(passwordFinal, "aeab417a9b5a7cf3");
		try
		{
			passwordFinal.set(item, "finalox");
			fail();
		}
		catch(final FinalViolationException e)
		{
			assertEquals(item, e.getItem());
			assertEquals(passwordFinal, e.getFeature());
		}
		assertEquals("885406ef34cef302ae05cffbfc7d490a7a38e92c241014c2cb8667fa30f039590c649c0d80b5ab21a0d5bae8ab016dcb43d9b233962917c61827f1e924c98ffed30e4675ab08230c", item.getPasswordFinalSHA512s8i5());
		assertTrue(item.checkPasswordFinal("finalo"));
		assertTrue(!item.checkPasswordFinal("finalox"));
		assertTrue(!item.checkPasswordFinal(""));
		assertTrue(!item.checkPasswordFinal(null));
		assertContains(TYPE.search(passwordFinal.isNull()));
		assertContains(item, TYPE.search(passwordFinal.isNotNull()));

		assertEquals("885406ef34cef302a5b5577715808f6cae847208da6117c46119ab13d30f7c464ccc72e28e9d9b52c0be210c0ac25f7938327f63c6c8b67557e1f011b5d0e68a5a7beab6485b495a", item.getPasswordMandatorySHA512s8i5());
		assertTrue(item.checkPasswordMandatory("musso"));
		assertTrue(!item.checkPasswordMandatory("mussx"));
		assertTrue(!item.checkPasswordMandatory(""));
		assertTrue(!item.checkPasswordMandatory(null));
		assertContains(TYPE.search(passwordMandatory.isNull()));
		assertContains(item, TYPE.search(passwordMandatory.isNotNull()));

		item.setPasswordMandatory("mussx");
		assertEquals("aeab417a9b5a7cf3d970dd5d411fe76615c3f8934a224cd32d98ecb34de62c27e1c488e895695f1d04655cd4ea8183d668a3093a9df730493e0cf61a75839a5cca91659d8ba551b6", item.getPasswordMandatorySHA512s8i5());
		assertTrue(!item.checkPasswordMandatory("musso"));
		assertTrue(item.checkPasswordMandatory("mussx"));
		assertTrue(!item.checkPasswordMandatory(""));
		assertTrue(!item.checkPasswordMandatory(null));
		assertContains(TYPE.search(passwordMandatory.isNull()));
		assertContains(item, TYPE.search(passwordMandatory.isNotNull()));

		// duplicated in  MessageDigestAlgorithmTest
		item.setPasswordMandatory("");
		assertEquals(EMPTY_HASH, item.getPasswordMandatorySHA512s8i5());
		assertTrue(!item.checkPasswordMandatory("musso"));
		assertTrue(!item.checkPasswordMandatory("mussx"));
		assertTrue(item.checkPasswordMandatory(""));
		assertTrue(!item.checkPasswordMandatory(null));
		assertContains(TYPE.search(passwordMandatory.isNull()));
		assertContains(item, TYPE.search(passwordMandatory.isNotNull()));

		try
		{
			item.setPasswordMandatory(null);
			fail();
		}
		catch(final MandatoryViolationException e)
		{
			assertEquals(passwordMandatory.getStorage(), e.getFeature());
			assertEquals(item, e.getItem());
		}
		password.checkPlainText(null);
		try
		{
			passwordMandatory.checkPlainText(null);
			fail();
		}
		catch(final MandatoryViolationException e)
		{
			assertEquals(passwordMandatory, e.getFeature());
			assertEquals(null, e.getItem());
		}
		assertEquals(EMPTY_HASH, item.getPasswordMandatorySHA512s8i5());
		assertTrue(!item.checkPasswordMandatory("musso"));
		assertTrue(!item.checkPasswordMandatory("mussx"));
		assertTrue(item.checkPasswordMandatory(""));
		assertTrue(!item.checkPasswordMandatory(null));
		assertContains(TYPE.search(passwordMandatory.isNull()));
		assertContains(item, TYPE.search(passwordMandatory.isNotNull()));

		item.setPasswordSHA512s8i5("123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234");
		assertEquals("123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234", item.getPasswordSHA512s8i5());
		assertTrue(!item.checkPassword(""));
		assertTrue(!item.checkPassword(null));
		assertContains(TYPE.search(password.isNull()));
		assertContains(item, TYPE.search(password.isNotNull()));

		item.setPassword("");
		assertEquals(EMPTY_HASH, item.getPasswordSHA512s8i5());
		assertTrue(item.checkPassword(""));
		assertTrue(!item.checkPassword(null));
		assertContains(TYPE.search(password.isNull()));
		assertContains(item, TYPE.search(password.isNotNull()));

		item.setPassword(null);
		assertEquals(null, item.getPasswordSHA512s8i5());
		assertTrue(!item.checkPassword(""));
		assertTrue(item.checkPassword(null));
		assertContains(item, TYPE.search(password.isNull()));
		assertContains(TYPE.search(password.isNotNull()));
	}

	@SuppressWarnings("deprecation")
	static MessageDigestAlgorithm algo(final Hash hash)
	{
		return (MessageDigestAlgorithm)hash.getAlgorithm();
	}

	static String encoding(final Hash hash)
	{
		return ((AlgorithmAdapter)hash.getAlgorithm2()).charset.name();
	}
}
