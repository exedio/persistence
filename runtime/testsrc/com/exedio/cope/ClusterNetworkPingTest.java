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

import static com.exedio.cope.tojunit.Assert.sleepLongerThan;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.exedio.cope.util.SequenceChecker;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;
import org.junit.jupiter.api.Test;

public class ClusterNetworkPingTest extends ClusterNetworkTest
{
	private String localhost;
	private boolean fromMyself = false;
	private double fromMyselfBeforeA;
	private double fromMyselfBeforeB;

	@Test void testMulticast() throws InterruptedException, SocketException
	{
		for(final Enumeration<NetworkInterface> interfaceI = NetworkInterface.getNetworkInterfaces(); interfaceI.hasMoreElements(); )
		{
			final NetworkInterface networkInterface = interfaceI.nextElement();
			if("lo".equals(networkInterface.getName()))
				continue;

			for(final Enumeration<InetAddress> addressI = networkInterface.getInetAddresses(); addressI.hasMoreElements(); )
			{
				final InetAddress address = addressI.nextElement();
				if(!(address instanceof Inet4Address))
					continue;
				if(address.isLinkLocalAddress()) // network interface "idrac" on jenkins slave
					continue;

				localhost = address.toString();
			}
		}
		assertFalse(modelA.isConnected());
		assertFalse(modelB.isConnected());

		// when running this test alone, it fails on Windows if modelA is connected before modelB
		modelB.connect(getPropertiesMulticast());
		modelA.connect(getPropertiesMulticast());

		assertEquals("Connect Properties Source (multicast)", modelA.getConnectProperties().getSource());
		assertEquals("Connect Properties Source (multicast)", modelB.getConnectProperties().getSource());

		fromMyself = true;
		test();
	}

	@Test void testSinglecast() throws InterruptedException
	{
		localhost = "/127.0.0.1";
		assertFalse(modelA.isConnected());
		assertFalse(modelB.isConnected());

		modelA.connect(getPropertiesSinglecast(Port.B, Port.A));
		modelB.connect(getPropertiesSinglecast(Port.A, Port.B));

		assertEquals("Connect Properties Source (singlecast B<-[A])", modelA.getConnectProperties().getSource());
		assertEquals("Connect Properties Source (singlecast A<-[B])", modelB.getConnectProperties().getSource());

		test();
	}

	private void test() throws InterruptedException
	{
		fromMyselfBeforeA = count("fromMyself", modelA);
		fromMyselfBeforeB = count("fromMyself", modelB);

		assertTrue(modelA.isConnected());
		assertTrue(modelB.isConnected());
		assertNotNull(modelA.getClusterProperties());
		assertNotNull(modelB.getClusterProperties());

		assertIt(0, 0);

		modelA.pingClusterNetwork();
		sleepLongerThan(500);
		assertIt(1, 0);

		modelA.pingClusterNetwork();
		sleepLongerThan(500);
		assertIt(2, 0);

		modelB.pingClusterNetwork();
		sleepLongerThan(500);
		assertIt(2, 1);
	}

	private void assertIt(final int pingA, final int pingB)
	{
		final ClusterSenderInfo senderA = modelA.getClusterSenderInfo();
		final ClusterSenderInfo senderB = modelB.getClusterSenderInfo();
		assertEquals(0, senderA.getTrafficClass());
		assertEquals(0, senderB.getTrafficClass());
		assertEquals(0, senderA.getInvalidationSplit());
		assertEquals(0, senderB.getInvalidationSplit());
		assertEquals(0, count("invalidationSplit", modelA));
		assertEquals(0, count("invalidationSplit", modelB));

		final ClusterListenerInfo listenerA = modelA.getClusterListenerInfo();
		final ClusterListenerInfo listenerB = modelB.getClusterListenerInfo();
		assertIt(pingA+pingB, fromMyselfBeforeA, modelA, listenerA);
		assertIt(pingA+pingB, fromMyselfBeforeB, modelB, listenerB);

		final List<ClusterListenerInfo.Node> nodesA = listenerA.getNodes();
		final List<ClusterListenerInfo.Node> nodesB = listenerB.getNodes();
		if( pingA>0 || pingB>0 )
		{
			assertEquals(1, nodesA.size());
			assertEquals(1, nodesB.size());
			final ClusterListenerInfo.Node nodeA = nodesA.get(0);
			final ClusterListenerInfo.Node nodeB = nodesB.get(0);
			assertEquals(senderA.getNodeID(), nodeB.getID());
			assertEquals(senderB.getNodeID(), nodeA.getID());
			assertEquals(senderA.getNodeIDString(), nodeB.getIDString());
			assertEquals(senderB.getNodeIDString(), nodeA.getIDString());
			assertEquals(senderA.getLocalPort(), nodeB.getPort());
			assertEquals(senderB.getLocalPort(), nodeA.getPort());
			assertLastRoundTripSet(pingA, modelA, nodeA);
			assertLastRoundTripSet(pingB, modelB, nodeB);
			assertIt(pingB, modelB, modelA, nodeA.getPingInfo(), "ping");
			assertIt(pingA, modelA, modelB, nodeB.getPingInfo(), "ping");
			assertIt(pingA, modelB, modelA, nodeA.getPongInfo(), "pong");
			assertIt(pingB, modelA, modelB, nodeB.getPongInfo(), "pong");
			assertIt(0, modelB, modelA, nodeA.getInvalidateInfo(), "invalidate");
			assertIt(0, modelA, modelB, nodeB.getInvalidateInfo(), "invalidate");
		}
		else
		{
			assertEquals(0, nodesA.size());
			assertEquals(0, nodesB.size());
		}
	}

	private void assertIt(
			final int fromMyself,
			final double fromMyselfBefore,
			final Model model,
			final ClusterListenerInfo actual)
	{
		assertEquals(0, actual.getException());
		assertEquals(0, actual.getMissingMagic());
		assertEquals(0, actual.getWrongSecret());
		assertEquals(this.fromMyself ? fromMyself : 0, actual.getFromMyself()     - fromMyselfBefore);
		assertEquals(this.fromMyself ? fromMyself : 0, count("fromMyself", model) - fromMyselfBefore);
	}

	private void assertIt(
			final int inOrder,
			final Model sendModel,
			final Model listenModel,
			final SequenceChecker.Info actual,
			final String kind)
	{
		assertEquals(inOrder, actual.getInOrder());
		assertEquals(0, actual.getOutOfOrder());
		assertEquals(0, actual.getDuplicate());
		assertEquals(0, actual.getLost());
		assertEquals(0, actual.getLate());
		assertEquals(0, actual.getPending());

		assertEquals(inOrder, countSequence("sequence", sendModel, listenModel, kind, "inOrder"));
		assertEquals(0,       countSequence("sequence", sendModel, listenModel, kind, "early"));
		assertEquals(0,       countSequence("sequence", sendModel, listenModel, kind, "outOfOrder"));
		assertEquals(0,       countSequence("sequence", sendModel, listenModel, kind, "duplicate"));
		assertEquals(0,       countSequence("sequence", sendModel, listenModel, kind, "late"));
		assertEquals(0,       countSequence("sequence", sendModel, listenModel, kind, "lost"));
		assertEquals(0,       gaugeSequence("pending",  sendModel, listenModel, kind));
	}

	static void assertLastRoundTripSet(
			final long expected,
			final Model expectedLocalModel,
			final ClusterListenerInfo.Node actual)
	{
		if(expected>0)
		{
			assertNotNull(actual.getLastRoundTrip   ());
			assertNotNull(actual.getMinimumRoundTrip());
			assertNotNull(actual.getMaximumRoundTrip());
			assertNotNull(actual.getLastRoundTrip   ().getDate());
			assertNotNull(actual.getMinimumRoundTrip().getDate());
			assertNotNull(actual.getMaximumRoundTrip().getDate());
			assertGreaterZero(actual.getLastRoundTrip   ().getNanos());
			assertGreaterZero(actual.getMinimumRoundTrip().getNanos());
			assertGreaterZero(actual.getMaximumRoundTrip().getNanos());
			assertLessOrEqual(actual.getMinimumRoundTrip().getNanos(), actual.getLastRoundTrip   ().getNanos());
			assertLessOrEqual(actual.getLastRoundTrip   ().getNanos(), actual.getMaximumRoundTrip().getNanos());
		}
		else
		{
			assertNull(actual.getLastRoundTrip());
			assertNull(actual.getMinimumRoundTrip());
			assertNull(actual.getMaximumRoundTrip());
		}
		assertEquals(expected, ((Timer)PrometheusMeterRegistrar.meter(Cluster.class, "roundTrip",
				Tags.of(
						"model", expectedLocalModel.toString(),
						"id", actual.getIDString(),
						"address", actual.getAddress().toString(),
						"port", "" + actual.getPort()))).count());
	}

	private static void assertGreaterZero(final long actual)
	{
		assertTrue(actual>0, "" + actual);
	}

	private static void assertLessOrEqual(final long a, final long b)
	{
		assertTrue(a<=b, "" + a + "<=" + b);
	}

	static double count(final String nameSuffix, final Model model)
	{
		return ((Counter)PrometheusMeterRegistrar.meter(
				Cluster.class, nameSuffix,
				Tags.of("model", model.toString()))).count();
	}

	private double countSequence(
			final String nameSuffix,
			final Model sendModel,
			final Model listenModel,
			final String kind,
			final String result)
	{
		return ((Counter)meterSequence(nameSuffix, sendModel, listenModel, kind, Tags.of("result", result))).count();
	}

	private double gaugeSequence(
			final String nameSuffix,
			final Model sendModel,
			final Model listenModel,
			final String kind)
	{
		return ((Gauge)meterSequence(nameSuffix, sendModel, listenModel, kind, Tags.empty())).value();
	}

	private Meter meterSequence(
			final String nameSuffix,
			final Model sendModel,
			final Model listenModel,
			final String kind,
			final Tags tags)
	{
		assertNotSame(sendModel, listenModel);

		final ClusterSenderInfo sendInfo = sendModel.getClusterSenderInfo();
		return PrometheusMeterRegistrar.meter(
				Cluster.class, nameSuffix,
				tags.and(
						"model", listenModel.toString(),
						"id", sendInfo.getNodeIDString(),
						"address", localhost,
						"port", "" + sendInfo.getLocalPort(),
						"kind", kind));
	}
}
