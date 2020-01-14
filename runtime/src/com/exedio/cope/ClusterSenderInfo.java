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

import static com.exedio.cope.InfoRegistry.count;

import io.micrometer.core.instrument.Counter;

public final class ClusterSenderInfo
{
	private final int nodeID;
	private final int localPort;
	private final int sendBufferSize;
	private final int trafficClass;
	private final long invalidationSplit;

	ClusterSenderInfo(
			final int nodeID,
			final int localPort,
			final int sendBufferSize,
			final int trafficClass,
			final Counter invalidationSplit)
	{
		this.nodeID = nodeID;
		this.localPort = localPort;
		this.sendBufferSize = sendBufferSize;
		this.trafficClass = trafficClass;
		this.invalidationSplit = count(invalidationSplit);
	}

	/**
	 * @see #getNodeIDString()
	 * @see ClusterListenerInfo.Node#getID()
	 */
	public int getNodeID()
	{
		return nodeID;
	}

	/**
	 * @see #getNodeID()
	 * @see ClusterListenerInfo.Node#getIDString()
	 */
	public String getNodeIDString()
	{
		return toStringNodeID(nodeID);
	}

	public int getLocalPort()
	{
		return localPort;
	}

	public int getSendBufferSize()
	{
		return sendBufferSize;
	}

	public int getTrafficClass()
	{
		return trafficClass;
	}

	public long getInvalidationSplit()
	{
		return invalidationSplit;
	}

	public static String toStringNodeID(final int nodeID)
	{
		return Integer.toHexString(nodeID);
	}
}
