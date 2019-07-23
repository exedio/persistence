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

package com.exedio.cope;

import static com.exedio.cope.tojunit.Assert.assertFails;
import static com.exedio.cope.tojunit.Assert.assertWithin;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.exedio.cope.Model.NotConnectedException;
import com.exedio.cope.vaultmock.VaultMockService;
import java.util.Date;
import org.junit.jupiter.api.Test;

public class ConnectTest extends TestWithEnvironment
{
	private static final Date beforeModel = new Date();
	private static final Model MODEL = new Model(ConnectItem.TYPE, ConnectItem2.TYPE);
	private static final Date afterModel = new Date();

	public ConnectTest()
	{
		super(MODEL);
	}

	@Test void testConnect()
	{
		final ConnectProperties defaultProps = copeRule.getConnectProperties();
		// test duplicate call of connect
		assertTrue(model.isConnected());
		final Date connectDate = model.getConnectDate();
		assertNotNull(connectDate);
		assertEquals(connectDate.toInstant(), model.getConnectInstant());
		try
		{
			model.connect(defaultProps);
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("model already been connected", e.getMessage());
		}
		try
		{
			model.connect(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("properties", e.getMessage());
		}
		assertTrue(model.isConnected());
		assertNotSame(connectDate, model.getConnectDate());
		assertEquals(connectDate.toInstant(), model.getConnectInstant());
	}

	@Test void testModel()
	{
		try
		{
			new Model((Type[])null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("explicitTypes", e.getMessage());
		}
		try
		{
			new Model(new Type<?>[]{});
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("explicitTypes must not be empty", e.getMessage());
		}
	}

	@Test void testDisconnect()
	{
		assertWithin(beforeModel, afterModel, model.getInitializeDate());
		assertEquals(model.getInitializeDate().toInstant(), model.getInitializeInstant());

		model.commit();
		final ConnectProperties p = model.getConnectProperties();
		assertNotNull(p);
		final VaultMockService vault = (VaultMockService)model.connect().vault;
		if(vault!=null)
			assertFalse(vault.isClosed());

		model.disconnect();
		assertFalse(model.isConnected());
		try
		{
			model.getConnectProperties();
			fail();
		}
		catch(final NotConnectedException e)
		{
			assertEquals(model, e.getModel());
			assertEquals("model not connected, use Model#connect for " + model, e.getMessage());
		}
		assertEquals(null, model.getConnectDate());
		assertFails(
				model::getConnectInstant,
				NotConnectedException.class,
				"model not connected, use Model#connect for " + model);
		if(vault!=null)
			assertTrue(vault.isClosed());

		try
		{
			model.disconnect();
			fail();
		}
		catch(final NotConnectedException e)
		{
			assertEquals(model, e.getModel());
			assertEquals("model not connected, use Model#connect for " + model, e.getMessage());
		}
		assertFalse(model.isConnected());
		assertEquals(null, model.getConnectDate());
		assertFails(
				model::getConnectInstant,
				NotConnectedException.class,
				"model not connected, use Model#connect for " + model);
		if(vault!=null)
			assertTrue(vault.isClosed());

		final Date before = new Date();
		model.connect(p);
		final Date after = new Date();
		assertTrue(model.isConnected());
		assertSame(p, model.getConnectProperties());
		assertWithin(before, after, model.getConnectDate());
		assertEquals(model.getConnectDate().toInstant(), model.getConnectInstant());
		if(vault!=null)
		{
			assertTrue(vault.isClosed());
			final VaultMockService vaultNew = (VaultMockService)model.connect().vault;
			assertFalse(vaultNew.isClosed());
			assertNotSame(vault, vaultNew);
		}
		model.startTransaction("ModelTest.testDisconnect");
	}
}
