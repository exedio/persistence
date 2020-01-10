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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.exedio.cope.util.SequenceChecker;
import java.util.List;
import org.junit.jupiter.api.Test;

public class ClusterNetworkPingTest extends ClusterNetworkTest
{
	private boolean fromMyself = false;

	@Test void testMulticast() throws InterruptedException
	{
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
		assertFalse(modelA.isConnected());
		assertFalse(modelB.isConnected());

		modelA.connect(getPropertiesSinglecast(true));
		modelB.connect(getPropertiesSinglecast(false));

		assertEquals("Connect Properties Source (singlecast forward)",  modelA.getConnectProperties().getSource());
		assertEquals("Connect Properties Source (singlecast backward)", modelB.getConnectProperties().getSource());

		test();
	}

	private void test() throws InterruptedException
	{
		assertTrue(modelA.isConnected());
		assertTrue(modelB.isConnected());
		assertNotNull(modelA.getClusterProperties());
		assertNotNull(modelB.getClusterProperties());

		assertIt(0, 0);

		modelA.pingClusterNetwork();
		sleepLongerThan(50);
		assertIt(1, 0);

		modelA.pingClusterNetwork();
		sleepLongerThan(50);
		assertIt(2, 0);

		modelB.pingClusterNetwork();
		sleepLongerThan(50);
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

		final ClusterListenerInfo listenerA = modelA.getClusterListenerInfo();
		final ClusterListenerInfo listenerB = modelB.getClusterListenerInfo();
		assertIt(pingA+pingB, listenerA);
		assertIt(pingA+pingB, listenerB);

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
			assertLastRoundTripSet(pingA>0, nodeA);
			assertLastRoundTripSet(pingB>0, nodeB);
			assertIt(pingB, nodeA.getPingInfo());
			assertIt(pingA, nodeB.getPingInfo());
			assertIt(pingA, nodeA.getPongInfo());
			assertIt(pingB, nodeB.getPongInfo());
			assertIt(0, nodeA.getInvalidateInfo());
			assertIt(0, nodeB.getInvalidateInfo());
		}
		else
		{
			assertEquals(0, nodesA.size());
			assertEquals(0, nodesB.size());
		}
	}

	private void assertIt(
			final int fromMyself,
			final ClusterListenerInfo actual)
	{
		assertEquals(0, actual.getException());
		assertEquals(0, actual.getMissingMagic());
		assertEquals(0, actual.getWrongSecret());
		assertEquals(this.fromMyself ? fromMyself : 0, actual.getFromMyself());
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

	static void assertLastRoundTripSet(
			final boolean expected,
			final ClusterListenerInfo.Node actual)
	{
		if(expected)
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
	}

	private static void assertGreaterZero(final long actual)
	{
		assertTrue(actual>0, "" + actual);
	}

	private static void assertLessOrEqual(final long a, final long b)
	{
		assertTrue(a<=b, "" + a + "<=" + b);
	}
}
