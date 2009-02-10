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
		private final int invalidateInOrder;
		private final int invalidateOutOfOrder;
		private final int invalidateDuplicate;
		private final int invalidateLost;
		private final int invalidateLate;
		
		public Node(
				final int id,
				final Date firstEncounter,
				final InetAddress address,
				final int port,
				final long ping,
				final Date pingLast,
				final long pong,
				final Date pongLast,
				final int invalidateInOrder,
				final int invalidateOutOfOrder,
				final int invalidateDuplicate,
				final int invalidateLost,
				final int invalidateLate)
		{
			this.id = id;
			this.firstEncounter = firstEncounter;
			this.address = address;
			this.port = port;
			this.ping = ping;
			this.pingLast = pingLast;
			this.pong = pong;
			this.pongLast = pongLast;
			this.invalidateInOrder    = invalidateInOrder;
			this.invalidateOutOfOrder = invalidateOutOfOrder;
			this.invalidateDuplicate  = invalidateDuplicate;
			this.invalidateLost       = invalidateLost;
			this.invalidateLate       = invalidateLate;
			
			if(ping<0)
				throw new IllegalArgumentException();
			if(pong<0)
				throw new IllegalArgumentException();
			if(invalidateInOrder<0)
				throw new IllegalArgumentException();
			if(invalidateOutOfOrder<0)
				throw new IllegalArgumentException();
			if(invalidateDuplicate<0)
				throw new IllegalArgumentException();
			if(invalidateLost<0)
				throw new IllegalArgumentException();
			if(invalidateLate<0)
				throw new IllegalArgumentException();
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
		
		public int getInvalidateInOrder()
		{
			return invalidateInOrder;
		}

		public int getInvalidateOutOfOrder()
		{
			return invalidateOutOfOrder;
		}

		public int getInvalidateDuplicate()
		{
			return invalidateDuplicate;
		}

		public int getInvalidateLost()
		{
			return invalidateLost;
		}

		public int getInvalidateLate()
		{
			return invalidateLate;
		}
		
		@Override
		public String toString()
		{
			return String.valueOf(id) + '/' + ping + '/' + pong + '/' + invalidateInOrder + '/' + invalidateOutOfOrder;
		}
	}
}
