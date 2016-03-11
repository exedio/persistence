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

import static com.exedio.cope.tojunit.Assert.assertUnmodifiable;
import static com.exedio.cope.tojunit.Assert.sleepLongerThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import com.exedio.cope.util.SequenceChecker;
import java.util.List;
import org.junit.Test;

public class ClusterNetworkPingTest extends ClusterNetworkTest
{
	@Test public void testMulticast() throws InterruptedException
	{
		try
		{
			modelA.getThreadControllers();
			fail();
		}
		catch(final Model.NotConnectedException e)
		{
			assertEquals(modelA, e.getModel());
		}

		// when running this test alone, it fails on Windows if modelA is connected before modelB
		modelB.connect(getProperties(true, -1, -1));
		modelA.connect(getProperties(true, -1, -1));

		assertEquals("Connect Properties Source (multicast)", modelA.getConnectProperties().getSource());
		assertEquals("Connect Properties Source (multicast)", modelB.getConnectProperties().getSource());

		test(true);
	}

	@Test public void testSinglecast() throws InterruptedException
	{
		modelA.connect(getProperties(false, 14446, 14447));
		modelB.connect(getProperties(false, 14447, 14446));

		assertEquals("Connect Properties Source (14446>14447)", modelA.getConnectProperties().getSource());
		assertEquals("Connect Properties Source (14447>14446)", modelB.getConnectProperties().getSource());

		test(false);
	}

	private static void test(final boolean multicast) throws InterruptedException
	{
		assertNotNull(modelA.getClusterProperties());
		assertNotNull(modelB.getClusterProperties());
		assertUnmodifiable(modelA.getThreadControllers());

		assertIt(multicast, 0, 0);

		modelA.pingClusterNetwork();
		sleepLongerThan(50);
		assertIt(multicast, 1, 0);

		modelA.pingClusterNetwork();
		sleepLongerThan(50);
		assertIt(multicast, 2, 0);

		modelB.pingClusterNetwork();
		sleepLongerThan(50);
		assertIt(multicast, 2, 1);
	}

	private static void assertIt(final boolean multicast, final int pingA, final int pingB)
	{
		final ClusterSenderInfo senderA = modelA.getClusterSenderInfo();
		final ClusterSenderInfo senderB = modelB.getClusterSenderInfo();
		assertEquals(0, senderA.getInvalidationSplit());
		assertEquals(0, senderB.getInvalidationSplit());

		final ClusterListenerInfo listenerA = modelA.getClusterListenerInfo();
		final ClusterListenerInfo listenerB = modelB.getClusterListenerInfo();
		assertIt(multicast ? (pingA+pingB) : 0, listenerA);
		assertIt(multicast ? (pingA+pingB) : 0, listenerB);

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

	private static final void assertIt(
			final int fromMyself,
			final ClusterListenerInfo actual)
	{
		assertEquals(0, actual.getException());
		assertEquals(0, actual.getMissingMagic());
		assertEquals(0, actual.getWrongSecret());
		assertEquals(fromMyself, actual.getFromMyself());
	}

	private static final void assertIt(
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
}
