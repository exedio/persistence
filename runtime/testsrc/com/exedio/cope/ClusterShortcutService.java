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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import gnu.trove.TLongHashSet;
import io.micrometer.core.instrument.Counter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import org.opentest4j.AssertionFailedError;

final class ClusterShortcutService extends ClusterService
{
	private static final LinkedHashSet<ClusterShortcutService> instances = new LinkedHashSet<>();

	private final ClusterServiceParameters parameters;
	private final int instanceNumber;

	ClusterShortcutService(final ClusterServiceParameters parameters)
	{
		super(null);
		assertNotNull(parameters);
		assertNotNull(parameters.metricsTemplate);
		assertEquals(1, parameters.concreteTypeCount);
		assertNotNull(parameters.connect);
		this.parameters = parameters;
		this.instanceNumber = instances.size();
		//noinspection ThisEscapedInObjectConstruction
		assertEquals(true, instances.add(this));
	}

	@Override
	void sendInvalidate(final TLongHashSet[] invalidations)
	{
		for(final ClusterShortcutService instance : instances)
			if(instance!=this)
				instance.parameters.connect.invalidate(
						invalidations, new TransactionInfoRemote(instanceNumber));
	}

	@Override
	void sendPing(final int count)
	{
		throw new AssertionFailedError();
	}

	@Override
	void startClose()
	{
		assertEquals(true, instances.remove(this));
	}

	@Override
	void joinClose()
	{
	}

	@Override
	void addThreadControllers(final ArrayList<ThreadController> result)
	{
		throw new AssertionFailedError();
	}

	@Override
	ClusterSenderInfo getSenderInfo()
	{
		return new ClusterSenderInfo(
				instanceNumber,
				5555, // getLocalPort(),
				6666, // getSendBufferSize(),
				7777, // getTrafficClass(),
				new Counter() // invalidationSplit
				{
					@Override
					public void increment(final double amount)
					{
						throw new AssertionFailedError();
					}
					@Override
					public double count()
					{
						return 8888;
					}
					@Override
					public Id getId()
					{
						throw new AssertionFailedError();
					}
				});
	}

	@Override
	ClusterListenerInfo getListenerInfo()
	{
		throw new AssertionFailedError();
	}
}
