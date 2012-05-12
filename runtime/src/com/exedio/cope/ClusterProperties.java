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

import com.exedio.cope.util.Properties;

final class ClusterProperties extends Properties
{
	static ClusterProperties get(final Properties.Source source)
	{
		final ClusterProperties clusterProperties = new ClusterProperties(source);
		if(!clusterProperties.isEnabled())
			return null;

		return clusterProperties;
	}

	private static final String MULTICAST_ADDRESS = "230.0.0.1";
	private static final int    MULTICAST_PORT = 14446;

	/**
	 * a value of 0 disables cluster invalidation at all
	 */
	private final IntField     secret              = new     IntField("secret", 0, MIN_VALUE);
	private final BooleanField nodeAuto            = new BooleanField("nodeAuto" , true);
	private final IntField     nodeField           = new     IntField("node"     , 0, MIN_VALUE);
	        final BooleanField log                 = new BooleanField("log", true);
	private final BooleanField sendSourcePortAuto  = new BooleanField("sendSourcePortAuto" , true);
	private final IntField     sendSourcePort      = new     IntField("sendSourcePort"     , 14445, 1);
	private final StringField  sendAddressField    = new  StringField("sendAddress",         MULTICAST_ADDRESS);
	        final IntField     sendDestinationPort = new     IntField("sendDestinationPort", MULTICAST_PORT, 1);
	private final BooleanField sendBufferDefault   = new BooleanField("sendBufferDefault"  , true);
	private final IntField     sendBuffer          = new     IntField("sendBuffer"         , 50000, 1);
	private final BooleanField sendTrafficDefault  = new BooleanField("sendTrafficDefault" , true);
	private final IntField     sendTraffic         = new     IntField("sendTraffic"        , 0, 0);
	private final StringField  listenAddressField  = new  StringField("listenAddress",       MULTICAST_ADDRESS);
	private final IntField     listenPort          = new     IntField("listenPort",          MULTICAST_PORT, 1);
	private final BooleanField listenBufferDefault = new BooleanField("listenBufferDefault", true);
	private final IntField     listenBuffer        = new     IntField("listenBuffer"       , 50000, 1);
	private final IntField     listenThreads       = new     IntField("listenThreads",       1, 1);
	private final IntField     listenThreadsMax    = new     IntField("listenThreadsMax",    10, 1);
	private final BooleanField listenPrioritySet   = new BooleanField("listenPrioritySet",   false);
	private final IntField     listenPriority      = new     IntField("listenPriority",      MAX_PRIORITY, MIN_PRIORITY);
	        final IntField     listenSeqCheckCap   = new     IntField("listenSequenceCheckerCapacity", 200, 1);
	private final BooleanField multicast           = new BooleanField("multicast",           true);
	private final IntField     packetSizeField     = new     IntField("packetSize",          1400, 32);

	final int node;
	final InetAddress sendAddress, listenAddress;
	final int packetSize;
	private final byte[] pingPayload;

	@edu.umd.cs.findbugs.annotations.SuppressWarnings("DMI_RANDOM_USED_ONLY_ONCE") // Random object created and used only once
	private ClusterProperties(final Source source)
	{
		super(source, null);

		if(isEnabled())
		{
			if(nodeAuto.booleanValue())
			{
				this.node = new Random().nextInt();
			}
			else
			{
				this.node = nodeField.intValue();
				if(node==0)
					throw new IllegalArgumentException(); // must not be left at default value
			}
			if(log.booleanValue())
				System.out.println("COPE Cluster Network node id: " + ClusterSenderInfo.toStringNodeID(node));

			this.sendAddress   = getAddress(sendAddressField);
			this.listenAddress = getAddress(listenAddressField);

			if(listenThreads.intValue()>listenThreadsMax.intValue())
				throw new IllegalArgumentException(
						listenThreads.getKey() + '=' + listenThreads.intValue() + " must be less or equal " +
						listenThreadsMax.getKey() + '=' + listenThreadsMax.intValue());

			this.packetSize = packetSizeField.intValue() & (~3);
			{
				final Random r = new Random(secret.intValue());
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

	private InetAddress getAddress(final StringField field)
	{
		final String value = field.stringValue();
		try
		{
			return InetAddress.getByName(value);
		}
		catch(final UnknownHostException e)
		{
			throw new RuntimeException(value, e);
		}
	}

	private boolean isEnabled()
	{
		return secret.intValue()!=0;
	}

	int getSecret()
	{
		if(!isEnabled())
			throw new IllegalStateException("is disabled");

		return secret.intValue();
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
				sendSourcePortAuto.booleanValue()
				? new DatagramSocket()
				: new DatagramSocket(sendSourcePort.intValue());
			if(!sendBufferDefault.booleanValue())
				result.setSendBufferSize(sendBuffer.intValue());
			if(!sendTrafficDefault.booleanValue())
				result.setTrafficClass(sendTraffic.intValue());
			return result;
		}
		catch(final SocketException e)
		{
			throw new RuntimeException(
					String.valueOf(sendSourcePort.intValue()) + '/' +
					String.valueOf(sendSourcePort.intValue()), e);
		}
	}

	int getListenThreads()
	{
		return listenThreads.intValue();
	}

	int getListenThreadsMax()
	{
		return listenThreadsMax.intValue();
	}

	DatagramSocket newListenSocket()
	{
		if(!isEnabled())
			throw new IllegalStateException("is disabled");

		final int port = listenPort.intValue();
		try
		{
			DatagramSocket result;
			if(multicast.booleanValue())
			{
				final MulticastSocket resultMulti = new MulticastSocket(port);
				resultMulti.joinGroup(listenAddress);
				result = resultMulti;
			}
			else
			{
				result = new DatagramSocket(port);
			}
			if(!listenBufferDefault.booleanValue())
				result.setReceiveBufferSize(listenBuffer.intValue());
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

		if(listenPrioritySet.booleanValue())
			thread.setPriority(listenPriority.intValue());
	}
}
