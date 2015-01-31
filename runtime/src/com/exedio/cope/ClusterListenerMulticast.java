/*
 * Copyright (C) 2004-2015  exedio GmbH (www.exedio.com)
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

import com.exedio.cope.util.Hex;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.text.MessageFormat;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class ClusterListenerMulticast extends ClusterListenerModel implements Runnable
{
	private static final Logger logger = LoggerFactory.getLogger(ClusterListenerMulticast.class);

	private final int packetSize;
	private final InetAddress address;
	private final DatagramSocket socket;
	private final int receiveBufferSize;

	private final ThreadSwarm threads;
	private volatile boolean threadRun = true;

	ClusterListenerMulticast(
			final ClusterProperties properties,
			final String modelName,
			final ClusterSender sender,
			final int typeLength, final Connect connect)
	{
		super(properties, sender, typeLength, connect);
		this.packetSize = properties.packetSize;
		this.address = properties.listenAddress;
		this.socket = properties.newListenSocket();
		try
		{
			this.receiveBufferSize = socket.getReceiveBufferSize();
		}
		catch(final SocketException e)
		{
			throw new RuntimeException(e);
		}

		this.threads = new ThreadSwarm(
				this,
				"COPE Cluster Listener " + modelName + ' ' + ClusterSenderInfo.toStringNodeID(properties.node),
				properties.getListenThreadsMax()
		);
		properties.setListenPriority(threads);
		threads.start(properties.getListenThreads());
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
					exception.inc();
					logger.error("fail", e);
				}
				else
				{
					if(logger.isInfoEnabled())
					{
						final Thread t = Thread.currentThread();
						logger.info("{} ({}) gracefully terminates: {}", new Object[]{t.getName(), t.getId(), e.getMessage()});
					}
				}
			}
			catch(final Exception e)
			{
				onListenFailure(e, packet);
			}
			catch(final AssertionError e)
			{
				onListenFailure(e, packet);
			}
		}
		logTerminate();
	}

	private void onListenFailure(final Throwable throwable, final DatagramPacket packet)
	{
		exception.inc();
		if(logger.isErrorEnabled())
			logger.error(MessageFormat.format("ClusterListenerMulticast {0}", Hex.encodeLower(packet.getData(), packet.getOffset(), packet.getLength()) ),
					throwable );
	}

	private static void logTerminate()
	{
		if(logger.isInfoEnabled())
		{
			final Thread t = Thread.currentThread();
			logger.info("{} ({}) terminates.", t.getName(), t.getId());
		}
	}

	@Override
	int getReceiveBufferSize()
	{
		return receiveBufferSize;
	}

	void addThreadControllers(final ArrayList<ThreadController> list)
	{
		threads.addThreadControllers(list);
	}

	void startClose()
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
	}

	void joinClose()
	{
		threads.join();
	}
}
