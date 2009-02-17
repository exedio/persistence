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

package com.exedio.cope.util;

import java.net.InetAddress;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public final class ClusterListenerInfo
{
	private final long exception;
	private final long missingMagic;
	private final long wrongSecret;
	private final long fromMyself;
	private final List<Node> nodes;
	
	public ClusterListenerInfo(
			final long exception,
			final long missingMagic,
			final long wrongSecret,
			final long fromMyself,
			final List<Node> nodes)
	{
		this.exception = exception;
		this.missingMagic = missingMagic;
		this.wrongSecret = wrongSecret;
		this.fromMyself = fromMyself;
		this.nodes = Collections.unmodifiableList(nodes);
	}
	
	public long getException()
	{
		return exception;
	}
	
	public long getMissingMagic()
	{
		return missingMagic;
	}
	
	public long getWrongSecret()
	{
		return wrongSecret;
	}
	
	public long getFromMyself()
	{
		return fromMyself;
	}
	
	public List<Node> getNodes()
	{
		return nodes;
	}
	
	public static final class Node
	{
		private final int id;
		final Date firstEncounter;
		private final InetAddress address;
		private final int port;
		private final long ping;
		private final Date pingLast;
		private final long pong;
		private final Date pongLast;
		private final SequenceChecker.Counter pingPongCounter;
		private final SequenceChecker.Counter invalidateCounter;
		
		public Node(
				final int id,
				final Date firstEncounter,
				final InetAddress address,
				final int port,
				final long ping,
				final Date pingLast,
				final long pong,
				final Date pongLast,
				final SequenceChecker.Counter pingPongCounter,
				final SequenceChecker.Counter invalidateCounter)
		{
			this.id = id;
			this.firstEncounter = firstEncounter;
			this.address = address;
			this.port = port;
			this.ping = ping;
			this.pingLast = pingLast;
			this.pong = pong;
			this.pongLast = pongLast;
			this.pingPongCounter = pingPongCounter;
			this.invalidateCounter = invalidateCounter;
			
			if(ping<0)
				throw new IllegalArgumentException();
			if(pong<0)
				throw new IllegalArgumentException();
			if(pingPongCounter==null)
				throw new NullPointerException();
			if(invalidateCounter==null)
				throw new NullPointerException();
		}
		
		public int getID()
		{
			return id;
		}
		
		public Date getFirstEncounter()
		{
			return firstEncounter;
		}
		
		public InetAddress getAddress()
		{
			return address;
		}
		
		public int getPort()
		{
			return port;
		}
		
		public long getPing()
		{
			return ping;
		}
		
		public Date getPingLast()
		{
			return pingLast;
		}
		
		public long getPong()
		{
			return pong;
		}

		public Date getPongLast()
		{
			return pongLast;
		}
		
		public SequenceChecker.Counter getPingPongCounter()
		{
			return pingPongCounter;
		}
		
		public SequenceChecker.Counter getInvalidateCounter()
		{
			return invalidateCounter;
		}
		
		@Override
		public String toString()
		{
			return String.valueOf(id) + '/' + ping + '/' + pong;
		}
	}
}
