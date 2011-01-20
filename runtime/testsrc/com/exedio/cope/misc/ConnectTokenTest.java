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

package com.exedio.cope.misc;

import static com.exedio.cope.misc.ConnectToken.getTokens;
import static com.exedio.cope.misc.ConnectToken.issue;
import static com.exedio.cope.misc.ConnectToken.issueIfConnected;
import static com.exedio.cope.util.Properties.getSystemPropertySource;

import java.io.File;
import java.util.Date;
import java.util.Properties;

import com.exedio.cope.ConnectProperties;
import com.exedio.cope.Model;
import com.exedio.cope.TestLogHandler;
import com.exedio.cope.junit.CopeAssert;

public class ConnectTokenTest extends CopeAssert
{
	private static final Model model = new Model(ConnectTokenItem.TYPE);

	static
	{
		model.enableSerialization(ConnectTokenTest.class, "model");
	}

	TestLogHandler log = null;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		log = new TestLogHandler();
		ConnectToken.logger.addHandler(log);
		ConnectToken.logger.setUseParentHandlers(false);
	}

	@Override
	protected void tearDown() throws Exception
	{
		ConnectToken.logger.removeHandler(log);
		log = null;
		ConnectToken.logger.setUseParentHandlers(true);
		super.tearDown();
	}

	public void testIt()
	{
		assertNotConnected();

		assertEquals(null, issueIfConnected(model, "isNull"));
		assertNotConnected();

		final ConnectProperties props = new ConnectProperties(getSystemPropertySource());

		final Date before0 = new Date();
		final ConnectToken token0 = issue(model, props, "token0Name");
		final Date after0 = new Date();
		assertTrue(model.isConnected());
		assertSame(props, model.getConnectProperties());
		final Date connectDate = model.getConnectDate();
		assertWithin(before0, after0, connectDate);
		assertEqualsUnmodifiable(list(token0), getTokens(model));
		assertToken(0, before0, after0, "token0Name", false, true, false, token0);
		assertEquals("com.exedio.cope.misc.ConnectTokenTest#model/0(token0Name)", token0.toString());
		log.assertInfo("ConnectToken " + model.toString() + ": connected 0 (token0Name)");

		final Date before1 = new Date();
		final ConnectToken token1 = issue(model,
				new ConnectProperties(getSystemPropertySource())/* not the same but equal */,
				"token1Name");
		final Date after1 = new Date();
		assertTrue(model.isConnected());
		assertSame(props, model.getConnectProperties());
		assertEquals(connectDate, model.getConnectDate());
		assertEqualsUnmodifiable(list(token0, token1), getTokens(model));
		assertToken(0, before0, after0, "token0Name", false, true,  false, token0);
		assertToken(1, before1, after1, "token1Name", false, false, false, token1);
		log.assertInfo("ConnectToken " + model.toString() + ": issued 1 (token1Name)");

		{
			final File dpf = ConnectProperties.getDefaultPropertyFile();
			final Properties dp = ConnectProperties.loadProperties(dpf);
			dp.setProperty("database.user", "zack");
			final ConnectProperties props2 = new ConnectProperties(dp, "ConnectTokenTestChangedProps", ConnectProperties.getSystemPropertySource());
			try
			{
				issue(model, props2, "tokenXName");
				fail();
			}
			catch(final IllegalArgumentException e)
			{
				assertEquals(
						"inconsistent initialization for database.user between " + props.getSource() +
						" and ConnectTokenTestChangedProps, expected " + props.getDatabaseUser() +
						" but got zack.", e.getMessage());
			}
			assertTrue(model.isConnected());
			assertSame(props, model.getConnectProperties());
			assertEquals(connectDate, model.getConnectDate());
			assertEqualsUnmodifiable(list(token0, token1), getTokens(model));
			log.assertEmpty();
		}

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
		log.assertInfo("ConnectToken " + model.toString() + ": issued conditionally 2 (token2Name)");

		assertEquals(false, token0.returnIt());
		assertTrue(model.isConnected());
		assertSame(props, model.getConnectProperties());
		assertEquals(connectDate, model.getConnectDate());
		assertEqualsUnmodifiable(list(token1, token2), getTokens(model));
		assertToken(0, before0, after0, "token0Name", false, true,  true,  token0);
		assertToken(1, before1, after1, "token1Name", false, false, false, token1);
		assertToken(2, before2, after2, "token2Name", true,  false, false, token2);
		log.assertInfo("ConnectToken " + model.toString() + ": returned 0 (token0Name)");

		assertEquals(false, token2.returnIt());
		assertTrue(model.isConnected());
		assertSame(props, model.getConnectProperties());
		assertEquals(connectDate, model.getConnectDate());
		assertEqualsUnmodifiable(list(token1), getTokens(model));
		assertToken(0, before0, after0, "token0Name", false, true,  true,  token0);
		assertToken(1, before1, after1, "token1Name", false, false, false, token1);
		assertToken(2, before2, after2, "token2Name", true,  false, true,  token2);
		log.assertInfo("ConnectToken " + model.toString() + ": returned 2 (token2Name)");

		assertEquals(true, token1.returnIt());
		assertNotConnected();
		assertToken(0, before0, after0, "token0Name", false, true,  true, token0);
		assertToken(1, before1, after1, "token1Name", false, false, true, token1);
		assertToken(2, before2, after2, "token2Name", true,  false, true,  token2);
		log.assertInfo("ConnectToken " + model.toString() + ": disconnected 1 (token1Name)");

		try
		{
			token0.returnIt();
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

	public void testConditionally()
	{
		assertNotConnected();

		assertEquals(null, issueIfConnected(model, "isNull"));
		assertNotConnected();

		final ConnectProperties props = new ConnectProperties(getSystemPropertySource());

		final Date before0 = new Date();
		final ConnectToken token0 = issue(model, props, "token0Name");
		final Date after0 = new Date();
		assertTrue(model.isConnected());
		assertSame(props, model.getConnectProperties());
		final Date connectDate = model.getConnectDate();
		assertWithin(before0, after0, connectDate);
		assertEqualsUnmodifiable(list(token0), getTokens(model));
		assertToken(0, before0, after0, "token0Name", false, true, false, token0);
		assertEquals("com.exedio.cope.misc.ConnectTokenTest#model/0(token0Name)", token0.toString());
		log.assertInfo("ConnectToken " + model.toString() + ": connected 0 (token0Name)");

		final Date before1 = new Date();
		final ConnectToken token1 = issue(model,
				new ConnectProperties(getSystemPropertySource())/* not the same but equal */,
				"token1Name");
		final Date after1 = new Date();
		assertTrue(model.isConnected());
		assertSame(props, model.getConnectProperties());
		assertEquals(connectDate, model.getConnectDate());
		assertEqualsUnmodifiable(list(token0, token1), getTokens(model));
		assertToken(0, before0, after0, "token0Name", false, true,  false, token0);
		assertToken(1, before1, after1, "token1Name", false, false, false, token1);
		log.assertInfo("ConnectToken " + model.toString() + ": issued 1 (token1Name)");

		assertEquals(false, token1.returnItConditionally());
		assertTrue(model.isConnected());
		assertToken(0, before0, after0, "token0Name", false, true,  false, token0);
		assertToken(1, before1, after1, "token1Name", false, false, true,  token1);
		log.assertInfo("ConnectToken " + model.toString() + ": returned 1 (token1Name)");

		assertEquals(false, token1.returnItConditionally());
		assertTrue(model.isConnected());
		assertToken(0, before0, after0, "token0Name", false, true,  false, token0);
		assertToken(1, before1, after1, "token1Name", false, false, true,  token1);
		log.assertEmpty();

		assertEquals(true, token0.returnItConditionally());
		assertNotConnected();
		assertToken(0, before0, after0, "token0Name", false, true,  true, token0);
		assertToken(1, before1, after1, "token1Name", false, false, true, token1);
		log.assertInfo("ConnectToken " + model.toString() + ": disconnected 0 (token0Name)");

		assertEquals(false, token0.returnItConditionally());
		assertNotConnected();
		assertToken(0, before0, after0, "token0Name", false, true,  true, token0);
		assertToken(1, before1, after1, "token1Name", false, false, true, token1);
		log.assertEmpty();
	}

	public void testNullName()
	{
		assertNotConnected();

		final ConnectProperties props = new ConnectProperties(getSystemPropertySource());
		final Date before0 = new Date();
		final ConnectToken token0 = issue(model, props, null);
		final Date after0 = new Date();
		assertTrue(model.isConnected());
		assertSame(props, model.getConnectProperties());
		final Date connectDate = model.getConnectDate();
		assertWithin(before0, after0, connectDate);
		assertEqualsUnmodifiable(list(token0), getTokens(model));
		assertToken(0, before0, after0, null, false, true, false, token0);
		assertEquals("com.exedio.cope.misc.ConnectTokenTest#model/0", token0.toString());
		log.assertInfo("ConnectToken " + model.toString() + ": connected 0 (null)");

		assertEquals(true, token0.returnIt());
		assertNotConnected();
		assertToken(0, before0, after0, null, false, true,  true, token0);
		log.assertInfo("ConnectToken " + model.toString() + ": disconnected 0 (null)");
	}

	private void assertNotConnected()
	{
		assertFalse(model.isConnected());
		try
		{
			model.getConnectProperties();
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("model not yet connected, use Model#connect", e.getMessage());
		}
		assertNull(model.getConnectDate());
		assertEqualsUnmodifiable(list(), getTokens(model));
	}

	private void assertToken(
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
