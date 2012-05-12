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

package com.exedio.cope;

import java.util.Date;

public class ConnectTest extends AbstractRuntimeTest
{
	private static final Date beforeModel = new Date();
	private static final Model MODEL = new Model(ConnectItem.TYPE, ConnectItem2.TYPE);
	private static final Date afterModel = new Date();

	public ConnectTest()
	{
		super(MODEL);
	}

	public void testSupportsReadCommitted()
	{
		assertEquals( true, model.hasCurrentTransaction() );
		assertEquals(dialect.supportsReadCommitted, model.supportsReadCommitted());
	}

	public static final void assertEquals(final String expected, final String actual)
	{
		assertEquals("-----"+expected+"-----"+actual+"-----", expected, actual);
	}

	public void testConnect()
	{
		final ConnectProperties defaultProps = getConnectProperties();
		// test duplicate call of connect
		assertTrue(model.isConnected());
		final Date connectDate = model.getConnectDate();
		assertNotNull(connectDate);
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
	}

	public void testModel()
	{
		try
		{
			new Model((Type[])null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("types", e.getMessage());
		}
		try
		{
			new Model(new Type[]{});
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("types must not be empty", e.getMessage());
		}
	}

	public void testDisconnect()
	{
		assertWithin(beforeModel, afterModel, model.getInitializeDate());

		model.commit();
		final ConnectProperties p = model.getConnectProperties();
		assertNotNull(p);

		model.disconnect();
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
		assertEquals(null, model.getConnectDate());

		try
		{
			model.disconnect();
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("model not yet connected, use Model#connect", e.getMessage());
		}
		assertFalse(model.isConnected());
		assertEquals(null, model.getConnectDate());

		final Date before = new Date();
		model.connect(p);
		final Date after = new Date();
		assertTrue(model.isConnected());
		assertSame(p, model.getConnectProperties());
		assertWithin(before, after, model.getConnectDate());
		model.startTransaction("ModelTest.testDisconnect");
	}
}
