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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

public final class ChangeEvent
{
	private final Item[] items;
	private final TransactionInfo transactionInfo;

	@SuppressWarnings("AssignmentToCollectionOrArrayFieldFromParameter")
	ChangeEvent(final Item[] items, final TransactionInfo transactionInfo)
	{
		this.items = items;
		this.transactionInfo = transactionInfo;

		assert items.length>0;
	}

	public Collection<Item> getItems()
	{
		return Collections.unmodifiableCollection(Arrays.asList(items));
	}

	/**
	 * @see #getRemoteNodeIDString()
	 */
	public String getNodeID()
	{
		return transactionInfo.getNodeID();
	}

	public boolean isRemote()
	{
		return transactionInfo.isRemote();
	}

	/**
	 * @throws NotAvailableException if that information is not available
	 * @see ClusterSenderInfo#getNodeID()
	 * @see #getRemoteNodeIDString()
	 */
	public int getRemoteNodeID() throws NotAvailableException
	{
		return transactionInfo.getRemoteNodeID();
	}

	/**
	 * @throws NotAvailableException if that information is not available
	 * @see ClusterSenderInfo#getNodeIDString()
	 * @see #getRemoteNodeID()
	 */
	public String getRemoteNodeIDString() throws NotAvailableException
	{
		return ClusterSenderInfo.toStringNodeID(transactionInfo.getRemoteNodeID());
	}

	/**
	 * @throws NotAvailableException if that information is not available
	 * @see Transaction#getID()
	 */
	public long getTransactionID() throws NotAvailableException
	{
		return transactionInfo.getID();
	}

	/**
	 * @throws NotAvailableException if that information is not available
	 * @see Transaction#getName()
	 */
	public String getTransactionName() throws NotAvailableException
	{
		return transactionInfo.getName();
	}

	/**
	 * @throws NotAvailableException if that information is not available
	 * @see Transaction#getStartDate()
	 */
	public Date getTransactionStartDate() throws NotAvailableException
	{
		return transactionInfo.getStartDate();
	}

	public static final class NotAvailableException extends Exception
	{
		private static final long serialVersionUID = 1l;

		NotAvailableException(final String message)
		{
			super(message);
		}
	}

	@Override
	public String toString()
	{
		return Arrays.toString(items) + ' ' + transactionInfo;
	}
}
