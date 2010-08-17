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
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;

final class ClusterListenerMulticast extends ClusterListenerModel implements Runnable
{
	private final boolean log;
	private final int packetSize;
	private final InetAddress address;
	private final DatagramSocket socket;

	private final Thread[] threads;
	private volatile boolean threadRun = true;

	ClusterListenerMulticast(
			final ClusterProperties properties,
			final ClusterSender sender,
			final int typeLength, final Connect connect)
	{
		super(properties, sender, typeLength, connect);
		this.log = properties.log.booleanValue();
		this.packetSize = properties.packetSize;
		this.address = properties.listenAddress;
		this.socket = properties.getListenSocket();

		this.threads = new Thread[properties.getListenThreads()];
		for(int i = 0; i<threads.length; i++)
		{
			final Thread thread = new Thread(this);
			thread.setName("COPE Cluster Listener " + (i+1) + '/' + threads.length);
			thread.setDaemon(true);
			properties.setListenPriority(thread);
			threads[i] = thread;
		}
		for(final Thread thread : threads)
		{
			thread.start();
			if(log)
				System.out.println(thread.getName() + " (" + thread.getId() + ") started.");
		}
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
						{
							final Thread t = Thread.currentThread();
							System.out.println(t.getName() + " (" + t.getId() + ") gracefully terminates: " + e.getMessage());
						}
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
			{
				final Thread t = Thread.currentThread();
				System.out.println(t.getName() + " (" + t.getId() + ") terminates.");
			}
		}

	@Override
	void close()
	{
		threadRun = false;
		if(socket instanceof MulticastSocket)
		{
			try
			{
				((MulticastSocket)socket).leaveGroup(address);
			}
			catch(final IOException e)
			{
				throw new RuntimeException(e);
			}
		}
		socket.close();

		for(final Thread thread : threads)
		{
			try
			{
				thread.join();
			}
			catch(final InterruptedException e)
			{
				throw new RuntimeException(thread.getName() + '(' + thread.getId() + ')', e);
			}
		}
	}
}
