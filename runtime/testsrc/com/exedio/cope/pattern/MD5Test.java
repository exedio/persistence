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

import static com.exedio.cope.pattern.MD5Item.TYPE;
import static com.exedio.cope.pattern.MD5Item.password;
import static com.exedio.cope.pattern.MessageDigestHashTest.algo;
import static com.exedio.cope.pattern.MessageDigestHashTest.encoding;
import static com.exedio.cope.tojunit.Assert.assertContains;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.Model;
import com.exedio.cope.TestWithEnvironment;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MD5Test extends TestWithEnvironment
{
	public static final Model MODEL = new Model(TYPE);

	static
	{
		MODEL.enableSerialization(MD5Test.class, "MODEL");
	}

	public MD5Test()
	{
		super(MODEL);
	}

	MD5Item item;

	@BeforeEach final void setUp()
	{
		item = new MD5Item(FRANK);
	}

	// reference example from https://de.wikipedia.org/wiki/MD5
	// duplicated in  MessageDigestAlgorithmTest
	private static final String FRANZ = "Franz jagt im komplett verwahrlosten Taxi quer durch Bayern";
	private static final String FRANK = "Frank jagt im komplett verwahrlosten Taxi quer durch Bayern";
	private static final String FRANZ_MD5 = "a3cca2b2aa1e3b5b3b5aad99a8529074";
	private static final String FRANK_MD5 = "7e716d0e702df0505fc72e2b89467910";
	private static final String EMPTY_MD5 = "d41d8cd98f00b204e9800998ecf8427e";

	@Test void testMD5()
	{
		assertEquals(Arrays.asList(
				TYPE.getThis(),
				password,
				password.getStorage()),
			TYPE.getFeatures());

		assertEquals(TYPE, password.getType());
		assertEquals("password", password.getName());
		assertEquals("MD5", password.getAlgorithmID());
		assertEquals(TYPE, password.getStorage().getType());
		assertEquals("password-MD5", password.getStorage().getName());
		assertEquals(false, password.getStorage().isFinal());
		assertEquals(true, password.getStorage().isMandatory());
		assertEquals(32, password.getStorage().getMinimumLength());
		assertEquals(32, password.getStorage().getMaximumLength());
		assertEquals(password, password.getStorage().getPattern());
		assertEquals(true, password.isInitial());
		assertEquals(false, password.isFinal());
		assertEquals(true, password.isMandatory());
		assertEquals(String.class, password.getInitialType());
		assertContains(MandatoryViolationException.class, password.getInitialExceptions());
		assertEquals(UTF_8.name(), encoding(password));
		assertEquals(1, algo(password).getIterations());

		assertEquals(FRANZ_MD5, password.hash(FRANZ));
		assertEquals(FRANK_MD5, password.hash(FRANK));
		assertEquals(EMPTY_MD5, password.hash(""));

		assertEquals(FRANK_MD5, item.getPasswordMD5());
		assertTrue(item.checkPassword(FRANK));
		assertTrue(!item.checkPassword(FRANZ));
		assertTrue(!item.checkPassword(""));
		assertTrue(!item.checkPassword(null));
		assertContains(TYPE.search(password.isNull()));
		assertContains(item, TYPE.search(password.isNotNull()));

		item.setPassword("");
		assertEquals(EMPTY_MD5, item.getPasswordMD5());
		assertTrue(!item.checkPassword(FRANZ));
		assertTrue(!item.checkPassword(FRANK));
		assertTrue(item.checkPassword(""));
		assertTrue(!item.checkPassword(null));
		assertContains(TYPE.search(password.isNull()));
		assertContains(item, TYPE.search(password.isNotNull()));

		item.setPassword(FRANZ);
		assertEquals(FRANZ_MD5, item.getPasswordMD5());
		assertTrue(item.checkPassword(FRANZ));
		assertTrue(!item.checkPassword(FRANK));
		assertTrue(!item.checkPassword(""));
		assertTrue(!item.checkPassword(null));
		assertContains(TYPE.search(password.isNull()));
		assertContains(item, TYPE.search(password.isNotNull()));

		item.setPassword(FRANK);
		assertEquals(FRANK_MD5, item.getPasswordMD5());
		assertTrue(!item.checkPassword(FRANZ));
		assertTrue(item.checkPassword(FRANK));
		assertTrue(!item.checkPassword(""));
		assertTrue(!item.checkPassword(null));
		assertContains(TYPE.search(password.isNull()));
		assertContains(item, TYPE.search(password.isNotNull()));
	}
}
