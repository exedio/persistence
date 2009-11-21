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

package com.exedio.cope.info;

import java.net.InetAddress;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.exedio.cope.util.SequenceChecker;

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
		final long firstEncounter;
		private final InetAddress address;
		private final int port;
		private final SequenceChecker.Info pingInfo;
		private final SequenceChecker.Info pongInfo;
		private final SequenceChecker.Info invalidateInfo;
		
		public Node(
				final int id,
				final Date firstEncounter,
				final InetAddress address,
				final int port,
				final SequenceChecker.Info pingInfo,
				final SequenceChecker.Info pongInfo,
				final SequenceChecker.Info invalidateInfo)
		{
			this.id = id;
			this.firstEncounter = firstEncounter.getTime();
			this.address = address;
			this.port = port;
			this.pingInfo = pingInfo;
			this.pongInfo = pongInfo;
			this.invalidateInfo = invalidateInfo;
			
			if(pingInfo==null)
				throw new NullPointerException();
			if(pongInfo==null)
				throw new NullPointerException();
			if(invalidateInfo==null)
				throw new NullPointerException();
		}
		
		public int getID()
		{
			return id;
		}
		
		public Date getFirstEncounter()
		{
			return new Date(firstEncounter);
		}
		
		public InetAddress getAddress()
		{
			return address;
		}
		
		public int getPort()
		{
			return port;
		}
		
		public SequenceChecker.Info getPingInfo()
		{
			return pingInfo;
		}
		
		public SequenceChecker.Info getPongInfo()
		{
			return pongInfo;
		}
		
		public SequenceChecker.Info getInvalidateInfo()
		{
			return invalidateInfo;
		}
		
		@Override
		public String toString()
		{
			return String.valueOf(id);
		}
	}
}
