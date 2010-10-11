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

import static com.exedio.cope.ClusterConstants.KIND_INVALIDATE;
import static com.exedio.cope.ClusterConstants.KIND_PING;
import static com.exedio.cope.ClusterConstants.KIND_PONG;
import static com.exedio.cope.ClusterConstants.MAGIC0;
import static com.exedio.cope.ClusterConstants.MAGIC1;
import static com.exedio.cope.ClusterConstants.MAGIC2;
import static com.exedio.cope.ClusterConstants.MAGIC3;
import gnu.trove.TIntHashSet;
import gnu.trove.TIntObjectHashMap;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;

import com.exedio.cope.util.SequenceChecker;

abstract class ClusterListener
{
	private final ClusterProperties properties;
	private final int secret;
	private final int localNode;
	private final boolean log;
	private final int typeLength;

	ClusterListener(
			final ClusterProperties properties,
			final int typeLength)
	{
		this.properties = properties;
		this.secret = properties.getSecret();
		this.localNode = properties.node;
		this.log = properties.log.booleanValue();
		this.typeLength = typeLength;
	}

	final void handle(final DatagramPacket packet)
	{
		final Iter iter = new Iter(packet);

		if(iter.nextByte()!=MAGIC0 ||
			iter.nextByte()!=MAGIC1 ||
			iter.nextByte()!=MAGIC2 ||
			iter.nextByte()!=MAGIC3)
		{
			missingMagic++;
			return;
		}

		if(secret!=iter.nextInt())
		{
			wrongSecret++;
			return;
		}

		final int node = iter.nextInt();
		if(localNode==node)
		{
			fromMyself++;
			return;
		}

		// kind
		final int kind = iter.nextInt();

		switch(kind)
		{
			case KIND_PING:
			{
				if(handlePingPong(packet, iter, node, true))
					pong();
				break;
			}
			case KIND_PONG:
			{
				handlePingPong(packet, iter, node, false);
				break;
			}
			case KIND_INVALIDATE:
			{
				final int sequence = iter.nextInt();

				if(node(node, packet).invalidate(sequence))
				{
					if(log)
						System.out.println("COPE Cluster Listener invalidate duplicate " + sequence + " from " + packet.getAddress());
					break;
				}

				final TIntHashSet[] invalidations = new TIntHashSet[typeLength];
				outer: while(iter.hasNext())
				{
					final int typeIdTransiently = iter.nextInt();
					final TIntHashSet set = new TIntHashSet();
					invalidations[typeIdTransiently] = set;
					inner: while(true)
					{
						if(!iter.hasNext())
							break outer;

						final int pk = iter.nextInt();
						if(pk==PK.NaPK)
							break inner;

						set.add(pk);
					}
				}

				invalidate(node, invalidations);

				break;
			}
			default:
				throw new RuntimeException("illegal kind: " + kind);
		}
	}

	private boolean handlePingPong(final DatagramPacket packet, final Iter iter, final int node, final boolean ping)
	{
		final int sequence = iter.nextInt();

		iter.checkPingPayload(properties, ping);

		if(node(node, packet).pingPong(ping, sequence))
		{
			if(log)
				System.out.println("COPE Cluster Listener " + pingString(ping) + " duplicate " + sequence + " from " + packet.getAddress());
			return false;
		}

		return true;
	}

	static String pingString(final boolean ping)
	{
		return ping ? "ping" : "pong";
	}

	private static final class Iter
	{
		private final int length;
		private final int offset;
		private final int endOffset;
		private final byte[] buf;
		private int pos;

		Iter(final DatagramPacket packet)
		{
			this.offset = packet.getOffset();
			this.length = packet.getLength();
			this.endOffset = offset + length;
			this.buf = packet.getData();

			this.pos = offset;
		}

		boolean hasNext()
		{
			return pos<endOffset;
		}

		byte nextByte()
		{
			final byte result = buf[pos++];
			if(pos>endOffset)
				throw new RuntimeException(String.valueOf(endOffset));
			return result;
		}

		int nextInt()
		{
			final int result = ClusterListener.unmarshal(pos, buf, endOffset);
			pos += 4;
			return result;
		}

		void checkPingPayload(final ClusterProperties properties, final boolean ping)
		{
			properties.checkPingPayload(pos, buf, offset, length, ping);
		}
	}

	static final int unmarshal(int pos, final byte[] buf, final int endOffset)
	{
		final int result =
			((buf[pos++] & 0xff)    ) |
			((buf[pos++] & 0xff)<< 8) |
			((buf[pos++] & 0xff)<<16) |
			((buf[pos++] & 0xff)<<24) ;
		if(pos>endOffset)
			throw new RuntimeException(String.valueOf(endOffset));
		return result;
	}

	abstract void invalidate(int node, TIntHashSet[] invalidations);
	abstract void pong();
	abstract int getReceiveBufferSize();
	abstract void close();

	// info

	volatile long exception = 0;
	private volatile long missingMagic = 0;
	private volatile long wrongSecret = 0;
	private volatile long fromMyself = 0;
	private final TIntObjectHashMap<Node> nodes = new TIntObjectHashMap<Node>();

	private static final class Node
	{
		final int id;
		final long firstEncounter;
		final InetAddress address;
		final int port;
		final SequenceChecker pingSequenceChecker;
		final SequenceChecker pongSequenceChecker;
		final SequenceChecker invalidateSequenceChecker;

		Node(final int id, final DatagramPacket packet, final boolean log)
		{
			this.id = id;
			this.firstEncounter = System.currentTimeMillis();
			this.address = packet.getAddress();
			this.port = packet.getPort();
			this.pingSequenceChecker = new SequenceChecker(200);
			this.pongSequenceChecker = new SequenceChecker(200);
			this.invalidateSequenceChecker = new SequenceChecker(200);
			if(log)
				System.out.println("COPE Cluster Listener encountered new node " + id);
		}

		boolean pingPong(final boolean ping, final int sequence)
		{
			return (ping ? pingSequenceChecker : pongSequenceChecker).check(sequence);
		}

		boolean invalidate(final int sequence)
		{
			return invalidateSequenceChecker.check(sequence);
		}

		ClusterListenerInfo.Node getInfo()
		{
			return new ClusterListenerInfo.Node(
					id,
					new Date(firstEncounter),
					address, port,
					pingSequenceChecker.getInfo(),
					pongSequenceChecker.getInfo(),
					invalidateSequenceChecker.getInfo());
		}
	}

	final Node node(final int id, final DatagramPacket packet)
	{
		synchronized(nodes)
		{
			Node result = nodes.get(id);
			if(result!=null)
				return result;

			nodes.put(id, result = new Node(id, packet, log));
			return result;
		}
	}

	final ClusterListenerInfo getInfo()
	{
		final Node[] ns;
		synchronized(nodes)
		{
			ns = nodes.getValues(new Node[nodes.size()]);
		}
		final ArrayList<ClusterListenerInfo.Node> infoNodes = new ArrayList<ClusterListenerInfo.Node>(ns.length);
		for(final Node n : ns)
			infoNodes.add(n.getInfo());

		return new ClusterListenerInfo(
				getReceiveBufferSize(),
				exception,
				missingMagic,
				wrongSecret,
				fromMyself,
				infoNodes);
	}
}
