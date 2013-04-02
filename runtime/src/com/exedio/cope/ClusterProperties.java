/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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

import static java.lang.Integer.MIN_VALUE;
import static java.lang.Thread.MAX_PRIORITY;
import static java.lang.Thread.MIN_PRIORITY;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Random;

import com.exedio.cope.util.PrefixSource;
import com.exedio.cope.util.Properties;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

final class ClusterProperties extends Properties
{
	static ClusterProperties get(final ConnectProperties properties)
	{
		if(properties.noContext())
			return null;

		final Properties.Source source = new PrefixSource(properties.getContext(), "cluster.");
		final ClusterProperties clusterProperties = new ClusterProperties(source);
		if(!clusterProperties.isEnabled())
			return null;

		if(!properties.primaryKeyGenerator.persistent)
			throw new IllegalArgumentException("cluster network not supported together with schema.primaryKeyGenerator=" + properties.primaryKeyGenerator.name());

		return clusterProperties;
	}

	private static final String MULTICAST_ADDRESS = "230.0.0.1";
	private static final int    MULTICAST_PORT = 14446;

	/**
	 * a value of 0 disables cluster invalidation at all
	 */
	private final int     secret              = value("secret", 0, MIN_VALUE);
	private final boolean nodeAuto            = value("nodeAuto" , true);
	private final int     nodeField           = value("node"     , 0, MIN_VALUE);
	        final boolean log                 = value("log", true);
	private final boolean sendSourcePortAuto  = value("sendSourcePortAuto" , true);
	private final int     sendSourcePort      = value("sendSourcePort"     , 14445, 1);
	private final String  sendAddressField    = value("sendAddress",         MULTICAST_ADDRESS);
	        final int     sendDestinationPort = value("sendDestinationPort", MULTICAST_PORT, 1);
	private final boolean sendBufferDefault   = value("sendBufferDefault"  , true);
	private final int     sendBuffer          = value("sendBuffer"         , 50000, 1);
	private final boolean sendTrafficDefault  = value("sendTrafficDefault" , true);
	private final int     sendTraffic         = value("sendTraffic"        , 0, 0);
	private final String  listenAddressField  = value("listenAddress",       MULTICAST_ADDRESS);
	private final int     listenPort          = value("listenPort",          MULTICAST_PORT, 1);
	private final boolean listenBufferDefault = value("listenBufferDefault", true);
	private final int     listenBuffer        = value("listenBuffer"       , 50000, 1);
	private final int     listenThreads       = value("listenThreads",       1, 1);
	private final int     listenThreadsMax    = value("listenThreadsMax",    10, 1);
	private final boolean listenPrioritySet   = value("listenPrioritySet",   false);
	private final int     listenPriority      = value("listenPriority",      MAX_PRIORITY, MIN_PRIORITY);
	        final int     listenSeqCheckCap   = value("listenSequenceCheckerCapacity", 200, 1);
	private final boolean multicast           = value("multicast",           true);
	private final int     packetSizeField     = value("packetSize",          1400, 32);

	final int node;
	final InetAddress sendAddress, listenAddress;
	final int packetSize;
	private final byte[] pingPayload;

	@SuppressFBWarnings("DMI_RANDOM_USED_ONLY_ONCE") // Random object created and used only once
	private ClusterProperties(final Source source)
	{
		super(source, null);

		if(isEnabled())
		{
			if(nodeAuto)
			{
				this.node = new Random().nextInt();
			}
			else
			{
				this.node = nodeField;
				if(node==0)
					throw new IllegalArgumentException(); // must not be left at default value
			}
			if(log)
				System.out.println("COPE Cluster Network node id: " + ClusterSenderInfo.toStringNodeID(node));

			this.sendAddress   = getAddress(sendAddressField);
			this.listenAddress = getAddress(listenAddressField);

			if(listenThreads>listenThreadsMax)
				throw new IllegalArgumentException(
						"listenThreads=" + listenThreads + " must be less or equal " +
						"listenThreadsMax=" + listenThreadsMax);

			this.packetSize = packetSizeField & (~3);
			{
				final Random r = new Random(secret);
				final byte[] pingPayload = new byte[this.packetSize];
				for(int pos = 20; pos<pingPayload.length; pos++)
					pingPayload[pos] = (byte)(r.nextInt()>>8);
				this.pingPayload = pingPayload;
			}
		}
		else
		{
			this.node = 0;
			this.sendAddress   = null;
			this.listenAddress = null;
			this.packetSize = MIN_VALUE;
			this.pingPayload = null;
		}
	}

	private static InetAddress getAddress(final String field) // TODO remove
	{
		try
		{
			return InetAddress.getByName(field);
		}
		catch(final UnknownHostException e)
		{
			throw new RuntimeException(field, e);
		}
	}

	private boolean isEnabled()
	{
		return secret!=0;
	}

	int getSecret()
	{
		if(!isEnabled())
			throw new IllegalStateException("is disabled");

		return secret;
	}

	int copyPingPayload(int pos, final byte[] destination)
	{
		for(; pos<packetSize; pos++)
			destination[pos] = pingPayload[pos];
		return pos;
	}

	void checkPingPayload(int pos, final byte[] buf, final int offset, final int length, final boolean ping)
	{
		if(length!=packetSize)
			throw new RuntimeException("invalid " + ClusterListener.pingString(ping) + ", expected length " + packetSize + ", but was " + length);
		final int endPos = offset + length;
		final byte[] pingPayload = this.pingPayload;
		for(; pos<endPos; pos++)
			if(pingPayload[pos-offset]!=buf[pos])
				throw new RuntimeException("invalid " + ClusterListener.pingString(ping) + ", at position " + (pos-offset) + " expected " + pingPayload[pos-offset] + ", but was " + buf[pos]);
	}

	DatagramSocket newSendSocket()
	{
		if(!isEnabled())
			throw new IllegalStateException("is disabled");

		try
		{
			final DatagramSocket result =
				sendSourcePortAuto
				? new DatagramSocket()
				: new DatagramSocket(sendSourcePort);
			if(!sendBufferDefault)
				result.setSendBufferSize(sendBuffer);
			if(!sendTrafficDefault)
				result.setTrafficClass(sendTraffic);
			return result;
		}
		catch(final SocketException e)
		{
			throw new RuntimeException(
					String.valueOf(sendSourcePort) + '/' +
					String.valueOf(sendSourcePort), e);
		}
	}

	int getListenThreads()
	{
		return listenThreads; // TODO remove
	}

	int getListenThreadsMax()
	{
		return listenThreadsMax; // TODO remove
	}

	DatagramSocket newListenSocket()
	{
		if(!isEnabled())
			throw new IllegalStateException("is disabled");

		final int port = listenPort;
		try
		{
			DatagramSocket result;
			if(multicast)
			{
				final MulticastSocket resultMulti = new MulticastSocket(port);
				resultMulti.joinGroup(listenAddress);
				result = resultMulti;
			}
			else
			{
				result = new DatagramSocket(port);
			}
			if(!listenBufferDefault)
				result.setReceiveBufferSize(listenBuffer);
			return result;
		}
		catch(final IOException e)
		{
			throw new RuntimeException(String.valueOf(port), e);
		}
	}

	void setListenPriority(final ThreadSwarm thread)
	{
		if(!isEnabled())
			throw new IllegalStateException("is disabled");

		if(listenPrioritySet)
			thread.setPriority(listenPriority);
	}
}
