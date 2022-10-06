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
import static com.exedio.cope.InfoRegistry.countInt;

import com.exedio.cope.util.Hex;
import com.exedio.cope.util.SequenceChecker;
import com.exedio.cope.util.SequenceChecker2;
import com.exedio.cope.util.SequenceChecker2.Result;
import gnu.trove.TIntObjectHashMap;
import gnu.trove.TLongHashSet;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.IntConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class ClusterListener
{
	private static final Logger logger = LoggerFactory.getLogger(ClusterListener.class);

	private final ClusterProperties properties;
	private final int secret;
	private final int localNode;
	private final int seqCheckCapacity;
	private final int typeLength;
	private final MetricsBuilder metrics;

	ClusterListener(
			final ClusterProperties properties,
			final MetricsBuilder metrics,
			final int typeLength)
	{
		this.properties = properties;
		this.secret = properties.secret;
		this.localNode = properties.node;
		this.seqCheckCapacity = properties.listenSeqCheckCap;
		this.typeLength = typeLength;
		this.metrics = metrics;
		exception    = metrics.counter("fail",         "How often a received packet failed to parse.", Tags.empty());
		missingMagic = metrics.counter("missingMagic", "How often a received packet did not start with the magic bytes " + Hex.encodeUpper(MAGIC) + '.', Tags.empty());
		wrongSecret  = metrics.counter("wrongSecret",  "How often a received packet did not carry the secret this cluster is configured with.", Tags.empty());
		fromMyself   = metrics.counter("fromMyself",   "How often a received packet did come from this node itself.", Tags.empty());
	}

	final void handle(final DatagramPacket packet)
	{
		final ClusterIterator iter = new ClusterIterator(packet);

		if(!iter.checkBytes(MAGIC))
		{
			missingMagic.increment();
			return;
		}

		if(secret!=iter.next())
		{
			wrongSecret.increment();
			return;
		}

		final int remoteNode = iter.next();
		if(localNode==remoteNode)
		{
			fromMyself.increment();
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

				invalidate(invalidations, new TransactionInfoRemote(remoteNode));

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

	abstract void invalidate(TLongHashSet[] invalidations, TransactionInfoRemote info);
	abstract void pong(long pingNanos, int pingNode);
	abstract int getReceiveBufferSize();

	// info

	final Counter exception;
	private final Counter missingMagic;
	private final Counter wrongSecret;
	private final Counter fromMyself;
	private final TIntObjectHashMap<Node> nodes = new TIntObjectHashMap<>();

	private static final class Node
	{
		private static final Logger logger = LoggerFactory.getLogger(ClusterListener.class);

		private final int id;
		private final String idString;
		private final long firstEncounter;
		private final InetAddress address;
		private final int port;
		private final Timer roundTrip;
		private long lastRoundTripDate  = Long.MIN_VALUE;
		private long lastRoundTripNanos = Long.MIN_VALUE;
		private long  minRoundTripDate  = Long.MIN_VALUE;
		private long  minRoundTripNanos = Long.MAX_VALUE;
		private long  maxRoundTripDate  = Long.MIN_VALUE;
		private long  maxRoundTripNanos = Long.MIN_VALUE;
		private final SeqCheck invalidateSeqCheck;
		private final SeqCheck       pingSeqCheck;
		private final SeqCheck       pongSeqCheck;

		Node(
				final int id,
				final DatagramPacket packet,
				final MetricsBuilder metricsTemplate,
				final int seqCheckCapacity)
		{
			this.id = id;
			this.idString = toStringNodeID(id);
			this.firstEncounter = System.currentTimeMillis();
			this.address = packet.getAddress();
			this.port = packet.getPort();
			final MetricsBuilder metrics = metricsTemplate.tag(Tags.of(
					"id", idString,
					"address", Objects.toString(address),
					"port", String.valueOf(port)));
			this.roundTrip = metrics.timer("roundTrip", "The time needed by a round trip of a ping to / pong from this node.", Tags.empty());
			this.invalidateSeqCheck = new SeqCheck(seqCheckCapacity, metrics, "invalidate");
			this.      pingSeqCheck = new SeqCheck(seqCheckCapacity, metrics, "ping");
			this.      pongSeqCheck = new SeqCheck(seqCheckCapacity, metrics, "pong");
			if(logger.isInfoEnabled())
				logger.info("encountered new node {}", idString);
		}

		boolean invalidate(final int sequence)
		{
			return invalidateSeqCheck.check(sequence);
		}

		boolean pingPong(final boolean ping, final int sequence)
		{
			return (ping ? pingSeqCheck : pongSeqCheck).check(sequence);
		}

		void roundTrip(final long pingNanos)
		{
			final long now = System.currentTimeMillis();
			final long nanos = System.nanoTime() - pingNanos;

			roundTrip.record(nanos, TimeUnit.NANOSECONDS);

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
					invalidateSeqCheck.getInfo(),
					pingSeqCheck      .getInfo(),
					pongSeqCheck      .getInfo());
		}

		private static ClusterListenerInfo.RoundTrip getInfo(final long date, final long nanos)
		{
			return date!=Long.MIN_VALUE ? new ClusterListenerInfo.RoundTrip(date, nanos) : null;
		}

		private static final class SeqCheck
		{
			private final SequenceChecker2 backing;

			private final EnumMap<Result,Counter> counters = new EnumMap<>(Result.class);
			private final Counter lost;
			private final IntConsumer lostConsumer;
			private final Object lock = new Object();

			SeqCheck(final int capacity, final MetricsBuilder metrics, final String kind)
			{
				backing = new SequenceChecker2(capacity);

				final String NAME = "sequence";
				final String DESC = "How sequence numbers did arrive";
				final Tags kindTag = Tags.of("kind", kind);
				final String RESULT = "result";
				for(final Result r : Result.values())
					counters.put(r,
							metrics.counter(NAME, DESC, kindTag.and(RESULT, r.name())));
				lost =   metrics.counter(NAME, DESC, kindTag.and(RESULT, "lost"));
				lostConsumer = lost::increment;
				metrics.gauge(backing, SequenceChecker2::getPending, "pending", "How many sequence number are due yet", kindTag); // Must not have nameSuffix "sequence", otherwise it fails in micrometer 1.7.4: "Collector already registered that provides name"
			}

			public boolean check(final int number)
			{
				final Result result;
				synchronized(lock)
				{
					result = backing.check(number, lostConsumer);
				}
				counters.get(result).increment();
				return result == Result.duplicate;
			}

			public SequenceChecker.Info getInfo()
			{
				return new SequenceChecker.Info(
						countInt(counters.get(Result.inOrder)),
						countInt(counters.get(Result.outOfOrder)),
						countInt(counters.get(Result.duplicate)),
						countInt(lost),
						countInt(counters.get(Result.late)),
						backing.getPending());
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

			nodes.put(id, result = new Node(id, packet, metrics, seqCheckCapacity));
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
				exception,
				missingMagic,
				wrongSecret,
				fromMyself,
				infoNodes);
	}
}
