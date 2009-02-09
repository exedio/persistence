/*
 * Copyright (C) 2004-2008  exedio GmbH (www.exedio.com)
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

import gnu.trove.TIntHashSet;
import gnu.trove.TIntIterator;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;

final class ClusterConfig
{
	static final byte MAGIC0 = (byte)0xc0;
	static final byte MAGIC1 = (byte)0xbe;
	static final byte MAGIC2 = 0x11;
	static final byte MAGIC3 = 0x11;
	
	static final int PING_AT_SEQUENCE = -1;
	static final int PONG_AT_SEQUENCE = -2;
	
	final int secret;
	final int node;
	final boolean log;
	final int packetSize;
	final InetAddress group;
	final byte[] pingPayload;
	
	ClusterConfig(final int secret, final ConnectProperties properties)
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
			for(int pos = 16; pos<pingPayload.length; pos++)
				pingPayload[pos] = (byte)(r.nextInt()>>8);
			this.pingPayload = pingPayload;
		}
		if(log)
			System.out.println("COPE Cluster Invalidation node id: " + node);
	}
	
	static final String toString(final TIntHashSet[] invalidations)
	{
		final StringBuilder bf = new StringBuilder();
		bf.append('[');
		boolean first = true;
		for(final TIntHashSet invalidation : invalidations)
		{
			if(first)
				first = false;
			else
				bf.append(", ");
			
			if(invalidation!=null)
			{
				bf.append('{');
				boolean first2 = true;
				for(final TIntIterator i = invalidation.iterator(); i.hasNext(); )
				{
					if(first2)
						first2 = false;
					else
						bf.append(',');
					
					bf.append(i.next());
				}
				bf.append('}');
			}
			else
				bf.append("null");
		}
		bf.append(']');
		
		return bf.toString();
	}
}
