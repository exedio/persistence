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

import com.exedio.cope.util.Properties;

final class ClusterConfig
{
	static final byte MAGIC0 = (byte)0xc0;
	static final byte MAGIC1 = (byte)0xbe;
	static final byte MAGIC2 = 0x11;
	static final byte MAGIC3 = 0x11;

	static final int KIND_PING       = 0x00110001;
	static final int KIND_PONG       = 0x00110002;
	static final int KIND_INVALIDATE = 0x00120001;

	final ClusterProperties properties;
	final int node;

	static ClusterConfig get(final ConnectProperties properties)
	{
		final Properties.Source context = properties.getContext();
		final ClusterProperties clusterProperties = new ClusterProperties(context);
		if(!clusterProperties.isEnabled())
			return null;

		return new ClusterConfig(clusterProperties.createNode(), clusterProperties);
	}

	ClusterConfig(final int node, final ClusterProperties properties)
	{
		this.properties = properties;
		this.node = node;
		if(properties.log.booleanValue())
			System.out.println("COPE Cluster Network node id: " + node);
	}
}
