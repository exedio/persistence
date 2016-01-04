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
import static com.exedio.cope.misc.ConnectToken.removeProperties;
import static com.exedio.cope.misc.ConnectToken.setProperties;

import com.exedio.cope.ConnectProperties;
import com.exedio.cope.Model;
import com.exedio.cope.UtilTestLogAppender;
import com.exedio.cope.junit.CopeAssert;
import java.io.File;
import java.util.Date;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ConnectTokenTest extends CopeAssert
{
	private static final Model model = new Model(ConnectTokenItem.TYPE);
	private static final ConnectProperties props = new ConnectProperties(new File("runtime/utiltest.properties"));

	static
	{
		model.enableSerialization(ConnectTokenTest.class, "model");
	}

	private static final Logger logger = Logger.getLogger(ConnectToken.class);
	UtilTestLogAppender log = null;

	@Override
	@Before public final void setUp() throws Exception
	{
		super.setUp();
		log = new UtilTestLogAppender();
		logger.addAppender(log);
		setProperties(model, props);
	}

	@Override
	@After public final void tearDown() throws Exception
	{
		removeProperties(model);
		logger.removeAppender(log);
		log = null;
		super.tearDown();
	}

	@Test public void testIt()
	{
		assertSame(props, getProperties(model));
		assertNotConnected();

		assertEquals(null, issueIfConnected(model, "isNull"));
		assertNotConnected();

		try
		{
			setProperties(model, null);
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
		log.assertInfo("" + model + ": connected 0 (token0Name)");

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
		log.assertInfo("" + model + ": issued 1 (token1Name)");

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
		log.assertInfo("" + model + ": issued conditionally 2 (token2Name)");

		assertEquals(false, token0.returnStrictly());
		assertTrue(model.isConnected());
		assertSame(props, model.getConnectProperties());
		assertEquals(connectDate, model.getConnectDate());
		assertEqualsUnmodifiable(list(token1, token2), getTokens(model));
		assertToken(0, before0, after0, "token0Name", false, true,  true,  token0);
		assertToken(1, before1, after1, "token1Name", false, false, false, token1);
		assertToken(2, before2, after2, "token2Name", true,  false, false, token2);
		log.assertInfo("" + model + ": returned 0 (token0Name)");

		assertEquals(false, token2.returnStrictly());
		assertTrue(model.isConnected());
		assertSame(props, model.getConnectProperties());
		assertEquals(connectDate, model.getConnectDate());
		assertEqualsUnmodifiable(list(token1), getTokens(model));
		assertToken(0, before0, after0, "token0Name", false, true,  true,  token0);
		assertToken(1, before1, after1, "token1Name", false, false, false, token1);
		assertToken(2, before2, after2, "token2Name", true,  false, true,  token2);
		log.assertInfo("" + model + ": returned 2 (token2Name)");

		assertEquals(true, token1.returnStrictly());
		assertNotConnected();
		assertToken(0, before0, after0, "token0Name", false, true,  true, token0);
		assertToken(1, before1, after1, "token1Name", false, false, true, token1);
		assertToken(2, before2, after2, "token2Name", true,  false, true,  token2);
		log.assertInfo("" + model + ": disconnected 1 (token1Name)");

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

	@Test public void testConditionally()
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
		log.assertInfo("" + model + ": connected 0 (token0Name)");

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
		log.assertInfo("" + model + ": issued 1 (token1Name)");

		assertEquals(false, token1.returnItConditionally());
		assertTrue(model.isConnected());
		assertToken(0, before0, after0, "token0Name", false, true,  false, token0);
		assertToken(1, before1, after1, "token1Name", false, false, true,  token1);
		log.assertInfo("" + model + ": returned 1 (token1Name)");

		assertEquals(false, token1.returnItConditionally());
		assertTrue(model.isConnected());
		assertToken(0, before0, after0, "token0Name", false, true,  false, token0);
		assertToken(1, before1, after1, "token1Name", false, false, true,  token1);
		log.assertWarn("" + model + ": returned 1 excessively (token1Name)");

		assertEquals(true, token0.returnItConditionally());
		assertNotConnected();
		assertToken(0, before0, after0, "token0Name", false, true,  true, token0);
		assertToken(1, before1, after1, "token1Name", false, false, true, token1);
		log.assertInfo("" + model + ": disconnected 0 (token0Name)");

		assertEquals(false, token0.returnItConditionally());
		assertNotConnected();
		assertToken(0, before0, after0, "token0Name", false, true,  true, token0);
		assertToken(1, before1, after1, "token1Name", false, false, true, token1);
		log.assertWarn("" + model + ": returned 0 excessively (token0Name)");
	}

	@Test public void testNullName()
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
		log.assertInfo("" + model + ": connected 0 (null)");

		assertEquals(true, token0.returnStrictly());
		assertNotConnected();
		assertToken(0, before0, after0, null, false, true,  true, token0);
		log.assertInfo("" + model + ": disconnected 0 (null)");
	}

	@Test public void testSetDuplicate()
	{
		try
		{
			setProperties(model, props);
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
		assertWithin(before, after, token.getIssueDate());
		assertEquals(name, token.getName());
		assertEquals(conditional, token.wasConditional());
		assertEquals(didConnect, token.didConnect());
		assertEquals(isReturned, token.isReturned());
	}
}
