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
import java.util.Date;

import com.exedio.cope.util.ClusterListenerInfo;
import com.exedio.cope.util.SequenceChecker;

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
				{
					exception++;
					e.printStackTrace();
				}
				else
				{
					if(log)
						System.out.println("COPE Cluster Listener graceful shutdown: " + e.getMessage());
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
			return;
		}
		pos += 4;

		// kind
		final int kind = unmarshal(pos, buf);
		pos += 4;
		switch(kind)
		{
			case ClusterConfig.KIND_PING:
			case ClusterConfig.KIND_PONG:
			{
				final boolean ping = (kind==ClusterConfig.KIND_PING);
				
				final int sequence = unmarshal(pos, buf);
				pos += 4;
				
				if(length!=config.packetSize)
					throw new RuntimeException("invalid " + (ping?"ping":"pong") + ", expected length " + config.packetSize + ", but was " + length);
				final byte[] pingPayload = config.pingPayload;
				for(; pos<length; pos++)
				{
					if(pingPayload[pos]!=buf[pos])
						throw new RuntimeException("invalid " + (ping?"ping":"pong") + ", at position " + pos + " expected " + pingPayload[pos] + ", but was " + buf[pos]);
				}
				
				if(node(node, packet).pingPong(ping, sequence))
				{
					if(log)
						System.out.println("COPE Cluster Listener " + (ping?"ping":"pong") + " duplicate " + sequence + " from " + packet.getAddress());
					break;
				}
				
				if(log)
					System.out.println("COPE Cluster Listener " + (ping?"ping":"pong") + " from " + packet.getAddress());
				
				if(testSink!=null)
				{
					testSink.add(ping ? "PING" : "PONG");
				}
				else
				{
					if(ping)
						sender.pong();
				}
				break;
			}
			case ClusterConfig.KIND_INVALIDATE:
			{
				final int sequence = unmarshal(pos, buf);
				pos += 4;
				if(node(node, packet).invalidate(sequence))
				{
					if(log)
						System.out.println("COPE Cluster Listener invalidate duplicate " + sequence + " from " + packet.getAddress());
					break;
				}
			
				final TIntHashSet[] invalidations = new TIntHashSet[typeLength];
				outer: while(pos<length)
				{
					final int typeIdTransiently = unmarshal(pos, buf);
					pos += 4;
					
					final TIntHashSet set = new TIntHashSet();
					invalidations[typeIdTransiently] = set;
					inner: while(true)
					{
						if(pos>=length)
							break outer;
						
						final int pk = unmarshal(pos, buf);
						pos += 4;
						
						if(pk==PK.NaPK)
							break inner;
						
						set.add(pk);
					}
				}
				
				if(testSink!=null)
				{
					testSink.add(invalidations);
				}
				else
				{
					itemCache.invalidate(invalidations);
					queryCache.invalidate(invalidations);
				}
				break;
			}
			default:
				throw new RuntimeException("illegal kind: " + kind);
		}
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
		final long firstEncounter;
		final InetAddress address;
		final int port;
		final SequenceChecker pingSequenceChecker;
		final SequenceChecker pongSequenceChecker;
		final SequenceChecker invalidateSequenceChecker;
		
		Node(final int id, final DatagramPacket packet, final boolean log)
		{
			this.id = id;
			this.firstEncounter = System.currentTimeMillis();
			this.address = packet.getAddress();
			this.port = packet.getPort();
			this.pingSequenceChecker = new SequenceChecker(200);
			this.pongSequenceChecker = new SequenceChecker(200);
			this.invalidateSequenceChecker = new SequenceChecker(200);
			if(log)
				System.out.println("COPE Cluster Listener encountered new node " + id);
		}
		
		boolean pingPong(final boolean ping, final int sequence)
		{
			return (ping ? pingSequenceChecker : pongSequenceChecker).check(sequence);
		}
		
		boolean invalidate(final int sequence)
		{
			return invalidateSequenceChecker.check(sequence);
		}
		
		ClusterListenerInfo.Node getInfo()
		{
			return new ClusterListenerInfo.Node(
					id,
					new Date(firstEncounter), 
					address, port,
					pingSequenceChecker.getCounter(),
					pongSequenceChecker.getCounter(),
					invalidateSequenceChecker.getCounter());
		}
	}
	
	Node node(final int id, final DatagramPacket packet)
	{
		synchronized(nodes)
		{
			Node result = nodes.get(id);
			if(result!=null)
				return result;
			
			nodes.put(id, result = new Node(id, packet, log));
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
