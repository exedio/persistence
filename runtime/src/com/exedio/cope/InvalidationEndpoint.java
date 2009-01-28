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
import gnu.trove.TIntIterator;

import java.net.InetAddress;
import java.net.UnknownHostException;

abstract class InvalidationEndpoint
{
	protected static final byte MAGIC0 = (byte)0xc0;
	protected static final byte MAGIC1 = (byte)0xbe;
	protected static final byte MAGIC2 = 0x11;
	protected static final byte MAGIC3 = 0x11;
	
	protected static final int PING_AT_SEQUENCE = -1;
	protected static final int PONG_AT_SEQUENCE = -2;
	
	protected final int secret;
	protected final int node;
	protected final int packetSize;
	protected final InetAddress group;
	
	protected InvalidationEndpoint(final int secret, final int node, final ConnectProperties properties)
	{
		this.secret = secret;
		this.node = node;
		this.packetSize = properties.clusterPacketSize.getIntValue() & (~3);
		try
		{
			this.group = InetAddress.getByName(properties.clusterGroup.getStringValue());
		}
		catch(UnknownHostException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	protected static final String toString(final TIntHashSet[] invalidations)
	{
		final StringBuilder bf = new StringBuilder();
		bf.append('[');
		boolean first = true;
		for(final TIntHashSet invalidation : invalidations)
		{
			if(first)
				first = false;
			else
				bf.append(", ");
			
			if(invalidation!=null)
			{
				bf.append('{');
				boolean first2 = true;
				for(final TIntIterator i = invalidation.iterator(); i.hasNext(); )
				{
					if(first2)
						first2 = false;
					else
						bf.append(',');
					
					bf.append(i.next());
				}
				bf.append('}');
			}
			else
				bf.append("null");
		}
		bf.append(']');
		
		return bf.toString();
	}
}
