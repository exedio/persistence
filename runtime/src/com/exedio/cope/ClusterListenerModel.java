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

abstract class ClusterListenerModel extends ClusterListener
{
	private final ClusterSender sender;
	private final Connect connect;

	ClusterListenerModel(
			final ClusterProperties properties,
			final String modelName,
			final ClusterSender sender,
			final int typeLength, final Connect connect)
	{
		super(properties, modelName, typeLength);
		this.sender = sender;
		this.connect = connect;
	}

	@Override
	final void invalidate(final TLongHashSet[] invalidations, final TransactionInfoRemote info)
	{
		connect.invalidate(invalidations, info);
	}

	@Override
	final void pong(final long pingNanos, final int pingNode)
	{
		sender.pong(pingNanos, pingNode);
	}
}
