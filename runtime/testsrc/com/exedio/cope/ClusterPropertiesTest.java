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

import static com.exedio.cope.tojunit.TestSources.describe;
import static com.exedio.cope.tojunit.TestSources.single;
import static com.exedio.cope.util.Sources.cascade;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.exedio.cope.instrument.WrapperIgnore;
import com.exedio.cope.tojunit.TestSources;
import com.exedio.cope.util.IllegalPropertiesException;
import com.exedio.cope.util.Properties.Source;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

public class ClusterPropertiesTest
{
	@Test public void testOk()
	{
		final Source s = cascade(
				TestSources.minimal(),
				single("schema.primaryKeyGenerator", PrimaryKeyGenerator.sequence),
				single("cluster", true),
				single("cluster.secret", 1234),
				single("cluster.listen.threads.initial", 5),
				single("cluster.listen.threads.max", 5)
		);

		model.connect(ConnectProperties.create(s));
		assertEquals(true, model.isClusterEnabled());
		final ClusterProperties p = (ClusterProperties)model.getClusterProperties();
		assertEquals(5, p.listenThreads.initial);
		assertEquals(5, p.listenThreads.max);
	}

	@Test public void testFailListenThreads()
	{
		final Source s = describe("DESC", cascade(
				single("secret", 1234),
				single("listen.threads.initial", 5),
				single("listen.threads.max", 4)
		));
		try
		{
			ClusterProperties.factory().create(s);
			fail();
		}
		catch(final IllegalPropertiesException e)
		{
			assertEquals(
					"property listen.threads.initial in DESC " +
					"must be less or equal max=4, " +
					"but was 5",
					e.getMessage());
		}
	}

	@Test public void testSecretZero()
	{
		final Source s = describe("DESC",
				single("secret", 0)
		);
		try
		{
			ClusterProperties.factory().create(s);
			fail();
		}
		catch(final IllegalPropertiesException e)
		{
			assertEquals(
					"property secret in DESC " +
					"must not be zero",
					e.getMessage());
		}
	}

	@Test public void testFailPrimaryKeyGeneratorMemory()
	{
		final Source s = describe("DESC", cascade(
				TestSources.minimal(),
				single("cluster", true),
				single("cluster.secret", 1234)
		));
		try
		{
			ConnectProperties.create(s);
			fail();
		}
		catch(final IllegalPropertiesException e)
		{
			assertEquals(
					"property cluster in DESC not supported together with schema.primaryKeyGenerator=memory",
					e.getMessage());
		}
	}

	@SuppressWarnings("static-method")
	@AfterEach public final void tearDown()
	{
		if(model.isConnected())
			model.disconnect();
	}

	@WrapperIgnore
	private static final class AType extends Item
	{
		private AType(final ActivationParameters ap) { super(ap); }
		private static final long serialVersionUID = 1l;
	}

	private static final Model model = new Model(TypesBound.newType(AType.class));

	static
	{
		model.enableSerialization(ClusterPropertiesTest.class, "model");
	}
}
