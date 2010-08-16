/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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

import java.io.File;
import java.util.Collection;
import java.util.List;

import com.exedio.cope.junit.CopeAssert;
import com.exedio.cope.util.Properties;
import com.exedio.cope.util.SequenceChecker;

public class ClusterNetworkTest extends CopeAssert
{
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		final ConnectProperties defaultProperties = new ConnectProperties(new File("runtime/utiltest.properties"));
		final Properties.Source source = defaultProperties.getSourceObject();
		final ConnectProperties properties = new ConnectProperties(
				new Properties.Source()
				{
					public String get(final String key)
					{
						if(key.equals("cluster"))
							return "true";
						else if(key.equals("cluster.log"))
							return "false";
						else
							return source.get(key);
					}

					public String getDescription()
					{
						return source.getDescription();
					}

					public Collection<String> keySet()
					{
						return source.keySet();
					}
				},
				new Properties.Source()
				{
					public String get(final String key)
					{
						if(key.equals("cluster.secret"))
							return "1234";
						else
							throw new RuntimeException(key);
					}

					public String getDescription()
					{
						return "Connect Properties Context";
					}

					public Collection<String> keySet()
					{
						return null;
					}
				}
			);
		modelA.connect(properties);
		modelB.connect(properties);
	}

	@Override
	protected void tearDown() throws Exception
	{
		modelB.disconnect();
		modelA.disconnect();
		super.tearDown();
	}

	public void testPing() throws InterruptedException
	{
		assertEquals("Connect Properties Context", modelA.getConnectProperties().getContext().getDescription());
		assertEquals("Connect Properties Context", modelB.getConnectProperties().getContext().getDescription());
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

	private static void assertIt(final int pingA, final int pingB)
	{
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
		assertEquals(pingA+pingB, listenerA.getFromMyself());
		assertEquals(pingA+pingB, listenerB.getFromMyself());

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

	static class TypeA extends Item
	{
		private TypeA(final ActivationParameters ap)
		{
			super(ap);
		}

		private static final long serialVersionUID = 1l;

		static final Type<TypeA> TYPE = TypesBound.newType(TypeA.class);
	}

	static class TypeB extends Item
	{
		private TypeB(final ActivationParameters ap)
		{
			super(ap);
		}

		private static final long serialVersionUID = 1l;

		static final Type<TypeB> TYPE = TypesBound.newType(TypeB.class);
	}

	static final Model modelA = new Model(TypeA.TYPE);
	static final Model modelB = new Model(TypeB.TYPE);
}
