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

final class ClusterNoneService extends ClusterService
{
	@Override
	void sendInvalidate(final TLongHashSet[] invalidations)
	{
	}

	@Override
	void sendPing(final int count)
	{
		throw new IllegalStateException("cluster network not enabled");
	}

	@Override
	void startClose()
	{
	}

	@Override
	void joinClose()
	{
	}

	@Override
	void addThreadControllers(final ArrayList<ThreadController> result)
	{
	}

	@Override
	ClusterSenderInfo getSenderInfo()
	{
		return null;
	}

	@Override
	ClusterListenerInfo getListenerInfo()
	{
		return null;
	}


	private ClusterNoneService(
			@SuppressWarnings("unused") final ClusterServiceParameters parameters)
	{
		super(null);
	}
}
