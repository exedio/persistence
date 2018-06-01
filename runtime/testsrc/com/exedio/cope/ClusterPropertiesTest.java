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
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.exedio.cope.instrument.WrapperIgnore;
import com.exedio.cope.tojunit.TestSources;
import com.exedio.cope.util.IllegalPropertiesException;
import com.exedio.cope.util.Properties.Field;
import com.exedio.cope.util.Properties.Source;
import java.util.Iterator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

public class ClusterPropertiesTest
{
	@Test void testOk()
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

	@Test void testMulticast()
	{
		final ClusterProperties p = ClusterProperties.factory().create(describe("DESC", cascade(
				single("secret", 1234)
		)));
		final String ADDRESS = "230.0.0.1";
		final int PORT = 14446;
		final Iterator<Field<?>> fields = p.getFields().iterator();
		assertIt("secret", 0, 1234, fields);
		assertIt("nodeAuto", true, fields);
		assertIt("node", 0, fields);
		assertIt("multicast", true, fields);
		assertIt("sendSourcePortAuto", true, fields);
		assertIt("sendSourcePort", 14445, fields);
		assertIt("sendInterface", "DEFAULT", fields);
		assertIt("sendAddress", ADDRESS, fields);
		assertIt("sendDestinationPort", PORT, fields);
		assertIt("sendBufferDefault", true, fields);
		assertIt("sendBuffer", 50000, fields);
		assertIt("sendTrafficDefault", true, fields);
		assertIt("sendTraffic", 0, fields);
		assertIt("listenAddress", ADDRESS, fields);
		assertIt("listenPort", PORT, fields);
		assertIt("listenInterface", "DEFAULT", fields);
		assertIt("listenDisableLoopback", false, fields);
		assertIt("listenBufferDefault", true, fields);
		assertIt("listenBuffer", 50000, fields);
		assertIt("listen.threads.initial", 1, fields);
		assertIt("listen.threads.max", 10, fields);
		assertIt("listen.threads.priority.set", false, fields);
		assertIt("listen.threads.priority.value", Thread.MAX_PRIORITY, fields);
		assertIt("listenSequenceCheckerCapacity", 200, fields);
		assertIt("packetSize", 1400, fields);
		assertFalse(fields.hasNext());
	}

	@Test void testSinglecast()
	{
		// https://tools.ietf.org/html/rfc5737
		// The blocks 192.0.2.0/24 (TEST-NET-1), 198.51.100.0/24 (TEST-NET-2),
		// and 203.0.113.0/24 (TEST-NET-3) are provided for use in
		// documentation.
		final String ADDRESS = "192.0.2.88";

		final ClusterProperties p = ClusterProperties.factory().create(describe("DESC", cascade(
				single("secret", 1234),
				single("multicast", false),
				single("sendAddress", ADDRESS)
		)));
		final int PORT = 14446;
		final Iterator<Field<?>> fields = p.getFields().iterator();
		assertIt("secret", 0, 1234, fields);
		assertIt("nodeAuto", true, fields);
		assertIt("node", 0, fields);
		assertIt("multicast", true, false, fields);
		assertIt("sendSourcePortAuto", true, fields);
		assertIt("sendSourcePort", 14445, fields);
		assertIt("sendInterface", "DEFAULT", fields);
		assertIt("sendAddress", null, ADDRESS, fields);
		assertIt("sendDestinationPort", PORT, fields);
		assertIt("sendBufferDefault", true, fields);
		assertIt("sendBuffer", 50000, fields);
		assertIt("sendTrafficDefault", true, fields);
		assertIt("sendTraffic", 0, fields);
		assertIt("listenPort", PORT, fields);
		assertIt("listenBufferDefault", true, fields);
		assertIt("listenBuffer", 50000, fields);
		assertIt("listen.threads.initial", 1, fields);
		assertIt("listen.threads.max", 10, fields);
		assertIt("listen.threads.priority.set", false, fields);
		assertIt("listen.threads.priority.value", Thread.MAX_PRIORITY, fields);
		assertIt("listenSequenceCheckerCapacity", 200, fields);
		assertIt("packetSize", 1400, fields);
		assertFalse(fields.hasNext());
	}

	private static void assertIt(
			final String expectedKey,
			final Object expectedValue,
			final Iterator<Field<?>> actualIterator)
	{
		assertIt(expectedKey, expectedValue, expectedValue, actualIterator);
	}

	private static void assertIt(
			final String expectedKey,
			final Object expectedDefault,
			final Object expectedValue,
			final Iterator<Field<?>> actualIterator)
	{
		final Field<?> actual = actualIterator.next();
		assertAll(
				() -> assertEquals(expectedKey,     actual.getKey(), "key"),
				() -> assertEquals(expectedDefault, actual.getDefaultValue(), expectedKey + " default"),
				() -> assertEquals(expectedValue,   actual.getValue(),        expectedKey + " value"));
	}

	@Test void testFailListenThreads()
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

	@Test void testSecretZero()
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

	@Test void testNodeZero()
	{
		final Source s = describe("DESC", cascade(
				single("secret", 1234),
				single("nodeAuto", false),
				single("node", 0)
		));
		try
		{
			ClusterProperties.factory().create(s);
			fail();
		}
		catch(final IllegalPropertiesException e)
		{
			assertEquals(
					"property node in DESC " +
					"must not be zero",
					e.getMessage());
		}
	}

	@Test void testFailPrimaryKeyGeneratorMemory()
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
	@AfterEach final void tearDown()
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
