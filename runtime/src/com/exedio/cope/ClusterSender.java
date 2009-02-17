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

final class ClusterSender
{
	private final ClusterConfig config;
	private final boolean log;
	private final int sourcePort;
	private final int destinationPort;
	private final DatagramSocket socket;
	
	private static final int PROLOG_SIZE = 12;
	private final byte[] prolog;
	
	private Sequence pingPongSequence = new Sequence();
	private Sequence invalidationSequence = new Sequence();
	
	ArrayList<byte[]> testSink = null;
	
	ClusterSender(final ClusterConfig config, final ConnectProperties properties)
	{
		this.config = config;
		this.log = config.log;
		this.sourcePort      = properties.clusterSendSourcePort.getIntValue();
		this.destinationPort = properties.clusterSendDestinationPort.getIntValue();
		try
		{
			this.socket = new DatagramSocket(sourcePort);
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}
		
		final byte[] prolog = new byte[PROLOG_SIZE];
		prolog[0] = ClusterConfig.MAGIC0;
		prolog[1] = ClusterConfig.MAGIC1;
		prolog[2] = ClusterConfig.MAGIC2;
		prolog[3] = ClusterConfig.MAGIC3;
		int pos = 4;
		pos = marshal(pos, prolog, config.secret);
		pos = marshal(pos, prolog, config.node);
		assert pos==PROLOG_SIZE;
		this.prolog = prolog;
	}
	
	void ping(final int count)
	{
		pingPong(ClusterConfig.KIND_PING, count);
	}
	
	void pong()
	{
		pingPong(ClusterConfig.KIND_PONG, 1);
	}
	
	private void pingPong(final int kind, final int count)
	{
		if(count<=0)
			throw new IllegalArgumentException("count must be greater than zero, but was " + count);
		
		assert kind==ClusterConfig.KIND_PING||kind==ClusterConfig.KIND_PONG : kind;
		
		final byte[] buf = new byte[config.packetSize];
		System.arraycopy(prolog, 0, buf, 0, PROLOG_SIZE);
		
		for(int i = 0; i<count; i++)
		{
			int pos = PROLOG_SIZE;
			pos = marshal(pos, buf, kind);
			pos = marshal(pos, buf, pingPongSequence.next());
				
			for(; pos<config.packetSize; pos++)
				buf[pos] = config.pingPayload[pos];
			assert pos==config.packetSize : pos;
			
			send(config.packetSize, buf, new TIntHashSet[]{});
		}
	}
	
	void invalidate(final TIntHashSet[] invalidations)
	{
		final int length;
		{
			int pos = 0;
			for(final TIntHashSet invalidation : invalidations)
				if(invalidation!=null)
					pos += 2 + invalidation.size();
			length = PROLOG_SIZE + 8 + (pos << 2);
		}
		final byte[] buf = new byte[Math.min(length, config.packetSize)];
		System.arraycopy(prolog, 0, buf, 0, PROLOG_SIZE);
		
		int typeIdTransiently = 0;
		TIntIterator i = null;
		packetLoop: do
		{
			int pos = PROLOG_SIZE;
			
			pos = marshal(pos, buf, ClusterConfig.KIND_INVALIDATE);
			pos = marshal(pos, buf, invalidationSequence.next());
			
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
					if(pos>=config.packetSize)
					{
						send(pos, buf, invalidations);
						continue packetLoop;
					}
					pos = marshal(pos, buf, typeIdTransiently);
					
					if(i==null)
						i = invalidation.iterator();
					while(i.hasNext())
					{
						if(pos>=config.packetSize)
						{
							send(pos, buf, invalidations);
							continue packetLoop;
						}
						pos = marshal(pos, buf, i.next());
					}
					
					if(pos>=config.packetSize)
					{
						send(pos, buf, invalidations);
						continue packetLoop;
					}
					pos = marshal(pos, buf, PK.NaPK);
					
					i = null;
				}
			}
			
			send(pos, buf, invalidations);
			break;
		}
		while(true);
	}
	
	private void send(final int length, final byte[] buf, final TIntHashSet[] invalidations)
	{
		if(testSink!=null)
		{
			final byte[] bufCopy = new byte[length];
			System.arraycopy(buf, 0, bufCopy, 0, length);
			testSink.add(bufCopy);
		}
		else
		{
			try
			{
				final DatagramPacket packet = new DatagramPacket(buf, length, config.group, destinationPort);
				socket.send(packet);
				if(log)
					System.out.println("COPE Cluster Sender sent (" + buf.length + "): " + ClusterConfig.toString(invalidations));
	      }
			catch(IOException e)
			{
				throw new RuntimeException(e);
			}
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
	
	private static final class Sequence
	{
		private int count = 0;
		private final Object lock = new Object();
		
		Sequence()
		{
			// just make constructor package private
		}
		
		int next()
		{
			synchronized(lock)
			{
				return count++;
			}
		}
	}
}
