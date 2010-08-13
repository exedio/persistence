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

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

final class ClusterSenderMulticast extends ClusterSender
{
	private final ClusterConfig config;
	private final int destinationPort;
	private final DatagramSocket socket;





	ClusterSenderMulticast(final ClusterConfig config, final ConnectProperties properties)
	{
		super(config);
		this.config = config;
		this.destinationPort = properties.clusterSendDestinationPort.intValue();
		this.socket = properties.getClusterSendSocket();
	}

	@Override void send(final int length, final byte[] buf) throws IOException
	{
		{
			final DatagramPacket packet =
				new DatagramPacket(buf, length, config.group, destinationPort);
			socket.send(packet);
		}
	}

	@Override
	void close()
	{
		socket.close();
	}
}
