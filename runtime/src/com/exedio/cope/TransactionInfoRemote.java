/*
 * Copyright (C) 2004-2011  exedio GmbH (www.exedio.com)
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

import java.util.Date;

final class TransactionInfoRemote extends TransactionInfo
{
	private final int remoteNode;

	TransactionInfoRemote(final int remoteNode)
	{
		this.remoteNode = remoteNode;
	}

	@Override
	boolean isRemote()
	{
		return true;
	}

	@Override
	int getRemoteNodeID()
	{
		return remoteNode;
	}

	@Override
	long getID() throws ChangeEvent.NotAvailableException
	{
		throw newRemoteException();
	}

	@Override
	String getName() throws ChangeEvent.NotAvailableException
	{
		throw newRemoteException();
	}

	@Override
	Date getStartDate() throws ChangeEvent.NotAvailableException
	{
		throw newRemoteException();
	}

	private ChangeEvent.NotAvailableException newRemoteException()
	{
		return new ChangeEvent.NotAvailableException("remote");
	}
}
