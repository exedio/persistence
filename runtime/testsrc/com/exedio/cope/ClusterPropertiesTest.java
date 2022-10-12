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

import static com.exedio.cope.instrument.Visibility.NONE;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static com.exedio.cope.tojunit.TestSources.describe;
import static com.exedio.cope.tojunit.TestSources.single;
import static com.exedio.cope.util.Sources.cascade;
import static java.util.Collections.emptySet;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.tojunit.TestSources;
import com.exedio.cope.util.IllegalPropertiesException;
import com.exedio.cope.util.Properties.Field;
import com.exedio.cope.util.Properties.Source;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
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

		final ConnectProperties props = ConnectProperties.create(s);
		assertEquals(true, props.isClusterEnabled());
		model.connect(props);
		assertEquals(true, model.isClusterEnabled());
		final ClusterProperties p = (ClusterProperties)model.getClusterProperties();
		assertEquals(emptySet(), p.getOrphanedKeys());
		assertEquals(5, p.listenThreads.initial);
		assertEquals(5, p.listenThreads.max);
	}

	@Test void testMulticast()
	{
		final ClusterProperties p = ClusterProperties.factory().create(describe("DESC", cascade(
				single("secret", 1234)
		)));
		final String ADDRESS = "224.0.0.50";
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

	@Test void testCustom()
	{
		final Source s = cascade(
				TestSources.minimal(),
				single("schema.primaryKeyGenerator", PrimaryKeyGenerator.sequence),
				single("cluster", true),
				single("cluster.secret", 1234),
				single("cluster.sendBufferDefault", false),
				single("cluster.sendBuffer", 14888),
				single("cluster.sendTrafficDefault", false),
				single("cluster.sendTraffic", 44),
				single("cluster.listenBufferDefault", false),
				single("cluster.listenBuffer", 15888)
		);

		final ConnectProperties props = ConnectProperties.create(s);
		assertEquals(true, props.isClusterEnabled());
		model.connect(props);
		assertEquals(true, model.isClusterEnabled());
		final ClusterProperties p = (ClusterProperties)model.getClusterProperties();
		assertEquals(emptySet(), p.getOrphanedKeys());
		final ClusterSenderInfo sender = model.getClusterSenderInfo();
		assertEquals(14888, sender.getSendBufferSize());
		assertEquals(44, sender.getTrafficClass());
		final ClusterListenerInfo listener = model.getClusterListenerInfo();
		assertEquals(15888, listener.getReceiveBufferSize());
	}

	@Test void testFailListenThreads()
	{
		final Source s = describe("DESC", cascade(
				single("secret", 1234),
				single("listen.threads.initial", 5),
				single("listen.threads.max", 4)
		));
		assertFails(
				() -> ClusterProperties.factory().create(s),
				IllegalPropertiesException.class,
				"property listen.threads.initial in DESC " +
				"must be less or equal max=4, " +
				"but was 5");
	}

	@Test void testSecretZero()
	{
		final Source s = describe("DESC",
				single("secret", 0)
		);
		assertFails(
				() -> ClusterProperties.factory().create(s),
				IllegalPropertiesException.class,
				"property secret in DESC " +
				"must not be zero");
	}

	@Test void testNodeZero()
	{
		final Source s = describe("DESC", cascade(
				single("secret", 1234),
				single("nodeAuto", false),
				single("node", 0)
		));
		assertFails(
				() -> ClusterProperties.factory().create(s),
				IllegalPropertiesException.class,
				"property node in DESC " +
				"must not be zero");
	}

	@Test void testSendInterfaceEmpty()
	{
		final Source s = describe("DESC", cascade(
				single("secret", 1234),
				single("sendInterface", "")
		));
		assertFails(
				() -> ClusterProperties.factory().create(s),
				IllegalPropertiesException.class,
				"property sendInterface in DESC " +
				"must not be empty");
	}

	@Test void testSendAddressDefault() throws UnknownHostException
	{
		final Source s = describe("DESC", cascade(
				single("secret", 1234)
		));
		final ClusterProperties p = ClusterProperties.factory().create(s);
		assertEquals(InetAddress.getByName("224.0.0.50"), p.send()[0].address);
		assertEquals(14446, p.send()[0].port);
		assertEquals(1, p.send().length);
	}

	@Test void testSendAddressEmpty()
	{
		final Source s = describe("DESC", cascade(
				single("secret", 1234),
				single("sendAddress", "")
		));
		assertFails(
				() -> ClusterProperties.factory().create(s),
				IllegalPropertiesException.class,
				"property sendAddress in DESC " +
				"must not be empty");
	}

	@Test void testSendAddressTrimStart()
	{
		final Source s = describe("DESC", cascade(
				single("secret", 1234),
				single("sendAddress", " 224.0.0.55")
		));
		assertFails(
				() -> ClusterProperties.factory().create(s),
				IllegalPropertiesException.class,
				"property sendAddress in DESC " +
				"must be trimmed, but was ' 224.0.0.55'");
	}

	@Test void testSendAddressTrimEnd()
	{
		final Source s = describe("DESC", cascade(
				single("secret", 1234),
				single("sendAddress", "224.0.0.55 ")
		));
		assertFails(
				() -> ClusterProperties.factory().create(s),
				IllegalPropertiesException.class,
				"property sendAddress in DESC " +
				"must be trimmed, but was '224.0.0.55 '");
	}

	@Test void testSendAddressSet() throws UnknownHostException
	{
		final Source s = describe("DESC", cascade(
				single("secret", 1234),
				single("sendAddress", "224.0.0.55")
		));
		final ClusterProperties p = ClusterProperties.factory().create(s);
		assertEquals(InetAddress.getByName("224.0.0.55"), p.send()[0].address);
		assertEquals(14446, p.send()[0].port);
		assertEquals(1, p.send().length);
	}

	@Test void testSendAddressSetWithPort() throws UnknownHostException
	{
		final Source s = describe("DESC", cascade(
				single("secret", 1234),
				single("sendAddress", "224.0.0.55:14464")
		));
		final ClusterProperties p = ClusterProperties.factory().create(s);
		assertEquals(InetAddress.getByName("224.0.0.55"), p.send()[0].address);
		assertEquals(14464, p.send()[0].port);
		assertEquals(1, p.send().length);
	}

	@Test void testSendAddressWrong()
	{
		final Source s = describe("DESC", cascade(
				single("secret", 1234),
				single("sendAddress", "zack")
		));
		final IllegalPropertiesException e = assertFails(
				() -> ClusterProperties.factory().create(s),
				IllegalPropertiesException.class,
				"property sendAddress in DESC " +
				"must be a valid host name, " +
				"but was 'zack'");
		assertEquals(UnknownHostException.class, e.getCause().getClass());
	}

	@Test void testSendAddressPortEmpty()
	{
		final Source s = describe("DESC", cascade(
				single("secret", 1234),
				single("sendAddress", "224.0.0.55:")
		));
		final IllegalPropertiesException e = assertFails(
				() -> ClusterProperties.factory().create(s),
				IllegalPropertiesException.class,
				"property sendAddress in DESC " +
				"must have an integer between 1 and 65535 as port, " +
				"but was '' at position 10 in '224.0.0.55:'");
		assertEquals(NumberFormatException.class, e.getCause().getClass());
	}

	@Test void testSendAddressPortNoInteger()
	{
		final Source s = describe("DESC", cascade(
				single("secret", 1234),
				single("sendAddress", "224.0.0.55:zack")
		));
		final IllegalPropertiesException e = assertFails(
				() -> ClusterProperties.factory().create(s),
				IllegalPropertiesException.class,
				"property sendAddress in DESC " +
				"must have an integer between 1 and 65535 as port, " +
				"but was 'zack' at position 10 in '224.0.0.55:zack'");
		assertEquals(NumberFormatException.class, e.getCause().getClass());
	}

	@Test void testSendAddressPortMinimumExceeded()
	{
		final Source s = describe("DESC", cascade(
				single("secret", 1234),
				single("sendAddress", "224.0.0.50:0")
		));
		assertFails(
				() -> ClusterProperties.factory().create(s),
				IllegalPropertiesException.class,
				"property sendAddress in DESC " +
				"must have an integer between 1 and 65535 as port, " +
				"but was 0 at position 10 in '224.0.0.50:0'");
	}

	@Test void testSendAddressPortMaximumExceeded()
	{
		final Source s = describe("DESC", cascade(
				single("secret", 1234),
				single("sendAddress", "224.0.0.50:65536")
		));
		assertFails(
				() -> ClusterProperties.factory().create(s),
				IllegalPropertiesException.class,
				"property sendAddress in DESC " +
				"must have an integer between 1 and 65535 as port, " +
				"but was 65536 at position 10 in '224.0.0.50:65536'");
	}

	@Test void testSendAddressPortMinimum() throws UnknownHostException
	{
		final Source s = describe("DESC", cascade(
				single("secret", 1234),
				single("sendAddress", "224.0.0.50:1")
		));
		final ClusterProperties p = ClusterProperties.factory().create(s);
		assertEquals(InetAddress.getByName("224.0.0.50"), p.send()[0].address);
		assertEquals(1, p.send()[0].port);
		assertEquals(1, p.send().length);
	}

	@Test void testSendAddressPortMaximum() throws UnknownHostException
	{
		final Source s = describe("DESC", cascade(
				single("secret", 1234),
				single("sendAddress", "224.0.0.50:65535")
		));
		final ClusterProperties p = ClusterProperties.factory().create(s);
		assertEquals(InetAddress.getByName("224.0.0.50"), p.send()[0].address);
		assertEquals(65535, p.send()[0].port);
		assertEquals(1, p.send().length);
	}

	@Test void testSendAddressMultiple() throws UnknownHostException
	{
		final Source s = describe("DESC", cascade(
				single("secret", 1234),
				single("multicast", false),
				single("sendAddress", "224.0.0.55 224.0.0.56:14464")
		));
		final ClusterProperties p = ClusterProperties.factory().create(s);
		assertEquals(InetAddress.getByName("224.0.0.55"), p.send()[0].address);
		assertEquals(14446, p.send()[0].port);
		assertEquals(InetAddress.getByName("224.0.0.56"), p.send()[1].address);
		assertEquals(14464, p.send()[1].port);
		assertEquals(2, p.send().length);
	}

	@Test void testSendAddressMultipleSpaces() throws UnknownHostException
	{
		final Source s = describe("DESC", cascade(
				single("secret", 1234),
				single("multicast", false),
				single("sendAddress", "224.0.0.55   224.0.0.56:14464")
		));
		final ClusterProperties p = ClusterProperties.factory().create(s);
		assertEquals(InetAddress.getByName("224.0.0.55"), p.send()[0].address);
		assertEquals(14446, p.send()[0].port);
		assertEquals(InetAddress.getByName("224.0.0.56"), p.send()[1].address);
		assertEquals(14464, p.send()[1].port);
		assertEquals(2, p.send().length);
	}

	@Test void testSendAddressMultipleMulticast()
	{
		final Source s = describe("DESC", cascade(
				single("secret", 1234),
				single("sendAddress", "224.0.0.55 224.0.0.56:14464")
		));
		assertFails(
				() -> ClusterProperties.factory().create(s),
				IllegalPropertiesException.class,
				"property sendAddress in DESC must must contain exactly one address for multicast, " +
				"but was [/224.0.0.55:14446, /224.0.0.56:14464]");
	}

	@Test void testListenAddressEmpty()
	{
		final Source s = describe("DESC", cascade(
				single("secret", 1234),
				single("listenAddress", "")
		));
		assertFails(
				() -> ClusterProperties.factory().create(s),
				IllegalPropertiesException.class,
				"property listenAddress in DESC " +
				"must not be empty");
	}

	@Test void testListenInterfaceEmpty() throws SocketException
	{
		final Source s = describe("DESC", cascade(
				single("secret", 1234),
				single("listenInterface", "")
		));

		final StringBuilder interfaces = new StringBuilder();
		for(final Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces(); e.hasMoreElements(); )
			interfaces.
					append(interfaces.length()>0?", ":"(").
					append(e.nextElement().getName());
		interfaces.append(")");

		assertFails(
				() -> ClusterProperties.factory().create(s),
				IllegalPropertiesException.class,
				"property listenInterface in DESC " +
				"must be DEFAULT or one of the network interfaces: " + interfaces + ", " +
				"but was ''");
	}

	@Test void testFailPrimaryKeyGeneratorMemory()
	{
		final Source s = describe("DESC", cascade(
				TestSources.minimal(),
				single("cluster", true),
				single("cluster.secret", 1234)
		));
		assertFails(
				() -> ConnectProperties.create(s),
				IllegalPropertiesException.class,
				"property cluster in DESC not supported together with schema.primaryKeyGenerator=memory");
	}

	@AfterEach final void tearDown()
	{
		if(model.isConnected())
			model.disconnect();
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static final class AType extends Item
	{
		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<AType> TYPE = com.exedio.cope.TypesBound.newType(AType.class,AType::new);

		@com.exedio.cope.instrument.Generated
		private AType(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	private static final Model model = new Model(AType.TYPE);

	static
	{
		model.enableSerialization(ClusterPropertiesTest.class, "model");
	}
}
