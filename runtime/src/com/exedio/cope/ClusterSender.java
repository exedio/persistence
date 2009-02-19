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

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

final class ClusterSender
{
	private final ClusterConfig config;
	private final int destinationPort;
	private final DatagramSocket socket;
	
	private static final int KIND = 12;
	private static final int SEQUENCE = 16;
	private final byte[] pingPongTemplate;
	
	private static final int INVALIDATE_TEMPLATE_SIZE = 16;
	private final byte[] invalidateTemplate;
	
	private final AtomicInteger pingSequence = new AtomicInteger(-1);
	private final AtomicInteger pongSequence = new AtomicInteger(-1);
	private final AtomicInteger invalidationSequence = new AtomicInteger(-1);
	
	ArrayList<byte[]> testSink = null;
	
	ClusterSender(final ClusterConfig config, final ConnectProperties properties)
	{
		this.config = config;
		this.destinationPort = properties.clusterSendDestinationPort.getIntValue();
		try
		{
			this.socket = new DatagramSocket(properties.clusterSendSourcePort.getIntValue());
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}
		{
			final byte[] pingPongTemplate = new byte[config.packetSize];
			pingPongTemplate[0] = ClusterConfig.MAGIC0;
			pingPongTemplate[1] = ClusterConfig.MAGIC1;
			pingPongTemplate[2] = ClusterConfig.MAGIC2;
			pingPongTemplate[3] = ClusterConfig.MAGIC3;
			int pos = 4;
			pos = marshal(pos, pingPongTemplate, config.secret);
			pos = marshal(pos, pingPongTemplate, config.node);
			assert pos==KIND;
			pos = marshal(pos, pingPongTemplate, 0xeeeeee);
			assert pos==SEQUENCE;
			assert pos==INVALIDATE_TEMPLATE_SIZE;
			pos = marshal(pos, pingPongTemplate, 0xdddddd);
				
			for(; pos<config.packetSize; pos++)
				pingPongTemplate[pos] = config.pingPayload[pos];
			assert pos==config.packetSize : pos;
			this.pingPongTemplate = pingPongTemplate;
		}
		{
			final byte[] invalidateTemplate = new byte[INVALIDATE_TEMPLATE_SIZE];
			invalidateTemplate[0] = ClusterConfig.MAGIC0;
			invalidateTemplate[1] = ClusterConfig.MAGIC1;
			invalidateTemplate[2] = ClusterConfig.MAGIC2;
			invalidateTemplate[3] = ClusterConfig.MAGIC3;
			int pos = 4;
			pos = marshal(pos, invalidateTemplate, config.secret);
			pos = marshal(pos, invalidateTemplate, config.node);
			assert pos==KIND;
			pos = marshal(pos, invalidateTemplate, ClusterConfig.KIND_INVALIDATE);
			assert pos==SEQUENCE;
			assert pos==INVALIDATE_TEMPLATE_SIZE;
			this.invalidateTemplate = invalidateTemplate;
		}
	}
	
	void ping(final int count)
	{
		pingPong(ClusterConfig.KIND_PING, pingSequence, count);
	}
	
	void pong()
	{
		pingPong(ClusterConfig.KIND_PONG, pongSequence, 1);
	}
	
	private void pingPong(final int kind, final AtomicInteger sequence, final int count)
	{
		if(count<=0)
			throw new IllegalArgumentException("count must be greater than zero, but was " + count);
		
		assert kind==ClusterConfig.KIND_PING||kind==ClusterConfig.KIND_PONG : kind;
		final int packetSize = config.packetSize;
		
		final byte[] buf = new byte[packetSize];
		System.arraycopy(pingPongTemplate, 0, buf, 0, packetSize);
		marshal(KIND, buf, kind);
		
		try
		{
			for(int i = 0; i<count; i++)
			{
				marshal(SEQUENCE, buf, sequence.incrementAndGet());
				send(packetSize, buf);
			}
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	void invalidate(final TIntHashSet[] invalidations)
	{
		final int packetSize = config.packetSize;
		final int length;
		{
			int pos = 0;
			for(final TIntHashSet invalidation : invalidations)
				if(invalidation!=null)
					pos += 2 + invalidation.size();
			length = INVALIDATE_TEMPLATE_SIZE + 8 + (pos << 2);
		}
		final byte[] buf = new byte[Math.min(length, packetSize)];
		System.arraycopy(invalidateTemplate, 0, buf, 0, INVALIDATE_TEMPLATE_SIZE);
		
		int typeIdTransiently = 0;
		TIntIterator i = null;
		try
		{
			packetLoop: do
			{
				int pos = INVALIDATE_TEMPLATE_SIZE;
				
				pos = marshal(pos, buf, invalidationSequence.incrementAndGet());
				
				for(; typeIdTransiently<invalidations.length; typeIdTransiently++)
				{
					if(i!=null && !i.hasNext())
					{
						i = null;
						continue;
					}
					
					final TIntHashSet invalidation = invalidations[typeIdTransiently];
					if(invalidation!=null)
					{
						if(pos>=packetSize)
						{
							send(pos, buf);
							continue packetLoop;
						}
						pos = marshal(pos, buf, typeIdTransiently);
						
						if(i==null)
							i = invalidation.iterator();
						while(i.hasNext())
						{
							if(pos>=packetSize)
							{
								send(pos, buf);
								continue packetLoop;
							}
							pos = marshal(pos, buf, i.next());
						}
						
						if(pos>=packetSize)
						{
							send(pos, buf);
							continue packetLoop;
						}
						pos = marshal(pos, buf, PK.NaPK);
						
						i = null;
					}
				}
				
				send(pos, buf);
				break;
			}
			while(true);
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	private void send(final int length, final byte[] buf) throws IOException
	{
		if(testSink!=null)
		{
			final byte[] bufCopy = new byte[length];
			System.arraycopy(buf, 0, bufCopy, 0, length);
			testSink.add(bufCopy);
		}
		else
		{
			final DatagramPacket packet =
				new DatagramPacket(buf, length, config.group, destinationPort);
			socket.send(packet);
		}
	}
	
	static int marshal(int pos, final byte[] buf, final int i)
	{
		buf[pos++] = (byte)( i       & 0xff);
		buf[pos++] = (byte)((i>>> 8) & 0xff);
		buf[pos++] = (byte)((i>>>16) & 0xff);
		buf[pos++] = (byte)((i>>>24) & 0xff);
		return pos;
	}
	
	void close()
	{
		socket.close();
	}
}
