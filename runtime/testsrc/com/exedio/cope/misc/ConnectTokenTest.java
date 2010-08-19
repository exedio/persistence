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

		final ConnectProperties props = new ConnectProperties(ConnectProperties.getSystemPropertySource());

		final Date before1 = new Date();
		final ConnectToken token1 = ConnectToken.issue(model, props, "token1Name");
		final Date after1 = new Date();
		assertTrue(model.isConnected());
		assertSame(props, model.getConnectProperties());
		final Date connectDate = model.getConnectDate();
		assertWithin(before1, after1, connectDate);
		assertEqualsUnmodifiable(list(token1), ConnectToken.getTokens(model));
		assertToken(0, before1, after1, "token1Name", true, false, token1);

		final Date before2 = new Date();
		final ConnectToken token2 = ConnectToken.issue(model,
				new ConnectProperties(ConnectProperties.getSystemPropertySource())/* not the same but equal */,
				"token2Name");
		final Date after2 = new Date();
		assertTrue(model.isConnected());
		assertSame(props, model.getConnectProperties());
		assertEquals(connectDate, model.getConnectDate());
		assertEqualsUnmodifiable(list(token1, token2), ConnectToken.getTokens(model));
		assertToken(0, before1, after1, "token1Name", true,  false, token1);
		assertToken(1, before2, after2, "token2Name", false, false, token2);

		{
			final File dpf = ConnectProperties.getDefaultPropertyFile();
			final Properties dp = ConnectProperties.loadProperties(dpf);
			dp.setProperty("database.user", "zack");
			final ConnectProperties props2 = new ConnectProperties(dp, "ConnectTokenTestChangedProps", ConnectProperties.getSystemPropertySource());
			try
			{
				ConnectToken.issue(model, props2, "tokenXName");
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
			assertEqualsUnmodifiable(list(token1, token2), ConnectToken.getTokens(model));
		}

		assertEquals(false, token1.returnIt());
		assertTrue(model.isConnected());
		assertSame(props, model.getConnectProperties());
		assertEquals(connectDate, model.getConnectDate());
		assertEqualsUnmodifiable(list(token2), ConnectToken.getTokens(model));
		assertToken(0, before1, after1, "token1Name", true,  true,  token1);
		assertToken(1, before2, after2, "token2Name", false, false, token2);


		assertEquals(true, token2.returnIt());
		assertNotConnected();
		assertToken(0, before1, after1, "token1Name", true,  true, token1);
		assertToken(1, before2, after2, "token2Name", false, true, token2);

		try
		{
			token1.returnIt();
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("connect token 0 already returned", e.getMessage());
		}
		assertNotConnected();
		assertToken(0, before1, after1, "token1Name", true,  true, token1);
		assertToken(1, before2, after2, "token2Name", false, true, token2);
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
		assertEqualsUnmodifiable(list(), ConnectToken.getTokens(model));
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
