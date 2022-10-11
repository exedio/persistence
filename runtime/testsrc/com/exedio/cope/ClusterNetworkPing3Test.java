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

import static com.exedio.cope.ClusterNetworkPingTest.assertLastRoundTripSet;
import static com.exedio.cope.ClusterNetworkPingTest.count;
import static com.exedio.cope.instrument.Visibility.NONE;
import static com.exedio.cope.tojunit.Assert.sensitive;
import static com.exedio.cope.tojunit.Assert.sleepLongerThan;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.exedio.cope.ClusterListenerInfo.Node;
import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.util.SequenceChecker;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

public class ClusterNetworkPing3Test extends ClusterNetworkTest
{
	private boolean fromMyself = false;
	private double fromMyselfBeforeA;
	private double fromMyselfBeforeB;
	private double fromMyselfBeforeC;

	@Test void testMulticast() throws InterruptedException
	{
		assertFalse(modelA.isConnected());
		assertFalse(modelB.isConnected());
		assertFalse(modelC.isConnected());

		modelC.connect(getPropertiesMulticast());
		modelB.connect(getPropertiesMulticast());
		modelA.connect(getPropertiesMulticast());

		assertEquals("Connect Properties Source (multicast)", modelA.getConnectProperties().getSource());
		assertEquals("Connect Properties Source (multicast)", modelB.getConnectProperties().getSource());
		assertEquals("Connect Properties Source (multicast)", modelC.getConnectProperties().getSource());

		fromMyself = true;
		test();
	}

	@Test void testSinglecast() throws InterruptedException
	{
		assertFalse(modelA.isConnected());
		assertFalse(modelB.isConnected());
		assertFalse(modelC.isConnected());

		modelC.connect(getPropertiesSinglecast(Port.C, Port.A, Port.B));
		modelB.connect(getPropertiesSinglecast(Port.B, Port.A, Port.C));
		modelA.connect(getPropertiesSinglecast(Port.A, Port.B, Port.C));

		assertEquals("Connect Properties Source (singlecast C<-[A, B])", modelC.getConnectProperties().getSource());
		assertEquals("Connect Properties Source (singlecast B<-[A, C])", modelB.getConnectProperties().getSource());
		assertEquals("Connect Properties Source (singlecast A<-[B, C])", modelA.getConnectProperties().getSource());

		test();
	}

	private void test() throws InterruptedException
	{
		assertTrue(modelA.isConnected());
		assertTrue(modelB.isConnected());
		assertTrue(modelC.isConnected());
		assertNotNull(modelA.getClusterProperties());
		assertNotNull(modelB.getClusterProperties());
		assertNotNull(modelC.getClusterProperties());

		fromMyselfBeforeA = count("fromMyself", modelA);
		fromMyselfBeforeB = count("fromMyself", modelB);
		fromMyselfBeforeC = count("fromMyself", modelC);

		assertIt(0, 0, 0);

		modelA.pingClusterNetwork();
		sleepLongerThan(500);
		assertIt(1, 0, 0);

		modelA.pingClusterNetwork();
		sleepLongerThan(500);
		assertIt(2, 0, 0);

		modelB.pingClusterNetwork();
		sleepLongerThan(500);
		assertIt(2, 1, 0);

		modelC.pingClusterNetwork();
		sleepLongerThan(500);
		assertIt(2, 1, 1);

		modelA.pingClusterNetwork();
		sleepLongerThan(500);
		assertIt(3, 1, 1);

		modelC.pingClusterNetwork();
		sleepLongerThan(500);
		assertIt(3, 1, 2);
	}

	private void assertIt(final int pingA, final int pingB, final int pingC)
	{
		final ClusterSenderInfo senderA = modelA.getClusterSenderInfo();
		final ClusterSenderInfo senderB = modelB.getClusterSenderInfo();
		final ClusterSenderInfo senderC = modelC.getClusterSenderInfo();
		assertGreaterZero(senderA.getSendBufferSize());
		assertGreaterZero(senderB.getSendBufferSize());
		assertGreaterZero(senderC.getSendBufferSize());
		assertEquals(0, senderA.getTrafficClass());
		assertEquals(0, senderB.getTrafficClass());
		assertEquals(0, senderC.getTrafficClass());
		assertEquals(0, senderA.getInvalidationSplit());
		assertEquals(0, senderB.getInvalidationSplit());
		assertEquals(0, senderC.getInvalidationSplit());
		assertEquals(0, count("invalidationSplit", modelA));
		assertEquals(0, count("invalidationSplit", modelB));
		assertEquals(0, count("invalidationSplit", modelC));

		final ClusterListenerInfo listenerA = modelA.getClusterListenerInfo();
		final ClusterListenerInfo listenerB = modelB.getClusterListenerInfo();
		final ClusterListenerInfo listenerC = modelC.getClusterListenerInfo();
		assertIt(pingA+pingB+pingC, fromMyselfBeforeA, modelA, listenerA);
		assertIt(pingA+pingB+pingC, fromMyselfBeforeB, modelB, listenerB);
		assertIt(pingA+pingB+pingC, fromMyselfBeforeC, modelC, listenerC);

		final Map<Integer,Node> nodesA = getNodes(listenerA);
		final Map<Integer,Node> nodesB = getNodes(listenerB);
		final Map<Integer,Node> nodesC = getNodes(listenerC);
		if( pingA>0 || pingB>0  || pingC>0 )
		{
			assertEquals(2, nodesA.size());
			assertEquals(2, nodesB.size());
			assertEquals(2, nodesC.size());
			final Node nodeBA = nodesB.get(senderA.getNodeID());
			final Node nodeCA = nodesC.get(senderA.getNodeID());
			final Node nodeAB = nodesA.get(senderB.getNodeID());
			final Node nodeCB = nodesC.get(senderB.getNodeID());
			final Node nodeAC = nodesA.get(senderC.getNodeID());
			final Node nodeBC = nodesB.get(senderC.getNodeID());
			assertEquals(senderA.getNodeID(), nodeBA.getID());
			assertEquals(senderA.getNodeID(), nodeCA.getID());
			assertEquals(senderB.getNodeID(), nodeAB.getID());
			assertEquals(senderB.getNodeID(), nodeCB.getID());
			assertEquals(senderC.getNodeID(), nodeAC.getID());
			assertEquals(senderC.getNodeID(), nodeBC.getID());
			assertEquals(senderA.getLocalPort(), nodeBA.getPort());
			assertEquals(senderA.getLocalPort(), nodeCA.getPort());
			assertEquals(senderB.getLocalPort(), nodeAB.getPort());
			assertEquals(senderB.getLocalPort(), nodeCB.getPort());
			assertEquals(senderC.getLocalPort(), nodeAC.getPort());
			assertEquals(senderC.getLocalPort(), nodeBC.getPort());
			assertIt(pingA, nodeBA.getPingInfo());
			assertIt(pingA, nodeCA.getPingInfo());
			assertIt(pingB, nodeAB.getPingInfo());
			assertIt(pingB, nodeCB.getPingInfo());
			assertIt(pingC, nodeAC.getPingInfo());
			assertIt(pingC, nodeBC.getPingInfo());
			assertIt(pingB+pingC, nodeBA.getPongInfo());
			assertIt(pingB+pingC, nodeCA.getPongInfo());
			assertIt(pingA+pingC, nodeAB.getPongInfo());
			assertIt(pingA+pingC, nodeCB.getPongInfo());
			assertIt(pingA+pingB, nodeAC.getPongInfo());
			assertIt(pingA+pingB, nodeBC.getPongInfo());
			assertIt(0, nodeBA.getInvalidateInfo());
			assertIt(0, nodeCA.getInvalidateInfo());
			assertIt(0, nodeAB.getInvalidateInfo());
			assertIt(0, nodeCB.getInvalidateInfo());
			assertIt(0, nodeAC.getInvalidateInfo());
			assertIt(0, nodeBC.getInvalidateInfo());
			assertLastRoundTripSet(pingB, modelB, nodeBA);
			assertLastRoundTripSet(pingC, modelC, nodeCA);
			assertLastRoundTripSet(pingA, modelA, nodeAB);
			assertLastRoundTripSet(pingC, modelC, nodeCB);
			assertLastRoundTripSet(pingA, modelA, nodeAC);
			assertLastRoundTripSet(pingB, modelB, nodeBC);
		}
		else
		{
			assertEquals(0, nodesA.size());
			assertEquals(0, nodesB.size());
			assertEquals(0, nodesC.size());
		}
	}

	private void assertIt(
			final int fromMyself,
			final double fromMyselfBefore,
			final Model model,
			final ClusterListenerInfo actual)
	{
		assertGreaterZero(actual.getReceiveBufferSize());
		assertEquals(0, actual.getException());
		assertEquals(0, actual.getMissingMagic());
		assertEquals(0, actual.getWrongSecret());
		assertEquals(this.fromMyself ? fromMyself : 0, actual.getFromMyself()     - fromMyselfBefore);
		assertEquals(this.fromMyself ? fromMyself : 0, count("fromMyself", model) - fromMyselfBefore);
	}

	private static Map<Integer,Node> getNodes(final ClusterListenerInfo i)
	{
		final HashMap<Integer,Node> result = new HashMap<>();
		for(final Node n : i.getNodes())
			assertNull(result.put(n.getID(), n));
		return sensitive(result, Integer.class, Node.class);
	}

	private static void assertIt(
			final int inOrder,
			final SequenceChecker.Info actual)
	{
		assertEquals(inOrder, actual.getInOrder());
		assertEquals(0, actual.getOutOfOrder());
		assertEquals(0, actual.getDuplicate());
		assertEquals(0, actual.getLost());
		assertEquals(0, actual.getLate());
		assertEquals(0, actual.getPending());
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static final class TypeC extends Item
	{
		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<TypeC> TYPE = com.exedio.cope.TypesBound.newType(TypeC.class,TypeC::new);

		@com.exedio.cope.instrument.Generated
		private TypeC(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	static final Model modelC = new Model(TypeC.TYPE);

	static
	{
		modelC.enableSerialization(ClusterNetworkPing3Test.class, "modelC");
	}

	@AfterEach
	void tearDown()
	{
		modelC.disconnect();
		modelC.removeAllChangeListeners();
	}
}
