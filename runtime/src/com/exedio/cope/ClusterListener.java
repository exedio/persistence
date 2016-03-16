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

import static com.exedio.cope.ClusterUtil.KIND_INVALIDATE;
import static com.exedio.cope.ClusterUtil.KIND_PING;
import static com.exedio.cope.ClusterUtil.KIND_PONG;
import static com.exedio.cope.ClusterUtil.MAGIC0;
import static com.exedio.cope.ClusterUtil.MAGIC1;
import static com.exedio.cope.ClusterUtil.MAGIC2;
import static com.exedio.cope.ClusterUtil.MAGIC3;
import static com.exedio.cope.ClusterUtil.pingString;

import com.exedio.cope.util.SequenceChecker;
import gnu.trove.TIntHashSet;
import gnu.trove.TIntObjectHashMap;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.NoSuchElementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class ClusterListener
{
	private static final Logger logger = LoggerFactory.getLogger(ClusterListener.class);

	private final ClusterProperties properties;
	private final int secret;
	private final int localNode;
	private final int sequenceCheckerCapacity;
	private final int typeLength;

	ClusterListener(
			final ClusterProperties properties,
			final int typeLength)
	{
		this.properties = properties;
		this.secret = properties.getSecret();
		this.localNode = properties.node;
		this.sequenceCheckerCapacity = properties.listenSeqCheckCap;
		this.typeLength = typeLength;
	}

	final void handle(final DatagramPacket packet)
	{
		final Iter iter = new Iter(packet);

		if(!iter.checkBytes(MAGIC))
		{
			missingMagic.inc();
			return;
		}

		if(secret!=iter.next())
		{
			wrongSecret.inc();
			return;
		}

		final int remoteNode = iter.next();
		if(localNode==remoteNode)
		{
			fromMyself.inc();
			return;
		}

		// kind
		final int kind = iter.next();

		switch(kind)
		{
			case KIND_PING:
			{
				handlePingPong(packet, iter, remoteNode, true);
				break;
			}
			case KIND_PONG:
			{
				handlePingPong(packet, iter, remoteNode, false);
				break;
			}
			case KIND_INVALIDATE:
			{
				final int sequence = iter.next();

				if(node(remoteNode, packet).invalidate(sequence))
				{
					if(logger.isWarnEnabled())
						logger.warn("invalidate duplicate {} from {}", sequence, packet.getAddress());
					break;
				}

				final TIntHashSet[] invalidations = new TIntHashSet[typeLength];
				outer: while(iter.hasNext())
				{
					final int typeIdTransiently = iter.next();
					final TIntHashSet set = new TIntHashSet();
					invalidations[typeIdTransiently] = set;
					inner: while(true)
					{
						if(!iter.hasNext())
							break outer;

						final int pk = iter.next();
						if(pk==PK.NaPK)
							break inner;

						set.add(pk);
					}
				}

				invalidate(remoteNode, invalidations);

				break;
			}
			default:
				throw new RuntimeException("illegal kind: " + kind);
		}
	}

	private static final byte[] MAGIC = new byte[]{MAGIC0, MAGIC1, MAGIC2, MAGIC3};

	private void handlePingPong(
			final DatagramPacket packet,
			final Iter iter,
			final int remoteNode,
			final boolean ping)
	{
		final int sequence = iter.next();

		iter.checkPingPayload(properties, ping);

		if(node(remoteNode, packet).pingPong(ping, sequence))
		{
			if(logger.isWarnEnabled())
				logger.warn("{} duplicate {} from {}", new Object[]{pingString(ping), sequence, packet.getAddress()});
			return;
		}

		if(ping)
			pong();
	}

	static final class Iter
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

		boolean checkBytes(final byte[] expected)
		{
			int pos = this.pos;
			for(int i = 0; i<expected.length; i++)
				if(expected[i]!=buf[pos++])
				{
					if(pos>endOffset)
						throw new NoSuchElementException(String.valueOf(length));
					return false;
				}

			if(pos>endOffset)
				throw new NoSuchElementException(String.valueOf(length));
			this.pos = pos;
			return true;
		}

		int next()
		{
			int pos = this.pos;
			final int result =
				((buf[pos++] & 0xff)    ) |
				((buf[pos++] & 0xff)<< 8) |
				((buf[pos++] & 0xff)<<16) |
				((buf[pos++] & 0xff)<<24) ;
			if(pos>endOffset)
				throw new NoSuchElementException(String.valueOf(length));
			this.pos = pos;
			return result;
		}

		void checkPingPayload(final ClusterProperties properties, final boolean ping)
		{
			properties.checkPingPayload(pos, buf, offset, length, ping);
		}
	}

	abstract void invalidate(int remoteNode, TIntHashSet[] invalidations);
	abstract void pong();
	abstract int getReceiveBufferSize();

	// info

	final VolatileLong exception = new VolatileLong();
	private final VolatileLong missingMagic = new VolatileLong();
	private final VolatileLong wrongSecret = new VolatileLong();
	private final VolatileLong fromMyself = new VolatileLong();
	private final TIntObjectHashMap<Node> nodes = new TIntObjectHashMap<>();

	private static final class Node
	{
		private static final Logger logger = LoggerFactory.getLogger(ClusterListener.class);

		private final int id;
		private final long firstEncounter;
		private final InetAddress address;
		private final int port;
		private final SequenceChecker invalidateSequenceChecker;
		private final SequenceChecker pingSequenceChecker;
		private final SequenceChecker pongSequenceChecker;

		Node(
				final int id,
				final DatagramPacket packet,
				final int sequenceCheckerCapacity)
		{
			this.id = id;
			this.firstEncounter = System.currentTimeMillis();
			this.address = packet.getAddress();
			this.port = packet.getPort();
			this.invalidateSequenceChecker = new SequenceChecker(sequenceCheckerCapacity);
			this.pingSequenceChecker       = new SequenceChecker(sequenceCheckerCapacity);
			this.pongSequenceChecker       = new SequenceChecker(sequenceCheckerCapacity);
			if(logger.isInfoEnabled())
				logger.info("encountered new node {}", id);
		}

		boolean invalidate(final int sequence)
		{
			return check(invalidateSequenceChecker, sequence);
		}

		boolean pingPong(final boolean ping, final int sequence)
		{
			return check((ping ? pingSequenceChecker : pongSequenceChecker), sequence);
		}

		private static boolean check(final SequenceChecker checker, final int sequence)
		{
			synchronized(checker)
			{
				return checker.check(sequence);
			}
		}

		ClusterListenerInfo.Node getInfo()
		{
			return new ClusterListenerInfo.Node(
					id,
					new Date(firstEncounter),
					address, port,
					getInfo(invalidateSequenceChecker),
					getInfo(pingSequenceChecker),
					getInfo(pongSequenceChecker));
		}

		private static SequenceChecker.Info getInfo(final SequenceChecker checker)
		{
			synchronized(checker)
			{
				return checker.getInfo();
			}
		}
	}

	final Node node(final int id, final DatagramPacket packet)
	{
		synchronized(nodes)
		{
			Node result = nodes.get(id);
			if(result!=null)
				return result;

			nodes.put(id, result = new Node(id, packet, sequenceCheckerCapacity));
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
		final ArrayList<ClusterListenerInfo.Node> infoNodes = new ArrayList<>(ns.length);
		for(final Node n : ns)
			infoNodes.add(n.getInfo());

		return new ClusterListenerInfo(
				getReceiveBufferSize(),
				exception.get(),
				missingMagic.get(),
				wrongSecret.get(),
				fromMyself.get(),
				infoNodes);
	}
}
