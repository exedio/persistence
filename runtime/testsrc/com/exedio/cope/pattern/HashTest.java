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

import static com.exedio.cope.pattern.HashItem.TYPE;
import static com.exedio.cope.pattern.HashItem.internal;
import static com.exedio.cope.pattern.HashItem.limited15;
import static com.exedio.cope.pattern.HashItem.with3PinValidator;
import static com.exedio.cope.pattern.HashItem.withCorruptValidator;
import static com.exedio.cope.tojunit.Assert.assertContains;
import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static com.exedio.cope.tojunit.Assert.list;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.Join;
import com.exedio.cope.Model;
import com.exedio.cope.Query;
import com.exedio.cope.SetValue;
import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.misc.Computed;
import com.exedio.cope.pattern.Hash.InvalidPlainTextException;
import com.exedio.cope.testmodel.WrapHash;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class HashTest extends TestWithEnvironment
{
	public static final Model MODEL = new Model(TYPE, HashItemHolder.TYPE);

	public HashTest()
	{
		super(MODEL);
	}

	HashItem item;

	@BeforeEach final void setUp()
	{
		item = new HashItem();
	}

	@Test void testExplicitExternal()
	{
		assertEquals(Arrays.asList(
				TYPE.getThis(),
				internal,
				internal.getStorage(),
				limited15,
				limited15.getStorage(),
				withCorruptValidator,
				withCorruptValidator.getStorage(),
				with3PinValidator,
				with3PinValidator.getStorage()
			), TYPE.getFeatures());
	}

	@Test void testInternal()
	{
		assertEquals(TYPE, internal.getType());
		assertEquals("internal", internal.getName());
		assertEquals("wrap", internal.getAlgorithmID());
		assertEquals(TYPE, internal.getStorage().getType());
		assertEquals("internal-wrap", internal.getStorage().getName());
		assertEquals(internal, internal.getStorage().getPattern());
		assertEqualsUnmodifiable(list(internal.getStorage()), internal.getSourceFeatures());
		assertEquals(false, internal.isInitial());
		assertEquals(false, internal.isFinal());
		assertContains(internal.getInitialExceptions());
		assertTrue(internal.getStorage().isAnnotationPresent(Computed.class));

		assertEquals(null, internal.getStorage().get(item));
		assertTrue(item.checkInternal(null));
		assertFalse(item.checkInternal(""));
		assertFalse(item.checkInternal("zack"));
		assertTrue(item.isInternalNull());

		hashT.assertCount(0);
		item.setInternal("03affe07");
		hashT.assertCount(1);
		assertEquals("[03affe07]", internal.getStorage().get(item));
		assertFalse(item.checkInternal(null));
		checkMatchT.assertCount(0);
		checkMismatchT.assertCount(0);
		assertFalse(item.checkInternal("0"));
		checkMismatchT.assertCount(1);
		assertTrue(item.checkInternal("03affe07"));
		checkMatchT.assertCount(1);
		assertFalse(item.isInternalNull());

		hashT.assertCount(0);
		item.set(SetValue.map(internal, "03affe08"));
		hashT.assertCount(1);
		assertEquals("[03affe08]", internal.getStorage().get(item));
		assertFalse(item.checkInternal(null));
		checkMatchT.assertCount(0);
		checkMismatchT.assertCount(0);
		assertFalse(item.checkInternal("0"));
		checkMismatchT.assertCount(1);
		assertFalse(item.checkInternal("03affe07"));
		checkMismatchT.assertCount(1);
		assertTrue(item.checkInternal("03affe08"));
		checkMatchT.assertCount(1);
		assertFalse(item.isInternalNull());

		hashT.assertCount(0);
		final HashItem item2 = new HashItem(SetValue.map(internal, "03affe09"));
		hashT.assertCount(1);
		assertEquals("[03affe09]", internal.getStorage().get(item2));
		assertFalse(item2.checkInternal(null));
		assertFalse(item2.checkInternal("03affe10"));
		assertTrue(item2.checkInternal("03affe09"));
		assertFalse(item2.isInternalNull());

		final HashItem item3 = TYPE.newItem(SetValue.map(internal, "03affe10"));
		assertEquals("[03affe10]", internal.getStorage().get(item3));
		assertFalse(item3.checkInternal(null));
		assertFalse(item3.checkInternal("03affe09"));
		assertTrue(item3.checkInternal("03affe10"));
		assertFalse(item3.isInternalNull());
	}

	private final FeatureTimerTester hashT          = new FeatureTimerTester(internal, "hash");
	private final FeatureTimerTester checkMatchT    = new FeatureTimerTester(internal, "check", "result", "match");
	private final FeatureTimerTester checkMismatchT = new FeatureTimerTester(internal, "check", "result", "mismatch");

	@Test void testAlgorithmReturnsNull()
	{
		final String RETURN_NULL = "RETURN_NULL";
		assertFalse(item.checkInternal(RETURN_NULL));
		internal.checkPlainText(RETURN_NULL);
		internal.blind(RETURN_NULL);

		assertFails(
				() -> internal.hash(RETURN_NULL),
				NullPointerException.class,
				"wrap");

		assertFails(
				() -> item.setInternal(RETURN_NULL),
				NullPointerException.class,
				"wrap");
		assertEquals(null, internal.getStorage().get(item));

		final SetValue<String> sv = SetValue.map(internal, RETURN_NULL);
		assertFails(
				() -> item.set(sv),
				NullPointerException.class,
				"wrap");
		assertEquals(null, internal.getStorage().get(item));

		assertFails(
				() -> new HashItem(sv),
				NullPointerException.class,
				"wrap");
		assertEquals(Arrays.asList(item), TYPE.search());
	}

	@Test void testLimit()
	{
		final String ok = "012345678901234";
		assertEquals("[" + ok + "]", internal.hash(ok));

		final String tooLong = ok + "x";
		try
		{
			limited15.checkPlainText(tooLong);
			fail();
		}
		catch(final InvalidPlainTextException e)
		{
			assertEquals("plain text length violation, must be no longer than 15, but was 16 for HashItem.limited15", e.getMessage());
			assertEquals(limited15, e.getFeature());
			assertEquals("plain text length violation, must be no longer than 15, but was 16 for HashItem.limited15", e.getMessage(true));
			assertEquals("plain text length violation, must be no longer than 15, but was 16", e.getMessage(false));
			assertEquals(tooLong, e.getPlainText());
			assertEquals(true, e.wasLimit());
			assertEquals(null, e.getItem());
		}
		limited15.checkPlainText(ok);

		try
		{
			limited15.hash(tooLong);
			fail();
		}
		catch(final InvalidPlainTextException e)
		{
			assertEquals("plain text length violation, must be no longer than 15, but was 16 for HashItem.limited15", e.getMessage());
			assertEquals(limited15, e.getFeature());
			assertEquals("plain text length violation, must be no longer than 15, but was 16 for HashItem.limited15", e.getMessage(true));
			assertEquals("plain text length violation, must be no longer than 15, but was 16", e.getMessage(false));
			assertEquals(tooLong, e.getPlainText());
			assertEquals(true, e.wasLimit());
			assertEquals(null, e.getItem());
		}

		item.setLimited15(ok);
		assertEquals(true, item.checkLimited15(ok));

		try
		{
			item.setLimited15(tooLong);
			fail();
		}
		catch(final InvalidPlainTextException e)
		{
			assertEquals("plain text length violation, must be no longer than 15, but was 16 for HashItem.limited15", e.getMessage());
			assertEquals(limited15, e.getFeature());
			assertEquals("plain text length violation, must be no longer than 15, but was 16 for HashItem.limited15", e.getMessage(true));
			assertEquals("plain text length violation, must be no longer than 15, but was 16", e.getMessage(false));
			assertEquals(tooLong, e.getPlainText());
			assertEquals(true, e.wasLimit());
			assertEquals(item, e.getItem());
		}
		assertEquals(true, item.checkLimited15(ok));

		final String tooLongHash = "[" + tooLong + "]";
		item.setLimited15wrap(tooLongHash);
		item.setInternalwrap(tooLongHash);
		assertEquals(false, item.checkLimited15(ok));
		assertEquals(false, item.checkLimited15(tooLong));
		assertEquals(false, item.checkInternal(ok));
		assertEquals(true,  item.checkInternal(tooLong));
	}

	@SuppressWarnings("deprecation") // OK testing deprecated api
	@Test void testConditions()
	{
		final HashItem item2 = new HashItem();
		item2.setInternal("123");
		final HashItemHolder h1 = new HashItemHolder(item);
		final HashItemHolder h2 = new HashItemHolder(item2);

		assertEquals(list(item), TYPE.search(internal.isNull()));
		assertEquals(list(item2), TYPE.search(internal.isNotNull()));

		{
			final Query<HashItemHolder> query = HashItemHolder.TYPE.newQuery();
			final Join join1 = query.join(TYPE, HashItemHolder.hashItem::isTarget);
			query.narrow( internal.getStorage().bind(join1).isNull() );

			final Join join2 = query.join(TYPE, HashItemHolder.hashItem::isTarget);
			query.narrow( internal.isNull(join2) );

			assertEquals( list(h1), query.search() );
		}

		{
			final Query<HashItemHolder> query = HashItemHolder.TYPE.newQuery();
			final Join join1 = query.join(TYPE, HashItemHolder.hashItem::isTarget);
			query.narrow( internal.getStorage().bind(join1).isNotNull() );

			final Join join2 = query.join(TYPE, HashItemHolder.hashItem::isTarget);
			query.narrow( internal.isNotNull(join2) );

			assertEquals( list(h2), query.search() );
		}
	}

	@Test void testValidatorValidate()
	{
		// try null as validator
		final Hash withoutValidator = new Hash(WrapHash.ALGORITHM);
		try
		{
			withoutValidator.validate(null);
			fail();
		}
		catch (final NullPointerException e)
		{
			assertEquals("validator", e.getMessage());
		}

		// use default validator
		final Hash hash = withoutValidator.validate(
			new Hash.DefaultPlainTextValidator());
		assertNull(hash.hash(null));
		assertNotNull(hash.hash(""));
		assertNotNull(hash.hash("sdsidh"));
	}

	/**
	 * Check(..) must not call validator: why? Check(..) compares the password sent over http with the persistent hash
	 * stored in the database. It does never change on the database. In opposite to this, the validator is used to ensure
	 * that a password, when storing it (the hash) to the database, fulfills the expected format and length. Example
	 * 4-digit-pin: numeric, length==4. The validator should only be called when the user changes its password or a
	 * new random password is generated.
	 *
	 * @see Hash#blind(String)
	 * @see Hash#check(com.exedio.cope.Item, String)
	 */
	@Test void testCheckMustNotCallValidator()
	{
		// validator must not be called from check(..)
		withCorruptValidator.check(item, "");
		withCorruptValidator.check(item, "sd232");

		// counter example - where the validator will be called
		try
		{
			withCorruptValidator.hash("sdsadd");
		}
		catch (final IllegalStateException e)
		{
			assertEquals("validate", e.getMessage());
		}
	}

	@Test void testValidatorSingleSetValue()
	{
			// with success
			final HashItem anItem = TYPE.newItem();
			anItem.setWith3PinValidator("452");
			assertEquals("[452]", anItem.getWith3PinValidatorwrap());

			// with invalid input data
			try
			{
				anItem.setWith3PinValidator("4544");
				fail();
			}
			catch (final Hash.InvalidPlainTextException e)
			{
				assertEquals("4544", e.getPlainText());
				assertEquals(false, e.wasLimit());
				assertEquals("Pin greater than 3 digits for HashItem.with3PinValidator", e.getMessage());
				assertEquals(with3PinValidator, e.getFeature());
				assertEquals(anItem, e.getItem());
			}
			assertEquals("[452]", anItem.getWith3PinValidatorwrap()); // <= contains still previous data

			// with corrupt validator
			try
			{
				anItem.setWithCorruptValidator("4544");
				fail();
			}
			catch (final IllegalStateException e)
			{
				assertEquals("validate", e.getMessage());
			}
			assertEquals("[452]", anItem.getWith3PinValidatorwrap()); // <= contains still previous data
	}

	@Test void testHashItemMassSetValuesWithValidatedHash()
	{
		// testing mass set

		// with success
		final HashItem anItem = TYPE.newItem();
		assertNotNull(anItem);
		anItem.set(SetValue.map(with3PinValidator, "123"), SetValue.map(internal, "2"));
		assertEquals("[123]", anItem.getWith3PinValidatorwrap());

		// fails because invalid data
		try
		{
			anItem.set( SetValue.map(with3PinValidator, "1"), SetValue.map(internal, "2") );
			fail();
		}
		catch (final Hash.InvalidPlainTextException e)
		{
			assertEquals("1", e.getPlainText());
			assertEquals(false, e.wasLimit());
			assertEquals("Pin less than 3 digits for HashItem.with3PinValidator", e.getMessage());
			assertEquals(with3PinValidator, e.getFeature());
			assertEquals(anItem, e.getItem());
		}

		// fails because validator throws always an exception
		try
		{
			anItem.set( SetValue.map(withCorruptValidator, "1"), SetValue.map(internal, "2") );
			fail();
		}
		catch (final IllegalStateException e)
		{
			assertEquals("validate", e.getMessage());
		}
	}

	@Test void testHashItemConstructionWithValidatedHashValues()
	{
		// test with a validator which always throws an exception
		try
		{
			withCorruptValidator.checkPlainText("03affe10");
			fail();
		}
		catch(final IllegalStateException ise)
		{
			assertEquals("validate", ise.getMessage());
		}
		try
		{
			TYPE.newItem(SetValue.map(withCorruptValidator, "03affe10"));
			fail();
		}
		catch (final IllegalStateException ise)
		{
			assertEquals("validate", ise.getMessage());
		}

		// testing  with validator that discards the given pin string
		try
		{
			with3PinValidator.checkPlainText("99x");
			fail();
		}
		catch(final Hash.InvalidPlainTextException e)
		{
			assertEquals("Pin is not a number for HashItem.with3PinValidator", e.getMessage());
			assertEquals("99x", e.getPlainText());
			assertEquals(false, e.wasLimit());
			assertEquals(with3PinValidator, e.getFeature());
			assertEquals(null, e.getItem());
		}
		try
		{
			TYPE.newItem(SetValue.map(with3PinValidator, "99x"));
			fail();
		}
		catch (final Hash.InvalidPlainTextException e)
		{
			assertEquals("Pin is not a number for HashItem.with3PinValidator", e.getMessage());
			assertEquals("99x", e.getPlainText());
			assertEquals(false, e.wasLimit());
			assertEquals(with3PinValidator, e.getFeature());
			assertEquals(null, e.getItem());
		}

		// test with validator that accepts the given pin string
		with3PinValidator.checkPlainText("978");
		final SetValue<?> setValue = SetValue.map(with3PinValidator, "978");
		final HashItem anItem = TYPE.newItem(setValue);
		assertEquals("[978]", with3PinValidator.getStorage().get(anItem));
	}
}
