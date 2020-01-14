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
import static com.exedio.cope.ClusterUtil.marshal;

import gnu.trove.TLongHashSet;
import gnu.trove.TLongIterator;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Tags;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

abstract class ClusterSender
{
	private final ClusterProperties properties;

	private static final int KIND = 12;
	private static final int SEQUENCE = 16;
	private static final int PING_NANOS = 20;
	private static final int PING_NODE = 28;
	private final byte[] pingPongTemplate;

	private static final int INVALIDATE_TEMPLATE_SIZE = 16;
	private final byte[] invalidateTemplate;

	private final AtomicInteger pingSequence = new AtomicInteger();
	private final AtomicInteger pongSequence = new AtomicInteger();
	private final AtomicInteger invalidationSequence = new AtomicInteger();

	ClusterSender(final ClusterProperties properties, final String modelName)
	{
		this.properties = properties;
		{
			final byte[] pingPongTemplate = new byte[properties.packetSize];
			pingPongTemplate[0] = MAGIC0;
			pingPongTemplate[1] = MAGIC1;
			pingPongTemplate[2] = MAGIC2;
			pingPongTemplate[3] = MAGIC3;
			int pos = 4;
			pos = marshal(pos, pingPongTemplate, properties.secret);
			pos = marshal(pos, pingPongTemplate, properties.node);
			assert pos==KIND;
			pos = marshal(pos, pingPongTemplate, 0xeeeeee);
			assert pos==SEQUENCE;
			//noinspection ConstantConditions
			assert pos==INVALIDATE_TEMPLATE_SIZE;
			pos = marshal(pos, pingPongTemplate, 0xdddddd);
			assert pos==PING_NANOS;
			pos = marshal(pos, pingPongTemplate, 0xccccccbbbbbbl);
			assert pos==PING_NODE;
			pos = marshal(pos, pingPongTemplate, 0xaaaaaa);

			pos = properties.copyPingPayload(pos, pingPongTemplate);
			assert pos==properties.packetSize : pos;
			this.pingPongTemplate = pingPongTemplate;
		}
		{
			final byte[] invalidateTemplate = new byte[INVALIDATE_TEMPLATE_SIZE];
			invalidateTemplate[0] = MAGIC0;
			invalidateTemplate[1] = MAGIC1;
			invalidateTemplate[2] = MAGIC2;
			invalidateTemplate[3] = MAGIC3;
			int pos = 4;
			pos = marshal(pos, invalidateTemplate, properties.secret);
			pos = marshal(pos, invalidateTemplate, properties.node);
			assert pos==KIND;
			pos = marshal(pos, invalidateTemplate, KIND_INVALIDATE);
			assert pos==SEQUENCE;
			//noinspection ConstantConditions
			assert pos==INVALIDATE_TEMPLATE_SIZE;
			this.invalidateTemplate = invalidateTemplate;
		}
		final MetricsBuilder metrics = new MetricsBuilder(Cluster.class, Tags.of("model", modelName));
		invalidationSplit = metrics.counter("invalidationSplit", "How often an invalidation must be split before sending due to packet size constraint.", Tags.empty());
	}

	final void ping(final int count)
	{
		pingPong(KIND_PING, pingSequence, count, nanoTime(), properties.node);
	}

	final void pong(final long pingNanos, final int pingNode)
	{
		pingPong(KIND_PONG, pongSequence, 1, pingNanos, pingNode);
	}

	private void pingPong(
			final int kind, final AtomicInteger sequence,
			final int count, final long nanos, final int node)
	{
		assert kind==KIND_PING||kind==KIND_PONG : kind;
		final int packetSize = properties.packetSize;

		final byte[] buf = new byte[packetSize];
		System.arraycopy(pingPongTemplate, 0, buf, 0, packetSize);
		marshal(KIND, buf, kind);

		try
		{
			int sequenceStart = sequence.getAndAdd(count);
			for(int i = 0; i<count; i++)
			{
				marshal(SEQUENCE, buf, sequenceStart++);
				marshal(PING_NANOS, buf, nanos);
				marshal(PING_NODE, buf, node);
				send(packetSize, buf);
			}
		}
		catch(final IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	// info
	private final Counter invalidationSplit;

	final void invalidate(final TLongHashSet[] invalidations)
	{
		final int packetSize = properties.packetSize;
		final int packetSizeMinus4 = packetSize-4;
		final int length;
		{
			int l =
					INVALIDATE_TEMPLATE_SIZE +
					4; // invalidationSequence

			for(final TLongHashSet invalidation : invalidations)
				if(invalidation!=null)
					l +=
						12 + // type id (4 bytes) + NaPK for end (8 bytes)
						(invalidation.size() << 3); // 8 bytes per PK

			length = l;
		}
		final byte[] buf = new byte[Math.min(length, packetSize)];
		System.arraycopy(invalidateTemplate, 0, buf, 0, INVALIDATE_TEMPLATE_SIZE);

		int typeIdTransiently = 0;
		TLongIterator i = null;
		try
		{
			int packetCount = 0;
			packetLoop: do
			{
				packetCount++;

				int pos = INVALIDATE_TEMPLATE_SIZE;

				pos = marshal(pos, buf, invalidationSequence.getAndIncrement());

				boolean packetNotEmpty = false;

				for(; typeIdTransiently<invalidations.length; typeIdTransiently++)
				{
					if(i!=null && !i.hasNext())
					{
						i = null;
						continue;
					}

					final TLongHashSet invalidation = invalidations[typeIdTransiently];
					if(invalidation!=null)
					{
						if(pos>=packetSize)
						{
							send(pos, buf);
							continue packetLoop;
						}
						pos = marshal(pos, buf, typeIdTransiently);
						packetNotEmpty = true;

						if(i==null)
							i = invalidation.iterator();
						while(i.hasNext())
						{
							if(pos>=packetSizeMinus4)
							{
								send(pos, buf);
								continue packetLoop;
							}
							pos = marshal(pos, buf, i.next());
						}

						if(pos>=packetSizeMinus4)
						{
							send(pos, buf);
							continue packetLoop;
						}
						pos = marshal(pos, buf, PK.NaPK);

						i = null;
					}
				}

				if(packetNotEmpty)
					send(pos, buf);
				break;
			}
			while(true);

			invalidationSplit.increment(packetCount-1);
		}
		catch(final IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	final ClusterSenderInfo getInfo()
	{
		return new ClusterSenderInfo(
				properties.node,
				getLocalPort(),
				getSendBufferSize(),
				getTrafficClass(),
				invalidationSplit);
	}

	abstract long nanoTime();
	@SuppressWarnings("RedundantThrows") // IDEA bug - IOException is not redundant
	abstract void send(final int length, final byte[] buf) throws IOException;
	abstract int getLocalPort();
	abstract int getSendBufferSize();
	abstract int getTrafficClass();
}
