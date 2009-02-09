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
import gnu.trove.TIntObjectHashMap;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.util.ArrayList;

import com.exedio.cope.util.ClusterListenerInfo;

final class ClusterListener implements Runnable
{
	private final ClusterConfig config;
	private final boolean log;
	private final int port;
	private final MulticastSocket socket;
	
	private final ClusterSender sender;
	private final int typeLength;
	private final ItemCache itemCache;
	private final QueryCache queryCache;
	
	private final Thread thread;
	private volatile boolean threadRun = true;
	
	ArrayList<Object> testSink = null;
	
	ClusterListener(
			final ClusterConfig config, final ConnectProperties properties,
			final ClusterSender sender,
			final int typeLength, final ItemCache itemCache, final QueryCache queryCache)
	{
		this.config = config;
		this.log = config.log;
		this.port = properties.clusterListenPort.getIntValue();
		try
		{
			this.socket = new MulticastSocket(port);
			socket.joinGroup(config.group);
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
		final byte[] buf = new byte[config.packetSize];
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
				handle(packet);
	      }
			catch(SocketException e)
			{
				if(threadRun)
					e.printStackTrace();
				else
				{
					if(log)
						System.out.println("COPE Cluster Invalidation Listener shutdown with message: " + e.getMessage());
				}
			}
			catch(Exception e)
			{
				exception++;
				e.printStackTrace();
			}
		}
	}
	
	void handle(final DatagramPacket packet)
	{
		int pos = packet.getOffset();
		final byte[] buf = packet.getData();
		final int length = packet.getLength();
		
		if(buf[pos++]!=ClusterConfig.MAGIC0 ||
			buf[pos++]!=ClusterConfig.MAGIC1 ||
			buf[pos++]!=ClusterConfig.MAGIC2 ||
			buf[pos++]!=ClusterConfig.MAGIC3)
		{
			missingMagic++;
			return;
		}
		
		if(config.secret!=unmarshal(pos, buf))
		{
			wrongSecret++;
			return;
		}
		pos += 4;
		
		final int node = unmarshal(pos, buf);
		if(config.node==node)
		{
			fromMyself++;
			
			if(testSink==null && log)
				System.out.println("COPE Cluster Invalidation received from " + packet.getAddress() + " is from myself.");
			return;
		}
		pos += 4;

		// sequence
		final int sequence = unmarshal(pos, buf);
		pos += 4;
		switch(sequence)
		{
			case ClusterConfig.PING_AT_SEQUENCE:
			case ClusterConfig.PONG_AT_SEQUENCE:
				final String m = (sequence==ClusterConfig.PING_AT_SEQUENCE) ? "invalid ping" : "invalid pong";
				
				if(length!=config.packetSize)
					throw new RuntimeException(m + ", expected length " + config.packetSize + ", but was " + length);
				for(; pos<length; pos++)
				{
					if(config.pingPayload[pos]!=buf[pos])
						throw new RuntimeException(m + ", at position " + pos + " expected " + config.pingPayload[pos] + ", but was " + buf[pos]);
				}
				
				node(node).pingPong(sequence==ClusterConfig.PING_AT_SEQUENCE, packet.getAddress(), packet.getPort());
				
				if(testSink!=null)
				{
					testSink.add(sequence);
				}
				else
				{
					switch(sequence)
					{
						case ClusterConfig.PING_AT_SEQUENCE:
							if(log)
								System.out.println("COPE Cluster Invalidation PING received from " + packet.getAddress());
							sender.pong();
							break;
						case ClusterConfig.PONG_AT_SEQUENCE:
							if(log)
								System.out.println("COPE Cluster Invalidation PONG received from " + packet.getAddress());
							break;
						default:
							throw new RuntimeException(String.valueOf(sequence));
					}
				}
				break;
			
			default:
				final TIntHashSet[] invalidations = handleInvalidation(pos, buf, length, sequence);
				if(testSink!=null)
				{
					testSink.add(invalidations);
				}
				else
				{
					if(log)
						System.out.println("COPE Cluster Invalidation received from " + packet.getAddress() + ": " + ClusterConfig.toString(invalidations));
					itemCache.invalidate(invalidations);
					queryCache.invalidate(invalidations);
				}
				break;
		}
	}
	
	TIntHashSet[] handleInvalidation(int pos, final byte[] buf, final int length, final int sequence)
	{
		if(log)
			System.out.println("COPE Cluster Invalidation received sequence " + sequence);
		
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
			socket.leaveGroup(config.group);
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
	
	// info
	
	private volatile long exception = 0;
	private volatile long missingMagic = 0;
	private volatile long wrongSecret = 0;
	private volatile long fromMyself = 0;
	private final TIntObjectHashMap<Node> nodes = new TIntObjectHashMap<Node>();
	
	private static class Node
	{
		final int id;
		volatile InetAddress address = null;
		volatile int port = -1;
		volatile long ping = 0;
		volatile long pong = 0;
		
		Node(final int id, final boolean log)
		{
			this.id = id;
			if(log)
				System.out.println("COPE Cluster Invalidation learned about node " + id);
		}
		
		void pingPong(final boolean ping, final InetAddress address, final int port)
		{
			this.address = address;
			this.port = port;
			
			if(ping)
				this.ping++;
			else
				this.pong++;
		}
		
		ClusterListenerInfo.Node getInfo()
		{
			return new ClusterListenerInfo.Node(id, address, port, ping, pong);
		}
	}
	
	Node node(final int id)
	{
		synchronized(nodes)
		{
			Node result = nodes.get(id);
			if(result!=null)
				return result;
			
			nodes.put(id, result = new Node(id, log));
			return result;
		}
	}
	
	ClusterListenerInfo getInfo()
	{
		final Node[] ns;
		synchronized(nodes)
		{
			ns = nodes.getValues(new Node[nodes.size()]);
		}
		final ArrayList<ClusterListenerInfo.Node> infoNodes = new ArrayList<ClusterListenerInfo.Node>(ns.length);
		for(final Node n : ns)
			infoNodes.add(n.getInfo());
		
		return new ClusterListenerInfo(exception, missingMagic, wrongSecret, fromMyself, infoNodes);
	}
}
