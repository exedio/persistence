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

import gnu.trove.TLongHashSet;
import java.util.ArrayList;

final class Cluster
{
	final ClusterProperties properties;
	final ClusterSenderMulticast sender;
	final ClusterListenerMulticast listener;

	Cluster(
			final ModelMetrics metricsTemplate,
			final Types types,
			final ClusterProperties properties,
			final Connect connect)
	{
		final ModelMetrics metrics = metricsTemplate.name(Cluster.class);
		this.properties = properties;
		this.sender   = new ClusterSenderMulticast(properties, metrics);
		this.listener = new ClusterListenerMulticast(properties, metrics, sender, types.concreteTypeCount, connect);
		metrics.gaugeConnect(c -> c.cluster.properties.listenThreads.initial, "listenThreads", "ClusterProperties#listenThreads#initial"); // TODO should expose the actual number of active threads
		metrics.gaugeConnect(c -> c.cluster.properties.listenThreads.priorityForGauge, "listenThreadPriority", "ClusterProperties#listenThreads#priority"); // TODO should expose the actual priority of threads
		metrics.gaugeConnect(c -> c.cluster.properties.packetSize, "packetSize", "ClusterProperties#packetSize");
	}

	void sendInvalidate(final TLongHashSet[] invalidations)
	{
		sender.invalidate(invalidations);
	}

	void sendPing(final int count)
	{
		sender.ping(count);
	}

	void startClose()
	{
		sender.close();
		listener.startClose();
	}

	void joinClose()
	{
		listener.joinClose();
	}

	void addThreadControllers(final ArrayList<ThreadController> result)
	{
		listener.addThreadControllers(result);
	}

	ClusterSenderInfo getSenderInfo()
	{
		return sender.getInfo();
	}

	ClusterListenerInfo getListenerInfo()
	{
		return listener.getInfo();
	}
}
