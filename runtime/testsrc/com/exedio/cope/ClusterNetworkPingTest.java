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

import static com.exedio.cope.AssertUtil.assertUnmodifiable;
import static com.exedio.cope.AssertUtil.sleepLongerThan;
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

		assertEquals("Connect Properties Context (multicast)", modelA.getConnectProperties().getContext().getDescription());
		assertEquals("Connect Properties Context (multicast)", modelB.getConnectProperties().getContext().getDescription());
		assertUnmodifiable(modelA.getThreadControllers());
		assertIt(true, 0, 0);

		modelA.pingClusterNetwork();
		sleepLongerThan(50);
		assertIt(true, 1, 0);

		modelA.pingClusterNetwork();
		sleepLongerThan(50);
		assertIt(true, 2, 0);

		modelB.pingClusterNetwork();
		sleepLongerThan(50);
		assertIt(true, 2, 1);
	}

	@Test public void testSinglecast() throws InterruptedException
	{
		modelA.connect(getProperties(false, 14446, 14447));
		modelB.connect(getProperties(false, 14447, 14446));

		assertEquals("Connect Properties Context (14446>14447)", modelA.getConnectProperties().getContext().getDescription());
		assertEquals("Connect Properties Context (14447>14446)", modelB.getConnectProperties().getContext().getDescription());
		assertIt(false, 0, 0);

		modelA.pingClusterNetwork();
		sleepLongerThan(50);
		assertIt(false, 1, 0);

		modelA.pingClusterNetwork();
		sleepLongerThan(50);
		assertIt(false, 2, 0);

		modelB.pingClusterNetwork();
		sleepLongerThan(50);
		assertIt(false, 2, 1);
	}

	private static void assertIt(final boolean multicast, final int pingA, final int pingB)
	{
		assertNotNull(modelA.getClusterProperties());
		assertNotNull(modelB.getClusterProperties());

		final ClusterSenderInfo senderA = modelA.getClusterSenderInfo();
		final ClusterSenderInfo senderB = modelB.getClusterSenderInfo();
		assertEquals(0, senderA.getInvalidationSplit());
		assertEquals(0, senderB.getInvalidationSplit());

		final ClusterListenerInfo listenerA = modelA.getClusterListenerInfo();
		final ClusterListenerInfo listenerB = modelB.getClusterListenerInfo();
		assertEquals(0, listenerA.getException());
		assertEquals(0, listenerB.getException());
		assertEquals(0, listenerA.getMissingMagic());
		assertEquals(0, listenerB.getMissingMagic());
		assertEquals(0, listenerA.getWrongSecret());
		assertEquals(0, listenerB.getWrongSecret());
		assertEquals(multicast ? (pingA+pingB) : 0, listenerA.getFromMyself());
		assertEquals(multicast ? (pingA+pingB) : 0, listenerB.getFromMyself());

		final List<ClusterListenerInfo.Node> nodesA = listenerA.getNodes();
		final List<ClusterListenerInfo.Node> nodesB = listenerB.getNodes();
		assertEquals((pingA==0) ? 0 : 1, nodesA.size());
		assertEquals((pingA==0) ? 0 : 1, nodesB.size());
		if(pingA>0)
		{
			final ClusterListenerInfo.Node nodeA = nodesA.get(0);
			final ClusterListenerInfo.Node nodeB = nodesB.get(0);
			assertEquals(senderA.getNodeID(), nodeB.getID());
			assertEquals(senderB.getNodeID(), nodeA.getID());
			assertEquals(senderA.getNodeIDString(), nodeB.getIDString());
			assertEquals(senderB.getNodeIDString(), nodeA.getIDString());
			assertIt(pingB, 0, 0, 0, 0, 0, nodeA.getPingInfo());
			assertIt(pingA, 0, 0, 0, 0, 0, nodeB.getPingInfo());
			assertIt(pingA, 0, 0, 0, 0, 0, nodeA.getPongInfo());
			assertIt(pingB, 0, 0, 0, 0, 0, nodeB.getPongInfo());
			assertIt(0, 0, 0, 0, 0, 0, nodeA.getInvalidateInfo());
			assertIt(0, 0, 0, 0, 0, 0, nodeB.getInvalidateInfo());
		}
	}

	private static final void assertIt(
			final int inOrder,
			final int outOfOrder,
			final int duplicate,
			final int lost,
			final int late,
			final int pending,
			final SequenceChecker.Info actual)
	{
		assertEquals(inOrder   , actual.getInOrder());
		assertEquals(outOfOrder, actual.getOutOfOrder());
		assertEquals(duplicate , actual.getDuplicate());
		assertEquals(lost      , actual.getLost());
		assertEquals(late      , actual.getLate());
		assertEquals(pending   , actual.getPending());
	}
}
