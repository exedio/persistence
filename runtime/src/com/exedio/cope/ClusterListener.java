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

import static com.exedio.cope.ClusterSenderInfo.toStringNodeID;
import static com.exedio.cope.ClusterUtil.KIND_INVALIDATE;
import static com.exedio.cope.ClusterUtil.KIND_PING;
import static com.exedio.cope.ClusterUtil.KIND_PONG;
import static com.exedio.cope.ClusterUtil.MAGIC0;
import static com.exedio.cope.ClusterUtil.MAGIC1;
import static com.exedio.cope.ClusterUtil.MAGIC2;
import static com.exedio.cope.ClusterUtil.MAGIC3;
import static com.exedio.cope.ClusterUtil.pingString;

import com.exedio.cope.util.SequenceChecker;
import gnu.trove.TIntObjectHashMap;
import gnu.trove.TLongHashSet;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicLong;
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
		this.secret = properties.secret;
		this.localNode = properties.node;
		this.sequenceCheckerCapacity = properties.listenSeqCheckCap;
		this.typeLength = typeLength;
	}

	final void handle(final DatagramPacket packet)
	{
		final ClusterIterator iter = new ClusterIterator(packet);

		if(!iter.checkBytes(MAGIC))
		{
			missingMagic.incrementAndGet();
			return;
		}

		if(secret!=iter.next())
		{
			wrongSecret.incrementAndGet();
			return;
		}

		final int remoteNode = iter.next();
		if(localNode==remoteNode)
		{
			fromMyself.incrementAndGet();
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

				final TLongHashSet[] invalidations = new TLongHashSet[typeLength];
				outer: while(iter.hasNext())
				{
					final int typeIdTransiently = iter.next();
					final TLongHashSet set = new TLongHashSet();
					invalidations[typeIdTransiently] = set;
					inner: while(true)
					{
						if(!iter.hasNext())
							break outer;

						final long pk = iter.nextLong();
						if(pk==PK.NaPK)
							//noinspection UnnecessaryLabelOnBreakStatement
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

	private static final byte[] MAGIC = {MAGIC0, MAGIC1, MAGIC2, MAGIC3};

	private void handlePingPong(
			final DatagramPacket packet,
			final ClusterIterator iter,
			final int remoteNode,
			final boolean ping)
	{
		final int sequence = iter.next();
		final long nanos = iter.nextLong();
		final int pingNode = iter.next();

		iter.checkPingPayload(properties, ping);

		final Node node = node(remoteNode, packet);
		if(node.pingPong(ping, sequence))
		{
			if(logger.isWarnEnabled())
				logger.warn("{} duplicate {} from {}", new Object[]{pingString(ping), sequence, packet.getAddress()});
			return;
		}

		if(ping)
		{
			pong(nanos, pingNode);
		}
		else
		{
			// Must capture round trip time only if pong was triggered by a ping coming from local node.
			// Otherwise System#nanoTime() of different JVMs is used to measure elapsed time.
			if(pingNode==localNode)
				node.roundTrip(nanos);
		}
	}

	abstract void invalidate(int remoteNode, TLongHashSet[] invalidations);
	abstract void pong(long pingNanos, int pingNode);
	abstract int getReceiveBufferSize();

	// info

	final AtomicLong exception = new AtomicLong();
	private final AtomicLong missingMagic = new AtomicLong();
	private final AtomicLong wrongSecret = new AtomicLong();
	private final AtomicLong fromMyself = new AtomicLong();
	private final TIntObjectHashMap<Node> nodes = new TIntObjectHashMap<>();

	private static final class Node
	{
		@SuppressWarnings("LoggerInitializedWithForeignClass")
		private static final Logger logger = LoggerFactory.getLogger(ClusterListener.class);

		private final int id;
		private final String idString;
		private final long firstEncounter;
		private final InetAddress address;
		private final int port;
		private long lastRoundTripDate  = Long.MIN_VALUE;
		private long lastRoundTripNanos = Long.MIN_VALUE;
		private long  minRoundTripDate  = Long.MIN_VALUE;
		private long  minRoundTripNanos = Long.MAX_VALUE;
		private long  maxRoundTripDate  = Long.MIN_VALUE;
		private long  maxRoundTripNanos = Long.MIN_VALUE;
		private final SequenceChecker invalidateSequenceChecker;
		private final SequenceChecker pingSequenceChecker;
		private final SequenceChecker pongSequenceChecker;

		Node(
				final int id,
				final DatagramPacket packet,
				final int sequenceCheckerCapacity)
		{
			this.id = id;
			this.idString = toStringNodeID(id);
			this.firstEncounter = System.currentTimeMillis();
			this.address = packet.getAddress();
			this.port = packet.getPort();
			this.invalidateSequenceChecker = new SequenceChecker(sequenceCheckerCapacity);
			this.pingSequenceChecker       = new SequenceChecker(sequenceCheckerCapacity);
			this.pongSequenceChecker       = new SequenceChecker(sequenceCheckerCapacity);
			if(logger.isInfoEnabled())
				logger.info("encountered new node {}", idString);
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
			//noinspection SynchronizationOnLocalVariableOrMethodParameter OK: parametersare not supplied from outside this class
			synchronized(checker)
			{
				return checker.check(sequence);
			}
		}

		void roundTrip(final long pingNanos)
		{
			final long now = System.currentTimeMillis();
			final long nanos = System.nanoTime() - pingNanos;

			lastRoundTripDate = now;
			lastRoundTripNanos = nanos;

			if(nanos<minRoundTripNanos)
			{
				minRoundTripDate = now;
				minRoundTripNanos = nanos;
			}
			if(nanos>maxRoundTripNanos)
			{
				maxRoundTripDate = now;
				maxRoundTripNanos = nanos;
			}

			if(logger.isInfoEnabled())
				logger.info("ping pong round trip via {} ({}:{}) took {}ns", new Object[]{
						idString,
						address, port,
						formatNanos(nanos)});
		}

		private static String formatNanos(final long nanos)
		{
			return NumberFormat.getInstance(Locale.ENGLISH).format(nanos);
		}

		ClusterListenerInfo.Node getInfo()
		{
			return new ClusterListenerInfo.Node(
					id,
					new Date(firstEncounter),
					address, port,
					getInfo(lastRoundTripDate, lastRoundTripNanos),
					getInfo( minRoundTripDate,  minRoundTripNanos),
					getInfo( maxRoundTripDate,  maxRoundTripNanos),
					getInfo(invalidateSequenceChecker),
					getInfo(pingSequenceChecker),
					getInfo(pongSequenceChecker));
		}

		private static ClusterListenerInfo.RoundTrip getInfo(final long date, final long nanos)
		{
			return date!=Long.MIN_VALUE ? new ClusterListenerInfo.RoundTrip(date, nanos) : null;
		}

		private static SequenceChecker.Info getInfo(final SequenceChecker checker)
		{
			//noinspection SynchronizationOnLocalVariableOrMethodParameter OK: parametersare not supplied from outside this class
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
