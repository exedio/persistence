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
import java.util.Arrays;

final class InvalidationListener extends InvalidationEndpoint implements Runnable
{
	private final int port;
	private final MulticastSocket socket;
	
	private final int typeLength;
	private final ItemCache itemCache;
	private final QueryCache queryCache;
	
	private final Thread thread;
	private volatile boolean threadRun = true;
	
	InvalidationListener(
			final int secret, final ConnectProperties properties,
			final int typeLength, final ItemCache itemCache, final QueryCache queryCache)
	{
		super(secret, properties);
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
		this.typeLength = typeLength;
		this.itemCache = itemCache;
		this.queryCache = queryCache;
		thread = new Thread(this);
		thread.start();
	}
	
	public void run()
	{
		final byte[] buf = new byte[BUFFER_SIZE];
		final DatagramPacket packet = new DatagramPacket(buf, buf.length);
		
		while(threadRun)
		{
			try
			{
				System.out.println("--r----");
				if(!threadRun)
					return;
				socket.receive(packet);
				if(!threadRun)
					return;
				final TIntHashSet[] invalidations =
					unmarshal(packet.getOffset(), packet.getData(), packet.getLength(), secret, typeLength);
				System.out.println("COPE Cluster Invalidation received from " + packet.getSocketAddress() + ": " + Arrays.asList(invalidations));
				itemCache.invalidate(invalidations);
				queryCache.invalidate(invalidations);
	      }
			catch(Exception e)
			{
				// TODO count and display in console
				e.printStackTrace();
			}
		}
	}
	
	static TIntHashSet[] unmarshal(int pos, final byte[] buf, final int length, final int secret, final int typeLength)
	{
		if(buf[pos++]!=MAGIC0 ||
			buf[pos++]!=MAGIC1 ||
			buf[pos++]!=MAGIC2 ||
			buf[pos++]!=MAGIC3)
			throw new RuntimeException("missing magic");
		
		if(secret!=unmarshal(pos, buf))
			throw new RuntimeException("wrong secret");
		pos += 4;
		
		final TIntHashSet[] result = new TIntHashSet[typeLength];
		while(pos<length)
		{
			final int typeIdTransiently = unmarshal(pos, buf);
			pos += 4;
			
			final int pkLength = unmarshal(pos, buf);
			pos += 4;
			
			if(pkLength>0)
			{
				final TIntHashSet set = new TIntHashSet();
				for(int i = 0; i<pkLength; i++)
				{
					set.add(unmarshal(pos, buf));
					pos += 4;
				}
				result[typeIdTransiently] = set;
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
