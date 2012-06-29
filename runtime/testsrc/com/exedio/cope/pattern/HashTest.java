/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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

import com.exedio.cope.*;
import com.exedio.cope.misc.Computed;

import java.security.SecureRandom;
import java.util.Arrays;

public class HashTest extends AbstractRuntimeTest
{
	public static final Model MODEL = new Model(HashItem.TYPE, HashItemHolder.TYPE);

	public HashTest()
	{
		super(MODEL);
	}

	HashItem item;

	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		item = deleteOnTearDown(new HashItem());
	}

	public void testExplicitExternal()
	{
		assertEquals(Arrays.asList(
				item.TYPE.getThis(),
				item.explicitExternalWrap,
				item.explicitExternal,
				item.implicitExternal,
				item.implicitExternal.getStorage(),
				item.internal,
				item.internal.getStorage()
			), item.TYPE.getFeatures());

		assertEquals(item.TYPE, item.explicitExternal.getType());
		assertEquals("explicitExternal", item.explicitExternal.getName());
		assertEquals("wrap", item.explicitExternal.getAlgorithmName());
		assertEquals(item.explicitExternalWrap, item.explicitExternal.getStorage());
		assertEquals(item.explicitExternal, item.explicitExternalWrap.getPattern());
		assertEqualsUnmodifiable(list(item.explicitExternalWrap), item.explicitExternal.getSourceFeatures());
		assertEquals(false, item.explicitExternalWrap.isInitial());
		assertEquals(false, item.explicitExternalWrap.isFinal());
		assertContains(StringLengthViolationException.class, item.explicitExternalWrap.getInitialExceptions());
		assertFalse(item.explicitExternal.getStorage().isAnnotationPresent(Computed.class));

		assertNull(item.getExplicitExternalWrap());
		assertTrue(item.checkExplicitExternal(null));
		assertTrue(!item.checkExplicitExternal("bing"));
		assertContains(item, item.TYPE.search(item.explicitExternal.isNull()));
		assertContains(item.TYPE.search(item.explicitExternal.isNotNull()));

		item.setExplicitExternalWrap("03affe01");
		assertEquals("03affe01", item.getExplicitExternalWrap());
		assertTrue(!item.checkExplicitExternal(null));
		assertTrue(!item.checkExplicitExternal("03affe01"));
		assertContains(item.TYPE.search(item.explicitExternal.isNull()));
		assertContains(item, item.TYPE.search(item.explicitExternal.isNotNull()));

		item.setExplicitExternal("03affe02");
		assertEquals("3403affe0243", item.getExplicitExternalWrap());
		assertTrue(!item.checkExplicitExternal(null));
		assertTrue(!item.checkExplicitExternal("03affe01"));
		assertTrue(item.checkExplicitExternal("03affe02"));
		assertContains(item.TYPE.search(item.explicitExternal.isNull()));
		assertContains(item, item.TYPE.search(item.explicitExternal.isNotNull()));
	}

	public void testImplicitExternal()
	{
		assertEquals(item.TYPE, item.implicitExternal.getType());
		assertEquals("implicitExternal", item.implicitExternal.getName());
		assertEquals("wrap", item.implicitExternal.getAlgorithmName());
		assertEquals(item.TYPE, item.implicitExternal.getStorage().getType());
		assertEquals("implicitExternal-wrap", item.implicitExternal.getStorage().getName());
		assertEquals(item.implicitExternal, item.implicitExternal.getStorage().getPattern());
		assertEqualsUnmodifiable(list(item.implicitExternal.getStorage()), item.implicitExternal.getSourceFeatures());
		assertEquals(false, item.implicitExternal.isInitial());
		assertEquals(false, item.implicitExternal.isFinal());
		assertContains(item.implicitExternal.getInitialExceptions());
		assertTrue(item.implicitExternal.getStorage().isAnnotationPresent(Computed.class));

		assertEquals(null, item.get(item.implicitExternal.getStorage()));
		assertTrue(item.checkImplicitExternal(null));
		assertFalse(item.checkImplicitExternal(""));
		assertFalse(item.checkImplicitExternal("zack"));

		item.setImplicitExternal("03affe05");
		assertEquals("3403affe0543", item.get(item.implicitExternal.getStorage()));
		assertFalse(item.checkImplicitExternal(null));
		assertFalse(item.checkImplicitExternal("0"));
		assertTrue(item.checkImplicitExternal("03affe05"));
	}

	public void testInternal()
	{
		assertEquals(item.TYPE, item.internal.getType());
		assertEquals("internal", item.internal.getName());
		assertEquals("wrap", item.internal.getAlgorithmName());
		assertEquals(item.TYPE, item.internal.getStorage().getType());
		assertEquals("internal-wrap", item.internal.getStorage().getName());
		assertEquals(item.internal, item.internal.getStorage().getPattern());
		assertEqualsUnmodifiable(list(item.internal.getStorage()), item.internal.getSourceFeatures());
		assertEquals(false, item.internal.isInitial());
		assertEquals(false, item.internal.isFinal());
		assertContains(item.internal.getInitialExceptions());
		assertTrue(item.internal.getStorage().isAnnotationPresent(Computed.class));

		assertEquals(null, item.get(item.internal.getStorage()));
		assertTrue(item.checkInternal(null));
		assertFalse(item.checkInternal(""));
		assertFalse(item.checkInternal("zack"));

		item.setInternal("03affe07");
		assertEquals("3403affe0743", item.get(item.internal.getStorage()));
		assertFalse(item.checkInternal(null));
		assertFalse(item.checkInternal("0"));
		assertTrue(item.checkInternal("03affe07"));

		item.set(item.internal.map("03affe08"));
		assertEquals("3403affe0843", item.get(item.internal.getStorage()));
		assertFalse(item.checkInternal(null));
		assertFalse(item.checkInternal("0"));
		assertFalse(item.checkInternal("03affe07"));
		assertTrue(item.checkInternal("03affe08"));

		final HashItem item2 = deleteOnTearDown(new HashItem(new SetValue[]{item.internal.map("03affe09")}));
		assertEquals("3403affe0943", item2.get(item2.internal.getStorage()));
		assertFalse(item2.checkInternal(null));
		assertFalse(item2.checkInternal("03affe10"));
		assertTrue(item2.checkInternal("03affe09"));

		final HashItem item3 = deleteOnTearDown(HashItem.TYPE.newItem(item.internal.map("03affe10")));
		assertEquals("3403affe1043", item3.get(item3.internal.getStorage()));
		assertFalse(item3.checkInternal(null));
		assertFalse(item3.checkInternal("03affe09"));
		assertTrue(item3.checkInternal("03affe10"));
	}

	public void testConditions()
	{
		HashItem item2 = deleteOnTearDown(new HashItem());
		item2.setImplicitExternal("123");
		HashItemHolder h1 = deleteOnTearDown(new HashItemHolder(item));
		HashItemHolder h2 = deleteOnTearDown(new HashItemHolder(item2));

		assertEquals(list(item), HashItem.TYPE.search(HashItem.implicitExternal.isNull()));
		assertEquals(list(item2), HashItem.TYPE.search(HashItem.implicitExternal.isNotNull()));

		{
			Query<HashItemHolder> query = HashItemHolder.TYPE.newQuery();
			Join join1 = query.join(HashItem.TYPE);
			join1.setCondition(HashItemHolder.hashItem.equalTarget(join1) );
			query.narrow( HashItem.implicitExternal.getStorage().bind(join1).isNull() );

			Join join2 = query.join(HashItem.TYPE);
			join2.setCondition(HashItemHolder.hashItem.equalTarget(join2) );
			query.narrow( HashItem.implicitExternal.isNull(join2) );

			assertEquals( list(h1), query.search() );
		}

		{
			Query<HashItemHolder> query = HashItemHolder.TYPE.newQuery();
			Join join1 = query.join(HashItem.TYPE);
			join1.setCondition(HashItemHolder.hashItem.equalTarget(join1) );
			query.narrow( HashItem.implicitExternal.getStorage().bind(join1).isNotNull() );

			Join join2 = query.join(HashItem.TYPE);
			join2.setCondition(HashItemHolder.hashItem.equalTarget(join2) );
			query.narrow( HashItem.implicitExternal.isNotNull(join2) );

			assertEquals( list(h2), query.search() );
		}
	}

	/** @see MessageDigestHashTest#testValidator() */
	public void testValidatorValidate()
	{
		// try null as validator
		try
		{
			new Hash(new MessageDigestAlgorithm("SHA-512", 0, 1), (Hash.PlainTextValidator)null);
			fail();
		}
		catch (NullPointerException e)
		{
			assertEquals("validator", e.getMessage());
		}

		// use default validator
		Hash hash = new Hash(new MessageDigestAlgorithm("SHA-512", 0, 1), new Hash.DefaultPlainTextValidator());
		assertNull(hash.hash(null));
		assertNotNull(hash.hash(""));
		assertNotNull(hash.hash("sdsidh"));

		// use digit pin validator
		hash = new Hash(new MessageDigestAlgorithm("SHA-512", 0, 1), new Hash.DigitPinValidator(/*pin len */3));
		assertNotNull(hash.hash("123"));

		// test error handling
		try
		{
			hash.hash("12");
			fail();
		}
		catch (Hash.InvalidPlainTextException e)
		{
			assertEquals("Pin less than 3 digits", e.getMessage());
		}
		try
		{
			hash.hash("1234");
			fail();
		}
		catch (Hash.InvalidPlainTextException e)
		{
			assertEquals("Pin greater than 3 digits", e.getMessage());
		}
		try
		{
			hash.hash("12a");
			fail();
		}
		catch (Hash.InvalidPlainTextException e)
		{
			assertEquals("Pin '12a' is not a number", e.getMessage());
		}
	}

	public void testValidatorNewRandomPlainText()
	{
		SecureRandom random = new SecureRandom();
		for (int pinLen = 1; pinLen < 6; pinLen++)
		{
			Hash.DigitPinValidator pinValidator = new Hash.DigitPinValidator(pinLen);
			for (int i=0; i<1000; i++)
			{
				String newPin = pinValidator.newRandomPlainText(random);
				assertEquals(pinLen, newPin.length());
			}
		}

		try
		{
			new Hash.DigitPinValidator(0);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			assertEquals("pinLen must be greater 0", e.getMessage());
		}

		try
		{
			new Hash.DigitPinValidator(24);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			assertEquals("pinLen exceeds limit of max 10", e.getMessage());
		}
	}
}
