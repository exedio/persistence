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

package com.exedio.cope.misc;

import static com.exedio.cope.misc.ConnectToken.getProperties;
import static com.exedio.cope.misc.ConnectToken.getTokens;
import static com.exedio.cope.misc.ConnectToken.issue;
import static com.exedio.cope.misc.ConnectToken.issueIfConnected;
import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static com.exedio.cope.tojunit.Assert.assertWithin;
import static com.exedio.cope.tojunit.Assert.list;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.ConnectProperties;
import com.exedio.cope.Model;
import com.exedio.cope.tojunit.ConnectTokenRule;
import com.exedio.cope.tojunit.LogRule;
import com.exedio.cope.tojunit.MainRule;
import com.exedio.cope.tojunit.TestSources;
import java.util.Date;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@MainRule.Tag
public class ConnectTokenTest
{
	private static final Model model = new Model(ConnectTokenItem.TYPE);
	private static final ConnectProperties props = ConnectProperties.create(TestSources.minimal());

	static
	{
		model.enableSerialization(ConnectTokenTest.class, "model");
	}

	private final LogRule log = new LogRule(ConnectToken.class);

	private final ConnectTokenRule ctr = new ConnectTokenRule(model);

	@BeforeEach final void setUp()
	{
		ctr.set(props);
	}

	@Test void testIt()
	{
		assertSame(props, getProperties(model));
		assertNotConnected();

		assertEquals(null, issueIfConnected(model, "isNull"));
		assertNotConnected();

		try
		{
			ctr.set((ConnectProperties)null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("properties", e.getMessage());
		}
		assertNotConnected();

		try
		{
			ctr.set((Supplier<ConnectProperties>)null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("properties", e.getMessage());
		}
		assertNotConnected();

		final Date before0 = new Date();
		final ConnectToken token0 = issue(model, "token0Name");
		final Date after0 = new Date();
		assertTrue(model.isConnected());
		assertSame(props, model.getConnectProperties());
		final Date connectDate = model.getConnectDate();
		assertWithin(before0, after0, connectDate);
		assertEqualsUnmodifiable(list(token0), getTokens(model));
		assertToken(0, before0, after0, "token0Name", false, true, false, token0);
		assertEquals("com.exedio.cope.misc.ConnectTokenTest#model/0(token0Name)", token0.toString());
		log.assertInfo(model + ": connected 0 (token0Name)");

		final Date before1 = new Date();
		final ConnectToken token1 = issue(model,
				"token1Name");
		final Date after1 = new Date();
		assertTrue(model.isConnected());
		assertSame(props, model.getConnectProperties());
		assertEquals(connectDate, model.getConnectDate());
		assertEqualsUnmodifiable(list(token0, token1), getTokens(model));
		assertToken(0, before0, after0, "token0Name", false, true,  false, token0);
		assertToken(1, before1, after1, "token1Name", false, false, false, token1);
		log.assertInfo(model + ": issued 1 (token1Name)");

		final Date before2 = new Date();
		final ConnectToken token2 = issueIfConnected(model, "token2Name");
		final Date after2 = new Date();
		assertTrue(model.isConnected());
		assertSame(props, model.getConnectProperties());
		assertEquals(connectDate, model.getConnectDate());
		assertEqualsUnmodifiable(list(token0, token1, token2), getTokens(model));
		assertToken(0, before0, after0, "token0Name", false, true,  false, token0);
		assertToken(1, before1, after1, "token1Name", false, false, false, token1);
		assertToken(2, before2, after2, "token2Name", true,  false, false, token2);
		log.assertInfo(model + ": issued conditionally 2 (token2Name)");

		assertEquals(false, token0.returnStrictly());
		assertTrue(model.isConnected());
		assertSame(props, model.getConnectProperties());
		assertEquals(connectDate, model.getConnectDate());
		assertEqualsUnmodifiable(list(token1, token2), getTokens(model));
		assertToken(0, before0, after0, "token0Name", false, true,  true,  token0);
		assertToken(1, before1, after1, "token1Name", false, false, false, token1);
		assertToken(2, before2, after2, "token2Name", true,  false, false, token2);
		log.assertInfo(model + ": returned 0 (token0Name)");

		assertEquals(false, token2.returnStrictly());
		assertTrue(model.isConnected());
		assertSame(props, model.getConnectProperties());
		assertEquals(connectDate, model.getConnectDate());
		assertEqualsUnmodifiable(list(token1), getTokens(model));
		assertToken(0, before0, after0, "token0Name", false, true,  true,  token0);
		assertToken(1, before1, after1, "token1Name", false, false, false, token1);
		assertToken(2, before2, after2, "token2Name", true,  false, true,  token2);
		log.assertInfo(model + ": returned 2 (token2Name)");

		assertEquals(true, token1.returnStrictly());
		assertNotConnected();
		assertToken(0, before0, after0, "token0Name", false, true,  true, token0);
		assertToken(1, before1, after1, "token1Name", false, false, true, token1);
		assertToken(2, before2, after2, "token2Name", true,  false, true,  token2);
		log.assertInfo(model + ": disconnected 1 (token1Name)");

		try
		{
			token0.returnStrictly();
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("connect token 0 already returned", e.getMessage());
		}
		assertNotConnected();
		assertToken(0, before0, after0, "token0Name", false, true,  true, token0);
		assertToken(1, before1, after1, "token1Name", false, false, true, token1);
		assertToken(2, before2, after2, "token2Name", true, false, true, token2);
		log.assertEmpty();

		assertEquals(null, issueIfConnected(model, "isNull"));
		assertNotConnected();
		assertToken(0, before0, after0, "token0Name", false, true,  true, token0);
		assertToken(1, before1, after1, "token1Name", false, false, true, token1);
		assertToken(2, before2, after2, "token2Name", true,  false, true, token2);
		log.assertEmpty();
	}

	@Test void testConditionally()
	{
		assertNotConnected();

		assertEquals(null, issueIfConnected(model, "isNull"));
		assertNotConnected();

		final Date before0 = new Date();
		final ConnectToken token0 = issue(model, "token0Name");
		final Date after0 = new Date();
		assertTrue(model.isConnected());
		assertSame(props, model.getConnectProperties());
		final Date connectDate = model.getConnectDate();
		assertWithin(before0, after0, connectDate);
		assertEqualsUnmodifiable(list(token0), getTokens(model));
		assertToken(0, before0, after0, "token0Name", false, true, false, token0);
		assertEquals("com.exedio.cope.misc.ConnectTokenTest#model/0(token0Name)", token0.toString());
		log.assertInfo(model + ": connected 0 (token0Name)");

		final Date before1 = new Date();
		final ConnectToken token1 = issue(model,
				"token1Name");
		final Date after1 = new Date();
		assertTrue(model.isConnected());
		assertSame(props, model.getConnectProperties());
		assertEquals(connectDate, model.getConnectDate());
		assertEqualsUnmodifiable(list(token0, token1), getTokens(model));
		assertToken(0, before0, after0, "token0Name", false, true,  false, token0);
		assertToken(1, before1, after1, "token1Name", false, false, false, token1);
		log.assertInfo(model + ": issued 1 (token1Name)");

		assertEquals(false, token1.returnItConditionally());
		assertTrue(model.isConnected());
		assertToken(0, before0, after0, "token0Name", false, true,  false, token0);
		assertToken(1, before1, after1, "token1Name", false, false, true,  token1);
		log.assertInfo(model + ": returned 1 (token1Name)");

		assertEquals(false, token1.returnItConditionally());
		assertTrue(model.isConnected());
		assertToken(0, before0, after0, "token0Name", false, true,  false, token0);
		assertToken(1, before1, after1, "token1Name", false, false, true,  token1);
		log.assertWarn(model + ": returned 1 excessively (token1Name)");

		assertEquals(true, token0.returnItConditionally());
		assertNotConnected();
		assertToken(0, before0, after0, "token0Name", false, true,  true, token0);
		assertToken(1, before1, after1, "token1Name", false, false, true, token1);
		log.assertInfo(model + ": disconnected 0 (token0Name)");

		assertEquals(false, token0.returnItConditionally());
		assertNotConnected();
		assertToken(0, before0, after0, "token0Name", false, true,  true, token0);
		assertToken(1, before1, after1, "token1Name", false, false, true, token1);
		log.assertWarn(model + ": returned 0 excessively (token0Name)");
	}

	@Test void testReturnOnFailureOfOk()
	{
		assertNotConnected();

		final ConnectToken token = issue(model, "tokenName");
		assertTrue(model.isConnected());
		assertFalse(token.isReturned());

		final Target target = new Target(false);
		assertSame(token, token.returnOnFailureOf(target));
		assertTrue(model.isConnected());
		assertFalse(token.isReturned());
		assertSame(token, target.token());
	}

	@Test void testReturnOnFailureOfFail()
	{
		assertNotConnected();

		final ConnectToken token = issue(model, "tokenName");
		assertTrue(model.isConnected());
		assertFalse(token.isReturned());

		final Target target = new Target(true);
		try
		{
			token.returnOnFailureOf(target);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("Afterwards fail", e.getMessage());
		}
		assertFalse(model.isConnected());
		assertTrue(token.isReturned());
		assertSame(token, target.token());
	}

	@Test void testReturnOnFailureOfNullTarget()
	{
		assertNotConnected();

		//noinspection resource
		final ConnectToken token = issue(model, "tokenName");
		assertTrue(model.isConnected());
		assertFalse(token.isReturned());

		try
		{
			token.returnOnFailureOf(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals(null, e.getMessage());
		}
		assertFalse(model.isConnected());
		assertTrue(token.isReturned());
	}

	@Test void testReturnOnFailureOfReturnedOk()
	{
		assertNotConnected();

		//noinspection resource
		final ConnectToken token = issue(model, "tokenName");
		token.returnStrictly();
		assertFalse(model.isConnected());
		assertTrue(token.isReturned());

		final Target target = new Target(false);
		try
		{
			token.returnOnFailureOf(target);
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("connect token 0 already returned", e.getMessage());
		}
		assertFalse(model.isConnected());
		assertTrue(token.isReturned());
		assertSame(null, target.token());
	}

	@Test void testReturnOnFailureOfReturnedFail()
	{
		assertNotConnected();

		//noinspection resource
		final ConnectToken token = issue(model, "tokenName");
		token.returnStrictly();
		assertFalse(model.isConnected());
		assertTrue(token.isReturned());

		final Target target = new Target(true);
		try
		{
			token.returnOnFailureOf(target);
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("connect token 0 already returned", e.getMessage());
		}
		assertFalse(model.isConnected());
		assertTrue(token.isReturned());
		assertSame(null, target.token());
	}

	private static class Target implements Consumer<ConnectToken>
	{
		private final boolean fail;

		Target(final boolean fail)
		{
			this.fail = fail;
		}

		private ConnectToken token;
		ConnectToken token() { return token; }

		@Override
		public void accept(final ConnectToken token)
		{
			assertNotNull(token);
			assertNull(this.token);
			this.token = token;
			if(fail)
				throw new IllegalArgumentException("Afterwards fail");
		}
	}

	@Test void testAutoCloseable()
	{
		assertNotConnected();

		final ConnectToken tokenSave;
		try(ConnectToken token = issue(model, "tokenName"))
		{
			tokenSave = token;
			assertTrue(model.isConnected());
			assertFalse(token.isReturned());
		}
		assertNotConnected();
		assertTrue(tokenSave.isReturned());
	}

	@Test void testAutoCloseableAlreadyReturned()
	{
		assertNotConnected();

		final ConnectToken tokenSave;
		try(ConnectToken token = issue(model, "tokenName"))
		{
			tokenSave = token;
			assertTrue(model.isConnected());
			assertFalse(token.isReturned());

			token.returnStrictly();
			assertNotConnected();
			assertTrue(tokenSave.isReturned());
		}
		assertNotConnected();
		assertTrue(tokenSave.isReturned());
	}

	@Test void testNullName()
	{
		assertNotConnected();

		final Date before0 = new Date();
		final ConnectToken token0 = issue(model, null);
		final Date after0 = new Date();
		assertTrue(model.isConnected());
		assertSame(props, model.getConnectProperties());
		final Date connectDate = model.getConnectDate();
		assertWithin(before0, after0, connectDate);
		assertEqualsUnmodifiable(list(token0), getTokens(model));
		assertToken(0, before0, after0, null, false, true, false, token0);
		assertEquals("com.exedio.cope.misc.ConnectTokenTest#model/0", token0.toString());
		log.assertInfo(model + ": connected 0 (null)");

		assertEquals(true, token0.returnStrictly());
		assertNotConnected();
		assertToken(0, before0, after0, null, false, true,  true, token0);
		log.assertInfo(model + ": disconnected 0 (null)");
	}

	@Test void testSetDuplicate()
	{
		try
		{
			ctr.set(props);
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals(
					"Properties already set for model " +
					"com.exedio.cope.misc.ConnectTokenTest#model.",
					e.getMessage());
		}
	}

	@Test void testSetDuplicateSupplier()
	{
		try
		{
			ctr.set(() -> props);
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals(
					"Properties already set for model " +
					"com.exedio.cope.misc.ConnectTokenTest#model.",
					e.getMessage());
		}
	}

	private static void assertNotConnected()
	{
		assertFalse(model.isConnected());
		try
		{
			model.getConnectProperties();
			fail();
		}
		catch(final Model.NotConnectedException e)
		{
			assertEquals(model, e.getModel());
		}
		assertNull(model.getConnectDate());
		assertEqualsUnmodifiable(list(), getTokens(model));
	}

	private static void assertToken(
			final int id,
			final Date before,
			final Date after,
			final String name,
			final boolean conditional,
			final boolean didConnect,
			final boolean isReturned,
			final ConnectToken token)
	{
		assertSame(model, token.getModel());
		assertEquals(id, token.getID());
		assertWithin(before, after, token.getIssueInstant());
		assertWithin(before, after, token.getIssueDate());
		assertEquals(name, token.getName());
		assertEquals(conditional, token.wasConditional());
		assertEquals(didConnect, token.didConnect());
		assertEquals(isReturned, token.isReturned());
	}
}
