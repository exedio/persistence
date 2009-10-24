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
	static final byte MAGIC0 = (byte)0xc0;
	static final byte MAGIC1 = (byte)0xbe;
	static final byte MAGIC2 = 0x11;
	static final byte MAGIC3 = 0x11;
	
	static final int KIND_PING       = 0x00110001;
	static final int KIND_PONG       = 0x00110002;
	static final int KIND_INVALIDATE = 0x00120001;
	
	final int secret;
	final int node;
	final boolean log;
	final int packetSize;
	final InetAddress group;
	final byte[] pingPayload;
	
	static ClusterConfig get(final ConnectProperties properties)
	{
		final Properties.Source context = properties.getContext();
		{
			final String secretS = context.get("cluster.secret");
			if(secretS==null)
				return null;
			
			final int secret;
			try
			{
				secret = Integer.valueOf(secretS);
			}
			catch(NumberFormatException e)
			{
				throw new RuntimeException("cluster.secret must be a valid integer, but was >" + secretS + '<', e);
			}
			
			return new ClusterConfig(secret, properties);
		}
	}
	
	private ClusterConfig(final int secret, final ConnectProperties properties)
	{
		this(secret, new Random().nextInt(), properties);
	}
	
	ClusterConfig(final int secret, final int node, final ConnectProperties properties)
	{
		this.secret = secret;
		this.node = node;
		this.log = properties.clusterLog.getBooleanValue();
		this.packetSize = properties.clusterPacketSize.getIntValue() & (~3);
		try
		{
			this.group = InetAddress.getByName(properties.clusterGroup.getStringValue());
		}
		catch(UnknownHostException e)
		{
			throw new RuntimeException(e);
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
