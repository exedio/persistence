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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.exedio.cope.instrument.WrapperIgnore;
import com.exedio.cope.tojunit.TestSources;
import org.junit.After;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ClusterDisabledTest
{
	@Test public void test()
	{
		assertEquals(false, model. isClusterEnabled());
		assertEquals(null,  model.getClusterProperties());
		assertEquals(null,  model.getClusterSenderInfo());
		assertEquals(null,  model.getClusterListenerInfo());

		try
		{
			model.pingClusterNetwork();
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("cluster network not enabled", e.getMessage());
		}

		try
		{
			model.pingClusterNetwork(5);
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("cluster network not enabled", e.getMessage());
		}

		try
		{
			model.pingClusterNetwork(0);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("count must be greater zero, but was 0", e.getMessage());
		}
	}


	@SuppressWarnings("static-method")
	@BeforeEach public final void setUp()
	{
		model.connect(ConnectProperties.create(TestSources.minimal()));
	}

	@SuppressWarnings("static-method")
	@After public final void tearDown()
	{
		if(model.isConnected())
			model.disconnect();
	}

	@WrapperIgnore
	private static final class AnItem extends Item
	{
		private AnItem(final ActivationParameters ap) { super(ap); }
		private static final long serialVersionUID = 1l;
	}

	private static final Model model = new Model(TypesBound.newType(AnItem.class));

	static
	{
		model.enableSerialization(ClusterDisabledTest.class, "model");
	}
}
