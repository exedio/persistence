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

import static java.util.Objects.requireNonNull;

import com.exedio.cope.util.SequenceChecker;
import java.net.InetAddress;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public final class ClusterListenerInfo
{
	private final int receiveBufferSize;
	private final long exception;
	private final long missingMagic;
	private final long wrongSecret;
	private final long fromMyself;
	private final List<Node> nodes;

	ClusterListenerInfo(
			final int receiveBufferSize,
			final long exception,
			final long missingMagic,
			final long wrongSecret,
			final long fromMyself,
			final List<Node> nodes)
	{
		this.receiveBufferSize = receiveBufferSize;
		this.exception = exception;
		this.missingMagic = missingMagic;
		this.wrongSecret = wrongSecret;
		this.fromMyself = fromMyself;
		this.nodes = Collections.unmodifiableList(nodes);
	}

	public int getReceiveBufferSize()
	{
		return receiveBufferSize;
	}

	public long getException()
	{
		return exception;
	}

	public long getMissingMagic()
	{
		return missingMagic;
	}

	public long getWrongSecret()
	{
		return wrongSecret;
	}

	public long getFromMyself()
	{
		return fromMyself;
	}

	public List<Node> getNodes()
	{
		return nodes;
	}

	public static final class Node
	{
		private final int id;
		private final long firstEncounter;
		private final InetAddress address;
		private final int port;
		private final RoundTrip lastRoundTrip;
		private final RoundTrip  minRoundTrip;
		private final RoundTrip  maxRoundTrip;
		private final SequenceChecker.Info invalidateInfo;
		private final SequenceChecker.Info pingInfo;
		private final SequenceChecker.Info pongInfo;

		Node(
				final int id,
				final Date firstEncounter,
				final InetAddress address,
				final int port,
				final RoundTrip lastRoundTrip,
				final RoundTrip  minRoundTrip,
				final RoundTrip  maxRoundTrip,
				final SequenceChecker.Info invalidateInfo,
				final SequenceChecker.Info pingInfo,
				final SequenceChecker.Info pongInfo)
		{
			this.id = id;
			this.firstEncounter = firstEncounter.getTime();
			this.address = address;
			this.port = port;
			this.lastRoundTrip = lastRoundTrip;
			this. minRoundTrip =  minRoundTrip;
			this. maxRoundTrip =  maxRoundTrip;
			this.invalidateInfo = requireNonNull(invalidateInfo);
			this.pingInfo = requireNonNull(pingInfo);
			this.pongInfo = requireNonNull(pongInfo);
		}

		/**
		 * Returns the {@link ClusterSenderInfo#getNodeID() id} of the node.
		 * @see #getIDString()
		 */
		public int getID()
		{
			return id;
		}

		/**
		 * Returns the {@link ClusterSenderInfo#getNodeIDString() id} of the node.
		 * @see #getID()
		 */
		public String getIDString()
		{
			return ClusterSenderInfo.toStringNodeID(id);
		}

		public Date getFirstEncounter()
		{
			return new Date(firstEncounter);
		}

		/**
		 * @see java.net.DatagramPacket#getAddress()
		 */
		public InetAddress getAddress()
		{
			return address;
		}

		/**
		 * @see java.net.DatagramPacket#getPort()
		 */
		public int getPort()
		{
			return port;
		}

		public RoundTrip getLastRoundTrip()
		{
			return lastRoundTrip;
		}

		public RoundTrip getMinimumRoundTrip()
		{
			return minRoundTrip;
		}

		public RoundTrip getMaximumRoundTrip()
		{
			return maxRoundTrip;
		}

		public SequenceChecker.Info getInvalidateInfo()
		{
			return invalidateInfo;
		}

		public SequenceChecker.Info getPingInfo()
		{
			return pingInfo;
		}

		public SequenceChecker.Info getPongInfo()
		{
			return pongInfo;
		}

		@Override
		public String toString()
		{
			return String.valueOf(id);
		}
	}

	public static final class RoundTrip
	{
		private final long date;
		private final long nanos;

		RoundTrip(
				final long date,
				final long nanos)
		{
			this.date  = date;
			this.nanos = nanos;
		}

		public Date getDate()
		{
			return new Date(date);
		}

		public long getNanos()
		{
			return nanos;
		}

		@Override
		public String toString()
		{
			return "" + date;
		}
	}
}
