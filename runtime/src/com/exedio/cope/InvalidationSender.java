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

final class InvalidationSender extends InvalidationEndpoint
{
	private final int sourcePort;
	private final int destinationPort;
	private final DatagramSocket socket;
	
	private static final int PROLOG_SIZE = 12;
	private final byte[] prolog;
	
	private int sequenceCount = 0;
	private final Object sequenceLock = new Object();
	
	ArrayList<byte[]> testSink = null;
	
	InvalidationSender(final int secret, final int node, final ConnectProperties properties)
	{
		super(secret, node, properties);
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
		prolog[0] = MAGIC0;
		prolog[1] = MAGIC1;
		prolog[2] = MAGIC2;
		prolog[3] = MAGIC3;
		int pos = 4;
		pos = marshal(pos, prolog, secret);
		pos = marshal(pos, prolog, node);
		assert pos==PROLOG_SIZE;
		this.prolog = prolog;
	}
	
	void ping()
	{
		pingPong(PING_AT_SEQUENCE);
	}
	
	void pong()
	{
		pingPong(PONG_AT_SEQUENCE);
	}
	
	private void pingPong(final int messageAtSequence)
	{
		assert messageAtSequence<0 : messageAtSequence;
		
		final byte[] buf = new byte[packetSize];
		System.arraycopy(prolog, 0, buf, 0, PROLOG_SIZE);
		
		int pos = PROLOG_SIZE;
		pos = marshal(pos, buf, messageAtSequence);
			
		while(pos>=packetSize)
			pos = marshal(pos, buf, pos);
			
		send(pos, buf, new TIntHashSet[]{});
	}
	
	private int nextSequence()
	{
		synchronized(sequenceLock)
		{
			return sequenceCount++;
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
			length = PROLOG_SIZE + 4 + (pos << 2);
		}
		final byte[] buf = new byte[Math.min(length, packetSize)];
		System.arraycopy(prolog, 0, buf, 0, PROLOG_SIZE);
		
		int typeIdTransiently = 0;
		TIntIterator i = null;
		packetLoop: do
		{
			int pos = PROLOG_SIZE;
			
			pos = marshal(pos, buf, nextSequence());
			
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
						send(pos, buf, invalidations);
						continue packetLoop;
					}
					pos = marshal(pos, buf, typeIdTransiently);
					
					if(i==null)
						i = invalidation.iterator();
					while(i.hasNext())
					{
						if(pos>=packetSize)
						{
							send(pos, buf, invalidations);
							continue packetLoop;
						}
						pos = marshal(pos, buf, i.next());
					}
					
					if(pos>=packetSize)
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
				final DatagramPacket packet = new DatagramPacket(buf, length, group, destinationPort);
				final long start = System.currentTimeMillis();
				socket.send(packet);
				System.out.println("COPE Cluster Invalidation sent (" + buf.length + ',' + (System.currentTimeMillis()-start) + "ms): " + toString(invalidations));
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
}
