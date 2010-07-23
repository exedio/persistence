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

	// reference example from http://de.wikipedia.org/wiki/MD5
	// duplicated in  MessageDigestAlgorithmTest
	private static final String FRANZ = "Franz jagt im komplett verwahrlosten Taxi quer durch Bayern";
	private static final String FRANK = "Frank jagt im komplett verwahrlosten Taxi quer durch Bayern";

	public void testMD5()
	{
		assertEquals(Arrays.asList(
				item.TYPE.getThis(),
				item.password,
				item.password.getStorage()),
			item.TYPE.getFeatures());

		assertEquals(item.TYPE, item.password.getType());
		assertEquals("password", item.password.getName());
		assertEquals("MD5", item.password.getAlgorithmName());
		assertEquals(item.TYPE, item.password.getStorage().getType());
		assertEquals("password-MD5", item.password.getStorage().getName());
		assertEquals(false, item.password.getStorage().isFinal());
		assertEquals(true, item.password.getStorage().isMandatory());
		assertEquals(32, item.password.getStorage().getMinimumLength());
		assertEquals(32, item.password.getStorage().getMaximumLength());
		assertEquals(item.password, item.password.getStorage().getPattern());
		assertEquals(true, item.password.isInitial());
		assertEquals(false, item.password.isFinal());
		assertEquals(true, item.password.isMandatory());
		assertEquals(String.class, item.password.getInitialType());
		assertContains(MandatoryViolationException.class, item.password.getInitialExceptions());
		assertEquals("utf8", item.password.getEncoding());
		assertEquals(1, ((MessageDigestAlgorithm)item.password.getAlgorithm()).getIterations());

		assertEquals("780e05d22aa148f225ea2d9f0e97b109", item.getPasswordMD5());
		assertTrue(item.checkPassword("musso"));
		assertTrue(!item.checkPassword("mussx"));
		assertTrue(!item.checkPassword(""));
		assertTrue(!item.checkPassword(null));
		assertContains(item.TYPE.search(item.password.isNull()));
		assertContains(item, item.TYPE.search(item.password.isNotNull()));

		item.setPassword("");
		assertEquals(EMPTY_HASH, item.getPasswordMD5());
		assertTrue(!item.checkPassword("musso"));
		assertTrue(!item.checkPassword("mussx"));
		assertTrue(item.checkPassword(""));
		assertTrue(!item.checkPassword(null));
		assertContains(item.TYPE.search(item.password.isNull()));
		assertContains(item, item.TYPE.search(item.password.isNotNull()));

		assertEquals("a3cca2b2aa1e3b5b3b5aad99a8529074", item.password.hash(FRANZ));
		assertEquals("7e716d0e702df0505fc72e2b89467910", item.password.hash(FRANK));

		item.setPassword(FRANZ);
		assertEquals("a3cca2b2aa1e3b5b3b5aad99a8529074", item.getPasswordMD5());
		assertTrue(item.checkPassword(FRANZ));
		assertTrue(!item.checkPassword(FRANK));
		assertTrue(!item.checkPassword(""));
		assertTrue(!item.checkPassword(null));
		assertContains(item.TYPE.search(item.password.isNull()));
		assertContains(item, item.TYPE.search(item.password.isNotNull()));

		item.setPassword(FRANK);
		assertEquals("7e716d0e702df0505fc72e2b89467910", item.getPasswordMD5());
		assertTrue(!item.checkPassword(FRANZ));
		assertTrue(item.checkPassword(FRANK));
		assertTrue(!item.checkPassword(""));
		assertTrue(!item.checkPassword(null));
		assertContains(item.TYPE.search(item.password.isNull()));
		assertContains(item, item.TYPE.search(item.password.isNotNull()));

		// duplicated in  MessageDigestAlgorithmTest
		item.setPassword("");
		assertEquals(EMPTY_HASH, item.getPasswordMD5());
		assertTrue(!item.checkPassword(FRANZ));
		assertTrue(!item.checkPassword(FRANK));
		assertTrue(item.checkPassword(""));
		assertTrue(!item.checkPassword(null));
		assertContains(item.TYPE.search(item.password.isNull()));
		assertContains(item, item.TYPE.search(item.password.isNotNull()));
	}
}
