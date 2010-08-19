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
import static com.exedio.cope.util.Properties.getSystemPropertySource;

import java.io.File;
import java.util.Date;
import java.util.Properties;

import com.exedio.cope.ConnectProperties;
import com.exedio.cope.Model;
import com.exedio.cope.junit.CopeAssert;

public class ConnectTokenTest extends CopeAssert
{
	private static final Model model = new Model(ConnectTokenItem.TYPE);

	public void testIt()
	{
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
		assertToken(0, before0, after0, "token0Name", true, false, token0);

		final Date before1 = new Date();
		final ConnectToken token1 = issue(model,
				new ConnectProperties(getSystemPropertySource())/* not the same but equal */,
				"token1Name");
		final Date after1 = new Date();
		assertTrue(model.isConnected());
		assertSame(props, model.getConnectProperties());
		assertEquals(connectDate, model.getConnectDate());
		assertEqualsUnmodifiable(list(token0, token1), getTokens(model));
		assertToken(0, before0, after0, "token0Name", true,  false, token0);
		assertToken(1, before1, after1, "token1Name", false, false, token1);

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
		}

		assertEquals(false, token0.returnIt());
		assertTrue(model.isConnected());
		assertSame(props, model.getConnectProperties());
		assertEquals(connectDate, model.getConnectDate());
		assertEqualsUnmodifiable(list(token1), getTokens(model));
		assertToken(0, before0, after0, "token0Name", true,  true,  token0);
		assertToken(1, before1, after1, "token1Name", false, false, token1);


		assertEquals(true, token1.returnIt());
		assertNotConnected();
		assertToken(0, before0, after0, "token0Name", true,  true, token0);
		assertToken(1, before1, after1, "token1Name", false, true, token1);

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
		assertToken(0, before0, after0, "token0Name", true,  true, token0);
		assertToken(1, before1, after1, "token1Name", false, true, token1);
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
			final boolean didConnect,
			final boolean isReturned,
			final ConnectToken token)
	{
		assertSame(model, token.getModel());
		assertEquals(id, token.getID());
		assertWithin(before, after, token.getIssueDate());
		assertEquals(name, token.getName());
		assertEquals(didConnect, token.didConnect());
		assertEquals(isReturned, token.isReturned());

	}
}
