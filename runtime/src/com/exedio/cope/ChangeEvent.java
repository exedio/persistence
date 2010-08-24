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

import java.util.Collection;
import java.util.Date;
import java.util.List;

public final class ChangeEvent
{
	private final List<Item> items;
	private final TransactionInfo transactionInfo;

	ChangeEvent(final List<Item> items, final TransactionInfo transactionInfo)
	{
		this.items = items;
		this.transactionInfo = transactionInfo;
	}

	public Collection<Item> getItems()
	{
		return items;
	}

	public boolean isRemote()
	{
		return transactionInfo.isRemote();
	}

	/**
	 * @see ClusterSenderInfo#getNodeID()
	 */
	public int getRemoteNodeID()
	{
		return transactionInfo.getRemoteNodeID();
	}

	/**
	 * @throws IllegalStateException is that information is not available
	 * @see Transaction#getID()
	 */
	public long getTransactionID()
	{
		return transactionInfo.getID();
	}

	/**
	 * @throws IllegalStateException is that information is not available
	 * @see Transaction#getName()
	 */
	public String getTransactionName()
	{
		return transactionInfo.getName();
	}

	/**
	 * @throws IllegalStateException is that information is not available
	 * @see Transaction#getStartDate()
	 */
	public Date getTransactionStartDate()
	{
		return transactionInfo.getStartDate();
	}

	// ------------------- deprecated stuff -------------------

	/**
	 * @deprecated Use {@link #getRemoteNodeID()} instead
	 */
	@Deprecated
	public int getOriginClusterNodeID()
	{
		return getRemoteNodeID();
	}
}
