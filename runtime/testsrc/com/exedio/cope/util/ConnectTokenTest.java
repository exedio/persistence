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

package com.exedio.cope.util;

import static com.exedio.cope.misc.ConnectToken.removeProperties;
import static com.exedio.cope.misc.ConnectToken.setProperties;
import static com.exedio.cope.util.Properties.SYSTEM_PROPERTY_SOURCE;

import com.exedio.cope.ConnectProperties;
import com.exedio.cope.Model;
import com.exedio.cope.junit.CopeAssert;
import java.util.Date;

public class ConnectTokenTest extends CopeAssert
{
	private static final Model model = new Model(ConnectTokenItem.TYPE);
	private static final ConnectProperties props = new ConnectProperties(SYSTEM_PROPERTY_SOURCE);

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		setProperties(model, props);
	}

	@Override
	protected void tearDown() throws Exception
	{
		removeProperties(model);
		super.tearDown();
	}

	@Deprecated
	public void testIt()
	{
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
		assertEqualsUnmodifiable(list(), ConnectToken.getTokens(model));

		final Date before1 = new Date();
		final ConnectToken token1 = ConnectToken.issue(model, props, "token1Name");
		final Date after1 = new Date();
		assertSame(props, model.getConnectProperties());
		final Date connectDate = model.getConnectDate();
		assertWithin(before1, after1, connectDate);
		assertEqualsUnmodifiable(list(token1), ConnectToken.getTokens(model));
		assertSame(model, token1.getModel());
		assertEquals(0, token1.getID());
		assertWithin(before1, after1, token1.getIssueDate());
		assertEquals("token1Name", token1.getName());
		assertEquals(true, token1.didConnect());
		assertEquals(false, token1.isReturned());

		final Date before2 = new Date();
		final ConnectToken token2 = ConnectToken.issue(model,
				new com.exedio.cope.ConnectProperties(com.exedio.cope.ConnectProperties.getSystemPropertySource())/* not the same but equal */,
				"token2Name");
		final Date after2 = new Date();
		assertSame(props, model.getConnectProperties());
		assertEquals(connectDate, model.getConnectDate());
		assertEqualsUnmodifiable(list(token1, token2), ConnectToken.getTokens(model));
		assertEquals(false, token1.isReturned());
		assertSame(model, token2.getModel());
		assertEquals(1, token2.getID());
		assertWithin(before2, after2, token2.getIssueDate());
		assertEquals("token2Name", token2.getName());
		assertEquals(false, token2.didConnect());
		assertEquals(false, token2.isReturned());

		assertEquals(false, token1.returnIt());
		assertSame(props, model.getConnectProperties());
		assertEquals(connectDate, model.getConnectDate());
		assertEqualsUnmodifiable(list(token2), ConnectToken.getTokens(model));
		assertEquals(true, token1.isReturned());
		assertEquals(false, token2.isReturned());


		assertEquals(true, token2.returnIt());
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
		assertEqualsUnmodifiable(list(), ConnectToken.getTokens(model));
		assertEquals(true, token1.isReturned());
		assertEquals(true, token2.isReturned());

		try
		{
			token1.returnIt();
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("connect token 0 already returned", e.getMessage());
		}
		assertEqualsUnmodifiable(list(), ConnectToken.getTokens(model));
		assertSame(model, token1.getModel());
		assertSame(model, token2.getModel());
		assertEquals(0, token1.getID());
		assertEquals(1, token2.getID());
		assertWithin(before1, after1, token1.getIssueDate());
		assertWithin(before2, after2, token2.getIssueDate());
		assertEquals("token1Name", token1.getName());
		assertEquals("token2Name", token2.getName());
		assertEquals(true,  token1.didConnect());
		assertEquals(false, token2.didConnect());
		assertEquals(true, token1.isReturned());
		assertEquals(true, token2.isReturned());
	}
}
