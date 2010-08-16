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

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.net.SocketException;

final class ClusterListenerMulticast extends ClusterListenerModel implements Runnable
{
	private final ClusterConfig config;
	private final boolean log;
	private final int port;
	private final MulticastSocket socket;

	private final Thread thread;
	private volatile boolean threadRun = true;

	ClusterListenerMulticast(
			final ClusterConfig config,
			final ClusterSender sender,
			final int typeLength, final Connect connect)
	{
		super(config, sender, typeLength, connect);
		this.config = config;
		this.log = config.log;
		this.port = config.properties.listenPort.intValue();
		try
		{
			this.socket = new MulticastSocket(port);
			socket.joinGroup(config.group);
		}
		catch(final IOException e)
		{
			throw new RuntimeException(e);
		}
		thread = new Thread(this);
		thread.setName("COPE Cluster Listener");
		thread.setDaemon(true);
		config.properties.setListenPriority(thread);
		thread.start();
		if(log)
			System.out.println("COPE Cluster Listener Multicast thread " + thread.getId() + " started.");
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
				{
					logTerminate();
					return;
				}

				socket.receive(packet);

				if(!threadRun)
				{
					logTerminate();
					return;
				}

				handle(packet);
	      }
			catch(final SocketException e)
			{
				if(threadRun)
				{
					exception++;
					e.printStackTrace();
				}
				else
				{
					if(log)
						System.out.println("COPE Cluster Listener Multicast thread " + thread.getId() + " gracefully terminates: " + e.getMessage());
				}
			}
			catch(final Exception e)
			{
				exception++;
				e.printStackTrace();
			}
		}
		logTerminate();
	}

	private void logTerminate()
	{
		if(log)
			System.out.println("COPE Cluster Listener Multicast thread " + thread.getId() + " terminates.");
	}

	@Override
	void close()
	{
		threadRun = false;
		try
		{
			socket.leaveGroup(config.group);
		}
		catch(final IOException e)
		{
			throw new RuntimeException(e);
		}
		socket.close();
		try
		{
			thread.join();
		}
		catch(final InterruptedException e)
		{
			throw new RuntimeException(e);
		}
	}
}
