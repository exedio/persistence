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

final class ClusterListenerMock extends ClusterListener
{
	ArrayList<Object> testSink = null;
	private boolean closed = false;

	ClusterListenerMock(
			final ClusterProperties properties,
			final int typeLength)
	{
		super(properties, new ModelMetrics(null, "MOCK_MODEL_NAME").name(Cluster.class), typeLength);
	}

	@Override
	void invalidate(final TLongHashSet[] invalidations, final TransactionInfoRemote info)
	{
		testSink.add(invalidations);
	}

	@Override
	void pong(final long pingNanos, final int pingNode)
	{
		testSink.add("PONG(" + Long.toHexString(pingNanos) + "," + Long.toHexString(pingNode) + ")");
	}

	@Override
	int getReceiveBufferSize()
	{
		return 234567;
	}

	void close()
	{
		if(closed)
			throw new RuntimeException();
		closed = true;
	}
}
