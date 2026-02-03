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

final class ClusterUdpService extends ClusterService
{
	final ClusterUdpProperties properties;
	final ClusterUdpSenderMulticast sender;
	final ClusterUdpListenerMulticast listener;

	ClusterUdpService(
			final ModelMetrics metricsTemplate,
			final Types types,
			final ClusterUdpProperties properties,
			final Connect connect)
	{
		super(properties);
		final ModelMetrics metrics = metricsTemplate.name(Cluster.class);
		this.properties = properties;
		this.sender   = new ClusterUdpSenderMulticast(properties, metrics);
		this.listener = new ClusterUdpListenerMulticast(properties, metrics, sender, types.concreteTypeCount, connect);
		metrics.gaugeConnect(c -> of(c).properties.listenThreads.initial, "listenThreads", "ClusterUdpProperties#listenThreads#initial"); // TODO should expose the actual number of active threads
		metrics.gaugeConnect(c -> of(c).properties.listenThreads.priorityForGauge, "listenThreadPriority", "ClusterUdpProperties#listenThreads#priority"); // TODO should expose the actual priority of threads
		metrics.gaugeConnect(c -> of(c).properties.packetSize, "packetSize", "ClusterUdpProperties#packetSize");
	}

	static ClusterUdpService of(final Connect connect)
	{
		return (ClusterUdpService)connect.cluster;
	}

	@Override
	void sendInvalidate(final TLongHashSet[] invalidations)
	{
		sender.invalidate(invalidations);
	}

	@Override
	void sendPing(final int count)
	{
		sender.ping(count);
	}

	@Override
	void startClose()
	{
		sender.close();
		listener.startClose();
	}

	@Override
	void joinClose()
	{
		listener.joinClose();
	}

	@Override
	void addThreadControllers(final ArrayList<ThreadController> result)
	{
		listener.addThreadControllers(result);
	}

	@Override
	ClusterSenderInfo getSenderInfo()
	{
		return sender.getInfo();
	}

	@Override
	ClusterListenerInfo getListenerInfo()
	{
		return listener.getInfo();
	}
}
