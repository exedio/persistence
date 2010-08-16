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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;

import com.exedio.cope.util.Properties;

final class ClusterConfig
{
	private static final String SECRET_NAME = "cluster.secret";

	static final byte MAGIC0 = (byte)0xc0;
	static final byte MAGIC1 = (byte)0xbe;
	static final byte MAGIC2 = 0x11;
	static final byte MAGIC3 = 0x11;

	static final int KIND_PING       = 0x00110001;
	static final int KIND_PONG       = 0x00110002;
	static final int KIND_INVALIDATE = 0x00120001;

	final ClusterProperties properties;
	final int secret;
	final int node;
	final boolean log;
	final int packetSize;
	final InetAddress group;
	final byte[] pingPayload;

	static ClusterConfig get(final ConnectProperties properties)
	{
		final Properties.Source context = properties.getContext();
		final String secretString = context.get(SECRET_NAME);
		if(secretString==null)
			return null;

		final int secret;
		try
		{
			secret = Integer.valueOf(secretString);
		}
		catch(final NumberFormatException e)
		{
			throw new RuntimeException(SECRET_NAME + " must be a valid integer, but was >" + secretString + '<', e);
		}

		final ClusterProperties clusterProperties = new ClusterProperties(context);

		return new ClusterConfig(secret, new Random().nextInt(), clusterProperties);
	}

	ClusterConfig(final int secret, final int node, final ClusterProperties properties)
	{
		this.properties = properties;
		this.secret = secret;
		this.node = node;
		this.log = properties.log.booleanValue();
		this.packetSize = properties.packetSize.intValue() & (~3);
		try
		{
			this.group = InetAddress.getByName(properties.group.stringValue());
		}
		catch(final UnknownHostException e)
		{
			throw new RuntimeException(properties.group.stringValue(), e);
		}
		{
			final Random r = new Random(secret);
			final byte[] pingPayload = new byte[packetSize];
			for(int pos = 20; pos<pingPayload.length; pos++)
				pingPayload[pos] = (byte)(r.nextInt()>>8);
			this.pingPayload = pingPayload;
		}
		if(log)
			System.out.println("COPE Cluster Network node id: " + node);
	}
}
