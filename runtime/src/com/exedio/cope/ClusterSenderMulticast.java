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

import static java.net.StandardSocketOptions.IP_MULTICAST_LOOP;

import com.exedio.cope.ClusterProperties.Send;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

final class ClusterSenderMulticast extends ClusterSender
{
	private final Send[] send;
	private final DatagramSocket socket;
	private final int localPort;
	private final int sendBufferSize;
	private final int trafficClass;
	private final boolean loopback;

	ClusterSenderMulticast(final ClusterProperties properties, final ModelMetrics metrics)
	{
		super(properties, metrics);
		this.send = properties.send();
		this.socket = properties.newSendSocket();
		this.localPort = socket.getLocalPort();
		try
		{
			this.sendBufferSize = socket.getSendBufferSize();
			this.trafficClass = socket.getTrafficClass();
			this.loopback = socket.getOption(IP_MULTICAST_LOOP);
		}
		catch(final IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	long nanoTime()
	{
		return System.nanoTime();
	}

	@Override
	void send(final int length, final byte[] buf) throws IOException
	{
		final DatagramPacket packet =
			new DatagramPacket(buf, length);
		for(final Send send : this.send)
		{
			packet.setAddress(send.address);
			packet.setPort(send.port);
			socket.send(packet);
		}
	}

	@Override
	int getLocalPort()
	{
		return localPort;
	}

	@Override
	int getSendBufferSize()
	{
		return sendBufferSize;
	}

	@Override
	int getTrafficClass()
	{
		return trafficClass;
	}

	boolean getLoopback()
	{
		return loopback;
	}

	void close()
	{
		socket.close();
	}
}
