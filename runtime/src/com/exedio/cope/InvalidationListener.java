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

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;

final class InvalidationListener extends InvalidationEndpoint implements Runnable
{
	private final int port;
	private final MulticastSocket socket;
	
	private final InvalidationSender sender;
	private final int typeLength;
	private final ItemCache itemCache;
	private final QueryCache queryCache;
	
	private final Thread thread;
	private volatile boolean threadRun = true;
	
	InvalidationListener(
			final int secret, final int node, final ConnectProperties properties,
			final InvalidationSender sender,
			final int typeLength, final ItemCache itemCache, final QueryCache queryCache)
	{
		super(secret, node, properties);
		this.port = properties.clusterListenPort.getIntValue();
		try
		{
			this.socket = new MulticastSocket(port);
			socket.joinGroup(group);
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}
		this.sender = sender;
		this.typeLength = typeLength;
		this.itemCache = itemCache;
		this.queryCache = queryCache;
		thread = new Thread(this);
		thread.start();
	}
	
	public void run()
	{
		final byte[] buf = new byte[packetSize];
		final DatagramPacket packet = new DatagramPacket(buf, buf.length);
		
		while(threadRun)
		{
			try
			{
				if(!threadRun)
					return;
				socket.receive(packet);
				if(!threadRun)
					return;
				final Object unmarshalled =
					unmarshal(packet.getOffset(), packet.getData(), packet.getLength(), secret, node, typeLength);
				if(unmarshalled instanceof TIntHashSet[])
				{
					final TIntHashSet[] invalidations = (TIntHashSet[])unmarshalled;
					System.out.println("COPE Cluster Invalidation received from " + packet.getSocketAddress() + ": " + toString(invalidations));
					itemCache.invalidate(invalidations);
					queryCache.invalidate(invalidations);
				}
				else if(unmarshalled==null)
				{
					System.out.println("COPE Cluster Invalidation received from " + packet.getSocketAddress() + " is from myself.");
				}
				else if(unmarshalled instanceof Integer)
				{
					switch(((Integer)unmarshalled).intValue())
					{
						case InvalidationSender.PING_AT_SEQUENCE:
							System.out.println("COPE Cluster Invalidation PING received from " + packet.getSocketAddress());
							sender.pong();
							break;
						case InvalidationSender.PONG_AT_SEQUENCE:
							System.out.println("COPE Cluster Invalidation PONG received from " + packet.getSocketAddress());
							break;
						default:
							throw new RuntimeException(String.valueOf(unmarshalled));
					}
				}
	      }
			catch(Exception e)
			{
				// TODO count and display in console
				e.printStackTrace();
			}
		}
	}
	
	static Object unmarshal(int pos, final byte[] buf, final int length, final int secret, final int node, final int typeLength)
	{
		if(buf[pos++]!=MAGIC0 ||
			buf[pos++]!=MAGIC1 ||
			buf[pos++]!=MAGIC2 ||
			buf[pos++]!=MAGIC3)
			throw new RuntimeException("missing magic");
		
		if(secret!=unmarshal(pos, buf))
			throw new RuntimeException("wrong secret");
		pos += 4;
		
		if(node==unmarshal(pos, buf))
			return null;
		pos += 4;

		// sequence
		final int sequence = unmarshal(pos, buf);
		switch(sequence)
		{
			case InvalidationSender.PING_AT_SEQUENCE:
			case InvalidationSender.PONG_AT_SEQUENCE:
				return sequence;
		}
		System.out.println("COPE Cluster Invalidation received sequence " + sequence);
		pos += 4;
		
		final TIntHashSet[] result = new TIntHashSet[typeLength];
		while(pos<length)
		{
			final int typeIdTransiently = unmarshal(pos, buf);
			pos += 4;
			
			final TIntHashSet set = new TIntHashSet();
			result[typeIdTransiently] = set;
			while(true)
			{
				if(pos>=length)
					return result;
				
				final int pk = unmarshal(pos, buf);
				pos += 4;
				
				if(pk==PK.NaPK)
					break;
				
				set.add(pk);
			}
		}
		return result;
	}
	
	static int unmarshal(int pos, final byte[] buf)
	{
		return
			((buf[pos++] & 0xff)    ) |
			((buf[pos++] & 0xff)<< 8) |
			((buf[pos++] & 0xff)<<16) |
			((buf[pos++] & 0xff)<<24) ;
	}
	
	void close()
	{
		threadRun = false;
		try
		{
			socket.leaveGroup(group);
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}
		socket.close();
		try
		{
			thread.join();
		}
		catch(InterruptedException e)
		{
			throw new RuntimeException(e);
		}
	}
}
